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

package fr.gouv.finances.dgfip.xemelios.common.generators;

import java.io.OutputStream;

/**
 *
 * @author cmarchand
 */
public interface Generator {

    /**
     * Lance la g�n�ration dans l'outputStream sp�cifi�
     * @param os
     * @throws Exception
     */
    public void run(OutputStream os) throws GeneratingError;

}
