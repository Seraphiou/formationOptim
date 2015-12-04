/*
 * Copyright
 *  2010 axYus - http://www.axyus.com
 *  2010 C.Marchand - christophe.marchand@axyus.com
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
 * Permet de modéliser une valeur
 * @author cmarchand
 */
public class ValueModel implements NoeudModifiable {
    public static final transient QName QNAME = new QName("value");
    private String key, value;
    private RequiredCritereModel parent;

    public ValueModel(QName qn) {
        super();
    }

    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return parent;
    }

    @Override
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        parent = (RequiredCritereModel)p;
    }

    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        return null;
    }

    @Override
    public void modifyAttr(String attrName, String value) {
        if("key".equals(attrName)) key = value;
        else if("value".equals(attrName)) this.value = value;
    }

    @Override
    public void modifyAttrs(Attributes attrs) {
        for(int i=0;i<attrs.getLength();i++) {
            modifyAttr(attrs.getLocalName(i), attrs.getValue(i));
        }
    }

    @Override
    public String[] getChildIdAttrName(String childTagName) {
        return null;
    }

    @Override
    public void resetCharData() { }

    @Override
    public String getIdValue() { return "key"; }

    @Override
    public String getConfigXPath() {
        return getParentAsNoeudModifiable().getConfigXPath().concat("/").concat("value[@key='").concat(this.key).concat("']");
    }

    @Override
    public void addCharacterData(String cData) throws SAXException { }

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        throw new SAXException("no child expected into <value />");
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        key = attributes.getValue("key");
        value = attributes.getValue("value");
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QNAME);
        output.addAttribute("key", key);
        output.addAttribute("value", value);
        output.endTag(QNAME);
    }

    @Override
    public void validate() throws InvalidXmlDefinition { }

    @Override
    public QName getQName() {
        return QNAME;
    }

    @Override
    public ValueModel clone() {
        ValueModel other = new ValueModel(QNAME);
        other.key=key;
        other.value=value;
        return other;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void prepareForUnload() {
        parent = null;
    }

}
