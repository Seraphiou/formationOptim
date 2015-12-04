/*
 * Copyright
 *   2009 axYus - www.axyus.com
 *   2009 C.Marchand - christophe.marchand@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.common.config.customLinks;

import fr.gouv.finances.dgfip.xemelios.common.config.DocumentsModel;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to carry custom link parameters
 * @author chm
 */
public class CustomLinkParameters {
    private String srcDocId = null;
    private String srcEtatId  = null;
    private String srcElementId = null;
    private String srcCollectivite = null;
    private String srcBudget = null;
    private Map<String,String> parameters = null;

    public CustomLinkParameters() {
        parameters = new HashMap<String, String>();
    }

    public String getSrcBudget() {
        return srcBudget;
    }

    public void setSrcBudget(String srcBudget) {
        this.srcBudget = srcBudget;
    }

    public String getSrcCollectivite() {
        return srcCollectivite;
    }

    public void setSrcCollectivite(String srcCollectivite) {
        this.srcCollectivite = srcCollectivite;
    }

    public String getSrcDocId() {
        return srcDocId;
    }

    public void setSrcDocId(String srcDocId) {
        this.srcDocId = srcDocId;
    }

    public String getSrcElementId() {
        return srcElementId;
    }

    public void setSrcElementId(String srcElementId) {
        this.srcElementId = srcElementId;
    }

    public String getSrcEtatId() {
        return srcEtatId;
    }

    public void setSrcEtatId(String srcEtatId) {
        this.srcEtatId = srcEtatId;
    }
    public void addParameter(String paramName, String paramValue) {
        parameters.put(paramName, paramValue);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
    public String getParameter(String paramName) {
        if(paramName==null) throw new RuntimeException(this.getClass().getName()+".getParameter(null) is not allowed");
        return parameters.get(paramName);
    }
    public String getCustomLinkResolverName(DocumentsModel docs) {
        return docs.getDocumentById(srcDocId).getLinkResolverClassName();
    }
}
