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
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import java.util.List;
import javax.xml.namespace.QName;

/**
 * Modelise une liste de resultats
 * @author chm
 */
public class ListeResultatModel implements NoeudModifiable, Cloneable {
    private static Logger logger = Logger.getLogger(ListeResultatModel.class);
    private ElementModel parent;
    private NoeudModifiable _NMParent = null;
    public static final transient String TAG = "liste-resultat";
    public static final transient QName QN = new QName(TAG);
    
    private String triDefaut, ordre;
    private Hashtable<String,ChampModel> champs;
    private Vector<ChampModel> listeChamps;
    private HashMap<String,ChampModel> hChampModels;
    private Vector<HiddenModel> hiddens;
    private HashMap<String,HiddenModel> hHiddenModels;
    private Vector<WidgetModel> widgets;
    private HashMap<String,WidgetModel> hWidgets;
    
    public ListeResultatModel(QName tagName) {
        super();
        champs = new Hashtable<String,ChampModel>();
        listeChamps = new Vector<ChampModel>();
        hChampModels = new HashMap<String,ChampModel> ();
        hiddens = new Vector<HiddenModel>();
        hHiddenModels = new HashMap<String,HiddenModel> ();
        widgets = new Vector<WidgetModel>();
        hWidgets = new HashMap<String,WidgetModel>();
        
        WidgetModel wm = new WidgetModel(WidgetModel.QN);
        wm.setClassName("fr.gouv.finances.cp.xemelios.widgets.generic.ViewCode");
        wm.setId("viewcode");
        wm.setLibelle("Voir le Xml...");
        
        try {
            addChild(wm, WidgetModel.QN);
        } catch(Exception ex) {
            logger.error("<init>.new widget",ex);
        }
        
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(ChampModel.QN.equals(tagName)) {
            ChampModel cm = (ChampModel)child;
            if(hChampModels.containsKey(cm.getId())) {
                ChampModel old = hChampModels.get(cm.getId());
                champs.remove(old.getId());
                listeChamps.remove(old);
                hChampModels.remove(old.getId());
            }
            cm.setParent(this);
            cm.setParentAsNoeudModifiable(this);
            champs.put(cm.getId(),cm);
            listeChamps.add(cm);
            hChampModels.put(cm.getId(), cm);
        } else if(HiddenModel.QN.equals(tagName)) {
            HiddenModel hm = (HiddenModel)child;
            if(hHiddenModels.containsKey(hm.getName())) {
                HiddenModel old = hHiddenModels.get(hm.getName());
                hiddens.remove(old);
                hHiddenModels.remove(old.getName());
            }
            hm.setParentAsNoeudModifiable(this);
            hiddens.add(hm);
            hHiddenModels.put(hm.getName(), hm);
        } else if(WidgetModel.QN.equals(tagName)) {
            WidgetModel wm = (WidgetModel)child;
            if(hWidgets.containsKey(wm.getId())) {
                WidgetModel old = hWidgets.get(wm.getId());
                widgets.remove(old);
                hWidgets.remove(old.getId());
            }
            wm.setParentAsNoeudModifiable(this);
            widgets.add(wm);
            hWidgets.put(wm.getId(),wm);
        }
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        triDefaut=(attributes.getValue("tri-defaut")!=null?attributes.getValue("tri-defaut"):triDefaut);
        ordre=(attributes.getValue("ordre")!=null?attributes.getValue("ordre"):ordre);
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        output.addAttribute("tri-defaut", triDefaut);
        output.addAttribute("ordre", ordre);
        for(HiddenModel hm:hiddens) {
            hm.marshall(output);
        }
        for(ChampModel cm:listeChamps) {
            cm.marshall(output);
        }
        for(WidgetModel wm:widgets) {
            // on ne le marshall pas, il ne doit jamais être écrit dans les conf
            if(!wm.getId().equals("viewcode"))
                wm.marshall(output);
        }
        output.endTag(QN);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(triDefaut==null) throw new InvalidXmlDefinition("//"+TAG+"/@tri-defaut is required ("+getConfigXPath()+")");
        for(String s:triDefaut.split(",")) {
            if(s.length()==0) continue;
            if(champs.get(s)==null) throw new InvalidXmlDefinition(getConfigXPath()+"/@tri-defaut references a champ that is not defined: "+s);
        }
        if(ordre==null) throw new InvalidXmlDefinition("//"+TAG+"/@ordre is required ("+getConfigXPath()+")");
        for(WidgetModel wm:widgets) wm.validate();
        for(ChampModel cm:listeChamps) cm.validate();
        for(HiddenModel h:hiddens) h.validate();
    }
    public Hashtable<String, ChampModel> getChamps() {return champs;}
    public String getOrdre() {return ordre;}
    public String getTriDefaut() {return triDefaut;}
    public Vector<ChampModel> getListeChamps() { return listeChamps; }
    @Override
    public ListeResultatModel clone() {
        ListeResultatModel other = new ListeResultatModel(QN);
        other.triDefaut = (triDefaut==null?null:new String(triDefaut));
        other.ordre = (ordre==null?null:new String(ordre));
        for(ChampModel cm:listeChamps) {
            ChampModel cm2 = cm.clone();
            try { 
                other.addChild(cm2,ChampModel.QN);
            } catch(SAXException ignore) {
                logger.error("clone().champ",ignore);
            }
        }
        other.champs.putAll(this.champs);
        for(HiddenModel hm:hiddens) {
            try { 
                other.addChild(hm.clone(),HiddenModel.QN);
            } catch(SAXException saxEx) {
                logger.error("clone().hidden",saxEx);
            }
        }
        other.emptyWidgets();
        for(WidgetModel wm:widgets) {
            try {
                other.addChild(wm.clone(),WidgetModel.QN);
            } catch(SAXException saxEx) {
                logger.error("clone().widget",saxEx);
            }
        }
        return other;
    }
    public ElementModel getParent() { return parent; }
    public void setParent(ElementModel parent) { this.parent=parent; }
    public void setOrdre(String ordre) { this.ordre = ordre; }
    public void setTriDefaut(String triDefaut) { this.triDefaut = triDefaut; }
    
    public Vector<HiddenModel> getHiddens() { return hiddens; }
    public HashMap<String,HiddenModel> getHhiddens() { return hHiddenModels; }
    
    
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
        if(ChampModel.TAG.equals(tagName)) {
            return hChampModels.get(id);
        } else if(HiddenModel.TAG.equals(tagName)) {
            return hHiddenModels.get(id);
        } else if(WidgetModel.TAG.equals(tagName)) {
            return hWidgets.get(id);
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
            logger.error("Erreur lors de la mise à jour des attributs : "+e);
        }
    }
    @Override
    public String[] getChildIdAttrName(String childTagName) {
        if(ChampModel.TAG.equals(childTagName)) {
            return new String[]{"id"};
        } else if(HiddenModel.TAG.equals(childTagName)) {
            return new String[]{"name"};
        } else if(WidgetModel.TAG.equals(childTagName)) {
            return new String[]{"id"};
        } else {
            return null;
        }
    }
    @Override
    public void resetCharData() {
    }
    @Override
    public String getIdValue() { return null; }
    
    public void removeAllChamps() {
        this.champs = new Hashtable<String,ChampModel>();
        this.listeChamps = new Vector<ChampModel>();
        this.hChampModels = new HashMap<String,ChampModel>();
    }
    public Vector<WidgetModel> getWidgets() { return widgets; }
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
        QName name = new QName(uri,localName);
        if(ChampModel.QN.equals(name)) {
            return hChampModels.get(atts.getValue("id"));
        } else if(HiddenModel.QN.equals(name)) {
            return hHiddenModels.get(atts.getValue("name"));
        } else if(WidgetModel.QN.equals(name)) {
            return hWidgets.get(atts.getValue("id"));
        }
        return null;
    }

    @Override
    public QName getQName() {
        return QN;
    }
    protected void emptyWidgets() {
        hWidgets.clear();
        widgets.clear();
    }
    public void changeAllDisplayableChamps(List<ChampModel> nouveauxChamps) {
        for(int i=listeChamps.size()-1;i>=0;i--) {
            ChampModel cm = listeChamps.elementAt(i);
            if(cm.isAffichable()) {
                champs.remove(cm.getId());
                listeChamps.remove(cm);
            }
        }
        for(ChampModel cm:nouveauxChamps) {
            try {
                addChild(cm, ChampModel.QN);
            } catch(Throwable t) {
                logger.error("changeAllDisplayableChamps(List).champ",t);
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<").append(QN).append(" tri-default=\"").append(triDefaut).append("\" sort=\"").append(ordre).append("\">\n");
        for(ChampModel cm:listeChamps) {
            if(cm.isAfficheDefaut()) {
                sb.append("\t<").append(ChampModel.QN).append(" id=\"").append(cm.getId()).append("\" libelle=\"").append(cm.getLibelle()).append("\" affichable=\"").append(Boolean.toString(cm.isAffichable())).append("\" affiche=\"").append(Boolean.toString(cm.isAfficheDefaut())).append("/>\n");
            }
        }
        sb.append("</").append(QN).append(">\n");
        return sb.toString();
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        for(ChampModel cm: champs.values()) cm.prepareForUnload();
        for(HiddenModel hm: hiddens) hm.prepareForUnload();
        for(WidgetModel wm: widgets) wm.prepareForUnload();
    }

}
