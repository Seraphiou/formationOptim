/*******************************************************************************
 * Copyright
 *   2012 axYus - www.axyus.com
 *   2012 P.Renier - paul.renier@axyus.com
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
 *******************************************************************************/
package fr.gouv.finances.dgfip.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * @author paul.renier
 * 
 */
public class QueryProvider {
	protected Properties queries;

	public QueryProvider(Properties queries) {
		super();
		this.queries = queries;
	}

	public QueryProvider(File propertiesFile) throws MalformedURLException, IOException {
		this(propertiesFile.toURI().toURL());
	}

	public QueryProvider(URL propertiesFile) throws IOException {
		InputStream in = propertiesFile.openStream();

		if (in == null) {
			throw new FileNotFoundException("Le fichier de requêtes n'a pas été trouvé : " + propertiesFile);
		}

		try {
			initFromInputStream(in);
		} finally {
			in.close();
		}
	}

	public QueryProvider(InputStream in) throws IOException {
		initFromInputStream(in);
	}

	private void initFromInputStream(InputStream in) throws IOException {
		queries = new Properties();
		queries.load(in);
	}

	public String getQuery(String key) {
		return queries.getProperty(key);
	}

}
