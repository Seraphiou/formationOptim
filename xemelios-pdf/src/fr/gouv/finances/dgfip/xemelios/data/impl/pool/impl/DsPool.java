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
import fr.gouv.finances.dgfip.xemelios.data.impl.pool.SQLRuntimeException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Pool that wraps a JNDI DataSource
 * @author chm
 */
public class DsPool implements Pool {

  private DataSource dataSource;

  public DsPool(String lookupName) {
    this(getDSFromJNDI(lookupName));
  }

  public DsPool(DataSource dataSource) {
    this.dataSource = dataSource;
    Connection connection = null;
    try {
      connection = this.dataSource.getConnection();
    } catch (SQLException sqle) {
      throw new SQLRuntimeException(sqle);
    } finally {
      try {
        releaseConnection(connection);
      } catch (SQLException sqle) {
        throw new SQLRuntimeException(sqle);
      }
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
    return false;
  }

  static private DataSource getDSFromJNDI(String jndiName) {
    try {
      Context ctx = new InitialContext();
      return (DataSource) ctx.lookup(jndiName);
    } catch (NamingException ne) {
      throw new RuntimeException(ne);
    }
  }

    @Override
    public int getNumActive() {
        return 0;
    }

    @Override
    public int getNumIdle() {
        return 0;
    }

}
