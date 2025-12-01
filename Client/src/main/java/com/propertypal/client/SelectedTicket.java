package com.propertypal.client;

import javafx.collections.ObservableList;

//static class to store currently selected ticket
//similar to DEMOSelectedTicket class

public class SelectedTicket
{

    private static ObservableList<String> selected;

    public static void set(ObservableList<String> tkt)
    {
        selected = tkt;
    }

    public static ObservableList<String> get()
    {
        return selected;
    }

    public static void clear()
    {
        selected = null;
    }
}