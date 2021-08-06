
package com.epm.app.slxtrasloper;

import java.rmi.RemoteException;
import psdi.mbo.MboSet;
import psdi.mbo.custapp.CustomMbo;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 * Envío a JDEdwards y registro histórico de traslados a operar de activos.
 * 
 * @author javalos
 */
public class TraslOper extends CustomMbo implements TraslOperRemote {
    
    public TraslOper(MboSet ms) throws RemoteException {
        super(ms);
    }

    @Override
    public void add() throws MXException, RemoteException {
        super.add();
        setValue("createby", getUserInfo().getPersonId(), 2L);
        setValue("createdate", MXServer.getMXServer().getDate(), 2L);
    }

    /**
     * Una vez que se crean los registros quedan como históricos.
     */
    @Override
    public void init() throws MXException {
        super.init();
        try {
            if (!isNew())
                setFlag(7L, true);
        } catch (RemoteException e) {
        }
    }
    
    @Override
    public boolean is5_1() throws MXException, RemoteException {
        return !getString("slxpreactivo").equals(getString("ASSET.slxjd"));
    }
}
