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

package fr.gouv.finances.dgfip.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * The <code>Amount</code> class presents a method to format
 * amounts according to Xemelios specifications. In a near future,
 * we should parameterize the pattern used.
 * <p>
 * @author  Christophe Marchand
 * @author jp.tessier
 * @version 1.94, 05/11/04
 */
public class Amount extends BigDecimal implements Serializable {
    
    private static DecimalFormat df = new DecimalFormat("#,##0.00");
    static {
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
    }
    private static final Object locker = new Object();
    
    private String strRepresentation = null;


    /**
     * A constant holding a Not-a-Number (NaN) value of type <code>float</code>.
     * It is equivalent to the value returned by
     * <code>Float.intBitsToFloat(0x7fc00000)</code>.
     */
    public static final float NaN = 0.0f / 0.0f;

    /**
     * Constructs a newly allocated <code>Float</code> object that represents
     * the primitive <code>float</code> argument.
     * 
     * @param value
     *            the value to be represented by the <code>Float</code>.
     */
    public Amount(float value) {
        super(value); //!=NaN?value:NaN);
    }


    /**
     * Constructs a newly allocated <code>Float</code> object that represents
     * the floating-point value of type <code>float</code> represented by the
     * string. The string is converted to a <code>float</code> value as if by
     * the <code>valueOf</code> method.
     * 
     * @param s
     *            a string to be converted to a <code>Float</code>.
     * @exception NumberFormatException
     *                if the string does not contain a parsable number.
     * @see java.lang.Float#valueOf(java.lang.String)
     */
    public Amount(String s) throws NumberFormatException {
    	super(s);
    }


    /**
     * Returns a string representation of this <code>Amount</code> object.
     * Xemelios specifies pattern to use is <tt>#,##0.00</tt> .
     * 
     * @return a <code>String</code> representation of this object.
     */
    public String stringRepresentation() {
        if(strRepresentation==null) {
            synchronized(locker) {
                strRepresentation = df.format((BigDecimal)this);
            }
        }
        return strRepresentation;
    }

    /**
     * Compare this value to another 
     * @param anotherFloat
     *            the <code>Float</code> to be compared.
     * @return the value <code>0</code> if <code>anotherFloat</code> is
     *         numerically equal to this <code>Float</code>; a value less
     *         than <code>0</code> if this <code>Float</code> is numerically
     *         less than <code>anotherFloat</code>; and a value greater than
     *         <code>0</code> if this <code>Float</code> is numerically
     *         greater than <code>anotherFloat</code>.
     * 
     * @since 1.2
     * @see BigDecimal#compareTo(Object)
     */
    public int compareTo(Amount anotherAmount) {
    	return super.compareTo((BigDecimal)anotherAmount);
    }

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3256727298932617777L;

}
