/*
 * Copyright
 *   2008 axYus - www.axyus.com
 *   2008 C.Marchand - christophe.marchand@axyus.com
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
package fr.gouv.finances.dgfip.utils.xml.transform;

import java.io.File;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.Logger;

/**
 *
 * @author chm
 */
public class CustomURIResolver implements URIResolver {
    private static final Logger logger = Logger.getLogger(CustomURIResolver.class);
        private URIResolver _parent;
        private String _base;

        public CustomURIResolver(URIResolver parent,String base) {
            super();
            this._parent=parent;
            this._base=base;
        }

    @Override
        public Source resolve(String href, String base) throws TransformerException {
            Source src = null;
            if(href.startsWith(".")) {
                File f = new File(new File(_base),href);
                if(f.exists()) src = new StreamSource(f);
            }
            if(src==null) {
                src = _parent.resolve(href, _base);
            }
            logger.debug("resolving "+href+" from "+base+" -> "+src);
            return src;
        }

}
