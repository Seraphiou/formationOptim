/*
 * XemeliosComponentsParser.java
 *
 * Created on 9 novembre 2007, 10:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fr.gouv.finances.dgfip.xemelios.common.components;

import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshalParser;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author chm
 */
public class XemeliosComponentsParser extends XmlMarshalParser {
    
    /** Creates a new instance of XemeliosComponentsParser */
    public XemeliosComponentsParser() throws SAXException, ParserConfigurationException, FactoryConfigurationError {
        super(new Mapping(),true, FactoryProvider.getSaxParserFactory());
    }
    private static class Mapping extends HashMap<QName,Class> {
        
        public Mapping() {
            put(XemeliosComponentsModel.QN,XemeliosComponentsModel.class);
            put(ComponentModel.QN,ComponentModel.class);
        }
    }
    
}
