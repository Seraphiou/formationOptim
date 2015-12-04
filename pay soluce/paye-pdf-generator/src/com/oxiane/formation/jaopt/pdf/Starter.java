/*
 * Copyright OXIANE, 98 avenue du Gal Leclerc, 92100 Boulogne. Tous droits réserves.
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
package com.oxiane.formation.jaopt.pdf;

import com.oxiane.formation.jaopt.pdf.utils.FactoryProvider;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Point d'entrée de la génération PDF
 * @author cmarchand
 */
public class Starter {
    private final File inputDir, outputDir;
    private static Logger logger = null;
    public static final int POOL_SIZE = 4;
    TransformerFactory tRepositoryFactory = null;

    Starter(final File inputDir, final File outputDir) throws Exception {
        super();
        this.inputDir=inputDir;
        this.outputDir=outputDir;
        tRepositoryFactory = FactoryProvider.getTransformerFactory();
    }
    public void run() {
        long start = System.currentTimeMillis();
        Document repository = null;
        try {
            repository = loadRepository();
        } catch(ParserConfigurationException | SAXException | IOException ex) {
            logger.error("while loading repository",ex);
        }
        Templates templates = null;
        try {
            TransformerFactory tFactory = FactoryProvider.getTransformerFactory();
            templates = tFactory.newTemplates(new StreamSource(new File(new File("xml-resources"),"etatPaye.xsl")));
        } catch(Exception ex) {
            logger.error("loading XSLT", ex);
        }
        File[] sourceFiles = inputDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml") && !"repository.xml".equals(name);
            }
        });
        
        // creating pools
        BlockingQueue<Document> repositoryPool = new ArrayBlockingQueue<>(POOL_SIZE);
        BlockingQueue<Transformer> transformerPool = new ArrayBlockingQueue<>(POOL_SIZE);
        try {
            repositoryPool.add(repository);
            transformerPool.add(templates.newTransformer());
            for(int i=1;i<POOL_SIZE;i++) {
                if(!repositoryPool.add(cloneRepository(repository))) {
                    logger.warn("Fail to insert repository @"+i);
                }
                transformerPool.add(templates.newTransformer());
            }
        } catch (Exception ex) {
            logger.warn("Fail to insert repository", ex);
        }
            
        long cloningDuration = System.currentTimeMillis()-start;
        ExecutorService service = Executors.newFixedThreadPool(POOL_SIZE);
        for(File sourceFile:sourceFiles) {
            String shortName = sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf('.'));
            File outputFile = new File(outputDir, shortName+".pdf");
            PdfConverter converter = new PdfConverter(sourceFile, transformerPool, repositoryPool, outputFile);
            service.submit(converter);
        }
        service.shutdown();
        while(!service.isTerminated()) {
            // no-op
        }
        long duration = System.currentTimeMillis()-start;
        logger.info("Transforming to PDF took "+duration+"ms");
        logger.info("cloning repository took "+cloningDuration+"ms");
    }

    private Document cloneRepository(final Document repository) throws Exception {
        return (Document)repository.cloneNode(true);
    }

    private Document loadRepository() throws ParserConfigurationException, SAXException, IOException {
        File repositoryFile = new File(inputDir, "repository.xml");
        if(repositoryFile.exists() && repositoryFile.isFile()) {
            DocumentBuilderFactory domFactory = FactoryProvider.getDocumentBuilderFactory();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            return builder.parse(repositoryFile);
        }
        return null;
    }
    public static void main(String[] args) {
        if(args.length<2) {
            displaySyntax(-1);
        }
        File inputDir = new File(args[0]);
        File outputDir = new File(args[1]);
        if(!inputDir.exists()) {
            try {
                System.err.println(inputDir.toURI().toURL().toExternalForm()+" does not exits");
            } catch(Exception ex) {
                System.err.println(inputDir.getAbsolutePath()+" does not exists");
            }
            System.exit(-2);
        }
        if(!inputDir.isDirectory()) {
            try {
                System.err.println(inputDir.toURI().toURL().toExternalForm()+" is not a folder");
            } catch(Exception ex) {
                System.err.println(inputDir.getAbsolutePath()+" is not a folder");
            }
            System.exit(-2);
        }
        if(!outputDir.exists()) {
            outputDir.mkdirs();
        }
        if(!outputDir.isDirectory()) {
            try {
                System.err.println(outputDir.toURI().toURL().toExternalForm()+" is not a directory");
            } catch(Exception ex) {
                System.err.println(outputDir.getAbsolutePath()+" is not a directory");
            }
            System.exit(-2);
        }
        // cleaning output folder
        for(File f:outputDir.listFiles()) {
            f.delete();
        }
        try {
            initLoggers();
        } catch (MalformedURLException ex) {
            ex.printStackTrace(System.err);
        }
        waiter("Appuez sur <ENTER> pour lancer la génération :");
        try {
            new Starter(inputDir, outputDir).run();
        } catch(Exception ex) {
            logger.error("while running Starter", ex);
        }
    }
    
    private static void displaySyntax(final int retCode) {
        System.err.println(SYNTAX);
        if(retCode<0) {
            System.exit(retCode);
        }
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

    private static void initLoggers() throws RuntimeException, MalformedURLException {
        File f = new File(new File("."),"conf/log4j.xml");
        if(!f.exists()) {
            throw new RuntimeException("impossible de trouver "+f.getAbsolutePath());
        }
        URL url = f.toURI().toURL();
        DOMConfigurator.configure(url);
        logger = Logger.getLogger(Starter.class);
    }

    private static final String SYNTAX = "java "+Starter.class.getName()+" <in-dir> <out-dir>\n" +
            "where\n" +
            "\t<in-dir> is the directory that contains XML files to convert\n" +
            "\t<out-dir> is the repository where to create PDF files";
    
}
