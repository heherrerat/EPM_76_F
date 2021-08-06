package com.epm.app.labor;

import java.rmi.RemoteException;
import psdi.app.labor.ServRecTransSet;
import psdi.app.labor.ServRecTransSetRemote;
import psdi.mbo.*;
import psdi.util.MXException;

public class CustServRecTransSet extends ServRecTransSet implements ServRecTransSetRemote, MboSetListenable
{
    public CustServRecTransSet(MboServerInterface ms)
        throws MXException, RemoteException
    {
        super(ms);
    }
    
    protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException
    {
        return new CustServRecTrans(ms);
    }
}