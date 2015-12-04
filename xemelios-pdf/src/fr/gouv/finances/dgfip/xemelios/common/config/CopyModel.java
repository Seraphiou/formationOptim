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

import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import org.apache.log4j.Logger;

/**
 * @author chm
 * @deprecated
 */
public class CopyModel implements XmlMarshallable {
    private static final Logger logger = Logger.getLogger(CopyModel.class);
    public static final transient String TAG = "copy";
    public static final transient QName QN = new QName(TAG);
    private String tag;
    private Hashtable<String,PathModel> paths;
    private Hashtable<String,TransformModel> transforms;

    /**
     * 
     */
    public CopyModel(QName tagName) {
        super();
        logger.warn("<"+TAG+"> This element is deprecated. Do not use anymore ! </"+TAG+">");
        paths = new Hashtable<String,PathModel>();
        transforms = new Hashtable<String,TransformModel>();
        throw new UnsupportedOperationException("Cette classe est d�pr�ci�e, elle ne doit plus �tre utilis�e.");
    }

    public void addCharacterData(String cData) throws SAXException {}

    public void addChild(XmlMarshallable child, String tagName) throws SAXException {
        if(PathModel.TAG.equals(tagName)) {
            PathModel pm = (PathModel)child;
            paths.put(pm.getName(),pm);
        } else if(TransformModel.TAG.equals(tagName)) {
            TransformModel tm = (TransformModel)child;
            transforms.put(tm.getAttr(),tm);
        }
    }

    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        tag = attributes.getValue("tag-name");
        return this;
    }

    public void marshall(XmlOutputter output) {throw new Error("Not yet implemented"); }

    public void validate() throws InvalidXmlDefinition {}

    @Override
    public boolean equals(Object o) {
        if(o==this) return true;
        if(o instanceof String) return tag.equals(o);
        if(o instanceof CopyModel) {
            CopyModel other = (CopyModel)o;
            return other.getTag().equals(tag);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return tag.hashCode();
    }

    @Override
    public CopyModel clone() {
        CopyModel cm = new CopyModel(QN);
        cm.tag = this.tag;
        for(Enumeration<String> enumer=paths.keys();enumer.hasMoreElements();) {
            String key = enumer.nextElement();
            PathModel pm = paths.get(key);
            cm.paths.put(key,pm);
        }
        for(Enumeration<String> enumer=transforms.keys();enumer.hasMoreElements();) {
            String key = enumer.nextElement();
            TransformModel tm = transforms.get(key);
            cm.transforms.put(key,tm);
        }
        return cm;
    }
    public String getTag() { return tag; }
    public PathModel getPath(String path) {
        return paths.get(path);
    }
    public TransformModel getTransform(String attName) {
        return transforms.get(attName);
    }

    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public QName getQName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
