package com.epm.app.workorder;

import java.rmi.RemoteException;

import psdi.app.workorder.WO;
import psdi.app.workorder.WPService;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.SqlFormat;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

import com.epm.app.common.CustLlenadoCuentas;
//com.epm.app.workorder.CustFldWPSrvSlxContractNum
public class CustFldWPSrvSlxContractNum extends MAXTableDomain
//public class CustFldWPSrvSlxContractNum extends MboValueAdapter
{
	private static MXLogger logger;

	public CustFldWPSrvSlxContractNum(MboValue mbv)
    {
        super(mbv);
        logger = MXLoggerFactory.getLogger("maximo.application");
        /**/       
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldWPSrvSlxContractNum.CustFldWPSrvSlxContractNum: INICIO");

        setRelationship("CONTRACT", "");
        String map1[] = {"slxcontractnum"};
        String map2[] = {"contractnum"};
        setLookupKeyMapInOrder(map1, map2);

        logger.debug("00 CustFldWPSrvSlxContractNum.CustFldWPSrvSlxContractNum: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
        /**/
    }

    public MboSetRemote getList() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldWPSrvSlxContractNum.getList: INICIO");

        Mbo wpService = getMboValue().getMbo();
        String str = "(((status = 'APROBADO' or status = 'EXPIRADO') and contractnum in (select a.contractnum from CONTRACTAUTH a where '" + wpService.getString("siteid") + "' = a.AUTHSITEID)) and (vendor = '" + wpService.getString("vendor") + "' or '" + wpService.getString("vendor") + "' is null)) and ('" + wpService.getString("itemnum") + "' is null or contractnum in (select b.contractnum from CONTRACTLINE b where '" + wpService.getString("itemnum") + "' = b.itemnum and '" + wpService.getString("itemsetid") + "' = b.itemsetid))";
        logger.debug("00.01 CustFldWPSrvSlxContractNum.getList: str = " + str);
        SqlFormat sqlf = new SqlFormat(wpService.getUserInfo(), str);
        MboSetRemote mboSet = wpService.getMboSet("$LOOKUPCONTRACT", "CONTRACT", sqlf.format());

        logger.debug("00 CustFldWPSrvSlxContractNum.getList: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
        
        return mboSet;
    }

    public void action() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldWPSrvSlxContractNum.action: INICIO");

    	super.action();
    	
    	WPService wpService = (WPService)getMboValue().getMbo();
        WO wo = (WO)wpService.getOwner();

    	boolean boMaterial = false;

        // Unidad de Negocio.
        logger.debug("01 CustFldWPSrvSlxContractNum.action: if(!wo.isNull(\"SLXCENTROACTIVIDAD\") || !wpService.isNull(\"SLXUNIDNEGOCIO\"))= " + (!wo.isNull("SLXCENTROACTIVIDAD") || !wpService.isNull("SLXUNIDNEGOCIO")));
    	if (!wo.isNull("SLXCENTROACTIVIDAD") || !wpService.isNull("SLXUNIDNEGOCIO"))
    	{
    		String stUnidadNegOT = "";

            logger.debug("02 CustFldWPSrvSlxContractNum.action: if(!wo.isNull(\"SLXCENTROACTIVIDAD\"))= " + (!wo.isNull("SLXCENTROACTIVIDAD")));
            if(!wo.isNull("SLXCENTROACTIVIDAD")) 
            	stUnidadNegOT = wo.getString("SLXCENTROACTIVIDAD");
    		String stUnidadNegWP = "";

            logger.debug("03 CustFldWPSrvSlxContractNum.action: if(!wo.isNull(\"SLXUNIDNEGOCIO\"))= " + (!wo.isNull("SLXUNIDNEGOCIO")));
    		if(!wpService.isNull("SLXUNIDNEGOCIO")) 
    			stUnidadNegWP = wpService.getString("SLXUNIDNEGOCIO");
    		String stUnidadNeg = stUnidadNegOT;

            logger.debug("04 CustFldWPSrvSlxContractNum.action: if(stUnidadNegOT != stUnidadNegWP)= " + (stUnidadNegOT != stUnidadNegWP));
            if (stUnidadNegOT != stUnidadNegWP) 
            	stUnidadNeg = stUnidadNegWP;

    		String strCuenta = "";
    		String strObjetoCuenta = "";
    		
    		// Servicios NO contratados.
            logger.debug("05 CustFldWPSrvSlxContractNum.action: if(getMboValue().isNull())= " + (getMboValue().isNull()));
	        if (getMboValue().isNull())
	        {
	        	strCuenta = "SLXGLCTAAUXSERV";
	    		strObjetoCuenta = "SLXGLCUENTAOBJETO"; // Cuenta Objeto Servicios NO Contratados.
	    	}
	        // Servicios contratados.
	        else
	        {
	        	strCuenta = "SLXGLCTAAUXSERVCONT";
	    		strObjetoCuenta = "SLXGLCUENTAOBJETOSERVCONT"; // Cuenta Objeto Servicios Contratados.
	        }

	        // Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
	        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
            logger.debug("06 CustFldWPSrvSlxContractNum.action: if(stUnidadNeg.length()!=0)= " + (stUnidadNeg.length()!=0));
	        if(stUnidadNeg.length()!=0)
	        	CustLlenadoCuentas.setCuentasPlan(wpService, stUnidadNeg, strCuenta, boMaterial, strObjetoCuenta);
    	}
        logger.debug("00 CustFldWPSrvSlxContractNum.action: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
}

