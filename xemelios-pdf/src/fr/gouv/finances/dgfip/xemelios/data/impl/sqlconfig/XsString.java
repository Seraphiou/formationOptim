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
package fr.gouv.finances.dgfip.xemelios.data.impl.sqlconfig;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;

public class XsString implements XmlMarshallable {
    private static final Logger logger = Logger.getLogger(XsString.class);

    private String tagName;
    private StringBuffer sb;
    private QName qn;

    public XsString(QName tagName) {
        super();
        this.qn = tagName;
        this.tagName = qn.getLocalPart();
        sb = new StringBuffer();
    }

    public void addCharacterData(String cData) throws SAXException {
        sb.append(cData);
    }

    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
    }

    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        return this;
    }

    public void marshall(XmlOutputter output) {
        output.startTag(tagName);
        output.addCharacterData(getData());
        output.endTag(tagName);

    }

    public void validate() throws InvalidXmlDefinition {
    }

    public String getData() {
        return sb.toString();
    }

    public void resetData() {
        sb.setLength(0);
    }

    @Override
    public XsString clone() {
        XsString other = new XsString(qn);
        try {
            other.addCharacterData(getData());
        } catch (Throwable t) {
            logger.error("clone().addCharData",t);
        }
        return other;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return getData().equals(obj);
        }
        return toString().equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.sb != null ? this.sb.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getData();
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    public QName getQName() {
        return qn;
    }
}
