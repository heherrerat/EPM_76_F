package com.epm.app.plusdcuest;

import java.rmi.RemoteException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import psdi.app.workorder.WORemote;
import psdi.mbo.*;
import psdi.plusd.app.workorder.PlusDWO;
import psdi.plusd.common.mbo.MboSetIterator;
import psdi.plusd.common.mbo.filter.SumSupport;
import psdi.security.UserInfo;
import psdi.server.*;
import psdi.util.*;
import psdi.util.logging.*;
import psdi.plusd.app.plusdcuest.*;
import psdi.app.workorder.*;

public class CustPlusDEstControl extends PlusDEstControl implements PlusDEstControlRemote
{
	private static MXLogger logger;

    public CustPlusDEstControl(MboSet mboset) throws MXException, RemoteException
    {
        super(mboset);
		logger = MXLoggerFactory.getLogger("maximo.application");
    }
    
    public void save() throws MXException, RemoteException
    {
        super.save();	
        WO wo = (WO)getMboSet("WORKORDERREL1").getMbo(0);
        if (wo!=null)
        {
        	logger.debug("00 CustPlusDEstControl.save: Despues de obtener la OT asociada: " + wo.getString("WONUM") + " Unidad de Negocio: " + wo.getString("SLXCENTROACTIVIDAD"));
        	logger.debug("01 isNew(): " + isNew());
        	logger.debug("02 toBeDeleted(): " + toBeDeleted());
        	logger.debug("03 isModified(SLXUNIDNEGOCIO): " + isModified("SLXUNIDNEGOCIO"));
        	logger.debug("04 SLXUNIDNEGOCIO: " + getString("SLXUNIDNEGOCIO"));
        	logger.debug("05 isModified(MASTERWONUM): " + isModified("MASTERWONUM"));
        	logger.debug("05 isModified(ACCEPTEDVERSION): " + isModified("ACCEPTEDVERSION"));
	        if ( isNew() && !toBeDeleted() || isModified("SLXUNIDNEGOCIO") || isModified("MASTERWONUM") || isModified("ACCEPTEDVERSION") )
	        {
	        	//wo.setValueNull("SLXCENTROACTIVIDAD", 11L);
	        	//wo.setValue("SLXCENTROACTIVIDAD", getString("SLXUNIDNEGOCIO"));
	        	logger.debug("06 NO se actualizará la Unidad de Negocio en la OT, pero si se debiese llenar en los planificiados...");
	        }
        }
    }
}