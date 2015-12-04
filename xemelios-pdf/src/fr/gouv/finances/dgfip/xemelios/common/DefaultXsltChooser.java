/*
 * Copyright
 *   2011 axYus - www.axyus.com
 *   2011 JP.Tessier - jean-philippe.tessier@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.common;

import java.io.File;

import org.w3c.dom.Document;

import fr.gouv.finances.dgfip.utils.XsltFileChooser;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ElementModel;
import fr.gouv.finances.dgfip.xemelios.common.config.EtatModel;

/**
 * @author jp.tessier
 *
 */
public class DefaultXsltChooser implements XsltFileChooser {
	@Override
	public File getXslt(DocumentModel dm, EtatModel em, ElementModel element, Document doc) {
		File xslFile = null;
		xslFile = new File(new File(new File(System.getProperty("user.home")),"xemelios/documents-def-ovrrd"),element.getXslt());
		if(!xslFile.exists()) xslFile = new File(em.getParent().getBaseDirectory(),element.getXslt());
		return xslFile;
	}
}
