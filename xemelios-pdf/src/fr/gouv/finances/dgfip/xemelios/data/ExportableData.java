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

package fr.gouv.finances.dgfip.xemelios.data;

import java.util.Vector;

/**
 *
 * @author chm
 */
public class ExportableData {
		public static final int LVL_DOC = 1;
		public static final int LVL_COL = 2;
		public static final int LVL_BUD = 3;
                public static final int TYPE_PERSO1 = 4;
                public static final int TYPE_PERSO2 = 5;
		public static final int LVL_FIC = 6; // 4 et 5 sont pris par les specialkey1 et specialKey2
		private String key;
		private String libelle;
		private int level;
		private boolean selected = false;
		private boolean deleted = false;
		private Vector<ExportableData> childs;
		
		public ExportableData () {			
			childs = new Vector<ExportableData>();
		}
		public ExportableData (String k, String li, int lv, boolean s, Vector<ExportableData> c) {
			setKey(k);
			setLibelle(li);
			setLevel(lv);
			setSelected(s);
			childs.addAll(c);
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public int getLevel() {
			return level;
		}
		public void setLevel(int level) {
			this.level = level;
		}
		public String getLibelle() {
			return libelle;
		}
		public void setLibelle(String libelle) {
			this.libelle = libelle;
		}
		public boolean isSelected() {
			return selected;
		}
		public void setSelected(boolean selected) {
			this.selected = selected;
		}
		public Vector<ExportableData> getChilds() {
			return childs;
		}
		public void setChilds(Vector<ExportableData> childs) {
			this.childs = childs;
		}
		public void addChild (ExportableData d) {
			this.childs.add(d);
		}
		public ExportableData getChild(int i) {
			if (i<childs.size()) return childs.get(i);
			else return null;
		}
		public ExportableData getChild(String k) {
			for (ExportableData c:childs) {
				if (c.getKey().equals(k)) return c;
			}
			return null;
		}
		public String toString () { return libelle; }
/*		public String toString () {
			String ret = "";
			for (int i=0; i<level; i++) ret+= "\t";
			ret += libelle+(selected?" selected ":" none ")+"\n";
			for (Data dd:childs) {
				ret += dd.toString();
			}
			return ret;
		}
*/		public boolean isDeleted() {
			return deleted;
		}
		public void setDeleted(boolean deleted) {
			this.deleted = deleted;
		}

}
