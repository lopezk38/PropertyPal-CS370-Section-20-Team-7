package com.propertypal.network.responses;

import com.propertypal.network.enums.BaseResponseEnum;

public class CreateAcctResponse extends BaseResponse
{
    public String TOKEN;

    public static class AccountStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_EMAIL = 1;
        public static final int ERR_BAD_PASSWORD = 2;
        public static final int ERR_BAD_NAME = 4;
        public static final int ERR_BAD_PROP_ADDR1 = 8;
        public static final int ERR_BAD_PROP_ADDR2 = 16;
        public static final int ERR_BAD_PROP_SUITE = 32;
        public static final int ERR_BAD_PROP_CITY = 64;
        public static final int ERR_BAD_PROP_STATE = 128;
        public static final int ERR_BAD_PROP_ZIP = 256;
        public static final int ERR_BAD_PROP_COUNTRY = 512;
        public static final int ERR_BAD_BILL_ADDR1 = 1024;
        public static final int ERR_BAD_BILL_ADDR2 = 2048;
        public static final int ERR_BAD_BILL_SUITE = 4096;
        public static final int ERR_BAD_BILL_CITY = 2^13;
        public static final int ERR_BAD_BILL_STATE = 2^14;
        public static final int ERR_BAD_BILL_ZIP = 2^15;
        public static final int ERR_BAD_BILL_COUNTRY = 2^16;
        public static final int ERR_BAD_PHONE = 2^17;
    }
}
