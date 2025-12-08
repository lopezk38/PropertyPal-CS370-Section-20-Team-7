package com.propertypal.server.LogicBlocks;

import com.propertypal.server.ClientRequest;
import com.propertypal.server.SecurityFilter;
import com.propertypal.server.DbWrapper;

import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.time.LocalDateTime;

public class AuthLogic extends BaseLogic
{
    public void handleLogin(ClientRequest req)
    {
        LoginPacket packet = (LoginPacket) req.packet;
        String email = packet.email;
        String password = packet.password;

        if (email == null || email.isEmpty())
        {
            //No username given
            req.setBaseErrResponse(LoginResponse.LoginStatus.ERR_BAD_EMAIL);
            filter.sendResponse(req);
            return;
        }

        if (password == null || password.isEmpty())
        {
            //No password given
            req.setBaseErrResponse(LoginResponse.LoginStatus.ERR_BAD_PASSWORD);
            filter.sendResponse(req);
            return;
        }

        //Query for user ID belonging to username
        //Query for hashed pw in db
        PreparedStatement userNameQ = null;
        try
        {
            userNameQ = db.compileQuery("""
            SELECT userID, hashedPW, isLandlord
            FROM Users
            WHERE email = ?
            """);
            userNameQ.setString(1, email);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleLogin userName query compilation: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }

        ResultSet userNameR = null;
        try
        {
            userNameR = userNameQ.executeQuery();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleLogin userName query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            db.closeConnection(userNameQ);
            return;
        }

        long userID;
        String validPW;
        Boolean isLandlord;
        try
        {
            if (userNameR.next())
            {
                //Got a user
                userID = userNameR.getLong("userID");
                validPW = userNameR.getString("hashedPW");
                isLandlord = userNameR.getBoolean("isLandlord");
            }
            else
            {
                //User not found
                req.setBaseErrResponse(LoginResponse.LoginStatus.ERR_BAD_EMAIL);
                filter.sendResponse(req);

                db.closeConnection(userNameQ);
                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleLogin userName response parsing: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            db.closeConnection(userNameQ);
            return;
        }

        //Check for match
        //Respond w/ rejection if so
        if (!password.equals(validPW))
        {
            //Wrong PW
            req.setBaseErrResponse(LoginResponse.LoginStatus.ERR_BAD_PASSWORD);
            filter.sendResponse(req);
            return;
        }

        //From here down, login succeeded
        //Generate token
        String token = UUID.randomUUID().toString();

        //Query to store token, IP, expiration timestamp
        LocalDateTime exprTime = LocalDateTime.now().plusHours(24);
        String ip = req.getRemoteIP();

        PreparedStatement tokenQ = null;
        try
        {
            tokenQ = db.compileQuery("UPDATE Users SET loginAuthToken = ?, loginTokenExpiration = ?, loginTokenValidIP = ? WHERE userID = ?");
            tokenQ.setString(1, token);
            tokenQ.setString(2, exprTime.toString());
            tokenQ.setString(3, ip);
            tokenQ.setLong(4, userID);

            tokenQ.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleLogin DB update: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(tokenQ);
        }

        //Respond with token
        LoginResponse resp = new LoginResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.TOKEN = token;
        resp.IS_LANDLORD = isLandlord;
        req.setResponse(resp);
        filter.sendResponse(req);
    }

    public void handleLogout(ClientRequest req)
    {
        LogoutPacket packet = (LogoutPacket) req.packet;

        //Delete auth token for this user in db
        PreparedStatement userQ = null;
        try
        {
            //Compile and execute query
            userQ = db.compileQuery("""
            UPDATE Users
            SET loginAuthToken = NULL, loginTokenExpiration = NULL, loginTokenValidIP = NULL
            WHERE loginAuthToken = ?
            """);
            userQ.setString(1, packet.token);

            int changedRowCnt = userQ.executeUpdate();

            //Verify a row was changed
            if (changedRowCnt < 1)
            {
                //No row was changed. Bad user?
                System.out.println("WARNING: User attempted to logout but handler changed no rows in DB");

                req.setUnknownErrResponse();
                filter.sendResponse(req);

                return;
            }
            else if (changedRowCnt > 1)
            {
                System.out.println("WARNING: User logout affected multiple users somehow?");
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during logout query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(userQ);
        }

        //Respond with success
        LogoutResponse resp = new LogoutResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        req.setResponse(resp);
        filter.sendResponse(req);
    }
}