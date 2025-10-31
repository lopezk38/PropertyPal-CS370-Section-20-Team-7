package com.propertypal;

import java.io.IOException;

public class Launcher
{
    public static void main(String[] args) throws java.lang.InterruptedException, IOException
    {
        NetServer httpServer = new NetServer(678);
        System.out.println("Server started on port " + Integer.toString(httpServer.getPort()));

        Thread.currentThread().join(); //Sleep indefinitely

        httpServer.close();
    }
}
