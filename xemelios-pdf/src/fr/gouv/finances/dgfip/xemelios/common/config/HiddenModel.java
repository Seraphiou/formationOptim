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
 *
 * @author chm
 */
public class HiddenModel implements NoeudModifiable, Cloneable {
    private static Logger logger = Logger.getLogger(HiddenModel.class);
    private NoeudModifiable _NMParent = null;
    
    public static final transient String TAG = "hidden";
    public static final transient QName QN = new QName(TAG);
    private String name;
    private XPathModel valeur;
    
    public HiddenModel(QName tagName)  {
        super();
    }
    @Override
    public HiddenModel clone() {
        HiddenModel hm = new HiddenModel(QN);
        hm.name = name;
        try {
            hm.addChild(this.valeur.clone(), DocumentsMapping.VALEUR);
        } catch (Throwable t) {
            logger.error("clone().valeur",t);
        }
//        hm.valeur = valeur.clone();
        return hm;
    }
    
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        name=(attributes.getValue("name")!=null?attributes.getValue("name"):name);
        return this;
    }
    
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(DocumentsMapping.VALEUR.equals(tagName)) {
            valeur = (XPathModel)child;
            valeur.setParentAsNoeudModifiable(this);
        } else throw new SAXException("unexpected child in <hidden> : "+tagName);
    }
    
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        output.addAttribute("name", name);
        valeur.marshall(output);
        output.endTag(QN);
    }
    
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(name==null || name.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@name is required ("+getParentAsNoeudModifiable().getConfigXPath()+")");
        valeur.validate();
    }
    public XPathModel getPath() { return valeur; }
    public String getName() { return name; }
    
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
        return valeur;
    }
    
    @Override
    public void modifyAttr(String attrName, String value) {
    }
    
    @Override
    public void modifyAttrs(Attributes attrs) {
        try {
            getAttributes(new XmlAttributes(attrs));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise � jour des attributs : "+e);
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
    public String getIdValue() { return getName(); }
    private String configXPath = null;
    @Override
    public String getConfigXPath() {
        if(configXPath==null) {
            if(getParentAsNoeudModifiable()!=null) configXPath = getParentAsNoeudModifiable().getConfigXPath();
            else configXPath="";
            configXPath+="/"+TAG;
        }
        return configXPath;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        valeur = null;
    }
}