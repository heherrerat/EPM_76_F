
package com.epm.app.slxaltasbajas;

import java.rmi.RemoteException;
import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class FldDeltaLongitud extends MboValueAdapter {

    public FldDeltaLongitud(MboValue mbv) {
        super(mbv);
    }

    @Override
    public void action() throws MXException, RemoteException {
        Mbo mbo = mboValue.getMbo();
        double value = mboValue.getDouble();
        mbo.setValue("deltalongitud", Math.abs(value), 11L);
        if (mbo.isNull("tipoalta"))
            mbo.setValue("tipoalta", value > 0.0 ? "ALTA" : "BAJA", 11L);
    }

    @Override
    public void validate() throws MXException, RemoteException {
        if (mboValue.getDouble() == 0.0D)
            throw new MXApplicationException("designer", "generic", new Object[] { "La variación de longitud no puede ser cero." });
    }
}
