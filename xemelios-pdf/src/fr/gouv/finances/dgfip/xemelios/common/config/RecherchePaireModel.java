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

import fr.gouv.finances.dgfip.utils.Pair;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;
import javax.xml.namespace.QName;

/**
 * Modelise une recherche de paires code/libelle
 * @author chm
 */
public class RecherchePaireModel implements NoeudModifiable {
    private static Logger logger = Logger.getLogger(RecherchePaireModel.class);
    public static final int SORT_NO=0;
    public static final int SORT_KEY=1;
    public static final int SORT_LIB=2;

    private NoeudModifiable _NMParent = null;
    
    private XPathModel path, codePath, libellePath;
    private String tag, id;
    private String sort = "no";
    private QName qn;
    
    public RecherchePaireModel(QName tagName) {
        super();
        this.qn = tagName;
        this.tag=qn.getLocalPart();
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(DocumentsMapping.PATH.equals(tagName)) {
            path = (XPathModel)child;
            path.setParentAsNoeudModifiable(this);
        } else if(DocumentsMapping.CODE_PATH.equals(tagName)) {
            codePath = (XPathModel)child;
            codePath.setParentAsNoeudModifiable(this);
        } else if(DocumentsMapping.LIBELLE_PATH.equals(tagName)) {
            libellePath = (XPathModel)child;
            libellePath.setParentAsNoeudModifiable(this);
        }
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id=attributes.getValue("id");
        sort=(attributes.getValue("sort")!=null?attributes.getValue("sort"):sort);
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(tag);
        output.addAttribute("sort",sort);
        path.marshall(output);
        codePath.marshall(output);
        libellePath.marshall(output);
        output.endTag(tag);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(id==null || id.length()==0) throw new InvalidXmlDefinition("//"+tag+"/@id is required ("+getParentAsNoeudModifiable().getConfigXPath()+")/"+tag);
        if(path!=null) path.validate();
        codePath.validate();
        libellePath.validate();
    }
    public XPathModel getCodePath() {
        return codePath;
    }
    public XPathModel getLibellePath() {
        return libellePath;
    }
    public XPathModel getPath() {
        return path;
    }
    public int getSortBy() {
        if("no".equals(sort)) return SORT_NO;
        else if("key".equals(sort)) return SORT_KEY;
        else return SORT_LIB;
    }
    @Override
    public RecherchePaireModel clone() {
        RecherchePaireModel rpm = new RecherchePaireModel(getQName());
        rpm.id = this.id;
        rpm.sort=sort;
        try {
            if(path!=null)
                rpm.addChild(this.path.clone(), DocumentsMapping.PATH);
        } catch (Throwable t) {
            logger.error("clone().path",t);
        }
        try {
            rpm.addChild(this.codePath.clone(), DocumentsMapping.CODE_PATH);
        } catch (Throwable t) {
            logger.error("clone().codePath",t);
        }
        try {
            rpm.addChild(this.libellePath.clone(), DocumentsMapping.LIBELLE_PATH);
        } catch (Throwable t) {
            logger.error("clone().libellePath",t);
        }
        return rpm;
    }
    
    @Override
    public void modifyAttr(String attrName, String value) {
    }
    
    @Override
    public void modifyAttrs(Attributes attrs) {
        try {
            getAttributes(new XmlAttributes(attrs));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise & jour des attributs : "+e);
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
        if("path".equals(tagName)) {
            return path;
        } else if("code-path".equals(tagName)) {
            return codePath;
        } else if("libelle-path".equals(tagName)) {
            return libellePath;
        } else {
            return null;
        }
    }
    @Override
    public String[] getChildIdAttrName(String childTagName) {
        return null;
    }
    @Override
    public String getIdValue() { return null; }
    @Override
    public void resetCharData() {
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    private String configXPath = null;
    @Override
    public String getConfigXPath() {
        if(configXPath==null) {
            if(getParentAsNoeudModifiable()!=null) configXPath = getParentAsNoeudModifiable().getConfigXPath();
            else configXPath="";
            configXPath+="/"+tag+"[@id='"+getId()+"']";
        }
        return configXPath;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
//        QName name = new QName(uri,localName);
//        if(DocumentsMapping.PATH.equals(name)) {
//            return path;
//        } else if(DocumentsMapping.CODE_PATH.equals(name)) {
//            return codePath;
//        } else if(DocumentsMapping.LIBELLE_PATH.equals(name)) {
//            return libellePath;
//        }
        return null;
    }

    @Override
    public QName getQName() { return qn; }
    @SuppressWarnings("unchecked")
    public Collection<Pair> getNewRecipient() {
        int sortBy = getSortBy();
        if(sortBy==SORT_NO) return new Vector<Pair>();
        Comparator<Pair> cp;
        if(sortBy==SORT_KEY) {
            cp = new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    Pair p1=(Pair)o1;
                    Pair p2=(Pair)o2;
                    return p1.key.compareTo(p2.key);
                }
            };
        } else {
            cp = new Comparator() {
                @Override
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
    public void prepareForUnload() {
        _NMParent = null;
        if(codePath!=null) codePath.prepareForUnload();
        if(libellePath!=null) libellePath.prepareForUnload();
        if(path!=null) path.prepareForUnload();
    }
}
