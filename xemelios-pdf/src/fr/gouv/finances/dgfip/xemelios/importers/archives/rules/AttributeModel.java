/*
 * Copyright
 *  2012 axYus - http://www.axyus.com
 *  2012 C.Marchand - christophe.marchand@axyus.com
 *
 * This file is part of XEMELIOS_NB.
 *
 * XEMELIOS_NB is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * XEMELIOS_NB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS_NB; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package fr.gouv.finances.dgfip.xemelios.importers.archives.rules;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import nu.xom.Attribute;
import nu.xom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Permet de modéliser un filtre sur un attribut supplémentaire
 * 
 * @author cmarchand
 */
public class AttributeModel implements XomDefinitionable {
	public static final QName QN = new QName(RulesParser.RULES_NS, "attribute");

	private String name, value;
	private boolean differs = false;
	private boolean unset = false;

	public AttributeModel(QName qn) {
		super();
	}

	public AttributeModel(Element el) {
		this(QN);
		name = el.getAttributeValue("name");
		if (name != null && name.length() == 0)
			name = null;
		value = el.getAttributeValue("value");
		if (value != null && value.length() == 0)
			value = null;
		if ("true".equals(el.getAttributeValue("differs")))
			differs = true;
		if ("true".equals(el.getAttributeValue("unset")))
			unset = true;
	}

	@Override
	public void addCharacterData(String cData) throws SAXException {
	}

	@Override
	public void addChild(XmlMarshallable child, QName tag) throws SAXException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
		name = attributes.getValue("name");
		value = attributes.getValue("value");
		differs = attributes.getBooleanValue("differs");
		unset = attributes.getBooleanValue("unset");
		return this;
	}

	@Override
	public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
		return null;
	}

	@Override
	public void marshall(XmlOutputter output) {
		output.startTag(QN);
		output.addAttribute("name", name);
		output.addAttribute("value", value);
		if (differs)
			output.addAttribute("differs", true);
		if (unset)
			output.addAttribute("unset", "true");
		output.endTag(QN);
	}

	@Override
	public void validate() throws InvalidXmlDefinition {
	}

	@Override
	public QName getQName() {
		return QN;
	}

	@Override
	public AttributeModel clone() {
		AttributeModel ret = new AttributeModel(QN);
		ret.name = name;
		ret.value = value;
		ret.differs = differs;
		ret.unset = unset;
		return ret;
	}

	/**
	 * Renvoie <tt>true</tt> si ce document satisfait les conditions sur cet
	 * attribut
	 * 
	 * @param document
	 * @return
	 */
	public boolean matches(Element document) {
		String documentValue = document.getAttributeValue(name);
		boolean	comp = (unset ? (documentValue == null) : value.equals(documentValue));

		if (differs)
			return !comp;
		else
			return comp;
	}

	@Override
	public Element getXomDefinition() {
		Element ret = new Element(QN.getLocalPart(), QN.getNamespaceURI());
		ret.addAttribute(new Attribute("name", name));
		if (value != null)
			ret.addAttribute(new Attribute("value", value));
		if (differs)
			ret.addAttribute(new Attribute("differs", "true"));
		if (unset)
			ret.addAttribute(new Attribute("unset", "true"));
		return ret;
	}

	public void setDiffers(boolean differs) {
		this.differs = differs;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
