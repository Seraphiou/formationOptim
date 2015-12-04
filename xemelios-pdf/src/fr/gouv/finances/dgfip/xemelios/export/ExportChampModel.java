/*
 * Créé le 27 juin 07
 *
 * Pour changer le modèle de ce fichier généré, allez à :
 * Fenêtre&gt;Préférences&gt;Java&gt;Génération de code&gt;Code et commentaires
 */
package fr.gouv.finances.dgfip.xemelios.export;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

public class ExportChampModel implements XmlMarshallable {
	
	public static final transient String TAG = "champ";
        public static final transient QName QN = new QName(TAG);
	
	private String id, libelle;
	private boolean editable = false, selectionne = false, identifiant = false;
	
	public ExportChampModel (QName tagName) {
		id = libelle = "";
	}

    @Override
	public void addCharacterData(String cData) throws SAXException {}

    @Override
	public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
	}

    @Override
	public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
		id = attributes.getValue("id");
		libelle = attributes.getValue("libelle");
		editable = attributes.getBooleanValue("editable");
		selectionne = attributes.getBooleanValue("selectionne");
		identifiant = attributes.getBooleanValue("identifiant");
		return this;
	}

    @Override
	public void marshall(XmlOutputter output) {
		output.startTag("champ");
		output.addAttribute("id", id);
		output.addAttribute("libelle", libelle);
		output.addAttribute("editable", editable);
		output.addAttribute("selectionne", selectionne);
		output.addAttribute("identifiant", identifiant);
		output.endTag("champ");
	}
	
    @Override
	public ExportChampModel clone () {
		ExportChampModel em = new ExportChampModel(QN);
		em.setId(id);
		em.setLibelle(libelle);
		em.setEditable(editable);
		em.setSelectionne(selectionne);
		em.setIdentifiant(identifiant);
		return em;
	}

    @Override
	public void validate() throws InvalidXmlDefinition {}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
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

	public boolean isSelectionne() {
		return selectionne;
	}

	public void setSelectionne(boolean selectionne) {
		this.selectionne = selectionne;
	}
	
    @Override
	public String toString () {
		return getLibelle();
	}

	public boolean isIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(boolean identifiant) {
		this.identifiant = identifiant;
	}

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public QName getQName() {
        return QN;
    }
}