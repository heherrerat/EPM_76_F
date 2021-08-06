package com.epm.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.app.location.LocHierarchy;
import psdi.app.location.LocHierarchySet;
import psdi.app.location.LocationRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.pluss.app.workorder.PlusSFldWOAssetNum;
import psdi.server.MXServer;
import psdi.util.MXApplicationYesNoCancelException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

// psdi.pluss.app.workorder.PlusSFldWOAssetNum
public class CustPlusSFldWOAssetNum extends PlusSFldWOAssetNum
{
	private static MXLogger logger;
	public CustPlusSFldWOAssetNum(MboValue arg0) throws MXException
	{
	     super(arg0);
		 logger = MXLoggerFactory.getLogger("maximo.application");
	}
 
	public void action() throws MXException, RemoteException
	{
		super.action();
		
		logger.debug("CustPlusSFldWOAssetNum. action");
		if (!getMboValue().isNull() && !getMboValue().getMbo().isNull("location"))
		{
	    	String app = getMboValue().getMbo().getThisMboSet().getApp();
			logger.debug("CustPlusSFldWOAssetNum. action. App: " + app);
	    	if ( app != null )
	    	{
		    	if ( app.equalsIgnoreCase("PLUSDWOTRK"))
		    	{
			        LocationRemote locationRem = (LocationRemote)getMboValue().getMbo().getMboSet("$locations","locations","location = :location and siteid = :siteid").getMbo(0);
			        if (locationRem!=null)
			        {
			        	MboRemote mboCtrct = locationRem.getMboSet("SLXCNTRCTLOCAT").getMbo(0);
			        	@SuppressWarnings("unused")
						int inError = 0;
			        	if (mboCtrct != null)
			        		inError = validacionError(mboCtrct);
			        	else
			        		procesaJerarquia(locationRem);
			        }
		    	}
	    	}
		}
		logger.debug("CustPlusSFldWOAssetNum. action. end");
	}
	
	public void procesaJerarquia(LocationRemote locationRem) throws MXException, RemoteException {
		LocHierarchySet locAnces = (LocHierarchySet)locationRem.getMboSet("SLXHIERARCHY");
        int index = 0;
        String strLoc = "";
        String strSit = "";
        int inError = 0;
        for(LocHierarchy child = null; (child = (LocHierarchy)locAnces.getMbo(index)) != null;)
        {
        	//LocationRemote locationChl = (LocationRemote)child;
	        strLoc = child.getString("parent");
	        strSit = child.getString("siteid");
	        logger.debug("CustPlusSFldWOAssetNum. procesaJerarquia. Ubicación: " + strLoc + " Site: " + strSit);	        
	        LocationRemote locationChl = (LocationRemote)getMboValue().getMbo().getMboSet("$locations"+index,"locations","location = '" + strLoc + "' and siteid = '" + strSit + "'").getMbo(0);
	        if ( locationChl != null)
	        {
	        	MboRemote mboCtrct = locationChl.getMboSet("SLXCNTRCTLOCAT").getMbo(0);
	        	inError = 0;
	        	if (mboCtrct != null)
	        	{
	        		inError = validacionError(mboCtrct);
	        		if (inError > 0) break;
	        	}
	        	else
	        		procesaJerarquia(locationChl);
	        }
            index++;
        }
	}
	
	public int validacionError(MboRemote mboCtrct) throws MXException, RemoteException
	{	
		int inError = 0;
        Date currentDT = MXServer.getMXServer().getDate();
        Date fechaIni = mboCtrct.getDate("STARTDATE");
        Date fechaFin = mboCtrct.getDate("ENDDATE");
        String stLocation = mboCtrct.getString("LOCATION");
		Object params[] = {stLocation};
        logger.debug("CustPlusSFldWOAssetNum. validacionError. Ubicación: " + stLocation);
        // "El Activo no tiene actualmente un contrato vigente".
        if (!(currentDT.after(fechaIni) && currentDT.before(fechaFin)))
        {
        	inError = 1;
    		//throw new MXApplicationWarningException("slxwo", "assetcontract", params);
            int userInput = MXApplicationYesNoCancelException.getUserInput("BMXZZ0049I",MXServer.getMXServer(), getMboValue().getMbo().getUserInfo());
            switch(userInput)
            {
    	        case MXApplicationYesNoCancelException.NULL:
    	        {
    	        	throw new MXApplicationYesNoCancelException("BMXZZ0049I","slxwo", "assetcontract", params);
    	        }
    	        case MXApplicationYesNoCancelException.YES:
    	        {
    	        	break;
    	        }
    	        case MXApplicationYesNoCancelException.NO:
    	        {
    	        	break;
    	        }
            }        	
        }

        return inError;
	}
}