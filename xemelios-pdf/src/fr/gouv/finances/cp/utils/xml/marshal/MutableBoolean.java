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

/**
 * Mutable version of {@link #java.lang.Boolean}.
 * <p>This class is fully-compliant with {@link java.lang.Boolean}, except
 * {@link #valueOf(String)} method, which returns a <code>MutableBoolean</code>
 * instead of a {@link java.lang.Boolean}.
 * <p>License : LGPL
 * @author: Christophe MARCHAND
 */
public class MutableBoolean implements java.io.Serializable {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3904682695270414643L;
	private boolean value = false;

	/**
	 * MutableBoolean constructor comment.
	 */
	public MutableBoolean() {
		super();
	}

	/**
	 * Constructs a <code>MutableBoolean</code> from a {@link java.lang.Boolean} value.
	 * @param value The initial value.
	 */
	public MutableBoolean(Boolean value) {
		super();
		this.value = value.booleanValue();
	}

	/**
	 * Constructs a <code>MutableBoolean</code> from a {@link java.lang.String} value.
	 * @param value The initial value.
	 */
	public MutableBoolean(String value) {
		super();
		if (value != null) {
			this.value = value.equalsIgnoreCase("true");
		}
	}
	/**
	 * Constructs a <code>MutableBoolean</code> from a <code>boolean</code> value.
	 * @param value The initial value.
	 */
	public MutableBoolean(boolean value) {
		super();
		this.value = value;
	}
	/**
	 * Returns the value of this <code>MutableBoolean</code> object as a boolean primitive.
	 * @return boolean
	 */
	public boolean booleanValue() {
		return value;
	}
	/**
	 * Returns <code>true</code> if and only if the argument is <code>not null</code>
	 * and is a {@link java.lang.Boolean} or a <code>MutableBoolean</code> object that
	 * represents the same boolean value as this object.
	 * @return boolean
	 * @param o java.lang.Object
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else
			if (o instanceof java.lang.Boolean) {
				return (((java.lang.Boolean) o).booleanValue() == value);
			} else
				if (o instanceof MutableBoolean) {
					return (((MutableBoolean) o).booleanValue() == value);
				} else {
					return false;
				}
	}
//	/**
//	 * Returns <code>true</code> if and only if the system property named by the
//	 * argument exists and is equal to the string <code>"true"</code>.
//	 * <p>If there is no property with the specified <code>name</code>, or if the
//	 * specified <code>name</code> is empty or <code>null</code>, then <code>false</code>
//	 * is returned.
//	 * @return boolean
//	 * @param name java.lang.String
//	 */
//	public boolean getBoolean(String name) {
//		String propVal = Sys tem.getProperty(name);
//		if (propVal == null) {
//			return false;
//		}
//		return propVal.equalsIgnoreCase("true");
//	}
	/**
	 * Returns a hash code for this <code>MutableBoolean</code> object.
	 * @return the integer <code>1231</code> if this object represents <code>true</code>;
	 * returns the integer <code>1237</code> if this object represents <code>false</code>.
	 */
	@Override
	public int hashCode() {
		return (value ? 1231 : 1237);
	}
	/**
	 * Insert the method's description here.
	 * @param newValue boolean
	 */
	public void setValue(boolean newValue) {
		value = newValue;
	}
	/**
	 * Returns a <code>String</code> object representing this <code>MutableBoolean</code>'s
	 * value. If this object represents the value <code>true</code>, a string equal to 
	 * "<code>true</code>" is returned. Otherwise, a string equal to "<code>false</code>"
	 * is returned.
	 * @return a string representation of this object.
	 */
	@Override
	public String toString() {
		return (value ? "true" : "false");
	}
	/**
	 * Returns a <code>MutableBoolean</code> with a value represented by the 
	 * specified String. The <code>MutableBoolean</code> returned represents 
	 * the value <code>true</code> if the string argument is <code>not null</code>
	 * and is equal, ignoring case, to the string "<code>true</code>". 
	 * <p>Example: <code>Boolean.valueOf("True")</code> returns <code>true</code>.
	 * <br>Example: <code>Boolean.valueOf("yes")</code> returns <code>false</code>.
	 * @return the <code>MutableBoolean</code> value represented by the string.
	 * @param s a string.
	 */
	public static MutableBoolean valueOf(String s) {
		return new MutableBoolean(s);
	}
}