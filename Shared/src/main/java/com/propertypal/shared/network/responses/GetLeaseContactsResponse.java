package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class GetLeaseContactsResponse extends BaseResponse
{
    public String LL_FNAME;
    public String LL_LNAME;
    public String LL_EMAIL;
    public String LL_PHONE;
    public String TT_FNAME;
    public String TT_LNAME;
    public String TT_EMAIL;
    public String TT_PHONE;

    public static class GetLeaseContactsStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_LEASE = 1;
        public static final int ERR_NOT_PARTICIPANT = 2;
    }
}
