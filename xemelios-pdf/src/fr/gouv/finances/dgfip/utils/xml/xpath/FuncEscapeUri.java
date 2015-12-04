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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.xpath.XPathFunctionException;

import org.apache.log4j.Logger;


/**
 * Implementation for escape-uri XPath function
 * Syntax is <tt>escape-uri(uri,decode)</tt> where decode is a boolean value : true it decodes, false it encodes.
 * @author chm
 */
public class FuncEscapeUri extends AbstractXPathFunction {
    private static final Logger logger = Logger.getLogger(FuncEscapeUri.class);
    public static final transient String FUNCTION_NAME = "escape-uri";
    
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3545795485781537336L;
    
    /**
     *
     */
    public FuncEscapeUri() {
        super();
    }
    
    @Override
    public Object evaluate(List args) throws XPathFunctionException {
        if(args.size()!=2) throw new XPathFunctionException("escape-uri requires exactly 2 parameters");
        Object o1 = args.get(0);
        String s1=null;
        s1 = getStringValue(o1);
        Object o2 = args.get(1);
        boolean escapeIt = false;
        escapeIt = Boolean.valueOf(getStringValue(o2));
        String ret = null;
        if(escapeIt) {
            try { 
                ret = URLDecoder.decode(s1,"UTF-8");
            } catch(UnsupportedEncodingException eEx) {
                logger.error("evaluate(list)",eEx);
            }
        } else {
            try {
                ret = URLEncoder.encode(s1,"UTF-8");
            } catch(UnsupportedEncodingException eEx) {
                logger.error("evaluate(list)",eEx);
            }
        }
        return ret;
    }
    
}
