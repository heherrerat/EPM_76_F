package com.epm.webclient.beans.slxassetpr;

import java.rmi.RemoteException;
import java.util.Vector;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXApplicationYesNoCancelException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import static psdi.webclient.system.beans.WebClientBean.EVENT_HANDLED;
import psdi.webclient.system.controller.WebClientEvent;

/**
 *
 * @author javalos
 */
public class SelectAssetsBean extends DataBean {

    @Override
    public synchronized int execute() throws MXException, RemoteException {
        WebClientEvent event = this.clientSession.getCurrentEvent();
        int msgRet = event.getMessageReturn();
        if (msgRet < 0) {
            int count = clientSession.getDataBean("tableBean").count();
            System.out.println("____count: " + count);
            Vector slctn = clientSession.getDataBean("tableBean").getSelection();
            if (slctn.size() <= 0) {
                if (count > 500)
                    throw new MXApplicationException("designer", "generic", new Object[] {"Debe restringir la búsqueda a no más de 500 registros"});
                throw new MXApplicationYesNoCancelException("ConfirmListActionId", "jspmessages", "ConfirmListAction", new String[]{Integer.toString(count)});
            }
            addAssets();
        }
        else if (msgRet == 2) {
            addAssets();
        }
        this.clientSession.getDataBean("tableBean").save();
        DataBean bean = this.app.getDataBean("reqAssets");
        bean = this.clientSession.getDataBean("reqAssets");
//        bean.fireStructureChangedEvent();
//        bean.refreshTable();
//        bean.reloadTable();
//        this.sessionContext.queueRefreshEvent();
        bean.reset();
        return EVENT_HANDLED;   // Sacado de página de Bruno Portaluri (http://maximodev.blogspot.cl/2011/08/call-java-method-on-action-menu-or.html)
//        return EVENT_CONTINUE;
    }
    
    private void addAssets() throws MXException, RemoteException {
        System.out.println("____adding asset!____");
        System.out.println("____dp adding asset!____");
        MboSetRemote mboSet;
        if (this.getMbo().getBoolean("replacecurrentassets")) {
            mboSet = this.app.getResultsBean().getMbo().getMboSet("ASSETPRLINE");
            mboSet.moveFirst();
            do {
                mboSet.setValueNull("slxassetprnum");
//                mboSet.getMbo().delete(2L);
            } while (mboSet.moveNext() != null);
        }
        Vector<MboRemote> slctn = this.clientSession.getDataBean("tableBean").getSelection();
        String currPRnum = this.app.getAppBean().getMbo().getString("assetprnum");
        System.out.println("___PRnum: " + currPRnum);
        if (slctn.size() > 0) {
            for (MboRemote mbo : slctn) {
                System.out.println("____agregando selección");
                mbo.setValue("slxassetprnum", currPRnum, 2L);
            }
        } else {
            mboSet = this.clientSession.getDataBean("tableBean").getMboSet();
            mboSet.moveFirst();
            do {
                System.out.println("___agregando filtrados");
                mboSet.setValue("slxassetprnum", currPRnum, 2L);
            } while (mboSet.moveNext() != null);
        }
    }
}
