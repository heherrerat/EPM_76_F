
package com.epm.app.slxassetpr;

import java.rmi.RemoteException;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class AssetPRSet extends MboSet {

    public AssetPRSet(MboServerInterface ms) throws RemoteException {
        super(ms);
    }

    @Override
    protected Mbo getMboInstance(MboSet mboset) throws MXException, RemoteException {
        return new AssetPR(mboset);
    }
}
