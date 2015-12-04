/*
 * Copyright
 *   2007 axYus - www.axyus.com
 *   2007 C.Marchand - christophe.marchand@axyus.com
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
 *
 * @author chm
 */
public class FuncStringIsNotNull extends FuncStringIsNull {
    public static final transient String FUNCTION_NAME = "StringIsNotNull";
    private static final Logger logger = Logger.getLogger(FuncStringIsNotNull.class);
    
    public Object evaluate(List args) throws XPathFunctionException {
        Boolean ret = Boolean.FALSE;
        if(args.size()!=2) throw new XPathFunctionException("StringIsNotNull requires exactly 2 parameters");
        boolean bRet = ((Boolean)super.evaluate(args)).booleanValue();
        ret = (bRet?Boolean.FALSE:Boolean.TRUE);
//        logger.debug("evaluate("+args+") -> "+ret);
        return ret;
    }
}
