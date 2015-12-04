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

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import fr.gouv.finances.dgfip.xemelios.common.Constants;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;
import java.util.List;
import javax.xml.namespace.QName;

/**
 * Modelise l'element a afficher
 * @author chm
 */
public class ElementModel implements NoeudModifiable, EnvironmentDomain, PropertyChangeListener {
    public static final transient String TAG = "element";
    public static final transient QName QN = new QName(TAG);
    private static final Logger logger = Logger.getLogger(ElementModel.class);
    private static final Object locker = new Object();
    
    private NoeudModifiable _NMParent = null;
    
    private ListeResultatModel listeResultat;
    private Vector<CritereModel> vCriteres;
    private Hashtable<String,CritereModel> hCriteres;
//    private Vector<IndexModel> indexes;
    private Vector<PluginModel> plugins;
    private HashMap<String,PluginModel> hPlugins;
    private String id, titre, balise, xslt;
    private XPathModel path;
    private EtatModel parent;
    private boolean browsable=false, searchable=true, display=true;
    private EnvironmentModel env;
    private int maxDisplay = 100;
//    private boolean modeAnomaly = false;
    private Vector<CritereModel> availableCriteres;
    private SimpleElement simpleParent;
    private EnfantModel enfants;
    private QName qn;
    private HelpModel help;
    
    // do not clone this
    private ArrayList<PropertyChangeListener> listeners;
    
    public ElementModel(QName tagName) {
        super();
        this.qn=tagName;
        vCriteres = new Vector<CritereModel>();
        hCriteres = new Hashtable<String,CritereModel>();
        //availableCriteres = new Vector<CritereModel>();
        plugins = new Vector<PluginModel>();
        hPlugins = new HashMap<String,PluginModel>();
        listeners = new ArrayList<PropertyChangeListener>();
        listeners.add(this);
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(ListeResultatModel.QN.equals(tagName)) {
            listeResultat = (ListeResultatModel)child;
            listeResultat.setParentAsNoeudModifiable(this);
            listeResultat.setParent(this);
        } else if(CritereModel.QN.equals(tagName)) {
            CritereModel c = (CritereModel)child;
            if(hCriteres.containsKey(c.getId())) {
                CritereModel old = hCriteres.get(c.getId());
                vCriteres.remove(old);
                hCriteres.remove(old.getId());
            }
            c.setParentAsNoeudModifiable(this);
            vCriteres.add(c);
            hCriteres.put(c.getId(), c);
            
        } else if(DocumentsMapping.PATH.equals(tagName)) {
            path = (XPathModel)child;
            path.setParentAsNoeudModifiable(this);
        } else if(PluginModel.QN.equals(tagName)) {
            PluginModel pm = (PluginModel)child;
            if(hPlugins.containsKey(pm.getId())) {
                PluginModel old = hPlugins.get(pm.getId());
                plugins.remove(old);
                hPlugins.remove(old.getId());
            }
            pm.setParentAsNoeudModifiable(this);
            plugins.add(pm);
            hPlugins.put(pm.getId(), pm);
        } else if(EnvironmentModel.QN.equals(tagName)) {
            env=(EnvironmentModel)child;
            env.setParentAsNoeudModifiable(this);
        } else if (DocumentsMapping.PARENT.equals(tagName)) {
            simpleParent = (SimpleElement)child;
            simpleParent.setParentAsNoeudModifiable(this);
        } else if (DocumentsMapping.ENFANTS.equals(tagName)) {
            enfants = (EnfantModel)child;
            enfants.setParentAsNoeudModifiable(this);
        } else if(HelpModel.QN.equals(tagName)) {
            help = (HelpModel)child;
        } else throw new SAXException("unexpected child element : "+tagName);
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id = (attributes.getValue("id")!=null?attributes.getValue("id"):id);
        titre = (attributes.getValue("titre")!=null?attributes.getValue("titre"):titre);
        balise = (attributes.getValue("balise")!=null?attributes.getValue("balise"):balise);
        xslt = (attributes.getValue("xslt-file")!=null?attributes.getValue("xslt-file"):xslt);
        try {
            if (attributes.getValue("max-display")!=null)
                maxDisplay = (attributes.getIntValue("max-display"));
        } catch(Throwable t) {
            logger.error("invalid int value for max-display: "+attributes.getValue("max-display"));
        }
        if(attributes.getValue("browsable")!=null) browsable=attributes.getBooleanValue("browsable");
        if(attributes.getValue("searchable")!=null) searchable=attributes.getBooleanValue("searchable");
        if(attributes.getValue("display")!=null) display=attributes.getBooleanValue("display");
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        output.addAttribute("id", id);
        output.addAttribute("titre", titre);
        output.addAttribute("browsable", browsable);
        output.addAttribute("searchable", searchable);
        output.addAttribute("display", display);
        if(listeResultat!=null) listeResultat.marshall(output);
        for(CritereModel critere:getCriteres()) critere.marshall(output);
        for(PluginModel plugin:getPlugins()) plugin.marshall(output);
        output.endTag(QN);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(id==null) throw new InvalidXmlDefinition("//"+TAG+"/@id is required ("+getParentAsNoeudModifiable().getConfigXPath()+")");
        if(balise==null || balise.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@balise is required ("+getConfigXPath()+")");
        if(titre==null || titre.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@titre is required ("+getConfigXPath()+")");
        path.validate();
        if(env!=null) env.validate();
        if(listeResultat!=null) listeResultat.validate();
        if(simpleParent!=null) simpleParent.validate();
        if(enfants!=null) enfants.validate();
        for(CritereModel cm:vCriteres) cm.validate();
    }
    
    public String getBalise() {return balise;}
    public String getId() {return id;}
    @Override
    public String getIdValue() { return getId(); }
    public ListeResultatModel getListeResultat() {return listeResultat;}
    public String getTitre() { return titre; }
    public Vector<CritereModel> getCriteres() { return vCriteres; }
    public XPathModel getPath() { return path; }
    public int getMaxDisplay() {
        return maxDisplay;
    }
    public Vector<PluginModel> getPlugins() {
        return plugins;
    }
    @Override
    @SuppressWarnings("static-access")
    public ElementModel clone() {
        ElementModel em = new ElementModel(QN);
        try {
            if(listeResultat!=null)
                em.addChild(this.listeResultat.clone(), ListeResultatModel.QN);
        } catch(Throwable t) {
            logger.error("clone().listeResultat",t);
        }
        for(CritereModel cm:this.vCriteres) try { em.addChild(cm.clone(), cm.QN); } catch (Throwable t) {
            logger.error("clone().critere",t);
        }
        for(PluginModel pm:plugins) try { em.addChild(pm.clone(),PluginModel.QN); } catch(Throwable t) {
            logger.error("clone().plugin",t);
        }
        em.id = this.id;
        em.titre = this.titre;
        em.balise = this.balise;
        try {
            if(path!=null)
                em.addChild(this.path, DocumentsMapping.PATH);
        } catch (Throwable t) {
            logger.error("clone().path",t);
        }
        em.maxDisplay = this.maxDisplay;
        em.browsable=this.browsable;
        em.display=this.display;
        try {
            if(env!=null)
                em.addChild(this.env.clone(), EnvironmentModel.QN);
        } catch (Throwable t) {
            logger.error("clone().environment",t);
        }
//        em.modeAnomaly=this.modeAnomaly;
        em.searchable=this.searchable;
        em.xslt=this.xslt;
        em._NMParent=this._NMParent;
        if (this.simpleParent!=null)
            em.simpleParent = this.simpleParent.clone();
        else
            em.simpleParent = null;
        try {
            if(enfants!=null)
                em.addChild(enfants, DocumentsMapping.ENFANTS);
        } catch (Throwable t) {
            logger.error("clone().enfants",t);
        }
        if(help!=null)
            em.help = help.clone();
        return em;
    }
    public String getXslt() {
        if(xslt==null) return ((EtatModel)parent).getXslt();
        return xslt;
    }
    public boolean isBrowsable() {
        return browsable;
    }
    public boolean isSearchable() {
        return searchable;
    }
//    public boolean isModeAnomaly() { return modeAnomaly; }
    
    @Override
    public EnvironmentDomain getChildAt(int domain, int pos) { return null; }
    
    @Override
    public boolean hasEnvironment(int domain) {
        if(env==null) return false;
        Enumeration<VariableModel> enumer = env.getVariables(domain);
        if(enumer==null) return false;
        return enumer.hasMoreElements();
    }
    
    @Override
    public Enumeration<VariableModel> getVariables(int domain) { return env.getVariables(domain); }
    
    @Override
    public int getChildCount(int domain, PropertiesExpansion applicationProperties) { return 0; }
    
    @Override
    public Enumeration<EnvironmentDomain> children(int domain, PropertiesExpansion applicationProperties) { return null; }
    
    @Override
    public String toString() { return titre; }
    
    public EtatModel getParent() {
        return parent;
    }
    public void setParent(EtatModel parent) {
        this.parent = parent;
    }
    
    @Override
    public Object getValue(final String path) throws DataConfigurationException {
        String sTmp;
        if(path.startsWith("/")) sTmp = path.substring(1); else sTmp = path;
        if("@max-display".equals(sTmp)) return new Integer(getMaxDisplay());
        else throw new DataConfigurationException(sTmp+" is not readable");
    }
    @Override
    public void setValue(final String path, final Object value) throws DataConfigurationException {
        String sTmp;
        if(path.startsWith("/")) sTmp = path.substring(1); else sTmp = path;
        if("@max-display".equals(sTmp)) {
            int newValue = Integer.parseInt(value.toString());
            if(newValue!=maxDisplay) {
                int oldValue = maxDisplay;
                maxDisplay = newValue;
                firePropertyChange("max-display",oldValue,newValue);
            }
        } else throw new DataConfigurationException(sTmp+" is not readable");
    }
    
    // property listeners
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }
    
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        firePropertyChange(propertyName,new Integer(oldValue),new Integer(newValue));
    }
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        firePropertyChange(propertyName,Boolean.valueOf(oldValue),Boolean.valueOf(newValue));
    }
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
        for(PropertyChangeListener listener:listeners) {
            listener.propertyChange(pce);
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
    public Vector<CritereModel> getAvailableCriteres() {
        if(availableCriteres==null) {
            synchronized(locker) {
                availableCriteres = new Vector<CritereModel>();
                for(CritereModel cm:getCriteres()) {
                    availableCriteres.add(cm);
                }
            }
        }
        return availableCriteres;
    }
    public Iterator<PropertyChangeListener> getPropertyListeners() {
        if(listeners.isEmpty()) return null;
        return listeners.iterator();
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
        if(ListeResultatModel.TAG.equals(tagName)) {
            return (NoeudModifiable)listeResultat;
        } else if(CritereModel.TAG.equals(tagName)) {
            return (NoeudModifiable)hCriteres.get(id);
        } else if("path".equals(tagName)) {
            return (NoeudModifiable)path;
        } else if(PluginModel.TAG.equals(tagName)) {
            return (NoeudModifiable)plugins;
        } else if(EnvironmentModel.TAG.equals(tagName)) {
            return (NoeudModifiable)env;
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
        if(CritereModel.TAG.equals(childTagName)) {
            return new String[]{"id"};
        } else if(PluginModel.TAG.equals(childTagName)) {
            return new String[]{"id"};
        } else if (EnvironmentModel.TAG.equals(childTagName)) {
            return new String[]{""};
        } else {
            return null;
        }
    }
    @Override
    public void resetCharData() {
    }
    public EnfantModel getEnfant() {
        return enfants;
    }
    public void setEnfant(EnfantModel enfant) {
        this.enfants = enfant;
    }
    public SimpleElement getSimpleParent() {
        return simpleParent;
    }
    public CritereModel getCritere(String critereId) { return hCriteres.get(critereId); }
    public Element createSmallDOM(Document doc) {
        Element ret = doc.createElementNS(Constants.CONFIG_NS_URI,"conf:"+TAG);
        ret.setAttribute("id",getId());
        ret.setAttribute("libelle",getTitre());
        return ret;
    }
    public boolean isDisplay() { return display; }

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
        QName name=new QName(uri,localName);
        if(ListeResultatModel.QN.equals(name)) {
            return listeResultat;
        } else if(CritereModel.QN.equals(name)) {
            return hCriteres.get(atts.getValue("id"));
        } else if(PluginModel.QN.equals(name)) {
            return hPlugins.get(atts.getValue("id"));
        } else if(EnvironmentModel.QN.equals(name)) {
            return env;
        } else if (DocumentsMapping.PARENT.equals(name)) {
            return simpleParent;
        } else if (DocumentsMapping.ENFANTS.equals(name)) {
            return enfants;
        }
        return null;
    }

    @Override
    public QName getQName() { return qn; }
    public boolean equals(ElementModel em) {
        if(em==null) return false;
        return getId().equals(em.getId());
    }

    public String getKey() {
        return getParent().getParent().getId()+"|"+getParent().getId()+"|"+getId();
    }
    public List<ElementModel> getSousElements() {
        Vector<ElementModel> ret = new Vector<ElementModel>();
        if(getEnfant()!=null) {
            for(SimpleElement se:getEnfant().getEnfants().values()) {
                ret.add(getParent().getElementById(se.getElement()));
            }

        }
        return ret;
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
        if(simpleParent!=null) simpleParent.prepareForUnload();
        if(enfants!=null) enfants.prepareForUnload();
        for(CritereModel cm: vCriteres) cm.prepareForUnload();
        if(listeResultat!=null) listeResultat.prepareForUnload();
        for(PluginModel pm:plugins) pm.prepareForUnload();
    }
    
}
