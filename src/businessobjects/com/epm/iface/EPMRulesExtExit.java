
package com.epm.iface;

import java.rmi.RemoteException;
import java.util.List;
import org.jdom.Element;
import psdi.iface.mic.StructureData;
import psdi.iface.mic.StructureObject;
import psdi.iface.migexits.ExternalExit;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.HTML;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 * Desde sistemas externos GOT y FSM no dispondrán del ASSETNUM ni del LOCATION,
 * por lo que para los distintos webservices que requieren el activo o la ubicación
 * enviarán, en el caso del activo, el nº operativo y en el de la ubicación el alias, 
 * y esta clase realiza la conversión tanto para la entrada como para la salida.
 * 
 * También, en las interfaces de salida, hay que quitar el texto enriquecido de todos 
 * los campos descripciones detalladas.
 * 
 * @author javalos
 */
public class EPMRulesExtExit extends ExternalExit {
    
    @Override
    public StructureData mapDataIn(StructureData data) throws MXException, RemoteException {
        System.out.println("____initedIn = " + this.initedIn);
        System.out.println("____initedOut = " + this.initedOut);
        data.breakData();
        convertirCodigos(data, 0);
        reemplazarAlmacenFSM(data);
        return super.mapDataIn(data);
    }

    @Override
    public StructureData mapDataOut(StructureData data) throws MXException, RemoteException {
        System.out.println("____initedIn = " + this.initedIn);
        System.out.println("____initedOut = " + this.initedOut);
        data.breakData();
        convertirCodigos(data, 1);
        quitarTextoEnriquecido(data.getCurrentData());
        return super.mapDataOut(data);
    }
    
    final String[][] dfltFieldInfoArray = {{"ASSETNUM", "slxnumope", "SITEID", "ASSET", "No se encontró el activo para la Planta y número operativo especificado."}, 
                                           {"LOCATION", "slxalias", "SITEID", "LOCATIONS", "No se encontró la ubicación para la Planta y alias especificado."}};

    private void convertirCodigos(StructureData data, int zeroIsIn) throws MXException, RemoteException {
        data.breakData();
        do {
            convertirCodigos_recur(data, zeroIsIn, null);
        } while (data.moveToNextObjectStructure());
    }
    
    private void convertirCodigos_recur(StructureObject strucObj, int zeroIsIn, String ancestorSITEID) throws MXException, RemoteException {
        String siteid = null;
        String[][] fieldInfoArray = getFieldInfoArray(strucObj.getName(), zeroIsIn);
        for (String[] info : fieldInfoArray) {
            siteid = strucObj.isCurrentDataNull(info[2]) ? ancestorSITEID : strucObj.getCurrentData(info[2]);
            if (!strucObj.isCurrentDataNull(info[0])) {
                if (siteid == null)
                    throw new MXApplicationException("designer", "generic", new Object[] {"No se encontró la Planta en el mensaje XML para convertir el código de activo/ubicación."});
                MboSetRemote qrySet = MXServer.getMXServer().getMboSet(info[3], userInfo);
                qrySet.setWhere("siteid = '" + siteid + "' and " + info[1-zeroIsIn] + "='" + strucObj.getCurrentData(info[0]) + "'");
                if (qrySet.isEmpty())
                    throw new MXApplicationException("designer", "generic", new Object[] {info[4]});
                strucObj.setCurrentData(info[0], qrySet.getMbo(0).getString(info[zeroIsIn]));
            }
        }
        for (StructureObject child : (List<StructureObject>) strucObj.getChildrenData())
            if (child.getChildrenData().size() > 0 && !"WPMATERIAL,WPITEM,MRLINE".contains(child.getName()))    // Para evitar conversión de almacenes !
                convertirCodigos_recur(child, zeroIsIn, siteid);
    }
    
    private String[][] getFieldInfoArray(String objectName, int direction) {
        if (objectName.equals("SR") && direction == 1)
            return new String[][] {{"ASSETNUM", "slxjd", "ASSETSITEID", "ASSET", "No se encontró el activo para la Planta y número de JDEdwards especificado."}};
        return dfltFieldInfoArray;
    }
    
    /**
     * Quitar texto enriquecido para todas las interfaces de salida (funciona!)
     */
    private void quitarTextoEnriquecido(Element element) {
        if (element.getName().endsWith("_LONGDESCRIPTION"))
            element.setText(HTML.toPlainText(element.getText()));
        for (Object child : element.getChildren())
            quitarTextoEnriquecido((Element) child);
    }

    /**
     * Sobreescribe el almacén que viene desde FSM por el A01 o A300, en ese orden, 
     * dependiendo de en cuál de ellos encuentre stock para el ítem.
     */
    private void reemplazarAlmacenFSM(StructureData data) throws MXException, RemoteException {
        if (!ifaceName.equals("WoConsumosCreateFSM") && !ifaceName.equals("WoConsumosUpdateFSM"))
            return;
        do_loop:
        do {
            List<StructureObject> list = data.getChildrenData("MATUSETRANS");
            MboSetRemote inventorySet = getMboSet("INVENTORY");
            for (StructureObject matusetrans : list) {
                String itemnum = matusetrans.getCurrentData("ITEMNUM");
                if (itemnum == null || itemnum.isEmpty() || "1".equals(matusetrans.getCurrentData("SLXESRESIDUO")))
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
        } while (data.moveToNextObjectStructure());        
    }
    
}
