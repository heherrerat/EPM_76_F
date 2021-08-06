
package com.epm.iface.app.slxaltasbajas;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import psdi.iface.mic.MicSetIn;
import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class AltasFindAssetSetIn extends MicSetIn {

    public AltasFindAssetSetIn() throws MXException, RemoteException {
    }

    @Override
    public int presetMboRules() throws MXException, RemoteException {
        if (processTable.equals("SLXALTASBAJAS")) {
            if (struc.isCurrentDataNull("ASSETNUM")) {
                List<String[]> namesList = new ArrayList<>(2);
                namesList.add(new String[] {"SLXAGRUPAMIENTO", "slxagrupamiento"});
                namesList.add(new String[] {"G3E_FID", "slxgis"});
                for (String[] names : namesList) {
                    String value = struc.getCurrentData(names[0]);
                    if (value != null && !value.isEmpty()) {
                        MboRemote asset = mbo.getMboSet("$findAsset", "ASSET", names[1] + "='" + value + "'").getMbo(0);
                        if (asset == null) {
                            throw new MXApplicationException("slxaltasbajas", "assetNotFound", new Object[] {names[1], value});
                        } else {
                            mbo.setValue("siteid", asset.getString("siteid"), 2L);
                            mbo.setValue("assetnum", asset.getString("assetnum"), 2L);
                            struc.removeFromCurrentData("SITEID");
                            struc.removeFromCurrentData("ASSETNUM");
                            break;
                        }
                    }
                }
            }
        }
        return 1;
    }
}
