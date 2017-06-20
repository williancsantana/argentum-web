/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DengueLetalidadeGrave.java
 *
 * Created on 30/06/2010, 22:53:11
 */
package com.org.view;

import com.org.facade.SessionFacadeImpl;
import com.org.negocio.Configuracao;
import com.org.negocio.FiltroArquivo;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;

/**
 *
 * @author geraldo
 */
public class DengueLetalidadeGrave extends javax.swing.JPanel {

    SessionFacadeImpl session = new SessionFacadeImpl();

    /** Creates new form DengueLetalidadeGrave */
    public DengueLetalidadeGrave() {
        initComponents();
        ComboBoxModel modelo;
        this.session.setBrasil(true);
        this.session.setTodosMunicipios(true);
        modelo = new DefaultComboBoxModel(this.session.retornaUFs());
        this.cbUf.setModel(modelo);
        Oportunidade.preencheAnos(cbAnoInicialMunicipio2, 2007);
        Oportunidade.preencheAnos(cbAnoFinalMunicipio, 2007);
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
        if (Integer.parseInt(this.spSemanaInicial.getValue().toString()) > 53 || Integer.parseInt(this.spSemanaFinal.getValue().toString()) > 53) {
            Master.mensagem("Semana deve ser menor que 54");
            return false;
        }
        int anoInicial = Integer.parseInt(cbAnoInicialMunicipio2.getSelectedItem().toString());
        int anoFinal = Integer.parseInt(cbAnoFinalMunicipio.getSelectedItem().toString());
        int semanaIncial = Integer.parseInt(this.spSemanaInicial.getValue().toString());
        int semanaFinal = Integer.parseInt(this.spSemanaFinal.getValue().toString());
        if (anoFinal < anoInicial) {
            Master.mensagem("Ano final deve ser maior que inicial");
            return false;
        } else {
            if ((anoInicial == anoFinal) && semanaFinal < semanaIncial) {
                Master.mensagem("Semana final deve ser maior que inicial");
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

        cbUf = new javax.swing.JComboBox();
        cbMunicipio = new javax.swing.JComboBox();
        btLimpar = new javax.swing.JButton();
        btCalcular = new javax.swing.JButton();
        lblUF = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        chkExportarDbf = new javax.swing.JCheckBox();
        jpDataPrimeiroSintomas = new javax.swing.JPanel();
        lblAviso = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        cbAnoInicialMunicipio2 = new javax.swing.JComboBox();
        spSemanaInicial = new javax.swing.JSpinner();
        jLabel28 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        spSemanaFinal = new javax.swing.JSpinner();
        cbAnoFinalMunicipio = new javax.swing.JComboBox();
        jLabel29 = new javax.swing.JLabel();
        cbRegional = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        prbStatus = new javax.swing.JProgressBar();
        pnlArquivos = new javax.swing.JPanel();
        btnSelecionarArquivos = new javax.swing.JButton();
        btnLimparSelecao = new javax.swing.JButton();
        lblArquivosSelecionados = new javax.swing.JLabel();

        cbUf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbUfActionPerformed(evt);
            }
        });

        cbMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbMunicipioActionPerformed(evt);
            }
        });

        btLimpar.setText("Limpar");
        btLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLimparActionPerformed(evt);
            }
        });

        btCalcular.setLabel("Calcular");
        btCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCalcularActionPerformed(evt);
            }
        });

        lblUF.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblUF.setText("UF de Residência:"); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Regional de Residência:"); // NOI18N

        chkExportarDbf.setText("Salvar resultado em DBF");

        jpDataPrimeiroSintomas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Período dos Primeiros Sintomas ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        lblAviso.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAviso.setForeground(new java.awt.Color(255, 0, 0));

        jLabel27.setText("DE   Ano:");

        cbAnoInicialMunicipio2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008" }));
        cbAnoInicialMunicipio2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAnoInicialMunicipio2ActionPerformed(evt);
            }
        });

        spSemanaInicial.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spSemanaInicialStateChanged(evt);
            }
        });

        jLabel28.setText("Semana:");

        jLabel30.setText("Semana:");

        spSemanaFinal.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spSemanaFinalStateChanged(evt);
            }
        });

        cbAnoFinalMunicipio.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008" }));
        cbAnoFinalMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAnoFinalMunicipioActionPerformed(evt);
            }
        });

        jLabel29.setText("ATÉ  Ano:");

        javax.swing.GroupLayout jpDataPrimeiroSintomasLayout = new javax.swing.GroupLayout(jpDataPrimeiroSintomas);
        jpDataPrimeiroSintomas.setLayout(jpDataPrimeiroSintomasLayout);
        jpDataPrimeiroSintomasLayout.setHorizontalGroup(
            jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDataPrimeiroSintomasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAviso)
                .addContainerGap(399, Short.MAX_VALUE))
            .addGroup(jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jpDataPrimeiroSintomasLayout.createSequentialGroup()
                    .addGap(101, 101, 101)
                    .addGroup(jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jpDataPrimeiroSintomasLayout.createSequentialGroup()
                            .addComponent(jLabel29)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cbAnoFinalMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel30)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(spSemanaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jpDataPrimeiroSintomasLayout.createSequentialGroup()
                            .addComponent(jLabel27)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cbAnoInicialMunicipio2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel28)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(spSemanaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(90, Short.MAX_VALUE)))
        );
        jpDataPrimeiroSintomasLayout.setVerticalGroup(
            jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDataPrimeiroSintomasLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(lblAviso)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jpDataPrimeiroSintomasLayout.createSequentialGroup()
                    .addGap(1, 1, 1)
                    .addGroup(jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel27)
                        .addComponent(cbAnoInicialMunicipio2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel28)
                        .addComponent(spSemanaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jpDataPrimeiroSintomasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel29)
                        .addComponent(cbAnoFinalMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel30)
                        .addComponent(spSemanaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        cbRegional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRegionalActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("Município de Residência:"); // NOI18N

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
            .addGap(0, 410, Short.MAX_VALUE)
            .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlArquivosLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pnlArquivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblArquivosSelecionados, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
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
                        .addComponent(btLimpar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(prbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jpDataPrimeiroSintomas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(pnlArquivos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
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
                .addComponent(jpDataPrimeiroSintomas, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void cbUfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbUfActionPerformed
        ComboBoxModel modelo;
        modelo = new DefaultComboBoxModel(this.session.retornaRegionais(this.cbUf.getSelectedItem().toString()));
        this.cbRegional.setModel(modelo);
        modelo = new DefaultComboBoxModel(this.session.retornaMunicipios(this.cbUf.getSelectedItem().toString()));
        this.cbMunicipio.setModel(modelo);
}//GEN-LAST:event_cbUfActionPerformed

    private void cbMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMunicipioActionPerformed
}//GEN-LAST:event_cbMunicipioActionPerformed

    private void btLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLimparActionPerformed
        cbMunicipio.removeAllItems();
        cbRegional.removeAllItems();
        cbUf.setSelectedIndex(0);
}//GEN-LAST:event_btLimparActionPerformed

    private void btCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCalcularActionPerformed
        if (!this.preencheuFormulario()) {
            return;
        }
        session = new SessionFacadeImpl();
        session.setBrasil(true);
        session.setTodosMunicipios(true);
        SessionFacadeImpl.setNomeDbf("DENGN");
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
        parametros.put("parVariosArquivos", "sim");
        parametros.put("parArquivos", this.lblArquivosSelecionados.getText());
        parametros.put("parAnoInicial", this.cbAnoInicialMunicipio2.getSelectedItem().toString());
        parametros.put("parAnoFinal", this.cbAnoFinalMunicipio.getSelectedItem().toString());
        parametros.put("parSemanaInicial", this.spSemanaInicial.getValue().toString());
        parametros.put("parSemanaFinal", this.spSemanaFinal.getValue().toString());
        session.setParametros(parametros);
        session.setJprogress(prbStatus);
        session.setMunicipio(cbMunicipio.getSelectedItem().toString());
        session.setRegional(cbRegional.getSelectedItem().toString());
        session.setUf(cbUf.getSelectedItem().toString());
        session.setRelatorio("DengueLetalidadeGrave");
        session.execute();
}//GEN-LAST:event_btCalcularActionPerformed

    private void cbRegionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRegionalActionPerformed
        ComboBoxModel modelo;
        modelo = new DefaultComboBoxModel(this.session.retornaMunicipios(this.cbUf.getSelectedItem().toString(), this.cbRegional.getSelectedItem().toString()));
        this.cbMunicipio.setModel(modelo);
}//GEN-LAST:event_cbRegionalActionPerformed

    private void cbAnoInicialMunicipio2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAnoInicialMunicipio2ActionPerformed
}//GEN-LAST:event_cbAnoInicialMunicipio2ActionPerformed

    private void spSemanaInicialStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spSemanaInicialStateChanged
        if (Integer.parseInt(spSemanaInicial.getValue().toString()) > 53) {
            spSemanaInicial.setValue(53);
        }
}//GEN-LAST:event_spSemanaInicialStateChanged

    private void spSemanaFinalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spSemanaFinalStateChanged
        if (Integer.parseInt(spSemanaFinal.getValue().toString()) > 53) {
            spSemanaFinal.setValue(53);
        }
}//GEN-LAST:event_spSemanaFinalStateChanged

    private void cbAnoFinalMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAnoFinalMunicipioActionPerformed
}//GEN-LAST:event_cbAnoFinalMunicipioActionPerformed

    private void btnSelecionarArquivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarArquivosActionPerformed
        JFileChooser fileopen = new JFileChooser();
        fileopen.setMultiSelectionEnabled(true);
        //        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivo DBF", "dbf");
        FiltroArquivo filtro = new FiltroArquivo();
        filtro.addExtension("dbf");
        filtro.setDescription("Arquivo DBF");
        filtro.addInicioNome("DENGN");

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
    private javax.swing.JComboBox cbAnoFinalMunicipio;
    private javax.swing.JComboBox cbAnoInicialMunicipio2;
    private javax.swing.JComboBox cbMunicipio;
    private javax.swing.JComboBox cbRegional;
    private javax.swing.JComboBox cbUf;
    private javax.swing.JCheckBox chkExportarDbf;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jpDataPrimeiroSintomas;
    private javax.swing.JLabel lblArquivosSelecionados;
    private javax.swing.JLabel lblAviso;
    private javax.swing.JLabel lblUF;
    private javax.swing.JPanel pnlArquivos;
    private javax.swing.JProgressBar prbStatus;
    private javax.swing.JSpinner spSemanaFinal;
    private javax.swing.JSpinner spSemanaInicial;
    // End of variables declaration//GEN-END:variables
}
