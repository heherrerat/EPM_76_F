
package com.epm.iface;

import java.rmi.RemoteException;
import psdi.iface.mic.MicSetIn;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 * Clave alternativa para servicios de creación de OT's completas con consumos
 * desde GOT, JDE, FSM, para que no duplique OT's (falla de sistemas externos en
 * enviar dos veces) y se dejan servicios asíncronos.
 * 
 * @author javalos
 */
public class GOTAltKeySetIn extends MicSetIn {

    public GOTAltKeySetIn() throws MXException, RemoteException {
    }

    /**
     * No funcionó para creación; se implementa checkValidateErrors().
     */
    public String[] getKeyArray_old(String name) throws MXException, RemoteException {
        if (name.equals("WORKORDER"))
            return new String[] {"SOURCESYSID","EXTERNALREFID"};
        return super.getKeyArray(name);
    }

    /**
     * Validar clave alternativa para creación.
     */
    @Override
    public void checkValidateErrors() throws MXException, RemoteException {
        if (mbo.getName().equals("WORKORDER")) {
            if (mbo.isNull("sourcesysid") || mbo.isNull("externalrefid"))
                throw new MXApplicationException("slxgot", "missingKeyFields");
            MboSetRemote woSet = mbo.getMboSet("$exists", "WORKORDER", "sourcesysid=:sourcesysid and externalrefid=:externalrefid");
            if (!woSet.isEmpty())
                throw new MXApplicationException("slxgot", "duplicateKey", new Object[] {mbo.getString("sourcesysid"), mbo.getString("externalrefid")});
        }
        super.checkValidateErrors();
    }
}
