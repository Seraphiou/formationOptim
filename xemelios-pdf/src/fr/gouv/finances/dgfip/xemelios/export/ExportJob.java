/*
 * 
 * Copyright
 *  2009 axYus - www.axyus.com
 *  2009 Christophe Marchand <christophe.marchand@axyus.com>
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
 * 
 */

package fr.gouv.finances.dgfip.xemelios.export;

import fr.gouv.finances.dgfip.xemelios.data.DataLayerManager;
import fr.gouv.finances.dgfip.xemelios.data.DataResultSet;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Christophe Marchand <christophe.marchand@axyus.com>
 */
public class ExportJob {
    private static final Logger logger = Logger.getLogger(ExportJob.class);
    public static final transient int STATUS_SUBMIT=0;
    public static final transient int STATUS_PARSED=1;
    public static final transient int STATUS_RUNNING=2;
    public static final transient int STATUS_TERMINATED=3;
    public static final transient int STATUS_DOWNLOADED=4;
    public static final transient int STATUS_ERROR=5;
    public static final transient int STATUS_CLEANED=6;
    public static final transient String[] STATUS_TXT_FR={"Soumis", "Evalu&eacute;", "En cours", "Termin&eacute;", "T&eacute;l&eacute;charg&eacute;", "Erreur", "Prug&eacute;"};

    private Long idExport=null;
    private String owner=null;
    private String fileName=null;
    private String exportName=null;
    private String documentId=null;
    private String etatId=null;
    private String elementId=null;
    private Long exportConfigId=null;
    private int priority=-1;
    private int status=-1;
    private int size=-1;
    private Date submitDate=null;
    private Date runDate=null;
    private Date downloadDate=null;
    byte[] drs=null;
    private String errorMessage=null;
    private String generatedFileName=null;

    public ExportJob() {
        super();
    }

    public void setDataResultSet(DataResultSet drs) {
        try {
            this.drs = DataLayerManager.getImplementation().serializeDataResultSet(drs);
        } catch(Exception ex) {
            logger.error("setDataResultSet(DataResultSet)",ex);
        }
    }
    public DataResultSet getDataResultSet() {
        if(drs==null) return null;
        try {
            return DataLayerManager.getImplementation().deserializeDataResultSet(drs);
        } catch(Exception ex) {
            logger.error("getDataResultSet()",ex);
            return null;
        }
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Timestamp downloadDate) {
        this.downloadDate = downloadDate;
    }

    public byte[] getDrs() {
        return drs;
    }

    public void setDrs(byte[] drs) {
        this.drs = drs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getEtatId() {
        return etatId;
    }

    public void setEtatId(String etatId) {
        this.etatId = etatId;
    }

    public Long getExportConfigId() {
        return exportConfigId;
    }

    public void setExportConfigId(Long exportConfigId) {
        this.exportConfigId = exportConfigId;
    }

    public String getExportName() {
        return exportName;
    }

    public void setExportName(String exportName) {
        this.exportName = exportName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getIdExport() {
        return idExport;
    }

    public void setIdExport(Long idExport) {
        this.idExport = idExport;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getRunDate() {
        return runDate;
    }

    public void setRunDate(Timestamp runDate) {
        this.runDate = runDate;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Timestamp submitDate) {
        this.submitDate = submitDate;
    }

    public String getGeneratedFileName() {
        return generatedFileName;
    }

    public void setGeneratedFileName(String generatedFileName) {
        this.generatedFileName = generatedFileName;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

}
