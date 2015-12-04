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

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

/**
 * Represents a special-key-entry tag
 * @author chm
 */
public class SpecialKeyEntryModel implements NoeudModifiable, Cloneable {

    private static Logger logger = Logger.getLogger(SpecialKeyEntryModel.class);
    private NoeudModifiable _NMParent = null;
    private String configXPath = null;
    public static final transient String TAG = "special-key-entry";
    public static final transient QName QN = new QName(TAG);
    private boolean distinct = false,  depends = false;
    private int pos = 0;
    private XPathModel path;
    private TextModel key1,  key2;
    private String id;
    private QName qn;

    public SpecialKeyEntryModel(QName tagName) {
        super();
        this.qn=tagName;
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
    }

    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (DocumentsMapping.PATH.equals(tagName)) {
            path = (XPathModel) child;
            path.setParentAsNoeudModifiable(this);
        } else if (DocumentsMapping.KEY1.equals(tagName)) {
            key1 = (TextModel) child;
            key1.setParentAsNoeudModifiable(this);
        } else if (DocumentsMapping.KEY2.equals(tagName)) {
            key2 = (TextModel) child;
            key2.setParentAsNoeudModifiable(this);
        }
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        if (attributes.getValue("id") != null) {
            id = attributes.getValue("id");
        }
        if (attributes.getValue("pos") != null) {
            pos = attributes.getIntValue("pos");
        }
        if (attributes.getValue("distinct") != null) {
            distinct = attributes.getBooleanValue("distinct");
        }
        if (attributes.getValue("depends") != null) {
            depends = attributes.getBooleanValue("depends");
        }
        return this;
    }

    @Override
    public void marshall(XmlOutputter output) {
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        if (id == null || id.length() == 0) {
            throw new InvalidXmlDefinition("//" + TAG + "/@id is required (" + getParentAsNoeudModifiable().getConfigXPath() + "/" + TAG + ")");
        }
        NoeudModifiable localParent = getParentAsNoeudModifiable();
        while (!(localParent instanceof DocumentModel) && localParent != null) {
            localParent = localParent.getParentAsNoeudModifiable();
        }
        if (localParent != null) {
            DocumentModel dm = (DocumentModel) localParent;
            boolean found = false;
            for (SpecialKeyModel skm : dm.getSpecialKeys()) {
                found |= skm.getId().equals(id);
            }
            if (!found) {
                throw new InvalidXmlDefinition(getConfigXPath() + " references an unknown special-key: " + id);
            }
        }
    }

    @Override
    public SpecialKeyEntryModel clone() {
        SpecialKeyEntryModel other = new SpecialKeyEntryModel(QN);
        other.depends = depends;
        other.distinct = distinct;
        try {
            if(path!=null)
                other.addChild(this.path.clone(), DocumentsMapping.PATH);
        } catch (Throwable t) {
            logger.error("clone().path",t);
        }
        other.pos = pos;
        try {
            if(key1!=null)
                other.addChild(this.key1.clone(), DocumentsMapping.KEY1);
        } catch (Throwable t) {
                logger.error("clone().key1",t);
        }
        try {
            if(key2!=null)
                other.addChild(this.key2.clone(), DocumentsMapping.KEY2);
        } catch (Throwable t) {
            logger.error("clone().key2",t);
        }
        return other;
    }

    public boolean isDepends() {
        return depends;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public XPathModel getPath() {
        return path;
    }

    public int getPos() {
        return pos;
    }

    public TextModel getKey1() {
        return key1;
    }

    public TextModel getKey2() {
        return key2;
    }

    @Override
    public void modifyAttr(String attrName, String value) {
    }

    @Override
    public void modifyAttrs(Attributes attrs) {
        try {
            getAttributes(new XmlAttributes(attrs));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise � jour des attributs : " + e);
        }
    }

    @Override
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        this._NMParent = p;
    }

    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return this._NMParent;
    }

    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        if ("path".equals(tagName)) {
            return path;
        } else if ("key1".equals(tagName)) {
            return key1;
        } else if ("key2".equals(tagName)) {
            return key2;
        } else {
            return null;
        }
    }

    @Override
    public String[] getChildIdAttrName(String childTagName) {
        return null;
    }

    @Override
    public void resetCharData() {
    }

    public String getId() {
        return id;
    }

    @Override
    public String getIdValue() {
        return getId();
    }

    @Override
    public String getConfigXPath() {
        if (configXPath == null) {
            if (getParentAsNoeudModifiable() != null) {
                configXPath = getParentAsNoeudModifiable().getConfigXPath();
            } else {
                configXPath = "";
            }
            configXPath += "/" + TAG + "[@id='" + getId() + "']";
        }
        return configXPath;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        QName name = new QName(uri,localName);
//        if (DocumentsMapping.PATH.equals(name)) {
//            return path;
/*        } else*/ if (DocumentsMapping.KEY1.equals(name)) {
            return key1;
        } else if (DocumentsMapping.KEY2.equals(name)) {
            return key2;
        }
        return null;
    }

    @Override
    public QName getQName() { return qn; }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        if(path!=null) path.prepareForUnload();
        if(key1!=null) key1.prepareForUnload();
        if(key2!=null) key2.prepareForUnload();
    }
}
