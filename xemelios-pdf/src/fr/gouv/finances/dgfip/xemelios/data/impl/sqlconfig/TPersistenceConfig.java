/*
 * Copyright 
 *   2007 axYus - www.axyus.com
 *   2007 C.Marchand - christophe.marchand@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.data.impl.sqlconfig;

import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

public class TPersistenceConfig implements XmlMarshallable {
    public static final transient String DATATYPE_STRING="string";
    public static final transient String DATATYPE_INTEGER="integer";
    public static final transient String DATATYPE_DECIMAL="decimal";
    public static final transient String DATATYPE_FLOAT="float";
    public static final transient String DATATYPE_DATE="date";
    public static final transient String DATATYPE_BOOLEAN="boolean";

    public static final String TAG = "persistence-config";
    public static final transient QName QN = new QName(TAG);
	
	private Hashtable<String,TLayer> layers;
	private String baseDirectory = null;

	public TPersistenceConfig(QName tagName) {
		super();
		layers = new Hashtable<String,TLayer>();
	}
	public void setBaseDirectory(String baseDir) {
		baseDirectory = baseDir;
		for(TLayer layer:layers.values()) layer.setBaseDirectory(baseDir);
	}

	public void addCharacterData(String cData) throws SAXException { }

	public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
		if(TLayer.QN.equals(tagName)) {
			TLayer layer = (TLayer)child;
			layer.setBaseDirectory(baseDirectory);
			layers.put(layer.getName(),layer);
		}
	}

	public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
		return this;
	}

	public void marshall(XmlOutputter output) { }

	public void validate() throws InvalidXmlDefinition { }

	public TLayer getLayer(String layerName) { return layers.get(layerName); }
	public TPersistenceConfig clone() { 
		TPersistenceConfig other = new TPersistenceConfig(QN);
		for (String s:layers.keySet()) {
			other.layers.put(s, this.layers.get(s).clone());
		}
		other.baseDirectory=baseDirectory;
		return other;
	}

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return layers.get(atts.getValue("name"));
    }

    public QName getQName() {
        return QN;
    }
}
