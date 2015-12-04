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

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

public class TKey implements XmlMarshallable {

    private String tagName;
    private ArrayList<String> pathes;
    private QName qn;

    public TKey(QName tagName) {
        super();
        this.qn = tagName;
        this.tagName = qn.getLocalPart();
        pathes = new ArrayList<String>();
    }

    public void addCharacterData(String cData) throws SAXException {

    }

    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (SqlConfigMapping.PATH.equals(tagName)) {
            pathes.add(((XsString) child).getData());
        }
    }

    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        return this;
    }

    public void marshall(XmlOutputter output) {
    }

    public void validate() throws InvalidXmlDefinition {
    }

    @Override
    public TKey clone() {
        return this;
    }

    public ArrayList<String> getPathes() {
        return pathes;
    }

    public String getTagName() {
        return tagName;
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    public QName getQName() {
        return qn;
    }
}
