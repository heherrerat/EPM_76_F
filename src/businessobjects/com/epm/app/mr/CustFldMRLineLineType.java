package com.epm.app.mr;

import java.rmi.RemoteException;
import psdi.app.mr.FldMRLineLineType;
import psdi.mbo.*;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class CustFldMRLineLineType extends FldMRLineLineType
{
	private static MXLogger logger;

	public CustFldMRLineLineType(MboValue mbv) throws MXException, RemoteException
    {
        super(mbv);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

    public void action() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldMRLineLineType.action: INICIO");

        super.action();
    	
        CustMRLine mrline = (CustMRLine)getMboValue().getMbo();
        logger.debug("01 CustFldMRLineLineType.action: if(!mrline.toBeAdded())= " + (!mrline.toBeAdded()));
        if(!mrline.toBeAdded())
            return;
        String previousLineType = getTranslator().toInternalString("LINETYPE", getMboValue().getPreviousValue().asString());
        String internalLineType = mrline.getInternalLineType();
        
        logger.debug("02 CustFldMRLineLineType.action: if(internalLineType.equalsIgnoreCase(previousLineType))= " + (internalLineType.equalsIgnoreCase(previousLineType)));
        if(internalLineType.equalsIgnoreCase(previousLineType))
            return;

        mrline.voManejoCuentas();
        
        logger.debug("00 CustFldMRLineLineType.action: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
}