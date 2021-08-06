package com.epm.app.workorder;

import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.app.workorder.*;

import com.epm.app.common.CustLlenadoCuentas;

import java.rmi.RemoteException;

import psdi.util.MXException;

public class CustFldWPMatSlxContractNum extends MboValueAdapter
{
    public CustFldWPMatSlxContractNum(MboValue mbv)
    {
        super(mbv);
    }

    public void action() throws MXException, RemoteException
    {
    	super.action();
    	
        WPMaterial wpMaterial = (WPMaterial)getMboValue().getMbo();
        WO wo = (WO)wpMaterial.getOwner();

    	boolean boMaterial = true;

        // Unidade de Negocio.
    	if (!wo.isNull("SLXCENTROACTIVIDAD") || !wpMaterial.isNull("SLXUNIDNEGOCIO"))
    	{
    		String stUnidadNegOT = "";
    		if(!wo.isNull("SLXCENTROACTIVIDAD")) stUnidadNegOT = wo.getString("SLXCENTROACTIVIDAD");
    		String stUnidadNegWP = "";
    		if(!wpMaterial.isNull("SLXUNIDNEGOCIO")) stUnidadNegWP = wpMaterial.getString("SLXUNIDNEGOCIO");
    		String stUnidadNeg = stUnidadNegOT;
    		if (stUnidadNegOT != stUnidadNegWP) stUnidadNeg = stUnidadNegWP;

    		String strCuenta = "";
    		String strObjetoCuenta = "";

    		// Materiales NO contratados.
	        //if (getMboValue().isNull())
		    if (wpMaterial.getInt("DIRECTREQ") == 0)
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
	        if(stUnidadNeg.length()!=0)
	        	CustLlenadoCuentas.setCuentasPlan(wpMaterial, stUnidadNeg, strCuenta, boMaterial, strObjetoCuenta);
    	}
    }
}

