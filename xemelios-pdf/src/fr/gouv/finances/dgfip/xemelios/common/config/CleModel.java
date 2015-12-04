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

package fr.gouv.finances.dgfip.xemelios.common.config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

/**
 * Modelise une entree de cle
 * @author chm
 * @deprecated
 */
public class CleModel implements XmlMarshallable {
    public static final transient String TAG = "cle";
    public static final transient QName QN = new QName(TAG);
    private StringBuffer balise;
    private QName qn;
    
    public CleModel(QName tagName) {
        super();
        this.qn=tagName;
        balise = new StringBuffer();
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {
        balise.append(cData);
    }
    @Override
    public void addChild(XmlMarshallable child, QName tagName)throws SAXException {}
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes)throws SAXException {
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {throw new Error("Not yet implemented"); }
    @Override
    public void validate() throws InvalidXmlDefinition {}
    public String getBalise() { return balise.toString(); }
    @Override
    public CleModel clone() {
        CleModel cm = new CleModel(QN);
        cm.balise = new StringBuffer(this.balise.toString());
        return cm;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) { return  null; }

    @Override
    public QName getQName() {
        return qn;
    }
}
