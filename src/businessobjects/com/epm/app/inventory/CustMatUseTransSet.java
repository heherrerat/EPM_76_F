package com.epm.app.inventory;

import java.rmi.RemoteException;
import psdi.app.inventory.MatUseTransSet;
import psdi.app.inventory.MatUseTransSetRemote;
import psdi.mbo.*;
import psdi.util.MXException;

public class CustMatUseTransSet extends MatUseTransSet implements MatUseTransSetRemote, MboSetListenable
{
    public CustMatUseTransSet(MboServerInterface ms) throws MXException, RemoteException
    {
        super(ms);
    }

    protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException
    {
        return new CustMatUseTrans(ms);
    }
}