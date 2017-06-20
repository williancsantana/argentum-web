/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.util;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * @author taidson.santos
 */
public class FileFilterUtils extends FileFilter{

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
 
        String extension = ArquivoUtils.getExtension(f);
        if (extension != null) {
            if (extension.equals(ArquivoUtils.dbf)){
                    return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Arquivo DBF (.dbf)";
    }
 
    
}
