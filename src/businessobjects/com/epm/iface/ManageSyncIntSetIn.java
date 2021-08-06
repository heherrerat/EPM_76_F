
package com.epm.iface;

import java.rmi.RemoteException;
import psdi.iface.mic.MicSetIn;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

/**
 * Funcionalidad auxiliar para soportar la integración síncrona con JDEdwards.
 * 
 * @author javalos
 */
public class ManageSyncIntSetIn extends MicSetIn {
    
    final String auxFieldName = "SLXAUXMIF_NP";
    
    public ManageSyncIntSetIn() throws MXException, RemoteException {
    }
    
    /**
     * En el caso de la MR no deja agregar si no es con 2L !
     */
    @Override
    public MboRemote addMbo(MboSetRemote mboSet) throws MXException, RemoteException {
        if (mboSet.getName().equals("SLXEXTSYSNUMS"))
            return mboSet.add(2L);
        return super.addMbo(mboSet);
    }

    @Override
    public int presetMboRules() throws MXException, RemoteException {

        // Campo auxiliar debe setearse con 2L para poder reutilizar!
        if (struc.isInCurrentData(auxFieldName)) {
            mbo.setValue(auxFieldName, struc.getCurrentData(auxFieldName), 2L);
            struc.removeFromCurrentData(auxFieldName);
        }
        return super.presetMboRules();
    }
}
