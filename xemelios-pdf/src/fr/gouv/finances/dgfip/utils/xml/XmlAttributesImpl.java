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

package fr.gouv.finances.dgfip.utils.xml;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 *
 * @author chm
 */
public class XmlAttributesImpl implements Attributes {
    private static final Logger logger = Logger.getLogger(XmlAttributesImpl.class);
    private ArrayList<Attribute> attrs;
    private Hashtable<String,String> prefixToUri;
    private Hashtable<String,TreeSet<String>> uriToPrefixes;
    private Hashtable<String,Integer> qNamePosition;
    
    /** Creates a new instance of XmlAttributesImpl */
    public XmlAttributesImpl() {
        attrs = new ArrayList<Attribute>();
        prefixToUri = new Hashtable<String,String>();
        uriToPrefixes = new Hashtable<String,TreeSet<String>>();
        qNamePosition = new Hashtable<String,Integer>();
    }

    private class Attribute {
        private String URI;
        private String localName;
        private String qName;
        private String type;
        private String value;
        public Attribute(String URI, String localName, String qName, String type, String value) {
            super();
            this.URI=URI;
            this.localName=localName;
            this.qName=qName;
            this.type=type;
            this.value=value;
        }
    }
    
    public void addAttribute(String URI, String localName, String qName, String type, String value) {
//        logger.debug("addAttribute("+URI+","+localName+","+qName+","+type+","+value+")");
        Attribute attr = new Attribute(URI,localName,qName,type,value);
        if(URI!=null && qName.indexOf(':')>=0) {
            String prefix = qName.substring(0,qName.indexOf(':'));
            prefixToUri.put(prefix,URI);
            TreeSet<String> prefixes = uriToPrefixes.get(URI);
            if(prefixes==null) {
                prefixes = new TreeSet<String>();
                uriToPrefixes.put(URI,prefixes);
            }
            prefixes.add(prefix);
        }
        String myQName = "{"+(URI==null?"":URI)+"}"+localName;
        qNamePosition.put(myQName,new Integer(attrs.size()));
        attrs.add(attr);
    }
    public int getLength() { return attrs.size(); }

    public String getValue(String qName) {
//        logger.debug("getValue("+qName+")");
        int pos = qName.indexOf(':');
        String myQName=null;
        if(pos>=0) {
            String prefix=qName.substring(0,pos);
            String localName=qName.substring(pos+1);
            String URI = prefixToUri.get(prefix);
            myQName = "{"+(URI==null?"":URI)+"}"+localName;
        } else myQName = "{}"+qName;
        Integer position = qNamePosition.get(myQName);
        if(position==null) return null;
        Attribute attr = attrs.get(position.intValue());
        return attr.value;
    }

    public String getType(String qName) {
//        logger.debug("getType("+qName+")");
        int pos = qName.indexOf(':');
        String myQName=null;
        if(pos>=0) {
            String prefix=qName.substring(0,pos);
            String localName=qName.substring(pos+1);
            String URI = prefixToUri.get(prefix);
            myQName = "{"+(URI==null?"":URI)+"}"+localName;
        } else myQName = "{}"+qName;
        Integer position = qNamePosition.get(myQName);
        if(position==null) return null;
        Attribute attr = attrs.get(position.intValue());
        return attr.type;
    }

    public int getIndex(String qName) {
//        logger.debug("getIndex("+qName+")");
        int pos = qName.indexOf(':');
        String myQName=null;
        if(pos>=0) {
            String prefix=qName.substring(0,pos);
            String localName=qName.substring(pos+1);
            String URI = prefixToUri.get(prefix);
            myQName = "{"+(URI==null?"":URI)+"}"+localName;
        } else myQName = "{}"+qName;
        Integer position = qNamePosition.get(myQName);
        if(position==null) return -1;
        return position.intValue();
    }

    public String getValue(int index) {
//        logger.debug("getValue("+index+")");
        Attribute attr = attrs.get(index);
        return attr.value;
    }

    public String getURI(int index) {
//        logger.debug("getURI("+index+")");
        Attribute attr = attrs.get(index);
        if(attr==null) return null;
        return attr.URI;
    }

    public String getType(int index) {
//        logger.debug("getType("+index+")");
        Attribute attr = attrs.get(index);
        if(attr==null) return null;
        return attr.type;
    }

    public String getQName(int index) {
//        logger.debug("getQName("+index+")");
        Attribute attr = attrs.get(index);
        if(attr==null) return null;
        return attr.qName;
    }

    public String getLocalName(int index) {
//        logger.debug("getLocalName("+index+")");
        Attribute attr = attrs.get(index);
        if(attr==null) return null;
        return attr.localName;
    }

    public String getValue(String uri, String localName) {
//        logger.debug("getValue("+uri+","+localName+")");
        String myQName = "{"+uri+"}"+localName;
        Integer position = qNamePosition.get(myQName);
        if(position==null) return null;
        Attribute attr = attrs.get(position.intValue());
        if(attr==null) return null;
        return attr.value;
    }

    public String getType(String uri, String localName) {
//        logger.debug("getType("+uri+","+localName+")");
        String myQName = "{"+uri+"}"+localName;
        Integer position = qNamePosition.get(myQName);
        if(position==null) return null;
        Attribute attr = attrs.get(position.intValue());
        if(attr==null) return null;
        return attr.type;
    }

    public int getIndex(String uri, String localName) {
//        logger.debug("getIndex("+uri+","+localName+")");
        String myQName = "{"+uri+"}"+localName;
        Integer position = qNamePosition.get(myQName);
        if(position==null) return -1;
        return position.intValue();
    }
    
}
