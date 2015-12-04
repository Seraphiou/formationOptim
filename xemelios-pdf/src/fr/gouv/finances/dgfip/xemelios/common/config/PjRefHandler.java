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

package fr.gouv.finances.dgfip.xemelios.common.config;

import java.util.Vector;

import org.w3c.dom.Element;

/**
 * This class carries many PJRef items to display them in a {@link fr.gouv.finances.cp.xemelios.ui.resulttable.ResultTable}
 * @author chm
 *
 */
public class PjRefHandler implements Comparable {
	public static final transient String DISPLAY="pj-ref-list";
	private Vector<PJRefInfo> pjs;

	public PjRefHandler() {
		super();
		pjs = new Vector<PJRefInfo>();
	}
	@Override
	public String toString() { return DISPLAY; }
	public void addPj(DocumentModel dm, Element el) {
		PJRefInfo pj = new PJRefInfo(dm,el);
		pjs.add(pj);
	}
	public Iterable<PJRefInfo> getPjs() {
		return pjs;
	}
	public int compareTo(Object o) {
		return 0;
	}
        public int getPjCount() { return pjs.size(); }
	
}
