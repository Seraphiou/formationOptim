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

public class ExportModel implements XmlMarshallable {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ExportModel.class);
	
	public static final transient String TAG = "export";
        public static final transient QName QN = new QName(TAG);
	
	private Vector<ExportElementModel> enfants;
	private String id, libelle, code;
	
	public ExportModel (QName tagName) {
		id = libelle = code = "";
		enfants = new Vector<ExportElementModel>();
	}

	public void addCharacterData(String cData) throws SAXException {}

	public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
		enfants.add((ExportElementModel)child);
	}

	public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
		id = attributes.getValue("id");
		libelle = attributes.getValue("libelle");
		code = attributes.getValue("code");
		return this;
	}

	public void marshall(XmlOutputter output) {
		output.startTag("export");
		output.addAttribute("id", id);
		output.addAttribute("libelle", libelle);
		output.addAttribute("code", code);
		for (ExportElementModel em:enfants) { em.marshall(output); }
		output.endTag("export");
	}
	
    @Override
	public ExportModel clone () {
		ExportModel em = new ExportModel(QN);
		em.setId(id);
		em.setLibelle(libelle);
		em.setCode(code);
		for (ExportElementModel e:enfants) {
			try { 
                em.addChild(e, ExportElementModel.QN);
            } catch (Exception ex) {
                logger.error("clone().enfant", ex);
            }
		}
		return em;
	}

	public void validate() throws InvalidXmlDefinition {
        if(enfants.size()>1) throw new InvalidXmlDefinition(getId()+" has "+enfants.size()+" ExportElementModel");
    }

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

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void addEnfant (ExportElementModel enf) {
		enfants.add(enf);
	}

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    public QName getQName() {
        return QN;
    }
}