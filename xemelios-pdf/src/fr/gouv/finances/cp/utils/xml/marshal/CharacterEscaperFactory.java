/*
 * Copyright
 *   2008 axYus - www.axyus.com
 *   2008 c.Marhcand - christophe.marchand@axyus.com
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

import fr.gouv.finances.cp.utils.xml.marshal.escapers.IdenticalEscaper;

/**
 *
 * @author chm
 */
public class CharacterEscaperFactory {
    
    public static CharacterEscaper getCharacterEscaper(String charset) {
//        if("ISO-8859-1".equals(charset)) {
//            return new ISO88591Escaper();
//        } else if("UTF-8".equals(charset)) {
//            return new UTF8Escaper();
//        } else
//            return new DefaultCharacterEscaper();
        return new IdenticalEscaper();
    }

}
