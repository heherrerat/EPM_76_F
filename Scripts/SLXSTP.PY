# Propietario: Script de automatización de propiedad de SOLEX
# Desarrollador: Javier García <javier.garcia@solex.com.co>
# Funcionalidad: Asigna valor 1 al flag de campo Ajuste diseño SLXAJUDISENO en la OT PLUSDWOTRK
# Fecha: 2018-02-05
# Modificado: Incluir regla paraq AR y PA 
# Fecha: 2019-05-09
# Modificado por Ricardo Aranguren y Dora Jaramillo para ajuste de DISEÑO 
# Fecha: 2020-07-08
# Modificado por Juan Antonio González para ajuste de SITEID dinámico
# Fecha: 2020-12-24


from java.lang import System
import java
from psdi.server import MXServer
from psdi.mbo import MboConstants
from psdi.util import MXApplicationException

slxdistribucion = mbo.getMboSet("$SLXALNDOMAIN_D", "ALNDOMAIN", "DOMAINID='SLXDISTRIBUCION' AND VALUE='"+SITEID+"'")
slxaguas = mbo.getMboSet("$SLXALNDOMAIN_A", "ALNDOMAIN", "DOMAINID='SLXAGUAS' AND VALUE='"+SITEID+"'")

if app!='SLXWOPROJECT':
#    if SLXSTP == 1 and (REASONFORCHANGE is None or REASONFORCHANGE=='') and WORKTYPE=='PROYE' and SITEID == '0133_DE' :
    if SLXSTP == 1 and (REASONFORCHANGE is None or REASONFORCHANGE=='') and WORKTYPE=='PROYE' and slxdistribucion.isEmpty() is False:
#     REASONFORCHANGE='Razon del cambio'
     SLXAJUDISENO=1
# mbo.getThisMboSet().save()
# REASONFORCHANGE=''

#    if SLXSTP == 1 and REASONFORCHANGE!='' and WORKTYPE=='PROYE' and SITEID == '0133_DE' :
    if SLXSTP == 1 and REASONFORCHANGE!='' and WORKTYPE=='PROYE' and slxdistribucion.isEmpty() is False:
     SLXAJUDISENO=1

#    if (SITEID == '0141_PA' or SITEID == '0151_AR' ) and SLXSTP == 1:
    if slxaguas.isEmpty() is False and SLXSTP == 1:
     SLXAJUDISENO=1

if  app=='SLXWOPROJECT':
    if ( SLXFASEPROY==u'DISEÑO' or SLXFASEPROY=='PUESTASERV' ): 
     SLXAJUDISENO=1
    elif (SLXFASEPROY!=''):
     SLXAJUDISENO=0