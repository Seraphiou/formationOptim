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
 * along with XEMELIOS; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package fr.gouv.finances.dgfip.xemelios.common.config;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Permet de modéliser l'aide en ligne associée à un élément.
 * @author cmarchand
 */
public class HelpModel implements XmlMarshallable {
    public static final QName QN = new QName("help");
    private QName qn;
    private StringBuilder sb;

    public HelpModel(QName qn) {
        super();
        this.qn=qn;
        sb = new StringBuilder();
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
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        output.addCharacterData(sb.toString());
        output.endTag(QN);
    }

    @Override
    public void validate() throws InvalidXmlDefinition {}

    @Override
    public QName getQName() {
        if(qn!=null) return qn;
        else return QN;
    }
    @Override
    public HelpModel clone() {
        return this;
    }
    public String getText() { return sb.toString(); }
}
