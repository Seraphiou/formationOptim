/*
 * Copyright
 *   2009 axYus - www.axyus.com
 *   2009 c.Marhcand - christophe.marchand@axyus.com
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;

import fr.gouv.finances.dgfip.utils.QueryProvider;
import fr.gouv.finances.dgfip.xemelios.data.DataLayerManager;

/**
 * 
 * @author christophe.marchand
 */
public abstract class AbstractImportPatcherImpl implements ImportPatcher {
	private static final Logger logger = Logger.getLogger(AbstractImportPatcherImpl.class);
	private Hashtable<String, Object> parameters = new Hashtable<String, Object>();
	private ImportServiceProvider isp;
	protected QueryProvider queryProvider;

	@Override
	public void setParameter(String paramName, Object value) {
		parameters.put(paramName, value);
	}

	@Override
	public void setParameter(String paramName, File file) {
		parameters.put(paramName, file);
	}

	@Override
	public void setParameter(String paramName, String value) {
		parameters.put(paramName, value);
	}

	public Object getParameter(String paramName) {
		return parameters.get(paramName);
	}

	public void setImportServiceProvider(ImportServiceProvider isp) {
		this.isp = isp;
	}

	public ImportServiceProvider getImportServiceProvider() {
		return isp;
	};

	protected void initializeQueryProvider() {
		Properties props = null;

		try {
			String layerName = DataLayerManager.getImplementation().getLayerName();
			String fileName = layerName + "-queries.properties";
			InputStream in = this.getClass().getResourceAsStream(fileName);

			if (in == null) {
				throw new FileNotFoundException("Le fichier de requêtes n'a pas été trouvé : " + fileName);
			}

			try {
				props = new Properties();
				props.load(in);
				queryProvider = new QueryProvider(props);
				logger.info("QueryProvider initialisé pour '" + this.getClass() + "'");
			} finally {
				in.close();
			}
		} catch (Exception e) {
			logger.warn("Impossible d'initialiser le QueryProvider pour '" + this.getClass() + "' : " + e.getMessage());
			queryProvider = null;
			throw new RuntimeException(e);
		}
	}

	public QueryProvider getQueryProvider() {
		return queryProvider;
	}
}
