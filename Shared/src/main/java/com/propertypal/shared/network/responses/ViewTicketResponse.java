package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

import java.util.ArrayList;
import java.util.List;

public class ViewTicketResponse extends BaseResponse
{
    public Integer TICKET_TYPE; //Use TicketEnums to set
    public Integer TICKET_STATE; //Use TicketEnums to set
    public String TITLE;
    public String DESCRIPTION;
    public Integer PERMISSIONS; //Use PermissionsEnum to set
    public ArrayList<Long> ATTACHMENT_IDS;
    public ArrayList<Long> COMMENT_IDS;

    public static class TicketStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_TICKET = 1;
    }
}
