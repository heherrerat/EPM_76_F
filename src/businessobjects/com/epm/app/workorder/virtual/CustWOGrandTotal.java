package com.epm.app.workorder.virtual;

import psdi.app.workorder.virtual.*;
import java.rmi.RemoteException;
import psdi.mbo.*;
import psdi.util.MXException;

public class CustWOGrandTotal extends WOGrandTotal implements NonPersistentMboRemote
{
	public CustWOGrandTotal(MboSet ms) throws MXException, RemoteException
	{
	     super(ms);
	}
}