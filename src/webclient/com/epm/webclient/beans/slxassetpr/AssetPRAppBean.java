
package com.epm.webclient.beans.slxassetpr;

import java.rmi.RemoteException;
import java.util.Vector;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.beans.common.StatefulAppBean;

/**
 *
 * @author javalos
 */
public class AssetPRAppBean extends StatefulAppBean {
    
    public int deleteall() throws MXException, RemoteException {
        System.out.println("____en deleteall");
        MboSetRemote lines = getMbo().getMboSet("ASSETPRLINE");
        int i = 0; MboRemote line;
        while ((line = lines.getMbo(i++)) != null)
            line.setValueNull("slxassetprnum", 2L);
        clientSession.getDataBean("reqAssets").reset();
        return 1;
    }
    
    public int deleteslctd() throws MXException, RemoteException {
        MboSetRemote lines = getMbo().getMboSet("ASSETPRLINE");
        Vector selection = lines.getSelection();
        if (selection != null && !selection.isEmpty())
            for (Object line : selection)
                ((MboRemote) line).setValueNull("slxassetprnum", 2L);
        clientSession.getDataBean("reqAssets").reset();
        return 1;
    }
}
