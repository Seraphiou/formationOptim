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
package fr.gouv.finances.dgfip.xemelios.data;

import java.util.Iterator;

import org.w3c.dom.Document;

import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.xemelios.auth.UnauthorizedException;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.config.EtatModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ListeResultatModel;

/**
 * The way to carry search results from data layer
 * @author chm
 */
public interface DataResultSet extends Iterator<DataHandler> {
    
    /**
     * Returns the real number of records found. If this value isn't available,
     * returns the estimated size.
     * @return
     */
    public int getCount();
    
    /**
     * Returns the estimated size of the search result
     * @return
     */
    public int getEstimatedSize();
    /**
     * Returns <tt>true</tt> if a next page is available
     * @return
     */
    public boolean hasNextPage();
    /**
     * Returns <tt>true</tt> if a previous page is available
     * @return
     */
    public boolean hasPreviousPage();
    public void setEnvironment(ListeResultatModel lrm, CachedData cache);
    public ListeResultatModel getListeResultatModel();
    public void setListeResultatModel(ListeResultatModel l);
    public boolean hasNext();
    public EtatModel getEtatModel();
    public Pair getCollectivite();
    public Pair getBudget();
    public void nextPage() throws DataAccessException, DataConfigurationException;
    public void lastPage() throws DataAccessException, DataConfigurationException;    
    public void previousPage() throws DataAccessException, DataConfigurationException;
    public void firstPage() throws DataAccessException, DataConfigurationException;
    public void setPage(int page) throws DataAccessException, DataConfigurationException;
    public int getEstimatedPageCount();
    public int getCurrentPage();
    public Document getDocument(String docId) throws DataConfigurationException, DataAccessException, UnauthorizedException;
    public String getDocumentEncoding(String docId) throws DataConfigurationException, DataAccessException;
    /**
     * Frees all unused allocated memory
     *
     */
    public void clear();
    
//    public String calculateAggregate(String columnName, String tableName, String operator, String dataType) throws DataConfigurationException, DataAccessException;
}
