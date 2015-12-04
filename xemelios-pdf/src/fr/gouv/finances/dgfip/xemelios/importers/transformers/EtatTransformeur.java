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

package fr.gouv.finances.dgfip.xemelios.importers.transformers;

import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;

public interface EtatTransformeur {
	public static final int RETURN_CODE_OK = 0;
	public static final int RETURN_CODE_ERR = 1;
	
	public int getReturnCode();
	public void transform (Pair collectivite, Pair budget,String fileEncoding, String xmlVersion, String baseDirectory, String buff,XemeliosUser user);
        public void setArchiveName(String archiveName);
        public String getArchiveName();
}
