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

package fr.gouv.finances.dgfip.xemelios.common;

/**
 * All constants
 * @author chm
 */
public interface Constants {
    public final String NOM_APP = "XEMELIOS";
    public final String DISPLAY_NOM_APP = "XeMeLios";
    
    // system properties
//    public final String CONF_FILE_NAME_PROPERTY = NOM_APP.replaceAll(" ","")+".conf.file.name";
    /**
     * Emplacement des fichiers de configuration de documents. Si cette propri�t�
     * contient une liste de r�pertoires, tous les r�pertoires seront trait�s
     * l'un apr�s l'autre
     */
    public static final String SYS_PROP_DOC_DEF_DIR             = "xemelios.documents.def.dir";
    /**
     * Emplacement du fichier de configuration de Log4J. Ce doit �tre une configuration XML.
     */
    public static final String SYS_PROP_LOG4J                   = "xemelios.log4j.xml";
    /**
     * Liste des classes de persistence disponibles, s�par�es par des virgules (&apos;,&apos;).
     */
    public static final String SYS_PROP_AVAILABLE_LAYERS        = "xemelios.available.datalayers";
    /**
     * Nom de la couche de persistence � d�marrer. C'est bien le nom, tel que renvoy� par
     * {@link fr.gouv.finances.dgfip.xemelios.data.AbstractDataImpl#getLayerName()}, et pas le
     * nom de la classe.
     */
    public static final String SYS_PROP_DATA_IMPL               = "xemelios.data.impl";
    public static final String PERSONNAL_CONFIG_FILENAME        = "xemelios-config.xml";
    public static final String CRITERIAS_INPUTS_FILENAME        = "xemelios-criterias-inputs.properties";
    /**
     * Ancien fichier (V4) o� �taient enregistr�s les composants install�s.
     */
    @Deprecated
    public static final String SYS_PROP_COMPONENTS_FILENAME     = "xemelios.components.filename";
    /**
     * Nom du fichier contenant les composants install�s
     */
    public static final String SYS_PROP_INSTALLED_FILENAME      = "xemelios.installed.filename";
    /**
     * Source o� t�l�charger les mises � jour.
     * Plus utilis� depuis la V5
     */
    @Deprecated
    public static final String SYS_PROP_UPDATE_SOURCE           = "xemelios.updater.source";
    public static final String SYS_PROP_PROXY_SERVER            = "xemelios.proxy.server";
    public static final String SYS_PROP_PROXY_PORT              = "xemelios.proxy.port";
    public static final String SYS_PROP_PROXY_USER              = "xemelios.proxy.user";
    public static final String SYS_PROP_PROXY_PASSWD            = "xemelios.proxy.password";
    public static final String SYS_PROP_PROXY_DOMAIN            = "xemelios.proxy.domain";
    /**
     * &quot;true&quot; permet de logger le XML des documents charg�s depuis la persistence.
     */
    public static final String SYS_PROP_LOG_DOM_DOC             = "xemelios.log.dom.doc";
    /**
     * Dernier r�peroitre depusi lequel on a choisit un fichier � importer
     */
    public static final String SYS_PROP_LAST_INPUT_DIRECTORY    = "xemelios.last.input.directory";

    /**
     * Format de date � utiliser pour les affichages dans XeMeLios
     */
    public static final String SYS_PROP_DATE_FORMAT             = "xemelios.date.format";
    /**
     * Permet de sp�cifier si on fait des exports excel ou non. Plus utilis� depuis la V5.
     */
    @Deprecated
    public static final String SYS_PROP_EXPORT_EXCEL            = "xemelios.export.xls";
    /**
     * Ligne de commande permettant de lancer le navigateur. Utile seulement pour les
     * machines dont l'OS n'est pas habituel (Windows, Linux ou MAC)
     */
    public static final String SYS_PROP_NAVIGATOR_CMD_LINE      = "navigator.command.line";
    /**
     * Pr�fixe � ajouter � une URL (de fichier) pour pouvoir la lancer en param�tre
     * de ligne de commande du navigateur. Normalement, ne devrait plus �tre utilis�.
     */
    @Deprecated
    public static final String SYS_PROP_NAVIGATOR_PREFIX        = "navigator.url.prefix";
    /**
     * Permet de lancer l'IHM Swing en mode debug. Extr�mement p�nalisant, � n'utiliser
     * qu'ne sachant parfaitement ce qu'on fait.
     */
    public static final String SYS_PROP_DEBUG_UI                = "xemelios.debug.ui";
    /**
     * Permet de ne pas effectuer le contr�le de conformit� au sch�ma. Ne semble plus
     * �tre utilis�.
     */
    public static final String SYS_PROP_SKIP_VALIDATION_SCHEMA  = "xemelios.control.skipSchemaValidation";
    /**
     * Interdit la mise en cache des XHTML g�n�r�s lors des navigations ou des visualisations.
     * Utile uniquement en d�veloppement, lorsque les XSL de visualisation changent
     */
    public static final String SYS_PROP_NAVIGATE_FORCE_RELOAD   = "xemelios.navigation.force-reload";
    /**
     * Emplacement o� les ressources sont stock�es.
     * TODO: reprendre compl�tement le fonctionnement des ressources.
     */
    public static final String SYS_PROP_RESOURCES_LOCATION      = "xemelios.resources.location";
    /**
     * &quot;true&quot; ajoute un menu dans Fichier permettant de lancer le GC. Affiche un RamViewer
     * dans la StatusBar
     */
    public static final String SYS_PROP_ADMIN_MENU_GC           = "admin.menu.gc";
    /**
     * &quot;true&quot; ajoute dans le menu Fichier un menu permettant de recharger
     * la configuration des documents. Utile en d�veloppement pour tester les changement
     * de cofniguration. <b>Attention</b>, il faut r�ouvrir les fen�tres de recherche
     * pour prendre en compte les nouvelles configurations charg�es.
     */
    public static final String SYS_PROP_RESET_CONFIG            = "xemelios.reset.config";
    /**
     * URL de l'aide en ligne.
     */
    public static final String SYS_PROP_HELP_URL                = "xemelios.help.url";
    /**
     * Emplacement des programmes XeMeLios. Cette propri�t� est requise absolument
     * dans les param�tres de lancement de XeMeLios.
     * En g�n�ral vaut <tt>C:/Program Files/DGCP/Xemelios/Xemelios</tt>
     */
    public static final String SYS_PROP_XEMELIOS_PRG            = "xemelios.prg";
    /**
     * Permet d'�viter la v�rification automatique de la pr�sence de mises � jour.
     * Utile en d�veloppement.
     */
    public static final String SYS_PROP_SKIP_UPDATE_CHECK       = "xemelios.skip.update.check";
    /**
     * R�pertoire o� les jars des outils compagnons sont.
     */
    public static final String SYS_PROP_XEMELIOS_TOOLS_DIR      = "xemelios.tools.directory";
    /**
     * Permet d'emp�cher le chargement dynamique des jars des outils compagnons. Pour
     * que cela fonctionne, il faut que les classes des outils compagnons soient dans le classpath
     * de XeMeLios, et que le jar soit pr�sent pour qu'il soit scann� par le chargeur d'outils
     * compagnons.
     */
    public static final String SYS_PROP_XEMELIOS_AVOID_TOOL_JAR_LOADING = "xemelios.tools.avoid-jar-loading";

    /**
     * Classe prenant en charge les exports
     */
    public static final String SYS_PROP_XEMELIOS_EXPORTER_CLASS = "xemelios.exporter.class";

    /**
     * &quot;true&quot; permet d'afficher des informations du pool de connection dans la
     * status bar. Utile pour v�rifier qu'on a bien relach� toutes les connexions � la
     * base de donn�es.
     */
    public static final String SYS_PROP_SHOW_POOL_STATUS        = "xemelios.display.pool.status";
    /**
     * La valeur � sp�cifier comme type de document lorsqu'on importe une archive XeMeLios
     * depuis l'importeur en ligne de commande.
     */
    public static final String XEMELIOS_ARCHIVE_SIGN            = "xemelios.archive";
    /**
     * Un truc immonde � ne pas utiliser, pour avoir un r�f�rentiel des collectivit�s.
     * Ce truc n'aurait jamais du exister.
     */
    public static final String SYS_PROP_AUTH_REF_URL            = "xemelios.auth.referentiel.url";
    /**
     * &quot;true&quot; premet d'�diter dans XeMeLios les propri�t�s syst�mes d�finies
     * (en fait, les propri�t�s dont les noms sont ici...).
     */
    public static final String SYS_PROP_PNL_SYSPROP             = "xemelios.system.properties.editable";

    /**
     * Permet d'�viter la v�rification des archives avant import. Plus utilis� depuis
     * {@link fr.gouv.finances.dgfip.xemelios.importers.archives.ArchiveImporter}
     */
    @Deprecated
    public static final String SYS_PROP_ARCH_AVOID_CHECK        = "xemelios.archive.avoid.check.import";

    /**
     * Strat�gie de mise � jour � utiliser. Les valeurs possibles sont
     * <ul><li>{@link #MAJ_SILENT} installe les mises � jour sans poser de questions</li>
     * <li>{@link #MAJ_CONFIRM} demande � l'utilisateur si il faut installer</li>
     * <li>{@link #MAJ_NONE} n'installe jamais les mises � jour trouv�es. Ceci est diff�rent
     * de {@link #SYS_PROP_SKIP_UPDATE_CHECK}, puisqu'on v�rifie quand m�me si il y a des choses
     * disponibles.</li></ul>
     */
    public static final String SYS_PROP_MAJ_STRATEGY            = "xemelios.maj.strategy";
    public static final String MAJ_SILENT                       = "silent";
    public static final String MAJ_CONFIRM                      = "confirm";
    public static final String MAJ_NONE                         = "none";
    /**
     * Sources des mises � jour. Les valeurs possibles sont :
     * <ul><li>{@link #MAJ_PROD} pour les mises � jour grand public</li>
     * <li>{@link #MAJ_RECETTE} pour les mises � jour destin�es aux beta-testeurs</li>
     * <li>{@link #MAJ_PRIVATE} pour les mises � jour destin�es � la recette</li>
     * </ul>
     */
    public static final String SYS_PROP_MAJ_CONFIG              = "xemelios.maj.config";
    public static final String MAJ_PROD                         = "PRODUCTION";
    public static final String MAJ_RECETTE                      = "RECETTE";
    public static final String MAJ_PRIVATE                      = "PRIVATE";
    /**
     * URL du fichier de d�finition des profils
     */
    public static final String SYS_PROP_MAJ_PROFILS_URL         = "xemelios.profils.source";

    /**
     * Si non d�finit, on est au premier d�marrage de XeMeLios. <b>Attention</b>: si
     * vaut &quot;true&quot; cela signifie que le premi�er d�marrage a d�j� �t�
     * effectu� compl�tement.
     */
    public static final String SYS_PROP_FIRST_START             = "xemelios.first.start";
    /**
     * Permet de forcer un poste qui n'est pas sur le serveur
     * a mettre � jour la base de V4 � V5
     */
    public static final String SYS_PROP_FORCE_DB_UPGRADE        = "xemelios.db.upgrade.force";
    /**
     * Permet d'autoriser un poste qui n'est pas physiquement
     * sur le serveur � modifier l'authentification
     */
    public static final String SYS_PROP_FORCE_AUTH_CFG          = "xemelios.auth.cfg.force";

    // pour monitorer les imports
    public static final String SYS_PROP_IMPORT_TIMING           = "xemelios.import.timing.log.file";
    public static final String SYS_PROP_IMPORT_AVOID_DELETE     = "xemelios.import.avoid.delete";

    public static final String SYS_PROP_LOG4J_PATCHED          = "xemelios.log4j.patched";

    public static final String SYS_PROP_IMPORT_ARCHIVE_MODE     = "xemelios.archive.import.mode";
    public static final String IMPORT_ARCHIVE_TOTAL             = "total";
    public static final String IMPORT_ARCHIVE_PARTIEL           = "partiel";
    
    public static final String ANOMALLY_NS_URI = "http://projets.admisource.gouv.fr/xemelios/namespaces#anomally";
    public static final String ADDED_NS_URI = "http://projets.admisource.gouv.fr/xemelios/namespaces#added";
    public static final String CONFIG_NS_URI = "http://projets.admisource.gouv.fr/xemelios/namespaces#config";
    public static final String MANIFESTE_NS_URI = "http://www.xemelios.org/namespaces#manifeste";
    public static final String RULES_NS_URI = "http://xemelios.org/schemas#import-rules";

    /**
     * Nombre limite de collectivit�s � afficher dans la liste d�roulante de choix de collectivit�.
     */
    public static final int MAX_DISPLAYED_COLLECTIVITES = 5;
}
