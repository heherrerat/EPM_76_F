package com.epm.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
/*
update maxattribute    set classname = 'com.epm.app.workorder.CustFldEjecContCommon'
where objectname = 'SLX_EJEC_CONT' and attributename in ('ITEMNUM', 'CONTRACTNUM', 'LINETYPE'); 
update maxattributecfg set classname = 'com.epm.app.workorder.CustFldEjecContCommon'
where objectname = 'SLX_EJEC_CONT' and attributename in ('ITEMNUM', 'CONTRACTNUM', 'LINETYPE'); 
*/
public class CustFldEjecContCommon extends MboValueAdapter
{
	private static MXLogger logger;

	public CustFldEjecContCommon(MboValue mbv)
    {
        super(mbv);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

    public void action() throws MXException, RemoteException
    {
        logger.info("");
        logger.info("**********************************************************************");
        logger.info("00 CustFldEjecContCommon.action: INICIO");

    	super.action();
    	
    	CustEjecCont ejeccont = (CustEjecCont)getMboValue().getMbo();
    	ejeccont.voObtieneGLAccount();
    	
        logger.info("00 CustFldEjecContCommon.action: FINAL");
        logger.info("**********************************************************************");
        logger.info("");
    }
}
