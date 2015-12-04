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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author cmarchand
 */
public class Window extends JFrame {
    
    public Window() {
        super("Test");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JButton pbClose = new JButton("Rechercher des bouteilles");
        JButton pbOrder = new JButton("Commander");
        JButton pbBottle = new JButton("Créer de nouvelles bouteilles");
        pbClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        add(pbClose);
        pbBottle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        add(pbBottle);
        pbOrder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        pbOrder.setLocation((int)pbClose.getSize().getWidth(), 0);
        pbBottle.setLocation((int)pbClose.getSize().getWidth()+(int)pbOrder.getSize().getWidth(), 0);
        add(pbOrder);
        pack();
        setVisible(true);
    }
}
