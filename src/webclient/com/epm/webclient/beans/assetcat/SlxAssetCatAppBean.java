package com.epm.webclient.beans.assetcat;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.servlet.ServletException;

import psdi.app.assetcatalog.*;
import psdi.mbo.*;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
import psdi.webclient.beans.asset.SelectMoreAssetsMoveBean;
import psdi.webclient.beans.common.ClassificationBean;
import psdi.webclient.beans.servicedesk.SearchBean;
import psdi.webclient.beans.servicedesk.TableBean;
import psdi.webclient.system.beans.AppBean;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.*;
import psdi.webclient.system.session.WebClientSession;
import psdi.webclient.beans.assetcat.*;
import psdi.util.MXApplicationException;
import psdi.util.MXApplicationWarningException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class SlxAssetCatAppBean extends AssetCatAppBean
{
	private static MXLogger logger;

    public SlxAssetCatAppBean()
    {
		logger = MXLoggerFactory.getLogger("maximo.application");
    }
    
	public synchronized int SLXUPDASSETDESC() throws MXException, RemoteException {
		logger.debug("00 SlxAssetCatAppBean.SLXUPDASSETDESC: INICIO");
		
		ClassStructure mboClassStruc = (ClassStructure)this.app.getAppBean().getMbo();
		boolean boFlag = !mboClassStruc.getBoolean("SLXUPDATEASSETDESC");
		mboClassStruc.setValue("SLXUPDATEASSETDESC", boFlag, 11L);
		
		ActualizaJerarquiaAbajo(mboClassStruc, boFlag);
		this.app.getAppBean().save();

		logger.debug("00 SlxAssetCatAppBean.SLXUPDASSETDESC: FIN");
		return 1;
	}
	
	public void ActualizaJerarquiaAbajo(ClassStructure mboClassStruc, boolean boFlag) throws MXException, RemoteException {
		ClassStructureSetRemote mboClassChild = (ClassStructureSetRemote)mboClassStruc.getMboSet("CHILDREN");
        int index = 0;
        for(ClassStructure child = null; (child = (ClassStructure)mboClassChild.getMbo(index)) != null;)
        {
    		logger.debug("00 SlxAssetCatAppBean.ActualizaJerarquiaAbajo. CLASSSTRUCTUREID: " + child.getString("CLASSSTRUCTUREID"));
        	child.setValue("SLXUPDATEASSETDESC", boFlag, 11L);
        	ActualizaJerarquiaAbajo(child, boFlag);
            index++;
        }

	}
}
