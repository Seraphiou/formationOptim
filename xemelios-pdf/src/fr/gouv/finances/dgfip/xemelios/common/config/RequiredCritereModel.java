/*
 * Copyright
 *  2010 axYus - http://www.axyus.com
 *  2010 C.Marchand - christophe.marchand@axyus.com
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

package fr.gouv.finances.dgfip.xemelios.common.config;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import java.util.Hashtable;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Permet de modéliser un critère requis
 * @author cmarchand
 */
public class RequiredCritereModel implements NoeudModifiable {
    public static final transient QName QNAME = new QName("required-critere");
    private String refId;
    private SearchHelperModel parent;
    private Hashtable<String,ValueModel> values;

    public RequiredCritereModel(QName qn) {
        super();
        values = new Hashtable<String, ValueModel>();
    }
    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return parent;
    }

    @Override
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        this.parent = (SearchHelperModel)p;
    }

    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        return values.get(id);
    }

    @Override
    public void modifyAttr(String attrName, String value) { }

    @Override
    public void modifyAttrs(Attributes attrs) { }

    @Override
    public String[] getChildIdAttrName(String childTagName) { return null; }

    @Override
    public void resetCharData() { }

    @Override
    public String getIdValue() {
        return "ref-id";
    }

    @Override
    public String getConfigXPath() {
        return getParentAsNoeudModifiable().getConfigXPath().concat("/required-critere[@ref-id='").concat(refId).concat("']");
    }

    @Override
    public void addCharacterData(String cData) throws SAXException { }

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        ValueModel vm = (ValueModel)child;
        values.put(vm.getKey(),vm);
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        refId = attributes.getValue("ref-id");
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return values.get(atts.getValue("key"));
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QNAME);
        output.addAttribute("ref-id", refId);
        for(ValueModel vm:values.values()) vm.marshall(output);
        output.endTag(QNAME);
    }

    @Override
    public void validate() throws InvalidXmlDefinition { }

    @Override
    public QName getQName() {
        return QNAME;
    }

    @Override
    public Object clone() {
        RequiredCritereModel rcm = new RequiredCritereModel(QNAME);
        rcm.refId = this.refId;
        for(ValueModel vm:values.values()) rcm.values.put(vm.getKey(), vm.clone());
        return rcm;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Hashtable<String, ValueModel> getValues() {
        return values;
    }

    public void setValues(Hashtable<String, ValueModel> values) {
        this.values = values;
    }

    @Override
    public void prepareForUnload() {
        parent = null;
        for(ValueModel vm:values.values()) vm.prepareForUnload();
    }

}
