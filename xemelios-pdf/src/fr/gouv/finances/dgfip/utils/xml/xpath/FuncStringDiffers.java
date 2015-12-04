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

import javax.xml.xpath.XPathFunctionException;



/**
 * Execute the StringEquals() function.
 * Usage: <tt>xem:StringDiffer(bar,"foo")</tt>
 */
public class FuncStringDiffers extends AbstractXPathFunction {
    public static final transient String FUNCTION_NAME = "StringDiffers";
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3904959742068537399L;
    
    public Object evaluate(List args) throws XPathFunctionException {
        if(args.size()!=2) throw new XPathFunctionException("StringDiffers requires exactly 2 parameters");
        Object o1 = args.get(0);
        Object o2 = args.get(1);
        String s1=null, s2=null;
        s1 = getStringValue(o1);
        s2 = getStringValue(o2);
        if(s1==null && s2==null) return Boolean.FALSE;
        if(s1==null || s2==null) return Boolean.TRUE;
        return (s1.equals(s2))?Boolean.FALSE:Boolean.TRUE;
    }
    
    
}
