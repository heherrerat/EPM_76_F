
package com.epm.app.slxtrasloper;

import java.rmi.RemoteException;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class CustFldContratoAlmacenable extends MboValueAdapter {

    public CustFldContratoAlmacenable(MboValue mbv) {
        super(mbv);
    }

    @Override
    public void initValue() throws MXException, RemoteException {
        super.initValue();
        MboRemote assetpr = getMboValue().getMbo().getMboSet("SLXASSETPR").getMbo(0);
        if (assetpr != null) {
            boolean contrAlmcbl = !assetpr.isNull("contractrefnum") && assetpr.getBoolean("slxalmacenable");
            getMboValue().setValue(contrAlmcbl, 11L);
            getMboValue().getMbo().setValue("retiropormaterial", contrAlmcbl, 11L);
        }
    }
}