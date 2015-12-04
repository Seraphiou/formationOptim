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

import java.util.Properties;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

/**
 * @author chm
 * @deprecated
 */
public class IndexModel implements XmlMarshallable {
    public static final transient String TAG = "index";
    public static final transient QName QN = new QName(TAG);
    private String name;
    private Properties props;
    
    public IndexModel(String tagName) {
        super();
        props = new Properties();
        throw new UnsupportedOperationException("Cette classe est d�pr�ci�e, elle ne doit plus �tre utilis�e");
    }

    public void addCharacterData(String cData) throws SAXException {}
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(PropertyModel.QN.equals(tagName)) {
            PropertyModel p = (PropertyModel)child;
            props.setProperty(p.getName(),p.getValue());
        }
    }
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        name = attributes.getValue("name");
        return this;
    }
    public void marshall(XmlOutputter output) {throw new Error("Not yet implemented"); }
    public void validate() throws InvalidXmlDefinition {}
    public String getName() {
        return name;
    }
    public Properties getProps() {
        return props;
    }
    @Override
    public IndexModel clone() {
        IndexModel im = new IndexModel(TAG);
        im.name = this.name;
        im.props.putAll(props);
        return im;
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    public QName getQName() {
        return QN;
    }
}
