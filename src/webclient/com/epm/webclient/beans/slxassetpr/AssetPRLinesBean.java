
package com.epm.webclient.beans.slxassetpr;

import java.rmi.RemoteException;
import java.util.Vector;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

/**
 *
 * @author javalos
 */
public class AssetPRLinesBean extends DataBean {
    
    public int deleteall() throws MXException, RemoteException {
        System.out.println("____en deleteall");
        MboSetRemote lines = MXServer.getMXServer().getMboSet("ASSET", getMboSet().getUserInfo());
        int i = 0; MboRemote line;
        while ((line = lines.getMbo(i++)) != null)
            line.setValueNull("slxassetprnum", 2L);
//        clientSession.getDataBean("reqAssets").reset();
//        lines.save();
        save();
        reset();
        return 1;
    }
    
    public int deleteslctd() throws MXException, RemoteException {
        MboSetRemote lines = getMboSet();
        Vector selection = lines.getSelection();
        if (selection != null && !selection.isEmpty())
            for (Object line : selection)
                ((MboRemote) line).setValueNull("slxassetprnum", 2L);
//        clientSession.getDataBean("reqAssets").reset();
//        lines.save();
        save();
        app.getAppBean().save();
        reset();
        return 1;
    }
}
