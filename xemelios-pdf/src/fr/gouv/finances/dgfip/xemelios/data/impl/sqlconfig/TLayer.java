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

public class TLayer implements XmlMarshallable {

    public static final String TAG = "layer";
    public static final transient QName QN = new QName(TAG);
    private String name,  persistenceModel;
    private Hashtable<String, TDocument> documents;
    private String baseDirectory = null;

    public TLayer(QName tagName) {
        super();
        documents = new Hashtable<String, TDocument>();
    }

    public void setBaseDirectory(String baseDir) {
        baseDirectory = baseDir;
        for (TDocument doc : documents.values()) {
            doc.setBaseDirectory(baseDir);
        }
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
    }

    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (TDocument.QN.equals(tagName)) {
            TDocument doc = (TDocument) child;
            doc.setBaseDirectory(baseDirectory);
            documents.put(doc.getId(), doc);
        }
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        name = attributes.getValue("name");
        persistenceModel = attributes.getValue("persistence-model");
        return this;
    }

    @Override
    public void marshall(XmlOutputter output) {
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersistenceModel() {
        return persistenceModel;
    }

    public void setPersistenceModel(String persistenceModel) {
        this.persistenceModel = persistenceModel;
    }

    public TDocument getDocument(String docId) {
        return documents.get(docId);
    }

    public void removeDocument(String docId) {
        documents.remove(docId);
    }

    public void addDocument(String docId, TDocument doc) {
        documents.put(docId, doc);
    }

    @Override
    public TLayer clone() {
        TLayer other = new TLayer(QN);
        other.name = this.name;
        other.persistenceModel = this.persistenceModel;
        for (String s : documents.keySet()) {
            other.documents.put(s, this.documents.get(s).clone());
        }
        other.baseDirectory = baseDirectory;
        return other;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        String ext = atts.getValue("extends");
        if(ext==null) return null;
        TDocument ret = documents.get(ext).clone();
        return ret;
    }

    @Override
    public QName getQName() {
        return QN;
    }
}
