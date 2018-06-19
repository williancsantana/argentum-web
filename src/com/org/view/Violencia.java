package com.org.view;

import com.org.facade.SessionFacadeImpl;
import com.org.model.classes.Agravo;
import com.org.negocio.Configuracao;
import com.org.negocio.FiltroArquivo;
import com.org.util.SinanDateUtil;
import com.org.util.SinanUtil;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;

/**
 *
 * @author joao
 */
public class Violencia extends javax.swing.JPanel {

    SessionFacadeImpl session = new SessionFacadeImpl();

    public Violencia() {
        initComponents();
        ComboBoxModel modelo;
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
            SinanUtil.mensagem("Selecione a UF de residência");
            return false;
        }
        if (cbDesagregacao.getSelectedItem().toString().equals("-- Selecione --")) {
            SinanUtil.mensagem("Selecione a Desagregação desejada");
            return false;
        }
        if (null != cbRegional.getSelectedItem() && cbRegional.getSelectedItem().toString().equals("-- Selecione --")
                && (cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regiões de Saúde")
                || cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regional de Saúde"))) {
            SinanUtil.mensagem("Selecione a Região ou Regional de residência");
            return false;
        }
        if (cbMunicipio.getSelectedItem().toString().equals("-- Selecione --")) {
            SinanUtil.mensagem("Selecione o Município de residência");
            return false;
        }
        if (SinanDateUtil.dateToStringException(dataInicio.getDate(), "dd/MM/yyyy").equals("")) {
            SinanUtil.mensagem("Informe o período inicial");
            return false;
        }
        if (SinanDateUtil.dateToStringException(dataFim.getDate(), "dd/MM/yyyy").equals("")) {
            SinanUtil.mensagem("Informe o período final");
            return false;
        }
        if (Integer.parseInt(SinanDateUtil.dateToStringException(dataInicio.getDate(), "dd/MM/yyyy").substring(6, 10)) < 2007) {
            SinanUtil.mensagem("O período inicial deve ser maior que 2007");
            return false;
        }
        try {
            Date dtInicio = Agravo.converterParaData(SinanDateUtil.dateToStringException(dataInicio.getDate(), "dd/MM/yyyy"));
            Date dtFim = Agravo.converterParaData(SinanDateUtil.dateToStringException(dataFim.getDate(), "dd/MM/yyyy"));
            if (dtFim.before(dtInicio)) {
                SinanUtil.mensagem("Data final deve ser maior que inicial");
                return false;
            }
        } catch (Exception e) {
            SinanUtil.mensagem("Data inválida");
            return false;
        }
        if (lblArquivosSelecionados.getText().equals("Nenhum arquivo selecionado") && SessionFacadeImpl.isDbf()) {
            SinanUtil.mensagem("Selecione um arquivo.");
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblRegional = new javax.swing.JLabel();
        chkExportarDbf = new javax.swing.JCheckBox();
        cbUf = new javax.swing.JComboBox();
        lblDesagregacao = new javax.swing.JLabel();
        jLabel = new javax.swing.JLabel();
        cbDesagregacao = new javax.swing.JComboBox();
        cbRegional = new javax.swing.JComboBox();
        btCalcular = new javax.swing.JButton();
        jpDataPrimeiroSintomas = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblAviso = new javax.swing.JLabel();
        dataInicio = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        dataFim = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        btLimpar = new javax.swing.JButton();
        lblUF = new javax.swing.JLabel();
        prbStatus = new javax.swing.JProgressBar();
        cbMunicipio = new javax.swing.JComboBox();
        pnlArquivos = new javax.swing.JPanel();
        btnSelecionarArquivos = new javax.swing.JButton();
        btnLimparSelecao = new javax.swing.JButton();
        lblArquivosSelecionados = new javax.swing.JLabel();

        lblRegional.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblRegional.setText("Regional de Notificação:"); // NOI18N

        chkExportarDbf.setText("Salvar resultado em DBF");

        cbUf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbUfActionPerformed(evt);
            }
        });

        lblDesagregacao.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDesagregacao.setText("Desagregação:");

        jLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel.setText("Município de Notificação:"); // NOI18N

        cbDesagregacao.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Selecione --", "UF subdividida por Regiões de Saúde", "UF subdividida por Regional de Saúde", "Somente municípios" }));
        cbDesagregacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDesagregacaoActionPerformed(evt);
            }
        });

        cbRegional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRegionalActionPerformed(evt);
            }
        });

        btCalcular.setLabel("Calcular");
        btCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCalcularActionPerformed(evt);
            }
        });

        jpDataPrimeiroSintomas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data de Notificação", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel9.setText("De"); // NOI18N

        jLabel10.setText("até"); // NOI18N

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
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataFim, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblAviso))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpDataPrimeiroSintomasLayout.setVerticalGroup(
            jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDataPrimeiroSintomasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dataFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpDataPrimeiroSintomasLayout.createSequentialGroup()
                        .addGroup(jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAviso)))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        btLimpar.setText("Limpar");
        btLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLimparActionPerformed(evt);
            }
        });

        lblUF.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUF.setText("UF de Notificação:"); // NOI18N

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
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlArquivosLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblArquivosSelecionados, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlArquivosLayout.createSequentialGroup()
                            .addComponent(btnSelecionarArquivos)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnLimparSelecao)))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        pnlArquivosLayout.setVerticalGroup(
            pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 71, Short.MAX_VALUE)
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
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(prbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(11, 11, 11))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jpDataPrimeiroSintomas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(134, 134, 134)
                                .addComponent(btCalcular)
                                .addGap(26, 26, 26)
                                .addComponent(btLimpar))
                            .addComponent(pnlArquivos, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblRegional)
                    .addComponent(jLabel)
                    .addComponent(lblDesagregacao)
                    .addComponent(lblUF))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkExportarDbf)
                    .addComponent(cbUf, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(cbMunicipio, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbRegional, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbDesagregacao, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(444, Short.MAX_VALUE))
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
                    .addComponent(lblDesagregacao))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbRegional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRegional))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel)
                    .addComponent(cbMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                    .addComponent(btLimpar))
                .addContainerGap())
        );

        jpDataPrimeiroSintomas.getAccessibleContext().setAccessibleName("Data de Notificação\n");
    }// </editor-fold>//GEN-END:initComponents

    private void cbUfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbUfActionPerformed
        cbDesagregacao.setSelectedIndex(1);
    }//GEN-LAST:event_cbUfActionPerformed

    private void cbDesagregacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDesagregacaoActionPerformed
        ComboBoxModel modelo;

        if (this.cbDesagregacao.getSelectedItem().toString().equals("Somente municípios")) {
            lblRegional.setVisible(false);
            cbRegional.setVisible(false);
        } else if (this.cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regiões de Saúde")) {
            lblRegional.setText("Região de Notificação");
            lblRegional.setVisible(true);
            cbRegional.setVisible(true);
            modelo = new DefaultComboBoxModel(this.session.retornaRegioes(this.cbUf.getSelectedItem().toString()));
            this.cbRegional.setModel(modelo);
        } else if (this.cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regional de Saúde")) {
            lblRegional.setText("Regional de Notificação");
            lblRegional.setVisible(true);
            cbRegional.setVisible(true);
            modelo = new DefaultComboBoxModel(this.session.retornaRegionais(this.cbUf.getSelectedItem().toString()));
            this.cbRegional.setModel(modelo);
        }

        modelo = new DefaultComboBoxModel(this.session.retornaMunicipios(this.cbUf.getSelectedItem().toString()));
        this.cbMunicipio.setModel(modelo);
    }//GEN-LAST:event_cbDesagregacaoActionPerformed

    private void cbRegionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRegionalActionPerformed
        ComboBoxModel modelo;
        int isRegiao = this.cbDesagregacao.getSelectedIndex();
        if (cbRegional.getSelectedItem() != null) {
            Vector<String> municipiosPactuacao = this.session.retornaMunicipiosPactuacao(isRegiao, this.cbUf.getSelectedItem().toString(), this.cbRegional.getSelectedItem().toString());
            if (!cbDesagregacao.getSelectedItem().toString().equals("Somente municípios")) {
                municipiosPactuacao.add(2, "NENHUM");
            }
            modelo = new DefaultComboBoxModel(municipiosPactuacao);
            this.cbMunicipio.setModel(modelo);
        }

/*        if (cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regional de Saúde")) {
            modelo = new DefaultComboBoxModel(this.session.retornaMunicipiosPactuacao(isRegiao, this.cbUf.getSelectedItem().toString(), this.cbRegional.getSelectedItem().toString()));
            this.cbMunicipio.setModel(modelo);
        } else if (cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regiões de Saúde")) {

            modelo = new DefaultComboBoxModel(this.session.retornaMunicipiosPQAVS(this.cbUf.getSelectedItem().toString(), this.cbRegional.getSelectedItem().toString()));
            this.cbMunicipio.setModel(modelo);
        }*/

 /*       if (cbRegional.getSelectedItem() != null) {
            Vector<String> municipiosPactuacao = this.session.retornaMunicipiosPQAVS(this.cbDesagregacao.getSelectedIndex(), this.cbUf.getSelectedItem().toString(), this.cbRegional.getSelectedItem().toString());
            municipiosPactuacao.add(2, "NENHUM");
            modelo = new DefaultComboBoxModel(municipiosPactuacao);
            this.cbMunicipio.setModel(modelo);
        }*/
    }//GEN-LAST:event_cbRegionalActionPerformed

    private void btCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCalcularActionPerformed
        if (!this.preencheuFormulario()) {
            return;
        }
        session = new SessionFacadeImpl();
        session.setBrasil(true);
        SessionFacadeImpl.setNomeDbf("VIOLENET");
        session.setTodosMunicipios(true);
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
        parametros.put("parArquivos", this.lblArquivosSelecionados.getText());
        parametros.put("parVariosArquivos", "sim");
        parametros.put("parIsRegiao", false);
        parametros.put("parIsRegional", false);
        if(!cbMunicipio.getSelectedItem().toString().equals("NENHUM"))
            parametros.put("parNenhum", false);//parametro para listar ou não os municípios
        parametros.put("parDesagregacao", cbDesagregacao.getSelectedItem().toString());
        parametros.put("parSgUf", cbUf.getSelectedItem().toString());
        parametros.put("parRegionalSaude", "");
        parametros.put("parRegiaoSaude", "");
        parametros.put("parAnoPeriodoAvaliacao",
                SinanDateUtil.dateToStringException(dataInicio.getDate(), "dd/MM/yyyy")
                + " a " + SinanDateUtil.dateToStringException(dataFim.getDate(), "dd/MM/yyyy"));

        if (cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regiões de Saúde")) {
            parametros.put("parIsRegiao", true);
            parametros.put("parRegiaoSaude", cbRegional.getSelectedItem().toString());
        } else if (cbDesagregacao.getSelectedItem().toString().equals("UF subdividida por Regional de Saúde")) {
            parametros.put("parRegionalSaude", cbRegional.getSelectedItem().toString());
            parametros.put("parIsRegional", true);
            session.setRegional(cbRegional.getSelectedItem().toString());
        }

        if (cbMunicipio.getSelectedItem().toString().isEmpty()) {
            parametros.put("municipioEspecifico", "");
        } else if (cbMunicipio.getSelectedItem().toString().equals("TODOS")) {
            parametros.put("municipioEspecifico", "TODOS");
        } else if (cbMunicipio.getSelectedItem().toString().equals("NENHUM")) {
            parametros.put("municipioEspecifico", "NENHUM");
            parametros.put("parNenhum",true);
        } else {
            parametros.put("municipioEspecifico", cbMunicipio.getSelectedItem().toString());
        }
        //parametros.put("parAnoPeriodoAvaliacao", SinanDateUtil.dateToStringException(dataInicio.getDate(), "dd/MM/yyyy") + " a " + SinanDateUtil.dateToStringException(dataFim.getDate(), "dd/MM/yyyy"));
        session.setParametros(parametros);
        session.setDataFim(SinanDateUtil.dateToStringException(dataFim.getDate(), "dd/MM/yyyy"));
        session.setDataInicio(SinanDateUtil.dateToStringException(dataInicio.getDate(), "dd/MM/yyyy"));
        session.setJprogress(prbStatus);
        session.setMunicipio(cbMunicipio.getSelectedItem().toString());
        if (cbRegional.getSelectedItem() != null) {
            session.setRegional(cbRegional.getSelectedItem().toString());
            parametros.put("parRegiaoSaude", cbRegional.getSelectedItem().toString());
        }

        session.setUf(cbUf.getSelectedItem().toString());
        session.setRelatorio("Violencia");
        session.execute();
    }//GEN-LAST:event_btCalcularActionPerformed

    private void btLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLimparActionPerformed
        cbMunicipio.removeAllItems();
        cbRegional.removeAllItems();
        cbUf.setSelectedIndex(0);
    }//GEN-LAST:event_btLimparActionPerformed

    private void cbMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMunicipioActionPerformed

    }//GEN-LAST:event_cbMunicipioActionPerformed

    private void btnSelecionarArquivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarArquivosActionPerformed
        JFileChooser fileopen = new JFileChooser();
        fileopen.setMultiSelectionEnabled(true);
        FiltroArquivo filtro = new FiltroArquivo();
        filtro.addExtension("dbf");
        filtro.setDescription("Arquivo DBF");
        filtro.addInicioNome("VIOLE");

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

    private void btnLimparSelecaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparSelecaoActionPerformed
        lblArquivosSelecionados.setText("Nenhum arquivo selecionado");
    }//GEN-LAST:event_btnLimparSelecaoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCalcular;
    private javax.swing.JButton btLimpar;
    private javax.swing.JButton btnLimparSelecao;
    private javax.swing.JButton btnSelecionarArquivos;
    private javax.swing.JComboBox cbDesagregacao;
    private javax.swing.JComboBox cbMunicipio;
    private javax.swing.JComboBox cbRegional;
    private javax.swing.JComboBox cbUf;
    private javax.swing.JCheckBox chkExportarDbf;
    private com.toedter.calendar.JDateChooser dataFim;
    private com.toedter.calendar.JDateChooser dataInicio;
    private javax.swing.JLabel jLabel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jpDataPrimeiroSintomas;
    private javax.swing.JLabel lblArquivosSelecionados;
    private javax.swing.JLabel lblAviso;
    private javax.swing.JLabel lblDesagregacao;
    private javax.swing.JLabel lblRegional;
    private javax.swing.JLabel lblUF;
    private javax.swing.JPanel pnlArquivos;
    private javax.swing.JProgressBar prbStatus;
    // End of variables declaration//GEN-END:variables
}
