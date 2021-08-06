# Propietario: Script de automatización de propiedad de SOLEX
# Desarrollador: Javier García <javier.garcia@solex.com.co>
# Funcionalidad: Script para condiciones de flujo de trabajo (LINEALES)
# Fecha: 2018-01-19
# Actualizacion 27-03-2020 por Fernando Gutierrez fernando.gutierrez@solex.com.co

#   _____  ____  _      ________   __
#  / ____|/ __ \| |    |  ____\ \ / /
# | (___ | |  | | |    | |__   \ V /
#  \___ \| |  | | |    |  __|   > <
#  ____) | |__| | |____| |____ / . \
# |_____/ \____/|______|______/_/ \_\

### Condición debe tener las mismas validaciones que el script SLXWFVAL01
description = mbo.getString("DESCRIPTION")
from psdi.mbo import Mbo
from psdi.server import MXServer
from psdi.mbo import SqlFormat
status = mbo.getString('STATUS')
evalresult = False

#  ______ _    _ _   _  _____ _____ ____  _   _ ______  _____
# |  ____| |  | | \ | |/ ____|_   _/ __ \| \ | |  ____|/ ____|
# | |__  | |  | |  \| | |      | || |  | |  \| | |__  | (___
# |  __| | |  | | . ` | |      | || |  | | . ` |  __|  \___ \
# | |    | |__| | |\  | |____ _| || |__| | |\  | |____ ____) |
# |_|     \____/|_| \_|\_____|_____\____/|_| \_|______|_____/

""" funciones usadas para validar el ciclo de vida de la OT a lo largo del camio de Status """

def blankfieldchk(Setname, evalresult, args):
    """
    Funcion que evalua si un campo (atributo) no tiene valor ingresado  devolviento "True"  o "False
    en caso de tener valor cargado.
    :param Setname : mbo de la OT.
    :param evalresult : contiene el valor incial de evalresult
    :param args : diccionario que contiene los parametros que se van a evaluar.
    :return evalresult
    """
    for iterator in args:
        if (Setname.getString(iterator) == ''):
            evalresult = True
            break

    return evalresult


def espplan(fmbo, evalresult, parent):
    """
     Funcion que evalua si una OT se encuentra en estado  ESPPLAN.
     :param fmbo: contiene el mbo que viene desde offpsring podria sera la OT padre o hijos.
     :param evalresult: contiene el valor incial de evalresult
     :param parent: contiene 1 si la OT es padre o 0 si es una ot hija
     :return: evalresult.
     """

    try:
        actionSet = MXServer.getMXServer().getMboSet("action", fmbo.getUserInfo());
        sqf = SqlFormat("action = :1")
        sqf.setObject(1, "action", "action", "SLXFECHASFSMAUT")
        actionSet.setWhere(sqf.format())
        actionSet.reset()
        actionSet.getMbo(0).executeAction(mbo)
    except:
        print "Exception: ", sys.exc_info()

    almacen = ''
    WpMaterialSet = fmbo.getMboSet("SHOWPLANMATERIAL")
    WpMaterial = WpMaterialSet.moveFirst()


    while WpMaterial is not None and evalresult == False:
        if WpMaterial.getString("DIRECTREQ") == 0:
            materials = ["SLXUNIDNEGOCIO", "SLXCAUX", "SLXTIPOCOSTO", "ISSUETO"]
        else:
            materials = ["SLXUNIDNEGOCIO", "SLXCAUX", "ISSUETO"]
        evalresult = blankfieldchk(WpMaterial, evalresult, materials)

        itemMboSet = WpMaterial.getMboSet("ITEM")
        estadoitem='ACTIVE'
        if itemMboSet.isEmpty() is False:
            itemMbo = itemMboSet.moveFirst()
            estadoitem = itemMbo.getString("STATUS")       
        if estadoitem!='ACTIVE':
            evalresult = True

        if evalresult:
            break

        StoreroomSet = WpMaterial.getMboSet("LOCATION")

        if (StoreroomSet.isEmpty() is False):
            Storeroom = StoreroomSet.moveFirst()
            if (Storeroom.getString("SLXALMPRINC") != almacen and almacen != ''):
                evalresult = True
                almacen = Storeroom.getString("SLXALMPRINC")
                break

        WpMaterial = WpMaterialSet.moveNext()

    WpServiceSet = fmbo.getMboSet("SHOWPLANSERVICE")
    WpService = WpServiceSet.moveFirst()

    while WpService is not None and evalresult == False:
        services = ["SLXUNIDNEGOCIO", "SLXCAUX", "SLXCOBJETO"]
        evalresult = blankfieldchk(WpService, evalresult, services)
        if evalresult:
            break

        WpService = WpServiceSet.moveNext()

    ServiceaddressSet = fmbo.getMboSet("SERVICEADDRESS")
    Serviceaddress = ServiceaddressSet.moveFirst()

    while Serviceaddress is not None and evalresult == False:
        address = ["LONGITUDEX", "LATITUDEY"]
        evalresult = blankfieldchk(Serviceaddress, evalresult, address)
        if evalresult:
            break

        Serviceaddress = ServiceaddressSet.moveNext()

    if (fmbo.getString('WORKTYPE') == '' or fmbo.getString('SLXCENTROACTIVIDAD') == '' or fmbo.getDouble('ESTDUR') == 0 or fmbo.getString('TARGSTARTDATE') == '' or fmbo.getString('TARGCOMPDATE') == ''):
        evalresult = True

    grotMboSet = fmbo.getMboSet("SLXGRRESP")
    if grotMboSet.isEmpty() is False:
        grotMbo = grotMboSet.moveFirst()
        modelo = grotMbo.getString("SLXMODELOTIPO")
    else:
        modelo = ''

	#Obtener el SITEID de los dominios
    slxaguas = mbo.getMboSet("$SLXALNDOMAIN_A", "ALNDOMAIN", "DOMAINID='SLXAGUAS' AND VALUE='"+fmbo.getString('SITEID')+"'")
    slxgas = mbo.getMboSet("$SLXALNDOMAIN_G", "ALNDOMAIN", "DOMAINID='SLXGAS' AND VALUE='"+mbo.getString('SITEID')+"'")	

    #if (fmbo.getString('SLXGRMTO') == '' and modelo != 'ALUMBRADO' and fmbo.getString('SITEID') != '0141_PA' and fmbo.getString('SITEID') != '0151_AR'):
    if (fmbo.getString('SLXGRMTO') == '' and modelo != 'ALUMBRADO' and slxaguas.isEmpty()):
        evalresult = True

    #if (fmbo.getString('SITEID') == '0141_PA' or fmbo.getString('SITEID') == '0151_AR') and fmbo.getString('CLASSSTRUCTUREID') == '':
    if slxaguas.isEmpty() is False and fmbo.getString('CLASSSTRUCTUREID') == '':
        evalresult = True     

    ####### Poner el campo SLXAJUDISENO en 1 para las Ots de Proyectos GAS #######
    #if (mbo.getString('SITEID') == '0172_GA' and mbo.getString('WORKTYPE') == 'PROYE'):
    if (slxgas.isEmpty() is False and mbo.getString('WORKTYPE') == 'PROYE'):
        mbo.setValue("SLXAJUDISENO", 1, 11L)

    ####### Validaciones para estado de la OT ESPPLAN Y PARA FSM#########
    if (fmbo.getBoolean('SLXFSM') == 1 and (fmbo.getString('SLXTIPOACCION') == '' or fmbo.getString('SLXACTCOST') == '' or fmbo.getString('SLXTIPOTAREA') == '' or fmbo.getString('SNECONSTRAINT') == '' or fmbo.getString('FNLCONSTRAINT') == '')):
        evalresult = True

    assetMboSet = fmbo.getMboSet("ASSET")
    estadoact=''
    if assetMboSet.isEmpty() is False:
        assetMbo = assetMboSet.moveFirst()
        estadoact = assetMbo.getString("STATUS")    
    if estadoact=='BAJOBSOL' or estadoact=='BAJSIMPLE' or estadoact=='BAJASINIES' or estadoact=='DECOMMISSIONED': 
        evalresult = True
    
    taskMboSet = fmbo.getMboSet("SHOWTASKS")
    if taskMboSet.isEmpty() is False:
        taskMbo = taskMboSet.moveFirst()
        while taskMbo is not None and evalresult == False:        
            assettaskMboSet = taskMbo.getMboSet("ASSET")
            estadoacttask=''
            if assettaskMboSet.isEmpty() is False:
                assettaskMbo = assettaskMboSet.moveFirst()
                estadoacttask = assettaskMbo.getString("STATUS")    
            if estadoacttask=='BAJOBSOL' or estadoacttask=='BAJSIMPLE' or estadoacttask=='BAJASINIES' or estadoacttask=='DECOMMISSIONED': 
                evalresult = True
                break                
            taskMbo = taskMboSet.moveNext()

    return evalresult


####### Validaciones para estado de la OT PLANEADA  o ESPAPROB#########
def planeada(fmbo, evalresult, parent):
    """
     Funcion que evalua si una OT se encuentra en estado PLANEADA  o ESPAPROB.
     :param fmbo: contiene el mbo que viene desde offpsring podria sera la OT padre o hijos.
     :param evalresult: contiene el valor incial de evalresult
     :param parent: contiene 1 si la OT es padre o 0 si es una ot hija
     :return: evalresult
     """

    if fmbo.getBoolean('SLXFSM') == 0:
        if ((fmbo.getBoolean("SLXSTP") == 1) and (fmbo.getBoolean("SLXSTPAPROB") == 0)):
            evalresult = True

        poMboSet = fmbo.getMboSet('SLXPO')
        if (poMboSet.isEmpty() is False):
            evalresult = True
    else:
        campos = ("SLXSTPAPROB", "FNLCONSTRAINT")
        evalresult = blankfieldchk(fmbo, evalresult, campos)


    return evalresult


def espprog(fmbo, evalresult, parent):
    """
     Funcion que evalua si una OT se encuentra en estado ESPPROG.
     :param fmbo: contiene el mbo que viene desde offpsring podria sera la OT padre o hijos.
     :param evalresult: contiene el valor incial de evalresult
     :param parent: contiene 1 si la OT es padre o 0 si es una ot hija
     :return: evalresult
     """
    if (fmbo.getString('SCHEDSTART') == '' or fmbo.getString('SCHEDFINISH') == ''):
        evalresult = True

    if (fmbo.getBoolean('SLXSTP') == 1 and fmbo.getBoolean('SLXSTPAPROB') == 0):
        evalresult = True

    poMboSet = fmbo.getMboSet('SLXPO')
    if (poMboSet.isEmpty() is False):
        evalresult = True

    return evalresult


def esppo(fmbo, evalresult, parent):
    """
     Funcion que evalua si una OT se encuentra en estado ESPPO.
     :param fmbo: contiene el mbo que viene desde offpsring podria sera la OT padre o hijos.
     :param evalresult: contiene el valor incial de evalresult
     :param parent: contiene 1 si la OT es padre o 0 si es una ot hija
     :return: evalresult
     """

    if (fmbo.getBoolean('SLXFSM') == 1 and (fmbo.getString('SNECONSTRAINT') == '' or fmbo.getString('FNLCONSTRAINT') == '')):
        evalresult = True

    if (fmbo.getBoolean('SLXSTP') == 1 and fmbo.getBoolean('SLXSTPAPROB') == 0):
        evalresult = True

    poMboSet = fmbo.getMboSet('SLXPO')
    if (poMboSet.isEmpty() is False):
        evalresult = True

    return evalresult


def ejecutado(fmbo, evalresult, parent):

    # Validar si activo de reemplazo fue movido/despachado y si esta en estado Operacion
    if parent:
        Descendenciaset = fmbo.getMboSet("SLXDESCENDENCIA")
        Descendencia = Descendenciaset.moveFirst()
        while Descendencia is not None:
            wodesc = Descendencia.getMboSet("WORKORDER", 'WORKORDER', " wonum = :wonum and siteid=:siteid ").moveFirst()
            Actparteset = wodesc.getMboSet("$SLXMATUSETRANS", 'MATUSETRANS', " refwo=:wonum and tositeid=:siteid and linetype='ITEM' ")
            if Actparteset.isEmpty() is False:
                Actparte = Actparteset.moveFirst()
                while Actparte is not None:
                    SlxItemSet = Actparte.getMboSet("ITEM")
                    SlxalmSet = Actparte.getMboSet("LOCATIONS")
                    if SlxItemSet.isEmpty() is False and SlxalmSet.isEmpty() is False:
                        if SlxItemSet.moveFirst().getBoolean("SLXARE") == 1 and SlxalmSet.moveFirst().getString("SLXUBICACIONARE") != '':
                            ifaceset = Actparte.getMboSet("$ACTIVOREEMP_IFACE", 'ACTIVOREEMP_IFACE', " RTRIM(wonum) = :wonum and RTRIM(newsite)=:tositeid ")
                            if wodesc.getString('ASSETNUM') == '' or ifaceset.isEmpty() is True:
                                evalresult = True
                                break
                            elif wodesc.getString('ASSETNUM') != '':
                                if wodesc.getMboSet("ASSET").moveFirst().getString('STATUS') != 'OPERACION':
                                    evalresult = True
                                    break

                    Actparte = Actparteset.moveNext()
            if evalresult:
                break

            Descendencia = Descendenciaset.moveNext()

    if fmbo.getString('ACTFINISH')=='':
        evalresult = True
        
    EjecContSet = fmbo.getMboSet("SLX_EJEC_CONT_LIQ")
    if EjecContSet.isEmpty() is False and fmbo.getString('ACTFINISH')!='':
        EjecCont = EjecContSet.moveFirst()
        while EjecCont is not None:
            ContSet=EjecCont.getMboSet("SLXCONTRACT", "CONTRACT", "  :contractnum=contractnum and status='APROBADO' " )
            if ContSet.isEmpty() is False: 
                Cont = ContSet.moveFirst()
                if fmbo.getDate('ACTFINISH') >  Cont.getDate('ENDDATE') or fmbo.getDate('ACTFINISH') <  Cont.getDate('STARTDATE'):
                    evalresult = True
                    break
                        
            EjecCont = EjecContSet.moveNext()
            
    return evalresult


def completo(fmbo, evalresult, parent):
    """
     Funcion que evalua si una OT se encuentra en estado COMPLETO.
     :param fmbo: contiene el mbo que viene desde offpsring podria sera la OT padre o hijos.
     :param evalresult: contiene el valor incial de evalresult
     :param parent: contiene 1 si la OT es padre o 0 si es una ot hija
     :return: evalresult
     """

    if (fmbo.getString("SLXESTADOLIQ") != 'LIQCOMP' and fmbo.getString("SLXESTADOLIQ") != ''):
        evalresult = True

    if (fmbo.getString("WORKTYPE") in ('CINME', 'CPROG')):
        FailurereportSet = fmbo.getMboSet("FAILUREREPORT")
        FailurereportSet.setWhere("TYPE='REMEDY'")
        if (FailurereportSet.isEmpty() is True):
            evalresult = True

        if (fmbo.getString('SLXCPERSONA') == '' or fmbo.getString('SLXCMAMBIENTE') == '' or fmbo.getString('SLXCCOSTO') == '' or fmbo.getString('SLXCREPUTACION') == ''):
            evalresult = True

    poMboSet = fmbo.getMboSet("SLXPOCIERRE")
    if (poMboSet.isEmpty() is False):
        evalresult = True

    return evalresult


def offspring(fmbo, evalresult, destiny):
    """
    Funcion que se encarga de buscar la descendencia de una OT, determinar si es padre e iterar sobre la descendencia
    enviando al mbo a su destino con el valor del argumento destiny.
    :param fmbo: contiene el mbo de la OT principal
    :param evalresult: contiene el valor incial de evalresult
    :param destiny: contiene el nombre de la funcion con la cual se evaluara la OT actual
    :return: evalresult
    """
    Descendenciaset = fmbo.getMboSet("SLXDESCENDENCIANOTASK")
    Descendencia = Descendenciaset.moveFirst()
    while Descendencia is not None:
        parent = 0
        parentwonum = Descendencia.getString("WONUM")
        parentancestor = Descendencia.getString("ANCESTOR")
        if parentwonum == parentancestor:
            parent = 1

        wodesc = Descendencia.getMboSet("WORKORDER", 'WORKORDER', " wonum = :wonum and siteid=:siteid ").moveFirst()
        evalresult = destiny(wodesc, evalresult, parent)
        Descendencia = Descendenciaset.moveNext()

    return evalresult


#                    /^\/^\
#                  _|__|  O|
#         \/     /~     \_/ \
#          \____|__________/  \
#                 \_______      \
#                         `\     \                 \
#                           |     |                  \
#                          /      /                    \
#                         /     /                       \
#                       /      /                         \ \
#                      /     /                            \  \
#                    /     /             _----_            \   \
#                   /     /           _-~      ~-_         |   |
#                  (      (        _-~    _--_    ~-_     _/   |
#                   \      ~-____-~    _-~    ~-_    ~-_-~    /
#                     ~-_           _-~          ~-_       _-~
#                        ~--______-~                ~-___-~


# __      __     _      _____ _____          _____ _____ ____  _   _ ______  _____
# \ \    / /\   | |    |_   _|  __ \   /\   / ____|_   _/ __ \| \ | |  ____|/ ____|
#  \ \  / /  \  | |      | | | |  | | /  \ | |      | || |  | |  \| | |__  | (___
#   \ \/ / /\ \ | |      | | | |  | |/ /\ \| |      | || |  | | . ` |  __|  \___ \
#    \  / ____ \| |____ _| |_| |__| / ____ \ |____ _| || |__| | |\  | |____ ____) |
#     \/_/    \_\______|_____|_____/_/    \_\_____|_____\____/|_| \_|______|_____/

""" Validacion del status para determinar haci cual funcion deberia entrar"""

destiny = espplan
if status == "ESPPLAN":
    destiny = espplan
elif (status == 'PLANEADA' or status == 'ESPAPROB'):
    destiny = planeada
elif(status == 'ESPPROG' or status == 'ESPMO' or status == 'ESPMAT' or status == 'ESPCOND'):
    destiny = espprog
elif status == 'EJECUTADO' or status == 'EJECINCOMP' or status == 'INFOINCOMP':
    destiny = ejecutado    
elif (status == 'ESPPO'):
    destiny = esppo
elif (status == 'COMPLETO'):
    destiny = completo

evalresult = offspring(mbo, evalresult, destiny)