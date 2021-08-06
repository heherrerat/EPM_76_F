
package com.epm.iface.gis;

import java.rmi.RemoteException;
import psdi.iface.app.asset.MaxAssetProcess;
import psdi.util.MXException;

/**
 * Permite usar una clave alternativa sin tener que crear un índice único.
 * 
 * @author javalos
 */
public class GISAssetAltKeySetIn_distrbcn extends MaxAssetProcess {

    public GISAssetAltKeySetIn_distrbcn() throws MXException, RemoteException {
    }

    @Override
    public String[] getKeyArray(String name) throws MXException, RemoteException {
        if (name.equals("ASSET")) {
            if (interfaceName.equals("CONDUCTORPRIM"))
                return new String[] {"PLUSSFEATURECLASS", "SLXAGRUPAMIENTO"};
            return new String[] {"PLUSSFEATURECLASS", "SLXGIS"};
        }
        return super.getKeyArray(name);
    }
}
