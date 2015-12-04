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

package fr.gouv.finances.dgfip.xemelios.common.config;

import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import java.util.Iterator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;


/**
 * This class represents a TPJRef object.
 * Schema definition is available at <a href="http://admisource.gouv.fr/frs/?group_id=9">admisource</a>
 * @author chm
 *
 */
public class PJRefInfo {
    // Warning: these constants are copied in fr.gouv.finances.dgfip.xemelios.web.gwt.client.widget.search.PjRefWidget
    // mind to modify this class if theses constants are modified
	public static final transient String SUPPORT_ONLINE = "00";
	public static final transient String SUPPORT_LOCAL1 = "01";
	public static final transient String SUPPORT_PAPIER = "02";
	public static final transient String SUPPORT_CD_DVD = "03";
        public static final transient String SUPPORT_LOCAL2 = "10";
        
	public static XPathFactory xpf = null;
	static {
//		try {
//			xpf = XPathFactory.newInstance(XPathFactory.DEFAULT_OBJECT_MODEL_URI);
//		} catch(Throwable t) {
//			xpf = XPathFactory.newInstance();
//		}
//	    xpf.setXPathFunctionResolver(new FunctionResolver());
            xpf = FactoryProvider.getXPathFactory();
	}
	
	private String support, id, nom;

	public PJRefInfo(DocumentModel dm, Element el) {
		super();
		XPath xp = xpf.newXPath();
		xp.setNamespaceContext(dm.getNamespaces());
                String prefix = null;
                Iterator<String> it = dm.getNamespaces().getPrefixes(dm.getPjNamespaceUri());
                while(it.hasNext()) {
                    String s = it.next();
                    if(s.length()>0) {
                        prefix = s;
                        break;
                    }
                }
                if(prefix!=null) prefix = prefix+":";
		try {
			support = xp.evaluate(prefix+"Support/@V",el);
			id = xp.evaluate(prefix+"IdUnique/@V",el);
			nom = xp.evaluate(prefix+"NomPJ/@V",el);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	public String getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}

	public String getSupport() {
		return support;
	}

}
