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

import java.lang.reflect.Constructor;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author CBO
 */
public interface RowMapper {

  Object map(ResultSet rs) throws SQLException;

  static public class ClassRowMapper implements RowMapper {

    private Class clazz;

    public ClassRowMapper(Class clazz) {
      this.clazz = clazz;
    }

    public Object map(ResultSet rs) throws SQLException {
      try {
        Constructor constructor = clazz.getConstructor(new Class[]{ResultSet.class});
        return constructor.newInstance(new Object[]{rs});
      } catch (Exception ex) {
        throw new RuntimeException("Erreur lors de la création d'une instance de la classe " + this.clazz, ex);
      }
    }
  }

  static public abstract class ColRowMapper implements RowMapper {

    private Integer colIndex;
    private String colName;

    public ColRowMapper() {
      this(1);
    }

    public ColRowMapper(int colIndex) {
      this.colIndex = new Integer(colIndex);
    }

    public ColRowMapper(String colName) {
      this.colName = colName;
    }

    public Integer getColIndex() {
      return colIndex;
    }

    public void setColIndex(Integer colIndex) {
      this.colIndex = colIndex;
      this.colName = null;
    }

    public String getColName() {
      return colName;
    }

    public void setColName(String colName) {
      this.colName = colName;
      this.colIndex = null;
    }
  }

  static public class ColToStringRowMapper extends ColRowMapper {

    public ColToStringRowMapper() {
      super();
    }

    public ColToStringRowMapper(int colIndex) {
      super(colIndex);
    }

    public ColToStringRowMapper(String colName) {
      super(colName);
    }

    public Object map(ResultSet rs) throws SQLException {
      return getColIndex() != null ? rs.getString(getColIndex().intValue()) : rs.getString(getColName());
    }
  }

  static public class ColToIntegerRowMapper extends ColRowMapper {

    public ColToIntegerRowMapper() {
      super();
    }

    public ColToIntegerRowMapper(int colIndex) {
      super(colIndex);
    }

    public ColToIntegerRowMapper(String colName) {
      super(colName);
    }

    public Object map(ResultSet rs) throws SQLException {
      return getColIndex() != null ? JdbcUtils.getInteger(rs, getColIndex().intValue()) : JdbcUtils.getInteger(rs, getColName());
    }
  }

  static public class ColToLongRowMapper extends ColRowMapper {

    public ColToLongRowMapper() {
      super();
    }

    public ColToLongRowMapper(int colIndex) {
      super(colIndex);
    }

    public ColToLongRowMapper(String colName) {
      super(colName);
    }

    public Object map(ResultSet rs) throws SQLException {
      return getColIndex() != null ? JdbcUtils.getLong(rs, getColIndex().intValue()) : JdbcUtils.getLong(rs, getColName());
    }
  }

  static public class ColToFloatRowMapper extends ColRowMapper {

    public ColToFloatRowMapper() {
      super();
    }

    public ColToFloatRowMapper(int colIndex) {
      super(colIndex);
    }

    public ColToFloatRowMapper(String colName) {
      super(colName);
    }

    public Object map(ResultSet rs) throws SQLException {
      return getColIndex() != null ? JdbcUtils.getFloat(rs, getColIndex().intValue()) : JdbcUtils.getFloat(rs, getColName());
    }
  }

  static public class ColToDoubleRowMapper extends ColRowMapper {

    public ColToDoubleRowMapper() {
      super();
    }

    public ColToDoubleRowMapper(int colIndex) {
      super(colIndex);
    }

    public ColToDoubleRowMapper(String colName) {
      super(colName);
    }

    public Object map(ResultSet rs) throws SQLException {
      return getColIndex() != null ? JdbcUtils.getDouble(rs, getColIndex().intValue()) : JdbcUtils.getDouble(rs, getColName());
    }
  }

  static public class ColToBooleanRowMapper extends ColRowMapper {

    public ColToBooleanRowMapper() {
      super();
    }

    public ColToBooleanRowMapper(int colIndex) {
      super(colIndex);
    }

    public ColToBooleanRowMapper(String colName) {
      super(colName);
    }

    public Object map(ResultSet rs) throws SQLException {
      return getColIndex() != null ? JdbcUtils.getBoolean(rs, getColIndex().intValue()) : JdbcUtils.getBoolean(rs, getColName());
    }
  }

  static public class ColToDateRowMapper extends ColRowMapper {

    public ColToDateRowMapper() {
      super();
    }

    public ColToDateRowMapper(int colIndex) {
      super(colIndex);
    }

    public ColToDateRowMapper(String colName) {
      super(colName);
    }

    public Object map(ResultSet rs) throws SQLException {
      return getColIndex() != null ? rs.getDate(getColIndex().intValue()) : rs.getDate(getColName());
    }
  }

  static public class ColToTimeRowMapper extends ColRowMapper {

    public ColToTimeRowMapper() {
      super();
    }

    public ColToTimeRowMapper(int colIndex) {
      super(colIndex);
    }

    public ColToTimeRowMapper(String colName) {
      super(colName);
    }

    public Object map(ResultSet rs) throws SQLException {
      return getColIndex() != null ? rs.getTime(getColIndex().intValue()) : rs.getTime(getColName());
    }
  }

  static public class ColToTimestampRowMapper extends ColRowMapper {

    public ColToTimestampRowMapper() {
      super();
    }

    public ColToTimestampRowMapper(int colIndex) {
      super(colIndex);
    }

    public ColToTimestampRowMapper(String colName) {
      super(colName);
    }

    public Object map(ResultSet rs) throws SQLException {
      return getColIndex() != null ? rs.getTimestamp(getColIndex().intValue()) : rs.getTimestamp(getColName());
    }
  }
  
  static public class ClobColToStringRowMapper extends ColRowMapper {

    public ClobColToStringRowMapper() {
      super();
    }

    public ClobColToStringRowMapper(int colIndex) {
      super(colIndex);
    }

    public ClobColToStringRowMapper(String colName) {
      super(colName);
    }

    public Object map(ResultSet rs) throws SQLException {
      Clob clob = getColIndex() != null ? rs.getClob(getColIndex().intValue()) : rs.getClob(getColName());
      return clob != null && clob.length() > 0 ? clob.getSubString(1, (int) (clob.length())) : null;
    }
  }
  
  static public class BlobColToByteArrayRowMapper extends ColRowMapper {
    
    public BlobColToByteArrayRowMapper() {
      super();
    }

    public BlobColToByteArrayRowMapper(int colIndex) {
      super(colIndex);
    }

    public BlobColToByteArrayRowMapper(String colName) {
      super(colName);
    }

    public Object map(ResultSet rs) throws SQLException {
      Blob blob = getColIndex() != null ? rs.getBlob(getColIndex().intValue()) : rs.getBlob(getColName());
      return blob != null && blob.length() > 0 ? blob.getBytes(1, (int) (blob.length())) : null;
    }
  }
}
