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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author CBO
 */
public interface PStmtBinder {

  static public final PStmtBinder nullPStmtBinder = new PStmtBinder.NullPStmtBinder();

  public void bind(PreparedStatement pStmt) throws SQLException;

  public String toLogableBindedString(String sql);

  public String toLogableParametersString();

  public static class SimplePStmtBinderBuilder {

    private List paramValues = new ArrayList();
    private List paramTypes = new ArrayList();

    public SimplePStmtBinder toPStmtBinder() {
      int paramTypesAsIntArray[] = new int[paramTypes.size()];
      for (int i = 0; i < paramTypes.size(); i++) {
        paramTypesAsIntArray[i] = ((Integer) paramTypes.get(i)).intValue();
      }
      return new SimplePStmtBinder(paramValues.toArray(), paramTypesAsIntArray);
    }

    public SimplePStmtBinderBuilder add(int value) {
      paramValues.add(new Integer(value));
      paramTypes.add(new Integer(Types.NUMERIC));
      return this;
    }

    public SimplePStmtBinderBuilder add(Integer value) {
      paramValues.add(value);
      paramTypes.add(new Integer(Types.NUMERIC));
      return this;
    }

    public SimplePStmtBinderBuilder add(long value) {
      paramValues.add(new Long(value));
      paramTypes.add(new Integer(Types.NUMERIC));
      return this;
    }

    public SimplePStmtBinderBuilder add(Long value) {
      paramValues.add(value);
      paramTypes.add(new Integer(Types.NUMERIC));
      return this;
    }

    public SimplePStmtBinderBuilder add(float value) {
      paramValues.add(new Float(value));
      paramTypes.add(new Integer(Types.NUMERIC));
      return this;
    }

    public SimplePStmtBinderBuilder add(Float value) {
      paramValues.add(value);
      paramTypes.add(new Integer(Types.NUMERIC));
      return this;
    }

    public SimplePStmtBinderBuilder add(double value) {
      paramValues.add(new Double(value));
      paramTypes.add(new Integer(Types.NUMERIC));
      return this;
    }

    public SimplePStmtBinderBuilder add(Double value) {
      paramValues.add(value);
      paramTypes.add(new Integer(Types.NUMERIC));
      return this;
    }

    public SimplePStmtBinderBuilder add(String value) {
      paramValues.add(value);
      paramTypes.add(new Integer(Types.VARCHAR));
      return this;
    }

    public SimplePStmtBinderBuilder add(Date value) {
      paramValues.add(value);
      paramTypes.add(new Integer(Types.DATE));
      return this;
    }

    public SimplePStmtBinderBuilder add(Timestamp value) {
      paramValues.add(value);
      paramTypes.add(new Integer(Types.TIMESTAMP));
      return this;
    }

    public SimplePStmtBinderBuilder add(BigDecimal value) {
      paramValues.add(value);
      paramTypes.add(new Integer(Types.NUMERIC));
      return this;
    }

    public SimplePStmtBinderBuilder add(boolean value) {
      paramValues.add(new Boolean(value));
      paramTypes.add(new Integer(Types.BOOLEAN));
      return this;
    }

    public SimplePStmtBinderBuilder add(Boolean value) {
      paramValues.add(value);
      paramTypes.add(new Integer(Types.BOOLEAN));
      return this;
    }

    public SimplePStmtBinderBuilder addAll(SimplePStmtBinder binder) {
      for (int i = 0; binder.parameters != null && i < binder.parameters.length; i++) {
        paramValues.add(binder.parameters[i]);
        paramTypes.add(new Integer((binder.sqlTypes != null && i < binder.sqlTypes.length) ? binder.sqlTypes[i] : Types.NULL));
      }
      return this;
    }
  }

  public static class SimplePStmtBinder implements PStmtBinder {

    static private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Object parameters[];
    private int sqlTypes[];

    public SimplePStmtBinder(Object parameters[]) {
      this(parameters, null);
    }

    public SimplePStmtBinder(Object parameters[], int sqlTypes[]) {
      this.parameters = parameters;
      this.sqlTypes = sqlTypes;
    }

    public void bind(PreparedStatement pStmt) throws SQLException {
      for (int i = 0; parameters != null && i < parameters.length; i++) {
        Integer sqlType = getSQLType(pStmt, i);
        Object value = parameters[i];
        if (sqlType != null && sqlType.intValue() != Types.NULL) {
          pStmt.setObject(i + 1, value, sqlType.intValue());
        } else {
          pStmt.setObject(i + 1, value);
        }
      }
    }

    public String toLogableBindedString(String sql) {
      for (int i = 0; parameters != null && i < parameters.length; i++) {
        String param;
        if (parameters[i] == null) {
          param = "null";
        } else if (parameters[i] instanceof String) {
          param = "'" + parameters[i].toString() + "'";
        } else if (parameters[i] instanceof java.util.Date) { 
          param = "TO_DATE('" + dateFormat.format(parameters[i]) + "', 'DD/MM/YYYY')";
        } else {
          param = parameters[i].toString();
        }
        sql = replaceFirstString(sql, "?", param);
      }
      return sql;
    }

    public String toLogableParametersString() {
      StringBuffer sb = new StringBuffer();
      if (parameters == null) {
        sb.append("no parameter");
      } else {
        for (int i = 0; i < parameters.length; i++) {
          if (i > 0) {
            sb.append("\n");
          }
          sb.append("[");
          sb.append(parameters[i] == null ? null : parameters[i]);
          sb.append("]");
        }
      }
      return sb.toString();
    }

    private Integer getSQLType(PreparedStatement pStmt, int i) throws SQLException {
      if (sqlTypes != null && sqlTypes.length > i) {
        return new Integer(sqlTypes[i]);
      } else {
        ResultSetMetaData metaData = pStmt.getMetaData();
        return metaData != null ? new Integer(metaData.getColumnType(i + 1)) : null;
      }
    }

    static private String replaceFirstString(String where, String what, String with) {
      int index;
      if ((index = where.indexOf(what)) >= 0) {
        return where.substring(0, index) + with + where.substring(index + what.length());
      }
      return where;
    }
  }

  public static class NullPStmtBinder extends SimplePStmtBinder {

    private NullPStmtBinder() {
      super(null, null);
    }
  }
}
