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
 * Represente un chemin DomPath. Attention, plusieurs tags utilisent ce model
 * @author chm
 */
public class SimpleElement implements NoeudModifiable {
    private static Logger logger = Logger.getLogger(SimpleElement.class);
    private NoeudModifiable _NMParent = null;
    
    private String tag;
    private String element, path, pseudoCompletePath;
    
    private QName qn;
    
    private NoeudModifiable parent = null;
    
    public void setParent(NoeudModifiable parent) {
        this.parent = parent;
    }
    public SimpleElement(QName tagName) {
        super();
        this.qn=tagName;
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {
    }
    @Override
    public void addChild(XmlMarshallable child, QName tagName)throws SAXException {}
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes)throws SAXException {
        element = (attributes.getValue("element")!=null?attributes.getValue("element"):element);
        path = (attributes.getValue("path")!=null?attributes.getValue("path"):path);
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(tag);
        output.addAttribute("element", element);
        output.addAttribute("path", path);
        output.endTag(tag);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(element==null || element.length()==0) throw new InvalidXmlDefinition("//parent/@element is required");
        if(path==null || path.length()==0) throw new InvalidXmlDefinition("//parent/@path is required");
        NoeudModifiable localParent = getParentAsNoeudModifiable();
        while(!(localParent instanceof ElementModel)) {
            localParent = localParent.getParentAsNoeudModifiable();
        }
        ElementModel em = (ElementModel)localParent;
        if(em.getParent().getElementById(element)==null) {
            throw new InvalidXmlDefinition("/document[@id='"+em.getParent().getParent().getId()+"']/etat[@id='"+em.getParent().getId()+"']/element[@id='"+em.getId()+"']/{parent|enfant/.../}/@element references an unavailable element: "+element);
        }
    }
    public String getPath() {return path.toString();}
    public String getTag() {return tag;}
    @Override
    public SimpleElement clone() {
        SimpleElement xp = new SimpleElement(getQName());
        xp.path = this.path;
        xp.element = this.element;
        return xp;
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
    public String getIdValue() { return null; }
    public String getElement() {
        return element;
    }
    public void setElement(String element) {
        this.element = element;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getPseudoCompletePath() {
        return pseudoCompletePath;
    }
    public void setPseudoCompletePath(String pseudoCompletePath) {
        this.pseudoCompletePath = pseudoCompletePath;
    }

    private String configXPath = null;
    @Override
    public String getConfigXPath() {
        if(configXPath==null) {
            if(getParentAsNoeudModifiable()!=null) configXPath = getParentAsNoeudModifiable().getConfigXPath();
            else configXPath="";
            configXPath+="/"+tag+"[@element='"+getElement()+"']";
        }
        return configXPath;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public QName getQName() {
        return qn;
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
    }
    
}
