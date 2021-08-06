
package com.epm.app.slxassetpr;

import java.rmi.RemoteException;
import psdi.mbo.Mbo;
import psdi.mbo.MboConstants;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class CustFldSlxAlmacenable extends MboValueAdapter {

    public CustFldSlxAlmacenable(MboValue mbv) {
        super(mbv);
    }

    @Override
    public void action() throws MXException, RemoteException {
        Mbo mbo = getMboValue().getMbo();
        Boolean almacenable = getMboValue().getBoolean();
        mbo.setFieldFlag("slxalmacen", 7L, !almacenable);
        mbo.setFieldFlag("slxalmacen", MboConstants.REQUIRED, almacenable);
        if (!almacenable)
            mbo.setValueNull("slxalmacen", 2L);
    }
}
