package com.epm.iface.app.workorder;

import java.rmi.RemoteException;
import psdi.iface.mic.StatefulMicSetIn;
import psdi.mbo.MboRemote;
import psdi.util.MXException;

public class ChangeStatusSetIn extends StatefulMicSetIn
{
    public ChangeStatusSetIn() throws MXException, RemoteException
    {
    }

    public void afterProcess()
        throws MXException, RemoteException
    {
        if(!mbo.isNew() && mbo.isModified("schedstart") && mbo.isModified("schedfinish"))
            try
            {
                mbo.setValue("statusiface", true, 2L);
                mbo.setValue("status", "FSMMOD", 2L);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        super.afterProcess();
    }
}