
package com.epm.app.workorder;

import java.rmi.RemoteException;
import psdi.plusd.app.workorder.PlusDWORemote;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public interface CustPlusDWORemote extends PlusDWORemote {
    
    public void cancelaOTsJDE_old() throws MXException, RemoteException;
    public void validaCancelaTareaFSM() throws MXException, RemoteException;
    public void marcaEnvioCancelaTareaFSM() throws MXException, RemoteException;
}
