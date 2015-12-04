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

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

public class TPatch implements XmlMarshallable {

    public static final transient String TAG = "patch";
    public static final transient QName QN = new QName(TAG);
    private int fromVersion;
    private TTable parent;
    private Vector<TSql> sqlOrders;

    public TPatch(QName tagName) {
        super();
//		sb = new StringBuffer();
        sqlOrders = new Vector<TSql>();
    }

    public void addCharacterData(String cData) throws SAXException {
//		sb.append(cData);
    }

    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (TSql.QN.equals(tagName)) {
            TSql sql = (TSql) child;
            sql.setParent(this);
            sqlOrders.add(sql);
        }
    }

    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        fromVersion = attributes.getIntValue("from-version");
        return this;
    }

    public void marshall(XmlOutputter output) {
    }

    public void validate() throws InvalidXmlDefinition {
    }

    @Override
    public TPatch clone() {
        TPatch other = new TPatch(QN);
        other.fromVersion = this.fromVersion;
//		other.parent = this.parent.clone();
        for (TSql t : sqlOrders) {
            other.sqlOrders.add(t.clone(other));
        }
        return other;
    }

    public TPatch clone(TTable parent) {
        TPatch other = clone();
        other.parent = parent;
        return other;
    }
//	public String getScript() { return sb.toString().replaceAll("\\$\\{base-name\\}",getParent().getBaseName()); }
    public int getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(int fromVersion) {
        this.fromVersion = fromVersion;
    }

    public TTable getParent() {
        return parent;
    }

    void setTable(TTable parent) {
        this.parent = parent;
    }

    public Vector<TSql> getOrders() {
        return sqlOrders;
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    public QName getQName() {
        return QN;
    }
}
