/*
 * Copyright
 *   2007 axYus - www.axyus.com
 *   2007 J-P.Tessier - jean-philippe.tessier@axyus.com
 *   2009 - C.Marchand - christophe.marchand@axyus.com
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
package fr.gouv.finances.cp.xemelios.importers.batch;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.Constants;
import fr.gouv.finances.dgfip.xemelios.common.PJRef;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentsModel;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentsParser;
import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;
import fr.gouv.finances.dgfip.xemelios.data.DataLayerManager;
import fr.gouv.finances.dgfip.xemelios.importers.EtatImporteur;
import fr.gouv.finances.dgfip.xemelios.importers.archives.ArchiveImporter;
import fr.gouv.finances.dgfip.xemelios.importers.archives.rules.RulesModel;
import fr.gouv.finances.dgfip.xemelios.importers.archives.rules.RulesParser;
import fr.gouv.finances.dgfip.xemelios.utils.Errors;
import fr.gouv.finances.dgfip.xemelios.utils.FileUtils;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

/**
 * This class allow to import XML files with a batch import.
 * 
 * 
 * @since 3.1
 * 
 * @author jptessier,chm
 */
public class BatchRealImporter {
    private final int IMPORT_SUCCESS = 0;
    private final int IMPORT_WARNING = 5;
    private final int IMPORT_FAILURE = 10;
    private final int SYNTAX_ERROR = 25;

    private static Logger logger = null;
    private ArrayList<File> files;
    private Pair budg,  coll;
    private DocumentsModel docModels;
    private String documentid;
    private Vector<String> cmopts;
    private Options opts = null;
    private String[] arguments;
    private PosixParser posixparser;
    private boolean budgIsInterractif = false,  collIsInterractif = false;
    private String documentsDefDir;
    private XemeliosUser user;
    private ArrayList<File> filesToDrop;
    private PropertiesExpansion applicationConfiguration;
    private RulesModel archiveRules = null;

    public BatchRealImporter(PropertiesExpansion applicationConfiguration, String[] args) throws Exception {
        super();
        this.applicationConfiguration = applicationConfiguration;
        this.files = new ArrayList<File>();
        this.filesToDrop = new ArrayList<File>();
        this.opts = new Options();
        arguments = args;
        createLogger();
        setCommandLineOptions();
        posixparser = new PosixParser();
        CommandLine cm = posixparser.parse(opts, arguments);
        Option[] s = cm.getOptions();
        cmopts = new Vector<String>();
        for (int i = 0; i < s.length; i++) {
            cmopts.add(s[i].getOpt());
        }
        if (!cmopts.contains("h")) {
            // Chargement des fichiers de configuration
            if(cmopts.contains("g")) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i].matches("-g")) {
                        documentsDefDir = args[i + 1];
                        logger.debug("documents-def.dir =" + documentsDefDir);
                    }
                }
            }
            loadConfig();
            // Type de document dans la ligne de commande?
            if (cmopts.contains("d")) {
                // Recupération du type de document
                for (int i = 0; i < args.length; i++) {
                    if (args[i].matches("-d")) {
                        documentid = args[i + 1];
                        logger.debug("docId =" + documentid);
                    } else if(args[i].matches("-r")) {
                        String rulesFileName = args[i+1];
                        RulesParser rp = new RulesParser(FactoryProvider.getSaxParserFactory());
                        rp.parse(new File(rulesFileName));
                        RulesModel rules = (RulesModel)rp.getMarshallable();
                        setArchiveRules(rules);
                    }
                }
                if (cmopts.contains("f")) {
                    Vector<String> fil = new Vector<String>();
                    for (int i = 0; i < args.length; i++) {
                        if (args[i].matches("-f")) {
                            for (int j = i + 1; j < args.length; j++) {
                                fil.add(args[j]);
                            }
                        }
                    }
                    setFilesToImport(fil.toArray());
                }
                // Option fichier(s) présent(s) dans la ligne de commande?
                if (cmopts.contains("f") && cmopts.contains("i")) {
                    Vector<String> fil = new Vector<String>();
                    for (int i = 0; i < args.length; i++) {
                        if (args[i].matches("-i")) {
                            if (args[i + 1].matches("yes")) {
                                budgIsInterractif = true;
                            }
                            if (args[i + 2].matches("yes")) {
                                collIsInterractif = true;
                            }
                        }
                    }
                    setFilesToImport(fil.toArray());
                } else {
                    System.err.println("L'option fichier(s) est obligatoire dans la ligne de commande lorsque le mode interractif est declaré.");
                    System.exit(SYNTAX_ERROR);
                }
                if(cmopts.contains("u")) {
                    for (int i = 0; i < args.length; i++) {
                        if (args[i].matches("-u")) {
                            final String strUser = args[i+1];
                            // c'est un peu violent, ok, mais on verra plus tard
                            user = new XemeliosUser() {
                                @Override
                                public String getId() {
                                    return strUser;
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
                    }
                } else {
                    System.err.println("L'option -u est obligatoire");
                    System.exit(SYNTAX_ERROR);
                }
                System.exit(importer(documentid));
            } else {
                System.err.println("L'option type de document est obligatoire dans la ligne de commande.");
                System.exit(SYNTAX_ERROR);
            }
        } else {
            System.out.println("\n\nAide de l'importeur batch.\n" +
                    "_________________________\n\n" +
                    "Syntaxe : importerBatch -config config-dir -d typeDoc [-c code libelle] [-b code libelle] [-i (y|n budget) (y|n collectivite)] [-r fichier.regles] -f fichier [fichier [...]]\n\n" +
                    "-config : chaine contenant le ou les répertoires où se trouvent les fichiers de définition des configurations de documents\n" +
                    "          si cette option n'est pas fournie, utilise le contenu de la variable d'environnement "+Constants.SYS_PROP_DOC_DEF_DIR+"\n\n"+
                    "-d : MODELE DU DOCUMENT à importer >> Option Obligatoire.\n\n" +
                    "-b : BUDGET DU FICHIER  >> Option Facultative.\n\n" +
                    "       - Si le budget est présent dans le fichier cette option est inutile.\n" +
                    "       - Sinon précisez le libellé puis le code du budget.\n" +
                    "		- Si pas d'argument pour le budget :\n" +
                    "			- Si présence d'un budget par defaut dans fichier de conf, alors celui-ci sera pris.\n" +
                    "			- Sinon, abandon de l'import car le budget n'est pas définit.\n" +
                    "		- Si option particulière si y n alors mode interactif avec l'utilisateur dans la console." +
                    "Pour chaque fichier sans budget, on pose la question à l'utilisateur.\n\n" +
                    "-c : COLLECTIVITE DU FICHIER  >> Option Facultative.\n\n" +
                    "		- Si la collectivité est présente dans le fichier, on l'utilise.\n" +
                    "		- Sinon on prend celui de l'argument.\n" +
                    "		- Si pas d'argument pour la collectivité, alors mode interactif avec l'utilisateur dans la console.\n" +
                    "Pour chaque fichier sans collectivité, on pose la question à l'utilisateur.\n\n" +
                    "-i : MODE INTERACTIF  >> Option Facultative.\n" +
                    "		- y ou n permet d'activer l'option interactive pour l'element en question.\n\n" +
                    "-r : permet de spécifier le fichier de règles à appliquer à l'import de l'archive ;\n\t\tuniquement si -d xemelios.archive.\n\n" +
                    "-u : utilisateur (id ou login) à utiliser opur l'import\n\n"+
                    "-f : NOM DU/DES FICHIER(S) à importer");
            System.exit(SYNTAX_ERROR);
        }
    }

    public static void main(String[] args) throws Exception {
        BatchRealImporter o = new BatchRealImporter(new PropertiesExpansion(), args);
    }

    private void setFilesToImport(Object[] nomFichier) {
        for (int i = 0; i < nomFichier.length; i++) {
            files.add(new File(nomFichier[i].toString()));
        }
    }

    private void setCommandLineOptions() {
        opts.addOption("d", true, "Modele de Document");
        opts.getOption("d").setArgs(1);
        opts.addOption("b", true, "Budget du document");
        opts.getOption("b").setArgs(2);
        opts.addOption("c", true, "Collectivite du document");
        opts.getOption("c").setArgs(2);
        opts.addOption("i", true, "Mode interractif pour la saisie du budget ou de la collectivite du document");
        opts.getOption("i").setArgs(2);
        opts.addOption("f", true, "Fichier(s) a importer");
        opts.addOption("h", false, "Aide");
        opts.addOption("help", false, "Aide");
        opts.addOption("g","config",true,"Emplacements des fichiers de configuration");
        opts.getOption("g").setArgs(1);
        opts.addOption("u",true,"Utilisateur réalisant l'import");
        opts.getOption("u").setArgs(1);
        opts.addOption("r", true, "Fichier de règles pour les archives");
        opts.getOption("r").setArgs(1);
    }

    private int importer(String docId) {
        if (!collIsInterractif) {
            // Si collectivite présente dans la ligne de commande
            if (cmopts.contains("c") && coll == null) {
                for (int i = 0; i < arguments.length; i++) {
                    if (arguments[i].matches("-c")) {
                        coll = new Pair(arguments[i + 1], arguments[i + 2]);
                    }
                }
            }
        }
        if (!budgIsInterractif) {
            if (cmopts.contains("b") && budg == null) {
                for (int i = 0; i < arguments.length; i++) {
                    if (arguments[i].matches("-b")) {
                        budg = new Pair(arguments[i + 1], arguments[i + 2]);
                    }
                }
            }
        }
        if(Constants.XEMELIOS_ARCHIVE_SIGN.equals(docId)) {
            ImportServiceBatchProvider isp = new ImportServiceBatchProvider(null, null, null);
            isp.setDisplayFeedback(false);
            isp.setAlwaysOverwrite(true);
            try {
                ArchiveImporter ai = new ArchiveImporter(docModels, files.get(0), isp, applicationConfiguration, getArchiveRules());
                ai.setUser(user);
                Errors errors = ai.doImport();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                logger.debug("Fin import archive a "+sdf.format(new Date()));
                StringBuilder sb = new StringBuilder();
                int errorLevel = 0;
                for(Errors.Error error:errors.getErrors()) {
                    errorLevel = Math.max(errorLevel, error.getSeverity());
                    sb.append("[").append(error.getSeverityMessage()).append("] ").append(error.getMessage()).append("\n");
                }
                if(sb.length()>0) {
                    System.out.println(sb.toString());
                }
                if(errorLevel>=Errors.SEVERITY_ERROR) return IMPORT_FAILURE;
                else if(errorLevel>=Errors.SEVERITY_WARNING) return IMPORT_WARNING;
                else return IMPORT_SUCCESS;
            } catch(Exception ex) {
                logger.error("Import archive ",ex);
                return IMPORT_FAILURE;
            }
        } else {
            DocumentModel dm = docModels.getDocumentById(docId);
            try {
                Class clazz = Class.forName(dm.getImportClass());
                @SuppressWarnings("unchecked")
                Constructor<EtatImporteur> cc = clazz.getConstructor(XemeliosUser.class, PropertiesExpansion.class);
                Object obj = cc.newInstance(user, applicationConfiguration);
                if (!(obj instanceof EtatImporteur)) {
                    String msg = "Cette classe n'est pas un importeur.\n" +
                            "Le fichier de configuration qui vous a été livré est certainement invalide.\n" +
                            "Veuillez contacter votre fournisseur.";
                    System.err.println(msg);
                    logger.error(msg);
                    return IMPORT_FAILURE;
                }
                EtatImporteur ei = (EtatImporteur) obj;
//                if(budg==null) {
//                    // Recuperration du budget par defaut à partir du fichier de conf (default-budget)
//                    if (!dm.getDefaultBudgets().isEmpty()) {
//                        budg = dm.getDefaultBudgets().firstElement();
//                    } else {
//                        String msg = "Aucun budget définit pour ce fichier.\n" +
//                                "Le budget n'est definit ni dans le fichier,ni dans le fichier de configuration et ni dans la ligne de commande.\n" +
//                                "L'import a donc été annulé pour faute de budget.";
//                        System.err.println(msg);
//                        logger.error(msg);
//                        return IMPORT_FAILURE;
//                    }
//                }
                ImportServiceBatchProvider isbp = new ImportServiceBatchProvider(dm, coll, budg);
                isbp.setAlwaysOverwrite(true);
                ei.setImpSvcProvider(isbp);
                ei.setDocument(dm);
                ei.setApplicationConfiguration(applicationConfiguration);
                ImportContent fToImport = files(dm.getExtension(), dm.getBalise());
                ei.setFilesToImport(fToImport.filesToImport);
                ei.setPjs(fToImport.pjs);
                ei.execute();
                ei.get();
System.err.println("Warning count: "+ei.getWarningCount());
                if(ei.getInProcessException()!=null) return IMPORT_FAILURE;
                else if(ei.getWarningCount()>0) return IMPORT_WARNING;
                else return IMPORT_SUCCESS;
            } catch (Exception ex) {
                logger.error("private void importer(String docId)");
                ex.printStackTrace();
                return IMPORT_FAILURE;
            } finally {
                for(File f:filesToDrop) f.delete();
            }
        }
    }

    public class ImportContent {

        ArrayList<File> filesToImport;
        ArrayList<PJRef> pjs;

        public ImportContent() {
            super();
            filesToImport = new ArrayList<File>();
            pjs = new ArrayList<PJRef>();
        }
    }

    private void createLogger() {
        try {
            String log4jConfFile = applicationConfiguration.getProperty("xemelios.log4j.xml");
            DOMConfigurator.configure(log4jConfFile);
        } catch(Throwable t) {}
        logger = Logger.getLogger(BatchRealImporter.class);
        logger.info("log4J initialise");
    }

    protected void loadConfig() throws DataConfigurationException {
        String reps = null;
        if(documentsDefDir!=null) reps = documentsDefDir;
        else reps = applicationConfiguration.getProperty(Constants.SYS_PROP_DOC_DEF_DIR);
        docModels = getDocumentsInfos(reps);
        initDataLayer();
    }

    protected void initDataLayer() throws DataConfigurationException {
        String availableLayers = applicationConfiguration.getProperty(Constants.SYS_PROP_AVAILABLE_LAYERS);
        //String availableLayers = applicationConfiguration.getProperty(Constants.SYS_PROP_AVAILABLE_LAYERS,"fr.gouv.finances.cp.xemelios.data.impl.MySqlDataLayer");
        StringTokenizer tokenizer = new StringTokenizer(availableLayers, ",");
        while (tokenizer.hasMoreTokens()) {
            String className = tokenizer.nextToken();
            try {
                Class.forName(className);
            } catch (ClassNotFoundException cnfEx) {
                logger.error("instanciating data layer: ", cnfEx);
            }
        }
        DataLayerManager.setApplicationProperties(applicationConfiguration);
        //DataLayerManager.setDataImpl(applicationConfiguration.getProperty(Constants.SYS_PROP_DATA_IMPL,"mysql"));
        DataLayerManager.setDataImpl(applicationConfiguration.getProperty(Constants.SYS_PROP_DATA_IMPL));
    }

    protected DocumentsModel getDocumentsInfos(String repertoires) {
        DocumentsModel ret = null;
        String[] dirs = repertoires.split(",");
        for (String repertoire : dirs) {
            try {
                File dir = new File(repertoire);
                logger.debug("Searching " + repertoire);
                if (!dir.exists()) {
                    logger.info(repertoire + " does not exists.");
                    continue;
                }
                // Fichiers de config
                File[] fichiers = dir.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toUpperCase().endsWith(".XML") && !"build.xml".equals(name);
                    }
                });
                if (fichiers == null) {
                    logger.info("No file found in " + repertoire);
                    return null;
                }
                DocumentsParser dp = new DocumentsParser();
                for (int i = 0; i < fichiers.length; i++) {
                    dp.parse(fichiers[i]);
                    DocumentsModel dm = (DocumentsModel) dp.getMarshallable();
                    if (ret == null) {
                        ret = dm;
                        for (DocumentModel docModel : dm.getDocuments()) {
                            docModel.setBaseDirectory(repertoire);
                        }
                    } else {
                        for (DocumentModel docModel : dm.getDocuments()) {
                            docModel.setBaseDirectory(repertoire);
                            ret.addChild(docModel, DocumentModel.QN);
                        }
                    }
                }
            } catch (Exception ex) {
                logger.error("getDocumentsInfos repertoire=" + repertoire);
                logger.error(ex);
            }
        }
        return ret;
    }

    private ImportContent files(final String extension, final String titreEtat) {
        ImportContent ic = new ImportContent();
        Vector<File> ret = new Vector<File>();
        ret.addAll(files);
        // on regarde si l'un des fichiers a importer est un zip
        for (int i = 0; i < ret.size(); i++) {
            if (ret.get(i).getName().toLowerCase().endsWith(".zip")) {
                if (ret.get(i).exists()) {
                    ZipFile zf = null;
                    try {
                        zf = new ZipFile(ret.get(i));
                        for (Enumeration<? extends ZipEntry> enumer = zf.entries(); enumer.hasMoreElements();) {
                            ZipEntry ze = enumer.nextElement();
                            if (!ze.isDirectory()) {
                                String fileName = ze.getName();
                                String entryName = fileName.toLowerCase();
                                fileName = fileName.replace(
                                        File.pathSeparatorChar, '_').replace(
                                        File.separatorChar, '_').replace(':',
                                        '|').replace('\'', '_').replace('/',
                                        '_');
                                logger.debug(entryName);
                                if (PJRef.isPJ(ze)) {
                                    PJRef pj = new PJRef(ze);
                                    File tmpFile = pj.writeTmpFile(FileUtils.getTempDir(), zf);
                                    ic.pjs.add(pj);
                                    filesToDrop.add(tmpFile);
                                } else if ((entryName.endsWith(extension.toLowerCase()) || entryName.endsWith(".xml")) && !fileName.startsWith("_")) {
                                    // on decompresse le fichier dans le
                                    // repertoire temporaire, comme ca il sera
                                    // supprime en quittant
                                    InputStream is = zf.getInputStream(ze);
                                    BufferedInputStream bis = new BufferedInputStream(is);
                                    File output = new File(FileUtils.getTempDir(), fileName);
                                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output));
                                    byte[] buffer = new byte[1024];
                                    int read = bis.read(buffer);
                                    while (read > 0) {
                                        bos.write(buffer, 0, read);
                                        read = bis.read(buffer);
                                    }
                                    bos.flush();
                                    bos.close();
                                    bis.close();
                                    ic.filesToImport.add(output);
                                    filesToDrop.add(output);
                                }
                            }
                        }
                        zf.close();
                    } catch (ZipException zEx) {
                        System.out.println("Le fichier " + ret.get(i).getName() + " n'est pas une archive ZIP valide.");
                    } catch (IOException ioEx) {
                        ioEx.printStackTrace();
                    } finally {
                        if (zf != null) {
                            try {
                                zf.close();
                            } catch (Throwable t) {
                            }
                        }
                    }
                }
            } else if(ret.get(i).getName().toLowerCase().endsWith(".gz")) {
                try {
                    String fileName = ret.get(i).getName();
                    fileName = fileName.substring(0, fileName.length()-3);
                    File output = new File(FileUtils.getTempDir(), fileName);
                    GZIPInputStream gis = new GZIPInputStream(new FileInputStream(ret.get(i)));
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output));
                    byte[] buffer = new byte[1024];
                    int read = gis.read(buffer);
                    while (read > 0) {
                        bos.write(buffer, 0, read);
                        read = gis.read(buffer);
                    }
                    bos.flush();
                    bos.close();
                    gis.close();
                    ic.filesToImport.add(output);
                    filesToDrop.add(output);
                } catch(IOException ioEx) {
                    // nothing to do
                }
            } else {
                ic.filesToImport.add(ret.get(i));
                // dans ce cas là, on ne le supprime pas
            }
        }
        return ic;
    }

    public RulesModel getArchiveRules() {
        return archiveRules;
    }

    public void setArchiveRules(RulesModel archiveRules) {
        this.archiveRules = archiveRules;
    }

}