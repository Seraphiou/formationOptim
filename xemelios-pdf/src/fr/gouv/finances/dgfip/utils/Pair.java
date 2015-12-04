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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 * @author chm
 */
public class Pair implements Comparable {
    private Logger logger = Logger.getLogger(Pair.class);
    public String key, libelle;
    public Pair(String key, String libelle) {
        super();
        this.key=key;
        this.libelle=libelle;
    }
    public Pair() { super(); }
    @Override
    public String toString() {
        return StringEscapeUtils.unescapeXml(libelle);
    }
    @Override
    public boolean equals(Object o) {
        if(o==null) return false;
        if(o instanceof Pair) {
            Pair other = (Pair)o;
            boolean ret = false;
            if(key==null && other!=null) ret = false;
            else if(key!=null && other.key==null) ret = false;
            else ret = key.equals(other.key);
//            logger.debug("comparing @"+key+"@ with @"+other.key+"@ => "+Boolean.toString(ret));
            return ret;
        } else if(o instanceof String) {
            return key.equals(o);
        } else {
            logger.warn("Comparing a Pair with a "+o.getClass().getName());
        }
        return false;
    }
    public int compareTo(Object o) {
        if(o instanceof Pair) {
            Pair other = (Pair)o;
            int ret = 0;
            if(libelle!=null && other.libelle!=null) ret = libelle.compareTo(other.libelle);
            if(ret==0 && key!=null && other.key!=null) ret = key.compareTo(other.key);
            return ret;
        }
        return -1;
    }
    public boolean isFull() { return key!=null && libelle!=null; }
}
