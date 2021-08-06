
package com.epm.webclient.beans.slxassetpr;

import java.rmi.RemoteException;
import psdi.util.MXException;
import psdi.webclient.beans.common.ChangeStatusBean;
import psdi.webclient.system.controller.WebClientEvent;

/**
 * Selecciona el mbo actual para que la clase base ChangeStatusBean opere sobre
 * ese no más y no sobre el set entero (ChangeStatusSet.changeStatusOnSet).
 * Basado en WOChildChangeStatusBean.
 * 
 * @author javalos
 */
public class CustChangeStatusBean extends ChangeStatusBean {

    @Override
    public int execute() throws MXException, RemoteException {
        WebClientEvent event = this.clientSession.getCurrentEvent();
//        this.parent.getMbo(event.getRow()).select();
        this.parent.getMbo().select();
        return super.execute();
    }
}
