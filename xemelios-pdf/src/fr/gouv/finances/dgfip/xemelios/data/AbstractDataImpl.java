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

import fr.gouv.finances.dgfip.xemelios.auth.UnauthorizedException;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.EtatModel;
import fr.gouv.finances.dgfip.xemelios.data.impl.sqlconfig.PersistenceConfigParser;
import fr.gouv.finances.dgfip.xemelios.data.impl.sqlconfig.TPersistenceConfig;
import fr.gouv.finances.dgfip.xemelios.utils.GZipUtils;
import fr.gouv.finances.dgfip.xemelios.common.config.Loader;

/**
 * Provides some services
 * @author chm
 */
public abstract class AbstractDataImpl implements DataImpl {

    private final static Logger logger = Logger.getLogger(AbstractDataImpl.class);
    private final Object locker = new Object();
    private boolean useCachedPersistence = true;
    static protected Map<String, Class> persistenceModels = new HashMap<String, Class>();

    static {
        persistenceModels.put("xemelios-sql", TPersistenceConfig.class);
    }
    protected Map<String, TPersistenceConfig> cachedPersistence = new HashMap<String, TPersistenceConfig>();
    protected File documentConfigDirectory = null;

    public boolean importElement(DocumentModel dm, EtatModel currentEtat, String sourceFileName, String elementName, Document element, int overwriteMode) throws DataAccessException {
        return false;
    }

    @Override
    public void setDocumentsConfigDirectory(File directory) {
        this.documentConfigDirectory = directory;
    }

    @Override
    public void reset() {
        cachedPersistence.clear();
    }

    @Override
    public TPersistenceConfig getPersistenceConfig(DocumentModel dm, XemeliosUser user) throws DataConfigurationException, UnauthorizedException {
        TPersistenceConfig pc = cachedPersistence.get(dm.getId());
        long start = System.currentTimeMillis();
        if (pc == null) {
            synchronized (locker) {
//                    if(dm.getExtendedDocId()!=null) {
//                        DocumentModel inheritee = dm.getParent().getDocumentById(dm.getExtendedDocId());
//                        getPersistenceConfig(inheritee, user);
//                    }
                logger.info("Chargement de la configuration " + dm.getTitre());
                String fileName = dm.getPersistenceConfigFile();
                File configFile = new File(new File(dm.getBaseDirectory()), fileName);
                try {

                    // .XML => persistence normale
                    if (fileName.toUpperCase().endsWith(".XML")) {
                        PersistenceConfigParser handler = new PersistenceConfigParser(dm.getBaseDirectory());
                        handler.parse(configFile);
                        pc = (TPersistenceConfig) handler.getMarshallable();
                        pc.setBaseDirectory(dm.getBaseDirectory());
                        if (isUseCachedPersistence()) {
                            cachedPersistence.put(dm.getId(), pc);
                        }
                    } //	            	.EXML => persistence derivee
                    else if (fileName.toUpperCase().endsWith(".EXML")) {
                        PersistenceConfigParser handler = new PersistenceConfigParser(dm.getBaseDirectory());
                        String parentDocId = dm.getExtendedDocId();
                        DocumentModel parentConfig = Loader.getDocumentsInfos((String) null).getDocumentById(parentDocId);
                        TPersistenceConfig parentPersistConfig = getPersistenceConfig(parentConfig, user);
                        handler.setInitialData(parentPersistConfig);
                        handler.parse(configFile);
                        pc = (TPersistenceConfig) handler.getMarshallable();
                        pc.setBaseDirectory(dm.getBaseDirectory());
                        if(isUseCachedPersistence())
                            cachedPersistence.put(dm.getId(), pc);
                    }
                } catch (ParserConfigurationException pcEx) {
                    throw new DataConfigurationException(pcEx);
                } catch (IOException ioEx) {
                    throw new DataConfigurationException(ioEx);
                } catch (SAXException saxEx) {
                    throw new DataConfigurationException(saxEx);
                }
            }	// synchronized
        }
        return pc;
    }

    public boolean isUseCachedPersistence() {
        return useCachedPersistence;
    }

    public void setUseCachedPersistence(boolean useCachedPersistence) {
        this.useCachedPersistence = useCachedPersistence;
        if (!useCachedPersistence) {
            reset();
        }
    }
    
    /**
	 * Cette méthode retourne un flux non-compressé - lisible
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected InputStream getReadableInputStream(InputStream in) throws IOException {
		PushbackInputStream rawInputStream = new PushbackInputStream(in, 2);
		byte[] docHead = new byte[2];
		InputStream docIn;

		rawInputStream.read(docHead);
		rawInputStream.unread(docHead);

		if (GZipUtils.isGZipped(docHead)) {
			logger.debug("Read document as gzipped");
			docIn = new GZIPInputStream(rawInputStream);
		} else {
			logger.debug("Read document as text stream");
			docIn = rawInputStream;
		}

		return docIn;
	}

	/**
	 * Retourne un DOM Document à partir d'un flux
	 * @param docIn
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 */
	protected Document parseDOMFromInputStream(DocumentBuilderFactory domFactory, InputStream docIn) throws SAXException, IOException, ParserConfigurationException {
		try {
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			logger.debug("Parse doc stream");
			return builder.parse(new InputSource(docIn));
		} finally {
			docIn.close();
		}
	}
}
