
package com.epm.iface;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import org.jdom.Element;
import org.jdom.Namespace;
import psdi.iface.mic.StructureData;
import psdi.iface.mic.StructureObject;
import psdi.iface.migexits.UserExit;
import psdi.util.MXException;

/**
 * Maneja integración síncrona con JDEdwards y FSM.
 * 
 * En particular, ayuda a manejar el caso en que son solo servicios y por lo
 * tanto hay que tomar datos de cuenta de la 1era. línea y enviarlos en la cabe-
 * cera de una OT de JDE vacía. En el caso de la OT se podía incluir el MBO de
 * servicios WPITEM, pero en el caso de la MR no es posible diferenciar los sets
 * solo por la relación ya que el framework dice que no se puede repetir un mismo
 * nombre sin ser una estructura de objetos recursiva aun especificando distinta 
 * relación (después me di cuenta que sí lo permite pero en distinto nivel).
 * 
 * @author javalos
 */
public class ManageSyncIntUserExit extends UserExit {
    
    private final String auxFieldName = "SLXAUXMIF_NP";

    @Override
    public StructureData setUserValueOut(StructureData irData, StructureData erData) throws MXException, RemoteException {
        erData.breakData();

        if (ifaceName.startsWith("ERPCreaReserva")) 
        {
            // El string separado por pipe (|) es llenado por el script que gatilla el envío a los SSEE.
            String[] split = erData.getCurrentData(auxFieldName).split("\\|", -1);
            
            try {
                // Estos campos son usados por el mapa XSL de salida
                erData.setCurrentData("SCR_TIPODOC", split[0]);
                erData.setCurrentData("SCR_UNIDNEGOCIO", split[1]);
                erData.setCurrentData("SCR_CAUX", split[2]);
                
            } catch (Exception e) {
            }
        }
        /**
         * Si la cancelacíón viene de memoria debido a un error al aprobar OT, 
         * elimina elementos WPITEM de la OT 'dummy' utilizada y luego 'recrea' 
         * los WPITEM necesarios según la cantidad de líneas, llenando campos 
         * necesarios VENDOR y SLXUNIDNEGOCIO; esto permite reutilizar el mismo 
         * mapa XSL que cuando se cuenta con la OT original.
         */
        else if (ifaceName.equals("ERPCancelaCompra")) 
        {
            String[] split = erData.getCurrentData(auxFieldName).split("\\|");
            
            if (split.length > 1) {
                erData.removeChildren("WPITEM");
                
                int i = 5;
                do {
                    erData.createChildrenData("WPITEM", true);
                    erData.setCurrentData("VENDOR", split[0]);
                    erData.setCurrentData("SLXUNIDNEGOCIO", split[1]);
                    erData.createChildrenData("SLXEXTSYSNUMS", true);
                    erData.setCurrentData("DOCUMENTCOMPANY", split[2]);
                    erData.setCurrentData("DOCUMENTNUMBER", split[3]);
                    erData.setCurrentData("DOCUMENTTYPECODE", split[4]);
                    erData.setCurrentData("DOCUMENTLINENUMBER", split[i]);
                    erData.setAsCurrent(erData.getParentData());
                    erData.setAsCurrent(erData.getParentData());
                } 
                while (++i < split.length);
                
                erData.setCurrentData(auxFieldName, split[0]);  // requerido por el XSL
            }
        }
        else if (ifaceName.equals("FSMCreaTarea")) 
        {
            ns = erData.getCurrentData().getNamespace();
            addDoctypesWO_recur(erData);
        }
        
        return erData;
    }
    
    private Namespace ns;
    
    /**
     * Para enviar creación de tarea a FSM se recorre toda la jerarquía de OT's
     * y se colocan tags locales para los distintos tipos de documento de cada una;
     * el mapa XSL itera y filtra sobre estos tipos generando el XML definitivo.
     */
    private void addDoctypesWO_recur(StructureObject strucObjWo) throws MXException {
        HashSet<String> doctypes = new HashSet<>();
        for (StructureObject child : (List<StructureObject>) strucObjWo.getChildrenData("WPMATERIAL"))
            doctypes.add(child.getCurrentData(auxFieldName));
        
        strucObjWo.createChildrenData("TIPOS_DOC");
        StructureObject newTag = (StructureObject) strucObjWo.getChildrenData("TIPOS_DOC").get(0);
        for (String doctype : doctypes) 
            newTag.getCurrentData().addContent(new Element("TIPO_DOC", ns).setText(doctype));
        
        for (StructureObject daughterWo : (List<StructureObject>) strucObjWo.getChildrenData("WORKORDER"))
            addDoctypesWO_recur(daughterWo);
    }
}
