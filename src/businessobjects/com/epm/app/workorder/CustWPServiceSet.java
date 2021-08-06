package com.epm.app.workorder;

import java.rmi.RemoteException;
import psdi.mbo.*;
import psdi.plusd.app.workorder.PlusDWPServiceSet;
import psdi.plusd.app.workorder.PlusDWPServiceSetRemote;
import psdi.util.MXException;

public class CustWPServiceSet extends PlusDWPServiceSet implements PlusDWPServiceSetRemote
{
    public CustWPServiceSet(MboServerInterface arg0) throws MXException, RemoteException
    {
        super(arg0);
    }

    protected Mbo getMboInstance(MboSet mboset) throws MXException, RemoteException
    {
        return new CustWPService(mboset);
    }
}