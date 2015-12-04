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

import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.w3c.dom.NodeList;


/**
 * Execute the InList() function.
 * Usage: <tt>xem:StringInList(fruit,"orange","apple","grapefruit",...)</tt>
 */
public class FuncStringInList extends AbstractXPathFunction {
    public static final transient String FUNCTION_NAME = "InList";
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3690756198181974327L;
    
    public Object evaluate(List args) throws XPathFunctionException {
        if(args.size()<2) throw new XPathFunctionException("InList requires at least 2 parameters");
        Object o1 = args.get(0);
        String valueToSearch=null;
        valueToSearch = getStringValue(o1);
        String[] data = new String[args.size()-1];
        for(int i=0;i<args.size()-1;i++) {
            Object o = args.get(i+1);
            data[i]=getStringValue(o);
        }
        Arrays.sort(data);
        int ret = Arrays.binarySearch(data,valueToSearch);
        return ret>=0?Boolean.TRUE:Boolean.FALSE;
    }
}
