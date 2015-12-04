/*
 * Copyright
 *  2011 axYus - http://www.axyus.com
 *  2011 C.Marchand - christophe.marchand@axyus.com
 *
 * This file is part of XEMELIOS_NB.
 *
 * XEMELIOS_NB is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * XEMELIOS_NB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS_NB; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package fr.gouv.finances.dgfip.utils.xml.xpath;

import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.xpath.XPathFunctionException;
import org.apache.log4j.Logger;

/**
 *
 * @author cmarchand
 */
public class FuncTruncDatetime extends AbstractXPathFunction {
    private static final Logger logger = Logger.getLogger(FuncTruncDatetime.class);
    public static final transient String FUNCTION_NAME = "TruncDatetime";

    @Override
    public Object evaluate(List args) throws XPathFunctionException {
        if(args.size()!=1) throw new XPathFunctionException("TruncDatetime requires exactly 1 parameter");
        Object o1 = args.get(0);
        String s1=null;
        s1 = getStringValue(o1);
        try {
            DatatypeFactory df = DatatypeFactory.newInstance();
            s1 = normalizeDateFormat(s1);
            XMLGregorianCalendar d1 = df.newXMLGregorianCalendar(s1).normalize();
            d1.setHour(0);
            d1.setMinute(0);
            d1.setSecond(0);
            d1.setMillisecond(0);
            return d1;
        } catch(DatatypeConfigurationException dcEx) {
            logger.error("",dcEx);
            return o1;
        }
    }

}
