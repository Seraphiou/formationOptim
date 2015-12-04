/*
 * Copyright 
 *   2005 axYus - www.axyus.com
 *   2005 L.Meckert - lmeckert@club-internet.fr
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

package fr.gouv.finances.dgfip.utils;
//
// Appels de methodes statiques dans des classes
// jni donc  supposant des chargements de librairies dynamiques
// Le but est surtout de remapper les Exceptions
// Si MyClass est 
// Object myObject;
// try {
//    myObject = Jnideps.instanciate("MyClass") ;
// }catch( exception e) {
//    reagir selon exception
// }
// 
// 

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.gouv.finances.dgfip.xemelios.common.ToolException;

public class Jnideps {
    public static Object invoke(String Sclass, String Smethod, Class SmethodParams[], Object params[]) throws ToolException {
        Class classe;
        try {
            classe = Class.forName(Sclass);
        }
        catch (Exception e) {
            throw new ToolException("Impossible de charger la classe " + Sclass, e, ToolException.ERROR_JNIDEPS_BADPARAMS);
        } // bye 1
        Method classMethod;
        try {
            classMethod = classe.getDeclaredMethod(Smethod, SmethodParams);
        }
        catch (Exception e) {
            throw new ToolException("Impossible de retrouver la methode " + Smethod + " de la classe " + Sclass, e, ToolException.ERROR_JNIDEPS_BADPARAMS);
        } // bye 2
        Object result = null;
        try {
            result = classMethod.invoke(null, params);
        }
        catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (t != null) {
                if (t instanceof java.lang.SecurityException) {
                    throw new ToolException("Impossible de charger la librairie: permission non accordee", t, ToolException.ERROR_JNIDEPS_SECURITY);
                } else if (t instanceof java.lang.UnsatisfiedLinkError) {
                    throw new ToolException("Impossible de charger la librairie: fichier introuvable ou format incorrect", t, ToolException.ERROR_JNIDEPS_UNSATISFIEDLINK);
                } else throw new ToolException(t, -1);
            }
        }
        catch (Throwable e) {
            throw new ToolException(e, -1);
        }
        return result;
    }

    // Wrappers; ajouter a la demande
    public static String invoke(String Sclass, String Smethod, String param) throws ToolException {
        Object retval;
        try {
            retval = invoke(Sclass, Smethod, new Class[] { String.class }, new Object[] { param });
        }
        catch (ToolException e) {
            throw e;
        }
        if (retval == null) {
            return null;
        }
        if (retval instanceof String) {
            return (String)retval;
        } 
        throw new ToolException("la methode " + Smethod + " de la classe " + Sclass + " ne renvoie pas un objet String", ToolException.ERROR_JNIDEPS_BADPARAMS);
    }

    // Petit main de test
    public static void main(String[] argv) {
        try {
            Object oVal = invoke("RegAccess", "getRegistryEntriuiy", new Class[] { String.class }, new Object[] { "HKEY_CLASSES_ROOT\\HTTP\\SHELL\\OPEN\\COMMAND" });
            if (oVal instanceof String) {
            }
        }
        catch (ToolException e) {
            System.err.println(e);
        }
        try {
            Object oVal = invoke("RegAccess", "getRegistryEntry", new Class[] { String.class }, new Object[] { "HKEY_LOCAL_MACHINE\\SOFTWARE\\LOTUS\\COMPANY" });
            if (oVal instanceof String) {
            }
        }
        catch (ToolException e) {
            System.err.println(e);
        }
        try {
            String sVal = invoke("RegAccess", "getRegistryEntry", "HKEY_CLASSES_ROOT\\HTTP\\SHELL\\OPEN\\COMMAND");
        }
        catch (ToolException e) {
            System.err.println(e);
        }

    }
}
