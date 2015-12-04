/*
 * Copyright
 *  2011 axYus - http://www.axyus.com
 *  2011 C.Marchand - christophe.marchand@axyus.com
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

package fr.gouv.finances.dgfip.xemelios.common.generators;

import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ElementModel;
import fr.gouv.finances.dgfip.xemelios.common.config.EtatModel;
import org.apache.log4j.Logger;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;

/**
 * La classe de base pour les générateurs PDF
 * @author cmarchand
 */
public abstract class AbstractPdfGenerator implements Generator {
    private GenerationRequest generationRequest;

    public AbstractPdfGenerator(XemeliosUser user, DocumentModel documentModel, ElementModel elementModel, EtatModel etatModel, String collectivite, String budget, String exercice) {
        super();
        generationRequest = new GenerationRequest(user, documentModel, elementModel, etatModel, collectivite, budget, exercice);
    }

    public GenerationRequest getGenerationRequest() {
        return generationRequest;
    }

    public class PdfGeneratorAgentCallback extends ITextUserAgent {
        public PdfGeneratorAgentCallback(ITextOutputDevice device) {
            super(device);
        }

        @Override
        public String resolveURI(String uri) {
            return super.resolveURI(uri);
        }
    }

    public static class GenerationRequest {
        private XemeliosUser user;
        private DocumentModel documentModel;
        private ElementModel elementModel;
        private EtatModel etatModel;
        private String collectivite;
        private String budget;
        private String exercice;
        private Pair pCollectivite, pBudget;

        public GenerationRequest(XemeliosUser user, DocumentModel documentModel, ElementModel elementModel, EtatModel etatModel, String collectivite, String budget, String exercice) {
            this.user = user;
            this.documentModel = documentModel;
            this.etatModel = etatModel;
            this.elementModel= elementModel;
            this.collectivite = collectivite;
            this.budget = budget;
            this.exercice = exercice;
            this.pCollectivite = new Pair(collectivite, collectivite);
            this.pBudget = new Pair(budget, budget);
        }

        public String getBudget() {
            return budget;
        }

        public String getCollectivite() {
            return collectivite;
        }

        public DocumentModel getDocumentModel() {
            return documentModel;
        }

        public ElementModel getElementModel() {
            return elementModel;
        }

        public EtatModel getEtatModel() {
            return etatModel;
        }

        public String getExercice() {
            return exercice;
        }

        public XemeliosUser getUser() {
            return user;
        }

        public Pair getpBudget() {
            return pBudget;
        }

        public Pair getpCollectivite() {
            return pCollectivite;
        }

        @Override
        public String toString() {
            String pad = "&nbsp;&nbsp;&nbsp;&nbsp;";
            StringBuilder sb = new StringBuilder();
            sb.append(pad).append("documentId=").append(documentModel.getId()).append("<br/>\n");
            sb.append(pad).append("etatId=").append(etatModel.getId()).append("<br/>\n");
            sb.append(pad).append("elementId=").append(elementModel.getId()).append("<br/>\n");
            sb.append(pad).append("collectivite=").append(collectivite).append("<br/>\n");
            sb.append(pad).append("budget=").append(budget).append("<br/>\n");
            sb.append(pad).append("exercice=").append(exercice).append("<br/>\n");
            return sb.toString();
        }
        
    }
}
