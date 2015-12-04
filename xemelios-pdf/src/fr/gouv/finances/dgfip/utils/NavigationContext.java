/*
 * Copyright 
 *   2007 axYus - www.axyus.com
 *   2007 C.Marchand - christophe.marchand@axyus.com
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
package fr.gouv.finances.dgfip.utils;

import java.util.Properties;


/**
 * Stores context of navigation
 * @author chm
 */
public class NavigationContext implements Cloneable {

    private String docId,  etatId,  elementId,  collectivite,  budget,  sp1,  sp2,  sp3,  path;
    private Dest lastDestination;


    /**
     * 
     */
    public NavigationContext() {
        super();
    }

    public NavigationContext(String docId, String etatId, String elementId, String collectivite, String budget, String sp1, String sp2, String sp3, String path) {
        this();
        this.docId = docId;
        this.etatId = etatId;
        this.elementId = elementId;
        this.collectivite = collectivite;
        this.budget = budget;
        this.sp1 = sp1;
        this.sp2 = sp2;
        this.sp3 = sp3;
        this.path = path;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        if (budget != null) {
            this.budget = budget;
        }
    }

    public String getCollectivite() {
        return collectivite;
    }

    public void setCollectivite(String collectivite) {
        if (collectivite != null) {
            this.collectivite = collectivite;
        }
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        if (docId != null) {
            this.docId = docId;
        }
    }

    public String getEtatId() {
        return etatId;
    }

    public void setEtatId(String etatId) {
        if (etatId != null) {
            this.etatId = etatId;
            this.elementId = null;
            this.path = null;
        }
    }

    public String getSp1() {
        return sp1;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSp1(String sp1) {
//        if (sp1 != null) {
            this.sp1 = sp1;
//        }
    }

    public String getSp2() {
        return sp2;
    }

    public void setSp2(String sp2) {
//        if (sp2 != null) {
            this.sp2 = sp2;
//        }
    }

    public String getSp3() {
        return sp3;
    }

    public void setSp3(String sp3) {
        this.sp3 = sp3;
    }

    @Override
    public NavigationContext clone() {
        NavigationContext nc = new NavigationContext();
        nc.budget = this.budget;
        nc.collectivite = this.collectivite;
        nc.docId = this.docId;
        nc.etatId = this.etatId;
        nc.elementId = elementId;
        nc.sp1 = this.sp1;
        nc.sp2 = sp2;
        nc.sp3 = sp3;
        nc.path = this.path;
        return nc;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\tdocId=").append(docId).append("\n");
        sb.append("\tetatId=").append(etatId).append("\n");
        sb.append("\telementId=").append(elementId).append("\n");
        sb.append("\tcollectivite=").append(collectivite).append("\n");
        sb.append("\tbudget=").append(budget).append("\n");
        sb.append("\tsp1=").append(sp1).append("\n");
        sb.append("\tsp2=").append(sp2).append("\n");
        sb.append("\tsp3=").append(sp3).append("\n");
        sb.append("\tpath=").append(path).append("\n");
        return sb.toString();
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        if (elementId != null) {
            this.elementId = elementId;
        }
    }
    public Dest getLastDestination() {
        return lastDestination;
    }

    public void setLastDestination(Dest lastDestination) {
        this.lastDestination = lastDestination;
    }

    public void configXPath(Properties additionalParameters) {
        String _path = getPath();
        if(_path==null) return;
        int pos = _path.indexOf('%');
        while(pos>=0) {
            int closing = _path.indexOf('%', pos+1);
            if(closing<0) throw new RuntimeException("a '%' is not closed! : "+_path);
            String paramName = _path.substring(pos+1, closing);
            if(additionalParameters.containsKey(paramName)) {
                String begin = _path.substring(0,pos);
                String end = _path.substring(closing+1);
                String replacedValue = additionalParameters.getProperty(paramName);
                _path = begin.concat(replacedValue).concat(end);
                pos = _path.indexOf('%', pos);
            } else {
                pos = _path.indexOf('%', closing);
            }
        }
        setPath(_path);
    }
}
