/*
 * Copyright
 *  2010 axYus - http://www.axyus.com
 *  2010 C.Marchand - christophe.marchand@axyus.com
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
 * along with XEMELIOS ; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package fr.gouv.finances.dgfip.xemelios.common.config;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author cmarchand
 */
public class SearchHelperParameterModel implements NoeudModifiable {
    public static final String CURRENT_COLLECTIVITE = "$current-collectivite";
    public static final String CURRENT_BUDGET = "$current-budget";
    private QName qn;
    private boolean editable=true;
    private NoeudModifiable parent;
    private StringBuilder sb;

    public SearchHelperParameterModel(QName qn) {
        super();
        this.qn=qn;
        sb = new StringBuilder();
    }

    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return parent;
    }

    @Override
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        this.parent=p;
    }

    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void modifyAttr(String attrName, String value) {
        if("editable".equals(attrName)) editable=Boolean.parseBoolean(value);
    }

    @Override
    public void modifyAttrs(Attributes attrs) { }

    @Override
    public String[] getChildIdAttrName(String childTagName) {
        return null;
    }

    @Override
    public void resetCharData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getIdValue() {
        return qn.getLocalPart();
    }

    @Override
    public String getConfigXPath() {
        return getParentAsNoeudModifiable().getConfigXPath().concat("/").concat(qn.getLocalPart());
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
        sb.append(cData);
    }

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        String sTmp = attributes.getValue("editable");
        if(sTmp!=null && sTmp.length()>0) {
            try {
                editable = Boolean.parseBoolean(sTmp);
            } catch(Throwable ignore) {}
        }
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public void marshall(XmlOutputter output) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        // TODO
    }

    @Override
    public QName getQName() {
        return qn;
    }
    @Override
    public SearchHelperParameterModel clone() {
        SearchHelperParameterModel other = new SearchHelperParameterModel(getQName());
        other.editable=editable;
        other.sb.append(getValue());
        return other;
    }
    public String getValue() {
        return sb.toString();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public void prepareForUnload() {
        parent = null;
    }
    
}
