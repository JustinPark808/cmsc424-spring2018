package functionaldependency;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Statement;


public class FDFinder {

    public static void main(String[] argv) {

        System.out.println("-------- PostgreSQL "
                + "JDBC Connection Testing ------------");

        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;

        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/fddb", "test1",
                    "asdf");

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;

        }

        if (connection != null) {
            doWork(connection);
        } else {
            System.out.println("Failed to make connection!");
        }
    }

    public static void doWork(Connection c) {
        try {
            DatabaseMetaData dbmd = c.getMetaData();
            ResultSet rs = dbmd.getColumns(null, "%", "%dataset%", "%");
            ArrayList<String> obj = new ArrayList<String>();
            ArrayList<String> aboveThreshold = new ArrayList<String>();
            while (rs.next()) {
                obj.add(rs.getString("COLUMN_NAME"));
            }
            System.out.println(obj);
            for (int i = 0; i < obj.size(); i++) {
                for (int j = 0; j < obj.size(); j++) {
                    if (i != j) {
                        String col1 = obj.get(i);
                        String col2 = obj.get(j);
                        double r = doQuery(c, col1, col2);
                        System.out.println("Confidence(" + col1 + "," + col2 + ") = " + r);
                        if (r > 1.5)
                            aboveThreshold.add("Confidence(" + col1 + "," + col2 + ") = " + r);

                    }
                }
            }
            System.out.println("Confidences above threshold:\n" + aboveThreshold);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static double doQuery(Connection c, String col1, String col2) {
        /* TODO
         * Your query should return a single row with a single column that
         * contains the confidence calculation for col1 and col2 which are
         * parameters to this method. In other words, your SQL query returns
         * confidence(col1,col2)
         */
        String query =
            "WITH cardinalities AS ( " +
            "   SELECT  " +
                    col1 + ", " +
            "       1.0 / count(DISTINCT " + col2 + ") AS cardinality " +
            "   FROM dataset " +
            "   GROUP BY " + col1 + " " +
            "), " +
            "duplicates AS ( " +
            "   SELECT " +
                    col1 + ", " +
            "       count(" + col2 + ") AS count " +
            "   FROM dataset " +
            "   WHERE " + col1 + " IN ( " +
            "       SELECT " + col1 + " " +
            "       FROM dataset " +
            "       GROUP BY " + col1 + " " +
            "       HAVING count(*) > 1 " +
            "   ) " +
            "   GROUP BY " +
                    col1 + ", " +
                    col2 + " " +
            "), " +
            "consistencies AS ( " +
            "   SELECT " +
                    col1 + ", " +
            "       max(count) / sum(count) AS consistency " +
            "   FROM duplicates " +
            "   GROUP BY " + col1 + " " +
            ") " +
            "SELECT sum(cardinality) / count(cardinalities." + col1 + " ) + " +
            "       (1 + sum(consistency)) / (1 + count(consistencies." + col1 + " )) " +
            "       AS confidence " +
            "FROM cardinalities NATURAL LEFT OUTER JOIN consistencies ";

        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            rs.next();

            return rs.getFloat(1);

        } catch (SQLException e) {
            e.printStackTrace();

            return 0;

        }
    }

}
