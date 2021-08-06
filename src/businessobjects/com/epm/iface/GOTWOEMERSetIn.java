
package com.epm.iface;

import java.rmi.RemoteException;
import psdi.iface.mic.MicSetIn;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.StatefulMbo;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.workflow.WorkFlowService;

/**
 * 
 * Al principio se seteaba el campo STATUS en ESPPROG por regla de servicio empresarial,
 * pero se caía con 'NoneType has no attribute getBoolean' en el script que maneja
 * la integración síncrona multitarea cuando se aprueban órdenes de trabajo, dado que dicho
 * script no está pensado para trabajar con una jerarquía de OT's creadas en memoria por 
 * un plan de trabajo y que todavía no han sido guardadas en la base de datos.
 * 
 * Por lo tanto, se omitió la regla de seteo del estado en el servicio empresarial y se cambia
 * el estado aquí.
 * 
 * Nota 1: no fue posible hacerlo con la misma OT ni siquiera con un save. Por otro lado, al obtener 
 * nuevamente la OT desde la BD no fue necesario el save().
 * 
 * Nota 2: se hizo finalmente como clase y no como script porque en la clase se puede
 * saber de qué interfaz se trata mientras que en el script no; y como la estructura de
 * objetos se usa en 2 interfaces (solo en una tiene que aprobar).
 * 
 * 
 * @author javalos
 */
public class GOTWOEMERSetIn extends MicSetIn {

    public GOTWOEMERSetIn() throws MXException, RemoteException {
    }

    @Override
    public int presetMboRules() throws MXException, RemoteException {
        if (processTable.equals("WORKORDER")) {
            
            String[][] data = {
                {"DESCRIPTION",null}, 
                {"WOPRIORITY","3"},
                {"SLXGRRESP","SPT01"},
                {"ASSETNUM",null},
                {"LOCATION",null}
            };
            
            for (String[] array : data) {
                String campo = array[0];
                if (struc.isInCurrentData(campo)) {
                    String origValue = struc.getCurrentData(campo);
                    try {
                        mbo.setValue(campo, origValue);

                    } catch (Exception e) {
                        if (campo.equals("LOCATION")) {
                            MboRemote topLoc = mbo.getMboSet("$topLoc", "LOCHIERARCHY", "siteid=:siteid and parent is null").getMbo(0);
                            if (topLoc == null)
                                throw new MXApplicationException("24x7", "topLocNotFound", new Object[] {mbo.getString("SITEID")});
                            array[1] = topLoc.getString("location");
                        }
                        mbo.setValue(campo, array[1]);
                        MboSetRemote wlogSet = mbo.getMboSet("WORKLOG");
                        wlogSet.add();
                        wlogSet.setValue("description", "Error al setear valor " + origValue + " en el campo " + campo);
                        wlogSet.setValue("description_longdescription", e.getMessage());
                        
                    } finally {
                        struc.removeFromCurrentData(campo);
                    }
                }
            }
        }
        return super.presetMboRules();
    }

    @Override
    public void afterProcess() throws MXException, RemoteException {
        if (interfaceName.equals("CreaWoAprobGOT")) {
            MboRemote wo = getPrimaryMbo();
            wo = wo.getMboSet("$me", "workorder", "siteid=:siteid and wonum=:wonum").getMbo(0);
            ((StatefulMbo) wo).changeStatus("ESPPROG", null, "Estado cambiado por integración");
        }
        /**
         * Cambio "24x7" (nº CC?)
         * Iniciar flujo a todas las OT que tuvieron problema seteando alguno 
         * de los campos definidos.
         */
        WorkFlowService wfservice = null;
        MboRemote wo;
        for (int i = 0; (wo = primaryMboSet.getMbo(i)) != null; i++) {
            if (wo.getMboSet("WORKLOG").toBeSaved()) {
                if (wfservice == null)
                    wfservice = (WorkFlowService) MXServer.getMXServer().lookup("WORKFLOW");
                wfservice.initiateWorkflow("SLXWO_000", wo);
            }
        }
    }
}
