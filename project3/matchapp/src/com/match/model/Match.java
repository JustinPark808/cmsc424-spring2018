package com.match.model;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.NullPointerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import java.io.File;
import java.io.IOException;

public class Match {
    static Connection con = null;

    private int id1;
    private int id2;
    private String date;
    private int rating;

    // TODO
    public Match() {}

    // TODO
    public Match(int id1, int id2, String date, int rating) {
        this.id1 = id1;
        this.id2 = id2;
        this.date = date;
        this.rating = rating;
    }

    /*
     * TODO: 
     * Each Match object corresponds to a row in the matches table
     * Therefore, you should have one field in this class corresponding to
     * each attribute of the matches table (id1, id2, etc).
     * You should also write getter methods that returns the current value for
     * each of these fields:
     * 
     * getUserID(): Returns the value of the id1 field. --- this is optional
     * getMatchedID(): Returns the value of the id2 field --- this is required
     * getDate(): Returns the date of the match as a String --- this is required
     * (This should follow string form of an java.sql.date object, which looks
     * like yyyy-mm-dd).
     * getRating(): Get the value of the rating field ---  this is required
     *
     * Also, you should write the getMatchesFor(String id) function (see below)
     *
     * Write method for feedback page in part 6
     * Write any other method you think would be useful or needed
     */

    // TODO
    public int getUserID() {
        return id1;
    }

    // TODO
    public int getMatchedID() {
        return id2;
    }

    // TODO
    public String getDate() {
        return date;
    }

    // TODO
    public double getRating() {
        return rating;
    }

    private static final Logger logger = LogManager.getLogger("match");
    static JsonFactory factory = new JsonFactory();

    /* 
     * Return an array of Match objects that correspond to each match in the
     * matches tables for which the id1 value is equal to the id parameter of
     * this method. Ignore any records in the matches table for which the id2
     * column is equal to the id parameter. If id does not represent a person
     * in the database or if the person with that id does not appear as id1 in
     * any matches, return an empty array. A person cannot match with his or
     * her self and should be prevented from occurring.
     */
    public static Match[] getMatchesFor(String id) {
        // TODO
        con = getConnection();

        // TODO
        if (con == null) {
            logger.warn("Connection Failed!");
            return new Match[0];
        }

        // TODO
        if (id.length() == 0) return new Match[0];

        // TODO
        List<Match> matches = new ArrayList<>();

        // TODO
        try {
            // TODO
            String pStmtStr =
                "SELECT * " +
                "FROM match " +
                "WHERE id1 = ?";

            // TODO
            PreparedStatement pStmt = con.prepareStatement(pStmtStr);
            pStmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = pStmt.executeQuery();

            // TODO
            while (rs.next()) {
                // TODO
                int id1 = rs.getInt("id1");
                int id2 = rs.getInt("id2");
                String date = rs.getString("date_of_match");
                int rating = rs.getInt("rating");

                // TODO
                matches.add(new Match(id1, id2, date, rating));
            }

            return (Match[]) matches.toArray(new Match[matches.size()]);
        } catch (SQLException sqle) {
            logger.warn("getMatchesFor() Query Failed!");
            logger.warn(sqle.getMessage());
            return new Match[0];
        }
    }

    /*
     *
     */
    public static double addFeedback(int id1, int id2, int approval) {
        con = getConnection();

        // TODO
        if (con == null) {
            logger.warn("Connection Failed!");
            return -1.0;
        }

        try {
            // TODO
            String pStmtStr =
                "SELECT approval_rating " +
                "FROM person " +
                "WHERE id = ? " +
                "AND ? IN ( " +
                "   SELECT id1 " +
                "   FROM match " +
                ")";

            // TODO
            PreparedStatement pStmt = con.prepareStatement(pStmtStr);
            pStmt.setInt(1, id2);
            pStmt.setInt(2, id1);
            ResultSet rs = pStmt.executeQuery();

            // TODO
            if (rs.next()) {
                // TODO
                double rating = rs.getDouble("approval_rating");
                rating += (approval == 1 ? 1 : -1);
                rating = Math.max(0.00, rating);
                rating = Math.min(rating, 9.99);

                // TODO
                pStmtStr =
                    "UPDATE person " +
                    "SET approval_rating = ? " +
                    "WHERE id = ?";

                // TODO
                pStmt = con.prepareStatement(pStmtStr);
                pStmt.setDouble(1, rating);
                pStmt.setInt(2, id2);
                pStmt.execute();

                return rating;
            // TODO
            } else {
                logger.warn("Match Not Found!");

                return -1.0;
            }
        } catch (SQLException sqle) {
            logger.warn("adjustRating() Query Failed!");
            logger.warn(sqle.getMessage());

            return -1.0;
        }
    }

    private static Connection getConnection() {
        // Return existing connection after first call
        if (con != null) {
            return con;
        }
        logger.trace("Getting database connection...");
        // Get RDS connection from environment properties provided by Elastic Beanstalk
        con = getRemoteConnection();
        // If that fails, attempt to connect to a local postgres server
        if (con == null) {
            con = getLocalConnection();
        }
        // If that fails, give up
        if (con == null) {
            return null;
        }
        // Attempt to initialize the database on first connection
        //initDatabase();
        return con;
    }

    //Used for AWS connection to DB, not used locally!
    private static Connection getRemoteConnection() {
        /* Read database info from /tmp/database.json (advanced, more secure option)
         * - Requires database.config to be moved into .ebextensions folder and updated to 
         * point to a JSON file in an S3 bucket that the instance profile has permission to read.
         */
        try {
            /* Load the file and create a parser. If the project is not configured to store
             * database credentials in S3, fail out and try the next method.
             */
            File databaseConfig = new File("/tmp/database.json");
            JsonParser parser = factory.createParser(databaseConfig);
            // Load the Postgresql driver class
            Class.forName("org.postgresql.Driver");
            /* Read the first value in the JSON document with Jackson. This must be a full JDBC
             *  connection string a la jdbc:postgresql://hostname:port/dbName?user=userName&password=password
             */
            JsonToken jsonToken = null;
            while ( jsonToken != JsonToken.VALUE_STRING ) 
                jsonToken = parser.nextToken();
            String jdbcUrl = parser.getValueAsString();
            // Connect to the database
            logger.trace("Getting remote connection with url from database config file.");
            Connection con = DriverManager.getConnection(jdbcUrl);
            logger.info("Remote connection successful.");
            return con;
        }
        catch (IOException e) { logger.warn("Database configuration file not found. Checking environment variables.");}
        catch (ClassNotFoundException e) { logger.warn(e.toString());}
        catch (SQLException e) { logger.warn(e.toString());}

        // Read database info from environment variables (standard configration)
        if (System.getProperty("RDS_HOSTNAME") != null) {
            try {
                Class.forName("org.postgresql.Driver");
                String dbName = System.getProperty("RDS_DB_NAME");
                String userName = System.getProperty("RDS_USERNAME");
                String password = System.getProperty("RDS_PASSWORD");
                String hostname = System.getProperty("RDS_HOSTNAME");
                String port = System.getProperty("RDS_PORT");
                String jdbcUrl = "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
                logger.trace("Getting remote connection with connection string from environment variables.");
                Connection con = DriverManager.getConnection(jdbcUrl);
                logger.info("Remote connection successful.");
                return con;
            }
            catch (ClassNotFoundException e) { logger.warn(e.toString());}
            catch (SQLException e) { logger.warn(e.toString());}
        }
        return null;
    }

    /* Connect to the local database for development purposes
       Your database must be named "matchapp" and you must make a user "matchmaker" with the password "kingofthenorth"
       */
    private static Connection getLocalConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            logger.info("Getting local connection");
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost/matchapp",
                    "matchmaker",
                    "kingofthenorth");
            logger.info("Local connection successful.");
            return con;
        }
        catch (ClassNotFoundException e) { logger.warn(e.toString());}
        catch (SQLException e) { logger.warn(e.toString());}
        return null;
    }
}
