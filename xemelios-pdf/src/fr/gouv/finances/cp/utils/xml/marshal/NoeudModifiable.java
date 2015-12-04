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
 * Cette interface apporte un certain nombre de m�thodes permettant de naviguer au sein de l'arborescence construite d'apr�s un fichier
 * de configuration m�tier.
 *
 */
public interface NoeudModifiable extends XmlMarshallable {
    /**
     * Retourne le <tt>NoeudModifiable</tt> repr�sentant l'�l�ment parent dans l'arbre
     * @return NoeudModifiable
     */
    public NoeudModifiable getParentAsNoeudModifiable();
    /**
     * Met � jour le <tt>NoeudModifiable</tt> repr�sentant l'�l�ment parent dans l'arbre
     * @param p le parent
     */
    public void setParentAsNoeudModifiable(NoeudModifiable p);
    /**
     * Retourne un �l�ment fils retrouv� dans l'une des collections de fils de l'�l�ment courant � partir de son tag et de son identifiant.
     * Attention, son identifiant n'est pas toujours la valeur de l'attribut <tt>id</tt>.
     * Pour connaitre le nom de l'attribut identifiant, utiliser {@link getChildIdAttrName(String)}
     * @param tagName le tag du fils cherch�
     * @param id l'identifiant du fils cherch�
     * @return NoeudModifiable
     */
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id);
    /**
     * NON IMPLEMENTE POUR LE MOMENT
     */
    public void modifyAttr(String attrName, String value);
    /**
     * Met � jour les attributs de l'�lement courant avec ceux de la collection <tt>attrs</tt>
     * @param attrs la liste des attributs � mettre � jour
     */
    public void modifyAttrs(Attributes attrs);
    /**
     * Retourne le nom du ou des attribut(s) servant d'identifiant pour le fils dont le tag est pass� en param�tre
     * @param childTagName le tag du fils
     * @return String[]
     */
    public String[] getChildIdAttrName(String childTagName);
    /**
     * Permet de remettre � z�ro le buffer rempli par addCharacterData
     */
    public void resetCharData();
    /**
     * Renvoie la valeur de l'attribut identifiant.
     * Attention, cet attribut n'est pas forc�ment <tt>id</tt>.
     * Pour connaitre le nom de l'attribut identifiant, utiliser {@link getChildIdAttrName(String)}
     * @return id value
     */
    public String getIdValue();
    /**
     * Renvoie le XPath de cet element
     */
    public String getConfigXPath();

    /**
     * R�alise des op�rations pr�alables � la suppression de la m�moire,
     * afin d'inciter fortement le GC � purger les objets
     */
    public void prepareForUnload();

}