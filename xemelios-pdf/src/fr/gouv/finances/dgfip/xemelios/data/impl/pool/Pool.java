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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Contract interface of Pool
 * @author CBO
 */
public interface Pool {

    /**
     * Returns a Connection from pool
     * @return
     * @throws java.sql.SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * Allows a connection retrived from pool via {@link getConnection()} to return to pool
     * @param connection
     * @throws java.sql.SQLException
     */
    void releaseConnection(Connection connection) throws SQLException;

    /**
     * Shutdowns the pool, and close all connection in it.
     * @throws java.sql.SQLException
     */
    boolean shutdown() throws SQLException;

    /**
     * Returns the current number of active connections that have been allocated
     * @return
     */
    public int getNumActive();

    /**
     * Returns the current number of idle connections that are waiting to be allocated
     * @return
     */
    public int getNumIdle();
}
