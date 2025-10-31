package com.propertypal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class DbWrapper
{
    private static DbWrapper instance = null;

    private HashMap<PreparedStatement, Connection> openConnections = new HashMap<PreparedStatement, Connection>();

    private final String dbUrl = "jdbc:h2:./data/db";
    private final String dbUName = "propertypalAdmin";
    private final String dbPw = "";

    public static DbWrapper getInstance()
    {
        if (instance == null)
        {
            instance = new DbWrapper();
        }

        return instance;
    }

    public DbWrapper()
    {
        if (instance != null) throw new RuntimeException("Only one instance of DbWrapper may exist");

        instance = this;

        //Make sure DB is there/gen DB if not
        if (!validateTableFormat())
        {
            //Assume DB is not there/uninitialized
            //TODO Add function to backup DB
            dbInit();
        }
    }

    private void dbInit() throws RuntimeException
    {
        Connection con = establishCon();

        try
        {
            con.createStatement().execute("DROP TABLE IF EXISTS Users");
            con.createStatement().execute("CREATE Table Users (userID bigint, firstName VARCHAR, lastName VARCHAR, email VARCHAR, billingAddress1 VARCHAR, billingAddress2 VARCHAR, billingCity VARCHAR, billingZIP VARCHAR, billingCountry VARCHAR, taxID VARCHAR, registerDate timestamp, hashedPW VARCHAR, requirePWReset boolean, loginAuthToken VARCHAR, loginTokenExpiration timestamp, loginTokenValidIP VARCHAR, paypalAPIToken VARCHAR, CONSTRAINT PK_ID PRIMARY KEY (userID), CONSTRAINT UQ_EM UNIQUE (email), CONSTRAINT UQ_TOK UNIQUE (loginAuthToken))");
            //TODO add other tables

            //DEBUG
            con.createStatement().execute("INSERT INTO Users (userID, email, hashedPW) VALUES (1001, '1@2.com', 'pass')");
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Failed to init DB due to SQLException: " + e.toString());
            throw new RuntimeException("Could not init DB"); //Rethrow for caller to deal with
        }

        try
        {
            con.close();
        }
        catch (SQLException e)
        {
            System.out.println("WARNING: Unable to close DB during validation");
        }
    }

    private Connection establishCon() throws RuntimeException
    {
        try
        {
            return DriverManager.getConnection(dbUrl, dbUName, dbPw);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Failed to connect to DB");
            throw new RuntimeException("Could not connect to DB");
        }
    }

    private boolean validateTableFormat()
    {
        Connection con = establishCon();

        try
        {
            //Users table
            con.createStatement().execute("SELECT userID, firstName, lastName, email, billingAddress1, billingAddress2, billingCity, billingState, billingZIP, billingCountry, taxID, registerDate, hashedPW, requirePWReset, loginAuthToken, loginTokenExpiration, loginTokenValidIP, paypalAPIToken LIMIT 1");
        }
        catch (SQLException e)
        {
            return false;
        }

        try
        {
            con.close();
        }
        catch (SQLException e)
        {
            System.out.println("WARNING: Unable to close DB during validation");
        }

        return true;
    }

    public PreparedStatement compileQuery(String query) throws SQLException
    {
        if (query == null || query.isEmpty())
        {
            return null;
        }

        try
        {
            Connection con = establishCon();
            PreparedStatement statement = con.prepareStatement(query);
            openConnections.put(statement, con);
            return statement;
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Failed to compile SQL query: " + query);
            throw e; //Rethrow for caller to deal with
        }
    }

    public void closeConnection(PreparedStatement usedQuery)
    {
        try
        {
            Connection con = openConnections.get(usedQuery);
            if (con != null)
            {
                con.close();
                openConnections.remove(usedQuery);
            }
            else
            {
                System.out.println("WARNING: Attempted to close already closed DB connection");
            }
        }
        catch (SQLException e)
        {
            System.out.println("WARNING: Failed to close DB connection");
        }
    }

    //Used internally, do not make public as it is vulnerable to injection
    private ResultSet query(String query) throws SQLException
    {
        if (query == null || query.isEmpty())
        {
            return null;
        }

        Connection con = establishCon();
        Statement statement = con.createStatement();
        ResultSet result = null;

        try
        {
            if (statement.execute(query))
            {
                result = statement.getResultSet();
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Failed to execute SQL query: " + query);
            throw e; //Rethrow for caller to deal with
        }

        try
        {
            con.close();
        }
        catch (SQLException e)
        {
            System.out.println("WARNING: Unable to close DB during validation");
        }

        return result;
    }
}
