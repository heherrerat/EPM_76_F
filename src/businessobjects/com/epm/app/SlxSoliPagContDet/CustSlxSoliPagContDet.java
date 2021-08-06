package com.epm.app.SlxSoliPagContDet;

import java.rmi.RemoteException;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.custapp.CustomMbo;
import psdi.mbo.custapp.CustomMboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class CustSlxSoliPagContDet extends CustomMbo implements CustomMboRemote {

    private static MXLogger logger;

    public CustSlxSoliPagContDet(MboSet ms) throws MXException, RemoteException {
        super(ms);
        logger = MXLoggerFactory.getLogger("maximo.application");
    }

    @Override
    public void add() throws MXException, RemoteException {
        logger.debug("************************************************************");
        logger.debug("00 CustSlxSoliPagContDet.add: INICIO");
        MboRemote mboOwner = getOwner();
        logger.debug("00 CustSlxSoliPagContDet.add: ESTADO: " + mboOwner.getString("status"));
        if (mboOwner.getString("status").equals("CONFIRMADA")) {
            throw new MXApplicationException("SLX_SOLI_PAG_CONT", "wrongstatus");
        }
        logger.debug("00 CustSlxSoliPagContDet.add: Se inserta el valor autonumerico a linea");
        MboSetRemote mboSetDet = mboOwner.getMboSet("SLX_SOLI_PAG_CONT_DET");
        setValue("nroline", mboSetDet.max("nroline") + 1, 2L);
        logger.debug("00 CustSlxSoliPagContDet.add: FIN");
        logger.debug("************************************************************");
        super.add();
    }

    @Override
    public void undelete() throws MXException, RemoteException {
        super.undelete();
        setValue("nroline", getThisMboSet().max("nroline") + 1, 2L);
    }
    
}
