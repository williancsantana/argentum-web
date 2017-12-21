/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.Date;

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
    
    public static void gerarLogErro(Exception e){
        String data = SinanDateUtil.dateToString(new Date(), "dd/MM/YYYY HH:mm:ss");
        data = data.replace("/","-").replace(":", "-");
        File log = new File("log "+data+".txt");        
        try{
            PrintStream ps = new PrintStream(log);
            e.printStackTrace(ps);
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
    }
}
