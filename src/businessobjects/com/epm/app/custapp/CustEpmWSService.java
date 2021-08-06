
package com.epm.app.custapp;

import java.rmi.RemoteException;
import java.util.Date;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import psdi.app.asset.AssetRemote;
import psdi.iface.mic.MicUtil;
import psdi.iface.webservices.action.WSMboKey;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.custapp.CustomService;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 * Servicio personalizado que agrupa webservices estándar para EPM.
 * 
 * @author javalos
 */
public class CustEpmWSService extends CustomService implements CustEpmWSServiceRemote {

    public CustEpmWSService() throws RemoteException {
    }

    public CustEpmWSService(MXServer mxs) throws RemoteException {
        super(mxs);
    }
    
    @Override
    @WebMethod(operationName="reportDowntime")
    public void reportDowntime(@WSMboKey("ASSET") MboRemote assetMbo, @WSMboKey("WORKORDER") MboRemote woMbo, Date startDate, Date endDate, double hoursDown, String code, boolean operational) throws MXException, RemoteException {
        ((AssetRemote) assetMbo).reportDowntime(woMbo, startDate, endDate, hoursDown, code, operational);
        assetMbo.getThisMboSet().save();
    }

    @Override
    @WebMethod(operationName="informarRecepcionActivos")
    public void informarRecepcionActivos(@WSMboKey("SLXASSETPR") @WebParam(name="solicitud") MboRemote pr, int cantidad, boolean esDevolucion, double costoUnit, boolean recepCompleta, String numPreActivo, long idInternoJDE, String numOC_JDE, Date fechaRecep) throws MXException, RemoteException {
        if (cantidad <= 0)
            throw new MXApplicationException("designer", "generic", new Object[] {"La cantidad a recibir/devolver debe ser mayor que cero"});
        if (pr == null)
            throw new MXApplicationException("designer", "generic", new Object[] {"No se encontró la solicitud de compra de activos para esta recepción."});
        MboSetRemote assetSet; MboRemote asset = null; int i = 0;
        if (esDevolucion) {
            pr.setValue("iscomplete", false, 2L);
            assetSet = pr.getMboSet("$dev", "ASSET", "siteid=:siteid and slxassetprnum=:assetprnum and slxprreceived=:yes and slxnumope is null");
            while (i++ < cantidad && (asset = assetSet.moveNext()) != null) {
                asset.setValue("slxprreceived", false, 2L);
                asset.setValue("slxfechadevlcnjde", fechaRecep, 2L);
                asset.setValue("slxdevueltojde", true, 2L);
            }
            if (i < cantidad)
                MicUtil.INTEGRATIONLOGGER.warn("No fue posible devolver la cantidad de activos solicitada en 'recepción de Activo Fijo': no se encontraron suficientes activos recibidos asociados a la solicitud de compra");
        } else {
            if (pr.getBoolean("iscomplete"))
                throw new MXApplicationException("designer", "generic", new Object[] {"No se pueden seguir recibiendo activos para esta solicitud de compra porque ya se encuentra completa"});
                
            assetSet = pr.getMboSet("$rec", "ASSET", "siteid=:siteid and slxassetprnum=:assetprnum and slxprreceived=:no");
            while (i++ < cantidad && (asset = assetSet.moveNext()) != null) {
                asset.setValue("slxprreceived", true, 2L);
                asset.setValue("purchaseprice", costoUnit, 2L);
                asset.setValue("slxoc", numOC_JDE, 2L);
                asset.setValue("slxpreactivo", numPreActivo, 2L);     // se permite pisar??
                asset.setValue("slxfecharecpcnjde", fechaRecep, 2L);
                asset.setValueNull("slxfechadevlcnjde", 2L);
                asset.setValue("slxdevueltojde", false, 2L);
            }
            if (i < cantidad) {
                MicUtil.INTEGRATIONLOGGER.warn("No fue posible recibir la cantidad de activos solicitada en 'recepción de Activo Fijo': no se encontraron suficientes activos por recibir asociados a la solicitud de compra");
                throw new MXApplicationException("designer", "generic", new Object[] {"No fue posible recibir la cantidad de activos solicitada: no se encontraron suficientes activos por recibir asociados a la solicitud de compra"});
            }
            if (recepCompleta || assetSet.moveNext() == null)
                pr.setValue("iscomplete", true, 2L);   // autocompletar??
        }
        pr.getThisMboSet().save();
    }
}
