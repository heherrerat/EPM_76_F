
package com.epm.app.slxassetpr;

import java.rmi.RemoteException;
import psdi.mbo.MboConstants;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValueInfo;
import psdi.mbo.StatefulMbo;
import psdi.mbo.StatusHandler;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class AssetPR extends StatefulMbo {

    public AssetPR(MboSet ms) throws RemoteException {
        super(ms);
    }

    @Override
    public void init() throws MXException {
        super.init();
        try {
            if (getString("status").equals("APPR"))
                setFlag(7L, true);
            else {
                setContractFlags();
                setFieldFlag("vendor", MboConstants.REQUIRED, true);
                setFieldFlag("currencycode", 7L, true);
            }
        } catch (RemoteException ex) {
        }
    }
    
    public void setContractFlags() throws MXException, RemoteException {
        boolean contrIsNull = isNull("contractrefnum");
        setFieldFlag("vendor", 7L, !contrIsNull);
//        setFieldFlag("itemnum", 7L, contrIsNull);
//        setFieldFlag("itemnum", MboConstants.REQUIRED, !contrIsNull);
        setFieldFlag("slxalmacenable", 7L, contrIsNull);
        setFieldFlag("slxmenorcuantia", 7L, !contrIsNull);
        
    }

    @Override
    protected StatusHandler getStatusHandler() {
        return new AssetPRStatusHandler(this);
    }

    @Override
    protected MboSetRemote getStatusHistory() throws MXException, RemoteException {
        return null;
    }

    @Override
    public String getStatusListName() {
        return "SLXASSETPRSTATUS";
    }

    /**
     * Se deben copiar algunos campos a mano ya que al parecer no copia el 
     * contractrefid por ser un ID y eso origina (por otras clases) que se 
     * limpien los demás; así que se setean después con flag 11L.
     */
    @Override
    public MboRemote duplicate() throws MXException, RemoteException {
        MboRemote newMbo = copy();
        newMbo.setValue("contractrefid", getString("contractrefid"), 11L);
        newMbo.setValue("contractrefnum", getString("contractrefnum"), 11L);
        newMbo.setValue("contractrefrev", getString("contractrefrev"), 11L);
        newMbo.setValue("vendor", getString("vendor"), 11L);
        newMbo.setValue("itemnum", getString("itemnum"), 11L);
        newMbo.setValue("slxalmacenable", getString("slxalmacenable"), 11L);
        newMbo.setValue("plantacontrato", getString("plantacontrato"), 11L);
        return newMbo;
    }
    
    @Override
    protected boolean skipCopyField(MboValueInfo mvi) throws RemoteException, MXException {
        return mvi.getName().equals("STATUS");
    }
}
