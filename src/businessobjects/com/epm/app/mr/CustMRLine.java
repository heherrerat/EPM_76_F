package com.epm.app.mr;

import java.rmi.RemoteException;

import psdi.app.asset.Asset;
import psdi.app.mr.MRLine;
import psdi.app.mr.MRLineRemote;
import psdi.app.pm.PM;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

import com.epm.app.common.CustLlenadoCuentas;

public class CustMRLine extends MRLine implements MRLineRemote
{
	private static MXLogger logger;
	
    public CustMRLine(MboSet ms) throws MXException, RemoteException
    {
        super(ms);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }
    
    public void add() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustMRLine.add: INICIO");

        super.add();
/*    	
        String internalLineType = getInternalLineType();
        
        boolean boContinuar = false;
        boolean boMaterial = false;
        String stCuentaNo = "";
        String strObjetoCuenta = "";
        
        //String stCuentaSi = "";
        // NO existe el concepto de planificados, así que se usan las cuentas de materiales y servicios NO contratados.
        logger.debug("01 CustMRLine.add: if(internalLineType.equalsIgnoreCase(\"SERVICE\") || internalLineType.equalsIgnoreCase(\"STDSERVICE\"))= " + (internalLineType.equalsIgnoreCase("SERVICE") || internalLineType.equalsIgnoreCase("STDSERVICE")));
        if(internalLineType.equalsIgnoreCase("SERVICE") || internalLineType.equalsIgnoreCase("STDSERVICE"))
        {
        	boContinuar = true;
        	stCuentaNo = "SLXGLCTAAUXSERV";
        	//stCuentaSi = "SLXGLCTAAUXMATERCONT";
			//HHT strObjetoCuenta = "SLXGLCUENTAOBJETOSNC"; // Cuenta Objeto Servicios NO Contratados.
			strObjetoCuenta = "SLXGLCUENTAOBJETO"; // Cuenta Objeto Servicios NO Contratados.
        }
        else
        {
            logger.debug("02 CustMRLine.add: if(internalLineType.equalsIgnoreCase(\"ITEM\") || internalLineType.equalsIgnoreCase(\"MATERIAL\"))= " + (internalLineType.equalsIgnoreCase("ITEM") || internalLineType.equalsIgnoreCase("MATERIAL")));
        	if(internalLineType.equalsIgnoreCase("ITEM") || internalLineType.equalsIgnoreCase("MATERIAL"))
            {
            	boMaterial = true;
            	boContinuar = true;
            	stCuentaNo = "SLXGLCTAAUXMATER";
            	//HHTstrObjetoCuenta = "SLXGLCUENTAOBJETO"; // Cuenta Objeto Materiales
            	strObjetoCuenta = "SLXGLCUENTAOBJETOMAT"; // Cuenta Objeto Materiales
            	//stCuentaSi = "SLXGLCTAAUXSERVCONT";
            }
        }

        logger.debug("03 CustMRLine.add: if(boContinuar)= " + (boContinuar));
        if (boContinuar)
        {
	        MboRemote refMR = getOwner();
	        MboRemote refWo = CustLlenadoCuentas.getWo(refMR);
	        logger.debug("04 CustMRLine.add: if(refWo != null)= " + (refWo != null));
	        if(refWo != null)
	        {
	            // Unidade de Negocio.
		        logger.debug("05 CustMRLine.add: if(!refWo.isNull(\"SLXCENTROACTIVIDAD\")= " + (!refWo.isNull("SLXCENTROACTIVIDAD")));
	        	if (!refWo.isNull("SLXCENTROACTIVIDAD"))
	        	{
	        		String stUnidadNeg = refWo.getString("SLXCENTROACTIVIDAD");
	        		//String strCuenta = "";
	        		// Materiales NO contratados.
	        		//if (isNull("SLXCONTRACTNUM"))
	        		//	strCuenta = stCuentaNo;
			        // Materiales contratados.
	        		//else
	        		//	strCuenta = stCuentaSi;
			        
			        // Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
			        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
	        		//CustLlenadoCuentas.setCuentasPlan(this, stUnidadNeg, strCuenta);
	        		CustLlenadoCuentas.setCuentasPlan(this, stUnidadNeg, stCuentaNo, boMaterial, strObjetoCuenta);
	        	}
	        }
        }
*/
        voManejoCuentas();
        
        logger.debug("00 CustMRLine.add: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
    
    public void voManejoCuentas() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustMRLine.voManejoCuentas: INICIO");

        String internalLineType = getInternalLineType();
        
        boolean boContinuar = false;
        boolean boMaterial = false;
        String stCuenta = "";
        String stCuentaObjeto = "";
        
        //String stCuentaSi = "";
        // NO existe el concepto de planificados, así que se usan las cuentas de materiales y servicios NO contratados.
        logger.debug("01 CustMRLine.voManejoCuentas: if(internalLineType.equalsIgnoreCase(\"SERVICE\") || internalLineType.equalsIgnoreCase(\"STDSERVICE\"))= " + (internalLineType.equalsIgnoreCase("SERVICE") || internalLineType.equalsIgnoreCase("STDSERVICE")));
        if(internalLineType.equalsIgnoreCase("SERVICE") || internalLineType.equalsIgnoreCase("STDSERVICE"))
        {
        	boContinuar = true;
            // Servicios NO contratados.
            logger.debug("02 CustMRLine.voManejoCuentas: if(getMboValue(\"SLXCONTRACTNUM\").isNull())= " + (getMboValue("SLXCONTRACTNUM").isNull()));
            if (getMboValue("SLXCONTRACTNUM").isNull())
            {
            	stCuenta = "SLXGLCTAAUXSERV";
            	stCuentaObjeto = "SLXGLCUENTAOBJETO"; // Cuenta Objeto Servicios NO Contratados.
            }
            // Servicios contratados.
            else
            {
            	stCuenta = "SLXGLCTAAUXSERVCONT";
            	stCuentaObjeto = "SLXGLCUENTAOBJETOSERVCONT"; // Cuenta Objeto Servicios Contratados.
            }
        }
        else
        {
            logger.debug("03 CustMRLine.voManejoCuentas: if(internalLineType.equalsIgnoreCase(\"ITEM\") || internalLineType.equalsIgnoreCase(\"MATERIAL\"))= " + (internalLineType.equalsIgnoreCase("ITEM") || internalLineType.equalsIgnoreCase("MATERIAL")));
        	if(internalLineType.equalsIgnoreCase("ITEM") || internalLineType.equalsIgnoreCase("MATERIAL"))
            {
            	boMaterial = true;
            	boContinuar = true;
/*HHT 20190705 ------------------------------------------
            	// Materiales NO contratados.
                logger.debug("04 CustMRLine.voManejoCuentas: if(getMboValue(\"SLXCONTRACTNUM\").isNull())= " + (getMboValue("SLXCONTRACTNUM").isNull()));
                if(getMboValue("SLXCONTRACTNUM").isNull())
                {
                	stCuenta = "SLXGLCTAAUXMATER";
                	stCuentaObjeto = "SLXGLCUENTAOBJETOMAT"; // Cuenta Objeto Materiales
                }
                // Materiales contratados.
                else
                {
                	stCuenta = "SLXGLCTAAUXMATERCONT";
                	stCuentaObjeto = "SLXGLCUENTAOBJETOMATCONT"; // Cuenta Objeto Materiales Contratados
                }
*/
                logger.debug("04 CustMRLine.voManejoCuentas: if(!getMboValue(\"DIRECTREQ\").getBoolean())= " + (!getMboValue("DIRECTREQ").getBoolean()));
                if(!getMboValue("DIRECTREQ").getBoolean())
                {
                	// Materiales NO contratados.
                    logger.debug("05 CustMRLine.voManejoCuentas: if(getMboValue(\"SLXCONTRACTNUM\").isNull())= " + (getMboValue("SLXCONTRACTNUM").isNull()));
                    if(getMboValue("SLXCONTRACTNUM").isNull())
                    {
                    	stCuenta = "SLXGLCTAAUXMATER";
                    	stCuentaObjeto = "SLXGLCUENTAOBJETOMAT"; // Cuenta Objeto Materiales
                    }
                    // Materiales contratados.
                    else
                    {
                    	stCuenta = "SLXGLCTAAUXMATERCONT";
                    	stCuentaObjeto = "SLXGLCUENTAOBJETOMATCONT"; // Cuenta Objeto Materiales Contratados
                    }
                }
                // Materiales contratados.
                else
                {
                	stCuenta = "SLXGLCTAAUXMATERCONT";
                	stCuentaObjeto = "SLXGLCUENTAOBJETOMATCONT"; // Cuenta Objeto Materiales Contratados
                }
/*HHT 20190705 ------------------------------------------*/
            }
        }

        logger.debug("06 CustMRLine.voManejoCuentas: if(boContinuar)= " + (boContinuar));
        if (boContinuar)
        {
	        MboRemote refMR = getOwner();
	        MboRemote refWo = CustLlenadoCuentas.getWo(refMR);
	        logger.debug("07 CustMRLine.voManejoCuentas: if(refWo != null)= " + (refWo != null));
	        if(refWo != null)
	        {
	            // Unidade de Negocio.
		        logger.debug("08 CustMRLine.voManejoCuentas: if(!refWo.isNull(\"SLXCENTROACTIVIDAD\")= " + (!refWo.isNull("SLXCENTROACTIVIDAD")));
	        	if (!refWo.isNull("SLXCENTROACTIVIDAD"))
	        	{
	        		String stUnidadNeg = refWo.getString("SLXCENTROACTIVIDAD");
			        // Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
			        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
	        		//CustLlenadoCuentas.setCuentasPlan(this, stUnidadNeg, strCuenta);
	        		CustLlenadoCuentas.setCuentasPlan(this, stUnidadNeg, stCuenta, boMaterial, stCuentaObjeto);
	        	}
	        }
        }
        
        logger.debug("00 CustMRLine.voManejoCuentas: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }

    public void appValidate() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustMRLine.appValidate: INICIO");

        super.appValidate();
        
		MboRemote owner = getOwner();
        logger.debug("01A CustMRLine.appValidate: if(" + (owner != null) + " && (!" + owner.getString("STATUS") + ".equalsIgnoreCase(\"CERRADA\")))= " + ((owner != null) && (!owner.getString("STATUS").equalsIgnoreCase("CERRADA"))));
        if((owner != null) && (!owner.getString("STATUS").equalsIgnoreCase("CERRADA")))
        {
            String internalLineType = getInternalLineType();
            
            boolean boContinuar = false;
            boolean boMaterial  = false;
        	MboSetRemote setCtas = null;
            String stCuentaObjeto = "";
    		String stCuenta       = "";
    		String stFiltro       = "";
    		String stMsg          = "";

            logger.debug("01 CustMRLine.appValidate: if(internalLineType.equalsIgnoreCase(\"SERVICE\") || internalLineType.equalsIgnoreCase(\"STDSERVICE\"))= " + (internalLineType.equalsIgnoreCase("SERVICE") || internalLineType.equalsIgnoreCase("STDSERVICE")));
            if(internalLineType.equalsIgnoreCase("SERVICE") || internalLineType.equalsIgnoreCase("STDSERVICE"))
            {
            	boContinuar = true;
                logger.debug("02 CustMRLine.appValidate: if(getMboValue(\"SLXCONTRACTNUM\").isNull())= " + (getMboValue("SLXCONTRACTNUM").isNull()));
                if (getMboValue("SLXCONTRACTNUM").isNull())
                {
                	stCuenta = "SLXGLCTAAUXSERV";
                	stCuentaObjeto = "SLXGLCUENTAOBJETO";
                }
                else
                {
                	stCuenta = "SLXGLCTAAUXSERVCONT";
                	stCuentaObjeto = "SLXGLCUENTAOBJETOSERVCONT";
                }
            	stFiltro = " and " + stCuenta + " = '" + getString("SLXCAUX") + "' AND " + stCuentaObjeto + " = '" + getString("SLXCOBJETO") + "'";
            	stMsg = "Existe un problema con los datos ingresados (Cuenta Objeto o Cuenta Auxiliar)";
            }
            else
            {
                logger.debug("03 CustMRLine.appValidate: if(internalLineType.equalsIgnoreCase(\"ITEM\") || internalLineType.equalsIgnoreCase(\"MATERIAL\"))= " + (internalLineType.equalsIgnoreCase("ITEM") || internalLineType.equalsIgnoreCase("MATERIAL")));
            	if(internalLineType.equalsIgnoreCase("ITEM") || internalLineType.equalsIgnoreCase("MATERIAL"))
                {
                	boMaterial = true;
                	boContinuar = true;
                    logger.debug("04 CustMRLine.appValidate: if(!getMboValue(\"DIRECTREQ\").getBoolean())= " + (!getMboValue("DIRECTREQ").getBoolean()));
                    if(!getMboValue("DIRECTREQ").getBoolean())
                    {
                        logger.debug("05 CustMRLine.appValidate: if(getMboValue(\"SLXCONTRACTNUM\").isNull())= " + (getMboValue("SLXCONTRACTNUM").isNull()));
                        if(getMboValue("SLXCONTRACTNUM").isNull())
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
                    else
                    {
                    	stCuenta = "SLXGLCTAAUXMATERCONT";
                    	stCuentaObjeto = "SLXGLCUENTAOBJETOMATCONT";
    		        	stFiltro = " and " + stCuenta + " = '" + getString("SLXCAUX") + "' AND " + stCuentaObjeto + " = '" + getString("SLXCOBJETO") + "'";
    		        	stMsg = "Existe un problema con los datos ingresados (Cuenta Objeto o Cuenta Auxiliar)";
                    }
                }
            }
            
        	logger.debug("06 CustMRLine.appValidate: if(isNull(\"STORELOC\"))= " + (isNull("STORELOC")));
        	if(isNull("STORELOC"))
        	{
            	logger.debug("07 CustMRLine.appValidate: if(isNull(\"STORELOCSITE\"))= " + (isNull("STORELOCSITE")));
        		if(isNull("STORELOCSITE"))
        		{
                	logger.debug("08 CustMRLine.appValidate: if(isNull(\"SITEID\"))= " + (isNull("SITEID")));
        			if(isNull("SITEID"))
        				setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getOwner().getString("SITEID") + "'" + stFiltro);
        			else
        				setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("SITEID") + "'" + stFiltro);
        		}
        		else
        			setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and siteid = '" + getString("STORELOCSITE") + "'" + stFiltro);
        	}
        	else
        	{
            	setCtas = getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + getString("SLXUNIDNEGOCIO") + "' and slxalmacen = '" + getString("STORELOC") + "' and siteid = '" + getString("STORELOCSITE") + "'" + stFiltro);
        	}
            
        	logger.debug("10 CustMRLine.appValidate: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
            if(setCtas.isEmpty())
            {
            	Object[] params = { stMsg };
            	throw new MXApplicationException("SLX_SOLI_PAG_CONT", "ErrorInesperado", params);
            }
        }

        logger.debug("00 CustMRLine.appValidate: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
}