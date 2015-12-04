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

package com.oxiane.formation.jaopt.pdf.utils;

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
    private static final DocumentBuilderFactory domFactory = new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();
    private static final SAXParserFactory saxParserFactory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
    private static final Configuration configuration = new net.sf.saxon.Configuration();
    private static final XPathFactory xpathFactory = new net.sf.saxon.xpath.XPathFactoryImpl(configuration);

    static {
        domFactory.setNamespaceAware(true);
        saxParserFactory.setNamespaceAware(true);
    }
    /**
     * Renvoie un DocumentBuilderFactory correctement initialis� pour Xemelios
     * @return
     */
    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        return domFactory;
    }
    /**
     * Renvoie un XPathFactory correctement initialis� pour Xemelios
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
    public static TransformerFactory getTransformerFactory(final Configuration config) {
        return new net.sf.saxon.TransformerFactoryImpl(config);
    }
    
    /**
     * Renvoie un TransformerFactory correctement initialisé, et utilisant la Configuration par défaut
     * @return 
     */
    public static TransformerFactory getTransformerFactory() {
        return getTransformerFactory(configuration);
    }

}
