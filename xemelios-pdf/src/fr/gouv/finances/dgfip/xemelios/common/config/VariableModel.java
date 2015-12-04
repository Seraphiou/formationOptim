/*
 * Copyright 
 *   2007 axYus - www.axyus.com
 *   2007 C.Marchand - christophe.marchand@axyus.com
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

public class VariableModel implements NoeudModifiable {
    private NoeudModifiable _NMParent = null;
    private static final Logger logger = Logger.getLogger(VariableModel.class);
    public static final String TAG = "variable";
    public static final transient QName QN = new QName(TAG);
    private String name, type;
    private TextModel libelle;
    private ReferenceModel reference;
    private int domain;
    
    public VariableModel(QName tagName) {
        super();
    }
    
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(DocumentsMapping.LIBELLE.equals(tagName)) {
            libelle = (TextModel)child;
            libelle.setParentAsNoeudModifiable(this);
        } else if(ReferenceModel.QN.equals(tagName)) {
            reference = (ReferenceModel)child;
            reference.setParentAsNoeudModifiable(this);
        }
    }
    
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        name = (attributes.getValue("name")!=null?attributes.getValue("name"):name);
        type = (attributes.getValue("type")!=null?attributes.getValue("type"):type);
        String sTmp = attributes.getValue("domain");
        if("documents".equals(sTmp)) domain = EnvironmentDomain.DOMAIN_DOCUMENTS;
        else if("element".equals(sTmp)) domain = EnvironmentDomain.DOMAIN_ELEMENT;
        else throw new SAXException(sTmp+" is not a valid value for attribute variable/@domain" + "name = " + name + " type=" + type);
        return this;
    }
    
    @Override
    public void marshall(XmlOutputter output) {}
    
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(libelle==null) throw new InvalidXmlDefinition("//"+TAG+"/libelle is required");
        if(libelle.getData()==null || libelle.getData().length()==0) throw new InvalidXmlDefinition("//"+TAG+"/libelle/text() can not be empty");
        if(reference==null) throw new InvalidXmlDefinition("//"+TAG+"/reference is required");
        reference.validate();
        if(name==null || name.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/name can not be null or empty");
        if(type==null || type.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@type can not be null or empty");
        // domain is checked in getAttributes
    }
    @Override
    public VariableModel clone() {
        VariableModel other = new VariableModel(QN);
        try {
            other.addChild(this.libelle.clone(), DocumentsMapping.LIBELLE);
        } catch (Throwable t) {
            logger.error("clone().libelle",t);
        }
        other.type=this.type;
        other.name=this.name;
        other.domain=this.domain;
        try {
            other.addChild(this.reference.clone(), ReferenceModel.QN);
        } catch (Throwable t) {
            logger.error("clone().reference",t);
        }
        return other;
    }
    public String getName() { return name; }
    
    
    public TextModel getLibelle() {
        return libelle;
    }
    
    public ReferenceModel getReference() {
        return reference;
    }
    
    
    public String getType() {
        return type;
    }
    
    @Override
    public String toString() { return getLibelle().getData(); }
    public int getDomain() { return domain; }
    
    
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
        if("libelle".equals(tagName)) {
            return libelle;
        } else if(ReferenceModel.TAG.equals(tagName)) {
            return reference;
        } else {
            return null;
        }
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
    public String getIdValue() { return null; }
    
    @Override
    public void resetCharData() {
    }
    
    private String configXPath = null;
    @Override
    public String getConfigXPath() {
        if(configXPath==null) {
            if(getParentAsNoeudModifiable()!=null) configXPath = getParentAsNoeudModifiable().getConfigXPath();
            else configXPath="";
            configXPath+="/"+TAG+"[@name='"+getName()+"']";
        }
        return configXPath;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        QName qn = new QName(uri,localName);
        if(DocumentsMapping.LIBELLE.equals(qn)) {
            return libelle;
        } else if(ReferenceModel.QN.equals(qn)) {
            return reference;
        }
        return null;
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        if(libelle!=null) libelle.prepareForUnload();
        if(reference!=null) reference.prepareForUnload();
    }
    
}
