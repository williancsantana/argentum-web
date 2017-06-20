/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.service;

import com.linuxense.javadbf.DBFException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author taidson.santos
 */
public class DuplicidadeProcess extends SwingWorker<Void, DuplicidadeService> {
        List<String> arquivos;
        JProgressBar jProgressBar;
        DuplicidadeService duplicidadeService;
        Map<String, String> parametros;

        public DuplicidadeProcess() {
        }

        public DuplicidadeProcess(List<String> arquivos, Map<String, String> parametros, JProgressBar jProgressBar) {
            this.arquivos = arquivos;
            this.parametros = parametros;
            this.jProgressBar = jProgressBar;
        }

        @Override
        protected Void doInBackground() throws DBFException, SQLException, ParseException {
            duplicidadeService = new DuplicidadeService();
            
            duplicidadeService.executar(parametros, arquivos, jProgressBar);
            return null;
        }
        
        
}
