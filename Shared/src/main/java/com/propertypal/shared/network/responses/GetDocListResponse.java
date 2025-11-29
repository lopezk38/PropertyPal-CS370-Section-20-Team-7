package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;
import java.util.List;

public class GetDocListResponse extends BaseResponse
{
    public List<Long> DOCS = null;

    public static class GetDocListStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_LEASE = 1;
    }
}
