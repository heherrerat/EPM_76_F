package com.epm.app.workorder;

import java.rmi.RemoteException;

import psdi.app.workorder.WO;
import psdi.app.workorder.WPMaterial;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.plusd.app.workorder.PlusDFldWpMatLocation;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

import com.epm.app.common.CustLlenadoCuentas;
//psdi.plusd.app.workorder.PlusDFldWpMatLocation
//com.epm.app.workorder.CustFldWpMatLocation
public class CustFldWpMatLocation extends PlusDFldWpMatLocation
{
	private static MXLogger logger;
    public CustFldWpMatLocation(MboValue mbv) throws MXException
    {
        super(mbv);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

    public void action() throws MXException, RemoteException
    {
        logger.info("");
        logger.info("**********************************************************************");
        logger.info("00 CustFldWpMatLocation.action: INICIO");

        super.action();
        
        WPMaterial wpMaterial = (WPMaterial)getMboValue().getMbo();
        WO wo = (WO)wpMaterial.getOwner();

        logger.info("01 CustFldWpMatLocation.action: if(!getMboValue().isNull())= " + (!getMboValue().isNull()));
        if(!getMboValue().isNull())
        {
        	MboSetRemote locationsSet = getMboValue().getMbo().getMboSet("$locations","locations","location = :location");
    		MboRemote locations = null;

    		logger.info("02 CustFldWpMatLocation.action: if(!locationsSet.isEmpty())= " + (!locationsSet.isEmpty()));
        	if(!locationsSet.isEmpty())
        	{
        		locations = locationsSet.getMbo(0);

        		//logger.info("00.03 CustFldWpMatLocation.action: if(wpMaterial.isNull(\"storelocsite\"))= " + (wpMaterial.isNull("storelocsite")));
        		//if(wpMaterial.isNull("storelocsite"))
            	wpMaterial.setValue("storelocsite", locations.getString("siteid"));
        	}
        	locationsSet.close();

            logger.info("03 CustFldWpMatLocation.action: if(wo == null)= " + (wo == null));
            if(wo == null)
    			wo = (WO)getMboValue().getMbo().getMboSet("$workorder", "workorder", "wonum = :wonum and siteid = :siteid").getMbo(0);

            boolean boMaterial = true;
    		String stUnidadNegOT = "";
    		String stUnidadNegWP = "";
    		String stUnidadNeg = "";
    		String stCuenta = "";
    		String stObjetoCuenta = "";

    		// Unidade de Negocio.
            logger.info("04 CustFldWpMatLocation.action: if(!wo.isNull(\"SLXCENTROACTIVIDAD\") || !wpMaterial.isNull(\"SLXUNIDNEGOCIO\"))= " + (!wo.isNull("SLXCENTROACTIVIDAD") || !wpMaterial.isNull("SLXUNIDNEGOCIO")));
        	if(!wo.isNull("SLXCENTROACTIVIDAD") || !wpMaterial.isNull("SLXUNIDNEGOCIO"))
        	{

                logger.info("05 CustFldWpMatLocation.action: if(!wo.isNull(\"SLXCENTROACTIVIDAD\"))= " + (!wo.isNull("SLXCENTROACTIVIDAD")));
        		if(!wo.isNull("SLXCENTROACTIVIDAD")) 
        			stUnidadNegOT = wo.getString("SLXCENTROACTIVIDAD");

                logger.info("06 CustFldWpMatLocation.action: if(!wo.isNull(\"SLXUNIDNEGOCIO\"))= " + (!wo.isNull("SLXUNIDNEGOCIO")));
        		if(!wpMaterial.isNull("SLXUNIDNEGOCIO")) 
        			stUnidadNegWP = wpMaterial.getString("SLXUNIDNEGOCIO");
        		stUnidadNeg = stUnidadNegOT;

                logger.info("07 CustFldWpMatLocation.action: if(stUnidadNegOT != stUnidadNegWP)= " + (stUnidadNegOT != stUnidadNegWP));
        		if(stUnidadNegOT != stUnidadNegWP) 
        			stUnidadNeg = stUnidadNegWP;

        		// Materiales NO contratados.
                logger.debug("08 CustFldWpMatLocation.action: if(wpMaterialget.Int(\"DIRECTREQ\") == 0)= " + (wpMaterial.getInt("DIRECTREQ") == 0));
    	        if (wpMaterial.getInt("DIRECTREQ") == 0)
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
    	        
                logger.info("09 CustFldWpMatLocation.action: if(stUnidadNeg.length()!=0 && !wpMaterial.isNull(\"ITEMNUM\"))= " + (stUnidadNeg.length()!=0 && !wpMaterial.isNull("ITEMNUM")));
    	        if(stUnidadNeg.length()!=0 && !wpMaterial.isNull("ITEMNUM"))
    	        {
                    logger.info("09.01 CustFldWpMatLocation.action: if(!wo.getMboValue(\"JPNUM\").isNull())= " + (!wo.getMboValue("JPNUM").isNull()));
    	        	if(!wo.getMboValue("JPNUM").isNull())
    	        	{
    	        		MboSetRemote jobplanSet = wo.getMboSet("$jobplan", "JOBPLAN", "jpnum = '" + wo.getMboValue("JPNUM") + "'");
                        logger.info("09.02 CustFldWpMatLocation.action: if(!jobplanSet.isEmpty())= " + (!jobplanSet.isEmpty()));
    	        		if(!jobplanSet.isEmpty())
    	        		{
        	        		jobplanSet.setOrderBy("PLUSCREVNUM desc");
        	        		jobplanSet.reset();
        	        		MboRemote jobplan = jobplanSet.getMbo(0);
        	        		String stJPUnidadNegocio = jobplan.getString("SLXCENTROACTIVIDAD");
                            logger.info("09.03 CustFldWpMatLocation.action: if((stJPUnidadNegocio.length() > 0) && (!stUnidadNeg.equalsIgnoreCase(stJPUnidadNegocio)))= " + ((stJPUnidadNegocio.length() > 0) && (!stUnidadNeg.equalsIgnoreCase(stJPUnidadNegocio))));
        	        		if((stJPUnidadNegocio.length() > 0) && (!stUnidadNeg.equalsIgnoreCase(stJPUnidadNegocio)))
        	        			stUnidadNeg = stJPUnidadNegocio;
    	        		}
    	        	}

    	        	MboSetRemote setCtas = wpMaterial.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidadNeg + "' and slxalmacen = '" + getMboValue().getString() + "'");
    				
    		        logger.info("10 CustFldWpMatLocation.action: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
    				if(setCtas.isEmpty())
    					throw new MXApplicationException("slxplusdwotrk", "ErrorUNAlm");

    				// Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
    		        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
    		        CustLlenadoCuentas.setCuentasPlan(wpMaterial, stUnidadNeg, stCuenta, boMaterial, stObjetoCuenta);
    	        }
        	}
        }
/*
        logger.info("01 CustFldWpMatLocation.action: if(wo == null)= " + (wo == null));
        if(wo == null)
			wo = (WO)getMboValue().getMbo().getMboSet("$workorder", "workorder", "wonum = :wonum and siteid = :siteid").getMbo(0);

        boolean boMaterial = true;
		String stUnidadNegOT = "";
		String stUnidadNegWP = "";
		String stUnidadNeg = "";
		String stCuenta = "";
		String stObjetoCuenta = "";

		// Unidade de Negocio.
        logger.info("02 CustFldWpMatLocation.action: if(!wo.isNull(\"SLXCENTROACTIVIDAD\") || !wpMaterial.isNull(\"SLXUNIDNEGOCIO\"))= " + (!wo.isNull("SLXCENTROACTIVIDAD") || !wpMaterial.isNull("SLXUNIDNEGOCIO")));
    	if(!wo.isNull("SLXCENTROACTIVIDAD") || !wpMaterial.isNull("SLXUNIDNEGOCIO"))
    	{

            logger.info("03 CustFldWpMatLocation.action: if(!wo.isNull(\"SLXCENTROACTIVIDAD\"))= " + (!wo.isNull("SLXCENTROACTIVIDAD")));
    		if(!wo.isNull("SLXCENTROACTIVIDAD")) 
    			stUnidadNegOT = wo.getString("SLXCENTROACTIVIDAD");

            logger.info("04 CustFldWpMatLocation.action: if(!wo.isNull(\"SLXUNIDNEGOCIO\"))= " + (!wo.isNull("SLXUNIDNEGOCIO")));
    		if(!wpMaterial.isNull("SLXUNIDNEGOCIO")) 
    			stUnidadNegWP = wpMaterial.getString("SLXUNIDNEGOCIO");
    		stUnidadNeg = stUnidadNegOT;

            logger.info("05 CustFldWpMatLocation.action: if(stUnidadNegOT != stUnidadNegWP)= " + (stUnidadNegOT != stUnidadNegWP));
    		if(stUnidadNegOT != stUnidadNegWP) 
    			stUnidadNeg = stUnidadNegWP;

    		// Materiales NO contratados.
            //logger.info("06 CustFldWpMatLocation.action: if(wpMaterial.getMboValue(\"SLXCONTRACTNUM\").isNull())= " + (wpMaterial.getMboValue("SLXCONTRACTNUM").isNull()));
	        //if(wpMaterial.getMboValue("SLXCONTRACTNUM").isNull())
            logger.debug("06 CustFldWpMatLocation.action: if(wpMaterialget.Int(\"DIRECTREQ\") == 0)= " + (wpMaterial.getInt("DIRECTREQ") == 0));
	        if (wpMaterial.getInt("DIRECTREQ") == 0)
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
	        
            //logger.info("07 CustFldWpMatLocation.action: if(stUnidadNeg.length()!=0)= " + (stUnidadNeg.length()!=0));
	        //if(stUnidadNeg.length()!=0)
            logger.info("07 CustFldWpMatLocation.action: if(stUnidadNeg.length()!=0 && !wpMaterial.isNull(\"ITEMNUM\"))= " + (stUnidadNeg.length()!=0 && !wpMaterial.isNull("ITEMNUM")));
	        if(stUnidadNeg.length()!=0 && !wpMaterial.isNull("ITEMNUM"))
	        {
				//MboSetRemote setCtas = wpMaterial.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidadNeg + "' and slxalmacen = '" + getMboValue().getString() + "'");
				//MboSetRemote setCtas = wpMaterial.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidadNeg + "' and slxalmacen = :location");
				MboSetRemote setCtas = wpMaterial.getMboSet("$slxmapctas", "slxmapctas", "slxunidnegocio = '" + stUnidadNeg + "' and slxalmacen = '" + wpMaterial.isNull("LOCATION") + "'");
				
		        logger.info("08 CustFldWpMatLocation.action: if(setCtas.isEmpty())= " + (setCtas.isEmpty()));
				if(setCtas.isEmpty())
					throw new MXApplicationException("slxplusdwotrk", "ErrorUNAlm");

				// Llamar a método que busque el mapeo de la cuenta y llene los campos en común.
		        // Pasar MBO, Unidad de Negocio y campo de cuenta a obtener.
		        CustLlenadoCuentas.setCuentasPlan(wpMaterial, stUnidadNeg, stCuenta, boMaterial, stObjetoCuenta);
	        }
    	}
*/
    	logger.info("00 CustFldWpMatLocation.action: FINAL");
        logger.info("**********************************************************************");
        logger.info("");
    }
}