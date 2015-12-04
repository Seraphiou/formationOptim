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

package fr.gouv.finances.dgfip.utils.xml.certs;

import fr.gouv.finances.dgfip.utils.Base64;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 *
 * @author chm
 */
public class Certificate509 {
    private static final Logger logger = Logger.getLogger(Certificate509.class);
    
    /** Creates a new instance of Certificate509 */
    public Certificate509() {
    }
    public static String extractCN(String base64) {
//        BASE64Decoder decoder=new BASE64Decoder();
        String emetteur = "";
        try {
//            byte[] buffer = decoder.decodeBuffer(base64);
            byte[] buffer = Base64.decode(base64);
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            X509Certificate cert = (X509Certificate)factory.generateCertificate(bais);
//printCert(cert);
            String dn = cert.getSubjectX500Principal().getName();
//logger.debug("dn="+dn);
            StringTokenizer tokenizer = new StringTokenizer(dn,",");
            while(tokenizer.hasMoreTokens()) {
                    String tok = tokenizer.nextToken();
                    if(tok.startsWith("CN=")) emetteur = tok.substring(3); 
            }
        } catch(Exception ioEx) {
            logger.error("unable to decode "+base64);
        }
        return emetteur;
    }
    public static String extractEMail(String base64) {
//        BASE64Decoder decoder=new BASE64Decoder();
        String emetteur = "";
        try {
//            byte[] buffer = decoder.decodeBuffer(base64);
            byte[] buffer = Base64.decode(base64);
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            X509Certificate cert = (X509Certificate)factory.generateCertificate(bais);
            String dn = cert.getSubjectX500Principal().toString();
//logger.debug("subjectDN="+dn);
            StringTokenizer tokenizer = new StringTokenizer(dn,",");
            while(tokenizer.hasMoreTokens()) {
                    String tok = tokenizer.nextToken();
                    if(tok.startsWith("EMAILADDRESS=")) emetteur = tok.substring("EMAILADDRESS=".length()); 
            }
        } catch(Exception ioEx) {
            logger.error("unable to decode "+base64);
        }
        return emetteur;
    }
    private static void printCert(X509Certificate cert) {
        logger.debug("issuerDN.toString()="+cert.getIssuerDN().toString());
        logger.debug("issuerDN.getName()="+cert.getIssuerDN().getName());
        logger.debug("X500principal.toString()="+cert.getIssuerX500Principal().toString());
        logger.debug("X500principal.getName()="+cert.getIssuerX500Principal().getName());
        logger.debug("subjectDn.toString()="+cert.getSubjectDN().toString());
        logger.debug("subjectDn.getName()="+cert.getSubjectDN().getName());
        logger.debug("subjectX500principal.toString()="+cert.getSubjectX500Principal().toString());
        logger.debug("subjectX500principal.getName()="+cert.getSubjectX500Principal().getName());
        try {
            Collection<List<?>> coll = cert.getIssuerAlternativeNames();
            if(coll!=null) {
                for(List<?> l:coll) {
                    for(Object o:l) {
                        logger.debug("issuerAltName="+o.toString());
                    }
                }
            }
            Collection<List<?>> coll2 = cert.getSubjectAlternativeNames();
            if(coll!=null) {
                for(List<?> l:coll2) {
                    for(Object o:l) {
                        logger.debug("subjectAltName="+o.toString());
                    }
                }
            }
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
