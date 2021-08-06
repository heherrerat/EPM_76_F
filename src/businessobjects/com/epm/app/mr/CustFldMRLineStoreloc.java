package com.epm.app.mr;

import java.rmi.RemoteException;

import com.epm.app.common.CustLlenadoCuentas;

import psdi.app.mr.FldMRLineStoreloc;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/*
update maxattribute    set classname = 'com.epm.app.mr.CustFldMRLineStoreloc' 
where objectname = 'MRLINE' and attributename = 'STORELOC';
update maxattributecfg set classname = 'com.epm.app.mr.CustFldMRLineStoreloc' 
where objectname = 'MRLINE' and attributename = 'STORELOC';
commit;

update maxattribute    set classname = 'psdi.app.mr.FldMRLineStoreloc' 
where objectname = 'MRLINE' and attributename = 'STORELOC';
update maxattributecfg set classname = 'psdi.app.mr.FldMRLineStoreloc' 
where objectname = 'MRLINE' and attributename = 'STORELOC';
commit;
*/

public class CustFldMRLineStoreloc extends FldMRLineStoreloc
{
	private static MXLogger logger;
    public CustFldMRLineStoreloc(MboValue mbv) throws MXException
    {
        super(mbv);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

    public void action() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldMRLineStoreloc.action: INICIO");

        super.action();
        CustMRLine mrline = (CustMRLine)getMboValue().getMbo();

        logger.info("01 CustFldMRLineStoreloc.action: if(!getMboValue().isNull())= " + (!getMboValue().isNull()));
        if(!getMboValue().isNull())
        {
        	MboSetRemote locationsSet = getMboValue().getMbo().getMboSet("$locations","locations","location = '" + getMboValue().getString() + "'");
    		MboRemote locations = null;

    		logger.info("02 CustFldMRLineStoreloc.action: if(!locationsSet.isEmpty())= " + (!locationsSet.isEmpty()));
        	if(!locationsSet.isEmpty())
        	{
        		locations = locationsSet.getMbo(0);
        		logger.info("02.01 CustFldMRLineStoreloc.action: locations.siteid= " + locations.getString("siteid"));
        		mrline.setValue("storelocsite", locations.getString("siteid"));
        		getMboValue("storelocsite").setValue(locations.getString("siteid"));
        	}
        	else
        		mrline.setValueNull("storelocsite");
        		
        	locationsSet.close();

            String stUnidadNeg = getMboValue("SLXUNIDNEGOCIO").toString();
            logger.info("03 CustFldMRLineStoreloc.action: if(stUnidadNeg.length()!=0 && !mrline.isNull(\"ITEMNUM\"))= " + (stUnidadNeg.length()!=0 && !mrline.isNull("ITEMNUM")));
            if(stUnidadNeg.length()!=0 && !mrline.isNull("ITEMNUM"))
            {
    			MboSetRemote setCtas = mrline.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidadNeg + "' and slxalmacen = '" + getMboValue().getString() + "'");
    	        logger.info("04 CustFldMRLineStoreloc.action: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
    			if(setCtas.isEmpty())
    				throw new MXApplicationException("slxplusdwotrk", "ErrorUNAlm");
    			mrline.voManejoCuentas();
            }
        }
        	
/*
        mrline.voManejoCuentas();
        
        String stUnidadNeg = getMboValue("SLXUNIDNEGOCIO").toString();
        logger.info("01 CustFldMRLineStoreloc.action: if(stUnidadNeg.length()!=0)= " + (stUnidadNeg.length()!=0));
        if(stUnidadNeg.length()!=0)
        {
			MboSetRemote setCtas = mrline.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidadNeg + "' and slxalmacen = '" + getMboValue().getString() + "'");
	        logger.info("02 CustFldMRLineStoreloc.action: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
			if(setCtas.isEmpty())
				throw new MXApplicationException("slxplusdwotrk", "ErrorUNAlm");
			mrline.voManejoCuentas();
        }
*/
    	logger.debug("00 CustFldMRLineStoreloc.action: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
}