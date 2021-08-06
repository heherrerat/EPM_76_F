
package com.epm.iface;

import java.rmi.RemoteException;
import psdi.iface.mic.StructureData;
import psdi.iface.migexits.UserExit;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 * Devuelve el campo EXTERNALREFID enviado por SSEE en la respuesta de la creación
 * (no fue posible hacerlo de otra forma: clave alternativa, manejo en memoria, etc.).
 * 
 * @author javalos
 */
public class ReturnExternalRefIdExit extends UserExit {
    
    @Override
    public StructureData setUserValueOut(StructureData irData) throws MXException, RemoteException {
        irData.breakData();
        MboSetRemote woSet = MXServer.getMXServer().getMboSet("WORKORDER", userInfo);
        woSet.setWhere("siteid = '" + irData.getCurrentData("SITEID") + "' and wonum = '" + irData.getCurrentData("WONUM") + "'");
        irData.setCurrentData("EXTERNALREFID", woSet.getMbo(0).getString("externalrefid"));
        return irData;
    }
}
