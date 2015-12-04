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
class DBHashSwapEngine<T> extends HashSwapEngine<T> {

    HashSwapPersister<T> persister;

    DBHashSwapEngine(Class<T> tclass) {
        try {
            HashSwapAttributesPicker<T> picker = new HashSwapAttributesPicker<T>(tclass);
            persister = new HashSwapPersister<T>(picker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    void put(String key, T value) throws HashSwapEngineException {

        persister.storeAt(key, value);
    }

    @Override
    int size() throws HashSwapEngineException {
        int ret = -1;
        try {
            ret = persister.size();
        } catch (ClassCastException e) {
            throw (new HashSwapEngineException(e, HashSwapEngineException.ERROR_CLASSCASTEXCEPTION));
        } catch (NullPointerException e) {
            throw (new HashSwapEngineException(e, HashSwapEngineException.ERROR_NULLPOINTEREXCEPTION));
        } finally {
            return ret;
        }
    }

    @Override
    T get(String key) throws HashSwapEngineException {
        T ret = null;
        try {
            ret = persister.readFromStorage(key);
        } catch (ClassCastException e) {
            throw (new HashSwapEngineException(e, HashSwapEngineException.ERROR_CLASSCASTEXCEPTION));
        } catch (NullPointerException e) {
            throw (new HashSwapEngineException(e, HashSwapEngineException.ERROR_NULLPOINTEREXCEPTION));
        } finally {
            return ret;
        }
    }

    @Override
    T getFilled(String key, T objToFill) throws HashSwapEngineException {
        T ret = null;
        try {
            ret = persister.fillFromStorage(key, objToFill);
        } catch (ClassCastException e) {
            throw (new HashSwapEngineException(e, HashSwapEngineException.ERROR_CLASSCASTEXCEPTION));
        } catch (NullPointerException e) {
            throw (new HashSwapEngineException(e, HashSwapEngineException.ERROR_NULLPOINTEREXCEPTION));
        } finally {
            return ret;
        }
    }

    @Override
    boolean containsKey(String key) throws HashSwapEngineException {
        boolean ret = false;
        try {
            ret = persister.exists(key);
        } catch (ClassCastException e) {
            throw (new HashSwapEngineException(e, HashSwapEngineException.ERROR_CLASSCASTEXCEPTION));
        } catch (NullPointerException e) {
            throw (new HashSwapEngineException(e, HashSwapEngineException.ERROR_NULLPOINTEREXCEPTION));
        } finally {
            return ret;
        }
    }

    @Override
    T remove(String key) throws HashSwapEngineException {
        T ret = null;
        try {
            ret = persister.readFromStorage(key);
            if (ret != null) {
                persister.delete(key);
            }
        } catch (ClassCastException e) {
            throw (new HashSwapEngineException(e, HashSwapEngineException.ERROR_CLASSCASTEXCEPTION));
        } catch (NullPointerException e) {
            throw (new HashSwapEngineException(e, HashSwapEngineException.ERROR_NULLPOINTEREXCEPTION));
        } catch (UnsupportedOperationException e) {
            throw (new HashSwapEngineException(e, HashSwapEngineException.ERROR_UNSUPPORTEDOPERATIONEXCEPTION));
        } finally {
            return ret;
        }
    }

    @Override
    void destruct() throws HashSwapEngineException {
        try {
            if(persister!=null) persister.destruct();
            persister = null;
        } catch (Exception e) {
            throw (new HashSwapEngineException(e, HashSwapEngineException.ERROR_GENERICEXCEPTION));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        destruct();
        super.finalize();
    }
    
}
