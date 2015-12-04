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
 * This class writes information on a Connection
 * @author CBO
 * @author chm
 */
public class ConnectionInfo {

    private String driver;
    private String driverVersion;
    private String database;
    private String databaseVersion;
    private String url;
    private String username;

    static public ConnectionInfo fromConnection(Connection connection) throws SQLException {
        return new ConnectionInfo(connection.getMetaData().getDriverName(), connection.getMetaData().getDriverVersion(),
                connection.getMetaData().getDatabaseProductName(), connection.getMetaData().getDatabaseProductVersion(),
                connection.getMetaData().getURL(), connection.getMetaData().getUserName());
    }

    public ConnectionInfo(String driver, String driverVersion, String database, String databaseVersion, String url, String username) {
        this.driver = driver;
        this.driverVersion = driverVersion;
        this.database = database;
        this.databaseVersion = databaseVersion;
        this.url = url;
        this.username = username;
    }

    public String getDatabase() {
        return database;
    }

    public String getDatabaseVersion() {
        return databaseVersion;
    }

    public String getDriver() {
        return driver;
    }

    public String getDriverVersion() {
        return driverVersion;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Driver : " + driver + " " + driverVersion + "\n");
        sb.append("Database : " + database + " " + databaseVersion + "\n");
        sb.append("Url : " + url);
        sb.append("Username : " + username);
        return sb.toString();
    }
}
