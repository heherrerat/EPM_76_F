
package com.epm.app.slxaltasbajas;

import java.rmi.RemoteException;
import psdi.mbo.MboSet;
import psdi.mbo.custapp.CustomMbo;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class AltasBajas extends CustomMbo {
    
    public AltasBajas(MboSet ms) throws RemoteException {
        super(ms);
    }

    @Override
    public void add() throws MXException, RemoteException {
        super.add();
        setValue("createdate", MXServer.getMXServer().getDate());
    }

    @Override
    public void appValidate() throws MXException, RemoteException {
        super.appValidate();
        if (getDouble("deltalongitud") == 0.0)
            throw new MXApplicationException("designer", "generic", new Object[] { "La variación de longitud no puede ser cero." });
    }

    @Override
    public void initFieldFlagsOnMbo(String attrName) throws MXException {
        try {
            boolean isEditField = attrName.equals("ENVIADAJDE") || attrName.equals("MSJEERRORJDE") || attrName.equals("MSJEERRORJDE_LONGDESCRIPTION") || attrName.equals("FECHAENVIO");
            if (isNew()) {
                setFieldFlag(attrName, 7L, isEditField);
            } else {
                setFieldFlag(attrName, 7L, !isEditField);
            }
        } catch (RemoteException ex) {
        }
    }
}
