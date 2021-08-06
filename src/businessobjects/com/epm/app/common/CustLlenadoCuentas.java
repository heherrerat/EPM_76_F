package com.epm.app.common;

import java.io.Serializable;
import java.rmi.RemoteException;
import psdi.app.workorder.WORemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class CustLlenadoCuentas implements Serializable
{
	private static final long serialVersionUID = 4187151099336056462L;
	private static MXLogger logger = MXLoggerFactory.getLogger("maximo.application");
    
	public CustLlenadoCuentas()
    {
    }
	
    public static void setCuentasPlan(MboRemote mboPlan, String stUnidnegocio, String stCuenta, boolean boMaterial, String stCuentaObjeto) throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustLlenadoCuentas.setCuentasPlan: INICIO stUnidnegocio= " + stUnidnegocio + ", stCuenta= " + stCuenta + ", stCuentaObjeto= " + stCuentaObjeto);

        String stAlmacen = "LOCATION";
    	//-------------------------------------------------------------------------
    	//HHT 20210721: A solicitud de Carlos Gómez
    	//---------------------- INICIO -------------------------------------------
        String stWhere   = "";
        String stWhere2  = "";
        MboRemote    wo  = getWo(mboPlan);
       	logger.debug("01 CustLlenadoCuentas.setCuentasPlan: if(wo != null)= " + (wo != null));
        if(wo != null)
        {
        	/*
           	logger.debug("02 CustLlenadoCuentas.setCuentasPlan: if(!wo.isNull(\"SLXOBJCOSTEO\"))= " + (!wo.isNull("SLXOBJCOSTEO")));
    		if(!wo.isNull("SLXOBJCOSTEO"))
    			stWhere = stWhere + " and SLXOBJCOSTEO = '" + wo.getString("SLXOBJCOSTEO") + "'";
        	logger.debug("03 CustLlenadoCuentas.setCuentasPlan: if(!wo.isNull(\"SLXTIPOACCION\"))= " + (!wo.isNull("SLXTIPOACCION")));
    		if(!wo.isNull("SLXTIPOACCION"))
    			stWhere = stWhere + " and SLXTIPOACCION = '" + wo.getString("SLXTIPOACCION") + "'";
    		*/
        	int inFiltro = 0;
           	logger.debug("02 CustLlenadoCuentas.setCuentasPlan: if(!wo.isNull(\"SLXOBJCOSTEO\"))= " + (!wo.isNull("SLXOBJCOSTEO")));
        	if(!wo.isNull("SLXOBJCOSTEO"))
        	{
        		inFiltro = 1;
            	logger.debug("03 CustLlenadoCuentas.setCuentasPlan: if(!wo.isNull(\"SLXTIPOACCION\"))= " + (!wo.isNull("SLXTIPOACCION")));
        		if(!wo.isNull("SLXTIPOACCION"))
             		inFiltro = 3;
        	}
        	else
        	{
            	logger.debug("04 CustLlenadoCuentas.setCuentasPlan: if(!wo.isNull(\"SLXTIPOACCION\"))= " + (!wo.isNull("SLXTIPOACCION")));
        		if(!wo.isNull("SLXTIPOACCION"))
             		inFiltro = 2;
         	}
        	logger.debug("05 CustLlenadoCuentas.setCuentasPlan: inFiltro= " + inFiltro);
        	if(inFiltro == 0)
        		stWhere = " and SLXOBJCOSTEO is null  and SLXTIPOACCION is null ";
        	else if (inFiltro == 1)
        		stWhere = " and SLXOBJCOSTEO = '" + wo.getString("SLXOBJCOSTEO") + "' and SLXTIPOACCION is null ";
        	else if (inFiltro == 2)
        		stWhere = " and SLXOBJCOSTEO is null and SLXTIPOACCION = '" + wo.getString("SLXTIPOACCION") + "' ";
        	else if (inFiltro == 3)
        	{
           		stWhere  = " and (SLXOBJCOSTEO = '" + wo.getString("SLXOBJCOSTEO") + "' and SLXTIPOACCION = '" + wo.getString("SLXTIPOACCION") + "')";
           		stWhere2 = " and (SLXOBJCOSTEO = '" + wo.getString("SLXOBJCOSTEO") + "' or SLXTIPOACCION = '" + wo.getString("SLXTIPOACCION") + "')";
        	}
        }
     	//---------------------- FINAL --------------------------------------------
    	logger.debug("06 CustLlenadoCuentas.setCuentasPlan: if(mboPlan.getName().equalsIgnoreCase(\"MRLINE\"))= " + (mboPlan.getName().equalsIgnoreCase("MRLINE")));
		if(mboPlan.getName().equalsIgnoreCase("MRLINE"))
			stAlmacen = "STORELOC";
		else
		{
			stAlmacen = "LOCATION";
		}
    	logger.debug("06.01 CustLlenadoCuentas.setCuentasPlan: Almacen / Planta Almacen / Planta OT 1 / Planta OT 2= " + mboPlan.getString(stAlmacen) + " / " + mboPlan.getString("STORELOCSITE") + " / " + mboPlan.getString("SITEID") + " / " + mboPlan.getOwner().getString("SITEID"));
		 		
        MboRemote mboCta = null;
    	MboSetRemote setCtas = null;
        
    	logger.debug("07 CustLlenadoCuentas.setCuentasPlan: if(mboPlan.isNull(stAlmacen))= " + (mboPlan.isNull(stAlmacen)));
    	if(mboPlan.isNull(stAlmacen))
    	{
        	logger.debug("07 CustLlenadoCuentas.setCuentasPlan: if(mboPlan.isNull(\"STORELOCSITE\"))= " + (mboPlan.isNull("STORELOCSITE")));
    		if(mboPlan.isNull("STORELOCSITE"))
    		{
		    	//-------------------------------------------------------------------------
		    	//HHT 20210721: A solicitud de Carlos Gómez
		    	//---------------------- INICIO -------------------------------------------
            	logger.debug("08 CustLlenadoCuentas.setCuentasPlan: if(mboPlan.isNull(\"SITEID\"))= " + (mboPlan.isNull("SITEID")));
    			if(mboPlan.isNull("SITEID"))
    				//setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getOwner().getString("SITEID") + "'");
    			{
    				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getOwner().getString("SITEID") + "'" + stWhere);
                	logger.debug("09 CustLlenadoCuentas.setCuentasPlan: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
    	            if(setCtas.isEmpty())
        				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getOwner().getString("SITEID") + "'" + stWhere2);   	            	
                	logger.debug("10 CustLlenadoCuentas.setCuentasPlan: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
    	            if(setCtas.isEmpty())
        				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getOwner().getString("SITEID") + "'");   	            	
    	            setCtas.setOrderBy("SLXUNIDNEGOCIO, " + stCuentaObjeto + ", " + stCuenta);
    			}
    			else
    				//setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getString("SITEID") + "'");
    			{
    				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getString("SITEID") + "'" + stWhere);
                	logger.debug("11 CustLlenadoCuentas.setCuentasPlan: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
    	            if(setCtas.isEmpty())
        				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getString("SITEID") + "'" + stWhere2);
                	logger.debug("12 CustLlenadoCuentas.setCuentasPlan: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
    	            if(setCtas.isEmpty())
        				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getString("SITEID") + "'");   	            	
    	            setCtas.setOrderBy("SLXUNIDNEGOCIO, " + stCuentaObjeto + ", " + stCuenta);
    			}
    		}
    		else
    		{
            	logger.debug("13 CustLlenadoCuentas.setCuentasPlan: if(mboPlan.getName().equalsIgnoreCase(\"MRLINE\") && mboPlan.getBoolean(\"DIRECTREQ\"))= " + (mboPlan.getName().equalsIgnoreCase("MRLINE") && mboPlan.getBoolean("DIRECTREQ")));
    			if(mboPlan.getName().equalsIgnoreCase("MRLINE") && mboPlan.getBoolean("DIRECTREQ"))
    			{
                	logger.debug("14 CustLlenadoCuentas.setCuentasPlan: if(mboPlan.isNull(\"SITEID\"))= " + (mboPlan.isNull("SITEID")));
        			if(mboPlan.isNull("SITEID"))
        				//setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getOwner().getString("SITEID") + "'");
        			{
        				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getOwner().getString("SITEID") + "'" + stWhere);
                    	logger.debug("15 CustLlenadoCuentas.setCuentasPlan: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
        	            if(setCtas.isEmpty())
            				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getOwner().getString("SITEID") + "'" + stWhere2);
                    	logger.debug("16 CustLlenadoCuentas.setCuentasPlan: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
        	            if(setCtas.isEmpty())
            				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getOwner().getString("SITEID") + "'");
    		            setCtas.setOrderBy("SLXUNIDNEGOCIO, " + stCuentaObjeto + ", " + stCuenta);
        			}
        			else
        				//setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getString("SITEID") + "'");
        			{
        				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getString("SITEID") + "'" + stWhere);
                    	logger.debug("17 CustLlenadoCuentas.setCuentasPlan: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
        	            if(setCtas.isEmpty())
            				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getString("SITEID") + "'" + stWhere2);
                    	logger.debug("18 CustLlenadoCuentas.setCuentasPlan: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
        	            if(setCtas.isEmpty())
        	            	setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getString("SITEID") + "'");
    		            setCtas.setOrderBy("SLXUNIDNEGOCIO, " + stCuentaObjeto + ", " + stCuenta);
        			}
    			}
    			else
        			//setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getString("STORELOCSITE") + "'");
    			{
    				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getString("STORELOCSITE") + "'" + stWhere);
                	logger.debug("19 CustLlenadoCuentas.setCuentasPlan: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
    	            if(setCtas.isEmpty())
        				setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getString("STORELOCSITE") + "'" + stWhere2);
                	logger.debug("20 CustLlenadoCuentas.setCuentasPlan: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
    	            if(setCtas.isEmpty())
    	            	setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and siteid = '" + mboPlan.getString("STORELOCSITE") + "'");
    	            setCtas.setOrderBy("SLXUNIDNEGOCIO, " + stCuentaObjeto + ", " + stCuenta);
    			}
    		}
    	}
    	else
    		//setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and slxalmacen = '" + mboPlan.getString(stAlmacen) + "' and siteid = '" + mboPlan.getString("STORELOCSITE") + "'");
    	{
    		setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and slxalmacen = '" + mboPlan.getString(stAlmacen) + "' and siteid = '" + mboPlan.getString("STORELOCSITE") + "'" + stWhere);
        	logger.debug("21 CustLlenadoCuentas.setCuentasPlan: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
            if(setCtas.isEmpty())
        		setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and slxalmacen = '" + mboPlan.getString(stAlmacen) + "' and siteid = '" + mboPlan.getString("STORELOCSITE") + "'" + stWhere2);
        	logger.debug("22 CustLlenadoCuentas.setCuentasPlan: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
            if(setCtas.isEmpty())
            	setCtas = mboPlan.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidnegocio + "' and slxalmacen = '" + mboPlan.getString(stAlmacen) + "' and siteid = '" + mboPlan.getString("STORELOCSITE") + "'");
            setCtas.setOrderBy("SLXUNIDNEGOCIO, " + stCuentaObjeto + ", " + stCuenta);
    	}
    	//---------------------- FINAL --------------------------------------------
        
    	logger.debug("11 CustLlenadoCuentas.setCuentasPlan: if(!setCtas.isEmpty())= " + (!setCtas.isEmpty()));
        if(!setCtas.isEmpty())
        {
        	mboCta = setCtas.getMbo(0);
        	mboPlan.setValue("SLXUNIDNEGOCIO", mboCta.getString("SLXUNIDNEGOCIO"), 11L);
        	mboPlan.setValue("SLXCAUX",  mboCta.getString(stCuenta), 11L);
        	
        	// Sólo para Materiales, se debe llenar el tipo de costo.
            logger.debug("12 CustLlenadoCuentas.setCuentasPlan: if(boMaterial)= " + (boMaterial));
        	if(boMaterial)
        	{		
        		mboPlan.setValue("SLXTIPOCOSTO", mboCta.getString("SLXTIPOCOSTO"), 11L);
               	mboPlan.setValue("SLXCOBJETO",  mboCta.getString(stCuentaObjeto), 11L);
        	}
        	else
        		mboPlan.setValue("SLXCOBJETO",  mboCta.getString(stCuentaObjeto), 11L);
        	
        	//-------------------------------------------------------------------------
        	//HHT 20201014: A solicitud de Ricardo Aranguren
        	//mboPlan.getOwner().setValue("SLXPREACTIVO", mboCta.getString("SLXPREACTIVO"), 11L);
            logger.debug("13 CustLlenadoCuentas.setCuentasPlan: if(mboPlan.getOwner().isNull(\"SLXPREACTIVO\"))= " + (mboPlan.getOwner().isNull("SLXPREACTIVO")));
        	if(mboPlan.getOwner().isNull("SLXPREACTIVO"))
        		mboPlan.getOwner().setValue("SLXPREACTIVO", mboCta.getString("SLXPREACTIVO"), 11L);
        	//-------------------------------------------------------------------------
        }
        setCtas.close();
     
        logger.debug("00 CustLlenadoCuentas.setCuentasPlan: FINAL");
    	logger.debug("**********************************************************************");
        logger.debug("");
    }
 
    public static void setCuentasReal(MboRemote mboReal, String stUnidnegocio, String stCuenta, String stCuentaObjeto, boolean boMat, MboRemote wo) throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustLlenadoCuentas.setCuentasReal: INICIO stUnidnegocio= " + stUnidnegocio + ", stCuenta= " + stCuenta + ", stCuentaObjeto= " + stCuentaObjeto);
    	//-------------------------------------------------------------------------
    	//HHT 20210721: A solicitud de Carlos Gómez
    	//---------------------- INICIO -------------------------------------------
        /*    	
        MboRemote mboCta = null;
        MboSetRemote setCtas = mboReal.getMboSet("$slxmapctas", "slxmapctas", "SLXUNIDNEGOCIO = '" + stUnidnegocio + "' and siteid = '" + mboReal.getString("SITEID") + "'" + stFiltro);
        
        logger.debug("01 CustLlenadoCuentas.setCuentasReal: if(!setCtas.isEmpty())= " + (!setCtas.isEmpty()));
        if(!setCtas.isEmpty())
        {
        	mboCta = setCtas.getMbo(0);
        	String strGL =
        	mboCta.getString("SLXUNIDNEGOCIO") + "." +
        	mboCta.getString(stCuentaObjeto) + "." +
        	mboCta.getString(stCuenta);
        	mboReal.setValue("gldebitacct", strGL, 11L);
        }
        setCtas.close();
        */
        MboRemote mboCta = null;
       	MboSetRemote setCtas = null;
        
    	//-------------------------------------------------------------------------
    	//HHT 20210721: A solicitud de Carlos Gómez
    	//---------------------- INICIO -------------------------------------------
        String stWhere   = "";
        String stWhere2  = "";
    	int inFiltro = 0;
       	logger.debug("01 CustLlenadoCuentas.seteaCuentasReal: if(!wo.isNull(\"SLXOBJCOSTEO\"))= " + (!wo.isNull("SLXOBJCOSTEO")));
    	if(!wo.isNull("SLXOBJCOSTEO"))
    	{
    		inFiltro = 1;
        	logger.debug("02 CustLlenadoCuentas.seteaCuentasReal: if(!wo.isNull(\"SLXTIPOACCION\"))= " + (!wo.isNull("SLXTIPOACCION")));
    		if(!wo.isNull("SLXTIPOACCION"))
         		inFiltro = 3;
    	}
    	else
    	{
        	logger.debug("03 CustLlenadoCuentas.seteaCuentasReal: if(!wo.isNull(\"SLXTIPOACCION\"))= " + (!wo.isNull("SLXTIPOACCION")));
    		if(!wo.isNull("SLXTIPOACCION"))
         		inFiltro = 2;
     	}
    	logger.debug("04 CustLlenadoCuentas.seteaCuentasReal: inFiltro= " + inFiltro);
    	if(inFiltro == 0)
    		stWhere = " and SLXOBJCOSTEO is null  and SLXTIPOACCION is null ";
    	else if (inFiltro == 1)
    		stWhere = " and SLXOBJCOSTEO = '" + wo.getString("SLXOBJCOSTEO") + "' and SLXTIPOACCION is null ";
    	else if (inFiltro == 2)
    		stWhere = " and SLXOBJCOSTEO is null and SLXTIPOACCION = '" + wo.getString("SLXTIPOACCION") + "' ";
    	else if (inFiltro == 3)
    	{
       		stWhere  = " and (SLXOBJCOSTEO = '" + wo.getString("SLXOBJCOSTEO") + "' and SLXTIPOACCION = '" + wo.getString("SLXTIPOACCION") + "')";
       		stWhere2 = " and (SLXOBJCOSTEO = '" + wo.getString("SLXOBJCOSTEO") + "' or SLXTIPOACCION = '" + wo.getString("SLXTIPOACCION") + "')";
    	}
     	//---------------------- FINAL --------------------------------------------
        logger.info("05 CustLlenadoCuentas.seteaCuentasReal: if(boMat)= " + (boMat));
        if(boMat)
        {
           	setCtas = mboReal.getMboSet("$slxmapctas", "slxmapctas", "SLXUNIDNEGOCIO = '" + stUnidnegocio + "' and siteid = 'EPM_CORP'" + stWhere);
            logger.info("06 CustLlenadoCuentas.seteaCuentasReal: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
           	if(setCtas.isEmpty())
               	setCtas = mboReal.getMboSet("$slxmapctas", "slxmapctas", "SLXUNIDNEGOCIO = '" + stUnidnegocio + "' and siteid = 'EPM_CORP'" + stWhere2);
            logger.info("07 CustLlenadoCuentas.seteaCuentasReal: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
           	if(setCtas.isEmpty())
               	setCtas = mboReal.getMboSet("$slxmapctas", "slxmapctas", "SLXUNIDNEGOCIO = '" + stUnidnegocio + "' and siteid = 'EPM_CORP'");
            setCtas.setOrderBy("SLXUNIDNEGOCIO, " + stCuentaObjeto + ", " + stCuenta);
        }
        else
        {
           	setCtas = mboReal.getMboSet("$slxmapctas", "slxmapctas", "SLXUNIDNEGOCIO = '" + stUnidnegocio + "' and siteid = '" + mboReal.getString("SITEID") + "'" + stWhere);
            logger.info("08 CustLlenadoCuentas.seteaCuentasReal: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
           	if(setCtas.isEmpty())
               	setCtas = mboReal.getMboSet("$slxmapctas", "slxmapctas", "SLXUNIDNEGOCIO = '" + stUnidnegocio + "' and siteid = '" + mboReal.getString("SITEID") + "'" + stWhere2);
            logger.info("09 CustLlenadoCuentas.seteaCuentasReal: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
           	if(setCtas.isEmpty())
               	setCtas = mboReal.getMboSet("$slxmapctas", "slxmapctas", "SLXUNIDNEGOCIO = '" + stUnidnegocio + "' and siteid = '" + mboReal.getString("SITEID") + "'");
            setCtas.setOrderBy("SLXUNIDNEGOCIO, " + stCuentaObjeto + ", " + stCuenta);
        }
        
        logger.info("10 CustLlenadoCuentas.seteaCuentasReal: if(!setCtas.isEmpty())= " + (!setCtas.isEmpty()));
        if(!setCtas.isEmpty())
        {
        	mboCta = setCtas.getMbo(0);
        	String stGL =
        	mboCta.getString("SLXUNIDNEGOCIO") + "." +
        	mboCta.getString(stCuentaObjeto) + "." +
        	mboCta.getString(stCuenta);
        	mboReal.setValue("glaccount", stGL, 11L);
        }
        setCtas.close();
    	//---------------------- FINAL --------------------------------------------
        logger.debug("00 CustLlenadoCuentas.setCuentasReal: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
    
    public static MboRemote getWo(MboRemote mboRemote) throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustLlenadoCuentas.getWo: INICIO");
        
        logger.debug("01 CustLlenadoCuentas.getWo: if(mboRemote.isNull(\"wonum\"))= " + (mboRemote.isNull("wonum")));
    	if (mboRemote.isNull("wonum"))
    	{
            logger.debug("00 CustLlenadoCuentas.getWo: FINAL null");
            logger.debug("**********************************************************************");
            logger.debug("");
    		return null;
    	}
    	MboRemote woMbo = null;
        String wonum = mboRemote.getString("wonum");
        SqlFormat sqf = new SqlFormat(mboRemote, "wonum=:1 and siteid=:2");
        sqf.setObject(1, "WORKORDER", "wonum", wonum);
        sqf.setObject(2, "WORKORDER", "siteid", mboRemote.getString("siteid"));
        woMbo = (WORemote)((MboSet)mboRemote.getThisMboSet()).getSharedMboSet("WORKORDER", sqf.format()).getMbo(0);

        logger.debug("00 CustLlenadoCuentas.getWo: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
        
        return woMbo;
    }  
}
