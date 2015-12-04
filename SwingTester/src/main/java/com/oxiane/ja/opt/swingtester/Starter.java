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
package com.oxiane.ja.opt.swingtester;

import com.oxiane.ja.opt.CLAgent;
import java.io.InputStreamReader;

/**
 * Démarreur
 */
public class Starter {

    public static void main(String[] args) {
        System.out.println(CLAgent.getLoadedClassCount()+" classes loaded");
        waiter("Appuyez sur <ENTER> pour continuer");
        try {
            Class clazz = Class.forName("com.oxiane.ja.opt.swingtester.Window");
            Object o = clazz.newInstance();
            System.out.println(CLAgent.getLoadedClassCount()+" classes loaded and size of o "+CLAgent.sizeof(o));
        } catch(Exception ex) {}
    }
    private static void waiter(String msg) {
        System.out.print(msg);
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            char[] buffer = new char[64];
            int read = isr.read(buffer);
            boolean start=false;
            while(!start) {
                for(int i=0; i<read; i++) {
                    if(buffer[i]=='\n') {
                        start=true;
                        break;
                    }
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
