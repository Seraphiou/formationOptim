/*
 * Copyright
 *   2009 axYus - www.axyus.com
 *   2009 C.Bosquet - charles.bosquet@axyus.com
 *
 * This file is part of XEMELIOS.
 *
 * XEMELIOS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * XEMELIOS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package fr.gouv.finances.dgfip.utils;

import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.xpath.XPathFactory;
import net.sf.saxon.type.ConversionResult;
import net.sf.saxon.type.ValidationFailure;
import net.sf.saxon.value.DateTimeValue;
import net.sf.saxon.value.DateValue;

/**
 * @author CBO
 */
public class DateUtils {

    static private final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    static private final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    static private final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    private static final SimpleDateFormat dt_sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

    static public Date parseDate(String dateAsString) throws ParseException {
        return parseDate(dateAsString, DEFAULT_DATE_FORMAT);
    }

    static public Date parseDate(String dateAsString, String format) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(dateAsString);
    }

    static public String formatDate(Date date) {
        return formatDate(date, DEFAULT_DATE_FORMAT);
    }

    static public String formatDateTime(Date date) {
        return formatDate(date, DEFAULT_DATE_TIME_FORMAT);
    }

    static public String formatTime(Date date) {
        return formatDate(date, DEFAULT_TIME_FORMAT);
    }

    static public String formatDate(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    static public String formatDate(Timestamp ts, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(ts);
    }

    static public Date truncDate(Date date) {
        return truncDate(date, DEFAULT_DATE_FORMAT);
    }

    static public Date truncDate(Date date, String format) {
        try {
            return parseDate(formatDate(date, format), format);
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
    }

    static public Date addDay(Date date, int dayCount) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, dayCount);
        return calendar.getTime();
    }

    static public Date subDay(Date date, int dayCount) {
        return addDay(date, -dayCount);
    }

    static public java.sql.Date toSqlDate(Date date) {
        return date != null ? new java.sql.Date(date.getTime()) : null;
    }

    static public String durationToString(Long ms) {
        if (ms == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        // on aura pas de traitement qui dure plus d'un an !
//        if (ms >= 31536000000l) {
//            long years = ms / 31536000000l;
//            ms = ms % 31536000000l;
//            sb.append(years);
//            sb.append(" An" + (years > 1 ? "s " : ""));
//        }
//        if (ms >= 86400000l) {
            long days = ms / 86400000l;
            ms = ms % 86400000l;
//            sb.append(sb.length() > 0 ? " " : "");
            if(days<10) sb.append("0");
            sb.append(days);
            sb.append("j ");
//            sb.append(" Jour" + (days > 1 ? "s " : ""));
//        }
//        if (ms >= 3600000l) {
            long hours = ms / 3600000l;
            ms = ms % 3600000l;
//            sb.append(sb.length() > 0 ? " " : "");
            if(hours<10) sb.append("0");
            sb.append(hours);
            sb.append(":");
//            sb.append(" Heure" + (hours > 1 ? "s " : ""));
//        }
//        if (ms >= 60000l) {
            long minutes = ms / 60000l;
            ms = ms % 60000l;
  //          sb.append(sb.length() > 0 ? " " : "");
            if(minutes<10) sb.append("0");
            sb.append(minutes);
//            sb.append(" Minute" + (minutes > 1 ? "s " : ""));
            sb.append(":");
//        }
//        if (ms >= 1000l) {
            long secondes = ms / 1000l;
            ms = ms % 1000l;
//            sb.append(sb.length() > 0 ? " " : "");
            if(secondes<10) sb.append("0");
            sb.append(secondes);
//            sb.append(" Seconde" + (secondes > 1 ? "s" : ""));
            sb.append(".");
//        }
//        if ((ms > 0l) || (sb.length() < 1)) {
//            sb.append(sb.length() > 0 ? " " : "");
            if(ms<100) sb.append("0");
            if(ms<10) sb.append("0");
            sb.append(ms);      // + " ms"
//        }
        return sb.toString();
    }

    /**
     * Renvoie une date formatée au format xs:dateTime
     * @param date
     * @return
     */
    public static String formatXsDateTime(Date date) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        DateTimeValue dtVal = new DateTimeValue(cal, true);
//        String sTmp = dt_sdf.format(date);
//        return sTmp.substring(0, sTmp.length()-2);
        return new StringBuilder(dtVal.getPrimitiveStringValue()).toString();
    }
    public static Date parseXsDateTime(String s) throws Exception {
        XPathFactory xpf = FactoryProvider.getXPathFactory();
        net.sf.saxon.xpath.XPathFactoryImpl saxonFactory = (net.sf.saxon.xpath.XPathFactoryImpl)xpf;
        ConversionResult cr = DateTimeValue.makeDateTimeValue(s, saxonFactory.getConfiguration().getConversionRules());
        if(cr instanceof DateTimeValue) {
            DateTimeValue dtVal = (DateTimeValue)cr;
            return dtVal.getCalendar().getTime();
        } else if(cr instanceof ValidationFailure) {
            cr = DateValue.makeDateValue(s, saxonFactory.getConfiguration().getConversionRules());
            if(cr instanceof DateValue) {
                return ((DateValue)cr).getCalendar().getTime();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.parse(s);
            }
        }
        return null;
    }

    private DateUtils() {
    }
}
