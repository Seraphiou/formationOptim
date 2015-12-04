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

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author chm
 */
public class WidgetModel implements NoeudModifiable {
    private static final Logger logger = Logger.getLogger(WidgetModel.class);
    public static final transient String TAG = "widget";
    public static final transient QName QN = new QName(TAG);
    
    private String id, className, libelle;
    private ParametersModel params;
    private NoeudModifiable _NMParent;

    private HelpModel help;
    
    /** Creates a new instance of WidgetModel */
    public WidgetModel(QName tagName) {
        super();
        try {
            addChild(new ParametersModel(ParametersModel.QN),ParametersModel.QN);
        } catch(Throwable t) {
            logger.error("<init>.parameters",t);
        }
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
    }

    @Override
    public String[] getChildIdAttrName(String childTagName) {
        if(ParameterModel.TAG.equals(childTagName)) return new String[]{"name"};
        else return null;
    }

    @Override
    public void modifyAttrs(Attributes attrs) {
        setClassName(attrs.getValue("class"));
        setLibelle(attrs.getValue("libelle"));
    }

    @Override
    public boolean equals(Object obj) {
        boolean retValue = false;
        if(obj instanceof WidgetModel) {
            WidgetModel other = (WidgetModel)obj;
            retValue = other.getId().equals(id);
        }
        return retValue;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        output.addAttribute("id",id);
        output.addAttribute("class", className);
        output.addAttribute("libelle", libelle);
        if(params!=null && params.getParameters().size()>0) {
            params.marshall(output);
        }
        output.endTag(QN);
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        setId(attributes.getValue("id"));
        setClassName(attributes.getValue("class"));
        setLibelle(attributes.getValue("libelle"));
        return this;
    }

    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(ParametersModel.QN.equals(tagName)) {
            params = (ParametersModel)child;
            params.setParentAsNoeudModifiable(this);
        } else if(ParameterModel.QN.equals(tagName)) {
            params.addChild(child,tagName);
        } else if(HelpModel.QN.equals(tagName)) {
            help = (HelpModel)child;
        }
    }

    @Override
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        _NMParent = p;
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        if(className==null || className.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@class is required and can not be null");
        if(libelle==null || libelle.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@libelle is required and can not be null");
        if(params!=null) params.validate();
    }

    @Override
    public String toString() {
        return getLibelle();
    }

    @Override
    public void resetCharData() {
    }

    @Override
    public WidgetModel clone() {
        WidgetModel retValue = new WidgetModel(QN);
        retValue.setId(getId());
        retValue.setClassName(getClassName());
        retValue.setLibelle(getLibelle());
        try {
            retValue.addChild(params.clone(),ParametersModel.QN);
        } catch(Throwable t) {
            logger.error("clone().parameters",t);
        }
        if(help!=null)
            retValue.help = help.clone();
        return retValue;
    }

    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        if(ParametersModel.TAG.equals(tagName)) return params;
        else if(ParameterModel.TAG.equals(tagName)) return params.getChildAsNoeudModifiable(tagName,id);
        else return null;
    }

    @Override
    public String getIdValue() {
        return getId();
    }

    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return _NMParent;
    }

    @Override
    public void modifyAttr(String attrName, String value) {
        if("class".equals(attrName)) setClassName(value);
        else if("libelle".equals(attrName)) setLibelle(value);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    public ParameterModel getParam(String paramName) {
        return params.getParameter(paramName);
    }
    private String configXPath = null;
    @Override
    public String getConfigXPath() {
        if(configXPath==null) {
            if(getParentAsNoeudModifiable()!=null) configXPath = getParentAsNoeudModifiable().getConfigXPath();
            else configXPath="";
            configXPath+="/"+TAG+"[@id='"+getId()+"']";
        }
        return configXPath;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        QName name = new QName(uri,localName);
        if(ParametersModel.QN.equals(name)) {
            return params;
        } else if(ParameterModel.QN.equals(name)) {
            return params.getParameter(atts.getValue("name"));
        }
        return null;
    }

    @Override
    public QName getQName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public HelpModel getHelp() {
        return help;
    }

    public void setHelp(HelpModel help) {
        this.help = help;
    }

    public List<ParameterModel> getParameters() {
        return params.getParameters();
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        if(params!=null) params.prepareForUnload();
    }
    
}
