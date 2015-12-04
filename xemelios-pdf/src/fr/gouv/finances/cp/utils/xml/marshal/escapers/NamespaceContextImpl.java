/*
 * 
 * Copyright
 *  2009 axYus - www.axyus.com
 *  2009 Christophe Marchand <christophe.marchand@axyus.com>
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
 * 
 */

package fr.gouv.finances.cp.utils.xml.marshal.escapers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.XMLConstants;

/**
 *
 * @author Christophe Marchand <christophe.marchand@axyus.com>
 */
public class NamespaceContextImpl {
    private Hashtable<String,String> prefixToUri = null;
    private Hashtable<String,ArrayList<String>> uriToPrefix = null;
    private ArrayList<NsMapping> mappings = null;

    /** Creates a new instance of NamespaceContextImpl */
    public NamespaceContextImpl() {
        super();
        prefixToUri = new Hashtable<String,String>();
        uriToPrefix = new Hashtable<String,ArrayList<String>>();
        mappings = new ArrayList<NsMapping>();
    }
    public void addMapping(String prefix, String uri) {
        if(prefix==null || uri==null) return;
        prefixToUri.put(prefix,uri);
        ArrayList<String> prefixes = uriToPrefix.get(uri);
        if(prefixes==null) {
            prefixes = new ArrayList<String>();
            uriToPrefix.put(uri,prefixes);
        }
        prefixes.add(prefix);
        Collections.sort(prefixes);
        mappings.add(new NsMapping(prefix,uri));
    }
    public void removeMapping(String prefix) {
        String uri = prefixToUri.remove(prefix);
        if(uri!=null) {
        	ArrayList<String> prefixes = uriToPrefix.get(uri);
            prefixes.remove(prefix);
            mappings.remove(prefix);
        }
    }

    public String getNamespaceURI(String prefix) {
        String ret = prefixToUri.get(prefix);
        return ret;
    }

    public String getPrefix(String namespaceURI) {
        String ret = null;
        if(namespaceURI==null) throw new IllegalArgumentException("namespaceURI can not be null");
        else if(XMLConstants.XML_NS_PREFIX.equals(namespaceURI)) {
            ret = XMLConstants.XML_NS_URI;
        } else if(XMLConstants.XMLNS_ATTRIBUTE.equals(namespaceURI)) {
            ret = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        }
        ArrayList<String> prefixes = uriToPrefix.get(namespaceURI);
        if(prefixes!=null) {
            ret = prefixes.get(0);
        }
        return ret;
    }

    public Iterator getPrefixes(String namespaceURI) {
        if(namespaceURI==null) throw new IllegalArgumentException("namespaceURI must not be null");
        ArrayList<String> prefixes = uriToPrefix.get(namespaceURI);
        if(prefixes!=null) {
            return prefixes.iterator();
        } else if(XMLConstants.XML_NS_PREFIX.equals(namespaceURI)) {
            return new StringIterator<String>(XMLConstants.XML_NS_URI);
        } else if(XMLConstants.XMLNS_ATTRIBUTE.equals(namespaceURI)) {
            return new StringIterator<String>(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        }
        return null;
    }
    public ArrayList<NsMapping> getMappings() {
    	return mappings;
    }

    public static class NsMapping {
        String prefix, uri;
        boolean written = false;
        public NsMapping(String prefix,String uri) {
            super();
            this.prefix = prefix;
            this.uri = uri;
        }
        public boolean isWritten() { return written; }
        public void setWritten() { written = true; }
        public boolean equals(Object o) {
            if(o instanceof NsMapping) {
                NsMapping other = (NsMapping)o;
                return other.prefix.equals(this.prefix);
            } else if(o instanceof String) {
                return prefix.equals((String)o);
            }
            return false;
        }
        public String getPrefix() { return prefix; }
        public String getUri() { return uri; }
    }
    @SuppressWarnings("hiding")
    private class StringIterator<String> implements Iterator {
        private final String data;
        private boolean answered = false;
        public StringIterator(String data) {
            this.data=data;
        }
        public boolean hasNext() { return !answered; }
        public String next() {
            if(answered) throw new NoSuchElementException();
            answered=true;
            return data;
        }
        public void remove() {
            throw new UnsupportedOperationException("can not remove !");
        }
    }

}
