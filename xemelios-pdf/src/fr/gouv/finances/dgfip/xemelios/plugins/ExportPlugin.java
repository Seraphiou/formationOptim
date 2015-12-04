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
import fr.gouv.finances.dgfip.xemelios.common.config.PluginModel;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 *
 * @author chm
 */
public abstract class ExportPlugin {
    private Hashtable<String,String> params;
    private XemeliosUser user;
    private ElementModel elementModel;
    private Pair collectivite, budget, nomenclatureReference;
    private PluginModel model;
    private OutputStream os;
    private ProgressListener progressListener;

    public ExportPlugin(XemeliosUser user) {
        super();
        this.user=user;
    }


    /**
     * This method is called by doInBackground. It must return the buffer to export
     * @return The buffer to write in output file.
     * @throws Exception
     */
    public abstract Long doExport() throws Exception;
    /**
     * This method is called by doInBackground to let you read parameters
     * @throws Exception
     */
    public abstract void computeParameters() throws Exception;

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

    public void setParameters(Hashtable<String,String> params) {
        this.params=params;
    }
    public Hashtable<String,String> getParameters() { return params; }
    public void setOutputStream(OutputStream os) { this.os=os; }
    public OutputStream getOutputStream() { return os; }
    public ProgressListener getProgressListener() { return progressListener; }
    public void setProgressListener(ProgressListener progressListener) { this.progressListener = progressListener; }
    public Pair getRefenrenceNomenclature() {
            return nomenclatureReference;
    }

    public void setRefenrenceNomenclature(Pair refenrenceNomenclature) {
            this.nomenclatureReference = refenrenceNomenclature;
    }

    public abstract String getDefaultFileExtension();

    public interface ProgressListener {
        public void setProgressInfos(int maxProgress, String progressMessage);
        public void pushProgress();
        public void setProgressVisible(boolean show);
    }
}
