package com.propertypal.network.responses;

public class LogoutResponse extends BaseResponse
{
    //Doesn't need anything more than what its parent offers

    public static class LogoutStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_IP = 1;
    }
}
