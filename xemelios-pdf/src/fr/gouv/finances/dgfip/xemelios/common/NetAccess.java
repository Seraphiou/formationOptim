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

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;
import java.net.InetAddress;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.log4j.Logger;

/**
 * Helper class for network access
 * @author chm
 */
public class NetAccess {
    private static final transient Logger logger = Logger.getLogger(NetAccess.class);
    
    /** Creates a new instance of NetAccess */
    public NetAccess() {
    }
    
    public static HttpClient getHttpClient(PropertiesExpansion applicationProperties) throws DataConfigurationException {
        String proxyHost = applicationProperties.getProperty(Constants.SYS_PROP_PROXY_SERVER);
        String proxyPort = applicationProperties.getProperty(Constants.SYS_PROP_PROXY_PORT);
        String proxyUser = applicationProperties.getProperty(Constants.SYS_PROP_PROXY_USER);
        String sTmp = applicationProperties.getProperty(Constants.SYS_PROP_PROXY_PASSWD);
        String proxyPasswd = sTmp!=null?Scramble.unScramblePassword(sTmp):null;
        int intProxyPort = 0;
        if(proxyPort!=null) {
            try {
                intProxyPort = Integer.parseInt(proxyPort);
            } catch(NumberFormatException nfEx) {
                throw new DataConfigurationException(proxyPort+" n'est pas un numéro de port valide.");
            }
        }
        String domainName = applicationProperties.getProperty(Constants.SYS_PROP_PROXY_DOMAIN);

        HttpClient client = new HttpClient();
        //client.getParams().setAuthenticationPreemptive(true);   // check use of this
        HostConfiguration hc = new HostConfiguration();
        if(proxyHost!=null) {
            hc.setProxy(proxyHost,intProxyPort);
            client.setHostConfiguration(hc);
        }
        if(proxyUser!=null) {
            Credentials creds = null;
            if(domainName!=null && domainName.length()>0) {
                String hostName = "127.0.0.1";
                try {
                    InetAddress ip = InetAddress.getByName("127.0.0.1");
                    hostName = ip.getHostName();
                } catch(Exception ex) {
                    logger.error("",ex);
                }
                creds = new NTCredentials(proxyUser, proxyPasswd, hostName, domainName);
            } else {
                creds = new UsernamePasswordCredentials(proxyUser, proxyPasswd);
            }
            client.getState().setProxyCredentials(AuthScope.ANY, creds);
//            client.getState().setProxyCredentials(AuthScope.ANY,new UsernamePasswordCredentials(proxyUser,proxyPasswd));
        }
        return client;
    }
    
}
