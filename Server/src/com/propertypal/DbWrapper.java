package com.propertypal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbWrapper
{
    private static DbWrapper instance = null;

    private final String dbUrl = "jdbc:h2:~/data/db";
    private final String dbUName = "propertypalAdmin";
    private final String dbPw = "";

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
            con.createStatement().execute("CREATE Table Users (userID bigint PRIMARY KEY, firstName text, lastName text, email text ALTERNATE KEY), billingAddress1 text, billingAddress2 text, billingCity text, billingZIP text, billingCountry text, taxID text, registerDate timestamp, hashedPW text, requirePWReset boolean, loginAuthToken text ALTERNATE KEY, loginTokenExpiration timestamp, loginTokenValidIP inet, paypalAPIToken text)");
            //TODO add other tables
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Failed to init DB");
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
            throw new RuntimeException("Could not init DB");
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

    public static DbWrapper getInstance()
    {
        if (instance == null)
        {
            instance = new DbWrapper();
        }

        return instance;
    }

    public ResultSet query(String query) throws SQLException
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
