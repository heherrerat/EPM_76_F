package com.epm.app.mr;

import java.rmi.RemoteException;
import psdi.app.mr.MRLineSet;
import psdi.app.mr.MRLineSetRemote;
import psdi.mbo.*;
import psdi.util.MXException;

public class CustMRLineSet extends MRLineSet implements MRLineSetRemote
{
    public CustMRLineSet(MboServerInterface ms) throws MXException, RemoteException
    {
        super(ms);
    }

    protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException
    {
        return new CustMRLine(ms);
    }
}