package com.match.model;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.lang.NullPointerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import java.io.File;
import java.io.IOException;


public class Person {

    static Connection con = null;

    // Fields for a person entry in the person table
    // Remember to add one of your own choice of fields 
    private String firstName, lastName, major, language, county;
    private int age, gender, seeking_relationship, seeking_gender;
    private double approval_rating;

    // TODO
    private String foo;

    //used for logging output, see catalina.out for log files
    private static final Logger logger = LogManager.getLogger("match");
    static JsonFactory factory = new JsonFactory();

    public Person() {}

    //constructor for person if only need to display the name
    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    //constructor for person with all fields
    public Person(String firstName, String lastName, int age, String major,
            int gender, int seeking_relationship, int seeking_gender,
            String language, String county, double approval_rating, String foo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.major = major;
        this.gender = gender;
        this.seeking_relationship = seeking_relationship;
        this.seeking_gender = seeking_gender;
        this.language = language;
        this.county = county;
        this.approval_rating = approval_rating;
        this.foo = foo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    /*
     * Return an array of all the people in the person table 
     * You will need to make a SQL call via JDBC to the database to get all of
     * the people. Since the webpage only needs to display the person's first
     * and last name, only those fields of the Person object need to be
     * instantiated (i.e., you can use the second of the three Person 
     * constructors above)
     */
    public static Person[] getPeople() {
        con = getConnection();

        if (con == null) {
            logger.warn("Connection Failed!");
            Person failed = new Person("Connection", "Failed");

            return new Person[] { failed };
        }

        List<Person> people = new ArrayList<>();

        try {
            // TODO
            String pStmtStr = 
                "SELECT first_name, last_name FROM person";

            // TODO
            PreparedStatement pStmt = con.prepareStatement(pStmtStr);
            ResultSet rs = pStmt.executeQuery();

            // TODO
            while (rs.next()) {
                // TODO
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                Person curr = new Person(firstName, lastName);
                people.add(curr);
            }

            return people.toArray(new Person[people.size()]);
        } catch (SQLException sqle) {
            logger.warn("getPeople() Query Failed!");
            Person failed = new Person("Query", "Failed");

            return new Person[] { failed };
        }
    }

    /* For every person record in the database, search each of its character
     * fields to see if input query is a substring of any of them.
     * Return everything that matches with every char/varchar column. This
     * should be case senstive in finding substring matches.
     *
     * For example if we have 2 people with:
     * First: Alex, Last: Westmore, Major: Biology, County: Howard, Language: ENG
     * First: Dave, Last: Howland, Major: Geology, County: Frederick, Language: ENG
     *
     * If we query with the string: "How", the Person array that is returned
     * contains both people 
     * If we query with the string: "more", the Person array that is returned
     * contains just Alex
     * If we query with the string: COUNT, the Person array is empty
     *
     * The order of the people returned does not matter
     *
     * If no rows in the database are found with a substring match, you should
     * return an empty array of Person.
     */
    public static Person[] getPersonSearch(String query) {
        con = getConnection();

        if (con == null) {
            logger.warn("Connection Failed!");
            Person failed = new Person("Connection", "Failed");

            return new Person[] { failed };
        }

        // TODO
        List<Person> people = new ArrayList<>();

        // TODO
        try {
            // TODO
            String pStmtStr = 
                "SELECT first_name, last_name " +
                "FROM   person " +
                "WHERE  first_name LIKE ? " +
                "OR     last_name LIKE ? " +
                "OR     major LIKE ? " +
                "OR     language LIKE ? " +
                "OR     county LIKE ? " +
                "OR     foo LIKE ?";

            // TODO
            PreparedStatement pStmt = con.prepareStatement(pStmtStr);
            for (int i = 1; i <= 6; i++) { pStmt.setString(i, '%' + query + '%'); }

            // TODO
            ResultSet rs = pStmt.executeQuery();

            // TODO
            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                Person curr = new Person(firstName, lastName);
                people.add(curr);
            }

            return people.toArray(new Person[people.size()]);
        } catch (SQLException sqle) {
            logger.warn("getPersonSearch() Query Failed!");
            Person failed = new Person("Query", "Failed");

            return new Person[] { failed };
        }
    }

    /*
     * This should return a Person object with all of its fields instatiated 
     * for the person with the given id in the person table. Note that since id
     * is unique, there will only be one person ever returned by this method.
     *
     * Return a person with the first name "No" and the last name "Matches" if
     * the person with the id does not exist.
     */
    public static Person getPerson(String id) {
        // TODO
        con = getConnection();

        // TODO
        if (con == null) {
            logger.warn("Connection Failed!");
            return new Person("Connection", "Failed");
        }

        // TODO
        if (id.length() == 0) return new Person("No", "Matches");

        // TODO
        try {
            // TODO
            String pStmtStr =
                "SELECT * " +
                "FROM person " +
                "WHERE id = ?";

            // TODO
            PreparedStatement pStmt = con.prepareStatement(pStmtStr);
            pStmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = pStmt.executeQuery();

            // TODO
            if (!rs.next()) {
                return new Person("No", "Matches");
                // TODO
            } else {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int age = rs.getInt("age");
                String major = rs.getString("major");
                int gender = rs.getInt("gender");
                int seekingRelationshipType = rs.getInt("seeking_relationship_type");
                int seekingGender = rs.getInt("seeking_gender");
                String language = rs.getString("language");
                String county = rs.getString("county");
                double approvalRating = rs.getDouble("approval_rating");
                String foo = rs.getString("foo");

                return new Person(firstName, lastName, age, major, gender,
                        seekingRelationshipType, seekingGender, language,
                        county, approvalRating, foo);
            }
        } catch (Exception sqle) {
            logger.warn("getPerson() Query Failed!");
            logger.warn(sqle.getMessage());
            return new Person("Query", "Failed");
        }
    }

    /*
     * Add a person to the database with all of the fields specified
     *
     * If the connection fails or the person was not inserted, return -1,
     * otherwise return the id of the person that you inserted
     *
     * You must use a prepared statement to insert the person
     */
    public static int addPerson(String first, String last, int age, 
            String major, int gender, int seeking_relationship,
            int seeking_gender, String language, String county,
            double approval_rating, String foo) {
        con = getConnection();

        if (con == null) {
            logger.warn("Connection Failed!");

            return -1;
        }

        try {
            // TODO
            String pStmtStr =
                "INSERT INTO person " +
                "   (first_name, last_name, age, major, gender, " + 
                "   seeking_relationship_type, seeking_gender, language, county, " +
                "   approval_rating, foo) " +
                "VALUES " +
                "   (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING id";

            // TODO
            PreparedStatement pStmt = con.prepareStatement(pStmtStr);
            pStmt.setString(1, first);
            pStmt.setString(2, last);
            pStmt.setInt(3, age);
            pStmt.setString(4, major);
            pStmt.setInt(5, gender);
            pStmt.setInt(6, seeking_relationship);
            pStmt.setInt(7, seeking_gender);
            pStmt.setString(8, language);
            pStmt.setString(9, county);
            pStmt.setDouble(10, approval_rating);
            pStmt.setString(11, foo);
            ResultSet rs = pStmt.executeQuery();

            // TODO
            return rs.next() ? rs.getInt("id") : -1;
        } catch (SQLException sqle) {
            logger.warn("addPerson() Query Failed!");
            logger.warn(sqle.getMessage());

            return -1;
        }
    }

    /*
     * Return a list of the best 5 matches in the database for the person with
     * the given id based on the score method that you write later in this file
     * (see below).
     *
     * A person is not allowed to be matched with herself or himself.
     *
     * You must write these matches to the matches table including the date and
     * their score
     *
     * Note: Once someone has already matched with someone, do not return them
     * again. This means your method will return the top 5 matches for that
     * person in the entire database on the first call. On the second call it
     * should return the next 5 best matches, and so on.
     *
     * If the person with id does not exist, return a Person array with one
     * person with the first name "No" and last name "Matches"
     * If the person has no matches, return an empty Person array
     */
    public static Person[] getMatchedPeople(String id) {
        con = getConnection();

        // TODO
        if (con == null) {
            logger.warn("Connection Failed!");
            Person failed = new Person("Connection", "Failed");
            return new Person[] { failed };
        }

        //TODO
        final Person p = getPerson(id);
        if (p.firstName.equals("No") && p.lastName.equals("Matches")) return new Person[] { p };

        // TODO
        Comparator<Person> scoreComparator = new Comparator<Person>() {
            public int compare(Person p1, Person p2) {
                double score1 = computeMatchScore(p, p1);
                double score2 = computeMatchScore(p, p2);

                return score1 <= score2 ? 1 : -1;
            }
        };
        TreeMap<Person, Integer> topMatches = new TreeMap<>(scoreComparator);

        // TODO
        try {
            // TODO
            String pStmtStr =
                "SELECT id " +
                "FROM person " +
                "WHERE id != ? " +
                "AND id NOT IN ( " +
                "   SELECT id2 " +
                "   FROM match " +
                "   WHERE id1 = ? " +
                ")";

            // TODO
            PreparedStatement pStmt = con.prepareStatement(pStmtStr);
            pStmt.setInt(1, Integer.parseInt(id));
            pStmt.setInt(2, Integer.parseInt(id));
            ResultSet rs = pStmt.executeQuery();

            // TODO
            while (rs.next()) {
                // TODO
                int altId = rs.getInt("id");
                Person alt = getPerson(Integer.toString(altId));

                // TODO
                if (topMatches.size() < 5) {
                    topMatches.put(alt, altId);
                    // TODO
                } else if (scoreComparator.compare(alt, topMatches.lastKey()) < 0) {
                    topMatches.pollLastEntry();
                    topMatches.put(alt, altId);
                }
            }

            // TODO
            pStmtStr =
                "INSERT INTO match " +
                "VALUES (?, ?, ?, ?)";

            // TODO
            for (Map.Entry<Person, Integer> entry : topMatches.entrySet()) {
                pStmt = con.prepareStatement(pStmtStr);
                pStmt.setInt(1, Integer.parseInt(id));
                pStmt.setInt(2, entry.getValue());
                pStmt.setDate(3, new Date(System.currentTimeMillis())); 
                pStmt.setDouble(4, computeMatchScore(p, entry.getKey()));
                pStmt.execute();
            }

            return (Person[]) topMatches.keySet().toArray(new Person[topMatches.size()]);
        } catch (SQLException sqle) {
            logger.warn("getMatchedPeople() Query Failed!");
            logger.warn(sqle.getMessage());
            Person failed = new Person("Query", "Failed");
            return new Person[] { failed };
        }
    }

    /* Fill in this method to compute the match score between 2 people. 
     * How you score is up to you, but you must use all fields in person 
     * except for the name and id fields in some way.
     *
     * This has to be deterministic (not random) which means the same 2 people
     * must generate the same score every time the method is called.
     */
    public static double computeMatchScore (Person p, Person alt){
        double score = 0;

        if (Math.abs(p.age - alt.age) <= 5) score++;
        if (p.major.equals(alt.major)) score++;
        if (p.gender == alt.seeking_gender && p.seeking_gender == alt.gender) score++;
        if (p.seeking_relationship == alt.seeking_relationship) score++;
        if (p.language.equals(alt.language)) score++;
        if (p.county.equals(alt.county)) score++;
        score += 10 - Math.abs(p.approval_rating - alt.approval_rating);
        if (p.foo != null && p.foo.equals(alt.foo)) score++;
        score *= 100.0 / 17;

        return score;
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
