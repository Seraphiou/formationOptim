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

import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshalParser;
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 * Le parser qui permet de modéliser tout cela
 * @author cmarchand
 */
public class RulesParser extends XmlMarshalParser {
    public static final transient String RULES_NS = "http://xemelios.org/schemas#import-rules";
    private static final HashMap<QName, Class> MAPPING = new HashMap<QName, Class>();
    static {
        MAPPING.put(RulesModel.QN, RulesModel.class);
        MAPPING.put(ActionsModel.QN, ActionsModel.class);
        MAPPING.put(DeleteModel.QN, DeleteModel.class);
        MAPPING.put(FilterModel.QN_EXCLUDE, FilterModel.class);
        MAPPING.put(FilterModel.QN_INCLUDE, FilterModel.class);
        MAPPING.put(PredicatModel.QN, PredicatModel.class);
        MAPPING.put(SectionModel.QN, SectionModel.class);
        MAPPING.put(AttributeModel.QN, AttributeModel.class);
        MAPPING.put(ImportModel.QN, ImportModel.class);
    }
    public RulesParser(SAXParserFactory saxFactory) throws SAXException, ParserConfigurationException {
        super(MAPPING, saxFactory);
    }

}
