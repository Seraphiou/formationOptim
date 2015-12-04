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

public class ParameterModel implements NoeudModifiable {
    Logger logger = Logger.getLogger(ParameterModel.class);
    private NoeudModifiable _NMParent = null;
    
    public static final transient String TAG = "parameter";
    public static final transient QName QN = new QName(TAG);
    private String name, value;
    
    public ParameterModel(QName tagName) {
        super();
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public void addCharacterData(String cData) throws SAXException {
        if(value==null) value=cData;
        else value+=cData;
    }
    
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException { }
    
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes)
    throws SAXException {
        name = (attributes.getValue("name")!=null?attributes.getValue("name"):name);
        value = (attributes.getValue("value")!=null?attributes.getValue("value"):value);
        return null;
    }
    
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        output.addAttribute("name", name);
        if(value.length()<10) output.addAttribute("value", value);
        else output.addCharacterData("<![CDATA["+value+"]]>");
        output.endTag(TAG);
    }
    
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(name==null || name.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@name can not be null or empty ("+getParentAsNoeudModifiable().getConfigXPath()+").");
        if(value==null || value.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@value can not be null or empty ("+getConfigXPath()+").");
    }
    
    @Override
    public ParameterModel clone() {
        ParameterModel pm = new ParameterModel(QN);
        pm.name = this.name;
        pm.value = this.value;
        return pm;
    }
    
    @Override
    public boolean equals(Object other) {
        if(other instanceof ParameterModel) {
            return(((ParameterModel)other).name.equals(this.name));
        } else
            return this.equals(other);
    }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
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
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        this._NMParent = p;
    }
    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return this._NMParent;
    }
    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        return null;
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
            configXPath+="/"+TAG+"[@name='"+getName()+"']";
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
    }
}
