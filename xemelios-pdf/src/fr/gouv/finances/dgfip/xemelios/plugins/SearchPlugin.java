/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.gouv.finances.dgfip.xemelios.plugins;

import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ElementModel;
import fr.gouv.finances.dgfip.xemelios.common.config.EtatModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ListeResultatModel;
import fr.gouv.finances.dgfip.xemelios.common.config.PluginModel;
import fr.gouv.finances.dgfip.xemelios.data.DataResultSet;
import java.util.Hashtable;

/**
 *
 * @author chm
 */
public abstract class SearchPlugin {
    private Hashtable<String,String> params;
    private XemeliosUser user;
    private ElementModel elementModel;
    private Pair collectivite, budget, nomenclatureReference;
    private PluginModel model;
    private ListeResultatModel lrm;
    
    public SearchPlugin(XemeliosUser user) {
        super();
        this.user=user;
    }
    public void setElementModel(ElementModel em) {
        this.elementModel=em;
    }
    public ElementModel getElementModel() { return elementModel; }
    public void setCollectivite(Pair coll) { this.collectivite=coll; }
    public Pair getCollectivite() { return collectivite; }
    public void setBudget(Pair budg) { this.budget=budg; }
    public Pair getBudget() { return budget; }
    public void setNomenclatureReference(Pair nomencl) { this.nomenclatureReference=nomencl; }
    public Pair getNomenclatureReference() { return nomenclatureReference; }
    public EtatModel getEtatModel() { return elementModel.getParent(); }
    public DocumentModel getDocumentModel() { return getEtatModel().getParent(); }
    public void setPluginModel(PluginModel model) { this.model=model; }
    public PluginModel getPluginModel() { return model; }
    public XemeliosUser getUser() { return user; }
    public void setListeResultatModel(ListeResultatModel lrm) { this.lrm=lrm; }
    public ListeResultatModel getListeResultat() { return lrm; }

    public void setParameters(Hashtable<String,String> params) {
        this.params=params;
    }
    public Hashtable<String,String> getParameters() { return params; }
    
    public abstract DataResultSet getResultSet() throws Exception;
    /*
     * Build the SQL condition from MySqlDataLayer(getOperators) and value val
     */
    public static String convertOpString(String op, String val) {
        if ("starts-with".equals(op)) {
            return " like '" + val + "%' ";
        } else if ("contains".equals(op)) {
            return " like '%" + val + "%' ";
        } else if ("xem:ends-with".equals(op)) {
            return " like '%" + val + "' ";
        } else if ("xem:StringEquals".equals(op)) {
            return " like '" + val + "' ";
        } else if ("xem:StringDiffers".equals(op)) {
            return " <> '" + val + "' ";
        } else if ("xem:StringIsNull".equals(op)) {
            return " = '' ";
        } else if ("xem:StringIsNotNull".equals(op)) {
            return " <> '' ";
        } else {
            return "";
        }
    }
}
