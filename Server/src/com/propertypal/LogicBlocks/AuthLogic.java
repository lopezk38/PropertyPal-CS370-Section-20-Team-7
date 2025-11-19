package com.propertypal.LogicBlocks;

import com.propertypal.ClientRequest;
import com.propertypal.SecurityFilter;
import com.propertypal.DbWrapper;
import com.propertypal.network.responses.*;
import com.propertypal.network.packets.*;
import com.propertypal.network.enums.*;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
//import org.springframework.security.crypto.bcrypt.BCrypt;
import java.util.UUID;
import java.time.LocalDateTime;

public class AuthLogic extends BaseLogic
{
    public void handleLogin(ClientRequest req)
    {
        LoginPacket packet = (LoginPacket) req.packet;
        String email = packet.email;
        String password = packet.password;

        //Query for user ID belonging to username
        //Query for hashed pw in db
        PreparedStatement userNameQ = null;
        try
        {
            userNameQ = db.compileQuery("SELECT userID, hashedPW FROM Users WHERE email = ?");
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
        try
        {
            if (userNameR.next())
            {
                //Got a user
                userID = userNameR.getLong("userID");
                validPW = userNameR.getString("hashedPW");
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
        req.setResponse(resp);
        filter.sendResponse(req);


    }
}