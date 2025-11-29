package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class GetRoleResponse extends BaseResponse
{
    public Integer ROLE; //Use RoleEnum to set this

    public static class GetRoleStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_LEASE = 1;
        public static final int ERR_NOT_PARTICIPANT = 2;
    }
}
