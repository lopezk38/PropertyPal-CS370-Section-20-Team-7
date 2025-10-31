package com.propertypal;

public class CoreLogic
{
    private static CoreLogic instance = null;

    public CoreLogic()
    {
        if (instance != null) throw new RuntimeException("Only one instance of CoreLogic may exist");

        instance = this;
    }

    public static CoreLogic getInstance()
    {
        if (instance == null)
        {
            instance = new CoreLogic();
        }

        return instance;
    }

    public void handleLogin(ClientRequest req)
    {
        DbWrapper db = DbWrapper.getInstance();

        //Query for user ID belonging to username
        //Query for hashed pw in db

        //Check for match
        //Respond w/ rejection if so

        //From here down, login succeeded
        //Generate token
        //Query to store token, IP, timestamp,
        //Respond with token
    }
}
