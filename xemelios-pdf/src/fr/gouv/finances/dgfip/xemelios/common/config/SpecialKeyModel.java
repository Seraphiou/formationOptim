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

import java.util.Vector;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import java.util.Hashtable;
import javax.xml.namespace.QName;

/**
 * Permet de definir les filtres de suppression
 * @author chm
 */
public class SpecialKeyModel implements NoeudModifiable, Comparable {
    Logger logger = Logger.getLogger(SpecialKeyModel.class);
    private NoeudModifiable _NMParent = null;
    
    public static final transient String TAG = "special-key";
    public static final transient QName QN = new QName(TAG);
    private int pos = 0;
    private String id, libelle, path, transform;
    private Vector<OptionModel> descriptions;
    private Hashtable<String,OptionModel> hDescriptions;
    
    public SpecialKeyModel(QName tagName) {
        super();
        descriptions = new Vector<OptionModel>();
        hDescriptions = new Hashtable<String,OptionModel>();
    }
    
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        OptionModel om = (OptionModel)child;
        if(hDescriptions.containsKey(om.getValue())) {
            OptionModel old = hDescriptions.get(om.getValue());
            descriptions.remove(old);
            hDescriptions.remove(old.getValue());
        }
        om.setParentAsNoeudModifiable(this);
        descriptions.add(om);
        hDescriptions.put(om.getValue(),om);
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id = (attributes.getValue("id")!=null?attributes.getValue("id"):id);
        if (attributes.getValue("pos")!=null)
            pos = attributes.getIntValue("pos");
        libelle = (attributes.getValue("libelle")!=null?attributes.getValue("libelle"):libelle);
        path = (attributes.getValue("path")!=null?attributes.getValue("path"):path);
        transform = (attributes.getValue("transform")!=null?attributes.getValue("transform"):transform);
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {throw new Error("Not yet implemented"); }
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(getId()==null || getId().length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@id is required ("+getParentAsNoeudModifiable().getConfigXPath()+"/"+TAG+")");
    }
    public String getLibelle() {
        return libelle;
    }
    public String getPath() {
        return path;
    }
    public int getPos() {
        return pos;
    }
    public String getId() {
        if(id==null) id=Integer.toString(pos);
        return id;
    }
    @Override
    public SpecialKeyModel clone() {
        SpecialKeyModel dfm = new SpecialKeyModel(QN);
        dfm.id = this.id;
        dfm.pos = this.pos;
        dfm.libelle = this.libelle;
        dfm.path = this.path;
        dfm.transform = this.transform;
        //dfm.descriptions.addAll(this.descriptions);
        for (OptionModel om:this.descriptions) {
            try {
                dfm.addChild(om, OptionModel.QN);
            } catch (Throwable t) {
                logger.error("clone().option",t);
            }
        }
        
        return dfm;
    }
    public Vector<OptionModel> getDescriptions() {
        return descriptions;
    }
    public String getTransform() {
        return transform;
    }
    @Override
    public int compareTo(Object o) {
        if(o==this) return 0;
        if(o instanceof SpecialKeyModel) {
            SpecialKeyModel other=(SpecialKeyModel)o;
            if(pos<other.pos) return -1;
            else if(pos==other.pos) return 0;
            else return 1;
        }
        return 0;
    }
    public String transformValue(String value) {
        String ret = value;
        if("normalizeMonth".equals(transform)) {
            int i = Integer.parseInt(value);
            if(i<10) return "0"+Integer.toString(i);
            ret = Integer.toString(i);
        } else if("Identity".equals(transform)) ret = value;
        if("UpperCase".equals(transform)) ret = value.toUpperCase();
        if("LowerCase".equals(transform)) ret = value.toLowerCase();
        return ret;
    }
    public String getDescriptionOfValue(String value) {
        for(OptionModel om:descriptions) {
            if(om.getValue().equals(value)) return om.getLibelle();
        }
        return value;
    }
    
    public void setLibelle(String libelle) {
        this.libelle = libelle;
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
        return new String[]{"id"};
    }
    @Override
    public String getIdValue() { return getId(); }
    
    @Override
    public void resetCharData() {
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
        return hDescriptions.get(atts.getValue("value"));
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        for(OptionModel om:descriptions) om.prepareForUnload();
    }
}
