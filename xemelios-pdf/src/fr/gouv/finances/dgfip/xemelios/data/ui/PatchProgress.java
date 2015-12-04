/*
 * Copyright
 *   2007 axYus - www.axyus.com
 *   2007 C.Marchand - christophe.marchand@axyus.com
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

package fr.gouv.finances.dgfip.xemelios.data.ui;

/**
 *
 * @author chm
 */
public interface PatchProgress {
    /**
     * Permet de définir la borne maximale du progrès
     * @param maxProgress
     */
    public void setMaxProgress(long maxProgress);
    /**
     * Initialise le progrès à 0
     */
    public void startProgress();
    /**
     * Initialise le progrès à 0 en affichant le message
     * @param message
     */
    public void startProgress(String message);
    /**
     * Incrémente le rpogrès de 1
     */
    public void pushProgress();
    /**
     * Termine le progrès à 100% ou {@link #getProgress()}
     */
    public void endProgress();
    /**
     * Affiche ou masque le  progrès
     * @param visible
     */
    public void setVisible(boolean visible);
    /**
     * Termine tout
     */
    public void dispose();
    /**
     * Renvoie la valeur de progrès courante
     */
    public long getProgress();
    /**
     * Positionne le progrès courant à <tt>progress</tt>
     * @param progress
     */
    public void setProgress(long progress);
    /**
     * Affiche un message
     * @param message
     */
    public void setMessage(String message);
}
