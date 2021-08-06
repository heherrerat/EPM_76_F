
package com.epm.iface.activofijo;

import java.rmi.RemoteException;
import psdi.iface.mic.MaximoEventFilter;
import psdi.iface.mic.PublishInfo;
import psdi.mbo.MboRemote;
import psdi.util.MXException;

/**
 * El sistema externo SIGMA (GIS) puede realizar cambios de estado de activos
 * a través de sincronizacrión REST por cron o mediante un web service de cambio
 * de estado; estos cambios así como los manuales deben ir a JDEdwards si cumplen
 * las condiciones.
 * 
 * @author javalos
 */
public class AllowRecurrenceFilter extends MaximoEventFilter {
    
    public AllowRecurrenceFilter(PublishInfo pubInfo) throws MXException {
        super(pubInfo);
    }

    @Override
    protected boolean stopRecurrence(MboRemote mbo) throws MXException, RemoteException {
        return false;
    }
}
