package com.epm.iface.gis;

import java.rmi.RemoteException;
import psdi.iface.app.location.MaxOperLocProcess;
import psdi.iface.mic.StructureObject;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class GISLocationProcess extends MaxOperLocProcess {

    public GISLocationProcess() throws MXException, RemoteException {
    }
    
    @Override
    public int presetMboRules() throws MXException, RemoteException {
        /**
         * Arreglo bug MAXIMO: para que no aparezcan ceros en la descripción 
         * autogenerada de la especificación técnica, se setea en nulo antes de
         * poner el valor; eso de alguna manera arregla la precisión del dato.
         */
        if (mbo.getName().equals("LOCATIONSPEC")) 
            if ("NUMERIC".equals(mbo.getString("ASSETATTRIBUTE.datatype"))) 
                mbo.setValueNull("numvalue", 11L);        
        
        /**
         * Arreglo problema al cambiar ubicación padre;
         * buscar el padre actual en la BD (en el JSON solo viene la actual).
         */
        StructureObject lochierStruc;
        MboRemote lochierMbo;
        String parent_struc;
        
        if (mbo.getName().equals("LOCATIONS")) 
        {            
            lochierStruc = (StructureObject) struc.getChildrenData("LOCHIERARCHY").get(0);
            lochierMbo = mbo.getMboSet("$locHier", "LOCHIERARCHY", "siteid=:siteid and location=:location and systemid='" + lochierStruc.getCurrentData("SYSTEMID") + "'").getMbo(0);
            if (lochierMbo != null) {
                parent_struc = lochierStruc.getCurrentData("PARENT");
                if (!parent_struc.equals(lochierMbo.getString("parent"))) {
                    lochierStruc.setCurrentData("PARENT", lochierMbo.getString("parent"));
                    lochierStruc.setCurrentData("NEWPARENT", parent_struc);
                }
            }
        } else if (mbo.getName().equals("LOCHIERARCHY")) {
            if (!mbo.isNew()) {     // Cuando es nuevo se cae el validate porque todavía no se setea al systemid y además no es necesario
                // Arreglo bug (MAXIMO?) en clase estándar no puede pisar campo PARENT con NEWPARENT debido al flag de integración!
                //mbo.setValue("parent", data.getCurrentData('PARENT'), 2L)
                struc.removeFromCurrentData("PARENT");
            }
        }
        return super.presetMboRules();
    }   
    
    /**
     * Completar plantilla de especificación.
     */
    @Override
    protected void callMboExit() throws MXException, RemoteException {
        super.callMboExit();
        
        String filter;
        MboRemote loctn, newspec;
        MboSetRemote loctnSpecSet, classSpecSet;
        
        loctn = primaryMbo;
        
        if (loctn.isNew()) {
            if (!loctn.isNull("classstructureid")) {
                loctnSpecSet = loctn.getMboSet("LOCATIONSSPECCLASS");
                filter = "";
                for (int i = 0; i < loctnSpecSet.getSize(); i++) 
                    filter = filter + " ,'" + loctnSpecSet.getMbo(i).getString("assetattrid") + "'";
                if (!filter.isEmpty())
                    filter = " and assetattrid not in (" + filter.substring(2) + ")";
                classSpecSet = loctn.getMboSet("CLASSSTRUCTURE").getMbo(0).getMboSet("$missingClassSpecs", "CLASSSPEC", "classstructureid=:classstructureid " + filter);
                while (classSpecSet.moveNext() != null) {
                    newspec = loctnSpecSet.add(2L);
                    newspec.setValue("assetattrid", classSpecSet.getString("assetattrid"), 2L);
                }
            }
        }
    }
}
