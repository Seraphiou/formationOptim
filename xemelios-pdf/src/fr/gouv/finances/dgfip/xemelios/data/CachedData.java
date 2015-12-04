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

package fr.gouv.finances.dgfip.xemelios.data;

import java.util.Hashtable;

/**
 * Cet objet n'a d'existence que lors d'une recherche.
 * Il sert a mettre en cache les donn�es provenant d'un
 * XPath sur le document general.
 * Normalement, cette classe ne devrait plus etre utilisee
 * du fait de l'extension de DomPath a l'access aux parents
 * @author chm
 */
public class CachedData {
    
    private Hashtable<String,Hashtable<String,Object>> data;
    // first key is document ID, next is XPath
    
    public CachedData() {
        super();
        data = new Hashtable<String,Hashtable<String,Object>>();
    }

    public void setCachedData(String documentName,String path, Object value) {
        Hashtable<String,Object> docEntry = data.get(documentName);
        if(docEntry==null) {
            docEntry = new Hashtable<String,Object>();
            data.put(documentName,docEntry);
        }
        docEntry.put(path,value);
    }
    
    public Object getCachedData(String documentName, String path) {
        Hashtable<String,Object> docEntry = data.get(documentName);
        if(docEntry==null) return null;
        return docEntry.get(path);
    }
    /**
     * Empties all cached data
     */
    public void flush() {
    	for(Hashtable<String,Object> h:data.values()) {
    		h.clear();
    	}
    	data.clear();
    }
    public int getSize() {
    	int size = 0;
    	for(Hashtable<String,Object> h:data.values()) size += h.size();
    	return size;
    }
}
