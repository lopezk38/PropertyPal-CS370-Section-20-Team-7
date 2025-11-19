package com.propertypal.LogicBlocks;

import com.propertypal.ClientRequest;
import com.propertypal.SecurityFilter;
import com.propertypal.DbWrapper;
import com.propertypal.network.responses.*;
import com.propertypal.network.packets.*;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
//import java.security.SecureRandom;
//import org.springframework.security.crypto.bcrypt.BCrypt;
import java.util.UUID;
import java.time.LocalDateTime;

public class AccountLogic extends BaseLogic
{
    public void handleCreateTenantAccount(ClientRequest req)
    {
        CreateAcctPacket packet = (CreateAcctPacket) req.packet;
        String email = packet.email;
        String password = packet.password;

        boolean emailIsAvailable = false;
        //TODO Query - Check if email is taken

        //Build query to get ID if email exists in DB
        PreparedStatement emailQ = null;
        try
        {
            emailQ = db.compileQuery("SELECT userID FROM Users WHERE email = ?");
            emailQ.setString(1, email);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleCreateTenantAccount email query compilation: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }

        //Execute the query
        ResultSet emailR = null;
        try
        {
            emailR = emailQ.executeQuery();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleCreateTenantAccount email query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            db.closeConnection(emailQ);
            return;
        }

        //Extract the result
        try
        {
            if (emailR.next())
            {
                //Got a user
                long userID = emailR.getLong("userID");
                emailIsAvailable = emailR.wasNull(); //Make sure the user wasn't null before condemning the email
            }
            else
            {
                //User not found
                emailIsAvailable = true;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleLogin userName response parsing: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            db.closeConnection(emailQ);
            return;
        }
        finally
        {
            db.closeConnection(emailQ);
        }

        if (!emailIsAvailable) //db handles generation of unique user IDs by itself
        {
            //Respond with error to try a different email
            req.setBaseErrResponse(CreateAcctResponse.AccountStatus.ERR_BAD_EMAIL);
            filter.sendResponse(req);
        }

        //Build query to store user account info
        PreparedStatement acctQ = null;
        String token = UUID.randomUUID().toString();
        try
        {
            acctQ = db.compileQuery("""
                    INSERT INTO Users (
                    firstName,
                    lastName,
                    email,
                    billingAddress1,
                    billingAddress2,
                    billingCity,
                    billingState,
                    billingZIP,
                    billingCountry,
                    registerDate,
                    hashedPW,
                    isLandlord,
                    loginAuthToken,
                    loginTokenExpiration,
                    loginTokenValidIP
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""");

            acctQ.setString(1, packet.firstName);
            acctQ.setString(2, packet.lastName);
            acctQ.setString(3, email);
            acctQ.setString(4, packet.billAddr1);
            acctQ.setString(5, packet.billAddr2);
            acctQ.setString(6, packet.billCity);
            acctQ.setString(7, packet.billState);
            acctQ.setString(8, packet.billZip);
            acctQ.setString(9, packet.billCountry);
            acctQ.setString(10, LocalDateTime.now().toString());
            acctQ.setString(11, password);
            acctQ.setBoolean(12, false);
            acctQ.setString(13, token);
            acctQ.setString(14, LocalDateTime.now().plusHours(24).toString());
            acctQ.setString(15, req.getRemoteIP());
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleCreateTenantAccount account query compilation: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }

        //Execute the query
        ResultSet acctR = null;
        try
        {
            acctR = acctQ.executeQuery();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleCreateTenantAccount account query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            db.closeConnection(acctQ);
            return;
        }

        db.closeConnection(acctQ);

        //Succeded, send OK
        CreateAcctResponse resp = new CreateAcctResponse();
        resp.TOKEN = token;
        req.setResponse(resp);
        filter.sendResponse(req);
    }

    public void handleCreateLandlordAccount(ClientRequest req)
    {
        //TODO
    }

    public void handleCreateInvite(ClientRequest req)
    {
        //TODO
    }

    public void handleAcceptInvite(ClientRequest req)
    {
        //TODO
    }
}
