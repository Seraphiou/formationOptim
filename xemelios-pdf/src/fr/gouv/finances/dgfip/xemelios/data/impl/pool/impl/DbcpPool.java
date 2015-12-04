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
package fr.gouv.finances.dgfip.xemelios.data.impl.pool.impl;

import fr.gouv.finances.dgfip.xemelios.data.impl.pool.Pool;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;

/**
 * This pool wraps a DBCP pool.
 * @author chm
 * @author CBO
 */
public class DbcpPool implements Pool {
    private static final Logger logger = Logger.getLogger(DbcpPool.class);

    private DataSource dataSource;

    public DbcpPool(Properties props) {
        super();
        initDataSource(props);
    }

    protected void initDataSource(Properties props) {
        try {
            dataSource = BasicDataSourceFactory.createDataSource(props);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public void releaseConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public boolean shutdown() throws SQLException {
        logger.info("shutdown");
        ((BasicDataSource) getDataSource()).close();
        return true;
    }

    @Override
    public int getNumActive() {
        return ((BasicDataSource) getDataSource()).getNumActive();
    }

    @Override
    public int getNumIdle() {
        return ((BasicDataSource) getDataSource()).getNumIdle();
    }
}
