
package com.epm.iface;

import java.rmi.RemoteException;
import psdi.iface.mic.IntegrationContext;
import psdi.iface.mic.MaximoEventFilter;
import psdi.iface.mic.PublishInfo;
import psdi.mbo.MboRemote;
import psdi.util.MXException;

/**
 * El sistema externo SIGMA (GIS) puede realizar cambios de estado de activos
 * a través de sincronizacrión REST por cron o mediante un web service de cambio
 * de estado; estos cambios así como los manuales deben ir a JDEdwards si cumplen
 * las condiciones.
 * 
 * @author javalos
 */
public class AllowRecurrenceFilter extends MaximoEventFilter {
    
    public AllowRecurrenceFilter(PublishInfo pubInfo) throws MXException {
        super(pubInfo);
    }

    @Override
    protected boolean stopRecurrence(MboRemote mbo) throws MXException, RemoteException {
        /**
         * Inicio Prueba
         * 
         * Se intenta ver qué datos vienen en el IntegrationContext para ver si 
         * viene el servicio empresarial por ejemplo para poder diferenciar, pero
         * no viene.
         */
//        IntegrationContext context = IntegrationContext.getCurrentContext();
//        if ((context != null) && (context.getStringProperty("sourcetype") != null)) {
//            System.out.println("context.getStringProperty(\"sourcetype\"): " + context.getStringProperty("sourcetype"));
//            java.util.Map<String, Object> map = context.getProperties();
//            for (String key : map.keySet()) {
//                System.out.println("key: " + key + "\\t obj: " + map.get(key).toString());
//            }
//        }
//        System.out.println("___mbo SENDERSYSID = " + mbo.getString("sendersysid"));
//        System.out.println("___mbo SENDERSYSID is modified =  " + mbo.isModified("sendersysid"));
//        System.out.println("___userInfo.isInteractive() =  " + mbo.getUserInfo().isInteractive());
        /**
         * Fin Prueba.
         */
        
        return !(mbo.getUserInfo().isInteractive() || (mbo.isModified("sendersysid") && 
                mbo.getString("sendersysid").startsWith("GIS")));
    }
}
