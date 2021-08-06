package com.epm.app.inventory;

import com.epm.app.common.CustLlenadoCuentas;

import java.rmi.RemoteException;
import psdi.app.inventory.MatUseTrans;
import psdi.app.inventory.MatUseTransRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
//com.epm.app.inventory.CustMatUseTransSet
public class CustMatUseTrans extends MatUseTrans implements MatUseTransRemote
{
	boolean boCargoDirecto = false;
	private static MXLogger logger;
	
    public CustMatUseTrans(MboSet ms) throws MXException, RemoteException
    {
        super(ms);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

    //public void add() throws MXException, RemoteException
    public void save() throws MXException, RemoteException
    {
        logger.info("");
        logger.info("**********************************************************************");
        logger.info("00 CustMatUseTrans.save: INICIO");

        super.save();//add();

    	// Verifica si el registro viene de una integración desde JDE.
    	boolean boIntegracion = false;
    	//if (!isNull("SENDERSYSID") && getString("SENDERSYSID").equals("JDE"))    boIntegracion = true;   	
        logger.info("01 CustMatUseTrans.save: if(!isNull(\"SENDERSYSID\") && (getString(\"SENDERSYSID\").contains(\"JDE\") || getString(\"SENDERSYSID\").contains(\"ERP\")))= " + (!isNull("SENDERSYSID") && (getString("SENDERSYSID").contains("JDE") || getString("SENDERSYSID").contains("ERP"))));
    	if (!isNull("SENDERSYSID") && (getString("SENDERSYSID").contains("JDE") || getString("SENDERSYSID").contains("ERP")))    
    		boIntegracion = true;
    	
    	boolean boContrato    = false;
        logger.info("02 CustMatUseTrans.save: if(!isNull(\"SLXCONTRACTNUM\"))= " + (!isNull("SLXCONTRACTNUM")));
    	if (!isNull("SLXCONTRACTNUM")) 
			boContrato    = true;
    	
    	//boolean boCargoDirecto    = getBoolean("directreq");

        // No contratados y vienen por integración, NO se deben considerar.
        logger.info("03 CustMatUseTrans.save: if(!boContrato && boIntegracion )= " + (!boContrato && boIntegracion ));
        if (!boContrato && boIntegracion ) 
			return;

        // Contratados y no cargo directo, NO se deben considerar.
        //if (boContrato && !boCargoDirecto) return;

        // Contratados y cargo directo, NO se deben considerar.
        //if (boContrato && boCargoDirecto) return;

    	// Obtiene la información de la Orden de trabajo asociada.
    	MboRemote refWo = getWO();
        logger.info("04 CustMatUseTrans.save: if(refWo == null)= " + (refWo == null));
        if(refWo == null) 
			return;
  
		boolean boEsPlanificado = false;
        
        logger.info("09 CustMatUseTrans.save: if(!boContrato && !boIntegracion)= " + (!boContrato && !boIntegracion));
        if(!boContrato && !boIntegracion)
        {
            //boEsPlanificado = boEsMaterialPlanificado(boContrato);
            boEsPlanificado = boEsMaterialPlanificado();
            
            // Si no es un material planificado, se debe usar el algoritmo de obtencion de mapeo de cuentas.
            logger.info("10' CustMatUseTrans.save: if(!boEsPlanificado)= " + (!boEsPlanificado));
            if (!boEsPlanificado)
            {
    	        // Unidade de Negocio.
                logger.info("11 CustMatUseTrans.save: if(!refWo.isNull(\"SLXCENTROACTIVIDAD\"))= " + (!refWo.isNull("SLXCENTROACTIVIDAD")));
    	    	if (!refWo.isNull("SLXCENTROACTIVIDAD"))
    	    	{
    	    		String stUnidadNeg = refWo.getString("SLXCENTROACTIVIDAD");
    	    		String stCuenta = "";
    	    		String stObjetoCuenta = "";
    	    		
    	    		// Materiales NO contratados.
//    		        if (!boMatContratados)
//    		        	stCuenta = "SLXGLCTAAUXSERV";
    		        // Materiales contratados.
//    		        else
//    		        	stCuenta = "SLXGLCTAAUXSERVCONT";
    		        
    	    		stCuenta = "SLXGLCTAAUXMATER";
                    logger.info("12 CustMatUseTrans.save: if(boContrato)= " + (boContrato));
    	    		if(boContrato)
    	    			stObjetoCuenta = "SLXGLCUENTAOBJETOMATCONT"; // Cuenta Objeto Materiales Contrato
    	    		else
    	    			stObjetoCuenta = "SLXGLCUENTAOBJETOMAT"; // Cuenta Objeto Materiales No Contrato

    		        // Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
    		        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
    		        //CustLlenadoCuentas.setCuentasReal(this, stUnidadNeg, stCuenta, stObjetoCuenta);
        	        CustLlenadoCuentas.setCuentasReal((MboRemote)this, stUnidadNeg, stCuenta, stObjetoCuenta, false, refWo);
    	    	}
            }
        }
        logger.info("00 CustMatUseTrans.save: FINAL");
        logger.info("**********************************************************************");
        logger.info("");
    }
    
//    public boolean boEsMaterialPlanificado(boolean boContrato) throws MXException, RemoteException
    public boolean boEsMaterialPlanificado() throws MXException, RemoteException
    {
        logger.info("");
        logger.info("**********************************************************************");
        logger.info("00 CustEjecCont.boEsMaterialPlanificado: INICIO");
    	boolean boExiste = false;
    	
    	//String stCuentaObjeto = "SLXGLCUENTAOBJETOMAT";
    	//if(boContrato)
    	//	stCuentaObjeto = "SLXGLCUENTAOBJETOMATCONT";

    	String strItemnum = getString("ITEMNUM");
    	String strSiteid = getString("SITEID");
        MboSetRemote myWPMaterialSetRemote = getMboSet("$WPMATERIAL"+strItemnum+strSiteid, "WPMATERIAL", "ITEMNUM=:itemnum and SITEID=:siteid");
        logger.info("01 CustEjecCont.boEsMaterialPlanificado: if(!myWPMaterialSetRemote.isEmpty())= " + (!myWPMaterialSetRemote.isEmpty()));
        if(!myWPMaterialSetRemote.isEmpty())
        {        	
        	boExiste = true;
        	MboRemote wpMat = myWPMaterialSetRemote.getMbo(0);
/*
        	String strGL =
        	wpMat.getString("SLXUNIDNEGOCIO") + 
        	//wpMat.getString(stCuentaObjeto) + 
        	wpMat.getString("SLXGLCUENTAOBJETO") + 
        	wpMat.getString("SLXCAUX");
*/
        	String strGL = wpMat.getString("SLXUNIDNEGOCIO") + "." + wpMat.getString("SLXGLCUENTAOBJETO") + "." + wpMat.getString("SLXCAUX");
        	setValue("gldebitacct", strGL, 11L);
        	boCargoDirecto = wpMat.getBoolean("directreq");
        }    	

        logger.info("00 CustEjecCont.boEsMaterialPlanificado: FINAL");
        logger.info("**********************************************************************");
        logger.info("");
    	return boExiste;
    }
}
