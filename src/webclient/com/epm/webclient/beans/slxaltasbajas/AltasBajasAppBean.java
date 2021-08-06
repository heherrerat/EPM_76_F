
package com.epm.webclient.beans.slxaltasbajas;

import com.epm.app.slxaltasbajas.AltasBajasSetRemote;
import java.rmi.RemoteException;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.AppBean;
import psdi.webclient.system.beans.WebClientBean;
import psdi.webclient.system.controller.WebClientEvent;

/**
 *
 * @author javalos
 */
public class AltasBajasAppBean extends AppBean {
    
    public int enviarjde() throws MXException, RemoteException {
        WebClientEvent event = clientSession.getCurrentEvent();
        String eventValue = event.getValueString();
        
        if (getMboSet().getSelection().isEmpty())
            throw new MXApplicationException("slxaltasbajas", "noSelctn");
        
        MXException mxexcep = null;
        try {
            ((AltasBajasSetRemote) getMboSet()).enviarJDEdwards(eventValue);
        } catch (MXException e) {
            mxexcep = e;
        }
        
        SAVE();
//        app.getDataBean().reset();
        app.getDataBean("history").reset();
        
        if (mxexcep != null)
            throw mxexcep;
        else 
            throw new MXApplicationException("slxaltasbajas", "enviada", new Object[] {eventValue});
        
//        return WebClientBean.EVENT_HANDLED;
    }
}
