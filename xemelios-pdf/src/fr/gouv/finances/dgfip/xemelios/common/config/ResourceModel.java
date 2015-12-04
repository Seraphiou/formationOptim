/*
 * 
 * Copyright
 *  2009 axYus - www.axyus.com
 *  2009 Christophe Marchand <christophe.marchand@axyus.com>
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
import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author Christophe Marchand <christophe.marchand@axyus.com>
 */
public class ResourceModel implements XmlMarshallable {
    private ArrayList<XmlMarshallable> data;
    private String id;
    public static final transient String TAG ="resource";
    public static final transient QName QN = new QName(TAG);

    public ResourceModel(QName tagName) {
        super();
        data = new ArrayList<XmlMarshallable>();
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {}

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        data.add(child);
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id=attributes.getValue("id");
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        output.addAttribute("id", id);
        for(XmlMarshallable xml:data) xml.marshall(output);
        output.endTag(QN);
    }

    @Override
    public void validate() throws InvalidXmlDefinition {}

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public ResourceModel clone() {
        ResourceModel rm = new ResourceModel(QN);
        rm.id=this.id;
        for(XmlMarshallable xml:data) try { rm.addChild(xml, xml.getQName()); } catch(Throwable t) {}
        return rm;
    }

    public ArrayList<XmlMarshallable> getData() {
        return data;
    }

    public void setData(ArrayList<XmlMarshallable> data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
