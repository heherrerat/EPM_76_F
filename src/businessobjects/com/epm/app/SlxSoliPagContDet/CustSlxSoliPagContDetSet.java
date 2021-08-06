package com.epm.app.SlxSoliPagContDet;

import java.rmi.RemoteException;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.custapp.CustomMboSet;
import psdi.util.MXException;

public class CustSlxSoliPagContDetSet extends CustomMboSet
{
	public CustSlxSoliPagContDetSet(MboServerInterface ms) throws MXException, RemoteException
	{
		super(ms);
    }

	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
        return new CustSlxSoliPagContDet(ms);
    }
}