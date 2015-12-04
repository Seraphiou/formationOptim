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

import javax.xml.namespace.QName;

/**
 * Base interface that each tag-associated class must implement.
 * <br><b>WARNING&nbsp;:</b>&nbsp;there is no language-way to force this,
 * but you <b>MUST</b> provide a constructor with this signature :
 * <br><center><code>public &lt;className&gt; (String tagName) {}</code></center>
 * <br>or instanciation will throw a <code>java.lang.NoSuchMethodException</code>.
 * <p>License : LGPL
 * @author Christophe MARCHAND
 */
public interface XmlMarshallable extends Cloneable {

    /**
     * Called by the <code>XmlUnMarshaller</code> to add <code>#CDATA</code> or <code>#PCDATA</code> data.
     * @param cData java.lang.String
     * @exception org.xml.sax.SAXException The exception description.
     */
    public void addCharacterData(String cData) throws org.xml.sax.SAXException;

    /**
     * Receive notification that a child is to be added to this element.
     * @param child The child to add.
     * @param tag The child's element QName.
     * @exception org.xml.sax.SAXException The exception description.
     */
    public void addChild(XmlMarshallable child, QName tag) throws org.xml.sax.SAXException;

    /**
     * This method is called by the <code>XmlUnMarshaller</code> to allow the model to load its attributes.
     * If the object to construct depends on attributes or anything you can imagine,
     * instanciate what's desired and returns it ; the returned object will be used
     * for next call. In other case, just add <code>return this;</code> to your code.
     * @param attributes com.labodev.xml.XmlAttributes
     * @return XmlMarshallable The resulted object.
     * @exception org.xml.sax.SAXException The exception description.
     */
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws org.xml.sax.SAXException;

    /**
     * Returns the child of this object that is to be modified by xml reading.
     * @param uri The namespace-uri of the child object name
     * @param localName The child localName
     * @param qName The child quailfied-name
     * @param atts The attributes defined in xml file for the child object
     * @return The child found or <code>null</code>
     */
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, org.xml.sax.Attributes atts);

    /**
     * Writes this element, its attributes and its child(ren).
     * @param output com.labodev.xml.XmlOutputter
     */
    public void marshall(XmlOutputter output);

    /**
     * Must throw an <code>InvalidXmlException</code> if an attribute or one of
     * its children is wrong.
     * @exception com.labodev.xml.marshal.InvalidXmlDefinition The exception description.
     */
    public void validate() throws InvalidXmlDefinition;

    public Object clone();

    public QName getQName();
}
