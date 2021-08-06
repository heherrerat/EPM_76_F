package com.epm.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.plusd.app.workorder.PlusDWOSet;
import psdi.plusd.app.workorder.PlusDWOSetRemote;
import psdi.util.MXException;

public class CustWOSet extends PlusDWOSet implements PlusDWOSetRemote
{
    public CustWOSet(MboServerInterface arg0) throws MXException, RemoteException
    {
        super(arg0);
    }

    protected Mbo getMboInstance(MboSet mboset) throws MXException, RemoteException
    {
        return new CustWO(mboset);
    }
}
