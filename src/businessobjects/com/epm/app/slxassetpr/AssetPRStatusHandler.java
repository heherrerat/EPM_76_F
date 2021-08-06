
package com.epm.app.slxassetpr;

import java.rmi.RemoteException;
import java.util.Date;
import psdi.iface.mic.InvokeChannelCache;
import psdi.mbo.StatefulMbo;
import psdi.mbo.StatusHandler;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class AssetPRStatusHandler extends StatusHandler {

    private final StatefulMbo mbo;
    
    public AssetPRStatusHandler(StatefulMbo sm) {
        super(sm);
        mbo = sm;
    }

    @Override
    public void checkStatusChangeAuthorization(String string) throws MXException, RemoteException {
    }

    @Override
    public void changeStatus(String currentStatus, String desiredStatus, Date date, String memo) throws MXException, RemoteException {
        String initialInternalStatus = mbo.getTranslator().toInternalString("SLXASSETPRSTATUS", mbo.getMboValue("status").getInitialValue().asString(), mbo);
        String desiredInternalStatus = mbo.getTranslator().toInternalString("SLXASSETPRSTATUS", desiredStatus, mbo);
        if (initialInternalStatus.equals("WAPPR") && desiredInternalStatus.equals("APPR")) {
            if (mbo.getMboSet("ASSETPRLINE").isEmpty())
                throw new MXApplicationException("designer", "generic", new Object[] {"Debe seleccionar al menos una línea de activo para poder aprobar esta solicitud."});
            // Gatillar la integración de Activo Fijo: si se está aprobando, enviar solicitud a JDEdwards invocando un webservice.
            mbo.getMboSet("EXTSYSNUMS").deleteAll();
            InvokeChannelCache.getInstance().getInvokeChannel("compraActivoFijo_JDE").invoke(null, mbo, mbo, null);
        }
        mbo.setValue("status", desiredStatus, 2L);
        mbo.setValue("statusdate", date, 2L);
    }
}
