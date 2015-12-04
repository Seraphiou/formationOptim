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

package fr.gouv.finances.dgfip.utils.xml;


// Ajout LM  pour emploi dans xsl analyse des flux
import fr.gouv.finances.dgfip.utils.xml.certs.Certificate509;
import fr.gouv.finances.dgfip.xemelios.data.DataImpl;
import fr.gouv.finances.dgfip.xemelios.data.DataLayerManager;
import fr.gouv.finances.dgfip.xemelios.data.impl.pool.PoolManager;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import jonelo.jacksum.algorithm.AbstractChecksum;
import jonelo.jacksum.algorithm.MD;
import org.apache.log4j.Logger;
import sun.security.x509.CertificateX509Key;

/**
 *
 * @author chm
 */
public class HelperFunction {
    private static final transient Logger logger = Logger.getLogger(HelperFunction.class);
    private static long count=0;
    public HelperFunction() {
        super();
    }

    /**
     * Appelé depuis la XSL d'import mysql de la carte achat
     * @return
     */
    public static String generateUniqueID() {
        StringBuilder sb = new StringBuilder();
        try {
            InetAddress ia = InetAddress.getLocalHost();
            AbstractChecksum localChecksum = new MD("MD5");
            localChecksum.update(ia.getHostName().getBytes());
            sb.append(localChecksum.getFormattedValue());
        } catch(Exception ex) {}
        sb.append("-").append(System.currentTimeMillis()).append("-").append(++count);
        return sb.toString();
    }

    public static String getDate( String fmt) {
        Date aujordhui  = new Date();
        DateFormat dateFormat = new SimpleDateFormat(fmt);
        return dateFormat.format(aujordhui);
    }
    public static String extractEMail(String base64) {
        return Certificate509.extractEMail(base64);
    }
    public static String extractCN(String base64) {
        return Certificate509.extractCN(base64);
    }
    public static void main(String[] args) {
//        String s = generateUniqueID();
    }
    public static String getPresenceArchive(String annee, String collectivite) {
        String ret = "Non";
        Connection con = null;
        try {
        	DataImpl dataLayer = DataLayerManager.getImplementation();
        	String layerName = dataLayer.getLayerName();
        	String query = ("oracle".equals(layerName) ? "SELECT 1 FROM CG_ACCUEIL WHERE COLLECTIVITE=? AND EXERCICE=?" : "SELECT 1 FROM CG_ACCUEIL_IX WHERE COLLECTIVITE=? AND EXERCICE=?" );
        	        	
            con = PoolManager.getInstance().getConnection();         
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,collectivite);
            ps.setString(2,annee);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) ret = "Oui";
            rs.close();
            ps.close();
        } catch(Exception ex) {
           logger.error("getPresenceArchive("+annee+","+collectivite+")",ex);
        } finally {
            if(con!=null) PoolManager.getInstance().releaseConnection(con);
        }
        return ret;
    }

}
