package com.epm.app.mr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.mbo.SqlFormat;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
/*
update maxattribute set classname='com.epm.app.mr.CustFldMRLineSlxContractNum'
where objectname='MRLINE' and attributename='SLXCONTRACTNUM';
update maxattributecfg set classname='com.epm.app.mr.CustFldMRLineSlxContractNum'
where objectname='MRLINE' and attributename='SLXCONTRACTNUM';
commit;
*/
public class CustFldMRLineSlxContractNum extends MAXTableDomain//MboValueAdapter
{
	private static MXLogger logger;

    public CustFldMRLineSlxContractNum(MboValue mbv)
    {
        super(mbv);
        logger = MXLoggerFactory.getLogger("maximo.application");
        /**/       
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldMRLineSlxContractNum.CustFldWPSrvSlxContractNum: INICIO");

        setRelationship("CONTRACT", "");
        String map1[] = {"slxcontractnum"};
        String map2[] = {"contractnum"};
        setLookupKeyMapInOrder(map1, map2);

        logger.debug("00 CustFldMRLineSlxContractNum.CustFldWPSrvSlxContractNum: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
        /**/
    }

    public MboSetRemote getList() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldMRLineSlxContractNum.getList: INICIO");

        Mbo wpService = getMboValue().getMbo();
        String str = "(((status = 'APROBADO' or status = 'EXPIRADO') and contractnum in (select a.contractnum from CONTRACTAUTH a where '" + wpService.getString("siteid") + "' = a.AUTHSITEID)) and (vendor = '" + wpService.getString("vendor") + "' or '" + wpService.getString("vendor") + "' is null)) and ('" + wpService.getString("itemnum") + "' is null or contractnum in (select b.contractnum from CONTRACTLINE b where '" + wpService.getString("itemnum") + "' = b.itemnum and '" + wpService.getString("itemsetid") + "' = b.itemsetid))";
        logger.debug("00.01 CustFldMRLineSlxContractNum.getList: str = " + str);
        SqlFormat sqlf = new SqlFormat(wpService.getUserInfo(), str);
        MboSetRemote mboSet = wpService.getMboSet("$LOOKUPCONTRACT", "CONTRACT", sqlf.format());

        logger.debug("00 CustFldMRLineSlxContractNum.getList: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
        
        return mboSet;
    }

    public void action() throws MXException, RemoteException
    {
        logger.debug("");
        logger.debug("**********************************************************************");
        logger.debug("00 CustFldMRLineStoreloc.action: INICIO");

        super.action();
        CustMRLine mrline = (CustMRLine)getMboValue().getMbo();
        mrline.voManejoCuentas();

    	logger.debug("00 CustFldMRLineStoreloc.action: FINAL");
        logger.debug("**********************************************************************");
        logger.debug("");
    }
}

