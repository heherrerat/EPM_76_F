package com.epm.app.workorder;

import java.rmi.RemoteException;
import psdi.app.workorder.WORemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.mbo.custapp.CustomMbo;
import psdi.mbo.custapp.CustomMboRemote;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

import com.epm.app.common.CustLlenadoCuentas;

public class CustEjecCont extends CustomMbo implements CustomMboRemote
{
	boolean boCargoDirecto = false;
	private static MXLogger logger;
	
    public CustEjecCont(MboSet ms) throws MXException, RemoteException
    {
        super(ms);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

	public void voObtieneGLAccount() throws MXException, RemoteException
    {
        logger.info("");
        logger.info("**********************************************************************");
        logger.info("00 CustEjecCont.voObtieneGLAccount: INICIO");

    	MboRemote refWo            = getWO();
		String    stObjetoCuenta   = "";
		String    stCuenta         = "";
        boolean   boEsPlanificado  = false;
        boolean   boMaterial       = true;
        String    internalLineType = getTranslator().toInternalString("LINETYPE", getString("linetype"));
		String    stUnidadNeg      = refWo.getString("SLXCENTROACTIVIDAD");
        
        String    stWhere          = "";
		
    	logger.info("01 CustEjecCont.voObtieneGLAccount: if(refWo == null)= " + (refWo == null));
        if(refWo != null)
        {
    		stUnidadNeg    = refWo.getString("SLXCENTROACTIVIDAD");

    		logger.info("06 CustEjecCont.voObtieneGLAccount: if(internalLineType.equalsIgnoreCase(\"SERVICE\") || internalLineType.equalsIgnoreCase(\"STDSERVICE\"))= " + (internalLineType.equalsIgnoreCase("SERVICE") || internalLineType.equalsIgnoreCase("STDSERVICE")));
            if(internalLineType.equalsIgnoreCase("SERVICE") || internalLineType.equalsIgnoreCase("STDSERVICE"))
            {
                boEsPlanificado = boEsServicioPlanificado();
        		stCuenta = "SLXGLCTAAUXSERVCONT";
        		boMaterial = false;
        		
                logger.info("07 CustEjecCont.voObtieneGLAccount: if(!isNull(\"CONTRACTNUM\"))= " + (!isNull("CONTRACTNUM")));
        		if(!isNull("CONTRACTNUM"))
        			stObjetoCuenta = "SLXGLCUENTAOBJETOSERVCONT";
        		else
        			stObjetoCuenta = "SLXGLCUENTAOBJETO";
            }
            else
            {
                logger.info("08 CustEjecCont.voObtieneGLAccount: if(internalLineType.equalsIgnoreCase(\"ITEM\") || internalLineType.equalsIgnoreCase(\"MATERIAL\"))= " + (internalLineType.equalsIgnoreCase("ITEM") || internalLineType.equalsIgnoreCase("MATERIAL")));
            	if(internalLineType.equalsIgnoreCase("ITEM") || internalLineType.equalsIgnoreCase("MATERIAL"))
            	{
                    boEsPlanificado = boEsMaterialPlanificado();
            		stCuenta = "SLXGLCTAAUXMATER";
            		
                    logger.info("09 CustEjecCont.voObtieneGLAccount: if(!isNull(\"CONTRACTNUM\"))= " + (!isNull("CONTRACTNUM")));
            		if(!isNull("CONTRACTNUM"))
            			stObjetoCuenta = "SLXGLCUENTAOBJETOMATCONT";
            		else
            			stObjetoCuenta = "SLXGLCUENTAOBJETOMAT";
            	}
            }

            logger.info("10 CustEjecCont.voObtieneGLAccount: if(!boEsPlanificado)= " + (!boEsPlanificado));
            if(!boEsPlanificado)
            	//CustLlenadoCuentas.setCuentasReal(this, stUnidadNeg, stCuenta, stObjetoCuenta, boMaterial);
    	        CustLlenadoCuentas.setCuentasReal((MboRemote)this, stUnidadNeg, stCuenta, stObjetoCuenta, false, refWo);
        }

        logger.info("00 CustEjecCont.voObtieneGLAccount: FINAL");
        logger.info("**********************************************************************");
        logger.info("");
    }
/*    
    public void seteaCuentasReal(String stUnidnegocio, String stCuenta, String stCuentaObjeto, boolean boMat, String stFiltro) throws MXException, RemoteException
    {
        logger.info("");
        logger.info("**********************************************************************");
        logger.info("00 CustLlenadoCuentas.seteaCuentasReal: INICIO stUnidnegocio= " + stUnidnegocio + ", stCuenta= " + stCuenta + ", stCuentaObjeto= " + stCuentaObjeto + ", stFiltro= " + stFiltro);
    	
        MboRemote mboCta = null;
       	MboSetRemote setCtas = null;
        
        if(boMat)
           	setCtas = getMboSet("$slxmapctas", "slxmapctas", "SLXUNIDNEGOCIO = '" + stUnidnegocio + "' and siteid = 'EPM_CORP'" + stFiltro);
        else
        {
           	setCtas = getMboSet("$slxmapctas", "slxmapctas", "SLXUNIDNEGOCIO = '" + stUnidnegocio + "' and siteid = '" + getString("SITEID") + "'" + stFiltro);
            setCtas.setOrderBy("SLXUNIDNEGOCIO, " + stCuentaObjeto + ", " + stCuenta);
        }
        
        logger.info("01 CustLlenadoCuentas.seteaCuentasReal: if(!setCtas.isEmpty())= " + (!setCtas.isEmpty()));
        if(!setCtas.isEmpty())
        {
        	mboCta = setCtas.getMbo(0);
        	String stGL =
        	mboCta.getString("SLXUNIDNEGOCIO") + "." +
        	mboCta.getString(stCuentaObjeto) + "." +
        	mboCta.getString(stCuenta);
        	setValue("glaccount", stGL, 11L);
        }
        setCtas.close();
        
        logger.info("00 CustLlenadoCuentas.seteaCuentasReal: FINAL");
        logger.info("**********************************************************************");
        logger.info("");
    }
*/
    public boolean boEsMaterialPlanificado() throws MXException, RemoteException
    {
        logger.info("");
        logger.info("**********************************************************************");
        logger.info("00 CustEjecCont.boEsMaterialPlanificado: INICIO");
        
    	boolean boExiste = false;
    	String strItemnum = getString("ITEMNUM");
    	String strSiteid = getString("SITEID");
        MboSetRemote myWPMaterialSetRemote = getMboSet("$WPMATERIAL"+strItemnum+strSiteid, "WPMATERIAL", "WONUM = :wonum and ITEMNUM = :itemnum and SITEID = :siteid");
        
        logger.info("01 CustEjecCont.boEsMaterialPlanificado: if(!myWPMaterialSetRemote.isEmpty())= " + (!myWPMaterialSetRemote.isEmpty()));
        if(!myWPMaterialSetRemote.isEmpty())
        {        	
        	boExiste = true;
        	MboRemote wpMat = myWPMaterialSetRemote.getMbo(0);
        	String strGL = wpMat.getString("SLXUNIDNEGOCIO") + "." + wpMat.getString("SLXGLCUENTAOBJETO") + "." + wpMat.getString("SLXCAUX");
        	setValue("glaccount", strGL, 11L);
        	boCargoDirecto = wpMat.getBoolean("directreq");
        }    	
        myWPMaterialSetRemote.close();

        logger.info("00 CustEjecCont.boEsMaterialPlanificado: FINAL");
        logger.info("**********************************************************************");
        logger.info("");
    	return boExiste;
    }
    
    public boolean boEsServicioPlanificado() throws MXException, RemoteException
    {
        logger.info("");
        logger.info("**********************************************************************");
        logger.info("00 CustEjecCont.boEsServicioPlanificado: INICIO");
        
    	boolean boExiste = false;
    	String strItemnum = getString("ITEMNUM");
    	String strSiteid = getString("SITEID");
        MboSetRemote myWPServiceSetRemote = getMboSet("$WPSERVICE"+strItemnum+strSiteid, "WPSERVICE", "WONUM = :wonum and ITEMNUM = :itemnum and SITEID = :siteid");

        logger.info("01 CustEjecCont.boEsServicioPlanificado: if(!myWPServiceSetRemote.isEmpty())= " + (!myWPServiceSetRemote.isEmpty()));
        if(!myWPServiceSetRemote.isEmpty())
        {        	
        	boExiste = true;
        	MboRemote wpServ = myWPServiceSetRemote.getMbo(0);
        	String strGL = wpServ.getString("SLXUNIDNEGOCIO") + "." + wpServ.getString("SLXCOBJETO") + "." + wpServ.getString("SLXCAUX");
        	setValue("glaccount", strGL, 11L);
        }    	
        myWPServiceSetRemote.close();
        
        logger.info("00 CustEjecCont.boEsServicioPlanificado: FINAL");
        logger.info("**********************************************************************");
        logger.info("");
    	return boExiste;
    }

    private MboRemote getWO() throws MXException, RemoteException
    {
        logger.info("");
        logger.info("**********************************************************************");
        logger.info("00 CustEjecCont.getWO: INICIO");

        logger.info("01 CustEjecCont.getWO: if(isNull(\"refwo\"))= " + (isNull("refwo")));
    	if(isNull("refwo")) 
    		return null;
    	
        String wonum = getString("refwo");
        SqlFormat sqf = new SqlFormat(this, "wonum = :1 and siteid = :siteid");
        sqf.setObject(1, "WORKORDER", "wonum", wonum);
        MboRemote woMbo = (WORemote)((MboSet)getThisMboSet()).getSharedMboSet("WORKORDER", sqf.format()).getMbo(0);

        logger.info("02 CustEjecCont.getWO: if(woMbo == null)= " + (woMbo == null));
        if(woMbo == null)
        	woMbo = (WORemote)getOwner();
/*        
        logger.info("02 CustEjecCont.getWO: if(woMbo == null)= " + (woMbo == null));
        if(woMbo == null)
            logger.info("02.01 CustEjecCont.getWO: SE VA A CAER!!");
*/
        logger.info("00 CustEjecCont.getWO: FINAL");
        logger.info("**********************************************************************");
        logger.info("");
    	return woMbo;
    }
}
