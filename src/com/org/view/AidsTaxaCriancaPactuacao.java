/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AidsIncidenciaCrianca.java
 *
 * Created on 28/05/2010, 10:35:08
 */
package com.org.view;

import com.org.facade.SessionFacadeImpl;
import com.org.model.classes.Agravo;
import com.org.negocio.Configuracao;
import com.org.negocio.FiltroArquivo;
import com.org.util.SinanDateUtil;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;

/**
 *
 * @author geraldo
 */
public class AidsTaxaCriancaPactuacao extends DengueLetalidade {

    SessionFacadeImpl session = new SessionFacadeImpl();

    /** Creates new form AidsIncidenciaCrianca */
    public AidsTaxaCriancaPactuacao() {
        initComponents();
        ComboBoxModel modelo;
        this.session.setBrasil(true);
        this.session.setTodosMunicipios(true);
        modelo = new DefaultComboBoxModel(this.session.retornaUFs());
        this.cbUf.setModel(modelo);
        if (SessionFacadeImpl.isDbf()) {
            pnlArquivos.setVisible(true);
        } else {
            pnlArquivos.setVisible(false);
        }
    }

    private boolean preencheuFormulario() {
        if (cbUf.getSelectedItem().toString().equals("-- Selecione --")) {
            Master.mensagem("Selecione a UF de residência");
            return false;
        }
        if (SinanDateUtil.dateToStringException(dataInicio.getDate(), "dd/MM/yyyy").equals("")) {
            Master.mensagem("Informe o período inicial");
            return false;
        }
        if (SinanDateUtil.dateToStringException(dataFim.getDate(), "dd/MM/yyyy").equals("")) {
            Master.mensagem("Informe o período final");
            return false;
        }
        if (Integer.parseInt(SinanDateUtil.dateToStringException(dataInicio.getDate(), "dd/MM/yyyy").substring(6, 10)) < 2007) {
            Master.mensagem("Não é possível utilizar esse relatório para datas anteriores a 2007");
            return false;
        }
        try {
            Date dtInicio = Agravo.converterParaData(SinanDateUtil.dateToStringException(dataInicio.getDate(), "dd/MM/yyyy"));
            Date dtFim = Agravo.converterParaData(SinanDateUtil.dateToStringException(dataFim.getDate(), "dd/MM/yyyy"));
            if (dtFim.before(dtInicio)) {
                Master.mensagem("Data final deve ser maior que inicial");
                return false;
            }
        } catch (Exception e) {
            Master.mensagem("Data inválida");
            return false;
        }
        if(lblArquivosSelecionados.getText().equals("Nenhum arquivo selecionado")&& SessionFacadeImpl.isDbf()){
            Master.mensagem("Selecione um arquivo.");
            return false;
        }


        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cbRegional = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cbUf = new javax.swing.JComboBox();
        chkExportarDbf = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        cbMunicipio = new javax.swing.JComboBox();
        lblUF = new javax.swing.JLabel();
        jpDataPrimeiroSintomas = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblAviso = new javax.swing.JLabel();
        dataInicio = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        dataFim = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        prbStatus = new javax.swing.JProgressBar();
        btCalcular = new javax.swing.JButton();
        btLimpar = new javax.swing.JButton();
        pnlArquivos = new javax.swing.JPanel();
        btnSelecionarArquivos = new javax.swing.JButton();
        btnLimparSelecao = new javax.swing.JButton();
        lblArquivosSelecionados = new javax.swing.JLabel();

        cbRegional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRegionalActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Município de Residência:"); // NOI18N

        cbUf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbUfActionPerformed(evt);
            }
        });

        chkExportarDbf.setText("Salvar resultado em DBF");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Regional de Residência:"); // NOI18N

        cbMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbMunicipioActionPerformed(evt);
            }
        });

        lblUF.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUF.setText("UF de Residência:"); // NOI18N

        jpDataPrimeiroSintomas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data de Diagnóstico\n", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel7.setText("De"); // NOI18N

        jLabel8.setText("até"); // NOI18N

        lblAviso.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAviso.setForeground(new java.awt.Color(255, 0, 0));

        dataInicio.getJCalendar().setWeekOfYearVisible(false);

        dataFim.getJCalendar().setWeekOfYearVisible(false);

        javax.swing.GroupLayout jpDataPrimeiroSintomasLayout = new javax.swing.GroupLayout(jpDataPrimeiroSintomas);
        jpDataPrimeiroSintomas.setLayout(jpDataPrimeiroSintomasLayout);
        jpDataPrimeiroSintomasLayout.setHorizontalGroup(
            jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDataPrimeiroSintomasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDataPrimeiroSintomasLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataFim, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblAviso))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jpDataPrimeiroSintomasLayout.setVerticalGroup(
            jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpDataPrimeiroSintomasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dataFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel8)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAviso))
        );

        btCalcular.setLabel("Calcular");
        btCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCalcularActionPerformed(evt);
            }
        });

        btLimpar.setText("Limpar");
        btLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLimparActionPerformed(evt);
            }
        });

        pnlArquivos.setBorder(javax.swing.BorderFactory.createTitledBorder("Selecione os DBF "));

        btnSelecionarArquivos.setText("Selecionar arquivos");
        btnSelecionarArquivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarArquivosActionPerformed(evt);
            }
        });

        btnLimparSelecao.setText("Limpar Seleção");
        btnLimparSelecao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparSelecaoActionPerformed(evt);
            }
        });

        lblArquivosSelecionados.setText("Nenhum arquivo selecionado");
        lblArquivosSelecionados.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblArquivosSelecionados.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout pnlArquivosLayout = new javax.swing.GroupLayout(pnlArquivos);
        pnlArquivos.setLayout(pnlArquivosLayout);
        pnlArquivosLayout.setHorizontalGroup(
            pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 414, Short.MAX_VALUE)
            .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlArquivosLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblArquivosSelecionados, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                        .addGroup(pnlArquivosLayout.createSequentialGroup()
                            .addComponent(btnSelecionarArquivos)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnLimparSelecao)
                            .addContainerGap(166, Short.MAX_VALUE)))))
        );
        pnlArquivosLayout.setVerticalGroup(
            pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 61, Short.MAX_VALUE)
            .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlArquivosLayout.createSequentialGroup()
                    .addGap(1, 1, 1)
                    .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSelecionarArquivos)
                        .addComponent(btnLimparSelecao))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lblArquivosSelecionados)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblUF)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbMunicipio, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbRegional, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbUf, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkExportarDbf)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addComponent(btCalcular)
                        .addGap(26, 26, 26)
                        .addComponent(btLimpar)))
                .addContainerGap(44, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(prbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addGap(21, 21, 21))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpDataPrimeiroSintomas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlArquivos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUF)
                    .addComponent(cbUf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbRegional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkExportarDbf)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpDataPrimeiroSintomas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlArquivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCalcular, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btLimpar)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbRegionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRegionalActionPerformed
        ComboBoxModel modelo;
        modelo = new DefaultComboBoxModel(this.session.retornaMunicipios(this.cbUf.getSelectedItem().toString(), this.cbRegional.getSelectedItem().toString()));
        this.cbMunicipio.setModel(modelo);
}//GEN-LAST:event_cbRegionalActionPerformed

    private void cbUfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbUfActionPerformed
        ComboBoxModel modelo;
        modelo = new DefaultComboBoxModel(this.session.retornaRegionais(this.cbUf.getSelectedItem().toString()));
        this.cbRegional.setModel(modelo);
        modelo = new DefaultComboBoxModel(this.session.retornaMunicipios(this.cbUf.getSelectedItem().toString()));
        this.cbMunicipio.setModel(modelo);
}//GEN-LAST:event_cbUfActionPerformed

    private void cbMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMunicipioActionPerformed
}//GEN-LAST:event_cbMunicipioActionPerformed

    private void btCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCalcularActionPerformed
        if (!this.preencheuFormulario()) {
            return;
        }
        session = new SessionFacadeImpl();
        session.setBrasil(true);
        SessionFacadeImpl.setNomeDbf("AIDSC");
        session.setTodosMunicipios(true);
//        if (SessionFacadeImpl.isDbf()) {
//            if (!Master.escolherDBF()) //gerar o relatorio
//            {
//                return;
//            }
//        }
        //verifica se vai exportar para dbf o resultado
        if (chkExportarDbf.isSelected()) {
            session.setExportarDbf(true);
            //abrir janela para definir o nome do arquivo para exportação
            //            if (!Master.setNomeArquivoDBF()) {
            //                return;
            //            }
        } else {
            session.setExportarDbf(false);
        }
        this.prbStatus.setStringPainted(true);
        this.prbStatus.setValue(0);
        //passa as datas selecionadas
        Map parametros = new HashMap();
        parametros.put("parArquivos", this.lblArquivosSelecionados.getText());
        parametros.put("parVariosArquivos", "sim");
        session.setParametros(parametros);
        session.setDataFim(SinanDateUtil.dateToStringException(dataFim.getDate(), "dd/MM/yyyy"));
        session.setDataInicio(SinanDateUtil.dateToStringException(dataInicio.getDate(), "dd/MM/yyyy"));
        session.setJprogress(prbStatus);
        session.setMunicipio(cbMunicipio.getSelectedItem().toString());
        session.setRegional(cbRegional.getSelectedItem().toString());
        session.setUf(cbUf.getSelectedItem().toString());
        session.setRelatorio("AidsTaxaCriancaPactuacao");
        session.execute();
}//GEN-LAST:event_btCalcularActionPerformed

    private void btLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLimparActionPerformed
        cbMunicipio.removeAllItems();
        cbRegional.removeAllItems();
        cbUf.setSelectedIndex(0);
}//GEN-LAST:event_btLimparActionPerformed

    private void btnSelecionarArquivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarArquivosActionPerformed
        JFileChooser fileopen = new JFileChooser();
        fileopen.setMultiSelectionEnabled(true);
        //        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivo DBF", "dbf");
        FiltroArquivo filtro = new FiltroArquivo();
        filtro.addExtension("dbf");
        filtro.setDescription("Arquivo DBF");
        filtro.addInicioNome("AIDSC");

        //        fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileopen.addChoosableFileFilter(filtro);

        File file2 = new File(new Configuracao().getCaminho());
        fileopen.setCurrentDirectory(file2);
        int ret = fileopen.showDialog(null, "Abrir DBF");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File[] files = fileopen.getSelectedFiles();
            if (lblArquivosSelecionados.getText().equals("Nenhum arquivo selecionado")) {
                lblArquivosSelecionados.setText("");
            }
            for (int i = 0; i < files.length; i++) {
                //verifica se ja está selecionado
                if (lblArquivosSelecionados.getText().lastIndexOf(files[i].getName()) == -1) {
                    lblArquivosSelecionados.setText(lblArquivosSelecionados.getText() + files[i].getName() + "||");
                }
                Configuracao.setPropriedade("caminho", files[i].getParent() + "\\\\");
            }

        }
       
}//GEN-LAST:event_btnSelecionarArquivosActionPerformed

    private void btnLimparSelecaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparSelecaoActionPerformed
        lblArquivosSelecionados.setText("Nenhum arquivo selecionado");
}//GEN-LAST:event_btnLimparSelecaoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCalcular;
    private javax.swing.JButton btLimpar;
    private javax.swing.JButton btnLimparSelecao;
    private javax.swing.JButton btnSelecionarArquivos;
    private javax.swing.JComboBox cbMunicipio;
    private javax.swing.JComboBox cbRegional;
    private javax.swing.JComboBox cbUf;
    private javax.swing.JCheckBox chkExportarDbf;
    private com.toedter.calendar.JDateChooser dataFim;
    private com.toedter.calendar.JDateChooser dataInicio;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jpDataPrimeiroSintomas;
    private javax.swing.JLabel lblArquivosSelecionados;
    private javax.swing.JLabel lblAviso;
    private javax.swing.JLabel lblUF;
    private javax.swing.JPanel pnlArquivos;
    private javax.swing.JProgressBar prbStatus;
    // End of variables declaration//GEN-END:variables
}
