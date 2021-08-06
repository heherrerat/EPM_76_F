package com.epm.app.workorder;

import java.rmi.RemoteException;
import psdi.app.asset.Asset;
import psdi.app.pm.PM;
import psdi.app.workorder.WO;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.plusd.app.workorder.PlusDWPService;
import psdi.plusd.app.workorder.PlusDWPServiceRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

import com.epm.app.common.CustLlenadoCuentas;
//com.epm.app.workorder.CustWPServiceSet
//psdi.plusd.app.workorder.PlusDWPServiceSet
public class CustWPService extends PlusDWPService implements PlusDWPServiceRemote
{
	private static MXLogger logger;
	
    public CustWPService(MboSet arg0) throws MXException, RemoteException
    {
        super(arg0);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }
    
    public void add() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustWPService.add: INICIO");

        super.add();
        /*
        MboRemote refWo = getOwner();
    	String strApp = refWo.getThisMboSet().getApp();
    	MboRemote refOwner = null;
    	String stUN="";
    	
    	logger.debug("01 CustWPService.add: if(" + strApp + "!=null)= " + (strApp != null));
    	if(strApp != null)
    	{
    		refOwner = refWo;
    		stUN="SLXCENTROACTIVIDAD";
    		logger.debug("01.01 CustWPService Usa OT");
    	}
    	else
    	{
    		stUN="SLXUNIDNEGOCIO";
    		logger.debug("01.02 CustWPService Usa EUC");

        	logger.debug("02 CustWPService.add: if(refWo.getBoolean(\"ISTASK\"))= " + (refWo.getBoolean("ISTASK")));
    		if(refWo.getBoolean("ISTASK"))
            	//refOwner = refWo.getMboSet("PARENT").getMbo(0);
            	refOwner = refWo.getMboSet("$PLUSDESTCONTROLst", "PLUSDESTCONTROL", "masterwonum = :parent and siteid = :siteid").getMbo(0);
            else
            	//refOwner = refWo;
            	refOwner = refWo.getMboSet("$PLUSDESTCONTROLsp", "PLUSDESTCONTROL", "masterwonum = :wonum and siteid = :siteid").getMbo(0);
    	}
        
    	logger.debug("03 CustWPService.add: if(refOwner != null)= " + (refOwner != null));
        if(refOwner != null)
        {
        	boolean boMaterial = false;

            // Unidade de Negocio.
        	//if (!refOwner.isNull("SLXCENTROACTIVIDAD"))
        	logger.debug("04 CustWPService.add: if(!refOwner.isNull(stUN))= " + (!refOwner.isNull(stUN)));
        	if(!refOwner.isNull(stUN))
        	{
        		//String stUnidadNeg = refOwner.getString("SLXCENTROACTIVIDAD");
        		String stUnidadNeg = refOwner.getString(stUN);
        		String stCuenta = "";
        		String stObjetoCuenta = "";
        		
        		// Servicios NO contratados.
            	logger.debug("05 CustWPService.add: if(isNull(\"SLXCONTRACTNUM\"))= " + (isNull("SLXCONTRACTNUM")));
		        if(isNull("SLXCONTRACTNUM"))
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
                logger.debug("06 CustWPService.add: if(stUnidadNeg.length()!=0)= " + (stUnidadNeg.length()!=0));
		        if(stUnidadNeg.length()!=0)
		        	CustLlenadoCuentas.setCuentasPlan(this, stUnidadNeg, stCuenta, boMaterial, stObjetoCuenta);
        	}
        }
        */
        WO refWo = (WO)getOwner();
        String stUnidadNeg = refWo.getString("SLXCENTROACTIVIDAD");
        
        logger.debug("01 CustWPService.add: if(!refWo.getMboValue(\"JPNUM\").isNull())= " + (!refWo.getMboValue("JPNUM").isNull()));
    	if(!refWo.getMboValue("JPNUM").isNull())
    	{
    		MboSetRemote jobplanSet = refWo.getMboSet("$jobplan", "JOBPLAN", "jpnum = '" + refWo.getMboValue("JPNUM") + "'");
            logger.debug("02 CustWPService.add: if(!jobplanSet.isEmpty())= " + (!jobplanSet.isEmpty()));
    		if(!jobplanSet.isEmpty())
    		{
        		jobplanSet.setOrderBy("PLUSCREVNUM desc");
        		jobplanSet.reset();
        		MboRemote jobplan = jobplanSet.getMbo(0);
        		String stJPUnidadNegocio = jobplan.getString("SLXCENTROACTIVIDAD");
                logger.debug("03 CustWPService.add: if((stJPUnidadNegocio.length() > 0) && (!stUnidadNeg.equalsIgnoreCase(stJPUnidadNegocio)))= " + ((stJPUnidadNegocio.length() > 0) && (!stUnidadNeg.equalsIgnoreCase(stJPUnidadNegocio))));
        		if((stJPUnidadNegocio.length() > 0) && (!stUnidadNeg.equalsIgnoreCase(stJPUnidadNegocio)))
        			stUnidadNeg = stJPUnidadNegocio;
    		}
    	}
        
        setValue("SLXUNIDNEGOCIO", stUnidadNeg);
        voManejoCuentas();
        
        logger.debug("00 CustWPService.add: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
    
    
    public void voManejoCuentas() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustWPService.voManejoCuentas: INICIO");

        MboRemote refWo    = getOwner();
    	MboRemote refOwner = null;
    	String strApp      = refWo.getThisMboSet().getApp();
    	String stUN        = "";
    	
    	logger.debug("01 CustWPService.voManejoCuentas: if(" + strApp + "!=null)= " + (strApp != null));
    	if(strApp != null)
    	{
    		refOwner = refWo;
    		stUN="SLXCENTROACTIVIDAD";
    		logger.debug("01.01 CustWPService Usa OT");
    	}
    	else
    	{
    		stUN="SLXUNIDNEGOCIO";
    		logger.debug("01.02 CustWPService Usa EUC");

        	logger.debug("02 CustWPService.voManejoCuentas: if(refWo.getBoolean(\"ISTASK\"))= " + (refWo.getBoolean("ISTASK")));
    		if(refWo.getBoolean("ISTASK"))
            	//refOwner = refWo.getMboSet("PARENT").getMbo(0);
            	refOwner = refWo.getMboSet("$PLUSDESTCONTROLST", "PLUSDESTCONTROL", "masterwonum = :parent and siteid = :siteid").getMbo(0);
            else
            	//refOwner = refWo;
            	refOwner = refWo.getMboSet("$PLUSDESTCONTROLSP", "PLUSDESTCONTROL", "masterwonum = :wonum and siteid = :siteid").getMbo(0);
    	}
        
    	logger.debug("03 CustWPService.voManejoCuentas: if(refOwner != null)= " + (refOwner != null));
        if(refOwner != null)
        {
        	boolean boMaterial = false;

            // Unidade de Negocio.
        	//if (!refOwner.isNull("SLXCENTROACTIVIDAD"))
        	logger.debug("04 CustWPService.voManejoCuentas: if(!refOwner.isNull(stUN))= " + (!refOwner.isNull(stUN)));
        	if(!refOwner.isNull(stUN))
        	{
        		//String stUnidadNeg = refOwner.getString("SLXCENTROACTIVIDAD");
        		String stUnidadNeg = refOwner.getString(stUN);
        		String stCuenta = "";
        		String stObjetoCuenta = "";
        		
        		// Servicios NO contratados.
            	logger.debug("05 CustWPService.voManejoCuentas: if(isNull(\"SLXCONTRACTNUM\"))= " + (isNull("SLXCONTRACTNUM")));
		        if(isNull("SLXCONTRACTNUM"))
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
                logger.debug("06 CustWPService.voManejoCuentas: if(stUnidadNeg.length()!=0)= " + (stUnidadNeg.length()!=0));
		        if(stUnidadNeg.length()!=0)
		        	CustLlenadoCuentas.setCuentasPlan((MboRemote)this, stUnidadNeg, stCuenta, boMaterial, stObjetoCuenta);
        	}
        }
        
        logger.debug("00 CustWPService.voManejoCuentas: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
    
    public void appValidate() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustWPService.appValidate: INICIO");

        super.appValidate();
        /*
        MboRemote refWo = getOwner();
    	String strApp = refWo.getThisMboSet().getApp();
    	MboRemote refOwner = null;
    	String stUN="";
    	
    	logger.debug("01 CustWPService.appValidate: if(" + strApp + "!=null)= " + (strApp != null));
    	if(strApp != null)
    	{
    		refOwner = refWo;
    		stUN="SLXCENTROACTIVIDAD";
    	}
    	else
    	{
    		stUN="SLXUNIDNEGOCIO";

        	logger.debug("02 CustWPService.appValidate: if(refWo.getBoolean(\"ISTASK\"))= " + (refWo.getBoolean("ISTASK")));
    		if(refWo.getBoolean("ISTASK"))
            	//refOwner = refWo.getMboSet("PARENT").getMbo(0);
            	refOwner = refWo.getMboSet("$PLUSDESTCONTROLst", "PLUSDESTCONTROL", "masterwonum = :parent and siteid = :siteid").getMbo(0);
            else
            	//refOwner = refWo;
            	refOwner = refWo.getMboSet("$PLUSDESTCONTROLsp", "PLUSDESTCONTROL", "masterwonum = :wonum and siteid = :siteid").getMbo(0);
    	}
        
    	logger.debug("03 CustWPService.appValidate: if(refOwner != null)= " + (refOwner != null));
        if(refOwner != null)
        {
        	boolean boMaterial = false;

            // Unidade de Negocio.
        	//if (!refOwner.isNull("SLXCENTROACTIVIDAD"))
        	logger.debug("04 CustWPService.appValidate: if(!refOwner.isNull(stUN))= " + (!refOwner.isNull(stUN)));
        	if(!refOwner.isNull(stUN))
        	{
        		//String stUnidadNeg = refOwner.getString("SLXCENTROACTIVIDAD");
        		String stUnidadNeg = refOwner.getString(stUN);
        		String stCuenta = "";
        		String stObjetoCuenta = "";
        		
        		// Servicios NO contratados.
            	logger.debug("05 CustWPService.appValidate: if(isNull(\"SLXCONTRACTNUM\"))= " + (isNull("SLXCONTRACTNUM")));
		        if(isNull("SLXCONTRACTNUM"))
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
                logger.debug("06 CustWPService.appValidate: if(stUnidadNeg.length()!=0)= " + (stUnidadNeg.length()!=0));
		        if(stUnidadNeg.length()!=0)
		        	CustLlenadoCuentas.setCuentasPlan(this, stUnidadNeg, stCuenta, boMaterial, stObjetoCuenta);
        	}
        }
        */
    	MboSetRemote setCtas = null;   	
    	MboRemote wo       = (WO)getOwner();
        MboRemote refOwner = null;
    	String stApp          = wo.getThisMboSet().getApp();
    	String stUN           = "";
		String stCuenta       = "";
		String stCuentaObjeto = "";
		String stFiltro       = "";
		String stMsg          = "";

        logger.debug("01 CustWPService.appValidate: if(!(wo.getOwner() instanceof Asset) && !(wo.getOwner() instanceof PM))= " + (!(wo.getOwner() instanceof Asset) && !(wo.getOwner() instanceof PM)));
        if(!(wo.getOwner() instanceof Asset) && !(wo.getOwner() instanceof PM))
		{
            logger.debug("02 CustWPService.appValidate: if(" + stApp + " != null)= " + (stApp != null));
        	if(stApp != null)
        	{
        		refOwner = wo;
        		stUN="SLXCENTROACTIVIDAD";
        	}
        	else
        	{
        		stUN="SLXUNIDNEGOCIO";
        		
            	logger.debug("03 CustWPService.appValidate: if(wo.getBoolean(\"ISTASK\"))= " + (wo.getBoolean("ISTASK")));
        		if(wo.getBoolean("ISTASK"))
                	refOwner = wo.getMboSet("$PLUSDESTCONTROLST", "PLUSDESTCONTROL", "masterwonum = :parent and siteid = :siteid").getMbo(0);
                else
                	refOwner = wo.getMboSet("$PLUSDESTCONTROLSP", "PLUSDESTCONTROL", "masterwonum = :wonum and siteid = :siteid").getMbo(0);
        	}
            
        	logger.debug("04 CustWPService.appValidate: if(refOwner != null)= " + (refOwner != null));
            if(refOwner != null)
            {
            	logger.debug("05 CustWPService.appValidate: if(!refOwner.isNull(stUN))= " + (!refOwner.isNull(stUN)));
            	if(!refOwner.isNull(stUN))
            	{
                	logger.debug("06 CustWPService.appValidate: if(isNull(\"SLXCONTRACTNUM\"))= " + (isNull("SLXCONTRACTNUM")));
    		        if(isNull("SLXCONTRACTNUM"))
    		        {
    		        	stCuenta = "SLXGLCTAAUXSERV";
    		        	stCuentaObjeto = "SLXGLCUENTAOBJETO";
    		        }
    		        else
    		        {
    		        	stCuenta = "SLXGLCTAAUXSERVCONT";
    		        	stCuentaObjeto = "SLXGLCUENTAOBJETOSERVCONT";
    		        }
            	}
            
            	stFiltro = " and " + stCuenta + " = '" + getString("SLXCAUX") + "' AND " + stCuentaObjeto + " = '" + getString("SLXCOBJETO") + "'";
            	stMsg = "Existe un problema con los datos ingresados (Cuenta Objeto o Cuenta Auxiliar)";
                
            	logger.debug("07 CustWPService.appValidate: if(isNull(\"LOCATION\"))= " + (isNull("LOCATION")));
            	if(isNull("LOCATION"))
            	{
                	logger.debug("08 CustWPService.appValidate: if(isNull(\"STORELOCSITE\"))= " + (isNull("STORELOCSITE")));
            		if(isNull("STORELOCSITE"))
            		{
                    	logger.debug("09 CustWPService.appValidate: if(isNull(\"SITEID\"))= " + (isNull("SITEID")));
            			if(isNull("SITEID"))
            				//setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getOwner().getString("SITEID") + "'");
            			{
            				setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getOwner().getString("SITEID") + "'" + stFiltro);
                        	logger.debug("10 CustWPService.appValidate: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
            				if(setCtas.isEmpty())
            					setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getOwner().getString("SITEID") + "'");
            			}
            			else
            				//setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("SITEID") + "'");
            			{
            				setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("SITEID") + "'" + stFiltro);
                        	logger.debug("11 CustWPService.appValidate: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
            				if(setCtas.isEmpty())
            					setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("SITEID") + "'");
            			}
            		}
            		else
            			//setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("STORELOCSITE") + "'");
            		{
    					setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("STORELOCSITE") + "'" + stFiltro);
            			logger.debug("12 CustWPService.appValidate: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
        				if(setCtas.isEmpty())
        					setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("STORELOCSITE") + "'");
            		}
            	}
            	else
            		//setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and slxalmacen = '" + getString("LOCATION") + "' and siteid = '" + getString("STORELOCSITE") + "'");
            	{
	        		setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and slxalmacen = '" + getString("LOCATION") + "' and siteid = '" + getString("STORELOCSITE") + "'" + stFiltro);
            		logger.debug("13 CustWPService.appValidate: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
    				if(setCtas.isEmpty())
    	        		setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and slxalmacen = '" + getString("LOCATION") + "' and siteid = '" + getString("STORELOCSITE") + "'");
            	}
                
            	logger.debug("14 CustWPService.appValidate: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
                if(setCtas.isEmpty())
                {
                	Object[] params = { stMsg };
                	throw new MXApplicationException("SLX_SOLI_PAG_CONT", "ErrorInesperado", params);
                }
            }
		}        
        logger.debug("00 CustWPService.appValidate: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
    
/* HHT 20190620 a solicitud de Sergio Moreno   
    public void appValidate() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustWPService.appValidate: INICIO");
        
    	super.appValidate();
    	MboSetRemote wpserviceSet = getThisMboSet();
    	MboRemote wpservice = null;
    	String stSLXUNIDNEGOCIO = "";
    	String stSLXCAUX        = "";
    	String stSLXCOBJETO     = "";
    	
    	for(int i = 0; ((wpservice = wpserviceSet.getMbo(i)) != null); i++)
    	{
        	logger.debug("01." + i + " CustWPService.appValidate: if(wpservice.isModified() && wpservice.toBeSaved())= " + (wpservice.isModified() && wpservice.toBeSaved()));
    		if(wpservice.isModified() && wpservice.toBeSaved())
    		{
            	stSLXUNIDNEGOCIO = wpservice.getString("SLXUNIDNEGOCIO");
            	stSLXCAUX        = wpservice.getString("SLXCAUX");
            	stSLXCOBJETO     = wpservice.getString("SLXCOBJETO");
    		}
    	}

    	for(int i = 0; ((wpservice = wpserviceSet.getMbo(i)) != null); i++)
    	{
        	wpservice.setValue("SLXUNIDNEGOCIO", stSLXUNIDNEGOCIO, 11L);
        	wpservice.setValue("SLXCAUX"       , stSLXCAUX       , 11L);
        	wpservice.setValue("SLXCOBJETO"    , stSLXCOBJETO    , 11L);
    	}
    	
        logger.debug("00 CustWPService.appValidate: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
*/
    public void init() throws MXException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustWPService.init: INICIO");
        
        super.init();
        try 
        {
        	logger.debug("01 CustWPService.init: if(getMboValue(\"SLXUNIDNEGOCIO\").isNull())= " + (getMboValue("SLXUNIDNEGOCIO").isNull()));
        	if(getMboValue("SLXUNIDNEGOCIO").isNull())
        		voManejoCuentas();
		} 
        catch (RemoteException e) 
        {
		}
        
        logger.debug("00 CustWPService.init: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }}