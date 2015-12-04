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
package com.oxiane.ja.opt.jmeterstress.web;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author cmarchand
 */
public class PerfServlet extends HttpServlet {
    private Random rand = new Random();

    @Override
    public void init() throws ServletException {
        super.init();
        log("PerfServlet.init");
    }

    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        int limit = Math.abs(getNext());
        long start = System.nanoTime();
        long value = 0l;
        for(int i=0; i< limit; i++) {
            value+=i;
        }
        sb.append("<html><head><title>Performance Stressed System</title></head><body><p>Calculer la somme des entiers de 1 à ").append(limit).append(" a pris ").append((System.nanoTime()-start)/1000.0).append(" milli-secondes.</p></body></html>");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setHeader("Content-Type", "text/html; charset=utf-8");
        resp.getOutputStream().write(sb.toString().getBytes(Charset.forName("UTF-8")));
        resp.getOutputStream().flush();
        log("requesting "+limit);
    }
    
    private synchronized int getNext() {
        try {
            Thread.sleep(100);
        } catch(Exception ex) {}
        return rand.nextInt();
    }
    
    
    
}
