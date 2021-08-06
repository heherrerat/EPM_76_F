package com.epm.util;

import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import psdi.app.asset.AssetMeterRemote;
import psdi.app.measurement.MeasurementRemote;
import psdi.app.meter.DeployedMeter;
import psdi.app.meter.DeployedMeterRemote;
import psdi.app.system.CrontaskParamInfo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import static com.epm.util.IntScadaCron.ReadingLevel.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import static java.sql.ResultSet.*;
import java.sql.SQLException;
import java.sql.Statement;
import psdi.security.ConnectionKey;

/**
 *
 * @author javalos
 */
public class IntScadaCron extends SimpleCronTask
{
    CrontaskParamInfo[] params;
    private static final String param_waitHours_continuous = "HorasEsperaContinuos";
    private static final String param_waitHours_oscillating = "HorasEsperaOscilantes";
    
    MboRemote deployedMeter;
    MXLogger logger;    
    int waitHoursContinuous;
    int waitHoursOscillating;
    MboSetRemote assetMeterSet, locationMeterSet, measurementSet;
    int processed = 0, imported = 0;
    MboRemote measurePoint = null;
    
    @Override
    public void cronAction() {
        
        String tag = null, prevTag = null, meterType = null;
        Exception prevException = null;
        Reading lastReading = null, lastBelow = null, lastAbove = null;
        Date readingDate = null;
        double readingValue = 0.0;
        logger = getCronTaskLogger();
        String reason;
        
        ConnectionKey connKey = getRunasUserInfo().getConnectionKey();
        Connection conn;
        ResultSet rs = null;
        
        try {
            waitHoursContinuous = getNonNullParamAsInt(param_waitHours_continuous);
            waitHoursOscillating = getNonNullParamAsInt(param_waitHours_oscillating);
            
            processed = 0;
            imported = 0;
            
            assetMeterSet = MXServer.getMXServer().getMboSet("assetmeter", getRunasUserInfo());
            locationMeterSet = MXServer.getMXServer().getMboSet("locationmeter", getRunasUserInfo());
            
            conn = MXServer.getMXServer().getDBManager().getConnection(connKey);
            Statement st = conn.createStatement(TYPE_FORWARD_ONLY, CONCUR_UPDATABLE);
            String campos = "slxinterscadaid, tag, valorlectura, fechahora, fechareg, flaghistorico, flagproceso, codresult, descresult";
            rs = st.executeQuery("select "+ campos +" from slxinterscada where flaghistorico=0 and flagproceso=0 order by tag, fechahora");
            
            while (rs.next()) {
                try { 
                    rs.updateString("codresult", "IGN");
                    rs.updateNull("descresult");
                    
                    tag = rs.getString("tag");
                    readingDate = rs.getTimestamp("fechahora");
                    readingValue = rs.getDouble("valorlectura");

                    if (tag.equals(prevTag)) {
                        if (prevException instanceof DeployedMeterNotFoundException)
                            throw prevException;
                    } else { 
                        String where = "slxtagscada = '" + tag + "'";
                        if ((deployedMeter = queryMboSet(assetMeterSet, where + " and siteid = (select a.siteid from asset a inner join assetmeter m on a.assetnum = m.assetnum and a.siteid = m.siteid and m.slxtagscada = '" + tag + "' and a.moved = 0)", null, null)) == null)
                            if ((deployedMeter = queryMboSet(locationMeterSet, where, null, null)) == null)
                                throw new DeployedMeterNotFoundException();

                        meterType = this.deployedMeter.getMboSet("meter").getMbo(0).getString("metertype");
                        logger.debug("IntScada: Tipo de medidor: "+ meterType);
                        if (meterType.equals("GAUGE")) {
                            lastReading = createReading(queryMboSet(deployedMeter.getMboSet("measurement"), "measuredate <= :1", new Object[] {readingDate}, "measuredate desc"));
                        } else {
//							lastReading = createReading(queryMboSet(deployedMeter.getMboSet("meterreading"), "readingdate <= :1", new Object[] { readingDate }, "readingdate desc"));
							lastReading = createReading(queryMboSet(deployedMeter.getMboSet(deployedMeter instanceof AssetMeterRemote ? "meterreading" : "locmeterreading"), "readingdate <= :1", new Object[] { readingDate }, "readingdate desc"));
                        	logger.debug("IntScada: ultima lectura -> "+ lastReading);
                        }

                        if (deployedMeter.getMboValueData("pointnum").isNull())
                            measurePoint = null;
                        else {
                            measurePoint = deployedMeter.getMboSet("measurepoint").getMbo(0);
                            measurementSet = measurePoint.getMboSet("measurement");
                            lastBelow = createReading(queryMboSet(measurementSet, "measurementvalue <= :1 and measuredate <= :2", new Object[] {measurePoint.getDouble("lowerwarning"), readingDate}, "measuredate desc"));
                            lastAbove = createReading(queryMboSet(measurementSet, "measurementvalue >= :1 and measuredate <= :2", new Object[] {measurePoint.getDouble("upperwarning"), readingDate}, "measuredate desc"));
                        }
                    }
                    
                    reason = null;
//					Si es continuo y se cumple la ventana
                    if (meterType.equals("CONTINUOUS")) {
                    	if (isTimeElapsed(lastReading, rs, true))
                    		lastReading = importReading(rs, waitHoursContinuous + " horas desde la última medición");
//					Si es oscilante SIN PUNTO de medición
                    } else if (measurePoint == null) {
                    	if (isTimeElapsed(lastReading, rs, false)) //pasar ventana del medidor
                    		lastReading = importReading(rs, waitHoursOscillating + " horas desde la última medición");
//					Si es oscilante CON PTO de medición
                    } else {
                    	ReadingLevel level = getReadingLevel(readingValue);
                    	switch (level) {
/* **
* Autor: Ariel Ibaceta
* Problema: error en integracion con SCADA esta importando mas de la cuenta en rango
* Solucion: El if a continuacion encargado de validar los lapsos de tiempo, no tenia las llaves y dejo fuera la linea que hace el import,
* por lo que cada vez que entraba aqui, esta hace que el mensaje se repita.
* 								if (isTimeElapsed(lastReading, rs, false))
*                               	logger.debug("IntScadaCron: Valor en RANGO: lastReading.value: "+ lastReading.value + " / new reading.value: "+ readingValue);
*                                   lastReading = lastAbove = lastBelow = importReading(rs, waitHoursOscillating + " horas desde última medición " + level);
* **/
                    	case IN_RANGE:
							if (isTimeElapsed(lastReading, rs, false)) {
								//logger.debug("IntScadaCron: Valor en RANGO: lastReading.value: " + lastReading.value + " / new reading.value: " + readingValue);
								lastReading = lastAbove = lastBelow = importReading(rs, waitHoursOscillating + " horas desde ultima medicion " + level);																																																
							}
							break;
                            
                    	case LOWER_WARNING:
                    	case LOWER_ACTION:
                    		if (lastBelow == null || getReadingLevel(lastBelow) == IN_RANGE || (getReadingLevel(lastBelow) == LOWER_WARNING && level == LOWER_ACTION))
                    			reason = "lectura escaló a " + level;
                    		else if (isTimeElapsed(lastBelow, rs, false))
                    			reason = waitHoursOscillating + " horas desde última medición en nivel de " + level;
                    		if (reason != null)
                    			lastReading = lastBelow = importReading(rs, reason);
                    		break;
                    	case UPPER_WARNING:
                    	case UPPER_ACTION:
                    		if (lastAbove == null || getReadingLevel(lastAbove) == IN_RANGE || (getReadingLevel(lastAbove) == UPPER_WARNING && level == UPPER_ACTION))
                    			reason = "lectura escaló a " + level;
                    		else if (isTimeElapsed(lastAbove, rs, false)) 
                    			reason = waitHoursOscillating + " horas desde última medición en nivel de " + level;
                    		if (reason != null)
                    			lastReading = lastAbove = importReading(rs, reason);
                    		break;
                    	}
                    }
                    prevException = null;
                } catch (Exception e) {
                	prevException = e;
                	logger.debug("IntScada: e == null: "+ (e == null));
                	e.printStackTrace(System.out);
                	logger.debug("IntScada: Mensaje Exception: "+e.getMessage());
                	String errorMsg = e.getMessage().substring(0, Math.min(e.getMessage().length(), 254));
                	rs.updateString("codresult", "ERR");
                	rs.updateString("descresult", errorMsg);
                	logger.info("Lectura con error: " + errorMsg);
                } finally {
                	processed++;
                	rs.updateBoolean("flagproceso", true);
                	rs.updateRow();
                	prevTag = tag;
                }
            }
            logger.info("Finalizada cronAction de tarea cron " + getCrontaskInstance().getId() + ": " + processed + " lecturas procesadas, " + imported + " importadas.");
        	} catch (Exception ex) {
        		ex.printStackTrace(System.out);
        		try { logger.error("Ha ocurrido una excepcion inesperada en tarea cron " + getCrontaskInstance().getId() + ": " + ex.getMessage()); } catch (Exception ex1) {}
        	} finally {
        		try { rs.close(); } catch (SQLException e) {}
        		try { MXServer.getMXServer().getDBManager().freeConnection(connKey); } catch (Exception e) {}
        		try { assetMeterSet.close(); } catch (Exception e) {}
        		try { locationMeterSet.close(); } catch (Exception e) {}
        	}
    }
	
    private boolean isTimeElapsed(Reading reading1, ResultSet reading2, boolean isContinuous)throws MXException, RemoteException, SQLException{
		if (reading1 == null) {
			return true;
		}
		Calendar date1 = Calendar.getInstance();
		date1.setTimeInMillis(reading1.date.getTime());
		Calendar date2 = Calendar.getInstance();
		date2.setTimeInMillis(reading2.getTimestamp("fechahora").getTime());
		logger.debug("IntScadaCron: isTimeElapsed: antes date2: " + date2.getTime());
		date2.add(Calendar.MILLISECOND, 1);
		logger.debug("IntScadaCron: isTimeElapsed: despues date2: " + date2.getTime());
		date1.add(Calendar.HOUR, isContinuous ? waitHoursContinuous : waitHoursOscillating);
		return date2.after(date1);
	}
	private class Reading{
		Double value;
		Date date;
        public Reading(ResultSet rs) throws SQLException {
            value = rs.getDouble("valorlectura");
            date = rs.getTimestamp("fechahora");
        }
        public Reading(MboRemote mbo) throws MXException, RemoteException {
            value = mbo.getDouble(mbo instanceof MeasurementRemote ? "measurementvalue" : "reading");
            date = mbo.getDate(mbo instanceof MeasurementRemote ? "measuredate" : "readingdate");
        }
    }

    public Reading createReading(MboRemote mbo) throws RemoteException, MXException {
    	return mbo == null ? null : new Reading(mbo);
    }
    
    private Reading importReading(ResultSet rs, String reason) throws MXException, RemoteException, SQLException {
        deployedMeter.setValue("inspector", getRunasUserInfo().getLoginUserName());
        deployedMeter.setValue("newreadingdate", rs.getTimestamp("fechahora"));
        deployedMeter.setValue("newreading", NumberFormat.getInstance().format(rs.getDouble("valorlectura")));
        deployedMeter.setValue("isdelta", false); 
        deployedMeter.getThisMboSet().save();
        deployedMeter = deployedMeter.getThisMboSet().getMbo(0);    // requery the set and refresh the reference
        rs.updateString("codresult", "IMP");
        rs.updateString("descresult", "importada - motivo: " + reason);
        imported++;
        logger.info("Lectura importada correctamente: " + reason);
        return new Reading(rs);
    }
    class DeployedMeterNotFoundException extends Exception {
        public DeployedMeterNotFoundException() {
            super("No se encontro el medidor para el tag.");
        }
    }
	
    private MboRemote queryMboSet(MboSetRemote mboSet, String where, Object[] params, String orderBy) throws RemoteException, MXException {
        SqlFormat sqlFormat = new SqlFormat(where);
        int i = 0;
        if (params != null) {
            for (Object param : params) {
                if (param instanceof Double)
                    sqlFormat.setDouble(++i, (Double) param);
                else if (param instanceof Date)
                    sqlFormat.setDate(++i, (Date) param);
            }
        }
        mboSet.reset();
        mboSet.setWhere(sqlFormat.format());
        mboSet.setOrderBy(orderBy);
        return mboSet.moveFirst();
    }
    
    public String getNonNullParamAsString(String parameter) throws MXException, RemoteException {
        String valor = getParamAsString(parameter);
        if (isNullOrEmpty(valor))
            throw new IllegalArgumentException("El valor del parametro " + parameter + " no puede ser nulo.");
        return valor;
    }
    
    public int getNonNullParamAsInt(String parameter) throws MXException, RemoteException {
        getNonNullParamAsString(parameter);
        try {
            return getParamAsInt(parameter);
        } catch (MXException e) {
            throw new IllegalArgumentException("El valor del parsmetro " + parameter + " debe ser un numero entero.");
        }
    }
    
    @Override
    public CrontaskParamInfo[] getParameters() throws MXException, RemoteException {
        if (params == null) {
            params = new CrontaskParamInfo[] {
              newParam(param_waitHours_continuous, "Tiempo de espera en horas", "12"),
              newParam(param_waitHours_oscillating, "Tiempo de espera en horas", "4")
            };
        }
        return params;
    }
    
    private CrontaskParamInfo newParam(String name, String description, String defaultValue) {
        CrontaskParamInfo p = new CrontaskParamInfo();
        p.setName(name);
        p.setDefault(defaultValue);
        return p;
    }    

    private boolean isNullOrEmpty(String string) { 
        return string == null || string.length() == 0;
    }
    
    ReadingLevel getReadingLevel(Reading reading) throws MXException, RemoteException {
        return getReadingLevel(reading.value);
    }
    
    ReadingLevel getReadingLevel(double readingValue) throws MXException, RemoteException {
        if (readingValue >= measurePoint.getDouble("upperaction"))
            return UPPER_ACTION;
        else if (readingValue >= measurePoint.getDouble("upperwarning"))
            return UPPER_WARNING;
        else if (readingValue > measurePoint.getDouble("lowerwarning"))
            return IN_RANGE;
        else if (readingValue > measurePoint.getDouble("loweraction"))
            return LOWER_WARNING;
        else
            return LOWER_ACTION;
    }
    
    enum ReadingLevel {
        IN_RANGE("dentro de rango"),
        LOWER_WARNING("alerta inferior"),
        LOWER_ACTION("acción inferior"),
        UPPER_WARNING("alerta superior"),
        UPPER_ACTION("acción superior");
        
        private final String displayText;
        
        ReadingLevel(String displayText) {
            this.displayText = displayText;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }
}
