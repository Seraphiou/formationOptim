/*
 * Copyright OXIANE, 98 avenue du Gal Leclerc, 92100 Boulogne. Tous droits réserves.
 * Ce produit ou document est protege par un copyright et distribue avec des licences
 * qui en restreignent l'utilisation, la copie, la distribution, et la decompilation.
 * Ce produit peut etre reproduit par les stagiaires des formations dispensees par
 * OXIANE.
 * OXIANE, le logo OXIANE sont des marques de fabrique ou des marques deposees, ou
 * marques de service, de OXIANE en France et dans d'autres pays.
 * CETTE PUBLICATION EST FOURNIE "EN L'ETAT" ET AUCUNE GARANTIE, EXPRESSE OU IMPLICITE,
 * N'EST ACCORDEE, Y COMPRIS DES GARANTIES CONCERNANT LA VALEUR MARCHANDE, L'APTITUDE
 * DE LA PUBLICATION A REPONDRE A UNE UTILISATION PARTICULIERE, OU LE FAIT QU'ELLE NE
 * SOIT PAS CONTREFAISANTE DE PRODUIT DE TIERS. CE DENI DE GARANTIE NE S'APPLIQUERAIT
 * PAS, DANS LA MESURE OU IL SERAIT TENU JURIDIQUEMENT NUL ET NON AVENU.
 */ 
package com.oxiane.ja.opt;

import java.lang.instrument.Instrumentation;

/**
 *
 * @author cmarchand
 */
public class CLAgent {
    private static Instrumentation instr;

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        CLAgent.instr = instrumentation;
    }
    
    public static Class[] getLoadedClass() {
        return instr.getAllLoadedClasses();
    }
    
    public static long getLoadedClassCount() {
        return instr.getAllLoadedClasses().length;
    }
    
    public static long sizeof(Object o) {
        return instr.getObjectSize(o);
    }
}
