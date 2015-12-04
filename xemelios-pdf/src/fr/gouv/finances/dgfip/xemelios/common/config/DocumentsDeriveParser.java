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

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import javax.xml.namespace.QName;

/**
 * @deprecated
 * @author nicolas.lecorre
 */
public class DocumentsDeriveParser extends DefaultHandler {
	private static Logger logger = Logger.getLogger(DocumentsDeriveParser.class);
	
	NoeudModifiable current, oldCurrent = null;
	boolean inConstruction = false;
	String closeConstructionOn=null;
	String baseDirectory;
	private HashMap<QName, Class> mapping ;
	
	// la liste des configuration d�j� lues et dont on peut d�river
	private DocumentsModel dmsReference = null;
	
	// le DocumentModel que l'on enrichi
	private DocumentModel dmEnrichi = null;
	
	public DocumentModel getDmEnrichi() {
		return dmEnrichi;
	}

	public DocumentsDeriveParser (DocumentsModel dm, String baseDir) {
		dmsReference = dm;
		baseDirectory=baseDir;
                mapping = new DocumentsMapping();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		/**
		 * Si on lit un d�but de balise documents-derives, on l'ignore
		 */
		if ("documents-derives".equals(localName)) return;
		
		/**
		 * Si on lit un d�but de balise document, il faut aller chercher la configuration dont on d�rive via l'attribut "extends"
		 */
		if ("document".equals(localName)) {
			if (dmsReference == null || dmsReference.getDocuments().size()==0) throw new SAXException ("Aucun document n'est disponible pour h�ritage.");
            String extendsId = attributes.getValue("extends");
            DocumentModel dmEtendu = dmsReference.getDocumentById(extendsId);
            if(dmEtendu==null) throw new SAXException("Configuration de "+extendsId+" introuvable");
			dmEnrichi = dmEtendu.clone();
			// on le prends comme point de d�part.
			current = dmEnrichi;
			// on mets � jour 
			current.modifyAttrs(attributes);
			dmEnrichi.setBaseDirectory(baseDirectory);
			dmEnrichi.setExtendedDocId(extendsId);
			/**
			 * ATTENTION
			 * Si la configuration dont on d�rive est une configuration d�riv�e, il se peut qu'elle ne soit pas encore charg�e.
			 * La d�rivation de plusieurs niveaux n'est pas prise en charge...
			 */
			
			
		/**
		 * Sinon on tente d'enrichir le mod�le trouv�
		 */	
		} else {
			
			/**
			 * On r�cup�re dans la configuration dont on d�rive, le noeud qui nous int�resse, c'est � dire le noeud qui doit �tre
			 * modifi� par la nouvelle configuration 
			 */
			
			NoeudModifiable child = null;
			// la clef du fils cherch�.
			// Elle est soit simplement la valeur d'un attribut,
			// soit la concat�nation de plusieurs
			String clef = "";
			Object ob = current.getChildIdAttrName(localName);
			if (ob!=null) {
				String[] clefs = (String[])ob;
				for (String c:clefs) {
					clef += attributes.getValue(c)+"|";
				}
				if (clef.endsWith("|")) clef = clef.substring(0, clef.length()-1);
			}
			child = current.getChildAsNoeudModifiable(localName,clef);
						
			/**
			 * Si ce noeud n'existe pas, il s'agit alors d'un noeud � cr�er
			 * Si on est en "construction", cela veux dire que l'on a commenc� � cr�er un nouveau fils qui a lui m�me des fils. 
			 * On se trouve par exemple � traiter une valeur d'un nouveau champs :
			 * 	<champ id="lr8" datatype="string" libelle="Montant Cumul annuel" affichable="true" exportable="true" identifiant="false">
	         *  	<valeur>BlocBordereau/MtCumulAnnuel/@V</valeur>
	         *  </champ>
			 * 
			 */
			if(child==null || inConstruction) {
				try {
					Class[] paramTypes = new Class[1]; paramTypes[0] = String.class;
					Class clazz = mapping.get (localName);
					Constructor ctr = clazz.getConstructor(paramTypes);
					Object[] params = new Object[1]; params[0] = localName;
					NoeudModifiable nouveau = (NoeudModifiable)ctr.newInstance(params);
					nouveau.getAttributes(new XmlAttributes(attributes));
					//current.addChild(nouveau, localName);
					current=nouveau;
					closeConstructionOn=localName;
					inConstruction = true;
				} catch (Exception e) {
					logger.error ("Erreur lors de la cr�ation du noeud ("+localName+") : ", e);
					throw new SAXException(e);
				}
				
			/**
			 * Sinon, on modifie simplement les attributs du noeud avec ceux �ventuellement red�finis et on se repositionne pour la suite
			 */
			} else {
				current = child;
				current.modifyAttrs(attributes);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		/**
		 * Si on lit une fin de balise documents-derives, on l'ignore
		 */
		if ("documents-derives".equals(localName)) return;
		
		/**
		 * Si l'on a une fin de document, alors on a fini d'enrichir dmEnrichi. Il faut l'ajouter � la liste des docs
		 */
		if ("document".equals(localName)) {
			dmsReference.addChild(dmEnrichi, DocumentModel.QN);
		} 
		/**
		 * On a trait� un noeud. On se repositionne pour la suite.
		 */
		else {
			current = current.getParentAsNoeudModifiable();
			if(inConstruction && localName.equals(closeConstructionOn)) inConstruction = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (current != null) {
			// Attention, on supprime l'ancienne valeur de ce tag; 
			// peut �tre existe-t-il des cas o� cela ne doit pas �tre fait
			if (!current.equals(oldCurrent)) {
				current.resetCharData();
				oldCurrent = current;
			}
			current.addCharacterData(new String (ch, start, length));
		}
		else
			super.characters(ch, start, length);
	} 
	
	
}
