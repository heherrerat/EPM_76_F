package com.epm.app.workorder.virtual;

import psdi.app.workorder.virtual.*;

import java.rmi.RemoteException;

import psdi.app.workorder.WO;
import psdi.mbo.*;
import psdi.util.*;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class CustWOGrandTotalSet extends WOGrandTotalSet implements NonPersistentMboSetRemote
{
	private static MXLogger logger;
	
	public CustWOGrandTotalSet(MboServerInterface ms) throws MXException, RemoteException
	{
	     super(ms);
	 	 logger = MXLoggerFactory.getLogger("maximo.application");
	}
	
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException
	{
	    return new CustWOGrandTotal(ms);
	}
	
    public MboRemote setup() throws MXException, RemoteException
    {
        WO ownerWO = (WO)getOwner();
        MboRemote woTotalRemote = null;
        woTotalRemote = super.setup();
        try
        {

	        if(ownerWO != null)
	        {
	        	woTotalRemote = addAtEnd();
	            double doLineCost = 0.0D;
	            MboSetRemote workpack = ownerWO.getMboSet("workpack");
	            workpack.reset();
	
	            logger.debug("setup. OwnerWO: " + ownerWO.getString("WONUM") + " isEmpty()= " + (workpack.isEmpty()));
	            if(!workpack.isEmpty())
	            {
	                WO otHija = null; 
	                //MboRemote otChil = null;
	                MboSetRemote mboSetEjec = null;
	                MboRemote mboEjec = null;
	                
	                /*
	            	for(int h=0; h<workpack.count(); h++)
	            	{
	            		otChil=workpack.getMbo(h);
	            		logger.debug("setup. otHija: " + (otChil!=null));
	            		logger.debug("setup. otHija: " + otChil);
	            	}
	            	*/
	            	
            		logger.debug("setup. Procesa OTs");
	            	for(int i=0; (otHija=(WO)workpack.getMbo(i))!=null; i++)
	            	{
	            		logger.debug("setup. Procesa OTs. Obtiene Lineas de Contratistas");
                    	mboSetEjec = otHija.getMboSet("SLX_EJEC_CONT");
	                    if(!mboSetEjec.isEmpty())
	                    {
		                	for(int j=0; (mboEjec=mboSetEjec.getMbo(j))!=null; j++)
		                		doLineCost += (mboEjec.getDouble("UNITCOST") * mboEjec.getDouble("QUANTITY"));
	                    }
	            	}
	                workpack.close();
	            }
	            
	            // Coste Total Ejecución Contratista
	            woTotalRemote.setValue("total", "Costo para Liquidar", 11L);
	            woTotalRemote.setValueNull("est", 11L);
	            woTotalRemote.setValueNull("estatappr", 11L);
	            woTotalRemote.setValueNull("exceeded", 11L);
	            woTotalRemote.setValue("act", doLineCost, 11L);
	        }
        }
        catch (Exception exc)
        {
        	logger.debug("CustWOGrandTotalSet. Error: " + exc.getMessage());
        }

        return woTotalRemote;
	}
    
	public MboRemote setup2() throws MXException, RemoteException
    {
        WO ownerWO = (WO)getOwner();
        MboRemote woTotalRemote = null;
        woTotalRemote = super.setup();
        if(ownerWO != null)
        {
        	woTotalRemote = addAtEnd();
            double doLineCost = 0.0D;
            MboSetRemote workpack = ownerWO.getMboSet("workpack");
            /*
            if(workpack != null)
            {
                WO WOChild = null;
                for(int i = 0; (WOChild = (WO)workpack.getMbo(i)) != null; i++)
                    doLineCost += WOChild.getMboSet("SLX_EJEC_CONT").sum("LINECOST");

                workpack.close();
            }
            */

            int max = workpack.count();
            logger.debug("CustWOGrandTotalSet. OwnerWO: " + ownerWO.getString("WONUM") + " count= " + max);
            if(max>0)
            {
                logger.debug("CustWOGrandTotalSet. Wworkpack.count(): " + max);
                MboRemote otHija = null; 
                MboSetRemote mboSetEjec = null;
                MboRemote mboEjec = null;
            	for(int i=0; i<max; i++)
            	{
                    logger.debug("CustWOGrandTotalSet. i: " + i);
            		
                    try
                    {
                        otHija=workpack.getMbo(i);
                        
                		logger.debug("CustWOGrandTotalSet. (y): " + i + " OT Nula? " + (otHija==null));

                        logger.debug("CustWOGrandTotalSet. WOChild: " + otHija.getString("siteid"));
                        otHija.getMboSet("SLX_EJEC_CONT");
	            		//mboSetEjec = ownerWO.getMboSet("$SLX_EJEC_CONT"+i, "SLX_EJEC_CONT", "refwo='" + mboSetEjec.getMbo(i).getString("wonum") + "' and siteid='" + mboSetEjec.getMbo(i).getString("siteid") + "'");
	                    logger.debug("CustWOGrandTotalSet. mboSetEjec.count(): " + mboSetEjec.count());
	                    if(!mboSetEjec.isEmpty())
	                    {
	                        logger.debug("CustWOGrandTotalSet. Procesa Lineas");
		                	for(int j=0; (mboEjec=mboSetEjec.getMbo(j))!=null; j++)
		                		doLineCost += (mboEjec.getDouble("UNITCOST") * mboEjec.getDouble("QUANTITY"));
	                    }
                    }
                    catch (Exception exc)
	                {
                    	logger.debug("CustWOGrandTotalSet. Error: " + exc.getMessage());
	                }

            	}
                workpack.close();
            }
            
            // Coste Total Ejecución Contratista
            woTotalRemote.setValue("total", "Costo para Liquidar", 11L);
            woTotalRemote.setValueNull("est", 11L);
            woTotalRemote.setValueNull("estatappr", 11L);
            woTotalRemote.setValueNull("exceeded", 11L);
            woTotalRemote.setValue("act", doLineCost, 11L);
        }
        return woTotalRemote;
    }
}
