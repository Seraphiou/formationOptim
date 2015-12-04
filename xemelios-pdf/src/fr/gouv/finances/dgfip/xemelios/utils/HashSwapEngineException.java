/*
 * Copyright 
 *   2008 axYus - www.axyus.com
 *   2008 L.Meckert - laurent.meckert@axyus.com
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

class HashSwapEngineException extends Exception {
    public static final int ERROR_GENERICEXCEPTION  	= 4;
    public static final int ERROR_UNSUPPORTEDOPERATIONEXCEPTION				= 1;
    public static final int ERROR_CLASSCASTEXCEPTION			= 2;
    public static final int ERROR_ILLEGALARGUMENTEXCEPTION 			= 3;
    public static final int ERROR_NULLPOINTEREXCEPTION  	= 4;
    private int errorCode;
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3256728364101677363L;
    
    /**
     * @return Returns the errorCode.
     */
    public int getErrorCode() {
        return errorCode;
    }
    
    /**
     * 
     */
    public HashSwapEngineException(int code) {
        super();
        this.errorCode=code;
    }
    /**
     * @param message
     */
    public HashSwapEngineException(String message, int code) {
		super(message);
		this.errorCode=code;
    }
    /**
     * @param message
     * @param cause
     */
    public HashSwapEngineException(String message, Throwable cause, int code) {
        super(message, cause);
        this.errorCode=code;
    }
    /**
     * @param cause
     */
    public HashSwapEngineException(Throwable cause, int code) {
        super(cause);
        this.errorCode=code;
    }

}


