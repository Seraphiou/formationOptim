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

public class TElement implements XmlMarshallable {

    public static final transient String TAG = "element";
    public static final transient QName QN = new QName(TAG);
    private XsString specialCond,  sqlCount;
    private String id;

    public TElement(QName tagName) {
        super();
    }

    public void addCharacterData(String cData) throws SAXException {
    }

    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (SqlConfigMapping.SPECIAL_COND.equals(tagName)) {
            specialCond = (XsString) child;
        } else if (SqlConfigMapping.SQL_COUNT.equals(tagName)) {
            sqlCount = (XsString) child;
        }

    }

    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id = attributes.getValue("id");
        return this;
    }

    public void marshall(XmlOutputter output) {
    }

    public void validate() throws InvalidXmlDefinition {
    }

    @Override
    public TElement clone() {
        TElement other = new TElement(QN);
        other.id = this.id;
        other.specialCond = this.specialCond.clone();
        other.sqlCount = this.sqlCount.clone();
        return other;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpecialCond() {
        return specialCond.getData();
    }

    public void setSpecialCond(XsString specialCond) {
        this.specialCond = specialCond;
    }

    public String getSqlCount() {
        return sqlCount.getData();
    }

    public void setSqlCount(XsString sqlCount) {
        this.sqlCount = sqlCount;
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        QName name = new QName(uri, localName);
        if (SqlConfigMapping.SPECIAL_COND.equals(name)) {
            return specialCond;
        } else if (SqlConfigMapping.SQL_COUNT.equals(name)) {
            return sqlCount;
        }
        return null;
    }

    public QName getQName() {
        return QN;
    }
}
