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

import java.util.StringTokenizer;



/**
 * This class represents a XPath path. It should be named "XPath" but there are too many
 * classes with this name, so, XWay...
 * This class is unmutable.
 * It is not used now, but in a near future, it should be used by {@link fr.gouv.finances.cp.xemelios.data.impl.MySqlDataLayer}
 * in sql request generation.
 * @author chm
 */
public class XWay {
    private String path;
    public XWay() {
        super();
    }
    public XWay(String path) throws InvalidPathExpressionException {
        if(path==null) throw new InvalidPathExpressionException("path can not be null");
        if(path.charAt(0)!='/') throw new InvalidPathExpressionException("relative path not allowed");
        if(path.endsWith("/")) throw new InvalidPathExpressionException("path can not end with \"/\"");
        this.path = path;
    }
    public XWay append(String path) throws InvalidPathExpressionException {
        if(path==null) throw new InvalidPathExpressionException("path can not be null");
        if(path.startsWith("/")) return new XWay(path);
        StringTokenizer st = new StringTokenizer(path,"/");
        StringBuilder sb = new StringBuilder(this.path);
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            if(token.equals("..")) sb = upOneLevel(sb.toString());
            else if(!token.equals(".")) sb.append("/").append(token);
        }
        return new XWay(sb.toString());
    }

    private static StringBuilder upOneLevel(String path) {
        int pos = path.lastIndexOf('/');
        return new StringBuilder(path.substring(0,pos));
    }
    @Override
    public String toString() { return path; }
    
    public static String compactXPath(String path) {
        StringTokenizer st = new StringTokenizer(path,"/");
        StringBuilder sb = new StringBuilder();
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            if(token.equals("..")) sb = upOneLevel(sb.toString());
            else if(!token.equals(".")) sb.append("/").append(token);
        }
        return sb.toString();
    }
}
