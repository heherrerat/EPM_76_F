
package com.epm.iface.gis;

import com.ibm.json.java.JSONObject;
import com.ibm.tivoli.maximo.fdmbo.JSONPathEvaluator;
import com.ibm.tivoli.maximo.oslc.OslcUtils;
import java.rmi.RemoteException;
import psdi.iface.mic.StructureData;
import psdi.iface.migexits.UserExit;
import static psdi.iface.mos.MosProcess.INTEGRATIONLOGGER;
import psdi.util.MXException;

/**
 * Cuando descripci�n de ubicaciones en GIS tiene comillas, por ejemplo para 
 * indicar pulgadas, estas comillas dobles son escapadas de alguna manera en la
 * conversi�n de JSON a XML que hace la clase de Spatial (salida externa); por 
 * lo tanto, aqu� se vuelve a poner el valor 'bruto' que ven�a en el JSON para
 * el caso de la descripci�n.
 * 
 * @author javalos
 */
public class GISLocationUserExit extends UserExit {

    @Override
    public StructureData setUserValueIn(StructureData irData, StructureData erData) throws MXException, RemoteException {

        JSONObject jsonData = (JSONObject) OslcUtils.bytesToJSON(erData.getDataAsBytes());

        jsonData = (JSONObject) JSONPathEvaluator.parseObjectPath(jsonData, "//features/attributes", 3);
        
        String gisFieldName = "DESCRIPCION";
        if (extSystem.equals("GIS_ProvAguas") || extSystem.equals("GIS_AguasResid")) {
            gisFieldName = "DESCRIPCION_UB";
        }
        
        if (jsonData.containsKey(gisFieldName)) {
            String descrptn = (String) jsonData.get(gisFieldName);
            if (descrptn.contains("\"")) {
                INTEGRATIONLOGGER.warn("descripci�n de ubicaci�n de GIS contiene comillas dobles (\"); saltando conversi�n de Spatial...");
                irData.breakData();
                irData.setCurrentData("DESCRIPTION", descrptn);
            }
        }
        return irData;
    }
}
