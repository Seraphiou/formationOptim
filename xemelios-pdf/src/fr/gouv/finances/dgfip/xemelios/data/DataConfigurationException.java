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

package fr.gouv.finances.dgfip.xemelios.data;

/**
 * This exception denotes a misconfiguration in data layer.
 * See message for more explanations.
 * @author chm
 */
public class DataConfigurationException extends Exception {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3906646397301240632L;

    /**
     * 
     */
    public DataConfigurationException() {
        super();
    }

    /**
     * @param message
     */
    public DataConfigurationException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public DataConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public DataConfigurationException(Throwable cause) {
        super(cause);
    }

}
