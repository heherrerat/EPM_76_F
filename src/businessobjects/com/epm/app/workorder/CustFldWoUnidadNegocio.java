package com.epm.app.workorder;
import psdi.mbo.MboValue;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValueAdapter;
import psdi.app.workorder.*;
import com.epm.app.common.CustLlenadoCuentas;
import java.rmi.RemoteException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
public class CustFldWoUnidadNegocio extends MboValueAdapter
{
	private static MXLogger logger;
    
	public CustFldWoUnidadNegocio(MboValue mbv)
    {
        super(mbv);
		logger = MXLoggerFactory.getLogger("maximo.application");
    }
    
    public void action() throws MXException, RemoteException
    {
    	super.action();
    	
    	WO wo = (WO)getMboValue().getMbo();
    	String strApp = wo.getThisMboSet().getApp();
    	logger.debug("00 CustFldWoUnidadNegocio APP: " + strApp);
    	if (wo.getThisMboSet().getApp()==null) return;
    	logger.debug("01 CustFldWoUnidadNegocio");
    	MboRemote mboMapCta = wo.getMboSet("SLXMAPCTAS").getMbo(0);
    	logger.debug("02 CustFldWoUnidadNegocio");
    	//-------------------------------------------------------------------------
    	//HHT 20201014: A solicitud de Ricardo Aranguren
    	//if (mboMapCta != null)
    	if (mboMapCta != null && wo.isNull("slxpreactivo"))
       	//-------------------------------------------------------------------------
    	{	
        	logger.debug("03 CustFldWoUnidadNegocio");
    		wo.setValue("slxpreactivo", mboMapCta.getString("slxpreactivo"), NOACCESSCHECK | NOVALIDATION);
        	logger.debug("04 CustFldWoUnidadNegocio. Pre Activo: " + mboMapCta.getString("slxpreactivo"));
    	}
    	WPMaterialSet wpMatSet = (WPMaterialSet)wo.getMboSet("WPMATERIAL");
    	logger.debug("05 CustFldWoUnidadNegocio. Materiales de la OT Padre...");
    	WPServiceSet wpSrvSet = (WPServiceSet)wo.getMboSet("WPSERVICE");
    	logger.debug("06 CustFldWoUnidadNegocio. Servicios de la OT Padre...");
    	voProcesaCambiosUnidadNegocio(wpMatSet, wpSrvSet);
    	logger.debug("07 CustFldWoUnidadNegocio. Despues de Procesar Materiales y Servicios de la OT Padre...");
    	
    	MboSetRemote mboSetTask = wo.getMboSet("CHILDTASK");
    	logger.debug("08 CustFldWoUnidadNegocio. Obtencion de Tareas de la OT Padre...");
    	if (!mboSetTask.isEmpty())
    	{
    		int x = 0;
            for(MboRemote wpTask = mboSetTask.getMbo(x); wpTask != null; wpTask = mboSetTask.getMbo(x))
            {
            	logger.debug("09 CustFldWoUnidadNegocio. Materiales de la OT Tarea..." + x);
            	WPMaterialSet wpMatTaskSet = (WPMaterialSet)wpTask.getMboSet("WPMATERIAL");
            	logger.debug("10 CustFldWoUnidadNegocio. Servicios de la OT Tarea..." + x);
            	WPServiceSet wpSrvTaskSet = (WPServiceSet)wpTask.getMboSet("WPSERVICE");
            	voProcesaCambiosUnidadNegocio(wpMatTaskSet, wpSrvTaskSet);
            	logger.debug("11 CustFldWoUnidadNegocio. Despues de Procesar Materiales y Servicios de la OT Tarea..." + x);
            	x++;
            }
    	}
    }
       
    public void voProcesaCambiosUnidadNegocio(MboSetRemote wpMatSet, MboSetRemote wpSrvSet) throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: INICIO");
        
    	// Unidad de Negocio.
    	String stUnidadNeg = getMboValue().getString();
    	/*
    	String stUnidadNeg = "";
    	
		logger.debug("01A CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: if(!getMboValue().isNull())= " + (!getMboValue().isNull()));
    	if(!getMboValue().isNull())
    	{
    		stUnidadNeg = getMboValue().getString();
    	}
    	else
    	{
    		logger.debug("02A CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: if(!getMboValue(\"jpnum\").isNull())= " + (!getMboValue("jpnum").isNull()));
    		if(!getMboValue("jpnum").isNull())
    		{
    			MboRemote jobplan = getMboValue().getMbo().getMboSet("JOBPLAN").getMbo(0);
        		logger.debug("03A CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: if(jobplan != null)= " + (jobplan != null));
    			if(jobplan != null && !jobplan.isNull("SLXCENTROACTIVIDAD"))
    				stUnidadNeg = jobplan.getString("SLXCENTROACTIVIDAD");
    			else
        		{
            		logger.debug("04A CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: if(!getMboValue(\"assetnum\").isNull())= " + (!getMboValue("assetnum").isNull()));
            		if(!getMboValue("assetnum").isNull())
            		{
                		MboRemote asset = getMboValue().getMbo().getMboSet("ASSET").getMbo(0);
                		logger.debug("05A CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: if(asset != null)= " + (asset != null));
            			if(asset != null && !asset.isNull("SLXCENTROACTIVIDAD"))
            				stUnidadNeg = asset.getString("SLXCENTROACTIVIDAD");
            		}
        		}
    		}
    		else
    		{
        		logger.debug("06A CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: if(!getMboValue(\"assetnum\").isNull())= " + (!getMboValue("assetnum").isNull()));
        		if(!getMboValue("assetnum").isNull())
        		{
            		MboRemote asset = getMboValue().getMbo().getMboSet("ASSET").getMbo(0);
            		logger.debug("07A CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: if(asset != null)= " + (asset != null));
        			if(asset != null && !asset.isNull("SLXCENTROACTIVIDAD"))
        				stUnidadNeg = asset.getString("SLXCENTROACTIVIDAD");
        		}
    		}
    		getMboValue().setValue(stUnidadNeg, 11L);
    	}
    	*/
    	
    	boolean boMaterial = true;
        int x = 0;
		String stCuenta = "";
		String stObjetoCuenta = "";
        
		logger.debug("01 CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: if(!wpMatSet.isEmpty())= " + (!wpMatSet.isEmpty()));
    	if(!wpMatSet.isEmpty())
    	{
            for(MboRemote wpMaterial = wpMatSet.getMbo(x); wpMaterial != null; wpMaterial = wpMatSet.getMbo(x))
            {
    			stCuenta = "";
    			stObjetoCuenta = "";
    	
    			// Materiales NO contratados.
    	        logger.debug("02." + x + " CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: if(wpMaterial.isNull(\"SLXCONTRACTNUM\"))= " + (wpMaterial.isNull("SLXCONTRACTNUM")));
    	        if(wpMaterial.isNull("SLXCONTRACTNUM"))
    	        {
    	        	stCuenta = "SLXGLCTAAUXMATER";
	        		stObjetoCuenta = "SLXGLCUENTAOBJETOMAT"; // Cuenta Objeto Materiales
    	        }	        		
    	        // Materiales contratados.
    	        else
    	        {
    	        	stCuenta = "SLXGLCTAAUXMATERCONT";
	        		stObjetoCuenta = "SLXGLCUENTAOBJETOMATCONT"; // Cuenta Objeto Materiales Contratados
    	        }
    	        // Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
    	        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
    	        // Se agrega cuenta de objeto.
    	        CustLlenadoCuentas.setCuentasPlan(wpMaterial, stUnidadNeg, stCuenta, boMaterial, stObjetoCuenta);
                x++;
            }
    	}
    	boMaterial = false;
        x = 0;
        
        logger.debug("03 CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: if(!wpSrvSet.isEmpty())= " + (!wpSrvSet.isEmpty()));
        if(!wpSrvSet.isEmpty())
    	{
            for(MboRemote wpService = wpSrvSet.getMbo(x); wpService != null; wpService = wpSrvSet.getMbo(x))
            {
    			stCuenta = "";
    			stObjetoCuenta = "";
    	
    			// Servicios NO contratados.
    	        logger.debug("04." + x + " CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: if(wpService.isNull(\"SLXCONTRACTNUM\"))= " + (wpService.isNull("SLXCONTRACTNUM")));
    	        if (wpService.isNull("SLXCONTRACTNUM"))
    	        {
    	        	stCuenta = "SLXGLCTAAUXSERV";
        			stObjetoCuenta = "SLXGLCUENTAOBJETO"; // Cuenta Objeto Servicios NO Contratados.
    	        }
    	        // Servicios contratados.
    	        else
    	        {
    	        	stCuenta = "SLXGLCTAAUXSERVCONT";
    				stObjetoCuenta = "SLXGLCUENTAOBJETOSERVCONT"; // Cuenta Objeto Servicios Contratados.
    	        }
    	        // Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
    	        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
    	        CustLlenadoCuentas.setCuentasPlan(wpService, stUnidadNeg, stCuenta, boMaterial, stObjetoCuenta);
                x++;
            }
    	}
        
        logger.debug("00 CustFldWoUnidadNegocio.voProcesaCambiosUnidadNegocio: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
}