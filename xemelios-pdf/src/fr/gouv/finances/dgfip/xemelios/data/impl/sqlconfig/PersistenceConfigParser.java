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


import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshalParser;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;

public class PersistenceConfigParser extends XmlMarshalParser {
	private String baseDirectory = null;
	
	public PersistenceConfigParser(String baseDir) throws SAXException, ParserConfigurationException {
		super(new SqlConfigMapping(),true, FactoryProvider.getSaxParserFactory());
		baseDirectory = baseDir;
	}


}
