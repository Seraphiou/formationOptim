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

import java.util.Hashtable;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;

/**
 *
 * @author chm
 */
public class FunctionResolver implements XPathFunctionResolver {
    public static final transient String EXTENSION_NS = "http://admisource.gouv.fr/projects/xemelios/ext";
    
    private static Hashtable<QName,XPathFunction> functions = null;
    static {
        functions = new Hashtable<QName,XPathFunction>();
        functions.put(new QName(EXTENSION_NS,"StringEquals","xem"),new FuncStringEquals());
        functions.put(new QName(EXTENSION_NS,"StringDiffers","xem"),new FuncStringDiffers());
        functions.put(new QName(EXTENSION_NS,"ends-with","xem"),new FuncEndsWith());
        functions.put(new QName(EXTENSION_NS,"UpperCase","xem"),new FuncUppercase());
        functions.put(new QName(EXTENSION_NS,"LowerCase","xem"),new FuncLowercase());
        functions.put(new QName(EXTENSION_NS,"InList","xem"),new FuncStringInList());
        functions.put(new QName(EXTENSION_NS,"escape-uri","xem"),new FuncEscapeUri());
        functions.put(new QName(EXTENSION_NS,"DateBefore","xem"), new FuncDateBefore());
        functions.put(new QName(EXTENSION_NS,"DateAfter","xem"), new FuncDateAfter());
        functions.put(new QName(EXTENSION_NS,"DateEquals","xem"), new FuncDateEquals());
        functions.put(new QName(EXTENSION_NS,"StringIsNull","xem"),new FuncStringIsNull());
        functions.put(new QName(EXTENSION_NS,"StringIsNotNull","xem"),new FuncStringIsNotNull());
        functions.put(new QName(EXTENSION_NS,"does-not-contain","xem"),new FuncStringDoNotContains());
        functions.put(new QName(EXTENSION_NS,"generateUniqueID","xem"),new FuncGenerateUniqueID());
        functions.put(new QName(EXTENSION_NS,"TruncDatetime","xem"), new FuncTruncDatetime());
    }
    
    /** Creates a new instance of FunctionResolver */
    public FunctionResolver() {
    }

    @Override
    public XPathFunction resolveFunction(QName functionName, int arity) {
        QName fn = new QName(getNamespace(functionName.getPrefix()),functionName.getLocalPart(),functionName.getPrefix());
        XPathFunction function=functions.get(fn);
        return function;
    }
    private static String getNamespace(String prefix) { return "http://admisource.gouv.fr/projects/xemelios/ext"; }
    
}
