package com.epm.app.workorder.virtual;

import java.rmi.RemoteException;
import psdi.mbo.*;
import psdi.util.MXException;
import psdi.app.workorder.virtual.*;

public class CustWOTotal extends WOTotal implements NonPersistentMboRemote
{
	public CustWOTotal(MboSet ms) throws MXException, RemoteException
	{
		super(ms);
	}
}