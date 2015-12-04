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

package fr.gouv.finances.dgfip.utils.xml;

/**
 * @author chm
 *
 */
public class PathNotFoundException extends Exception {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3905800868497406003L;
    /**
     * 
     */
    public PathNotFoundException() {
        super();
    }
    /**
     * @param message
     */
    public PathNotFoundException(String message) {
        super(message);
    }
    /**
     * @param message
     * @param cause
     */
    public PathNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause
     */
    public PathNotFoundException(Throwable cause) {
        super(cause);
    }
}