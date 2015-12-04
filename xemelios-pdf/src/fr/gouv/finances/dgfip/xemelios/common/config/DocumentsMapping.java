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

import java.util.HashMap;
import javax.xml.namespace.QName;

/**
 *
 * @author chm
 */
public class DocumentsMapping extends HashMap<QName,Class> {
    
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3617293411154145847L;

    public DocumentsMapping() {
        put(ChampModel.QN,ChampModel.class);
        put(CritereModel.QN,CritereModel.class);
        put(DocumentModel.QN,DocumentModel.class);
        put(DocumentsModel.QN,DocumentsModel.class);
        put(ElementModel.QN,ElementModel.class);
        put(EtatModel.QN,EtatModel.class);
        put(InputModel.QN, InputModel.class);
        put(ListeResultatModel.QN,ListeResultatModel.class);
        put(OptionModel.QN,OptionModel.class);
        put(SelectModel.QN,SelectModel.class);
        put(EnteteModel.QN,EnteteModel.class);
        put(PropertyModel.QN,PropertyModel.class);
        put(PluginModel.QN,PluginModel.class);
        put(SpecialKeyModel.QN,SpecialKeyModel.class);
        put(SpecialKeyEntryModel.QN,SpecialKeyEntryModel.class);
        put(BlankModel.QN,BlankModel.class);
        put(NamespaceModel.QN,NamespaceModel.class);
        put(NSModel.QN,NSModel.class);
        put(DeleteFilterModel.QN,DeleteFilterModel.class);
        put(RECHERCHE_PAIRE,RecherchePaireModel.class);
        put(CollectiviteInfosModel.QN,CollectiviteInfosModel.class);
        put(BUDGET_PATH,RecherchePaireModel.class);
        put(VALEUR,XPathModel.class);
        put(PATH,XPathModel.class);
        put(CODE_PATH,XPathModel.class);
        put(LIBELLE_PATH,XPathModel.class);
        put(LIBELLES,IgnoredModel.class);
        put(TAG,IgnoredModel.class);
        put(ENUM,IgnoredModel.class);
        put(CHAMP_OPTIONNEL,ChampModel.class);
        put(DEFAULT_BUDGET,OptionModel.class);
        put(DESC,OptionModel.class);
        put(HEADER,OptionModel.class);
        put(LISTE_EXPORT,ListeResultatModel.class);
        put(KEY1,TextModel.class);
        put(KEY2,TextModel.class);
        put(ReferenceModel.QN,ReferenceModel.class);
        put(VariableModel.QN,VariableModel.class);
        put(EnvironmentModel.QN,EnvironmentModel.class);
        put(LIBELLE,TextModel.class);
        put(HiddenModel.QN,HiddenModel.class);
        put(ParametersModel.QN,ParametersModel.class);
        put(ParameterModel.QN,ParameterModel.class);
        put(PARENT,SimpleElement.class);
        put(ENFANTS,EnfantModel.class);
        put(ENFANT,SimpleElement.class);
        put(WidgetModel.QN,WidgetModel.class);
        put(CritereRefModel.QN,CritereRefModel.class);
        put(TraitementExterneModel.QN,TraitementExterneModel.class);
        put(REFERENCE_NOMENCLATURE_PATH,RecherchePaireModel.class);
        put(ParentModel.QN,ParentModel.class);
        put(ResourceModel.QN,ResourceModel.class);
        put(ResourceRefModel.QNAME,ResourceRefModel.class);
        put(HelpModel.QN, HelpModel.class);
        put(SearchHelperModel.QNAME, SearchHelperModel.class);
        put(COLLECTIVITE, SearchHelperParameterModel.class);
        put(BUDGET, SearchHelperParameterModel.class);
        put(CRITERE_MAPPING, SourceTargetModel.class);
        put(EXCLUDE_CRITERE, SourceTargetModel.class);
        put(RequiredCritereModel.QNAME, RequiredCritereModel.class);
        put(ValueModel.QNAME, ValueModel.class);
    }
    
    public static final QName VALEUR = new QName("valeur");
    public static final QName PATH = new QName("path");
    public static final QName CODE_PATH = new QName("code-path");
    public static final QName LIBELLE_PATH = new QName("libelle-path");
    public static final QName RECHERCHE_PAIRE = new QName("recherche-paire");
    public static final QName KEY1 = new QName("key1");
    public static final QName KEY2 = new QName("key2");
//    public static final QName COLLECTIVITE_PATH = new QName("collectivite-path");
    public static final QName BUDGET_PATH = new QName("budget-path");
    public static final QName DEFAULT_BUDGET = new QName("default-budget");
    public static final QName PARENT = new QName("parent");
    public static final QName ENFANT = new QName("enfant");
    public static final QName ENFANTS = new QName("enfants");
    public static final QName HEADER = new QName("header");
    public static final QName LISTE_EXPORT = new QName("liste-export");
    public static final QName LIBELLE = new QName("libelle");
    public static final QName LIBELLES = new QName("libelles");
    public static final QName TAG = new QName("tag");
    public static final QName ENUM = new QName("enum");
    public static final QName CHAMP_OPTIONNEL = new QName("champ-optionnel");
    public static final QName DESC = new QName("desc");
    public static final QName REFERENCE_NOMENCLATURE_PATH = new QName("reference-nomenclature-path");
    public static final QName COLLECTIVITE = new QName("collectivite");
    public static final QName BUDGET = new QName("budget");
    public static final QName CRITERE_MAPPING = new QName("critere-mapping");
    public static final QName EXCLUDE_CRITERE = new QName("exclude-critere");
}
