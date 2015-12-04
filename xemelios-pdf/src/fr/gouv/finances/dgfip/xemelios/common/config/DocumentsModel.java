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

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import fr.gouv.finances.dgfip.xemelios.common.Constants;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;

/**
 * Modelise un ensemble de documents
 * @author chm
 */
public class DocumentsModel implements XmlMarshallable, EnvironmentDomain, NoeudModifiable {
    private static final Logger logger = Logger.getLogger(DocumentsModel.class);
    public static final transient String TAG = "documents";
    public static final transient QName QN = new QName(TAG);
    TreeSet<DocumentModel> documents;
    Hashtable<String,DocumentModel> docsById;
    Vector<EnvironmentDomain> vDocuments;
    private Document smallDOM;
    private QName qn;
    
    public DocumentsModel(QName tag) {
        super();
        this.qn=tag;
        documents = new TreeSet<DocumentModel>();
        vDocuments = new Vector<EnvironmentDomain>();
        docsById = new Hashtable<String,DocumentModel>();
    }
    
    @Override
    public void addCharacterData(String cData) throws SAXException {throw new SAXException("pas de CDATA attendu");}
    @Override
    public void addChild(XmlMarshallable child, QName tagName)	throws SAXException {
        if(DocumentModel.QN.equals(tagName)) {
            DocumentModel dm = (DocumentModel)child;
            if(docsById.containsKey(dm.getId())) {
                DocumentModel old = docsById.get(dm.getId());
                documents.remove(old);
                vDocuments.remove(old);
                docsById.remove(old.getId());
            }
            documents.add(dm);
            vDocuments.add(dm);
            docsById.put(dm.getId(),dm);
            dm.setParentAsNoeudModifiable(this);
        } else throw new SAXException(tagName + ": tag non attendu");
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {return this;}
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        for(DocumentModel doc:documents)
            doc.marshall(output);
        output.endTag(QN);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {
        for(DocumentModel dm:documents) dm.validate();
    }
    public TreeSet<DocumentModel> getDocuments() { return documents; }
    public DocumentModel getDocumentById(String docId) {
        return docsById.get(docId);
    }
    @Override
    public DocumentsModel clone() {
        DocumentsModel dom = new DocumentsModel(QN);
        for(DocumentModel dm:this.documents)
            try {
                dom.addChild(dm.clone(),DocumentModel.QN);
            } catch(Throwable t) {
                logger.error("clone().document",t);
            }
        return dom;
    }
    
    @Override
    public EnvironmentDomain getChildAt(int domain,int pos) {
        Iterator<DocumentModel> it = documents.iterator();
        for(int i=0;i<pos;i++) it.next();
        return it.next();
    }
    
    @Override
    public boolean hasEnvironment(int domain) { return false; }
    
    @Override
    public Enumeration<VariableModel> getVariables(int domain) { return null; }

    @Override
    public int getChildCount(int domain, PropertiesExpansion applicationProperties) { return documents.size(); }

    @Override
    public Enumeration<EnvironmentDomain> children(int domain, PropertiesExpansion applicationProperties) { return vDocuments.elements(); }
    
    @Override
    public String toString() { return "documents"; }
    
    @Override
    public Object getValue(final String path) throws DataConfigurationException {
        String sTmp;
        if(path.startsWith(TAG)) sTmp = path.substring(TAG.length());
        else sTmp = path;
        if(sTmp.startsWith("/")) sTmp = sTmp.substring(1);
        if(sTmp.startsWith("@")) {
            throw new DataConfigurationException(path+" is not significant on "+DocumentsModel.class.getName());
        }
        // now, sTmp should starts with document
        if(sTmp.startsWith(DocumentModel.TAG)) {
            int pos = sTmp.indexOf("/");
            String next = sTmp.substring(pos);
            String condition = sTmp.substring(DocumentModel.TAG.length(),pos);
            if(condition.startsWith("[") && condition.endsWith("]")) condition = condition.substring(1,condition.length()-1).trim();
            if(condition.startsWith("@id=")) {
                String documentId = condition.substring(5,condition.length()-1);
                DocumentModel dm = getDocumentById(documentId);
                return dm.getValue(next);
            } else {
                throw new DataConfigurationException(condition+" is not valid to access to "+DocumentModel.TAG);
            }
        } else {
            throw new DataConfigurationException(sTmp + " : invalid path on "+DocumentsModel.class.getName());
        }
    }
    @Override
    public void setValue(final String path, final Object value) throws DataConfigurationException {
        String sTmp;
        if(path.startsWith(TAG)) sTmp = path.substring(TAG.length());
        else sTmp = path;
        if(sTmp.startsWith("/")) sTmp = sTmp.substring(1);
        if(sTmp.startsWith("@")) {
            throw new DataConfigurationException(path+" is not significant on "+DocumentsModel.class.getName());
        }
        // now, sTmp should starts with document
        if(sTmp.startsWith(DocumentModel.TAG)) {
            int pos = sTmp.indexOf("/");
            String next = sTmp.substring(pos);
            String condition = sTmp.substring(DocumentModel.TAG.length(),pos);
            if(condition.startsWith("[") && condition.endsWith("]")) condition = condition.substring(1,condition.length()-1).trim();
            if(condition.startsWith("@id=")) {
                String documentId = condition.substring(5,condition.length()-1);
                DocumentModel dm = getDocumentById(documentId);
                dm.setValue(next,value);
            } else {
                throw new DataConfigurationException(condition+" is not valid to access to "+DocumentModel.TAG);
            }
        } else {
            throw new DataConfigurationException(sTmp + " : invalid path on "+DocumentsModel.class.getName());
        }
    }
    /**
     * Disponible uniquement pour palier au bug sur les savedRequests. Ne pas utiliser en dehors
     * @param elementId L'id de l'element cherche
     * @return tous les elements dont l'id est celui specifie
     */
    public Vector<ElementModel> getElementsById(String elementId) {
        Vector<ElementModel> ret = new Vector<ElementModel>();
        for(DocumentModel dm:documents) {
            ret.addAll(dm.getElementsById(elementId));
        }
        return ret;
    }
    private void createSmallDOM() {
        try {
            DocumentBuilderFactory domFactory = FactoryProvider.getDocumentBuilderFactory();
            domFactory.setNamespaceAware(true);
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
            Document doc = domBuilder.newDocument();
            Element domDocuments = doc.createElementNS(Constants.CONFIG_NS_URI,"conf:"+TAG);
            doc.appendChild(domDocuments);
            for(DocumentModel dm:getDocuments()) {
                Element document = dm.createSmallDOM(doc);
                domDocuments.appendChild(document);
            }
            smallDOM = doc;
        } catch(ParserConfigurationException pcEx) {
            logger.error("createSmallDom()",pcEx);
        }
    }
    public Document getSmallDOM() { 
        if(smallDOM==null) createSmallDOM();
        return smallDOM;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        if(atts.getValue("extends")!=null) return docsById.get(atts.getValue("extends")).clone();
        else return docsById.get(atts.getValue("id"));
    }

    @Override
    public QName getQName() { return qn;}

    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return null;
    }

    @Override
    public void setParentAsNoeudModifiable(NoeudModifiable p) { }

    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        return getDocumentById(id);
    }

    @Override
    public void modifyAttr(String attrName, String value) { }

    @Override
    public void modifyAttrs(Attributes attrs) { }

    @Override
    public String[] getChildIdAttrName(String childTagName) { return new String[] {"id"}; }

    @Override
    public void resetCharData() { }

    @Override
    public String getIdValue() { return null; }

    @Override
    public String getConfigXPath() {
        //return "/documents";
        // dans un premier temps, pour compatibilité
        return "";
    }
    /**
     * Cette méthode permet de supprimer toutes les références croisées vers les parents dans les enfants
     */
    public void prepareForUnload() {
        for(DocumentModel dm:documents) {
            dm.prepareForUnload();
        }
    }
}
