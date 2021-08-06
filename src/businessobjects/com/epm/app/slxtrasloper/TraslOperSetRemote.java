
package com.epm.app.slxtrasloper;

import java.rmi.RemoteException;
import java.util.Date;
import javax.jws.WebMethod;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.custapp.CustomMboSetRemote;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public interface TraslOperSetRemote extends CustomMboSetRemote {
    
    public MboRemote createRecord(MboRemote wo, boolean simular) throws MXException, RemoteException;
    public void createRecord(MboRemote wo, boolean simular, Date date) throws MXException, RemoteException;
    public void createRecords(MboSetRemote woSet, boolean simular) throws MXException, RemoteException;
    public void enviarJDEdwards() throws RemoteException, MXException;
    public void marcarActivosTrasladoOperar() throws MXException, RemoteException;
    public void setSourceWoViewSet(MboSetRemote woSet);
    public void borrarEnviados() throws MXException, RemoteException;
}
