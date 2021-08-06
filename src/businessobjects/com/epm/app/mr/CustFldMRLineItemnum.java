package com.epm.app.mr;

import java.rmi.RemoteException;
import psdi.app.mr.FldMRLineItemnum;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class CustFldMRLineItemnum extends FldMRLineItemnum//;MAXTableDomain
{
	private static MXLogger logger;

	public CustFldMRLineItemnum(MboValue mbv) throws MXException
	{
        super(mbv);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

	public void action() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldMRLineItemnum.action: INICIO");

    	super.action();
    	
    	CustMRLine mrline = (CustMRLine)getMboValue().getMbo();
       
        logger.debug("01 CustFldMRLineItemnum.action: if(!mrline.getMboValue(\"SLXCONTRACTNUM\").isNull())= " + (!mrline.getMboValue("SLXCONTRACTNUM").isNull()));
        if(!mrline.getMboValue("SLXCONTRACTNUM").isNull())
        {
        	MboSetRemote contractlineSet = mrline.getMboSet("$contractline", "CONTRACTLINE", "contractnum = '" + mrline.getString("SLXCONTRACTNUM") + "' and itemnum = '" + mrline.getString("ITEMNUM") + "'");
            logger.debug("02 CustFldMRLineItemnum.action: if(contractlineSet.isEmpty())= " + (contractlineSet.isEmpty()));
        	if(contractlineSet.isEmpty())
        		mrline.setValueNull("SLXCONTRACTNUM", 11L);
        }
        mrline.voManejoCuentas();

        logger.debug("00 CustFldMRLineItemnum.action: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
}

