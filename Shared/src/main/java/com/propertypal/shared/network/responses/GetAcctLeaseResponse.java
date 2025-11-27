package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class GetAcctLeaseResponse extends BaseResponse
{
    public Long LEASE;

    public static class GetAcctLeaseStatus extends BaseResponseEnum
    {
        public static final int ERR_NO_LEASE = 1;
    }
}
