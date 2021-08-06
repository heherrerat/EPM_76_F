
package com.epm.app.slxtrasloper;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import psdi.iface.mic.InvokeChannelCache;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.custapp.CustomMboSet;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class TraslOperSet extends CustomMboSet implements TraslOperSetRemote {
    
    public TraslOperSet(MboServerInterface ms) throws RemoteException {
        super(ms);
    }

    @Override
    protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
        return new TraslOper(ms);
    }

    MboSetRemote sourceWoViewSet;
    
    @Override
    public void setSourceWoViewSet(MboSetRemote woViewSet) {
        sourceWoViewSet = woViewSet;
    }
    
    @Override
    public MboRemote createRecord(MboRemote woview, boolean simular) throws MXException, RemoteException {
        MboRemote newTrasl = add();
        newTrasl.setValue("siteid", woview.getString("siteid"));
        newTrasl.setValue("wonum", woview.getString("wonum"));
        newTrasl.setValue("assetnum", woview.getString("assetnum"));
        newTrasl.setValue("slxpreactivo", woview.getString("preactivo_calc"));
        newTrasl.setValue("retiropormaterial", woview.getString("retiropormaterial"));
        newTrasl.setValue("simular", simular);
        return newTrasl;
    }
    
    @Override
    public void createRecord(MboRemote woview, boolean simular, Date date) throws MXException, RemoteException {
        createRecord(woview, simular).setValue("createdate", date, 11L);
    }
    
    @Override
    public void createRecords(MboSetRemote woViewSet, boolean simular) throws MXException, RemoteException {
        int i = 0; MboRemote woview;
        Date now = MXServer.getMXServer().getDate();
        while ((woview = woViewSet.getMbo(i++)) != null) {
            createRecord(woview, simular);
            setValue("createdate", now);    // para que todos queden parejos con la misma fecha (de hecho, sería la única forma de identificarlos como grupo)
        }
    }

    /**
     * Traslado a operar en forma manual (no va a JDEdwards, solo los marca).
     * Esta opción es para cuando el 'problema' se arregla por el lado de JDE y 
     * por lo tanto en MAXIMO solo queda marcarlos como ya 'procesados' para que
     * salgan de la lista de pendientes.
     */    
    @Override
    public void marcarActivosTrasladoOperar() throws MXException, RemoteException {
        Set<String> set = new HashSet();
        String filter = "";
        for (int i = 0; moveTo(i) != null; i++) {
            String assetnum = getString("assetnum");
            if (set.add(assetnum))
                filter += ",'" + assetnum + "'";
        }
        MboSetRemote assetSet = getMbo(0).getMboSet("$assetsTraslOper", "ASSET", "siteid=:siteid and assetnum in (" + filter.substring(1) + ")");
        while (assetSet.moveNext() != null)
            assetSet.setValue("slxtrasladooperar", true, 2L);
    }
    
    /**
     * Solo se permite un Set con un solo código de preactivo y del mismo tipo:
     * 5.1 o 5.2. Además deben estar presentes todos los registros del preactivo
     * que haya en la base de datos.
     */
    private void validarParaEnvio() throws MXException, RemoteException {
        if (getSize() > 0) {
            TraslOperRemote first = (TraslOperRemote) moveFirst();
            while (moveNext() != null) {
                TraslOperRemote current = (TraslOperRemote) getMbo();
                if (current.is5_1() != first.is5_1())
                    throw new MXApplicationException("slxtrasloper", "mezclaTipos", new Object[] {});
                else if (!current.getString("slxpreactivo").equals(first.getString("slxpreactivo")))
                    throw new MXApplicationException("slxtrasloper", "mezclaPreactivos", new Object[] {});
                else if (current.getBoolean("simular") != first.getBoolean("simular"))
                    throw new MXApplicationException("slxtrasloper", "mezclaSimular", new Object[] {});
            }
            if (sourceWoViewSet != null) {
                MboSetRemote qryWoViewSet = MXServer.getMXServer().getMboSet("SLXTRASLOPERVIEW", getUserInfo());
                qryWoViewSet.setUserWhere(sourceWoViewSet.getUserWhere());
                qryWoViewSet.setQbe("siteid", "=" + first.getString("siteid"));
                qryWoViewSet.setQbe("preactivo_calc", "=" + first.getString("slxpreactivo"));
                if (qryWoViewSet.count() != getSize())
                    throw new MXApplicationException("slxtrasloper", "masOTsEnBD", new Object[] { first.getString("slxpreactivo"), qryWoViewSet.count() });
            }
        }
    }
    
    @Override
    public void enviarJDEdwards() throws RemoteException, MXException {
        if (getSize() > 0) {
            TraslOperRemote first = (TraslOperRemote) moveFirst();
            validarParaEnvio();
            String channelName = "JDETrasladoOperActivos52";
            if (first.is5_1()) {
                channelName = "JDETrasladoOperActivos51";
                calcularPctjeDistrib();
            }
            borrarEnviados();
            InvokeChannelCache.getInstance().getInvokeChannel(channelName).invoke(null, this, this, MboSetRemote.UPDATEONLY, null);
        }
    }
    
    private void calcularPctjeDistrib() throws MXException, RemoteException {
        double costoTotal = 0;
        for (int i = 0; moveTo(i) != null; i++) {
            double costo = getDouble("WORKORDER.actmatcost") + getDouble("WORKORDER.actservcost") + getDouble("WORKORDER.actlabcost") + getDouble("WORKORDER.acttoolcost");
            if (getBoolean("retiropormaterial"))
                costo += getMbo().getDouble("ASSET.purchaseprice");
            costoTotal += costo;
            setValue("pctjedistrib", costo, 11L);
        }
        if (costoTotal > 0)
            for (int i = 0; moveTo(i) != null; i++)
                setValue("pctjedistrib", (getDouble("pctjedistrib") / costoTotal) * 100.0, 11L);
    }
    
    @Override
    public void borrarEnviados() throws MXException, RemoteException {
        MboSetRemote borrarSet = getMbo(0).getMboSet("$borrar", "SLXTRASLOPER", "slxpreactivo=:slxpreactivo");
        borrarSet.deleteAll(2L);
    }
}
