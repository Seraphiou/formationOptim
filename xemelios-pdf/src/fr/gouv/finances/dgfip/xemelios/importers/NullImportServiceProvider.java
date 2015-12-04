/*
 * Copyright
 *  2012 axYus - http://www.axyus.com
 *  2012 C.Marchand - christophe.marchand@axyus.com
 *
 * This file is part of XEMELIOS_NB.
 *
 * XEMELIOS_NB is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * XEMELIOS_NB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS_NB; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package fr.gouv.finances.dgfip.xemelios.importers;

import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.xemelios.common.ToolException;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;

/**
 *
 * @author cmarchand
 */
public class NullImportServiceProvider implements ImportServiceProvider {
    private boolean displayFeedback = false;
    private Pair budget, collectivite;
    private EtatImporteur ei;

    @Override
    public Pair getBudget(DocumentModel dm, Pair collectivite, String fileName, Pair budg) throws ToolException {
        return budget;
    }

    @Override
    public Pair getCollectivite(DocumentModel dm, String fileName, Pair coll) throws ToolException {
        return collectivite;
    }

    @Override
    public boolean getOverwrite(String docName) throws Exception {
        return true;
    }

    @Override
    public void pushCurrentProgress(int progress) throws NullPointerException {}

    @Override
    public void displayProgress(int nbFiles) {}

    @Override
    public void displayMessage(String msg, int msgType) {}

    @Override
    public void displayException(Throwable t) {}

    @Override
    public void startLongWait() {}

    @Override
    public void endLongWait() {}

    @Override
    public void setDisplayFeedback(boolean display) {
        this.displayFeedback = display;
    }

    @Override
    public boolean shouldDisplayFeedback() {
        return displayFeedback;
    }

    @Override
    public void setCollectivite(Pair p) {
        this.collectivite = p;
    }

    @Override
    public void setBudget(Pair p) {
        this.budget = p;
    }

    @Override
    public void setEtatImporter(EtatImporteur ei) {
        this.ei = ei;
    }

    @Override
    public void setAlwaysOverwrite(boolean b) {}

    @Override
    public Pair getCollectivite() {
        return collectivite;
    }

    @Override
    public Pair getBudget() {
        return budget;
    }

}
