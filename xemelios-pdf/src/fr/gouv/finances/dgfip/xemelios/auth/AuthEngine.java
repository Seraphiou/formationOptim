/*
 * Copyright
 *   2008 axYus - www.axyus.com
 *   2008 C.Bosquet - charles.bosquet@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.auth;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import fr.gouv.finances.dgfip.xemelios.data.DataLayerManager;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * Contract of authentication engines. Concrete implementation
 * must provide a no-argument constructor.
 * @author CBO
 */
public interface AuthEngine {

    /**
     * Return true if the login is required by createUser method
     * @return true if the login is required by createUser method
     */
    boolean isLoginRequired();

    /**
     * Return true if the password is required by createUser method
     * @return true if the password is required by createUser method
     */
    boolean isPasswordRequired();

    /**
     * Return the user authenticated by the login and the password provided, null if login or password is incorrect
     * @param login
     * @param password
     * @return the user authenticated by the login and the password provided, null if login or password is incorrect
     */
    XemeliosUser createUser(String login, String password);

    static public class Holder {
        private static final Logger logger = Logger.getLogger(Holder.class);

        static public final String DEFAULT_AUTH_ENGINE_SWING_CLASS_NAME_PROPERTY = "authEngineClassName";
        static public final String DEFAULT_AUTH_ENGINE_WEB_CLASS_NAME_PROPERTY = "web.authEngineClassName";
        static private final String DEFAULT_AUTH_ENGINE_CLASS_NAME = "fr.gouv.finances.dgfip.xemelios.auth.impl.std.db.StdDbAuthEngineImpl";
        static private AuthEngine authEngine;

        /**
         * Initialise la couche d'authentification
         * @param applicationProperties
         */
        static public void init(PropertiesExpansion applicationProperties) {
            init(DEFAULT_AUTH_ENGINE_SWING_CLASS_NAME_PROPERTY);
        }
        static public void init(String propertyName) {
            try {
                String authEngineClassName = DataLayerManager.getImplementation().getParameterValue(propertyName);
                logger.debug("try to start "+authEngineClassName);
                if (authEngineClassName == null) {
                    authEngineClassName = DEFAULT_AUTH_ENGINE_CLASS_NAME;
                }
                Class authEngineclass = Class.forName(authEngineClassName);
                authEngine = (AuthEngine)authEngineclass.newInstance();
            } catch(Exception e) {
                logger.error("auth start: ",e);
                throw new RuntimeException(e);
            }
        }

        static public AuthEngine getAuthEngine() {
            return authEngine;
        }

    }

    static public class Helper {

        static private final int ROLE_CONNECT_BIT = 0x0001; // 00000001
        static private final int ROLE_IMPORT_BIT  = 0x0002; // 00000010
        static private final int ROLE_CLEAN_BIT   = 0x0004; // 00000100
        static private final int ROLE_SEARCH_BIT  = 0x0010; // 00001000
        static private final int ROLE_BROWSE_BIT  = 0x0020; // 00010000
        static private final int ROLE_EXPORT_BIT  = 0x0040; // 00100000

        static public Set rolesFromInt(int rolesAsInt) {
            Set toReturn = new HashSet();
            if ((rolesAsInt & ROLE_BROWSE_BIT) > 0) {
                toReturn.add(XemeliosUser.ROLE_BROWSE);
            }
            if ((rolesAsInt & ROLE_CLEAN_BIT) > 0) {
                toReturn.add(XemeliosUser.ROLE_CLEAN);
            }
            if ((rolesAsInt & ROLE_CONNECT_BIT) > 0) {
                toReturn.add(XemeliosUser.ROLE_CONNECT);
            }
            if ((rolesAsInt & ROLE_EXPORT_BIT) > 0) {
                toReturn.add(XemeliosUser.ROLE_EXPORT);
            }
            if ((rolesAsInt & ROLE_IMPORT_BIT) > 0) {
                toReturn.add(XemeliosUser.ROLE_IMPORT);
            }
            if ((rolesAsInt & ROLE_SEARCH_BIT) > 0) {
                toReturn.add(XemeliosUser.ROLE_SEARCH);
            }
            return toReturn;
        }

    }
}
