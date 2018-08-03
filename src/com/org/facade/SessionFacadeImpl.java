/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor. ff
 */
package com.org.facade;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.org.bd.Conexao;
import com.org.bd.DBFUtil;
import com.org.beans.RegiaoSaude;
import com.org.beans.RegiaoSaudePQAVS;
import com.org.beans.UFCoap;
import com.org.beans.UFPQAVS;
import com.org.model.classes.Agravo;
import com.org.model.classes.DBF;
import com.org.model.classes.UF;
import com.org.model.classes.agravos.OportunidadeMalariaPactuacao;
import com.org.model.classes.agravos.oportunidade.OportunidadeAgravoPQAVS;
import com.org.model.classes.agravos.oportunidade.OportunidadeAgravoCOAP;
import com.org.negocio.Configuracao;
import com.org.negocio.Util;
import com.org.service.AutoctoneMalariaService;
import com.org.service.OportunidadeCOAPService;
import com.org.service.OportunidadePQAVSService;
import com.org.service.OportunidadePQAVSServicePactuacao;
import com.org.service.RecebimentoLoteService;
import com.org.service.SemEpidPQAVSService;
import com.org.util.ArquivoUtils;
import com.org.util.SinanUtil;
import com.org.view.AidsTaxaCrianca;
import com.org.view.Completitude;
import com.org.view.DengueLetalidade;
import com.org.view.DengueLetalidadeGrave;
import com.org.view.Duplicidade;
import com.org.view.ExantematicaOportuno;
import com.org.view.TuberculoseCoorte;
import com.org.view.HepatiteBC;
import com.org.view.Master;
import com.org.view.Oportunidade;
import com.org.view.PFA15anos;
import com.org.view.HanseniaseCoorte;
import com.org.view.HepatiteB;
import com.org.view.OportunidadeCOAP;
import com.org.view.OportunidadePQAVS;
import com.org.view.Regularidade;
import com.org.view.SaudeTrabalhador;
import com.org.view.SifilisCongenitaIncidencia;
import com.org.view.hanseniaseListagem;
import com.org.view.*;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author geraldo
 */
public class SessionFacadeImpl extends SwingWorker<Void, Agravo> implements SessionFacade, EventListener {

    private static boolean dbf;
    private static String nomeDbf;
    private String uf;
    private String municipio;
    private String regional;
    private String relatorio;
    private JProgressBar jprogress;
    private JProgressBar jprogressGeral;
    private boolean exportarDbf;
    private List beans;
    private com.org.model.classes.Agravo agravo;
    private String dataInicio;
    private String dataFim;
    private JasperViewer viewer;
    private String versao = SinanUtil.getVersaoSinanRelatorios();
    private Map parametros;
    private boolean brasil;//indica se acrescenta o brasil como selecao
    private boolean temOpcaoTodasUFs = true;//indica se acrescenta TODAS no campo UF
    private boolean todosMunicipios;//indica se acrescenta Listar municípios na combo de municipios
    //variaveis para oportunidade
    private String dataAvaliacao;
    private String anoAvaliado;
    private String dtInicioAvaliacao;
    private String dtFimAvaliacao;
    private String nomeAgravo;
    private boolean temListagem;
    private boolean auxiliar;

    //variáveis para recebimento de lotes
    private String dtInicioTrasnf;
    private String dtFimTransf;
    private String dtInicioReceb;
    private String dtFimReceb;

    private String formataSemana(String semana) {
        if (Integer.parseInt(semana) < 10) {
            return "0" + semana;
        } else {
            return semana;
        }
    }

    public Map populaSemana(Map parametros) {
        int semanaInicio = Integer.parseInt(parametros.get("parSemanaInicial").toString());
        int semanaFim = Integer.parseInt(parametros.get("parSemanaFinal").toString());
        String semanas = "";

        if (parametros.get("parAnoInicial").toString().equals(parametros.get("parAnoFinal").toString())) {
            for (int i = semanaInicio; i <= semanaFim; i++) {
                semanas = semanas + "'" + parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)) + "'";
                if (i < semanaFim) {
                    semanas = semanas + ",";
                }
            }
        } else {
            for (int i = semanaInicio; i <= 53; i++) {
                semanas = semanas + "'" + parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)) + "'";
                if (i < 53) {
                    semanas = semanas + ",";
                }
            }
            for (int i = 1; i <= semanaFim; i++) {
                semanas = semanas + "'" + parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)) + "'";
                if (i < semanaFim) {
                    semanas = semanas + ",";
                }
            }
        }
        parametros.put("parDataInicio", semanas);
        parametros.put("f", "1");
        return parametros;
    }

    public void geraRelatorio(String uf, String municipio) {

        Util util = new Util();
        Conexao con = util.conectarSiceb();
        JRDataSource jrds = null;
        Connection conexao = null;
        if (!isDbf()) {
            con.conect();
            conexao = con.getC();
        }
        //monta as header das colunas
        agravo.setColunas(agravo.getColunas());
        //seta a barra de status
        agravo.setBarraStatus(jprogress);
        agravo.setBarraStatusGeral(jprogressGeral);
        //busca parametros
        if (parametros == null) {
            parametros = new HashMap();
        }
        parametros.putAll(agravo.getParametros());

        parametros.put("parConfig", "");
        parametros.put("parVersao", getVersao());
        parametros.put("municipios", "nao");

        parametros.put("CABECALHO1", "República Federativa do Brasil - Ministério da Saúde");
        parametros.put("CABECALHO2", "Sistema de Informação de Agravos de Notificação - Sinan");
        parametros.put("RODAPE1", "SINAN Relatórios - Versão " + SinanUtil.getVersaoSinanRelatorios());

        Date data = new Date();
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        parametros.put("parDataAtual", formatador.format(data));
        if (relatorio.equals("DengueLetalidadeGrave")) {
            parametros.put("parPeriodo", "de ano " + parametros.get("parAnoInicial").toString() + " semana " + parametros.get("parSemanaInicial").toString() + " \na ano " + parametros.get("parAnoFinal").toString() + " semana " + parametros.get("parSemanaFinal").toString());
            parametros = agravo.populaSemana(parametros);
        }
        if (relatorio.equals("SaudeTrabalhador")) {
            if (parametros.get("parDiscriminarPorAgravo").toString().equals("true")) {
                parametros.put("parTituloLocal", "Agravo");
            }
        }
        try {
            JasperPrint impressao = null;
            if (uf.equals("Brasil") || uf.equals("TODAS")) {
                if (municipio.equals("TODOS")) {
                    parametros.put("municipios", "sim");
                }
                parametros.put("parUf", "brasil");
                if (relatorio.equals("AutoctonesMalariaPactuacao")) {
                    if (uf.equals("TODAS")) {
                        parametros.put("parUf", "TODAS");
                    }
                }

                parametros.put("parSgUf", uf);
                agravo.getTaxaEstado(conexao, parametros);
                beans = agravo.getBeanMunicipios(conexao, parametros);

                parametros.put("parCompletitude", agravo.getTextoCompletitude() + " " + agravo.getPercentualCompletitude());
            } else {
                parametros.put("parSgUf", uf);
                parametros.put("parUf", String.valueOf(getCodigoUf(uf)));
                if (parametros.get("parRegiaoSaude") != null && parametros.get("parRegiaoSaude") != "") {
                    parametros.put("parCodRegiaoSaude", getCodRegiaoSaude(parametros.get("parRegiaoSaude").toString(), uf));
                    parametros.put("parNomeRegiao", getRegional());
                }
                if (municipio.equals("TODOS") || municipio.equals("-- Selecione --")) {
                    if (regional == null) {
                        regional = "-- Selecione --";
                    }
                    if (municipio.equals("-- Selecione --") && (regional.equals("-- Selecione --") || regional.equals("TODAS"))) {
                        //relatorio para uma uf especifica 
                        parametros.put("nivelAgregacao", "UF");
                        parametros.put("taxaEstadual", agravo.getTaxaEstado(conexao, parametros));
                        parametros.put("parCompletitude", agravo.getCompletitude(conexao, parametros));
                    } else {
                        parametros.put("nivelAgregacao", "UF");
                        parametros.put("parNomeMunicipio", municipio);
                        parametros.put("parNomeRegional", getRegional());
                        parametros.put("parCodRegional", getCodRegional(getRegional(), uf, relatorio));
                        parametros.put("municipios", "sim");
                        parametros.put("taxaEstadual", agravo.getTaxaEstado(conexao, parametros));
                        parametros.put("parCompletitude", agravo.getCompletitude(conexao, parametros));

                    }
                    beans = agravo.getBeansEstadoEspecifico(conexao, parametros);
                } else {
                    parametros.put("nivelAgregacao", "Municipio");
                    parametros.put("parNomeMunicipio", municipio);
                    parametros.put("parMunicipio", getCodMunicipio(municipio, uf));
                    parametros.put("taxaEstadual", agravo.getTaxaEstado(conexao, parametros));
                    if (!relatorio.equals("RecebimentoLote")) {
                        parametros.put("parCompletitude", agravo.getCompletitude(conexao, parametros));
                        beans = agravo.getBeansMunicipioEspecifico(conexao, parametros);
                    }
                }
            }

            //montar o cFabeçalho do relatorio
            //1-nível de agregacao
            if (parametros.get("parNenhum") != null) {
                if ((Boolean) parametros.get("parNenhum")) {
                    parametros.put("parDescricaoReg", "");
                } else {
                    parametros.put("parDescricaoReg", "Regional de Saúde");
                    if ((Boolean) parametros.get("parIsRegiao") != null && (Boolean) parametros.get("parIsRegiao")) {
                        parametros.put("parDescricaoReg", "Região de Saúde");
                    }
                }
            }

            String nomeReg = "Regional";
            if ((Boolean) parametros.get("parIsRegiao") != null && (Boolean) parametros.get("parIsRegiao")) {
                nomeReg = "Região";
            }
            if (getRegional() != null) {
                parametros.put("parNomeMunicipio", "Nível de Agregação:\nUF " + agravo.getTipoAgregacao() + ": " + uf + "\n" + nomeReg + " " + agravo.getTipoAgregacao() + ":" + getRegional() + "\nMunicípio " + agravo.getTipoAgregacao() + ":" + municipio);
            } else {
                parametros.put("parNomeMunicipio", "Nível de Agregação:\nUF " + agravo.getTipoAgregacao() + ": " + uf + "\nMunicípio " + agravo.getTipoAgregacao() + ":" + municipio);
            }

            if (!relatorio.equals("Oportunidade") && !relatorio.equals("OportunidadePQAVSPactuacao") && !relatorio.equals("OportunidadeCOAP") && !relatorio.equals("OportunidadePQAVS") && !relatorio.equals("RecebimentoLote")) {
                parametros.put("parPeriodo", "Período " + agravo.getPeriodo() + ":\n" + parametros.get("parPeriodo"));
            }
            if (!relatorio.equals("RecebimentoLote") && parametros.get("parCompletitude").equals(" null")) {
                parametros.put("parCompletitude", "");
            }

            if (relatorio != null && relatorio.equals("RecebimentoLote")) {
                //Realizar aqui o teste de agrupamento por Região de Saúde

                List teste = new ArrayList();

                RecebimentoLoteService recebimentoLoteService = new RecebimentoLoteService();
                teste = recebimentoLoteService.getCalculaResultado(parametros, agravo);
            }

            if (relatorio != null && relatorio.equals("SemEpidPQAVS")) {
                //Realizar aqui o teste de agrupamento por Região de Saúde
                SemEpidPQAVSService semEpidPQAVSService = new SemEpidPQAVSService();
                List<RegiaoSaudePQAVS> listaRegiaoSaude = new ArrayList<RegiaoSaudePQAVS>();
                List<UFPQAVS> listaUF = new ArrayList<UFPQAVS>();
                semEpidPQAVSService.calcularResultado(beans);
                if (parametros.get("parDesagregacao").equals("UF subdividida por Regiões de Saúde")) {
                    parametros.put("TITULO_COLUNA", "UF       Região de Saúde");
                    parametros.put("QTDE_REG_MUNIC_AGR", "Regiões");
                    listaRegiaoSaude = semEpidPQAVSService.converterMapaRegiaoSaudeEmLista(semEpidPQAVSService.agruparRegiaoSaude2(beans), parametros);
                    listaUF = semEpidPQAVSService.converterMapaUFRegiaoSaudeEmLista(semEpidPQAVSService.agruparUFRegiaoSaude2(listaRegiaoSaude), parametros);
                    semEpidPQAVSService.gerarRelatorioPQAVSUFRegiaoSaude(listaUF, parametros, 0, null);
                    if (parametros.get("exportarDBF").equals(true)) {
                        OportunidadeAgravoPQAVS bean;
                        List<OportunidadeAgravoPQAVS> listaBean = new ArrayList<OportunidadeAgravoPQAVS>();
                        for (UFPQAVS item : listaUF) {
                            for (RegiaoSaudePQAVS regiao : item.getLista()) {
                                bean = new OportunidadeAgravoPQAVS();
                                bean.setUf(regiao.getUf());
                                bean.setCodRegiaoSaude(regiao.getCodRegiaoSaude());

                                bean.setRegiaoSaude(regiao.getNmAgravo());// VERIFICAR ESTE TRECHO
                                bean.setNmAgravo("");
                                bean.setQtdOportuno(regiao.getQtdOportuno());
                                bean.setTotal(regiao.getTotal());
                                listaBean.add(bean);
                            }
                        }
                        semEpidPQAVSService.gerarDBFPQAVSDefineCampos(listaBean);
                    }
                } else {
                    if (parametros.get("parDiscriminarPorAgravo").equals(true)) {
                        parametros.put("TITULO_COLUNA", "                Agravo");
                        parametros.put("QTDE_REG_MUNIC_AGR", "Agravos");
                    } else {
                        parametros.put("TITULO_COLUNA", "UF       Região de Saúde");
                        parametros.put("QTDE_REG_MUNIC_AGR", "Municípios");
                    }

                    listaRegiaoSaude = semEpidPQAVSService.converterMapaRegiaoSaudeEmLista(semEpidPQAVSService.agruparRegiaoSaude(beans), parametros);
                    semEpidPQAVSService.gerarRelatorioPQAVS(listaRegiaoSaude, parametros, 0);
                    if (parametros.get("exportarDBF").equals(true)) {
                        beans.remove(beans.size() - 1);
                        semEpidPQAVSService.gerarDBFPQAVSDefineCampos(beans);
                        // CHAMAR EXPORTAR PASSA beans como parâmetro
                    }
//                    List<OportunidadeAgravoPQAVS> listaTDN = new ArrayList<OportunidadeAgravoPQAVS>();
//                    listaTDN = beans;
//                    
//                    for (OportunidadeAgravoPQAVS bean : listaTDN) {
//                        if(!SinanUtil.verificaMunicipioIgnorado(bean))
//                            SinanUtil.imprimirConsole(bean.getCodAgravo() +"; "+ bean.getQtdOportuno() +"; "+ bean.getTotal() +"; "+ bean.getNmAgravo());
//                    }

                }
            }

            if (relatorio != null && relatorio.equals("OportunidadePQAVS")) {
                //Realizar aqui o teste de agrupamento por Região de Saúde
                OportunidadePQAVSService oportunidadePQAVSService = new OportunidadePQAVSService();
                List<RegiaoSaudePQAVS> listaRegiaoSaude = new ArrayList<RegiaoSaudePQAVS>();
                List<UFPQAVS> listaUF = new ArrayList<UFPQAVS>();
                if (parametros.get("parDesagregacao").equals("UF subdividida por Regiões de Saúde")) {
                    parametros.put("TITULO_COLUNA", "UF       Região de Saúde");
                    parametros.put("QTDE_REG_MUNIC_AGR", "Regiões");
                    listaRegiaoSaude = oportunidadePQAVSService.converterMapaRegiaoSaudeEmLista(oportunidadePQAVSService.agruparRegiaoSaude2(beans), parametros);
                    listaUF = oportunidadePQAVSService.converterMapaUFRegiaoSaudeEmLista(oportunidadePQAVSService.agruparUFRegiaoSaude2(listaRegiaoSaude), parametros);
                    oportunidadePQAVSService.gerarRelatorioPQAVSUFRegiaoSaude(listaUF, parametros, 0, null);
                    if (parametros.get("exportarDBF").equals(true)) {
                        OportunidadeAgravoPQAVS bean;
                        List<OportunidadeAgravoPQAVS> listaBean = new ArrayList<OportunidadeAgravoPQAVS>();
                        for (UFPQAVS item : listaUF) {
                            for (RegiaoSaudePQAVS regiao : item.getLista()) {
                                bean = new OportunidadeAgravoPQAVS();
                                bean.setUf(regiao.getUf());
                                bean.setCodRegiaoSaude(regiao.getCodRegiaoSaude());
                                bean.setRegiaoSaude(regiao.getNmAgravo());// VERIFICAR ESTE TRECHO
                                bean.setNmAgravo("");
                                bean.setQtdOportuno(regiao.getQtdOportuno());
                                bean.setTotal(regiao.getTotal());
                                listaBean.add(bean);
                            }
                        }
                        oportunidadePQAVSService.gerarDBFPQAVSDefineCampos(listaBean);
                    }
                } else {
                    if (parametros.get("parDiscriminarPorAgravo").equals(true)) {
                        parametros.put("TITULO_COLUNA", "                Agravo");
                        parametros.put("QTDE_REG_MUNIC_AGR", "Agravos");
                    } else {
                        parametros.put("TITULO_COLUNA", "UF       Região de Saúde");
                        parametros.put("QTDE_REG_MUNIC_AGR", "Municípios");
                    }

                    listaRegiaoSaude = oportunidadePQAVSService.converterMapaRegiaoSaudeEmLista(oportunidadePQAVSService.agruparRegiaoSaude(beans), parametros);
                    oportunidadePQAVSService.gerarRelatorioPQAVS(listaRegiaoSaude, parametros, 0);
                    if (parametros.get("exportarDBF").equals(true)) {
                        beans.remove(beans.size() - 1);
                        oportunidadePQAVSService.gerarDBFPQAVSDefineCampos(beans);
                        // CHAMAR EXPORTAR PASSA beans como parâmetro
                    }
//                    List<OportunidadeAgravoPQAVS> listaTDN = new ArrayList<OportunidadeAgravoPQAVS>();
//                    listaTDN = beans;
//                    
//                    for (OportunidadeAgravoPQAVS bean : listaTDN) {
//                        if(!SinanUtil.verificaMunicipioIgnorado(bean))
//                            SinanUtil.imprimirConsole(bean.getCodAgravo() +"; "+ bean.getQtdOportuno() +"; "+ bean.getTotal() +"; "+ bean.getNmAgravo());
//                    }

                }
            }

            if (relatorio != null && relatorio.equals("OportunidadePQAVSPactuacao")) {
                //Realizar aqui o teste de agrupamento por Região de Saúde
                OportunidadePQAVSServicePactuacao oportunidadePQAVSService = new OportunidadePQAVSServicePactuacao();
                List<RegiaoSaudePQAVS> listaRegiaoSaude = new ArrayList<RegiaoSaudePQAVS>();
                List<UFPQAVS> listaUF = new ArrayList<UFPQAVS>();
                if (!parametros.get("parDiscriminarPorAgravo").equals(true)) {
                    if (parametros.get("parDesagregacao").equals("UF subdividida por Regiões de Saúde")) {

                        if (parametros.get("parNenhum").toString().equals("true")) {
                            parametros.put("TITULO_COLUNA", "UF       Região de Saúde");
                            parametros.put("QTDE_REG_MUNIC_AGR", "Regiões");
                            listaRegiaoSaude = oportunidadePQAVSService.converterMapaRegiaoSaudeEmLista(oportunidadePQAVSService.agruparRegiaoSaude2(beans), parametros);
                            listaUF = oportunidadePQAVSService.converterMapaUFRegiaoSaudeEmLista(oportunidadePQAVSService.agruparUFRegiaoSaude2(listaRegiaoSaude), parametros);
                            oportunidadePQAVSService.gerarRelatorioPQAVSUFRegiaoSaude(listaUF, parametros, 0, null);

                            if (parametros.get("exportarDBF").equals(true)) {
                                OportunidadeAgravoPQAVS bean;
                                List<OportunidadeAgravoPQAVS> listaBean = new ArrayList<OportunidadeAgravoPQAVS>();
                                for (UFPQAVS item : listaUF) {
                                    for (RegiaoSaudePQAVS regiao : item.getLista()) {
                                        bean = new OportunidadeAgravoPQAVS();
                                        bean.setUf(regiao.getUf());
                                        bean.setCodRegiaoSaude(regiao.getCodRegiaoSaude());
                                        bean.setRegiaoSaude(regiao.getNmAgravo());// VERIFICAR ESTE TRECHO
                                        bean.setNmAgravo("");
                                        bean.setQtdOportuno(regiao.getQtdOportuno());
                                        bean.setTotal(regiao.getTotal());
                                        listaBean.add(bean);
                                    }
                                }
                                oportunidadePQAVSService.gerarDBFPQAVSDefineCampos(listaBean, parametros);
                            }
                        } else {
                            parametros.put("TITULO_COLUNA", "UF       Região de Saúde");
                            parametros.put("QTDE_REG_MUNIC_AGR", "Municípios");

                            listaRegiaoSaude = oportunidadePQAVSService.converterMapaRegiaoSaudeEmLista(oportunidadePQAVSService.agruparRegiaoSaude(beans), parametros);
                            oportunidadePQAVSService.gerarRelatorioPQAVS(listaRegiaoSaude, parametros, 0);
                            if (parametros.get("exportarDBF").equals(true)) {
                                beans.remove(beans.size() - 1);
                                oportunidadePQAVSService.gerarDBFPQAVSDefineCampos(beans, parametros);
                                // CHAMAR EXPORTAR PASSA beans como parâmetro
                            }
                        }

                    } else if (parametros.get("parDesagregacao").equals("UF subdividida por Regionais de Saúde")) {

                        if (parametros.get("parNenhum").toString().equals("true")) {
                            parametros.put("TITULO_COLUNA", "UF       Regionais de Saúde");
                            parametros.put("QTDE_REG_MUNIC_AGR", "Regionais");
                            listaRegiaoSaude = oportunidadePQAVSService.converterMapaRegionalSaudeEmLista(oportunidadePQAVSService.agruparRegionalSaude2(beans), parametros);
                            listaUF = oportunidadePQAVSService.converterMapaUFRegiaoSaudeEmLista(oportunidadePQAVSService.agruparUFRegiaoSaude2(listaRegiaoSaude), parametros);
                            oportunidadePQAVSService.gerarRelatorioPQAVSUFRegiaoSaude(listaUF, parametros, 0, null);

                            if (parametros.get("exportarDBF").equals(true)) {
                                OportunidadeAgravoPQAVS bean;
                                List<OportunidadeAgravoPQAVS> listaBean = new ArrayList<OportunidadeAgravoPQAVS>();
                                for (UFPQAVS item : listaUF) {
                                    for (RegiaoSaudePQAVS regiao : item.getLista()) {
                                        bean = new OportunidadeAgravoPQAVS();
                                        bean.setUf(regiao.getUf());
                                        bean.setCodRegiaoSaude(regiao.getCodRegiaoSaude());
                                        bean.setRegiaoSaude(regiao.getNmAgravo());// VERIFICAR ESTE TRECHO
                                        bean.setNmAgravo("");
                                        bean.setQtdOportuno(regiao.getQtdOportuno());
                                        bean.setTotal(regiao.getTotal());
                                        listaBean.add(bean);
                                    }
                                }
                                oportunidadePQAVSService.gerarDBFPQAVSDefineCampos(listaBean, parametros);
                            }
                        } else {
                            parametros.put("TITULO_COLUNA", "UF       Regional de Saúde");
                            parametros.put("QTDE_REG_MUNIC_AGR", "Municípios");

                            listaRegiaoSaude = oportunidadePQAVSService.converterMapaRegiaoSaudeEmLista(oportunidadePQAVSService.agruparRegionalSaude(beans), parametros);
                            oportunidadePQAVSService.gerarRelatorioPQAVS(listaRegiaoSaude, parametros, 0);
                            if (parametros.get("exportarDBF").equals(true)) {
                                beans.remove(beans.size() - 1);
                                oportunidadePQAVSService.gerarDBFPQAVSDefineCampos(beans, parametros);
                                // CHAMAR EXPORTAR PASSA beans como parâmetro
                            }
                        }

                    } else if (parametros.get("parDesagregacao").equals("Somente municípios")) {
                        parametros.put("TITULO_COLUNA", "UF       Municípios");
                        parametros.put("QTDE_REG_MUNIC_AGR", "Quantidade");
                        listaRegiaoSaude = oportunidadePQAVSService.converterMapaRegiaoSaudeEmLista(oportunidadePQAVSService.agruparRegionalSaude(beans), parametros);
                        oportunidadePQAVSService.gerarRelatorioPQAVS(listaRegiaoSaude, parametros, 0);
                        if (parametros.get("exportarDBF").equals(true)) {
                                beans.remove(beans.size() - 1);
                                oportunidadePQAVSService.gerarDBFPQAVSDefineCampos(beans, parametros);
                                // CHAMAR EXPORTAR PASSA beans como parâmetro
                                // ADEQUAR AS COLUNAS CONFORME DESAGREGAÇÃO
                        }
                    }
                } else {
                    parametros.put("TITULO_COLUNA", "                Agravo");
                    parametros.put("QTDE_REG_MUNIC_AGR", "Agravos");
                    listaRegiaoSaude = oportunidadePQAVSService.converterMapaRegiaoSaudeEmLista(oportunidadePQAVSService.agruparRegiaoSaude(beans), parametros);
                    oportunidadePQAVSService.gerarRelatorioPQAVS(listaRegiaoSaude, parametros, 0);
                    if (parametros.get("exportarDBF").equals(true)) {
                        beans.remove(beans.size() - 1);
                        oportunidadePQAVSService.gerarDBFPQAVSDefineCampos(beans, parametros);
                        // CHAMAR EXPORTAR PASSA beans como parâmetro
                    }

                }
                
                if (this.isTemListagem()) {
                    gerarRelatorioListagem(agravo.getListagemCasosPQAVS());
                }
                /* else {
                    if (parametros.get("parDiscriminarPorAgravo").equals(true)) {
                        parametros.put("TITULO_COLUNA", "                Agravo");
                        parametros.put("QTDE_REG_MUNIC_AGR", "Agravos");
                    }

                    listaRegiaoSaude = oportunidadePQAVSService.converterMapaRegiaoSaudeEmLista(oportunidadePQAVSService.agruparRegiaoSaude(beans), parametros);
                    oportunidadePQAVSService.gerarRelatorioPQAVS(listaRegiaoSaude, parametros, 0);

                    if (parametros.get("exportarDBF").equals(true)) {
                        beans.remove(beans.size() - 1);
                        oportunidadePQAVSService.gerarDBFPQAVSDefineCampos(beans, parametros);
                        // CHAMAR EXPORTAR PASSA beans como parâmetro
                    }
                }*/
            }

            if (relatorio != null && relatorio.equals("OportunidadeCOAP")) {
                //Realizar aqui o teste de agrupamento por Região de Saúde
                OportunidadeCOAPService oportunidadeCOAPService = new OportunidadeCOAPService();
                List<RegiaoSaude> listaRegiaoSaude = new ArrayList<RegiaoSaude>();
                List<UFCoap> listaUF = new ArrayList<UFCoap>();
                if (parametros.get("parDesagregacao").equals("UF subdividida por Regiões de Saúde")) {
                    parametros.put("TITULO_COLUNA", "UF       Região de Saúde");
                    parametros.put("QTDE_REG_MUNIC_AGR", "Regiões");
                    listaRegiaoSaude = oportunidadeCOAPService.converterMapaRegiaoSaudeEmLista(oportunidadeCOAPService.agruparRegiaoSaude2(beans), parametros);
                    listaUF = oportunidadeCOAPService.converterMapaUFRegiaoSaudeEmLista(oportunidadeCOAPService.agruparUFRegiaoSaude2(listaRegiaoSaude), parametros);
                    oportunidadeCOAPService.gerarRelatorioCOAPUFRegiaoSaude(listaUF, parametros, 0, null);
                    if (parametros.get("exportarDBF").equals(true)) {
                        OportunidadeAgravoCOAP bean;
                        List<OportunidadeAgravoCOAP> listaBean = new ArrayList<OportunidadeAgravoCOAP>();
                        for (UFCoap item : listaUF) {
                            for (RegiaoSaude regiao : item.getLista()) {
                                bean = new OportunidadeAgravoCOAP();
                                bean.setUf(regiao.getUf());
                                bean.setCodRegiaoSaude(regiao.getCodRegiaoSaude());

                                bean.setRegiaoSaude(regiao.getNmAgravo());// VERIFICAR ESTE TRECHO
                                bean.setNmAgravo("");
                                bean.setQtdOportuno(regiao.getQtdOportuno());
                                bean.setTotal(regiao.getTotal());
                                listaBean.add(bean);
                            }
                        }
                        oportunidadeCOAPService.gerarDBFCOAPDefineCampos(listaBean);
                    }
                } else {
                    if (parametros.get("parDiscriminarPorAgravo").equals(true)) {
                        parametros.put("TITULO_COLUNA", "                Agravo");
                        parametros.put("QTDE_REG_MUNIC_AGR", "Agravos");
                    } else {
                        parametros.put("TITULO_COLUNA", "UF       Região de Saúde");
                        parametros.put("QTDE_REG_MUNIC_AGR", "Municípios");
                    }

                    listaRegiaoSaude = oportunidadeCOAPService.converterMapaRegiaoSaudeEmLista(oportunidadeCOAPService.agruparRegiaoSaude(beans), parametros);
                    oportunidadeCOAPService.gerarRelatorioCOAP(listaRegiaoSaude, parametros, 0);
                    if (parametros.get("exportarDBF").equals(true)) {
                        beans.remove(beans.size() - 1);
                        if(parametros.get("parDiscriminarPorAgravo").equals(true))
                            oportunidadeCOAPService.gerarDBFAgravoCOAPCampos(beans);
                        else
                            oportunidadeCOAPService.gerarDBFCOAPDefineCampos(beans);
                        // CHAMAR EXPORTAR PASSA beans como parâmetro
                    }
//                    List<OportunidadeAgravoCOAP> listaTDN = new ArrayList<OportunidadeAgravoCOAP>();
//                    listaTDN = beans;
//                    
//                    for (OportunidadeAgravoCOAP bean : listaTDN) {
//                        if(!SinanUtil.verificaMunicipioIgnorado(bean))
//                            SinanUtil.imprimirConsole(bean.getCodAgravo() +"; "+ bean.getQtdOportuno() +"; "+ bean.getTotal() +"; "+ bean.getNmAgravo());
//                    }
//                    

                }
            } else {
                jrds = new JRBeanArrayDataSource(beans.toArray());
                URL arquivo;
                if (parametros.get("parJasper") == null) {
                    arquivo = getClass().getResource(agravo.getCaminhoJasper());
                } else {
                    arquivo = getClass().getResource("/com/org/relatorios/" + parametros.get("parJasper").toString() + ".jasper");
                }
                //retirar o fator de multiplicacao
                if (parametros.get("parFator") != null) {
                    if (parametros.get("parFator").toString().equals("1")) {
                        parametros.put("parFator", "Não se aplica");
                    }
                }
                //verifica se tem o parametro de arquivos
                if (parametros.get("parVariosArquivos") != null && isDbf()) {
                    parametros.put("parArquivos", "Arquivos selecionados:\n " + parametros.get("parArquivos").toString());
                } else {
                    if (!relatorio.equals("Regularidade")) {
                        parametros.put("parArquivos", "");
                    }
                }
                impressao = JasperFillManager.fillReport(arquivo.openStream(), parametros, jrds);
                viewer = new JasperViewer(impressao, false);
                if (this.isTemListagem()) {
                    gerarRelatorioListagem(agravo.getListagemCasos());
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    private void gerarRelatorioListagem(List listagem) throws IOException, JRException {
        parametros.put("parTitulo1", "Listagem de casos de doenças de notificação compulsória não encerrados ou inconclusivos");
        JRDataSource jrdsListagem = new JRBeanArrayDataSource(listagem.toArray());
        URL arquivoListagem = getClass().getResource("/com/org/relatorios/listagemOportunidade.jasper");
        JasperPrint imprimirListagem = JasperFillManager.fillReport(arquivoListagem.openStream(), parametros, jrdsListagem);
        JasperViewer viewerListagem = new JasperViewer(imprimirListagem, false);
        viewerListagem.setVisible(true);
    }

    public JPanel retornaPanelPactuacao(String relatorio) {
        JPanel panel = null;
        System.out.println("");
        if (relatorio.equals("Número de casos novos de AIDS em menores de 5 anos")) {
            panel = new AidsIndicadorCriancaPactuacao();
            this.relatorio = "AidsIndicadorCriancaPactuacao";
        } else if (relatorio.equals("Número de casos novos de sífilis congênita em menores de 1 ano de idade")) {
            panel = new SifilisCongenitaIncidenciaPactuacao();
            this.relatorio = "SifilisCongenitaIncidenciaPactuacao";

        } else if (relatorio.equals("Proporção de casos DNCI encerrados em até 60 dias após notificação")) {
            panel = new OportunidadePQAVSPactuacao();
            this.relatorio = "OportunidadePQAVSPactuacao";
        } else if (relatorio.equals("Número de casos autóctones de malária")) {
            panel = new AutoctonesMalariaPactuacao();
            this.relatorio = "AutoctonesMalariaPactuacao";
        } else if (relatorio.equals("Proporção de casos de malária que iniciaram tratamento em tempo oportuno")) {
            panel = new com.org.view.OportunidadeMalariaPactuacao();
            this.relatorio = "OportunidadeMalariaPactuacao";
        } else if (relatorio.equals("Proporção de contatos examinados de casos novos de tuberculose")) {
            panel = new com.org.view.ContatosExaminadosTuberculosePactuacao();
            this.relatorio = "ExaminadosTuberculosePactuacao";
        } else if (relatorio.equals("Proporção de notificações de Violência com o campo raça/cor preenchido de forma válida")) {
            panel = new Violencia();
            this.relatorio = "Violencia";
        } else if (relatorio.equals("Número de semanas epidemiológicas com informação")) {
            panel = new SemanaEpidemiologicaPactuacao();
            this.relatorio = "SemanaEpidemiologicaPactuacao";
        }
        if (relatorio.equals("Proporção de cura dos casos novos de hanseníase diagnosticados nos anos das coortes")) {
            panel = new HanseniaseCoorteCuraPactuacao();
            this.relatorio = "HanseniaseCoorteCura";
        } else if (relatorio.equals("Proporção de contatos examinados de casos novos de hanseníase")) {
            panel = new com.org.view.ContatosExaminadosHanseniasePactuacao();
            this.relatorio = "ExaminadosHanseniasePactuacao";
        } else if (relatorio.equals("Proporção de preenchimento do campo ocupação")) {
            panel = new com.org.view.PreenchimentoOcupacaoTrabalhadorPactuacao();
            this.relatorio = "PreenchimentoOcupacaoTrabalhadorPactuacao";
        }

        return panel;
    }

    public JPanel retornaPanel(String relatorio) {
        JPanel panel = null;
        //verifica qual relatorio foi selecionado
        if (relatorio.equals("Listagem de notificações de prováveis faltosos e abandono do tratamento de hanseníase")) {
            panel = new hanseniaseListagem();
            this.relatorio = "ListagemHanseniase";
        }
        if (relatorio.equals("Taxa de letalidade por Febre Hemorrágica Dengue")) {
            panel = new DengueLetalidade();
            this.relatorio = "DengueLetalidade";
        }
        if (relatorio.equals("Taxa de incidência de aids em menores de 5 anos de idade")) {
            panel = new AidsTaxaCrianca();
            this.relatorio = "AidsTaxaCrianca";
        }
        if (relatorio.equals("Proporção de doenças exantemáticas investigados oportunamente") || relatorio.equals("Proporção de doenças exantemáticas investigados oportunamente (PAVS 2010/2011)")) {
            panel = new ExantematicaOportuno("ExantematicaOportuno");
//            this.relatorio = ;
        }
        if (relatorio.equals("Proporção de doenças exantemáticas investigados oportuna e adequadamente")) {
            panel = new ExantematicaOportuno("ExantematicaOportunoAdequado");
//            this.relatorio = ;
        }
        if (relatorio.equals("Percentual de casos de hepatites B e C")) {
            panel = new HepatiteBC();
            this.relatorio = "HepatiteBC";
        }
        if (relatorio.equals("Percentual de casos de hepatites B confirmados por sorologia")) {
            panel = new HepatiteB();
            this.relatorio = "HepatiteB";
        }
        if (relatorio.equals("Taxa de notificação de casos de PFA em menores de 15 anos") || relatorio.equals("Taxa de notificação de casos de PFA em menores de 15 anos (PAVS 2010/2011)")) {
            panel = new PFA15anos();
            this.relatorio = "PFA15anos";
        }
        if (relatorio.equals("Situação da coorte de casos novos de Tuberculose")) {
            panel = new TuberculoseCoorte();
            this.relatorio = "TuberculoseCoorte";
        }
        if (relatorio.equals("Situação da coorte de casos novos de hanseníase")) {
            panel = new HanseniaseCoorte();
            this.relatorio = "HanseniaseCoorte";
        }
        if (relatorio.equals("Recebimento de Lotes")) {
            panel = new RecebimentoLote();
            this.relatorio = "RecebimentoLote";
        }
        if (relatorio.equals("Número de semanas epidemiológicas com informação")) {
            panel = new SemEpidPQAVS();
            this.relatorio = "SemEpidPQAVS";
        }
        if (relatorio.equals("Proporção de casos DNCI encerrados em até 60 dias após notificação")) {
            panel = new OportunidadePQAVS();
            this.relatorio = "OportunidadePQAVS";
        }
        if (relatorio.equals("COAP - Encerramento Oportuno da Investigação")) {
            panel = new OportunidadeCOAP();
            this.relatorio = "OportunidadeCOAP";
        }
        if (relatorio.equals("Encerramento Oportuno da Investigação")) {
            panel = new Oportunidade();
            this.relatorio = "Oportunidade";
        }
        if (relatorio.equals("Regularidade na alimentação do Sinan")) {
            panel = new Regularidade();
            this.relatorio = "Regularidade";
        }/*
        if (relatorio.equals("Duplicidade")) {
            panel = new Duplicidade();
            this.relatorio = "Duplicidade";
        }*/
        if (relatorio.equals("Taxa de letalidade das formas graves de dengue")) {
            panel = new DengueLetalidadeGrave();
            this.relatorio = "DengueLetalidadeGrave";
        }
        if (relatorio.equals("Incidência de Sífilis Congênita")) {
            panel = new SifilisCongenitaIncidencia();
            this.relatorio = "SifilisCongenitaIncidencia";
        }
        if (relatorio.equals("Número de notificações dos agravos à Saúde do trabalhador")) {
            panel = new SaudeTrabalhador();
            this.relatorio = "SaudeTrabalhador";
        }
        if (relatorio.equals("Número de casos autóctones de malária")) {
            panel = new AutoctonesMalariaPactuacao();
            this.relatorio = "AutoctonesMalariaPactuacao";
        }

        if (relatorio.equals("Análise de Completitude")) {
            Frame parent = new Frame();
            Dialog completitude = new Completitude(parent, true);
            completitude.setVisible(true);
            return null;
        }

        return panel;
    }

    @Override
    protected Void doInBackground() throws Exception {
        //verifica qual relatóriio foi escolhido
        if (relatorio.equals("ListagemHanseniase")) {
            agravo = new com.org.model.classes.agravos.Hanseniase(isDbf());
        }
        if (relatorio.equals("DengueLetalidade")) {
            agravo = new com.org.model.classes.agravos.DengueLetalidade(isDbf());
        }
        if (relatorio.equals("DengueLetalidadeGrave")) {
            agravo = new com.org.model.classes.agravos.DengueLetalidadeGrave(isDbf());
        }
        if (relatorio.equals("AidsTaxaCrianca")) {
            agravo = new com.org.model.classes.agravos.AidsTaxaCrianca(isDbf());
        }
        if (relatorio.equals("AidsIndicadorCriancaPactuacao")) {
            agravo = new com.org.model.classes.agravos.AidsIndicadorCriancaPactuacao(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);
            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);

        }
        if (relatorio.equals("SifilisCongenitaIncidencia")) {
            agravo = new com.org.model.classes.agravos.SifilisCongenitaIncidencia(isDbf());
        }
        if (relatorio.equals("SifilisCongenitaIncidenciaPactuacao")) {
            agravo = new com.org.model.classes.agravos.SifilisCongenitaIncidenciaPactuacao(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);
            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);
        }
        if (relatorio.equals("SaudeTrabalhador")) {
            agravo = new com.org.model.classes.agravos.SaudeTrabalhador(isDbf());
        }
        if (relatorio.equals("ExantematicaOportunoAdequado")) {
            //15/09/2011-redirecionado processamento para nova classe criada para este fim, ou seja, possibilitar tb esse relatório em modo DBF
            agravo = new com.org.model.classes.agravos.ExantematicaOportunoAdequadamente(isDbf(), true);
        }
        if (relatorio.equals("ExantematicaOportuno")) {
            agravo = new com.org.model.classes.agravos.ExantematicaOportuno(isDbf(), false);
        }
        if (relatorio.equals("HepatiteBC")) {
            agravo = new com.org.model.classes.agravos.HepatiteBC(isDbf());
        }
        if (relatorio.equals("HepatiteB")) {
            agravo = new com.org.model.classes.agravos.HepatiteB(isDbf());
        }
        if (relatorio.equals("PFA15anos")) {
            agravo = new com.org.model.classes.agravos.PFA15anos(isDbf());
        }

        if (relatorio.equals("AutoctonesMalariaPactuacao")) {
            agravo = new com.org.model.classes.agravos.AutoctoneMalariaPactuacao(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);

            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);
        }
        if (relatorio.equals("SemanaEpidemiologicaPactuacao")) {
            agravo = new com.org.model.classes.agravos.SemanaEpidemiologicaPactuacao(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);

            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);
        }

        if (relatorio.equals("PreenchimentoOcupacaoTrabalhadorPactuacao")) {
            agravo = new com.org.model.classes.agravos.PreenchimentoOcupacaoTrabalhadorPactuacao(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);
            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);
        }

        if (relatorio.equals("ExaminadosTuberculosePactuacao")) {
            agravo = new com.org.model.classes.agravos.ContatosExaminadosTuberculosePactuacao(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);
            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);
        }
        if (relatorio.equals("ExaminadosHanseniasePactuacao")) {
            agravo = new com.org.model.classes.agravos.ContatosExaminadosHanseniasePactuacao(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);
            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);
        }

        if (relatorio.equals("OportunidadeMalariaPactuacao")) {
            agravo = new com.org.model.classes.agravos.OportunidadeMalariaPactuacao(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);

            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);

        }

        if (relatorio.equals("OportunidadeCOAP")) {
            agravo = new com.org.model.classes.agravos.OportunidadeCOAP(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);
            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setNomeAgravo(nomeAgravo);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);
        }
        if (relatorio.equals("RecebimentoLote")) {
            agravo = new com.org.model.classes.agravos.RecebimentoLote(isDbf());
            agravo.setAnoAvaliado("2010");
            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDtInicioTransf(this.dtInicioTrasnf);
            agravo.setDtFimTransf(this.dtFimTransf);
            agravo.setDtInicioReceb(this.dtInicioReceb);
            agravo.setDtFimReceb(this.dtFimReceb);
            agravo.setDataAvaliacao(this.dtInicioAvaliacao);
            agravo.setNomeAgravo("agravo");
            agravo.setUf("uf");
            agravo.setParametros(parametros);
            agravo.setMunicipio("municipio");
            agravo.setRegional("regional");
            agravo.setTemListagem(false);
        }
        if (relatorio.equals("SemEpidPQAVS")) {
            agravo = new com.org.model.classes.agravos.SemEpidPQAVS(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);
            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setNomeAgravo(nomeAgravo);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);
        }
        if (relatorio.equals("OportunidadePQAVS")) {
            agravo = new com.org.model.classes.agravos.OportunidadePQAVS(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);
            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setNomeAgravo(nomeAgravo);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);
        }
        if (relatorio.equals("OportunidadePQAVSPactuacao")) {
            agravo = new com.org.model.classes.agravos.OportunidadePQAVSPactuacao(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);
            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setNomeAgravo(nomeAgravo);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);
        }

        if (relatorio.equals("Oportunidade")) {
            agravo = new com.org.model.classes.agravos.Oportunidade(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);
            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setNomeAgravo(nomeAgravo);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);
        }
        if (relatorio.equals("HanseniaseCoorte")) {
            agravo = new com.org.model.classes.agravos.HanseniaseCoorte(isDbf());
        }
        if (relatorio.equals("HanseniaseCoorteCura")) {
            agravo = new com.org.model.classes.agravos.HanseniaseCoorteCuraPactuacao(isDbf());
        }
        if (relatorio.equals("TuberculoseCoorte")) {
            agravo = new com.org.model.classes.agravos.TuberculoseCoorte(isDbf(), isAuxiliar());
        }
        if (relatorio.equals("Regularidade")) {
            agravo = new com.org.model.classes.agravos.Regularidade(isDbf());
        }
        if (relatorio.equals("Violencia")) {
            agravo = new com.org.model.classes.agravos.ViolenciaAgravo(isDbf());
            agravo.setAnoAvaliado(this.anoAvaliado);
            agravo.setDtInicioAvaliacao(this.dtInicioAvaliacao);
            agravo.setDtFimAvaliacao(this.dtFimAvaliacao);
            agravo.setDataAvaliacao(dataAvaliacao);
            agravo.setUf(uf);
            agravo.setMunicipio(municipio);
            agravo.setRegional(regional);
            agravo.setTemListagem(this.temListagem);
        }
        /*
        if (relatorio.equals("Duplicidade")) {
            agravo = new com.org.model.classes.agravos.Regularidade(isDbf());
        }*/
//       if (relatorio.equals("Análise de Completitude")) {
//            Frame parent =  new Frame();
//            Dialog completitude = new Completitude(parent, true);
//            completitude.setVisible(true);
//            
//        }
        agravo.setDataFim(this.dataFim);
        agravo.setDataInicio(this.dataInicio);

        this.geraRelatorio(this.uf, this.municipio);
        //veririca se vai exportar para DBF
        if (this.exportarDbf) {
            if (!Master.setNomeArquivoDBF()) {

                viewer.setVisible(true);

                return null;
            }
//            if (relatorio.equals("Oportunidade")) {
//                com.org.model.classes.agravos.Oportunidade oportunidade = new com.org.model.classes.agravos.Oportunidade(isDbf());
//                oportunidade = (com.org.model.classes.agravos.Oportunidade) agravo;
//                new DBF().beanToDbf(agravo.getColunas(), oportunidade.getListExportacao(), agravo);
//            } else {
            new DBF().beanToDbf(agravo.getColunas(), beans, agravo);
//            }

        }
        if (relatorio.equals("Regularidade")) {
            if (Configuracao.getPropriedade("gtsinan").toString().equals("1")) {
                return null;
            } else {
                viewer.setVisible(true);
            }
        } else {
            viewer.setVisible(true);
        }
        
        System.out.println(relatorio+" - "+agravo.getCaminhoJasper());
        
        return null;
    }

    /*Alterado pelo grupo do sinan relatórios */
    public String[] retornaSubgrupos(String grupo) {
        String[] subgrupos = new String[]{""};
        if (grupo.equals("Pactuações Anteriores")) {
            subgrupos = new String[]{
                "Selecione a pactuação",
                "COAP até 2015",
                "PACTO 2008/2009",
                "PACTO 2010/2011",
                "PQAVS até 2016"
            };
            return subgrupos;
        }
        return subgrupos;
    }

    public String[] retornaGrupos() {
        String[] grupos = {
            "Selecione um Grupo",
            "PQAVS a partir de 2017",
            "Pactuação Interfederativa 2017 a 2021",
//            "Pactuações Anteriores",
            "Outros relatórios"
        };
        return grupos;
    }

    public String[] retornaAgravos(String grupo) {
        String[] grupos = new String[]{""};
        if (grupo.equals("Análise de Completitude")) {
//            grupos = new String[]{"Selecione um Agravo", "Encerramento Oportuno da Investigação", "Aids", "Dengue",
//                        "Hepatite", "Hanseníase", "PFA", "Saúde do Trabalhador", "Sífilis Congênita", "Tuberculose"};
            grupos = new String[]{"--"};
        }
        if (grupo.equals("Recebimento de Lotes")) {
//            grupos = new String[]{"Selecione um Agravo", "Encerramento Oportuno da Investigação", "Aids", "Dengue",
//                        "Hepatite", "Hanseníase", "PFA", "Saúde do Trabalhador", "Sífilis Congênita", "Tuberculose"};
            grupos = new String[]{"-------"};
        }
//        if (grupo.equals("PQAVS - Semanas epidemiológicas com notificação")) {
////            grupos = new String[]{"Selecione um Agravo", "Encerramento Oportuno da Investigação", "Aids", "Dengue",
////                        "Hepatite", "Hanseníase", "PFA", "Saúde do Trabalhador", "Sífilis Congênita", "Tuberculose"};
//            grupos = new String[]{"--------"};
//        }
        if (grupo.equals("PQAVS até 2016")) {
//            grupos = new String[]{"Selecione um Agravo", "Encerramento Oportuno da Investigação", "Aids", "Dengue",
//                        "Hepatite", "Hanseníase", "PFA", "Saúde do Trabalhador", "Sífilis Congênita", "Tuberculose"};
            grupos = new String[]{"------"};
        }
        if (grupo.equals("COAP até 2015")) {
//            grupos = new String[]{"Selecione um Agravo", "Encerramento Oportuno da Investigação", "Aids", "Dengue",
//                        "Hepatite", "Hanseníase", "PFA", "Saúde do Trabalhador", "Sífilis Congênita", "Tuberculose"};
            grupos = new String[]{"-----"};
        }
        //Novo pacto
        if (grupo.equals("Pactuação Interfederativa 2017 a 2021")) {
//            grupos = new String[]{"Selecione um Agravo", "Encerramento Oportuno da Investigação", "Aids", "Dengue", "Exantemática",
//                        "Hepatite", "Hanseníase", "PFA", "Tuberculose"};
            grupos = new String[]{"--"};
        }
        if (grupo.equals("PACTO 2010/2011")) {
//            grupos = new String[]{"Selecione um Agravo", "Encerramento Oportuno da Investigação", "Aids", "Dengue",
//                        "Hepatite", "Hanseníase", "PFA", "Saúde do Trabalhador", "Sífilis Congênita", "Tuberculose"};
            grupos = new String[]{"--"};
        }
        if (grupo.equals("PACTO 2008/2009")) {
//            grupos = new String[]{"Selecione um Agravo", "Encerramento Oportuno da Investigação", "Aids", "Dengue", "Exantemática",
//                        "Hepatite", "Hanseníase", "PFA", "Tuberculose"};
            grupos = new String[]{"--"};
        }
        /*    if (grupo.equals("Regularidade na alimentação do Sinan")) {
            grupos = new String[]{"---"};
        }*/
 /*
        if (grupo.equals("Duplicidade")) {
            grupos = new String[]{"----"};
        }*/
 /*     if (grupo.equals("Outros relatórios")) {
            grupos = new String[]{"Selecione um Agravo", "Exantemática", "Hanseníase", "PFA"};
        }*/
        return grupos;
    }

    public String[] retornaRelatorios(String grupo, String agravo) {
        String[] relatorios = new String[]{""};
        //verifica se o relatório é o de regularidade
        if (agravo.equals("---")) {
            relatorios = new String[]{"Regularidade na alimentação do Sinan"};
        }/*
        else if(agravo.equals("----")) {
            relatorios = new String[]{"Duplicidade"};
        }*/ else if (agravo.equals("-------")) {
            relatorios = new String[]{"Recebimento de Lotes"};
        } //        else if(agravo.equals("--------")) {
        //            relatorios = new String[]{"PQAVS - Semanas epidemiológicas com notificação"};
        //        }
        else if (agravo.equals("------")) {
            relatorios = new String[]{"Selecione o Relatório", "Número de semanas epidemiológicas com informação", "Proporção de casos DNCI encerrados em até 60 dias após notificação"};
        } else if (agravo.equals("-----")) {
            relatorios = new String[]{"COAP - Encerramento Oportuno da Investigação"};
        } else {
            if (grupo.equals("PACTO 2010/2011")) {
                relatorios = new String[]{"Selecione o Relatório", "Encerramento Oportuno da Investigação",
                    "Situação da coorte de casos novos de Tuberculose",
                    "Incidência de Sífilis Congênita",
                    "Número de notificações dos agravos à Saúde do trabalhador",
                    "Situação da coorte de casos novos de hanseníase",
                    "Percentual de casos de hepatites B confirmados por sorologia",
                    "Taxa de letalidade das formas graves de dengue",
                    "Taxa de incidência de aids em menores de 5 anos de idade"
                };
            }
            if (grupo.equals("Pactuação Interfederativa 2017 a 2021")) {
                relatorios = new String[]{"Selecione o Relatório",
                    "Número de casos novos de AIDS em menores de 5 anos",
                    "Número de casos novos de sífilis congênita em menores de 1 ano de idade",
                    "Proporção de casos DNCI encerrados em até 60 dias após notificação",
                    "Número de casos autóctones de malária",
                    "Proporção de cura dos casos novos de hanseníase diagnosticados nos anos das coortes",
                    "Proporção de preenchimento do campo ocupação"
                };
            }
            if (grupo.equals("PACTO 2008/2009")) {
                relatorios = new String[]{"Selecione o Relatório",
                    "Situação da coorte de casos novos de Tuberculose",
                    "Taxa de notificação de casos de PFA em menores de 15 anos",
                    "Percentual de casos de hepatites B e C",
                    "Proporção de doenças exantemáticas investigados oportunamente",
                    "Taxa de letalidade por Febre Hemorrágica Dengue",
                    "Taxa de incidência de aids em menores de 5 anos de idade",
                    "Situação da coorte de casos novos de hanseníase"};
            }
            if (grupo.equals("Regularidade na alimentação do Sinan")) {
                relatorios = new String[]{"Selecione o Relatório", "Regularidade na alimentação do Sinan"};
            }
            if (grupo.equals("Recebimento de Lotes")) {
                relatorios = new String[]{"Recebimento de Lotes"};
            }
//            if (grupo.equals("PQAVS - Semanas epidemiológicas com notificação")) {
//                relatorios = new String[]{"Selecione o Relatório", "PQAVS - Semanas epidemiológicas com notificação"};
//            }
            if (grupo.equals("PQAVS até 2016")) {
                relatorios = new String[]{"Selecione o Relatório", "Número de semanas epidemiológicas com informação", "Proporção de casos DNCI encerrados em até 60 dias após notificação"};
            }
            if (grupo.equals("COAP até 2015")) {
                relatorios = new String[]{"Selecione o Relatório", "COAP - Encerramento Oportuno da Investigação"};
            }
            if (grupo.equals("Análise de Completitude")) {
                Frame parent = new Frame();
                Dialog completitude = new Completitude(parent, true);
                completitude.setVisible(true);
            }

            if (grupo.equals("PQAVS a partir de 2017")) {
                relatorios = new String[]{"Selecione o Relatório",
                    "Número de semanas epidemiológicas com informação",
                    "Proporção de casos DNCI encerrados em até 60 dias após notificação",
                    "Proporção de notificações de Violência com o campo raça/cor preenchido de forma válida",
                    "Proporção de casos de malária que iniciaram tratamento em tempo oportuno",
                    "Proporção de contatos examinados de casos novos de hanseníase",
                    "Proporção de contatos examinados de casos novos de tuberculose",
                    "Proporção de preenchimento do campo ocupação"

                };
            }

            if (grupo.equals("Outros relatórios")) {
                relatorios = new String[]{"Selecione o Relatório",
//                    "Análise de Completitude",
//                    "Proporção de doenças exantemáticas investigados oportuna e adequadamente",
//                    "Proporção de doenças exantemáticas investigados oportunamente (PAVS 2010/2011)",
//                    "Taxa de notificação de casos de PFA em menores de 15 anos (PAVS 2010/2011)",
//                    "Listagem de notificações de prováveis faltosos e abandono do tratamento de hanseníase",
                    "Regularidade na alimentação do Sinan"
//                    "Número de semanas epidemiológicas com informação"

                };
            }

        }
        return relatorios;
    }

    public static boolean isDbf() {
        return dbf;
    }

    public static void setDbf(boolean aDbf) {
        dbf = aDbf;
    }

    public static String getNomeDbf() {
        return nomeDbf;
    }

    public static void setNomeDbf(String aNomeDbf) {
        nomeDbf = aNomeDbf;
    }

    public SessionFacadeImpl(boolean dbf) {
        setDbf(dbf);
    }

    public SessionFacadeImpl() {
    }

    public String getCodRegionalCOAP(String regional, String uf) throws SQLException {
        if (regional == null) {
            return "";
        }
        if (regional.equals("TODAS") || regional.isEmpty()) {
            return "";
        }
        if (isDbf()) {
            DBFReader reader = retornaObjetoDbfCaminhoArquivo("REGIAO", "dbf\\");
            Object[] rowObjects;
            DBFUtil utilDbf = new DBFUtil();
            try {
                utilDbf.mapearPosicoes(reader);
                while ((rowObjects = reader.nextRecord()) != null) {
                    if (regional.equals(utilDbf.getString(rowObjects, "NM_REGIAO")) && uf.equals(utilDbf.getString(rowObjects, "SG_UF"))) {
                        return utilDbf.getString(rowObjects, "ID_REGIAO");
                    }
                }
            } catch (DBFException e) {
                Master.mensagem("Erro: regional nao encontrada.Verifique se existe a pasta DBF e se os arquivo REGIAO.DBF está lá:\n" + e);
            }
            return "";
        }
        String config = null;
        String sql = "select co_seq_regional from dblocalidade.tb_regional_svs where no_regional = '" + regional + "'";
        Util util = new Util();
        Conexao con = util.conectarSiceb();
        con.conect();
        java.sql.Statement stm = con.getC().createStatement();
        ResultSet rs;
        try {
            rs = stm.executeQuery(sql);
        } catch (Exception exception) {
            sql = "select co_seq_regional from tb_regional_svs where no_regional = '" + regional + "'";
            rs = stm.executeQuery(sql);
        }
        rs.next();
        try {
            config = rs.getString("co_seq_regional");
        } catch (Exception exception) {
            try {
                config = rs.getString("co_regional_saude");
            } catch (Exception exception1) {
                config = "";
            }

        }

        return config;
    }

    public String getCodRegional(String regional, String uf, String relatorio) throws SQLException {
        if (regional == null) {
            return "";
        }
        if (regional.equals("TODAS") || regional.isEmpty()) {
            return "";
        }
        if (isDbf()) {
            if (relatorio == null || !relatorio.equals("OportunidadeCOAP")) {
                DBFReader reader = retornaObjetoDbfCaminhoArquivo("REGIONET", "dbf\\");
                Object[] rowObjects;
                DBFUtil utilDbf = new DBFUtil();
                try {
                    utilDbf.mapearPosicoes(reader);
                    while ((rowObjects = reader.nextRecord()) != null) {
                        if (regional.equals(utilDbf.getString(rowObjects, "NM_REGIONA"))  && uf.equals(utilDbf.getString(rowObjects, "SG_UF"))) {
                            return utilDbf.getString(rowObjects, "ID_REGIONA");
                        }
                    }
                } catch (DBFException e) {
                    Master.mensagem("Erro: regional nao encontrada.Verifique se existe a pasta DBF e se os arquivo REGIONET.DBF está lá:\n" + e);
                }
            } else {
                return getCodRegionalCOAP(regional, uf);
            }
            return "";
        }
        String config = null;
        String sql = "select co_seq_regional from dblocalidade.tb_regional_svs where no_regional = '" + regional + "'";
        Util util = new Util();
        Conexao con = util.conectarSiceb();
        con.conect();
        java.sql.Statement stm = con.getC().createStatement();
        ResultSet rs;
        try {
            rs = stm.executeQuery(sql);
        } catch (Exception exception) {
            sql = "select co_seq_regional from tb_regional_svs where no_regional = '" + regional + "'";
            rs = stm.executeQuery(sql);
        }
        rs.next();
        try {
            config = rs.getString("co_seq_regional");
        } catch (Exception exception) {
            try {
                config = rs.getString("co_regional_saude");
            } catch (Exception exception1) {
                config = "";
            }

        }

        return config;
    }

    public String getCodRegiao(String regional, String uf, String relatorio) throws SQLException {
        if (regional == null) {
            return "";
        }
        if (regional.equals("TODAS") || regional.isEmpty()) {
            return "";
        }
        if (isDbf()) {
            if (relatorio == null || !relatorio.equals("OportunidadeCOAP")) {
                DBFReader reader = retornaObjetoDbfCaminhoArquivo("REGIAO", "dbf\\");
                Object[] rowObjects;
                DBFUtil utilDbf = new DBFUtil();
                try {
                    utilDbf.mapearPosicoes(reader);
                    while ((rowObjects = reader.nextRecord()) != null) {
                        if (regional.equals(utilDbf.getString(rowObjects, "NM_REGIAO")) && uf.equals(utilDbf.getString(rowObjects, "SG_UF"))) {
                            return utilDbf.getString(rowObjects, "ID_REGIAO");
                        }
                    }
                } catch (DBFException e) {
                    Master.mensagem("Erro: regional nao encontrada.Verifique se existe a pasta DBF e se os arquivo REGIONET.DBF está lá:\n" + e);
                }
            } else {
                return getCodRegionalCOAP(regional, uf);
            }
            return "";
        }
        String config = null;
        String sql = "select co_seq_regional from dblocalidade.tb_regional_svs where no_regional = '" + regional + "'";
        Util util = new Util();
        Conexao con = util.conectarSiceb();
        con.conect();
        java.sql.Statement stm = con.getC().createStatement();
        ResultSet rs;
        try {
            rs = stm.executeQuery(sql);
        } catch (Exception exception) {
            sql = "select co_seq_regional from tb_regional_svs where no_regional = '" + regional + "'";
            rs = stm.executeQuery(sql);
        }
        rs.next();
        try {
            config = rs.getString("co_seq_regional");
        } catch (Exception exception) {
            try {
                config = rs.getString("co_regional_saude");
            } catch (Exception exception1) {
                config = "";
            }

        }

        return config;
    }

    public String getCodRegiaoSaude(String regiaoSaude, String UF) throws SQLException {
        if (regiaoSaude == null) {
            return "";
        }
        if (regiaoSaude.equals("TODAS") || regiaoSaude.isEmpty()) {
            return "";
        }
        if (isDbf()) {
            DBFReader reader = retornaObjetoDbfCaminhoArquivo("REGIAO", "dbf\\");
            Object[] rowObjects;
            DBFUtil utilDbf = new DBFUtil();
            try {
                utilDbf.mapearPosicoes(reader);
                while ((rowObjects = reader.nextRecord()) != null) {
                    if (regiaoSaude.equals(utilDbf.getString(rowObjects, "NM_REGIAO")) && UF.equals(utilDbf.getString(rowObjects, "SG_UF"))) {
                        return utilDbf.getString(rowObjects, "ID_REGIAO");
                    }
                }
            } catch (DBFException e) {
                Master.mensagem("Erro: regiao nao encontrada.Verifique se existe a pasta DBF e se os arquivo REGIAO.DBF está lá:\n" + e);
            }
            return "";
        }
        String config = null;
        String sql = "select co_seq_regional from dblocalidade.tb_regional_svs where no_regional = '" + regiaoSaude + "'";
        Util util = new Util();
        Conexao con = util.conectarSiceb();
        con.conect();
        java.sql.Statement stm = con.getC().createStatement();
        ResultSet rs;
        try {
            rs = stm.executeQuery(sql);
        } catch (Exception exception) {
            sql = "select co_seq_regional from tb_regional_svs where no_regional = '" + regiaoSaude + "'";
            rs = stm.executeQuery(sql);
        }
        rs.next();
        try {
            config = rs.getString("co_seq_regional");
        } catch (Exception exception) {
            try {
                config = rs.getString("co_regional_saude");
            } catch (Exception exception1) {
                config = "";
            }

        }

        return config;
    }

    public Vector<String> retornaUFs() {
        Vector<String> ufs = new Vector<String>();
        ufs.add(0, "-- Selecione --");
        try {
            Util util = new Util();
            //verifica qual banco
            if (!isDbf()) {
                Conexao con = util.conectarSiceb();
                con.conect();
                String sql = "select sg_uf from dbgeral.tb_uf order by sg_uf";
                ResultSet rs = null;
                java.sql.Statement stm = con.getC().createStatement();
                rs = stm.executeQuery(sql);
                while (rs.next()) {
                    ufs.add(rs.getString("sg_uf"));
                }
                con.disconect();
            } else {
                DBFReader reader = retornaObjetoDbfCaminhoArquivo("UF", "dbf\\");
                if (reader == null) {
                    Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null,
                            "Erro ao carregar as UFs. Verifique se existe a pasta DBF e se os arquivo UF.DBF está lá:\n");
                    throw new NullPointerException("Não foi possível carregar as UFs. Verifique se a pasta DBF existe e se o arquivo UF.DBF está lá!\n");
                }
                Object[] rowObjects;
                DBFUtil utilDbf = new DBFUtil();
                try {
                    utilDbf.mapearPosicoes(reader);
                    Vector ufsdbf = new Vector<String>();
                    if (isBrasil()) {
                        ufs.add("Brasil");

                    }
                    if (isTemOpcaoTodasUFs()) {
                        ufs.add("TODAS");
                    }
                    while ((rowObjects = reader.nextRecord()) != null) {
                        ufsdbf.add(utilDbf.getString(rowObjects, "SG_UF"));
                    }
                    Collections.sort(ufsdbf);
                    for (int i = 0; i < ufsdbf.size(); i++) {
                        ufs.add((String) ufsdbf.get(i));
                    }
                } catch (DBFException e) {
                    Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null,
                            "Erro: tabela uf.dbf nao encontrada. Verifique se existe a pasta DBF e se os arquivo UF.DBF está lá:" + e);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ufs;
    }

    public Vector<String> retornaRegioes(String UF) {
        String SG_UF = UF;
        UF = String.valueOf(this.getCodigoUf(UF));
        Vector<String> regionais = new Vector<String>();

        try {
            Util util = new Util();
            //verifica qual banco
            if (!isDbf()) {
                Conexao con = util.conectarSiceb();
                con.conect();
                String sql = "select no_regional from dblocalidade.tb_regional_svs where co_uf_ibge=" + UF + " and no_regional not like '%ignorado%' order by no_regional";
                ResultSet rs = null;
                java.sql.Statement stm = con.getC().createStatement();
                rs = stm.executeQuery(sql);
                while (rs.next()) {
                    regionais.add(rs.getString("no_regional"));
                }
                con.disconect();
            } else {
                DBFReader reader = retornaObjetoDbfCaminhoArquivo("REGIAO", "dbf\\");
                Object[] rowObjects;
                DBFUtil utilDbf = new DBFUtil();
                try {
                    utilDbf.mapearPosicoes(reader);
                    while ((rowObjects = reader.nextRecord()) != null) {
                        if (SG_UF.equals(utilDbf.getString(rowObjects, "SG_UF"))) {
                            regionais.add(utilDbf.getString(rowObjects, "NM_REGIAO"));
                        }
                    }
                    Collections.sort(regionais);
                    regionais.add(0, "-- Selecione --");
                    regionais.add(1, "TODAS");
                } catch (DBFException e) {
                    Master.mensagem("Erro ao carregar regionais. Verifique se existe a pasta DBF e se os arquivo REGIONET.DBF está lá:\n" + e);
                    System.out.println("Erro ao carregar REGIONAIS: " + e);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return regionais;
    }

    public Vector<String> retornaRegionais(String UF) {
        String SG_UF = UF;
        UF = String.valueOf(this.getCodigoUf(UF));
        Vector<String> regionais = new Vector<String>();
        try {
            Util util = new Util();
            //verifica qual banco
            if (!isDbf()) {
                Conexao con = util.conectarSiceb();
                con.conect();
                String sql = "select no_regional from dblocalidade.tb_regional_svs where co_uf_ibge=" + UF + " and no_regional not like '%ignorado%' order by no_regional";
                ResultSet rs = null;
                java.sql.Statement stm = con.getC().createStatement();
                rs = stm.executeQuery(sql);
                while (rs.next()) {
                    regionais.add(rs.getString("no_regional"));
                }
                con.disconect();
            } else {
                DBFReader reader = retornaObjetoDbfCaminhoArquivo("REGIONET", "dbf\\");
                Object[] rowObjects;
                DBFUtil utilDbf = new DBFUtil();
                try {
                    utilDbf.mapearPosicoes(reader);
                    while ((rowObjects = reader.nextRecord()) != null) {
                        if (SG_UF.equals(utilDbf.getString(rowObjects, "SG_UF"))) {
                            regionais.add(utilDbf.getString(rowObjects, "NM_REGIONA"));
                        }
                    }
                    Collections.sort(regionais);
                    regionais.add(0, "-- Selecione --");
                    regionais.add(1, "TODAS");

                } catch (DBFException e) {
                    Master.mensagem("Erro ao carregar regionais. Verifique se existe a pasta DBF e se os arquivo REGIONET.DBF está lá:\n" + e);
                    System.out.println("Erro ao carregar REGIONAIS: " + e);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return regionais;
    }

    public Vector<String> retornaMunicipios(String UF) {
        String sgUf = UF;
        UF = String.valueOf(this.getCodigoUf(UF));
        Vector<String> municipios = new Vector<String>();
        municipios.add(0, "-- Selecione --");
        try {
            Util util = new Util();

            //verifica qual banco
            if (!isDbf()) {
                Conexao con = util.conectarSiceb();
                con.conect();
                String sql = "select no_municipio from dbgeral.tb_municipio where co_uf_ibge=" + UF + " and (no_municipio not like '%IGNORADO%' and no_municipio not like '%ignorado%' and no_municipio not like '%Ignorado%')  order by no_municipio";
                ResultSet rs = null;
                java.sql.Statement stm = con.getC().createStatement();
                rs = stm.executeQuery(sql);
                while (rs.next()) {
                    municipios.add(rs.getString("no_municipio"));
                }
                con.disconect();
            } else {
                DBFReader reader = retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
                Object[] rowObjects;
                DBFUtil utilDbf = new DBFUtil();
                try {
                    utilDbf.mapearPosicoes(reader);
                    while ((rowObjects = reader.nextRecord()) != null) {
                        if (sgUf.equals(utilDbf.getString(rowObjects, "SG_UF"))) {
                            if (!utilDbf.getString(rowObjects, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1) {
                                if ((utilDbf.getString(rowObjects, "SG_UF").equals("DF") && utilDbf.getString(rowObjects, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects, "SG_UF").equals("DF")) {
                                    municipios.add(utilDbf.getString(rowObjects, "NM_MUNICIP"));
                                }
                            }
                        }
                    }
                    Collections.sort(municipios);
                } catch (DBFException e) {
                    Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, "Erro ao carregar municipios:\n" + e);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isTodosMunicipios()) {
            municipios.add(1, "TODOS");
        }
        return municipios;
    }

    public Vector<String> retornaMunicipiosCOAP(int indexNivelDesagregacao, String UF, String regional) {
        String codUf = String.valueOf(this.getCodigoUf(UF));
        String codRegional = "";
        try {
            codRegional = String.valueOf(this.getCodRegionalCOAP(regional, UF));
        } catch (SQLException e) {
            System.out.println(e);
        }

        Vector<String> municipios = new Vector<String>();
        municipios.add(0, "-- Selecione --");
        try {
            Util util = new Util();
            if (!regional.equals("-- Selecione --") && !regional.equals("TODAS") && indexNivelDesagregacao >= 2) {
                //verifica qual banco
                if (!isDbf()) {
                    Conexao con = util.conectarSiceb();
                    con.conect();
                    String sql = "select no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + codUf + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + codRegional + "' and (no_municipio not like '%Ignorado%' and no_municipio not like '%IGNORADO%')  order by no_municipio";
                    ResultSet rs = null;
                    java.sql.Statement stm = con.getC().createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        municipios.add(rs.getString("no_municipio"));
                    }
                    con.disconect();
                } else {
                    DBFReader reader = retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
                    Object[] rowObjects;
                    DBFUtil utilDbf = new DBFUtil();
                    try {
                        utilDbf.mapearPosicoes(reader);
                        while ((rowObjects = reader.nextRecord()) != null) {
                            if (codRegional.equals(utilDbf.getString(rowObjects, "ID_REGIAO"))) {
                                if (!utilDbf.getString(rowObjects, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1) {
                                    if ((utilDbf.getString(rowObjects, "SG_UF").equals("DF") && utilDbf.getString(rowObjects, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects, "SG_UF").equals("DF")) {
                                        municipios.add(utilDbf.getString(rowObjects, "NM_MUNICIP"));
                                    }
                                }
                            }
                        }
                        Collections.sort(municipios);
                    } catch (DBFException e) {
                        Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, "Erro ao carregar municipios:\n" + e);
                    }
                }
            } else {
                this.retornaMunicipios(UF);
            }

        } catch (SQLException ex) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isTodosMunicipios()) {
            municipios.add(1, "TODOS");
        }
        return municipios;
    }

    public Vector<String> retornaMunicipiosPQAVS(String UF, String regional) {
        String codUf = String.valueOf(this.getCodigoUf(UF));
        String codRegional = "";
        try {
            codRegional = String.valueOf(this.getCodRegionalCOAP(regional, UF));
        } catch (SQLException e) {
            System.out.println(e);
        }

        Vector<String> municipios = new Vector<String>();
        municipios.add(0, "-- Selecione --");
        try {
            Util util = new Util();
            if (!regional.equals("-- Selecione --") && !regional.equals("TODAS")) {
                //verifica qual banco
                if (!isDbf()) {
                    Conexao con = util.conectarSiceb();
                    con.conect();
                    String sql = "select no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + codUf + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + codRegional + "' and (no_municipio not like '%Ignorado%' and no_municipio not like '%IGNORADO%')  order by no_municipio";
                    ResultSet rs = null;
                    java.sql.Statement stm = con.getC().createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        municipios.add(rs.getString("no_municipio"));
                    }
                    con.disconect();
                } else {
                    DBFReader reader = retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
                    Object[] rowObjects;
                    DBFUtil utilDbf = new DBFUtil();
                    try {
                        utilDbf.mapearPosicoes(reader);
                        while ((rowObjects = reader.nextRecord()) != null) {
                            if (codRegional.equals(utilDbf.getString(rowObjects, "ID_REGIAO"))) {
                                if (!utilDbf.getString(rowObjects, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1) {
                                    if ((utilDbf.getString(rowObjects, "SG_UF").equals("DF") && utilDbf.getString(rowObjects, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects, "SG_UF").equals("DF")) {
                                        municipios.add(utilDbf.getString(rowObjects, "NM_MUNICIP"));
                                    }
                                }
                            }
                        }
                        Collections.sort(municipios);
                    } catch (DBFException e) {
                        Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, "Erro ao carregar municipios:\n" + e);
                    }
                }
            } else {
                this.retornaMunicipios(UF);
            }

        } catch (SQLException ex) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isTodosMunicipios()) {
            municipios.add(1, "TODOS");
        }
        return municipios;
    }

    public Vector<String> retornaMunicipiosPQAVS(int indexNivelDesagregacao, String UF, String regional) {
        String codUf = String.valueOf(this.getCodigoUf(UF));
        String codRegional = "";
        try {
            codRegional = String.valueOf(this.getCodRegionalCOAP(regional, UF));
        } catch (SQLException e) {
            System.out.println(e);
        }

        Vector<String> municipios = new Vector<String>();
        municipios.add(0, "-- Selecione --");
        try {
            Util util = new Util();
            if (!regional.equals("-- Selecione --") && !regional.equals("TODAS") && indexNivelDesagregacao >= 2) {
                //verifica qual banco
                if (!isDbf()) {
                    Conexao con = util.conectarSiceb();
                    con.conect();
                    String sql = "select no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + codUf + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + codRegional + "' and (no_municipio not like '%Ignorado%' and no_municipio not like '%IGNORADO%')  order by no_municipio";
                    ResultSet rs = null;
                    java.sql.Statement stm = con.getC().createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        municipios.add(rs.getString("no_municipio"));
                    }
                    con.disconect();
                } else {
                    DBFReader reader = retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
                    Object[] rowObjects;
                    DBFUtil utilDbf = new DBFUtil();
                    try {
                        utilDbf.mapearPosicoes(reader);
                        while ((rowObjects = reader.nextRecord()) != null) {
                            if (codRegional.equals(utilDbf.getString(rowObjects, "ID_REGIAO"))) {
                                if (!utilDbf.getString(rowObjects, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1) {
                                    if ((utilDbf.getString(rowObjects, "SG_UF").equals("DF") && utilDbf.getString(rowObjects, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects, "SG_UF").equals("DF")) {
                                        municipios.add(utilDbf.getString(rowObjects, "NM_MUNICIP"));
                                    }
                                }
                            }
                        }
                        Collections.sort(municipios);
                    } catch (DBFException e) {
                        Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, "Erro ao carregar municipios:\n" + e);
                    }
                }
            } else {
                this.retornaMunicipios(UF);
            }

        } catch (SQLException ex) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isTodosMunicipios()) {
            municipios.add(1, "TODOS");
        }
        return municipios;
    }

    public Vector<String> retornaMunicipiosPactuacao(int isRegiao, String UF, String regional) {
        String codUf = String.valueOf(this.getCodigoUf(UF));
        String codReg = "";
        String nomeVar = "ID_REGIONA";
        try {
            //se isRegiao for 1 a consulta é para regiao, se 0 a consulta é para regional
            if (isRegiao == 1) {
                codReg = String.valueOf(this.getCodRegiao(regional, UF, "Pactuacao"));
                nomeVar = "ID_REGIAO";
            } else if (isRegiao == 2) {
                codReg = String.valueOf(this.getCodRegional(regional, UF, "Pactuacao"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        Vector<String> municipios = new Vector<String>();
        municipios.add(0, "-- Selecione --");
        try {
            Util util = new Util();
            if (!regional.equals("-- Selecione --") && !regional.equals("TODAS") && !codReg.isEmpty()) {
                //verifica qual banco
                if (!isDbf()) {
                    Conexao con = util.conectarSiceb();
                    con.conect();
                    String sql = "select no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + codUf + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + codReg + "' and (no_municipio not like '%Ignorado%' and no_municipio not like '%IGNORADO%')  order by no_municipio";
                    ResultSet rs = null;
                    java.sql.Statement stm = con.getC().createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        municipios.add(rs.getString("no_municipio"));
                    }
                    con.disconect();
                } else {
                    DBFReader reader = retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
                    Object[] rowObjects;
                    DBFUtil utilDbf = new DBFUtil();
                    try {
                        utilDbf.mapearPosicoes(reader);
                        while ((rowObjects = reader.nextRecord()) != null) {
                            if (codReg.equals(utilDbf.getString(rowObjects, nomeVar))) {
                                if (!utilDbf.getString(rowObjects, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1) {
                                    if ((utilDbf.getString(rowObjects, "SG_UF").equals("DF") && utilDbf.getString(rowObjects, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects, "SG_UF").equals("DF")) {
                                        municipios.add(utilDbf.getString(rowObjects, "NM_MUNICIP"));
                                    }
                                }
                            }
                        }
                        Collections.sort(municipios);
                    } catch (DBFException e) {
                        Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, "Erro ao carregar municipios:\n" + e);
                    }
                }
            } else {
                this.retornaMunicipios(UF);
            }

        } catch (SQLException ex) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isTodosMunicipios()) {
            municipios.add(1, "TODOS");
        }
        return municipios;
    }

    public Vector<String> retornaMunicipios(String UF, String regional) {
        String codUf = String.valueOf(this.getCodigoUf(UF));
        String codRegional = "";
        try {
            codRegional = String.valueOf(this.getCodRegional(regional, UF, relatorio));
        } catch (SQLException e) {
            System.out.println(e);
        }

        Vector<String> municipios = new Vector<String>();
        municipios.add(0, "-- Selecione --");
        try {
            Util util = new Util();
            if (!regional.equals("-- Selecione --") && !regional.equals("TODAS")) {
                //verifica qual banco
                if (!isDbf()) {
                    Conexao con = util.conectarSiceb();
                    con.conect();
                    String sql = "select no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + codUf + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + codRegional + "' and (no_municipio not like '%Ignorado%' and no_municipio not like '%IGNORADO%')  order by no_municipio";
                    ResultSet rs = null;
                    java.sql.Statement stm = con.getC().createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        municipios.add(rs.getString("no_municipio"));
                    }
                    con.disconect();
                } else {
                    DBFReader reader = retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
                    Object[] rowObjects;
                    DBFUtil utilDbf = new DBFUtil();
                    try {
                        utilDbf.mapearPosicoes(reader);
                        while ((rowObjects = reader.nextRecord()) != null) {
                            if (codRegional.equals(utilDbf.getString(rowObjects, "ID_REGIONA"))) {
                                if (!utilDbf.getString(rowObjects, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1) {
                                    if ((utilDbf.getString(rowObjects, "SG_UF").equals("DF") && utilDbf.getString(rowObjects, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects, "SG_UF").equals("DF")) {
                                        municipios.add(utilDbf.getString(rowObjects, "NM_MUNICIP"));
                                    }
                                }
                            }
                        }
                        Collections.sort(municipios);
                    } catch (DBFException e) {
                        Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, "Erro ao carregar municipios:\n" + e);
                    }
                }
            } else {
                this.retornaMunicipios(UF);
            }

        } catch (SQLException ex) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isTodosMunicipios()) {
            municipios.add(1, "TODOS");
        }
        return municipios;
    }

    public int getCodigoUf(String UF) {
        int codigo = 0;
        try {
            Util util = new Util();
            //verifica qual banco
            if (!isDbf()) {
                Conexao con = util.conectarSiceb();
                con.conect();
                String sql = "select co_uf_ibge from dbgeral.tb_uf where sg_uf='" + UF + "'";
                ResultSet rs = null;
                java.sql.Statement stm = con.getC().createStatement();
                rs = stm.executeQuery(sql);
                while (rs.next()) {
                    codigo = rs.getInt("co_uf_ibge");
                }
                con.disconect();
            } else {
                DBFReader reader = retornaObjetoDbfCaminhoArquivo("UF", "dbf\\");
                Object[] rowObjects;
                DBFUtil utilDbf = new DBFUtil();
                try {
                    utilDbf.mapearPosicoes(reader);
                    while ((rowObjects = reader.nextRecord()) != null) {
                        if (UF.equals(utilDbf.getString(rowObjects, "SG_UF"))) {
                            codigo = utilDbf.getInt(rowObjects, "ID_UF");
                        }
                    }
                } catch (DBFException e) {
                    Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null,
                            "Erro: uf nao encontrada.Verifique se existe a pasta DBF e se os arquivo UF.DBF está lá\n" + e);
                    System.out.println("Erro ao carregar municipios: " + e);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    public String getCodMunicipio(String municipio, String UF) {
        String codigo = "";
        try {
            Util util = new Util();
            //verifica qual banco
            if (!isDbf()) {
                Conexao con = util.conectarSiceb();
                con.conect();
                String sql = "select co_municipio_ibge from dbgeral.tb_municipio where sg_uf = '" + UF + "' and no_municipio = '" + municipio + "'";
                ResultSet rs = null;
                java.sql.Statement stm = con.getC().createStatement();
                rs = stm.executeQuery(sql);
                while (rs.next()) {
                    codigo = rs.getString("co_municipio_ibge");
                }
                con.disconect();
            } else {
                DBFReader reader = retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
                Object[] rowObjects;
                DBFUtil utilDbf = new DBFUtil();
                try {
                    utilDbf.mapearPosicoes(reader);
                    while ((rowObjects = reader.nextRecord()) != null) {
                        if (municipio.equals(utilDbf.getString(rowObjects, "NM_MUNICIP")) && UF.equals(utilDbf.getString(rowObjects, "SG_UF"))) {
                            codigo = utilDbf.getString(rowObjects, "ID_MUNICIP");
                        }
                    }
                } catch (DBFException e) {
                    Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null,
                            "Erro: uf nao encontrada.Verifique se existe a pasta DBF e se os arquivo MUNIC.DBF está lá\n" + e);
                    System.out.println("Erro ao carregar municipios: " + e);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    public static DBFReader retornaObjetoDbfCaminhoArquivo(String arquivo, String caminho) {
        DBFReader reader = null;
        InputStream inputStream = null;
        try {
            if (System.getProperty("os.name").compareTo("Linux") == 0) {
                inputStream = new FileInputStream(caminho.replace("\\", "/") + arquivo + ".DBF"); // take dbf file as program argument
            } else {
                inputStream = new FileInputStream(caminho + arquivo + ".DBF"); // take dbf file as program argument
            }

        } catch (FileNotFoundException e) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, "Erro: tabela " + arquivo + ".dbf nao encontrada.\n" + e);
            return reader;
        }
        try {
            reader = new DBFReader(inputStream);
        } catch (DBFException e) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, "Erro: tabela " + arquivo + ".dbf nao encontrada.\n" + e);
        }
        return reader;
    }

    /**
     * @return the uf
     */
    public String getUf() {
        return uf;
    }

    /**
     * @param uf the uf to set
     */
    public void setUf(String uf) {
        this.uf = uf;
    }

    /**
     * @return the municipio
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * @param municipio the municipio to set
     */
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    /**
     * @return the relatorio
     */
    public String getRelatorio() {
        return relatorio;
    }

    /**
     * @param relatorio the relatorio to set
     */
    public void setRelatorio(String relatorio) {
        this.relatorio = relatorio;
    }

    /**
     * @return the jprogress
     */
    public JProgressBar getJprogress() {
        return jprogress;
    }

    public JProgressBar getJprogressGeral() {
        return jprogressGeral;
    }

    /**
     * @param jprogress the jprogress to set
     */
    public void setJprogress(JProgressBar jprogress) {
        this.jprogress = jprogress;
    }

    public void setJprogressGeral(JProgressBar jprogressGeral) {
        this.jprogressGeral = jprogressGeral;
    }

    /**
     * @return the exportarDbf
     */
    public boolean isExportarDbf() {
        return exportarDbf;
    }

    /**
     * @param exportarDbf the exportarDbf to set
     */
    public void setExportarDbf(boolean exportarDbf) {
        this.exportarDbf = exportarDbf;
    }

    /**
     * @return the dataInicio
     */
    public String getDataInicio() {
        return dataInicio;
    }

    /**
     * @param dataInicio the dataInicio to set
     */
    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    /**
     * @return the dataFim
     */
    public String getDataFim() {
        return dataFim;
    }

    /**
     * @param dataFim the dataFim to set
     */
    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    /**
     * @return the brasil
     */
    public boolean isBrasil() {
        return brasil;
    }

    /**
     * @param brasil the brasil to set
     */
    public void setBrasil(boolean brasil) {
        this.brasil = brasil;
    }

    /**
     * @return the regional
     */
    public String getRegional() {
        return regional;
    }

    /**
     * @param regional the regional to set
     */
    public void setRegional(String regional) {
        this.regional = regional;
    }

    /**
     * @return the todosMunicipios
     */
    public boolean isTodosMunicipios() {
        return todosMunicipios;
    }

    /**
     * @param todosMunicipios the todosMunicipios to set
     */
    public void setTodosMunicipios(boolean todosMunicipios) {
        this.todosMunicipios = todosMunicipios;
    }

    /**
     * @return the versao
     */
    public String getVersao() {
        return versao;
    }

    /**
     * @param versao the versao to set
     */
    public void setVersao(String versao) {
        this.versao = versao;
    }

    /**
     * @return the dataAvaliacao
     */
    public String getDataAvaliacao() {
        return dataAvaliacao;
    }

    /**
     * @param dataAvaliacao the dataAvaliacao to set
     */
    public void setDataAvaliacao(String dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    /**
     * @return the anoAvaliado
     */
    public String getAnoAvaliado() {
        return anoAvaliado;
    }

    /**
     * @param anoAvaliado the anoAvaliado to set
     */
    public void setAnoAvaliado(String anoAvaliado) {
        this.anoAvaliado = anoAvaliado;
    }

    public String getDtInicioAvaliacao() {
        return dtInicioAvaliacao;
    }

    public void setDtInicioAvaliacao(String dtInicioAvaliacao) {
        this.dtInicioAvaliacao = dtInicioAvaliacao;
    }

    public String getDtFimAvaliacao() {
        return dtFimAvaliacao;
    }

    public void setDtFimAvaliacao(String dtFimAvaliacao) {
        this.dtFimAvaliacao = dtFimAvaliacao;
    }

    /**
     * @return the nomeAgravo
     */
    public String getNomeAgravo() {
        return nomeAgravo;
    }

    /**
     * @param nomeAgravo the nomeAgravo to set
     */
    public void setNomeAgravo(String nomeAgravo) {
        this.nomeAgravo = nomeAgravo;
    }

    /**
     * @return the temListagem
     */
    public boolean isTemListagem() {
        return temListagem;
    }

    /**
     * @param temListagem the temListagem to set
     */
    public void setTemListagem(boolean temListagem) {
        this.temListagem = temListagem;
    }

    /**
     * @return the parametros
     */
    public Map getParametros() {
        return parametros;
    }

    /**
     * @param parametros the parametros to set
     */
    public void setParametros(Map parametros) {
        this.parametros = parametros;
    }

    /**
     * @return the temOpcaoTodasUFs
     */
    public boolean isTemOpcaoTodasUFs() {
        return temOpcaoTodasUFs;
    }

    /**
     * @param temOpcaoTodasUFs the temOpcaoTodasUFs to set
     */
    public void setTemOpcaoTodasUFs(boolean temOpcaoTodasUFs) {
        this.temOpcaoTodasUFs = temOpcaoTodasUFs;
    }

    public boolean isAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(boolean auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getDtFimReceb() {
        return dtFimReceb;
    }

    public void setDtFimReceb(String dtFimReceb) {
        this.dtFimReceb = dtFimReceb;
    }

    public String getDtFimTransf() {
        return dtFimTransf;
    }

    public void setDtFimTransf(String dtFimTransf) {
        this.dtFimTransf = dtFimTransf;
    }

    public String getDtInicioReceb() {
        return dtInicioReceb;
    }

    public void setDtInicioReceb(String dtInicioReceb) {
        this.dtInicioReceb = dtInicioReceb;
    }

    public String getDtInicioTrasnf() {
        return dtInicioTrasnf;
    }

    public void setDtInicioTrasnf(String dtInicioTrasnf) {
        this.dtInicioTrasnf = dtInicioTrasnf;
    }

}
