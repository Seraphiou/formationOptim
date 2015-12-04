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

package fr.gouv.finances.dgfip.xemelios.export;

import java.util.HashMap;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshalParser;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import javax.xml.namespace.QName;

/**
 * Parser de definition de document
 * @author chm
 */
public class ConfigParser extends XmlMarshalParser {
	public ConfigParser() throws SAXException, ParserConfigurationException, FactoryConfigurationError {
		super(new Mapping(),true, FactoryProvider.getSaxParserFactory());
	}
	
	private static class Mapping extends HashMap<QName,Class> {
		/**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = 3617293411154145847L;

		public Mapping() {
			put (ExportModel.QN, ExportModel.class);
			put (ExportElementModel.QN, ExportElementModel.class);
			put (ExportChampModel.QN, ExportChampModel.class);
                        put(ConfigModel.QN, ConfigModel.class);
		}
	}
}
