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

import org.apache.log4j.Logger;


/**
 * Execute the StringEquals() function.
 * Usage: <tt>xem:UpperCase(bar)</tt>
 *
 * 2009-11-10: correction des imports inutilisés
 */
public class FuncUppercase extends AbstractXPathFunction {
    public static final transient String FUNCTION_NAME = "UpperCase";
    private static final Logger logger = Logger.getLogger(FuncUppercase.class);
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3835151753133175091L;
    
    public Object evaluate(List args) throws XPathFunctionException {
        String _ret = null;
        if(args.size()!=1) throw new XPathFunctionException("UpperCase requires exactly 1 parameter");
        Object o1 = args.get(0);
        String ret = getStringValue(o1);
        _ret = ret!=null ? ret.toUpperCase() : null;
//        logger.debug("evaluate("+args+") -> "+_ret);
        return _ret;
    }
    
}
