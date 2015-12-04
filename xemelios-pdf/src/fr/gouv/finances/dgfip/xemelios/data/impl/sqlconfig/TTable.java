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

import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import java.util.ArrayList;
import javax.xml.namespace.QName;

public class TTable implements XmlMarshallable, Comparable {

    public static final String TAG = "table";
    public static final transient QName QN = new QName(TAG);
    private String baseName,  basedOn,  id,  type;
    private boolean mainTable;
    private int version;
    private Hashtable<Integer, TPatch> patches;
    private StringBuffer sb;
    private boolean ignoreConstraintError = false;
    private ArrayList<TSql> sqls;

    public String getBasedOn() {
        return basedOn;
    }

    public void setBasedOn(String basedOn) {
        this.basedOn = basedOn;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isMainTable() {
        return mainTable;
    }

    public void setMainTable(boolean mainTable) {
        this.mainTable = mainTable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public TTable(QName tagName) {
        super();
        patches = new Hashtable<Integer, TPatch>();
        sb = new StringBuffer();
        sqls = new ArrayList<TSql>();
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
        sb.append(cData);
    }

    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (TPatch.QN.equals(tagName)) {
            TPatch patch = (TPatch) child;
            patch.setTable(this);
            patches.put(new Integer(patch.getFromVersion()), patch);
        } else if(TSql.QN.equals(tagName)) {
            TSql sql = (TSql) child;
            sqls.add(sql);
        }
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id = (attributes.getValue("id") != null ? attributes.getValue("id") : id);
        baseName = (attributes.getValue("base-name") != null ? attributes.getValue("base-name") : baseName);
        basedOn = (attributes.getValue("based-on") != null ? attributes.getValue("based-on") : basedOn);
        mainTable = (attributes.getValue("main") != null ? attributes.getBooleanValue("main") : mainTable);
        version = (attributes.getValue("version") != null ? attributes.getIntValue("version") : version);
        type = (attributes.getValue("type") != null ? attributes.getValue("type") : type);
        ignoreConstraintError = (attributes.getValue("ignoreConstraintError")!=null ? attributes.getBooleanValue("ignoreConstraintError"):ignoreConstraintError);
        return this;
    }

    @Override
    public void marshall(XmlOutputter output) {
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
    }

    @Override
    public TTable clone() {
        TTable other = new TTable(QN);
        other.id = this.id;
        other.baseName = this.baseName;
        other.basedOn = this.basedOn;
        other.mainTable = this.mainTable;
        other.version = this.version;
        other.type = this.type;
        other.ignoreConstraintError=this.ignoreConstraintError;
        other.sb.append(this.sb.toString());
        for (Integer s : patches.keySet()) {
            other.patches.put(s, this.patches.get(s).clone(other));
        }

        return other;
    }

    public String getScript() {
        return sb.toString().replaceAll("\\$\\{base-name\\}", getBaseName());
    }

    public TPatch getPatchFromVersion(int version) {
        return patches.get(new Integer(version));
    }

    @Override
    public String toString() {
//		StringBuffer buff = new StringBuffer();
//		buff.append("id=").append(id).append("\nbase-name=").append(baseName);
//		return buff.toString();
        return getBaseName();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TTable) {
            TTable other = (TTable) o;
            return other.baseName.equals(baseName);
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof TTable) {
            TTable other = (TTable) o;
            return other.baseName.compareTo(baseName);
        }
        return -1;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        try {
            return patches.get(new Integer(Integer.parseInt(atts.getValue("from-version"))));
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean isIgnoreConstraintError() {
        return ignoreConstraintError;
    }

    @Override
    public QName getQName() {
        return QN;
    }

    public ArrayList<TSql> getSqls() {
        return sqls;
    }
    
}
