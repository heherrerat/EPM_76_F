
package com.epm.app.slxaltasbajas;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import psdi.iface.mic.InvokeChannelCache;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.custapp.CustomMboSet;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class AltasBajasSet extends CustomMboSet implements AltasBajasSetRemote {
    
    public AltasBajasSet(MboServerInterface ms) throws RemoteException {
        super(ms);
    }

    @Override
    protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
        return new AltasBajas(ms);
    }
    
    @Override
    public void enviarJDEdwards(String tipoEnviar) throws RemoteException, MXException {
        List<MboRemote> mbosToSendList = new ArrayList<>();
        Vector selctn = getSelection();
        if (selctn != null && !selctn.isEmpty()) {
            for (Object o : selctn) {
                MboRemote mbo = (MboRemote) o;
                if (!mbo.getString("tipoalta").equals(tipoEnviar))
                    throw new MXApplicationException("slxaltasbajas", "mezclaTipos", new Object[] {mbo.getString("tipoalta")});
                mbosToSendList.add(mbo);
            }
        }
 
        MboSetRemote sendSet = MXServer.getMXServer().getMboSet("SLXALTASBAJAS", getUserInfo());
        sendSet.setWhere("1=0");
        Date now = MXServer.getMXServer().getDate();
        
        try {
            for (MboRemote mbo : mbosToSendList) {
                mbo.setValue("fechaenvio", now);
                mbo.blindCopy(sendSet);
            }
            InvokeChannelCache.getInstance().getInvokeChannel("JDEAltasBajasActivos").invoke(null, sendSet, this, MboSetRemote.UPDATEONLY, null);
            for (MboRemote mbo : mbosToSendList) {
                mbo.setValue("enviadajde", true);
                mbo.setValueNull("msjeerrorjde");
            }
        } catch (MXException | RemoteException e) {
            for (MboRemote mbo : mbosToSendList) {
                mbo.setValue("msjeerrorjde", e.getMessage().substring(0, Math.min(e.getMessage().length(), 200)));
//                mbo.setValue("msjeerrorjde_longdescription", e.getMessage());     // da error "a value is required for the ldkey field" ¿?.
                mbo.setValue("fechaenvio", now);
            }
//            throw new MXApplicationException("slxaltas", "errorJDE", new Object[] { "JDEdwards arroja el siguiente error: \\n\\n" + e.getMessage() });
            throw new MXApplicationException("slxaltasbajas", "errorJDE", new Object[] {e.getMessage()});
        }
    }
    
    public void enviarAltasBajasJDEdwards_old() throws MXException, RemoteException {
        try {
            InvokeChannelCache.getInstance().getInvokeChannel("JDEAltasBajasActivos").invoke(null, this, this, MboSetRemote.UPDATEONLY, null);
            for (int i = 0; i < getSize(); i++)     // revisar el getSize(), paginación, etc.
                getMbo(i).setValue("enviadajde", true);
        } catch (MXException | RemoteException e) {
            for (int i = 0; i < getSize(); i++)
                getMbo(i).setValue("msjeerrorjde", e.getMessage());
        }
    }
}
