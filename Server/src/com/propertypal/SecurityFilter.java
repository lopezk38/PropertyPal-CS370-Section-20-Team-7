package com.propertypal;

import com.propertypal.filters.AuthFilters;
import com.propertypal.network.responses.*;
import com.propertypal.network.packets.*;

public class SecurityFilter
{
    private static SecurityFilter instance = null;

    private AuthFilters authFilter = new AuthFilters();

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

    private int enforceLoggedIn(ClientRequest req) { return authFilter.enforceLoggedIn(req); }

    //Auth
    public void filterLoginPacket(ClientRequest req) { authFilter.filterLoginPacket(req); }
    public void filterLogoutPacket(ClientRequest req) { authFilter.filterLogoutPacket(req); }

    //TODO add methods for each filter for modularity

    //Outbound
    public void sendResponse(ClientRequest request) { request.sendResponse(); }
}
