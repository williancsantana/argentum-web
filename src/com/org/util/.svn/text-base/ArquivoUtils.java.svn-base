/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.util;

import java.io.File;

/**
 *
 * @author taidson.santos
 */
public class ArquivoUtils {
    public final static String dbf = "dbf";

    /*
     * Get the extension of a file.
     */  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
