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

/**
 *
 * @author chm
 */
public class TSql implements XmlMarshallable {
    public static final String TAG = "sql";
    public static final QName QN = new QName(TAG);
    private boolean failOnError = true;
    private StringBuffer data;
    private TPatch parent;
    
    /** Creates a new instance of TSql */
    public TSql(QName tagName) {
        super();
        data = new StringBuffer();
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
        data.append(cData);
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        if(attributes.getValue("failonerror")!=null)
            failOnError = attributes.getBooleanValue("failonerror");
        return this;
    }

    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException { }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        if(!failOnError) output.addAttribute("failonerror",failOnError);
        output.addCharacterData(data.toString());
        output.endTag(TAG);
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
    }
    
    public String getSql() {
        return getScript(getParent().getParent().getBaseName());
    }
    public TPatch getParent() { return parent; }
    public void setParent(TPatch parent) { this.parent = parent; }
    @Override
    public TSql clone() {
        TSql other = new TSql(QN);
        other.data.append(this.data.toString());
        other.failOnError=this.failOnError;
//        other.parent = this.parent.clone();
        return other;
    }
    public TSql clone(TPatch parent) {
        TSql other = clone();
        other.parent=parent;
        return other;
    }
    public boolean isFailOnError() { return failOnError; }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public QName getQName() {
        return QN;
    }

    public String getScript(String tableName) {
        return data.toString().replaceAll("\\$\\{base-name\\}",tableName);
    }
}
