/*
 * Copyright
 *   2008 axYus - www.axyus.com
 *   2008 C.Marchand - christophe.marchand@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.data.impl.pool;

import fr.gouv.finances.dgfip.xemelios.common.Scramble;
import fr.gouv.finances.dgfip.xemelios.data.impl.pool.impl.DbcpPool;
import fr.gouv.finances.dgfip.xemelios.data.impl.pool.impl.DsPool;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * This class is in charge to provide pools
 */
public class PoolManager {
    public static final transient String PROP_JNDI_POOLNAME = "jndi.poolname";
    public static final transient String PROP_DATABASE_URL = "database.url";
    public static final transient String PROP_DRIVER_CLASS_NAME = "driver.class";
    public static final transient String PROP_USERNAME = "user";
    public static final transient String PROP_PASSWORD = "password";
    public static final transient String PROP_MAX_ACTIVE = "max.active";
    public static final transient String PROP_MAX_WAIT = "max.wait";
    public static final transient String PROP_VALIDATION_QUERY = "validation.query";

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PoolManager.class);
    private static final Object locker1 = new Object(),  locker2 = new Object();
    static private PoolManager instance = new PoolManager();
    private Pool pool;
    private boolean init = false;

    private PoolManager() {
    }

    static public PoolManager getInstance() {
        return PoolManager.instance;
    }

    private void checkInit() {
        if (!init) {
            throw new RuntimeException("Le singleton " + getClass().getName() + " doit être initialisé avant usage");
        }
    }

    public void init(Properties props) {
        if(init) { return; }
        synchronized (locker1) {
            synchronized (locker2) {
                if(props.containsKey(PROP_JNDI_POOLNAME)) {
                    pool = new DsPool(props.getProperty(PROP_JNDI_POOLNAME));
                } else {
                    Properties properties = new Properties();
                    StringBuffer buffer = new StringBuffer();
                    for(Enumeration<Object> keys = props.keys();keys.hasMoreElements();) {
                        String key = keys.nextElement().toString();
                        String value = props.getProperty(key);
                        if(PROP_DATABASE_URL.equals(key)) {
                            properties.setProperty("url", value);
                        } else if(PROP_DRIVER_CLASS_NAME.equals(key)) {
                            properties.setProperty("driverClassName", value);
                        } else if(PROP_USERNAME.equals(key)) {
                            properties.setProperty("username", value);
                        } else if(PROP_PASSWORD.equals(key)) {
                            properties.setProperty("password", Scramble.unScramblePassword(value));
                        } else if(PROP_MAX_ACTIVE.equals(key)) {
                            properties.setProperty("maxActive", value);
                        } else if(PROP_MAX_WAIT.equals(key)) {
                            properties.setProperty("maxWait", value);
                        } else if(PROP_VALIDATION_QUERY.equals(key)) {
                            properties.setProperty("validationQuery", value);
                        } else {
                            buffer.append(key).append("=").append(value).append(";");
                        }
                    }
                    if(properties.getProperty("maxActive")==null) properties.setProperty("maxActive", "8");
                    if(properties.getProperty("maxWait")==null) properties.setProperty("maxWait","-1");
                    properties.setProperty("connectionProperties", buffer.toString());
                    properties.setProperty("defaultAutoCommit", "true");
logger.debug(properties);
                    pool = new DbcpPool(properties);
                }
                if(pool!=null) {
                    init=true;
                }
            }
        }
    }
    private void assertIsNull(Object o,Properties props) {
//        logger.info(props);
        if(o!=null) {
            logger.error("previous value is not null: "+o);
            throw new RuntimeException("Previous value was not null");
        }
    }


    public void shutdown() throws SQLRuntimeException {
        synchronized (locker1) {
            synchronized(locker2) {
                try {
                    if(pool!=null) {
                        if(pool.shutdown()) {
                            pool = null;
                        }
                    }
                } catch(SQLException sqlEx) {
                    throw new SQLRuntimeException(sqlEx);
                }
            }
        }
    }

    public void logPoolInfo() throws SQLRuntimeException {
        checkInit();
        //for (Entry<String, Pool> poolEntry : poolsByName.entrySet()) {
            Connection connection = null;
            try {
                connection = pool.getConnection();
                logger.info(ConnectionInfo.fromConnection(connection).toString());
            } catch (SQLException sqle) {
                throw new SQLRuntimeException(sqle);
            } finally {
                try {
                    pool.releaseConnection(connection);
                } catch (SQLException sqle) {
                    throw new SQLRuntimeException(sqle);
                }
            }
//        }
    }

    public Connection getConnection() throws SQLRuntimeException {
        checkInit();
        try {
            return pool.getConnection();
        } catch (SQLException sqle) {
            throw new SQLRuntimeException(sqle);
        }
    }

    public void releaseConnection(Connection connection) throws SQLRuntimeException {
        checkInit();
        try {
            pool.releaseConnection(connection);
        } catch (SQLException sqle) {
            //throw new SQLRuntimeException(sqle);
            // throw rien du tout, juste pour logger
            logger.error("releaseConnection(Connection)", sqle);
        }
    }

    public Pool getPool() { return pool; }
}
