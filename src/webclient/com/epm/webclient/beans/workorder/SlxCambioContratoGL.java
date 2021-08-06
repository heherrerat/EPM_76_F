package com.epm.webclient.beans.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXApplicationException;
import psdi.util.MXApplicationWarningException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.Utility;
import psdi.webclient.system.controller.WebClientEvent;
import psdi.app.workorder.*;

import com.epm.app.workorder.*;

import psdi.app.workorder.virtual.*;

public class SlxCambioContratoGL extends DataBean
{
	private static MXLogger logger;
	
	public SlxCambioContratoGL()
	{
		logger = MXLoggerFactory.getLogger("maximo.application");
	}
	
	@Override
	protected void initialize() throws MXException, RemoteException {
		logger.debug("00 SlxCambioContratoGL.initialize: INICIO");
		super.initialize();
		
		MboRemote MboBase = this.app.getAppBean().getMbo();
		
		//setValue("wonum", MboBase.getString("wonum"), 11L);	
		setValue("siteid", MboBase.getString("siteid"), 11L);	
		setValue("orgid", MboBase.getString("orgid"), 11L);
		logger.debug("00 SlxCambioContratoGL.initialize: FIN");
	}
	
	public synchronized int cambiaGLContrato() throws MXException, RemoteException {
		logger.debug("00 SlxCambioContratoGL.cambiaGLContrato: INICIO");

		String stContrato = getString("CONTRACTNUM");
		String stGL = getString("GLACCOUNT");
		String stNewContrato = getString("NEWCONTRACTNUM");
		String stNewGL = getString("NEWGLACCOUNT");

		logger.debug("01 SlxCambioContratoGL.cambiaGLContrato: if(stContrato.length()<1 && stGL.length()<1 && stNewContrato.length()<1 && stNewGL.length()<1)= " + (stContrato.length()<1 && stGL.length()<1 && stNewContrato.length()<1 && stNewGL.length()<1));
		if (stContrato.length()<1 && stGL.length()<1 && stNewContrato.length()<1 && stNewGL.length()<1)
		{
			Object params[] = {"Debe ingresar información de cuentas y/o contratos."};
    		throw new MXApplicationException("slxgenerico", "informacion", params);
		}

		logger.debug("02 SlxCambioContratoGL.cambiaGLContrato: if((stContrato.length()>1 && stNewContrato.length()<1) || (stContrato.length()<1 && stNewContrato.length()>1))= " + ((stContrato.length()>1 && stNewContrato.length()<1) || (stContrato.length()<1 && stNewContrato.length()>1)));
		if ((stContrato.length()>1 && stNewContrato.length()<1) || (stContrato.length()<1 && stNewContrato.length()>1))
		{
			Object params[] = {"Debe ingresar información de ambos contratos."};
    		throw new MXApplicationException("slxgenerico", "informacion", params);
		}

		logger.debug("03 SlxCambioContratoGL.cambiaGLContrato: if((stGL.length()>1 && stNewGL.length()<1) || (stGL.length()<1 && stNewGL.length()>1))= " + ((stGL.length()>1 && stNewGL.length()<1) || (stGL.length()<1 && stNewGL.length()>1)));
		if ((stGL.length()>1 && stNewGL.length()<1) || (stGL.length()<1 && stNewGL.length()>1))
		{
			Object params[] = {"Debe ingresar información de ambas cuentas."};
    		throw new MXApplicationException("slxgenerico", "informacion", params);
		}

		MboSetRemote woSet = app.getResultsBean().getMboSet();
		WORemote wo;
		//WORemote WORemote = (WORemote)this.app.getAppBean().getMbo();		
        int i = 0;
        int iTot = 0;
        MboRemote line;
        boolean boLinNulo = false;
        boolean boSumarTotalizador = false;
        boolean boMarcarLineaCont = false;
		String contract="";
		String cuenta="";
		String stMsje="";
		String stWonum="";
		String stStatus="";
		boolean boSave=false;
		
		MboSetRemote mboSetEjecCont = null;
        for (int j = 0; (wo = (WORemote)woSet.getMbo(j)) != null; j++)
        {
    		logger.debug("04 SlxCambioContratoGL.cambiaGLContrato: if(wo.isSelected())= " + (wo.isSelected()));
            if (wo.isSelected())
            {
            	iTot = 0;
            	stWonum=wo.getString("WONUM");
            	stStatus=wo.getString("STATUS");

        		logger.debug("05 SlxCambioContratoGL.cambiaGLContrato: if(!stStatus.equalsIgnoreCase(\"COMPLETO\"))= " + (!stStatus.equalsIgnoreCase("COMPLETO")));
            	if (!stStatus.equalsIgnoreCase("COMPLETO"))
					stMsje = stMsje + "OT: " + stWonum + " NO cumple con el estado requerido." + "\n"; 
            	else
            	{
					//MboSetRemote mboSetEjecCont = (MboSetRemote)wo.getMboSet("SLX_EJEC_CONT");
					mboSetEjecCont = (MboSetRemote)wo.getMboSet("SLX_EJEC_CONT");
					try
					{
						logger.debug("00 SlxCambioContratoGL.cambiaGLContrato: where: " + mboSetEjecCont.getCompleteWhere());

		        		logger.debug("06 SlxCambioContratoGL.cambiaGLContrato: if(mboSetEjecCont.isEmpty())= " + (mboSetEjecCont.isEmpty()));
						if (mboSetEjecCont.isEmpty())
							stMsje = stMsje + "OT: " + stWonum + " sin registros de Ejecución contratista." + "\n";
						else
						{
							i = 0;
							while ((line = mboSetEjecCont.getMbo(i++)) != null)
							{
								contract="";
								cuenta="";
								boLinNulo = false;
								boSumarTotalizador=true;
								boMarcarLineaCont = false;
		
								int mboLiq = ((CustEjecCont)line).getMboSet("SLX_SOLI_PAG_DET").count();
								logger.debug("00 boValidaContratoLiquidacion. Count Acta Existe en Liq: " + mboLiq);
				        		logger.debug("07 SlxCambioContratoGL.cambiaGLContrato: if(mboLiq<1)= " + (mboLiq<1));
								if ( mboLiq<1 )
								{
					        		logger.debug("08 SlxCambioContratoGL.cambiaGLContrato: if(stContrato.length()>0 && stNewContrato.length()>0)= " + (stContrato.length()>0 && stNewContrato.length()>0));
									if ( stContrato.length()>0 && stNewContrato.length()>0 )
									{
						        		logger.debug("09 SlxCambioContratoGL.cambiaGLContrato: if(boValidaContratoLiquidacion(line))= " + (boValidaContratoLiquidacion(line)));
										if ( boValidaContratoLiquidacion(line) )
										{
							        		logger.debug("10 SlxCambioContratoGL.cambiaGLContrato: if(!line.isNull(\"CONTRACTNUM\"))= " + (!line.isNull("CONTRACTNUM")));
											if (!line.isNull("CONTRACTNUM"))
												contract=line.getString("CONTRACTNUM");
											else
												boLinNulo = true;
											
							        		logger.debug("11 SlxCambioContratoGL.cambiaGLContrato: if(contract.equals(getString(\"CONTRACTNUM\")) || (boLinNulo))= " + (!line.isNull("CONTRACTNUM")));
											if (contract.equals(getString("CONTRACTNUM")) || (boLinNulo) )
											{
												line.setValue("CONTRACTNUM", getString("NEWCONTRACTNUM"));
												boSave=true;
												iTot++;
												boMarcarLineaCont=true;
												boSumarTotalizador=false;
											}
										}
									}
									
					        		logger.debug("12 SlxCambioContratoGL.cambiaGLContrato: if(stGL.length()>0 && stNewGL.length()>0)= " + (stGL.length()>0 && stNewGL.length()>0));
									if ( stGL.length()>0 && stNewGL.length()>0 )
									{	
										cuenta="";
										boLinNulo = false;
						        		logger.debug("13 SlxCambioContratoGL.cambiaGLContrato: if(!line.isNull(\"GLACCOUNT\"))= " + (!line.isNull("GLACCOUNT")));
										if (!line.isNull("GLACCOUNT"))
											cuenta=line.getString("GLACCOUNT");
										else
											boLinNulo = true;
										
						        		logger.debug("14 SlxCambioContratoGL.cambiaGLContrato: if(cuenta.equals(stGL) || (boLinNulo))= " + (cuenta.equals(stGL) || (boLinNulo)));
										if (cuenta.equals(stGL) || (boLinNulo) )
										{
											line.setValue("GLACCOUNT", stNewGL);
											boSave=true;
							        		logger.debug("15 SlxCambioContratoGL.cambiaGLContrato: if(boSumarTotalizador)= " + (boSumarTotalizador));
											if (boSumarTotalizador) iTot++;
											boMarcarLineaCont=true;
										}
									}

					        		logger.debug("16 SlxCambioContratoGL.cambiaGLContrato: if(boMarcarLineaCont)= " + (boMarcarLineaCont));
									if(boMarcarLineaCont)
										line.setValue("GLCONTACT", true);
								}
							}
			
							//if (iTot>0) this.app.getAppBean().save();
							stMsje = stMsje + "Se han actualizado " + iTot + " registros de Ejecución contratista. OT: " + stWonum + "\n"; 
						}
				    }
					catch (MXException e)
					{
						logger.debug("***************************rollback()");
						mboSetEjecCont.rollback();
						logger.debug("***************************cleanup()");
						mboSetEjecCont.cleanup();
						logger.debug("***************************FIN cambiaGLContrato. rollback() cleanup()");
						Object errorMsje[] = { e.getMessage() };
						throw new MXApplicationException("slxgenerico", "informacion", errorMsje);
				    }
            	}
            }
        }
		logger.debug("17 SlxCambioContratoGL.cambiaGLContrato: if(boSave)= " + (boSave));
		if (boSave) mboSetEjecCont.save();
		
		logger.debug("18 SlxCambioContratoGL.cambiaGLContrato: if(stMsje.length()<1)= " + (stMsje.length()<1));
        if (stMsje.length()<1) stMsje = "No se ha procesado información." + "\n";
		Object params[] = {
				stMsje
            };
		((WOSet)this.app.getAppBean().getMboSet()).addWarning(new MXApplicationWarningException("slxgenerico", "informacion", params));

		Utility.sendEvent(new WebClientEvent("dialogclose", app.getCurrentPageId(), null, sessionContext));
		
		logger.debug("00 SlxCambioContratoGL.cambiaGLContrato: FIN");
		return 1;
	}

	public synchronized boolean boValidaContratoLiquidacion(MboRemote line) throws MXException, RemoteException {
		logger.debug("00 SlxCambioContratoGL.boValidaContratoLiquidacion: INICIO");
		boolean boValidar = true;
		String stNewContrato = getString("NEWCONTRACTNUM");
		int i = 0;

		MboSetRemote mboSetContratos = ((CustEjecCont)line).getMboSet("CONTRACT");
		logger.debug("01 SlxCambioContratoGL.boValidaContratoLiquidacion: if(!mboSetContratos.isEmpty())= " + (!mboSetContratos.isEmpty()));
		if(!mboSetContratos.isEmpty())
		{
	        MboRemote contrato;
			while ((contrato = mboSetContratos.getMbo(i++)) != null)
			{
				logger.debug("00 boValidaContratoLiquidacion. Contrato Nuevo: " + stNewContrato + " Contrato valido de lookup: " + contrato.getString("CONTRACTNUM"));
				logger.debug("02 SlxCambioContratoGL.boValidaContratoLiquidacion: if(stNewContrato.equals(contrato.getString(\"CONTRACTNUM\")))= " + (stNewContrato.equals(contrato.getString("CONTRACTNUM"))));
				if (stNewContrato.equals(contrato.getString("CONTRACTNUM")))
				{
					boValidar = true;
					break;
				}
			}
		}
		logger.debug("00 SlxCambioContratoGL.boValidaContratoLiquidacion: FINAL");
		return boValidar;
	}
		
	public synchronized int actualizaCostosVigentes() throws MXException, RemoteException {
		logger.debug("00 SlxCambioContratoGL.actualizaCostosVigentes: INICIO");

		MboSetRemote woSet = app.getResultsBean().getMboSet();
		WORemote wo;
		//WORemote WORemote = (WORemote)this.app.getAppBean().getMbo();		
        int i = 0;
        int iTot = 0;
		String stMsje="";
		String stWonum="";
		String stStatus="";
        double doUnitCost = 0.0D;
        boolean boSave=false;
        CustEjecCont line;

		MboSetRemote mboSetEjecCont = null;
        for (int j = 0; (wo = (WORemote)woSet.getMbo(j)) != null; j++)
        {
    		logger.debug("01 SlxCambioContratoGL.actualizaCostosVigentes: if(wo.isSelected())= " + (wo.isSelected()));
            if (wo.isSelected())
            {
            	iTot=0;
            	stWonum=wo.getString("WONUM");
            	stStatus=wo.getString("STATUS");
        		logger.debug("02 SlxCambioContratoGL.actualizaCostosVigentes: if(!stStatus.equalsIgnoreCase(\"COMPLETO\"))= " + (!stStatus.equalsIgnoreCase("COMPLETO")));
            	if (!stStatus.equalsIgnoreCase("COMPLETO"))
					stMsje = stMsje + "OT: " + stWonum + " NO cumple con el estado requerido." + "\n"; 
            	else
            	{
					//MboSetRemote mboSetEjecCont = (MboSetRemote)wo.getMboSet("SLX_EJEC_CONT");
            		mboSetEjecCont = (MboSetRemote)wo.getMboSet("SLX_EJEC_CONT");
					try
					{
						logger.debug("00 SlxCambioContratoGL.actualizaCostosVigentes: where: " + mboSetEjecCont.getCompleteWhere());
				        
		        		logger.debug("03 SlxCambioContratoGL.actualizaCostosVigentes: if(mboSetEjecCont.isEmpty())= " + (mboSetEjecCont.isEmpty()));
						if (mboSetEjecCont.isEmpty())
							stMsje = stMsje + "OT: " + stWonum + " sin registros de Ejecución contratista." + "\n";
						else
						{
							i = 0;
							while ((line = (CustEjecCont)mboSetEjecCont.getMbo(i++)) != null)
							{
								int mboLiq = ((CustEjecCont)line).getMboSet("SLX_SOLI_PAG_DET").count();
				        		logger.debug("04 SlxCambioContratoGL.actualizaCostosVigentes: if(mboLiq < 1)= " + (mboLiq < 1));
								if ( mboLiq < 1 )
								{
									MboSetRemote mboSetContratoVig = ((CustEjecCont)line).getMboSet("SLXCONTRACTLINE");
					        		logger.debug("05 SlxCambioContratoGL.actualizaCostosVigentes: if(!mboSetContratoVig.isEmpty())= " + (!mboSetContratoVig.isEmpty()));
									if (!mboSetContratoVig.isEmpty())
									{
										doUnitCost = mboSetContratoVig.getMbo(0).getDouble("UNITCOST");
										logger.debug("00 SlxCambioContratoGL.actualizaCostosVigentes: New Unitcost: " + doUnitCost);
										line.setValue("UNITCOST", doUnitCost);
										line.setValue("LINECOST", line.getDouble("QUANTITY") * doUnitCost);
										line.setValue("GLCONTACT", true);
										boSave=true;
										iTot++;
									}
								}
							}
			
							//if (iTot>0) this.app.getAppBean().save();
							stMsje = stMsje + "Se han actualizado " + iTot + " registros de Ejecución contratista. OT: " + stWonum + "\n"; 
						}
				    }
					catch (MXException e)
					{
						logger.debug("***************************rollback()");
						mboSetEjecCont.rollback();
						logger.debug("***************************cleanup()");
						mboSetEjecCont.cleanup();
						logger.debug("***************************FIN actualizaCostosVigentes. rollback() cleanup()");
						Object errorMsje[] = { e.getMessage() };
						throw new MXApplicationException("slxgenerico", "informacion", errorMsje);
				    }
            	}
            }
        }
		logger.debug("06 SlxCambioContratoGL.actualizaCostosVigentes: if(boSave)= " + (boSave));
		if (boSave) mboSetEjecCont.save();

		logger.debug("07 SlxCambioContratoGL.actualizaCostosVigentes: if(stMsje.length()<1)= " + (stMsje.length()<1));
        if (stMsje.length()<1) stMsje = "No se ha procesado información.";
		Object params[] = {
				stMsje
            };
		((WOSet)this.app.getAppBean().getMboSet()).addWarning(new MXApplicationWarningException("slxgenerico", "informacion", params));
		Utility.sendEvent(new WebClientEvent("dialogclose", app.getCurrentPageId(), null, sessionContext));
		
		logger.debug("00 SlxCambioContratoGL.actualizaCostosVigentes: FINAL");
		return 1;
	}
}
