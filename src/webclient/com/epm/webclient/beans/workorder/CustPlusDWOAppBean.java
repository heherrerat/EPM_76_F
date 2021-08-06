
package com.epm.webclient.beans.workorder;

import com.epm.app.workorder.CustPlusDWORemote;
import java.rmi.RemoteException;
import java.util.HashMap;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.plusd.webclient.beans.workorder.PlusDWOAppBean;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public class CustPlusDWOAppBean extends PlusDWOAppBean {
    
    /**
     * Envía cancelación de tarea a FSM masiva o individual desde aplicación
     * de la OT de Redes.
     */
    public int CANCELAFSM() throws MXException, RemoteException {
        
        if (app.onListTab()) {
            app.getResultsBean().hasRecordsForAction();
            MboSetRemote woSet = app.getResultsBean().getMboSet();
            Boolean selectn = !woSet.getSelection().isEmpty();
            
            HashMap<String, Object[]> excepMap;
            excepMap = new HashMap<>();
            
            MboRemote wo;
            for (int i = 0; (wo = woSet.getMbo(i)) != null; i++) {
                if (!selectn || wo.isSelected()) {
                    try {
                        ((CustPlusDWORemote) wo).validaCancelaTareaFSM();
                        
                    } catch (MXException e) {
                        if (!e.getErrorGroup().equals("slxfsm")) {
                            throw e;
                        }
                        String errorKey = e.getErrorKey();
                        if (!excepMap.containsKey(errorKey)) {
                            excepMap.put(errorKey, new Object[] {new StringBuilder(), e});
                        }
                        StringBuilder sb = (StringBuilder) excepMap.get(errorKey)[0];
                        sb.append(wo.getString("wonum")).append(", ");
                    }
                }
            }
            if (!excepMap.isEmpty()) {
                String errorMsg = "";
                final String breaks = "<br/><br/>";
                for (String key : excepMap.keySet()) {
                    String excepMsg = ((MXException) excepMap.get(key)[1]).getMessage();
                    String wonumsRaw = ((StringBuilder) excepMap.get(key)[0]).toString();
                    String wonums = wonumsRaw.substring(0, wonumsRaw.length() - 2);
                    errorMsg += breaks + excepMsg + breaks + wonums;
                }
                throw new MXApplicationException("slxfsm", "multiErrorsListTab", new Object[] {errorMsg.substring(breaks.length())});
            } else {
                for (int i = 0; (wo = woSet.getMbo(i)) != null; i++) {
                    if (!selectn || wo.isSelected()) {
                        ((CustPlusDWORemote) wo).marcaEnvioCancelaTareaFSM();
                    }
                }
                woSet.save();
            }
            
        } else {
            CustPlusDWORemote wo = (CustPlusDWORemote) getMbo();
            wo.validaCancelaTareaFSM();
            wo.marcaEnvioCancelaTareaFSM();
        }
        this.SAVE();

        return 1;
    }
    
}
