package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class AuthFilters extends BaseFilters
{
    public int enforceLoggedIn(ClientRequest req)
    {
        String token = req.packet.token;

        //Check DB for token, validate
        PreparedStatement tokenQ = null;
        LocalDateTime exprTime = null;
        String validIP = null;
        try
        {
            tokenQ = db.compileQuery("""
                    SELECT loginTokenExpiration, loginTokenValidIP
                    FROM Users
                    WHERE loginAuthToken = ?
                    """);

            tokenQ.setString(1, token);

            ResultSet tokenR = tokenQ.executeQuery();

            if (tokenR.next())
            {
                exprTime = tokenR.getTimestamp("loginTokenExpiration").toLocalDateTime();
                validIP = tokenR.getString("loginTokenValidIP");
            }
            else
            {
                //Token wasn't in the DB
                req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
                filter.sendResponse(req);

                return BaseResponseEnum.ERR_BAD_TOKEN;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during enforceLoggedIn token query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return BaseResponseEnum.ERR_UNKNOWN;
        }
        finally
        {
            db.closeConnection(tokenQ);
        }

        //Token existed, but still need to check constraints
        if (LocalDateTime.now().isAfter(exprTime))
        {
            //Token has expired
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);

            return BaseResponseEnum.ERR_BAD_TOKEN;
        }

        if (!req.getRemoteIP().equals(validIP))
        {
            //IP does not match IP which owns the token. Stolen token?
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);

            return BaseResponseEnum.ERR_BAD_TOKEN;
        }

        //All checks passed, allow packet to proceed
        return BaseResponseEnum.SUCCESS;
    }

    public void filterLoginPacket(ClientRequest req)
    {
        if (!(req.packet instanceof LoginPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterLoginPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //No restrictions for this type
        //Forward request
        logic.handleLogin(req);

        return;
    }

    public void filterLogoutPacket(ClientRequest req)
    {
        int loggedInCheckResult = enforceLoggedIn(req);
        if (loggedInCheckResult != BaseResponseEnum.SUCCESS) { return; }

        //Forward request
        logic.handleLogout(req);

        return;
    }
}
