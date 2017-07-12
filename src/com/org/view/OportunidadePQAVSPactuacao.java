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
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

/**
 *
 * @author geraldo
 */
public class OportunidadePQAVSPactuacao extends javax.swing.JPanel {

    SessionFacadeImpl session = new SessionFacadeImpl();

    /**
     * Creates new form Oportunidade
     */
    public OportunidadePQAVSPactuacao() {
        initComponents();
        //    iniciaCombo(cbAgravo);
        //   dtAvaliacaoOportunidade.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        dtAvaliacaoOportunidade.setDate(SinanDateUtil.currentDate());
        preencheAnos(anoAvaliadoOportunidade, 2007);
        ComboBoxModel modelo;
        modelo = new DefaultComboBoxModel(this.session.retornaUFs());
        this.cbUf.setModel(modelo);

        //    this.session.setBrasil(true);
        this.session.setTodosMunicipios(true);
        //    modelo = new DefaultComboBoxModel(this.session.retornaUFs());
        //    this.cbUf.setModel(modelo);

    }

    public static void preencheAnos(JComboBox combo, int anoInicial) {
        combo.removeAllItems();
        int anoFinal = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        for (int i = anoFinal; i >= anoInicial; i--) {
            combo.addItem(i);
        }
    }

    private boolean preencheuFormulario() {

        if (cbDesagregacao.getSelectedItem().toString().equals("-- Selecione --")) {
            SinanUtil.mensagem("Selecione o nível de desagregação");
            return false;
        }
        if (cbUf.getSelectedItem().toString().equals("-- Selecione --")) {
            Master.mensagem("Selecione a UF de residência");
            return false;
        }
        if (SinanDateUtil.dateToStringException(dtAvaliacaoOportunidade.getDate(), "dd/MM/yyyy").equals("")) {
            Master.mensagem("Informe a data de avaliação");
            return false;
        }
        if (rbPeriodoAvaliacao.isSelected()) {
            if (dtInicioAvaliacao.getDate() == null) {
                SinanUtil.mensagem("Informe o período de início de avaliação");
                return false;
            } else if (dtFimAvaliacao.getDate() == null) {
                SinanUtil.mensagem("Informe o período fim de avaliação");
                return false;
            }
        }

        if (cbRegional.getSelectedItem().toString().equals("-- Selecione --")) {
            SinanUtil.mensagem("Selecione região de saúde");
            return false;
        }
        if (cbMunicipio.getSelectedItem().toString().equals("-- Selecione --")) {
            SinanUtil.mensagem("Selecione município");
            return false;
        }
        if (lblArquivosSelecionados.getText().equals("Nenhum arquivo selecionado")) {
            SinanUtil.mensagem("Nenhum arquivo foi selecionado");
            return false;
        }
        return true;
    }

    private void iniciaCombo(JComboBox cbAgravo) {
        cbAgravo.removeAllItems();
        cbAgravo.addItem("TODOS");
        if (!cbDesagregacao.getSelectedItem().equals("Discriminar por Agravo")) {
        } else {
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
            cbAgravo.addItem("SINDROME RESPIRAT. AGUDA GRAVE ASSOC. A CORONAVIRUS");
            cbAgravo.addItem("TULAREMIA");
            cbAgravo.addItem("VARIOLA");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        panelOportunidade = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        anoAvaliadoOportunidade = new javax.swing.JComboBox();
        dtAvaliacaoOportunidade = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        dtInicioAvaliacao = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        dtFimAvaliacao = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        rbPeriodoAvaliacao = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        pnlArquivos = new javax.swing.JPanel();
        btnSelecionarArquivos = new javax.swing.JButton();
        btnLimparSelecao = new javax.swing.JButton();
        lblArquivosSelecionados = new javax.swing.JLabel();
        cbMunicipio = new javax.swing.JComboBox();
        cbRegional = new javax.swing.JComboBox();
        lblUF = new javax.swing.JLabel();
        cbUf = new javax.swing.JComboBox();
        chkExportarDbf = new javax.swing.JCheckBox();
        lblRegional = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        prbStatus = new javax.swing.JProgressBar();
        btCalcular = new javax.swing.JButton();
        btLimpar = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        cbAgravo = new javax.swing.JComboBox();
        cbDesagregacao = new javax.swing.JComboBox();
        lblDesagregacao = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(800, 373));

        panelOportunidade.setPreferredSize(new java.awt.Dimension(561, 99));

        jLabel23.setText("Data Avaliação:");

        anoAvaliadoOportunidade.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008" }));
        anoAvaliadoOportunidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anoAvaliadoOportunidadeActionPerformed(evt);
            }
        });

        dtAvaliacaoOportunidade.getJCalendar().setWeekOfYearVisible(false);

        dtInicioAvaliacao.getJCalendar().setWeekOfYearVisible(false);
        dtInicioAvaliacao.setEnabled(false);

        jLabel7.setText("De"); // NOI18N

        jLabel8.setText("até"); // NOI18N

        dtFimAvaliacao.getJCalendar().setWeekOfYearVisible(false);
        dtFimAvaliacao.setEnabled(false);

        buttonGroup1.add(rbPeriodoAvaliacao);
        rbPeriodoAvaliacao.setText("Período de avaliação");
        rbPeriodoAvaliacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPeriodoAvaliacaoActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton7);
        jRadioButton7.setSelected(true);
        jRadioButton7.setText("Ano de avaliação");
        jRadioButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton7ActionPerformed(evt);
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

        javax.swing.GroupLayout panelOportunidadeLayout = new javax.swing.GroupLayout(panelOportunidade);
        panelOportunidade.setLayout(panelOportunidadeLayout);
        panelOportunidadeLayout.setHorizontalGroup(
            panelOportunidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOportunidadeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOportunidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelOportunidadeLayout.createSequentialGroup()
                        .addGroup(panelOportunidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dtAvaliacaoOportunidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(panelOportunidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton7)
                            .addComponent(anoAvaliadoOportunidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelOportunidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelOportunidadeLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dtInicioAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dtFimAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(rbPeriodoAvaliacao)))
                    .addComponent(pnlArquivos, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelOportunidadeLayout.setVerticalGroup(
            panelOportunidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOportunidadeLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelOportunidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jRadioButton7)
                    .addComponent(rbPeriodoAvaliacao))
                .addGap(2, 2, 2)
                .addGroup(panelOportunidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelOportunidadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(dtInicioAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel8)
                        .addComponent(dtFimAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dtAvaliacaoOportunidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(anoAvaliadoOportunidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnlArquivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        lblUF.setText("UF de Residência:"); // NOI18N

        cbUf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbUfActionPerformed(evt);
            }
        });

        chkExportarDbf.setText("Salvar resultado em DBF");

        lblRegional.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblRegional.setText("Região de Saúde:"); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Município de Residência:"); // NOI18N

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

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Agravo:"); // NOI18N

        cbAgravo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAgravoActionPerformed(evt);
            }
        });

        cbDesagregacao.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Selecione --", "UF subdividida por Regiões de Saúde", "UF subdividida por Regionais de Saúde", "Discriminar por Agravo" }));
        cbDesagregacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDesagregacaoActionPerformed(evt);
            }
        });

        lblDesagregacao.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDesagregacao.setText("Desagregação:"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(prbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblRegional)
                                        .addComponent(jLabel4)
                                        .addComponent(lblDesagregacao)
                                        .addComponent(lblUF)
                                        .addComponent(jLabel9))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(cbMunicipio, javax.swing.GroupLayout.Alignment.LEADING, 0, 225, Short.MAX_VALUE)
                                            .addComponent(cbRegional, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(cbAgravo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cbDesagregacao, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cbUf, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(panelOportunidade, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(197, 197, 197)
                            .addComponent(chkExportarDbf))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(82, 82, 82)
                            .addComponent(btCalcular)
                            .addGap(86, 86, 86)
                            .addComponent(btLimpar))))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbUf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUF))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbDesagregacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDesagregacao, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbRegional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRegional))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(cbAgravo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addComponent(chkExportarDbf)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOportunidade, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCalcular, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(btLimpar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMunicipioActionPerformed

}//GEN-LAST:event_cbMunicipioActionPerformed

    private void cbRegionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRegionalActionPerformed
        ComboBoxModel modelo;
        if (cbRegional.getSelectedItem() != null) {
            Vector<String> municipiosPactuacao = this.session.retornaMunicipiosPQAVS(this.cbDesagregacao.getSelectedIndex(), this.cbUf.getSelectedItem().toString(), this.cbRegional.getSelectedItem().toString());
            municipiosPactuacao.add(2, "NENHUM");
            modelo = new DefaultComboBoxModel(municipiosPactuacao);

            this.cbMunicipio.setModel(modelo);
        }
}//GEN-LAST:event_cbRegionalActionPerformed

    private void jRadioButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton7ActionPerformed
        dtInicioAvaliacao.setEnabled(false);
        dtFimAvaliacao.setEnabled(false);
        anoAvaliadoOportunidade.setEnabled(true);
        dtInicioAvaliacao.setDate(null);
        dtFimAvaliacao.setDate(null);
}//GEN-LAST:event_jRadioButton7ActionPerformed

    private void rbPeriodoAvaliacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPeriodoAvaliacaoActionPerformed
        dtInicioAvaliacao.setEnabled(true);
        dtFimAvaliacao.setEnabled(true);
        anoAvaliadoOportunidade.setEnabled(false);
}//GEN-LAST:event_rbPeriodoAvaliacaoActionPerformed

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
        filtro.addInicioNome("DENGO");
        filtro.addInicioNome("CHIKO");

        //fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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

    private void cbUfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbUfActionPerformed
        cbDesagregacao.setSelectedIndex(0);
        cbRegional.removeAllItems();
        cbMunicipio.removeAllItems();

        iniciaCombo(cbAgravo);        // TODO add your handling code here:
        this.chkExportarDbf.setSelected(false);


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
        if (chkExportarDbf.isSelected()) {
            parametros.put("exportarDBF", true);
        } else {
            parametros.put("exportarDBF", false);
        }

        /**
         * SINAN 5.0 - Aterado para o novo modelo da View de pesquisa. Foi
         * criado um checkbox para que o usuário selecione caso queira
         * discriminar por agravo
         */
        if (cbDesagregacao.getSelectedItem().equals("Discriminar por Agravo")) {
            parametros.put("parDiscriminarPorAgravo", true);
        } else {
            parametros.put("parDiscriminarPorAgravo", false);
        }
        /**
         *
         */
//      parametros.put("parDiscriminarPorAgravo", chkDiscriminarPorAgravo.isSelected());

        session.setDataAvaliacao(SinanDateUtil.dateToStringException(dtAvaliacaoOportunidade.getDate(), "dd/MM/yyyy"));
        session.setNomeAgravo(cbAgravo.getSelectedItem().toString());
        session.setAnoAvaliado(anoAvaliadoOportunidade.getSelectedItem().toString());
        if (rbPeriodoAvaliacao.isSelected()) {
            session.setDtInicioAvaliacao(SinanDateUtil.dateToStringException(dtInicioAvaliacao.getDate(), "dd/MM/yyyy"));
            session.setDtFimAvaliacao(SinanDateUtil.dateToStringException(dtFimAvaliacao.getDate(), "dd/MM/yyyy"));
            parametros.put("parAnoPeriodoAvaliacao", SinanDateUtil.dateToStringException(dtInicioAvaliacao.getDate(), "dd/MM/yyyy") + " a " + SinanDateUtil.dateToStringException(dtFimAvaliacao.getDate(), "dd/MM/yyyy"));
            //adaptação para resolver situação para cálculo da Malária
            // session.setAnoAvaliado(dtInicioAvaliacao.toString().split("/")[2]);
        } else {
            parametros.put("parAnoPeriodoAvaliacao", anoAvaliadoOportunidade.getSelectedItem().toString());
        }
        parametros.put("parDesagregacao", cbDesagregacao.getSelectedItem().toString());

        parametros.put("parIsRegiao", true);
        if (cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regionais de Saúde")) {
            //parametros.put("parRegionalSaude", cbRegional.getSelectedItem().toString());
            session.setRegional(cbRegional.getSelectedItem().toString());
            parametros.put("parIsRegiao", false);
        } else {
            parametros.put("parRegiaoSaude", cbRegional.getSelectedItem().toString());
        }

        parametros.put("parNenhum", "false");
        if (cbMunicipio.getSelectedItem().toString().equals("NENHUM")) {
            cbMunicipio.setSelectedItem("TODOS");
            parametros.put("parMunic", cbMunicipio.getSelectedItem().toString());
            parametros.put("parNenhum", "true");
        } else {
            parametros.put("parMunic", cbMunicipio.getSelectedItem().toString());
        }

        session.setParametros(parametros);
//      session.setTemListagem(cbGerarListagem.isSelected());
        session.setJprogress(prbStatus);
        session.setMunicipio(cbMunicipio.getSelectedItem().toString());
        session.setRegional(cbRegional.getSelectedItem().toString());
        session.setUf(cbUf.getSelectedItem().toString());
        session.setRelatorio("OportunidadePQAVSPactuacao");
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
//        modelo = new DefaultComboBoxModel(this.session.retornaUFs());
//        this.cbUf.setModel(modelo);
//        if (cbDesagregacao.getSelectedItem().equals("UF subdividida por Regiões de Saúde")) {

        if (this.cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regiões de Saúde")) {
            lblRegional.setText("Região de Residência");
            lblRegional.setVisible(true);
            cbRegional.setVisible(true);
            modelo = new DefaultComboBoxModel(this.session.retornaRegioes(this.cbUf.getSelectedItem().toString()));
            this.cbRegional.setModel(modelo);

        } else if (this.cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regionais de Saúde")) {
            lblRegional.setText("Regional de Residência");
            lblRegional.setVisible(true);
            cbRegional.setVisible(true);
            modelo = new DefaultComboBoxModel(this.session.retornaRegionais(this.cbUf.getSelectedItem().toString()));
            this.cbRegional.setModel(modelo);
        }else {
            modelo = new DefaultComboBoxModel(this.session.retornaRegionais(this.cbUf.getSelectedItem().toString()));
            this.cbRegional.setModel(modelo);
           
        }


//        modelo = new DefaultComboBoxModel(this.session.retornaRegioes(this.cbUf.getSelectedItem().toString()));
 //       this.cbRegional.setModel(modelo);
        modelo = new DefaultComboBoxModel(this.session.retornaMunicipios(this.cbUf.getSelectedItem().toString()));
       this.cbMunicipio.setModel(modelo);
//        }
        iniciaCombo(cbAgravo);        // TODO add your handling code here:
        this.chkExportarDbf.setSelected(false);
        /*
        if(this.cbDesagregacao.getSelectedIndex() != 2){
            this.chkExportarDbf.setVisible(false);
        }else{
            this.chkExportarDbf.setVisible(true);
        }*/
    }//GEN-LAST:event_cbDesagregacaoActionPerformed

    private void cbAgravoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAgravoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbAgravoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox anoAvaliadoOportunidade;
    private javax.swing.JButton btCalcular;
    private javax.swing.JButton btLimpar;
    private javax.swing.JButton btnLimparSelecao;
    private javax.swing.JButton btnSelecionarArquivos;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cbAgravo;
    private javax.swing.JComboBox cbDesagregacao;
    private javax.swing.JComboBox cbMunicipio;
    private javax.swing.JComboBox cbRegional;
    private javax.swing.JComboBox cbUf;
    private javax.swing.JCheckBox chkExportarDbf;
    private com.toedter.calendar.JDateChooser dtAvaliacaoOportunidade;
    private com.toedter.calendar.JDateChooser dtFimAvaliacao;
    private com.toedter.calendar.JDateChooser dtInicioAvaliacao;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JLabel lblArquivosSelecionados;
    private javax.swing.JLabel lblDesagregacao;
    private javax.swing.JLabel lblRegional;
    private javax.swing.JLabel lblUF;
    private javax.swing.JPanel panelOportunidade;
    private javax.swing.JPanel pnlArquivos;
    private javax.swing.JProgressBar prbStatus;
    private javax.swing.JRadioButton rbPeriodoAvaliacao;
    // End of variables declaration//GEN-END:variables
}
