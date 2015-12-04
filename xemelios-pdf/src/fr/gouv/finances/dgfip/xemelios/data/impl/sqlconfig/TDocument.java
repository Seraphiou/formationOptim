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
import javax.xml.namespace.QName;

public class TDocument implements XmlMarshallable {

    public static final transient String[] DEFAULT_COLUMN_NAMES = {"COLLECTIVITE", "COLLECTIVITE_LIB", "BUDGET", "BUDGET_LIB", "INITIAL_DOC_NAME", "ARCHIVE_NAME" };
    public static final transient String TAG = "document";
    public static final transient QName QN = new QName(TAG);
    private String id,  repositoryImportXsltFile;

    private String collectiviteCodeColname = DEFAULT_COLUMN_NAMES[0];
    private String collectiviteLibColname = DEFAULT_COLUMN_NAMES[1];
    private String budgetCodeColname= DEFAULT_COLUMN_NAMES[2];
    private String budgetLibColname = DEFAULT_COLUMN_NAMES[3];
    private String initialDocNameColname = DEFAULT_COLUMN_NAMES[4];
    private String archiveNameColname = DEFAULT_COLUMN_NAMES[5];

    private TTable repositoryTable,  listBcTable,  specialKeyTable;
    private Hashtable<String, TEtat> etats;
    private String baseDirectory, extendedDoc;

    public TDocument(QName tagName) {
        super();
        etats = new Hashtable<String, TEtat>();
    }

    public void setBaseDirectory(String baseDir) {
        baseDirectory = baseDir;
        for (TEtat etat : etats.values()) {
            etat.setBaseDirectory(baseDir);
        }
    }

    public void addCharacterData(String cData) throws SAXException {
    }

    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (TTable.QN.equals(tagName)) {
            TTable table = (TTable) child;
            if ("repository".equals(table.getType())) {
                repositoryTable = table;
            } else if ("special-key".equals(table.getType())) {
                specialKeyTable = table;
            } else if ("list-bc".equals(table.getType())) {
                listBcTable = table;
            }
        } else if (TEtat.QN.equals(tagName)) {
            TEtat etat = (TEtat) child;
            etat.setBaseDirectory(baseDirectory);
            etat.setParent(this);
            etats.put(etat.getId(), etat);
        }
    }

    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id = attributes.getValue("id");
        repositoryImportXsltFile = attributes.getValue("repository-import-xslt-file");
        collectiviteCodeColname = attributes.getValue("collectivite-code-colname")!=null ? attributes.getValue("collectivite-code-colname") : collectiviteCodeColname;
        collectiviteLibColname = attributes.getValue("collectivite-lib-colname")!=null ? attributes.getValue("collectivite-lib-colname") : collectiviteLibColname;
        budgetCodeColname = attributes.getValue("budget-code-colname")!=null ? attributes.getValue("budget-code-colname") : budgetCodeColname;
        budgetLibColname = attributes.getValue("budget-lib-colname")!=null ? attributes.getValue("budget-lib-colname") : budgetLibColname;
        return this;
    }

    public void marshall(XmlOutputter output) {
    }

    public void validate() throws InvalidXmlDefinition {
    }

    @Override
    public TDocument clone() {
        TDocument other = new TDocument(QN);
        other.id = this.id;
        other.repositoryImportXsltFile = this.repositoryImportXsltFile;
        other.collectiviteCodeColname = this.collectiviteCodeColname;
        other.collectiviteLibColname = this.collectiviteLibColname;
        other.budgetCodeColname = this.budgetCodeColname;
        other.budgetLibColname = this.budgetLibColname;
        other.initialDocNameColname = this.initialDocNameColname;
        other.archiveNameColname = this.archiveNameColname;
        other.repositoryTable = this.repositoryTable.clone();
        other.listBcTable = this.listBcTable.clone();
        other.specialKeyTable = this.specialKeyTable.clone();
        for (String s : etats.keySet()) {
            TEtat newEtat = this.etats.get(s).clone();
            newEtat.setParent(other);
            other.etats.put(s, newEtat);
        }
        other.baseDirectory = baseDirectory;
        return other;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRepositoryImportXsltFile() {
        return repositoryImportXsltFile;
    }

    public void setRepositoryImportXsltFile(String repositoryImportXsltFile) {
        this.repositoryImportXsltFile = repositoryImportXsltFile;
    }

    public TEtat getEtat(String etatId) {
        return etats.get(etatId);
    }

    public TTable getListBcTable() {
        return listBcTable;
    }

    public void setListBcTable(TTable listBcTable) {
        this.listBcTable = listBcTable;
    }

    public TTable getRepositoryTable() {
        return repositoryTable;
    }

    public void setRepositoryTable(TTable repositoryTable) {
        this.repositoryTable = repositoryTable;
    }

    public TTable getSpecialKeyTable() {
        return specialKeyTable;
    }

    public void setSpecialKeyTable(TTable specialKeyTable) {
        this.specialKeyTable = specialKeyTable;
    }

    public Iterable<TEtat> getEtats() {
        return etats.values();
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        QName name = new QName(uri,localName);
        if(TTable.QN.equals(name)) {
            String type = atts.getValue("type");
            if ("repository".equals(type)) {
                return repositoryTable;
            } else if ("special-key".equals(type)) {
                return specialKeyTable;
            } else if ("list-bc".equals(type)) {
                return listBcTable;
            }
        } else if (TEtat.QN.equals(name)) {
            return etats.get(atts.getValue("id"));
        }
        return null;
    }

    public QName getQName() {
        return QN;
    }

    public boolean equals(TDocument other) {
        return other.getId().equals(id);
    }

    public String getBudgetCodeColname() {
        return budgetCodeColname;
    }

    public String getBudgetLibColname() {
        return budgetLibColname;
    }
    
    public String getInitialDocNameColname() {
        return initialDocNameColname;
    }
    
    public String getArchiveNameColname() {
        return archiveNameColname;
    }
    
    public String getCollectiviteCodeColname() {
        return collectiviteCodeColname;
    }

    public String getCollectiviteLibColname() {
        return collectiviteLibColname;
    }

}
