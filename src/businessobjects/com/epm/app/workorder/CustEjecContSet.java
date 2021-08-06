package com.epm.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.custapp.CustomMboSet;
import psdi.mbo.custapp.CustomMboSetRemote;
import psdi.util.MXException;

public class CustEjecContSet extends CustomMboSet implements CustomMboSetRemote
{
    public CustEjecContSet(MboServerInterface ms) throws MXException, RemoteException
    {
        super(ms);
    }

    protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException
    {
        return new CustEjecCont(ms);
    }
}