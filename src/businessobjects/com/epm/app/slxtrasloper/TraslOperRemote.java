
package com.epm.app.slxtrasloper;

import java.rmi.RemoteException;
import psdi.mbo.custapp.CustomMboRemote;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public interface TraslOperRemote extends CustomMboRemote {
    public boolean is5_1() throws MXException, RemoteException;
}
