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

package fr.gouv.finances.dgfip.utils.xml.dompath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.gouv.finances.dgfip.utils.xml.InvalidPathExpressionException;
import fr.gouv.finances.dgfip.utils.xml.PathNotFoundException;

/**
 * DomPath est un objet permettant de parcourir un arbre DOM.
 * La syntaxe est basee sur du XPath extremement simplifie.
 * Les recherches s'appliquent a un org.w3c.dom.Element et
 * renvoient toujours une valeur de type java.lang.String
 *
 * 2009-11-10: modification de la gestion des namespace en cas de XPath (pour les conditions)
 * @author chm
 */
public class DomPath {
    private static final Logger logger = Logger.getLogger(DomPath.class);
    private Stepper[] path = null;
    private static XPathFactory xpf = XPathFactory.newInstance();
    private String pathExpression;

    public DomPath(String pathExpression,final NamespaceContext ns) throws InvalidPathExpressionException {
//        StringTokenizer tokizer = new StringTokenizer(pathExpression,"/",false);
        this.pathExpression = pathExpression;
        String[] tokens = splitXPathExpr(pathExpression);
        int length = tokens.length;
        if(length==0) throw new InvalidPathExpressionException("empty");
        path = new Stepper[length];
        int i=0;
        length--;
        for(String token:tokens) {
            //String token = tokizer.nextToken();
            path[i] = new Stepper(token,ns);
            if(i<length) {
                if(path[i].getType()==Stepper.TYPE_ATTRIBUTE) {
                    throw new InvalidPathExpressionException("only the last element can be an attribute: "+pathExpression);
                }
            } else {
                if(path[i].getType()==Stepper.TYPE_ELEMENT) {
                    if(!(path[i].elementName.equals("text()") || path[i].elementName.equals("name()")) ) {
                        throw new InvalidPathExpressionException("the last element must be an attribute or a text(): "+pathExpression);
                    }
                }
            }
            i++;
        }
    }
    
    public Object getValue(Node node,boolean catchException) throws PathNotFoundException {
        try {
	        Object o = node;
	        for(Stepper stepper:path) {
	            if(o!=null && o instanceof Node) o = stepper.step((Node)o);
	        }
	        return o==null?null:o.toString();
        } catch(PathNotFoundException pnfEx) {
            if(!catchException) throw pnfEx;
            return null;
        }
    }
    protected static String[] splitXPathExpr(String xpath) {
        ArrayList<String> ret = new ArrayList<String>();
        int bracketCount=0;
        StringBuffer buff = new StringBuffer();
        for(char c:xpath.toCharArray()) {
                if(c=='[') {
                        bracketCount++;
                }
                if(c==']') bracketCount--;
                if(c=='/') {
                        if(bracketCount==0) {
                                ret.add(buff.toString());
                                buff = new StringBuffer();
                        } else {
                                buff.append(c);
                        }
                } else {
                        buff.append(c);
                }
        }
        ret.add(buff.toString());
        String[] ar = new String[ret.size()];
        for(int i=0;i<ar.length;i++) ar[i] = ret.get(i);
        return ar;
    }

    private class Stepper {
        public static final int TYPE_ELEMENT = 1;
        public static final int TYPE_ATTRIBUTE = 2;
        
        private String elementName = null;
        private int elementRank = 0;
        private String condition = null;
        private int type = 0;
        
        private NamespaceContext ns;
        
        public Stepper(String pathElement, NamespaceContext ns) throws InvalidPathExpressionException {
        	super();
        	this.ns=ns;
            if(pathElement.startsWith("@")) {
                type = TYPE_ATTRIBUTE;
                elementName = pathElement.substring(1);
                if(elementName==null || elementName.length()==0) throw new InvalidPathExpressionException("attribute name can not be empty");
                else if(elementName.indexOf('*')>=0) throw new InvalidPathExpressionException("attribute name can not contain *");
            } else {
                type = TYPE_ELEMENT;
                String s = pathElement;
                if(s.indexOf('[')>=0) {
                    String inner = s.substring(s.indexOf('[')+1,s.indexOf(']'));
                    int rank = 1;
                    if(inner.length()>0) {
	                    try {
	                        rank = Integer.parseInt(inner);
	                    } catch(NumberFormatException nfEx) {
                                condition = inner;
	                        //throw new InvalidPathExpressionException(inner+" is not a valid rank");
	                    }
                    }
                    if(rank==0) {
                        throw new InvalidPathExpressionException("rank can not be lower than 1");
                    }
                    elementRank = rank-1;
                    s = s.substring(0,s.indexOf('['));
                }
                elementName = s;
                if(elementName==null || elementName.length()==0) throw new InvalidPathExpressionException("element name can not be empty");
                else if(elementName.indexOf('*')>=0) throw new InvalidPathExpressionException("element name can not contain *");
            }
        }
        public int getType() { return type; }
        
        public Object step(Node node) throws PathNotFoundException {
            QName qn = getQName(elementName,ns);
            if(type==TYPE_ELEMENT) {
                Element el = (Element)node;
                if("text()".equals(elementName)) {
                    return el.getTextContent();
                } else if("name()".equals(elementName)) {
                	return el.getNodeName();
                }
                Node ret = null;
                if("..".equals(qn.getLocalPart())) {
                	ret=node.getParentNode();
                } else {
                    if(condition==null) {
	                //NodeList lst = el.getElementsByTagNameNS(qn.getNamespaceURI(),qn.getLocalPart());
                        NodeList lst = el.getChildNodes();
	                int parsingRank=0;
	                for(int i=0;i<lst.getLength();i++) {
	                	Node n = lst.item(i);
                                if(n.getNodeType()==Node.ELEMENT_NODE) {
                                    if(n.getLocalName().equals(qn.getLocalPart()) && (((qn.getNamespaceURI()==null || "".equals(ns.getPrefix(qn.getNamespaceURI()))) && n.getNamespaceURI()==null) || n.getNamespaceURI().equals(qn.getNamespaceURI()))) {
                                            if(parsingRank==elementRank) {
                                                ret = n;
                                                break;
                                            } else parsingRank++;
                                    }
                                    if(ret!=null) break;
                                }
	                }
                    } else {
                        String prefix = null;
                        // on recherche un prefix non-null, le XPath n'aimant pas bien les prefixes vides...
                        for(Iterator<String> it=ns.getPrefixes(qn.getNamespaceURI());it.hasNext();) {
                            String s = it.next();
                            if(prefix==null) prefix = s;
                            else if(prefix.length()<s.length()) prefix = s;
                        }
// modification pour quand cela contient le bon prefix
                        String xpathExpr = (prefix!=null&&prefix.length()>0 ? prefix+":" :"") + qn.getLocalPart() + "[" + condition + "]";
                        try {
                            XPath xp = xpf.newXPath();
                            xp.setNamespaceContext(ns);
                            Object o = xp.evaluate(xpathExpr,el,XPathConstants.NODESET);
                            if(o instanceof NodeList) {
                                NodeList nl = (NodeList) o;
                                if(nl.getLength()>0)
                                    ret = nl.item(0);
//                            } else if(o instanceof List) {
//                                List l = (List) o;
//                                if(!l.isEmpty())
//                                    ret = (Node)(l.get(0));
                            }
                        } catch(XPathExpressionException xpEx) {
                            logger.error("while evaluating "+xpathExpr,xpEx);
                        }
                    }
                }
                if(ret==null) throw new PathNotFoundException(elementName+"{"+elementRank+"} not found under "+node.getNodeName()+". XPath was "+pathExpression);
                return ret;
            }
            NamedNodeMap map = node.getAttributes();
            if(map==null || map.getLength()==0) throw new PathNotFoundException("no "+elementName+" attribute in "+node.getNodeName());
            Node attr = map.getNamedItemNS(qn.getNamespaceURI(),qn.getLocalPart());
            if(attr==null) {
            	// try to get it in default namespace
            	attr=map.getNamedItem(qn.getLocalPart());
            }
            if(attr!=null) return attr.getNodeValue();
//            else {
//                for(int i=0;i<map.getLength();i++) {
//                    Node n = map.item(i);
//                }
//            }
            return null;
        }
        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(type==TYPE_ELEMENT?"ELEMENT ":"ATTRIBUTE ").append(elementName).append(" rank=").append(elementRank);
            return sb.toString();
        }
        private QName getQName(String spec, NamespaceContext ns) {
        	QName ret = null;
        	int pos = spec.indexOf(':');
        	if(pos>=0) {
        		String prefix = spec.substring(0,pos);
        		String localName = spec.substring(pos+1);
        		String uri = ns.getNamespaceURI(prefix);
        		ret = new QName(uri,localName,prefix);
        	} else {
        		if(getType()==TYPE_ELEMENT) {
	        		String uri = ns.getNamespaceURI("");
	        		ret = new QName(uri,spec);
        		} else {
        			// it's an attrib, no default namespace on attribs
        			ret = new QName(spec);
        		}
        	}
        	return ret;
        }
    }
}
