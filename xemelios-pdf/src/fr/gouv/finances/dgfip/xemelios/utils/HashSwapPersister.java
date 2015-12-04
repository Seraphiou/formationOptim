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



import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;
import fr.gouv.finances.dgfip.xemelios.data.DataLayerManager;
import fr.gouv.finances.dgfip.xemelios.data.DataImpl;

/**
 * 
 *
 *
 */
class HashSwapPersister<T> { 
    private HashSwapAttributesPicker<T> picker;
    private String storageId;
    private String keyId;

    private Boolean storageCreated = false;

    /**
     * Implementation de base de donnees a laquelle on s'adresse
     */
    DataImpl myDataImpl;

    /**
     * structure opaque obtenue et renvoyee  a myDataImpl
     *
     */
    private Object persistHandle;

    /**
     * Constructeur
     * @param HashSwapAttributesPicker<T> p   : un objet HashSwapAttributesPicker 
     * charge d'analyser er de remplir les
     * champs de l'objet de type T
     * 
     */
    HashSwapPersister(HashSwapAttributesPicker<T> p) {
        picker =  p;
        init();
    }
    
  
    /**
     *  Initialisations 
     *   Cree un nom de table unique
     *   Cree un nom de cle unique dans cette table
     *   Recupere l'attribut "persistHandle" aupres de myDataImpl
     *  
     */
    void init( ) {
        int lower = 1;
        int higher = 1000;

        Integer random = (int)(Math.random() * (higher-lower)) + lower;

        Long now = System.currentTimeMillis();
        storageId = "hsw_" + now.toString() + "_" + random.toString();
        keyId = storageId + "_k";
        try {
            myDataImpl= DataLayerManager.getImplementation();
        } catch ( DataConfigurationException e) {
            e.printStackTrace();
            return;
        }
        persistHandle =  myDataImpl.HSWinitTempStorage( storageId,  keyId, 100 , picker.allFields ) ;

    }

    /**
     * Utilitaire ; verifie que la table a bien ete creee
     * (on ne la cree qu'au dernier moment si on en a vraiment besoin)
     */
    private void ensureStorageCreated() {
        if(!storageCreated) {
           
            myDataImpl.HSWdoCreateStorage(persistHandle);

            storageCreated = true;
        }

    }

    /**
     * Stocke le contenu de l'objet value avec k pour cle
     *
     * @param String k = la cle
     * @param T value  = objet a stocker
     *
     */
    void storeAt( String k, T value) {
        
        ensureStorageCreated() ;
     
        Object[] attValues;
   
        try {
            attValues = picker.getAttributesValues( value);
        }catch(Exception e) {

            e.printStackTrace();
            return;
        }
        myDataImpl.HSWdoInsert(persistHandle, k,  attValues );
      
    
    }



    /**
     * Verifie l'existence de cette cle
     * @param String key la cle
     * @return boolean true si trouve false sinon
     */
    boolean exists(String key) {
        //    ensureStorageCreated() ;
        if(!storageCreated) return false;
        return myDataImpl.HSWdoExists(persistHandle, key );
    }

    /**
     * Supprime cette cle 
     * @param String key la cle
     * @return boolean true si la cle existait et a ete detruite false sinon
     */
    boolean delete(String key) {
        if(!storageCreated) return false;
        return myDataImpl.HSWdoDelete(persistHandle, key );
    }
  

    /**
     * Recupere les donn�es stockees pour la cle key
     * @param String key la cle
     * @return Object[]  un tableau d'objet ou null si pas trouve
     *
     */
    Object[] getValues(String key) {
        if(!storageCreated) return null ;
        Object[] dummyValues  = new Object[picker.allFields.size()];
        return myDataImpl.HSWdoSelect(persistHandle, key , dummyValues );
    }

    /**
     * Recupere les donn�es stockees pour la cle key
     * @param String key la cle
     * @return T un objet de classe T instancie avec pour attributs
     * les champs relus dans la base (null si rien trouve)
     */
    T readFromStorage(String key) throws Exception {
        // Recupere le resultat du select 
        Object[] values  = getValues( key);
        // Batit une nouvelle instance de T . 
        // Il faut passer par la classe qui est connue de picker
        if (values != null) {
            T result = picker.instantiateWithValues(values);
        
            return result;
        }
        else
            {
                return null;
            }
    }


    /**
     * Recupere les donn�es stockees pour la cle key
     * @param String key la cle
     * @param  T un objet de classe T
     * @return T cet objet avec pour attributs
     * les champs relus dans la base (null si rien trouve)
     */
    T fillFromStorage(String key, T objToFill) throws Exception {
        // Recupere le resultat du select 
        
        Object[] values  = getValues( key);
    
        if (values != null && objToFill != null) {
           
            return   picker.fillFieldsOfObject(objToFill, values);
        
        }
        else
            {
                return null;
            }

    }

    /**
     * @return nombre d'objets stockes 
     */
    int size() throws Exception {
        if(!storageCreated) return 0 ;
        // Recupere le resultat du select 
        return myDataImpl.HSWdoCount(persistHandle);
    }

    

    /**
     * destructeur a appeler explicitement
     * si on veut que la table temporaire ne traine pas
     * pendant la suite de l'execution de Xemelios
     * ( elle serait bien entendu detruite en sortant de Xemelios 
     *   mais c'est plus propre)
     */
    void  destruct() throws Exception {
        //
        if(storageCreated)
            myDataImpl.HSWdoDrop(persistHandle);
        storageCreated = false;
    }

    @Override
    protected void finalize() throws Throwable {
        destruct();
        super.finalize();
    }

    


}

