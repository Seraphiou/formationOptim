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

public class TexteModel implements XmlMarshallable {

    public static final transient String TAG = "text";
    public static final transient QName QN = new QName(TAG);
    private String tagName;
    private QName qn;
    private StringBuilder data;

    public TexteModel(QName tagName) {
        super();
        this.qn = tagName;
        this.tagName = qn.getLocalPart();
        this.data = new StringBuilder();
    }

    public void addCharacterData(String cData) throws SAXException {
        data.append(cData);
    }

    public void addChild(XmlMarshallable child, String tagName) throws SAXException {
    }

    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        return this;
    }

    public void marshall(XmlOutputter output) {
        output.startTag(tagName);
        output.addCharacterData(data.toString());
        output.endTag(tagName);
    }

    public void validate() throws InvalidXmlDefinition {
    }

    public TexteModel clone() {
        TexteModel tm = new TexteModel(getQName());
        tm.data = new StringBuilder();
        tm.data.append(this.getText());
        return tm;
    }

    public String getText() {
        return data.toString().trim();
    }

    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    public QName getQName() {
        return qn;
    }
}
