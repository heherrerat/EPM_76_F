
package com.epm.iface.gis;

import java.rmi.RemoteException;
import psdi.iface.mic.StructureData;
import psdi.iface.migexits.UserExit;
import psdi.util.MXException;

/**
 * Env�o parcial de atributos no borre especificaci�n.
 * 
 * @author javalos
 */
public class GISAssetUserExit extends UserExit {

    @Override
    public StructureData setUserValueIn(StructureData irData, StructureData erData) throws MXException, RemoteException {
        irData.breakData();
        irData.setAction("AddChange");
        return irData;
    }
}
