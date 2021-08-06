# Propietario: Script de automatización de propiedad de SOLEX
# Desarrollador: Carlos Gómez <carlos.gomez@solex.com.co>
# Funcionalidad: Script establecer campos si es cambiado el plan de trabajo
# Fecha: 2017-08-04

from java.lang import System
from psdi.util import MXApplicationException
from psdi.server import MXServer
from java.util import Calendar 
from java.util import Date
from psdi.mbo import SqlFormat
from psdi.mbo import MboConstants

#parte 1. Script establecer campos si es cambiado el plan de trabajo

if padre !='' and padre is not None:
    ServiceAddressSet = mbo.getMboSet('SERVICEADDRESS')
    ServiceAddress = ServiceAddressSet.moveFirst()
    LocationSet = mbo.getMboSet('LOCATION')

    if (ServiceAddressSet .isEmpty() is False) and (LocationSet .isEmpty() is False) :
        Location = LocationSet.moveFirst()    
        ServiceAddress = ServiceAddressSet.moveFirst()
        mbo.setValue ('SLXALIAS',Location.getString ('SLXALIAS'),2L)
        mbo.setValue ('SLXCENTROACTIVIDAD',Location.getString ('SLXCENTROACTIVIDAD'),2L)
        ServiceAddress.setValue ('SLXBARRIO',Location.getString ('SLXBARRIO'),2L)
        ServiceAddress.setValue ('SLXCORREG',Location.getString ('SLXCORREG'),2L)
        ServiceAddress.setValue ('SLXDEPART',Location.getString ('SLXDEPART'),2L)
        ServiceAddress.setValue ('SLXDEPARTDESC',Location.getString ('SLXDEPARTDESC'),2L)
        ServiceAddress.setValue ('FORMATTEDADDRESS',Location.getString ('SLXDIREC'),2L)
        ServiceAddress.setValue ('LATITUDEY',Location.getString ('SLXLATITUD'),2L)
        ServiceAddress.setValue ('LONGITUDEX',Location.getString ('SLXLONGITUD'),2L)
        ServiceAddress.setValue ('SLXMUNICIPIO',Location.getString ('SLXMUNICIPIO'),2L)
        ServiceAddress.setValue ('SLXMUNICIPIODESC',Location.getString ('SLXMUNICIPIODESC'),2L)
        ServiceAddress.setValue ('SLXSUBREGION',Location.getString ('SLXSUBREGION'),2L)
        ServiceAddress.setValue ('SLXVEREDA',Location.getString ('SLXVEREDA'),2L)

#  Parte 2. Se agrega funcionalidad de establecer fechas de planificación a partir de un JP para replicas de Aguas
#  24 de may 2019
#  JGarcia 
System.out.println("***************INICIA FECHA INICIO PREVISTO***************");


#Obtener el SITEID de los dominios    
slxaguas = mbo.getMboSet("$SLXALNDOMAIN_A", "ALNDOMAIN", "DOMAINID='SLXAGUAS' AND VALUE='"+mbo.getString('SITEID')+"'")

#if (mbo.getString("SITEID") =='0141_PA' or  mbo.getString("SITEID") =='0151_AR') and plam != ''  and plam is not None and finoffset>0 and finoffset !='':
if slxaguas.isEmpty() is False and plam != ''  and plam is not None and finoffset>0 and finoffset !='':

    if  feciniprev =='' or feciniprev is None:
        feciniprev = MXServer.getMXServer().getDate()
    
    if finoffset>0 and finoffset !='':
        System.out.println("***************Entro por el valor de finoffset***************");
        calendar = Calendar.getInstance()
        calendar.setTime(feciniprev)
        t = int((finoffset))
        calendar.add(Calendar.HOUR_OF_DAY, t)
        fecfinprev = calendar.getTime()
        System.out.println("Tiene offset: "+str(fecfinprev)); 
    else:
        fecfinprev = MXServer.getMXServer().getDate()
        System.out.println("NO Tiene offset: "+str(fecfinprev));

#    try:
    actionSet = MXServer.getMXServer().getMboSet("action", mbo.getUserInfo());
    sqf = SqlFormat("action = :1")
    sqf.setObject(1, "action", "action", "SLXFECHASFSMAUT")
 
    actionSet.setWhere(sqf.format())
    actionSet.reset()
    actionSet.getMbo(0).executeAction(mbo)
#    except:
#        print "Exception: ", sys.exc_info()

System.out.println("***************FIN FECHA INICIO PREVISTO***************");