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

package fr.gouv.finances.dgfip.xemelios.data;

import java.io.IOException;

/**
 * Defines how to persist a "Document"
 * @author chm
 */
public interface PersistenceConfig {
    /**
     * Initialize this PersistenceConfig
     * @param layer The element to initialize with
     * @throws DataConfigurationException if case of trouble. See Message for further details.
     * @deprecated
     */
//    public void setLayer(Element layer) throws DataConfigurationException;
    
    public String getRepositoryXsltImportFile();
    public String getImportXsltFile(String etatId) throws DataConfigurationException;
    /**
     * 
     * @param etatId
     * @return
     * @deprectade
     */
//    public String getImportXsltOutputEncoding(String etatId);

    /**
     * Defines the directory where config files are.
     * This method is called by {@link fr.gouv.finances.xemelios.data.DataImpl}
     * when this data implementation is loaded.
     */
//    public void setDocumentsConfigDirectory(File directory);
    
    // helper methods
    /**
     * Retrieves the xslt buffer
     * @param docDefDir The directory of documents definitions
     */
    public String getImportXsltBuffer(String etatId) throws IOException, DataConfigurationException;
}
