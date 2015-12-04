/*
 * Copyright
 *   2005 axYus - www.axyus.com
 *   2005 C.Marchand - christophe.marchand@axyus.com
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

package fr.gouv.finances.dgfip.utils.xml.xpath;

import java.math.BigInteger;
import java.util.List;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import javax.xml.xpath.XPathFunctionException;



/**
 * Execute the DateBefore() function.<br/>
 * Usage: <tt>xem:DateBefore(date1,"date2")</tt><br/>
 * Returns <tt>TRUE</tt> if and only if <tt>date1</tt> is <strong>before</strong> and not equal <tt>date2</tt></br>
 * Dates are supposed to respect <tt>yyyy-MM-dd</tt> pattern
 */
public class FuncDateBefore extends AbstractXPathFunction {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FuncDateBefore.class);
    public static final transient String FUNCTION_NAME = "DateBefore";
    
    
    @Override
    public Object evaluate(List args) throws XPathFunctionException {
        if(args.size()!=2) throw new XPathFunctionException("DateBefore requires exactly 2 parameters");
        Object o1 = args.get(0);
        Object o2 = args.get(1);
        if(o1 instanceof BigInteger || o2 instanceof BigInteger) throw new XPathFunctionException("You must quote dates to use DateAfter");
        String s1=null, s2=null;
        s1 = getStringValue(o1);
        s2 = getStringValue(o2);
        if(s1==null && s2==null) return Boolean.FALSE;
        if(s1==null || s2==null) return Boolean.FALSE;
        Boolean jaxpRet = Boolean.FALSE;
        try {
            DatatypeFactory df = DatatypeFactory.newInstance();
            s1 = normalizeDateFormat(s1);
            s2 = normalizeDateFormat(s2);
            XMLGregorianCalendar d1 = normalize(df.newXMLGregorianCalendar(s1));
            XMLGregorianCalendar d2 = normalize(df.newXMLGregorianCalendar(s2));
            int compRet = d1.compare(d2);
            jaxpRet = (compRet <= 0)?Boolean.TRUE:Boolean.FALSE;
//            logger.debug(d1.getClass().getName());
            logger.debug("funcDateBefore-jaxp("+d1.toXMLFormat()+","+d2.toXMLFormat()+") -> "+jaxpRet+" ("+compRet+")");
        } catch(DatatypeConfigurationException dcEx) {
            logger.error("",dcEx);
        }
        return jaxpRet;
    }

    public XMLGregorianCalendar normalize(XMLGregorianCalendar d) {
        XMLGregorianCalendar ret = d.normalize();
        ret.setTimezone(TimeZone.getDefault().getOffset(ret.toGregorianCalendar().getTimeInMillis())/60000);
        return ret;
    }

    private void logDate(XMLGregorianCalendar d) {
        StringBuilder sb = new StringBuilder();
        sb.append("y=").append(d.getYear()).append("\n");
        sb.append("M=").append(d.getMonth()).append("\n");
        sb.append("d=").append(d.getDay()).append("\n");
        sb.append("h=").append(d.getHour()).append("\n");
        sb.append("m=").append(d.getMinute()).append("\n");
        logger.debug("date: "+d.toXMLFormat()+"\n"+sb.toString());
    }

    
}
