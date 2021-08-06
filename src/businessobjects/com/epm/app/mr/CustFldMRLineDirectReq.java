package com.epm.app.mr;

import java.rmi.RemoteException;

import psdi.app.mr.FldMRLineDirectReq;
import psdi.mbo.MboValue;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
//psdi.app.mr.FldMRLineDirectReq
//com.epm.app.mr.CustFldMRLineDirectReq

public class CustFldMRLineDirectReq extends FldMRLineDirectReq
{
	private static MXLogger logger;

    public CustFldMRLineDirectReq(MboValue mbv) throws MXException
    {
    	super(mbv);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

    public void action() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldMRLineDirectReq.action: INICIO");

        super.action();
        
        logger.debug("01 CustFldMRLineDirectReq.action: if(getMboValue().getBoolean())= " + (getMboValue().getBoolean()));
    	if(getMboValue().getBoolean())
    	{
    		getMboValue("STORELOCSITE").setValueNull();
    	}
		
    	CustMRLine mrline = (CustMRLine)getMboValue().getMbo();
        mrline.voManejoCuentas();

    	logger.debug("00 CustFldMRLineDirectReq.action: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
}

