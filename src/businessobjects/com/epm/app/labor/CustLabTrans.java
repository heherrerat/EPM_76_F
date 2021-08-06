package com.epm.app.labor;

import com.epm.app.common.CustLlenadoCuentas;

import java.rmi.RemoteException;

import psdi.app.labor.LabTrans;
import psdi.app.labor.LabTransRemote;
import psdi.app.workorder.WORemote;
import psdi.mbo.*;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class CustLabTrans extends LabTrans implements LabTransRemote
{
	private static MXLogger logger;

	public CustLabTrans(MboSet ms) throws MXException, RemoteException
    {
        super(ms);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }
    
    public void add() throws MXException, RemoteException
    {
        logger.info("");
        logger.info("**********************************************************************");
        logger.info("00 CustLabTrans.add: INICIO");

    	super.add();

    	// No se deben procesar registro de Mano de obra que provienen de una integración
    	// ni tampoco mano de obra NO contratada.
       	logger.debug("01 CustLabTrans.add: if(!isNull(\"SENDERSYSID\") || isNull(\"CONTRACTNUM\"))= " + (!isNull("SENDERSYSID") || isNull("CONTRACTNUM")));
    	if ( !isNull("SENDERSYSID") || isNull("CONTRACTNUM") ) 
    		return;
    	
    	MboRemote refWo = getWOot();
       	logger.debug("02 CustLabTrans.add: if(refWo == null)= " + (refWo == null));
        if(refWo == null) 
        	return;
  
		// Unidade de Negocio.
        logger.debug("07 CustLabTrans.add: if(!refWo.isNull(\"SLXCENTROACTIVIDAD\"))= " + (!refWo.isNull("SLXCENTROACTIVIDAD")));
    	if (!refWo.isNull("SLXCENTROACTIVIDAD"))
    	{
    		String stUnidadNeg = refWo.getString("SLXCENTROACTIVIDAD");
    		String stCuenta = "";
    		String stObjetoCuenta = "";

    		// Mano de obra contratada, se usa cuenta de servicios contratados.
	        stCuenta = "SLXGLCTAAUXSERVCONT";
    		//strObjetoCuenta = "SLXGLCUENTAOBJETOSC";
    		stObjetoCuenta = "SLXGLCUENTAOBJETOSERVCONT";
	        
    		// Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
	        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
	        //CustLlenadoCuentas.setCuentasReal(this, stUnidadNeg, strCuenta, strObjetoCuenta);
	        CustLlenadoCuentas.setCuentasReal((MboRemote)this, stUnidadNeg, stCuenta, stObjetoCuenta, false, refWo);
    	}
        logger.info("00 CustLabTrans.add: FINAL");
        logger.info("**********************************************************************");
        logger.info("");
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