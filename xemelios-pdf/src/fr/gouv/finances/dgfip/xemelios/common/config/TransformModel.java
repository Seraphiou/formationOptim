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

/**
 * Represente un tag <tt>transform</tt>
 * @author chm
 * @deprecated
 */
public class TransformModel implements XmlMarshallable {
    public static final transient String TAG = "transform";
    public static final transient QName QN = new QName(TAG);
    private String attr, dest, apply;

    /**
     * 
     */
    public TransformModel(QName tagName) {
        super();
        throw new UnsupportedOperationException("Cette classe est d�pr�ci�e, elle ne doit plus �tre utilis�e.");
    }

    /* (non-Javadoc)
     * @see fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable#addCharacterData(java.lang.String)
     */
    public void addCharacterData(String cData) throws SAXException {}

    /* (non-Javadoc)
     * @see fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable#addChild(fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable, java.lang.String)
     */
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {}

    /* (non-Javadoc)
     * @see fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable#getAttributes(fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes)
     */
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        attr = attributes.getValue("attr");
        dest = attributes.getValue("dest");
        apply = attributes.getValue("apply");
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

    public String getApply() {
        return apply;
    }
    public String getAttr() {
        return attr;
    }
    public String getDest() {
        return dest;
    }
    public String getTransformValue(String val) {
        if(val==null) return null;
        if(apply==null) return val;
        if(apply.equals("Identity")) return val;
        if(apply.equals("UpperCase")) return val.toUpperCase();
        if(apply.equals("LowerCase")) return val.toLowerCase();
        if(apply.equals("normalizeMonth")) {
            int i = Integer.parseInt(val);
            if(i<10) return "0"+Integer.toString(i);
            return Integer.toString(i);
        }
        return "unknown transformation: "+apply;
    }
    @Override
    public TransformModel clone() {
        TransformModel tm = new TransformModel(QN);
        tm.attr=this.attr;
        tm.dest=this.dest;
        tm.apply=this.apply;
        return tm;
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    public QName getQName() {
        return QN;
    }
}
