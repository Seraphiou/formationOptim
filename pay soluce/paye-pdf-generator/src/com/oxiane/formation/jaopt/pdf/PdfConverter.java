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

import com.oxiane.formation.jaopt.pdf.utils.CustomUserAgentCallback;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xhtmlrenderer.context.StyleReference;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 * Tâche produisant un PDF.
 * @author cmarchand
 */
public class PdfConverter implements Runnable {
    private static final Logger logger = Logger.getLogger(PdfConverter.class);
    
    private File inputFile, outputFile;
    private BlockingQueue<Document> repositoryPool;
    private BlockingQueue<Transformer> transformerPool;
    
    public PdfConverter(final File inputFile, final BlockingQueue<Transformer> transformerPool, final BlockingQueue<Document> repositoryPool, final File outputFile) {
        super();
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.transformerPool = transformerPool;
        this.repositoryPool = repositoryPool;
    }

    @Override
    public void run() {
        logger.debug("running");
        StreamSource source = new StreamSource(inputFile);
        File xhtmlFile = new File(outputFile.getParentFile(), outputFile.getName()+".xhtml");
        StreamResult result = new StreamResult(xhtmlFile);
        Document repository = null;
        Transformer transformer = null;
        try (OutputStream os = new FileOutputStream(outputFile)) {
            repository = repositoryPool.poll();
            transformer = transformerPool.poll();
            if(repository!=null) {
                transformer.setParameter("repository", repository);
            }
            transformer.transform(source, result);
            transformer.reset();
            transformerPool.add(transformer);
            transformer=null;
            repositoryPool.add(repository);
            repository=null;
            ITextRenderer renderer = new ITextRenderer();
            CustomUserAgentCallback callback = new CustomUserAgentCallback(renderer.getOutputDevice());
            callback.setSharedContext(renderer.getSharedContext());
            renderer.getSharedContext().setUserAgentCallback(callback);
            StyleReference css = new StyleReference(callback);
            renderer.setDocument(xhtmlFile);
            renderer.layout();
            renderer.createPDF(os, true);
            os.flush();
        } catch (NoSuchElementException ex) {
            logger.error(null, ex);
        } catch (IllegalStateException ex) {
            logger.error(null, ex);
        } catch (FileNotFoundException ex) {
            logger.error(null, ex);
        } catch (Exception ex) {
            logger.error(null, ex);
        } finally {
            if(repository!=null) {
                try {
                    repositoryPool.add(repository);
                } catch (Exception ex) { }
            }
            if(transformer!=null) {
                try {
                    transformerPool.add(transformer);
                } catch(Exception ex) {}
            }
            if(xhtmlFile!=null && xhtmlFile.exists()) {
                xhtmlFile.delete();
            }
        }
    }
    
}
