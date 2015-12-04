/*
 * Copyright
 *   2009 axYus - www.axyus.com
 *   2009 C.Marchand - christophe.marchand@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.importers;

import fr.gouv.finances.dgfip.utils.Pair;

/**
 *
 * @author chm
 */
class Collectivite extends Pair {
    private Collectivite parent;

    public Collectivite(String key, String libelle) {
        super();
        this.key=key;
        this.libelle=libelle;
    }
    public Collectivite() { super(); }
    public void setParent(Collectivite parent) {
        this.parent=parent;
    }
}
