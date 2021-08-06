package com.epm.app.workorder;

import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.app.workorder.*;

import com.epm.app.common.CustLlenadoCuentas;

import java.rmi.RemoteException;

import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
//com.epm.app.workorder.CustFldWPSrvUnidadNegocio
public class CustFldWPSrvUnidadNegocio extends MboValueAdapter
{
	private static MXLogger logger;

	public CustFldWPSrvUnidadNegocio(MboValue mbv)
    {
        super(mbv);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

    public void action() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldWPSrvUnidadNegocio.action: INICIO");

    	super.action();
    	
    	WPService wpService = (WPService)getMboValue().getMbo();

    	boolean boMaterial = false;

    	// Unidade de Negocio.
    	String stUnidadNeg = getMboValue().getString();

		String strCuenta = "";
		String strObjetoCuenta = "";

		// Servicios NO contratados.
        logger.debug("01 CustFldWPSrvUnidadNegocio.action: if(wpService.getMboValue(\"SLXCONTRACTNUM\").isNull())= " + (wpService.getMboValue("SLXCONTRACTNUM").isNull()));
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
        logger.debug("02 CustFldWPSrvUnidadNegocio.action: if(stUnidadNeg.length()!=0)= " + (stUnidadNeg.length()!=0));
        if(stUnidadNeg.length()!=0)
        	CustLlenadoCuentas.setCuentasPlan(wpService, stUnidadNeg, strCuenta, boMaterial, strObjetoCuenta);

        logger.debug("00 CustFldWPSrvUnidadNegocio.action: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
}