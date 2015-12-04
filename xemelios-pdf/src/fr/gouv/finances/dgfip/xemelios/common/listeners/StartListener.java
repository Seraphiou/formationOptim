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

package fr.gouv.finances.dgfip.xemelios.common.listeners;

/**
 * Cette interface d�finit les m�thodes de signalisation de progression
 * d'avancement du d�marrage de l'application.
 * @author chm
 */
public interface StartListener {

	/**
	 * Permet de notifier un message.
	 * @param msg
	 */
	public void notifyMessage(String msg);
	/**
	 * Permet de notifier une progression.
	 * La progression va de 0 a 100. Toute valeur en dehors de ces limites
	 * doit etre ignoree par les implementeurs.
	 * @param progress
	 */
	public void notifyProgress(int progress);
	/**
	 * Fait avancer la progression de 1 unit�
	 */
	public void pushProgress();
	/**
	 * Fait avancer la progression jusqu'a la fin
	 */
	public void completeProgress();
	public void setVisible(boolean visible);
}
