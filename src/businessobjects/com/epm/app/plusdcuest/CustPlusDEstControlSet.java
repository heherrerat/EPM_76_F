package com.epm.app.plusdcuest;

import java.rmi.RemoteException;
import psdi.mbo.*;
import psdi.security.ProfileRemote;
import psdi.txn.MXTransaction;
import psdi.util.MXException;
import psdi.plusd.app.plusdcuest.*;

public class CustPlusDEstControlSet extends PlusDEstControlSet implements PlusDEstControlSetRemote
{
    public CustPlusDEstControlSet(MboServerInterface mboserverinterface) throws MXException, RemoteException
    {
        super(mboserverinterface);
    }

    protected Mbo getMboInstance(MboSet mboset) throws MXException, RemoteException
    {
        return new CustPlusDEstControl(mboset);
    }
}