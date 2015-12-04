/*
 * Copyright
 *   2009 axYus - www.axyus.com
 *   2009 C.Marchand - christophe.marchand@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.auth;

/**
 * Exception thrown when a user does not has enough grants to complete an action
 * @author chm
 */
public class UnauthorizedException extends Exception {
    public static final long serialVersionUID = 1L;

    public UnauthorizedException() {
        super();
    }
    public UnauthorizedException(String message) {
        super(message);
    }
    public UnauthorizedException(String message,Throwable cause) {
        super(message,cause);
    }
    public UnauthorizedException(Throwable cause) {
        super(cause);
    }
}
