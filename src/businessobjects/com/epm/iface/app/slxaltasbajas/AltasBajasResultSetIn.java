
package com.epm.iface.app.slxaltasbajas;

import java.rmi.RemoteException;
import psdi.iface.mic.MicSetIn;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class AltasBajasResultSetIn extends MicSetIn {

    public AltasBajasResultSetIn() throws MXException, RemoteException {
    }

    /**
     * Valida que el registro esté en el estado correcto (ie, haya sido enviado
     * a JDE) para recibir el resultado del procesamiento por JDEdwards.
     */
    @Override
    public int presetMboRules() throws MXException, RemoteException {
        if (mbo.isNull("estado") || mbo.getString("estado").equals("NUEVO") || mbo.getString("estado").equals("PROCESADO")) {
            throw new MXApplicationException("slxaltasbajas", "wrongStatus", new Object[] {mbo.getLong("slxaltasbajasid")});
        }
        return super.presetMboRules();
    }
}
