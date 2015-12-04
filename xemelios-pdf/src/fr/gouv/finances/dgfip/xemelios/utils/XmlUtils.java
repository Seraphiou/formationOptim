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

package fr.gouv.finances.dgfip.xemelios.utils;

import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import sun.misc.Regexp;

/**
 * @author chm
 */
public class XmlUtils {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(XmlUtils.class);
    /**
     * Always returns localName
     * @param uri
     * @param localName
     * @param qName
     * @return
     */
    public static String getShortTagName(String uri, String localName, String qName) {
//    	if(uri!=null && uri.length()>0)
//    		logger.debug("uri="+uri+"\tlocalName="+localName+"\tqName="+qName);
        return localName;
    }
    public static QName getQName(String uri, String localName, String qName) {
    	return new QName(uri,localName,qName);
    }
    /**
     * Constructs and returns a XPath representation of this stack
     * @param stack
     * @return
     */
    public static String getPath(Stack<String> stack) {
        StringBuilder ret = new StringBuilder();
        for(String s:stack) {
            ret.append("/").append(s);
        }
        return ret.toString();
    }
    public static String getNoNamespacePath(Stack<QName> stack,NamespaceContext ctx) {
        StringBuilder ret = new StringBuilder();
        for(QName q:stack) {
        	if(q==null) throw new IllegalStateException("q is null");
            ret.append("/");
            ret.append(q.getLocalPart());
        }
        return ret.toString();
    }
    public static String getPath(Stack<QName> stack,NamespaceContext ctx) {
    	if(ctx==null) logger.fatal("NamespaceContext is null");
        StringBuilder ret = new StringBuilder();
        for(QName q:stack) {
        	if(q==null) throw new IllegalStateException("q is null");
            ret.append("/");
            String prefix = ctx.getPrefix(q.getNamespaceURI());
            if(prefix!=null && prefix.length()>0)
            	ret.append(prefix).append(":");
            ret.append(q.getLocalPart());
        }
        return ret.toString();
    }
    public static String getXmlDataSubstituteNode(Node node,String substituteWith, String substituteInWhat) {
        StringBuilder sb = new StringBuilder();
        switch(node.getNodeType()) {
            case Node.COMMENT_NODE:
            case Node.ENTITY_NODE:
            case Node.ENTITY_REFERENCE_NODE:
            case Node.NOTATION_NODE:
            case Node.PROCESSING_INSTRUCTION_NODE: break;
            case Node.DOCUMENT_NODE: 
            case Node.DOCUMENT_FRAGMENT_NODE:
            case Node.ELEMENT_NODE: {
                String nodeName = node.getNodeName();
                if(!substituteInWhat.equals(nodeName)) {
                    sb.append("<").append(nodeName);
	                StringBuilder attrs = new StringBuilder();
	                StringBuilder children = new StringBuilder();
	                NamedNodeMap nnm = node.getAttributes();
	                if(nnm!=null) {
	                    for(int i=0;i<nnm.getLength();i++) {
	                        Node attr = nnm.item(i);
	                        attrs.append(" ").append(getXmlDataSubstituteNode(attr,substituteWith,substituteInWhat));
	                    }
	                }
	                NodeList nl = node.getChildNodes();
	                if(nl!=null) {
	                    for(int i=0;i<nl.getLength();i++) {
	                        Node child = nl.item(i);
	                        if(child.getNodeType()==Node.ATTRIBUTE_NODE) {
	                            attrs.append(" ").append(getXmlDataSubstituteNode(child,substituteWith,substituteInWhat));
	                        } else {
	                            children.append(getXmlDataSubstituteNode(child,substituteWith,substituteInWhat));
	                        }
	                    }
	                }
	                sb.append(attrs.toString());
	                if(children.length()>0) {
	                    sb.append(">").append(children.toString()).append("</").append(nodeName).append(">");
	                } else {
	                    sb.append("/>");
	                }
                } else {
                    sb.append(substituteWith);
                }
                break;
            }
            case Node.ATTRIBUTE_NODE: {
                sb.append(node.getNodeName()).append("=\"").append(StringEscapeUtils.escapeXml(node.getNodeValue())).append("\"");
                break;
            }
            case Node.CDATA_SECTION_NODE: {
                sb.append("<![CDATA[").append(StringEscapeUtils.escapeXml(node.getNodeValue())).append("]]>");
            }
            case Node.TEXT_NODE: {
                sb.append(StringEscapeUtils.escapeXml(node.getNodeValue()));
            }
        }
        return sb.toString();
    }
    
    /**
     * For the moment, this method can only transform very simple XPath, like <tt>/n:foo/b:bar/@a</tt>
     * @param xpath
     * @param ns
     * @return
     */
    public static String normalizeNS(String xpath, NamespaceContext ns) {
    	StringTokenizer tokenizer = new StringTokenizer(xpath,"/",true);
    	StringBuilder sb = new StringBuilder();
    	while(tokenizer.hasMoreTokens()) {
    		String token = tokenizer.nextToken();
    		if(token.equals("/")) sb.append(token);
    		else {
    			boolean isAttribute = false;
    			if(token.startsWith("@")) {
    				token = token.substring(1);
    				isAttribute = true;
    			}
    			int pos = token.indexOf(':');
    			if(pos>=0) {
    				String prefix = token.substring(0,pos);
    				String localName = token.substring(pos+1);
    				String newPrefix = ns.getPrefix(ns.getNamespaceURI(prefix));
    				if(isAttribute && (newPrefix==null || newPrefix.length()==0)) newPrefix = prefix;
        			if(isAttribute) sb.append("@");
        			if(newPrefix.length()>0)
        				sb.append(newPrefix).append(":");
        			sb.append(localName);
    			} else {
    				if(!isAttribute) {
                                    String localName = token;
                                    if(XmlUtils.isValidNCName(localName)) {
                                        String uri = ns.getNamespaceURI("");
                                        if(uri!=null) {
                                            String newPrefix = ns.getPrefix(uri);
                                            if(newPrefix.length()>0) sb.append(newPrefix).append(":");
                                        }
    					sb.append(token);
                                    } else {
                                        sb.append(token);
                                    }
    				} else {
    					// attribute didn't had a namespace, let it like that
    					sb.append("@").append(token);
    				}
    			}
    		}
    	}
    	return sb.toString();
    }
    
    /**
     * This method transform XPath and delete prefix of namespaces
     * @param xpath
     * @return
     */
    public static String removePrefixNS(String xpath) {
    	return xpath.replaceAll(new Regexp("[/][a-z]*[:]").exp,"/");
    }
    
    public static String getStringRepresentation(DocumentFragment df) {
        StringBuilder sb = new StringBuilder();
        NodeList nl = df.getChildNodes();
        for(int i=0;i<nl.getLength();i++) {
            Node n = nl.item(i);
            sb.append(nodeToString(n));
        }
        return sb.toString();
    }
    public static String nodeToString(Node n) {
        StringBuilder sb = new StringBuilder();
        if(n instanceof Text) return n.getNodeValue().trim();
        else {
            NodeList nl = n.getChildNodes();
            for(int i=0;i<nl.getLength();i++) {
                Node child = nl.item(i);
                sb.append(nodeToString(child));
            }
            if(n.getNodeType()==Node.ELEMENT_NODE) {
                if("td".equalsIgnoreCase(n.getNodeName())) {
                    sb.append("\t");
                } else if("th".equalsIgnoreCase(n.getNodeName())) {
                    sb.append("\t");
                } else if("tr".equalsIgnoreCase(n.getNodeName())) {
                    sb.append("\n");
                } else if("br".equalsIgnoreCase(n.getNodeName())) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
    }

    public static boolean isValidNCName(String name) {
        boolean ret = true;
        for(char c:name.toCharArray()) {
            boolean isMinusc = ('a'<=c && c<='z');
            boolean isMajusc = ('A'<=c && c<='Z');
            boolean isDigit = ('0'<=c && c<='9');
            boolean isSpecialChar = (c=='.' || c=='-' || c=='_');
            ret = !(isMinusc || isMajusc || isDigit || isSpecialChar);
            if(!ret) break;
        }
        return ret;
    }

    public static void showDomInConsole(Document doc, String description) {
        try {
            javax.xml.transform.TransformerFactory tf = FactoryProvider.getTransformerFactory();
            javax.xml.transform.Transformer t = tf.newTransformer();

            javax.xml.transform.stream.StreamResult sr = new javax.xml.transform.stream.StreamResult(System.out);
            t.transform(new javax.xml.transform.dom.DOMSource(doc.getDocumentElement()), sr);
            t.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (Exception e) {
            logger.debug("Erreur lors de la visualisation du DOM du fichier asa!");
            e.printStackTrace();
        }
    }

}
