
package com.epm.app.slxassetpr.virtual;

import java.rmi.RemoteException;
import psdi.app.common.virtual.ChangeStatus;
import psdi.mbo.MboSet;
import psdi.mbo.NonPersistentMboRemote;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class AssetPRChangeStatus extends ChangeStatus implements NonPersistentMboRemote {
    
    public AssetPRChangeStatus(MboSet ms) throws MXException, RemoteException {
        super(ms);
    }
}
