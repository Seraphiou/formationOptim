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

package fr.gouv.finances.dgfip.xemelios.auth;

import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;

/**
 * Carries user's properties.
 * If an implementation carries user's password, it must be encrypted,
 * but its better not to keep password in instance.
 * @author chm
 *
 */
public interface XemeliosUser {

	static public final int ROLE_CONNECT_BIT = 0x0001; // 00000001
	static public final int ROLE_IMPORT_BIT = 0x0002;  // 00000010
	static public final int ROLE_CLEAN_BIT = 0x0004;   // 00000100
	static public final int ROLE_SEARCH_BIT = 0x0010;  // 00001000
	static public final int ROLE_BROWSE_BIT = 0x0020;  // 00010000
	static public final int ROLE_EXPORT_BIT = 0x0040;  // 00100000

	public final String ROLE_CONNECT = "CONNECT";
	public final String ROLE_IMPORT = "IMPORT";
	public final String ROLE_CLEAN = "CLEAN";
	public final String ROLE_SEARCH = "SEARCH";
	public final String ROLE_BROWSE = "BROWSE";
	public final String ROLE_EXPORT = "EXPORT";

	/**
	 * Returns this user's ID
	 * @return ID
	 */
	public String getId();
	/**
	 * Returns the display name for this user (you can decide to put anything in)
	 * @return The display name
	 */
	public String getDisplayName();

    /**
     * Returns true if this user has this role
     * @param role
     * @return true if this user has this role
     */
    public boolean hasRole(String role);

    /**
     * Returns true if this user is allowed to this document
     * @param docId
     * @return true if this user is allowed to this document
     */
    public boolean hasDocument(String document);

    /**
     * Returns true if this user is allowed to this document
     * @param docId
     * @return true if this user is allowed to this document
     */
    public boolean hasCollectivite(String collectivite, DocumentModel dm);
}
