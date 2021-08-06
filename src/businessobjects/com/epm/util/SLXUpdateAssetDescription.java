package com.epm.util;

import java.rmi.RemoteException;

import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
import psdi.app.assetcatalog.ClassStructure;
import psdi.mbo.MboSetRemote;
import psdi.app.asset.*;

public class SLXUpdateAssetDescription extends SimpleCronTask
{

    public SLXUpdateAssetDescription()
    {
        initialized = false;
        myLogger = MXLoggerFactory.getLogger("maximo.application");
    }

    public void init() throws MXException 
    {
        try
        {
            super.init();
        }
        catch(Exception e)
        {
        	myLogger.debug("SLXUpdateAssetDescription failed to init()");
            e.printStackTrace();
            return;
        }
        initialized = true;
    }

    public void cronAction()
    {
        if(!initialized)
        {
        	myLogger.debug("SLXUpdateAssetDescription Not initialized cronAction()");
            return;
        }
        try
        {
        	MXServer mxs = MXServer.getMXServer();
        	myLogger.debug("SLXUpdateAssetDescription cronAction() 01");
            UserInfo ui = this.getRunasUserInfo();
        	myLogger.debug("SLXUpdateAssetDescription cronAction() 02");
            MboSetRemote MboSetClass = mxs.getMboSet("CLASSSTRUCTURE", ui);
        	myLogger.debug("SLXUpdateAssetDescription cronAction() 03");
            MboSetClass.setWhere("SLXUPDATEASSETDESC=1");
        	myLogger.debug("SLXUpdateAssetDescription cronAction() 04");
            MboSetClass.reset();
        	myLogger.debug("SLXUpdateAssetDescription cronAction() 05");
            
            int classindex = 0;
            int assetindex = 0;
            int intotal = 0;
            String stClass="";
            //double stRowstamp=0.0D;
            //int inAumenta=0;
            //double loID = 0;
            for(ClassStructure child = null; (child = (ClassStructure)MboSetClass.getMbo(classindex)) != null;)
            {
            	stClass=child.getString("CLASSSTRUCTUREID");
            	myLogger.debug("SLXUpdateAssetDescription cronAction() 06. Procesando Class: " + stClass);
                
            	AssetSetRemote MboSetAsset = (AssetSetRemote)child.getMboSet("$ASSET"+stClass, "ASSET", "CLASSSTRUCTUREID='" + stClass + "'");
            	//loID = MboSetAsset.min("ASSETID");
                for(Asset mboAsset = null; (mboAsset = (Asset)MboSetAsset.getMbo(assetindex)) != null;)
                {
                	AssetSetRemote MboSetOneAsset = (AssetSetRemote)mboAsset.getMboSet("$ASSET"+stClass+assetindex, "ASSET", "ASSETID="+ mboAsset.getLong("ASSETID"));
                	myLogger.debug("SLXUpdateAssetDescription cronAction() 07. Procesando Asset: " + MboSetOneAsset.getMbo(0).getString("Assetnum"));
                	((Asset)MboSetOneAsset.getMbo(0)).updateDesc();
                	MboSetOneAsset.save();
                	assetindex++;
                } 
            	
            	/*/
            	intotal = MboSetAsset.count();
            	stRowstamp=MboSetAsset.max("ROWSTAMP");
            	MboSetAsset.close();
            	inAumenta=0;
            	while (intotal > inAumenta)
            	{
	            	process(child,stClass,stRowstamp,inAumenta);
	            	inAumenta += 3000;
            	}
            	*/
            	/*
            	AssetSetRemote MboSetAsset = (AssetSetRemote)child.getMboSet("$ASSET"+stClass, "ASSET", "CLASSSTRUCTUREID='" + stClass + "'");
                assetindex=0;
                for(Asset mboAsset = null; (mboAsset = (Asset)MboSetAsset.getMbo(assetindex)) != null;)
                {
                	myLogger.debug("SLXUpdateAssetDescription cronAction() 07. Procesando Asset: " + mboAsset.getString("Assetnum"));
                	mboAsset.updateDesc();
                	assetindex++;
                } 
                */
            	
            	child.setValue("SLXUPDATEASSETDESC", false, 11L);
            	classindex++;
            }

        	myLogger.debug("SLXUpdateAssetDescription cronAction() 08");
            MboSetClass.save();
            MboSetClass.close();
        }
        catch(Exception e)
        {
        	myLogger.debug("SLXUpdateAssetDescription cronAction() Error: " + e.getMessage());
        }
    }

    //public void process(ClassStructure child, String stClass, double stRowstamp, int contador) throws MXException, RemoteException
    public void process(ClassStructure child, String stClass, double loID, int contador) throws MXException, RemoteException
    {
    	int assetindex = 0;
    	int count = 0;
		AssetSetRemote MboSetAsset = (AssetSetRemote)child.getMboSet("$ASSET"+stClass+contador, "ASSET", "CLASSSTRUCTUREID='" + stClass + "' and ASSETID > " + loID + " and rownum < 3001");
	    //assetindex=0;
	    for(Asset mboAsset = null; (mboAsset = (Asset)MboSetAsset.getMbo(assetindex)) != null;)
	    {
	    	if (count == 3000)
	    	{		
            	loID = MboSetAsset.max("ASSETID");
	    		process(child, stClass, loID, count);
	    	}
	    	mboAsset.updateDesc();
	    	assetindex++;
	    	count++;
	    }
    	myLogger.debug("SLXUpdateAssetDescription cronAction() 07. process total: " + count + " ID Contador: " + contador);
	    MboSetAsset.save();
	}
    
    private boolean initialized;
    MXLogger myLogger;
}
