
package com.epm.iface;

import java.rmi.RemoteException;
import java.util.List;
import psdi.iface.mic.StructureData;
import psdi.iface.mic.StructureObject;
import psdi.iface.migexits.UserExit;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 * Interviene los despachos que vienen desde FSM por integración buscando el 
 * código de material en lista de almacenes tomada de propiedad personalizada.
 * 
 * @author javalos
 */
public class FSMInvIssueExit extends UserExit {

    @Override
    public StructureData setUserValueIn(StructureData irData, StructureData erData) throws MXException, RemoteException {
        irData.breakData();
        do_loop:
        do {
            List<StructureObject> list = irData.getChildrenData("MATUSETRANS");
            MboSetRemote inventorySet = getMboSet("INVENTORY");
            for (StructureObject matusetrans : list) {
                String itemnum = matusetrans.getCurrentData("ITEMNUM");
                if (itemnum == null || itemnum.isEmpty())
                    continue do_loop;
                inventorySet.setWhere("siteid='EPM_CORP' and location in ('A01','A300') and itemnum='" + itemnum + "'");
                inventorySet.setOrderBy("location");
                MboRemote balance = null;
                while (balance == null && inventorySet.moveNext() != null)
                    balance = inventorySet.getMbo().getMboSet("INVBALANCES").getMbo(0);
                if (balance == null)
                    /* Omitir
                    // No puedo modificar la lista que estoy iterando! se maneja con detach()
                    // irData.removeChildData("MATUSETRANS", list.indexOf(matusetrans));
                    matusetrans.getCurrentData().detach();*/
                    throw new MXApplicationException("designer", "generic", new Object[] {"No se encontraron registros de balance para el material " + itemnum + " en los almacenes A01 y A300"});
                else {
                    matusetrans.setCurrentData("SITEID", balance.getString("siteid"));
                    matusetrans.setCurrentData("STORELOC", balance.getString("location"));
                    matusetrans.setCurrentData("BINNUM", balance.getString("binnum"));
                    matusetrans.setCurrentData("LOTNUM", balance.getString("lotnum"));
                    matusetrans.setCurrentData("UNITCOST", inventorySet.getDouble("INVCOST.avgcost"));
                    matusetrans.removeFromCurrentData("LINECOST");
                }
                inventorySet.reset();
            }
        } while (irData.moveToNextObjectStructure());
        
        return irData;
    }
}
