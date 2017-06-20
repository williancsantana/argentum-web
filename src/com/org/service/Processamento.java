/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.service;

import com.linuxense.javadbf.DBFException;
import com.org.beans.Completitude;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 *
 * @author taidson.santos
 */
public class Processamento extends SwingWorker<Void, com.org.view.Completitude> {
        List<String> arquivos;
        JProgressBar jProgressBar;
        CompletitudeService completitudeService;
        JCheckBox checkBox;
        JCheckBox checkBox2;
        JTable jTable;
        Map<String, String> parametros;

        public Processamento() {
        }

        public Processamento(List<String> arquivos, Map<String, String> parametros, JProgressBar jProgressBar, JCheckBox checkBox, JTable jTable, JCheckBox checkBox2) {
            this.arquivos = arquivos;
            this.parametros = parametros;
            this.jProgressBar = jProgressBar;
            this.checkBox = checkBox;
            this.checkBox2 = checkBox2;
            this.jTable = jTable;
        }

        @Override
        protected Void doInBackground() throws DBFException, SQLException, ParseException {
            completitudeService = new CompletitudeService();
            
            completitudeService.executar(parametros, arquivos, jProgressBar, checkBox, jTable, checkBox2);
            return null;
        }
}
