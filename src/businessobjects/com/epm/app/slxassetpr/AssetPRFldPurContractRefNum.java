
package com.epm.app.slxassetpr;

import com.epm.app.workorder.CustFldPurContractRefNum;
import java.rmi.RemoteException;
import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class AssetPRFldPurContractRefNum extends CustFldPurContractRefNum {
    
    public AssetPRFldPurContractRefNum(MboValue mbv) throws MXException, RemoteException {
        super(mbv);
    }

    @Override
    public void action() throws MXException, RemoteException {
        super.action();
        Mbo mbo = getMboValue().getMbo();
        if (!getMboValue().isNull()) {
            mbo.setValue("slxalmacenable", true, 2L);
            mbo.setValue("slxmenorcuantia", false, 2L);
        }
        ((AssetPR) mbo).setContractFlags();
    }

    @Override
    protected void clearFields() throws MXException, RemoteException {
        super.clearFields();
        Mbo mbo = getMboValue().getMbo();
        mbo.setValueNull("itemnum", 2L);
        mbo.setValue("slxalmacenable", false, 2L);
        mbo.setValue("slxmenorcuantia", true, 2L);        
    }
}
