package com.epm.app.workorder;

import java.rmi.RemoteException;
import psdi.mbo.*;
import psdi.plusd.app.workorder.PlusDWPMaterialSet;
import psdi.plusd.app.workorder.PlusDWPMaterialSetRemote;
import psdi.util.MXException;

public class CustWPMaterialSet extends PlusDWPMaterialSet implements PlusDWPMaterialSetRemote
{
    public CustWPMaterialSet(MboServerInterface arg0) throws MXException, RemoteException
    {
        super(arg0);
    }

    protected Mbo getMboInstance(MboSet mboset) throws MXException, RemoteException
    {
        return new CustWPMaterial(mboset);
    }
}