package com.epm.app.workorder.virtual;

import psdi.app.workorder.virtual.*;

import java.rmi.RemoteException;

import psdi.app.workorder.WO;
import psdi.mbo.*;
import psdi.util.*;


public class CustWOTotalSet extends WOTotalSet implements NonPersistentMboSetRemote
{
    public CustWOTotalSet(MboServerInterface ms) throws MXException, RemoteException
    {
        super(ms);
    }

    protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException
    {
        return new CustWOTotal(ms);
    }
    
	public MboRemote setup() throws MXException, RemoteException
    {
        WO ownerWO = (WO)getOwner();
        MboRemote woTotalRemote = null;
        woTotalRemote = super.setup();
        woTotalRemote = addAtEnd();

        // Coste Total Ejecución Contratista
        woTotalRemote.setValue("total", "Costo para Liquidar", 11L);
        woTotalRemote.setValueNull("est", 11L);
        woTotalRemote.setValueNull("estatappr", 11L);
        woTotalRemote.setValueNull("exceeded", 11L);
        //double doLineCost = ownerWO.getMboSet("SLX_EJEC_CONT").sum("LINECOST");
        
        double doLineCost = 0.0D;
    	MboSetRemote mboSetEjec = null;
    	MboRemote mboEjec = null;
    	mboSetEjec = ownerWO.getMboSet("SLX_EJEC_CONT");
        if(!mboSetEjec.isEmpty())
        {
        	for(int j=0; (mboEjec=mboSetEjec.getMbo(j))!=null; j++)
        		doLineCost += (mboEjec.getDouble("UNITCOST") * mboEjec.getDouble("QUANTITY"));
        }
        woTotalRemote.setValue("act", doLineCost, 11L);
        return woTotalRemote;
    }
}