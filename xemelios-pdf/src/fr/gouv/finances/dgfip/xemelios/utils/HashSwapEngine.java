/*
 * Copyright 
 *   2008 axYus - www.axyus.com
 *   2008 L. Meckert - laurent.meckert@axyus.com
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


package fr.gouv.finances.dgfip.xemelios.utils;

/**
 * classe pour stocker les donnees d'un HashSwap
 *
 */

abstract class HashSwapEngine<T> {

    abstract  void put(String key, T value)  throws HashSwapEngineException  ;
    abstract  int size() throws  HashSwapEngineException;
    abstract  T get(String key)  throws HashSwapEngineException ;
    abstract  T getFilled(String key, T objToFill)  throws HashSwapEngineException ;
    abstract  boolean containsKey(String key)  throws HashSwapEngineException ;
    abstract T  remove(String key) throws HashSwapEngineException ;
    abstract void destruct () throws HashSwapEngineException ;
    


}
