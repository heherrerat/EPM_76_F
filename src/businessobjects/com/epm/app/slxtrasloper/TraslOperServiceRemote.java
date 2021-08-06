
package com.epm.app.slxtrasloper;

import java.rmi.RemoteException;
import javax.jws.WebMethod;
import psdi.mbo.MboSetRemote;
import psdi.server.AppServiceRemote;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public interface TraslOperServiceRemote extends AppServiceRemote {
    public void trasladarOperarManual(MboSetRemote woSet) throws RemoteException, MXException;
    public void enviarJDEdwards(MboSetRemote woSet, boolean simular) throws RemoteException, MXException;
    @WebMethod(operationName="resultadoTrasladoOperar")
    public void resultadoTrasladoOperar(String numPreactivo, boolean esError, String msjeError) throws MXException, RemoteException;
}
