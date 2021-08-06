
package com.epm.app.slxassetpr.virtual;

import java.rmi.RemoteException;
import psdi.app.common.virtual.ChangeStatusSet;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.NonPersistentMboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.mbo.StatefulMboRemote;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class AssetPRChangeStatusSet extends ChangeStatusSet implements NonPersistentMboSetRemote {

    public AssetPRChangeStatusSet(MboServerInterface ms) throws MXException, RemoteException {
        super(ms);
    }

    /**
     * Basado en PRChangeStatusSet
     */
    @Override
    protected MboSetRemote getMboIntoSet(MboRemote mbo) throws MXException, RemoteException {
        MboSetRemote changeSet = getMboServer().getMboSet("SLXASSETPR", getUserInfo());
        SqlFormat sqf = new SqlFormat(mbo, "assetprnum = :assetprnum and siteid = :siteid");
        changeSet.setWhere(sqf.format());
        return changeSet;
    }

    @Override
    protected Mbo getMboInstance(MboSet mboset) throws MXException, RemoteException {
        return new AssetPRChangeStatus(mboset);
    }

    /**
     * Basado en PRChangeStatusSet
     */
    @Override
    protected void changeMboStatus(MboRemote assetPR, MboRemote param) throws MXException, RemoteException {
        ((StatefulMboRemote) assetPR).changeStatus(param.getString("status"), param.getDate("statdate"), param.getString("memo"));
    }
}
