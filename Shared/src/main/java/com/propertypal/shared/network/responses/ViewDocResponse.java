package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;
import com.propertypal.shared.network.enums.PermissionsEnum;

public class ViewDocResponse extends BaseResponse
{
    public int PERMISSIONS;
    public int MIME_TYPE;
    public Long FILE_SIZE;
    public String DOC_DATA;

    public static class ViewDocPerms extends PermissionsEnum
    {
        public static final int NONE = 0;
        public static final int READ = 1;
        public static final int WRITE = 2;
        public static final int COMMENT = 4;
        public static final int ESIGN = 8;
    }

    public static class ViewDocStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_DOC = 1;
        public static final int ERR_BAD_MIME = 2;
        public static final int ERR_ILLEGAL_SIZE = 4;
        public static final int CORRUPT_PAYLOAD = 8;
    }
}
