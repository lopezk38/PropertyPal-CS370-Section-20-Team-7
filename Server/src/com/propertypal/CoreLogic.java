package com.propertypal;

import com.propertypal.logic.*;

public class CoreLogic
{
    private static CoreLogic instance = null;

    private AuthLogic authLogic = new AuthLogic();

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

    //AuthLogic
    public void handleLogin(ClientRequest req) { authLogic.handleLogin(req); }
    //TODO add more
}
