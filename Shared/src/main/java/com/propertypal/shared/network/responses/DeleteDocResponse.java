package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class DeleteDocResponse extends BaseResponse
{
    public static class DeleteDocStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_DOC = 1;
    }
}
