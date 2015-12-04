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

import java.util.Enumeration;
import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;

/**
 * Represente un chemin. Des restriction sont a appliquer par rapport a XPath :
 * Seul un chemin qualifie est autorise, et on le parcourt en remontant
 * @author chm
 * @deprecated
 */
public class PathModel implements XmlMarshallable {
    private static final Logger logger = Logger.getLogger(PathModel.class);
    public static final transient String TAG ="end-path";
    public static final transient QName QN = new QName(TAG);
    @Override
    public PathModel clone() {
        PathModel pm = new PathModel(QN);
        pm.name=this.name;
        for(Enumeration<String> enumer=transforms.keys();enumer.hasMoreElements();) {
            String key = enumer.nextElement();
            TransformModel tm = transforms.get(key).clone();
            //pm.transforms.put(key,tm);
            try {
                pm.addChild(tm,TransformModel.TAG);
            } catch(Throwable t) {
                logger.error("clone().transform",t);
            }
        }
        return pm;
    }
    private String name;
    private Hashtable<String,TransformModel> transforms;

    /**
     * 
     */
    public PathModel(QName tagName) {
        super();
        transforms = new Hashtable<String,TransformModel>();
        throw new UnsupportedOperationException("Cette classe est déprécée, elle ne doit plus être utilisée.");
    }

    /* (non-Javadoc)
     * @see fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable#addCharacterData(java.lang.String)
     */
    public void addCharacterData(String cData) throws SAXException {}

    /* (non-Javadoc)
     * @see fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable#addChild(fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable, java.lang.String)
     */
    public void addChild(XmlMarshallable child, String tagName) throws SAXException {
        TransformModel tm = (TransformModel)child;
        transforms.put(tm.getAttr(),tm);
    }

    /* (non-Javadoc)
     * @see fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable#getAttributes(fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes)
     */
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        name=attributes.getValue("name");
        return this;
    }

    /* (non-Javadoc)
     * @see fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable#marshall(fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter)
     */
    public void marshall(XmlOutputter output) {throw new Error("Not yet implemented"); }

    /* (non-Javadoc)
     * @see fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable#validate()
     */
    public void validate() throws InvalidXmlDefinition {}

    public String getName() {
        return name;
    }
    public TransformModel getTransform(String attrName) {
        return transforms.get(attrName);
    }

    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    public QName getQName() {
        return QN;
    }
}
