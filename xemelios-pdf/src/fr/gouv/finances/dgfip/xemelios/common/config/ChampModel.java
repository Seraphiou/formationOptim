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

import fr.gouv.finances.dgfip.utils.Amount;
import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import fr.gouv.finances.dgfip.utils.DateUtils;
import javax.xml.namespace.QName;

/**
 * Modelise un champ a d'affichage
 * @author chm
 */
public class ChampModel implements NoeudModifiable{
    private static final Logger logger = Logger.getLogger(ChampModel.class);
    private String configXPath = null;
    public static final transient String DATATYPE_PJ="pj-list";
    public static final transient String DATATYPE_DECIMAL="decimal";
    public static final transient String DATATYPE_AMOUNT="amount";
    public static final transient String DATATYPE_INTEGER="integer";
    public static final transient String DATATYPE_DATE="date";
    
    public static final transient String TAG = "champ";
    public static final transient QName QN = new QName(TAG);
    private QName qn;
    private String tag;
    private String id, libelle, datatype;
    private XPathModel path;
    private Class classToReturn=null;
    private boolean checked = false;
    private boolean afficheDefaut = true;
    private boolean sommePossible = false;
    private String dateFormat;
    private HelpModel help;
    /**
     * par défaut un champs est affichable; cela pour des raisons de compatibilité avec les anciennes configurations qui ne précisent pas cet attribut
     */
    private boolean affichable = true;
    /**
     * par défaut, un champ est triable. C'est a peu pres vrai, maitenant qu'on indexe tout
     */
    private boolean triable = true;
    private boolean exportable = false;
    private boolean identifiant = false;
    
    private NoeudModifiable _NMParent = null;
    
    private NoeudModifiable parent = null;
    
    public void setParent(NoeudModifiable parent) {
        this.parent = parent;
    }
    public ChampModel(QName tagName) {
        super();
        this.qn=tagName;
        this.tag=qn.getLocalPart();
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(DocumentsMapping.VALEUR.equals(tagName)) {
            path = (XPathModel)child;
            path.setParent(this);
            path.setParentAsNoeudModifiable(this);
        } else if(HelpModel.QN.equals(tagName)) {
            help = (HelpModel)child;
        }
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id = (attributes.getValue("id")!=null?attributes.getValue("id"):id);
        libelle = (attributes.getValue("libelle")!=null?attributes.getValue("libelle"):libelle);
        datatype=(attributes.getValue("datatype")!=null?attributes.getValue("datatype"):datatype);
        if(datatype==null) datatype="string";
        if (attributes.getValue("checked")!=null)
            checked = attributes.getBooleanValue("checked");
        if(attributes.getValue("default-display")!=null)
            afficheDefaut = attributes.getBooleanValue("default-display");
        if (attributes.getValue("aggregate")!=null)
            sommePossible = attributes.getBooleanValue("aggregate");
        if(attributes.getValue("affichable")!=null)
            affichable = attributes.getBooleanValue("affichable");
        if(attributes.getValue("exportable")!=null)
            exportable = attributes.getBooleanValue("exportable");
        if(attributes.getValue("identifiant")!=null)
            identifiant = attributes.getBooleanValue("identifiant");
        dateFormat = attributes.getValue("date-format");
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(tag);
        output.addAttribute("id",id);
        output.addAttribute("libelle",libelle);
        output.addAttribute("datatype",datatype);
        if(checked) output.addAttribute("checked","true");
        if(!afficheDefaut) output.addAttribute("default-display", "false");
        if(sommePossible) output.addAttribute("aggregate", "true");
        if(dateFormat!=null) output.addAttribute("date-format", dateFormat);
        if(help!=null) help.marshall(output);
        path.marshall(output);
        output.endTag(tag);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(id==null) throw new InvalidXmlDefinition("//"+TAG+"/@id is required ("+getParentAsNoeudModifiable().getConfigXPath()+"/"+TAG+")");
        if(libelle==null || libelle.length()==0) throw new InvalidXmlDefinition("//"+TAG+"/@libelle is required ("+getConfigXPath()+")");
        path.validate();
    }
    public String getId() {return id;}
    public String getLibelle() {return libelle;}
    public XPathModel getPath() { return path; }
    public Class getDataClass() {
        if(classToReturn==null) {
            if(DATATYPE_INTEGER.equalsIgnoreCase(datatype)) classToReturn=java.lang.Integer.class;
            else if(DATATYPE_DECIMAL.equalsIgnoreCase(datatype)) classToReturn=java.lang.Float.class;
            else if(DATATYPE_DATE.equalsIgnoreCase(datatype)) classToReturn=java.util.Date.class;
            else if(DATATYPE_AMOUNT.equalsIgnoreCase(datatype)) classToReturn= Amount.class ;
            else if(DATATYPE_PJ.equalsIgnoreCase(datatype)) classToReturn = PjRefHandler.class;
            else classToReturn=java.lang.String.class;
        }
        return classToReturn;
    }
    @Override
    public ChampModel clone() {
        ChampModel cm = new ChampModel(QN);
        cm .id = this.id;
//        cm.tag = this.tag;
        cm.libelle = this.libelle;
        cm.datatype = this.datatype;
        if(help!=null) cm.help = help.clone();
        try {cm.addChild(this.path.clone(), DocumentsMapping.VALEUR);} catch (Throwable t) {logger.error(t);}
        cm.checked = this.checked;
        cm.afficheDefaut = this.afficheDefaut;
        cm.sommePossible = this.sommePossible;
        cm.affichable=this.affichable;
        cm.identifiant=this.identifiant;
        cm.exportable=this.exportable;
        cm.dateFormat=dateFormat;
        if(help!=null)
            cm.help = help.clone();
        return cm;
    }
    
    @Override
    public boolean equals(Object other) {
        if(other instanceof ChampModel) {
            return(((ChampModel)other).id.equals(this.id));
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    
    
    public boolean isChecked() { return checked; }
    public void setChecked(boolean bChk) { checked = bChk; }
    public boolean isAfficheDefaut() { return afficheDefaut; }
    public void setAfficheDefaut(boolean affDef) { afficheDefaut = affDef; }
    public boolean isSommePossible() { return sommePossible; }
    public void setSommePossible(boolean somPos) { sommePossible = somPos; }
    
    public String getDatatype() { return datatype; }
    
    
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
            logger.error("Erreur lors de la mise à jour des attributs : "+e);
        }
    }
    @Override
    public String[] getChildIdAttrName(String childTagName) {
        return null;
    }
    @Override
    public void resetCharData() {
    }
    public boolean isAffichable() {
        return affichable;
    }
    public boolean isTriable() { return triable; }
    public void setAffichable(boolean affichable) {
        this.affichable = affichable;
    }
    @Override
    public String getIdValue() { return getId(); }
    public boolean isExportable() {
        return exportable || identifiant;
    }
    public boolean isIdentifiant() {
        return identifiant;
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
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        //if(DocumentsMapping.VALEUR.equals(new QName(uri,localName))) return path;
        return null;
    }

    @Override
    public QName getQName() { return qn; }
    /**
     * Return o transtypped according to datatype 
     * @param o
     * @return
     */
    public Comparable getValueOf(Object o) {
        Comparable computed = null;
        if(o==null || o.toString().length()==0) {
            computed = null;
        } else {
            Class retClass = getDataClass();
            if(retClass.equals(Float.class)) {
                try {
                    computed = new Float(Float.parseFloat((String)o));
                } catch(NumberFormatException nfEx) {
                    computed = new Float(Float.NaN);
                }
            }
            else if(retClass.equals(Integer.class)) {
                try {
                    computed = new Integer(Integer.parseInt((String)o));
                } catch(NumberFormatException nfEx) {
                    computed = "NaN";
                }
            }
            else if(retClass.equals(Amount.class)) {
                try {
                    computed = new Amount((String)o);
                } catch(NumberFormatException nfEx) {
                    computed = "NaN";
                }
            }
            else if(retClass.equals(java.util.Date.class)) {
                try {
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//                    computed = sdf.parse((String) o);
                    computed = DateUtils.parseXsDateTime((String)o);
                } catch (Exception ex) {
//                    logger.debug(null, ex);
                    computed = o.toString();
                }
            } else if(retClass.equals(PjRefHandler.class)) {
                    computed = (Comparable)o;
            } else computed = o.toString();	// pour les trucs qui sont vraiment pas triables !
        }
        return computed;
    }

    @Override
    public String toString() {
        return getLibelle();
    }

    public HelpModel getHelp() {
        return help;
    }

    public void setHelp(HelpModel help) {
        this.help = help;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        parent = null;
        if(path!=null) path.prepareForUnload();
    }
    
}
