package com.propertypal.shared.network.enums;

public class RoleEnum
{
    public static class Role
    {
        public static final int LANDLORD = 0;
        public static final int TENANT = 1;
        public static final int MAINT = 2;
        public static final int TAX = 3;
        public static final int UNKNOWN = -1;

        public static boolean validate(int enumVal)
        {
            return enumVal >= UNKNOWN && enumVal <= TAX;
        }
    }
}
