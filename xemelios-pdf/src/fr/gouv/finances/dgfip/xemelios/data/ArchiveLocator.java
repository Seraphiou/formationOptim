/*
 * Copyright
 *  2012 axYus - http://www.axyus.com
 *  2012 C.Marchand - christophe.marchand@axyus.com
 *
 * This file is part of XEMELIOS_NB.
 *
 * XEMELIOS_NB is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * XEMELIOS_NB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS_NB; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package fr.gouv.finances.dgfip.xemelios.data;

/**
 * Permet de d�teminer l'emplacement de stockage d'une archive �
 * partir de son nom.
 * Une telle classe doit fournir une m�thode statique<br/>
 * <tt>public static ArchiveLocator getInstance();</tt>
 * @author cmarchand
 */
public interface ArchiveLocator {

    /**
     * Renvoie l'emplacement de l'archive � partir de son nom.
     * Le chemin renvoy� est toujours relatif � une racine,
     * et ne sp�cifie que le r�pertoire o� les volumes de l'archive sont stock�s.
     * @param archiveName Le nom de l'archive
     * @return L'emplacement relatif
     */
    public String getArchiveLocation(String archiveName);
    /**
     * Renvoie le hash de la collectivit�. C'est cette m�thode qui d�termine
     * la r�partition.
     * @param collectivite
     * @return
     */
    public int getCollectiviteHash(String collectivite);
}
