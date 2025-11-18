package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.SecurityFilter;
import com.propertypal.CoreLogic;
import com.propertypal.DbWrapper;
import com.propertypal.network.responses.*;

public class BaseFilters
{
    protected SecurityFilter filter = null;
    protected CoreLogic logic = CoreLogic.getInstance();
    protected DbWrapper db = DbWrapper.getInstance();
}