package com.epm.app.workorder;

import java.rmi.RemoteException;
import psdi.app.asset.Asset;
import psdi.app.pm.PM;
import psdi.app.workorder.WO;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.plusd.app.workorder.PlusDWPMaterial;
import psdi.plusd.app.workorder.PlusDWPMaterialRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
import com.epm.app.common.CustLlenadoCuentas;
//com.epm.app.workorder.CustWPMaterialSet
//psdi.plusd.app.workorder.PlusDWPMaterialSet
public class CustWPMaterial extends PlusDWPMaterial implements PlusDWPMaterialRemote
{
	private static MXLogger logger;
	
    public CustWPMaterial(MboSet arg0) throws MXException, RemoteException
    {
        super(arg0);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

    public void add() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustWPMaterial.add: INICIO");

        super.add();
        WO refWo = (WO)getOwner();
        String stUnidadNeg = refWo.getString("SLXCENTROACTIVIDAD");
        
        logger.debug("01 CustWPMaterial.add: if(!refWo.getMboValue(\"JPNUM\").isNull())= " + (!refWo.getMboValue("JPNUM").isNull()));
    	if(!refWo.getMboValue("JPNUM").isNull())
    	{
    		MboSetRemote jobplanSet = refWo.getMboSet("$jobplan", "JOBPLAN", "jpnum = '" + refWo.getMboValue("JPNUM") + "'");
            logger.debug("02 CustWPMaterial.add: if(!jobplanSet.isEmpty())= " + (!jobplanSet.isEmpty()));
    		if(!jobplanSet.isEmpty())
    		{
        		jobplanSet.setOrderBy("PLUSCREVNUM desc");
        		jobplanSet.reset();
        		MboRemote jobplan = jobplanSet.getMbo(0);
        		String stJPUnidadNegocio = jobplan.getString("SLXCENTROACTIVIDAD");

        		logger.debug("03 CustWPMaterial.add: if((stJPUnidadNegocio.length() > 0) && (!stUnidadNeg.equalsIgnoreCase(stJPUnidadNegocio)))= " + ((stJPUnidadNegocio.length() > 0) && (!stUnidadNeg.equalsIgnoreCase(stJPUnidadNegocio))));
        		if((stJPUnidadNegocio.length() > 0) && (!stUnidadNeg.equalsIgnoreCase(stJPUnidadNegocio)))
        			stUnidadNeg = stJPUnidadNegocio;
    		}
    	}
        
        setValue("SLXUNIDNEGOCIO", stUnidadNeg);

        logger.debug("04 CustWPMaterial.add: if(refWo.isNull(\"SLXCENTROACTIVIDAD\"))= " + (refWo.isNull("SLXCENTROACTIVIDAD")));
        if(refWo.isNull("SLXCENTROACTIVIDAD"))
        	refWo.setValue("SLXCENTROACTIVIDAD", stUnidadNeg);
        voManejoCuentas();
        
        logger.debug("00 CustWPMaterial.add: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
 
    public void init() throws MXException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustWPMaterial.init: INICIO");
        
        super.init();
        try 
        {
/*
        	logger.debug("01 CustWPMaterial.init: if(getMboValue(\"SLXUNIDNEGOCIO\").isNull() && isModified())= " + (getMboValue("SLXUNIDNEGOCIO").isNull() && isModified()));
        	if(getMboValue("SLXUNIDNEGOCIO").isNull() && isModified())
*/
        	logger.debug("01 CustWPMaterial.init: if(getMboValue(\"SLXUNIDNEGOCIO\").isNull())= " + (getMboValue("SLXUNIDNEGOCIO").isNull()));
        	if(getMboValue("SLXUNIDNEGOCIO").isNull())
        		voManejoCuentas();
		} 
        catch (RemoteException e) 
        {
		}     
        logger.debug("00 CustWPMaterial.init: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
 
    public void voManejoCuentas() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustWPMaterial.voManejoCuentas: INICIO");

        MboRemote refWo    = getOwner();
        MboRemote refOwner = null;
    	String    stApp    = refWo.getThisMboSet().getApp();
    	String    stUN     = "";

        logger.debug("01 CustWPMaterial.voManejoCuentas: if(" + stApp + " != null)= " + (stApp != null));
    	if(stApp != null)
    	{
    		refOwner = refWo;
			stUN = "SLXCENTROACTIVIDAD";
			logger.debug("01.01 CustWPMaterial Usa OT");
    	}
    	else
    	{
    		stUN = "SLXUNIDNEGOCIO";
    		logger.debug("01.02 CustWPMaterial.voManejoCuentas: Usa EUC");

            logger.debug("02 CustWPMaterial.voManejoCuentas: if(refWo.getBoolean(\"ISTASK\"))= " + (refWo.getBoolean("ISTASK")));
	        if(refWo.getBoolean("ISTASK"))
	        	//refOwner = refWo.getMboSet("PARENT").getMbo(0);
	        	refOwner = refWo.getMboSet("$PLUSDESTCONTROLmt", "PLUSDESTCONTROL", "masterwonum = :parent and siteid = :siteid").getMbo(0);
	        else
	        	//refOwner = refWo;
	        	refOwner = refWo.getMboSet("$PLUSDESTCONTROLmp", "PLUSDESTCONTROL", "masterwonum = :wonum and siteid = :siteid").getMbo(0);
    	}
/*
        logger.debug("02.01 CustWPMaterial.voManejoCuentas: (refOwner == null)= " +(refOwner == null));
		if(refOwner == null)
		{
			refOwner = getMboSet("$workorder", "workorder", "wonum = :wonum and siteid = :siteid").getMbo(0);
			stUN = "SLXCENTROACTIVIDAD";
		}	
*/
    	logger.debug("03 CustWPMaterial.voManejoCuentas: if(refOwner != null)= " + (refOwner != null));
        if(refOwner != null)
        {
        	boolean boMaterial = true;

            // Unidade de Negocio.
        	//if (!refOwner.isNull("SLXCENTROACTIVIDAD"))
            logger.debug("04 CustWPMaterial.voManejoCuentas: if(!refOwner.isNull(stUN))= " + (!refOwner.isNull(stUN)));
            if(!refOwner.isNull(stUN))
        	{
        		//String stUnidadNeg = refOwner.getString("SLXCENTROACTIVIDAD");
           		String stUnidadNeg = refOwner.getString(stUN);
        		String stCuenta = "";
        		String stObjetoCuenta = "";
        		
        		// Materiales NO contratados.
                //logger.debug("05 CustWPMaterial.voManejoCuentas: if(isNull(\"SLXCONTRACTNUM\"))= " + (isNull("SLXCONTRACTNUM")));
		        //if (isNull("SLXCONTRACTNUM"))
                logger.debug("05 CustWPMaterial.voManejoCuentas: if(getInt(\"DIRECTREQ\") == 0)= " + (getInt("DIRECTREQ") == 0));
		        if (getInt("DIRECTREQ") == 0)
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
                logger.debug("06 CustWPMaterial.voManejoCuentas: if(stUnidadNeg.length()!=0)= " + (stUnidadNeg.length()!=0));
		        if(stUnidadNeg.length()!=0)
			        	CustLlenadoCuentas.setCuentasPlan((MboRemote)this, stUnidadNeg, stCuenta, boMaterial, stObjetoCuenta);
        	}
        }
        logger.debug("00 CustWPMaterial.voManejoCuentas: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }

    public void appValidate() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustWPMaterial.appValidate: INICIO");
        
        super.appValidate();        
    	MboSetRemote setCtas = null;    	
    	MboRemote wo       = (WO)getOwner();
        MboRemote refOwner = null;
    	String stApp          = wo.getThisMboSet().getApp();
    	String stUN           = "";
		String stCuenta       = "";
		String stCuentaObjeto = "";
		String stFiltro       = "";
		String stMsg          = "";
        
        logger.debug("01 CustWPMaterial.appValidate: if(!(wo.getOwner() instanceof Asset) && !(wo.getOwner() instanceof PM))= " + (!(wo.getOwner() instanceof Asset) && !(wo.getOwner() instanceof PM)));
        if(!(wo.getOwner() instanceof Asset) && !(wo.getOwner() instanceof PM))
		{
            logger.debug("02 CustWPMaterial.appValidate: if(" + stApp + " != null)= " + (stApp != null));
        	if(stApp != null)
        	{
        		refOwner = wo;
    			stUN = "SLXCENTROACTIVIDAD";
        	}
        	else
        	{
        		stUN = "SLXUNIDNEGOCIO";
        		
                logger.debug("03 CustWPMaterial.appValidate: if(wo.getBoolean(\"ISTASK\"))= " + (wo.getBoolean("ISTASK")));
    	        if(wo.getBoolean("ISTASK"))
    	        	refOwner = wo.getMboSet("$PLUSDESTCONTROLMT", "PLUSDESTCONTROL", "masterwonum = :parent and siteid = :siteid").getMbo(0);
    	        else
    	        	refOwner = wo.getMboSet("$PLUSDESTCONTROLMP", "PLUSDESTCONTROL", "masterwonum = :wonum and siteid = :siteid").getMbo(0);
        	}

            logger.debug("04 CustWPMaterial.appValidate: if(refOwner != null)= " + (refOwner != null));
        	if(refOwner != null)
            {
                logger.debug("05 CustWPMaterial.appValidate: if(!refOwner.isNull(stUN))= " + (!refOwner.isNull(stUN)));
                if(!refOwner.isNull(stUN))
            	{
            		
                    logger.debug("06 CustWPMaterial.appValidate: if(getInt(\"DIRECTREQ\") == 0)= " + (getInt("DIRECTREQ") == 0));
    		        if (getInt("DIRECTREQ") == 0)
    		        {
    		        	stCuenta = "SLXGLCTAAUXMATER";
    		        	stCuentaObjeto = "SLXGLCUENTAOBJETOMAT";
    		        	stFiltro = " and " + stCuenta + " = '" + getString("SLXCAUX") + "' AND SLXTIPOCOSTO = '" + getString("SLXTIPOCOSTO") + "'";
    		        	stMsg = "Existe un problema con los datos ingresados (Tipo Cuenta o Cuenta Auxiliar)";
    		        }
    		        else
    		        {
    		        	stCuenta = "SLXGLCTAAUXMATERCONT";
    		        	stCuentaObjeto = "SLXGLCUENTAOBJETOMATCONT";
    		        	stFiltro = " and " + stCuenta + " = '" + getString("SLXCAUX") + "' AND " + stCuentaObjeto + " = '" + getString("SLXCOBJETO") + "'";
    		        	stMsg = "Existe un problema con los datos ingresados (Cuenta Objeto o Cuenta Auxiliar)";
    		        }
            	}
                
            	logger.debug("07 CustWPMaterial.appValidate: if(isNull(\"LOCATION\"))= " + (isNull("LOCATION")));
            	if(isNull("LOCATION"))
            	{
                	logger.debug("08 CustWPMaterial.appValidate: if(isNull(\"STORELOCSITE\"))= " + (isNull("STORELOCSITE")));
            		if(isNull("STORELOCSITE"))
            		{
                    	logger.debug("09 CustWPMaterial.appValidate: if(isNull(\"SITEID\"))= " + (isNull("SITEID")));
            			if(isNull("SITEID"))
            				//setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getOwner().getString("SITEID") + "'");
            			{
            				setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getOwner().getString("SITEID") + "'" + stFiltro);
                        	logger.debug("10 CustWPMaterial.appValidate: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
            				if(setCtas.isEmpty())
            					setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getOwner().getString("SITEID") + "'");
            			}
            			else
            				//setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("SITEID") + "'");
            			{
            				setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("SITEID") + "'" + stFiltro);
                        	logger.debug("11 CustWPMaterial.appValidate: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
            				if(setCtas.isEmpty())
            					setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("SITEID") + "'");
            			}
             		}
            		else
            			//setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("STORELOCSITE") + "'");
        			{
            			setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("STORELOCSITE") + "'" + stFiltro);
                    	logger.debug("12 CustWPMaterial.appValidate: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
        				if(setCtas.isEmpty())
        					setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("STORELOCSITE") + "'");
        			}
            	}
            	else
            		//setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and slxalmacen = '" + getString("LOCATION") + "' and siteid = '" + getString("STORELOCSITE") + "'");
    			{
            		setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and slxalmacen = '" + getString("LOCATION") + "' and siteid = '" + getString("STORELOCSITE") + "'" + stFiltro);
                	logger.debug("13 CustWPMaterial.appValidate: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
    				if(setCtas.isEmpty())
    					setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and slxalmacen = '" + getString("LOCATION") + "' and siteid = '" + getString("STORELOCSITE") + "'");
    			}
                
            	logger.debug("14 CustWPMaterial.appValidate: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
                if(setCtas.isEmpty())
                {
                	Object[] params = { stMsg };
                	throw new MXApplicationException("SLX_SOLI_PAG_CONT", "ErrorInesperado", params);
                }
            }
		}
        logger.debug("00 CustWPMaterial.appValidate: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }

/*
    public void save() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustWPMaterial.save: INICIO");
        
        super.save();
        WO wo = (WO)getOwner();

        if(!(wo.getOwner() instanceof Asset) && !(wo.getOwner() instanceof PM))
 		{
 			voManejoCuentas();
 		}

        logger.debug("00 CustWPMaterial.save: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
*/
/*
    public void appValidate() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustWPMaterial.appValidate: INICIO");

    	super.appValidate();
    	
    	MboSetRemote wpmaterialSet = getThisMboSet();
    	MboRemote wpmaterial = null;
    	String stSLXUNIDNEGOCIO = "";
    	String stSLXCAUX        = "";
    	String stSLXTIPOCOSTO   = "";
    	String stSLXCOBJETO     = "";
    	
    	for(int i = 0; ((wpmaterial = wpmaterialSet.getMbo(i)) != null); i++)
    	{
            logger.debug("01." + i + " if(wpmaterial.isModified() && wpmaterial.toBeSaved())= " + (wpmaterial.isModified() && wpmaterial.toBeSaved()));
    		if(wpmaterial.isModified() && wpmaterial.toBeSaved())
    		{
            	stSLXUNIDNEGOCIO = wpmaterial.getString("SLXUNIDNEGOCIO");
            	stSLXCAUX        = wpmaterial.getString("SLXCAUX");
            	stSLXTIPOCOSTO   = wpmaterial.getString("SLXTIPOCOSTO");
            	stSLXCOBJETO     = wpmaterial.getString("SLXCOBJETO");
    		}
    	}

    	for(int i = 0; ((wpmaterial = wpmaterialSet.getMbo(i)) != null); i++)
    	{
        	wpmaterial.setValue("SLXUNIDNEGOCIO", stSLXUNIDNEGOCIO, 11L);
        	wpmaterial.setValue("SLXCAUX"       , stSLXCAUX       , 11L);
        	wpmaterial.setValue("SLXTIPOCOSTO"  , stSLXTIPOCOSTO  , 11L);
        	wpmaterial.setValue("SLXCOBJETO"    , stSLXCOBJETO    , 11L);
    	}
        
        logger.debug("00 CustWPMaterial.appValidate: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
*/
}