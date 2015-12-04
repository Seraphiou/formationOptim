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
package fr.gouv.finances.dgfip.xemelios.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Wraps a PJ from a zip file
 * @author chm
 *
 */
public class PJRef {

    private String collectivite;
    private String pjName;
    private String tmpFileName;
    private ZipEntry ze;
    private String fileName;
    private long uncompressedSize;
    private boolean valid = true;
    private byte[] data;
    private String path;

    public boolean isValid() {
        return valid;
    }

    public PJRef(ZipEntry ze) {
        super();
        this.ze = ze;
        initialize();
    }

    public PJRef() {
        super();
    }

    private void initialize() {
        String entryName = ze.getName();
        String sTmp = entryName.substring("PJ/".length());
        int pos = sTmp.lastIndexOf('/');
        if (pos < 0) {
            valid = false;
            return;
        }
        collectivite = sTmp.substring(0, pos);
        fileName = sTmp.substring(pos + 1);
        pos = fileName.lastIndexOf('.');
        if (pos >= 0) {
            pjName = fileName.substring(0, pos);
        } else {
            pjName = fileName;
        }
        uncompressedSize = ze.getSize();
    }

    public File writeTmpFile(File tmpDir, ZipFile zf) throws IOException {
        File tmpFile = File.createTempFile("PJ-", "", tmpDir);
        FileOutputStream fos = new FileOutputStream(tmpFile);
        InputStream is = zf.getInputStream(ze);
        byte[] buffer = new byte[1024];
        int read = 0;
        do {
            read = is.read(buffer);
            if (read >= 0) {
                fos.write(buffer, 0, read);
            }
        } while (read > 0);
        fos.flush();
        fos.close();
        tmpFileName = tmpFile.getPath();
        is.close();
        return tmpFile;
    }

    public InputStream getDataInputStream() throws IOException {
        if (tmpFileName != null) {
            return new FileInputStream(tmpFileName);
        } else {
            return new ByteArrayInputStream(data);
        }
    }

    public String getCollectivite() {
        return collectivite;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPjName() {
        return pjName;
    }

    public long getUncompressedSize() {
        return uncompressedSize;
    }

    public static boolean isPJ(ZipEntry ze) {
        String name = ze.getName();
        return !ze.isDirectory() && name.startsWith("PJ/");
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
        uncompressedSize = data.length;
    }

    public String getTmpFileName() {
        return tmpFileName;
    }

    public void setTmpFileName(String tmpFileName) {
        this.tmpFileName = tmpFileName;
    }

    public void setCollectivite(String collectivite) {
        this.collectivite = collectivite;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPjName(String pjName) {
        this.pjName = pjName;
    }

    public void setUncompressedSize(long uncompressedSize) {
        this.uncompressedSize = uncompressedSize;
    }

    /**
     * Renvoie le chemin d'accès à la PJ dans l'archive
     * @return
     */
    public String getPath() {
        if(ze!=null)
            return this.ze.getName();
        else return path;
    }
    /**
     * Permet de définir le chemin relatif de la PJ dans l'archive
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
