package com.propertypal.server.LogicBlocks;

import com.propertypal.server.ClientRequest;

import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
            System.out.println("ERROR: SQLException during handleCreateTenantAccount email response parsing: " + e.toString());

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
                    loginTokenValidIP,
                    phone
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""");

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
            acctQ.setString(16, packet.phone);

            acctQ.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleCreateTenantAccount account query compilation: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(acctQ);
        }

        //Succeeded, send OK
        CreateAcctResponse resp = new CreateAcctResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.TOKEN = token;
        req.setResponse(resp);
        filter.sendResponse(req);
    }

    public void handleCreateLandlordAccount(ClientRequest req)
    {
        CreateAcctPacket packet = (CreateAcctPacket) req.packet;
        String email = packet.email;
        String password = packet.password;

        boolean emailIsAvailable = false;

        //Build query to get ID if email exists in DB
        PreparedStatement emailQ = null;
        try
        {
            emailQ = db.compileQuery("SELECT userID FROM Users WHERE email = ?");
            emailQ.setString(1, email);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleCreateLandlordAccount email query compilation: " + e.toString());

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
            System.out.println("ERROR: SQLException during handleCreateLandlordAccount email query: " + e.toString());

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
            System.out.println("ERROR: SQLException during handleCreateLandlordAccount email response parsing: " + e.toString());

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
                    loginTokenValidIP,
                    taxID,
                    phone
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""");

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
            acctQ.setBoolean(12, true);
            acctQ.setString(13, token);
            acctQ.setString(14, LocalDateTime.now().plusHours(24).toString());
            acctQ.setString(15, req.getRemoteIP());
            acctQ.setString(16, packet.tax_id);
            acctQ.setString(17, packet.phone);

            acctQ.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleCreateTenantAccount account query compilation: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(acctQ);
        }

        //Store their property info
        PreparedStatement propQ = null;
        try
        {
            propQ = db.compileQuery("""
                    INSERT INTO Properties (
                    addr1,
                    addr2,
                    city,
                    state,
                    zipCode,
                    country
                    )
                    VALUES (?, ?, ?, ?, ?, ?)""");

            propQ.setString(1, packet.propAddr1);
            propQ.setString(2, packet.propAddr2);
            propQ.setString(3, packet.propCity);
            propQ.setString(4, packet.propState);
            propQ.setString(5, packet.propZip);
            propQ.setString(6, packet.propCountry);

            propQ.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleCreateLandlordAccount property query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }
        finally
        {
            db.closeConnection(propQ);
        }

        //Succeeded, send OK
        CreateAcctResponse resp = new CreateAcctResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.TOKEN = token;
        req.setResponse(resp);
        filter.sendResponse(req);
    }

    public void handleCreateInvite(ClientRequest req)
    {
        CreateInvitePacket packet = (CreateInvitePacket) req.packet;
        Long propertyId = packet.propertyId;
        Long target = packet.targetUser;

        //Get userID
        long landlordID = userIDFromToken(packet.token);
        if (landlordID == -1)
        {
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);
        }

        //Check if propertyID is valid and they own it
        if (propertyId == null || propertyId < 1)
        {
            req.setBaseErrResponse(CreateInviteResponse.InviteStatus.ERR_BAD_PROPERTY);
            filter.sendResponse(req);
        }

        PreparedStatement propQ = null;
        Long ownerID = null;
        try
        {
            propQ = db.compileQuery("""
                    SELECT owner
                    FROM Properties
                    WHERE propertyID = ?
                    """);

            propQ.setLong(1, propertyId);

            ResultSet propR = propQ.executeQuery();

            if (propR.next())
            {
                ownerID = propR.getLong("owner");
            }
            else
            {
                //PropID didn't exist in DB
                System.out.println("WARNING: PropertyID was invalid in handleCreateInvite");
                req.setBaseErrResponse(CreateInviteResponse.InviteStatus.ERR_BAD_PROPERTY);
                filter.sendResponse(req);

                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleCreateInvite property query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }
        finally
        {
            db.closeConnection(propQ);
        }

        if (ownerID == null || ownerID != landlordID)
        {
            //Requesting user is not the owner, reject
            req.setBaseErrResponse(BaseResponseEnum.ERR_PERMISSION_DENIED);
            filter.sendResponse(req);
        }

        //Insert invite into DB
        long inviteID = -1;
        PreparedStatement invQ = null;
        try
        {
            invQ = db.compileQuery("""
                    INSERT INTO Invites (
                    landlordID,
                    tenantID,
                    propertyID,
                    dateMade
                    )
                    VALUES (?, ?, ?, ?)""", Statement.RETURN_GENERATED_KEYS);

            invQ.setLong(1, landlordID);
            invQ.setLong(2, target);
            invQ.setLong(3, propertyId);
            invQ.setString(4, LocalDateTime.now().toString());

            invQ.executeUpdate();

            //Get the newly made primary key
            ResultSet res = invQ.getGeneratedKeys();
            if (res.next())
            {
                inviteID = res.getLong(1);
            }
            else throw new SQLException("Failed to generate invite primary key");
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleCreateInvite account query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }
        finally
        {
            db.closeConnection(invQ);
        }

        //Succeeded, send OK
        CreateInviteResponse resp = new CreateInviteResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.INVITE_ID = inviteID;
        req.setResponse(resp);
        filter.sendResponse(req);
    }

    public void handleAcceptInvite(ClientRequest req)
    {
        AcceptInvitePacket packet = (AcceptInvitePacket) req.packet;
        long inviteID = packet.inviteID;
        boolean accept = packet.accept;

        //Get userID
        long tenantID = userIDFromToken(packet.token);
        if (tenantID == -1)
        {
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);
        }

        //Retrieve invite data
        Long landlordID = null;
        Long storedTenID = null;
        Long propertyID = null;
        PreparedStatement invQ = null;
        try
        {
            invQ = db.compileQuery("""
            SELECT landlordID, tenantID, propertyID
            FROM Invites
            WHERE inviteID = ?
            """);
            invQ.setLong(1, propertyID);

            ResultSet invR = invQ.executeQuery();

            if (invR.next())
            {
                //Got an invite
                landlordID = invR.getLong("landlordID");
                storedTenID = invR.getLong("tenantID");
                propertyID = invR.getLong("propertyID");
            }
            else
            {
                //Invite not found
                req.setBaseErrResponse(AcceptInviteResponse.InviteStatus.ERR_BAD_INVITE);
                filter.sendResponse(req);

                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleAcceptInvite invite query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }
        finally
        {
            db.closeConnection(invQ);
        }

        //Validate IDs
        if (landlordID == null || landlordID < 1)
        {
            //Bad landlord
            System.out.println("ERROR: Invite did not have a landlord");
            req.setBaseErrResponse(AcceptInviteResponse.InviteStatus.ERR_BAD_INVITE);
            filter.sendResponse(req);

            return;
        }

        if (storedTenID == null || storedTenID < 1)
        {
            //Bad stored tenant
            System.out.println("ERROR: Invite did not have a tenant");
            req.setBaseErrResponse(AcceptInviteResponse.InviteStatus.ERR_BAD_INVITE);
            filter.sendResponse(req);

            return;
        }

        if (propertyID == null || propertyID < 1)
        {
            //Bad property
            System.out.println("ERROR: Invite did not have a property");
            req.setBaseErrResponse(AcceptInviteResponse.InviteStatus.ERR_BAD_INVITE);
            filter.sendResponse(req);

            return;
        }

        //Make sure invite is for this user
        if (tenantID != storedTenID)
        {
            //Invite is for someone else
            req.setBaseErrResponse(AcceptInviteResponse.InviteStatus.ERR_BAD_INVITE);
            filter.sendResponse(req);

            return;
        }

        //Make sure property is not already occupied
        PreparedStatement propQ = null;
        Long activeLease = null;
        try
        {
            propQ = db.compileQuery("""
            SELECT activeLease
            FROM Properties
            WHERE propertyID = ?
            """);
            propQ.setLong(1, propertyID);

            ResultSet propR = propQ.executeQuery();

            if (propR.next())
            {
                //Got the property
                activeLease = propR.getLong("activeLease");
            }
            else
            {
                //Property not found, malformed invite
                req.setBaseErrResponse(AcceptInviteResponse.InviteStatus.ERR_BAD_INVITE);
                filter.sendResponse(req);

                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleAcceptInvite property query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }
        finally
        {
            db.closeConnection(propQ);
        }

        if (activeLease != null && activeLease > 0)
        {
            //A lease already exists for this property
            req.setBaseErrResponse(AcceptInviteResponse.InviteStatus.ERR_LEASE_OCCUPIED);
            filter.sendResponse(req);
        }

        if (accept)
        {
            //Create a new lease
            long leaseID = -1;
            PreparedStatement leaseQ = null;
            try
            {
                leaseQ = db.compileQuery("""
                        INSERT INTO Leases (
                        associatedProperty,
                        tenantID,
                        active,
                        dateMade
                        )
                        VALUES (?, ?, ?, ?)""", Statement.RETURN_GENERATED_KEYS);

                leaseQ.setLong(1, propertyID);
                leaseQ.setLong(2, tenantID);
                leaseQ.setBoolean(3, true);
                leaseQ.setString(4, LocalDateTime.now().toString());

                leaseQ.executeUpdate();

                //Get the newly made primary key
                ResultSet res = leaseQ.getGeneratedKeys();
                if (res.next())
                {
                    leaseID = res.getLong(1);
                }
                else throw new SQLException("Failed to generate lease primary key");
            }
            catch (SQLException e)
            {
                System.out.println("ERROR: SQLException during handleAcceptInvite lease query: " + e.toString());

                req.setUnknownErrResponse();
                filter.sendResponse(req);
                return;
            }
            finally
            {
                db.closeConnection(leaseQ);
            }

            //Update property state
            PreparedStatement propQ2 = null;
            try
            {
                propQ2 = db.compileQuery("""
                        UPDATE Properties
                        SET activeLease = ?
                        WHERE propertyID = ?
                        """);

                propQ2.setLong(1, leaseID);
                propQ2.setLong(2, propertyID);

                propQ2.executeUpdate();
            }
            catch (SQLException e)
            {
                System.out.println("ERROR: SQLException during handleAcceptInvite property update query: " + e.toString());

                req.setUnknownErrResponse();
                filter.sendResponse(req);
                return;
            }
            finally
            {
                db.closeConnection(propQ2);
            }

            //Set landlord and tenant permissions
            PreparedStatement permsLQ = null;
            PreparedStatement permsTQ = null;
            try
            {
                //Landlord
                permsLQ = db.compileQuery("""
                        INSERT INTO LeasePermissions (
                        userID,
                        leaseID,
                        editName,
                        editAddress,
                        viewPaymentsPage,
                        genericTicketPerms,
                        maintTicketPerms,
                        taxTicketPerms,
                        rentTicketPerms,
                        viewTaxFinData,
                        addLeaseContract,
                        proposeLeaseChange,
                        viewLeaseContract,
                        addGenericDoc,
                        viewGenericDoc
                        )
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""");

                permsLQ.setLong(1, landlordID);
                permsLQ.setLong(2, leaseID);
                permsLQ.setBoolean(3, true);
                permsLQ.setBoolean(4, true);
                permsLQ.setBoolean(5, true);
                permsLQ.setInt(6, PermissionsEnum.Allowed.READ | PermissionsEnum.Allowed.WRITE | PermissionsEnum.Allowed.COMMENT);
                permsLQ.setInt(7, PermissionsEnum.Allowed.READ | PermissionsEnum.Allowed.WRITE | PermissionsEnum.Allowed.COMMENT);
                permsLQ.setInt(8, PermissionsEnum.Allowed.READ | PermissionsEnum.Allowed.WRITE | PermissionsEnum.Allowed.COMMENT);
                permsLQ.setInt(9, PermissionsEnum.Allowed.READ | PermissionsEnum.Allowed.WRITE | PermissionsEnum.Allowed.COMMENT);
                permsLQ.setBoolean(10, true);
                permsLQ.setBoolean(11, true);
                permsLQ.setBoolean(12, true);
                permsLQ.setBoolean(13, true);
                permsLQ.setBoolean(14, true);
                permsLQ.setBoolean(15, true);

                permsLQ.executeUpdate();

                //Tenant
                permsTQ = db.compileQuery("""
                        INSERT INTO LeasePermissions (
                        userID,
                        leaseID,
                        editName,
                        editAddress,
                        viewPaymentsPage,
                        genericTicketPerms,
                        maintTicketPerms,
                        taxTicketPerms,
                        rentTicketPerms,
                        viewTaxFinData,
                        addLeaseContract,
                        proposeLeaseChange,
                        viewLeaseContract,
                        addGenericDoc,
                        viewGenericDoc
                        )
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""");

                permsTQ.setLong(1, tenantID);
                permsTQ.setLong(2, leaseID);
                permsTQ.setBoolean(3, false);
                permsTQ.setBoolean(4, false);
                permsTQ.setBoolean(5, true);
                permsTQ.setInt(6, PermissionsEnum.Allowed.READ | PermissionsEnum.Allowed.WRITE | PermissionsEnum.Allowed.COMMENT);
                permsTQ.setInt(7, PermissionsEnum.Allowed.READ | PermissionsEnum.Allowed.WRITE | PermissionsEnum.Allowed.COMMENT);
                permsTQ.setInt(8, PermissionsEnum.Allowed.NONE);
                permsTQ.setInt(9, PermissionsEnum.Allowed.READ);
                permsTQ.setBoolean(10, false);
                permsTQ.setBoolean(11, false);
                permsTQ.setBoolean(12, true);
                permsTQ.setBoolean(13, true);
                permsTQ.setBoolean(14, true);
                permsTQ.setBoolean(15, true);

                permsTQ.executeUpdate();
            }
            catch (SQLException e)
            {
                System.out.println("ERROR: SQLException during handleAcceptInvite permissions query: " + e.toString());

                req.setUnknownErrResponse();
                filter.sendResponse(req);
                return;
            }
            finally
            {
                db.closeConnection(permsLQ);
                db.closeConnection(permsTQ);
            }
        }
        //Delete invite
        PreparedStatement invQ2 = null;
        try
        {
            invQ2 = db.compileQuery("""
                    DELETE FROM Invites
                    WHERE inviteID = ?
                    """);

            invQ2.setLong(1, inviteID);

            invQ2.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleAcceptInvite invite delete query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }
        finally
        {
            db.closeConnection(invQ2);
        }

        //Succeeded, send OK
        AcceptInviteResponse resp = new AcceptInviteResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        req.setResponse(resp);
        filter.sendResponse(req);
    }

    public void handleGetInviteList(ClientRequest req)
    {
        GetInviteListPacket packet = (GetInviteListPacket) req.packet;

        //Values to retrieve
        ArrayList<Long> invites = new ArrayList<Long>();

        //Get userID
        long userID = userIDFromToken(packet.token);
        if (userID == -1)
        {
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);
        }

        //Query DB for invite list
        PreparedStatement invitesQ = null;
        try
        {
            invitesQ = db.compileQuery("""
            SELECT inviteID
            FROM Invites
            WHERE tenantID = ?
            """);
            invitesQ.setLong(1, userID);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleGetInviteList invite query compilation: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }

        ResultSet invitesR = null;
        try
        {
            invitesR = invitesQ.executeQuery();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleGetInviteList invites query execution: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            db.closeConnection(invitesQ);
            return;
        }

        try
        {
            while (invitesR.next())
            {
                //adds inviteID to invites list
                invites.add(invitesR.getLong("inviteID"));
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleGetInviteList invite response parsing: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            db.closeConnection(invitesQ);
            return;
        }
        finally
        {
            db.closeConnection(invitesQ);
        }
        if (invites.isEmpty()) { invites = null; }

        //Build response and send
        GetInviteListResponse resp = new GetInviteListResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.INVITES = invites;
        req.setResponse(resp);

        filter.sendResponse(req);
    }

    public void handleGetAcctLeasePacket(ClientRequest req)
    {
        GetAcctLeasePacket packet = (GetAcctLeasePacket) req.packet;

        //Get userID
        long userID = userIDFromToken(packet.token);
        if (userID == -1)
        {
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);
        }

        //Get if user is landlord or tenant
        PreparedStatement roleQ = null;
        Boolean isLandlord = null;
        try
        {
            roleQ = db.compileQuery("""
                SELECT isLandlord
                FROM Users
                WHERE userID = ?
                """);

            roleQ.setLong(1, userID);

            ResultSet roleR = roleQ.executeQuery();

            if (roleR.next())
            {
                isLandlord = roleR.getBoolean("isLandlord");
            }
            else
            {
                //User didn't exist in DB somehow
                req.setUnknownErrResponse();
                req.sendResponse();

                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleGetAcctLeasePacket user role query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(roleQ);
        }

        //Check if account is fully configured yet
        if (isLandlord == null)
        {
            //User is not part of a lease yet
            GetAcctLeaseResponse resp = new GetAcctLeaseResponse();
            resp.STATUS = GetAcctLeaseResponse.GetAcctLeaseStatus.ERR_NO_LEASE;
            req.setResponse(resp);
            filter.sendResponse(req);

            return;
        }

        //Get the lease ID, looking at different tables depending on role
        Long leaseID = null;
        if (isLandlord)
        {
            //Look in properties table for active lease
            PreparedStatement propQ = null;
            try
            {
                propQ = db.compileQuery("""
                SELECT activeLease
                FROM Properties
                WHERE owner = ?
                """);

                propQ.setLong(1, userID);

                ResultSet propR = propQ.executeQuery();

                if (propR.next())
                {
                    leaseID = propR.getLong("activeLease");
                }
                else
                {
                    //Property doesn't have a lease right now
                    GetAcctLeaseResponse resp = new GetAcctLeaseResponse();
                    resp.STATUS = GetAcctLeaseResponse.GetAcctLeaseStatus.ERR_NO_LEASE;
                    req.setResponse(resp);
                    filter.sendResponse(req);

                    return;
                }
            }
            catch (SQLException e)
            {
                System.out.println("ERROR: SQLException during handleGetAcctLeasePacket property query: " + e.toString());

                req.setUnknownErrResponse();
                filter.sendResponse(req);

                return;
            }
            finally
            {
                db.closeConnection(propQ);
            }
        }
        else
        {
            //Look in leases table for tenant
            PreparedStatement leaseQ = null;
            try
            {
                leaseQ = db.compileQuery("""
                SELECT leaseID
                FROM Leases
                WHERE tenantID = ?
                """);

                leaseQ.setLong(1, userID);

                ResultSet leaseR = leaseQ.executeQuery();

                if (leaseR.next())
                {
                    leaseID = leaseR.getLong("leaseID");
                }
                else
                {
                    //Tenant doesn't have a lease yet
                    GetAcctLeaseResponse resp = new GetAcctLeaseResponse();
                    resp.STATUS = GetAcctLeaseResponse.GetAcctLeaseStatus.ERR_NO_LEASE;
                    req.setResponse(resp);
                    filter.sendResponse(req);

                    return;
                }
            }
            catch (SQLException e)
            {
                System.out.println("ERROR: SQLException during handleGetAcctLeasePacket lease query: " + e.toString());

                req.setUnknownErrResponse();
                filter.sendResponse(req);

                return;
            }
            finally
            {
                db.closeConnection(leaseQ);
            }

            //Double check we actually got a lease
            if (leaseID == null || leaseID < 1)
            {
                //No lease
                GetAcctLeaseResponse resp = new GetAcctLeaseResponse();
                resp.STATUS = GetAcctLeaseResponse.GetAcctLeaseStatus.ERR_NO_LEASE;
                req.setResponse(resp);
                filter.sendResponse(req);

                return;
            }

        }

        //If we got here, we got the lease successfully
        GetAcctLeaseResponse resp = new GetAcctLeaseResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.LEASE = leaseID;
        req.setResponse(resp);
        filter.sendResponse(req);

        return;
    }
}
