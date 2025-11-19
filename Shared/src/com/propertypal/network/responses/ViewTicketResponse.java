package com.propertypal.network.responses;

import com.propertypal.network.enums.BaseResponseEnum;

import java.util.List;

public class ViewTicketResponse extends BaseResponse
{
    public int TICKET_TYPE; //Use TicketEnums to set
    public int TICKET_STATE; //Use TicketEnums to set
    public String DESCRIPTION;
    public int PERMISSIONS; //Use PermissionsEnum to set
    public List<Long> ATTACHMENT_IDS;
    public List<Long> COMMENT_IDS;

    public static class TicketStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_TICKET = 1;
    }
}
