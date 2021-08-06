package com.epm.app.workorder;

import java.rmi.RemoteException;

import psdi.app.workorder.FldWpSerItemNum;
import psdi.app.workorder.WO;
import psdi.app.workorder.WPService;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

import com.epm.app.common.CustLlenadoCuentas;
//com.epm.app.workorder.CustFldWpSerItemNum
//psdi.app.workorder.FldWpSerItemNum
public class CustFldWpSerItemNum extends FldWpSerItemNum//;MAXTableDomain
{
	private static MXLogger logger;

	public CustFldWpSerItemNum(MboValue mbv) throws MXException
	{
        super(mbv);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

	public void action() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldWpSerItemNum.action: INICIO");

    	super.action();
    	
    	WPService wpService = (WPService)getMboValue().getMbo();
        WO wo = (WO)wpService.getOwner();
        
        logger.debug("01A CustFldWpSerItemNum.action: if(!wpService.getMboValue(\"SLXCONTRACTNUM\").isNull())= " + (!wpService.getMboValue("SLXCONTRACTNUM").isNull()));
        if(!wpService.getMboValue("SLXCONTRACTNUM").isNull())
        {
        	MboSetRemote contractlineSet = wpService.getMboSet("$contractline", "CONTRACTLINE", "contractnum = '" + wpService.getString("SLXCONTRACTNUM") + "' and itemnum = '" + wpService.getString("ITEMNUM") + "'");
            logger.debug("01B CustFldWpSerItemNum.action: if(contractlineSet.isEmpty())= " + (contractlineSet.isEmpty()));
        	if(contractlineSet.isEmpty())
        		wpService.setValueNull("SLXCONTRACTNUM", 11L);
        }

    	boolean boMaterial = false;

        // Unidad de Negocio.
        logger.debug("01 CustFldWpSerItemNum.action: if(!wo.isNull(\"SLXCENTROACTIVIDAD\") || !wpService.isNull(\"SLXUNIDNEGOCIO\"))= " + (!wo.isNull("SLXCENTROACTIVIDAD") || !wpService.isNull("SLXUNIDNEGOCIO")));
    	if (!wo.isNull("SLXCENTROACTIVIDAD") || !wpService.isNull("SLXUNIDNEGOCIO"))
    	{
    		String stUnidadNegOT = "";

            logger.debug("02 CustFldWpSerItemNum.action: if(!wo.isNull(\"SLXCENTROACTIVIDAD\"))= " + (!wo.isNull("SLXCENTROACTIVIDAD")));
            if(!wo.isNull("SLXCENTROACTIVIDAD")) 
            	stUnidadNegOT = wo.getString("SLXCENTROACTIVIDAD");
    		String stUnidadNegWP = "";

            logger.debug("03 CustFldWpSerItemNum.action: if(!wo.isNull(\"SLXUNIDNEGOCIO\"))= " + (!wo.isNull("SLXUNIDNEGOCIO")));
    		if(!wpService.isNull("SLXUNIDNEGOCIO")) 
    			stUnidadNegWP = wpService.getString("SLXUNIDNEGOCIO");
    		String stUnidadNeg = stUnidadNegOT;

            logger.debug("04 CustFldWpSerItemNum.action: if(stUnidadNegOT != stUnidadNegWP)= " + (stUnidadNegOT != stUnidadNegWP));
            if (stUnidadNegOT != stUnidadNegWP) 
            	stUnidadNeg = stUnidadNegWP;

    		String strCuenta = "";
    		String strObjetoCuenta = "";
    		
    		// Servicios NO contratados.
            logger.debug("05 CustFldWpSerItemNum.action: if(wpService.getMboValue(\"SLXCONTRACTNUM\").isNull())= " + (wpService.getMboValue("SLXCONTRACTNUM").isNull()));
	        if (wpService.getMboValue("SLXCONTRACTNUM").isNull())
	        {
	        	strCuenta = "SLXGLCTAAUXSERV";
	    		strObjetoCuenta = "SLXGLCUENTAOBJETO"; // Cuenta Objeto Servicios NO Contratados.
	    	}
	        // Servicios contratados.
	        else
	        {
	        	strCuenta = "SLXGLCTAAUXSERVCONT";
	    		strObjetoCuenta = "SLXGLCUENTAOBJETOSERVCONT"; // Cuenta Objeto Servicios Contratados.
	        }

	        // Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
	        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
            logger.debug("06 CustFldWpSerItemNum.action: if(stUnidadNeg.length()!=0)= " + (stUnidadNeg.length()!=0));
	        if(stUnidadNeg.length()!=0)
	        	CustLlenadoCuentas.setCuentasPlan(wpService, stUnidadNeg, strCuenta, boMaterial, strObjetoCuenta);
    	}
        logger.debug("00 CustFldWpSerItemNum.action: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
}

