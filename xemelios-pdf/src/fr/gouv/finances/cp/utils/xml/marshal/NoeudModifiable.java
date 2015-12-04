/*
 * Copyright
 *   2005 axYus - www.axyus.com
 *   2005 N LE CORRE - nicolas.lecorre@axyus.com
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

package fr.gouv.finances.cp.utils.xml.marshal;

import org.xml.sax.Attributes;

/**
 *
 * Cette interface apporte un certain nombre de méthodes permettant de naviguer au sein de l'arborescence construite d'après un fichier
 * de configuration métier.
 *
 */
public interface NoeudModifiable extends XmlMarshallable {
    /**
     * Retourne le <tt>NoeudModifiable</tt> représentant l'élément parent dans l'arbre
     * @return NoeudModifiable
     */
    public NoeudModifiable getParentAsNoeudModifiable();
    /**
     * Met à jour le <tt>NoeudModifiable</tt> représentant l'élément parent dans l'arbre
     * @param p le parent
     */
    public void setParentAsNoeudModifiable(NoeudModifiable p);
    /**
     * Retourne un élément fils retrouvé dans l'une des collections de fils de l'élément courant à partir de son tag et de son identifiant.
     * Attention, son identifiant n'est pas toujours la valeur de l'attribut <tt>id</tt>.
     * Pour connaitre le nom de l'attribut identifiant, utiliser {@link getChildIdAttrName(String)}
     * @param tagName le tag du fils cherché
     * @param id l'identifiant du fils cherché
     * @return NoeudModifiable
     */
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id);
    /**
     * NON IMPLEMENTE POUR LE MOMENT
     */
    public void modifyAttr(String attrName, String value);
    /**
     * Met à jour les attributs de l'élement courant avec ceux de la collection <tt>attrs</tt>
     * @param attrs la liste des attributs à mettre à jour
     */
    public void modifyAttrs(Attributes attrs);
    /**
     * Retourne le nom du ou des attribut(s) servant d'identifiant pour le fils dont le tag est passé en paramètre
     * @param childTagName le tag du fils
     * @return String[]
     */
    public String[] getChildIdAttrName(String childTagName);
    /**
     * Permet de remettre à zéro le buffer rempli par addCharacterData
     */
    public void resetCharData();
    /**
     * Renvoie la valeur de l'attribut identifiant.
     * Attention, cet attribut n'est pas forcément <tt>id</tt>.
     * Pour connaitre le nom de l'attribut identifiant, utiliser {@link getChildIdAttrName(String)}
     * @return id value
     */
    public String getIdValue();
    /**
     * Renvoie le XPath de cet element
     */
    public String getConfigXPath();

    /**
     * Réalise des opérations préalables à la suppression de la mémoire,
     * afin d'inciter fortement le GC à purger les objets
     */
    public void prepareForUnload();

}