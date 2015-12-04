/*
 * Copyright
 *  2012 axYus - http://www.axyus.com
 *  2012 C.Marchand - christophe.marchand@axyus.com
 *
 * This file is part of XEMELIOS_NB.
 *
 * XEMELIOS_NB is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * XEMELIOS_NB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS_NB; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package fr.gouv.finances.dgfip.xemelios.importers.archives.rules;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import fr.gouv.finances.dgfip.xemelios.importers.archives.ArchiveImporter;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import org.apache.commons.io.FilenameUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author cmarchand
 */
public class FilterModel implements XomDefinitionable {
    public static final transient QName QN_INCLUDE = new QName(RulesParser.RULES_NS, "include");
    public static final transient QName QN_EXCLUDE = new QName(RulesParser.RULES_NS, "exclude");
//    public static final transient QName QN_PREVIOUS = new QName(RulesParser.RULES_NS, "previous-import-section");
    public static final transient String MANIFESTE_NS = "http://www.xemelios.org/namespaces#manifeste";

    private String typeDoc, collectivite, budget, fileName;
    private ArrayList<AttributeModel> attributes;
    private boolean all = false;
    private QName qn;

    public FilterModel(QName qn) {
        super();
        this.qn=qn;
        attributes = new ArrayList<AttributeModel>();
    }
    public FilterModel(Element el) {
        super();
        attributes = new ArrayList<AttributeModel>();
        if("include".equals(el.getLocalName())) qn = QN_INCLUDE;
//        else if("previous-import-section".equals(el.getLocalName())) qn = QN_PREVIOUS;
        else qn = QN_EXCLUDE;
        typeDoc = el.getAttributeValue("type-doc");
        if(typeDoc!=null && typeDoc.length()==0) typeDoc=null;
        else typeDoc = ArchiveImporter.normalizeDocumentType(typeDoc);
        collectivite = el.getAttributeValue("collectivite");
        if(collectivite!=null && collectivite.length()==0) collectivite=null;
        budget = el.getAttributeValue("budget");
        if(budget!=null && budget.length()==0) budget=null;
        fileName = el.getAttributeValue("file-name");
        if(fileName!=null && fileName.length()==0) fileName=null;
        Nodes nl = el.query("rul:attribute", ArchiveImporter.getNamespaceCtx());
        for(int i=0;i<nl.size();i++) {
            attributes.add(new AttributeModel((Element)nl.get(i)));
        }
    }

    @Override
    public void addCharacterData(String cData) throws SAXException { }

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        attributes.add((AttributeModel)child);
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        typeDoc = attributes.getValue("type-doc");
        if(typeDoc!=null) typeDoc = ArchiveImporter.normalizeDocumentType(typeDoc);
        all = attributes.getValue("all")!=null;
        collectivite = attributes.getValue("collectivite");
        budget = attributes.getValue("budget");
        fileName = attributes.getValue("file-name");
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(qn);
        if(all) output.addAttribute("all", "true");
        else {
            if(typeDoc!=null) output.addAttribute("type-doc", typeDoc);
            if(collectivite!=null) output.addAttribute("collectivite", collectivite);
            if(budget!=null) output.addAttribute("budget", budget);
            if(fileName!=null) output.addAttribute("file-name", fileName);
        }
        for(AttributeModel am:attributes) am.marshall(output);
        output.endTag(qn);
    }

    @Override
    public void validate() throws InvalidXmlDefinition { }

    @Override
    public QName getQName() {
        return qn;
    }

    @Override
    public FilterModel clone() {
        FilterModel ret = new FilterModel(qn);
        ret.all = all;
        ret.typeDoc = typeDoc;
        for(AttributeModel am:attributes) ret.attributes.add(am.clone());
        return ret;
    }

    /**
     * Renvoie <tt>true</tt> si ce document satisfait ce filtre
     * @param document
     * @return
     */
    public boolean matches(Element document) {
        if(all) return true;
        else {
            boolean ret = true;
            if(typeDoc!=null) {
                String documentType = ArchiveImporter.normalizeDocumentType(document.getAttributeValue("type"));
                ret = ret && typeDoc.equals(documentType);
            }
            if(collectivite!=null) {
                String documentCollectivite = document.getAttributeValue("buIdCol");
                ret = ret && collectivite.equals(documentCollectivite);
            }
            if(budget!=null) {
                String documentBudget = document.getAttributeValue("buCode");
                ret = ret && budget.equals(documentBudget);
            }
            if(fileName!=null) {
                String documentFileName = FilenameUtils.getName(document.getAttributeValue("path"));
                ret = ret && fileName.equals(documentFileName);
            }
            for(AttributeModel am:attributes) {
                ret = ret && am.matches(document);
            }
            return ret;
        }
    }

    @Override
    public Element getXomDefinition() {
        Element ret = new Element(qn.getLocalPart(), qn.getNamespaceURI());
        if(all) ret.addAttribute(new Attribute("all", "true"));
        else {
            ret.addAttribute(new Attribute("type-doc", typeDoc));
            if(collectivite!=null) ret.addAttribute(new Attribute("collectivite", collectivite));
            if(budget!=null) ret.addAttribute(new Attribute("budget", budget));
            if(fileName!=null) ret.addAttribute(new Attribute("file-name", fileName));
        }
        for(AttributeModel am:attributes) ret.appendChild(am.getXomDefinition());
        return ret;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public void setCollectivite(String collectivite) {
        this.collectivite = collectivite;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTypeDoc(String typeDoc) {
        this.typeDoc = typeDoc;
    }
    
}
