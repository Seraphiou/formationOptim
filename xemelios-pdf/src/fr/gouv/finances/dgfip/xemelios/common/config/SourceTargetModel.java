/*
 * Copyright
 *  2010 axYus - http://www.axyus.com
 *  2010 C.Marchand - christophe.marchand@axyus.com
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
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author cmarchand
 */
public class SourceTargetModel implements NoeudModifiable {
//    public static final QName QNAME = new QName("critere-mapping");
    private QName qn;
    private NoeudModifiable parent;
    private String source, target, id;

    public SourceTargetModel(QName qn) {
        super();
        this.qn = qn;
    }

    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return parent;
    }

    @Override
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        this.parent=p;
    }

    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        return null;
    }

    @Override
    public void modifyAttr(String attrName, String value) {
        if("source".equals(attrName)) source = value;
        else if("target".equals(attrName)) target = value;
    }

    @Override
    public void modifyAttrs(Attributes attrs) { }

    @Override
    public String[] getChildIdAttrName(String childTagName) {
        return null;
    }

    @Override
    public void resetCharData() { }

    @Override
    public String getIdValue() {
        return getId();
    }

    @Override
    public String getConfigXPath() {
        return getParentAsNoeudModifiable().getConfigXPath().concat("/critere-mapping");
    }

    @Override
    public void addCharacterData(String cData) throws SAXException { }

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException { }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        source = attributes.getValue("source");
        target = attributes.getValue("target");
        id = attributes.getValue("id");
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public void marshall(XmlOutputter output) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        if(source==null) throw new InvalidXmlDefinition(getConfigXPath().concat("/@here is required"));
        if(target==null) throw new InvalidXmlDefinition(getConfigXPath().concat("/@remote is required"));
    }

    @Override
    public QName getQName() {
        return qn;
    }
    @Override
    public SourceTargetModel clone() {
        // TODO
        return this;
    }

    public String getSource() {
        return source;
    }

    public String getId() {
        return id;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public void prepareForUnload() {
        parent = null;
    }
    
}