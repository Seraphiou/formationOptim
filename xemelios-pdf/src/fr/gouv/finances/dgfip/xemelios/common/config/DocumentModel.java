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
package fr.gouv.finances.dgfip.xemelios.common.config;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.dgfip.utils.Pair;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;
import fr.gouv.finances.dgfip.xemelios.common.Constants;
import javax.xml.namespace.QName;

/**
 * Modelise un document
 * @author chm
 */
public class DocumentModel implements NoeudModifiable, EnvironmentDomain, Comparable {

    public static final transient String TAG = "document";
    public static final transient QName QN = new QName(TAG);
    public static final transient String DEFAULT_LIBELLE_COLLECTIVITE = "Collectivité";
    private static Logger logger = Logger.getLogger(DocumentModel.class);
    private NoeudModifiable _NMParent = null;
    private String configXPath = null;
    private Vector<EtatModel> etats;
    private HashMap<String, EtatModel> hEtats;
    private Vector<EnvironmentDomain> vEtats;
    private Vector<String> entetes;
    private Vector<Pair> defaultBudgets;
    private HashMap<String, OptionModel> hDefaultBudgets; // attention, on n'utilise pas de Pair mais un OptionModel pour sa compatibilité avec l'interface NoeudModifiable.
    private Vector<SpecialKeyModel> specialKeys;
    private HashMap<String, SpecialKeyModel> hSpecialKeys;
    private HashMap<String, EnteteModel> hEnteteModels;
    private String id,  extension,  schema,  titre,  balise,  referentiel,  importClass,  indexCreation,  libelleExtension,  referentielXsltFile,  defaultBrowsableEtat,  referentielImportTransfo,  persistenceConfigFile,  globalImportXsltFile,  globalUriTransformer, xsltFileChooser;
    private RecherchePaireModel budgetPath, referenceNomenclaturePath;
    private CollectiviteInfosModel collectivitePath;
    private NamespaceModel namespaces = null;
    private boolean browsable = false,  isControlable = false;
    private boolean groupMenuItems = true;
    private String controlConfigFile;
    private ParametersModel parameters;
    private String extendsDocId;
    private String pjNamespaceUri;
    private String displayInMenuIf;
    private String defaultEtatGlobal;
    private String libelleCollectivite = DEFAULT_LIBELLE_COLLECTIVITE;
    private String textToXmlTransformer = null;
    private int ordrePresentation = 1000;
    private Vector<TraitementExterneModel> traitementExternes;
    private HashMap<String, TraitementExterneModel> hTraitementExternes;
    private String baseDirectory = null;
    private String linkResolverClassName;
    private QName qn;
    private String natureIdentifiantCollectivite;
    private Hashtable<String,ResourceModel> resources;
    private boolean importable = true;
    private HelpModel help;
    private String navigateToDocumentSwingClassName, navigateToDocumentWebClassName;
    // utilisé uniquement pour l'aide
    private String genreTitre, cardinaliteTitre;
    private String archiveVentilationAttrName;

    public DocumentModel(QName tagName) {
        super();
        this.qn = tagName;
        etats = new Vector<EtatModel>();
        hEtats = new HashMap<String, EtatModel>();
        vEtats = new Vector<EnvironmentDomain>();
        entetes = new Vector<String>();
        hEnteteModels = new HashMap<String, EnteteModel>();
        defaultBudgets = new Vector<Pair>();
        hDefaultBudgets = new HashMap<String, OptionModel>();
        specialKeys = new Vector<SpecialKeyModel>();
        hSpecialKeys = new HashMap<String, SpecialKeyModel>();

        traitementExternes = new Vector<TraitementExterneModel>();
        hTraitementExternes = new HashMap<String, TraitementExterneModel>();
        resources = new Hashtable<String, ResourceModel>();
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
    }

    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (EnteteModel.QN.equals(tagName)) {
            EnteteModel em = (EnteteModel) child;
            if (hEnteteModels.containsKey(em.getId())) {
                EnteteModel old = hEnteteModels.get(em.getId());
                entetes.remove(old.getBalise());
                hEnteteModels.remove(old.getId());
            }
            em.setParentAsNoeudModifiable(this);
            String key = ((EnteteModel) child).getBalise();
            entetes.add(key);
            hEnteteModels.put(em.getId(), em);
        } else if (EtatModel.QN.equals(tagName)) {
            EtatModel etat = (EtatModel) child;
            if (hEtats.containsKey(etat.getId())) {
                EtatModel old = hEtats.get(etat.getId());
                etats.remove(old);
                vEtats.remove(old);
                hEtats.remove(old.getId());
            }
            etat.setParent(this);
            etat.setParentAsNoeudModifiable(this);
            etats.add(etat);
            vEtats.add(etat);
            hEtats.put(etat.getId(), etat);
        } else if (DocumentsMapping.BUDGET_PATH.equals(tagName)) {
            budgetPath = (RecherchePaireModel) child;
            budgetPath.setParentAsNoeudModifiable(this);
        } else if (CollectiviteInfosModel.QN.equals(tagName)) {
            collectivitePath = (CollectiviteInfosModel) child;
            collectivitePath.setParentAsNoeudModifiable(this);
        } else if (DocumentsMapping.REFERENCE_NOMENCLATURE_PATH.equals(tagName)) {
            referenceNomenclaturePath = (RecherchePaireModel) child;
            referenceNomenclaturePath.setParentAsNoeudModifiable(this);            
        } else if (DocumentsMapping.DEFAULT_BUDGET.equals(tagName)) {
            OptionModel om = (OptionModel) child;
            if (hDefaultBudgets.containsKey(om.getValue())) {
                OptionModel old = hDefaultBudgets.get(om.getValue());
                defaultBudgets.remove(new Pair(old.getValue(), old.getLibelle()));
                hDefaultBudgets.remove(old.getValue());
            }
            om.setParentAsNoeudModifiable(this);
            Pair p = new Pair(om.getValue(), om.getLibelle());
            defaultBudgets.add(p);
            hDefaultBudgets.put(om.getValue(), om);
        } else if (SpecialKeyModel.QN.equals(tagName)) {
            SpecialKeyModel skm = (SpecialKeyModel) child;
            if (hSpecialKeys.containsKey(skm.getId())) {
                SpecialKeyModel old = hSpecialKeys.get(skm.getId());
                specialKeys.remove(old);
                hSpecialKeys.remove(old.getId());
            }
            skm.setParentAsNoeudModifiable(this);
            specialKeys.add(skm);
            hSpecialKeys.put(skm.getId(), skm);
        } else if (NamespaceModel.QN.equals(tagName)) {
            namespaces = (NamespaceModel) child;
            namespaces.setParentAsNoeudModifiable(this);
        } else if (ParametersModel.QN.equals(tagName)) {
            parameters = (ParametersModel) child;
            parameters.setParentAsNoeudModifiable(this);
        } else if (TraitementExterneModel.QN.equals(tagName)) {
            TraitementExterneModel traitementExterne = (TraitementExterneModel) child;
            if (hTraitementExternes.containsKey(traitementExterne.getId())) {
                TraitementExterneModel old = hTraitementExternes.get(traitementExterne.getId());
                traitementExternes.remove(old);
                hTraitementExternes.remove(old.getId());
            }
            //     traitementExterne.setParent(this);
            traitementExterne.setParentAsNoeudModifiable(this);
            traitementExternes.add(traitementExterne);
            hTraitementExternes.put(traitementExterne.getId(), traitementExterne);
        } else if(ResourceModel.QN.equals(tagName)) {
            ResourceModel rm = (ResourceModel)child;
            resources.put(rm.getId(),rm);
        } else if(HelpModel.QN.equals(tagName)) {
            help = (HelpModel)child;
        }
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        String newId = attributes.getValue("id");
        if (newId != null && (id == null || !id.equals(newId))) {
            id = attributes.getValue("id");
            baseDirectory = null;
        }
        extension = (attributes.getValue("extension") != null ? attributes.getValue("extension") : extension);
        schema = (attributes.getValue("schema") != null ? attributes.getValue("schema") : schema);
        titre = (attributes.getValue("titre") != null ? attributes.getValue("titre") : titre);
        balise = (attributes.getValue("balise") != null ? attributes.getValue("balise") : balise);
        referentiel = (attributes.getValue("referentiel") != null ? attributes.getValue("referentiel") : referentiel);
        importClass = (attributes.getValue("import-class") != null ? attributes.getValue("import-class") : importClass);
        indexCreation = (attributes.getValue("index-creation") != null ? attributes.getValue("index-creation") : indexCreation);
        libelleExtension = (attributes.getValue("libelle-extension") != null ? attributes.getValue("libelle-extension") : libelleExtension);
        referentielXsltFile = (attributes.getValue("referentiel-xslt-file") != null ? attributes.getValue("referentiel-xslt-file") : referentielXsltFile);
        xsltFileChooser = (attributes.getValue("xslt-file-chooser") != null ? attributes.getValue("xslt-file-chooser") : xsltFileChooser);
        if (attributes.getValue("browsable") != null) {
            browsable = attributes.getBooleanValue("browsable");
        }
        if (attributes.getValue("isControlable") != null) {
            isControlable = attributes.getBooleanValue("isControlable");
        }
        defaultBrowsableEtat = (attributes.getValue("default-browsable-etat") != null ? attributes.getValue("default-browsable-etat") : defaultBrowsableEtat);
        referentielImportTransfo = (attributes.getValue("referentiel-import-xslt") != null ? attributes.getValue("referentiel-import-xslt") : referentielImportTransfo);
        persistenceConfigFile = (attributes.getValue("persistence-config") != null ? attributes.getValue("persistence-config") : persistenceConfigFile);
        if (attributes.getValue("menu-grouping") != null) {
            groupMenuItems = ("group".equals(attributes.getValue("menu-grouping")));
        }
        controlConfigFile = (attributes.getValue("control-config-file") != null ? attributes.getValue("control-config-file") : controlConfigFile);
        pjNamespaceUri = (attributes.getValue("pj-namespace-uri") != null ? attributes.getValue("pj-namespace-uri") : pjNamespaceUri);
        // celle-la n'est jamais heritee
        setDisplayInMenuIf(attributes.getValue("displayInMenuIf"));
        if (attributes.getValue("ordre-presentation") != null) {
            ordrePresentation = attributes.getIntValue("ordre-presentation");
        }
        globalImportXsltFile = (attributes.getValue("global-import-xslt-file") != null ? attributes.getValue("global-import-xslt-file") : globalImportXsltFile);
        globalUriTransformer = (attributes.getValue("global-uri-transformer") != null ? attributes.getValue("global-uri-transformer") : globalUriTransformer);
        extendsDocId = (attributes.getValue("extends") != null ? attributes.getValue("extends") : extendsDocId);
        defaultEtatGlobal = (attributes.getValue("default-etat-global") != null ? attributes.getValue("default-etat-global") : defaultEtatGlobal);
        libelleCollectivite = (attributes.getValue("libelle-collectivite") != null ? attributes.getValue("libelle-collectivite") : libelleCollectivite);
        linkResolverClassName = (attributes.getValue("link-resolver")!=null ? attributes.getValue("link-resolver") : linkResolverClassName);
        natureIdentifiantCollectivite = (attributes.getValue("natIdColl")!=null ? attributes.getValue("natIdColl") : natureIdentifiantCollectivite);
        textToXmlTransformer = (attributes.getValue("textToXmlTransformer")!=null ? attributes.getValue("textToXmlTransformer") : textToXmlTransformer);
        importable = (attributes.getValue("importable")!=null ? attributes.getBooleanValue("importable") : importable);
        navigateToDocumentSwingClassName = (attributes.getValue("navigate-to-document-class-swing")!=null ? attributes.getValue("navigate-to-document-class-swing") : navigateToDocumentSwingClassName);
        navigateToDocumentWebClassName = (attributes.getValue("navigate-to-document-class-web")!=null ? attributes.getValue("navigate-to-document-class-web") : navigateToDocumentWebClassName);
        genreTitre = (attributes.getValue("genre-titre")!=null ? attributes.getValue("genre-titre") : genreTitre);
        cardinaliteTitre = (attributes.getValue("cardinalite-titre")!=null ? attributes.getValue("cardinalite-titre") : cardinaliteTitre);
        archiveVentilationAttrName = (attributes.getValue("archive-ventil-attr")!=null ? attributes.getValue("archive-ventil-attr") : archiveVentilationAttrName);
        return this;
    }

    @Override
    public void marshall(XmlOutputter output) {
        // faut croire que ce n'est jamais appelé, ça !
        output.startTag(QN);
        // pour le moment, on e met que ce qui est nécessaire
        output.addAttribute("id", id);
        output.addAttribute("titre", titre);
        if(genreTitre!=null) output.addAttribute("genre-titre",genreTitre);
        if(cardinaliteTitre!=null) output.addAttribute("cardinalite-titre", cardinaliteTitre);
        for(EtatModel etat:etats) etat.marshall(output);
        output.endTag(QN);
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        // V5 : tous les ID doivent être en minuscules strictement
        if(!id.toLowerCase().equals(id)) {
            throw new InvalidXmlDefinition("Les identifiants de documents doivent impérativement être en minuscules. "+id+" n'est pas une valeur correcte.");
        }
        // namespaces
        if (namespaces == null) {
            throw new InvalidXmlDefinition("namespaces is required.");
        }
        namespaces.validate();
        // special-keys
        if (specialKeys.size() > 3) {
            throw new InvalidXmlDefinition("<" + TAG + "> must not have more than 3 <" + SpecialKeyModel.TAG + "/>.");
        }
        TreeSet<Integer> spIndices = new TreeSet<Integer>();
        for (SpecialKeyModel skm : specialKeys) {
            spIndices.add(new Integer(skm.getPos()));
            skm.validate();
        }
        if (spIndices.size() != specialKeys.size()) {
            throw new InvalidXmlDefinition("//special-key/@pos must be uniques in a <" + TAG + "> element.");
        }
        int i = 1;
        for (Integer count : spIndices) {
            if (count.intValue() != i) {
                throw new InvalidXmlDefinition("//special-key/@pos attributes must start at 1 and be consecutives");
            }
            i++;
        }
        // default-budget
        for (OptionModel om : hDefaultBudgets.values()) {
            om.validate();
        }
        if (collectivitePath != null) {
            collectivitePath.validate();
        }
        if (budgetPath != null) {
            budgetPath.validate();
        }
        if (referenceNomenclaturePath != null) {
        	referenceNomenclaturePath.validate();
        }        
        if (parameters != null) {
            parameters.validate();
        }
        for (EtatModel em : etats) {
            em.validate();
        }

        // verification de la presence des fichiers lies
        if (persistenceConfigFile != null) {
            File f = new File(new File(getBaseDirectory()), persistenceConfigFile);
            if (!f.exists()) {
                throw new InvalidXmlDefinition("file " + f.getAbsolutePath() + " can not be found");
            }
        }
        if (referentielXsltFile != null) {
            File f = new File(new File(getBaseDirectory()), referentielXsltFile);
            if (!f.exists()) {
                throw new InvalidXmlDefinition("file " + f.getAbsolutePath() + " can not be found");
            }
        }
        if (controlConfigFile != null) {
            File f = new File(new File(getBaseDirectory()), controlConfigFile);
            if (!f.exists()) {
                throw new InvalidXmlDefinition("file " + f.getAbsolutePath() + " can not be found");
            }
        }
        if (defaultEtatGlobal != null && getEtatById(defaultEtatGlobal) == null) {
            throw new InvalidXmlDefinition("L'état " + defaultEtatGlobal + " n'est pas défini dans " + getId());
        }
        if(natureIdentifiantCollectivite==null) {
            logger.error("",new InvalidXmlDefinition("L'attribut natIdColl(nature identifiant collectivité) est requis pour "+getId()));
        }
    }

    public String getBalise() {
        return balise;
    }

    public Vector<EtatModel> getEtats() {
        return etats;
    }

    public String getExtension() {
        return extension;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getIdValue() {
        return getId();
    }

    public String getReferentiel() {
        return referentiel;
    }

    public String getSchema() {
        return schema;
    }

    public String getTitre() {
        return titre;
    }

    public String getImportClass() {
        return importClass;
    }

    public boolean isControlable() {
        return isControlable;
    }

    public EtatModel getEtatById(String etatId) {
        for (Enumeration<EtatModel> enumer = etats.elements(); enumer.hasMoreElements();) {
            EtatModel etat = enumer.nextElement();
            if (etat.getId().equals(etatId)) {
                return etat;
            }
        }
        return null;
    }

    public EtatModel getEtatByTagName(String tagName) {
        for (Enumeration<EtatModel> enumer = etats.elements(); enumer.hasMoreElements();) {
            EtatModel etat = enumer.nextElement();
            if (etat.getBalise().equals(tagName)) {
                return etat;
            }
        }
        return null;
    }

    public TraitementExterneModel getTraitementExterneById(String traitementExterneId) {
        for (Enumeration<TraitementExterneModel> enumer = traitementExternes.elements(); enumer.hasMoreElements();) {
            TraitementExterneModel traitementExterne = enumer.nextElement();
            if (traitementExterne.getId().equals(traitementExterneId)) {
                return traitementExterne;
            }
        }
        return null;
    }

    public Vector<String> getEntetes() {
        return entetes;
    }

    public RecherchePaireModel getBudgetPath() {
        return budgetPath;
    }

    public CollectiviteInfosModel getCollectivitePath() {
        return collectivitePath;
    }
    public RecherchePaireModel getReferenceNomenclaturePath() {
        return referenceNomenclaturePath;
    }

    public String getLibelleExtension() {
        return libelleExtension == null ? getTitre() : libelleExtension;
    }

    public Hashtable<String, EnteteModel> getMap() {
        Hashtable<String, EnteteModel> tmp = new Hashtable<String, EnteteModel>();
        for (String s : hEnteteModels.keySet()) {
            tmp.put(s, hEnteteModels.get(s));
        }
        return tmp;
    }

    public String getIndexCreation() {
        return indexCreation;
    }

    @Override
    public String toString() {
        return titre;
    }

    public Vector<Pair> getDefaultBudgets() {
        return defaultBudgets;
    }

    public String getReferentielXsltFile() {
        return referentielXsltFile;
    }

    public String getXsltFileChooser() {
        return xsltFileChooser;
    }

    @Override
    public DocumentModel clone() {
        DocumentModel dm = new DocumentModel(QN);
        for (EtatModel em : this.etats) {
            try {
                dm.addChild(em.clone(dm), EtatModel.QN);
            } catch (Throwable t) {
                logger.error("clone().etats",t);
            }
        }

        for (TraitementExterneModel em : this.traitementExternes) {
            try {
                dm.addChild(em.clone(), TraitementExterneModel.QN);
            } catch (Throwable t) {
                logger.error("clone().traitementExterne",t);
            }
        }
        for (String s : this.hEnteteModels.keySet()) {
            try {
                dm.addChild(this.hEnteteModels.get(s).clone(), EnteteModel.QN);
            } catch (Throwable t) {
                logger.error("clone().entete",t);
            }
        }
        dm.id = this.id;
        dm.extension = this.extension;
        dm.schema = this.schema;
        dm.titre = this.titre;
        dm.balise = this.balise;
        dm.referentiel = this.referentiel;
        dm.importClass = this.importClass;
        dm.indexCreation = this.indexCreation;
        dm.libelleExtension = this.libelleExtension;
        dm.referentielXsltFile = this.referentielXsltFile;
        dm.xsltFileChooser = this.xsltFileChooser;
        dm.browsable = this.browsable;
        dm.isControlable = this.isControlable;
        dm.defaultBrowsableEtat = defaultBrowsableEtat;
        dm.extendsDocId = this.extendsDocId;
        dm.setDisplayInMenuIf(this.getDisplayInMenuIf());
        try {
            if(this.collectivitePath!=null)
                dm.addChild(this.collectivitePath.clone(), CollectiviteInfosModel.QN);
        } catch (Throwable t) {
            logger.error("clone().collectivitePath",t);
        }
        try {
            if(this.budgetPath!=null)
                dm.addChild(this.budgetPath.clone(), DocumentsMapping.BUDGET_PATH);
        } catch (Throwable t) {
            logger.error("clone().budgetPath",t);
        }
        try {
            if(this.referenceNomenclaturePath!=null)
                dm.addChild(this.referenceNomenclaturePath.clone(), DocumentsMapping.REFERENCE_NOMENCLATURE_PATH);
        } catch (Throwable t) {
            logger.error("clone().nomenclaturePath",t);
        }
        dm.persistenceConfigFile = this.persistenceConfigFile;
        for (String s : this.hDefaultBudgets.keySet()) {
            try {
                dm.addChild(this.hDefaultBudgets.get(s).clone(), DocumentsMapping.DEFAULT_BUDGET);
            } catch (Throwable t) {
                logger.error("clone().defaultBudget",t);
            }
        }
        dm.groupMenuItems = this.groupMenuItems;
        try {
            if(namespaces!=null)
                dm.addChild(this.namespaces.clone(), NamespaceModel.QN);
        } catch (Throwable t) {
            logger.error("clone().namespaces",t);
        }
        for (SpecialKeyModel sp : specialKeys) {
            try {
                dm.addChild(sp.clone(), SpecialKeyModel.QN);
            } catch (Throwable t) {
                logger.error("clone().specialKeys",t);
            }
        }
        dm.controlConfigFile = this.controlConfigFile;
        try {
            if(parameters!=null)
                dm.addChild(this.parameters.clone(), ParametersModel.QN);
        } catch (Throwable t) {
            logger.error("clone().parameters",t);
        }
        dm.baseDirectory = baseDirectory;
        dm.pjNamespaceUri = pjNamespaceUri;
        dm.globalImportXsltFile = this.globalImportXsltFile;
        dm.globalUriTransformer = this.globalUriTransformer;
        dm.defaultEtatGlobal = this.defaultEtatGlobal;
        dm.libelleCollectivite = this.libelleCollectivite;
        dm.linkResolverClassName = this.linkResolverClassName;
        dm.natureIdentifiantCollectivite = this.natureIdentifiantCollectivite;
        dm.textToXmlTransformer = this.textToXmlTransformer;
        if(!resources.isEmpty()) {
            for(ResourceModel rm:resources.values()) {
                try { dm.addChild(rm, ResourceModel.QN); } catch(Throwable t) {}
            }
        }
        dm.importable = this.importable;
        dm.archiveVentilationAttrName = archiveVentilationAttrName;
        return dm;
    }

    public Vector<SpecialKeyModel> getSpecialKeys() {
        return specialKeys;
    }

    public boolean isBrowsable() {
        return browsable;
    }

    public EtatModel getDefaultBrowsableEtat() {
        if (defaultBrowsableEtat != null) {
            return getEtatById(defaultBrowsableEtat);
        }
        return etats.elementAt(0);
    }

    /**
     * @return
     * @deprecated Now, xslt to apply before import are defined in persistence-config
     */
    @Deprecated
    public String getReferentielImportTransfo() {
        return referentielImportTransfo;
    }

    public String getPersistenceConfigFile() {
        return persistenceConfigFile;
    }

    public void setPersistenceConfigFile(String persistenceConfigFile) {
        this.persistenceConfigFile = persistenceConfigFile;
    }

    public NamespaceModel getNamespaces() {
        return namespaces;
    }

    public boolean isGroupMenuItem() {
        return groupMenuItems;
    }

    @Override
    public EnvironmentDomain getChildAt(int domain, int pos) {
        return etats.elementAt(pos);
    }

    @Override
    public boolean hasEnvironment(int domain) {
        return false;
    }

    @Override
    public Enumeration<VariableModel> getVariables(int domain) {
        return null;
    }

    @Override
    public int getChildCount(int domain, PropertiesExpansion applicationProperties) {
        int count = 0;
        for (EtatModel em : etats) {
            if (em.getDisplayInMenuIf() != null) {
                if ("true".equals(applicationProperties.getProperty(em.getDisplayInMenuIf()))) {
                    count++;
                }
            } else {
                count++;
            }
        }
        return count;
    }

    @Override
    public Enumeration<EnvironmentDomain> children(int domain, PropertiesExpansion applicationProperties) {
        Vector<EnvironmentDomain> childs = new Vector<EnvironmentDomain>();
        for (EtatModel em : etats) {
            if (em.getDisplayInMenuIf() != null) {
                if ("true".equals(applicationProperties.getProperty(em.getDisplayInMenuIf()))) {
                    childs.add(em);
                }
            } else {
                childs.add(em);
            }
        }
        return childs.elements();
    }

    @Override
    public Object getValue(final String path) throws DataConfigurationException {
        String sTmp;
        if (path.startsWith("/")) {
            sTmp = path.substring(1);
        } else {
            sTmp = path;
        }
        if (sTmp.startsWith(EtatModel.TAG)) {
            int pos = sTmp.indexOf('/');
            String next = sTmp.substring(pos);
            String condition = sTmp.substring(EtatModel.TAG.length(), pos);
            if (condition.startsWith("[") && condition.endsWith("]")) {
                condition = condition.substring(1, condition.length() - 1).trim();
            }
            if (condition.startsWith("@id=")) {
                String etatId = condition.substring(5, condition.length() - 1);
                EtatModel em = getEtatById(etatId);
                return em.getValue(next);
            } else {
                throw new DataConfigurationException("can only get etat by id");
            }
        } else {
            throw new DataConfigurationException("from document, you can only access to etat(s)");
        }
    }

    @Override
    public void setValue(final String path, final Object value) throws DataConfigurationException {
        String sTmp;
        if (path.startsWith("/")) {
            sTmp = path.substring(1);
        } else {
            sTmp = path;
        }
        if (sTmp.startsWith(EtatModel.TAG)) {
            int pos = sTmp.indexOf('/');
            String next = sTmp.substring(pos);
            String condition = sTmp.substring(EtatModel.TAG.length(), pos);
            if (condition.startsWith("[") && condition.endsWith("]")) {
                condition = condition.substring(1, condition.length() - 1).trim();
            }
            if (condition.startsWith("@id=")) {
                String etatId = condition.substring(5, condition.length() - 1);
                EtatModel em = getEtatById(etatId);
                em.setValue(next, value);
            } else {
                throw new DataConfigurationException("can only get etat by id");
            }
        } else {
            throw new DataConfigurationException("from document, you can only access to etat(s)");
        }
    }

    public Vector<ElementModel> getElementsById(String elementId) {
        Vector<ElementModel> ret = new Vector<ElementModel>();
        for (EtatModel em : etats) {
            ElementModel el = em.getElementById(elementId);
            if (el != null) {
                ret.add(el);
            }
        }
        return ret;
    }

    public String getControlConfigFile() {
        return controlConfigFile;
    }

    public void setBudgetPath(
        RecherchePaireModel budgetPath) {
        this.budgetPath = budgetPath;
    }

    public void setCollectivitePath(
        CollectiviteInfosModel collectivitePath) {
        this.collectivitePath = collectivitePath;
    }
    public void setReferenceNomenclaturePath(
        RecherchePaireModel referenceNomenclaturePath) {
        this.referenceNomenclaturePath = referenceNomenclaturePath;
    }

    public ParametersModel getParameters() {
        return parameters;
    }

    @Override
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        this._NMParent = p;
    }

    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return this._NMParent;
    }

    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        if (EnteteModel.TAG.equals(tagName)) {
            return hEnteteModels.get(id);
        } else if (EtatModel.TAG.equals(tagName)) {
            return hEtats.get(id);
        } else if ("budget-path".equals(tagName)) {
            return budgetPath;
        } else if ("reference-nomenclature-path".equals(tagName)) {
            return referenceNomenclaturePath;
        } else if ("collectivite-path".equals(tagName)) {
            return collectivitePath;
//        } else if(DeleteFilterModel.TAG.equals(tagName)) {
//           deleteFilters.add((DeleteFilterModel)child);
        } else if ("default-budget".equals(tagName)) {
            return hDefaultBudgets.get(id);
        } else if (SpecialKeyModel.TAG.equals(tagName)) {
            return hSpecialKeys.get(id);
        } else if (NamespaceModel.TAG.equals(tagName)) {
            return namespaces;
        } else if (ParametersModel.TAG.equals(tagName)) {
            return parameters;
        } else if (TraitementExterneModel.TAG.equals(tagName)) {
            return hTraitementExternes.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void modifyAttr(String attrName, String value) {
    }

    @Override
    public void modifyAttrs(Attributes attrs) {
        try {
            getAttributes(new XmlAttributes(attrs));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour des attributs : " + e);
        }
    }

    @Override
    public String[] getChildIdAttrName(String childTagName) {
        if (EnteteModel.TAG.equals(childTagName)) {
            return new String[]{"id"};
        } else if (EtatModel.TAG.equals(childTagName)) {
            return new String[]{"id"};
        } else if ("default-budget".equals(childTagName)) {
            return new String[]{"value"};
        } else if (SpecialKeyModel.TAG.equals(childTagName)) {
            return new String[]{"id"};
        } else {
            return null;
        }
    }

    @Override
    public void resetCharData() {
    }

    public void setVEtats(Vector<EtatModel> v) {
        this.etats = v;
    }

    public void setHEtats(HashMap<String, EtatModel> h) {
        this.hEtats = h;
    }

    public HashMap<String, EtatModel> getHEtats() {
        return hEtats;
    }

    public void setBaseDirectory(String baseDir) {
        this.baseDirectory = baseDir;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setExtendedDocId(String docId) {
        extendsDocId = docId;
    }

    public String getExtendedDocId() {
        return extendsDocId;
    }

    public Element createSmallDOM(Document doc) {
        Element ret = doc.createElementNS(Constants.CONFIG_NS_URI, "conf:" + TAG);
        ret.setAttribute("id", getId());
        ret.setAttribute("libelle", getTitre());
        ret.setAttribute("browsable", isBrowsable()?"yes":"no");
        if(getArchiveVentilationAttrName()!=null) ret.setAttribute("archive-ventil-attr", getArchiveVentilationAttrName());
        if(isBrowsable()) {
            ret.setAttribute("browsable-etat", getDefaultBrowsableEtat().getId());
        }
        for (EtatModel em : getEtats()) {
            Element etat = em.createSmallDOM(doc);
            ret.appendChild(etat);
        }
        return ret;
    }

    public DocumentsModel getParent() {
        return (DocumentsModel) _NMParent;
    }

    public String getPjNamespaceUri() {
        return pjNamespaceUri;
    }

    public String getDisplayInMenuIf() {
        return displayInMenuIf;
    }

    public void setDisplayInMenuIf(String displayInMenuIf) {
        this.displayInMenuIf = displayInMenuIf;
    }

    @Override
    public int compareTo(Object o) {
        int ret = -1000;
        if (o instanceof DocumentModel) {
            DocumentModel dm = (DocumentModel) o;
            ret = ordrePresentation - dm.ordrePresentation;
            if (ret == 0) {
                ret = getTitre().compareTo(dm.getTitre());
            }
        }
        return ret;
    }

    @Override
    public String getConfigXPath() {
        if (configXPath == null) {
            if (getParentAsNoeudModifiable() != null) {
                configXPath = getParentAsNoeudModifiable().getConfigXPath();
            } else {
                configXPath = "";
            }
            configXPath += "/" + TAG + "[@id='" + getId() + "']";
        }
        return configXPath;
    }

    public String getGlobalImportXsltFile() {
        return globalImportXsltFile;
    }

    public String getGlobalUriTransformer() {
        return globalUriTransformer;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        QName name = new QName(uri, localName);
        if (EnteteModel.QN.equals(name)) {
            return hEnteteModels.get(atts.getValue("id"));
        } else if (EtatModel.QN.equals(name)) {
            return hEtats.get(atts.getValue("id"));
        } else if (DocumentsMapping.BUDGET_PATH.equals(name)) {
            return budgetPath;
        } else if (CollectiviteInfosModel.QN.equals(name)) {
            return collectivitePath;
        } else if (DocumentsMapping.REFERENCE_NOMENCLATURE_PATH.equals(name)) {
            return referenceNomenclaturePath;
        } else if (DocumentsMapping.DEFAULT_BUDGET.equals(name)) {
            return hDefaultBudgets.get(atts.getValue("value"));
        } else if (SpecialKeyModel.QN.equals(name)) {
            return hSpecialKeys.get(atts.getValue("id"));
        } else if (NamespaceModel.QN.equals(name)) {
            return namespaces;
        } else if (ParametersModel.QN.equals(name)) {
            return parameters;
        }
        return null;
    }

    @Override
    public QName getQName() {
        return qn;
    }

    public String getDefaultEtatGlobal() {
        return defaultEtatGlobal;
    }
    public String getLibelleCollectivite() { return libelleCollectivite; }

    public String getLinkResolverClassName() {
        return linkResolverClassName;
    }

    public void setLinkResolverClassName(String linkResolverClassName) {
        this.linkResolverClassName = linkResolverClassName;
    }

    public String getNatureIdentifiantCollectivite() {
        return natureIdentifiantCollectivite;
    }

    public void setNatureIdentifiantCollectivite(String natureIdentifiantCollectivite) {
        this.natureIdentifiantCollectivite = natureIdentifiantCollectivite;
    }

    public ResourceModel getResource(String resId) {
        return resources.get(resId);
    }

    public String getTextToXmlTransformer() {
        return textToXmlTransformer;
    }
    public boolean isImportable() { return importable; }

    public HelpModel getHelp() {
        return help;
    }

    public void setHelp(HelpModel help) {
        this.help = help;
    }

    public String getNavigateToDocumentSwingClassName() {
        return navigateToDocumentSwingClassName;
    }

    public void setNavigateToDocumentSwingClassName(String navigateToDocumentSwingClassName) {
        this.navigateToDocumentSwingClassName = navigateToDocumentSwingClassName;
    }

    public String getNavigateToDocumentWebClassName() {
        return navigateToDocumentWebClassName;
    }

    public void setNavigateToDocumentWebClassName(String navigateToDocumentWebClassName) {
        this.navigateToDocumentWebClassName = navigateToDocumentWebClassName;
    }

    public String getArchiveVentilationAttrName() {
        return archiveVentilationAttrName;
    }
    @Override
    public void prepareForUnload() {
        _NMParent = null;
        for(EtatModel etat:etats)
            etat.prepareForUnload();
        for(OptionModel om:hDefaultBudgets.values()) om.prepareForUnload();
        for(SpecialKeyModel skm: specialKeys) skm.prepareForUnload();
        for(EnteteModel em:hEnteteModels.values()) em.prepareForUnload();
        if(budgetPath!=null) budgetPath.prepareForUnload();
        if(referenceNomenclaturePath!=null) referenceNomenclaturePath.prepareForUnload();
        if(collectivitePath!=null) collectivitePath.prepareForUnload();
        if(namespaces!=null) namespaces.prepareForUnload();
        if(parameters!=null) parameters.prepareForUnload();
        for(TraitementExterneModel te:traitementExternes) te.prepareForUnload();
    }
    
}
