package com.epm.webclient.beans.SlxSoliPagCont;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
import psdi.webclient.system.beans.DataBean;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.*;
import psdi.webclient.beans.common.ChangeStatusBean;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.beans.ResultsBean;
import psdi.webclient.system.controller.*;
import psdi.webclient.system.session.WebClientSession;

public class CustSlxSoliPagContAppBean extends DataBean
{
	private static MXLogger logger;
	public CustSlxSoliPagContAppBean(){
		logger = MXLoggerFactory.getLogger("maximo.application");
	}
	
	@Override
	protected void initialize() throws MXException, RemoteException {
		logger.debug("************************************************************");
		logger.debug("00 CustSlxSoliPagContAppBean.initialize: INICIO");
		MboRemote MboBase = this.app.getAppBean().getMbo();
		if (MboBase.getString("NUMACTA").isEmpty()||MboBase.getString("FECHAEJECUCION").isEmpty()
				||MboBase.getString("DESCRIPTION").isEmpty()||MboBase.getString("SLXTIPOSERVICIO").isEmpty())  
		{
			throw new MXApplicationException("SLX_SOLI_PAG_CONT", "nullrequired");
		}
		logger.debug("00 CustSlxSoliPagContAppBean.initialize: FIN");
		logger.debug("************************************************************");
		super.initialize();
	}
	
	//@Override
	public synchronized int executeOriginal() throws MXException, RemoteException {
		logger.debug("************************************************************");
		logger.debug("00 CustSlxSoliPagContAppBean.execute: INICIO");
		Vector vecMboSlxEjecContSet =  this.getMboSet().getSelection();
		if (vecMboSlxEjecContSet.isEmpty())
		{
			throw new MXApplicationException("SLX_SOLI_PAG_CONT", "noselect");
		}
		else
		{
			for (int i = 0; i<vecMboSlxEjecContSet.size(); i++)
			{	
				logger.debug("00 CustPlusdwotrkAppBean.execute: SE RECORRE LOS REGISTROS SELECCIONADOS");
				MboRemote MboSlxEjecCont = (MboRemote) vecMboSlxEjecContSet.get(i);
				MboSetRemote MboSlxSoliPagContDetSet = this.app.getAppBean().getMbo().getMboSet("SLX_SOLI_PAG_CONT_DET");
				MboRemote MboSlxSoliPagContDet = MboSlxSoliPagContDetSet.add(2L);
				logger.debug("00 CustPlusdwotrkAppBean.execute: SE GUARDA REGISTRO SLX_SOLI_PAG_CONT_DET");
				MboSlxSoliPagContDet.setValue("linetype", MboSlxEjecCont.getString("linetype"),2L);	
				MboSlxSoliPagContDet.setValue("siteid", MboSlxEjecCont.getString("siteid"),2L);
				MboSlxSoliPagContDet.setValue("itemnum", MboSlxEjecCont.getString("itemnum"),11L);
				MboSlxSoliPagContDet.setValue("description", MboSlxEjecCont.getString("description2"),2L);								
				MboSlxSoliPagContDet.setValue("itemsetid", MboSlxEjecCont.getString("itemsetid"),2L);
				MboSlxSoliPagContDet.setValue("linecost",0,2L);
				MboSlxSoliPagContDet.setValue("wonum", MboSlxEjecCont.getString("wonum"),2L);
				MboSlxSoliPagContDet.setValue("refwo", MboSlxEjecCont.getString("refwo"),2L);
				MboSlxSoliPagContDet.setValue("taskid", MboSlxEjecCont.getString("taskid"),2L);
				MboSlxSoliPagContDet.setValue("requestby", MboSlxEjecCont.getString("requestby"),2L);
				MboSlxSoliPagContDet.setValue("laborcode", MboSlxEjecCont.getString("laborcode"),2L);
				MboSlxSoliPagContDet.setValue("craft", MboSlxEjecCont.getString("craft"),2L);
				MboSlxSoliPagContDet.setValue("skilllevel", MboSlxEjecCont.getString("skilllevel"),2L);
				MboSlxSoliPagContDet.setValue("amcrew", MboSlxEjecCont.getString("amcrew"),2L);
				MboSlxSoliPagContDet.setValue("amcrewtype", MboSlxEjecCont.getString("amcrewtype"),2L);
				MboSlxSoliPagContDet.setValue("premiumpaycode", MboSlxEjecCont.getString("premiumpaycode"),2L);
				MboSlxSoliPagContDet.setValue("avancerep", MboSlxEjecCont.getInt("avance"),2L);
				MboSlxSoliPagContDet.setValue("quantity", MboSlxEjecCont.getDouble("quantity"),2L);
				MboSlxSoliPagContDet.setValue("cantidadpe", MboSlxEjecCont.getDouble("cantidadpe"),2L);
				MboSlxSoliPagContDet.setValue("costounpe", MboSlxEjecCont.getDouble("costounpe"),2L);				
				MboSlxSoliPagContDet.setValue("unitcost", MboSlxEjecCont.getDouble("unitcost"),2L);
				MboSlxSoliPagContDet.setValue("glaccount", MboSlxEjecCont.getString("glaccount"),2L);
				MboSlxSoliPagContDet.setValue("slx_ejec_contid", MboSlxEjecCont.getString("slx_ejec_contid"),2L);
				MboSlxSoliPagContDet.setValue("ob", MboSlxEjecCont.getString("slxob"),2L);
				MboSlxSoliPagContDet.setValue("slxspcnum", this.app.getAppBean().getMbo().getString("slxspcnum"),2L);
			}			
		}
		logger.debug("00 CustSlxSoliPagContAppBean.execute: FIN");
		logger.debug("************************************************************");
		this.app.getDataBean("tabla_detalle").refreshTable();
		logger.debug("************************************************************");
 
		
		Utility.sendEvent(new WebClientEvent("dialogclose", app.getCurrentPageId(), null, sessionContext));

		return super.execute();
	}

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		logger.debug("************************************************************");
		logger.debug("00 CustSlxSoliPagContAppBean.execute: INICIO");
		Vector vecMboSlxEjecContSet =  this.getMboSet().getSelection();
		if (vecMboSlxEjecContSet.isEmpty())
		{
			throw new MXApplicationException("SLX_SOLI_PAG_CONT", "noselect");
		}
		else
		{
			MboSetRemote MboSlxSoliPagContDetSet = this.app.getAppBean().getMbo().getMboSet("SLX_SOLI_PAG_CONT_DET");
			try
			{
			
				for (int i = 0; i<vecMboSlxEjecContSet.size(); i++)
				{	
					logger.debug("00 CustPlusdwotrkAppBean.execute: SE RECORRE LOS REGISTROS SELECCIONADOS");
					MboRemote MboSlxEjecCont = (MboRemote) vecMboSlxEjecContSet.get(i);
					MboRemote MboSlxSoliPagContDet = MboSlxSoliPagContDetSet.add(2L);
					logger.debug("00 CustPlusdwotrkAppBean.execute: SE GUARDA REGISTRO SLX_SOLI_PAG_CONT_DET");
					MboSlxSoliPagContDet.setValue("linetype", MboSlxEjecCont.getString("linetype"),2L);	
					MboSlxSoliPagContDet.setValue("siteid", MboSlxEjecCont.getString("siteid"),2L);
					MboSlxSoliPagContDet.setValue("itemnum", MboSlxEjecCont.getString("itemnum"),11L);
					MboSlxSoliPagContDet.setValue("description", MboSlxEjecCont.getString("description2"),2L);								
					MboSlxSoliPagContDet.setValue("itemsetid", MboSlxEjecCont.getString("itemsetid"),2L);
					MboSlxSoliPagContDet.setValue("linecost",0,2L);
					MboSlxSoliPagContDet.setValue("wonum", MboSlxEjecCont.getString("wonum"),2L);
					MboSlxSoliPagContDet.setValue("refwo", MboSlxEjecCont.getString("refwo"),2L);
					MboSlxSoliPagContDet.setValue("taskid", MboSlxEjecCont.getString("taskid"),2L);
					MboSlxSoliPagContDet.setValue("requestby", MboSlxEjecCont.getString("requestby"),2L);
					MboSlxSoliPagContDet.setValue("laborcode", MboSlxEjecCont.getString("laborcode"),2L);
					MboSlxSoliPagContDet.setValue("craft", MboSlxEjecCont.getString("craft"),2L);
					MboSlxSoliPagContDet.setValue("skilllevel", MboSlxEjecCont.getString("skilllevel"),2L);
					MboSlxSoliPagContDet.setValue("amcrew", MboSlxEjecCont.getString("amcrew"),2L);
					MboSlxSoliPagContDet.setValue("amcrewtype", MboSlxEjecCont.getString("amcrewtype"),2L);
					MboSlxSoliPagContDet.setValue("premiumpaycode", MboSlxEjecCont.getString("premiumpaycode"),2L);
					MboSlxSoliPagContDet.setValue("avancerep", MboSlxEjecCont.getInt("avance"),2L);
					MboSlxSoliPagContDet.setValue("quantity", MboSlxEjecCont.getDouble("quantity"),2L);
					MboSlxSoliPagContDet.setValue("cantidadpe", MboSlxEjecCont.getDouble("cantidadpe"),2L);
					MboSlxSoliPagContDet.setValue("costounpe", MboSlxEjecCont.getDouble("costounpe"),2L);				
					MboSlxSoliPagContDet.setValue("unitcost", MboSlxEjecCont.getDouble("unitcost"),2L);
					MboSlxSoliPagContDet.setValue("glaccount", MboSlxEjecCont.getString("glaccount"),2L);
					MboSlxSoliPagContDet.setValue("slx_ejec_contid", MboSlxEjecCont.getString("slx_ejec_contid"),2L);
					MboSlxSoliPagContDet.setValue("ob", MboSlxEjecCont.getString("slxob"),2L);
					MboSlxSoliPagContDet.setValue("slxspcnum", this.app.getAppBean().getMbo().getString("slxspcnum"),2L);
				}
	        } catch (MXException e) {
	    		logger.debug("***************************rollback()");
	        	MboSlxSoliPagContDetSet.rollback();
	    		logger.debug("***************************cleanup()");
	        	MboSlxSoliPagContDetSet.cleanup();
	    		logger.debug("***************************FIN execute. rollback() cleanup()");
	    		Object errorMsje[] = { e.getMessage() };
	    		throw new MXApplicationException("SLX_SOLI_PAG_CONT", "ErrorInesperado", errorMsje);
	        }
		}
		logger.debug("00 CustSlxSoliPagContAppBean.execute: FIN");
		logger.debug("************************************************************");
		this.app.getDataBean("tabla_detalle").refreshTable();
		logger.debug("************************************************************");
 
		
		Utility.sendEvent(new WebClientEvent("dialogclose", app.getCurrentPageId(), null, sessionContext));

		return super.execute();
	}

	public int allLinesOriginal() throws MXException, RemoteException {
		logger.debug("************************************************************");
		logger.debug("00 CustSlxSoliPagContAppBean.allLines: INICIO");
		MboSetRemote vecMboSlxEjecContSet =  this.getMboSet();
		for (int i = 0; i<vecMboSlxEjecContSet.count(); i++)
		{	
			logger.debug("00 CustPlusdwotrkAppBean.allLines: SE RECORRE LOS REGISTROS SELECCIONADOS");
			MboRemote MboSlxEjecCont = (MboRemote) vecMboSlxEjecContSet.getMbo(i);
			MboSetRemote MboSlxSoliPagContDetSet = this.app.getAppBean().getMbo().getMboSet("SLX_SOLI_PAG_CONT_DET");
			MboRemote MboSlxSoliPagContDet = MboSlxSoliPagContDetSet.add(2L);
			logger.debug("00 CustPlusdwotrkAppBean.allLines: SE GUARDA REGISTRO SLX_SOLI_PAG_CONT_DET");
			MboSlxSoliPagContDet.setValue("linetype", MboSlxEjecCont.getString("linetype"),2L);	
			MboSlxSoliPagContDet.setValue("siteid", MboSlxEjecCont.getString("siteid"),2L);
			MboSlxSoliPagContDet.setValue("itemnum", MboSlxEjecCont.getString("itemnum"),11L);
			MboSlxSoliPagContDet.setValue("description", MboSlxEjecCont.getString("description2"),2L);								
			MboSlxSoliPagContDet.setValue("itemsetid", MboSlxEjecCont.getString("itemsetid"),2L);
			MboSlxSoliPagContDet.setValue("linecost",0,2L);
			MboSlxSoliPagContDet.setValue("wonum", MboSlxEjecCont.getString("wonum"),2L);
			MboSlxSoliPagContDet.setValue("refwo", MboSlxEjecCont.getString("refwo"),2L);
			MboSlxSoliPagContDet.setValue("taskid", MboSlxEjecCont.getString("taskid"),2L);
			MboSlxSoliPagContDet.setValue("requestby", MboSlxEjecCont.getString("requestby"),2L);
			MboSlxSoliPagContDet.setValue("laborcode", MboSlxEjecCont.getString("laborcode"),2L);
			MboSlxSoliPagContDet.setValue("craft", MboSlxEjecCont.getString("craft"),2L);
			MboSlxSoliPagContDet.setValue("skilllevel", MboSlxEjecCont.getString("skilllevel"),2L);
			MboSlxSoliPagContDet.setValue("amcrew", MboSlxEjecCont.getString("amcrew"),2L);
			MboSlxSoliPagContDet.setValue("amcrewtype", MboSlxEjecCont.getString("amcrewtype"),2L);
			MboSlxSoliPagContDet.setValue("premiumpaycode", MboSlxEjecCont.getString("premiumpaycode"),2L);
			MboSlxSoliPagContDet.setValue("avancerep", MboSlxEjecCont.getInt("avance"),2L);
			MboSlxSoliPagContDet.setValue("quantity", MboSlxEjecCont.getDouble("quantity"),2L);
			MboSlxSoliPagContDet.setValue("cantidadpe", MboSlxEjecCont.getDouble("cantidadpe"),2L);
			MboSlxSoliPagContDet.setValue("costounpe", MboSlxEjecCont.getDouble("costounpe"),2L);				
			MboSlxSoliPagContDet.setValue("unitcost", MboSlxEjecCont.getDouble("unitcost"),2L);
			MboSlxSoliPagContDet.setValue("glaccount", MboSlxEjecCont.getString("glaccount"),2L);
			MboSlxSoliPagContDet.setValue("slx_ejec_contid", MboSlxEjecCont.getString("slx_ejec_contid"),2L);
			MboSlxSoliPagContDet.setValue("ob", MboSlxEjecCont.getString("slxob"),2L);
			MboSlxSoliPagContDet.setValue("slxspcnum", this.app.getAppBean().getMbo().getString("slxspcnum"),2L);
		}
		logger.debug("************************************************************");
		this.app.getDataBean("tabla_detalle").refreshTable();
		save();
		Utility.sendEvent(new WebClientEvent("dialogclose", app.getCurrentPageId(), null, sessionContext));
		logger.debug("00 CustSlxSoliPagContAppBean.allLines: FIN");
		logger.debug("************************************************************");
		return 1;
	}
	
	public int allLines() throws MXException, RemoteException {
		logger.debug("************************************************************");
		logger.debug("00 CustSlxSoliPagContAppBean.allLines: INICIO");
		MboSetRemote vecMboSlxEjecContSet =  this.getMboSet();
		MboSetRemote MboSlxSoliPagContDetSet = this.app.getAppBean().getMbo().getMboSet("SLX_SOLI_PAG_CONT_DET");
		try
		{
			for (int i = 0; i<vecMboSlxEjecContSet.count(); i++)
			{	
				logger.debug("00 CustPlusdwotrkAppBean.allLines: SE RECORRE LOS REGISTROS SELECCIONADOS");
				MboRemote MboSlxEjecCont = (MboRemote) vecMboSlxEjecContSet.getMbo(i);
				MboRemote MboSlxSoliPagContDet = MboSlxSoliPagContDetSet.add(2L);
				logger.debug("00 CustPlusdwotrkAppBean.allLines: SE GUARDA REGISTRO SLX_SOLI_PAG_CONT_DET");
				MboSlxSoliPagContDet.setValue("linetype", MboSlxEjecCont.getString("linetype"),2L);	
				MboSlxSoliPagContDet.setValue("siteid", MboSlxEjecCont.getString("siteid"),2L);
				MboSlxSoliPagContDet.setValue("itemnum", MboSlxEjecCont.getString("itemnum"),11L);
				MboSlxSoliPagContDet.setValue("description", MboSlxEjecCont.getString("description2"),2L);								
				MboSlxSoliPagContDet.setValue("itemsetid", MboSlxEjecCont.getString("itemsetid"),2L);
				MboSlxSoliPagContDet.setValue("linecost",0,2L);
				MboSlxSoliPagContDet.setValue("wonum", MboSlxEjecCont.getString("wonum"),2L);
				MboSlxSoliPagContDet.setValue("refwo", MboSlxEjecCont.getString("refwo"),2L);
				MboSlxSoliPagContDet.setValue("taskid", MboSlxEjecCont.getString("taskid"),2L);
				MboSlxSoliPagContDet.setValue("requestby", MboSlxEjecCont.getString("requestby"),2L);
				MboSlxSoliPagContDet.setValue("laborcode", MboSlxEjecCont.getString("laborcode"),2L);
				MboSlxSoliPagContDet.setValue("craft", MboSlxEjecCont.getString("craft"),2L);
				MboSlxSoliPagContDet.setValue("skilllevel", MboSlxEjecCont.getString("skilllevel"),2L);
				MboSlxSoliPagContDet.setValue("amcrew", MboSlxEjecCont.getString("amcrew"),2L);
				MboSlxSoliPagContDet.setValue("amcrewtype", MboSlxEjecCont.getString("amcrewtype"),2L);
				MboSlxSoliPagContDet.setValue("premiumpaycode", MboSlxEjecCont.getString("premiumpaycode"),2L);
				MboSlxSoliPagContDet.setValue("avancerep", MboSlxEjecCont.getInt("avance"),2L);
				MboSlxSoliPagContDet.setValue("quantity", MboSlxEjecCont.getDouble("quantity"),2L);
				MboSlxSoliPagContDet.setValue("cantidadpe", MboSlxEjecCont.getDouble("cantidadpe"),2L);
				MboSlxSoliPagContDet.setValue("costounpe", MboSlxEjecCont.getDouble("costounpe"),2L);				
				MboSlxSoliPagContDet.setValue("unitcost", MboSlxEjecCont.getDouble("unitcost"),2L);
				MboSlxSoliPagContDet.setValue("glaccount", MboSlxEjecCont.getString("glaccount"),2L);
				MboSlxSoliPagContDet.setValue("slx_ejec_contid", MboSlxEjecCont.getString("slx_ejec_contid"),2L);
				MboSlxSoliPagContDet.setValue("ob", MboSlxEjecCont.getString("slxob"),2L);
				MboSlxSoliPagContDet.setValue("slxspcnum", this.app.getAppBean().getMbo().getString("slxspcnum"),2L);
			}
        } catch (MXException e) {
    		logger.debug("***************************rollback()");
        	MboSlxSoliPagContDetSet.rollback();
    		logger.debug("***************************cleanup()");
        	MboSlxSoliPagContDetSet.cleanup();
    		logger.debug("***************************FIN allLines. rollback() cleanup()");
    		Object errorMsje[] = { e.getMessage() };
    		throw new MXApplicationException("SLX_SOLI_PAG_CONT", "ErrorInesperado", errorMsje);
        }
		logger.debug("************************************************************");
		this.app.getDataBean("tabla_detalle").refreshTable();
		save();
		Utility.sendEvent(new WebClientEvent("dialogclose", app.getCurrentPageId(), null, sessionContext));
		logger.debug("00 CustSlxSoliPagContAppBean.allLines: FIN");
		logger.debug("************************************************************");
		return 1;
	}
}