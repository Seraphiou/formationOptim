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
package fr.gouv.finances.cp.utils.xml.marshal;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * This class is a Marshaller parser.
 * @author: Christophe MARCHAND
 */
public class XmlMarshalParser {
	
	/**
	 * The XmlUnMarshaller.
	 */
	 protected XmlUnMarshaller unMarshaller = null;
	 
	 /**
	  * The SAX parser.
	  */
	 SAXParser parser = null;


	/**
	 * Constructs a new validating XmlMarsalParser based on this <code>mapping</code>
	 * and instanciate everything needed to parse.
         * Ajouté pour compatibilité avec ToTEM, ne pas utiliser ailleurs
	 * @param mapping The mapping between XML tags and classes to instanciate.
         * @deprecated
	 */
         @Deprecated
	public XmlMarshalParser(java.util.HashMap mapping) throws SAXException, ParserConfigurationException, FactoryConfigurationError {
            this(mapping,true, SAXParserFactory.newInstance());
	}

        /**
	 * Constructs a new validating XmlMarsalParser based on this <code>mapping</code>
	 * and instanciate everything needed to parse.
	 * @param mapping The mapping between XML tags and classes to instanciate.
         * @param saxFactory The SAXParserFactory to use
	 */
	public XmlMarshalParser(java.util.HashMap mapping, SAXParserFactory saxFactory) throws SAXException, ParserConfigurationException, FactoryConfigurationError {
            this(mapping,true, saxFactory);
	}


	/**
	 * Constructs a new XmlMarsalParser based on this <code>mapping</code>
	 * and instanciate everything needed to parse.
	 * @param mapping The mapping between XML tags and classes to instanciate
	 * @param validating Wether the parser should be a validating one or not
         * @param saxFactory The SAXParserFactory to use
	 */
	public XmlMarshalParser(java.util.HashMap mapping, boolean validating, SAXParserFactory saxFactory) throws SAXException, ParserConfigurationException, FactoryConfigurationError {
		super();
		unMarshaller = new XmlUnMarshaller(mapping);
		parser = saxFactory.newSAXParser();
	}


	/**
	 * Return the constructed <code>XmlMarshallable</code> object.
	 * @return com.labodev.xml.XmlMarshallable
	 */
	public XmlMarshallable getMarshallable() {
		return unMarshaller.getMarshallable();
	}

        public void setInitialData(XmlMarshallable data) {
            unMarshaller.setDataToChange(data);
        }

	/**
	 * Parse the content of the file specified as XML using the inner
	 * <code>XmlUnMarshaller</code>.
	 * @param f java.io.File
	 * @exception java.io.IOException The exception description.
	 * @exception org.xml.sax.SAXException The exception description.
	 */
	public void parse(java.io.File f) throws java.io.IOException, org.xml.sax.SAXException {
		parser.parse(f,unMarshaller);
	}


	/**
	 * Parse the content of the file specified as XML using the inner
	 * <code>XmlUnMarshaller</code>.
	 * @param is java.io.InputStream
	 * @exception java.io.IOException The exception description.
	 * @exception org.xml.sax.SAXException The exception description.
	 */
	public void parse(java.io.InputStream is) throws java.io.IOException, org.xml.sax.SAXException {
		parser.parse(is,unMarshaller);
	}


	/**
	 * Parse the content of the file specified as XML using the inner
	 * <code>XmlUnMarshaller</code>.
	 * @param uri java.lang.String
	 * @exception java.io.IOException The exception description.
	 * @exception org.xml.sax.SAXException The exception description.
	 */
	public void parse(String uri) throws java.io.IOException, org.xml.sax.SAXException {
		parser.parse(uri,unMarshaller);
	}

	/**
	 * Parse the content of the file specified as XML using the inner
	 * <code>XmlUnMarshaller</code>.
	 * @param is org.xml.sax.InputSource
	 * @exception java.io.IOException The exception description.
	 * @exception org.xml.sax.SAXException The exception description.
	 */
	public void parse(org.xml.sax.InputSource is) throws java.io.IOException, org.xml.sax.SAXException {
		parser.parse(is,unMarshaller);
	}

}
