/*
 * Copyright
 *  2011 axYus - http://www.axyus.com
 *  2011 C.Marchand - christophe.marchand@axyus.com
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

package fr.gouv.finances.dgfip.utils.xml;

import fr.gouv.finances.dgfip.utils.xml.xpath.FunctionResolver;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPathFactory;
import net.sf.saxon.Configuration;
import org.apache.log4j.Logger;

/**
 * Cette classe fournit les factory pour les traitements XML.
 * Il est formellement interdit d'instancier manuellement une factory, il faut
 * impérativement passer par cette classe
 *
 * @author cmarchand
 */
public class FactoryProvider {
    private static final transient Logger logger = Logger.getLogger(FactoryProvider.class);
    private static DocumentBuilderFactory domFactory = null;
    private static XPathFactory xpathFactory = null;
    private static SAXParserFactory saxParserFactory = null;
//    private static TransformerFactory transformerFactory = null;
    private static Configuration configuration = null;

    static {
        domFactory = new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();
        domFactory.setNamespaceAware(true);
        configuration = new net.sf.saxon.Configuration();
        try {
            new XemeliosFunctionsAdapter().register(configuration);
        } catch(Exception ex) {
            logger.error("while registring extension functions",ex);
        }
        xpathFactory = new net.sf.saxon.xpath.XPathFactoryImpl(configuration);
        xpathFactory.setXPathFunctionResolver(new FunctionResolver());
        saxParserFactory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
        saxParserFactory.setNamespaceAware(true);
//        transformerFactory = new net.sf.saxon.TransformerFactoryImpl(configuration);
    }
    /**
     * Renvoie un DocumentBuilderFactory correctement initialisé pour Xemelios
     * @return
     */
    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        return domFactory;
    }
    /**
     * Renvoie un XPathFactory correctement initialisé pour Xemelios
     * @return
     */
    public static XPathFactory getXPathFactory() {
        return xpathFactory;
    }
    /**
     * Renvoie un SAXParserFactory correctement initialisé pour Xemelios
     * @return
     */
    public static SAXParserFactory getSaxParserFactory() {
        return saxParserFactory;
    }
    /**
     * Renvoie un TransformerFactory correctement initialisé pour Xemelios
     * @return
     */
    public static TransformerFactory getTransformerFactory() {
        Configuration conf = new net.sf.saxon.Configuration();
        try {
            new XemeliosFunctionsAdapter().register(conf);
        } catch(Exception ex) {
            logger.error("while registring extension functions",ex);
        }
        return new net.sf.saxon.TransformerFactoryImpl(conf);
    }

    /**
     * Renvoie la configuration Saxon pour faire ce qu'on veut...
     * @return
     */
    public static Configuration getSaxonConfiguration() { return configuration; }
}
