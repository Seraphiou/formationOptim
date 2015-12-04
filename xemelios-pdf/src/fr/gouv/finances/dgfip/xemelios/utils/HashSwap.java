
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
 *  EN TRAVAUX
 *  
 *  besoin initial = objet rendant en gros les services de HashTable mais pouvant grossir sans prendre trop de place
 *  en memoire;
 * 
 * 
 ) Par rapport a la base de donnees 
 Il ne faut pas taper directement dans du code de base de donnees mais passer par les classes d'interfacec DataImpl
 (xemeiios-core/data/impl/... 
 et creer ce qu'il faut dans mysql-persistence


 2) Aspect concurrence
 Doit fonctionner en multi utilisateur mais aussi plusieurs instances pour un meme utilisateur
 Verifier par consequent si les tables temporaires de MySql 5.0.19 sont OK pour cet usage 


 3) Interface 
 A priori la cle sera String
 et la valeur une classe  avec attributs de divers types class T { public int i;  public float j ; public String k; etc ... }
 donc genre MaBigHashTable<String, T>

 et on appelle des 
 init( <T> )
 end( )
 put(String key, <T> value)
 <T> get(String key)
 putAll( HashTable<String, T> ht)

 J'aurais rêvé d' implementer toutes les methodes de HashTable mais c'est difficiles notamment  avec les
 collections dérivées qui ont besoin d'iterators speciaux ; en fait il faudrait pouvoir se promener dans 
 un ResultSet etc.. genre DataTable de .NET (pas Swing)  mais on va se limiter sagement. 


 
 *
 */
import java.util.Map;




/**
 * 
 *  objet rendant en gros les services de HashTable mais pouvant grossir sans prendre trop de place
 *  en memoire;
 * 
 *
 */
public class HashSwap< T> {
    /**
     * objet realisant le stockage. Ce peut etre  HTHashSwapEngine<T> pour implementation 
     * simple sur une vraie Hashtable ou DBHashSwapEngine<T> pour implementation sur la base de donnee
     */
    private HashSwapEngine<T>  engine;


    /**
     * Constructeur sans parametre. Uniquement pour mises au point de l'interface et comparaison
     * de performances (utilise l'implementation par Hashtable)
     */
    public  HashSwap( ) {
        engine = new HTHashSwapEngine<T>();
    }
    
    /**
     * Constructeur  standard (utilise l'implementation par la base de donnee)
     * La classe tclass doit etre passee en parametre elle n'es helas pas deductible du type T
     *  usage = HashSwap<MaClasse> h  = new HashSwap<MaClasse>(MaClasse.class)
     */
    public     HashSwap(Class<T> tclass ) {
        try {
   
            engine = new DBHashSwapEngine<T>(tclass);
            // test     engine = new HTHashSwapEngine<T>();
    
        }catch (Exception e ) {
            e.printStackTrace();
        }
    }
    
    /**
     * stocke un objet de type T a l'index key
     * @param  String  key la cle
     * @param T  value l'objet a stocker
     */
    public  void put(String key, T value)    throws  HashSwapEngineException
    {
        engine.put(key, value);
    }

  
    /**
     *  recupere un objet de type T a l'index key
     * @param  String  key la cle
     * @return  T  l'objet cherche ou null si pas trouve
     */
    public      T get(String key) throws HashSwapEngineException
    {
        return (T)(engine.get(key)); 
    }
    
    /**
     * Stocke le  contenu de ht 
     * @param Map<String,T> ht  
     *
     */
    public      void putAll( Map<String,T> ht)throws HashSwapEngineException {
        for (String key : ht.keySet()) {
            put( key, ht.get(key)) ;
        }
    }
 
    /**
     * @param String key = la cle a rechercher
     * @return boolean true si trouve, false sinon
     */
    public      boolean containsKey(String key)  throws  HashSwapEngineException
    {
        return engine.containsKey( key);
    }

    /**
     * @param String key = la cle a detuire
     * @return T objet se trouvant a cette cle, null si rien
     */
    public      T  remove(String key) throws  HashSwapEngineException
    {
        return (T)(engine.remove( key));

    }


    /**
     * @return int nombre d'objets stockes
     */
    public int size() throws  HashSwapEngineException
    {
        return engine.size();
    }

    /**
     * A Appeler apres usage, il faut mieux detruire les tables meme si elles
     * sont temporaires pour continuer a executer Xemelios
     *
     */
    public void destruct() throws  HashSwapEngineException
    {
        engine.destruct();
    }

    /**
     * petit main() pour test unitaires
     */
    public static void main( String args[]) {
        HashSwap< Integer>  moi = new HashSwap<Integer> ();

        try{
            moi.put( "1", 1);
            moi.put( "2", 2);

  
            HashSwap<String>  moi2 = new HashSwap<String> ();
            Map<String, String> variables = System.getenv();
            moi2.putAll(variables);

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}


