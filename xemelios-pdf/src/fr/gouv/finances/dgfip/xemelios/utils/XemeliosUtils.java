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

package fr.gouv.finances.dgfip.xemelios.utils;

import java.net.InetAddress;
import java.sql.Connection;
import org.apache.log4j.Logger;

/**
 * Classe avec diverses méthodes utilitaires
 * @author cmarchand
 */
public class XemeliosUtils {
    private static final Logger logger = Logger.getLogger(XemeliosUtils.class);
    /**
     * Renvoie si la base de données est sur la même machine que le client
     * @param con
     * @return
     */
    public static boolean isLocalComputerServer(Connection con) {
        try {
            String url = con.getMetaData().getURL();
            String server = null;
            if(url.contains("mysql")) {
                // url is jdbc:mysql://localhost/xemelios or jdbc:mysql://localhost:3307/xemelios
                url = url.substring(13);
                int pipePos = url.indexOf(':');
                int slashPos = url.indexOf('/');
                if (pipePos < 0) {
                    pipePos = 10000;
                }
                if (slashPos < 0) {
                    slashPos = 10000;
                }
                int pos = Math.min(pipePos, slashPos);
                if (pos > url.length()) {
                    pos = url.length();
                }
                server = url.substring(0, pos);
                if(server.indexOf(":")>=0) {
                    // cas de la présence d'un port
                    server = server.substring(0, server.indexOf(":"));
                }
                if("localhost".equals(server)) {
                    logger.debug("localhost is local computer");
                    return true;
                }
            } else if(url.contains("oracle")) {
                int arobase = url.indexOf('@');
                int endServeur = url.indexOf(':', arobase);
                server =  url.substring(arobase+1, endServeur);
            }
            InetAddress serverAddress = InetAddress.getByName(server);
            InetAddress localAddress = InetAddress.getLocalHost();
            String localHostAddress = localAddress.getHostAddress();
            String serverHostAddress = serverAddress.getHostAddress();
            logger.debug("comparing "+localAddress.getHostAddress()+" to "+serverAddress.getHostAddress());
            return localAddress.getHostAddress().equals(serverAddress.getHostAddress());
        } catch (Exception uhEx) {
            return false;
        }
    }

}
