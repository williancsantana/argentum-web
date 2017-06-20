/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.util;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author Taidson
 */
public class SinanProgressBarUtil extends JPanel{
    static final int MY_MINIMUM = 0;  
    static final int MY_MAXIMUM = 100;  

    public SinanProgressBarUtil() {  
    }
    public static void setValores(JProgressBar jProgressBar){
        jProgressBar.setMinimum(MY_MINIMUM);  
        jProgressBar.setMaximum(MY_MAXIMUM);  
        jProgressBar.setStringPainted(true);
    }

    public static void executeProgressBar(final JProgressBar jProgressBar, final int valor) {  

        try {  
            SwingUtilities.invokeLater(new Runnable() {  
                public void run() {  
                    jProgressBar.setValue(valor); 
                }  
            });  
            Thread.sleep(20);
            jProgressBar.repaint();
        } catch (InterruptedException e) {  
        }  
    }  
}
