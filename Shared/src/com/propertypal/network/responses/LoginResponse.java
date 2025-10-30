package com.propertypal.network.responses;

public class LoginResponse extends BaseResponse
{
    public String TOKEN;

    public static class LoginStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_EMAIL = 1;
        public static final int ERR_BAD_PASSWORD = 2;
    }
}
