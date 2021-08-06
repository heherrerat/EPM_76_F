package com.epm.app.labor;

import java.rmi.RemoteException;
import psdi.app.labor.LabTransSet;
import psdi.app.labor.LabTransSetRemote;
import psdi.mbo.*;
import psdi.util.MXException;

public class CustLabTransSet extends LabTransSet implements LabTransSetRemote, MboSetListenable
{
    public CustLabTransSet(MboServerInterface ms) throws MXException, RemoteException
    {
        super(ms);
    }

    protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException
    {
        return new CustLabTrans(ms);
    }
}