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

import java.sql.SQLException;

/**
 * This class wraps a SQLException into a RuntimeException
 * @author chm
 * @author CBO
 */
public class SQLRuntimeException extends RuntimeException {
  public SQLRuntimeException(SQLException sqle) {
    super(sqle);
  }

  public SQLException getSQLCause() {
    return (SQLException) getCause();
  }

  public String getSQLState() {
    return getSQLCause().getSQLState();
  }

  public int getErrorCode() {
    return getSQLCause().getErrorCode();
  }

}
