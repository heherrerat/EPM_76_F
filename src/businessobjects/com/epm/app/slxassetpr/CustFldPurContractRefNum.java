
package com.epm.app.slxassetpr;

import java.rmi.RemoteException;
import psdi.app.common.purchasing.FldPurContractRefNum;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.SqlFormat;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 * Clase basada en psdi.app.common.purchasing.FldPurContractRefNum.
 * 
 * @author javalos
 */
public class CustFldPurContractRefNum extends MAXTableDomain {
    
    public CustFldPurContractRefNum(MboValue mbv) throws MXException, RemoteException {
        super(mbv);
        setRelationship("contractauth", getWhere());

        setKeyMap("contractauth", new String[]{"vendor", "contractrefid", "contractrefnum"}, new String[]{"vendor", "contractid", "contractnum"});
        setKeyMap("purchview", new String[]{"contractrefid", "contractrefnum", "contractrefrev", "vendor"}, new String[]{"contractid", "contractnum", "revisionnum", "vendor"});
        setKeyMap("leaseview", new String[]{"contractrefid", "contractrefnum", "contractrefrev", "vendor"}, new String[]{"contractid", "contractnum", "revisionnum", "vendor"});
        
        setKeyMap("sfwview", new String[]{"contractrefid", "contractrefnum", "contractrefrev", "vendor"}, new String[]{"contractid", "contractnum", "revisionnum", "vendor"});
    }

    @Override
    public MboSetRemote getList() throws MXException, RemoteException {
        Mbo mbo = getMboValue().getMbo();
        return mbo.getMboSet("$contract", "CONTRACTAUTH", getListWhere());
    }
    
    final public String getWhere() throws MXException, RemoteException {
        Mbo mbo = getMboValue().getMbo();
        String status = getTranslator().toExternalList("CONTRACTSTATUS", "APPR", mbo);
        String currentDate = SqlFormat.getDateFunction(MXServer.getMXServer().getDate());
        String tempString = "contractnum in (select contractnum from contract where status in (" + status + ") and porequired = :yes and revisionnum=contractauth.revisionnum and orgid = contractauth.orgid " + " and (" + currentDate + " between startdate and enddate or (" + currentDate + ">=startdate and enddate is null))) ";
        tempString = tempString + " and authsiteid = :2 and authorgid = :3";
        SqlFormat sqlf = new SqlFormat(mbo.getUserInfo(), tempString);
        String siteID = mbo.getString("siteid");
        String orgID = mbo.getString("orgid");
        sqlf.setObject(2, "PO", "SITEID", siteID);
        sqlf.setObject(3, "CONTRACT", "ORGID", orgID);
        return sqlf.format();
    }
    
    public String getListWhere() throws MXException, RemoteException {
        String where = getWhere();
        MboRemote mbo = getMboValue().getMbo();
        if (!mbo.isNull("vendor"))
            where += " and vendor='" + mbo.getString("vendor") + "'";
        return where;
    }

    @Override
    public void action() throws MXException, RemoteException {
        AssetPR mbo = (AssetPR) getMboValue().getMbo();
        if (getMboValue().isNull()) {
            clearFields();
//            return;
        } else {
            mbo.setValue("currencyCode", mbo.getString("CONTRACTREF.currencyCode"), 2L);
            mbo.setValue("slxalmacenable", true, 2L);
            mbo.setValue("slxmenorcuantia", false, 2L);
        }
//        MboRemote contractRemote = (MboRemote) getContract();
//        if (contractRemote == null) {
//            return;
//        }
//        Mbo mbo = getMboValue().getMbo();
//
////        ((PurchasingMbo) mboRemote).copyFromContract(contractRemote, null);
//        mbo.setValue("contractrefid", contractRemote.getLong("contractid"), 2L);
//        mbo.setValue("contractrefrev", contractRemote.getString("revisionnum"), 2L);
//        mbo.setValue("vendor", contractRemote.getString("vendor"), 11L);
        
        mbo.setContractFlags();
    }
    
    private void clearFields() throws MXException, RemoteException {
        Mbo mbo = getMboValue().getMbo();
        mbo.setValueNull("contractrefid", 2L);
        mbo.setValueNull("contractrefrev", 2L);
        mbo.setValueNull("vendor", 2L);
        mbo.setValueNull("itemnum", 2L);
        mbo.setValue("currencycode", "COP", 2L);
        mbo.setValue("slxalmacenable", false, 2L);
        mbo.setValue("slxmenorcuantia", true, 2L);
    }
    
}
