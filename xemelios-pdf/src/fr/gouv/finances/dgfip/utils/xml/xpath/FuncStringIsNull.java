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

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import org.apache.log4j.Logger;

import org.w3c.dom.NodeList;

/**
 * Execute the StringIsNull() function.
 * Usage: <tt>xem:StringIsNull(bar,"ignorable")</tt>
 */
public class FuncStringIsNull /*extends Function */implements XPathFunction {
    public static final transient String FUNCTION_NAME = "StringIsNull";
    private static final Logger logger = Logger.getLogger(FuncStringIsNull.class);
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3835151753133175091L;

    public Object evaluate(List args) throws XPathFunctionException {
        Boolean ret = Boolean.FALSE;
        if(args.size()!=2) throw new XPathFunctionException("StringIsNull requires exactly 2 parameters");
        Object o1 = args.get(0);
        if(o1==null) ret = Boolean.TRUE;
        else if(o1 instanceof NodeList) {
            NodeList nl = (NodeList)o1;
            ret = (nl.getLength()==0?Boolean.TRUE:Boolean.FALSE);
        } else if(o1 instanceof List) {
            List l = (List)o1;
            ret = l.isEmpty();
        } else {
            ret = Boolean.FALSE;
        }
//        logger.debug("evaluate("+args+") -> "+ret);
        return ret;
    }


}
