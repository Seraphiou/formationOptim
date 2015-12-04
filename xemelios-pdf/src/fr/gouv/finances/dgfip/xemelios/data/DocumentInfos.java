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

package fr.gouv.finances.dgfip.xemelios.data;

import org.w3c.dom.Document;

/**
 *
 * @author chm
 */
public class DocumentInfos {
    private Document doc;
    private String docId, encoding;
    
    public DocumentInfos(String docId,Document doc) {
        this(docId, doc, null);
    }
    public DocumentInfos(String docId,Document doc, String encoding) {
        super();
        this.docId=docId;
        this.doc=doc;
        this.encoding=encoding;
    }

    public Document getDoc() {
        return doc;
    }

    public String getDocId() {
        return docId;
    }

    @Deprecated
    public String getEncoding() {
        return encoding;
    }
}
