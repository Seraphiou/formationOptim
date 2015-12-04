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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;

/**
 * @author chm
 * @deprecated
*/
public class CopyToModel implements XmlMarshallable {
    private static final Logger logger = Logger.getLogger(CopyToModel.class);
    public static final transient String TAG = "copy-to";
    public static final transient QName QN = new QName(TAG);
    private String elementRef; 
    public CopyToModel(QName tagName) {
        super();
        logger.warn("<"+TAG+"> This element is deprecated. Do not use anymore ! </"+TAG+">");
        throw new UnsupportedOperationException("Cette classe est d�pr�ci�e, elle ne doit plus �tre utilis�e.");
    }
    public void addCharacterData(String cData) throws SAXException {}
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {}
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        elementRef = attributes.getValue("ref");
        return this;
    }
    public void marshall(XmlOutputter output) {throw new Error("Not yet implemented"); }
    public void validate() throws InvalidXmlDefinition {}
    public String getElementRef() {
        return elementRef;
    }
    @Override
    public CopyToModel clone() {
        CopyToModel ctm = new CopyToModel(QN);
        ctm.elementRef = this.elementRef;
        return ctm;
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public QName getQName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
