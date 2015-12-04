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
import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.utils.StringUtilities;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.Constants;
import fr.gouv.finances.dgfip.xemelios.common.FileInfo;
import fr.gouv.finances.dgfip.xemelios.common.ToolException;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.EtatModel;
import fr.gouv.finances.dgfip.xemelios.data.DataAccessException;
import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;
import fr.gouv.finances.dgfip.xemelios.data.DataLayerManager;
import fr.gouv.finances.dgfip.xemelios.data.impl.sqlconfig.TDocument;
import fr.gouv.finances.dgfip.xemelios.data.impl.sqlconfig.TLayer;
import fr.gouv.finances.dgfip.xemelios.data.impl.sqlconfig.TPersistenceConfig;
import fr.gouv.finances.dgfip.xemelios.importers.transformers.EtatTransformeur;
import fr.gouv.finances.dgfip.xemelios.utils.FileUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class imports a document in the underlying data layer. First it splits
 * the document in small files that contains only one importable element. Then,
 * if the "Etat" configuration defines an Xsl to apply before importing, it
 * applies it. Finally, it delegates import to the data layer.
 *
 * This class has been fully-rewritten since Xemelios 3.0, replacing the old
 * <tt>DefaultImporteur</tt> one.
 *
 * @since 3.0
 *
 * @author chm
 * @author paul.renier
 */
public class DefaultImporter extends EtatImporteur {

    private static Logger logger = Logger.getLogger(DefaultImporter.class);
    private PropertiesExpansion applicationProperties;
    private PrintWriter importTimingOS;
    private LinkedHashMap<String, Transformer> importXsltCache;
    private File localTempDir = null;
    private boolean isLocalTempDirDefined = false;
    private DocumentBuilderFactory dbf = FactoryProvider.getDocumentBuilderFactory();

    public DefaultImporter(XemeliosUser user, PropertiesExpansion applicationProperties) {
        super();
        this.applicationProperties = applicationProperties;
        this.user = user;
        warningCount = 0;
        String importTimingLogFile = applicationProperties.getProperty(Constants.SYS_PROP_IMPORT_TIMING);
        if (importTimingLogFile != null) {
            try {
                importTimingOS = new PrintWriter(new FileWriter(importTimingLogFile, true), true); // on
                importTimingOS.println("fichier XML;fichier decoupe;action;date debut;date fin");
            } catch (Exception ex) {
            }
        }
        if (importTimingOS == null) {
            importTimingOS = new PrintWriter(new NullOutputStream());
        }
        importXsltCache = new LinkedHashMap<String, Transformer>() {
            @Override
            protected boolean removeEldestEntry(Entry<String, Transformer> eldest) {
                return size() > 0;
            }
        };
        dbf.setNamespaceAware(true);
    }

    @Override
    public void setDocument(DocumentModel dm) {
        this.dm = dm;
    }

    public File getTmpDir() {
        if (isLocalTempDirDefined) {
            return localTempDir;
        } else {
            return FileUtils.getTempDir();
        }
    }

    /**
     * Importe le fichier specifie.
     *
     * @param f Le fichier a importer
     * @see
     * fr.gouv.finances.cp.xemelios.importers.EtatImporteur#importe(java.io.File)
     */
    @Override
    protected FileInfo importFile(File f) throws Exception {
        logger.debug("generating " + f.getAbsolutePath());
        getImpSvcProvider().startLongWait();
        String fileEncoding = getFileEncoding(f);
        String xmlVersion = getFileXmlVersion(f);
        File tmpDir = getTmpDir();

        File fHeader = File.createTempFile("header-", "", tmpDir);
        File fRef = File.createTempFile("ref-", "", tmpDir);
        File fFooter = File.createTempFile("footer-", "", tmpDir);
        XmlSplitter xs = splitFile(fHeader, fRef, fFooter, tmpDir, f, fileEncoding);
        getImpSvcProvider().endLongWait();
        Pair collectivite = new Pair();
        Pair codeBudget = new Pair();

        //--------- check collectivite -------------
        if (col == null) {
            collectivite = xs.getCollectivite();
            if (collectivite != null && collectivite.isFull()) {
                // on a récupéré la collectivité
                getImpSvcProvider().setCollectivite(collectivite);
            } else if (getImpSvcProvider().getCollectivite() != null && getImpSvcProvider().getCollectivite().isFull()) {
                // on connait la collectivité
                collectivite = getImpSvcProvider().getCollectivite();
            } else {
                // ici on demande à entrer les infos sur la collectivités
                try {
                    collectivite = getImpSvcProvider().getCollectivite(dm, f.getName(), collectivite);
                } catch (ToolException e) {
                    removeTempDir();
                    if (e.getErrorCode() == ToolException.ERROR_INTERRUPTED) {
                        getImpSvcProvider().displayMessage(e.getMessage(), ImportServiceProvider.MSG_TYPE_INFORMATION);
                        return null;
                    } else {
                        throw e;
                    }
                }
            }
            col = collectivite;
        } else {
            // collectivité "par défaut"
            collectivite = col;
        }
        //--------- check budget -------------
        if (bdg == null) {
            codeBudget = xs.getBudget();
            if (codeBudget != null && codeBudget.isFull()) {
                // on a récupéré le budget
                getImpSvcProvider().setBudget(codeBudget);
            } else if (getImpSvcProvider().getBudget() != null && getImpSvcProvider().getBudget().isFull()) {
                // on connait le budget
                codeBudget = getImpSvcProvider().getBudget();
            } else {
                // ici on demande à entrer les infos sur le budget
                try {
                    codeBudget = getImpSvcProvider().getBudget(dm, collectivite, f.getName(), codeBudget);
                } catch (ToolException e) {
                    removeTempDir();
                    if (e.getErrorCode() == ToolException.ERROR_INTERRUPTED) {
                        getImpSvcProvider().displayMessage(e.getMessage(), ImportServiceProvider.MSG_TYPE_INFORMATION);
                        return null;
                    } else {
                        throw e;
                    }
                }
            }
            bdg = codeBudget;
        } else {
            // budget "par défaut"
            codeBudget = bdg;
        }

        //---------------------------------------------------
        ArrayList<File> dataFiles = new ArrayList<File>(xs.getDataFiles());
        String header = FileUtils.readTextFile(fHeader, fileEncoding);
        String footer = FileUtils.readTextFile(fFooter, fileEncoding);
        String ref = FileUtils.readTextFile(fRef, fileEncoding);

        int docCount = 1;
        DocumentBuilder db = dbf.newDocumentBuilder();

        // load documents
        TPersistenceConfig tpc = DataLayerManager.getImplementation().getPersistenceConfig(dm, getUser());
        if (tpc == null) {
            throw new RuntimeException("pas de configuration de persistence trouvée pour " + dm.getId() + " dans " + dm.getBaseDirectory());
        }

        TLayer layer = tpc.getLayer(DataLayerManager.getImplementation().getLayerName());
        if (layer == null) {
            throw new RuntimeException("pas de persistence trouvée pour ce moteur de persistence (" + DataLayerManager.getImplementation().getLayerName() + ") pour " + dm.getId() + " dans " + dm.getBaseDirectory());
        }

        TDocument pc = layer.getDocument(dm.getId());

        // check if document exists, and if so, if we must remove it
        if (DataLayerManager.getImplementation().isDocumentExists(dm, codeBudget, collectivite, f.getName(), user)) {
            boolean shouldImport = false;
            try {
                if (OVERWRITE_RULE_DEFAULT.equals(getOverwriteRule())) {
                    shouldImport = getOverwrite(f.getName());
                } else if (OVERWRITE_RULE_ALWAYS.equals(getOverwriteRule())) {
                    shouldImport = true;
                } else if (OVERWRITE_RULE_NEVER.equals(getOverwriteRule())) {
                    shouldImport = false;
                } else if (OVERWRITE_RULE_ASK.equals(getOverwriteRule())) {
                    shouldImport = getOverwrite(f.getName());
                }
                if (shouldImport) {
                    logger.debug("importing");
                    // this can be very long, display the infinite waiter
                    getImpSvcProvider().startLongWait();
                    long startDelete = System.currentTimeMillis();
                    DataLayerManager.getImplementation().removeDocument(dm, codeBudget, collectivite, f.getName(), user);
                    importTimingOS.append(f.getName()).append(";;DEL;").append(Long.toString(startDelete)).append(";").append(Long.toString(System.currentTimeMillis()));
                    importTimingOS.println();
                    getImpSvcProvider().endLongWait();
                } else {
                    logger.debug("skipping");
                    return new FileInfo();
                }
            } catch (DataAccessException daEx) {
                logger.error("DataAccessException while removing:", daEx.getCause());
                daEx.getCause().printStackTrace();
                throw daEx;
            } catch (DataConfigurationException dcEx) {
                logger.error("DataConfigurationException while removing:", dcEx.getCause());
                dcEx.getCause().printStackTrace();
            } catch (Exception ex) {
                // si exception ici, l'utilisateur veut annuler
                logger.error(ex);
                ex.printStackTrace();
                removeTempDir();
                return null;
            }
        }

        long startBC = System.currentTimeMillis();
        DataLayerManager.getImplementation().registerBudgetCollectivite(dm, codeBudget, collectivite, xs.getParentcollectivites(), f.getName(), getArchiveName(), user);
        importTimingOS.append(f.getName()).append(";;BC;").append(Long.toString(startBC)).append(";").append(Long.toString(startBC = System.currentTimeMillis()));
        importTimingOS.println();
        DataLayerManager.getImplementation().saveSpecialKeys(dm, codeBudget, collectivite, f.getName(), getArchiveName(), xs.getSpecialKey1(), xs.getSpecialKey2(), xs.getSpecialKey3(), user);
        importTimingOS.append(f.getName()).append(";;SK;").append(Long.toString(startBC)).append(";").append(Long.toString(System.currentTimeMillis()));
        importTimingOS.println();

        // import du repository
        boolean shouldDelete = !"true".equals(applicationProperties.getProperty(Constants.SYS_PROP_IMPORT_AVOID_DELETE));
        if (!isCancelled()) {
            StringBuffer fullText = new StringBuffer();
            fullText.append("<?xml version=\"").append(xmlVersion).append("\" encoding=\"").append(fileEncoding).append("\" ?>");
            fullText.append(header).append(ref).append(footer);
            Document doc = db.parse(new ByteArrayInputStream(fullText.toString().getBytes(fileEncoding)));
            if (pc.getRepositoryImportXsltFile() != null && pc.getRepositoryImportXsltFile().length() > 0) {
                Document doc2 = null;
                File xslNomencl = new File(new File(dm.getBaseDirectory()), pc.getRepositoryImportXsltFile());
                TransformerFactory tf = FactoryProvider.getTransformerFactory();
                Transformer trans = tf.newTransformer(new StreamSource(xslNomencl));
                DOMResult result = new DOMResult();
                DOMSource source = new DOMSource(doc);
                trans.transform(source, result);
                doc2 = (Document) result.getNode();
                if (doc2 != null) {
                    doc = doc2;
                }
            }
            DataLayerManager.getImplementation().saveRepository(dm, collectivite, doc, xs.getReferenceNomenclature(), user);
        } else {
            logger.debug("cancelled");
        }
        // DEBUT
        for (int fileCount = 0; fileCount < dataFiles.size(); fileCount++) {
            if (isCancelled()) {
                break;
            }
            File df = dataFiles.get(fileCount);
            final int progress = (int) ((fileCount + 1) * 100.0 / dataFiles.size());
            processTempFile(df, fileEncoding, xmlVersion, header, footer, pc, collectivite, codeBudget, f, docCount, shouldDelete, progress);
            docCount++;
        }
        // FIN
        importTimingOS.append(f.getName()).append(";;REF;;").append(Long.toString(System.currentTimeMillis()));
        importTimingOS.println();
        if (shouldDelete) {
            fHeader.delete();
            fRef.delete();
            fFooter.delete();
            removeTempDir();
        }
        return xs.getFileInfo();
    }
    
    protected void processTempFile(
            final File df,
            final String fileEncoding,
            final String xmlVersion,
            final String header,
            final String footer,
            final TDocument persistenceConfig,
            final Pair collectivite,
            final Pair codeBudget,
            final File originalFile,
            final int docCount,
            final boolean shouldDelete,
            final int progress) {
        try {
            long startFile = System.currentTimeMillis();
            String data = FileUtils.readTextFile(df, fileEncoding);
            StringBuilder fullText = new StringBuilder();
            fullText.append("<?xml version=\"").append(xmlVersion).append("\" encoding=\"").append(fileEncoding).append("\"?>");
            fullText.append(header).append(data).append(footer);
            String sFullText = fullText.toString();
            byte[] bData = sFullText.getBytes(fileEncoding);

            Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(bData));

            // il faut retrouver de quel etat est ce document
            // on cherche si la balise root contient un
            // dm.getEtatxxx().getBalise()
            EtatModel currentEtat = null;
            for (EtatModel em : dm.getEtats()) {
                String balise = em.getBalise();
                NodeList nl = doc.getElementsByTagName(balise);
                if (nl.getLength() > 0) {
                    currentEtat = em;
                    break;
                } else {
                    nl = doc.getElementsByTagNameNS(em.getBaliseNamespace(), balise);
                    if (nl.getLength() > 0) {
                        currentEtat = em;
                        break;
                    }
                }
            }
            // traitement d'erreur, on n'arrive pas à identifier l'etat
            if (currentEtat == null) {
                StringWriter sw = new StringWriter();
                sw.append("Impossible de déterminer l'état de ce document :\n");
                TransformerFactory errorTransFactory = FactoryProvider.getTransformerFactory();
                Transformer errorTransformer = errorTransFactory.newTransformer();
                errorTransformer.transform(new DOMSource(doc), new StreamResult(sw));
                sw.flush();
                sw.close();
                logger.error(sw.getBuffer().toString());
                return;
            }
            // apply before-import xslt
            if (persistenceConfig.getEtat(currentEtat.getId()).getImportXsltFile() != null) {
                Transformer trans = importXsltCache.get(persistenceConfig.getEtat(currentEtat.getId()).getImportXsltFile());
                if (trans == null) {
                    TransformerFactory tf = FactoryProvider.getTransformerFactory();
                    File directory = new File(currentEtat.getParent().getBaseDirectory());
                    File xslFile = new File(directory, persistenceConfig.getEtat(currentEtat.getId()).getImportXsltFile());
                    trans = tf.newTransformer(new StreamSource(xslFile));
                    importXsltCache.put(persistenceConfig.getEtat(currentEtat.getId()).getImportXsltFile(), trans);
                }
                // important, maintenant que c'est mis en cache !
                trans.reset();
                if (codeBudget != null) {
                    trans.setParameter("CodeBudget", codeBudget.key);
                    trans.setParameter("LibelleBudget", codeBudget.libelle);
                }
                if (collectivite != null) {
                    trans.setParameter("CodeCollectivite", collectivite.key);
                    trans.setParameter("LibelleCollectivite", collectivite.libelle);
                }
                if (getManifeste() != null) {
                    trans.setParameter("manifeste", new DOMSource(getManifeste()));
                }
                // on passe en parametre le nom du fichier
                trans.setParameter("file.name", originalFile.getName());

                trans.setOutputProperty(OutputKeys.ENCODING, fileEncoding);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                trans.transform(new StreamSource(new ByteArrayInputStream(sFullText.getBytes(fileEncoding))), new StreamResult(baos));
                bData = baos.toByteArray();
            }
            importTimingOS.append(originalFile.getName()).append(";").append(df.toURI().toURL().toExternalForm()).append(";XSL;").append(Long.toString(startFile)).append(";").append(Long.toString(startFile = System.currentTimeMillis()));
            importTimingOS.println();

            String docName = StringUtilities.removeFileNameSuffix(originalFile.getName()) + "-" + docCount + "." + dm.getExtension();
            if (!isCancelled()) {
                try {
                    if (!DataLayerManager.getImplementation().importElement(
                            dm, currentEtat, 
                            codeBudget, collectivite, 
                            originalFile.getName(), 
                            docName, bData, 
                            fileEncoding, 
                            getArchiveName(), user)) {
                        logger.warn(DataLayerManager.getImplementation().getWarnings());
                        warningCount++;
                    }
                } catch (DataAccessException daEx) {
                    logger.error("importing element:", daEx);
                    throw (Exception) daEx;
                } catch (DataConfigurationException dcEx) {
                    logger.error("importing element:", dcEx);
                    throw (Exception) dcEx.getCause();
                }
            }
            if(shouldDelete) {
                df.delete();
            }
            importTimingOS.append(originalFile.getName()).append(";").append(df.toURI().toURL().toExternalForm()).append(";IDX;").append(Long.toString(startFile)).append(";").append(Long.toString(startFile = System.currentTimeMillis()));
            importTimingOS.println();
            this.getImpSvcProvider().pushCurrentProgress(progress);
        } catch(Exception ex) {
        //TODO
        }
    }

    protected XmlSplitter splitFile(File fHeader, File fRef, File fFooter, File tmpDir, File fToImport, String fileEncoding) throws FileNotFoundException, UnsupportedEncodingException, SAXException, ParserConfigurationException {
        FileOutputStream fisHeader = new FileOutputStream(fHeader);
        FileOutputStream fisRef = new FileOutputStream(fRef);
        FileOutputStream fisFooter = new FileOutputStream(fFooter);
        XmlSplitter xs = new XmlSplitter(fisHeader, fisRef, fisFooter, tmpDir, dm, fileEncoding);
        xs.setImportLogPrintWriter(importTimingOS);
        xs.setSplittedFileName(fToImport.getName());
        SAXParserFactory sf = FactoryProvider.getSaxParserFactory();
        SAXParser parser = sf.newSAXParser();
        try {
            parser.parse(fToImport, xs);
        } catch (Exception ex) {
            ex.printStackTrace();
            String position = "line " + xs.getLocator().getLineNumber() + ": ";
            throw new SAXException(position + ex.getMessage(), ex);
        } finally {
            if (fisHeader != null) {
                try {
                    fisHeader.flush();
                    fisHeader.close();
                } catch (Throwable t) {
                }
            }
            if (fisRef != null) {
                try {
                    fisRef.flush();
                    fisRef.close();
                } catch (Throwable t) {
                }
            }
            if (fisFooter != null) {
                try {
                    fisFooter.flush();
                    fisFooter.close();
                } catch (Throwable t) {
                }
            }
        }
        return xs;
    }

    protected void removeTempDir() {
        File tmpDir = getTmpDir();
        // ne pas décomenter, il ne faut pas supprimer le répertoire, car on
        // peut faire plusieurs imports à la suite.
        // deltree(tmpDir);
    }

    protected static void deltree(File f) {
        if (f.exists()) {
            if (f.isDirectory()) {
                for (File child : f.listFiles()) {
                    if (child.isDirectory()) {
                        deltree(child);
                    } else {
                        child.delete();
                    }
                }
            }
            f.delete();
        }
    }

    public boolean getOverwrite(String docName) throws Exception {
        return getImpSvcProvider().getOverwrite(docName);
    }

    protected Pair getBudget(DocumentModel dm, Pair collectivite, String fileName, Pair budget) throws ToolException {
        return getImpSvcProvider().getBudget(dm, collectivite, fileName, budget);
    }

    protected Pair getCollectivite(DocumentModel dm, String fileName, Pair collectivite) throws ToolException {
        return getImpSvcProvider().getCollectivite(dm, fileName, collectivite);
    }

    protected static String getFileEncoding(File f) throws IOException {
        String ret = "UTF-8";
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
            byte[] bb = new byte[255];
            int read = bis.read(bb);
            if (read < 255) {
                logger.info("while reading " + f.getName() + " for getFileEncoding(), can only read " + read + " bytes.");
            }
            String buff = new String(bb);
            buff = buff.substring(buff.indexOf("<?"));
            buff = buff.substring(0, buff.indexOf("?>") + 2);
            String header = buff;
            int encodingPos = header.indexOf("encoding=");
            if (encodingPos >= 0) {
                String s = header.substring(encodingPos + 9);
                if (s.startsWith("\"")) {
                    ret = s.substring(1, s.indexOf('"', 1));
                } else if (s.startsWith("'")) {
                    ret = s.substring(1, s.indexOf('\'', 1));
                } else {
                    s = s.substring(0, s.length() - 2).trim();
                    if (s.indexOf(' ') > 0) {
                        ret = s.substring(0, s.indexOf(' '));
                    } else {
                        ret = s;
                    }
                }
            } else {
                ret = "UTF-8";
            }
            bis.close();
        } catch (Exception ex) {
            logger.error("getFileEncoding(File)" + ex);
        }
        logger.debug("encoding=" + ret);
        return ret;
    }

    protected static String getFileXmlVersion(File f) throws IOException {
        String ret = null;
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        byte[] bb = new byte[255];
        bis.read(bb);
        String buff = new String(bb);
        buff = buff.substring(buff.indexOf("<?"));
        buff = buff.substring(0, buff.indexOf("?>") + 2);
        String header = buff;
        int versionPos = header.indexOf("version=");
        if (versionPos >= 0) {
            String s = header.substring(versionPos + 8);
            if (s.startsWith("\"")) {
                ret = s.substring(1, s.indexOf('"', 1));
            } else if (s.startsWith("'")) {
                ret = s.substring(1, s.indexOf('\'', 1));
            } else {
                s = s.substring(0, s.length() - 2).trim();
                if (s.indexOf(' ') > 0) {
                    ret = s.substring(0, s.indexOf(' '));
                } else {
                    ret = s;
                }
            }
        } else {
            ret = (new InputStreamReader(new FileInputStream(f))).getEncoding();
        }
        bis.close();
        logger.debug("version=" + ret);
        return ret;
    }

    @Override
    public String getAdditionalMessage() {
        return "";
    }

    @Override
    protected void postProcess(boolean isErrorOccured) throws Exception {
        logger.info("Le traitement a prit " + ((System.currentTimeMillis() - start) / 1000) + " secs");
    }

    /**
     * Permet de spécifier à cet importeur un autre répertoire temporaire que
     * celui par défaut dans l'application. Utile lorsqu'on parallélise des
     * imports
     *
     * @param localTempDir
     */
    public void setLocalTempDir(File localTempDir) {
        this.localTempDir = localTempDir;
        isLocalTempDirDefined = true;
    }
}
