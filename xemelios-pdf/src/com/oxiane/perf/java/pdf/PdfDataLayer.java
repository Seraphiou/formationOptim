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
package com.oxiane.perf.java.pdf;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import fr.gouv.finances.dgfip.xemelios.auth.UnauthorizedException;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.PJRef;
import fr.gouv.finances.dgfip.xemelios.common.config.*;
import fr.gouv.finances.dgfip.xemelios.data.*;
import fr.gouv.finances.dgfip.xemelios.data.impl.sqlconfig.TDocument;
import fr.gouv.finances.dgfip.xemelios.data.impl.sqlconfig.TPersistenceConfig;
import fr.gouv.finances.dgfip.xemelios.export.ConfigModel;
import fr.gouv.finances.dgfip.xemelios.export.ExportJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * Classe bouchon de gestion de la persistence.
 * @author cmarchand
 */
public class PdfDataLayer extends AbstractDataImpl {
    private static final transient Logger logger = Logger.getLogger(PdfDataLayer.class);
    static final transient String LAYER_NAME = "mysql";
    static {
        DataLayerManager.regiterDataImpl(LAYER_NAME, PdfDataLayer.class);
    }
    private File documentsDefDirectory;

    // pour pouvoir le rappeler pour la génération PDF
    private PdfRunner pdfRunner;
    
    public PdfDataLayer(PropertiesExpansion applicationProperties) {
        super();
        setApplicationProperties(applicationProperties);
    }

    @Override
    public String getLayerName() { return LAYER_NAME; }

    @Override
    public void setDocumentsConfigDirectory(File directory) { 
        documentsDefDirectory = directory;
    }

    @Override
    public TPersistenceConfig getPersistenceConfig(DocumentModel dm, XemeliosUser user) throws DataConfigurationException, UnauthorizedException { 
        TPersistenceConfig pc = super.getPersistenceConfig(dm, user);
        TDocument spc = pc.getLayer(getLayerName()).getDocument(dm.getId());
        return pc;
    }

    @Override
    public void registerBudgetCollectivite(DocumentModel dm, Pair codeBudget, Pair collectivite, Pair[] parentCollectivites, String docName, String archiveName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { }

    @Override
    public boolean importElement(DocumentModel dm, EtatModel currentEtat, Pair codeBudget, Pair collectivite, String sourceFileName, String elementName, byte[] strElement, String encoding, String archiveName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { 
        if("etatPaye".equals(currentEtat.getId())) {
            //logger.debug("importElement("+dm.getId()+"."+currentEtat.getId()+","+codeBudget+","+collectivite+","+sourceFileName+","+elementName+",[...],"+encoding+","+archiveName+","+user+")");
            pdfRunner.createPdfFromBulletin(collectivite, codeBudget, strElement, elementName);
        }
        return true;
    }
    public void setPdfRunner(final PdfRunner runner) {
        this.pdfRunner = runner;
    }

    @Override
    public boolean saveRepository(DocumentModel dm, Pair collectivite, Document repository, Pair nomenclatureReference, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { 
        pdfRunner.saveRepository(repository);
        return true; 
    }

    @Override
    public void removeDocument(DocumentModel dm, Pair budget, Pair collectivite, String docName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { }

    @Override
    public boolean isDocumentExists(DocumentModel dm, Pair budget, Pair collectivite, String docName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return false; }

    @Override
    public void saveSpecialKeys(DocumentModel dm, Pair budget, Pair collectivite, String docName, String archiveName, Pair sk1, Pair sk2, Pair sk3, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { }

    @Override
    public boolean canSearch(DocumentModel dm, EtatModel em, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return true; }

    @Override
    public Vector<Pair> getSpecialKeys1(DocumentModel dm, Pair collectivite, Pair budget, boolean distinct, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public Vector<Pair> getSpecialKeys2(DocumentModel dm, Pair collectivite, Pair budget, Pair key1, boolean distinct, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public Vector<Pair> getSpecialKeys3(DocumentModel dm, Pair collectivite, Pair budget, Pair key1, Pair key2, boolean distinct, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public Vector<Pair> getParentCollectivites(DocumentModel dm, int level, Pair[] otherParents, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public int getCollectivitesCount(DocumentModel dm, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return 1;}

    @Override
    public Vector<Pair> getCollectivites(DocumentModel dm, Pair[] parents, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public Vector<Pair> getArchivesImported(XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public Vector<Pair> getBudgets(DocumentModel dm, Pair collectivite, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public Vector<String> getBudgetsAnnexes(DocumentModel dm, EtatModel em, Pair collectivite, Pair budget, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException {return null; }

    @Override
    public Collection<Pair> queryRepository(DocumentModel dm, Pair collectivite, RecherchePaireModel rpm, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException {return null; }

    @Override
    public Collection<Pair> queryRepository(DocumentModel dm, Pair collectivite, RecherchePaireModel rpm, Pair refNomenclature, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException {return null; }

    @Override
    public String getRepository(DocumentModel dm, Pair collectivite, Pair budget, String idNomenclature, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { 
        logger.error("NE PLUS UTILISER getRepository(DocumentModel,Pair,Pair,String,XemeliosUser)");
        Document doc = pdfRunner.getRepository();
        try {
            Transformer t = FactoryProvider.getTransformerFactory().newTransformer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Charset cs = Charset.forName("UTF-16");
            t.setOutputProperty(OutputKeys.ENCODING, cs.name());
            t.transform(new DOMSource(doc), new StreamResult(baos));
            baos.flush(); baos.close();
            return new String(baos.toByteArray(), cs);
        } catch(Exception ex) {
            throw new DataConfigurationException(ex);
        }
    }

    @Override
    public Vector<String> getRepositorys(DocumentModel dm, Pair collectivite, Pair budget, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { 
        String s = getRepository(dm,collectivite,budget,null,user);
        Vector<String> ret = new Vector<String>();
        ret.add(s);
        return ret;
    }

    @Override
    public Document getRepositoryAsDom(DocumentModel dm, Pair collectivite, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { 
        return pdfRunner.getRepository();
    }

    @Override
    public Document getRepositoryAsDom(DocumentModel dm, Pair collectivite, String idNomenclature, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { 
        return pdfRunner.getRepository();
    }

    @Override
    public Collection<String> getDistinctValues(ElementModel em, Pair collectivite, Pair budget, String path, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public boolean removeDocumentModel(DocumentModel dm, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return true; }

    @Override
    public boolean removeCollectivite(DocumentModel dm, Pair collectivite, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return true; }

    @Override
    public boolean removeBudget(DocumentModel dm, Pair collectivite, Pair budget, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return true; }

    @Override
    public boolean removeFichier(DocumentModel dm, String fichier, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return true; }

    @Override
    public boolean removeArchive(HashMap<String, DocumentModel> dms, String archiveName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return true; }

    @Override
    public boolean removeSpecialKey1(DocumentModel dm, Pair collectivite, Pair budget, Pair key1, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return true; }

    @Override
    public boolean removeSpecialKey2(DocumentModel dm, Pair collectivite, Pair budget, Pair key1, Pair key2, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return true; }

    @Override
    public boolean removeSpecialKey3(DocumentModel dm, Pair collectivite, Pair budget, Pair key1, Pair key2, Pair key3, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return true; }

    @Override
    public DataResultSet getEmptyDataResultSet() { return null; }

    @Override
    public DataResultSet search(ElementModel elementModel, Pair collectivite, Pair budget, String xPath, ListeResultatModel lrm, Vector<CritereModel> models, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return getEmptyDataResultSet(); }

    @Override
    public Pair[] getOperators(String datatype) { return null; }

    @Override
    public String getDocumentAsString(EtatModel etatModel, Pair collectivite, Pair budget, String docId, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null;}

    @Override
    public Document getDocumentAsDom(EtatModel etatModel, Pair collectivite, Pair budget, String docId, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public DocumentInfos getDocumentInfos(EtatModel etatModel, Pair collectivite, Pair budget, String docId, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public void importPj(PJRef pj, String archiveName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { }

    @Override
    public PJRef getPj(Pair collectivite, String pjName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public PJRef getPjByUniqueId(Pair collectivite, String id, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public boolean isPjAvailable(Pair collectivite, String id, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return false; }

    @Override
    public String getInitialDocNameFromDocId(EtatModel em, String collectivite, String budget, String docId, Connection con, XemeliosUser user) throws DataConfigurationException, SQLException, UnauthorizedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reset() { }

    @Override
    public String getLastWarnings() { return null; }

    @Override
    public ArrayList<String> getDocumentListFromSpecialKeys(ElementModel em, String colletivite, String budget, String sp1, String sp2, String sp3, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public ArrayList<String> getDocumentListFromXPath(ElementModel em, String collectivite, String budget, String sp1, String sp2, String sp3, String path, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public nu.xom.Document getManifesteFromArchive(String archiveName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public boolean updateManifeste(String archiveName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return true; }

    @Override
    public SimpleDateFormat getDateFormatter() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    public Pair[] getAggregateOperators(String datatype) { return null; }

    @Override
    public String calculateAggregate(DocumentModel dm, EtatModel em, String columnXPath, String operator, ChampModel cm, DataResultSet rs, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public void insertConfigExport(ConfigModel cm, XemeliosUser user) { }

    @Override
    public void updateConfigExport(ConfigModel cm, XemeliosUser user) { }

    @Override
    public Vector<ConfigModel> getListeConfigsExport(String docID, String etatID, XemeliosUser user) { return null; }

    @Override
    public ConfigModel getConfigExport(String docId, String etatId, String configId, XemeliosUser user) { return null; }

    @Override
    public void deleteConfigExport(ConfigModel cm, XemeliosUser user) { }

    @Override
    public ExportableData searchFichiers(DocumentModel dm, String coll, String budg, XemeliosUser user) throws DataAccessException, DataConfigurationException, UnauthorizedException { return null; }

    @Override
    public Vector<ExportableData> searchFichiers(DocumentsModel dms, String coll, String budg, XemeliosUser user) throws DataAccessException, DataConfigurationException, UnauthorizedException { return null; }

    @Override
    public DataResultSet getDataResultSetForExport(int whatToExport, ElementModel em, Pair coll, Pair budg, String fic, Pair SK1, Pair SK2, XemeliosUser user) throws DataConfigurationException, SQLException, UnauthorizedException { return getEmptyDataResultSet(); }

    @Override
    public String getParameterValue(String paramName) { return null; }

    @Override
    public void setParameterValue(String paramName, String paramValue) { }

    @Override
    public Object HSWinitTempStorage(String storageId, String keyId, int maxKeyLength, Vector<Field> fieldList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void HSWdoCreateStorage(Object storageHandle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void HSWdoInsert(Object storageHandle, String key, Object[] values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean HSWdoExists(Object storageHandle, String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object[] HSWdoSelect(Object storageHandle, String key, Object[] inoutObj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean HSWdoDelete(Object storageHandle, String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int HSWdoCount(Object storageHandle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean HSWdoDrop(Object storageHandle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isLocalComputerServer() { return true; }

    @Override
    public Vector<Pair> getListeCodeLibelleRepository(DocumentModel dm, Pair collectivite, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public Vector<Document> getRepositorysAsDom(DocumentModel dm, Pair collectivite, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { 
        Document doc = getRepositoryAsDom(dm, collectivite, user);
        Vector<Document> ret = new Vector<Document>();
        ret.add(doc);
        return ret;
    }

    @Override
    public String getLastInputValue(String inputId, XemeliosUser user) { return null; }

    @Override
    public void setLastInputValue(String inputId, String value, XemeliosUser user) { }

    @Override
    public void saveRequest(ElementModel em, RechercheModel recherche, XemeliosUser user) { }

    @Override
    public List<RechercheModel> getSavedRequests(ElementModel em, XemeliosUser user, boolean includePublics) { return null; }

    @Override
    public RechercheModel getSavedRequest(ElementModel em, XemeliosUser user, String searchId) { return null; }

    @Override
    public void shutdown() { }

    @Override
    public byte[] serializeDataResultSet(DataResultSet drs) { return null; }

    @Override
    public DataResultSet deserializeDataResultSet(byte[] buffer) { return getEmptyDataResultSet(); }

    @Override
    public void createExportJob(ExportJob job, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { }

    @Override
    public List<ExportJob> getExportJobList(XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public ExportJob getExportJob(long exportId, String user) throws DataConfigurationException, DataAccessException, UnauthorizedException { return null; }

    @Override
    public String getWarnings() { return null; }

    @Override
    public String getDatalayerConfiguration() { return null; }

    @Override
    public void setApplicationProperties(PropertiesExpansion applicationProperties) { }

    @Override
    public Hashtable<String, Object> getRedefinedConfigValues(XemeliosUser user) throws DataConfigurationException, DataAccessException { return null; }

    @Override
    public void saveRedefinedValues(Hashtable<String, Object> valuesToSet, List<String> valuesToUnset, XemeliosUser user) throws DataConfigurationException, DataAccessException { }

    @Override
    public void declareArchiveImported(String archiveName, XemeliosUser user) throws DataConfigurationException, UnauthorizedException { }

    @Override
    public boolean canImportDocument(String documentId, XemeliosUser user) throws UnauthorizedException { return true; }

    @Override
    public void setUseCachedPersistence(boolean useCachedPersistence) { }
    
}
