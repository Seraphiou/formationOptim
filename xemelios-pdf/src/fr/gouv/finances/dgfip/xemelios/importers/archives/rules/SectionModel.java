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

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import fr.gouv.finances.dgfip.xemelios.importers.archives.ArchiveImporter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author cmarchand
 */
public class SectionModel implements XmlMarshallable, XomDefinitionable {
    public static final QName QN = new QName(RulesParser.RULES_NS, "section");

    private String name;
    private PredicatModel predicat;
    private ActionsModel actions;
    private String overwriteRule;


    public SectionModel(QName qn) {
        super();
    }
    public SectionModel(Element el) throws XPathExpressionException {
        this(QN);
        name = el.getAttributeValue("name");
        overwriteRule = el.getAttributeValue("overwrite-rule");
        predicat = new PredicatModel((Element)(el.query("rul:predicat", ArchiveImporter.getNamespaceCtx()).get(0)));
        actions = new ActionsModel((Element)(el.query("rul:actions", ArchiveImporter.getNamespaceCtx()).get(0)));
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {}

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        if(tag.equals(PredicatModel.QN)) predicat = (PredicatModel)child;
        else if(tag.equals(ActionsModel.QN)) actions = (ActionsModel)child;
        else throw new SAXException(QN+": Unexpected child : "+tag);
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        name = attributes.getValue("name");
        overwriteRule = attributes.getValue("overwrite-rule");
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        output.addAttribute("name", name);
        if(overwriteRule!=null) output.addAttribute("overwrite-rule", overwriteRule);
        predicat.marshall(output);
        actions.marshall(output);
        output.endTag(QN);
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        if(name==null) throw new InvalidXmlDefinition("name attribute is required is section element");
        if(predicat==null) throw new InvalidXmlDefinition("a predicat child is required in section element");
        if(actions==null) throw new InvalidXmlDefinition("a actions child is required in section element");
        predicat.validate();
        actions.validate();
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public SectionModel clone() {
        SectionModel sm = new SectionModel(QN);
        sm.name=name;
        sm.overwriteRule = overwriteRule;
        sm.predicat = predicat.clone();
        sm.actions = actions.clone();
        return sm;
    }

    public ActionsModel getActions() {
        return actions;
    }

    public void setActions(ActionsModel actions) {
        this.actions = actions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PredicatModel getPredicat() {
        return predicat;
    }

    public void setPredicat(PredicatModel predicat) {
        this.predicat = predicat;
    }

    @Override
    public Element getXomDefinition() {
        try {
            Element ret = new Element("section", RulesParser.RULES_NS);
            ret.addAttribute(new Attribute("name", name));
            if(overwriteRule!=null) ret.addAttribute(new Attribute("overwrite-rule", overwriteRule));
            ret.appendChild(predicat.getXomDefinition());
            ret.appendChild(actions.getXomDefinition());
            return ret;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Element> getDocumentsToImport(Document archiveManifeste, PropertiesExpansion applicationProperties) throws XPathExpressionException {
        ArrayList<Element> ret = new ArrayList<Element>();
        Nodes nl = archiveManifeste.query("//document");
        for(int i=0;i<nl.size();i++) {
            Element document = (Element)nl.get(i);
            for(XomDefinitionable dd:actions.getActions()) {
                if(dd instanceof ImportModel) {
                    ImportModel im = (ImportModel)dd;
                    if(im.doesDocumentSatisfy(document)) {
                        String runIf = document.getAttributeValue("run-if");
                        if(runIf!=null && runIf.length()>0 && "true".equals(applicationProperties.getProperty(runIf)))
                            ret.add(document);
                        else if(runIf==null || runIf.length()==0)
                            ret.add(document);
                    }
                }
            }
        }
        return ret;
    }

    public String getOverwriteRule() {
        return overwriteRule;
    }

    public void setOverwriteRule(String overwriteRule) {
        this.overwriteRule = overwriteRule;
    }

}
