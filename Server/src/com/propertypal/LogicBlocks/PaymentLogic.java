package com.propertypal.LogicBlocks;

import com.propertypal.ClientRequest;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.enums.*;
import com.propertypal.shared.network.helpers.*;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class PaymentLogic extends BaseLogic
{
    public void handleRequestRent(ClientRequest req)
    {
        RequestRentPacket packet = (RequestRentPacket) req.packet;
        Long leaseID = packet.lease_id;

        //Spot validate lease
        if (leaseID == null || leaseID <= 0)
        {
            //Invalid lease given
            System.out.println("ERROR: Got invalid leaseID from client in handleRequestRent");

            req.setBaseErrResponse(RequestRentResponse.RequestRentStatus.ERR_BAD_LEASE);
            filter.sendResponse(req);
            return;
        }

        //Query for rent data
        PreparedStatement leaseQ = null;
        Integer dueDay = null;
        BigDecimal amount = null;
        try
        {
            leaseQ = db.compileQuery("""
                        SELECT rentDueDay, rentAmount
                        FROM Leases
                        WHERE leaseID = ?
                        """);

            leaseQ.setLong(1, leaseID);

            ResultSet leaseR = leaseQ.executeQuery();

            if (leaseR.next())
            {
                dueDay = leaseR.getInt("rentDueDay");
                amount = leaseR.getBigDecimal("rentAmount");
            }
            else
            {
                //Returned no data, lease did not exist
                System.out.println("WARNING: handleRequestRent rent data query returned no data. Invalid lease?");

                req.setBaseErrResponse(RequestRentResponse.RequestRentStatus.ERR_BAD_LEASE);
                filter.sendResponse(req);
                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleUpdAmountDue lease update query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }
        finally
        {
            db.closeConnection(leaseQ);
        }

        //Check rent data is valid
        if (dueDay == null || dueDay < 1 || dueDay > 31)
        {
            //Invalid due day
            System.out.println("WARNING: handleRequestRent rent data query returned invalid due day");

            req.setBaseErrResponse(RequestRentResponse.RequestRentStatus.ERR_NO_RENT_SETUP);
            filter.sendResponse(req);
            return;
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) //is null or negative
        {
            //Invalid amount
            System.out.println("WARNING: handleRequestRent rent data query returned invalid amount");

            req.setBaseErrResponse(RequestRentResponse.RequestRentStatus.ERR_NO_RENT_SETUP);
            filter.sendResponse(req);
            return;
        }

        //Get date of last request
        PreparedStatement lastReqQ = null;
        LocalDateTime lastReq = null;
        try
        {
            lastReqQ = db.compileQuery("""
                        SELECT MAX(dueDate)
                        FROM RentRequests
                        WHERE leaseID = ?
                        """);

            lastReqQ.setLong(1, leaseID);

            ResultSet lastReqR = lastReqQ.executeQuery();

            if (lastReqR.next())
            {
                lastReq = lastReqR.getTimestamp(1).toLocalDateTime();
            }
            else
            {
                //No rent request history exists
                System.out.println("WARNING: handleRequestRent last rent request date query returned null. Error or new lease?");
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleRequestRent last request query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }
        finally
        {
            db.closeConnection(lastReqQ);
        }

        //Generate rent request
        //Do this month (or next month if target day already passed)
        LocalDateTime curDate = LocalDateTime.now();
        LocalDateTime nextDueDate = LocalDateTime.of(curDate.getYear(), curDate.getMonth(), dueDay, 0, 0);
        if (nextDueDate.isBefore(curDate))
        {
            nextDueDate = nextDueDate.plusMonths(1);
        }

        //Make sure new request is not overlapping with an existing request
        if (lastReq != null && !nextDueDate.isAfter(lastReq))
        {
            //It's overlapping, add a month
            nextDueDate = nextDueDate.plusMonths(1);
        }

        PreparedStatement reqGenQ = null;
        try
        {
            reqGenQ = db.compileQuery("""
                        INSERT INTO RentRequests
                        (
                            leaseID,
                            dateMade,
                            dueDate,
                            amount,
                            paid
                        )
                        VALUES (?, ?, ?, ?, false)
                        """);

            reqGenQ.setLong(1, leaseID);
            reqGenQ.setString(2, curDate.toString());
            reqGenQ.setString(3, nextDueDate.toString());
            reqGenQ.setBigDecimal(4, amount);

            reqGenQ.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleRequestRent request gen query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }
        finally
        {
            db.closeConnection(reqGenQ);
        }

        //Succeeded, send OK
        RequestRentResponse resp = new RequestRentResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        req.setResponse(resp);
        filter.sendResponse(req);
    }

    public void handleUpdAmountDue(ClientRequest req)
    {
        UpdateAmountDuePacket packet = (UpdateAmountDuePacket) req.packet;

        Long leaseID = packet.lease_id;
        String rawAmount = packet.amount;
        BigDecimal amount;
        Integer dueDay = packet.dueDay;

        //Validate vars
        if (leaseID == null || leaseID <= 0)
        {
            //Invalid lease given
            System.out.println("ERROR: Got invalid leaseID from client in handleUpdAmountDue");

            req.setBaseErrResponse(UpdateAmountDueResponse.UpdateAmountDueStatus.ERR_BAD_LEASE);
            filter.sendResponse(req);
            return;
        }

        //Attempt to cast amount to BigDecimal
        try
        {
            amount = new BigDecimal(rawAmount);
        }
        catch (NumberFormatException e)
        {
            //Invalid number string given
            System.out.println("ERROR: Got invalid BigDecimal string from client in handleUpdAmountDue");

            req.setBaseErrResponse(UpdateAmountDueResponse.UpdateAmountDueStatus.ERR_BAD_AMOUNT);
            filter.sendResponse(req);
            return;
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) //is null or negative
        {
            //Invalid amount
            req.setBaseErrResponse(UpdateAmountDueResponse.UpdateAmountDueStatus.ERR_BAD_AMOUNT);
            filter.sendResponse(req);
            return;
        }

        if (dueDay == null || dueDay < 1 || dueDay > 31)
        {
            //Invalid due day given
            System.out.println("ERROR: Got invalid due day from client in handleUpdAmountDue");

            req.setBaseErrResponse(UpdateAmountDueResponse.UpdateAmountDueStatus.ERR_BAD_DUE_DAY);
            filter.sendResponse(req);
            return;
        }

        //Update lease state
        PreparedStatement leaseQ = null;
        try
        {
            leaseQ = db.compileQuery("""
                        UPDATE Leases
                        SET rentDueDay = ?, rentAmount = ?, rentLastUpdated = ?
                        WHERE leaseID = ?
                        """);

            leaseQ.setLong(1, dueDay);
            leaseQ.setBigDecimal(2, amount);
            leaseQ.setString(3, LocalDateTime.now().toString());
            leaseQ.setLong(4, leaseID);

            leaseQ.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleUpdAmountDue lease update query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }
        finally
        {
            db.closeConnection(leaseQ);
        }

        //Succeeded, send OK
        UpdateAmountDueResponse resp = new UpdateAmountDueResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        req.setResponse(resp);
        filter.sendResponse(req);
    }

    public void handlePayRent(ClientRequest req) { ; } //TODO
}
