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
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 * Modelise un critere
 * @author chm
 */
public class CritereModel implements NoeudModifiable {
    private static Logger logger = Logger.getLogger(CritereModel.class);
    private NoeudModifiable _NMParent = null;
    private String configXPath = null;
    
    public static final transient String TAG = "critere";
    public static final transient QName QN = new QName(TAG);
    private String libelle, id, idAffichage;
    private boolean displayOptional = false;
    /**
     * par défaut un critère est affichable; cela pour des raisons de compatibilité avec les anciennes configurations qui ne précisent pas cet attribut
     */
    private boolean affichable = true;
    private XPathModel path;
    private Vector<XmlMarshallable> vInputs;
    private HashMap<String,NoeudModifiable> hInputs;
    private Vector<PropertyModel> valeurs;	// sert a sauvegarder les requetes
    private HashMap<String,PropertyModel> properties;
    private BlankModel blank=null;
    private QName qn;
    private HelpModel help;
    
    /**
     * Not directly used by this object. Provided to data-layer implementors
     * as an helper datafield. Be careful, this field is <b>not</b> cloned.
     */
    private Object additionnalData = null;
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof CritereModel) {
            CritereModel other = (CritereModel)o;
            return other.getId().equals(getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    public CritereModel(QName tagName) {
        super();
        this.qn=tagName;
        vInputs = new Vector<XmlMarshallable>();
        hInputs = new HashMap<String,NoeudModifiable>();
        valeurs = new Vector<PropertyModel>();
        properties=new HashMap<String,PropertyModel>();
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(DocumentsMapping.PATH.equals(tagName)) {
            path = (XPathModel)child;
            path.setParentAsNoeudModifiable(this);
        } else if(PropertyModel.QN.equals(tagName)) {
            PropertyModel pm = (PropertyModel)child;
            if(properties.containsKey(pm.getName())) {
                PropertyModel old = properties.get(pm.getName());
                valeurs.remove(old);
                properties.remove(old.getName());
            }
            pm.setParentAsNoeudModifiable(this);
            valeurs.add(pm);
            properties.put(pm.getName(),pm);
        } else if(BlankModel.QN.equals(tagName)) {
            blank=(BlankModel)child;
            blank.setParentAsNoeudModifiable(this);
        } else if (InputModel.QN.equals(tagName)) {
            InputModel im = (InputModel)child;
            if(hInputs.containsKey(im.getId())) {
                InputModel old = (InputModel)hInputs.get(im.getId());
                vInputs.remove(old);
                hInputs.remove(old.getId());
            }
            im.setParentAsNoeudModifiable(this);
            vInputs.add(im);
            hInputs.put(im.getId(), (NoeudModifiable)im);
        } else if (SelectModel.QN.equals(tagName)) {
            SelectModel sm = (SelectModel)child;
            if(hInputs.containsKey(sm.getId())) {
                SelectModel old = (SelectModel)hInputs.get(sm.getId());
                vInputs.remove(old);
                hInputs.remove(old.getId());
            }
            sm.setParentAsNoeudModifiable(this);
            vInputs.add(sm);
            hInputs.put(sm.getId(), (NoeudModifiable)sm);
        } else if(HelpModel.QN.equals(tagName)) {
            help = (HelpModel)child;
        }
        
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes)throws SAXException {
        libelle = (attributes.getValue("libelle")!=null?attributes.getValue("libelle"):libelle);
        id = (attributes.getValue("id")!=null?attributes.getValue("id"):id);
        idAffichage = (attributes.getValue("optional-display-id")!=null?attributes.getValue("optional-display-id"):idAffichage);
        if(attributes.getValue("optional-display")!=null && attributes.getValue("optional-display").equals("true")) {
            displayOptional = true;
        }
        if(attributes.getValue("affichable")!=null) {
            affichable = attributes.getBooleanValue("affichable");
            logger.debug(getId()+".affichable set to "+Boolean.toString(affichable));
        }
//        String sTmp = attributes.getValue("mode");
//        if(sTmp!=null) {
//            if("both".equals(sTmp)) mode = MODE_BOTH;
//            else if("anomaly".equals(sTmp)) mode = MODE_ANOMALY;
//            else if("normal".equals(sTmp)) mode = MODE_NORMAL;
//        }
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        output.addAttribute("id",getId());
        output.addAttribute("libelle",libelle);
        if(!affichable) output.addAttribute("affichable", "false");
        if(getIdAffichage()!=null) output.addAttribute("optional-display-id", getIdAffichage());
        if(isDisplayOptional()) output.addAttribute("optional-display", "true");
        if(help!=null) help.marshall(output);
        path.marshall(output);
        if(hasBlank()) blank.marshall(output);
        for(XmlMarshallable xm:vInputs) xm.marshall(output);
        for(PropertyModel pm:valeurs) pm.marshall(output);
        output.endTag(TAG);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(id==null || id.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@id is required ("+getParentAsNoeudModifiable().getConfigXPath()+"/"+TAG+")");
        path.validate();
        for(XmlMarshallable xm:vInputs) xm.validate();
    }
    public Vector<XmlMarshallable> getInputs() {return vInputs;}
    public String getLibelle() {return libelle;}
    public XPathModel getPath() {return path;}
    @Override
    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(getLibelle());
//        sb.append("/");
//        for(PropertyModel pm:properties.values()) {
//            sb.append(pm.getName()).append("=").append(pm.getValue()).append(",");
//        }
//        return sb.toString();
        return getLibelle();
    }
    public Vector<PropertyModel> getValeurs() { return valeurs; }
    @Override
    public CritereModel clone() {
        CritereModel cm = new CritereModel(QN);
        cm.libelle = this.libelle;
        cm.id = this.id;
        cm.idAffichage = this.idAffichage;
        try {
            if(path!=null)
                cm.addChild(this.path.clone(), DocumentsMapping.PATH);
        } catch (Throwable t) {
            logger.error("clone().path",t);
        }
        for (String s:this.properties.keySet()) {
            try {
                cm.addChild(this.properties.get(s).clone(), PropertyModel.QN);
            } catch (Throwable t) {
                logger.error("clone().properties",t);
            }
        }
        cm.displayOptional=displayOptional;
        for(XmlMarshallable xm:vInputs) {
            try {
                cm.addChild((XmlMarshallable)xm.clone(), xm instanceof InputModel?InputModel.QN:SelectModel.QN);
            } catch (Throwable t) {
                logger.error("clone().vInputs",t);
            }
        }
        try {
            if(blank!=null) {
                cm.addChild(this.blank.clone(), BlankModel.QN);
            }
        } catch (Throwable t) {
            logger.error("clone().blank",t);
        }
//        cm.mode=this.mode;
        cm.affichable=this.affichable;
        // vieux hack tout pourri
        cm._NMParent=this._NMParent;
        if(help!=null) cm.help = help.clone();
        return cm;
    }
    public String getId() { return id; }
    
    public String getIdAffichage() { return idAffichage; }
    
    public Object getAdditionnalData() {
        return additionnalData;
    }
    public void setAdditionnalData(Object additionnalData) {
        this.additionnalData = additionnalData;
    }
    public String getProperty(String propName) {
        PropertyModel pm = properties.get(propName);
        if(pm!=null) return pm.getValue();
        return null;
    }
    public void removeAllProperties() {
        properties.clear();
    }
    public boolean hasBlank() { return blank!=null; }
    public boolean isDisplayOptional() {
        return this.displayOptional;
    }
    public void setDisplayOptional(boolean d) {
        this.displayOptional = d;
    }
//    public int getMode() { return mode; }
    
    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        if("path".equals(tagName)) {
            return path;
        } else if(PropertyModel.TAG.equals(tagName)) {
            return properties.get(id);
        } else if(BlankModel.TAG.equals(tagName)) {
            return blank;
        } else if (InputModel.TAG.equals(tagName)){
            return hInputs.get(id);
        } else {
            return null;
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
    public void modifyAttr(String attrName, String value) {
    }
    
    @Override
    public void modifyAttrs(Attributes attrs) {
        try {
            getAttributes(new XmlAttributes(attrs));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise ï¿½ jour des attributs : "+e);
        }
    }
    
    @Override
    public String[] getChildIdAttrName(String childTagName) {
        if(PropertyModel.TAG.equals(childTagName)) {
            return new String[] {"name"};
        } else if (InputModel.TAG.equals(childTagName)){
            return new String[] {"id"};
        } else {
            return null;
        }
    }
    
    @Override
    public void resetCharData() {
    }
    
    public boolean isAffichable() {
        return affichable;
    }
    
    public void setAffichable(boolean affichable) {
        this.affichable = affichable;
    }
    @Override
    public String getIdValue() { return getId(); }
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
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        QName qname = new QName(uri,localName);
//        if(DocumentsMapping.PATH.equals(qName)) {
//            return path;
/*        } else*/ if(PropertyModel.QN.equals(qname)) {
            return properties.get(id);
        } else if(BlankModel.QN.equals(qname)) {
            return blank;
        } else if (InputModel.QN.equals(qname)){
            return hInputs.get(id);
        } else {
            return null;
        }
    }

    @Override
    public QName getQName() { return qn; }
    public Collection<PropertyModel> getProperties() { return properties.values(); }

    public HelpModel getHelp() {
        return help;
    }

    public void setHelp(HelpModel help) {
        this.help = help;
    }

    public void setValeurs(Vector<PropertyModel> valeurs) {
        this.valeurs = valeurs;
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        for(NoeudModifiable nm:hInputs.values()) nm.prepareForUnload();
        for(PropertyModel pm:valeurs) pm.prepareForUnload();
        for(PropertyModel pm:properties.values()) pm.prepareForUnload();
        if(blank!=null) blank.prepareForUnload();
        if(path!=null) path.prepareForUnload();
    }
    
}
