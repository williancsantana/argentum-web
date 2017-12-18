/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Oportunidade.java
 *
 * Created on 06/06/2010, 18:16:54
 */
package com.org.view;

import com.org.facade.SessionFacadeImpl;
import com.org.util.SinanDateUtil;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import com.org.facade.SessionFacadeImpl;
import com.org.model.classes.Agravo;
import com.org.negocio.Configuracao;
import com.org.negocio.FiltroArquivo;
import com.org.negocio.Util;
import com.org.util.SinanDateUtil;
import com.org.util.SinanUtil;
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
public class SemEpidPQAVS extends javax.swing.JPanel {

    SessionFacadeImpl session = new SessionFacadeImpl();

    /** Creates new form Oportunidade */
    public SemEpidPQAVS() {
        initComponents();
    //    iniciaCombo(cbAgravo);
     //   dtAvaliacaoOportunidade.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
//#        dtAvaliacaoOportunidade.setDate(SinanDateUtil.currentDate());
        iniciaCombos();
        preencheAnos(anoAvaliadoOportunidade, 2007);
    //    ComboBoxModel modelo;
    //    this.session.setBrasil(true);
        this.session.setTodosMunicipios(true);
    //    modelo = new DefaultComboBoxModel(this.session.retornaUFs());
    //    this.cbUf.setModel(modelo);
        /*cbDesagregacao.setVisible(false);
        jLabel10.setVisible(false);   */

    }

    public static void preencheAnos(JComboBox combo, int anoInicial) {
        combo.removeAllItems();
        int anoFinal = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        for (int i = anoFinal; i >= anoInicial; i--) {
            combo.addItem(i);
        }
    }

    private boolean preencheuFormulario() {
        if(cbDesagregacao.getSelectedItem().toString().equals("-- Selecione --")){
            SinanUtil.mensagem("Selecione o nível de desagregação");
            return false;
        }
        if (cbUf.getSelectedItem().toString().equals("-- Selecione --")) {
            Master.mensagem("Selecione a UF de residência");
            return false;
        }
//#        if (SinanDateUtil.dateToStringException(dtAvaliacaoOportunidade.getDate(), "dd/MM/yyyy").equals("")) {
//#            Master.mensagem("Informe a data de avaliação");
//#            return false;
//#        }
//#        if(rbPeriodoAvaliacao.isSelected()){
//#            if(dtInicioAvaliacao.getDate() == null){
//#                SinanUtil.mensagem("Informe o período de início de avaliação");
//#                return false;
//#            }else if(dtFimAvaliacao.getDate() == null){
//#                SinanUtil.mensagem("Informe o período fim de avaliação");
//#                return false;
//#            }
//#        }
        
        if(cbRegional.getSelectedItem().toString().equals("-- Selecione --") 
                && !cbDesagregacao.getSelectedItem().toString().equals("Somente municípios")){
            SinanUtil.mensagem("Selecione região ou regional de saúde");
            return false;
        }
        if(cbMunicipio.getSelectedItem().toString().equals("-- Selecione --")
                && cbDesagregacao.getSelectedItem().toString().equals("Somente municípios")){
            SinanUtil.mensagem("Selecione município");
            return false;
        }
        if(lblArquivosSelecionados.getText().equals("Nenhum arquivo selecionado")){
            SinanUtil.mensagem("Nenhum arquivo foi selecionado");
            return false;
        }
        return true;
    }

    private void iniciaCombo(JComboBox cbAgravo) {
        cbAgravo.removeAllItems();
        cbAgravo.addItem("TODOS");
        if(cbDesagregacao.getSelectedIndex() == 3){
            
            cbAgravo.addItem("ANTRAZ PNEUMONICO");
            cbAgravo.addItem("ARENAVIRUS");
            cbAgravo.addItem("BOTULISMO");
            cbAgravo.addItem("COLERA");
            cbAgravo.addItem("DENGUE (OBITOS)");
            cbAgravo.addItem("EBOLA");
            cbAgravo.addItem("EVENTOS ADVERSOS GRAVES OU OBITOS POS-VACINACAO");
            cbAgravo.addItem("FEBRE AMARELA");
            cbAgravo.addItem("FEBRE DE CHIKUNGUNYA");
            cbAgravo.addItem("FEBRE DO NILO OCIDENTAL");
            cbAgravo.addItem("FEBRE MACULOSA E OUTRAS RIQUETISIOSES");
            cbAgravo.addItem("FEBRE PURPURICA BRASILEIRA");
            cbAgravo.addItem("INFLUENZA HUMANA PRODUZIDA POR NOVO SUBTIPO VIRAL");
            cbAgravo.addItem("LASSA");
            cbAgravo.addItem("MALARIA NA REGIAO EXTRA AMAZONICA");
            cbAgravo.addItem("MARBURG");
            cbAgravo.addItem("PARALISIA FLACIDA AGUDA");
            cbAgravo.addItem("PESTE");
            cbAgravo.addItem("RAIVA HUMANA");
            cbAgravo.addItem("RUBEOLA");
            cbAgravo.addItem("SARAMPO");
            cbAgravo.addItem("SINDROME DA RUBEOLA CONGENITA");
            cbAgravo.addItem("SINDROME RESPIRATORIA AGUDA GRAVE ASSOCIADA A CORONAVIRUS");
            cbAgravo.addItem("TULAREMIA");
            cbAgravo.addItem("VARIOLA");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        panelOportunidade = new javax.swing.JPanel();
        anoAvaliadoOportunidade = new javax.swing.JComboBox();
        cbMunicipio = new javax.swing.JComboBox();
        cbRegional = new javax.swing.JComboBox();
        lblUF = new javax.swing.JLabel();
        cbUf = new javax.swing.JComboBox();
        chkExportarDbf = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        prbStatus = new javax.swing.JProgressBar();
        btCalcular = new javax.swing.JButton();
        btLimpar = new javax.swing.JButton();
        pnlArquivos = new javax.swing.JPanel();
        btnSelecionarArquivos = new javax.swing.JButton();
        btnLimparSelecao = new javax.swing.JButton();
        lblArquivosSelecionados = new javax.swing.JLabel();
        cbDesagregacao = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(800, 373));

        panelOportunidade.setPreferredSize(new java.awt.Dimension(300, 99));

        anoAvaliadoOportunidade.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008" }));
        anoAvaliadoOportunidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anoAvaliadoOportunidadeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelOportunidadeLayout = new javax.swing.GroupLayout(panelOportunidade);
        panelOportunidade.setLayout(panelOportunidadeLayout);
        panelOportunidadeLayout.setHorizontalGroup(
            panelOportunidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOportunidadeLayout.createSequentialGroup()
                .addComponent(anoAvaliadoOportunidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 495, Short.MAX_VALUE))
        );
        panelOportunidadeLayout.setVerticalGroup(
            panelOportunidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOportunidadeLayout.createSequentialGroup()
                .addComponent(anoAvaliadoOportunidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 15, Short.MAX_VALUE))
        );

        cbMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbMunicipioActionPerformed(evt);
            }
        });

        cbRegional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRegionalActionPerformed(evt);
            }
        });

        lblUF.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUF.setText("UF de Notificação:"); // NOI18N

        cbUf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbUfActionPerformed(evt);
            }
        });

        chkExportarDbf.setText("Salvar resultado em DBF");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Região de Saúde:"); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Município de Notificação:"); // NOI18N

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
            .addGroup(pnlArquivosLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(btnSelecionarArquivos)
                .addGap(18, 18, 18)
                .addComponent(btnLimparSelecao)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlArquivosLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblArquivosSelecionados, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        pnlArquivosLayout.setVerticalGroup(
            pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlArquivosLayout.createSequentialGroup()
                .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelecionarArquivos)
                    .addComponent(btnLimparSelecao))
                .addContainerGap(38, Short.MAX_VALUE))
            .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlArquivosLayout.createSequentialGroup()
                    .addGap(30, 30, 30)
                    .addComponent(lblArquivosSelecionados)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        cbDesagregacao.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Selecione --", "Somente municípios", "UF subdividida por Regiões de Saúde", "UF subdividida por Regional de Saúde" }));
        cbDesagregacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDesagregacaoActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Desagregação:"); // NOI18N

        jLabel23.setText("           Ano Epidemiológico:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(145, 145, 145)
                                .addComponent(btCalcular)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btLimpar))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(prbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(pnlArquivos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelOportunidade, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel3)
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(lblUF)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbUf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(cbMunicipio, javax.swing.GroupLayout.Alignment.LEADING, 0, 225, Short.MAX_VALUE)
                                .addComponent(cbRegional, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(chkExportarDbf)
                            .addComponent(cbDesagregacao, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(185, 185, 185))
            .addGroup(layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(jLabel10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUF)
                    .addComponent(cbUf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbDesagregacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbRegional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addComponent(chkExportarDbf)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(panelOportunidade, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlArquivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(prbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btLimpar)
                    .addComponent(btCalcular, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMunicipioActionPerformed
        
}//GEN-LAST:event_cbMunicipioActionPerformed

    private void cbRegionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRegionalActionPerformed
        ComboBoxModel modelo;
        modelo = new DefaultComboBoxModel(this.session.retornaMunicipiosPQAVS(this.cbUf.getSelectedItem().toString(), this.cbRegional.getSelectedItem().toString()));
        this.cbMunicipio.setModel(modelo);
}//GEN-LAST:event_cbRegionalActionPerformed

    private void anoAvaliadoOportunidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_anoAvaliadoOportunidadeActionPerformed
        
}//GEN-LAST:event_anoAvaliadoOportunidadeActionPerformed

    private void btnLimparSelecaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparSelecaoActionPerformed
        lblArquivosSelecionados.setText("Nenhum arquivo selecionado");
}//GEN-LAST:event_btnLimparSelecaoActionPerformed

    private void btnSelecionarArquivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarArquivosActionPerformed
        JFileChooser fileopen = new JFileChooser();
        fileopen.setMultiSelectionEnabled(true);
        //        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivo DBF", "dbf");
        FiltroArquivo filtro = new FiltroArquivo();
        filtro.addExtension("dbf");
        filtro.setDescription("Arquivo DBF");
        filtro.addInicioNome("NINDI");
        filtro.addInicioNome("NSURT");
        filtro.addInicioNome("NNEGA");
        
        //fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileopen.addChoosableFileFilter(filtro);
fileopen.setFileFilter(filtro);
        
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

    private void cbUfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbUfActionPerformed
        ComboBoxModel modelo;
        modelo = new DefaultComboBoxModel(this.session.retornaRegioes(this.cbUf.getSelectedItem().toString()));
        this.cbRegional.setModel(modelo);
}//GEN-LAST:event_cbUfActionPerformed

    private void btCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCalcularActionPerformed
        
        if (!this.preencheuFormulario()) {
            return;
        }
        btCalcular.setEnabled(false);
        session = new SessionFacadeImpl();
 //       session.setBrasil(true);
        session.setTodosMunicipios(true);
        SessionFacadeImpl.setNomeDbf("NINDI");
        //        if (SessionFacadeImpl.isDbf()) {
        //            if (!Master.escolherDBF()) //gerar o relatorio
        //            {
        //                return;
        //            }
        //        }
        //verifica se vai exportar para dbf o resultado
        /*
        if (chkExportarDbf.isSelected()) {
            session.setExportarDbf(true);
            //abrir janela para definir o nome do arquivo para exportação
            //            if (!Master.setNomeArquivoDBF()) {
            //                return;
            //            }
        } else {
            session.setExportarDbf(false);
        }*/
        session.setExportarDbf(false);
        this.prbStatus.setStringPainted(true);
        this.prbStatus.setValue(0);
        //passa as datas selecionadas
        Map parametros = new HashMap();
        parametros.put("parArquivos", this.lblArquivosSelecionados.getText());
        parametros.put("parVariosArquivos", "sim");
        if(chkExportarDbf.isSelected())
            parametros.put("exportarDBF", true);
        else
            parametros.put("exportarDBF", false);
        
        if(cbDesagregacao.getSelectedItem().toString().equals("Discriminar por Agravo")){
            parametros.put("parDiscriminarPorAgravo", true);
        }else{
            parametros.put("parDiscriminarPorAgravo", false);
        }
//      parametros.put("parDiscriminarPorAgravo", chkDiscriminarPorAgravo.isSelected());
        
//#        session.setDataAvaliacao(SinanDateUtil.dateToStringException(dtAvaliacaoOportunidade.getDate(), "dd/MM/yyyy"));
//#        session.setNomeAgravo(cbAgravo.getSelectedItem().toString());
        session.setAnoAvaliado(anoAvaliadoOportunidade.getSelectedItem().toString());
//#        if(rbPeriodoAvaliacao.isSelected()){
//#            session.setDtInicioAvaliacao(SinanDateUtil.dateToStringException(dtInicioAvaliacao.getDate(), "dd/MM/yyyy"));
//#            session.setDtFimAvaliacao(SinanDateUtil.dateToStringException(dtFimAvaliacao.getDate(), "dd/MM/yyyy"));
//#            parametros.put("parAnoPeriodoAvaliacao", SinanDateUtil.dateToStringException(dtInicioAvaliacao.getDate(), "dd/MM/yyyy") + " a " + SinanDateUtil.dateToStringException(dtFimAvaliacao.getDate(), "dd/MM/yyyy"));
//#            //adaptação para resolver situação para cálculo da Malária
           // session.setAnoAvaliado(dtInicioAvaliacao.toString().split("/")[2]);
//#        }else{
//#            parametros.put("parAnoPeriodoAvaliacao", anoAvaliadoOportunidade.getSelectedItem().toString());
//#        }
        
        
        session.setDataAvaliacao("");
        session.setNomeAgravo("");     
        session.setDtInicioAvaliacao("");
        session.setDtFimAvaliacao("");
        
        
        parametros.put("parDiscriminarPorAgravo", "");
        parametros.put("parAnoPeriodoAvaliacao", "");
        parametros.put("parAnoEpid", anoAvaliadoOportunidade.getSelectedItem().toString());
        parametros.put("parNenhum", false);//parametro para listar ou não os municípios
        
 //       parametros.put("parDesagregacao", cbDesagregacao.getSelectedItem().toString());
        parametros.put("parDesagregacao", "UF subdividida por Regiões de Saúde e Municípios");
        parametros.put("parRegiaoSaude", cbRegional.getSelectedItem().toString());
        parametros.put("parMunic", cbMunicipio.getSelectedItem().toString());
        session.setParametros(parametros);
//        session.setTemListagem(cbGerarListagem.isSelected());
        session.setJprogress(prbStatus);
        session.setMunicipio(cbMunicipio.getSelectedItem().toString());
        session.setRegional(cbRegional.getSelectedItem().toString());
        session.setUf(cbUf.getSelectedItem().toString());
        session.setRelatorio("SemEpidPQAVS");
        session.execute();
        btCalcular.setEnabled(true);
}//GEN-LAST:event_btCalcularActionPerformed

    private void btLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLimparActionPerformed
        cbMunicipio.removeAllItems();
        cbRegional.removeAllItems();
        cbUf.setSelectedIndex(0);
}//GEN-LAST:event_btLimparActionPerformed

    private void cbDesagregacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDesagregacaoActionPerformed
        ComboBoxModel modelo;
        //modelo = new DefaultComboBoxModel(this.session.retornaUFs());
        //this.cbUf.setModel(modelo);
        this.chkExportarDbf.setSelected(false);
        
        if (this.cbDesagregacao.getSelectedItem().toString().equals("Somente municípios")) {
            jLabel3.setVisible(false);
            cbRegional.setVisible(false);
        } else if (this.cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regiões de Saúde")) {
            jLabel3.setText("Região de Residência");
            jLabel3.setVisible(true);
            cbRegional.setVisible(true);
            modelo = new DefaultComboBoxModel(this.session.retornaRegioes(this.cbUf.getSelectedItem().toString()));
            this.cbRegional.setModel(modelo);
        } else if (this.cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regional de Saúde")) {
            jLabel3.setText("Regional de Residência");
            jLabel3.setVisible(true);
            cbRegional.setVisible(true);
            modelo = new DefaultComboBoxModel(this.session.retornaRegionais(this.cbUf.getSelectedItem().toString()));
            this.cbRegional.setModel(modelo);
        }

        modelo = new DefaultComboBoxModel(this.session.retornaMunicipios(this.cbUf.getSelectedItem().toString()));
        this.cbMunicipio.setModel(modelo);
    }//GEN-LAST:event_cbDesagregacaoActionPerformed

    private void iniciaCombos(){
        ComboBoxModel modelo;
        modelo = new DefaultComboBoxModel(this.session.retornaUFs());
        this.cbUf.setModel(modelo);
        modelo = new DefaultComboBoxModel(this.session.retornaRegioes(this.cbUf.getSelectedItem().toString()));
        this.cbRegional.setModel(modelo);
        modelo = new DefaultComboBoxModel(this.session.retornaMunicipios(this.cbUf.getSelectedItem().toString()));
        this.cbMunicipio.setModel(modelo);
//#        iniciaCombo(cbAgravo);        // TODO add your handling code here:
        this.chkExportarDbf.setSelected(false);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox anoAvaliadoOportunidade;
    private javax.swing.JButton btCalcular;
    private javax.swing.JButton btLimpar;
    private javax.swing.JButton btnLimparSelecao;
    private javax.swing.JButton btnSelecionarArquivos;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cbDesagregacao;
    private javax.swing.JComboBox cbMunicipio;
    private javax.swing.JComboBox cbRegional;
    private javax.swing.JComboBox cbUf;
    private javax.swing.JCheckBox chkExportarDbf;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lblArquivosSelecionados;
    private javax.swing.JLabel lblUF;
    private javax.swing.JPanel panelOportunidade;
    private javax.swing.JPanel pnlArquivos;
    private javax.swing.JProgressBar prbStatus;
    // End of variables declaration//GEN-END:variables
}
