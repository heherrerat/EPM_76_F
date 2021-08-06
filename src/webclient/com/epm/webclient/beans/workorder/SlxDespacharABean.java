package com.epm.webclient.beans.workorder;

import java.rmi.RemoteException;






import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
//import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXApplicationWarningException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.Utility;
import psdi.webclient.system.controller.WebClientEvent;
/*
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.*;
import psdi.webclient.beans.common.ChangeStatusBean;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.beans.ResultsBean;
import psdi.webclient.system.controller.*;
import psdi.webclient.system.session.WebClientSession;
*/
import psdi.app.workorder.*;
import psdi.app.workorder.virtual.*;

public class SlxDespacharABean extends DataBean
{
	private static MXLogger logger;
	
	public SlxDespacharABean()
	{
		logger = MXLoggerFactory.getLogger("maximo.application");
	}
	
	@Override
	protected void initialize() throws MXException, RemoteException {
		super.initialize();
		
		logger.debug("00 SlxDespacharABean.initialize: INICIO");
		MboRemote MboBase = this.app.getAppBean().getMbo();
		/*
		logger.debug("00 SlxDespacharABean.initialize: wonum: " + MboBase.getString("wonum"));
		logger.debug("00 SlxDespacharABean.initialize: siteid: " + MboBase.getString("siteid"));
		logger.debug("00 SlxDespacharABean.initialize: getMboName: " + this.app.getAppBean().getMboName());
		logger.debug("00 SlxDespacharABean.initialize: getName: " + this.app.getAppBean().getMbo().getName());
		*/
		
		setValue("wonum", MboBase.getString("wonum"), 11L);	
		setValue("siteid", MboBase.getString("siteid"), 11L);	
		logger.debug("00 SlxDespacharABean.initialize: FIN");
	}
	
	public synchronized int despacharMat() throws MXException, RemoteException {
		logger.debug("00 SlxDespacharABean.despacharMat: INICIO");

		String stDestino = getString("DESPACHARADESTINO");
		//String stOrigen = getString("DESPACHARAORIGEN");
		boolean boInclNull = getBoolean("INCLNULOS");
		/*if (stOrigen.length()<1)
		{
			Object params[] = {"Debe ingresar Despachar A de Origen."};
    		throw new MXApplicationException("slxgenerico", "informacion", params);
		}
		*/
		if (stDestino.length()<1)
		{
			Object params[] = {"Debe ingresar un valor para Reemplazar 'Despachar A' de destino."};
    		throw new MXApplicationException("slxgenerico", "informacion", params);
		}

		// SHOWPLANMATERIAL
		WORemote WORemote = (WORemote)this.app.getAppBean().getMbo();

		ShowPlanMaterialSet mboSetWPMat = (ShowPlanMaterialSet)WORemote.getMboSet("SHOWPLANMATERIAL");
		try
		{
			//mboSetWPMat.setWhere("ISSUETO = '" + getString("DESPACHARAORIGEN") + "'");
			//mboSetWPMat.reset();
			//logger.debug("00 SlxDespacharABean.despacharMat: where: " + mboSetWPMat.getWhere());
			logger.debug("00 SlxDespacharABean.despacharMat: where: " + mboSetWPMat.getCompleteWhere());
			
	        int i = 0;
	        int iTot = 0;
	        MboRemote line;
	        boolean boLinNulo = false;
			if (!mboSetWPMat.isEmpty())
			{
				String despachara="";
				while ((line = mboSetWPMat.getMbo(i++)) != null)
				{		
					despachara="";
					boLinNulo = false;
					if (!line.isNull("ISSUETO"))
						despachara=line.getString("ISSUETO");
					else
						boLinNulo = true;
					
					if (despachara.equals(getString("DESPACHARAORIGEN")) || (boInclNull && boLinNulo) )
					{
						line.setValue("ISSUETO", getString("DESPACHARADESTINO"));
						iTot++;
					}
				}

				if (iTot>0) this.app.getAppBean().save();
				Object params[] = {
                        "Se han actualizado " + iTot + " registros de material."
                    };
				((WOSet)this.app.getAppBean().getMboSet()).addWarning(new MXApplicationWarningException("slxgenerico", "informacion", params));
				//mboSetWPMat.save();
			}
			//mboSetWPMat.setValue("ISSUETO", getString("DESPACHARADESTINO"));
			//("ISSUETO", getString("DESPACHARADESTINO"), where);
	    }
		catch (MXException e)
		{
			logger.debug("***************************rollback()");
			mboSetWPMat.rollback();
			logger.debug("***************************cleanup()");
			mboSetWPMat.cleanup();
			logger.debug("***************************FIN despacharMat. rollback() cleanup()");
			Object errorMsje[] = { e.getMessage() };
			throw new MXApplicationException("slxgenerico", "informacion", errorMsje);
	    }
		
		//save();
		Utility.sendEvent(new WebClientEvent("dialogclose", app.getCurrentPageId(), null, sessionContext));
		
		return 1;
	}

	public synchronized int despacharJde() throws MXException, RemoteException {
		logger.debug("00 SlxDespacharABean.despacharJde: INICIO");

		String stDestino = getString("DESPACHARADESTINO");
		String strOTJDE = getString("OTJDE");
		if (strOTJDE.length()<1)
		{
			Object params[] = {"Debe ingresar OT JDE."};
    		throw new MXApplicationException("slxgenerico", "informacion", params);
		}
		if (stDestino.length()<1)
		{
			Object params[] = {"Debe ingresar un valor para Reemplazar 'Despachar A'."};
    		throw new MXApplicationException("slxgenerico", "informacion", params);
		}

		WORemote WORemote = (WORemote)this.app.getAppBean().getMbo();
		String strWonum = WORemote.getString("wonum");	

		
		MboSetRemote mboSetDespacharJDE = WORemote.getMboSet("SLXISSUETOJDE");
		try
		{
			logger.debug("00 SlxDespacharABean.despacharJde: where: " + mboSetDespacharJDE.getCompleteWhere());
			
	        MboRemote line = mboSetDespacharJDE.add(2L);
	        line.setValue("WONUM", strWonum);
	        line.setValue("ORDERNUMBER", strOTJDE);
	        line.setValue("DESPACHARADESTINO", getString("DESPACHARADESTINO"));

	        llenaMRDespacharAorigen(line, strOTJDE);
	        
	        
	        // Aquí, se debe llamar al WebService para alterar el campo Despachar A en la OT de JDE.
	        // Aquí, se debe llamar al WebService para alterar el campo Despachar A en la OT de JDE.
	        
	        
	        this.app.getAppBean().save();

			 Object params[] = {
                    "Se ha enviado la actualización para la OT JDE."
                };
			((WOSet)this.app.getAppBean().getMboSet()).addWarning(new MXApplicationWarningException("slxgenerico", "informacion", params));
	    }
		catch (MXException e)
		{
			logger.debug("***************************rollback()");
			mboSetDespacharJDE.rollback();
			logger.debug("***************************cleanup()");
			mboSetDespacharJDE.cleanup();
			logger.debug("***************************FIN despacharMat. rollback() cleanup()");
			Object errorMsje[] = { e.getMessage() };
			throw new MXApplicationException("slxgenerico", "informacion", errorMsje);
	    }
		
		Utility.sendEvent(new WebClientEvent("dialogclose", app.getCurrentPageId(), null, sessionContext));
		
		return 1;
	}
	
    public void llenaMRDespacharAorigen(MboRemote line, String strOTJDE) throws MXException, RemoteException {
    	
    	String strTabla = getString("OWNERTABLE");
    	if (strTabla.equalsIgnoreCase("MRLINE"))
    	{
    		MboRemote mrline = this.getMbo().getMboSet("MRLINE").getMbo(0);
    		if ( mrline != null )
    		{
    			line.setValue("MRNUM", mrline.getString("MRNUM"));
        		line.setValue("DESPACHARAORIGEN", mrline.getString("SLXISSUETO"));
    		}
    	}
    	else if (strTabla.equalsIgnoreCase("WPITEM"))
    	{
    		MboRemote mrline = this.getMbo().getMboSet("WPMATERIAL").getMbo(0);
    		if ( mrline != null )
        		line.setValue("DESPACHARAORIGEN", mrline.getString("ISSUETO"));
    	}
    }
}
