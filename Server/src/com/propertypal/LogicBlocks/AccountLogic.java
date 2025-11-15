package com.propertypal.LogicBlocks;

import com.propertypal.ClientRequest;
import com.propertypal.SecurityFilter;
import com.propertypal.DbWrapper;
import com.propertypal.network.responses.*;
import com.propertypal.network.packets.*;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.security.SecureRandom;
import org.springframework.security.crypto.bcrypt.BCrypt;
import java.time.LocalDateTime;

public class AccountLogic
{
    public void handleCreateTenantAccount(ClientRequest req)
    {
        SecurityFilter filter = SecurityFilter.getInstance();
        DbWrapper db = DbWrapper.getInstance();

        if (!(req.packet instanceof CreateAcctPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: handleCreateAccount is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        CreateAcctPacket packet = (CreateAcctPacket) req.packet;
        String email = packet.email;
        String password = packet.password;

        boolean emailIsAvailable = false;
        //TODO Query - Check if email is taken

        //Generate userID
        long userID;
        if (emailIsAvailable)
        {
            SecureRandom r = new SecureRandom();
            userID = r.nextLong();
        }

        //TODO Query - store userID with correlating email

        //Hash and salt password
        String hashedPW = BCrypt.hashpw(password, BCrypt.gensalt());

        //TODO Query - store hashedPW in db
    }
}
