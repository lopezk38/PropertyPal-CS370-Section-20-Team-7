package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class RequestRentResponse extends BaseResponse
{
    //No additional fields needed other than what's in the base

    public static class RequestRentStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_LEASE = 1;
        public static final int ERR_NO_RENT_SETUP = 2;
    }
}
