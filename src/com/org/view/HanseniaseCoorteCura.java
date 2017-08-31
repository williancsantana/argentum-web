
package com.org.view;

import com.org.facade.SessionFacadeImpl;
import com.org.model.classes.Agravo;
import com.org.negocio.Configuracao;
import com.org.negocio.FiltroArquivo;
import com.org.negocio.Util;
import com.org.util.SinanDateUtil;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author geraldo
 */
public class HanseniaseCoorteCura extends javax.swing.JPanel {

    SessionFacadeImpl session = new SessionFacadeImpl();

    /** Creates new form TuberculoseCoorte */
    public HanseniaseCoorteCura() {
        initComponents();
        ComboBoxModel modelo;
        this.session.setBrasil(true);
        this.session.setTodosMunicipios(true);
        Oportunidade.preencheAnos(jComboBox1, 2001);
        Oportunidade.preencheAnos(jComboBox2, 2001);
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
        if (jRadioButton2.isSelected()) {
            if (SinanDateUtil.dateToStringException(dataInicio1.getDate(), "dd/MM/yyyy").equals("")) {
                Master.mensagem("Informe o período inicial");
                return false;
            }
            if (SinanDateUtil.dateToStringException(dataFim1.getDate(), "dd/MM/yyyy").equals("")) {
                Master.mensagem("Informe o período final");
                return false;
            }

            try {
                Date dtInicio = Agravo.converterParaData(SinanDateUtil.dateToStringException(dataInicio1.getDate(), "dd/MM/yyyy"));
                Date dtFim = Agravo.converterParaData(SinanDateUtil.dateToStringException(dataFim1.getDate(), "dd/MM/yyyy"));
                if (dtFim.before(dtInicio)) {
                    Master.mensagem("Data final deve ser maior que inicial");
                    return false;
                }
            } catch (Exception e) {
                Master.mensagem("Data inválida");
                return false;
            }
        }
        if (jRadioButton4.isSelected()) {
            if (SinanDateUtil.dateToStringException(dataInicio2.getDate(), "dd/MM/yyyy").equals("")) {
                Master.mensagem("Informe o período inicial");
                return false;
            }
            if (SinanDateUtil.dateToStringException(dataFim2.getDate(), "dd/MM/yyyy").equals("")) {
                Master.mensagem("Informe o período final");
                return false;
            }

            try {
                Date dtInicio = Agravo.converterParaData(SinanDateUtil.dateToStringException(dataInicio2.getDate(), "dd/MM/yyyy"));
                Date dtFim = Agravo.converterParaData(SinanDateUtil.dateToStringException(dataFim2.getDate(), "dd/MM/yyyy"));
                if (dtFim.before(dtInicio)) {
                    Master.mensagem("Data final deve ser maior que inicial");
                    return false;
                }
            } catch (Exception e) {
                Master.mensagem("Data inválida");
                return false;
            }
        }
        if(lblArquivosSelecionados.getText().equals("Nenhum arquivo selecionado") && SessionFacadeImpl.isDbf()){
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

        panelFiltroCoorteHanseniase = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        dataInicio1 = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        dataFim1 = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jPanel7 = new javax.swing.JPanel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        dataInicio2 = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        dataFim2 = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        prbStatus = new javax.swing.JProgressBar();
        btCalcular = new javax.swing.JButton();
        btLimpar = new javax.swing.JButton();
        cbRegional = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        chkExportarDbf = new javax.swing.JCheckBox();
        cbUf = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        lblUF = new javax.swing.JLabel();
        cbMunicipio = new javax.swing.JComboBox();
        pnlArquivos = new javax.swing.JPanel();
        btnSelecionarArquivos = new javax.swing.JButton();
        btnLimparSelecao = new javax.swing.JButton();
        lblArquivosSelecionados = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(520, 375));

        panelFiltroCoorteHanseniase.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Selecione ano ou período", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        panelFiltroCoorteHanseniase.setPreferredSize(new java.awt.Dimension(600, 223));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("PB"));

        jRadioButton1.setSelected(true);
        jRadioButton1.setLabel("Ano de diagnóstico");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        jRadioButton2.setLabel("Período de diagnóstico");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel10.setText("De"); // NOI18N

        jLabel11.setText("até"); // NOI18N

        dataInicio1.getJCalendar().setWeekOfYearVisible(false);
        dataInicio1.setEnabled(false);

        dataFim1.getJCalendar().setWeekOfYearVisible(false);
        dataFim1.setEnabled(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataInicio1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataFim1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(183, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jRadioButton1)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jRadioButton2)
                        .addComponent(jLabel10))
                    .addComponent(dataInicio1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataFim1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("MB"));

        jRadioButton3.setSelected(true);
        jRadioButton3.setLabel("Ano de diagnóstico");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        jRadioButton4.setLabel("Período de diagnóstico");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel12.setText("De"); // NOI18N

        jLabel13.setText("até"); // NOI18N

        dataInicio2.getJCalendar().setWeekOfYearVisible(false);
        dataInicio2.setEnabled(false);

        dataFim2.getJCalendar().setWeekOfYearVisible(false);
        dataFim2.setEnabled(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataInicio2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataFim2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(171, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButton3)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(jLabel13)
                        .addComponent(jRadioButton4))
                    .addComponent(dataInicio2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataFim2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelFiltroCoorteHanseniaseLayout = new javax.swing.GroupLayout(panelFiltroCoorteHanseniase);
        panelFiltroCoorteHanseniase.setLayout(panelFiltroCoorteHanseniaseLayout);
        panelFiltroCoorteHanseniaseLayout.setHorizontalGroup(
            panelFiltroCoorteHanseniaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltroCoorteHanseniaseLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        panelFiltroCoorteHanseniaseLayout.setVerticalGroup(
            panelFiltroCoorteHanseniaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltroCoorteHanseniaseLayout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        prbStatus.setMaximumSize(new java.awt.Dimension(200, 19));

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

        cbRegional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRegionalActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Município de Residência Atual:"); // NOI18N

        chkExportarDbf.setText("Salvar resultado em DBF");

        cbUf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbUfActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Regional de Residência:"); // NOI18N

        lblUF.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUF.setText("UF de Residência:"); // NOI18N

        cbMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbMunicipioActionPerformed(evt);
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
            .addGap(0, 528, Short.MAX_VALUE)
            .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlArquivosLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblArquivosSelecionados, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                        .addGroup(pnlArquivosLayout.createSequentialGroup()
                            .addComponent(btnSelecionarArquivos)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnLimparSelecao)
                            .addContainerGap(280, Short.MAX_VALUE)))))
        );
        pnlArquivosLayout.setVerticalGroup(
            pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 53, Short.MAX_VALUE)
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
                        .addGap(53, 53, 53)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblUF)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbMunicipio, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbRegional, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbUf, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(228, 228, 228)
                        .addComponent(chkExportarDbf))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(prbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(pnlArquivos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(panelFiltroCoorteHanseniase, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(168, 168, 168)
                        .addComponent(btCalcular)
                        .addGap(18, 18, 18)
                        .addComponent(btLimpar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
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
                .addComponent(panelFiltroCoorteHanseniase, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlArquivos, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCalcular, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(btLimpar))
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        jRadioButton3.setSelected(true);
        jRadioButton2.setSelected(false);
        jRadioButton4.setSelected(false);
        dataInicio1.setDate(null);
        dataInicio2.setDate(null);
        dataFim1.setDate(null);
        dataFim2.setDate(null);
        dataInicio1.setEnabled(false);
        dataInicio2.setEnabled(false);
        dataFim1.setEnabled(false);
        dataFim2.setEnabled(false);
        jComboBox1.setEnabled(true);
        jComboBox2.setEnabled(true);
}//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        jRadioButton4.setSelected(true);
        jRadioButton3.setSelected(false);
        jRadioButton1.setSelected(false);
        dataInicio1.setEnabled(true);
        dataInicio2.setEnabled(true);
        dataFim1.setEnabled(true);
        dataFim2.setEnabled(true);
        jComboBox1.setEnabled(false);
        jComboBox2.setEnabled(false);
}//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        jRadioButton1.setSelected(true);
        jRadioButton3.setSelected(true);
        jRadioButton2.setSelected(false);
        jRadioButton4.setSelected(false);
        dataInicio1.setDate(null);
        dataInicio2.setDate(null);
        dataFim1.setDate(null);
        dataFim2.setDate(null);
        dataInicio1.setEnabled(false);
        dataInicio2.setEnabled(false);
        dataFim1.setEnabled(false);
        dataFim2.setEnabled(false);
}//GEN-LAST:event_jComboBox1ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        jRadioButton1.setSelected(true);
        jRadioButton4.setSelected(false);
        jRadioButton2.setSelected(false);
        dataInicio1.setDate(null);
        dataInicio2.setDate(null);
        dataFim1.setDate(null);
        dataFim2.setDate(null);
        dataInicio1.setEnabled(false);
        dataInicio2.setEnabled(false);
        dataFim1.setEnabled(false);
        dataFim2.setEnabled(false);
        jComboBox1.setEnabled(true);
        jComboBox2.setEnabled(true);
}//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        jRadioButton2.setSelected(true);
        jRadioButton1.setSelected(false);
        jRadioButton3.setSelected(false);
        dataInicio1.setEnabled(true);
        dataInicio2.setEnabled(true);
        dataFim1.setEnabled(true);
        dataFim2.setEnabled(true);
        jComboBox1.setEnabled(false);
        jComboBox2.setEnabled(false);
}//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        jRadioButton1.setSelected(true);
        jRadioButton3.setSelected(true);
        jRadioButton2.setSelected(false);
        jRadioButton4.setSelected(false);
        dataInicio1.setDate(null);
        dataInicio2.setDate(null);
        dataFim1.setDate(null);
        dataFim2.setDate(null);
        dataInicio1.setEnabled(false);
        dataInicio2.setEnabled(false);
        dataFim1.setEnabled(false);
        dataFim2.setEnabled(false);
}//GEN-LAST:event_jComboBox2ActionPerformed

    private void btCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCalcularActionPerformed
        if (!this.preencheuFormulario()) {
            return;
        }
        session = new SessionFacadeImpl();
        session.setBrasil(true);
        session.setTodosMunicipios(true);
        SessionFacadeImpl.setNomeDbf("HANSN");
        //verifica se vai exportar para dbf o resultado
        if (chkExportarDbf.isSelected()) {
            session.setExportarDbf(true);
        } else {
            session.setExportarDbf(false);
        }
        this.prbStatus.setStringPainted(true);
        this.prbStatus.setValue(0);
        //passa as datas selecionadas
        Map parametros = new HashMap();
        parametros.put("parNenhum", false);
        if (jRadioButton2.isSelected()) {
            String dataInicio1 = SinanDateUtil.dateToStringException(this.dataInicio1.getDate(), "dd/MM/yyyy");
            String dataFim1 = SinanDateUtil.dateToStringException(this.dataFim1.getDate(), "dd/MM/yyyy");
            String dataInicio2 = SinanDateUtil.dateToStringException(this.dataInicio2.getDate(), "dd/MM/yyyy");
            String dataFim2 = SinanDateUtil.dateToStringException(this.dataFim2.getDate(), "dd/MM/yyyy");
            //verifica se preencheu as datas
            String message;
            if (dataInicio1.equals("") || dataFim1.equals("") || dataInicio2.equals("") || dataFim2.equals("")) {
                message = "Informe as datas de início e fim por favor";
                JOptionPane.showMessageDialog(new JFrame(), message, "Erro",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                //datas completas
                parametros.put("parDataInicio1", Util.formataData(dataInicio1));
                parametros.put("parDataFim1", Util.formataData(dataFim1));
                parametros.put("parDataInicio2", Util.formataData(dataInicio2));
                parametros.put("parDataFim2", Util.formataData(dataFim2));
                parametros.put("parPeriodo", "\nPB: de " + dataInicio1 + " a " + dataFim1 + "\nMB: de " + dataInicio2 + " a " + dataFim2 + "\n");
            }
        } else {
            //datas pelo ano
            String ano = jComboBox1.getSelectedItem().toString();
            String ano2 = jComboBox2.getSelectedItem().toString();
            parametros.put("parDataInicio1", ano + "-01-01");
            parametros.put("parDataFim1", ano + "-12-31");
            parametros.put("parDataInicio2", ano2 + "-01-01");
            parametros.put("parDataFim2", ano2 + "-12-31");
            parametros.put("parPeriodo", "\nPB: de " + "01/01/" + ano + " a " + "31/12/" + ano + "\nMB: de " + "01/01/" + ano2 + " a " + "31/12/" + ano2 + "\n");
        }
        parametros.put("parArquivos", this.lblArquivosSelecionados.getText());
        parametros.put("parVariosArquivos", "sim");
        session.setParametros(parametros);
        session.setJprogress(prbStatus);
        session.setMunicipio(cbMunicipio.getSelectedItem().toString());
        session.setRegional(cbRegional.getSelectedItem().toString());
        session.setUf(cbUf.getSelectedItem().toString());
        session.setRelatorio("HanseniaseCoorteCura");
        session.execute();
}//GEN-LAST:event_btCalcularActionPerformed

    private void btLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLimparActionPerformed
        cbMunicipio.removeAllItems();
        cbRegional.removeAllItems();
        cbUf.setSelectedIndex(0);
}//GEN-LAST:event_btLimparActionPerformed

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

    private void btnSelecionarArquivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarArquivosActionPerformed
        JFileChooser fileopen = new JFileChooser();
        fileopen.setMultiSelectionEnabled(true);
        //        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivo DBF", "dbf");
        FiltroArquivo filtro = new FiltroArquivo();
        filtro.addExtension("dbf");
        filtro.setDescription("Arquivo DBF");
        filtro.addInicioNome("HANSN");

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
    private com.toedter.calendar.JDateChooser dataFim1;
    private com.toedter.calendar.JDateChooser dataFim2;
    private com.toedter.calendar.JDateChooser dataInicio1;
    private com.toedter.calendar.JDateChooser dataInicio2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JLabel lblArquivosSelecionados;
    private javax.swing.JLabel lblUF;
    private javax.swing.JPanel panelFiltroCoorteHanseniase;
    private javax.swing.JPanel pnlArquivos;
    private javax.swing.JProgressBar prbStatus;
    // End of variables declaration//GEN-END:variables
}
