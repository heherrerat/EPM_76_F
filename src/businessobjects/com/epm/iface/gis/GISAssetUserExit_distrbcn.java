
package com.epm.iface.gis;

import com.ibm.json.java.JSONObject;
import com.ibm.tivoli.maximo.fdmbo.JSONPathEvaluator;
import com.ibm.tivoli.maximo.oslc.OslcUtils;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import psdi.iface.mic.StructureData;
import psdi.iface.migexits.UserExit;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.iterator.MboSetIterator;
import psdi.mbo.iterator.SimpleMboSetIterator;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 * Rellena el XML según reglas basadas en diccionario por capa de GIS (FNO);
 * también genera la plantilla de especificación técnica; esta clase contiene 
 * una regla temporal (ver comentarios) que se suponía una vez hechas las cargas 
 * desde GIS se quitaría en PROD (ref: S. Moreno), pero no se ha hecho ni tampoco 
 * se ha solicitado.
 * 
 * @author javalos
 */
public class GISAssetUserExit_distrbcn extends UserExit {
    
    private static final Map<Integer, Object[]> RULES_MAP = createRulesMap();

    @Override
    public StructureData setUserValueIn(StructureData irData, StructureData erData) throws MXException, RemoteException {
        /**
         * Código relativo al JSON sacado de clases estándar: 
         * 
         *      com.ibm.tivoli.maximo.fdmbo.JSONMapperExit
         *      com.ibm.tivoli.maximo.fdmbo.map.ExternalJSONMapper
         *      com.ibm.tivoli.maximo.fdmbo.JSONPathEvaluator
         *      com.ibm.tivoli.maximo.fdmbo.ExpressionEvaluator
         **/
        JSONObject jsonData = (JSONObject) OslcUtils.bytesToJSON(erData.getDataAsBytes());

        jsonData = (JSONObject) JSONPathEvaluator.parseObjectPath(jsonData, "//features/attributes", 3);
        
        irData.breakData();
        
        if (irData.isCurrentDataNull("SLXG3E_FNO"))
            throw new MXApplicationException("designer", "generic", new Object[] {"No se pudo clasificar el activo porque el campo G3E_FNO está vacío."});

        int fno = irData.getCurrentDataAsInt("SLXG3E_FNO");  // comentario python: También podía ser: fno = jsonData['G3E_FNO'] pero si se obtiene de esa manera llega como BigInteger (java.math)! (hay que llamar método intValue() y además en sgte. reglón)
        
        Object[] array = RULES_MAP.get(fno);
        String attrib = (String) array[0];
        
        if (attrib == null)
            array = Arrays.copyOfRange(array, 1, array.length);
        else {
            String attribVal = (String) jsonData.get(attrib);
            array = ((Map<String, Object[]>) array[1]).get(attribVal);
            if (array == null)
                throw new MXApplicationException("designer", "generic", new Object[] {"No se encontró el mapeo para " + attrib + "=" + attribVal + "; no pudo clasificarse el activo"});
        }
        
        irData.setCurrentData("CLASSSTRUCTUREID", (String) array[0]);
        if (array[1] != null)
            irData.setCurrentData("FAILURECODE", (String) array[1]);
        if (array[2] != null)
            irData.setCurrentData("SLXVIDAFINACT", (int) array[2]);
        if (array[3] != null)
            irData.setCurrentData("SLXVIDAREGUL", (int) array[3]);
        
        // Determinación de objeto costeo
        String tension = null, objcosteo;
        try {
            tension = (String) jsonData.get("TENSION_CONN");
        } catch (Exception e) {
        }
        
        if (tension == null)
            objcosteo = "499";
        else if ("7.621".equals(tension) || "13.2".equals(tension))
            objcosteo = "404";
        else if ("44".equals(tension) || "34.5".equals(tension))
            objcosteo = "403";
        else
            objcosteo = "405";
        irData.setCurrentData("SLXOBJCOSTEO", objcosteo);
        
        /**
         * REGLA TEMPORAL (quitar una vez en régimen!)
         * 
         * Esta regla estaba en la correlación JSON pero se puso aquí debido
         * a que no todas las capas tienen definido el campo CODIGO 
         * (acordarse también de quitar la regla para el ASSETNUM que quedó en la correlación!).
         */
        Object numope = null;
        if (fno == 20400)
            numope = jsonData.get("NRO_TRANSFORMADOR");
        else if (jsonData.containsKey("CODIGO"))
            numope = jsonData.get("CODIGO");
        irData.setCurrentData("SLXNUMOPE", (String) numope);

        // Generar plantilla de especificación técnica
        MboSetRemote classSet = MXServer.getMXServer().getMboSet("CLASSSTRUCTURE", this.userInfo);
        classSet.setWhere("classstructureid = '" + irData.getCurrentData("CLASSSTRUCTUREID") + "'");
        
        if (!classSet.isEmpty()) {
            MboSetRemote specSet = classSet.getMbo(0).getMboSet("CLASSSPEC");
            if (!specSet.isEmpty()) {
                for (MboSetIterator it = new SimpleMboSetIterator(specSet); it.hasNext(); ) {
                    MboRemote spec = it.next();
                    String attrid = spec.getString("assetattrid");
                    irData.createChildrenData("ASSETSPEC", true);
                    irData.setCurrentData("ASSETATTRID", attrid);
                    irData.setCurrentData("LINEARASSETSPECID", "0");
                    if (jsonData.containsKey(attrid)) {
                        irData.getCurrentData().setAttribute("in_json", "true");
                        String field = "ALN".equals(spec.getString("datatype")) ? "ALNVALUE" : "NUMVALUE";
                        Object value = jsonData.get(attrid);
                        if (value == null) {
                            irData.setCurrentDataNull(field);
                        } else {
                            // Código tomado de clase estándar: com.ibm.tivoli.maximo.fdmbo.map.ExternalJSONMapper
                            // Se descarta el caso en que el campo de MAXIMO sea fecha, ya que en la especificación técnica no es posible, solo ALN, NUMVALUE o TABLE.
                            String formattedValue = JSONPathEvaluator.objectToString(value);
                            formattedValue = formattedValue.replace("\"", "");
                            System.out.println("____formattedValue = " + formattedValue);
                            irData.setCurrentData(field, formattedValue);
                            //irData.setCurrentData(field, String.valueOf(value));   // siempre lo leerá como String
                        }
                    }
                    irData.setParentAsCurrent();
                }
            }
        }
        return irData;
    }

    private static Object[] merge(Object... objs) {
        List l = new ArrayList();
        for (Object o : objs) 
            if (o instanceof Object[])
//                l.addAll((java.util.Collection) o);   // Da error al ejecutar
                l.addAll(Arrays.asList((Object[]) o));  // si no se castea lo pone como 1 objeto
            else
                l.add(o);
        return l.toArray();
    }
    
    private static Map<Integer, Object[]> createRulesMap() {
        
        final Object[] a, b, c, d, e, f;
        
        a = merge("CAMARA", 840, 45);
        b = merge("DUCTO", 840, 45);
        c = merge("POSTE", 720, 45);
        d = merge("PARARRAY", 480, 45);
        e = merge("AISLADER", 480, 45);
        f = merge("1000049", e);
        
//        final Map<String, Object[]>[] attrMaps = new HashMap[5];  // Da error al ejecutar al hacer attrMaps[0].put(...)
        Map<String, Object[]> attrMap1, attrMap2, attrMap3, attrMap4, attrMap5;

        attrMap1 = new HashMap<>(7);
        attrMap1.put("DE CANGREJOS", merge("1000017", a));
        attrMap1.put("DE PASO", merge("1000018", a));
        attrMap1.put("DE EMPALME", merge("1000019", a));
        attrMap1.put("DE REGLETAS", merge("1000020", a));
        attrMap1.put("DE DISTRIBUCION", merge("1000021", a));
        attrMap1.put("PARA TRANSFORMADOR", merge("1000022", a));
        attrMap1.put("PARA SUICHES", merge("1000023", a));
        
        attrMap2 = new HashMap<>(4);
        attrMap2.put("ASBESTO - CEMENTO", merge("1000025", b));
        attrMap2.put("PVC - DB", merge("1000026", b));
        attrMap2.put("PVC - TDP", merge("1000027", b));
        attrMap2.put("METALICO", merge("1000028", b));
        
        attrMap3 = new HashMap<>(5);
        attrMap3.put("FIBRA DE VIDRIO", merge("1000030", c));
        attrMap3.put("CONCRETO", merge("1000031", c));
        attrMap3.put("MADERA", merge("1000032", c));
        attrMap3.put("METALICO", merge("1000033", c));
        attrMap3.put("OTRO", merge("1000055", c));
//        attrMap3.put("PRFV", merge("1000030", c));
        
        attrMap4 = new HashMap<>(2);
        attrMap4.put("ZNO POLIMERICO", merge("1000041", d));
        attrMap4.put("ZNO PORCELANA", merge("1000042", d));
        
        attrMap5 = new HashMap<>(6);
        attrMap5.put("SALIDA CON AISLADERO", f);
        attrMap5.put("ENTRADA CON AISLADERO", f);
        attrMap5.put("NORMAL", f);
        attrMap5.put("DE REPETICION", merge("1000050", e));
        attrMap5.put("FAULT TAMER", merge("1000051", e));
        attrMap5.put("TRIP SAVER", merge("1000056", e));
        
        final Map<Integer, Object[]> map = new HashMap<>();

        map.put(20200, new Object[] {null, "1000002", "CAPACIT", 240, 45});
        map.put(19000, new Object[] {null, "1000004", "RED.PRI", 480, 45});
        map.put(21200, new Object[] {null, "1000005", "RED.SEC", 480, 35});
        map.put(19800, new Object[] {null, "1000006", "RECONEC", 180, 45});
        map.put(19700, new Object[] {null, "1000012", "SUICHE", 300, 45});
        map.put(18400, new Object[] {null, "1000011", "SUICHE", 300, 45});
        map.put(20400, new Object[] {null, "1000014", "TRAFO", 480, 35});
        map.put(17400, new Object[] {"CLA_CAMARA", attrMap1});
        map.put(17300, new Object[] {"MATERIAL", attrMap2});
        map.put(17100, new Object[] {"MATERIAL", attrMap3});
        map.put(22200, new Object[] {null, "1000034", "BOBINAS", 360, 45});
        map.put(20300, new Object[] {null, "1000035", "REGULADO", 180, 45});
        map.put(19400, new Object[] {null, "1000039", "CUCHILL", 480, 45});
        map.put(20100, new Object[] {"TIPO_PARARRAYOS", attrMap4});
        map.put(20500, new Object[] {null, "1000043", "SENS.TEN", 480, 45});
        map.put(21700, new Object[] {null, "1000044", "CANGREJO", 480, 45});
        map.put(22800, new Object[] {null, "1000045", "DUCTO", 840, 45});
        map.put(18000, new Object[] {null, "1000046", "DOBLETIR", 480, 45});
        map.put(19600, new Object[] {null, "1000047", "SECCIONA", 480, 45});
        map.put(19300, new Object[] {"TIPO_AISLADERO", attrMap5});
        map.put(20900, new Object[] {null, "1000052", "PROTECT", 480, 35});
                
        return map;
    }
}
