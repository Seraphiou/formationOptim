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

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import fr.gouv.finances.dgfip.xemelios.utils.FileUtils;
import java.util.ArrayList;
import javax.xml.namespace.QName;

public class TEtat implements XmlMarshallable {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TEtat.class);
    public static final String TAG = "etat";
    public static final transient QName QN = new QName(TAG);
    private TTable documentTable;
    private Hashtable<String, TTable> indexTables, otherTables;
    private TKey key1,  key2,  key3,  key4,  key5,  key6,  key7,  key8,  key9,  key10;
    private Hashtable<String, TCriteria> criterias;
    private Hashtable<String, TSqlIndex> sqlIndexesById,  sqlIndexesByPath;
    private Hashtable<String, ArrayList<TSqlIndex>> allIndexesByPath;
    private Hashtable<String, TElement> elements;
    private TTable mainTable;
    private String xsltBuffer;
    private String baseDirectory = null;
    private String id,  importXsltFile;

    private TDocument parent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImportXsltFile() {
        return importXsltFile;
    }

    public void setImportXsltFile(String importXsltFile) {
        this.importXsltFile = importXsltFile;
    }

    public TEtat(QName tagName) {
        super();
        indexTables = new Hashtable<String, TTable>();
        otherTables = new Hashtable<String, TTable>();
        criterias = new Hashtable<String, TCriteria>();
        sqlIndexesById = new Hashtable<String, TSqlIndex>();
        sqlIndexesByPath = new Hashtable<String, TSqlIndex>();
        allIndexesByPath = new Hashtable<String, ArrayList<TSqlIndex>>();
        elements = new Hashtable<String, TElement>();
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
    }

    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (TTable.QN.equals(tagName)) {
            TTable table = (TTable) child;
            logger.debug(table);
            if ("document".equals(table.getType())) {
                documentTable = table;
            } else if("index".equals(table.getType())) {
                indexTables.put(table.getId(), table);
                if (table.isMainTable()) {
                    mainTable = table;
                }
            } else if("other".equals(table.getType())) {
                otherTables.put(table.getId(), table);
            }
        } else if (SqlConfigMapping.KEY1.equals(tagName)) {
            key1 = (TKey) child;
        } else if (SqlConfigMapping.KEY2.equals(tagName)) {
            key2 = (TKey) child;
        } else if (SqlConfigMapping.KEY3.equals(tagName)) {
            key3 = (TKey) child;
        } else if (SqlConfigMapping.KEY4.equals(tagName)) {
            key4 = (TKey) child;
        } else if (SqlConfigMapping.KEY5.equals(tagName)) {
            key5 = (TKey) child;
        } else if (SqlConfigMapping.KEY6.equals(tagName)) {
            key6 = (TKey) child;
        } else if (SqlConfigMapping.KEY7.equals(tagName)) {
            key7 = (TKey) child;
        } else if (SqlConfigMapping.KEY8.equals(tagName)) {
            key8 = (TKey) child;
        } else if (SqlConfigMapping.KEY9.equals(tagName)) {
            key9 = (TKey) child;
        } else if (SqlConfigMapping.KEY10.equals(tagName)) {
            key10 = (TKey) child;
        } else if (TCriteria.QN.equals(tagName)) {
            TCriteria crit = (TCriteria) child;
            criterias.put(crit.getId(), crit);
        } else if (TSqlIndex.QN.equals(tagName)) {
            TSqlIndex index = (TSqlIndex) child;
            sqlIndexesById.put(index.getId(), index);
            if(sqlIndexesByPath.get(index.getPath())==null || index.isUseInCriterias()) {
                sqlIndexesByPath.put(index.getPath(), index);
            } else {
                logger.debug("ignoring index "+index.getId());
            }
            ArrayList<TSqlIndex> allIndexes = allIndexesByPath.get(index.getPath());
            if(allIndexes==null) allIndexes = new ArrayList<TSqlIndex>();
            allIndexes.add(index);
            allIndexesByPath.put(index.getPath(), allIndexes);
        } else if (TElement.QN.equals(tagName)) {
            TElement el = (TElement) child;
            elements.put(el.getId(), el);
        }
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id = attributes.getValue("id");
        importXsltFile = attributes.getValue("import-xslt-file");
        return this;
    }

    @Override
    public void marshall(XmlOutputter output) {
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
    }

    @Override
    public TEtat clone() {
        TEtat other = new TEtat(QN);
        other.id = this.id;
        other.importXsltFile = this.importXsltFile;
        other.xsltBuffer = this.xsltBuffer;
        other.documentTable = this.documentTable.clone();
        other.key1 = (this.key1 != null ? this.key1.clone() : null);
        other.key2 = (this.key2 != null ? this.key2.clone() : null);
        other.key3 = (this.key3 != null ? this.key3.clone() : null);
        other.key4 = (this.key4 != null ? this.key4.clone() : null);
        other.key5 = (this.key5 != null ? this.key5.clone() : null);
        other.key6 = (this.key6 != null ? this.key6.clone() : null);
        other.key7 = (this.key7 != null ? this.key7.clone() : null);
        other.key8 = (this.key8 != null ? this.key8.clone() : null);
        other.key9 = (this.key9 != null ? this.key9.clone() : null);
        other.key10 = (this.key10 != null ? this.key10.clone() : null);
        for (String s : indexTables.keySet()) {
            other.indexTables.put(s, this.indexTables.get(s).clone());
        }
        other.mainTable = other.indexTables.get(this.mainTable.getId());
        for (String s : otherTables.keySet()) {
            other.otherTables.put(s, this.indexTables.get(s).clone());
        }
        for (String s : criterias.keySet()) {
            other.criterias.put(s, this.criterias.get(s).clone());
        }
        for(String key:allIndexesByPath.keySet()) {
            ArrayList<TSqlIndex> allIndexes = allIndexesByPath.get(key);
            for(TSqlIndex si:allIndexes) {
                try {
                    other.addChild(si.clone(), TSqlIndex.QN);
                } catch(Throwable t) {}
            }
        }
//        for (String s : sqlIndexesById.keySet()) {
//            other.sqlIndexesById.put(s, this.sqlIndexesById.get(s).clone());
//        }
//        for (String s : sqlIndexesByPath.keySet()) {
//            other.sqlIndexesByPath.put(s, this.sqlIndexesByPath.get(s).clone());
//        }
        for (String s : elements.keySet()) {
            other.elements.put(s, this.elements.get(s).clone());
        }
        other.baseDirectory = baseDirectory;
        return other;
    }

    public TTable getIndexTable(String indexTableId) {
        return indexTables.get(indexTableId);
    }
    public TTable getOtherTable(String otherTableId) {
        return otherTables.get(otherTableId);
    }

    public TTable getDocumentTable() {
        return documentTable;
    }

    public void setDocumentTable(TTable documentTable) {
        this.documentTable = documentTable;
    }

    public TCriteria getCriteria(String criteriaId) {
        return criterias.get(criteriaId);
    }

    public void removeCriteria(String criteriaId) {
        criterias.remove(criteriaId);
    }

    public TSqlIndex getSqlIndexById(String indexId) {
        return sqlIndexesById.get(indexId);
    }

    public TSqlIndex getSqlIndexByPath(String path) {
        String p = cleanPath(path);
//            logger.debug("clean path="+p+"\nold path="+path);
        return sqlIndexesByPath.get(p);
    }

    protected String cleanPath(final String path) {
        String p = new String(path);
        int pos = p.indexOf("/..");
        while (pos > 0) {
            String left = p.substring(0, pos);
            int lastSlash = left.lastIndexOf("/");
            if (lastSlash < 0) {
                break;
            }
            String s = null;
            if (lastSlash > 0) {
                s = left.substring(0, lastSlash);
            } else {
                s = "";
            }
            p = s + p.substring(pos + 3);
            pos = p.indexOf("/..");
        }
        return p;
    }

    public TElement getElement(String elementId) {
        return elements.get(elementId);
    }

    public TKey getKey1() {
        return key1;
    }

    public void setKey1(TKey key1) {
        this.key1 = key1;
    }

    public TKey getKey10() {
        return key10;
    }

    public void setKey10(TKey key10) {
        this.key10 = key10;
    }

    public TKey getKey2() {
        return key2;
    }

    public void setKey2(TKey key2) {
        this.key2 = key2;
    }

    public TKey getKey3() {
        return key3;
    }

    public void setKey3(TKey key3) {
        this.key3 = key3;
    }

    public TKey getKey4() {
        return key4;
    }

    public void setKey4(TKey key4) {
        this.key4 = key4;
    }

    public TKey getKey5() {
        return key5;
    }

    public void setKey5(TKey key5) {
        this.key5 = key5;
    }

    public TKey getKey6() {
        return key6;
    }

    public void setKey6(TKey key6) {
        this.key6 = key6;
    }

    public TKey getKey7() {
        return key7;
    }

    public void setKey7(TKey key7) {
        this.key7 = key7;
    }

    public TKey getKey8() {
        return key8;
    }

    public void setKey8(TKey key8) {
        this.key8 = key8;
    }

    public TKey getKey9() {
        return key9;
    }

    public void setKey9(TKey key9) {
        this.key9 = key9;
    }

    public Iterable<TTable> getIndexTables() {
        return indexTables.values();
    }

    public TTable getMaintable() {
        return mainTable;
    }

    public Iterable<TTable> getOtherTables() {
        return otherTables.values();
    }
    @Deprecated
    public String getImportXsltBuffer() {
        if (xsltBuffer == null && getImportXsltFile() != null) {
            File f = new File(new File(baseDirectory), getImportXsltFile());
            try {
                String xslEncoding = FileUtils.getFileEncoding(f);
                xsltBuffer = FileUtils.readTextFile(f, xslEncoding);
            } catch (IOException ioEx) {
                logger.error("in TEtat.getImportXsltBuffer() :", ioEx);
            }
        }
        return xsltBuffer;
    }

    public void setBaseDirectory(String baseDir) {
        baseDirectory = baseDir;
    }
    public String getBaseDirectory() { return baseDirectory; }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        QName tagName = new QName(uri,localName);
        if (TTable.QN.equals(tagName)) {
            if("document".equals(atts.getValue("type"))) {
                return documentTable;
            } else {
                String ext = atts.getValue("extends");
                if(ext==null) return null;
                return indexTables.get(ext);
            }
        } else if (SqlConfigMapping.KEY1.equals(tagName)) {
            return key1;
        } else if (SqlConfigMapping.KEY2.equals(tagName)) {
            return key2;
        } else if (SqlConfigMapping.KEY3.equals(tagName)) {
            return key3;
        } else if (SqlConfigMapping.KEY4.equals(tagName)) {
            return key4;
        } else if (SqlConfigMapping.KEY5.equals(tagName)) {
            return key5;
        } else if (SqlConfigMapping.KEY6.equals(tagName)) {
            return key6;
        } else if (SqlConfigMapping.KEY7.equals(tagName)) {
            return key7;
        } else if (SqlConfigMapping.KEY8.equals(tagName)) {
            return key8;
        } else if (SqlConfigMapping.KEY9.equals(tagName)) {
            return key9;
        } else if (SqlConfigMapping.KEY10.equals(tagName)) {
            return key10;
        } else if (TCriteria.QN.equals(tagName)) {
            return criterias.get(atts.getValue("id"));
        } else if (TSqlIndex.QN.equals(tagName)) {
            return sqlIndexesById.get(atts.getValue("id"));
        } else if (TElement.QN.equals(tagName)) {
            return elements.get(atts.getValue("id"));
        }
        return null;
    }

    @Override
    public QName getQName() {
        return QN;
    }

    public ArrayList<TSqlIndex> getAllIndexesByPath(String path) {
        ArrayList<TSqlIndex> ret = allIndexesByPath.get(path);
        if(ret==null) ret = new ArrayList<TSqlIndex>();
        return ret;
    }

    public TDocument getParent() {
        return parent;
    }

    public void setParent(TDocument parent) {
        this.parent = parent;
    }
    
}
