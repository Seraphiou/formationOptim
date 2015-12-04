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
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import fr.gouv.finances.dgfip.xemelios.ui.ImageResources;
import fr.gouv.finances.dgfip.xemelios.ui.ListDisplayable;
import javax.xml.namespace.QName;

/**
 * Represente un plugin
 * @author chm
 */
public class PluginModel implements NoeudModifiable, Comparable, ListDisplayable {
    private static Logger logger = Logger.getLogger(PluginModel.class);
    private NoeudModifiable _NMParent = null;
    
    public static final transient String TAG="plugin";
    public static final transient QName QN = new QName(TAG);
    
    public static final transient String TYPE_SEARCH="search";
    public static final transient String TYPE_EXPORT="export";
    private String id, pluginType, pluginTitle, pluginClass, availablePersistences;
    private boolean sumPossible = false;
    private Vector<CritereModel> criteres;
    private HashMap<String,CritereModel> hCriteres;
    private Vector<CritereRefModel> criteresOp;
    private HashMap<String,CritereRefModel> hCriteresOp;
    private Vector<PropertyModel> properties;
    private HashMap<String,PropertyModel> hProperties;
    private OptionModel entete;
    private String separator;
    private ListeResultatModel listeResultat;
    private HelpModel help;
    
    // only for performance enhancements
    private HashSet<String> persistences=null;
    
    public PluginModel(QName tag) {
        super();
        criteres = new Vector<CritereModel>();
        hCriteres = new HashMap<String,CritereModel>();
        criteresOp = new Vector<CritereRefModel>();
        hCriteresOp = new HashMap<String,CritereRefModel>();
        properties = new Vector<PropertyModel>();
        hProperties = new HashMap<String,PropertyModel>();
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(CritereRefModel.QN.equals(tagName)) {
        	CritereRefModel cm = (CritereRefModel)child;
            if(hCriteresOp.containsKey(cm.getId())) {
            	CritereRefModel old = hCriteresOp.get(cm.getId());
                criteresOp.remove(old);
                hCriteresOp.remove(old.getId());
            }
            cm.setParentAsNoeudModifiable(this);
            criteresOp.add(cm);
            hCriteresOp.put(cm.getId(), cm);
        } else if(CritereModel.QN.equals(tagName)) {
            CritereModel cm = (CritereModel)child;
            if(hCriteres.containsKey(cm.getId())) {
                CritereModel old = hCriteres.get(cm.getId());
                criteres.remove(old);
                hCriteres.remove(old.getId());
            }
            cm.setParentAsNoeudModifiable(this);
            criteres.add(cm);
            hCriteres.put(cm.getId(), cm);
        } else if(PropertyModel.QN.equals(tagName)) {
            PropertyModel pm = (PropertyModel)child;
            if(hProperties.containsKey(pm.getName())) {
                PropertyModel old = hProperties.get(pm.getName());
                properties.remove(old);
                hProperties.remove(old.getName());
            }
            pm.setParentAsNoeudModifiable(this);
            properties.add(pm);
            hProperties.put(pm.getName(), pm);
        } else if(DocumentsMapping.HEADER.equals(tagName)) {
            entete=(OptionModel)child;
            entete.setParentAsNoeudModifiable(this);
        } else if(DocumentsMapping.LISTE_EXPORT.equals(tagName)) {
            listeResultat=(ListeResultatModel)child;
            listeResultat.setParentAsNoeudModifiable(this);
        } else if(HelpModel.QN.equals(tagName)) {
            help = (HelpModel)child;
        }
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id=(attributes.getValue("id")!=null?attributes.getValue("id"):id);
        pluginType=(attributes.getValue("type")!=null?attributes.getValue("type"):pluginType);
        pluginTitle=(attributes.getValue("title")!=null?attributes.getValue("title"):pluginTitle);
        pluginClass=(attributes.getValue("class")!=null?attributes.getValue("class"):pluginClass);
        separator=(attributes.getValue("separator")!=null?attributes.getValue("separator"):separator);
        availablePersistences=(attributes.getValue("available-persistences")!=null?attributes.getValue("available-persistences"):availablePersistences);
        if (attributes.getValue("sum-possible")!=null)
            sumPossible=attributes.getBooleanValue("sum-possible");
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        output.addAttribute("id", id);
        output.addAttribute("type", pluginType);
        output.addAttribute("title", pluginTitle);
        if(help!=null) help.marshall(output);
        output.endTag(QN);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(id==null || id.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@id is required ("+getParentAsNoeudModifiable().getConfigXPath()+").");
        if(pluginClass==null || pluginClass.length()==0) throw new InvalidXmlDefinition(getConfigXPath()+"/@class is required.");
        if(pluginTitle==null || pluginTitle.length()==0) throw new InvalidXmlDefinition(getConfigXPath()+"/@title is required.");
        if(pluginType==null || pluginType.length()==0) throw new InvalidXmlDefinition(getConfigXPath()+"/@type is required");
        if(TYPE_SEARCH.equals(pluginType) && separator!=null) throw new InvalidXmlDefinition(getConfigXPath()+": @separator is forbidden when @type='search'.");
        if(TYPE_EXPORT.equals(pluginType) && sumPossible) throw new InvalidXmlDefinition(getConfigXPath()+": @sum-possible is forbidden when @type='export'.");
        if(TYPE_EXPORT.equals(pluginType) && listeResultat==null) throw new InvalidXmlDefinition(getConfigXPath()+": <liste-export/> is required when @type='export'.");
        if(TYPE_SEARCH.equals(pluginType) || entete!=null) throw new InvalidXmlDefinition(getConfigXPath()+": <header/> is forbidden when @type='search'.");
        if(TYPE_SEARCH.equals(pluginType) && criteres.size()==0) throw new InvalidXmlDefinition(getConfigXPath()+": at least one <critere/> is required when @type='search'.");
        for(CritereModel cm:criteres) cm.validate();
        for(CritereRefModel cm:criteresOp) cm.validate();
        for(PropertyModel pm:properties) pm.validate();
        if(listeResultat!=null) listeResultat.validate();
    }   
    @Override
    public PluginModel clone() {
        PluginModel pm = new PluginModel(QN);
        pm.id=id;
        pm.pluginClass = pluginClass;
        pm.pluginTitle = pluginTitle;
        pm.pluginType = pluginType;
        pm.availablePersistences=availablePersistences;
        pm.sumPossible = sumPossible;
        for(PropertyModel prop:properties) {
            try {
                pm.addChild(prop,PropertyModel.QN);
            } catch(SAXException ignore) {
                logger.error("clone().property",ignore);
            }
        }
        for(CritereModel crit:criteres) {
            try {
                pm.addChild(crit.clone(),CritereModel.QN);
            } catch(SAXException ignore) {
                logger.error("clone().critere",ignore);
            }
        }
        for(CritereRefModel crit:criteresOp) {
            try {
                pm.addChild(crit.clone(),CritereRefModel.QN);
            } catch(SAXException ignore) {
                logger.error("clone().critere-ref",ignore);
            }
        }
        pm.separator=separator;
        try {
            if(entete!=null)
                pm.addChild(this.entete.clone(), DocumentsMapping.HEADER);
        } catch (Throwable t) {
            logger.error("clone().entete",t);
        }
        try {
            if(listeResultat!=null)
                pm.addChild(this.listeResultat.clone(), DocumentsMapping.LISTE_EXPORT);
        } catch (Throwable t) {
            logger.error("clone().listeResultat",t);
        }
        if(help!=null)
            pm.help = help.clone();
        return pm;
    }
    
    public Vector<CritereModel> getCriteres() {
        return criteres;
    }
    public Vector<CritereRefModel> getCriteresOp() {
        return criteresOp;
    }
    public String getPluginClass() {
        return pluginClass;
    }
    public String getPluginTitle() {
        return pluginTitle;
    }
    public String getPluginType() {
        return pluginType;
    }
    public int compareTo(Object o) {
        if(o==this) return 0;
        if(o instanceof RechercheModel) {
            RechercheModel other = (RechercheModel)o;
            return getPluginTitle().compareTo(other.getName());
        } else if(o instanceof PluginModel) {
            PluginModel other = (PluginModel)o;
            return getPluginTitle().compareTo(other.getPluginTitle());
        }
        return 0;
    }
    @Override
    public String toString() { return getPluginTitle(); }
    public Vector<PropertyModel> getProperties() {
        return properties;
    }
    public String getSeparator() {
        return separator;
    }
    public OptionModel getEntete() {
        return entete;
    }
    public ListeResultatModel getListeExport() { return listeResultat; }
    public String getResource() {
        if(TYPE_SEARCH.equals(getPluginType())) return ImageResources.LST_SEARCH_P;
        else if(TYPE_EXPORT.equals(getPluginType())) return ImageResources.LST_EXPORT_P;
        return null;
    }
    public String getDisplayName() {
        return toString();
    }
    public boolean isDeletable() {
        return false;
    }
    public String getAvailablePersistences() {
        return availablePersistences;
    }
    public void setAvailablePersistences(String availablePersistences) {
        this.availablePersistences = availablePersistences;
    }
    public boolean isAvailableFor(String persistence) {
        if(persistences==null) {
            persistences=new HashSet<String>();
            if(availablePersistences!=null) {
                StringTokenizer st = new StringTokenizer(availablePersistences,",; ");
                while(st.hasMoreTokens()) {
                    String s = st.nextToken();
                    persistences.add(s);
                }
            }
        }
        return persistences.contains(persistence);
    }
    public void setSumPossible(boolean isSumPossible) {
        this.sumPossible = isSumPossible;
    }
    public boolean isSumPossible() {
        return this.sumPossible;
    }
    public String getId() {
        return id;
    }
    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }
    
    public void modifyAttr(String attrName, String value) {
    }
    
    public void modifyAttrs(Attributes attrs) {
        try {
            getAttributes(new XmlAttributes(attrs));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise � jour des attributs : "+e);
        }
    }
    
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        this._NMParent = p;
    }
    public NoeudModifiable getParentAsNoeudModifiable() {
        return this._NMParent;
    }
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        if(CritereRefModel.TAG.equals(tagName)) {
            return hCriteresOp.get(id);
        } else if(CritereModel.TAG.equals(tagName)) {
            return hCriteres.get(id);
        } else if(PropertyModel.TAG.equals(tagName)) {
            return hProperties.get(id);
        } else if("header".equals(tagName)) {
            return entete;
        } else if("liste-export".equals(tagName)) {
            return listeResultat;
        } else {
            return null;
        }
    }
    public String[] getChildIdAttrName(String childTagName) {
    	if(CritereRefModel.TAG.equals(childTagName)) {
            return new String[]{"id"};
        } else if(CritereModel.TAG.equals(childTagName)) {
            return new String[]{"id"};
        } else if(PropertyModel.TAG.equals(childTagName)) {
            return new String[]{"name"};
        } else {
            return null;
        }
    }
    public void resetCharData() {
    }
    public String getIdValue() { return getId(); }
    private String configXPath = null;
    public String getConfigXPath() {
        if(configXPath==null) {
            if(getParentAsNoeudModifiable()!=null) configXPath = getParentAsNoeudModifiable().getConfigXPath();
            else configXPath="";
            configXPath+="/"+TAG+"[@id='"+getId()+"']";
        }
        return configXPath;
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        QName name = new QName(uri,localName);
        if(CritereRefModel.QN.equals(name)) {
            return hCriteresOp.get(atts.getValue("id"));
        } else if(CritereModel.QN.equals(name)) {
            return hCriteres.get(atts.getValue("id"));
        } else if(PropertyModel.QN.equals(name)) {
            return hProperties.get(atts.getValue("name"));
        } else if(DocumentsMapping.HEADER.equals(name)) {
            return entete;
        } else if(DocumentsMapping.LISTE_EXPORT.equals(name)) {
            return listeResultat;
        }
        return null;
    }

    public QName getQName() {
        return QN;
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
        for(CritereModel cm:criteres) cm.prepareForUnload();
        for(CritereRefModel cm: criteresOp) cm.prepareForUnload();
        if(entete!=null) entete.prepareForUnload();
        for(PropertyModel pm:properties) pm.prepareForUnload();
        if(listeResultat!=null) listeResultat.prepareForUnload();
    }
    
}
