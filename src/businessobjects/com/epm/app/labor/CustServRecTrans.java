package com.epm.app.labor;

import com.epm.app.common.CustLlenadoCuentas;

import java.rmi.RemoteException;

import psdi.app.labor.ServRecTrans;
import psdi.app.labor.ServRecTransRemote;
import psdi.app.workorder.WORemote;
import psdi.mbo.*;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class CustServRecTrans extends ServRecTrans implements ServRecTransRemote
{
	private static MXLogger logger;

	public CustServRecTrans(MboSet ms) throws MXException, RemoteException
    {
        super(ms);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }
    
    //public void add() throws MXException, RemoteException
    public void save() throws MXException, RemoteException
    {
        logger.info("");
        logger.info("**********************************************************************");
        logger.info("00 CustServRecTrans.save: INICIO");

    	super.save();//add();

    	// No se deben procesar registro de Servicios que provienen de una integración.
    	//if (!isNull("SENDERSYSID") && getString("SENDERSYSID").equals("JDE")) return;
       	logger.debug("01 CustServRecTrans.save: if(!isNull(\"SENDERSYSID\") && (getString(\"SENDERSYSID\").contains(\"JDE\") || getString(\"SENDERSYSID\").equals(\"ERP\")))= " + (!isNull("SENDERSYSID") && (getString("SENDERSYSID").contains("JDE") || getString("SENDERSYSID").equals("ERP"))));
    	if (!isNull("SENDERSYSID") && (getString("SENDERSYSID").contains("JDE") || getString("SENDERSYSID").equals("ERP")))    
    		return;
    	
    	boolean boContrato    = false;
       	logger.debug("02 CustServRecTrans.save: if(!isNull(\"SLXCONTRACTNUM\"))= " + (!isNull("SLXCONTRACTNUM")));
       	if (!isNull("SLXCONTRACTNUM")) 
       		boContrato = true;

       	logger.debug("03 CustServRecTrans.save: if(!boContrato)= " + (!boContrato));
    	if(!boContrato) 
    		return;
    	
       	logger.debug("04 CustServRecTrans.save: if(!isNull(\"ponum\"))= " + (!isNull("ponum")));
    	if(!isNull("ponum"))
    	{
    		MboSetRemote poSet = getMboSet("POREV");
           	logger.debug("05 CustServRecTrans.save: if(!poSet.isEmpty())= " + (!poSet.isEmpty()));
    		if(!poSet.isEmpty())
    		{
        		MboRemote po = poSet.getMbo(0);
        		MboSetRemote purchviewSet = po.getMboSet("PURCHVIEW");
               	logger.debug("06 CustServRecTrans.save: if(!purchviewSet.isEmpty())= " + (!purchviewSet.isEmpty()));
        		if(!purchviewSet.isEmpty())
        			return;
    		}
    	}

    	MboRemote refWo = getWOot();
       	logger.debug("07 CustServRecTrans.save: if(refWo == null)= " + (refWo == null));
        if(refWo == null) 
        	return;
/*
        // Unidad de Negocio.
    	if (!refWo.isNull("SLXCENTROACTIVIDAD"))
    	{
    		String stUnidadNeg = refWo.getString("SLXCENTROACTIVIDAD");
    		String strCuenta = "";
            String strObjetoCuenta = "";
    		// Servicios NO contratados.
	        //if (isNull("CONTRACTNUM"))
	        //	strCuenta = "SLXGLCTAAUXSERV";
	        // Servicios contratados.
	        //else
	        //	strCuenta = "SLXGLCTAAUXSERVCONT";
    		strCuenta = "SLXGLCTAAUXSERV";
    		strObjetoCuenta = "SLXGLCUENTAOBJETOSNC";

	        // Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
	        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
	        CustLlenadoCuentas.setCuentasReal(this, stUnidadNeg, strCuenta, strObjetoCuenta);
    	}
    }
*/
        boolean boEsPlanificado = false;
        
        //boEsPlanificado = boEsServicioPlanificado(boContrato);
        boEsPlanificado = boEsServicioPlanificado();
        
        // Si no es un material planificado, se debe usar el algoritmo de obtencion de mapeo de cuentas.
    	logger.debug("12 CustServRecTrans.save: if(!boEsPlanificado)= " + (!boEsPlanificado));
        if (!boEsPlanificado)
        {
            // Unidad de Negocio.
        	logger.debug("12 CustServRecTrans.save: if(!refWo.isNull(\"SLXCENTROACTIVIDAD\"))= " + (!refWo.isNull("SLXCENTROACTIVIDAD")));
        	if (!refWo.isNull("SLXCENTROACTIVIDAD"))
        	{
        		String stUnidadNeg = refWo.getString("SLXCENTROACTIVIDAD");
        		String stCuenta = "";
                String stObjetoCuenta = "";
        		// Servicios NO contratados.
    	        //if (isNull("CONTRACTNUM"))
    	        //	strCuenta = "SLXGLCTAAUXSERV";
    	        // Servicios contratados.
    	        //else
    	        //	strCuenta = "SLXGLCTAAUXSERVCONT";
        		stCuenta = "SLXGLCTAAUXSERV";
            	logger.debug("13 CustServRecTrans.save: if(boContrato)= " + (boContrato));
	    		if(boContrato)
	    			stObjetoCuenta = "SLXGLCUENTAOBJETOSERVCONT";// Cuenta Objeto Servicio Contrato
	    		else
	    			//stObjetoCuenta = "SLXGLCUENTAOBJETOSERV";// Cuenta Objeto Servicio No Contrato
	    			stObjetoCuenta = "SLXGLCUENTAOBJETO";// Cuenta Objeto Servicio No Contrato

    	        // Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
    	        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
		        //CustLlenadoCuentas.setCuentasReal(this, stUnidadNeg, strCuenta, strObjetoCuenta);
    	        CustLlenadoCuentas.setCuentasReal((MboRemote)this, stUnidadNeg, stCuenta, stObjetoCuenta, false, refWo);
	    	}
        }
        logger.info("00 CustServRecTrans.save: FINAL");
        logger.info("**********************************************************************");
        logger.info("");
    }
    
//    public boolean boEsServicioPlanificado(boolean boContrato) throws MXException, RemoteException
    public boolean boEsServicioPlanificado() throws MXException, RemoteException
    {
    	boolean boExiste = false;

    	//String stCuentaObjeto = "SLXGLCUENTAOBJETO";
    	//if(boContrato)
		//	stCuentaObjeto = "SLXGLCUENTAOBJETOSERVCONT";

    	String strItemnum = getString("ITEMNUM");
    	String strSiteid = getString("SITEID");
        MboSetRemote myWPMaterialSetRemote = getMboSet("$WPSERVICE"+strItemnum+strSiteid, "WPSERVICE", "ITEMNUM=:itemnum and SITEID=:siteid");
        if(!myWPMaterialSetRemote.isEmpty())
        {        	
        	boExiste = true;
        	MboRemote wpMat = myWPMaterialSetRemote.getMbo(0);
        	/*
        	String strGL =
        	wpMat.getString("SLXUNIDNEGOCIO") + 
        	//wpMat.getString(stCuentaObjeto) + 
        	wpMat.getString("SLXCOBJETO") + 
        	wpMat.getString("SLXCAUX");
        	*/
        	String strGL = wpMat.getString("SLXUNIDNEGOCIO") + "." + wpMat.getString("SLXCOBJETO") + "." + wpMat.getString("SLXCAUX");
        	setValue("gldebitacct", strGL, 11L);
        }    	

    	return boExiste;
    }
        
    public MboRemote getWOot() throws MXException, RemoteException
    {
    	if (isNull("refwo")) return null;
    	
    	MboRemote woMbo = null;
        String wonum = getString("refwo");
        SqlFormat sqf = new SqlFormat(this, "wonum=:1 and siteid=:siteid");
        sqf.setObject(1, "WORKORDER", "wonum", wonum);
        woMbo = (WORemote)((MboSet)getThisMboSet()).getSharedMboSet("WORKORDER", sqf.format()).getMbo(0);
        return woMbo;
    }
}
