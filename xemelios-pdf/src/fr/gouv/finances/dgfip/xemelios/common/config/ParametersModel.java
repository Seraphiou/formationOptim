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

import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

public class ParametersModel implements NoeudModifiable {
    Logger logger = Logger.getLogger(ParametersModel.class);
    private NoeudModifiable _NMParent = null;
    
    public static final transient String TAG = "parameters";
    public static final transient QName QN = new QName(TAG);
    private Vector<ParameterModel> parameters;
    private HashMap<String,ParameterModel> hParameters;
    
    public ParametersModel(QName tagName) {
        super();
        parameters = new Vector<ParameterModel> ();
        hParameters = new HashMap<String,ParameterModel>();
    }
    
    public Vector<ParameterModel> getParameters() {
        return parameters;
    }
    public ParameterModel getParameter(String paramName) {
        return hParameters.get(paramName);
    }
    
    @Override
    public void addCharacterData(String cData) throws SAXException { }
    
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (ParameterModel.QN.equals(tagName)) {
            ParameterModel pm = (ParameterModel)child;
            pm.setParentAsNoeudModifiable(this);
            if(hParameters.containsKey(pm.getName())) {
                ParameterModel old = hParameters.get(pm.getName());
                parameters.remove(old);
                hParameters.remove(old.getName());
            }
            parameters.add(pm);
            hParameters.put(pm.getName(), pm);
        }
    }
    
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        return this;
    }
    
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        for (ParameterModel pm:parameters) {
            pm.marshall(output);
        }
        output.endTag(TAG);
    }
    
    @Override
    public void validate() throws InvalidXmlDefinition {
        for(ParameterModel p:parameters) p.validate();
    }
    
    @Override
    public ParametersModel clone()  {
        ParametersModel pm = new ParametersModel(QN);
        for (ParameterModel p:this.parameters) {
            try {
                pm.addChild(p.clone(), ParameterModel.QN);
            } catch (Throwable t) {
                logger.error("clone().parameter",t);
            }
        }
        return pm;
    }
    
    @Override
    public void modifyAttr(String attrName, String value) { }
    
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
        if (tagName.equals(ParameterModel.TAG)) {
            return hParameters.get(id);
        } else {
            return null;
        }
    }
    
    @Override
    public String[] getChildIdAttrName(String childTagName) {
        if (ParameterModel.TAG.equals(childTagName)) {
            return new String[]{"name"};
        } else {
            return null;
        }
    }
    @Override
    public String getIdValue() { return null; }
    
    @Override
    public void resetCharData() { }
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
        //return hParameters.get(atts.getValue("name"));
        return null;
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        for(ParameterModel pm:parameters) pm.prepareForUnload();
    }
}