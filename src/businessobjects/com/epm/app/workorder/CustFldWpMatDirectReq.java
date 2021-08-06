package com.epm.app.workorder;

import java.rmi.RemoteException;

import psdi.app.workorder.FldWpMatDirectReq;
import psdi.app.workorder.WO;
import psdi.app.workorder.WPMaterial;
import psdi.mbo.MboValue;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

import com.epm.app.common.CustLlenadoCuentas;
//psdi.app.workorder.FldWpMatDirectReq
//com.epm.app.workorder.CustFldWpMatDirectReq

public class CustFldWpMatDirectReq extends FldWpMatDirectReq
{
	private static MXLogger logger;

    public CustFldWpMatDirectReq(MboValue mbv)
    {
        super(mbv);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

    public void action() throws MXException, RemoteException
    {
        logger.info("");
        logger.info("**********************************************************************");
        logger.info("00 CustFldWpMatDirectReq.action: INICIO");

        super.action();
    	
        WPMaterial wpMaterial = (WPMaterial)getMboValue().getMbo();
        WO wo = (WO)wpMaterial.getOwner();

    	boolean boMaterial = true;

        // Unidade de Negocio.
        logger.info("01 CustFldWpMatDirectReq.action: if(!wo.isNull(\"SLXCENTROACTIVIDAD\") || !wpMaterial.isNull(\"SLXUNIDNEGOCIO\"))= " + (!wo.isNull("SLXCENTROACTIVIDAD") || !wpMaterial.isNull("SLXUNIDNEGOCIO")));
    	if (!wo.isNull("SLXCENTROACTIVIDAD") || !wpMaterial.isNull("SLXUNIDNEGOCIO"))
    	{
    		String stUnidadNegOT = "";
    		if(!wo.isNull("SLXCENTROACTIVIDAD")) stUnidadNegOT = wo.getString("SLXCENTROACTIVIDAD");
    		String stUnidadNegWP = "";

            logger.info("02 CustFldWpMatDirectReq.action: if(!wpMaterial.isNull(\"SLXUNIDNEGOCIO\"))= " + (!wpMaterial.isNull("SLXUNIDNEGOCIO")));
    		if(!wpMaterial.isNull("SLXUNIDNEGOCIO")) 
    			stUnidadNegWP = wpMaterial.getString("SLXUNIDNEGOCIO");
    		String stUnidadNeg = stUnidadNegOT;

            logger.info("03 CustFldWpMatDirectReq.action: if(stUnidadNegOT != stUnidadNegWP)= " + (stUnidadNegOT != stUnidadNegWP));
    		if (stUnidadNegOT != stUnidadNegWP) 
    			stUnidadNeg = stUnidadNegWP;

    		String strCuenta = "";
    		String strObjetoCuenta = "";

    		// Materiales NO contratados.
            logger.info("04 CustFldWpMatDirectReq.action: if(getMboValue().getInt() == 0)= " + (getMboValue().getInt() == 0));
	        if (getMboValue().getInt() == 0)
	        {
	        	strCuenta = "SLXGLCTAAUXMATER";
	        	strObjetoCuenta = "SLXGLCUENTAOBJETOMAT"; // Cuenta Objeto Materiales
	        }
	        // Materiales contratados.
	        else
	        {
	        	strCuenta = "SLXGLCTAAUXMATERCONT";
	        	strObjetoCuenta = "SLXGLCUENTAOBJETOMATCONT"; // Cuenta Objeto Materiales Contratados
	        }
	        
	        // Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
	        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
            logger.info("05 CustFldWpMatDirectReq.action: if(stUnidadNeg.length()!=0)= " + (stUnidadNeg.length()!=0));
	        if(stUnidadNeg.length()!=0)
	        	CustLlenadoCuentas.setCuentasPlan(wpMaterial, stUnidadNeg, strCuenta, boMaterial, strObjetoCuenta);
    	}

    	logger.info("00 CustFldWpMatDirectReq.action: FINAL");
        logger.info("**********************************************************************");
        logger.info("");
    }
}

