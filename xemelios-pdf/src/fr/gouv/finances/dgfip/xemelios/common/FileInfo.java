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

package fr.gouv.finances.dgfip.xemelios.common;

import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Attr;
//import org.w3c.dom.Element;

import fr.gouv.finances.dgfip.xemelios.common.config.DocumentsModel;
import fr.gouv.finances.dgfip.xemelios.common.config.EtatModel.Marker;

/**
 * Permet de stocker le nombre d'etats et d'elements contenus dans un document
 * 
 * @author chm
 */
public class FileInfo implements Cloneable {

    private Hashtable<Marker,Long> elementsCount;
    private long globalCount = 0L;
    private String message;
    private long debutImport = 0L;
    private long finImport = 0L;
    private long lastModify = 0L;
    private long warningCount = 0L;
    private Exception inProcessException = null;

    /**
     * 
     */
    public FileInfo() {
        super();
        elementsCount = new Hashtable<Marker,Long>();
    }
    public FileInfo(Exception ex) {
        this();
        inProcessException = ex;
    }
    public void merge(FileInfo other) {
        if(other==null) return;
        for(Enumeration<Marker> enumer=other.elementsCount.keys(); enumer.hasMoreElements();) {
            Marker marker = enumer.nextElement();
            Long otherCount = other.elementsCount.get(marker);
            Long thisCount = this.elementsCount.get(marker);
            if(thisCount!=null) otherCount = new Long(otherCount.longValue() + thisCount.longValue());
            this.elementsCount.put(marker,otherCount);            
            other.elementsCount.remove(marker);
        }
        if(!other.elementsCount.isEmpty()) this.elementsCount.putAll(other.elementsCount);
        globalCount+=other.getGlobalCount();        
        if(debutImport==0L) debutImport=other.debutImport;
        if(finImport<other.finImport) finImport=other.finImport;
        
        if(message!=null && other.getMessage()!=null) {
            this.message = this.message.concat(other.getMessage());
        } else {
            this.message=other.getMessage();
        }
        warningCount+=other.warningCount;
    }
    public void incElement(Marker marker) {
        Long ret = elementsCount.get(marker);
        if(ret==null) ret = new Long(0);
        elementsCount.put(marker,new Long(ret.longValue()+1));
        globalCount++;
    }
    public void setMessage(String message) { this.message=message; }
    public String getMessage() { return message; }
    
    public long getDebutImport() {
        return debutImport;
    }
    public void setDebutImport(long debutImport) {
        this.debutImport = debutImport;
    }
    public long getFinImport() {
        return finImport;
    }
    public void setFinImport(long finImport) {
        this.finImport = finImport;
    }

    public long getDurationImport() {
        return (finImport-debutImport);
    }
    
    public long getLastModify() {
        return lastModify;
    }
    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }
	
    public String toString(DocumentsModel dm) {
        StringBuffer ret = new StringBuffer();
        Hashtable<String,StringBuffer> docs = new Hashtable<String,StringBuffer>();
        for(Enumeration<Marker> enumer=elementsCount.keys(); enumer.hasMoreElements();) {
            Marker marker = enumer.nextElement();
            StringBuffer doc = docs.get(marker.getDocId());
            if(doc==null) {
                doc = new StringBuffer();
                String titre = marker.getDocId();
                try {
                    titre = dm.getDocumentById(marker.getDocId()).getTitre();
                } catch(NullPointerException npe) {
                    if(marker==null)
                        titre = "PJ";
                }
                doc.append(titre).append("\n");
                docs.put(marker==null?"PJ":marker.getDocId(), doc);
            }
            String titreEtat = marker.getEtatId()==null ? marker.getDocId() : marker.getEtatId();
            try {
                titreEtat = dm.getDocumentById(marker.getDocId()).getEtatById(marker.getEtatId()).getTitre();
            } catch(NullPointerException npe) {
                if(marker==null)
                    titreEtat = "PJ";
            }
            doc.append("   ").append(titreEtat).append("\t-> ").append(elementsCount.get(marker)).append(" éléments importés\n");
        }
        for(Enumeration<StringBuffer> buffs=docs.elements(); buffs.hasMoreElements();) {
            StringBuffer buff = buffs.nextElement();
            ret.append(buff);
        }
        if(warningCount>0) ret.append(warningCount).append(" avertissements rencontrés");
        if(ret.length()==0) ret.append("Aucune donnée importée.");
        ret.append("\n");
        if(message!=null) ret.append(getMessage());
        return ret.toString();
    }


    public org.w3c.dom.Element toXml(DocumentsModel dm, org.w3c.dom.Element root) {
        org.w3c.dom.Element doc = root.getOwnerDocument().createElementNS(root.getNamespaceURI(),"importation");
        int i = 0;
        for(Enumeration<Marker> enumer=elementsCount.keys(); enumer.hasMoreElements();) {
            Marker marker = enumer.nextElement();
            if(i==0){
                Attr documentId = root.getOwnerDocument().createAttributeNS(root.getNamespaceURI(), "document");
                documentId.setNodeValue(dm.getDocumentById(marker.getDocId()).getTitre());
                doc.setAttributeNode(documentId);
            }
            org.w3c.dom.Element element = root.getOwnerDocument().createElementNS(root.getNamespaceURI(),"element");
            
            Attr libelle = root.getOwnerDocument().createAttributeNS(root.getNamespaceURI(), "Libelle");
            Attr id = root.getOwnerDocument().createAttributeNS(root.getNamespaceURI(), "Id");
            Attr nb = root.getOwnerDocument().createAttributeNS(root.getNamespaceURI(), "Nb");            
            
            libelle.setNodeValue(dm.getDocumentById(marker.getDocId()).getEtatById(marker.getEtatId()).getTitre());
            id.setNodeValue(dm.getDocumentById(marker.getDocId()).getEtatById(marker.getEtatId()).getId());
            nb.setNodeValue(String.valueOf(elementsCount.get(marker)));
            
            element.setAttributeNode(libelle);
            element.setAttributeNode(id);
            element.setAttributeNode(nb);
            doc.appendChild(element);
            i++;
        }
        return doc;
    }
    public nu.xom.Element toXomXml(DocumentsModel dm) {
        nu.xom.Element doc = new nu.xom.Element("importation");
        int i=0;
        for(Enumeration<Marker> enumer = elementsCount.keys(); enumer.hasMoreElements();) {
            Marker marker = enumer.nextElement();
            if(i==0) {
                String titre = "<Document inconnu : "+marker.getDocId()+">";
                try {
                    titre = dm.getDocumentById(marker.getDocId()).getTitre();
                } catch(NullPointerException npe) {
                    if(marker==null)
                        titre = "PJ";
                }
                doc.addAttribute(new nu.xom.Attribute("document", titre));
            }
            nu.xom.Element element = new nu.xom.Element("element");
            String titreEtat = "<Etat inconnu : "+marker.getEtatId()+">";
            try {
                titreEtat = dm.getDocumentById(marker.getDocId()).getEtatById(marker.getEtatId()).getTitre();
            } catch(NullPointerException npe) {
                if(marker==null)
                    titreEtat = "PJ";
            }
            element.addAttribute(new nu.xom.Attribute("Libelle",titreEtat));
//            element.addAttribute(new nu.xom.Attribute("Id", dm.getDocumentById(marker.getDocId()).getEtatById(marker.getEtatId()).getId()));
            element.addAttribute(new nu.xom.Attribute("Id", marker.getEtatId()==null ? "PJ" : marker.getEtatId()));
            element.addAttribute(new nu.xom.Attribute("Nb", String.valueOf(elementsCount.get(marker))));
            doc.appendChild(element);
            i++;
        }
        return doc;
    }
	
    public long getGlobalCount() { return globalCount; }

    @Override
    @Deprecated
    public String toString() {
        return super.toString();
    }

    @Override
    public Object clone() {
        FileInfo o = null;
        try {
            // On récupère l'instance à renvoyer par l'appel de la
            // méthode super.clone()
            o = (FileInfo) super.clone();
        } catch(CloneNotSupportedException cnse) {
            // Ne devrait jamais arriver car nous implémentons
            // l'interface Cloneable
            cnse.printStackTrace(System.err);
        }
        o.elementsCount = (Hashtable<Marker,Long>) elementsCount.clone();
        o.warningCount = warningCount;
        // on renvoie le clone
        return o;
    }

    public long getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(long warningCount) {
        this.warningCount = warningCount;
    }

    public Exception getInProcessException() {
        return inProcessException;
    }
    
}
