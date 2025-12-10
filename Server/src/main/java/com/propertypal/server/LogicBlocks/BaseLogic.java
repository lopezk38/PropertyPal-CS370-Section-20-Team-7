package com.propertypal.server.LogicBlocks;

import com.propertypal.server.DbWrapper;
import com.propertypal.server.SecurityFilter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public abstract class BaseLogic
{
    protected SecurityFilter filter = SecurityFilter.getInstance();
    protected DbWrapper db = DbWrapper.getInstance();

    protected long userIDFromToken(String token)
    {
        //Query DB
        PreparedStatement idQ = null;
        long userID = -1;
        try
        {
            //Create new ticket entry
            idQ = db.compileQuery("""
                    SELECT userID
                    FROM Users
                    WHERE loginAuthToken = ?
                    """);

            idQ.setString(1, token);
            ResultSet res = idQ.executeQuery();

            //Extract the ID from the resultset
            if (res.next())
            {
                userID = res.getLong("userID");
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during conversion from token to userID: " + e.toString());

            return -1;
        }
        finally
        {
            db.closeConnection(idQ);
        }

        return userID;
    }
}