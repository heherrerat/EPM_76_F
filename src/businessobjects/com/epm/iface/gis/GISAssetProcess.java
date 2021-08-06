package com.epm.iface.gis;

import java.rmi.RemoteException;
import psdi.iface.app.asset.MaxAssetProcess;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class GISAssetProcess extends MaxAssetProcess {

    public GISAssetProcess() throws MXException, RemoteException {
    }

    @Override
    public String[] getKeyArray(String name) throws MXException, RemoteException {
        if (name.equals("ASSET")) {
            if (interfaceName.equals("CONDUCTORPRIM") || interfaceName.equals("TRAMO_GAS") || interfaceName.equals("CONDUCTOR"))
                return new String[] {"PLUSSFEATURECLASS", "SLXAGRUPAMIENTO"};
            else if (!interfaceName.equals("ANILLO") && !interfaceName.equals("ARTERIA") && !interfaceName.equals("ESTACION_VALVULAS") && !interfaceName.equals("RAMAL_GAS") && !interfaceName.equals("UNIDAD_RECTIFIC"))
                return new String[] {"PLUSSFEATURECLASS", "SLXGIS"};
        }
        return super.getKeyArray(name);
    }
    
    @Override
    public int presetMboRules() throws MXException, RemoteException {
        /**
         * Arreglo bug MAXIMO: para que no aparezcan ceros en la descripción 
         * autogenerada de la especificación técnica, se setea en nulo antes de
         * poner el valor; eso de alguna manera arregla la precisión del dato.
         */
        if (mbo.getName().equals("ASSETSPEC"))
            if ("NUMERIC".equals(mbo.getString("ASSETATTRIBUTE.datatype"))) 
                mbo.setValueNull("numvalue", 11L);
        
        return super.presetMboRules();
    }

    @Override
    public void checkValidateErrors() throws MXException, RemoteException {
        super.checkValidateErrors();
        if (mbo.getName().equals("ASSET"))
            if (mbo.isNull("slxg3e_fno"))
                throw new MXApplicationException("designer", "generic", new Object[] {"FNO nulo; debe especificar un FNO para poder aplicar reglas de mapeo"});
    }    
    
    /**
     * Comletar plantilla de especificación.
     */
    @Override
    protected void callMboExit() throws MXException, RemoteException {
        super.callMboExit();
        
        String filter;
        MboRemote asset, newspec;
        MboSetRemote assetSpecSet, classSpecSet;
        
        asset = primaryMbo;
        
        if (asset.isNew()) {
            if (!asset.isNull("classstructureid")) {
                assetSpecSet = asset.getMboSet("ASSETSPECCLASS");
                filter = "";
                for (int i = 0; i < assetSpecSet.getSize(); i++) 
                    filter = filter + " ,'" + assetSpecSet.getMbo(i).getString("assetattrid") + "'";
                if (!filter.isEmpty())
                    filter = " and assetattrid not in (" + filter.substring(2) + ")";
                classSpecSet = asset.getMboSet("CLASSSTRUCTURE").getMbo(0).getMboSet("$missingClassSpecs", "CLASSSPEC", "classstructureid=:classstructureid " + filter);
                while (classSpecSet.moveNext() != null) {
                    newspec = assetSpecSet.add(2L);
                    newspec.setValue("assetattrid", classSpecSet.getString("assetattrid"), 2L);
                    newspec.setValue("linearassetspecid", 0, 2L);
                }
            }
        }
    }
}
