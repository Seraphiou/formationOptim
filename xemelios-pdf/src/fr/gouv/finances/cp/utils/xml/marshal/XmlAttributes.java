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

import java.text.SimpleDateFormat;
import java.util.Date;
import org.xml.sax.SAXException;

/**
 * This class wraps a org.xml.sax.Attributes object and defines helper methods;
 * <p>
 * License : LGPL
 * 
 * @author: Christophe MARCHAND
 */
public class XmlAttributes {
    private org.xml.sax.Attributes attrs = null;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dt_sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

    /**
     * Construct an XmlAttributes based on this <code>attrs</code> attributes.
     */
    public XmlAttributes(org.xml.sax.Attributes attrs) {
        super();
        this.attrs = attrs;
    }

    /**
     * Returns the attributes.
     * 
     * @return org.xml.sax.Attributes
     */
    public org.xml.sax.Attributes getAttributes() {
        return attrs;
    }

    /**
     * Constructs and returns a <code>BigDecimal</code> parsed from a
     * specified attribute value.
     * 
     * @return java.math.BigDecimal
     * @param attName
     *            java.lang.String
     */
    public java.math.BigDecimal getBigDecimalValue(String attName) {
        return new java.math.BigDecimal(attrs.getValue(attName));
    }

    /**
     * Returns the boolean value of the specified attribute.
     * 
     * @return boolean
     * @param attName The attribute to read
     */
    public boolean getBooleanValue(String attName) throws SAXException {
    	// chm:2006-04-21
    	// as xs:boolean may have yes / true/ 1, change this
    	String sTmp = attrs.getValue(attName);
    	if(sTmp==null) return false;
    	sTmp = "|"+sTmp.toUpperCase()+"|";
    	if("|TRUE|YES|1|".contains(sTmp)) return true;
    	else if("|FALSE|NO|0|".contains(sTmp)) return false;
    	else throw new SAXException("invalid boolean value for attribute "+attName+"=\""+sTmp+"\"");
    }

    /**
     * Returns the <code>int</code> value
     * 
     * @return int
     * @param attName
     *            java.lang.String
     */
    public int getIntValue(String attName) {
        return Integer.parseInt(attrs.getValue(attName));
    }

    /**
     * Returns the <code>long</code> value of the specified attribute.
     * 
     * @return long
     * @param attName
     *            java.lang.String
     */
    public long getLongValue(String attName) {
        return Long.parseLong(attrs.getValue(attName));
    }

    /**
     * Returns the value of the specified attribute.
     * 
     * @return java.lang.String
     * @param attName
     *            java.lang.String
     */
    public String getValue(String attName) {
        return attrs.getValue(attName);
    }
    public char getCharValue(String attName) {
        String s = getValue(attName);
        if(s!=null && s.length()>0) return s.charAt(0);
        return '\u0000';
    }

    /**
     * Returns the value of the specified attribute.
     * 
     * @return java.lang.String
     * @param uri
     *            java.lang.String
     * @param localName
     *            java.lang.String
     */
    public String getValue(String uri, String localName) {
        return attrs.getValue(uri, localName);
    }
    /**
     * Returns the first char of the specified attribute, or <tt>\u0000</tt> if specified is empty or null 
     * @param uri
     * @param localName
     * @return
     */
    public char getCharValue(String uri, String localName) {
        String s = getValue(uri,localName);
        if(s!=null && s.length()>0) return s.charAt(0);
        return '\u0000';
    }

    
    public Date getDateValue(String attName) {
        String s = getValue(attName);
        if(s==null) return null;
        try {
            return sdf.parse(s);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public Date getDateValue(String uri, String localName) {
        String s = getValue(uri, localName);
        if(s==null) return null;
        try {
            return sdf.parse(s);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Date getDateTimeValue(String attName) {
        String s = getValue(attName);
        if(s==null) return null;
        try {
            return dt_sdf.parse(s);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public Date getDateTimeValue(String uri, String localName) {
        String s = getValue(uri, localName);
        if(s==null) return null;
        try {
            return dt_sdf.parse(s);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
