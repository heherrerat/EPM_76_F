package com.epm.app.workorder;

import java.rmi.RemoteException;
import java.util.HashMap;

import psdi.iface.mic.PublishChannel;
import psdi.iface.mic.PublishChannelCache;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.plusd.app.workorder.PlusDWO;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;


public class CustWO extends PlusDWO implements CustPlusDWORemote
{
	private static MXLogger logger;
	
    public CustWO(MboSet arg0) throws MXException, RemoteException
    {
        super(arg0);
		logger = MXLoggerFactory.getLogger("maximo.application");
    }
/*
    public void add() throws MXException, RemoteException
    {
        super.add();
        logger.debug("00 CustWO.add: pointnum: " + getString("pointnum"));
        if(!isNull("pointnum"))
        {
            MboRemote mboPoint = this.getMboSet("MEASUREPOINT").getMbo(0);
            logger.debug("01 CustWO.add: pointnum lleno: " + (mboPoint!=null));
            if(mboPoint!=null)
            {
                logger.debug("02 CustWO.add: Detener OT: " + mboPoint.getBoolean("SLXSTOPWOGEN"));
            	if (mboPoint.getBoolean("SLXSTOPWOGEN"))
            	{
		            MboSetRemote mboSetOTs = getMboSet("$WORKORDER", "WORKORDER", "SITEID=:siteid AND POINTNUM=:pointnum AND ACTFINISH IS NULL AND STATUS NOT IN (select VALUE from synonymdomain where domainid = 'WOSTATUS' AND MAXVALUE IN('CLOSE', 'CAN', 'COMP'))");
	                logger.debug("03 CustWO.add: Where: " + mboSetOTs.getWhere());
		            if (!mboSetOTs.isEmpty())
		            {
			            Object params[] = { getString("POINTNUM") };
			            //((MboSet)getThisMboSet()).addWarning(new MXApplicationException("MEASUREMENT", "WorkGeneration", params));
			            throw new MXApplicationException("measurement", "WorkGeneration", params);
		            }
            	}
            }
        }
    }
*/
    public void appValidate() throws MXException, RemoteException
    {
        logger.debug("00 CustWO.appValidate: pointnum: " + getString("pointnum"));
        logger.debug("00 CustWO.appValidate: isNew: " + isNew());
        if(isNew() && !isNull("pointnum"))
        {
            MboRemote mboPoint = this.getMboSet("MEASUREPOINT").getMbo(0);
            logger.debug("01 CustWO.appValidate: pointnum lleno: " + (mboPoint!=null));
            if(mboPoint!=null)
            {
                logger.debug("02 CustWO.appValidate: Detener OT: " + mboPoint.getBoolean("SLXSTOPWOGEN"));
            	if (mboPoint.getBoolean("SLXSTOPWOGEN"))
            	{
		            MboSetRemote mboSetOTs = getMboSet("$WORKORDER", "WORKORDER", "SITEID=:siteid AND POINTNUM=:pointnum AND ACTFINISH IS NULL AND STATUS NOT IN (select VALUE from synonymdomain where domainid = 'WOSTATUS' AND MAXVALUE IN('CLOSE', 'CAN', 'COMP'))");
	                logger.debug("03 CustWO.appValidate: Where: " + mboSetOTs.getWhere());
		            if (!mboSetOTs.isEmpty())
		            {
		            	int inOTs = mboSetOTs.count();
		            	getThisMboSet().rollback();
		            	getThisMboSet().cleanup();
			            logger.debug("00 CustWO.appValidate: Error. Existen: " + inOTs + " OTs para el punto: " + getString("pointnum"));
			            //((MboSet)getThisMboSet()).rollbackToCheckpoint();
			            //((MboSet)getThisMboSet()).clear();
			            Object params[] = { inOTs , getString("POINTNUM") };
			            //((MboSet)getThisMboSet()).addWarning(new MXApplicationException("slxwo", "wogencondition", params));
			            throw new MXApplicationException("slxwo", "wogencondition", params);
		            }
            	}
            }
        }
    	super.appValidate();
    }
    

    /**
     * A partir de las líneas de la OT, recopila todas las OT enviadas a crear a
     * JDE; tanto las que corresponden a reservas de material, como las de cargo
     * directo (tipo 2E) y la OT que se crea cuando solo hay compras (tipo 2A);
     * luego envía todas las cancelaciones a dicho sistema.
     * 
     * javalos
     */
    @Override
    public void cancelaOTsJDE_old() throws MXException, RemoteException {
        
        final String DOCTYPE_EMPTY = "2A";
        final String DOCTYPE_DIRECTREQ = "2G";
        
        HashMap<String, String> OTinfo2cancel;
        
        OTinfo2cancel = new HashMap<>();
        
        MboRemote wpmat;
        MboSetRemote linesSet = getMboSet("SLXWPLINES2SENDJDE");
        
        for (int i = 0; (wpmat = linesSet.getMbo(i)) != null; i++) {
            String numOTjde = wpmat.getString("SLXEXTSYSNUMS.orderNumber");
            if (numOTjde != null && !numOTjde.isEmpty()) {
                if (wpmat.getString("linetype").equals("ITEM")) {
                    String tipoDoc;
                    if (wpmat.getBoolean("directreq")) {
                        tipoDoc = DOCTYPE_DIRECTREQ;
                    } else {
                        tipoDoc = getTranslator().toInternalString("SLXTIPODOC_SYNONYM", wpmat.getString("slxtipocosto"));
                    }
                    OTinfo2cancel.put(tipoDoc, numOTjde);
                } else {
                    //OTinfo2cancel.putIfAbsent(numOTjde, DOCTYPE_EMPTY);
                    if (!OTinfo2cancel.containsKey(numOTjde))
                        OTinfo2cancel.put(numOTjde, DOCTYPE_EMPTY);
                }
            }
        }
        
        PublishChannel pubChannel;

        // Si es cancelación asíncrona...
        for (String key : OTinfo2cancel.keySet()) {
            pubChannel = PublishChannelCache.getInstance().getPublishChannel("ERPCancelaReserva");
            setValue("slxauxmif_np", key + "|" + OTinfo2cancel.get(key), 2L);
            pubChannel.publish(this, false);
        } 
    }

    /**
     * Valida que las OT's sean válidas para enviar cancelación de tarea a FSM;
     * Antes se tenía junto con el método para marcar para envío, pero para mejor
     * gestión de errores en la ficha de lista se separó.
     */
    @Override
    public void validaCancelaTareaFSM() throws MXException, RemoteException {
        if (!getBoolean("slxfsm")) {
            throw new MXApplicationException("slxfsm", "notFSM", (Object[]) null);
        }
        if (isNull("slxfsmnum")) {
            throw new MXApplicationException("slxfsm", "taskNotCreatedYet", (Object[]) null);
        }
        if (getBoolean("slxenviacanfsm")) {
            throw new MXApplicationException("slxfsm", "alreadySentCanc", (Object[]) null);
        }
        if (!getString("slxfsmstatus").equals("ABIERTA")) {
            throw new MXApplicationException("slxfsm", "wrongStatusCancel", (Object[]) null);
        }
    }
    
    /**
     * Marca el flag para envío de cancelación de tarea hacia FSM;
     * luego el canal de publicación envía el mensaje; 
     * se invoca individual o masiva.
     */
    @Override
    public void marcaEnvioCancelaTareaFSM() throws MXException, RemoteException {
        setValue("slxenviacanfsm", true);
    }
}
