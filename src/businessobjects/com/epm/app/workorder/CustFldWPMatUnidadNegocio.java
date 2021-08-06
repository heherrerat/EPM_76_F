package com.epm.app.workorder;

import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.app.workorder.*;

import com.epm.app.common.CustLlenadoCuentas;

import java.rmi.RemoteException;

import psdi.util.MXException;

public class CustFldWPMatUnidadNegocio extends MboValueAdapter
{
    public CustFldWPMatUnidadNegocio(MboValue mbv)
    {
        super(mbv);
    }

    public void action() throws MXException, RemoteException
    {
    	super.action();
    	
        WPMaterial wpMaterial = (WPMaterial)getMboValue().getMbo();

    	boolean boMaterial = true;

    	// Unidade de Negocio.
    	String stUnidadNeg = getMboValue().getString();

		String strCuenta = "";
		String strObjetoCuenta = "";

		// Materiales NO contratados.
        //if(wpMaterial.getMboValue("SLXCONTRACTNUM").isNull())
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