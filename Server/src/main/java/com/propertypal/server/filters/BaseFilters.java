package com.propertypal.server.filters;

import com.propertypal.server.ClientRequest;
import com.propertypal.server.SecurityFilter;
import com.propertypal.server.CoreLogic;
import com.propertypal.server.DbWrapper;

import com.propertypal.shared.network.responses.*;

public abstract class BaseFilters
{
    protected SecurityFilter filter = SecurityFilter.getInstance();
    protected CoreLogic logic = CoreLogic.getInstance();
    protected DbWrapper db = DbWrapper.getInstance();
}