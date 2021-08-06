
package com.epm.iface.gis;

import java.rmi.RemoteException;
import psdi.iface.mic.StructureData;
import psdi.iface.migexits.UserExit;
import psdi.util.MXException;

/**
 * Determina el feature class
 * 
 * @author javalos
 */
public class GISLocationUserExit_distrbcn extends UserExit {

    @Override
    public StructureData setUserValueIn(StructureData irData, StructureData erData) throws MXException, RemoteException {
        irData.breakData();
        irData.setCurrentData("PLUSSFEATURECLASS", ifaceName);
        return irData;
    }
}
