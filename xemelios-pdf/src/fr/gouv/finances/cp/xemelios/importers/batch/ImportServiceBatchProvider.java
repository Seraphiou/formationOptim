/*
 * Copyright
 *   2006 axYus - www.axyus.com
 *   2006 JP.Tessier- jean-philippe.tessier@axyus.com
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
package fr.gouv.finances.cp.xemelios.importers.batch;

import org.apache.log4j.Logger;

import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.xemelios.common.ToolException;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.importers.EtatImporteur;
import fr.gouv.finances.dgfip.xemelios.importers.ImportServiceProvider;

/**
 * This class implements a service for a batch import.
 * 
 * @since 3.1
 * 
 * @author jptessier
 * @author paul.renier
 */
public class ImportServiceBatchProvider implements ImportServiceProvider {

	private static Logger logger = Logger.getLogger(ImportServiceBatchProvider.class);
	public Pair collectivite, budget;
	public DocumentModel dm;

	public ImportServiceBatchProvider(DocumentModel dm, Pair collectivite, Pair budget) {
		super();
		this.dm = dm;
		this.budget = budget;
		this.collectivite = collectivite;
	}

	@Override
	public Pair getBudget(DocumentModel dm, Pair pCollectivite, String fileName, Pair pBudget) throws ToolException {
		logger.warn("No 'budget' for '" + fileName + "' (" + dm + "/" + pCollectivite + ")");
		throw new ToolException("Batch can't ask for unknow 'budget' : " + fileName + " (" + pCollectivite + ")", ToolException.ERROR_NO_DATA);
	}

	@Override
	public Pair getCollectivite(DocumentModel dm, String fileName, Pair pCollectivite) throws ToolException {
		logger.warn("No 'collectivite' for '" + fileName + "' (" + dm + ")");
		throw new ToolException("Batch can't ask for unknow 'collectivite' : " + fileName, ToolException.ERROR_NO_DATA);
	}

	@Override
	public boolean getOverwrite(String docName) throws Exception {
		return true;
	}

	@Override
	public void displayMessage(String msg, int msgType) {
		logger.info(msg);
	}

	@Override
	public void displayException(Throwable t) {
		t.printStackTrace();
	}

	@Override
	public void displayProgress(int nbFiles) {
	}

	@Override
	public void endLongWait() {
	}

	@Override
	public void pushCurrentProgress(int progress) throws NullPointerException {
	}

	@Override
	public void startLongWait() {
	}

	@Override
	public void setDisplayFeedback(boolean display) {
		// ignored
	}

	@Override
	public boolean shouldDisplayFeedback() {
		return true;
	}

	@Override
	public void setBudget(Pair p) {
		logger.debug("setBudget(" + p.key + "," + p.libelle + ")");
		budget = p;
	}

	@Override
	public void setCollectivite(Pair p) {
		logger.debug("setCollectivite(" + p.key + "," + p.libelle + ")");
		collectivite = p;
	}

	@Override
	public void setEtatImporter(EtatImporteur ei) {
		// nothing to do
	}

	@Override
	public void setAlwaysOverwrite(boolean b) {
		// nothing to do
	}

	@Override
	public Pair getCollectivite() {
		logger.debug("getCollectivite() -> " + collectivite.key + " / " + collectivite.libelle);
		return collectivite;
	}

	@Override
	public Pair getBudget() {
		logger.debug("getBudget() -> " + budget.key + " / " + budget.libelle);
		return budget;
	}
}
