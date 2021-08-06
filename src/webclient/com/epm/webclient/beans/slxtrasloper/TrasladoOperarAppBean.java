
package com.epm.webclient.beans.slxtrasloper;

import com.epm.app.slxtrasloper.TraslOperServiceRemote;
import java.rmi.RemoteException;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.AppBean;
import psdi.webclient.system.controller.WebClientEvent;

/**
 *
 * @author javalos
 */
public class TrasladoOperarAppBean extends AppBean {

    public int trasloper() throws MXException, RemoteException {
        WebClientEvent event = clientSession.getCurrentEvent();
        String eventValue = event.getValueString();
        boolean simular = eventValue.equals("simular");
        
        if (getMboSet().getSelection().isEmpty())
            throw new MXApplicationException("slxtrasloper", "noSelctn");
        
        TraslOperServiceRemote service = (TraslOperServiceRemote) MXServer.getMXServer().lookup("SLXTRASLOPER");
        if (eventValue.equals("manual"))
            service.trasladarOperarManual(getMboSet());
        else
            service.enviarJDEdwards(getMboSet(), simular);

        if (simular) {
            throw new MXApplicationException("slxtrasloper", "traslOperSimular", new Object[] {});
        } else {
            SAVE();
            app.getDataBean().reset();
            app.getDataBean("history").reset();
    //        app.getDataBean("history").refreshTable();
            throw new MXApplicationException("slxtrasloper", "traslOperProcesar", new Object[] {});
        }
    }
}
