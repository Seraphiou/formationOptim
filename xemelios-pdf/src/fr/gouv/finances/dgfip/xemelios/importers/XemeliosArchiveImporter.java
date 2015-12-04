/*
 * Copyright
 *   2009 axYus - www.axyus.com
 *   2009 C.Marchand - christophe.marchand@axyus.com
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

import fr.gouv.finances.dgfip.xemelios.utils.Errors;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import fr.gouv.finances.dgfip.utils.DateUtils;
import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.utils.StringUtilities;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.Constants;
import fr.gouv.finances.dgfip.xemelios.common.FileInfo;
import fr.gouv.finances.dgfip.xemelios.common.PJRef;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentsModel;
import fr.gouv.finances.dgfip.xemelios.common.config.EtatModel.Marker;
import fr.gouv.finances.dgfip.xemelios.common.config.Loader;
import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;
import fr.gouv.finances.dgfip.xemelios.data.DataImpl;
import fr.gouv.finances.dgfip.xemelios.data.DataLayerManager;
import fr.gouv.finances.dgfip.xemelios.utils.FileUtils;
import java.util.Date;
import javax.xml.transform.dom.DOMResult;

/**
 *
 * @author chm
 */
public class XemeliosArchiveImporter {

    private static final Logger logger = Logger.getLogger(XemeliosArchiveImporter.class);
    public static final transient String MANIFEST_FILENAME = "manifeste_XeMeLios.xml";
    public static final transient String MANIFEST_PATCH_XSL_NAME = "fr/gouv/finances/dgfip/xemelios/importers/XemeliosArchiveImporterPatchManifest.xsl";
    private DocumentsModel documentsModel;
    private File archiveFirstFile;
    private Document manifest;
    Archive arch;
//    private int currentArchiveNum = -1;
    private XemeliosUser user = null;
    private ImportServiceProvider isp;
    private FileInfo fileInfo = null;
    private ArrayList<File> filesToDrop, directoriesToDrop;
    private PropertiesExpansion applicationProperties;
    private File currentVolume = null;

    public XemeliosArchiveImporter(DocumentsModel dms, File fToImport, ImportServiceProvider isp, PropertiesExpansion applicationProperties) {
        super();
        this.documentsModel = dms;
        this.archiveFirstFile = fToImport;
        this.isp = isp;
        this.applicationProperties = applicationProperties;
        fileInfo = new FileInfo();
        filesToDrop = new ArrayList<File>();
        directoriesToDrop = new ArrayList<File>();
    }

    public void setUser(XemeliosUser user) {
        this.user = user;
    }

    public XemeliosUser getUser() {
        if (user == null) {
            user = new XemeliosUser() {

                @Override
                public String getId() {
                    return "authorized importer";
                }

                @Override
                public String getDisplayName() {
                    return getId();
                }

                @Override
                public boolean hasRole(String role) {
                    return true;
                }

                @Override
                public boolean hasDocument(String document) {
                    return true;
                }

                @Override
                public boolean hasCollectivite(String collectivite, DocumentModel dm) {
                    return true;
                }
            };
        }
        return user;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    @SuppressWarnings("unchecked")
    public Errors doImport() {
        Errors errors = checkArchive();
        if (errors.containsError()) {
            return errors;
        }
        File directory = archiveFirstFile.getParentFile();
        XPathFactory xpf = null;
        XPath xp = null;
        ZipFile zf = null;
        Document newManifeste = null;
        DocumentsModel docsModel = null;
        try {
            docsModel = Loader.getDocumentsInfos(null);
        } catch(Exception ignore) {
//            logger.error("doImport()",ignore);
        }
        for (Volume volume : arch.volumes) {
            File volumeFile = new File(directory, volume.fileName);
            try {
                if(!volumeFile.exists()) {
                    if("true".equals(applicationProperties.getProperty(Constants.SYS_PROP_ARCH_AVOID_CHECK))) continue;
                    else throw new ZipException();
                }
                currentVolume = volumeFile;
                zf = new ZipFile(volumeFile);
                fileInfo.setLastModify(volumeFile.lastModified());
                ZipEntry ze = zf.getEntry(MANIFEST_FILENAME);
                DocumentBuilderFactory domFactory = FactoryProvider.getDocumentBuilderFactory();
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                manifest = builder.parse(zf.getInputStream(ze));
                xpf = FactoryProvider.getXPathFactory();
                xp = xpf.newXPath();
//                int currentVolumeNumber = Integer.parseInt(xp.evaluate("/manifeste/volumes/volume[@fichier='"+volumeFile.getName()+"']/@num", manifest));
                if("true".equals(applicationProperties.getProperty(Constants.SYS_PROP_ARCH_AVOID_CHECK))) {
                    // dans ce cas, on n'importe que le volume désigné par le fichier source
                    if(!archiveFirstFile.getName().equals(volumeFile.getName()))
                        continue;
                }
// 1 - on contrôle que tous les types de documents sont importables
                NodeList documentIds = (NodeList) xp.evaluate("//document[not(preceding-sibling::node()/@type eq @type) and @volume eq " + volume.num + "]/@type", manifest, XPathConstants.NODESET);   //
                for (int i = 0; i < documentIds.getLength(); i++) {
                    String docId = documentIds.item(i).getNodeValue();
                    if ("CGE".equals(docId)) {
                        docId = "cg-colloc";
                    } else if ("CGETAT".equals(docId)) {
                        docId = "cg-etat";
                    } else if ("PES".equals(docId)) {
                        docId = "pes-aller";
                    } else if ("EDMN".equals(docId)) {
                        docId = "edmn";
                    } else if ("ERTN".equals(docId)) {
                        docId = "ertn";
                    } else if ("PJ".equals(docId)) {
                        docId = null;
                    }
                    if (docId == null) {
                        continue;
                    }
                    if (!DataLayerManager.getImplementation().canImportDocument(docId, user)) {
                        errors.addError(Errors.SEVERITY_WARNING, "Impossible d'importer ce type de document (" + docId + "), la base de donnée doit d'abord être mise à jour.");
                    }
                }
                if (errors.containsWarning()) {
                    return errors;
                }

                // Reconstruction du manifeste pour qu'il colle au volume
                TransformerFactory tf = FactoryProvider.getTransformerFactory();
                StreamSource source = new StreamSource(this.getClass().getClassLoader().getResourceAsStream(MANIFEST_PATCH_XSL_NAME));
                Transformer trans = tf.newTransformer(source);

                // Transformation du manifeste
                trans.setParameter("volume", volume.num);
                DOMResult domResult = new DOMResult();
                trans.transform(new DOMSource(manifest), domResult);

                // Recuperation du manifeste
                newManifeste = (Document)domResult.getNode();

                // Recuperation du nombre de document à importer
                Double fCount = (Double) xp.evaluate("count(//document[@volume=" + volume.num + "])", newManifeste, XPathConstants.NUMBER);
                isp.displayProgress((fCount.intValue() + 1));

                // Recuperation des documents ou actions à traiter 
                Object o = xp.evaluate("//document[@volume=" + volume.num + "] | //action[name(parent::node())!='on-delete']", newManifeste, XPathConstants.NODESET);
                String archiveName = (String) xp.evaluate("//volume[@num=" + volume.num + "]/@fichier", newManifeste, XPathConstants.STRING);

                // Mise à jour des manifestes découlant d'anciennes archives
                if (!DataLayerManager.getImplementation().updateManifeste(archiveName, getUser())) {
                    errors.addError(Errors.SEVERITY_WARNING, "Impossible d'importer ce document, les manifestes doivent être mis à jour au préalable.");
                    if (errors.containsWarning()) {
                        return errors;
                    }
                }

                // Parcourt de la liste de documents/actions
                if (o instanceof NodeList) {
                    NodeList nl = (NodeList) o;
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element document = (Element) nl.item(i);
                        if (document.getNodeName().equals("action")) {
                            doApplyAction(zf, document, xpf);
                        } else {
                            FileInfo fi = doImportDocument(zf, document);
                            if(fi!=null && fi.getInProcessException()==null) {
                                // Enrichissement du manifeste au niveau du document traité avec les données de l'import
                                document.setAttributeNS(document.getNamespaceURI(), "imported", "yes");
                                Element result = document.getOwnerDocument().createElementNS(document.getNamespaceURI(), "resultatimport");
                                // Attributs du résultat
                                Attr tempsImport = result.getOwnerDocument().createAttributeNS(result.getNamespaceURI(), "Duree");
                                Attr debutImport = result.getOwnerDocument().createAttributeNS(result.getNamespaceURI(), "Debut");
                                Attr finImport = result.getOwnerDocument().createAttributeNS(result.getNamespaceURI(), "Fin");
                                // Valeurs des attributs du résultat
                                tempsImport.setNodeValue(DateUtils.durationToString(fi.getDurationImport()));
                                debutImport.setNodeValue(DateUtils.formatXsDateTime(new Date(fi.getDebutImport())));
                                finImport.setNodeValue(DateUtils.formatXsDateTime(new Date(fi.getFinImport())));

// temporairement, on ne met plus le FileInfo dedans
                                //result.appendChild(fi.toXml(docsModel, document.getOwnerDocument().getDocumentElement()));
                                result.setAttributeNode(tempsImport);
                                result.setAttributeNode(debutImport);
                                result.setAttributeNode(finImport);

                                document.appendChild(result);
                                if(fi.getWarningCount()>0) {
                                    errors.addError(Errors.SEVERITY_WARNING, fi.getWarningCount()+" avertissement(s) pendant l'import de "+document.getAttribute("path"));
                                }
                            } else {
                                // le document n'a pas été importé
                                document.setAttributeNS(document.getNamespaceURI(), "imported", "no");
                                if(fi!=null)
                                    errors.addError(Errors.SEVERITY_ERROR, fi.getInProcessException().getCause().getMessage());
                            }
                        }
                    }
                }
                DataLayerManager.getImplementation().declareArchiveImported(volumeFile.getName(), user);

                // Enrichissement du manifeste avec les données de l'import
                Element manifeste = (Element) xp.evaluate("//manifeste", newManifeste, XPathConstants.NODE);
                Element result = manifeste.getOwnerDocument().createElementNS(manifeste.getNamespaceURI(), "resultatimport");

                // Attributs du résultat
                Attr tempsImport = manifeste.getOwnerDocument().createAttributeNS(manifeste.getNamespaceURI(), "Duree");
                Attr debutImport = manifeste.getOwnerDocument().createAttributeNS(manifeste.getNamespaceURI(), "Debut");
                Attr finImport = manifeste.getOwnerDocument().createAttributeNS(manifeste.getNamespaceURI(), "Fin");
                Attr userImport = manifeste.getOwnerDocument().createAttributeNS(manifeste.getNamespaceURI(), "User");
                Attr lastModifyImport = manifeste.getOwnerDocument().createAttributeNS(manifeste.getNamespaceURI(), "LastModify");
                // Valeurs des attributs du résultat
                tempsImport.setNodeValue(DateUtils.durationToString(fileInfo.getDurationImport()));
                debutImport.setNodeValue(DateUtils.formatXsDateTime(new Date(fileInfo.getDebutImport())));
                finImport.setNodeValue(DateUtils.formatXsDateTime(new Date(fileInfo.getFinImport())));
                userImport.setNodeValue(user.getId());
                lastModifyImport.setNodeValue(DateUtils.formatXsDateTime(new Date(fileInfo.getLastModify())));

                result.appendChild(fileInfo.toXml(docsModel, manifeste.getOwnerDocument().getDocumentElement()));
                result.setAttributeNode(tempsImport);
                result.setAttributeNode(debutImport);
                result.setAttributeNode(finImport);
                result.setAttributeNode(userImport);
                result.setAttributeNode(lastModifyImport);

                manifeste.appendChild(result);

                // Import du manifeste
                doImportManifeste(newManifeste, zf, xpf);
                
            } catch (Exception ex) {
                logger.error("", ex);
                if (ex instanceof ZipException) {
                    String msg = "Cette archive a probablement été renommée.\nSon nom d'orgine devait être \n\t" + volume.fileName + "\nElle ne peut être importée que si elle porte ce nom.";
                    boolean previousValue = isp.shouldDisplayFeedback();
                    errors.addError(Errors.SEVERITY_ERROR, msg);
                }
            } finally {
                try { zf.close(); } catch(IOException ioEx) {}
                cleanTempFiles();
            }
            currentVolume = null;
        }
        return errors;
    }

    protected FileInfo doImportDocument(ZipFile zf, Element document) throws IOException {
        String filePath = document.getAttribute("path");
        String type = document.getAttribute("type");
        String idColl = document.getAttribute("buIdCol");
        String libColl = document.getAttribute("buLibelle");
        String idBudg = document.getAttribute("buCode");
        String libBudg = "Budget Principal";
        String sTmp = document.getAttribute("libBudget");
        if (sTmp != null && sTmp.length() > 0) {
            libBudg = sTmp;
        }
        String codic = document.getAttribute("buCodic");
        String codColl = document.getAttribute("buCodeCol");
        String nomOrigine = document.getAttribute("nomOrigine");
        String idUnique = document.getAttribute("idUnique");
        String docId = null;
        if ("CGE".equals(type)) {
            docId = "cg-colloc";
        } else if ("CGETAT".equals(type)) {
            docId = "cg-etat";
        } else if ("PES".equals(type)) {
            docId = "pes-aller";
        } else if ("PJ".equals(type)) {
            docId = null;
        } else if ("ERTN".equals(type)) {
            docId = "ertn";
        } else if ("EDMN".equals(type)) {
            docId = "edmn";
        } else {
            docId = type;
        }
        String skipProperty = null;
        if (document.getAttribute("skip-if-exists") != null && document.getAttribute("skip-if-exists").length() > 0) {
            skipProperty = document.getAttribute("skip-if-exists");
        }
        if (skipProperty != null) {
            if ("true".equals(applicationProperties.getProperty(skipProperty))) {
                logger.info("skipping " + filePath + " because " + skipProperty + " exists.");
                return null;
            }
        }
        String overwriteRule = EtatImporteur.OVERWRITE_RULE_DEFAULT;
        if (document.getAttribute("default-overwrite") != null && document.getAttribute("default-overwrite").length() > 0) {
            overwriteRule = document.getAttribute("default-overwrite");
        }
        Pair coll = new Pair(idColl, libColl);
        Pair budg = new Pair(idBudg, libBudg);
        if (docId != null) {
            return doImportXmlDocument(docId, zf, filePath, coll, budg, overwriteRule);
        } else {
            return doImportPJ(zf, filePath, coll, nomOrigine, idUnique, overwriteRule);
//            return null;
        }
    }

    protected FileInfo doImportXmlDocument(String docId, ZipFile zf, String filePath, Pair collectivite, Pair budget, String overwriteRule) throws IOException {
        File source = null;
        try {
            source = extractFileFromZip(zf, filePath);
        } catch(Exception ex) {
            // on a pas réussi à extraire le fichier, l'import doit s'interrompre
            throw new RuntimeException(ex);
        }
        DocumentModel dm = documentsModel.getDocumentById(docId);
//        FileInfo fInfo = new FileInfo();
        try {
            Class clazz = Class.forName(dm.getImportClass());
            Constructor cc = clazz.getConstructor(XemeliosUser.class, PropertiesExpansion.class);
            Object obj = cc.newInstance(getUser(), applicationProperties);
            if (!(obj instanceof EtatImporteur)) {
                throw new DataConfigurationException("Cette classe n'est pas un importeur.\nLe fichier de configuration qui vous a été livré est certainement invalide.\nVeuillez contacter votre fournisseur.");
            }
            EtatImporteur ei = (EtatImporteur) obj;
            // WARNING : if one name per archive (and not one per volume), change this
            String archiveName = StringUtilities.extractFilenameFromPath(zf.getName());
            ei.setArchiveName(archiveName);
            ei.setImpSvcProvider(isp);
            ei.setOverwriteRule(overwriteRule);
            ei.setApplicationConfiguration(applicationProperties);

            ei.setDocument(dm);
            File[] fichiers = new File[]{source};
            ei.setFilesToImport(fichiers);

            isp.setEtatImporter(ei);
            isp.setCollectivite(collectivite);
            isp.setBudget(budget);

            ei.setManifeste(manifest);
            ei.run();
            if(ei.inProcessException!=null) {
                // il s'est passé un truc pourri, on regarde si il faut tout interrompre
                if(ei.inProcessException instanceof RuntimeException) {
                    // là c'est certain, il faut interrompre
                    throw ei.inProcessException;
                }
            }
            FileInfo fInfo = ei.getFileInfo();
            fInfo.setWarningCount(ei.getWarningCount());
            fileInfo.merge(ei.getFileInfo());
            return fInfo;
        } catch (Exception ex) {
            logger.error("importer", ex);
//            if(ex instanceof RuntimeException)
//                throw (RuntimeException)ex;
            return new FileInfo(ex);
        } finally {
            if (source.exists()) {
                source.delete();
            }
        }
    }

    protected FileInfo doImportManifeste(Document manif, ZipFile zf, XPathFactory xpf) throws IOException {
        File source = null;
        try {
            source = extractFileFromZip(zf, MANIFEST_FILENAME);
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
        DocumentModel dm = documentsModel.getDocumentById("manifeste");
        XPath xp = xpf.newXPath();

        String idColl = "0000";
        String libColl = "Traçabilité";
        String idBudg = "00";
        String libBudg = "--";

        Pair collectivite = new Pair(idColl, libColl);
        Pair budget = new Pair(idBudg, libBudg);
        File outputFile = null;
        try {
            TransformerFactory transfom = FactoryProvider.getTransformerFactory();
            outputFile = new File(FileUtils.getTempDir(), source.getName() + "-" + currentVolume.getName()+ "-" + DateUtils.formatDate(new java.util.Date(), "yyyyMMddHHmmssSSS") + ".xml");
            FileOutputStream fos = new FileOutputStream(outputFile);
            transfom.newTransformer().transform(new DOMSource(manif.getDocumentElement()), new StreamResult(fos));
            fos.flush();
            fos.close();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            Class clazz = Class.forName(dm.getImportClass());
            Constructor cc = clazz.getConstructor(XemeliosUser.class, PropertiesExpansion.class);
            Object obj = cc.newInstance(getUser(), applicationProperties);
            if (!(obj instanceof EtatImporteur)) {
                throw new DataConfigurationException("Cette classe n'est pas un importeur.\nLe fichier de configuration qui vous a été livré est certainement invalide.\nVeuillez contacter votre fournisseur.");
            }
            EtatImporteur ei = (EtatImporteur) obj;
            // WARNING : if one name per archive (and not one per volume), change this
            String archiveName = StringUtilities.extractFilenameFromPath(zf.getName());
            ei.setArchiveName(archiveName);
            ei.setImpSvcProvider(isp);
            ei.setOverwriteRule("never");
            ei.setApplicationConfiguration(applicationProperties);

            ei.setDocument(dm);
            File[] fichiers = new File[]{outputFile};
            ei.setFilesToImport(fichiers);

            isp.setCollectivite(collectivite);
            isp.setBudget(budget);
            ei.setCollectivite(collectivite);
            ei.setBudget(budget);
            isp.setEtatImporter(ei);

            ei.run();
            FileInfo fInfo = ei.getFileInfo();
            if(ei.getWarningCount()>0)
                fInfo.setWarningCount(ei.getWarningCount());
            return fileInfo;
        } catch (Exception ex) {
            logger.error("importer", ex);
            return new FileInfo();
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }
    }

    protected FileInfo doImportPJ(ZipFile zf, String filePath, Pair collectivite, String nomOrigine, String idUnique, String overwriteRule) throws IOException {
        File f = null;
        try {
            f = extractFileFromZip(zf, filePath);
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
        FileInfo fInfo = new FileInfo();
        try {
            // les PJ sont systématiquement écrasées
            DataImpl impl = DataLayerManager.getImplementation();
            fInfo.setDebutImport(System.currentTimeMillis());
            PJRef pj = new PJRef();
            pj.setCollectivite(collectivite.key);
            pj.setFileName(nomOrigine);
            pj.setPjName(idUnique);
            pj.setUncompressedSize(f.length());
            pj.setTmpFileName(f.getAbsolutePath());
            pj.setPath(filePath);
            // WARN : if one name per archive (and not one per volume), change this
//            impl.importPj(pj, zf.getName(), getUser());
            impl.importPj(pj, currentVolume.getName(), getUser());
            fInfo.incElement(new Marker("PJ", null));
        } catch (Exception ex) {
            logger.error("doImportPJ(ZipFile,String,Pair,Pair,String,String)", ex);
        } finally {
            if (f.exists()) {
                f.delete();
            }
            fInfo.setFinImport(System.currentTimeMillis());
        }
        return fInfo;
    }

    protected void doApplyAction(ZipFile zf, Element action, XPathFactory xpf) throws XPathExpressionException, IOException, Exception {
        String className = action.getAttribute("class");
        Class clazz = Class.forName(className);
        Object instance = clazz.newInstance();
        if (!(instance instanceof AbstractImportPatcherImpl)) {
            throw new Exception(className + " n'est pas un AbstractImportPatcherImpl");
        }
        AbstractImportPatcherImpl patcher = (AbstractImportPatcherImpl) instance;
        XPath xp = xpf.newXPath();
        Object o = xp.evaluate("parameter", action, XPathConstants.NODESET);
        if (o instanceof NodeList) {
            NodeList nl = (NodeList) o;
            for (int i = 0; i < nl.getLength(); i++) {
                Element param = (Element) nl.item(i);
                String paramName = param.getAttribute("name");
                String type = param.getAttribute("type");
                if ("java.io.File".equals(type)) {
                    String value = (String) xp.evaluate("./text()", param, XPathConstants.STRING);
                    ZipEntry ze = zf.getEntry(value);
                    File f = null;
                    try {
                        extractFileFromZip(zf, value);
                    } catch(Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    if (f != null) {
                        filesToDrop.add(f);
                        patcher.setParameter(paramName, f);
                    }
                } else if ("java.lang.String".equals(type)) {
                    String value = param.getAttribute("value");
                    if (value == null) {
                        value = (String) xp.evaluate("./text()", param, XPathConstants.STRING);
                    }
                    patcher.setParameter(paramName, value);
                } else if ("java.lang.Integer".equals(type)) {
                    String value = param.getAttribute("value");
                    if (value == null) {
                        value = (String) xp.evaluate("./text()", param, XPathConstants.STRING);
                    }
                    patcher.setParameter(paramName, Integer.valueOf(value));
                }
            }
        }
        patcher.setImportServiceProvider(isp);
        FileInfo info = patcher.run();
        fileInfo.merge(info);
    }

    protected File extractFileFromZip(ZipFile zf, String filePath) throws IOException {
        ZipEntry ze = zf.getEntry(filePath);
        File ret = new File(FileUtils.getTempDir(), filePath);
        File parent = ret.getParentFile();
        parent.mkdirs();
        InputStream is = zf.getInputStream(ze);
        OutputStream fos = new FileOutputStream(ret);
        byte[] buffer = new byte[1024];
        int read = is.read(buffer);
        do {
            if (read > 0) {
                fos.write(buffer, 0, read);
            }
            read = is.read(buffer);
        } while (read > 0);
        fos.flush();
        fos.close();
        filesToDrop.add(ret);
        while (!parent.equals(FileUtils.getTempDir())) {
            directoriesToDrop.add(parent);
            parent = parent.getParentFile();
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public Errors checkArchive() {
        Errors errors = new Errors();
        if (!archiveFirstFile.exists()) {
            errors.addError(Errors.SEVERITY_ERROR, "Le fichier est introuvable");
        } else {
            ZipFile zf = null;
            try {
                zf = new ZipFile(archiveFirstFile, ZipFile.OPEN_READ);
                ZipEntry ze = zf.getEntry(MANIFEST_FILENAME);
                if (ze == null) {
                    errors.addError(Errors.SEVERITY_ERROR, "Manifest (" + MANIFEST_FILENAME + ") non trouvé dans l'archive");
                } else {
                    DocumentBuilderFactory domFactory = FactoryProvider.getDocumentBuilderFactory();
                    DocumentBuilder builder = domFactory.newDocumentBuilder();
                    manifest = builder.parse(zf.getInputStream(ze));
                    XPathFactory xpf = FactoryProvider.getXPathFactory();
                    XPath xp = xpf.newXPath();
                    Object o = xp.evaluate("//volume", manifest, XPathConstants.NODESET);
                    TreeSet<Volume> volumes = new TreeSet<Volume>();
                    if (o instanceof NodeList) {
                        NodeList nl = (NodeList) o;
                        for (int i = 0; i < nl.getLength(); i++) {
                            Element volume = (Element) nl.item(i);
                            volumes.add(new Volume(volume));
                        }
                    }
                    int thisArchiveNum = 0;
                    for (Volume v : volumes) {
                        if (v.fileName.equals(archiveFirstFile.getName())) {
                            thisArchiveNum = v.num;
                            break;
                        }
                    }
                    arch = new Archive();
                    arch.setVolumes(volumes);
                    // on verifie si tous les volumes sont presents
                    // ce contrôle n'est pas fait si Constants
                    if("true".equals(applicationProperties.getProperty(Constants.SYS_PROP_ARCH_AVOID_CHECK)))
                        return errors;
                    File directory = archiveFirstFile.getParentFile();
                    for (Volume v : volumes) {
                        if (v.num != 1) {
                            File archive = new File(directory, v.fileName);
                            if (!archive.exists()) {
                                arch.removeVolume(v);
                                if (thisArchiveNum == 1) {
                                    errors.addError(Errors.SEVERITY_WARNING, v.fileName + " introuvable");
                                }
                            }
                        }
                    }
                }
            } catch (ZipException zEx) {
                errors.addError(Errors.SEVERITY_ERROR, archiveFirstFile.getName() + " : archive zip corrompue.");
                logger.info("archive zip corrompue:", zEx);
            } catch (Exception ex) {
                errors.addError(Errors.SEVERITY_ERROR, ex.getMessage());
            } finally {
                if(zf!=null) {
                    try {
                        zf.close();
                    } catch (IOException ex) {
                        // on ignore
                    }
                }
            }
        }
        return errors;
    }

    protected void cleanTempFiles() {
        for (File f : filesToDrop) {
            f.delete();
        }
        for (File f : directoriesToDrop) {
            f.delete();
        }
        filesToDrop = new ArrayList<File>();
        directoriesToDrop = new ArrayList<File>();
    }

    public DocumentsModel getDocumentsModel() {
        return documentsModel;
    }

    private class Volume implements Comparable {

        String fileName;
        int num = 0;

        public Volume(Element el) {
            fileName = el.getAttribute("fichier");
            String str = el.getAttribute("num");
            try {
                num = Integer.parseInt(str);
            } catch (NumberFormatException nfEx) {
            }
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof Volume) {
                return num - ((Volume) o).num;
            }
            return 1;
        }
    }

    private class Archive {

        TreeSet<Volume> volumes;

        public Archive() {
            super();
            volumes = new TreeSet<Volume>();
        }

        public void setVolumes(Set<Volume> vols) {
            volumes.addAll(vols);
        }

        public void removeVolume(Volume vol) {
            volumes.remove(vol);
        }
    }
}
