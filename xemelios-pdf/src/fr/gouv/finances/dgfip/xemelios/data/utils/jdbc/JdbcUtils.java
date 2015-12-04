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
package fr.gouv.finances.dgfip.xemelios.data.utils.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author CBO
 */
public class JdbcUtils {

  private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(JdbcUtils.class);

  static public Object queryObject(Connection connection, String sql, final RowMapper rowMapper) throws SQLException {
    return queryObject(connection, sql, rowMapper, null);
  }

  static public Object queryObject(Connection connection, String sql, final RowMapper rowMapper, PStmtBinder binder) throws SQLException {
    return queryObjectImpl(connection, sql, rowMapper, binder);
  }

  static public List queryObjects(Connection connection, String sql, final RowMapper rowMapper) throws SQLException {
    return queryObjects(connection, sql, rowMapper, null);
  }

  static public List queryObjects(Connection connection, String sql, final RowMapper rowMapper, PStmtBinder binder) throws SQLException {
    return queryObjectsImpl(connection, sql, rowMapper, binder);
  }

  static public void fetchSQL(Connection connection, String sql, RowHandler rowHandler) throws SQLException {
    fetchSQL(connection, sql, rowHandler, null);
  }

  static public void fetchSQL(Connection connection, String sql, RowHandler rowHandler, PStmtBinder binder) throws SQLException {
    fetchSQLImpl(connection, sql, rowHandler, binder);
  }

  static public UpdateResult executeUpdate(Connection connection, String sql) throws SQLException {
    return executeUpdate(connection, sql, null, null);
  }

  static public UpdateResult executeUpdate(Connection connection, String sql, PStmtBinder binder) throws SQLException {
    return executeUpdate(connection, sql, binder, null);
  }

  static public UpdateResult executeUpdate(Connection connection, String sql, String autoFields[]) throws SQLException {
    return executeUpdate(connection, sql, null, autoFields);
  }

  static public UpdateResult executeUpdate(Connection connection, String sql, PStmtBinder binder, String autoFields[]) throws SQLException {
    return executeUpdateImpl(connection, sql, binder, autoFields);
  }

  static public List<UpdateResult> executeUpdates(Connection connection, String sql, List<PStmtBinder> binders) throws SQLException {
    if (binders != null) {
      return executeUpdatesImpl(connection, sql, binders);
    } else {
      return Collections.emptyList();
    }
  }

  static public Integer getInteger(ResultSet rs, String colName) throws SQLException {
    int res = rs.getInt(colName);
    return rs.wasNull() ? null : new Integer(res);
  }

  static public Integer getInteger(ResultSet rs, int colIndex) throws SQLException {
    int res = rs.getInt(colIndex);
    return rs.wasNull() ? null : new Integer(res);
  }

  static public Long getLong(ResultSet rs, String colName) throws SQLException {
    long res = rs.getLong(colName);
    return rs.wasNull() ? null : new Long(res);
  }

  static public Long getLong(ResultSet rs, int colIndex) throws SQLException {
    long res = rs.getLong(colIndex);
    return rs.wasNull() ? null : new Long(res);
  }

  static public Float getFloat(ResultSet rs, String colName) throws SQLException {
    float res = rs.getFloat(colName);
    return rs.wasNull() ? null : new Float(res);
  }

  static public Float getFloat(ResultSet rs, int colIndex) throws SQLException {
    float res = rs.getFloat(colIndex);
    return rs.wasNull() ? null : new Float(res);
  }

  static public Double getDouble(ResultSet rs, String colName) throws SQLException {
    double res = rs.getDouble(colName);
    return rs.wasNull() ? null : new Double(res);
  }

  static public Double getDouble(ResultSet rs, int colIndex) throws SQLException {
    double res = rs.getDouble(colIndex);
    return rs.wasNull() ? null : new Double(res);
  }

  static public Boolean getBoolean(ResultSet rs, String colName) throws SQLException {
    boolean res = rs.getBoolean(colName);
    return rs.wasNull() ? null : new Boolean(res);
  }

  static public Boolean getBoolean(ResultSet rs, int colIndex) throws SQLException {
    boolean res = rs.getBoolean(colIndex);
    return rs.wasNull() ? null : new Boolean(res);
  }

  static public void rollbackQuietly(Connection connection) throws SQLException {
    if (connection != null) {
      try {
        connection.rollback();
      } catch(Throwable throwable) {
        logger.error("Error during rollbackQuietly", throwable);
      }
    }
  }
  
  static private Object queryObjectImpl(Connection connection, String sql, final RowMapper rowMapper, PStmtBinder binder) throws SQLException {
    final ObjectHolder objectHolder = new ObjectHolder();
    RowHandler rowHandler = new RowHandler() {

      public boolean handleRow(ResultSet rs) throws SQLException {
        objectHolder.setObject(rowMapper.map(rs));
        return false;
      }
    };
    fetchSQL(connection, sql, rowHandler, binder);
    return objectHolder.getObject();
  }

  static private List queryObjectsImpl(Connection connection, String sql, final RowMapper rowMapper, PStmtBinder binder) throws SQLException {
    final List objectList = new ArrayList();
    RowHandler rowHandler = new RowHandler() {

      public boolean handleRow(ResultSet rs) throws SQLException {
        objectList.add(rowMapper.map(rs));
        return true;
      }
    };
    fetchSQL(connection, sql, rowHandler, binder);
    return objectList;
  }

  static private void fetchSQLImpl(Connection connection, String sql, RowHandler rowHandler, PStmtBinder binder) throws SQLException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    binder = binder != null ? binder : PStmtBinder.nullPStmtBinder;
    try {
      long start = System.currentTimeMillis();
      ps = connection.prepareStatement(sql);
      binder.bind(ps);
      rs = ps.executeQuery();
      debug(sql, binder, start);
      boolean continueFetching = true;
      while (continueFetching && rs.next()) {
        continueFetching = rowHandler.handleRow(rs);
      }
    } catch (SQLException sqle) {
      error(sql, binder, sqle);
      throw sqle;
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
      } finally {
        if (ps != null) {
          ps.close();
        }
      }
    }
  }

  static private List<UpdateResult> executeUpdatesImpl(Connection connection, String sql, List<PStmtBinder> binders) throws SQLException {
    boolean isLogged = false;
    PreparedStatement ps = null;
    try {
      long start = System.currentTimeMillis();
      ps = connection.prepareStatement(sql);
      for (PStmtBinder binder : binders) {
        binder.bind(ps);
        try {
          ps.addBatch();
        } catch (SQLException sqle) {
          error(sql, binder, sqle);
          isLogged = true;
          throw (sqle);
        }
      }
      int toReturnAsInt[] = ps.executeBatch();
      List<UpdateResult> toReturn = new ArrayList<UpdateResult>(toReturnAsInt.length);
      debug(sql, binders, start);
      for (int i = 0; i < toReturnAsInt.length; i++) {
        toReturn.add(new UpdateResult(toReturnAsInt[i], null));
      }
      return toReturn;
    } catch (SQLException sqle) {
      if (!isLogged) {
        error(sql, binders, sqle);
      }
      throw sqle;
    } finally {
      if (ps != null) {
        ps.close();
      }
    }
  }

  static private UpdateResult executeUpdateImpl(Connection connection, String sql, PStmtBinder binder, String autoFields[]) throws SQLException {
    PreparedStatement ps = null;
    ResultSet keyRs = null;
    binder = binder != null ? binder : PStmtBinder.nullPStmtBinder;
    try {
      long start = System.currentTimeMillis();
      if (autoFields != null) {
        ps = connection.prepareStatement(sql, autoFields);
      } else {
        ps = connection.prepareStatement(sql);
      }
      binder.bind(ps);
      int countUpdated = ps.executeUpdate();
      debug(sql, binder, start);
      List autoFieldResults = null;
      if (autoFields != null && autoFields.length > 0) {
        keyRs = ps.getGeneratedKeys();
        autoFieldResults = new ArrayList();
        while (keyRs.next()) {
          autoFieldResults.add(keyRs.getObject(1));
        }
      } else {
        autoFieldResults = Collections.EMPTY_LIST;
      }
      return new UpdateResult(countUpdated, autoFieldResults.toArray());
    } catch (SQLException sqle) {
      error(sql, binder, sqle);
      throw sqle;
    } finally {
      try {
        if (keyRs != null) {
          keyRs.close();
        }
      } finally {
        if (ps != null) {
          ps.close();
        }
      }
    }
  }

  static private void error(String sql, PStmtBinder binder, SQLException sqle) {
    StringBuffer sb = new StringBuffer();
    sb.append("Erreur SQL :\n");
    sb.append("\n-----------------------------------------------------------------------------------------------\n");
    sb.append("---------- Original SQL is :\n");
    sb.append(sql);
    sb.append("\n");
    sb.append("---------- Parameters are :\n");
    sb.append(binder.toLogableParametersString());
    sb.append("\n---------- Binded SQL is :\n");
    sb.append(binder.toLogableBindedString(sql));
    sb.append("\n");
    sb.append("-----------------------------------------------------------------------------------------------\n");
    logger.info(sb.toString(), sqle);
  }

  static private void error(String sql, List<PStmtBinder> binders, SQLException sqle) {
    StringBuffer sb = new StringBuffer();
    sb.append("Erreur SQL :\n");
    sb.append("\n-----------------------------------------------------------------------------------------------\n");
    sb.append("---------- Original SQL is :\n");
    sb.append(sql);
    sb.append("\n");
    Iterator<PStmtBinder> iterator = binders.iterator();
    for (int i = 0; iterator.hasNext(); i++) {
      PStmtBinder binder = iterator.next();
      sb.append("---------- Parameters " + i + " are :\n");
      sb.append(binder.toLogableParametersString());
      sb.append("\n");
      if (i > 10) {
        sb.append("skipping others...\n");
        break;
      }
    }
    sb.append("-----------------------------------------------------------------------------------------------\n");
    logger.info(sb.toString(), sqle);
  }

  static private void debug(String sql, PStmtBinder binder, long startTime) {
    if (logger.isDebugEnabled()) {
      long duration = System.currentTimeMillis() - startTime;
      StringBuffer sb = new StringBuffer();
      sb.append("\n-----------------------------------------------------------------------------------------------\n");
      sb.append("---------- Original SQL is :\n");
      sb.append(sql);
      sb.append("\n");
      sb.append("---------- Binded SQL is :\n");
      sb.append(binder.toLogableBindedString(sql));
      sb.append("\n");
      sb.append("---------- Duration (without fetching) : " + duration + " ms\n");
      sb.append("-----------------------------------------------------------------------------------------------\n");
      logger.debug(sb.toString());
    }
  }

  static private void debug(String sql, List<PStmtBinder> binders, long startTime) {
    if (logger.isDebugEnabled()) {
      long duration = System.currentTimeMillis() - startTime;
      StringBuffer sb = new StringBuffer();
      sb.append("\n-----------------------------------------------------------------------------------------------\n");
      sb.append("---------- SQL for batch is :\n");
      sb.append(sql);
      sb.append("\n");
      Iterator<PStmtBinder> iterator = binders.iterator();
      for (int i = 0; iterator.hasNext(); i++) {
        PStmtBinder binder = iterator.next();
        sb.append("---------- Parameters " + i + " are :\n");
        sb.append(binder.toLogableParametersString());
        sb.append("\n");
        if (i > 10) {
          sb.append("skipping others...\n");
          break;
        }
      }
      sb.append("---------- Duration : " + duration + " ms\n");
      sb.append("-----------------------------------------------------------------------------------------------\n");
      logger.debug(sb.toString());
    }
  }
}
