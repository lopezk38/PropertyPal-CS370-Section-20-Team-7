package com.propertypal;

import com.propertypal.filters.AuthFilters;
import com.propertypal.network.packets.*;

public class SecurityFilter
{
    private static SecurityFilter instance = null;

    private AuthFilters AuthFilter = new AuthFilters();

    public SecurityFilter()
    {
        if (instance != null) throw new RuntimeException("Only one instance of SecurityFilter may exist");

        instance = this;
    }

    public static SecurityFilter getInstance()
    {
        if (instance == null)
        {
            instance = new SecurityFilter();
        }

        return instance;
    }

    private BaseResponseEnum enforceLoggedIn(ClientRequest req) { AuthFilters.enforceLoggedIn(req); }
    public LoginResponse.LoginStatus filterLoginPacket(ClientRequest req) { AuthFilters.filterLoginPacket(req); }
    //TODO add methods for each filter for modularity
}
