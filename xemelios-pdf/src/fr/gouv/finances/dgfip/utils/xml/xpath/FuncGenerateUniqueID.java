/*
 * Copyright
 *  2011 axYus - http://www.axyus.com
 *  2011 C.Marchand - christophe.marchand@axyus.com
 *
 * This file is part of XEMELIOS_NB.
 *
 * XEMELIOS_NB is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * XEMELIOS_NB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS_NB; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package fr.gouv.finances.dgfip.utils.xml.xpath;

import fr.gouv.finances.dgfip.utils.xml.HelperFunction;
import java.net.InetAddress;
import java.util.List;
import javax.xml.xpath.XPathFunctionException;
import jonelo.jacksum.algorithm.AbstractChecksum;
import jonelo.jacksum.algorithm.MD;

/**
 *
 * @author cmarchand
 */
public class FuncGenerateUniqueID  extends AbstractXPathFunction {

    @Override
    public Object evaluate(List args) throws XPathFunctionException {
        return HelperFunction.generateUniqueID();
    }

}
