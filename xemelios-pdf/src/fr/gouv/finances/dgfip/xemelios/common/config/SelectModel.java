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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

/**
 * Modelise une liste de selection
 * @author chm
 */
public class SelectModel implements NoeudModifiable {
    private static Logger logger = Logger.getLogger(SelectModel.class);
    private NoeudModifiable _NMParent = null;
    private String configXPath = null;
    
    public static final transient String TAG = "select";
    public static final transient QName QN = new QName(TAG);
    public static final int SORT_NO=0;
    public static final int SORT_KEY=1;
    public static final int SORT_LIB=2;
    
    private Vector<OptionModel> options;
    private HashMap<String,OptionModel> hOptions;
    private String id, libelle, value;
    private Vector<RecherchePaireModel> recherches;	// des recherches dans la nomenclature
    private HashMap<String,RecherchePaireModel> hRecherches;
    private Vector<SpecialKeyEntryModel> specialKeys;	// des recherches dans les cles-speciales
    private HashMap<String,SpecialKeyEntryModel> hSpecialKeys;
    private String sort = "no";
    private QName qn;
    private Vector<ResourceRefModel> resources;
    private HelpModel help;
    
    public SelectModel(QName tagName) {
        super();
        options = new Vector<OptionModel>();
        hOptions = new HashMap<String,OptionModel>();
        recherches = new Vector<RecherchePaireModel>();
        hRecherches = new HashMap<String,RecherchePaireModel>();
        specialKeys = new Vector<SpecialKeyEntryModel>();
        hSpecialKeys = new HashMap<String,SpecialKeyEntryModel>();
        resources = new Vector<ResourceRefModel>();
        this.qn=tagName;
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(OptionModel.QN.equals(tagName)) {
            OptionModel om = (OptionModel)child;
            if(hOptions.containsKey(om.getValue())) {
                OptionModel old = hOptions.get(om.getValue());
                options.remove(old);
                hOptions.remove(old.getValue());
            }
            om.setParentAsNoeudModifiable(this);
            options.add(om);
            hOptions.put(om.getValue(), om);
        } else if(DocumentsMapping.RECHERCHE_PAIRE.equals(tagName)) {
            RecherchePaireModel rpm = (RecherchePaireModel)child;
            if(hRecherches.containsKey(rpm.getId())) {
                RecherchePaireModel old = hRecherches.get(rpm.getId());
                recherches.remove(old);
                hRecherches.remove(old.getId());
            }
            rpm.setParentAsNoeudModifiable(this);
            recherches.add(rpm);
            hRecherches.put(rpm.getId(), rpm);
        } else if(SpecialKeyEntryModel.QN.equals(tagName)) {
            SpecialKeyEntryModel skem = (SpecialKeyEntryModel)child;
            String key = String.valueOf(skem.getPos());
            if(hSpecialKeys.containsKey(key)) {
                SpecialKeyEntryModel old = hSpecialKeys.get(key);
                specialKeys.remove(old);
                hSpecialKeys.remove(key);
            }
            skem.setParentAsNoeudModifiable(this);
            specialKeys.add(skem);
            hSpecialKeys.put(key, skem);
        } else if(ResourceRefModel.QNAME.equals(tagName)) {
            resources.add((ResourceRefModel)child);
        } else if(HelpModel.QN.equals(tagName)) {
            help = (HelpModel)child;
        }
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id=(attributes.getValue("id")!=null?attributes.getValue("id"):id);
        libelle=(attributes.getValue("libelle")!=null?attributes.getValue("libelle"):libelle);
        value=(attributes.getValue("value")!=null?attributes.getValue("value"):value);
        sort=(attributes.getValue("sort")!=null?attributes.getValue("sort"):sort);
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        output.addAttribute("id",id);
        output.addAttribute("sort",sort);
        if(libelle!=null) output.addAttribute("libelle",libelle);
        for(OptionModel om:options) om.marshall(output);
        for(RecherchePaireModel rpm:recherches) rpm.marshall(output);
        for(ResourceRefModel rrm:resources) rrm.marshall(output);
        output.endTag(TAG);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(id==null || id.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@id is required");
        for(OptionModel om:options) om.validate();
        for(RecherchePaireModel rpm:recherches) rpm.validate();
        for(SpecialKeyEntryModel spe:specialKeys) spe.validate();
    }
    public String getId() {return id;}
    public String getLibelle() {return libelle;}
    public Vector<OptionModel> getOptions() {return options;}
    public Vector<RecherchePaireModel> getRecherches() {
        return recherches;
    }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    @Override
    public SelectModel clone() {
        SelectModel sm = new SelectModel(QN);
        sm.id=this.id;
        sm.libelle=this.libelle;
        sm.value=value;
        sm.sort=sort;
        for(OptionModel om:this.options) {
            try {
                sm.addChild(om.clone(), OptionModel.QN);
            } catch (Throwable t) {
                logger.error("clone().option",t);
            }
        }
        for(RecherchePaireModel rpm:this.recherches) {
            try {
                sm.addChild(rpm.clone(), DocumentsMapping.RECHERCHE_PAIRE);
            } catch (Throwable t) {
                logger.error("clone().recherche-paire",t);
            }
        }
        for(SpecialKeyEntryModel skem:this.specialKeys) {
            try {sm.addChild(skem.clone(), SpecialKeyEntryModel.QN);
            } catch (Throwable t) {
                logger.error("clone().special-key-entry",t);
            }
        }
        for(ResourceRefModel rrm:this.resources) {
            try {
                sm.addChild(rrm.clone(), ResourceRefModel.QNAME);
            } catch(Throwable t) {}
        }
        return sm;
    }
    public int getSortBy() {
        if("no".equals(sort)) return SORT_NO;
        else if("key".equals(sort)) return SORT_KEY;
        else return SORT_LIB;
    }
    public Vector<SpecialKeyEntryModel> getSpecialKeys() {
        return specialKeys;
    }
    @SuppressWarnings("unchecked")
    public Collection<Pair> getNewRecipient() {
        int sortBy = getSortBy();
        if(sortBy==SORT_NO) return new Vector<Pair>();
        Comparator<Pair> cp;
        if(sortBy==SORT_KEY) {
            cp = new Comparator() {
                public int compare(Object o1, Object o2) {
                    Pair p1=(Pair)o1;
                    Pair p2=(Pair)o2;
                    return p1.key.compareTo(p2.key);
                }
            };
        } else {
            cp = new Comparator() {
                public int compare(Object o1, Object o2) {
                    Pair p1=(Pair)o1;
                    Pair p2=(Pair)o2;
                    return p1.libelle.compareTo(p2.libelle);
                }
            };
        }
        return new TreeSet<Pair>(cp);
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
        if(OptionModel.TAG.equals(tagName)) {
            return hOptions.get(id);
        } else if("recherche-paire".equals(tagName)) {
            return hRecherches.get(id);
        } else if(SpecialKeyEntryModel.TAG.equals(tagName)) {
            return hSpecialKeys.get(id);
        } else {
            return null;
        }
    }
    @Override
    public String[] getChildIdAttrName(String childTagName) {
        if(OptionModel.TAG.equals(childTagName)) {
            return new String[]{"value"};
        } else if("recherche-paire".equals(childTagName)) {
            //return new String[]{"path"};
            return new String[]{"id"};
        } else if(SpecialKeyEntryModel.TAG.equals(childTagName)) {
            return new String[]{"pos"};
        } else {
            return null;
        }
    }
    @Override
    public void resetCharData() {
    }
    @Override
    public String getIdValue() { return getId(); }
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
        QName qname = new QName(uri,localName);
        if(OptionModel.QN.equals(qname)) {
            return hOptions.get(atts.getValue("value"));
        } else if(DocumentsMapping.RECHERCHE_PAIRE.equals(qname)) {
            return hRecherches.get(atts.getValue("id"));
        } else if(SpecialKeyEntryModel.QN.equals(qname)) {
            return hSpecialKeys.get(atts.getValue("id"));
        }
        return null;
    }
    public boolean isDependantRepository(){
    	boolean ret = false;
    	if(recherches.size()>0)ret=true;
    	return ret;
    }

    @Override
    public QName getQName() { return qn; }

    public Vector<ResourceRefModel> getResources() {
        return resources;
    }

    public HelpModel getHelp() {
        return help;
    }

    public void setHelp(HelpModel help) {
        this.help = help;
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        for(OptionModel om:options) om.prepareForUnload();
        for(RecherchePaireModel rpm:recherches) rpm.prepareForUnload();
        for(SpecialKeyEntryModel skm:specialKeys) skm.prepareForUnload();
    }

}
