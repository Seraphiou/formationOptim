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

import java.util.Enumeration;
import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import java.util.Collection;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;

/**
 * Represente la configuration personnelle de l'utilisateur
 * @author chm
 */
public class PersonnalConfigurationModel implements XmlMarshallable {
    private static final Logger logger=Logger.getLogger(PersonnalConfigurationModel.class);
    public static final transient String TAG = "personnal-config";
    public static final transient QName QN = new QName(TAG);

    private Hashtable<String,SavedRequestsModel> savedRequests;
    
    public PersonnalConfigurationModel(QName tagName) {
        super();
        savedRequests = new Hashtable<String,SavedRequestsModel>();
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(SavedRequestsModel.QN.equals(tagName)) {
            SavedRequestsModel srm = (SavedRequestsModel)child;
            savedRequests.put(srm.getKey(),srm);
        }
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        for(Enumeration<SavedRequestsModel> enumer=savedRequests.elements();enumer.hasMoreElements();) {
            enumer.nextElement().marshall(output);
        }
        output.endTag(TAG);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {}
    @Override
    public Object clone() {
        return this;
    }
    public SavedRequestsModel getSavedRequests(ElementModel em) {
        String key = em.getKey();
        SavedRequestsModel srm = savedRequests.get(key);
        if(srm==null) {
            srm = new SavedRequestsModel(SavedRequestsModel.QN,em);
            savedRequests.put(em.getKey(),srm);
        }
        return srm;
    }
    public Collection<SavedRequestsModel> getAllSavedRequests() {
        return savedRequests.values();
    }
    
    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        String key = atts.getValue("documentId")+"|"+atts.getValue("etatId")+"|"+atts.getValue("elementId");
        return savedRequests.get(key);
    }

    @Override
    public QName getQName() {
        return QN;
    }
    
    public void setUnWritable(String name) {
        for(Enumeration<SavedRequestsModel> enumer=savedRequests.elements();enumer.hasMoreElements();) {
            enumer.nextElement().setUnWritable(name);
        }
    }
    public void merge(PersonnalConfigurationModel other) {
        for(Enumeration<SavedRequestsModel> enumer=other.savedRequests.elements();enumer.hasMoreElements();) {
            SavedRequestsModel sv = enumer.nextElement();
            SavedRequestsModel local = savedRequests.get(sv.getKey());
            if(local==null) {
                local = sv;
            } else {
                try {
                    if(sv!=null)
                        for(RechercheModel rm:sv.getRecherches())
                            local.addChild(rm, RechercheModel.QN);
                } catch(Throwable t) {
                    logger.error("merge(PersonnalConfigurationModel).recherche",t);
                }
            }
        }
    }
}
