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
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TreeMap;
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
import javax.xml.namespace.QName;

/**
 * Modelise un etat
 * @author chm
 */
public class EtatModel implements NoeudModifiable, EnvironmentDomain {
    public static final transient String TAG = "etat";
    public static final transient QName QN = new QName(TAG);
    private static Logger logger = Logger.getLogger(EtatModel.class);
    private NoeudModifiable _NMParent = null;
    
    private Vector<ElementModel> orderedElements;
    private TreeMap<String,ElementModel> elements;
    private Vector<String> entetes;
    private HashMap<String,EnteteModel> hEntetes;
    private DocumentModel parent;
    private String id,titre, balise, xslt, searchableElement, browsableElement, importableElement, displayInMenuIf, baliseNamespace;
    private boolean useExternalBrowser = true, browsable=false, searchable=true, multiPage=false;
    private Vector<EnvironmentDomain> vElements;
    private boolean exportable = false;
    private String transformClass;
    private Marker marker = null;
    private HelpModel help;
    
    public boolean isExportable() {
        return exportable;
    }
    public void setExportable(boolean exportable) {
        this.exportable = exportable;
    }
    public EtatModel(QName tagName) {
        super();
        entetes = new Vector<String>();
        hEntetes = new HashMap<String,EnteteModel> ();
        elements = new TreeMap<String,ElementModel>();
        orderedElements = new Vector<ElementModel>();
    }
    @Override
    public void addCharacterData(String cData) throws SAXException { }
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(ElementModel.QN.equals(tagName)) {
            ElementModel element = (ElementModel)child;
            if(elements.containsKey(element.getId())) {
                ElementModel old = elements.get(element.getId());
                elements.remove(old.getId());
                orderedElements.remove(old);
            }
            element.setParent(this);
            element.setParentAsNoeudModifiable(this);
            elements.put(element.getId(),element);
            orderedElements.add(element);
        } else if(EnteteModel.QN.equals(tagName)) {
            EnteteModel em = (EnteteModel)child;
            if(hEntetes.containsKey(em.getId())) {
                EnteteModel old = hEntetes.get(em.getId());
                entetes.remove(old.getBalise());
                hEntetes.remove(old.getId());
            }
            em.setParentAsNoeudModifiable(this);
            entetes.add((em).getBalise());
            hEntetes.put(em.getId(), em);
        } else if(HelpModel.QN.equals(tagName)) {
            help = (HelpModel)child;
        }
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id=(attributes.getValue("id")!=null?attributes.getValue("id"):id);
        titre=(attributes.getValue("titre")!=null?attributes.getValue("titre"):titre);
        balise=(attributes.getValue("balise")!=null?attributes.getValue("balise"):balise);
        xslt=(attributes.getValue("xslt-file")!=null?attributes.getValue("xslt-file"):xslt);
        if(attributes.getValue("use-external-browser")!=null)
            useExternalBrowser = attributes.getBooleanValue("use-external-browser");
        if(attributes.getValue("browsable")!=null)
            browsable=attributes.getBooleanValue("browsable");
        if(attributes.getValue("searchable")!=null)
            searchable=attributes.getBooleanValue("searchable");
        if(attributes.getValue("multi-page")!=null)
            multiPage=attributes.getBooleanValue("multi-page");
        searchableElement=(attributes.getValue("searchable-element")!=null?attributes.getValue("searchable-element"):searchableElement);
        browsableElement=(attributes.getValue("browsable-element")!=null?attributes.getValue("browsable-element"):browsableElement);
        importableElement=(attributes.getValue("importable-element")!=null?attributes.getValue("importable-element"):importableElement);
        // cette propriete n'est jamais heritee
        displayInMenuIf = attributes.getValue("displayInMenuIf");
        if (attributes.getValue("exportable")!=null)
            exportable = attributes.getBooleanValue("exportable");
        transformClass=(attributes.getValue("transform-class")!=null?attributes.getValue("transform-class"):transformClass);
        baliseNamespace = (attributes.getValue("balise-namespace")!=null ? attributes.getValue("balise-namespace") : baliseNamespace);
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        output.addAttribute("id", id);
        output.addAttribute("titre",titre);
        output.addAttribute("searchable", searchable);
        output.addAttribute("browsable", browsable);
        if(displayInMenuIf!=null) output.addAttribute("displayInMenuIf", displayInMenuIf);
        if(help!=null) help.marshall(output);
        for(ElementModel element: elements.values()) element.marshall(output);
        output.endTag(QN);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(id==null) throw new InvalidXmlDefinition("//"+TAG+"/@id is required ("+getParentAsNoeudModifiable().getConfigXPath()+")");
        if(balise==null || balise.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@balise is reguired ("+getConfigXPath()+")");
//        if(baliseNamespace==null || baliseNamespace.length()==0) throw new InvalidXmlDefinition(getConfigXPath()+"/@balise-namespace is required");
        if(titre==null || titre.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@titre is reguired ("+getConfigXPath()+")");
        if(xslt!=null && getParent().getXsltFileChooser()==null) {
            File f = new File(new File(getParent().getBaseDirectory()),xslt);
            if(!f.exists()) throw new InvalidXmlDefinition(f.getAbsolutePath()+" can not be found");
        }
        for(ElementModel element:elements.values()) element.validate();
    }
    public String getBalise() {return balise;}
    public String getBaliseNamespace() { return baliseNamespace; }
//	public ElementModel getElement() {return element;}
    public String getId() {return id;}
    @Override
    public String getIdValue() { return getId(); }
    public String getTitre() {return titre;}
    public String getXslt() {
        return xslt;
    }
    public Vector<String> getEntetes() {
        return entetes;
    }
    public DocumentModel getParent() {
        return parent;
    }
    // a priori, seul DocumentModel peut definir le parent
    protected void setParent(DocumentModel parent) {
        this.parent = parent;
    }
    public EtatModel clone(DocumentModel otherParent) {
        EtatModel em = new EtatModel(QN);
        for(ElementModel element:elements.values())
            try {
                em.addChild(element.clone(),ElementModel.QN);
            } catch(Throwable t) {
                logger.error("clone().element",t);
            }
        for(String s:this.hEntetes.keySet())
            try {
                em.addChild(this.hEntetes.get(s).clone(), EnteteModel.QN);
            } catch (Throwable t) {
                logger.error("clone().entete",t);
            }
            if(help!=null) em.help = help.clone();
        em.parent = otherParent;
        em.id = this.id;
        em.titre = this.titre;
        em.balise = this.balise;
        em.xslt=this.xslt;
        em.browsable=this.browsable;
        em.searchable=this.searchable;
        em.browsableElement=this.browsableElement;
        em.importableElement=this.importableElement;
        em.multiPage=this.multiPage;
        em.searchableElement=this.searchableElement;
        em.useExternalBrowser=this.useExternalBrowser;
        em._NMParent=this._NMParent;
        em.exportable=this.exportable;
        em.transformClass=this.transformClass;
        em.baliseNamespace=this.baliseNamespace;
        return em;
    }
    @Override
    public EtatModel clone() { return clone((DocumentModel)parent); }
    public boolean useExternalBrowser() {
        return useExternalBrowser;
    }
    public boolean isBrowsable() {
        return browsable;
    }
    public boolean isSearchable() {
        return searchable;
    }
    public ElementModel getElementById(String elementId) { return elements.get(elementId); }
    public ElementModel getDefaultElement() {
        return elements.values().iterator().next();
    }
    public Collection<ElementModel> getSearchableElements() {
        ArrayList<ElementModel> ret = new ArrayList<ElementModel>();
        for(ElementModel em:orderedElements) {
            if(em.isSearchable()) ret.add(em);
        }
        //if(searchableElement!=null) return getElementById(searchableElement);
        //return getDefaultElement();
        return ret;
    }
    public ElementModel getBrowsableElement() {
        if(browsableElement!=null) return getElementById(browsableElement);
        return getDefaultElement();
    }
    public ElementModel getImportableElement() {
        if(importableElement!=null) return getElementById(importableElement);
        return getDefaultElement();
    }
    public boolean isMultiPage() {
        return multiPage;
    }
    
    @Override
    public EnvironmentDomain getChildAt(int domain, int pos) {
        if(vElements==null) {
            vElements = new Vector<EnvironmentDomain>();
            vElements.addAll(orderedElements);
        }
        return vElements.elementAt(pos);
    }
    
    
    @Override
    public boolean hasEnvironment(int domain) { return false; }
    
    @Override
    public Enumeration<VariableModel> getVariables(int domain) { return null; }
    
    @Override
    public int getChildCount(int domain, PropertiesExpansion applicationProperties) { return elements.size(); }
    
    @Override
    public Enumeration<EnvironmentDomain> children(int domain, PropertiesExpansion applicationProperties) {
        if(vElements==null) {
            vElements = new Vector<EnvironmentDomain>();
            vElements.addAll(elements.values());
        }
        return (Enumeration<EnvironmentDomain>)(vElements.elements());
    }
    @Override
    public String toString() { return titre; }
    
    @Override
    public Object getValue(final String path) throws DataConfigurationException {
        String sTmp;
        if(path.startsWith("/")) sTmp = path.substring(1); else sTmp = path;
        if(sTmp.startsWith(ElementModel.TAG)) {
            int pos = sTmp.indexOf('/');
            String next = sTmp.substring(pos);
            String condition = sTmp.substring(ElementModel.TAG.length(),pos);
            if(condition.startsWith("[") && condition.endsWith("]")) condition = condition.substring(1,condition.length()-1);
            if(condition.startsWith("@id=")) {
                String elementId = condition.substring(5,condition.length()-1);
                ElementModel em = getElementById(elementId);
                return em.getValue(next);
            } else {
                throw new DataConfigurationException("can only access to element by id");
            }
        } else {
            throw new DataConfigurationException("from etat, you can only access to element(s)");
        }
    }
    @Override
    public void setValue(final String path, final Object value) throws DataConfigurationException {
        String sTmp;
        if(path.startsWith("/")) sTmp = path.substring(1); else sTmp = path;
        if(sTmp.startsWith(ElementModel.TAG)) {
            int pos = sTmp.indexOf('/');
            String next = sTmp.substring(pos);
            String condition = sTmp.substring(ElementModel.TAG.length(),pos);
            if(condition.startsWith("[") && condition.endsWith("]")) condition = condition.substring(1,condition.length()-1);
            if(condition.startsWith("@id=")) {
                String elementId = condition.substring(5,condition.length()-1);
                ElementModel em = getElementById(elementId);
                em.setValue(next,value);
            } else {
                throw new DataConfigurationException("can only access to element by id");
            }
        } else {
            throw new DataConfigurationException("from etat, you can only access to element(s)");
        }
    }
    public String getDisplayInMenuIf() { return displayInMenuIf; }
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
        if (ElementModel.TAG.equals(tagName)) {
            return elements.get(id);
        } else if (EnteteModel.TAG.equals(tagName)) {
            return hEntetes.get(id);
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
        if (ElementModel.TAG.equals(childTagName)) {
            return new String[]{"id"};
        } else if (EnteteModel.TAG.equals(childTagName)) {
            return new String[]{"id"};
        } else {
            return null;
        }
    }
    @Override
    public void resetCharData() {
    }
    public TreeMap<String, ElementModel> getElements() {
        return elements;
    }
    public String getTransformClass() {
        return transformClass;
    }
    public void setTransformClass(String transformClass) {
        this.transformClass = transformClass;
    }
    public Element createSmallDOM(Document doc) {
        Element ret = doc.createElementNS(Constants.CONFIG_NS_URI,"conf:"+TAG);
        ret.setAttribute("id",getId());
        ret.setAttribute("libelle",getTitre());
        for(ElementModel em:orderedElements) {
            Element el = em.createSmallDOM(doc);
            ret.appendChild(el);
        }
        return ret;
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
        QName name=new QName(uri,localName);
        if(ElementModel.QN.equals(name)) {
            return elements.get(atts.getValue("id"));
        } else if(EnteteModel.QN.equals(name)) {
            return hEntetes.get(atts.getValue("id"));
        }
        return null;
    }

    @Override
    public QName getQName() {
        return QN;
    }

    public Marker getMarker() {
        if(marker==null) {
            String docId = null;
            if(getParent()!=null) docId = getParent().getId();
            marker = new Marker(docId,id);
        }
        return marker;
    }

    public HelpModel getHelp() {
        return help;
    }

    public void setHelp(HelpModel help) {
        this.help = help;
    }

    @Override
    public void prepareForUnload() {
        _NMParent=null;
        for(ElementModel element:elements.values()) element.prepareForUnload();
        for(EnteteModel em: hEntetes.values()) em.prepareForUnload();
        for(ElementModel em: orderedElements) em.prepareForUnload();
        parent = null;
    }

    public static class Marker {
        private String docId, etatId;

        public Marker(String docId, String etatId) {
            super();
            this.docId=docId;
            this.etatId=etatId;
        }

        public String getDocId() {
            return docId;
        }

        public String getEtatId() {
            return etatId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Marker other = (Marker) obj;
            if ((this.docId == null) ? (other.docId != null) : !this.docId.equals(other.docId)) {
                return false;
            }
            if ((this.etatId == null) ? (other.etatId != null) : !this.etatId.equals(other.etatId)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + (this.docId != null ? this.docId.hashCode() : 0);
            hash = 97 * hash + (this.etatId != null ? this.etatId.hashCode() : 0);
            return hash;
        }
       
    }
}
