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

package fr.gouv.finances.dgfip.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.log4j.Logger;

/**
 *
 * @author Christophe Marchand <christophe.marchand@axyus.com>
 */
public class ClassPathUtils {
    private static final Logger logger = Logger.getLogger(ClassPathUtils.class);

    public static String displayClassPathInfos() {
        StringBuilder sb = new StringBuilder();
        ClassLoader cl = ClassPathUtils.class.getClassLoader();
        if(cl instanceof URLClassLoader) {
            URLClassLoader ucl = (URLClassLoader)cl;
            URL[] urls = ucl.getURLs();
            for(URL url:urls) sb.append(displayUrlInfos(url));
        }
        return sb.toString();
    }
    private static String displayUrlInfos(URL url) {
        if(url.toExternalForm().endsWith(".jar")) {
            StringBuffer sb = new StringBuffer();
            sb.append(url).append("\n");
            try {
                String sUrl = url.toString().replaceAll(" ","%20");
                if(sUrl.startsWith("file:") && !sUrl.startsWith("file:/")) sUrl = "file:/".concat(sUrl.substring(5));
                url = new URL(sUrl);
                JarFile jf = new JarFile(new File(url.toURI()));
                Manifest mf = jf.getManifest();
                    Attributes att = mf.getMainAttributes();
                    for(Iterator<Map.Entry<Object,Object>> it2=att.entrySet().iterator();it2.hasNext();) {
                        Map.Entry<Object,Object> entry = it2.next();
                        sb.append("\t").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
                    }
            } catch (Exception ex) {
//                logger.error("displayUrlInfos(URL)",ex);
            }
            return sb.toString();
        }
        return "";
    }

    private static String makeUnderscored(int len) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<len;i++) sb.append("-");
        return sb.toString();
    }

    private ClassPathUtils() {
    }

}
