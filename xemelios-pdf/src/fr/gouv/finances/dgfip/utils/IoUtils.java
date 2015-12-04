/*
 * Copyright 
 *   2007 axYus - www.axyus.com
 *   2007 C.Marchand - christophe.marchand@axyus.com
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author chm
 *
 */
public class IoUtils {
	public static InputStream getInputStream(String fileName) throws IOException {
	    File f = new File(fileName);
	    if(!f.exists()) throw new IOException("File not found: "+fileName);
            return new FileInputStream(f);
	}
    public static String getEncoding(String buffer) {
        String ret = "UTF-8";
        String buff = new String(buffer);
        try {
	        buff = buff.substring(buff.indexOf("<?"));
	        buff = buff.substring(0,buff.indexOf("?>")+2);
	        String header = buff;
	        int encodingPos = header.indexOf("encoding=");
	        if(encodingPos>=0) {
	            String s = header.substring(encodingPos+9);
	            if(s.startsWith("\"")) {
	                ret=s.substring(1,s.indexOf('"',1));
	            } else {
	                s = s.substring(0,s.length()-2).trim();
	                if(s.indexOf(' ')>0) ret = s.substring(0,s.indexOf(' '));
	                else ret = s;
	            }
	        }
        } catch(Throwable t) {
            return ret;
        }
        return ret;
    }

}
