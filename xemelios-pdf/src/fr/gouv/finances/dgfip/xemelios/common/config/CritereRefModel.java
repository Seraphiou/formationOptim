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


import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;

/**
 * Modelise un critereRef
 * @author chm
 */
public class CritereRefModel implements NoeudModifiable {
    private static Logger logger = Logger.getLogger(CritereRefModel.class);
    private NoeudModifiable _NMParent = null;
    
    public static final transient String TAG = "critere-optionnel";
    public static final transient QName QN = new QName(TAG);
    private String id, idRef;
    private boolean optionnel = false;
    private String configXPath = null;
    private QName qn;
    
    /**
     * Not directly used by this object. Provided to data-layer implementors
     * as an helper datafield. Be careful, this field is <b>not</b> cloned.
     */
    private Object additionnalData = null;
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof CritereRefModel) {
            CritereRefModel other = (CritereRefModel)o;
            return other.getId().equals(getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    
    public CritereRefModel(String tagName) {
        super();
    }
    public CritereRefModel(QName tagName) {
    	super();
    	this.qn = tagName;
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes)throws SAXException {
        id = (attributes.getValue("id")!=null?attributes.getValue("id"):id);
        idRef = (attributes.getValue("id-ref")!=null?attributes.getValue("id-ref"):idRef);
        if (attributes.getValue("optional")!=null) {
        	optionnel = attributes.getBooleanValue("optional");
        }
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        output.addAttribute("id",getId());
        output.addAttribute("id-ref",getIdRef());
        output.addAttribute("optional", optionnel);
        output.endTag(TAG);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {
    }
    @Override
    public String toString() {
        return getId();
    }
    @Override
    public CritereRefModel clone() {
        CritereRefModel cm = new CritereRefModel(TAG);
        cm.id = this.id;
        cm.idRef = this.idRef;
        cm.optionnel=this.optionnel;
        // vieux hack tout pourri
        cm._NMParent=this._NMParent;
        return cm;
    }
    public String getId() { return id; }
    
    public Object getAdditionnalData() {
        return additionnalData;
    }
    public void setAdditionnalData(Object additionnalData) {
        this.additionnalData = additionnalData;
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
    public String getIdValue() { return getId(); }

	public boolean isOptionnel() {
		return optionnel;
	}

	public void setOptionnel(boolean optionnel) {
		this.optionnel = optionnel;
	}

	public String getIdRef() {
		return idRef;
	}

	public void setIdRef(String idRef) {
		this.idRef = idRef;
	}

    @Override
    public String getConfigXPath() {
        if(configXPath==null) {
            if(getParentAsNoeudModifiable()!=null) configXPath = getParentAsNoeudModifiable().getConfigXPath();
            else configXPath = "";
            configXPath+="/"+TAG+"[@id='"+getId()+"']";
        }
        return configXPath;
    }

    @Override
	public XmlMarshallable getChildToModify(String uri, String localName, String name, Attributes atts) {
		return null;
	}

    @Override
	public QName getQName() {
		return this.qn;
	}

    @Override
    public void prepareForUnload() {
        _NMParent = null;
    }
	
}
