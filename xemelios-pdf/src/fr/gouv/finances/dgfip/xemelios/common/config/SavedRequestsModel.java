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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;

/**
 * Represente les requetes enregistrees pour un etat donne
 * @author chm
 */
public class SavedRequestsModel implements XmlMarshallable {
    private static final Logger logger = Logger.getLogger(SavedRequestsModel.class);
    public static final transient String TAG = "saved-requests";
    public static final transient QName QN = new QName(TAG);
    
    private String elementId,etatId,documentId;
    private Hashtable<String,RechercheModel> requests;
    // utilisé pour le requêtes distribuées...
    private boolean unavailable = false;
    
    public SavedRequestsModel(QName tagName,ElementModel em) {
        this(tagName);
        this.elementId=em.getId();
        this.etatId=em.getParent().getId();
        this.documentId=em.getParent().getParent().getId();
    }
    public SavedRequestsModel(QName tagName) {
        super();
        requests = new Hashtable<String,RechercheModel>();
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(RechercheModel.QN.equals(tagName))
            add((RechercheModel)child);
    }
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        elementId = attributes.getValue("element-id");
        // for backward compatibility
        if(elementId==null) {
            // c'est l'ancienne version ou on stockait seulement l'etat
            elementId = attributes.getValue("etat-id");
            try {
                Vector<ElementModel> elements = Loader.getDocsInfos((String)null).getElementsById(elementId);
                if(elements.size()>0) {
                    etatId = elements.get(0).getParent().getId();
                    documentId = elements.get(0).getParent().getParent().getId();
                } else {
                    unavailable = true;
                }
            } catch(Exception ex) {
                logger.error("unexpected error at getAttributes(XmlAttributes)",ex);
            }
        } else {
            etatId = attributes.getValue("etat-id");
            documentId = attributes.getValue("document-id");
// transformation des anciens ID de document en nouveaux ID
            if("PES_ReleveCarteAchat".equals(documentId)) documentId = "carte-achat";
            else if("documentPaye".equals(documentId)) documentId = "cfg-paye";
            else if("compteGestion".equals(documentId)) documentId = "cg-colloc";
            else if("compteGestionEtat".equals(documentId)) documentId = "cg-etat";
            else if("DocEtatsFrais".equals(documentId)) documentId = "etat-frais";
            else if("DocEtatsFraisSPL".equals(documentId)) documentId = "etat-frais-spl";
            else if("DOC_FactureDepense".equals(documentId)) documentId = "facture-depense";
            else if("DOC_FactureRecette".equals(documentId)) documentId = "facture-recette";
            else if("PES_Aller".equals(documentId)) documentId = "pes-aller";
            else if("DOC_PES_Facture".equals(documentId)) documentId = "pes-facture";
            else if("DOC_Quittancement".equals(documentId)) documentId = "quittancement";
            else if("DocumentRapport".equals(documentId)) documentId = "rapport";
            else if("etatVersement".equals(documentId)) documentId = "versement";
        }
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {
        if(unavailable) return; // on l'ecrit pas si il est pourri
        output.startTag(TAG);
        output.addAttribute("document-id",documentId);
        output.addAttribute("etat-id",etatId);
        output.addAttribute("element-id",elementId);
        for(Enumeration<RechercheModel> enumer=requests.elements();enumer.hasMoreElements();) enumer.nextElement().marshall(output);
        output.endTag(TAG);
    }
    @Override
    public void validate() throws InvalidXmlDefinition {}
    @Override
    public Object clone() {
        SavedRequestsModel srm = new SavedRequestsModel(QN);
        for(Enumeration<String> enumer=requests.keys();enumer.hasMoreElements();) {
            String key = enumer.nextElement();
            RechercheModel rm = requests.get(key);
            try {
                srm.addChild(rm, RechercheModel.QN);
            } catch (Throwable t) {
                logger.error("clone().recherche",t);
            }
//            srm.requests.put(key,rm);
        }
        srm.documentId=documentId;
        srm.etatId=etatId;
        srm.elementId=elementId;
        srm.unavailable=unavailable;
        return srm;
    }
    public String getDocumentId() { return documentId; }
    public String getEtatId() { return elementId; }
    public String getElementId() { return elementId; }
    public void add(RechercheModel rm) {
        if(requests.containsKey(rm.getName()))
            requests.remove(rm.getName());
        requests.put(rm.getName(),rm);
    }
    public void delete(RechercheModel rm) {
        requests.remove(rm.getName());
    }

    public boolean isUnavailable() {
        return unavailable;
    }
    public Collection<RechercheModel> getRecherches() {
        return requests.values();
    }
    String getKey() { return documentId+"|"+etatId+"|"+elementId; }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return requests.get(atts.getValue("name"));
    }

    @Override
    public QName getQName() {
        return QN;
    }
    public void setUnWritable(String name) { 
        for(Enumeration<RechercheModel> enumer=requests.elements();enumer.hasMoreElements();) enumer.nextElement().setUnWritable(name);
    }
}
