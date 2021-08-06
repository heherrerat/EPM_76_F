# Propietario: Script de automatización de propiedad de SOLEX
# Desarrollador: Juan Antonio González <juan.gonzalez@solex.cl> (Creado por Ariel ibaceta)
# Funcionalidad: Copia registros del objeto SLX_SOLI_PAG_CONT_DET hacia el objeto SERVRECTRANS
# Fecha: 2017-03-24


from java.lang import System
from psdi.server import MXServer
from java.util import Date
from psdi.util import MXApplicationException

maximo = MXServer.getMXServer()
currentUser=maximo.getUserInfo(user) 

totalsinreajusteant = mbo.getDouble('MONTO_TOTAL_SIN')
suma_montototalsin = 0
suma_montototal  = 0
suma_iva = 0
suma_reajuste = 0
suma_montototalsin2 =0
diferencia_actaanterior=0
diferencia_actaanteriorpcj=0
dif=0
     
if (1==1):


    ponum = ''
    vendor_code = 'DUMMYVNDR'
    vendor_name = "SLX - Proveedor dummy integracion"
    SlxSoliPagContDetSet = mbo.getMboSet('SLX_SOLI_PAG_CONT_DETCONT')
    SlxSoliPagContDetSet.setWhere("itemnum not in (select itemnum from SERVRECTRANS where SERVRECTRANS.siteid=SLX_SOLI_PAG_CONT_DET.siteid and SERVRECTRANS.refwo=SLX_SOLI_PAG_CONT_DET.wonum and SLX_SOLI_PAG_CONT_DET.quantity=SERVRECTRANS.quantity)")
    System.out.println("____________________COMIENZA SCRIPT 7_______________________")


#if (status_modified and status_previous == "NUEVA" and status == "CONFIRMADA"):
#    SlxSoliPagContDetSet = mbo.getMboSet('SLX_SOLI_PAG_CONT_DET');
    if (SlxSoliPagContDetSet.isEmpty() is False):
        #System.out.println("____________________size de detalle")
        #System.out.println(SlxSoliPagContDetSet.getSize())
        i = 1
        poSet = maximo.getMboSet('PO', currentUser)
        poSet.setWhere ('1=2')
        System.out.println("____________________OBTUVE EL SET VACIO DE PO")
        po = poSet.add(2L)
        ponum = po.getString('PONUM')
        po.setValue('SITEID', mbo.getString('SITEID'), 2L)
        po.setValue('INSPECTIONREQUIRED',0, 2L)
        if mbo.getString('VENDOR') is None:
            vendorSet = MXServer.getMXServer().getMboSet('COMPANIES', userInfo)
            #vendorSet.setWhere("orgid='EPM' and company = '" + vendor_code + "'")
            vendorSet.setWhere("orgid='"+mbo.getString('ORGID')+"' and company = '" + vendor_code + "'")
            if vendorSet.isEmpty():
                #vendorSet.setInsertOrg('EPM')
                vendorSet.setInsertOrg(mbo.getString('ORGID'))
                vendor = vendorSet.add(2L)
                vendor.setValue("company", vendor_code, 2L)
                #vendor.setValue("orgid", 'EPM', 2L)
                vendor.setValue("orgid", mbo.getString('ORGID'), 2L)
                vendor.setValue("name", vendor_name[:min(len(vendor_name), vendor.getMboValueInfoStatic("name").getLength())], 2L)
                poSet.getMXTransaction().add(vendorSet.getMXTransaction())
                po.setValue('VENDOR', vendor_code, 2L)
        else:
            po.setValue('VENDOR', mbo.getString('VENDOR'), 2L)
            po.setValue('STATUS', 'APPR', 11L)
        SlxSoliPagContDet = SlxSoliPagContDetSet.moveFirst()
        poLineSet = po.getMboSet('POLINE')

        #System.out.println("____________________YA CREE EL PO, CREO AHORA LAS POLINE")

        while SlxSoliPagContDet is not None:
            SlxDetalleLiquidacionSet = SlxSoliPagContDet.getMboSet('SLX_EJEC_CONT')

            #System.out.println("____________________WONUM: "+SlxSoliPagContDet.getString('WONUM'))	
            #System.out.println("____________________SITEID: "+SlxSoliPagContDet.getString('SITEID'))	
            #System.out.println("____________________ORGID: "+SlxSoliPagContDet.getString('ORGID'))	
            #System.out.println("____________________GLACCOUNT: "+SlxSoliPagContDet.getString('GLACCOUNT'))	
            #System.out.println("____________________SLX_EJEC_CONTID: "+str(SlxSoliPagContDet.getInt('SLX_EJEC_CONTID')))	

            #System.out.println(i)
            poLine = poLineSet.add(2L)
            poLine.setValue('POLINENUM', i, 2L)
            linetype = "SERVICE" if(SlxSoliPagContDet.getString('LINETYPE') == 'ITEM') else SlxSoliPagContDet.getString('LINETYPE')
            poLine.setValue('LINETYPE', linetype, 11L)
            poLine.setValue('ITEMNUM', SlxSoliPagContDet.getString('ITEMNUM'), 11L)
            poLine.setValue('ITEMSETID', SlxSoliPagContDet.getString('ITEMSETID'), 2L)
            poLine.setValue('DESCRIPTION',SlxSoliPagContDet.getString('DESCRIPTION'), 2L)                  
            poLine.setValue('INSPECTIONREQUIRED', 0, 2L)
            poLine.setValue('ISSUE', 1, 2L)
            poLine.setValue('ORDERQTY', SlxSoliPagContDet.getDouble('QUANTITY'), 2L)
            poLine.setValue('ORDERUNIT', SlxSoliPagContDet.getString('ORDERUNIT'), 2L)
            poLine.setValue('CONVERSION', 1.0, 2L)
            poLine.setValue('UNITCOST', SlxSoliPagContDet.getDouble('UNITCOST'), 2L)
            poLine.setValue('LINECOST', SlxSoliPagContDet.getDouble('LINECOST'), 2L)
            poLine.setValue('REFWO', SlxSoliPagContDet.getString('WONUM'), 11L)
            poLine.setValue('GLDEBITACCT', SlxSoliPagContDet.getString('GLACCOUNT'), 11L)
            #System.out.println("____________________GLDEBITACCT: "+poLine.getString('GLDEBITACCT'))	
            if (SlxSoliPagContDet.getString('TASKID') !="" and SlxSoliPagContDet.getString('REFWO')!=SlxSoliPagContDet.getString('WONUM')):
                poLine.setValue('ENTEREDASTASK', 1, 2L)
            i=i+1


            if SlxDetalleLiquidacionSet.isEmpty() is False:
                SlxDetalleLiquidacion = SlxDetalleLiquidacionSet.getMbo(0)
                System.out.println("____________________CONTRACTNUM: "+SlxDetalleLiquidacion.getString('ITEMNUM'))	
                if (SlxDetalleLiquidacion.getString("CONTRACTNUM") != "" ): 
                    woMboSet = SlxSoliPagContDet.getMboSet("WORKORDER")
                    woMbo = woMboSet.getMbo(0)
                    SlxDetalleLiquidacion.setValue ('AVANCELIQ',SlxSoliPagContDet.getDouble('AVANCEREP'),2L)
                    SlxDetalleLiquidacion.setValue ('CANTIDADACUM',SlxDetalleLiquidacion.getDouble('CANTIDADACUM') + SlxSoliPagContDet.getDouble('QUANTITY'),2L)
                    if woMbo.getString("STATUS")!='COMPLETO':
                        SlxDetalleLiquidacion.setValue ('QUANTITY',0,2L)

            SlxSoliPagContDet = SlxSoliPagContDetSet.moveNext()        
        poSet.save()
        mbo.getThisMboSet().save()
        System.out.println("____________________SE GUARDA PO Y POLINE")
        poSet = maximo.getMboSet('PO', currentUser)
        poSet.setWhere ('PONUM = ' + ponum)
        po = poSet.getMbo(0)
        System.out.println("____________________OBTUVE EL PO CREADO PREVIAMENTE")
        poLineSet = po.getMboSet('POLINE')
        System.out.println("____________________OBTUVE LAS POLINE")
        servRecTransSet = po.getMboSet('SERVRECTRANS')
        System.out.println("____________________CON LA PO, CREO AHORA LAS SERVRECTRANS")
        poLine = poLineSet.moveFirst()
        while (poLine is not None):
            if (poLine.getString("LINETYPE") == 'SERVICE') or (poLine.getString("LINETYPE") == 'STDSERVICE'):
                System.out.println("____________________SERV WONUM: "+poLine.getString('REFWO'))	
                servRecTrans = servRecTransSet.add(2L)
                servRecTrans.setValue('POSITEID', mbo.getString('SITEID'),2L)
                servRecTrans.setValue('PONUM', ponum,2L)
                servRecTrans.setValue('POREVISIONNUM', po.getInt('REVISIONNUM'),2L)
                servRecTrans.setValue('DESCRIPTION', poLine.getString('DESCRIPTION'),2L)
                servRecTrans.setValue('POLINENUM', poLine.getInt('POLINENUM'),2L)
                servRecTrans.setValue('LINETYPE', poLine.getString('LINETYPE'),2L)
                servRecTrans.setValue('GLDEBITACCT', poLine.getString('GLDEBITACCT'),11L)
                #System.out.println("____________________GLDEBITACCT: "+servRecTrans.getString('GLDEBITACCT'))
            poLine = poLineSet.moveNext()
        servRecTransSet.save()
        System.out.println("____________________SE GUARDA SERVRECTRANS")

####    ACTUALIZAR ESTADO ORDENES DE TRABAJO LIQUIDADAS ##################
        SlxWOSet= mbo.getMboSet("$WOREL", 'WORKORDER', "wonum in (select distinct REFWO from SLX_SOLI_PAG_CONT_DET where SLX_SOLI_PAG_CONT_DET.SLXSPCNUM=:SLXSPCNUM and SLX_SOLI_PAG_CONT_DET.SITEID=:SITEID) and SITEID=:SITEID "  )			
        SlxWO = SlxWOSet.moveFirst()			
        while (SlxWO is not None):			
            SlxejecliqSet=SlxWO.getMboSet('SLX_EJEC_CONT_LIQ')
            if (SlxejecliqSet.isEmpty() is False):
                SlxejectoliqSet=SlxWO.getMboSet('SLX_EJEC_CONT_TOLIQ')
                if SlxejectoliqSet.isEmpty() is False:
                    SlxWO.setValue ('SLXESTADOLIQ','LIQINCOMP',2L)
                else:
                    SlxWO.setValue ('SLXESTADOLIQ','LIQCOMP',2L)					
            SlxWO = SlxWOSet.moveNext()
        SlxWOSet.save()

    else:
        System.out.println("size detalle: 0")   
System.out.println("____________________TERMINA SCRIPT 7_______________________")
mbo.getThisMboSet().save()