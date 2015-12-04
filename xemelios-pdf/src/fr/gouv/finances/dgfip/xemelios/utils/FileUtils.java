/*
 * Copyright 
 *   2005 axYus - www.axyus.com
 *   2005 C.Marchand - christophe.marchand@axyus.com
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

package fr.gouv.finances.dgfip.xemelios.utils;

import fr.gouv.finances.dgfip.xemelios.common.Constants;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

/**
 * @author chm
 */
public class FileUtils {
    private static final Logger logger = Logger.getLogger(FileUtils.class);
    private static File tempDir = null;
    static {
        tempDir = new File(new File(System.getProperty("java.io.tmpdir")),Constants.NOM_APP);
        if(!tempDir.exists()) tempDir.mkdirs();
    }
    /**
     * Reads the file and returns the content as a String
     * @param f the file to read
     * @return The content read
     * @throws IOException
     */
    public static String readTextFile(File f, String fileEncoding) throws IOException {
        StringBuffer sb = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(new FileInputStream(f),fileEncoding);
        char[] buf = new char[255];
        int read = isr.read(buf);
        while(read>0) {
        	sb.append(buf,0,read);
        	read = isr.read(buf);
        }
        isr.close();
        return sb.toString();
    }
    public static String getFileEncoding(File f) throws IOException {
        String ret = null;
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        byte[] bb = new byte[255];
        bis.read(bb);
        String buff = new String(bb);
        buff = buff.substring(buff.indexOf("<?"));
        buff = buff.substring(0,buff.indexOf("?>")+2);
        String header = buff;
        int encodingPos = header.indexOf("encoding=");
        if(encodingPos>=0) {
            String s = header.substring(encodingPos+9);
            if(s.startsWith("\"")) {
                ret=s.substring(1,s.indexOf('"',1));
            } else {
                s = s.substring(0,s.length()-2).trim();
                if(s.indexOf(' ')>0) ret = s.substring(0,s.indexOf(' '));
                else ret = s;
            }
        } else {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            ret = isr.getEncoding();
            isr.close();
        }
        bis.close();
        return ret;
    }

    public static void dropRecursiveDirectory(File dir) throws Exception {
        if(!dir.exists())
            throw new Exception(dir.getAbsolutePath()+" does not exist");
        if(!dir.isDirectory())
            throw new Exception(dir.getAbsolutePath()+" is not a directory");
        File[] files = dir.listFiles();
        for(File f: files) {
            if(f.isDirectory()) dropRecursiveDirectory(f);
            else f.delete();
        }
        dir.delete();
    }
    public static boolean isXmlFile(File input, String encoding) {
        boolean ret = false;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input), encoding));
            String line = br.readLine();
            int i = 0;
            while (line != null) {
                if (line.trim().length() > 0) {
                    i++;
                    if (i == 1 && line.trim().matches("[<][?]xml version[=].* encoding[=].*[?][>].*")) {
                        return true;
                    } else {
                        return false;
                    }
                }
                line = br.readLine();
            }
        } catch (Exception e) {
            logger.debug("Problème dans isXmlFile() :\n", e);
        }
        return ret;
    }

    public static File getTempDir() {
        return tempDir;
    }
}
