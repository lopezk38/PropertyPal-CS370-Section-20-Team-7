package com.propertypal.client;

import javafx.collections.ObservableList;

public class DEMOSelectedTicket
{

    private static ObservableList<String> ticket;

    public static void set(ObservableList<String> t)
    {
        ticket = t;
    }

    public static ObservableList<String> get()
    {
        return ticket;
    }

    public static void clear()
    {
        ticket = null;
    }
}