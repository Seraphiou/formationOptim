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

package fr.gouv.finances.dgfip.xemelios.importers;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import java.io.File;
import java.io.FileOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.FileInfo;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ParameterModel;
import fr.gouv.finances.dgfip.xemelios.utils.FileUtils;

/**
 * This class applies a XSL to the file to import prior calling the default import mechanism.
 *
 * @since 3.0
 *
 * @author chm
 */
public class ImporterWithXSLBeforeSplitFile extends DefaultImporter {
    
    public ImporterWithXSLBeforeSplitFile(XemeliosUser user, PropertiesExpansion applicationProperties) {
        super(user,applicationProperties);
    }
    
    @Override
    public void setDocument(DocumentModel dm) {
        this.dm = dm;
    }
    
    /**
     * Importe le fichier specifie.
     * @param f Le fichier a importer
     * @see fr.gouv.finances.cp.xemelios.importers.EtatImporteur#importe(java.io.File)
     */
    @Override
    protected FileInfo importFile(File f) throws Exception {
        getImpSvcProvider().startLongWait();
    	File fTmp = f;
    	for (ParameterModel pm:dm.getParameters().getParameters()) {
    		if ("pre-split-xsl-file".equals(pm.getName())) {
                    File tmpDest = new File(FileUtils.getTempDir(),fTmp.getName());
                    FileOutputStream fos = new FileOutputStream (tmpDest);
                    StreamResult sr = new StreamResult(fos);
                    Transformer trans = FactoryProvider.getTransformerFactory().newTransformer(new StreamSource(new File (new File(dm.getBaseDirectory()),pm.getValue())));
                    trans.transform(new StreamSource(f), sr);
                    fos.flush();
                    fos.close();
                    fTmp = tmpDest;
    		}
    	}
        getImpSvcProvider().endLongWait();
    	return super.importFile(fTmp);
    }
    
    
}
