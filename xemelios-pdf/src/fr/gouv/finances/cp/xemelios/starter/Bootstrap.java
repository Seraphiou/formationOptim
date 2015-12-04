/*
 * Copyright 
 *   2005 axYus - www.axyus.com
 *   2005 L.Meckert - lmeckert@club-internet.fr
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

package fr.gouv.finances.cp.xemelios.starter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import fr.gouv.finances.cp.xemelios.updater.starter.Updater;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import javax.swing.JOptionPane;

/**
 * Cette class est le lanceur de Xemelios
 * @author cmarchand
 */
public class Bootstrap {
    private static final Class[] parameters = new Class[] { URL.class };

    public static void addFile(String s) throws IOException {
        File f = new File(s);
        addFile(f);
    }

    public static void addFile(File f) throws IOException {
        addURL(f.toURI().toURL());
    }

    // bitonio trouvé sur le net
    public static void addURL(URL u) throws IOException {
        URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true); // voyou
            method.invoke(sysloader, new Object[] { u });
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }

    }

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
        }
        catch (SecurityException se) {
            System.err.println("Problème d'accès au fichier " + propsFileName);
        }
        if (propsFile != null) {
            try {
                FileInputStream in = new FileInputStream(propsFile);
                properties.load(in);
            } catch (IOException ioe) {
                System.err.println("Cannot load properties " + ioe);
                return null;
            }
        }
        // on ajoute toutes les propriétés système
        for(String propName: System.getProperties().stringPropertyNames())
            properties.setProperty(propName, System.getProperty(propName));
        return properties;
    }

    public static void main(String[] args) throws Exception {

        PropertiesExpansion applicationConfiguration = restoreProperties();
        // TODO
        // ouverture d'une socket pour contrôler l'unicité du Xemelios sur le poste
        try {
            int port = 8973;
            String sTmp = applicationConfiguration.getProperty("xemelios.isolation.port");
            if(sTmp!=null) {
                try {
                    port = Integer.parseInt(sTmp);
                } catch(Exception ignorable) {}
            }
            ServerSocket ss = new ServerSocket(port);
            // maintenant, il faut s'assurer que l'on fermera bien cela
            ServerSocketCloser ssc = new ServerSocketCloser(ss);
            Runtime.getRuntime().addShutdownHook(ssc);
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(null, "Un autre Xemelios est déjà en cours d'execution");
            System.exit(1);
        }
        new Updater(applicationConfiguration);

        String classdirs = applicationConfiguration.getProperty("xemelios.jars.dir");
        String startClassName = applicationConfiguration.getProperty("xemelios.startclass");
        String[] classdirsArray = classdirs.split(";");

        for (int dir = 0; dir < classdirsArray.length; ++dir) {
            try {
                File jarsdir = new File(classdirsArray[dir]);
                addURL(new URL("file:" + jarsdir.getAbsolutePath()));
                File[] listJars = jarsdir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        String s = pathname.getPath();
                        return s.endsWith(".jar");
                    }
                });
                for (int i = 0; i < listJars.length; ++i) {
                    addURL(new URL("file:" + listJars[i].getAbsolutePath()));
                }
            }
            catch (NullPointerException n) {
                System.err.println("Impossible de trouver le repertoire " + classdirsArray[dir]);
            }
        }

        Class startClass = Class.forName(startClassName);
        Constructor cc = startClass.getConstructor(PropertiesExpansion.class,String[].class);
        cc.newInstance(applicationConfiguration, args);
    }

    private static class ServerSocketCloser extends Thread {
        private ServerSocket ss;
        ServerSocketCloser(ServerSocket socketToClose) {
            super();
            this.ss = socketToClose;
        }

        @Override
        public void run() {
            try {
                ss.close();
            } catch(Exception ex) {
                // on ignore
            }
        }

    }
}
