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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import fr.gouv.finances.dgfip.xemelios.ui.ImageResources;
import fr.gouv.finances.dgfip.xemelios.ui.ListDisplayable;
import javax.xml.namespace.QName;

/**
 * Represente l'enregistrement d'une recherche utilisateur
 * @author chm
 */
public class RechercheModel implements XmlMarshallable, Comparable, ListDisplayable {
    public static final transient String TAG = "recherche";
    public static final transient QName QN = new QName(TAG);
    private String name;
    private String id;
    private boolean writable = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    private Vector<CritereModel> criteres;
//    private Hashtable<String,CritereModel> hCriteres;

    public RechercheModel(QName tagName) {
        super();
        this.criteres = new Vector<CritereModel>();
//        hCriteres = new Hashtable<String,CritereModel>();
    }
    public RechercheModel(QName tagName,String name) {
        this(tagName);
        this.name=name;
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(CritereModel.QN.equals(tagName)) {
            CritereModel cm = (CritereModel)child;
            criteres.add(cm);
        }
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        name = attributes.getValue("name");
        id = attributes.getValue("id");
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        if(!writable) return;
        output.startTag(TAG);
        output.addAttribute("name",name);
        if(id!=null) output.addAttribute("id", id);
        for(CritereModel cm:criteres) cm.marshall(output);
        output.endTag(TAG);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {}
    @Override
    public RechercheModel clone() {
        RechercheModel rm = new RechercheModel(QN,name);
        for(CritereModel cm:this.criteres) rm.criteres.add(cm);
        return rm;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name=name; }
    @Override
    public String toString() { return getName(); }
    @Override
    public int compareTo(Object o) {
        if(o==this) return 0;
        if(o instanceof RechercheModel) {
            RechercheModel other = (RechercheModel)o;
            return getName().compareTo(other.getName());
        } else if(o instanceof PluginModel) {
            PluginModel other = (PluginModel)o;
            return getName().compareTo(other.getPluginTitle());
        }
        return 0;
    }
    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) { return compareTo(o)==0; }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    public Vector<CritereModel> getCriteres() {
        return criteres;
    }
    @Override
    public String getDisplayName() {
        return toString();
    }
    @Override
    public String getResource() {
        return ImageResources.LST_SEARCH_SAV;
    }
    @Override
    public boolean isDeletable() {
        return true;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        //return hCriteres.get(atts.getValue("id"));
        return null;
    }

    @Override
    public QName getQName() {
        return QN;
    }
    public void setUnWritable(String name) {
        writable = false;
        this.name = this.name+" ("+name+")";
    }
}
