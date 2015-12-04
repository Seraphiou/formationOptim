/*
 * Copyright OXIANE, 98 avenue du Gal Leclerc, 92100 Boulogne. Tous droits r�serves.
 * Ce produit ou document est protege par un copyright et distribue avec des licences
 * qui en restreignent l'utilisation, la copie, la distribution, et la decompilation.
 * Ce produit peut etre reproduit par les stagiaires des formations dispensees par
 * OXIANE.
 * OXIANE, le logo OXIANE sont des marques de fabrique ou des marques deposees, ou
 * marques de service, de OXIANE en France et dans d'autres pays.
 * CETTE PUBLICATION EST FOURNIE "EN L'ETAT" ET AUCUNE GARANTIE, EXPRESSE OU IMPLICITE,
 * N'EST ACCORDEE, Y COMPRIS DES GARANTIES CONCERNANT LA VALEUR MARCHANDE, L'APTITUDE
 * DE LA PUBLICATION A REPONDRE A UNE UTILISATION PARTICULIERE, OU LE FAIT QU'ELLE NE
 * SOIT PAS CONTREFAISANTE DE PRODUIT DE TIERS. CE DENI DE GARANTIE NE S'APPLIQUERAIT
 * PAS, DANS LA MESURE OU IL SERAIT TENU JURIDIQUEMENT NUL ET NON AVENU.
 */ 
package com.oxiane.perf.java.pdf;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import fr.gouv.finances.cp.xemelios.importers.batch.ImportServiceBatchProvider;
import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.Constants;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentsModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ElementModel;
import fr.gouv.finances.dgfip.xemelios.common.config.Loader;
import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;
import fr.gouv.finances.dgfip.xemelios.data.DataLayerManager;
import fr.gouv.finances.dgfip.xemelios.importers.DefaultImporter;
import java.io.*;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.xhtmlrenderer.context.StyleReference;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;

/**
 *
 * @author cmarchand
 */
public class PdfRunner {
    private static final transient Logger logger = Logger.getLogger(PdfRunner.class);
    private File inputFile, outputDir;
    private PropertiesExpansion applicationProperties;
    private Document repository;
    /**
     * La configuration des documents
     */
    private DocumentsModel documents;
    private XemeliosUser __user;
    
    protected PdfRunner(final File inputFile, final File outputDir) {
        super();
        this.inputFile = inputFile;
        this.outputDir = outputDir;
    }
    
    /**
     * Lance la g�n�ration
     */
    protected void run() {
        try {
            initEnv();
            DefaultImporter importer = new DefaultImporter(getUser(), applicationProperties) {
                @Override
                protected void done() {
                    super.done();
                    waiter("Appuyez sur <ENTER> pour quitter :");
                }
            };
            DocumentModel payeModel = documents.getDocumentById("cfg-paye");
            importer.setDocument(payeModel);
            importer.setFilesToImport(new File[] {inputFile});
            importer.setImpSvcProvider(new ImportServiceBatchProvider(payeModel, null, null));
            waiter("Appuez sur <ENTER> pour lancer la g�n�ration :");
            importer.execute();
        } catch(Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    /**
     * Callback m�thode pour g�n�rer le PDF.
     * Bon, en terme de conception objet, c'est un peu pourri, mais c'est le plus rapide pour
     * r�utiliser le code existant...
     * @param collectivite
     * @param budget
     * @param data 
     */
    void createPdfFromBulletin(Pair collectivite, Pair budget, byte[] data, String fileName) {
        logger.debug("g�n�ration PDF de "+fileName);
        File tmpFile = new File("tmp.xhtml");
        try {
            DocumentBuilderFactory domFactory = FactoryProvider.getDocumentBuilderFactory();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document dom = builder.parse(new ByteArrayInputStream(data));
            DocumentModel payeModel = documents.getDocumentById("cfg-paye");
            ElementModel elementModel = payeModel.getEtatById("etatPaye").getElementById("payeIndivMensuel");
            String xsltName = elementModel.getXslt();
            File XsltFile = new File(payeModel.getBaseDirectory(),xsltName);
            TransformerFactory tFactory = FactoryProvider.getTransformerFactory();
            Transformer transformer = tFactory.newTransformer(new StreamSource(XsltFile));
            transformer.setParameter("repository", getRepository());
            File targetFile = new File(outputDir, fileName+".pdf");
            // un bug de xhtmlrenderer nous oblige � mettre le xhtml dans un fichier physique
            transformer.transform(new DOMSource(dom), new StreamResult(tmpFile));
            OutputStream os = new FileOutputStream(targetFile);
            ITextRenderer renderer = new ITextRenderer();
            CustomUserAgentCallback callback = new CustomUserAgentCallback(renderer.getOutputDevice());
            callback.setSharedContext(renderer.getSharedContext());
            renderer.getSharedContext().setUserAgentCallback(callback);
            StyleReference css = new StyleReference(callback);
            renderer.setDocument(tmpFile);
            renderer.layout();
            renderer.createPDF(os, true);
            os.flush(); os.close();
            tmpFile.delete();
        } catch(Exception ex) {
            logger.error("pendant la g�n�ration PDF de "+fileName, ex);
        }
    }
    /**
     * Callback m�thode pour r�cup�rer le repository
     * @param repository 
     */
    void saveRepository(final Document repository) {
        this.repository = repository;
    }
    /**
     * Callback method pour renvoyer le repository
     * @return 
     */
    Document getRepository() { return repository; }
    
    /**
     * Initialise l'environnement d'ex�cution.
     */
    private void initEnv() throws Exception {
        initLoggers();
        applicationProperties = new PropertiesExpansion(System.getProperties());
//        File documentsConfigFile = new File(new File("src/cfg-paye"),"conf");
        File documentsConfigFile = new File("documents-def");
        applicationProperties.setProperty(Constants.SYS_PROP_DOC_DEF_DIR, documentsConfigFile.getAbsolutePath());
        documents = Loader.getDocumentsInfos(applicationProperties.getProperty(Constants.SYS_PROP_DOC_DEF_DIR));
        if(documents==null) throw new DataConfigurationException("Pas de configuration de documents disponible");
        if(documents.getDocumentById("cfg-paye")==null) throw new DataConfigurationException("Pas de configuration de paye disponible");
        DataLayerManager.regiterDataImpl(PdfDataLayer.LAYER_NAME, PdfDataLayer.class);
        DataLayerManager.setDataImpl(PdfDataLayer.LAYER_NAME);
        ((PdfDataLayer)(DataLayerManager.getImplementation())).setPdfRunner(this);
    }
    
    private void initLoggers() throws Exception {
        File f = new File(new File("."),"conf/log4j.xml");
        if(!f.exists())
            throw new RuntimeException("impossible de trouver "+f.getAbsolutePath());
        URL url = f.toURI().toURL();
        DOMConfigurator.configure(url);
    }
    
    private XemeliosUser getUser() {
        if(__user==null) {
            __user = new XemeliosUser() {
                @Override
                public String getId() { return "user"; }

                @Override
                public String getDisplayName() { return getId(); }

                @Override
                public boolean hasRole(String role) { return true;}

                @Override
                public boolean hasDocument(String document) { return true; }

                @Override
                public boolean hasCollectivite(String collectivite, DocumentModel dm) { return true; }
            };
        }
        return __user;
    }

    /**
     * Lance la g�n�ration PDF du fichier pass� en param�tre.
     * arg1 : fichier xml � traiter
     * arg2 : r�pertoire de sortie des PDF
     * @param args 
     */
    public static void main(final String[] args) {
        File[] files = checkArgs(args);
        if(files!=null) {
            new PdfRunner(files[0], files[1]).run();
        }
    }
    
    private static File[] checkArgs(final String[] args) {
        File[] ret = null;
        if(args.length!=2) {
            displaySyntax();
        } else {
            File input = new File(args[0]);
            File output = new File(args[1]);
            if(!input.exists()) {
                System.err.println(input.getAbsolutePath()+" n'existe pas.");
                input=null;
            }
            if(!output.exists()) {
                System.out.println("Cr�ation de "+output.getAbsolutePath());
                output.mkdirs();
            } else if(!output.isDirectory()) {
                System.err.println(output+" n'est pas un r�pertoire.");
                output=null;
            }
            if(input!=null && output!=null) {
                ret = new File[] { input, output};
            }
        }
        return ret;
    }
    private static void displaySyntax() {
        StringBuilder sb = new StringBuilder();
        sb.append("java -cp ... ").append(PdfRunner.class).append(" <inputFile> <outputDir>\n");
        sb.append("\to� inputFile est le fichier XML � traiter\n");
        sb.append("\to� outputDir est le r�pertoire dans lequel cr�er les PDF.\n");
        sb.append("\t\tsi outputDir n'existe pas, il sera cr��.");
        System.err.println(sb.toString());
    }
    public static void waiter(String msg) {
        System.out.print(msg);
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            char[] buffer = new char[64];
            int read = isr.read(buffer);
            boolean start=false;
            while(!start) {
                for(int i=0; i<read; i++) {
                    if(buffer[i]=='\n') {
                        start=true;
                        break;
                    }
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
    class CustomUserAgentCallback extends ITextUserAgent {

        public CustomUserAgentCallback(ITextOutputDevice outputDevice) {
            super(outputDevice);
        }

        @Override
        public String resolveURI(String uri) {
            if (uri.startsWith("xemelios:")) {
                String servlet = null;
                String sTmp = uri;
                int start = uri.indexOf('?');
                if (start >= 0) {
                    servlet = uri.substring(uri.indexOf("/") + 1, start);
                    sTmp = uri.substring(start + 1);
                }
                if ("resource".equals(servlet)) {
                    String ret = null;
                    try {
                        File resourceDir = new File(applicationProperties.getProperty("xemelios.resources.location"));
                        File res = new File(resourceDir, sTmp);
                        ret = res.toURI().toURL().toExternalForm();
                    } catch (Throwable t) {
                    }
                    return ret;
                } else {
                    return super.resolveURI(uri);
                }
            } else {
                return super.resolveURI(uri);
            }
        }
    }
}
