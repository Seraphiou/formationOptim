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

import java.util.ArrayList;
import javax.xml.xpath.XPathFunction;

import net.sf.saxon.om.NodeInfo;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * Provides a default way to get string value from an object in a XPathExpression
 * 2009-11-10 : modification sur la récupération du texte dans un ..../text()
 * @author chm
 */
public abstract class AbstractXPathFunction implements XPathFunction {
    
    public String getStringValue(Object o) {
        if(o==null) return null;
        String ret = null;
        if(o instanceof Attr) {
            ret = ((Attr)o).getValue();
        } else if(o instanceof Text) {
            ret = ((Text)o).getData();
        } else if(o instanceof NodeList) {
            NodeList nl = (NodeList)o;
            ret=getStringValue(nl.item(0));
        } else {
            if(o instanceof String) ret=(String)o;
            else if(o instanceof ArrayList) {
                ArrayList al1 = (ArrayList)o;
                if(al1.size()>0) {
                    Object o2 = al1.get(0);
                    if(o2 instanceof Attr) ret = ((Attr)o2).getNodeValue();
                    else if(o2 instanceof CDATASection) ret = ((CDATASection)o2).getData();
                    else if(o2 instanceof Comment) ret = ((Comment)o2).getData();
                    else if(o2 instanceof ProcessingInstruction) ret = ((ProcessingInstruction)o2).getData();
                    else if(o2 instanceof Text) ret = ((Text)o2).getData();
                    else ret = o2.toString();
                } else if(o instanceof NodeInfo) {
                	ret = ((NodeInfo)o).getStringValue();
                } else return null;
            }
            else ret=o.toString();
        }
        
        return ret;
    }
    protected static String normalizeDateFormat(String s) {
        if(s.matches("^[0-9]{4}-[0-9][0-9]?-[0-9][0-9]?$")) {
            return s+"T00:00:00.000Z";
        } else return s;
    }
}
