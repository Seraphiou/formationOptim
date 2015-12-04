/*
 * Copyright
 *   2007 axYus - www.axyus.com
 *   2007 N.LeCorre - nicolas.lecorre@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.export;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;

public class ExportElementModel implements XmlMarshallable {
    private static final Logger logger = Logger.getLogger(ExportElementModel.class);
	
	public static final transient String TAG = "element";
        public static final transient QName QN = new QName(TAG);
	private String id, libelle;
	private Vector<ExportChampModel> champs;
	private Vector<ExportElementModel> enfants;
	private ExportElementModel parent;
	
	public ExportElementModel (QName tagName) {
		id = libelle = "";
		champs = new Vector<ExportChampModel>();
		enfants = new Vector<ExportElementModel>();
	}

	public void addCharacterData(String cData) throws SAXException {}

	public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
		if (ExportChampModel.QN.equals(tagName)) {
			champs.add((ExportChampModel)child);
		} else if (ExportElementModel.QN.equals(tagName)) {
			ExportElementModel e = (ExportElementModel)child;
			e.setParent(this);
			enfants.add(e);
		}
	}

	public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
		id = attributes.getValue("id");
		libelle = attributes.getValue("libelle");
		return this;
	}

	public void marshall(XmlOutputter output) {
		output.startTag("element");
		output.addAttribute("id", id);
		output.addAttribute("libelle", libelle);
		for (ExportChampModel ecm:champs) { ecm.marshall(output); }
		for (ExportElementModel eem:enfants) { eem.marshall(output); }
		output.endTag("element");
	}
	
    @Override
	public ExportElementModel clone () {
		ExportElementModel em = new ExportElementModel(QN);
		em.setId(id);
		em.setLibelle(libelle);
		em.setParent(parent.clone());
		for (ExportChampModel e:champs) {
			try { 
                em.addChild(e, ExportChampModel.QN);
            } catch (Exception ex) {
                logger.error("clone().champ",ex);
            }
		}
		for (ExportElementModel e:enfants) {
			try {
                em.addChild(e, ExportElementModel.QN);
            } catch (Exception ex) {
                logger.error("clone().enfant",ex);
            }
		}
		return em;
	}

	public void validate() throws InvalidXmlDefinition {}

	public Vector<ExportElementModel> getEnfants() {
		return enfants;
	}

	public void setEnfants(Vector<ExportElementModel> enfants) {
		this.enfants = enfants;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vector<ExportChampModel> getChamps() {
		return champs;
	}

	public void setChamps(Vector<ExportChampModel> champs) {
		this.champs = champs;
	}

	public void addEnfant (ExportElementModel enf) {
		if (this.enfants==null) this.enfants = new Vector<ExportElementModel>();
		this.enfants.add(enf);
	}
	public void addChamp (ExportChampModel enf) {
		if (this.champs==null) this.champs = new Vector<ExportChampModel>();
		this.champs.add(enf);
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	
    @Override
	public String toString () {
		return getId()+"-"+getLibelle();
	}
	
	public boolean hasChampSelected () {
		for (ExportChampModel ecm:champs) {
			if (ecm.isSelectionne()) return true;
		}
		return false;
	}
	
	public void selectChampsKey () {
		for (ExportChampModel ecm:champs) {
			if (ecm.isIdentifiant()) {
				ecm.setSelectionne(true);
			}
		}
	}
	public void selectParentChampsKey () {
		ExportElementModel p = parent;
		while (p!=null) {
			p.selectChampsKey();
			p = p.getParent();
		}
	}

	public ExportElementModel getParent() {
		return parent;
	}

	public void setParent(ExportElementModel parent) {
		this.parent = parent;
	}

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    public QName getQName() {
        return QN;
    }

	
}