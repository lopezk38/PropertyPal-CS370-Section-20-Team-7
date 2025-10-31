package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.CoreLogic;
import com.propertypal.DbWrapper;
import com.propertypal.network.responses.*;

public class BaseFilters
{
    private CoreLogic logic = CoreLogic.getInstance();
    private DbWrapper db = DbWrapper.getInstance();
}