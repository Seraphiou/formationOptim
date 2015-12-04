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
package com.oxiane.formation.jaopt.pdf.utils;

import java.io.File;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;

/**
 *
 * @author cmarchand
 */
public class CustomUserAgentCallback extends ITextUserAgent {

    public CustomUserAgentCallback(ITextOutputDevice outputDevice) {
        super(outputDevice);
    }

    @Override
    public String resolveURI(String uri) {
        if (uri.startsWith("xemelios:")) {
            String servlet = null;
            String sTmp = uri;
            int start = uri.indexOf('?');
            if (start >= 0) {
                servlet = uri.substring(uri.indexOf("/") + 1, start);
                sTmp = uri.substring(start + 1);
            }
            if ("resource".equals(servlet)) {
                String ret = null;
                try {
                    File resourceDir = new File(System.getProperty("xemelios.resources.location"));
                    File res = new File(resourceDir, sTmp);
                    ret = res.toURI().toURL().toExternalForm();
                } catch (Throwable t) {
                }
                return ret;
            } else {
                return super.resolveURI(uri);
            }
        } else {
            return super.resolveURI(uri);
        }
    }
}
