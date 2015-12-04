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
 * Modelise un champ de saisie
 * @author chm
 */
public class InputModel implements NoeudModifiable {
    private static Logger logger = Logger.getLogger(InputModel.class);
    private NoeudModifiable _NMParent = null;
    
    public static final transient String TAG = "input";
    public static final transient QName QN = new QName(TAG);
    private String id, libelle, datatype, helpPath, value,xmlFormat;
    private boolean uppercase = false;
    private boolean removeOperator = false;
    private CritereModel parent = null;
    private String keyName=null;
    private QName qn;
    private HelpModel help;
    private SearchHelperModel searchHelper;
    
    public void setParent(CritereModel parent) {
        this.parent = parent;
    }
    public boolean isUppercase() {
        return uppercase;
    }
    public InputModel(QName tagName) { super(); this.qn=tagName; }
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(HelpModel.QN.equals(tagName))
            help = (HelpModel)child;
        else if(SearchHelperModel.QNAME.equals(tagName)) {
            searchHelper = (SearchHelperModel)child;
            searchHelper.setParentAsNoeudModifiable(this);
        }
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id = (attributes.getValue("id")!=null?attributes.getValue("id"):id);
        libelle = (attributes.getValue("libelle")!=null?attributes.getValue("libelle"):libelle);
        datatype = (attributes.getValue("datatype")!=null?attributes.getValue("datatype"):datatype);
        if (attributes.getValue("uppercase")!=null)
            uppercase = attributes.getBooleanValue("uppercase");
        helpPath = (attributes.getValue("help")!=null?attributes.getValue("help"):helpPath);
        value = (attributes.getValue("value")!=null?attributes.getValue("value"):value);
        xmlFormat = (attributes.getValue("xml-format")!=null?attributes.getValue("xml-format"):xmlFormat);
        if(attributes.getValue("remove-operator")!=null)
            removeOperator = attributes.getBooleanValue("remove-operator");
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        output.addAttribute("id",id);
        if(libelle!=null) output.addAttribute("libelle",libelle);
        output.addAttribute("datatype",datatype);
        output.addAttribute("uppercase",uppercase);
        if("date".equals(datatype)) output.addAttribute("xml-format",xmlFormat);
        if(helpPath!=null) output.addAttribute("help",helpPath);
        if(removeOperator) output.addAttribute("remove-operator","true");
        output.endTag(TAG);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(id==null || id.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@id is required ("+getParentAsNoeudModifiable().getConfigXPath()+")");
//        if((xmlFormat==null || xmlFormat.length()==0) && "date".equals(datatype)) throw new InvalidXmlDefinition("//"+TAG+"[@datatype='date']/@xml-format is required ("+getConfigXPath()+")");
    }
    public String getDatatype() {return datatype;}
    public String getId() {return id;}
    public String getLibelle() {return libelle;}
    public String getHelpPath() { return helpPath; }
    public String getXmlDateFormat() {
        // changed for types
        //return xmlFormat;
        if(getDatatype().equals("date")) return "yyyy-MM-dd";
        else if(getDatatype().equals("time")) return "yyyy-MM-dd'T'hh:mm:ss";
        else return "";
    }
    public boolean hasToRemoveOperator() { return removeOperator; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value=value; }
    @Override
    public InputModel clone() {
        InputModel im = new InputModel(QN);
        im.id = this.id;
        im.libelle = this.libelle;
        im.datatype = this.datatype;
        im.helpPath = this.helpPath;
        im.uppercase = this.uppercase;
        im.xmlFormat = this.xmlFormat;
        im.value=this.value;
        im._NMParent=this._NMParent;
        im.removeOperator = this.removeOperator;
        if(this.searchHelper!=null) {
            im.searchHelper = this.searchHelper.clone();
            im.searchHelper.setParentAsNoeudModifiable(im);
        }
        return im;
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
    /**
     * This criteria key. Key is composed of Document id + "-" + Etat id + "-" + criteria id<br/>
     * a same criteria may be shared between differents elements.
     */
    public String getKeyName() {
        if(keyName==null) {
            CritereModel cm = (CritereModel)_NMParent;
            if(cm==null) return null;
            Object o = cm.getParentAsNoeudModifiable();
            ElementModel el = null;
            PluginModel pm = null;
            if(o instanceof ElementModel) {
                el = (ElementModel)o;
            } else if(o instanceof PluginModel) {
                pm = (PluginModel)o;
                el = (ElementModel)pm.getParentAsNoeudModifiable();
            }
            if(el==null) return null;
            EtatModel em = (EtatModel)el.getParent();
            if(em==null) return null;
            DocumentModel dm = em.getParent();
            if(dm==null) return null;
            StringBuffer b = new StringBuffer();
            b.append(dm.getId()).append("-").append(em.getId()).append("-");
            if(pm!=null) b.append(pm.getId()).append("-");
            b.append(getId());
            keyName = b.toString();
        }
        return keyName;
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
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) { return null; }

    @Override
    public QName getQName() { return qn; }

    public HelpModel getHelp() {
        return help;
    }

    public void setHelp(HelpModel help) {
        this.help = help;
    }

    public SearchHelperModel getSearchHelper() {
        return searchHelper;
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        parent = null;
        if(searchHelper!=null) searchHelper.prepareForUnload();
    }

    
}
