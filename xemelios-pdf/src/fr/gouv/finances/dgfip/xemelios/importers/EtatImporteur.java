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
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.FileInfo;
import fr.gouv.finances.dgfip.xemelios.common.PJRef;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.Loader;
import fr.gouv.finances.dgfip.xemelios.data.DataAccessException;
import fr.gouv.finances.dgfip.xemelios.data.DataImpl;
import fr.gouv.finances.dgfip.xemelios.data.DataLayerManager;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.jdesktop.swingworker.SwingWorker;
import org.w3c.dom.Document;

/**
 * Classe a surcharger pour creer des importeurs 
 * @author chm
 */
public abstract class EtatImporteur extends SwingWorker<String, String> {
    public static final transient String OVERWRITE_RULE_ALWAYS = "always";
    public static final transient String OVERWRITE_RULE_NEVER = "never";
    public static final transient String OVERWRITE_RULE_ASK = "ask";
    public static final transient String OVERWRITE_RULE_DEFAULT = "default";

    private static Logger logger = Logger.getLogger(EtatImporteur.class);
    protected DocumentModel dm;
    protected Pair col;
    protected Pair bdg;
    protected File[] filesToImport;
    protected PJRef[] pjs;
    protected Exception inProcessException = null;
    protected long start;
    private ImportServiceProvider impSvcProvider;
    protected XemeliosUser user;
    private FileInfo fileInfo = null;
    protected int warningCount = 0;
    protected String overwriteRule = OVERWRITE_RULE_DEFAULT;
    private PropertiesExpansion applicationConfiguration;
    private String archiveName = null;
    private Document manifeste = null; 
    
    @Override
    public String doInBackground() {
        fileInfo = new FileInfo();
        start = System.currentTimeMillis();
        this.getImpSvcProvider().displayProgress((filesToImport==null?0:filesToImport.length) + (pjs==null?0:pjs.length));
        try {
            preProcess();
            if(filesToImport!=null) {
                for (int i = 0; i < filesToImport.length; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    try {
                        File f = filesToImport[i];
                        final String fName = f.getName();
                        logger.debug(fName);
                        try {
                            if (SwingUtilities.isEventDispatchThread()) {
                                firePropertyChange("FILE_NAME", null, fName);
                            } else {
                                SwingUtilities.invokeAndWait(new Runnable() {

                                    @Override
                                    public void run() {
                                        firePropertyChange("FILE_NAME", null, fName);
                                    }
                                });
                            }
                        } catch (Throwable t) {
                            logger.fatal("in fireFileName:", t);
                        }
                        fileInfo.setDebutImport((new Date()).getTime());
                        logger.debug("PreImport file : " + f);
                        preImportFile(f);
                        logger.debug("Import file : " + f);
                        fileInfo.merge(importFile(f));
                        logger.debug("PostImport file : " + f);
                        postImportFile(f);
                        fileInfo.setFinImport((new Date()).getTime());
                        /**
                         * FA 55 - FA 65: deplacement apres l'import du fichier
                         */
                        col = bdg = null;
                    } catch(DataAccessException daEx) {
                        // c'est très grave, il faut arrêter le traitement
                        throw new RuntimeException(daEx);
                    // FIN FA 55
                    } catch (Exception ex) {
                        inProcessException = ex;
                        ex.printStackTrace();
                    }
                }
            }
        } catch(RuntimeException ex) {
            inProcessException = ex;
            ex.printStackTrace();
//            cancel(true);
            return ex.getCause().getCause().getMessage();
        } catch (Exception ex) {
            inProcessException = ex;
            ex.printStackTrace();
        }
        try {
            postProcess(inProcessException != null);
        } catch (Exception ex) {
            inProcessException = ex;
            ex.printStackTrace();
        }
        if (!isCancelled()) {
            try {
                DataImpl di = DataLayerManager.getImplementation();
                if(pjs!=null) {
                    for (PJRef pj : pjs) {
                        if (isCancelled()) {
                            break;
                        }
                        final String fName = pj.getFileName();
                        try {
                            SwingUtilities.invokeAndWait(new Runnable() {

                                @Override
                                public void run() {
                                    firePropertyChange("FILE_NAME", null, fName);
                                }
                            });
                        } catch (Throwable t) {
                            logger.fatal("in fireFileName:", t);
                        }
                        if (pj.isValid()) {
                            di.importPj(pj,getArchiveName(), user);
                        }
                    }
                }
            } catch (Exception ex) {
                inProcessException = ex;
                ex.printStackTrace();
            }
        }
        StringBuffer msg = new StringBuffer();
        if (!isCancelled()) {
            msg.append("Le traitement des fichiers est terminé.\n");
            if (fileInfo.getGlobalCount() == 0) {
                msg.append("Pas de données importées.");
            } else {
                try {
                    msg.append(fileInfo.toString(Loader.getDocumentsInfos(null)));
                } catch(Exception ex) {
                    // on ignore tout
                }
                msg.append(getAdditionalMessage());
            }
        } else {
            msg.append("Le traitement des fichiers a été interrompu.");
        }
        logger.info("\n" + msg);
        firePropertyChange("terminated", null, msg);	// histoire de pas faire un new
        return msg.toString();
    }

    /**
     * 
     */
    @Override
    protected void done() {
        super.done();
        if (inProcessException != null) {
            getImpSvcProvider().displayException(inProcessException);
        }
        String msg = "";
        try {
            msg = get();
            if(warningCount>0) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n\nCertains éléments ont pû mal être indexé, en particulier");
                sb.append("\nsi certains champs étaient trop longs dans les fichiers");
                sb.append("\nque vous avez importé.");
                sb.append("\n").append(warningCount).append(" élément(s) est(sont) concerné(s), mais ils ont bien été");
                sb.append("\nimportés et seront disponibles en recherche. Vous risquez");
                sb.append("\ncependant de ne pas les trouver en utilisant des critères");
                sb.append("\navec l'opérateur \"finit par\" ou \"contient\".");
                msg = msg.concat(sb.toString());
            }
        } catch (InterruptedException iEx) {
            iEx.printStackTrace();
        } catch (ExecutionException eEx) {
            eEx.printStackTrace();
        } catch (CancellationException cEx) {
            //cEx.printStackTrace();
            msg = "Import annulé";
        }
        getImpSvcProvider().displayMessage(msg, ImportServiceProvider.MSG_TYPE_INFORMATION);
    }
    public DocumentModel getDocumentModel() { return dm; }
    /**
     * Definit le modele de document
     * @param dm
     */
    public void setDocument(DocumentModel dm) {
        this.dm = dm;
    }

    /**
     * Definit le modele de document ainsi que la collectivite, le budget 
     * @param dm 
     * @param collectivite
     * @param budget
     */
    public void setDocument(DocumentModel dm, Pair collectivite, Pair budget) {
        this.dm = dm;
        this.bdg = budget;
        this.col = collectivite;
    }

    public abstract String getAdditionalMessage();

    /**
     * Definit les fichiers a importer
     * @param fichiers les fichiers a importer
     * @throws Exception
     */
    public void setFilesToImport(Collection<File> fichiers) throws Exception {
        Object[] data = fichiers.toArray();
        filesToImport = new File[data.length];
        for (int i = 0; i < data.length; i++) {
            filesToImport[i] = (File) data[i];
        }
    }

    public void setFilesToImport(File[] fichiers) throws Exception {
        filesToImport = fichiers;
    }

    public File[] getFilesToImport() throws Exception {
        return filesToImport;
    }
    
    public void setManifeste(Document manifeste){
    	this.manifeste=manifeste;
    }
    
    public Document getManifeste(){
    	return this.manifeste;
    }
    
    public void setPjs(Collection<PJRef> piecesJointes) throws Exception {
        Object[] data = piecesJointes.toArray();
        pjs = new PJRef[data.length];
        for (int i = 0; i < data.length; i++) {
            pjs[i] = (PJRef) data[i];
        }
    }

    /**
     * Cette methode est appelee une unique fois avant l'import des fichiers.
     * Pour placer des initialisations et des prétraitements, surcharger
     * cette méthode.
     * @throws Exception Si cette methode jette une exception, tout le traitement
     * d'import est stoppé
     */
    protected void preProcess() throws Exception {
    }

    /**
     * Cette methode est appelee une unique fois apres l'import des fichiers.
     * Pour placer des post-traitements, surcharger cette mï¿½thode.
     * @param isErrorOccured Vaut <tt>true</tt> si le process d'import a leve
     * une exception, <tt>false</tt> si l'import s'est correctement deroule
     * @throws Exception
     */
    protected void postProcess(boolean isErrorOccured) throws Exception {
    }

    /**
     * Cette methode est appelee systematiquement pour chaque fichier a importer
     * avant l'appel à la methode {@link import(File)}
     * @param f Le fichier qui sera importe.
     * @throws Exception
     */
    protected void preImportFile(File f) throws Exception {
    }

    /**
     * Cette methode est appelee systematiquement pour chaque fichier a importer
     * apres l'appel à la methode {@link import(File)}
     * @param f Le fichier qui a ete importe.
     * @throws Exception
     */
    protected void postImportFile(File f) throws Exception {
    }

    protected abstract FileInfo importFile(File f) throws Exception;

    public ImportServiceProvider getImpSvcProvider() {
        return impSvcProvider;
    }

    public void setImpSvcProvider(ImportServiceProvider impSvcProvider) {
        this.impSvcProvider = impSvcProvider;
    }
    
    public XemeliosUser getUser() { return user; }
    public Pair getCollectivite() { return col; }
    public Pair getBudget() { return bdg; }
    public void setCollectivite(Pair p) { col=p; }
    public void setBudget(Pair p) { bdg=p; }
    public FileInfo getFileInfo() { return fileInfo; }
    public void setOverwriteRule(String rule) { this.overwriteRule = rule; }
    public String getOverwriteRule() { return this.overwriteRule; }

    public PropertiesExpansion getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public void setApplicationConfiguration(PropertiesExpansion applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    /**
     * Renvoie le nom de l'archive qu'on importe, ou <tt>null</tt> si l'on est en dehors d'une archive
     * @return
     */
    public String getArchiveName() {
        return archiveName;
    }

    /**
     * Permet de définir le nom de l'archive en cours de traitement
     * @param archiveName
     */
    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    public Exception getInProcessException() {
        return inProcessException;
    }

    public int getWarningCount() {
        return warningCount;
    }

}
