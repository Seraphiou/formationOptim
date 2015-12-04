/*
 * Copyright
 *  2012 axYus - http://www.axyus.com
 *  2012 C.Marchand - christophe.marchand@axyus.com
 *
 * This file is part of XEMELIOS_NB.
 *
 * XEMELIOS_NB is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * XEMELIOS_NB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS_NB; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package fr.gouv.finances.dgfip.xemelios.importers.archives;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import fr.gouv.finances.dgfip.utils.DateUtils;
import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import fr.gouv.finances.dgfip.xemelios.auth.UnauthorizedException;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.Constants;
import fr.gouv.finances.dgfip.xemelios.common.FileInfo;
import fr.gouv.finances.dgfip.xemelios.common.PJRef;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentsModel;
import fr.gouv.finances.dgfip.xemelios.common.config.EtatModel.Marker;
import fr.gouv.finances.dgfip.xemelios.data.DataAccessException;
import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;
import fr.gouv.finances.dgfip.xemelios.data.DataLayerManager;
import fr.gouv.finances.dgfip.xemelios.importers.AbstractImportPatcherImpl;
import fr.gouv.finances.dgfip.xemelios.importers.EtatImporteur;
import fr.gouv.finances.dgfip.xemelios.importers.ImportServiceProvider;
import fr.gouv.finances.dgfip.xemelios.importers.archives.rules.DeleteModel;
import fr.gouv.finances.dgfip.xemelios.importers.archives.rules.XomDefinitionable;
import fr.gouv.finances.dgfip.xemelios.importers.archives.rules.ImportModel;
import fr.gouv.finances.dgfip.xemelios.utils.Errors;
import fr.gouv.finances.dgfip.xemelios.importers.archives.rules.RulesModel;
import fr.gouv.finances.dgfip.xemelios.importers.archives.rules.SectionModel;
import fr.gouv.finances.dgfip.xemelios.utils.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.xpath.XPathExpressionException;
import net.sf.saxon.option.xom.DocumentWrapper;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.XPathContext;
import nu.xom.converters.DOMConverter;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * L'importeur d'archive, nouvelle mouture.
 * Il sait traiter avec des règles
 * @author cmarchand
 */
public class ArchiveImporter {
    public static final transient String MANIFESTE_FILE_NAME = "manifeste_XeMeLios.xml";
    public static final transient String NON_DISPONIBLE = "Non disponible";
    public static final transient String NON = "Non";
    public static final transient String OUI = "Oui";
    private static final int OVERWRITE_RULE_UNSET = -1;
    private static final int OVERWRITE_RULE_OVERWRITE = 0;
    private static final int OVERWRITE_RULE_SKIP = 1;
    protected static final Logger logger = Logger.getLogger(ArchiveImporter.class);
    private DocumentsModel documentsModel;
    private File fileToImport;
    private ImportServiceProvider importServiceProvider;
    private PropertiesExpansion applicationProperties;
    private RulesModel rules;
    private XemeliosUser user;

    private static XPathContext nsCtx = null;

    // les fichiers qui devront être supprimes
    private ArrayList<File> filesToDrop;
    private Document archiveManifeste = null;
    private Document importedArchiveManifeste = null;
    private FileInfo __fileInfo;

    private File localTempDir = null;
    private boolean isLocalTempDirSet=false;

    public ArchiveImporter(DocumentsModel dms, File fToImport, ImportServiceProvider isp, PropertiesExpansion applicationProperties, RulesModel rules) {
        super();
        this.documentsModel = dms;
        this.fileToImport = fToImport;
        this.importServiceProvider = isp;
        this.applicationProperties = applicationProperties;
        this.rules = rules;
        filesToDrop = new ArrayList<File>();
    }

    public Errors doImport() {
        Errors errors = new Errors();
        try {
            ZipFile zipArchive = new ZipFile(fileToImport);
            ZipEntry manifesteEntry = zipArchive.getEntry(MANIFESTE_FILE_NAME);
            archiveManifeste = getManisfesteFromArchive(zipArchive.getInputStream(manifesteEntry));
            archiveManifeste.getRootElement().addAttribute(new Attribute("archive-name",getArchiveName(fileToImport)));
            zipArchive.close();
            HashMap<String, Object> importProperties = extractPropertiesFromArchiveManifeste(archiveManifeste);
            for(String docType: (String[])importProperties.get("archiveDocumentTypes")) {
                if (!docType.equals("PJ") && !DataLayerManager.getImplementation().canImportDocument(docType, getUser())) {
                    errors.addError(Errors.SEVERITY_WARNING, "Impossible d'importer ce type de document (" + docType + "), la base de donnée doit d'abord être mise à jour.");
                }
            }
            importedArchiveManifeste = DataLayerManager.getImplementation().getManifesteFromArchive(importProperties.get("archiveName").toString(), getUser());
            definePropertiesFromImportedManifeste(importedArchiveManifeste, importProperties);
            Element historique = null;
            if(importedArchiveManifeste!=null) {
                historique = (Element)importedArchiveManifeste.query("/m:manifeste/m:evenements", getNamespaceCtx()).get(0);
                // pour avoir un élément sans parent et pouvoir l'ajouter où on veut
                historique = new Element(historique);
            } else {
                historique = new Element("evenements");
            }
            archiveManifeste.getRootElement().appendChild(historique);
            boolean sectionApplied = false;
            for(SectionModel section:rules.getSections()) {
                SectionModel theSection = section;
                if(theSection.getPredicat().matches(importProperties)) {
                    logger.info("Application de la règle "+theSection.getName());
                    int globalOverwriteRule = OVERWRITE_RULE_UNSET;
                    if(theSection.getActions().isUsePreviousSection()) {
                        // alors il y a forcément un manifeste importé, et on va aller chercher sa section
                        Element sectionElement = (Element)importedArchiveManifeste.query("/m:manifeste/rul:section",getNamespaceCtx()).get(0);
                        if(sectionElement==null) throw new ImportException(new Errors.Error(Errors.SEVERITY_ERROR, "la section "+theSection.getName()+" impose l'application de la section du précédente import, mais celui-ci n'a pas été trouvé."), null);
                        theSection = new SectionModel(sectionElement);
                        // et on supprime toutes les données de l'archive
                        HashMap<String,DocumentModel> docsToDrop = new HashMap<String, DocumentModel>();
                        for(String docId:(String[])importProperties.get("archiveImportedDocumentTypes")) {
                            docsToDrop.put(docId, documentsModel.getDocumentById(docId));
                        }
                        DataLayerManager.getImplementation().removeArchive(docsToDrop, importProperties.get("archiveName").toString(), getUser());
                        Nodes deleteActions = importedArchiveManifeste.query("/m:manifeste/m:on-delete/m:action", getNamespaceCtx());
                        for(int i=0; i<deleteActions.size();i++) {
                            Element action = (Element)deleteActions.get(i);
                            doApplyAction(action);
                        }
                        // a ce stade, aucune mise à jour à faire dans le manifeste, tous les documents sont supprimés
                    } else {
                        if(importedArchiveManifeste!=null) {
                            // il faut reprendre l'historique de chacun des documents
                            Nodes importedDocuments = importedArchiveManifeste.query("//m:document", getNamespaceCtx());
                            for(int i=0;i<importedDocuments.size();i++) {
                                Element importedDoc = (Element)importedDocuments.get(i);
                                Element thisDoc = getElement(archiveManifeste.query("/manifeste/documents/document[@path='"+importedDoc.getAttributeValue("path")+"']"));
                                if(thisDoc!=null) {
                                    String __imported = importedDoc.getAttributeValue("imported");
                                    thisDoc.addAttribute(new Attribute("imported", __imported));
                                    if("Oui".equals(__imported)) {
                                        Element result = getElement(importedDoc.query("m:resultatimport", getNamespaceCtx()));
                                        if(result!=null)
                                        thisDoc.appendChild(new Element(result));
                                    }
                                }
                            }
                        }
                    }
                    if(theSection.getOverwriteRule()!=null) {
                        if(EtatImporteur.OVERWRITE_RULE_ALWAYS.equals(theSection.getOverwriteRule())) globalOverwriteRule = OVERWRITE_RULE_OVERWRITE;
                        else if(EtatImporteur.OVERWRITE_RULE_NEVER.equals(theSection.getOverwriteRule())) globalOverwriteRule = OVERWRITE_RULE_SKIP;
                    }
                    // on récupère la liste des documents à importer pour pouvoir contrôler qu'ils sont bien disponibles
                    List<Element> documentsToImport = theSection.getDocumentsToImport(archiveManifeste, applicationProperties);
                    if(documentsToImport.size()>0) {
                        TreeSet<String> volumesRequired = new TreeSet<String>();
                        for(Element filePlannedToImport:documentsToImport) {
                            volumesRequired.add(filePlannedToImport.getAttributeValue("volume"));
                        }
                        int maxVolumes = Integer.parseInt(volumesRequired.last());
                        File[] volumes = new File[maxVolumes+1];
                        for(String volume:volumesRequired) {
                            String fichierVolume = archiveManifeste.query("/manifeste/volumes/volume[@num="+volume+"]/@fichier").get(0).getValue();
                            File f = new File(fileToImport.getParentFile(),fichierVolume);
                            if(!f.exists()) {
                                errors.addError(Errors.SEVERITY_ERROR, f.getAbsolutePath()+" non trouvé");
                            }
                            volumes[Integer.parseInt(volume)] = f;
                        }
                        if(!errors.containsError()) {
    logger.info("displayProgress("+(documentsToImport.size()+1)+")");
    // ici, on ouvre une porte pour permettre de faire des modifs dans l'ArchiveImporteur
    preImport(theSection, archiveManifeste);
                            importServiceProvider.displayProgress(documentsToImport.size()+1);
                            boolean doImport = false;
                            boolean doDelete = false;
                            // on traite les actions
                            for(XomDefinitionable dd:theSection.getActions().getActions()) {
                                if(dd instanceof ImportModel) {
                                    //ImportModel im = (ImportModel)dd;
                                    // on a déjà déterminé la liste des fichiers à importer, donc on les importe
                                    for(Element documentToImport: documentsToImport) {
                                        int vol = Integer.parseInt(documentToImport.getAttributeValue("volume"));
                                        try {
                                            FileInfo fileInfo = doImportDocument(documentToImport, volumes[vol], importProperties, globalOverwriteRule);
                                            if(fileInfo.getInProcessException()!=null)
                                                errors.addError(Errors.SEVERITY_ERROR, fileInfo.getInProcessException().getMessage());
                                            if(__fileInfo==null) __fileInfo = fileInfo;
                                            else __fileInfo.merge(fileInfo);
                                            if(fileInfo.getGlobalCount()==0) {
                                                // rien n'a été importé, probablement parce que overwrite rule disait
                                                // qu'il ne fallait pas importer. Donc on ne modifie rien.
                                            } else {
                                                Element result = new Element("resultatimport");
                                                result.addAttribute(new Attribute("Duree", DateUtils.durationToString(fileInfo.getDurationImport())));
                                                result.addAttribute(new Attribute("Debut", DateUtils.formatXsDateTime(new Date(fileInfo.getDebutImport()))));
                                                result.addAttribute(new Attribute("Fin", DateUtils.formatXsDateTime(new Date(fileInfo.getFinImport()))));
                                                // on supprime le précédent résultat d'import, si il existait...
                                                Nodes previousResults = documentToImport.query("resultatimport | m:resultatimport", getNamespaceCtx());
                                                for(int i=previousResults.size()-1;i>=0;i--) {
                                                    Element __res = (Element)previousResults.get(i);
                                                    documentToImport.removeChild(__res);
                                                }
                                                documentToImport.insertChild(result,0);
                                                documentToImport.addAttribute(new Attribute("imported","Oui"));
                                            }
                                            // on applique les éventuelles actions portant sur ce document
                                            Nodes actions = archiveManifeste.query("/manifeste/action[@depends-on='"+documentToImport.getAttributeValue("path")+"']");
                                            for(int i=0;i<actions.size();i++) {
                                                Element action = (Element)actions.get(i);
                                                try {
                                                    FileInfo actFileInfo = doApplyAction(action);
                                                    if(__fileInfo==null) __fileInfo = actFileInfo;
                                                    else __fileInfo.merge(actFileInfo);
                                                } catch(Exception ex) {
                                                    logger.error("while applying "+action.toXML(),ex);
                                                }
                                            }
                                        } catch(Exception ex) {
                                            logger.error("while importing "+documentToImport.getAttributeValue("path"),ex);
                                            documentToImport.addAttribute(new Attribute("imported","Erreur: "+ex.getMessage()));
                                        }
                                    }
                                    doImport = true;
                                } else if(dd instanceof DeleteModel) {
                                    importServiceProvider.startLongWait();
                                    DeleteModel dm = (DeleteModel)dd;
                                    if(dm.getArchive()!=null) {
                                        String archiveName = null;
                                        if("archiveName".equals(dm.getArchive())) {
                                            // on remplace par le nom de l'archive
                                            archiveName = importProperties.get("archiveName").toString();
                                            // pour le moment, on n'autorise pas la suppression d'une autre archive
                                            HashMap<String,DocumentModel> map = new HashMap<String,DocumentModel>();
                                            for(String s:(String[])importProperties.get("archiveDocumentTypes")) {
                                                map.put(s, documentsModel.getDocumentById(s));
                                            }
                                            DataLayerManager.getImplementation().removeArchive(map, archiveName, getUser());
                                            Nodes documents = archiveManifeste.query("/manifeste/documents/document");
                                            for(int i=0;i<documents.size();i++) {
                                                Element doc = (Element)documents.get(i);
                                                Element resultImport = getElement(doc.query("m:resultatimport | resultatimport", getNamespaceCtx()));
                                                if(resultImport!=null) doc.removeChild(resultImport);
                                                doc.addAttribute(new Attribute("imported","Non"));
                                            }
                                            // on applique toutes les actions, puisqu'on a supprimé tous les documents
                                            Nodes actions = archiveManifeste.query("/manifeste/on-delete/action[@depends-on]");
                                            for(int i=0;i<actions.size();i++) {
                                                Element action = (Element)actions.get(i);
                                                try {
                                                    FileInfo fileInfo = doApplyAction(action);
                                                    if(__fileInfo==null) __fileInfo = fileInfo;
                                                    else __fileInfo.merge(fileInfo);
                                                } catch(Exception ex) {
                                                    logger.error("while applying "+action.toXML(),ex);
                                                }
                                            }
                                        } else if(dm.getTypeDoc()!=null) {
                                            if(dm.getCollectivite()!=null) {
                                                if(dm.getBudget()!=null) {
                                                    if(dm.getFileName()!=null) {
                                                        DataLayerManager.getImplementation().removeDocument(documentsModel.getDocumentById(dm.getTypeDoc()), new Pair(dm.getCollectivite(),dm.getCollectivite()), new Pair(dm.getBudget(),dm.getBudget()), dm.getFileName(), user);
                                                        Nodes documents = archiveManifeste.query("/manifeste/documents/document[@type='"+dm.getTypeDoc()+"' and @buIdCol='"+dm.getCollectivite()+"' and @buCode='"+dm.getBudget()+"' and ends-with(@path,'"+dm.getFileName()+"')]");
                                                        for(int i=0;i<documents.size();i++) {
                                                            Element doc = (Element)documents.get(i);
                                                            Element resultImport = getElement(doc.query("m:resultatimport | resultatimport", getNamespaceCtx()));
                                                            if(resultImport!=null) doc.removeChild(resultImport);
                                                            doc.addAttribute(new Attribute("imported","Non"));
                                                        }
                                                        // on applique les actions du document
                                                        Nodes actions = archiveManifeste.query("/manifeste/on-delete/action[ends-with(@depends-on,'"+dm.getFileName()+"')]");
                                                        for(int i=0;i<actions.size();i++) {
                                                            Element action = (Element)actions.get(i);
                                                            try {
                                                                FileInfo fileInfo = doApplyAction(action);
                                                                if(__fileInfo==null) __fileInfo = fileInfo;
                                                                else __fileInfo.merge(fileInfo);
                                                            } catch(Exception ex) {
                                                                logger.error("while applying "+action.toXML(),ex);
                                                            }
                                                        }
                                                    } else {
                                                        DataLayerManager.getImplementation().removeBudget(documentsModel.getDocumentById(dm.getTypeDoc()), new Pair(dm.getCollectivite(),dm.getCollectivite()), new Pair(dm.getBudget(),dm.getBudget()), user);
                                                        Nodes documents = archiveManifeste.query("/manifeste/documents/document[@type='"+dm.getTypeDoc()+"' and @buIdCol='"+dm.getCollectivite()+"' and @buCode='"+dm.getBudget()+"']");
                                                        for(int i=0;i<documents.size();i++) {
                                                            Element doc = (Element)documents.get(i);
                                                            Element resultImport = getElement(doc.query("m:resultatimport | resultatimport", getNamespaceCtx()));
                                                            if(resultImport!=null) doc.removeChild(resultImport);
                                                            doc.addAttribute(new Attribute("imported","Non"));
                                                            // on applique les actions du document
                                                            Nodes actions = archiveManifeste.query("/manifeste/on-delete/action[@depends-on='"+doc.getAttributeValue("path")+"']");
                                                            for(int a=0;a<actions.size();a++) {
                                                                Element action = (Element)actions.get(a);
                                                                try {
                                                                    FileInfo fileInfo = doApplyAction(action);
                                                                    if(__fileInfo==null) __fileInfo = fileInfo;
                                                                    else __fileInfo.merge(fileInfo);
                                                                } catch(Exception ex) {
                                                                    logger.error("while applying "+action.toXML(),ex);
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    DataLayerManager.getImplementation().removeCollectivite(documentsModel.getDocumentById(dm.getTypeDoc()), new Pair(dm.getCollectivite(),dm.getCollectivite()), user);
                                                    Nodes documents = archiveManifeste.query("/manifeste/documents/document[@type='"+dm.getTypeDoc()+"' and @buIdCol='"+dm.getCollectivite()+"']");
                                                    for(int i=0;i<documents.size();i++) {
                                                        Element doc = (Element)documents.get(i);
                                                        Element resultImport = getElement(doc.query("m:resultatimport | resultatimport", getNamespaceCtx()));
                                                        if(resultImport!=null) doc.removeChild(resultImport);
                                                        doc.addAttribute(new Attribute("imported","Non"));
                                                        // on applique les actions du document
                                                        Nodes actions = archiveManifeste.query("/manifeste/on-delete/action[@depends-on='"+doc.getAttributeValue("path")+"']");
                                                        for(int a=0;a<actions.size();a++) {
                                                            Element action = (Element)actions.get(a);
                                                            try {
                                                                FileInfo fileInfo = doApplyAction(action);
                                                                if(__fileInfo==null) __fileInfo = fileInfo;
                                                                else __fileInfo.merge(fileInfo);
                                                            } catch(Exception ex) {
                                                                logger.error("while applying "+action.toXML(),ex);
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                DataLayerManager.getImplementation().removeDocumentModel(documentsModel.getDocumentById(dm.getTypeDoc()), user);
                                                Nodes documents = archiveManifeste.query("/manifeste/documents/document[@type='"+dm.getTypeDoc()+"']");
                                                for(int i=0;i<documents.size();i++) {
                                                    Element doc = (Element)documents.get(i);
                                                    Element resultImport = getElement(doc.query("m:resultatimport | resultatimport", getNamespaceCtx()));
                                                    if(resultImport!=null) doc.removeChild(resultImport);
                                                    doc.addAttribute(new Attribute("imported","Non"));
                                                    // on applique les actions du document
                                                    Nodes actions = archiveManifeste.query("/manifeste/on-delete/action[@depends-on='"+doc.getAttributeValue("path")+"']");
                                                    for(int a=0;a<actions.size();a++) {
                                                        Element action = (Element)actions.get(a);
                                                        try {
                                                            FileInfo fileInfo = doApplyAction(action);
                                                            if(__fileInfo==null) __fileInfo = fileInfo;
                                                            else __fileInfo.merge(fileInfo);
                                                        } catch(Exception ex) {
                                                            logger.error("while applying "+action.toXML(),ex);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    doDelete = true;
                                }
                                importServiceProvider.endLongWait();
                            }
                            
                            if(doImport) {
                                // Pour compatibilité avec les archives avant 2011, on traite toutes les actions qui ne sont pas on-delete et qui n'ont pas de depends-on
                                Nodes actions = archiveManifeste.query("/manifeste/action[not(@depends-on)]");
                                for(int i=0;i<actions.size();i++) {
                                    Element action = (Element)actions.get(i);
                                    try {
                                        FileInfo fileInfo = doApplyAction(action);
                                        if(__fileInfo==null) __fileInfo = fileInfo;
                                        else __fileInfo.merge(fileInfo);
                                    } catch(Exception ex) {
                                        logger.error("while applying "+action.toXML(),ex);
                                    }
                                }
                            }
                            
                            if(doImport) {
                                // Pour les patchs edmn on applique les actions
                                Nodes actions = archiveManifeste.query("/manifeste/actions/action");
                                for(int i=0;i<actions.size();i++) {
                                    Element action = (Element)actions.get(i);
                                    try {
                                        FileInfo fileInfo = doApplyAction(action);
                                        if(__fileInfo==null) __fileInfo = fileInfo;
                                        else __fileInfo.merge(fileInfo);
                                    } catch(Exception ex) {
                                        logger.error("while applying "+action.toXML(),ex);
                                    }
                                }
                            }
                            
                            if(doDelete) {
                                // Pour compatibilité avec les archives avant 2011, on traite toutes les actions qui ne sont pas on-delete et qui n'ont pas de depends-on
                                Nodes actions = archiveManifeste.query("/manifeste/on-delete/action[not(@depends-on)]");
                                for(int i=0;i<actions.size();i++) {
                                    Element action = (Element)actions.get(i);
                                    try {
                                        FileInfo fileInfo = doApplyAction(action);
                                        if(__fileInfo==null) __fileInfo = fileInfo;
                                        else __fileInfo.merge(fileInfo);
                                    } catch(Exception ex) {
                                        logger.error("while applying "+action.toXML(),ex);
                                    }
                                }
                            }
                        }
                        // définir ici si il y a des données ou non
                        if(archiveManifeste.query("/manifeste/documents/document[@imported='Oui']").size()>0)
                            archiveManifeste.getRootElement().addAttribute(new Attribute("added:archive", Constants.ADDED_NS_URI,"Oui"));
                        else
                            archiveManifeste.getRootElement().addAttribute(new Attribute("added:archive", Constants.ADDED_NS_URI,"Non"));
                        // on ajoute les actions que l'on a pratiqué dans le manifeste,
                        // pour savoir quoi refaire plus tard...
                        archiveManifeste.getRootElement().appendChild(theSection.getXomDefinition());
                        sectionApplied = true;
                        break;
                    } else {
                        // il n'y avait rien à importer, mais la section a quand même été importée
                        sectionApplied = true;
                        break;
                    }
                }
            }
            if(sectionApplied) {
                // on a traité toutes les sections, peut-être il ne s'est rien passé...
                boolean somethingHasBeenImported = false;
                Nodes nodes = archiveManifeste.query("//document[@imported]");
                somethingHasBeenImported = nodes.size()>0;
                // on recherche tous les documents qui n'ont pas été traités, et on positionne un @imported='false'
                nodes = archiveManifeste.query("//document[not(@imported)]");
                for(int i=0;i<nodes.size();i++) {
                    Element el = (Element)nodes.get(i);
                    el.addAttribute(new Attribute("imported","Non"));
                }
                archiveManifeste.getRootElement().addAttribute(new Attribute("imported",Boolean.toString(somethingHasBeenImported)));
                Element result = new Element("resultatimport");
                if(result!=null) {
                    // on sait jamais... en cas de suppression par exemple...
                    if(__fileInfo!=null) {
                        result.addAttribute(new Attribute("Duree",DateUtils.durationToString(__fileInfo.getDurationImport())));
                        result.addAttribute(new Attribute("Debut", DateUtils.formatXsDateTime(new Date(__fileInfo.getDebutImport()))));
                        result.addAttribute(new Attribute("Fin", DateUtils.formatXsDateTime(new Date(__fileInfo.getFinImport()))));
                        result.addAttribute(new Attribute("User", getUser().getId()));
                        result.addAttribute(new Attribute("LastModify", DateUtils.formatXsDateTime(new Date(__fileInfo.getLastModify()))));
                        result.appendChild(__fileInfo.toXomXml(documentsModel));
                    }
                }
                archiveManifeste.getRootElement().appendChild(result);
                // l'historique des imports
                Element event = new Element("evenement");
                event.addAttribute(new Attribute("date",DateUtils.formatXsDateTime(new Date())));
                event.addAttribute(new Attribute("user", getUser().getId()));
                event.addAttribute(new Attribute("section", archiveManifeste.query("/manifeste/rul:section/@name", getNamespaceCtx()).get(0).getValue()));
                String version = archiveManifeste.getRootElement().getAttributeValue("version");
                if(version==null || version.length()==0) version = "1";
                event.addAttribute(new Attribute("version-archive", version));
                historique.insertChild(event, 0);
                doImportManifeste(archiveManifeste, importProperties.get("archiveName").toString());
                DataLayerManager.getImplementation().declareArchiveImported(importProperties.get("archiveName").toString(), user);
//                System.out.println(archiveManifeste.toXML());
            } else {
                errors.addError(Errors.SEVERITY_WARNING, "Cette archive ne peut être importée par aucune des règles de cette configuration.");
            }
        } catch (XPathExpressionException ex) {
            logger.error(ex.getMessage(),ex);
            errors.addError(Errors.SEVERITY_ERROR, ex.getMessage());
        } catch (IOException ioEx) {
            logger.error(fileToImport.getAbsolutePath()+": "+ioEx.getMessage(),ioEx);
            errors.addError(Errors.SEVERITY_ERROR, ioEx.getMessage());
        } catch(ImportException iEx) {
            Errors.Error error = iEx.error;
            errors.addError(error);
        } catch(DataConfigurationException dcEx) {
            logger.error(dcEx.getMessage(),dcEx);
            errors.addError(Errors.SEVERITY_ERROR, dcEx.getMessage());
        } catch(DataAccessException daEx) {
            logger.error(daEx.getMessage(),daEx);
            errors.addError(Errors.SEVERITY_ERROR, daEx.getMessage());
        } catch(UnauthorizedException ex) {
            logger.error(ex.getMessage(),ex);
            errors.addError(Errors.SEVERITY_ERROR, ex.getMessage());
        } catch(Throwable t) {
            t.printStackTrace();
            errors.addError(Errors.SEVERITY_ERROR, t.getMessage());
        } finally {
//            try { zipArchive.close(); } catch(Exception ex) {}
        }
        return errors;
    }

    protected FileInfo doImportDocument(Element document, File volume, HashMap<String, Object> importProperties, int overwriteRule) throws IOException {
        String filePath = document.getAttributeValue("path");
        String type = document.getAttributeValue("type");
        String idColl = document.getAttributeValue("buIdCol");
        String libColl = document.getAttributeValue("buLibelle");
        String idBudg = document.getAttributeValue("buCode");
        String libBudg = "Budget Principal";
        String nomOrigine = document.getAttributeValue("nomOrigine");
        String idUnique = document.getAttributeValue("idUnique");
        String sTmp = document.getAttributeValue("libBudget");
        if (sTmp != null && sTmp.length() > 0) {
            libBudg = sTmp;
        }
        String docId = normalizeDocumentType(type);
        String skipProperty = null;
        if (document.getAttributeValue("skip-if-exists") != null && document.getAttributeValue("skip-if-exists").length() > 0) {
            skipProperty = document.getAttributeValue("skip-if-exists");
        }
        if (skipProperty != null) {
            if ("true".equals(applicationProperties.getProperty(skipProperty))) {
                logger.info("skipping " + filePath + " because " + skipProperty + " exists.");
                return null;
            }
        }
        String overwriteBehavior = EtatImporteur.OVERWRITE_RULE_DEFAULT;
        if (document.getAttributeValue("default-overwrite") != null && document.getAttributeValue("default-overwrite").length() > 0) {
            overwriteBehavior = document.getAttributeValue("default-overwrite");
        }
        if(overwriteRule!=OVERWRITE_RULE_UNSET) {
            if(overwriteRule==OVERWRITE_RULE_OVERWRITE) overwriteBehavior = EtatImporteur.OVERWRITE_RULE_ALWAYS;
            else overwriteBehavior = EtatImporteur.OVERWRITE_RULE_NEVER;
        }
        Pair coll = new Pair(idColl, libColl);
        Pair budg = new Pair(idBudg, libBudg);
        if (docId != null && !"PJ".equals(docId)) {
            return doImportXmlDocument(docId, filePath, coll, budg, overwriteBehavior, document, volume, importProperties.get("archiveName").toString());
        } else {
            return doImportPJ(filePath, coll, nomOrigine, idUnique, document, volume, importProperties.get("archiveName").toString());
        }
    }
    protected FileInfo doImportXmlDocument(String docId, String filePath, Pair collectivite, Pair budget, String overwriteRule, Element document, File volume, String archiveName) throws IOException {
        File source = null;
        try {
            ZipFile zipArchive = new ZipFile(volume);
            source = extractFileFromZip(zipArchive, filePath);
            zipArchive.close();
        } catch(Exception ex) {
            // on a pas réussi à extraire le fichier, on l'indique dans le manifeste
            document.addAttribute(new Attribute("imported", NON_DISPONIBLE));

        }
        DocumentModel dm = documentsModel.getDocumentById(docId);
        try {
            Class clazz = Class.forName(dm.getImportClass());
            Constructor cc = clazz.getConstructor(XemeliosUser.class, PropertiesExpansion.class);
            Object obj = cc.newInstance(getUser(), applicationProperties);
            if (!(obj instanceof EtatImporteur)) {
                throw new DataConfigurationException("Cette classe n'est pas un importeur.\nLe fichier de configuration qui vous a été livré est certainement invalide.\nVeuillez contacter votre fournisseur.");
            }
            EtatImporteur ei = (EtatImporteur) obj;
            // WARNING : if one name per archive (and not one per volume), change this
            ei.setArchiveName(archiveName);
            ei.setImpSvcProvider(importServiceProvider);
            ei.setOverwriteRule(overwriteRule);
            ei.setApplicationConfiguration(applicationProperties);

            ei.setDocument(dm);
            File[] fichiers = new File[]{source};
            ei.setFilesToImport(fichiers);

            importServiceProvider.setEtatImporter(ei);
            importServiceProvider.setCollectivite(collectivite);
            importServiceProvider.setBudget(budget);

            ei.setManifeste(DOMConverter.convert(archiveManifeste, FactoryProvider.getDocumentBuilderFactory().newDocumentBuilder().getDOMImplementation()));
            ei.run();
            if(ei.getInProcessException()!=null) {
                // il s'est passé un truc pourri, on regarde si il faut tout interrompre
                document.addAttribute(new Attribute("imported", "Non"));
                if(ei.getInProcessException() instanceof RuntimeException) {
                    // là c'est certain, il faut interrompre
                    throw ei.getInProcessException();
                }
            }
            FileInfo fInfo = ei.getFileInfo();
            fInfo.setLastModify(volume.lastModified());
            fInfo.setWarningCount(ei.getWarningCount());
            document.addAttribute(new Attribute("imported", "Oui"));
            return fInfo;
        } catch (Exception ex) {
            logger.error("importer", ex);
            return new FileInfo(ex);
        } finally {
            if (source.exists()) {
                source.delete();
            }
        }
    }
    protected FileInfo doApplyAction(Element action) throws Exception {
        // l'action peut provenir d'un manifeste importé ou d'un manifeste neuf,
        // il faut gérer les deux namespaces
        String className = action.getAttributeValue("class");
        Class clazz = Class.forName(className);
        Object instance = clazz.newInstance();
        if (!(instance instanceof AbstractImportPatcherImpl)) {
            throw new Exception(className + " n'est pas un AbstractImportPatcherImpl");
        }
        AbstractImportPatcherImpl patcher = (AbstractImportPatcherImpl) instance;
        Nodes parameters = action.query("parameter | m:parameter", getNamespaceCtx());
        for (int i = 0; i < parameters.size(); i++) {
            Element param = (Element)parameters.get(i);
            String paramName = param.getAttributeValue("name");
            String type = param.getAttributeValue("type");
            if ("java.io.File".equals(type) && !"index.file".equals(paramName)) {
                String value = (String)param.query("text()").get(0).getValue();
                String volume = null;
                try {
                    volume = action.getDocument().query("//document[@path = '"+value+"']/@volume").get(0).getValue();
                } catch(Exception ex) {
                    volume = action.getDocument().query("//m:document[@path = '"+value+"']/@volume", getNamespaceCtx()).get(0).getValue();
                }
                if(volume==null) {
                    volume = action.getDocument().query("//m:document[@path = '"+value+"']/@volume", getNamespaceCtx()).get(0).getValue();
                }
                String archiveFileName = null;
                try {
                    archiveFileName = action.getDocument().query("//volume[@num = '"+volume+"']/@fichier").get(0).getValue();
                } catch(Exception ex) {
                    archiveFileName = action.getDocument().query("//m:volume[@num = '"+volume+"']/@fichier", getNamespaceCtx()).get(0).getValue();
                }
                if(archiveFileName==null)
                    archiveFileName = action.getDocument().query("//m:volume[@num = '"+volume+"']/@fichier", getNamespaceCtx()).get(0).getValue();
                File archiveFile = new File(fileToImport.getParentFile(),archiveFileName);
                ZipFile zf = new ZipFile(archiveFile);
                // ca ne peut pas marcher, il faudrait une référence au numéro de volume
                //ZipEntry ze = zf.getEntry(value);
                File f = null;
                try {
                    f = extractFileFromZip(zf, value);
                } catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
                if (f != null) {
                    filesToDrop.add(f);
                    patcher.setParameter(paramName, f);
                }
                zf.close();
            } else if ("java.io.File".equals(type) && "index.file".equals(paramName)) { // Pour les patchs de liasses edmn
                String value = (String)param.query("text()").get(0).getValue();
                int nbVolumes = action.getDocument().query("//volume").size();
                
                String archiveFileName = null;
                try {
                    archiveFileName = action.getDocument().query("//volume[@num = '" + nbVolumes + "']/@fichier").get(0).getValue();
                } catch(Exception ex) {
                    archiveFileName = action.getDocument().query("//m:volume[@num = '" + nbVolumes + "']/@fichier", getNamespaceCtx()).get(0).getValue();
                }
                if(archiveFileName==null) archiveFileName = action.getDocument().query("//m:volume[@num = '" + nbVolumes + "']/@fichier", getNamespaceCtx()).get(0).getValue();
                
                File archiveFile = new File(fileToImport.getParentFile(),archiveFileName);
                ZipFile zf = new ZipFile(archiveFile);
                // ca ne peut pas marcher, il faudrait une référence au numéro de volume
                //ZipEntry ze = zf.getEntry(value);
                File f = null;
                try {
                    f = extractFileFromZip(zf, value);
                } catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
                if (f != null) {
                    filesToDrop.add(f);
                    patcher.setParameter(paramName, f);
                }
                zf.close();
            } else if ("java.lang.String".equals(type)) {
                String value = param.getAttributeValue("value");
                if (value == null) {
                    value = param.query("text()").get(0).getValue();
                }
                patcher.setParameter(paramName, value);
            } else if ("java.lang.Integer".equals(type)) {
                String value = param.getAttributeValue("value");
                if (value == null) {
                    value = param.query("text()").get(0).getValue();
                }
                patcher.setParameter(paramName, Integer.valueOf(value));
            }
        }
        patcher.setImportServiceProvider(importServiceProvider);
        FileInfo info = patcher.run();
        return info;
    }
    protected FileInfo doImportManifeste(Document manif, String archiveName) throws IOException {
        DocumentModel dm = documentsModel.getDocumentById("manifeste2");
        String idColl = "0000";
        String libColl = "Traçabilité";
        String idBudg = "00";
        String libBudg = "--";

        Pair collectivite = new Pair(idColl, libColl);
        Pair budget = new Pair(idBudg, libBudg);
        if(importedArchiveManifeste!=null) {
            try {
                DataLayerManager.getImplementation().removeDocument(dm, budget, collectivite, archiveName+".xml", user);
            } catch(Exception ex) {
                logger.error("while dropping previous manifeste", ex);
            }
        }

        File outputFile = null;
        try {
            outputFile = new File(FileUtils.getTempDir(), archiveName+".xml");
            Charset cs = Charset.forName("UTF-8");
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outputFile),cs);
            osw.write(manif.toXML());
            osw.flush();
            osw.close();
            Class clazz = Class.forName(dm.getImportClass());
            Constructor cc = clazz.getConstructor(XemeliosUser.class, PropertiesExpansion.class);
            Object obj = cc.newInstance(getUser(), applicationProperties);
            if (!(obj instanceof EtatImporteur)) {
                throw new DataConfigurationException("Cette classe n'est pas un importeur.\nLe fichier de configuration qui vous a été livré est certainement invalide.\nVeuillez contacter votre fournisseur.");
            }
            EtatImporteur ei = (EtatImporteur) obj;
            // WARNING : if one name per archive (and not one per volume), change this
            ei.setArchiveName(archiveName);
            ei.setImpSvcProvider(importServiceProvider);
            importServiceProvider.setEtatImporter(ei);
            ei.setOverwriteRule("never");
            ei.setApplicationConfiguration(applicationProperties);

            ei.setDocument(dm);
            File[] fichiers = new File[]{outputFile};
            ei.setFilesToImport(fichiers);

            importServiceProvider.setCollectivite(collectivite);
            importServiceProvider.setBudget(budget);
            ei.setCollectivite(collectivite);
            ei.setBudget(budget);

            ei.run();
            FileInfo fInfo = ei.getFileInfo();
            if(ei.getWarningCount()>0)
                fInfo.setWarningCount(ei.getWarningCount());
            return fInfo;
        } catch (Exception ex) {
            logger.error("importer", ex);
            return new FileInfo();
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }
    }
    protected FileInfo doImportPJ(String filePath, Pair collectivite, String nomOrigine, String idUnique, Element document, File volume, String archiveName) throws IOException {
        File f = null;
        try {
            ZipFile zf = new ZipFile(volume);
            f = extractFileFromZip(zf, filePath);
            zf.close();
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
        FileInfo fInfo = new FileInfo();
        try {
            // les PJ sont systématiquement écrasées
            fInfo.setDebutImport(System.currentTimeMillis());
            PJRef pj = new PJRef();
            pj.setCollectivite(collectivite.key);
            pj.setFileName(nomOrigine);
            pj.setPjName(idUnique);
            pj.setUncompressedSize(f.length());
            pj.setTmpFileName(f.getAbsolutePath());
            pj.setPath(filePath);
            DataLayerManager.getImplementation().importPj(pj, archiveName, getUser());
            fInfo.incElement(new Marker("PJ", null));
            document.addAttribute(new Attribute("imported", "Oui"));
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

    protected Document getManisfesteFromArchive(InputStream is) throws ImportException {
        try {
            Builder builder = new Builder();
            Document ret = builder.build(is);
            return ret;
        } catch (ParsingException ex) {
            throw new ImportException(new Errors.Error(Errors.SEVERITY_ERROR, ex.getMessage()), ex);
        } catch (IOException ex) {
            throw new ImportException(new Errors.Error(Errors.SEVERITY_ERROR, ex.getMessage()), ex);
        }
    }
    protected HashMap<String, Object> extractPropertiesFromArchiveManifeste(Document archiveManifeste) throws XPathExpressionException {
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("archiveName", archiveManifeste.query("/manifeste/@archive-name").get(0).getValue());
        Nodes versionList = archiveManifeste.query("/manifeste/@version");
        // on peut ne pas avoir de version
        if(versionList.size()>0)
            props.put("archiveVersion", versionList.get(0).getValue());
        else
            props.put("archiveVersion", "1");
        props.put("archiveFile", fileToImport.getAbsolutePath());
        ArrayList<String> documentsTypes = new ArrayList<String>();
        Processor proc = new Processor(FactoryProvider.getSaxonConfiguration());
        try {
            XPathExecutable xpe = proc.newXPathCompiler().compile("distinct-values(//document/@type)");
            XPathSelector xps = xpe.load();
            xps.setContextItem(new XdmNode(new DocumentWrapper(archiveManifeste,"",FactoryProvider.getSaxonConfiguration())));
            for(Iterator<XdmItem> it = xps.iterator();it.hasNext();) {
                documentsTypes.add(normalizeDocumentType(it.next().getStringValue()));
            }
            String[] docTypesArray = new String[documentsTypes.size()];
            documentsTypes.toArray(docTypesArray);
            props.put("archiveDocumentTypes", docTypesArray);
        } catch(Exception ex) {
            props.put("archiveDocumentTypes", null);
            throw new XPathExpressionException(ex);
        }
        return props;
    }
    protected void definePropertiesFromImportedManifeste(Document importedArchiveManifeste, HashMap<String, Object> props) throws XPathExpressionException {
        if(importedArchiveManifeste==null) {
            props.put("archiveImported", false);
            props.put("archiveImportedVersion", null);
            props.put("archiveImportedDocumentTypes", null);
            props.put("archiveDataPresent", false);
            return;
        }
        props.put("archiveImported", true);
        // a l'import du manifeste dans XeMeLios, on change le namespace
        // la version peut être null
        String version = importedArchiveManifeste.getRootElement().getAttributeValue("version");
        if(version==null || version.length()==0) version = "1";
        props.put("archiveImportedVersion", version);
        Processor proc = new Processor(FactoryProvider.getSaxonConfiguration());
        try {
            ArrayList<String> documentsTypes = new ArrayList<String>();
            XPathCompiler xpc = proc.newXPathCompiler();
            xpc.declareNamespace("m", Constants.MANIFESTE_NS_URI);
            XPathExecutable xpe = xpc.compile("distinct-values(//m:document/@type)");
            XPathSelector xps = xpe.load();
            xps.setContextItem(new XdmNode(new DocumentWrapper(importedArchiveManifeste,"",FactoryProvider.getSaxonConfiguration())));
            for(Iterator<XdmItem> it = xps.iterator();it.hasNext();) {
                documentsTypes.add(normalizeDocumentType(it.next().getStringValue()));
            }
            String[] docTypesArray = new String[documentsTypes.size()];
            documentsTypes.toArray(docTypesArray);
            props.put("archiveImportedDocumentTypes", docTypesArray);
            props.put("archiveDataPresent", "Oui".equals(importedArchiveManifeste.query("/m:manifeste/@added:archive", getNamespaceCtx()).get(0).getValue()));
        } catch(Exception ex) {
            props.put("archiveImportedDocumentTypes", null);
            throw new XPathExpressionException(ex);
        }
    }
    /**
     * Normalise le type de document qu'on peut trouver dans un manifeste,
     * pour le faire correspondre aux véritables identifiants de type
     * de docuemnts.
     * @param docType
     * @return
     */
    public static String normalizeDocumentType(String docType) {
        if ("CGE".equals(docType)) {
            return "cg-colloc";
        } else if ("CGETAT".equals(docType)) {
            return "cg-etat";
        } else if ("PES".equals(docType)) {
            return "pes-aller";
        } else if ("EDMN".equals(docType)) {
            return "edmn";
        } else if ("ERTN".equals(docType)) {
            return "ertn";
        } else {
            return docType;
        }
    }

    /**
     * Cette fonction est appelée une fois la section à appliquer déterminée
     * et avant le premier import ou la première action.
     * Ici, on peut modifier la configuration de l'importeur avant de lancer
     * l'import.
     * @param section
     * @param archiveManifeste
     */
    public void preImport(SectionModel section, Document archiveManifeste) {

    }
    private class ImportException extends Exception {
        private Errors.Error error;
        public ImportException() {
            super();
        }
        public ImportException(Errors.Error error, Exception cause) {
            super(error.getMessage(),cause);
            this.error = error;
        }
    }

    public static XPathContext getNamespaceCtx() {
        if(nsCtx==null) {
            XPathContext ns = new XPathContext("m", Constants.MANIFESTE_NS_URI);
            ns.addNamespace("rul", Constants.RULES_NS_URI);
            ns.addNamespace("added", Constants.ADDED_NS_URI);
            nsCtx = ns;
        }
        return nsCtx;
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
    protected File extractFileFromZip(ZipFile zf, String filePath) throws IOException {
        ZipEntry ze = zf.getEntry(filePath);
        File ret = new File(getTempDir(), filePath);
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
        return ret;
    }
    public FileInfo getFileInfo() { return __fileInfo; }
    public DocumentsModel getDocumentsModel() { return documentsModel; }
    // un point d'entrée, pour les héritiers...
    protected void setDocumentsModel(DocumentsModel dm) { this.documentsModel = dm; }
    public File getTempDir() {
        if(isLocalTempDirSet) return localTempDir;
        else return FileUtils.getTempDir();
    }
    public static String getArchiveName(File fileToImport) {
        String fileName = fileToImport.getName();
        String extension = FilenameUtils.getExtension(fileName);
        String baseName = FilenameUtils.getBaseName(fileName);
        String shortName = baseName.replaceFirst("_[0-9]*$", "");
        return shortName+"."+extension;
    }
    public static String getImportMode(PropertiesExpansion applicationProperties) {
        String ret = Constants.IMPORT_ARCHIVE_TOTAL;
        String sTmp = applicationProperties.getProperty(Constants.SYS_PROP_IMPORT_ARCHIVE_MODE);
        if(sTmp!=null && Constants.IMPORT_ARCHIVE_PARTIEL.equals(sTmp)) ret = Constants.IMPORT_ARCHIVE_PARTIEL;
        return ret;
    }
    public void setLocalTempDir(File localTempDir) {
        this.localTempDir = localTempDir;
        isLocalTempDirSet = true;
    }

    public static Element getElement(Nodes nodes) {
        if(nodes.size()==0) return null;
        else return (Element)nodes.get(0);
    }
}
