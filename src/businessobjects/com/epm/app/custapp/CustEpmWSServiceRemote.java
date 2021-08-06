
package com.epm.app.custapp;

import java.rmi.RemoteException;
import java.util.Date;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import psdi.iface.webservices.action.WSMboKey;
import psdi.mbo.MboRemote;
import psdi.server.AppServiceRemote;
import psdi.util.MXException;

/**
 *
 * @author javalos
 */
public interface CustEpmWSServiceRemote extends AppServiceRemote {
    @WebMethod(operationName="reportDowntime")
    public void reportDowntime(@WSMboKey("ASSET") MboRemote assetMbo, @WSMboKey("WORKORDER") MboRemote woMbo, Date startDate, Date endDate, double hoursDown, String code, boolean operational) throws MXException, RemoteException;
    
    @WebMethod(operationName="informarRecepcionActivos")
    public void informarRecepcionActivos(@WSMboKey("SLXASSETPR") @WebParam(name="solicitud") MboRemote pr, int cantidad, boolean esDevolucion, double costoUnit, boolean recepCompleta, String numPreActivo, long idInternoJDE, String numOC_JDE, Date fechaRecep) throws MXException, RemoteException;
}
