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

import java.util.Hashtable;  // juste pour test interface initial

import java.util.Map;
import java.util.Set;

/**
 * classe pour stocker les donnees d'un HashSwap
 * version pour mise au point inerface des methodes + tentative optimisation si nb items ridicule
 */

class HTHashSwapEngine<T>  extends HashSwapEngine<T> {

    private java.util.Hashtable<String ,T> ht; 
    
    HTHashSwapEngine( )  {
        ht = new java.util.Hashtable<String ,T>();
    }
    void put(String key, T value)  throws HashSwapEngineException {
         try {
             ht.put(key, value);
         } catch(UnsupportedOperationException e) {
             throw(new HashSwapEngineException(e, HashSwapEngineException.ERROR_UNSUPPORTEDOPERATIONEXCEPTION));	
         } catch(ClassCastException e) {
             throw(new HashSwapEngineException(e, HashSwapEngineException.ERROR_CLASSCASTEXCEPTION));	
         } catch(IllegalArgumentException e) {
             throw(new HashSwapEngineException(e, HashSwapEngineException.ERROR_ILLEGALARGUMENTEXCEPTION));	
         } catch(NullPointerException e) {
             throw(new HashSwapEngineException(e, HashSwapEngineException.ERROR_NULLPOINTEREXCEPTION));	
         }
     }
         
     int size() {
         return ht.size();
     }
     T get(String key)  throws HashSwapEngineException {
         T ret = null;
         try{
             ret =  ht.get(key); 
         }catch(ClassCastException e) {
             throw(new HashSwapEngineException(e, HashSwapEngineException.ERROR_CLASSCASTEXCEPTION));	
         } catch(NullPointerException e) {
             throw(new HashSwapEngineException(e, HashSwapEngineException.ERROR_NULLPOINTEREXCEPTION));	
         }
         finally{
             return ret;
         }
     }


    T getFilled(String key, T objToFill)  throws HashSwapEngineException {
        return get(key); // NOP dans ce cas
    }


     Set<String> keySet() {
         Set<String> ret = null;
         try {
             ret=   ht.keySet();
         } catch(ClassCastException e) {
             throw(new HashSwapEngineException(e, HashSwapEngineException.ERROR_CLASSCASTEXCEPTION));	
         } 
         finally{
             return ret;
         }
     };

     boolean containsKey(String key)  throws HashSwapEngineException {
         boolean ret = false ;
         try{
             ret =  ht.containsKey(key); 
         }catch(ClassCastException e) {
             throw(new HashSwapEngineException(e, HashSwapEngineException.ERROR_CLASSCASTEXCEPTION));	
         } catch(NullPointerException e) {
             throw(new HashSwapEngineException(e, HashSwapEngineException.ERROR_NULLPOINTEREXCEPTION));	
         }
         finally{
             return ret;
         }

     }
     T  remove(String key) throws HashSwapEngineException{
         T ret = null;
         try{
             ret =  ht.remove(key); 
         }catch(ClassCastException e) {
             throw(new HashSwapEngineException(e, HashSwapEngineException.ERROR_CLASSCASTEXCEPTION));	
         } catch(NullPointerException e) {
             throw(new HashSwapEngineException(e, HashSwapEngineException.ERROR_NULLPOINTEREXCEPTION));	
         } catch(UnsupportedOperationException e) {
             throw(new HashSwapEngineException(e, HashSwapEngineException.ERROR_UNSUPPORTEDOPERATIONEXCEPTION));	
         } 
         finally{
             return ret;
         }  
     } 

  void destruct() throws HashSwapEngineException{
  }

    // more ...


}
