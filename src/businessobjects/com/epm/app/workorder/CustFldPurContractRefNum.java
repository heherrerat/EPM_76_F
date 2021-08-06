
package com.epm.app.workorder;

import java.rmi.RemoteException;
import psdi.app.contract.ContractRemote;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.SqlFormat;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 * Dominio tabla; maneja la selección y validación de un contrato, copiado y 
 * limpieza de campos; basada en: psdi.app.common.purchasing.FldPurContractRefNum;
 * la diferencia que tiene con esta clase es que se puede seleccionar un contrato
 * de cualquier site.
 * 
 * @author javalos
 */
public class CustFldPurContractRefNum extends MAXTableDomain {
    
    public CustFldPurContractRefNum(MboValue mbv) throws MXException, RemoteException {
        super(mbv);
        setRelationship("contractauth", "");

        setKeyMap("contractauth", new String[] {"vendor", "slxcontractrefid", "slxcontractrefnum", "slxcontractsite"}, new String[] {"vendor", "contractid", "contractnum", "authsiteid"});
        setKeyMap("purchview", new String[] { "vendor", "slxcontractrefid", "slxcontractrefnum", "slxcontractrefrev" }, new String[] { "vendor", "contractid", "contractnum", "revisionnum" });
    }

    @Override
    public void validate() throws MXException, RemoteException {
        if (getMboValue().isNull()) {
            return;
        }
        super.validate();

        MboRemote cntrct = getContract();
        Mbo mbo = getMboValue().getMbo();
        if (cntrct == null) {
            if (getMboValue().getPreviousValue().asString().equals("")) {
                mbo.setValueNull("slxcontractrefid", 2L);
                mbo.setValueNull("slxcontractrefrev", 2L);
            }
            throw new MXApplicationException("po", "contractnotapproved");
        }
        if (!cntrct.getBoolean("porequired")) {
            if (getMboValue().getPreviousValue().asString().equals("")) {
                mbo.setValueNull("slxcontractrefid", 2L);
                mbo.setValueNull("slxcontractrefrev", 2L);
            }
            throw new MXApplicationException("po", "poisrequired");
        }
        if (!mbo.isNull("slxcontractsite")) {   // Agregado (solo el "if").
            MboRemote cntctAuth = getContractAuth(cntrct);
            if (cntctAuth == null) {
                if (getMboValue().getPreviousValue().asString().equals("")) {
                    mbo.setValueNull("slxcontractrefid", 2L);
                    mbo.setValueNull("slxcontractrefrev", 2L);
                }
                Object[] param = { mbo.getString("slxcontractrefnum") };
                throw new MXApplicationException("po", "contractisnotauthorized", param);
            }
        }
        try {
            ((ContractRemote) cntrct).validateDates();
        
        } catch (MXException mxe) {
            if (getMboValue().getPreviousValue().asString().equals("")) {
                mbo.setValueNull("slxcontractrefid", 2L);
                mbo.setValueNull("slxcontractrefrev", 2L);
            }
            throw new MXApplicationException("contract", "InvalidDate");
        }
    }
    
    @Override
    public MboSetRemote getList() throws MXException, RemoteException {
        Mbo mbo = getMboValue().getMbo();
        String status = getTranslator().toExternalList("CONTRACTSTATUS", "APPR", mbo);
        String currentDate = SqlFormat.getDateFunction(MXServer.getMXServer().getDate());
        String where = "contractnum in (select contractnum from contract where status in (" + status + ") and porequired = :yes and revisionnum=contractauth.revisionnum and orgid = contractauth.orgid " + " and (" + currentDate + " between startdate and enddate or (" + currentDate + ">=startdate and enddate is null))) ";
        where = where + " and authorgid = :1";
        if (!mbo.isNull("slxcontractsite"))
            where += " and authsiteid=:2";
        if (!mbo.isNull("vendor"))
            where += " and vendor=:3";
        SqlFormat sqf = new SqlFormat(mbo.getUserInfo(), where);
        sqf.setObject(1, "CONTRACT", "ORGID", mbo.getString("orgid"));
        if (!mbo.isNull("slxcontractsite"))
            sqf.setObject(2, "CONTRACTAUTH", "AUTHSITEID", mbo.getString("slxcontractsite"));
        if (!mbo.isNull("vendor"))
            sqf.setObject(3, "CONTRACT", "VENDOR", mbo.getString("vendor"));
        return mbo.getMboSet("$contract", "CONTRACTAUTH", sqf.format());
    }

    @Override
    public void action() throws MXException, RemoteException {
        if (getMboValue().isNull()) {
            clearFields();
            return;
        } 
        MboRemote cntrct = (MboRemote) getContract();
        if (cntrct == null) {
            return;
        }
        Mbo mbo = getMboValue().getMbo();
//        ((PurchasingMbo) mbo).copyFromContract(cntrct, null);
        mbo.setValue("slxcontractrefid", cntrct.getLong("contractid"), 2L);
        mbo.setValue("slxcontractrefrev", cntrct.getString("revisionnum"), 2L);
//        mbo.setValue("currencycode", cntrct.getString("currencycode"), 2L); // Agregado c/r a clase estándar

        if (mbo.getString("vendor").equals("")) {
            mbo.setValue("vendor", cntrct.getString("vendor"), 11L);
        }
        mbo.setFieldFlag("vendor", 7L, true);
    }
    
    protected void clearFields() throws MXException, RemoteException {
        Mbo mbo = getMboValue().getMbo();
        mbo.setValueNull("slxcontractrefid", 2L);
        mbo.setValueNull("slxcontractrefrev", 2L);
        mbo.setValueNull("slxcontractsite", 2L);
        mbo.setValueNull("vendor", 2L);
//        mbo.setValue("currencycode", "COP", 2L);
    }

    public MboRemote getContract() throws MXException, RemoteException {
        Mbo mbo = getMboValue().getMbo();

        String status = getTranslator().toExternalList("CONTRACTSTATUS", "APPR", mbo);
        SqlFormat sqf = null;
        UserInfo userInfo = mbo.getUserInfo();
        if ((!mbo.isNull("slxcontractrefid")) || (userInfo.isInteractive())) {
            sqf = new SqlFormat(mbo, "contractid = :1 and status in (" + status + ")");
            sqf.setLong(1, mbo.getLong("slxcontractrefid"));
        } else {
            sqf = new SqlFormat(mbo, "contractnum=:1 and revisionnum=:2 and orgid=:3 and status in (" + status + ")");
            sqf.setObject(1, "CONTRACT", "contractnum", mbo.getString("slxcontractrefnum"));
            sqf.setObject(2, "CONTRACT", "revisionnum", mbo.getString("slxcontractrefrev"));
            sqf.setObject(3, "CONTRACT", "orgid", mbo.getString("orgid"));
        }
        MboSetRemote cntrctSet = ((Mbo) mbo).getMboSet("$findcontract", "CONTRACT", sqf.format());
        cntrctSet.reset();
        MboRemote cntrct = cntrctSet.getMbo(0);
        return cntrct;
    }

    public MboRemote getContractAuth(MboRemote cntrct) throws MXException, RemoteException {
        Mbo mbo = getMboValue().getMbo();
        SqlFormat sqf = new SqlFormat(mbo.getUserInfo(), "contractid=:1 and vendor=:3 and authsiteid=:4 and authorgid=:5");
        sqf.setLong(1, cntrct.getLong("contractid"));
        if (mbo.getString("vendor").equals("")) {
            MboRemote cntrctAuthDflt = cntrct.getMboSet("AUTHDEFAULT").getMbo(0);
            sqf.setObject(3, mbo.getName(), "vendor", cntrctAuthDflt.getString("vendor"));
        } else {
            sqf.setObject(3, mbo.getName(), "vendor", mbo.getString("vendor"));
        }
        sqf.setObject(4, mbo.getName(), "siteid", mbo.getString("slxcontractsite"));
        sqf.setObject(5, mbo.getName(), "orgid", mbo.getString("orgid"));
        MboRemote cntrctAuth = ((Mbo) cntrct).getMboSet("$conauthpur", "CONTRACTAUTH", sqf.format()).getMbo(0);
        return cntrctAuth;
    }
}
