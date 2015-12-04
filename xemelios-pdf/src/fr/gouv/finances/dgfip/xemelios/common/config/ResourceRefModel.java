/*
 * 
 * Copyright
 *  2009 axYus - www.axyus.com
 *  2009 Christophe Marchand <christophe.marchand@axyus.com>
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
 * 
 */

package fr.gouv.finances.dgfip.xemelios.common.config;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author Christophe Marchand <christophe.marchand@axyus.com>
 */
public class ResourceRefModel implements XmlMarshallable {
    public static final String TAGNAME="resource-ref";
    public static final QName QNAME = new QName(TAGNAME);

    private String refId;

    public ResourceRefModel(QName c) {
        super();
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {}

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {}

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        refId = attributes.getValue("ref-id");
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QNAME);
        output.addAttribute("ref-id", refId);
        output.endTag(QNAME);
    }

    @Override
    public void validate() throws InvalidXmlDefinition {}

    @Override
    public QName getQName() {
        return QNAME;
    }

    @Override
    public ResourceRefModel clone() {
        ResourceRefModel rrm = new ResourceRefModel(QNAME);
        rrm.refId=this.refId;
        return rrm;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

}
