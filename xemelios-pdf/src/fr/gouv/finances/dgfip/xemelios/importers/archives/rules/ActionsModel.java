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
import javax.xml.xpath.XPathExpressionException;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Représente le paquet d'actions
 * @author cmarchand
 */
public class ActionsModel implements XmlMarshallable, XomDefinitionable {

    public static final QName QN = new QName(RulesParser.RULES_NS, "actions");
    public static final String ATTR_NAME = "use-previous-section";

    private ArrayList<XomDefinitionable> actions;
    private boolean usePreviousSection = false;

    public ActionsModel(QName qn) {
        super();
        actions = new ArrayList<XomDefinitionable>();
    }
    public ActionsModel(Element el) throws XPathExpressionException {
        this(QN);
        usePreviousSection = "true".equals(el.getAttributeValue(ATTR_NAME));
        Nodes nl = el.query("rul:import | rul:delete", ArchiveImporter.getNamespaceCtx());
        for(int i=0;i<nl.size();i++) {
            Element child = (Element)nl.get(i);
            XomDefinitionable toAppend = null;
            if(ImportModel.QN.getLocalPart().equals(child.getLocalName())) toAppend = new ImportModel(child);
            else toAppend = new DeleteModel(child);
            actions.add(toAppend);
        }
    }
    @Override
    public void addCharacterData(String cData) throws SAXException { }

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        if(!(tag.equals(DeleteModel.QN) || tag.equals(ImportModel.QN))) throw new SAXException(QN+" only accept "+ImportModel.QN+" and "+DeleteModel.QN+" as children");
        actions.add((XomDefinitionable)child);
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        usePreviousSection = "true".equals(attributes.getValue(ATTR_NAME));
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        if(usePreviousSection) output.addAttribute(ATTR_NAME, "true");
        for(XmlMarshallable xm:actions) xm.marshall(output);
        output.endTag(QN);
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        if(usePreviousSection && !actions.isEmpty()) throw new InvalidXmlDefinition("Si "+ATTR_NAME+" vaut 'true', il ne peut pas y avoir d'imports et d'exports");
        for(XmlMarshallable xm:actions) xm.validate();
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public ActionsModel clone() {
        ActionsModel am = new ActionsModel(QN);
        am.usePreviousSection = usePreviousSection;
        for(XomDefinitionable xm:actions) am.actions.add(((XomDefinitionable)xm.clone()));
        return am;
    }

    public ArrayList<XomDefinitionable> getActions() {
        return actions;
    }

    public void setActions(ArrayList<XomDefinitionable> actions) {
        this.actions = actions;
    }

    @Override
    public Element getXomDefinition() {
        Element ret = new Element("actions", RulesParser.RULES_NS);
        if(usePreviousSection) ret.addAttribute(new Attribute(ATTR_NAME, "true"));
        for(XomDefinitionable child:actions) {
            ret.appendChild(child.getXomDefinition());
        }
        return ret;
    }

    public boolean isUsePreviousSection() {
        return usePreviousSection;
    }

    public void setUsePreviousSection(boolean usePreviousSection) {
        this.usePreviousSection = usePreviousSection;
    }
    
}
