
package com.epm.iface;

import java.rmi.RemoteException;
import psdi.iface.mic.MaximoEventFilter;
import psdi.iface.mic.PublishInfo;
import psdi.mbo.MboRemote;
import psdi.util.MXException;

/**
 * CC91 Piden reenviar cambios de estado cuando estos a su vez vienen por 
 * integración.
 * 
 * @author javalos
 */
public class GOTChgStatusFilter extends MaximoEventFilter {
    
    public GOTChgStatusFilter(PublishInfo pubInfo) throws MXException {
        super(pubInfo);
    }

    @Override
    protected boolean stopRecurrence(MboRemote mbo) throws MXException, RemoteException {
        boolean mifStop = super.stopRecurrence(mbo);
        if (mifStop) {
            if (mbo.getString("ownersysid").equals("GOT")) {
                String status = mbo.getString("status");
                if (status.equals("EJECUTADO") || status.equals("COMPNOREALIZADA"))
                    return false;
            }
        }
        return mifStop;
    }
}
