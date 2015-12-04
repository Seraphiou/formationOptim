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
import javax.xml.namespace.QName;
import nu.xom.Attribute;
import nu.xom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author cmarchand
 */
public class DeleteModel implements XomDefinitionable {
    public static final QName QN = new QName(RulesParser.RULES_NS,"delete");
    private String archive, typeDoc, collectivite, budget, fileName;

    public DeleteModel(QName qn) {
        super();
    }
    public DeleteModel(Element el) {
        this(QN);
        archive = getAttributeValue(el, "archive");
        typeDoc = getAttributeValue(el, "type-doc");
        collectivite = getAttributeValue(el, "collectivite");
        budget = getAttributeValue(el, "budget");
        fileName = getAttributeValue(el, "file-name");
    }
    @Override
    public void addCharacterData(String cData) throws SAXException { }

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        throw new SAXException("no child expected in "+QN);
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        archive = attributes.getValue("archive");
        typeDoc = attributes.getValue("type-doc");
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
        output.startTag(QN);
        if(archive!=null && archive.length()>0) output.addAttribute("archive", archive);
        if(typeDoc!=null && typeDoc.length()>0) {
            output.addAttribute("type-doc", typeDoc);
            if(collectivite!=null && collectivite.length()>0) {
                output.addAttribute("collectivite", collectivite);
                if(budget!=null && budget.length()>0) {
                    output.addAttribute("budget", budget);
                    if(fileName!=null && fileName.length()>0)
                        output.addAttribute("file-name", fileName);
                }
            }
        }
        output.endTag(QN);
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        if(archive==null && typeDoc==null ) throw new InvalidXmlDefinition(QN+" must have either archive or type-doc attribute");
        if(typeDoc==null && collectivite!=null) throw new InvalidXmlDefinition(QN+": if you provide collectivite attribute, you must provide type-doc attribute");
        if(collectivite==null && budget !=null) throw new InvalidXmlDefinition(QN+": if you provide budget attribute, you must provide collectivite attribute");
        if(budget==null && fileName!=null) throw new InvalidXmlDefinition(QN+": if you provide file-name attribute, you must provide budget attribute");
        if(archive!=null && archive.length()==0) throw new InvalidXmlDefinition(QN+": archive attribute must not be empty");
        if(typeDoc!=null && typeDoc.length()==0) throw new InvalidXmlDefinition(QN+": type-doc attribute must not be empty");
        if(collectivite!=null && collectivite.length()==0) throw new InvalidXmlDefinition(QN+": collectivite attribute must not be empty");
        if(budget!=null && budget.length()==0) throw new InvalidXmlDefinition(QN+": budget attribute must not be empty");
        if(fileName!=null && fileName.length()==0) throw new InvalidXmlDefinition(QN+": file-name attribute must not be empty");
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public DeleteModel clone() {
        DeleteModel dm = new DeleteModel(QN);
        dm.archive = archive;
        dm.budget = budget;
        dm.collectivite = collectivite;
        dm.fileName = fileName;
        dm.typeDoc = typeDoc;
        return dm;
    }

    public String getArchive() {
        return archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getCollectivite() {
        return collectivite;
    }

    public void setCollectivite(String collectivite) {
        this.collectivite = collectivite;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTypeDoc() {
        return typeDoc;
    }

    public void setTypeDoc(String typeDoc) {
        this.typeDoc = typeDoc;
    }

    @Override
    public Element getXomDefinition() {
        Element ret = new Element(QN.getLocalPart(), QN.getNamespaceURI());
        if(archive!=null) ret.addAttribute(new Attribute("archive", archive));
        if(typeDoc!=null) ret.addAttribute(new Attribute("type-doc", typeDoc));
        if(collectivite!=null) ret.addAttribute(new Attribute("collectivite", collectivite));
        if(budget!=null) ret.addAttribute(new Attribute("budget", budget));
        if(fileName!=null) ret.addAttribute(new Attribute("file-name", fileName));
        return ret;
    }

    private static String getAttributeValue(Element el, String attributeName) {
        String ret = el.getAttributeValue(attributeName);
        if(ret!=null && ret.length()==0) ret = null;
        return ret;
    }

}
