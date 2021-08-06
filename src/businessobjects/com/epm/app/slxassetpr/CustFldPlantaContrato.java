
package com.epm.app.slxassetpr;

import java.rmi.RemoteException;
import psdi.app.site.FldSiteID;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class CustFldPlantaContrato extends FldSiteID {

    public CustFldPlantaContrato(MboValue mbv) throws MXException {
        super(mbv);
    }

    @Override
    public void initValue() throws MXException, RemoteException {
        super.initValue();
        MboRemote mbo = getMboValue().getMbo();
        if (mbo.isNew())
            getMboValue().setValue(mbo.getString("siteid"), 2L);
    }
    
    @Override
    public void action() throws MXException, RemoteException {
        super.action();
        getMboValue().getMbo().setValueNull("contractrefnum", 2L);
    }
}
