/*
 * Copyright
 *   2007 axYus - www.axyus.com
 *   2007 J-P.Tessier - jean-philippe.tessier@axyus.com
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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import java.lang.reflect.Constructor;

/**
 * This class load config file and launch the real batch importer.
 * 
 * 
 * @since 3.1
 * 
 * @author jptessier,chm
 */
public class BatchImporter {

    private static final Class[] parameters = new Class[]{URL.class};

    static PropertiesExpansion restoreProperties() {
        PropertiesExpansion properties = new PropertiesExpansion();
        String propsFileName = System.getProperty("xemelios.properties");
        if (propsFileName == null) {
            propsFileName = "xemelios.properties";
        } // fichier de ce nom dans rep courant
        File propsFile = new File(propsFileName);
        try {
            if (propsFile.isFile() && propsFile.canRead()) {
            } else {
                System.err.println("Impossible d'accéder à " + propsFileName);
            }
        } catch (SecurityException se) {
            System.err.println("Problème d'accès au fichier " + propsFileName);
        }
        if (propsFile != null) {
            try {
                FileInputStream in = new FileInputStream(propsFile);
                properties.load(in);
            } catch (IOException ioe) {
                System.err.println("Cannot load properties " + ioe);
//                return null;
            }
        }
        for(String key:System.getProperties().stringPropertyNames()) {
            properties.setProperty(key, System.getProperty(key));
        }
        return properties;
    }

    public static void addURL(URL u) throws IOException {

        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true); // voyou
            method.invoke(sysloader, new Object[]{u});
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }// end try catch

    }// end method

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        PropertiesExpansion applicationConfiguration = restoreProperties();
        String classdirs = applicationConfiguration.getProperty("xemelios.jars.dir");
        String startClassName = "fr.gouv.finances.cp.xemelios.importers.batch.BatchRealImporter";
        if(classdirs!=null) {
            String[] classdirsArray = classdirs.split(";");

            for (int dir = 0; dir < classdirsArray.length; ++dir) {
                try {
                    File jarsdir = new File(classdirsArray[dir]);
                    addURL(new URL("file:" + jarsdir.getAbsolutePath()));
                    File[] listJars = jarsdir.listFiles(new FileFilter() {
                        public boolean accept(File pathname) {
                            String s = pathname.getPath();
                            return s.endsWith(".jar");
                        }
                    });
                    for (int i = 0; i < listJars.length; ++i) {
                        addURL(new URL("file:" + listJars[i].getAbsolutePath()));
                    }
                } catch (NullPointerException n) {
                    System.err.println("Impossible de trouver le repertoire " + classdirsArray[dir]);
                }
            }
        }

        Class startClass = Class.forName(startClassName);
        Constructor cc = startClass.getConstructor(PropertiesExpansion.class, String[].class);
        cc.newInstance(applicationConfiguration, args);
    }
}
