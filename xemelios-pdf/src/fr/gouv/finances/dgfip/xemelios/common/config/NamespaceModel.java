/*
 * Copyright
 *   2007 axYus - www.axyus.com
 *   2007 JP.Tessier - jean-philippe.tessier@axyus.com
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

public class NamespaceModel implements NoeudModifiable, NamespaceContext {
    Logger logger = Logger.getLogger(NamespaceModel.class);
    private NoeudModifiable _NMParent = null;
    
    public static final transient String TAG = "namespaces";
    public static final transient QName QN = new QName(TAG);
    private Hashtable<String,String> prefixToUri = null;
    private Hashtable<String,ArrayList<String>> uriToPrefix = null;
    private ArrayList<NSModel> mappings = null;
    private HashMap<String,NSModel> hNSModels;
    private final Object locker = new Object();
    
    public NamespaceModel(QName tagName) {
        super();
        prefixToUri = new Hashtable<String,String>();
        uriToPrefix = new Hashtable<String,ArrayList<String>>();
        mappings = new ArrayList<NSModel>();
        hNSModels = new HashMap<String,NSModel>();
        NSModel m = new NSModel(NSModel.QN);
        m.setPrefix("xem");
        m.setUri("http://admisource.gouv.fr/projects/xemelios/ext");
        try { 
            addChild(m, NSModel.QN);
        }catch(Throwable t) {
            logger.error("<init>.mapping",t);
        }
    }
    
    @Override
    public void addCharacterData(String cData) throws SAXException {}
    
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if(NSModel.QN.equals(tagName)) {
            NSModel ns = (NSModel)child;
            ns.setParentAsNoeudModifiable(this);
            hNSModels.put(ns.getPrefix(),ns);
            prefixToUri.put(ns.getPrefix(),ns.getUri());
            ArrayList<String> prefixes = uriToPrefix.get(ns.getUri());
            if(prefixes==null) {
                synchronized(locker) {
                    prefixes = new ArrayList<String>();
                    uriToPrefix.put(ns.getUri(),prefixes);
                }
            }
            prefixes.add(ns.getPrefix());
            mappings.add(ns);
        }
    }
    
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException { return this; }
    
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        for(String prefix:prefixToUri.keySet()) {
            output.startTag(NSModel.TAG);
            output.addAttribute("prefix",prefix);
            output.addAttribute("uri",prefixToUri.get(prefix));
            output.endTag(NSModel.TAG);
        }
        output.endTag(TAG);
    }
    
    @Override
    public void validate() throws InvalidXmlDefinition {
        if(mappings.size()==0) throw new InvalidXmlDefinition("<"+TAG+"> element must contain at least one <"+NSModel.TAG+"/> element ("+getConfigXPath()+").");
    }
    
    @Override
    public NamespaceModel clone() {
        NamespaceModel other = new NamespaceModel(QN);
        for(NSModel mapping:mappings) {
            try {
                other.addChild(mapping.clone(),NSModel.QN);
            } catch(Throwable t) {
                logger.error("clone().mapping",t);
            }
        }
        return other;
    }
    
    @Override
    public String getNamespaceURI(String prefix) {
        String ret = prefixToUri.get(prefix);
//		logger.debug("prefix:\""+ret+"\" -> "+ret);
        return ret;
    }
    
    @Override
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
//		logger.debug("ns:"+namespaceURI+" -> \""+ret+"\"");
        return ret;
    }
    
    @Override
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

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        for(NSModel ns:mappings) ns.prepareForUnload();
    }
    
    @SuppressWarnings("hiding")
    public static class StringIterator<String> implements Iterator {
        private final String data;
        private boolean answered = false;
        public StringIterator(String data) {
            this.data=data;
        }
        @Override
        public boolean hasNext() { return !answered; }
        @Override
        public String next() {
            if(answered) throw new NoSuchElementException();
            answered=true;
            return data;
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException("can not remove !");
        }
    }
    
    @Override
    public void modifyAttr(String attrName, String value) {
    }
    
    @Override
    public void modifyAttrs(Attributes attrs) {
        try {
            getAttributes(new XmlAttributes(attrs));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise � jour des attributs : "+e);
        }
    }
    
    @Override
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        this._NMParent = p;
    }
    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return this._NMParent;
    }
    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        if(NSModel.TAG.equals(tagName)) {
            return hNSModels.get(id);
        } else {
            return null;
        }
    }
    
    @Override
    public java.lang.String[] getChildIdAttrName(java.lang.String childTagName) {
        if(NSModel.TAG.equals(childTagName)) {
            return new String[]{"prefix"};
        } else {
            return null;
        }
    }
    @Override
    public String getIdValue() { return null; }
    
    @Override
    public void resetCharData() {
    }
    private String configXPath = null;
    @Override
    public String getConfigXPath() {
        if(configXPath==null) {
            if(getParentAsNoeudModifiable()!=null) configXPath = getParentAsNoeudModifiable().getConfigXPath();
            else configXPath="";
            configXPath+="/"+TAG;
        }
        return configXPath;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public QName getQName() {
        return QN;
    }
    public ArrayList<NSModel> getMappings() { return mappings; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(String p:prefixToUri.keySet()) {
            sb.append("xmlns").append((p.length()>0?":":"")).append(p).append("->").append(prefixToUri.get(p)).append("\n");
        }
        return sb.toString();
    }
}
