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
package fr.gouv.finances.cp.utils.xml.marshal;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Stack;

import javax.xml.namespace.QName;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is the DefaultHandler used to construct a <code>XmlMarshallable</code> structure.
 * <p>License : LGPL
 * @author Christophe MARCHAND
 */
public class XmlUnMarshaller extends DefaultHandler {
    private static boolean DEBUG = false;

    /**
     * The mapping between tags and classes to instanciate.
     */
    private HashMap<QName, Class> mapping = null;
    /**
     * The constructed object.
     */
    private XmlMarshallable marshallable = null;
    private XmlMarshallable dataToChange = null;
    /**
     * The stack of <code>XmlMarshallable</code> objects.
     */
    private Stack<XmlMarshallable> stack = null;

    /**
     * Constructs a new <code>XmlUnMarshaller</code> on this
     * <code>mapping</code>.<br><b>WARNING&nbsp;:</b>&nbsp;
     * You must create your HashMap with correct initial size
     * and correct loadFactor to increase performance.
     * <br><b>WARNING&nbsp;:</b>&nbsp;<code>mapping</code> must
     * not be modified by a concurrent thread than the one where
     * this <code>XmlUnMarshaller</code> runs, or results are
     * unpredictible.
     */
    public XmlUnMarshaller(HashMap<QName, Class> mapping) {
        super();
        this.mapping = mapping;
        stack = new Stack<XmlMarshallable>();
    }

    public void setDataToChange(XmlMarshallable data) {
        this.dataToChange = data;
    }

    /**
     * Receive notification of character data inside an element.
     * @param ch char[]
     * @param start int
     * @param length int
     * @exception org.xml.sax.SAXException The exception description.
     */
    @Override
    public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
        XmlMarshallable m = stack.peek();
        String s = new String(ch, start, length);
        // FIXME: enhance this for performance
        if (s.trim().length() > 0) {
            m.addCharacterData(s);
        }
    }

    /**
     * Receive notification of the end of an element.
     * @param uri java.lang.String
     * @param localName java.lang.String
     * @param qName java.lang.String
     * @exception org.xml.sax.SAXException The exception description.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws org.xml.sax.SAXException {
        if(DEBUG) {
            System.out.println("endElement("+uri+","+localName+","+qName+")");
        }
        XmlMarshallable m = stack.pop();
        if (stack.empty()) {
            marshallable = m;
        } else {
            XmlMarshallable parent = stack.peek();
            parent.addChild(m, getQName(uri, localName, qName));
        }
    }

    /**
     * Returns the constructed object.
     * @return com.labodev.xml.XmlMarshallable
     */
    public XmlMarshallable getMarshallable() {
        return marshallable;
    }

    /**
     * Returns the name, qualified or not.
     * @return java.lang.String
     * @param uri java.lang.String
     * @param localName java.lang.String
     * @param qName java.lang.String
     */
    protected static QName getQName(String uri, String localName, String qName) {
        return new QName(uri, localName);
    }

    /**
     * Receive notification of the start of an element.
     * @param uri java.lang.String
     * @param localName java.lang.String
     * @param qName java.lang.String
     * @param atts org.xml.sax.Attributes
     * @exception org.xml.sax.SAXException The exception description.
     */
    @Override
    public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
        if(DEBUG) {
            System.out.println("startElement("+uri+","+localName+","+qName+")");
        }
        QName tag = getQName(uri, localName, qName);
        Class clazz = (Class) mapping.get(tag);
        if (clazz == null) {
            throw new SAXException("Unknown mapping for tag <" + tag + ">.");
        }
        try {
            XmlMarshallable m = null;
            XmlMarshallable current = (!stack.isEmpty() ? stack.peek() : null);
            if (current != null) {
                m = current.getChildToModify(uri, localName, qName, atts);
            } else {
                if (dataToChange != null && dataToChange.getQName().equals(new QName(uri, localName))) {
                    m = dataToChange;
                }
            }
            if (m == null) {
                Constructor c = clazz.getConstructor(new Class[]{QName.class});
                m = (XmlMarshallable) c.newInstance(new Object[]{tag});
            }
            XmlMarshallable m2 = m.getAttributes(new XmlAttributes(atts));
            m2 = (m2 == null) ? m : m2;
            stack.push(m2);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SAXException(ex);
        }
    }
    public HashMap<QName, Class> getMapping() { return mapping; }
    /**
     * Permet de forcer le un-marshaller à logger
     * @param debug
     */
    public static void setDebug(boolean debug) {
        XmlUnMarshaller.DEBUG = debug;
    }
}
