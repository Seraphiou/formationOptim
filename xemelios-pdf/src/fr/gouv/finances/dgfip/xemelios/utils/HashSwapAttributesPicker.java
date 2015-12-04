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


/**
 * 
 * On veut stocker dans une table (temporaire) des objets de classes plus ou moins inconnue,
 * mais dont on sait qu'ils contiennent des champs 
 * et un constructeur sans parametre 
 * On utilise reflection pour identifier ces champs; il faut en principe qu'ils soient public
 * et de type primitif.
 * On peut utiliser des annotations pour preciser la longueur maximum des String
 * 
 * 
 */
package fr.gouv.finances.dgfip.xemelios.utils;

import java.lang.reflect.*;
import java.util.Vector;


class HashSwapAttributesPicker<T> {
  
    /**
     * C'est la classe T elle meme; mais je ne sais pas la deduire de <T>
     * elle est donc passee en parametre au constructeur
     */
    Class tClass;
    
    /**
     *  
     */
    Vector<java.lang.reflect.Field> allFields;

 
    /** 
     * Constructeur
     * @param Class<T> ct ( a l'appel, passer T.class 
     * @example  hsap = new  HashSwapAttributesPicker<MyClass>(MyClaas.class)
     * 
     */
    HashSwapAttributesPicker(  Class<T> ct) throws Exception{
        tClass = ct;
        Class c = ct;
        allFields = new Vector<Field>();
        while(c!=null && c != Object.class) {
            Field list[] = c.getDeclaredFields();
            AccessibleObject.setAccessible(list, true);

            for (int i = 0; i < list.length; i++) {
                Field fld =  list[i];
                String typeName = fld.getType().toString();
                if(acceptableType(typeName)) {
                    allFields.add(fld);
                }
            }
            c = c.getSuperclass();
        }
    }
    
 
    // Utilitaire filtre les types acceptes
    boolean acceptableType(String typeName) {
        if(  typeName.equals( "class java.lang.String" )
             || typeName.equals("int")
             || typeName.equals("class java.lang.Integer" )
             || typeName.equals("float")
             || typeName.equals("class java.lang.Float" )
             || typeName.equals("double")
             || typeName.equals("class java.lang.Double" )
             ||  typeName.equals("class java.util.Date" )) {
            return true;
        }
        else
            return false;


       }

//    public void dumpFields()
//    {
//        if(allFields != null) {
//            for (int i=0; i<allFields.size(); i++) {
//                Field fld  = allFields.get(i);
//                System.out.println("Attribute name: " + fld.getName());
//                System.out.println("Data type: " + fld.getType());
//                int mod = fld.getModifiers();
//                System.out.println("Modifiers: " +Modifier.toString(mod));
//                System.out.println("======================================");
//
//            }
//        }
//    }



    public Object[] getAttributesValues( T obj) throws IllegalAccessException {
        if(allFields != null   && obj != null) {
            Object  ret[] = new Object[allFields.size()];
                                 
            for (int i=0; i<allFields.size(); i++) {
                Field fld  = allFields.get(i);
                ret[i ] = fld.get(obj);

            }
            return ret;
        }
        return null;
    }


    public void dumpFieldsOfObject (T obj) throws IllegalAccessException {
        Object[] values = getAttributesValues(  obj);
        if(values != null) {
            for (int i=0; i<allFields.size(); i++) {
                Field fld  = allFields.get(i);
                System.out.println(fld.getType() + " " + fld.getName() + ":" + values[i].toString());
            }
        }
    }

    public T fillFieldsOfObject (T  obj, Object[] values) throws IllegalAccessException {
        if(values != null  && obj != null && allFields != null ) {
       
            for (int i=0; i<allFields.size(); i++) {

                Field fld  = allFields.get(i);
                fld.set(obj, values[i]);
            }
            return  obj;
        } else {
            System.out.println(" fillFieldsOfObject NOT OK ");
            return null;
        }
    }


    public T instantiateWithValues( Object[] values) throws IllegalAccessException,
                                                            InstantiationException{

      System.out.println("instantiateWithValues ...");
      try {
         T obj  = (T) (tClass.newInstance());
     
          return fillFieldsOfObject( obj, values);

      } catch(Exception e ) {
          e.printStackTrace();
          return null;
      }
    }



   //  public static void main(String[] args) {
 
//         try {
                 
//             HashSwapAttributesPicker<MyClass> picker = new HashSwapAttributesPicker<MyClass>( MyClass.class );
//             picker.dumpFields();
//             MyClass toto = new MyClass( 58, "Laurent Meckert", "Axyus", 999, 1234.56, "20080211");
//             picker.dumpFieldsOfObject(toto);

//             Object newValues[] = { 12, "lolo", "SuYXa", 676767};
//             picker.fillFieldsOfObject(toto,newValues);
//             picker.dumpFieldsOfObject(toto);


//         }	catch (Throwable e) {
//             System.err.println(e);
//         }
//     }
}

