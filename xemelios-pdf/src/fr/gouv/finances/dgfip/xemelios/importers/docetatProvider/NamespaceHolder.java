/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gouv.finances.dgfip.xemelios.importers.docetatProvider;

import fr.gouv.finances.dgfip.xemelios.common.config.NamespaceModel;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.xml.XMLConstants;

/**
 *
 * @author chm
 */
public class NamespaceHolder {

    private Hashtable<String, String> prefixToUri = null;
    private Hashtable<String, ArrayList<String>> uriToPrefix = null;

    public NamespaceHolder() {
        super();
        prefixToUri = new Hashtable<String, String>();
        uriToPrefix = new Hashtable<String, ArrayList<String>>();
    }

    public void addMapping(String prefix, String uri) {
        prefixToUri.put(prefix, uri);
        ArrayList<String> prefixes = uriToPrefix.get(uri);
        if (prefixes == null) {
            prefixes = new ArrayList<String>();
            uriToPrefix.put(uri, prefixes);
        }
        if (!prefixes.contains(prefix)) {
            prefixes.add(prefix);
        }
    }

    public void removeMapping(String prefix) {
        String uri = prefixToUri.remove(prefix);
        if (uri == null) {
            return;
        }
        ArrayList<String> prefixes = uriToPrefix.get(uri);
        if (prefixes != null) {
            prefixes.remove(prefix);
        }
    }

    public String getNamespaceURI(String prefix) {
        String ret = prefixToUri.get(prefix);
        return ret;
    }

    public String getPrefix(String namespaceURI) {
        String ret = null;
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI can not be null");
        } else if (XMLConstants.XML_NS_PREFIX.equals(namespaceURI)) {
            ret = XMLConstants.XML_NS_URI;
        } else if (XMLConstants.XMLNS_ATTRIBUTE.equals(namespaceURI)) {
            ret = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        }
        ArrayList<String> prefixes = uriToPrefix.get(namespaceURI);
        if (prefixes != null) {
            ret = prefixes.get(0);
            for (String s : prefixes) {
                if (s.length() < ret.length()) {
                    ret = s;
                }
            }
        }
        return ret;
    }

    public Iterator getPrefixes(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI must not be null");
        }
        ArrayList<String> prefixes = uriToPrefix.get(namespaceURI);
        if (prefixes != null) {
            return prefixes.iterator();
        } else if (XMLConstants.XML_NS_PREFIX.equals(namespaceURI)) {
            return new NamespaceModel.StringIterator<String>(XMLConstants.XML_NS_URI);
        } else if (XMLConstants.XMLNS_ATTRIBUTE.equals(namespaceURI)) {
            return new NamespaceModel.StringIterator<String>(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        }
        return null;
    }

    public Set<String> getAllPrefixes() {
        return prefixToUri.keySet();
    }
}
