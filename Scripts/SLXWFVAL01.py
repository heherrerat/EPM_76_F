# Propietario: Script de automatización de propiedad de SOLEX
# Desarrollador: Javier García <javier.garcia@solex.com.co>
# Funcionalidad: Validaciones de campos para flujo para flujo primer modelo y replicas (LINEALES)
# Fecha: 2018-01-19
# Actualizacion 27-03-2020 por Fernando Gutierrez fernando.gutierrez@solex.com.co

#   _____  ____  _      ________   __
#  / ____|/ __ \| |    |  ____\ \ / /
# | (___ | |  | | |    | |__   \ V /
#  \___ \| |  | | |    |  __|   > <
#  ____) | |__| | |____| |____ / . \
# |_____/ \____/|______|______/_/ \_\

### Validación debe tener las mismas condiciones que el script SLXWFCOND01

from psdi.mbo import Mbo
from psdi.server import MXServer
from java.lang import System
from psdi.mbo import MboConstants
from java.lang import String

System.out.println("********************Inicio script SLXWFVAL01******************************");
description = mbo.getString("DESCRIPTION")
status = mbo.getString("STATUS")
maximo = MXServer.getMXServer()
currentUser = maximo.getUserInfo(user)
woMboSet = maximo.getMboSet("WORKORDER", currentUser)
mbo.setValue('SLXPCBVAL', mbo.getDouble('SLXPCBVAL') + 1)

#  ______ _    _ _   _  _____ _____ ____  _   _ ______  _____
# |  ____| |  | | \ | |/ ____|_   _/ __ \| \ | |  ____|/ ____|
# | |__  | |  | |  \| | |      | || |  | |  \| | |__  | (___
# |  __| | |  | | . ` | |      | || |  | | . ` |  __|  \___ \
# | |    | |__| | |\  | |____ _| || |__| | |\  | |____ ____) |
# |_|     \____/|_| \_|\_____|_____\____/|_| \_|______|_____/

""" funciones usadas para validar el ciclo de vida de la OT a lo largo del cambio de Status """


def blankfieldmessage(Setname, texto, args):
    """
    Funcion que evalua si un campo (atributo) no tiene valor ingresado y devuelve un mensaje  informativo, con
    numero de OT, avisando que dicho campo es requerido.
    :param Setname : mbo de la OT.
    :param texto : contiene los mensajes informativos concatenados de las otras itearaciones.
    :param args : diccionario que contiene los parametros que se van a evaluar.
    :return texto : concatena los mensajes de los atributos evaluados.
    """
    otcurrent = str(Setname.getString("WONUM"))
    for iterator in args:
        if (Setname.getString(iterator) == ''):
            texto = texto + u'\n- La OT:<b> ' + otcurrent + '</b> requiere el campo <b>' + args[iterator] + '</b> '

    return texto


def espplan(fmbo, texto, parent):
    """
    Funcion que evalua si una OT se encuentra en estado  ESPPLAN.
    :param fmbo: contiene el mbo que viene desde offpsring podria sera la OT padre o hijos.
    :param texto: contiene los mensajes informativos concatenados de las otras itearaciones.
    :param parent: contiene 1 si la OT es padre o 0 si es una ot hija
    :return: texto concatena los mensajes de los atributos evaluados.
    """
    #Obtener el SITEID de los dominios    
    slxaguas = mbo.getMboSet("$SLXALNDOMAIN_A", "ALNDOMAIN", "DOMAINID='SLXAGUAS' AND VALUE='"+mbo.getString('SITEID')+"'")
    slxaguasF = mbo.getMboSet("$SLXALNDOMAIN_A", "ALNDOMAIN", "DOMAINID='SLXAGUAS' AND VALUE='"+fmbo.getString('SITEID')+"'")

    otcurrent = str(fmbo.getString("WONUM"))
    # texto = u'\n- ' + str(parent) + ' -- '+ otcurrent + "--" + texto
    grotMboSet = fmbo.getMboSet("SLXGRRESP")
    if grotMboSet.isEmpty() is False:
        grotMbo = grotMboSet.moveFirst()
        modelo = grotMbo.getString("SLXMODELOTIPO")
    else:
        modelo = ''

    # todo descartar tareas
    if parent:		
        if mbo.getDouble('ESTDUR') == 0:
            mbo.setValue('ESTDUR', '')
        # texto =  u'\n- <b>Entro al padre</b> con su parent ID '
        #if mbo.getString('SITEID') != '0141_PA' and mbo.getString('SITEID') != '0151_AR' and modelo != 'ALUMBRADO':
        if slxaguas.isEmpty() and modelo != 'ALUMBRADO':
            mbo.setFieldFlag('SLXGRMTO', MboConstants.REQUIRED, True)

        #if mbo.getString('SITEID') == '0141_PA' or mbo.getString('SITEID') == '0151_AR':        
        if slxaguas.isEmpty() is False:
            campos = {"CLASSSTRUCTUREID": "Clasificacion"}
            texto = blankfieldmessage(mbo, texto, campos)			

        mbo.setFieldFlag('SLXCENTROACTIVIDAD', MboConstants.REQUIRED, True)
        mbo.setFieldFlag('TARGSTARTDATE', MboConstants.REQUIRED, True)
        mbo.setFieldFlag('TARGCOMPDATE', MboConstants.REQUIRED, True)
        if mbo.getBoolean('SLXFSM') == 1:
            mbo.setFieldFlag('SLXTIPOACCION', MboConstants.REQUIRED, True)
            mbo.setFieldFlag('SLXACTCOST', MboConstants.REQUIRED, True)
            mbo.setFieldFlag('SLXTIPOTAREA', MboConstants.REQUIRED, True)
            mbo.setFieldFlag('SNECONSTRAINT', MboConstants.REQUIRED, True)
            mbo.setFieldFlag('FNLCONSTRAINT', MboConstants.REQUIRED, True)

        ServiceaddressSet = mbo.getMboSet("SERVICEADDRESS")
        Serviceaddress = ServiceaddressSet.moveFirst()
        while Serviceaddress is not None:
            if (Serviceaddress.getString("LONGITUDEX") == ''):
                Serviceaddress.setFieldFlag('LONGITUDEX', MboConstants.REQUIRED, True)
                Serviceaddress.setValue('DESCRIPTION', Serviceaddress.getString('DESCRIPTION') + '.')

            if (Serviceaddress.getString("LATITUDEY") == ''):
                Serviceaddress.setFieldFlag('LATITUDEY', MboConstants.REQUIRED, True)
                Serviceaddress.setValue('DESCRIPTION', Serviceaddress.getString('DESCRIPTION') + '.')

            Serviceaddress = ServiceaddressSet.moveNext()
    else:
        #if fmbo.getString('SITEID') != '0141_PA' and fmbo.getString('SITEID') != '0151_AR' and modelo != 'ALUMBRADO':
        if slxaguasF.isEmpty() and modelo != 'ALUMBRADO':
            campos = {"SLXGRRESP": "Grupo Responsable de OT", "SLXGRMTO": "Grupo Responsable de Ejecucion"}
            texto = blankfieldmessage(fmbo, texto, campos)

        #if fmbo.getString('SITEID') == '0141_PA' or fmbo.getString('SITEID') == '0151_AR':
        if slxaguasF.isEmpty() is False:
            campos = {"CLASSSTRUCTUREID": "Clasificacion"}
            texto = blankfieldmessage(fmbo, texto, campos)
        
        campos = {"SLXCENTROACTIVIDAD": "Unidad de negocio", "TARGSTARTDATE": "Fecha estimada ejecucion",
                  "TARGCOMPDATE": "Fecha estimada Finalizacion","ESTDUR": "Duración"}
        texto = blankfieldmessage(fmbo, texto, campos)

        if fmbo.getBoolean('SLXFSM') == 1:
            campos = {"SLXTIPOACCION": "Tipo de Accion", "SLXACTCOST": "Actividad de costos",
                      "SLXTIPOTAREA": "Tipo de Tarea FSM", "SNECONSTRAINT": "No iniciar antes de",
                      "FNLCONSTRAINT": "No finalizar antes de"}
            texto = blankfieldmessage(fmbo, texto, campos)
        
        if fmbo.getDouble("ESTDUR")==0:
            texto = texto + u'\n- La OT:<b> ' + otcurrent + '</b> requiere el campo <b>Duracion</b> mayor a 0 '

        ServiceaddressSet = fmbo.getMboSet("SERVICEADDRESS")
        Serviceaddress = ServiceaddressSet.moveFirst()
        while Serviceaddress is not None:
            campos = {"LONGITUDEX": "Longitud (X)", "LATITUDEY": "Latitud (Y)"}
            texto = blankfieldmessage(Serviceaddress, texto, campos)

            Serviceaddress = ServiceaddressSet.moveNext()


    almacen = ''
    WpMaterialSet = fmbo.getMboSet("SHOWPLANMATERIAL")
    WpMaterial = WpMaterialSet.moveFirst()

    while WpMaterial is not None:
        if (WpMaterial.getString("SLXUNIDNEGOCIO") == ''):
            texto = texto + u'\n- En la OT <b>' + otcurrent + '</b> Alguna linea de material planificado no cuenta con <b>unidad de negocio.</b> '

        if (WpMaterial.getString("SLXCAUX") == ''):
            texto = texto + u'\n- En la OT <b>' + otcurrent + '</b> Alguna linea de material planificado no cuenta con <b>cuenta auxiliar.</b> '

        if (WpMaterial.getString("SLXTIPOCOSTO") == ''and WpMaterial.getString("DIRECTREQ") == 0):
            texto = texto + u'\n- En la OT <b>' + otcurrent + '</b> Alguna linea de material planificado no cuenta con <b>tipo de costo.</b> '

        if (WpMaterial.getString("ISSUETO") == ''):
            itemnum = (WpMaterial.getString("ITEMNUM"))
            texto = texto + u'\n- En la OT <b>' + otcurrent + '</b> Alguna linea de material planificado no cuenta con <b>Despachar a.</b> '
            # texto = texto + u'\n- En la OT <b>' + otcurrent +'</b> el item <b>' +itemnum+'</b> no cuenta con <b>Despachar a.</b> '

        itemMboSet = WpMaterial.getMboSet("ITEM")
        estadoitem='ACTIVE'
        if itemMboSet.isEmpty() is False:
            itemMbo = itemMboSet.moveFirst()
            estadoitem = itemMbo.getString("STATUS")       
        if estadoitem!='ACTIVE':
            texto = texto + u'\n- En la OT <b>' + otcurrent + '</b> El articulo ' + itemMbo.getString("ITEMNUM") + ' tiene estado <b>' +estadoitem+'</b> '


        StoreroomSet = WpMaterial.getMboSet("LOCATION")
        if (StoreroomSet.isEmpty() is False):
            Storeroom = StoreroomSet.moveFirst()
            if (Storeroom.getString("SLXALMPRINC") != almacen and almacen != ''):
                texto = texto + u'\n- En la OT <b>' + otcurrent + '</b> No todas las lineas de material planificado tienen el mismo <b>almacen principal.</b> '
                almacen = Storeroom.getString("SLXALMPRINC")

        WpMaterial = WpMaterialSet.moveNext()

    WpServiceSet = fmbo.getMboSet("SHOWPLANSERVICE")
    WpService = WpServiceSet.moveFirst()

    while WpService is not None:
        if (WpService.getString("SLXUNIDNEGOCIO") == ''):
            texto = texto + u'\n- En la OT <b>' + otcurrent + '</b> Alguna linea de servicio planificado no cuenta con <b>unidad de negocio.</b> '

        if (WpService.getString("SLXCOBJETO") == ''):
            texto = texto + u'\n- En la OT <b>' + otcurrent + '</b> Alguna linea de servicio planificado no cuenta con <b>cuenta objeto.</b> '

        if (WpService.getString("SLXCAUX") == ''):
            texto = texto + u'\n- En la OT <b>' + otcurrent + '</b> Alguna linea de servicio planificado no cuenta con <b>cuenta auxiliar.</b> '

        WpService = WpServiceSet.moveNext()

    assetMboSet = fmbo.getMboSet("ASSET")
    estadoact=''
    if assetMboSet.isEmpty() is False:
        assetMbo = assetMboSet.moveFirst()
        estadoact = assetMbo.getString("STATUS")       
    if estadoact=='BAJOBSOL' or estadoact=='BAJSIMPLE' or estadoact=='BAJASINIES' or estadoact=='DECOMMISSIONED': 
        texto = texto + u'\n- En la OT <b>' + otcurrent + '</b> El activo ' + assetMbo.getString("ASSETNUM") + ' tiene estado <b>' +estadoact+'</b> '

    taskMboSet = fmbo.getMboSet("SHOWTASKS")
    if taskMboSet.isEmpty() is False:
        taskMbo = taskMboSet.moveFirst()
        while taskMbo is not None:        
            assettaskMboSet = taskMbo.getMboSet("ASSET")
            estadoacttask=''
            if assettaskMboSet.isEmpty() is False:
                assettaskMbo = assettaskMboSet.moveFirst()
                estadoacttask = assettaskMbo.getString("STATUS")    
            if estadoacttask=='BAJOBSOL' or estadoacttask=='BAJSIMPLE' or estadoacttask=='BAJASINIES' or estadoacttask=='DECOMMISSIONED': 
                texto = texto + u'\n- En la OT <b>' + otcurrent + '</b>, tarea <b>' + taskMbo.getString("taskid") + '</b> El activo <b>' + assettaskMbo.getString("ASSETNUM") + '</b> tiene estado <b>' +estadoacttask+'</b> '
            taskMbo = taskMboSet.moveNext()

    return texto


def planeada(fmbo, texto, parent):
    """
    Funcion que evalua si una OT se encuentra en estado  PLANEADA  o ESPAPROB
    :param fmbo: contiene el mbo que viene desde offpsring podria sera la OT padre o hijos.
    :param texto: contiene los mensajes informativos concatenados de las otras itearaciones.
    :param parent: contiene 1 si la OT es padre o 0 si es una ot hija
    :return: texto concatena los mensajes de los atributos evaluados.
    """
    otcurrent = str(fmbo.getString("WONUM"))

    if fmbo.getBoolean('SLXFSM') == 0:
        if (fmbo.getBoolean('SLXSTP') == 1 and fmbo.getBoolean('SLXSTPAPROB') == 0):
            texto = texto + u'\n- La <b>STP</b> de la OT <b>' + otcurrent + '</b> debe estar aprobada para poder aprobar la orden de trabajo. '
        poMboSet = fmbo.getMboSet("SLXPO")

        if (poMboSet.isEmpty() is False):
            texto = texto + u'\n- El <b>Permiso Operativo</b> de la OT <b>' + otcurrent + '</b> debe estar aprobado para poder aprobar la orden de trabajo. '

    else:
        if parent:
            mbo.setFieldFlag('SLXSTPAPROB', MboConstants.REQUIRED, True)
        else:
            campos = {"SLXSTPAPROB": "Permiso Operativo Aprobado"}
            texto = blankfieldmessage(fmbo, texto, campos)

    return texto


def espprog(fmbo, texto, parent):
    """
    Funcion que evalua si una OT se encuentra en estado  ESPPROG.
    :param fmbo: contiene el mbo que viene desde offpsring podria sera la OT padre o hijos.
    :param texto: contiene los mensajes informativos concatenados de las otras itearaciones.
    :param parent: contiene 1 si la OT es padre o 0 si es una ot hija
    :return: texto concatena los mensajes de los atributos evaluados.
    """
    otcurrent = str(fmbo.getString("WONUM"))

    if parent:
        mbo.setFieldFlag('SCHEDSTART', MboConstants.REQUIRED, True)
        mbo.setFieldFlag('SCHEDFINISH', MboConstants.REQUIRED, True)
    else:
        campos = {"SCHEDSTART": "Inicio Programado", "SCHEDFINISH": "Finalizacion Programada"}
        texto = blankfieldmessage(fmbo, texto, campos)

    if (fmbo.getBoolean('SLXSTP') == 1 and fmbo.getBoolean('SLXSTPAPROB') == 0):
        texto = texto + u'\n- La <b>STP</b> de la OT <b>' + otcurrent + '</b> debe estar aprobado para poder aprobar la orden de trabajo. '

    poMboSet = fmbo.getMboSet("SLXPO")
    if (poMboSet.isEmpty() is False):
        texto = texto + u'\n- El <b>Permiso Operativo</a> de la OT <b>' + otcurrent + '</b> debe estar aprobado para poder aprobar la orden de trabajo. '

    return texto


def esppo(fmbo, texto, parent):
    """
    Funcion que evalua si una OT se encuentra en estado  ESPPO.
    :param fmbo: contiene el mbo que viene desde offpsring podria sera la OT padre o hijos.
    :param texto: contiene los mensajes informativos concatenados de las otras itearaciones.
    :param parent: contiene 1 si la OT es padre o 0 si es una ot hija
    :return: texto concatena los mensajes de los atributos evaluados.
    """
    otcurrent = str(fmbo.getString("WONUM"))
    if parent:
        if (mbo.getBoolean('SLXFSM') == 1 and (
                mbo.getString('SNECONSTRAINT') == '' or mbo.getString('FNLCONSTRAINT') == '')):
            mbo.setFieldFlag('SNECONSTRAINT', MboConstants.REQUIRED, True)
            mbo.setFieldFlag('FNLCONSTRAINT', MboConstants.REQUIRED, True)
    else:
        campos = {"SNECONSTRAINT": "No iniciar antes de", "FNLCONSTRAINT": "No finalizar antes de"}
        texto = blankfieldmessage(fmbo, texto, campos)

    if (fmbo.getBoolean('SLXSTP') == 1 and fmbo.getBoolean('SLXSTPAPROB') == 0):
        texto = texto + u'\n- La <b>STP</b> de la OT <b>' + otcurrent + '</b> debe estar aprobado para poder aprobar la orden de trabajo. '

    poMboSet = fmbo.getMboSet("SLXPO")
    if (poMboSet.isEmpty() is False):
        texto = texto + u'\n- El <b>Permiso Operativo</a> de la OT <b>' + otcurrent + '</b> debe estar aprobado para poder aprobar la orden de trabajo. '

    return texto


def ejecutado(fmbo, texto, parent):
    otcurrent = str(fmbo.getString("WONUM"))
    # Validar si activo de reemplazo fue movido/despachado y si esta en estado Operacion
    # todo mejorar esta iteracion

    wodesc = fmbo.getMboSet("WORKORDER", 'WORKORDER', " wonum = :wonum and siteid=:siteid ").moveFirst()
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
                        texto = texto + u'\n - En la OT <b>' + otcurrent +'</b> No se ha informado el codigo de activo despachado desde JDE como activo de reemplazo.  '
                    elif wodesc.getString('ASSETNUM') != '':
                        if wodesc.getMboSet("ASSET").moveFirst().getString('STATUS') != 'OPERACION':
                            texto = texto + u'\n - En la OT <b>' + otcurrent +'</b> El activo de reemplazo asociado tiene un estado diferente a OPERACION. Para continuar, primero cambie el estado del activo'

            Actparte = Actparteset.moveNext()

    if fmbo.getString('ACTFINISH')=='':
        texto = texto + u'\n - En la OT <b>' + otcurrent + '</b> la fecha de finalizacion esta vacia <b>' 

    EjecContSet = fmbo.getMboSet("SLX_EJEC_CONT_LIQ")
    if EjecContSet.isEmpty() is False and fmbo.getString('ACTFINISH')!='':
        EjecCont = EjecContSet.moveFirst()
        while EjecCont is not None:
            ContSet=EjecCont.getMboSet("SLXCONTRACT", "CONTRACT", "  :contractnum=contractnum and status='APROBADO' " )
            if ContSet.isEmpty() is False: 
                Cont = ContSet.moveFirst()
                if fmbo.getDate('ACTFINISH') >  Cont.getDate('ENDDATE') or fmbo.getDate('ACTFINISH') <  Cont.getDate('STARTDATE'):
                    texto = texto + u'\n - En la OT <b>' + otcurrent + '</b> el contrato <b>' +Cont.getString('CONTRACTNUM') + '</b> no esta vigente, cree una revision y establezca fechas validas para continuar con la OT'
                        
            EjecCont = EjecContSet.moveNext()

    return texto



def completo(fmbo, texto, parent):
    """
    Funcion que evalua si una OT se encuentra en estado COMPLETO.
    :param fmbo: contiene el mbo que viene desde offpsring podria sera la OT padre o hijos.
    :param texto: contiene los mensajes informativos concatenados de las otras itearaciones.
    :param parent: contiene 1 si la OT es padre o 0 si es una ot hija
    :return: texto concatena los mensajes de los atributos evaluados.
    """
    otcurrent = str(fmbo.getString("WONUM"))
    if (fmbo.getString("SLXESTADOLIQ") != 'LIQCOMP' and fmbo.getString("SLXESTADOLIQ") != ''):
        texto = texto + u'\n- En la OT <b>' + otcurrent + u'</b> Una o mas lineas de ejecucion de contratista no han sido liquidadas. Favor verifique que todas las líneas se encuentran en registros de LAPC y que estos registros están en estado CONFIRMADA. '

    ####### Validaciones para estado de la OT COMPLETO y CORRECTIVO #########
    if (fmbo.getString("WORKTYPE") in ('CINME', 'CPROG')):
        FailurereportSet = fmbo.getMboSet("FAILUREREPORT")
        FailurereportSet.setWhere("TYPE='REMEDY'")
        if (FailurereportSet.isEmpty() is True):
            texto = texto + u'\n- En la OT <b>' + otcurrent + '</b> Debe ingresar la jerarquia de la clase de anomalia de la orden de trabajo. '

        if parent:
            mbo.setFieldFlag('SLXCPERSONA', MboConstants.REQUIRED, True)
            mbo.setFieldFlag('SLXCMAMBIENTE', MboConstants.REQUIRED, True)
            mbo.setFieldFlag('SLXCCOSTO', MboConstants.REQUIRED, True)
            mbo.setFieldFlag('SLXCREPUTACION', MboConstants.REQUIRED, True)
        else:
            # todo poner los valores correspondientes
            campos = {"SLXCPERSONA": "#####", "SLXCMAMBIENTE": "####", "SLXCCOSTO": "####", "SLXCREPUTACION": "####"}
            texto = blankfieldmessage(fmbo, texto, campos)

    pocMboSet = fmbo.getMboSet("SLXPOCIERRE")
    if (pocMboSet.isEmpty() is False):
        texto = texto + u'\n- En la OT <b>' + otcurrent + '</b> El Permiso Operativo debe estar cerrado para poder cerrar la orden de trabajo. '

    return texto


def offspring(fmbo, texto, destiny):
    """
    Funcion que se encarga de buscar la descendencia de una OT, determinar si es padre e iterar sobre la descendencia
    enviando al mbo a su destino con el valor del argumento destiny.
    :param fmbo: contiene el mbo de la OT principal
    :param texto: contiene el valor inicial de la variable texto de los mensajes infomativos
    :param destiny: contiene el nombre de la funcion con la cual se evaluara la OT actual
    :return: texto concatena los mensajes de los atributos evaluados.
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
        texto = destiny(wodesc, texto, parent)
        Descendencia = Descendenciaset.moveNext()

    return texto


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

""" Validacion del status para determinar hacia cual funcion deberia entrar"""

texto = u''
destiny = espplan
if status == "ESPPLAN":
    destiny = espplan
elif (status == 'PLANEADA' or status == 'ESPAPROB'):
    destiny = planeada
elif status == 'ESPPROG' or status == 'ESPMO' or status == 'ESPMAT' or status == 'ESPCOND':
    destiny = espprog
elif status == 'ESPPO':
    destiny = esppo
elif status == 'EJECUTADO' or status == 'EJECINCOMP' or status == 'INFOINCOMP':
    destiny = ejecutado   
elif status == 'COMPLETO':
    destiny = completo

final = offspring(mbo, texto, destiny)
if (final != ''):
    errorkey = final
    errorgroup = 'Error! '

System.out.println("********************FIN script SLXWFVAL01******************************");