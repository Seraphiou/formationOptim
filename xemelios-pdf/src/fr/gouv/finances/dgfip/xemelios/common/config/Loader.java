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

package fr.gouv.finances.dgfip.xemelios.common.config;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Provides helper method to load config files
 * @author chm
 */
public class Loader {
    private static final Logger logger = Logger.getLogger(Loader.class);
    private static DocumentsModel cache = null;
    private static String lastLoadSource = null;
    
    /**
     * Loads and returns config files. If config files contains at least one error, returns an empty DocumentsModel.
     * @param repertoires A comma-separated list of directories where to find config files
     * @return DocumentsModel the fully-assembled config
     */
    protected static DocumentsModel getDocsInfos(String repertoires) throws SAXException, ParserConfigurationException, IOException {
        DocumentsModel ret = cache;
        if(ret==null) {
            cache = __loadConfigFromFiles(repertoires);
            if(repertoires!=null)
                lastLoadSource = repertoires;
            ret = cache;
        }
        return ret;
    }

    /**
     * Cette méthode recharge forcément depuis les fichiers. Si <code>repertoires</code>
     * est null, utilise {@link #lastLoadSource} comme source.
     * <p><b><u>Important</u></b> : Il est <b>fondamental</b> que cette méthode ne modifie aucune variable statique de <tt>Loader</tt>,
     * pour fournir un point d'entrée (certes masqué) pour charger une seconde configuration.
     * @param repertoires L'emplacement d'où charger la configuration. Une chaine contenant une liste de répertoires
     * séparés par des virgules (&apos;,&apos;)
     * @return
     */
    protected static DocumentsModel __loadConfigFromFiles(String repertoires) throws SAXException, ParserConfigurationException, IOException {
        DocumentsModel ret = null;
        String sourceDirs = repertoires==null ? lastLoadSource : repertoires;
        String[] dirs = sourceDirs.split(",");
        for(String repertoire:dirs) {
            repertoire = repertoire.trim().replace("\t", "");
            File dir = new File(repertoire);
            if (!dir.exists()) {
                logger.info(repertoire + " does not exists.");
                continue;
            }
            // Fichiers de config
            File[] fichiers = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    boolean ret = name.toUpperCase().endsWith(".XML") && !"build.xml".equals(name);
                    return ret;
                }
            });
            if (fichiers == null) {
                logger.info("No .xml file found in " + repertoire);
                return null;
            }
            DocumentsParser dp = new DocumentsParser();
            for (int i = 0; i < fichiers.length; i++) {
                try {
                    dp.parse(fichiers[i]);
                } catch(Exception ex) {
                    logger.error("while parsing "+fichiers[i].getAbsolutePath(),ex);
                }
                DocumentsModel dm = (DocumentsModel) dp.getMarshallable();
                if (ret == null) {
                    ret = dm;
                    for(DocumentModel docModel:ret.getDocuments()) {
                        docModel.setBaseDirectory(repertoire);
                    }
                } else {
                    for(DocumentModel docModel : dm.getDocuments()) {
                        docModel.setBaseDirectory(repertoire);
                        ret.addChild(docModel, DocumentModel.QN);
                    }
                }
            }
        }
        for(String repertoire:dirs) {
            // fichiers dérivés d'une config
            repertoire = repertoire.trim().replace("\t", "");
            File dir = new File(repertoire);
            if (!dir.exists()) {
                logger.info(repertoire + " does not exists.");
                continue;
            }
            File[] fichiers = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toUpperCase().endsWith(".EXML");
                }
            });
            if (fichiers == null) {
                logger.info("No .exml file found in " + repertoire);
                return null;
            }
            for(File f:fichiers) {
                logger.debug("parsing "+f.getName());
                DocumentsParser dp = new DocumentsParser();
                dp.setInitialData(ret);
                dp.parse(f);
                ret = (DocumentsModel)dp.getMarshallable();
                for(DocumentModel docModel:ret.getDocuments()) {
                    if(docModel.getBaseDirectory()==null)
                        docModel.setBaseDirectory(repertoire);
                }
            }
        }
        return ret;
    }

    /**
     * Renvoie la configuration chargée.
     * Si la configuration a déjà été chargée, renoive celle en cache. Si le cache est vide, utilise <code>repertoires</code>
     * comme source.
     * @param repertoires
     * @return Renvoie une configuration qui peut être vide si la validation n'est pas satisfaisante.
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public static DocumentsModel getDocumentsInfos(String repertoires) throws SAXException, ParserConfigurationException, IOException {
        DocumentsModel ret = getDocsInfos(repertoires);
        try {
            ret.validate();
        } catch(Throwable t) {
            logger.error("while loading config files: "+t);
            t.printStackTrace();
            ret = new DocumentsModel(DocumentsModel.QN);
        }
        return ret;
    }
    public static void flushLoadedConfig() {
        cache=null;
    }
    
    /**
     * Renvoie la configuration qui a été chargée dans XeMeLios, mais telle qu'elle est dans les fichiers,
     * et pas modifiée par l'utilisateur.
     * @return
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public static DocumentsModel getConfigurationFromLoadedFiles() throws SAXException, ParserConfigurationException, IOException {
        return __loadConfigFromFiles(null);
    }

}
