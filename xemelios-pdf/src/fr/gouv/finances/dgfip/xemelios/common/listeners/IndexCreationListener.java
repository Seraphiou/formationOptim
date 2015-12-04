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

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

/**
 * Sert a ecouter les informations de creation d'index
 * @author chm
 */
public class IndexCreationListener {
    
    private static Logger logger = Logger.getLogger(IndexCreationListener.class);
    private static final Object locker = new Object();
    private static int numOfIndexes = 0;
    
//    private static boolean isInIndexCreation = false;
    
    public static void setStartIndexCreation(int size) {
        synchronized(locker) {
            numOfIndexes+=size;
        }
//        logger.debug("index creation=+"+size);
    }
    
    public static void fireEndIndexCreation(Integer numOfIndexesCreated) {
        synchronized(locker) {
            if(numOfIndexes>=0)
                numOfIndexes-=numOfIndexesCreated.intValue();
        }
        logger.debug("numOfIndexes="+numOfIndexes);
        if(numOfIndexes==0) {
            String msg = "Fin de cr�ation en arri�re plan des index";
            logger.debug(msg);
            JOptionPane.showMessageDialog(null,msg);
        }
    }
    
    public static boolean isInIndexCreation() {
        synchronized(locker) {
//            logger.debug("numOfIndexes="+numOfIndexes);
            return numOfIndexes>0;
        }
    }

}
