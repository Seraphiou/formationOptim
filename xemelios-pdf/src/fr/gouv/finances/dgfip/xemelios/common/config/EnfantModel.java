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

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

public class EnfantModel implements NoeudModifiable {

    private static Logger logger = Logger.getLogger(EnfantModel.class);
    private String configXPath = null;
    public final static transient String TAG = "enfants";
    public final static transient QName QN = new QName(TAG);
    private Hashtable<String, SimpleElement> enfants;
    private NoeudModifiable _NMParent = null;

    public Hashtable<String, SimpleElement> getEnfants() {
        return enfants;
    }

    public void setEnfants(Hashtable<String, SimpleElement> enfants) {
        this.enfants = enfants;
    }

    public EnfantModel(QName tagName) {
        super();
        enfants = new Hashtable<String, SimpleElement>();
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
    }

    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (DocumentsMapping.ENFANT.equals(tagName)) {
            SimpleElement se = (SimpleElement) child;
            if(enfants.containsKey(se.getElement())) {
                enfants.remove(se.getElement());
            }
            se.setParent(this);
            se.setParentAsNoeudModifiable(this);
            enfants.put(se.getElement(), se);
        }

    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        return this;
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        for (SimpleElement se : enfants.values()) {
            se.marshall(output);
        }
        output.endTag(TAG);
    }

    @Override
    public EnfantModel clone() {
        EnfantModel em = new EnfantModel(QN);

        for (String k : enfants.keySet()) {
            try {
                em.addChild(enfants.get(k).clone(), QN);
            } catch (Throwable t) {
                logger.error("clone().enfant",t);
            }
        }

        return em;
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        for (SimpleElement se : enfants.values()) {
            se.validate();
        }
    }

    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        return null;
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
    public void modifyAttr(String attrName, String value) {
    }

    @Override
    public void modifyAttrs(Attributes attrs) {
        try {
            getAttributes(new XmlAttributes(attrs));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour des attributs : " + e);
        }
    }

    @Override
    public String[] getChildIdAttrName(String childTagName) {
        return null;
    }

    @Override
    public void resetCharData() {

    }

    @Override
    public String getIdValue() {
        return null;
    }

    @Override
    public String getConfigXPath() {
        if (configXPath == null) {
            if (getParentAsNoeudModifiable() != null) {
                configXPath = getParentAsNoeudModifiable().getConfigXPath();
            } else {
                configXPath = "";
            }
            configXPath += "/" + TAG;
        }
        return configXPath;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return enfants.get(atts.getValue("element"));
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        for(SimpleElement se:enfants.values())
            se.prepareForUnload();
    }
}
