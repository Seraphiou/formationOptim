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
package fr.gouv.finances.dgfip.xemelios.importers;

import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.xemelios.common.ToolException;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;

/**
 * Definit le contrat de service pour les importeurs.
 * Ce sont les differentes implementations de cette interface
 * qui interagiront avec l'utilisateur via une interface swing,
 * une ligne de commande, un webservice, etc...
 */

public interface ImportServiceProvider {
	public final int MSG_TYPE_INFORMATION = 1;
	public final int MSG_TYPE_ERROR = 2;

    /**
     * Permet de demander à l'utilisateur de saisir les informations manquantes pour le budget
     * @param dm
     * @param collectivite
     * @param fileName
     * @param budg Les éventuelles informations connues pour le budget. Peut etre null
     * @return
     * @throws fr.gouv.finances.dgfip.xemelios.common.ToolException
     */
	public Pair getBudget(DocumentModel dm, Pair collectivite, String fileName, Pair budg) throws ToolException;
    /**
     * Permet de demander à l'utilisateur de saisir les informations manquantes de la collectivite
     * @param dm
     * @param fileName
     * @param coll Les éventuelles informations connues sur la collectivite. Peut etre null
     * @return
     * @throws fr.gouv.finances.dgfip.xemelios.common.ToolException
     */
    public Pair getCollectivite(DocumentModel dm, String fileName,Pair coll) throws ToolException;
	public boolean getOverwrite(String docName) throws Exception;
	public void pushCurrentProgress(int progress) throws NullPointerException;
	public void displayProgress(final int nbFiles);
	public void displayMessage(String msg, int msgType);
	public void displayException(Throwable t);
//	public void budgetInteractif(boolean value);
//	public void collectiviteInteractif(boolean value);
	public void startLongWait();
	public void endLongWait();
    public void setDisplayFeedback(boolean display);
    public boolean shouldDisplayFeedback();
    public void setCollectivite(Pair p);
    public void setBudget(Pair p);
    public void setEtatImporter(EtatImporteur ei);
    public void setAlwaysOverwrite(boolean b);
    /**
     * Permet de consulter si une collectivité a été spécifiée manuellement, sans riquer de poser la question à l'importeur
     * @return
     */
    public Pair getCollectivite();
    /**
     * Permet de consulter si un budget a été spécifié manuellement, sans risquer de poser la question à l'importeur
     * @return
     */
    public Pair getBudget();
}
