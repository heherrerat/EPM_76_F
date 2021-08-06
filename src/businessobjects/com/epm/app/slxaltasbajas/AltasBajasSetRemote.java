
package com.epm.app.slxaltasbajas;

import java.rmi.RemoteException;
import psdi.mbo.custapp.CustomMboSetRemote;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public interface AltasBajasSetRemote extends CustomMboSetRemote {
    
    public void enviarJDEdwards(String tipoEnviar) throws RemoteException, MXException;
    
}
