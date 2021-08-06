
package com.epm.iface;

import java.rmi.RemoteException;
import java.util.Map;
import psdi.iface.mic.MicSetOut;
import psdi.iface.mos.MosDetailInfo;
import psdi.mbo.MboRemote;
import psdi.util.MXException;

/**
 * Auxiliar para probar integración con JDEdwards con cualquier usuario de MX.
 * Quitar una vez en Producción.
 * 
 * @author javalos
 */
public class ManageSyncIntSetOut_aux extends MicSetOut {

    public ManageSyncIntSetOut_aux() throws MXException, RemoteException {
    }

    @Override
    public int checkBusinessRules(MboRemote mbo, MosDetailInfo mosDetInfo, Map<String, Object> ovrdColValueMap) throws MXException, RemoteException {
        if (getPrimaryMbo().getString("description").contains("test")) {
            if (mbo.getName().equals("PERSON")) {
                ovrdColValueMap.put("PERSONID", "43532420");    // PERSONID de Dora
                ovrdColValueMap.put("SLXCENACT", "0100");
            }
            else if (mbo.getName().equals("MR"))
                ovrdColValueMap.put("REQUESTEDFOR", "43061370");
        }
        return super.checkBusinessRules(mbo, mosDetInfo, ovrdColValueMap);
    }
}
