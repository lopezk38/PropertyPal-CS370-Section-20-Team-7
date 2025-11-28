package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

import java.time.LocalDateTime;

public class GetTicketInfoResponse extends BaseResponse
{
    public String DESCRIPTION;
    public int STATE; //Use TicketEnums.State to set this
    public int TYPE; //Use TicketEnums.Type to set this
    public LocalDateTime LAST_UPDATED;

    public static class GetTicketInfoStatus extends BaseResponseEnum
    {
        public static int ERR_BAD_TICKET = 1;
    }
}
