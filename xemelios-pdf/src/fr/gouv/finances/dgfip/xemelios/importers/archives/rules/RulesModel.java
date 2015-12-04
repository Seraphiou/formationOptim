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
import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author cmarchand
 */
public class RulesModel implements XmlMarshallable {
    public static final QName QN = new QName(RulesParser.RULES_NS, "rules");

    private String id;
    private ArrayList<SectionModel> sections = null;

    public RulesModel(QName qn) {
        super();
        sections = new ArrayList<SectionModel>();
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {}

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        if(tag.equals(SectionModel.QN))
            sections.add((SectionModel)child);
        else throw new SAXException(QN+": Unexpected child "+tag);
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id=attributes.getValue("id");
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        if(id!=null)
            output.addAttribute("id", id);
        for(SectionModel sm: sections) sm.marshall(output);
        output.endTag(QN);
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        for(SectionModel sm:sections) sm.validate();
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public RulesModel clone() {
        RulesModel rm = new RulesModel(QN);
        rm.id=id;
        for(SectionModel sm:sections) rm.sections.add(sm.clone());
        return rm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<SectionModel> getSections() {
        return sections;
    }

    public void setSections(ArrayList<SectionModel> sections) {
        this.sections = sections;
    }



}
