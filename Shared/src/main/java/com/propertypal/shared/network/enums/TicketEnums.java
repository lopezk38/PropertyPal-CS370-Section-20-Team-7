package com.propertypal.shared.network.enums;

public class TicketEnums
{
    public static class Type
    {
        public static final int STANDARD = 0;
        public static final int MAINTENANCE = 1;
        public static final int RENT = 2;
        public static final int TAX = 3;

        public static boolean validate(int enumVal)
        {
            return enumVal >= STANDARD && enumVal <= TAX;
        }
    }

    public static class State
    {
        public static final int NEW = 0;
        public static final int UNDER_REVIEW = 1;
        public static final int NEEDS_PREPAYMENT = 2;
        public static final int IN_PROGRESS = 3;
        public static final int NEEDS_PAYMENT = 4;
        public static final int CLOSED = 5;

        public static boolean validate(int enumVal)
        {
            return enumVal >= NEW && enumVal <= CLOSED;
        }
    }
}
