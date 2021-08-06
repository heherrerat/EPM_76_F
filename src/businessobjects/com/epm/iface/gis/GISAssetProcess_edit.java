
package com.epm.iface.gis;

import java.rmi.RemoteException;
import psdi.iface.mos.MosDetailInfo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

/**
 * Permite reutilizar para integraci�n el MboSet autogenerado con la 
 * especificaci�n al llenar el campo clasificaci�n (classstructureid).
 * 
 * @author javalos
 */
public class GISAssetProcess_edit extends GISAssetProcess {

    public GISAssetProcess_edit() throws MXException, RemoteException {
    }

    /**
     * Inhabilitar l�gica de clase base de relleno de plantilla de especificaci�n.
     */
    @Override
    protected void callMboExit() throws MXException, RemoteException {
    }

    @Override
    public MboSetRemote createMboSet(boolean isPrimaryMbo, MboRemote parentMbo, MosDetailInfo mdi, String processTable) throws MXException, RemoteException {
        if (processTable.equals("ASSETSPEC")) {
            if (parentMbo.isModified("classstructureid") && !parentMbo.isNull("classstructureid")) {
                // System.out.println(struc.getName()); // Para ver si mueve el "puntero" de la estructura pero no lo mueve ;)
                this.struc.setEditMode(true);
                return parentMbo.getMboSet("ASSETSPECCLASS");
            }
        }
        return super.createMboSet(isPrimaryMbo, parentMbo, mdi, processTable);
    }

    @Override
    public String[] getKeyArray(String name) throws MXException, RemoteException {
        if (name.equals("ASSETSPEC")) {
            if (primaryMbo.getBoolean("islinear")) {    // Si es lineal omitir el LINEARASSETSPECID!
                return new String[] {"ASSETATTRID","ASSETNUM","SECTION","SITEID"};
            }
        }
        return super.getKeyArray(name);
    }
}
