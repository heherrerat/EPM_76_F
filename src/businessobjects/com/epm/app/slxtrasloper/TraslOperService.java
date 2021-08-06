
package com.epm.app.slxtrasloper;

import java.rmi.RemoteException;
import java.util.Date;
import javax.jws.WebMethod;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.custapp.CustomService;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class TraslOperService extends CustomService implements TraslOperServiceRemote {

    public TraslOperService() throws RemoteException {
    }

    public TraslOperService(MXServer mxServer) throws RemoteException {
        super(mxServer);
    }

    @Override
    public void trasladarOperarManual(MboSetRemote woViewSet) throws RemoteException, MXException {
        TraslOperSetRemote mboSet = (TraslOperSetRemote) MXServer.getMXServer().getMboSet("SLXTRASLOPER", woViewSet.getUserInfo());
        woViewSet.getMXTransaction().add(mboSet.getMXTransaction());
        addMbosToSet(woViewSet, true, false, mboSet, null);
        mboSet.borrarEnviados();
        mboSet.marcarActivosTrasladoOperar();
    }    
    
    @Override
    public void enviarJDEdwards(MboSetRemote woViewSet, boolean simular) throws RemoteException, MXException {
        TraslOperSetRemote setA = (TraslOperSetRemote) MXServer.getMXServer().getMboSet("SLXTRASLOPER", woViewSet.getUserInfo());
        TraslOperSetRemote setB = (TraslOperSetRemote) MXServer.getMXServer().getMboSet("SLXTRASLOPER", woViewSet.getUserInfo());
        setA.setWhere("1=0");
        setB.setWhere("1=0");
        woViewSet.getMXTransaction().add(setA.getMXTransaction());
        woViewSet.getMXTransaction().add(setB.getMXTransaction());
        setA.setSourceWoViewSet(woViewSet);
        setB.setSourceWoViewSet(woViewSet);
        addMbosToSet(woViewSet, false, simular, setA, setB);
        setA.enviarJDEdwards();
        setB.enviarJDEdwards();
    }
    
    private void addMbosToSet(MboSetRemote woViewSet, boolean manual, boolean simular, TraslOperSetRemote setA, TraslOperSetRemote setB) throws MXException, RemoteException {
        Date now = MXServer.getMXServer().getDate();
        for (Object mbo : woViewSet.getSelection())
            addMboToSet((MboRemote) mbo, manual, simular, setA, setB, now);
    }
    
    private void addMboToSet(MboRemote wo_vw, boolean manual, boolean simular, TraslOperSetRemote setA, TraslOperSetRemote setB, Date date) throws MXException, RemoteException {
        if (manual || wo_vw.getString("preactivo_calc").equals(wo_vw.getString("slxjd_asset")))
            setA.createRecord(wo_vw, simular, date);
        else 
            setB.createRecord(wo_vw, simular, date);
    }

    /**
     * JDEdwards procesa los traslados a operar que se enviaron previamente desde
     * MAXIMO y eventualmente invoca a este servicio web para indicar qué preactivos
     * se procesaron correctamente y cuáles dieron error.
     */
    @Override
    @WebMethod(operationName="resultadoTrasladoOperar")
    public void resultadoTrasladoOperar(String numPreactivo, boolean esError, String msjeError) throws MXException, RemoteException {
        TraslOperSetRemote trasloperSet = (TraslOperSetRemote) MXServer.getMXServer().getMboSet("SLXTRASLOPER", MXServer.getMXServer().getSystemUserInfo());
        trasloperSet.setWhere("slxpreactivo = '" + numPreactivo + "'");
        if (trasloperSet.isEmpty())
            throw new MXApplicationException("slxtrasloper", "preactNotFound", new Object[] {numPreactivo});
        String status = esError ? "ERRORJDE" : "PROCESADO";
        while (trasloperSet.moveNext() != null) {
            trasloperSet.setValue("estado", status, 2L);
            if (esError)
                trasloperSet.setValue("msjeerror", msjeError, 2L);
        }
        if (!esError)
            trasloperSet.marcarActivosTrasladoOperar();
        trasloperSet.save();
    }
}
