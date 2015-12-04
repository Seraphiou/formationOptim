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
package fr.gouv.finances.dgfip.xemelios.data;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Document;

import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.xemelios.auth.UnauthorizedException;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.PJRef;
import fr.gouv.finances.dgfip.xemelios.common.config.ChampModel;
import fr.gouv.finances.dgfip.xemelios.common.config.CritereModel;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentsModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ElementModel;
import fr.gouv.finances.dgfip.xemelios.common.config.EtatModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ListeResultatModel;
import fr.gouv.finances.dgfip.xemelios.common.config.RechercheModel;
import fr.gouv.finances.dgfip.xemelios.common.config.RecherchePaireModel;
import fr.gouv.finances.dgfip.xemelios.data.impl.sqlconfig.TPersistenceConfig;
import fr.gouv.finances.dgfip.xemelios.export.ConfigModel;
import fr.gouv.finances.dgfip.xemelios.export.ExportJob;
import java.util.Hashtable;
import java.util.List;

/**
 * Defines what a data implementation must implement
 * Implementations must provide a constructor with a {@link fr.gouv.finances.cp.utils.PropertiesExpansion} parameter.
 * @author chm
 */
public interface DataImpl {
    /**
     * if element to import exists, do nothing
     */
    public static final int OVERWRITE_NEVER = 1;
    /**
     * if element to import exists, but is older, overwrite it
     */
    public static final int OVERWRITE_IF_NEWER = 2;
    /**
     * if element to import exists, always delete it before to write the newer one.
     */
    public static final int OVERWRITE_ALWAYS = 3;
    /**
     * if element to import exists, write a new one beside the existing
     */
    public static final int OVERWRITE_DUPLICATE = 4;
    /**
     * Read/Write access
     */
    public static final int READ_WRITE_PASS = 0;
    /**
     * Read Only access
     */
    public static final int READ_ONLY_PASS = 1;
    /**
     * Differents levels of exportable data.
     */
    public static final int EXPORT_SK1 = ExportableData.TYPE_PERSO1;
    public static final int EXPORT_SK2 = ExportableData.TYPE_PERSO2;
    public static final int EXPORT_FIC = ExportableData.LVL_FIC;
    public static final int EXPORT_BUD = ExportableData.LVL_BUD;
    public static final int EXPORT_COL = ExportableData.LVL_COL;
    public static final int EXPORT_DOC = ExportableData.LVL_DOC;

    /**
     * Returns the registred name of this persistence layer
     * @return
     */
    public String getLayerName();

    /**
     * Defines the directory where config files are.
     * This method is called by {@link fr.gouv.finances.xemelios.data.DataLayerManager}
     * when this data implementation is loaded.
     * @param directory
     */
    public void setDocumentsConfigDirectory(File directory);

    /**
     * Retrieves the persistence configuration for this EtatModel
     * @param dm DocumentModel to search persistence config for
     * @param user 
     * @return
     * @throws DataConfigurationException
     * @throws UnauthorizedException
     */
    public TPersistenceConfig getPersistenceConfig(DocumentModel dm,XemeliosUser user) throws DataConfigurationException, UnauthorizedException;

    /**
     * Declares (if not already done) that data has been imported for this (DocumentModel,budget,collectivite)
     * Actual implementations may cache registered data.
     * @param dm Document type
     * @param codeBudget budget
     * @param collectivite collectivite
     * @param parentCollectivites
     * @param docName
     * @param archiveName Le nom de l'archive dans laquelle se trouve le fichier
     * @param user
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public void registerBudgetCollectivite(DocumentModel dm, Pair codeBudget, Pair collectivite, Pair[] parentCollectivites, String docName, String archiveName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Import a element. This method <strong>MUST</strong> handle correctly warnings.
     * @param dm Document type
     * @param currentEtat Etat
     * @param codeBudget The budget this element belongs to
     * @param collectivite The collectivite this element belongs to
     * @param sourceFileName name of source file
     * @param elementName generated element name
     * @param strElement
     * @param encoding
     * @param archiveName  Le nom de l'archive dans laquelle était le fichier ; peut être null si le fichier XML n'est pas dans une archive
     * @param user
     * @return <tt>true</tt> if data has been written without warning, <tt>false</tt> if warnings occured.
     * @throws DataConfigurationException 
     * @throws DataAccessException if data has <strong>not</strong> been written. See message for details
     * @throws UnauthorizedException
     */
    public boolean importElement(DocumentModel dm, EtatModel currentEtat, Pair codeBudget, Pair collectivite, String sourceFileName, String elementName, byte[] strElement, String encoding,/*String originFileName,*/ String archiveName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Save repository. Overwrite existing one if necessary.
     * @param dm Document type
     * @param collectivite The collectivite this repostory belongs to
     * @param repository The xml repository
     * @param nomenclatureReference The named-nomenclature, or null if it is default nomenclature
     * @param user
     * @return Should always return <tt>true</tt> or throw an exception
     * @throws DataConfigurationException 
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public boolean saveRepository(DocumentModel dm, Pair collectivite, Document repository,Pair nomenclatureReference,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Removes a document from persistence system
     * @param dm Document type
     * @param budget
     * @param collectivite
     * @param docName The name fo document to remove
     * @param user
     * @throws DataConfigurationException If an error occurs while removing document
     * @throws DataAccessException If a configuration error occurs
     * @throws UnauthorizedException
     */
    public void removeDocument(DocumentModel dm, Pair budget, Pair collectivite, String docName,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Checks if the designated document has already been imported
     * @param dm Document type
     * @param budget
     * @param collectivite
     * @param docName The name of document to remove
     * @param user
     * @return <tt>true</tt> if document exists, <tt>false</tt> otherwise
     * @throws DataConfigurationException If an error occurs while removing document
     * @throws DataAccessException If a configuration error occurs
     * @throws UnauthorizedException
     */
    public boolean isDocumentExists(DocumentModel dm, Pair budget, Pair collectivite, String docName,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Save these special keys in underlying data layer.
     * If <tt>sk1</tt>, <tt>sk2</tt> and <tt>sk3</tt> are null, does nothing.
     * @param dm Document type
     * @param budget
     * @param collectivite
     * @param docName
     * @param archiveName Archive file name, if applies, <tt>null</tt> otherwise
     * @param sk1 Special key 1
     * @param sk2 Special key 2
     * @param sk3 Special key 3
     * @param user
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public void saveSpecialKeys(DocumentModel dm, Pair budget, Pair collectivite, String docName, String archiveName, Pair sk1, Pair sk2, Pair sk3,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Returns <tt>true</tt> if database contains documents on this DocumentModel and on this EtatModel
     * @param dm
     * @param em
     * @param user
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public boolean canSearch(DocumentModel dm, EtatModel em,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Return the registered special keys of first level for this (<tt>dm</tt>, <tt>collectivite</tt>, <tt>budget</tt>)
     * @param dm
     * @param collectivite
     * @param budget
     * @param distinct
     * @param user
     * @return A <tt>Vector&lt;Pair&gt;</tt> of special keys
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public Vector<Pair> getSpecialKeys1(DocumentModel dm, Pair collectivite, Pair budget, boolean distinct,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Return the registered special keys of second level for this (<tt>dm</tt>, <tt>collectivite</tt>, <tt>budget</tt>, <tt>key1</tt>)
     * @param dm
     * @param collectivite
     * @param budget
     * @param key1
     * @param distinct
     * @param user
     * @return A <tt>Vector&lt;Pair&gt;</tt> of special keys
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public Vector<Pair> getSpecialKeys2(DocumentModel dm, Pair collectivite, Pair budget, Pair key1, boolean distinct,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Return the registered special keys of second level for this (<tt>dm</tt>, <tt>collectivite</tt>, <tt>budget</tt>, <tt>key1</tt>, <tt>key2</tt>)
     * @param dm
     * @param collectivite
     * @param budget
     * @param key1
     * @param key2
     * @param distinct
     * @param user
     * @return A <tt>Vector&lt;Pair&gt;</tt> of special keys
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public Vector<Pair> getSpecialKeys3(DocumentModel dm, Pair collectivite, Pair budget, Pair key1, Pair key2, boolean distinct,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * For a given document model, returns the parent collectivites of level <i>level</i>, that matches eventuals<i>otherParents</i>, this <i>user</i> is allowed to search in.<br/>
     * If <i>otherParents</i> is null or empty, returns all parent collectivites of required level.<br/>
     * If <i>otherParents</i> is not empty, <i>otherParents[0]</i> will be used to match <i>level+1</i> parent, <i>otherParents[1]</i> will be used to match <i>level+2</i> parent, and so on...
     * @param dm The document to search for
     * @param level The level of parent collectivite to find. <i>level</i> must be &gt;=1
     * @param otherParents The eventuals parents the returned parents must match
     * @param user The user that is performing the search
     * @return
     * @throws fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException
     * @throws fr.gouv.finances.dgfip.xemelios.data.DataAccessException
     * @throws fr.gouv.finances.dgfip.xemelios.auth.UnauthorizedException
     */
    public Vector<Pair> getParentCollectivites(DocumentModel dm, int level,Pair[] otherParents, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Returns the number of collectivites this <i>user</i> is allowed to search in for this <i>dm</i>.
     * @param dm The document model to search in
     * @param user The user who performs search
     * @return The number of available collectivites
     * @throws fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException
     * @throws fr.gouv.finances.dgfip.xemelios.data.DataAccessException
     * @throws fr.gouv.finances.dgfip.xemelios.auth.UnauthorizedException
     */
    public int getCollectivitesCount(DocumentModel dm, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;
    /**
     * Returns the list of collectivites that have documents of type <tt>dm</tt>
     * @param dm
     * @param parents Parents collectivites, if any. If many levels are given, level 1 is at parents[0], level 2 at parents[1], etc...
     * @param user
     * @return A <tt>Vector&lt;Pair&gt;</tt>
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public Vector<Pair> getCollectivites(DocumentModel dm,Pair[] parents, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;
    /**
     * Returns the list of archives that have imported
     * @param user
     * @return A <tt>Vector&lt;Pair&gt;</tt>
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public Vector<Pair> getArchivesImported(XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;
    /**
     * Returns the list of budgets that have documents of type <tt>dm</tt> for this <tt>collectivite</tt>
     * @param dm
     * @param collectivite
     * @param user
     * @return <tt>Vector&lt;Pair&gt;</tt>
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public Vector<Pair> getBudgets(DocumentModel dm, Pair collectivite,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Returns the list of budgets annexes that have documents of type <tt>dm</tt> for this <tt>collectivite</tt> and <tt>budget</tt> 
     * @param dm
     * @param collectivite
     * @param budget
     * @param user
     * @return <tt>Vector&lt;Pair&gt;</tt>
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public Vector<String> getBudgetsAnnexes(DocumentModel dm, EtatModel em, Pair collectivite, Pair budget, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Queries the repository of <tt>dm</tt> with the specified query <tt>rpm</tt> for the specified <tt>collectivite</tt>
     * @param dm The document-repository to query
     * @param collectivite the collectivite to query the repository on.
     * @param rpm The query
     * @param user
     * @return A {@link Collection} of {@link Pair} that contains the matching repository entries.
     * @throws DataConfigurationException If a invlid configuration is provided
     * @throws DataAccessException If a problem ooccurs while accessing the repository.
     */
    public Collection<Pair> queryRepository(DocumentModel dm, Pair collectivite, RecherchePaireModel rpm,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;
    /**
     * Queries the repository of <tt>dm</tt> with the specified query <tt>rpm</tt> for the specified <tt>collectivite</tt>
     * @param dm The document-repository to query
     * @param collectivite the collectivite to query the repository on.
     * @param rpm The query
     * @return A {@link Collection} of {@link Pair} that contains the matching repository entries.
     * @throws DataConfigurationException If a invlid configuration is provided
     * @throws DataAccessException If a problem ooccurs while accessing the repository.
     */
    public Collection<Pair> queryRepository(DocumentModel dm,Pair collectivite,RecherchePaireModel rpm,Pair refNomenclature,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Returns the repository for the document type and the collectivite.
     * @param dm
     * @param collectivite
     * @param budget Always ignored as repoository are per-(DocumentModel/Collectivite)
     * @param idNomenclature is id of the repository
     * @return The Xml
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public String getRepository(DocumentModel dm, Pair collectivite, Pair budget,String idNomenclature,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;
    /**
     * Returns the repositorys for the document type and the collectivite.
     * @param dm
     * @param collectivite
     * @param budget Always ignored as repoository are per-(DocumentModel/Collectivite)
     * @return The Xml
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public Vector<String> getRepositorys(DocumentModel dm, Pair collectivite, Pair budget,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Return the repository as a dom object.
     * @param dm
     * @param collectivite
     * @param budget
     * @return dom object
     */
    public Document getRepositoryAsDom(DocumentModel dm, Pair collectivite,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;
    /**
     * Return the repository as a dom object.
     * @param dm
     * @param collectivite
     * @param idNomenclature
     * @return dom object
     */
    public Document getRepositoryAsDom(DocumentModel dm, Pair collectivite,String idNomenclature,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Returns the distinct values for this <tt>path</tt>
     * @param em
     * @param collectivite
     * @param budget
     * @param path
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public Collection<String> getDistinctValues(ElementModel em, Pair collectivite, Pair budget, String path,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Removes all data corresponding to the specified DocumentModel
     * @param dm
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public boolean removeDocumentModel(DocumentModel dm,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Removes all data corresponding to the specified DocumentModel AND collectivite
     * @param dm
     * @param collectivite
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public boolean removeCollectivite(DocumentModel dm, Pair collectivite,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Removes all data corresponding to specified DocumentModel, collectivite and  budget
     * @param dm
     * @param collectivite
     * @param budget
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public boolean removeBudget(DocumentModel dm, Pair collectivite, Pair budget,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Removes all data correcponding to specified DocumentModel and fichier
     * @param dm
     * @param fichier
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public boolean removeFichier(DocumentModel dm, String fichier,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Removes all data corresponding to specified archive
     * @param dm
     * @param archiveName
     * @return always true or throws an exeption
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public boolean removeArchive(HashMap<String,DocumentModel> dms, String archiveName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;
    
    /**
     * Removes all data corresponding to specified DocumentModel, collectivite, budget and special key 1
     * @param dm
     * @param collectivite
     * @param budget
     * @param key1
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public boolean removeSpecialKey1(DocumentModel dm, Pair collectivite, Pair budget, Pair key1,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Removes all data corresponding to specified DocumentModel, collectivite, budget, special key 1 and 2
     * @param dm
     * @param collectivite
     * @param budget
     * @param key1
     * @param key2
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public boolean removeSpecialKey2(DocumentModel dm, Pair collectivite, Pair budget, Pair key1, Pair key2,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Removes all data corresponding to specified DocumentModel, collectivite, budget, special key 1, 2 and 3
     * @param dm
     * @param collectivite
     * @param budget
     * @param key1
     * @param key2
     * @param key3
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public boolean removeSpecialKey3(DocumentModel dm, Pair collectivite, Pair budget, Pair key1, Pair key2, Pair key3,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Returns an implementation of an empty result set. Very usefull when error occurs....
     * @return What you asked for.
     */
    public DataResultSet getEmptyDataResultSet();

    /**
     * Search the database for specified criterias.
     * This method is called by the UI when the user wants to query a list of documents and to display
     * the results in the result table.
     * @param elementModel	The elementModel to retrieve
     * @param collectivite	The collectivite where to search in
     * @param budget		The budget where to search in
     * @param xPath			The XPath to apply
     * @param lrm			The ListeResultatModel to apply to display in the table
     * @param models		The criterias typed by the user (this is to help the underlying implementation to translate from XPath to implementation query language)
     * @return				A DataResultSet that can be given to the result table.
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public DataResultSet search(ElementModel elementModel, Pair collectivite, Pair budget, String xPath, ListeResultatModel lrm, Vector<CritereModel> models,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Returns the list of available operators for the underlying implementation for the given datatype
     * @param datatype
     * @return
     */
    public Pair[] getOperators(String datatype);

    /**
     * Returns the specified document as a string.
     * @param etatModel
     * @param collectivite
     * @param budget
     * @param docId
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @deprecated 
     */
    @Deprecated
    public String getDocumentAsString(EtatModel etatModel, Pair collectivite, Pair budget, String docId,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Returns the specified document as a DOM Document
     * @param etatModel
     * @param collectivite
     * @param budget
     * @param docId
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public Document getDocumentAsDom(EtatModel etatModel, Pair collectivite, Pair budget, String docId,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Returns the specified document infos as a DocumentInfos
     * @param etatModel
     * @param collectivite
     * @param budget
     * @param docId
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public DocumentInfos getDocumentInfos(EtatModel etatModel, Pair collectivite, Pair budget, String docId,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * This method imports a PJRef object in data layer.
     * @param pj
     * @param archiveName PJ archive name, if applies, <tt>null</tt> otherwise
     * @param user
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public void importPj(PJRef pj, String archiveName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * This method retreives the content of a PJ as a byte array.
     * @param collectivite
     * @param pjName PJ name
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public PJRef getPj(Pair collectivite, String pjName,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * This method retreives the content of a PJ as a byte array.
     * @param collectivite
     * @param id
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public PJRef getPjByUniqueId(Pair collectivite, String id,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * This method retreives the content of a PJ as a byte array.
     * @param collectivite
     * @param id
     * @param user
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @throws UnauthorizedException
     */
    public boolean isPjAvailable(Pair collectivite, String id,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * This method retreives the initial doc name for docid
     * @param etatmodel
     * @param collectivite
     * @param budget
     * @param docid
     */
    public String getInitialDocNameFromDocId(EtatModel em, String collectivite, String budget, String docId, Connection con, XemeliosUser user) throws DataConfigurationException, SQLException, UnauthorizedException;
    
    /**
     * This method reset all cached data and configuration. Next call to any other
     * method will reload configuration files. This does not include database access configuration.
     * Implementors should throw an {@link java.lang.IllegalStateException} if they
     * do not implement this method.
     */
    public void reset();

    /**
     * This method returns the last warnings that occured in the <strong>current</strong> thread,
     * but do not remove them from cache, opposite to {@link getWarnings()}.
     * @return The last warnings
     */
    public String getLastWarnings();

    /**
     * This method retrieves the document's id that match criterias. It is usually called by UI to retrieve the root-browsable document.
     * @param em
     * @param colletivite
     * @param budget
     * @param sp1 The special key 1. Pass <tt>null</tt> to ignore.
     * @param sp2 The special key 2. Pass <tt>null</tt> to ignore.
     * @param sp3 The special key 3. Pass <tt>null</tt> to ignore.
     * @return
     * @throws DataAccessException
     * @throws DataConfigurationException
     */
    public ArrayList<String> getDocumentListFromSpecialKeys(ElementModel em, String colletivite, String budget, String sp1, String sp2, String sp3,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * This method does the same job than {@link getDocumentListFromSpecialKeys} but it tries to apply an xpath filter on result.
     * Implementations may limit the xpath syntax to use in this method and should so throw an {@link DataConfigurationException}
     * with an explicit message, such as "XPath too complicated"
     * @param em
     * @param collectivite
     * @param budget
     * @param sp1
     * @param sp2
     * @param sp3
     * @param path
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public ArrayList<String> getDocumentListFromXPath(ElementModel em, String collectivite, String budget, String sp1, String sp2, String sp3, String path,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * This method search the manifest that corresponding to this archiveName
     * @param archiveName
     * @param user
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public nu.xom.Document getManifesteFromArchive(String archiveName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * This method update all the manifest that corresponding to this archiveName to non import
     * Now, we drop manifeste and reimport it
     * @param archiveName
     * @param user
     * @return
     * @throws DataConfigurationException
     * @throws DataAccessException
     * @deprecated
     */
    @Deprecated
    public boolean updateManifeste(String archiveName, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    public SimpleDateFormat getDateFormatter();

    /**
     * This method returns a list of operators that can be used on the given data type, for instance sum, average, max, min, etc...
     * @param datatype The type of data we want to know the operators for
     * @return An array of Pair objects representing the operators, or an empty Array if no operator is applicable.
     */
    public Pair[] getAggregateOperators(String datatype);

    /**
     * Calculates aggregated value
     * @param dm
     * @param em
     * @param columnXPath
     * @param operator
     * @param champ
     * @param cm Informations on how data is displayed in column
     * @param rs The {@link DataResultSet} used for the original request
     * @param user
     * @return The formatted result
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public String calculateAggregate(DocumentModel dm, EtatModel em, String columnXPath, String operator, ChampModel cm, DataResultSet rs,XemeliosUser user)
            throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Inserts this export configuration
     * @param cm la representation de la configuration
     */
    public void insertConfigExport(ConfigModel cm,XemeliosUser user);

    /**
     * Updates this export configuration
     * @param cm la reprï¿½sentation de la configuration
     */
    public void updateConfigExport(ConfigModel cm,XemeliosUser user);

    /**
     * Recherche la liste des configurations d'export pour un etat d'un document.
     * @param docID
     * @param etatID
     * @return
     */
    public Vector<ConfigModel> getListeConfigsExport(String docID, String etatID,XemeliosUser user);

    /**
     * Recherche une configuration d'export pour un etat d'un document.
     * @param docID
     * @param etatID
     * @param configId
     * @return
     */
    public ConfigModel getConfigExport(String docId, String etatId, String configId, XemeliosUser user);
    /**
     * Removes this export configuration
     * @param cm
     */
    public void deleteConfigExport(ConfigModel cm,XemeliosUser user);

    /**
     * Searches and returns imported files that matches these parameters.
     * Only <tt>dm</tt> is mandatory.
     * @param dm
     * @param coll
     * @param budg
     * @return
     */
    public ExportableData searchFichiers(DocumentModel dm, String coll, String budg,XemeliosUser user) throws DataAccessException, DataConfigurationException, UnauthorizedException;

    /**
     * Search and returns imported files that matches parameters.
     * No parameter is mandatory
     * @param dms
     * @param coll
     * @param budg
     * @return
     * @throws DataAccessException
     * @throws DataConfigurationException
     */
    public Vector<ExportableData> searchFichiers(DocumentsModel dms, String coll, String budg,XemeliosUser user) throws DataAccessException, DataConfigurationException, UnauthorizedException;

    /**
     * Searches the data to export
     * @param whatToExport The level of data to export
     * @param em The ElementModel
     * @param coll The collectivity
     * @param budg The budget
     * @param fic imported file
     * @param SK1 Special key 1
     * @param SK2 Special Key 2
     * @return
     * @throws DataConfigurationException
     * @throws SQLException
     */
    public DataResultSet getDataResultSetForExport(int whatToExport, ElementModel em, Pair coll, Pair budg, String fic, Pair SK1, Pair SK2,XemeliosUser user) throws DataConfigurationException, SQLException, UnauthorizedException;

    /**
     * Returns a parameter value, or null if parameter does not exists
     * @param paramName
     * @return
     */
    public String getParameterValue(final String paramName);

    /**
     * Sets a parameter value
     * @param paramName
     * @param paramValue
     */
    public void setParameterValue(final String paramName, final String paramValue);


    // LM 02/2008 : gestion de tables temporaire pour stockage type hashtable non limitee par la memoire
    public Object HSWinitTempStorage(String storageId, String keyId, int maxKeyLength, Vector<java.lang.reflect.Field> fieldList);

    public void HSWdoCreateStorage(Object storageHandle);

    public void HSWdoInsert(Object storageHandle, String key, Object[] values);

    public boolean HSWdoExists(Object storageHandle, String key);

    public Object[] HSWdoSelect(Object storageHandle, String key, Object[] inoutObj);

    public boolean HSWdoDelete(Object storageHandle, String key);

    public int HSWdoCount(Object storageHandle);

    public boolean HSWdoDrop(Object storageHandle);

    /**
     * Returns true if this class runs on the database server
     * @return
     */
    public boolean isLocalComputerServer();
    /**
     * 
     * @param dm : DocumentModel
     * @param collectivite : collectivite pour laquelle on veux les repository
     * @return : Un vector de Pair(code,libelle) de repository
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public Vector<Pair> getListeCodeLibelleRepository(DocumentModel dm, Pair collectivite, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;
    /**
     * Returns the DOM repositorys for the document type and the collectivite.
     * @param dm
     * @param collectivite
     * @param budget Always ignored as repoository are per-(DocumentModel/Collectivite)
     * @return Vector of Nodes
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public Vector<Document> getRepositorysAsDom(DocumentModel dm, Pair collectivite,XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;

    /**
     * Returns the last value typed by this user in the input denoted by this ID
     * @param inputId
     * @param user
     * @return
     */
    public String getLastInputValue(String inputId,XemeliosUser user);

    /**
     * Saves the value typed by this user for the input denoted by this ID
     */
    public void setLastInputValue(String inputId, String value, XemeliosUser user);

    /**
     * Saves a personnal request into database. By default, a saved request is not public
     * @param em The ElementModel that request belongs to
     * @param recherche The request to save
     * @param user The user that request belongs to
     */
    public void saveRequest(ElementModel em, RechercheModel recherche, XemeliosUser user);

    /**
     * Returns saved request for that user and that ElementModel. Optionnaly, includes public requests
     * @param em The ElementModel requested
     * @param user The user requested
     * @param includePublics Indicates if it should or not include public requests
     * @return The found requests, or an empty List if nothing found
     */
    public List<RechercheModel> getSavedRequests(ElementModel em, XemeliosUser user, boolean includePublics);
    /**
     * Retreives a saved search
     * @param em The element that request belong to
     * @param user The user
     * @param searchId
     * @return The saved search, or null if not found
     */
    public RechercheModel getSavedRequest(ElementModel em, XemeliosUser user, String searchId);

    /**
     * Release all resources used by the DataImpl
     */
    public void shutdown();

    /**
     * Serializes this DataResultSet. Given drs has been generated by the presistence layer that is
     * responsible of serialization.
     * @param drs The DataResultSet to serialize
     * @return a byte buffer representing the DRS
     */
    public byte[] serializeDataResultSet(DataResultSet drs);

    /**
     * Deserialize a previously serialized DataResultSet. The given buffer has been generated by the
     * layer responsible of deserialization.
     * @param buffer The serialized buffer
     * @return The DataResultSet
     */
    public DataResultSet deserializeDataResultSet(byte[] buffer);
    /**
     * Creates a new export job
     * @param job
     * @param user
     * @throws fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException
     * @throws fr.gouv.finances.dgfip.xemelios.data.DataAccessException
     * @throws fr.gouv.finances.dgfip.xemelios.auth.UnauthorizedException
     */
    public void createExportJob(ExportJob job, XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;
    
    /**
     * Returns the list of available export jobs for the specified user.
     * @param user
     * @return
     * @throws fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException
     * @throws fr.gouv.finances.dgfip.xemelios.data.DataAccessException
     * @throws fr.gouv.finances.dgfip.xemelios.auth.UnauthorizedException
     */
    public List<ExportJob> getExportJobList(XemeliosUser user) throws DataConfigurationException, DataAccessException, UnauthorizedException;
    /**
     * Returns the export job. This method is designed to be used from batches, and should not be used in web or swing environments.
     * @param exportId
     * @param user
     * @return
     * @throws fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException
     * @throws fr.gouv.finances.dgfip.xemelios.data.DataAccessException
     * @throws fr.gouv.finances.dgfip.xemelios.auth.UnauthorizedException
     */
    public ExportJob getExportJob(long exportId,String user) throws DataConfigurationException, DataAccessException, UnauthorizedException;
    /**
     * Returns warnings generated by the current thread since last call. 
     * @return The string containing the warnings.
     */
    public String getWarnings();

    /**
     * Returns many informations for debugging. There is no specified syntax, this is free-form text.
     * Return may be null.
     * @return
     */
    public String getDatalayerConfiguration();
    /**
     * Permet de définir les propriétés d'execution de l'application
     * @param applicationProperties
     */
    public void setApplicationProperties(PropertiesExpansion applicationProperties);

    /**
     * Returns the values of documents configuration the <code>user</code> has overriden.
     * @param user
     * @return An <code>hashtable&lt;xpath, value&gt;</code>
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public Hashtable<String,Object> getRedefinedConfigValues(XemeliosUser user) throws DataConfigurationException, DataAccessException;

    /**
     * Defines the overriden values and un-overriden values from documents configuration for a specified <code>user</code>.
     * This method should mix the values to define and to undefine with the ones already defined.
     * @param valuesToSet
     * @param valuesToUnset
     * @param user
     * @throws DataConfigurationException
     * @throws DataAccessException
     */
    public void saveRedefinedValues(Hashtable<String,Object> valuesToSet, List<String> valuesToUnset, XemeliosUser user) throws DataConfigurationException, DataAccessException;

    /**
     * Declares an archive has been successfully imported. Implementations <b>must</b> register import date.
     * @param archiveName
     * @param user
     * @throws DataConfigurationException If a persistence configuration is not correct
     * @throws UnauthorizedException If user is not allowed to perform this operation
     */
    public void declareArchiveImported(String archiveName, XemeliosUser user) throws DataConfigurationException, UnauthorizedException;

    /**
     * Indicates if database is in a state where an import of this type of document is possible.
     * @param documentId The documentType
     * @param user
     * @return <tt>true</tt> if database has been patched and imports can be done, <tt>false</tt> otherwise.
     * @throws UnauthorizedException
     */
    public boolean canImportDocument(String documentId, XemeliosUser user) throws UnauthorizedException;
    /**
     * Permet d'indiquer si on veut que les configurations de persistences soient mises en cache ou non.
     * Par défaut, elles sont mises en cache.
     * Peut être modifié à tout moment.
     * @param useCachedPersistence
     */
    public void setUseCachedPersistence(boolean useCachedPersistence);
}
