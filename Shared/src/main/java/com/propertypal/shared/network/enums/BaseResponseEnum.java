package com.propertypal.shared.network.enums;

public class BaseResponseEnum //Had to define our own enum to get around Java limitations
{
    public static final int SUCCESS = 0;
    public static final int ERR_PERMISSION_DENIED = 134217728; //sets bit 28 to 1
    public static final int ERR_BAD_TOKEN = 268435456; //sets bit 29 to 1
    public static final int ERR_BAD_TIMESTAMP = 536870912; //sets bit 30 to 1
    public static final int ERR_BAD_JSON = 1073741824; //sets bit 31 to 1
    public static final int ERR_UNKNOWN = -2147483648; //sets bit 32 to 1
}
