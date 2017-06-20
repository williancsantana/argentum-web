/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.classes.agravos;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.DBFUtil;
import com.org.model.classes.ColunasDbf;
import com.org.negocio.Configuracao;
import com.org.negocio.Util;
import com.org.view.Master;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanComparator;

/**
 *
 * @author geraldo
 */
public class HanseniaseCoorte extends com.org.model.classes.Agravo {

    private String cura;
    private String abandono;
    private String transfMesmoMunicipio;
    private String transfOutroMunicipio;
    private String transfOutroUf;
    private String naoPreenchido;
    private String erroDiagnostico;
    private String perNaoPreenchido;
    private String subTotal;
    private String total;
    private String perAbandono;
    private String perCura;
    private String transfOutroPais;
    private String obito;
    private String transfNaoEspecificada;
    static String dtPbInicial;
    static String dtPbFinal;
    static String dtMbInicial;
    static String dtMbFinal;

    public HanseniaseCoorte(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de Diagnóstico");
        setTipoAgregacao("de Residência Atual");
        init("postgres");
    }

    public HanseniaseCoorte() {
    }

    @Override
    public void init(String tipoBanco) {
        this.setMultiplicador(100000);
        this.setTipo("hans");
        this.setTitulo1("Situação da coorte de casos novos de hanseníase ");
//        this.setRodape("Método de cálculo: \na)  Seleção das coortes: Selecionar a coorte de novos ( mod de entrada = 1-caso novo) paucibacilares ( classificação operacional atual)  diagnosticados no ano anterior ao ano de avaliação. Para os multibacilares selecionar os casos novos diagnosticados 2  anos anteriores ao ano de avaliação. Para a emissão do relatório caberá ao usuário digitar o ano ou o período de diagnóstico em separado para os PB e MB. O Programa que emite o relatório somará os PB e os MB identificados na base de dados do Sinan\n" +
//                "b) Colunas: Após selecionar os casos segundo critérios da Coorte de Paucibacilares e de Multibacilares, selecionar por tipo de saida e colocar nas colunas corrspondentes.\n" +
//                "c)  Cálculo do percentual de cura: dividir o número de casos com \"tipo de saida\" igual a \"alta por cura\" pelo subtotal e multiplicar por 100.\n" +
//                "d)  Cálculo do percentual de abandono de tratamento: dividir o número de casos com \"tipo de saida\" igual a \"abandono\" pelo subtotal e multiplicar por 100. \n" +
//                "e)  Cálculo do percentual de Não Preenchido: dividir o número de casos com campo \"tipo de saida\" não preenchido pelo subtotal e multiplicar por 100.\n" +
//                "f) Será considerado o local de residência atual dos casos novos.\n" +
//                "g) Nível de Seleção –  Para o local  de residência atual dos casos,  o sistema oferece as seguintes opções: Brasil (total e por UF) ; UF (por regional ou por município); Regional (por município); Município (por distrito); Distrito (por bairro), Bairro.");
        this.setRodape("OBS. sobre método de cálculo: \nSeleção das coortes: " + "Casos novos (modo de entrada = 1-Caso novo) paucibacilares " + "(classificação operacional atual) diagnosticados no ano anterior ao ano " + "de avaliação e casos novos multibacilares diagnosticados 2 anos anteriores" + " ao ano de avaliação. No relatório são somados os PB e os MB selecionados na" + " base de dados do Sinan, segundo local de residência atual.\nCálculo do " + "percentual de cura, de abandono de tratamento e de tipo de saída Não " + "Preenchido: os casos novos com \"tipo de saída\" igual a \"erro " + "diagnóstico\" não são considerados no cálculo desses indicadores. ");
        this.setCura("0");
        this.setAbandono("0");
        this.setTransfMesmoMunicipio("0");
        this.setTransfOutroMunicipio("0");
        this.setTransfOutroPais("0");
        this.setTransfOutroUf("0");
        this.setErroDiagnostico("0");
        this.setObito("0");
        this.setNaoPreenchido("0");
        this.setSubTotal("0");
        this.setTotal("0");
        this.setTransfNaoEspecificada("0");
        this.setSqlNumeradorCompletitude("");
    }

    private void calculaBrasil(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        HashMap<String, HanseniaseCoorte> ufsBeans = new HashMap<String, HanseniaseCoorte>();
        DBFUtil utilDbf = new DBFUtil();
        ufsBeans = populaUfsBeansHanseniase();
        String coluna;
        if (parametros.get("municipios").toString().equals("sim")) {
            ufsBeans = populaMunicipiosBeansHans("BR", "");
            coluna = "MUNIRESAT";
        } else {
            ufsBeans = populaUfsBeansHanseniase();
            coluna = "UFRESAT";
        }

        //inicia o calculo

        String modoEntrada;
        Date dtDiagnostico;
        String classificacaoOperacionalAtual;
        String tipoAlta;
        Object[] rowObjects;

        HanseniaseCoorte hanseniaseUfResidencia;
        String dataInicio1 = (String) parametros.get("parDataInicio1");
        String dataFim1 = (String) parametros.get("parDataFim1");
        String dataInicio2 = (String) parametros.get("parDataInicio2");
        String dataFim2 = (String) parametros.get("parDataFim2");
        //loop para ler os arquivos selecionados
        String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
        for (int k = 0; k < arquivos.length; k++) {
            int i = 1;
            try {
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                utilDbf.mapearPosicoes(reader);
                double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                while ((rowObjects = reader.nextRecord()) != null) {
                    //cálculo da taxa estadual
                    //verifica a uf de residencia
                    if (utilDbf.getString(rowObjects, coluna) != null) {
                        //verifica se existe a referencia do municipio no bean
                        hanseniaseUfResidencia = ufsBeans.get(utilDbf.getString(rowObjects, coluna));
                        modoEntrada = utilDbf.getString(rowObjects, "MODOENTR", 1);
                        classificacaoOperacionalAtual = utilDbf.getString(rowObjects, "CLASSATUAL", 1);
                        dtDiagnostico = utilDbf.getDate(rowObjects, "DT_DIAG");
                        if (hanseniaseUfResidencia != null) {
                            if (modoEntrada.equals("1")) {
                                if (classificacaoOperacionalAtual != null) {
                                    if (isBetweenDates(dtDiagnostico, dataInicio1, dataFim1) && classificacaoOperacionalAtual.equals("1")) {
                                        //busca o tipo de alta
                                        tipoAlta = utilDbf.getString(rowObjects, "TPALTA_N", 1);
                                        hanseniaseUfResidencia = classificaAlta(tipoAlta, hanseniaseUfResidencia);
                                    }
                                    if (isBetweenDates(dtDiagnostico, dataInicio2, dataFim2) && classificacaoOperacionalAtual.equals("2")) {
                                        //busca o tipo de alta
                                        tipoAlta = utilDbf.getString(rowObjects, "TPALTA_N", 1);
                                        hanseniaseUfResidencia = classificaAlta(tipoAlta, hanseniaseUfResidencia);
                                    }
                                } else {
                                    System.out.println("Classificacao operacional atual em branco");
                                }
                            }
                        }
                    }
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setValue((int) percentual);
                    i++;
                }

            } catch (DBFException ex) {
                Master.mensagem("Erro:\n" + ex);
            }
        }
        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<HanseniaseCoorte> municipioBean = ufsBeans.values();
        if (parametros.get("parSgUf").toString().equals("TODAS") || parametros.get("municipios").toString().equals("sim")) {
            for (Iterator<HanseniaseCoorte> it = municipioBean.iterator(); it.hasNext();) {
                HanseniaseCoorte agravoDBF = it.next();
                this.getBeans().add(insereNoBean(agravoDBF));
            }
            Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
        }
        //inserir brasil
        HanseniaseCoorte agravoBean = new HanseniaseCoorte();
        agravoBean.setNomeMunicipio("BRASIL");
        agravoBean.init("");
        for (Iterator<HanseniaseCoorte> it = municipioBean.iterator(); it.hasNext();) {
            HanseniaseCoorte agravoUF = it.next();
            agravoBean.setAbandono(String.valueOf(Integer.parseInt(agravoUF.getAbandono()) + Integer.parseInt(agravoBean.getAbandono())));
            agravoBean.setCura(String.valueOf(Integer.parseInt(agravoUF.getCura()) + Integer.parseInt(agravoBean.getCura())));
            agravoBean.setErroDiagnostico(String.valueOf(Integer.parseInt(agravoUF.getErroDiagnostico()) + Integer.parseInt(agravoBean.getErroDiagnostico())));
            agravoBean.setNaoPreenchido(String.valueOf(Integer.parseInt(agravoUF.getNaoPreenchido()) + Integer.parseInt(agravoBean.getNaoPreenchido())));
            agravoBean.setObito(String.valueOf(Integer.parseInt(agravoUF.getObito()) + Integer.parseInt(agravoBean.getObito())));
            agravoBean.setSubTotal(String.valueOf(Integer.parseInt(agravoUF.getSubTotal()) + Integer.parseInt(agravoBean.getSubTotal())));
            agravoBean.setTotal(String.valueOf(Integer.parseInt(agravoUF.getTotal()) + Integer.parseInt(agravoBean.getTotal())));
            agravoBean.setTransfMesmoMunicipio(String.valueOf(Integer.parseInt(agravoUF.getTransfMesmoMunicipio()) + Integer.parseInt(agravoBean.getTransfMesmoMunicipio())));
            agravoBean.setTransfNaoEspecificada(String.valueOf(Integer.parseInt(agravoUF.getTransfNaoEspecificada()) + Integer.parseInt(agravoBean.getTransfNaoEspecificada())));
            agravoBean.setTransfOutroMunicipio(String.valueOf(Integer.parseInt(agravoUF.getTransfOutroMunicipio()) + Integer.parseInt(agravoBean.getTransfOutroMunicipio())));
            agravoBean.setTransfOutroPais(String.valueOf(Integer.parseInt(agravoUF.getTransfOutroPais()) + Integer.parseInt(agravoBean.getTransfOutroPais())));
            agravoBean.setTransfOutroUf(String.valueOf(Integer.parseInt(agravoUF.getTransfOutroUf()) + Integer.parseInt(agravoBean.getTransfOutroUf())));
        }
        this.getBeans().add(insereNoBean(agravoBean));

    }

    private void calculaMunicipios(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        HashMap<String, HanseniaseCoorte> municipiosBeans = new HashMap<String, HanseniaseCoorte>();
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        DBFUtil utilDbf = new DBFUtil();
        if (codRegional == null) {
            codRegional = "";
        }

        municipiosBeans = populaMunicipiosBeansHans(sgUfResidencia, codRegional);

        //inicia o calculo

        String modoEntrada;
        Date dtDiagnostico;
        String classificacaoOperacionalAtual;
        String tipoAlta;
        Object[] rowObjects;
        DecimalFormat df = new DecimalFormat("0.00");

        HanseniaseCoorte municipioResidencia;
        String dataInicio1 = (String) parametros.get("parDataInicio1");
        String dataFim1 = (String) parametros.get("parDataFim1");
        String dataInicio2 = (String) parametros.get("parDataInicio2");
        String dataFim2 = (String) parametros.get("parDataFim2");
        //loop para ler os arquivos selecionados
        String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
        for (int k = 0; k < arquivos.length; k++) {
            int i = 1;
            try {
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                utilDbf.mapearPosicoes(reader);
                double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                while ((rowObjects = reader.nextRecord()) != null) {
                    //cálculo da taxa estadual
                    //verifica a uf de residencia
                    if (utilDbf.getString(rowObjects, "UFRESAT") != null) {
                        //verifica se existe a referencia do municipio no bean
                        municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, "MUNIRESAT"));
                        modoEntrada = utilDbf.getString(rowObjects, "MODOENTR", 1);
                        classificacaoOperacionalAtual = utilDbf.getString(rowObjects, "CLASSATUAL", 1);
                        dtDiagnostico = utilDbf.getDate(rowObjects, "DT_DIAG");
                        if (municipioResidencia != null) {
                            if (modoEntrada.equals("1")) {
                                if (isBetweenDates(dtDiagnostico, dataInicio1, dataFim1) && classificacaoOperacionalAtual.equals("1")) {
                                    //busca o tipo de alta
                                    tipoAlta = utilDbf.getString(rowObjects, "TPALTA_N", 1);
                                    municipioResidencia = classificaAlta(tipoAlta, municipioResidencia);
                                }
                                if (isBetweenDates(dtDiagnostico, dataInicio2, dataFim2) && classificacaoOperacionalAtual.equals("2")) {
                                    //busca o tipo de alta
                                    tipoAlta = utilDbf.getString(rowObjects, "TPALTA_N", 1);
                                    municipioResidencia = classificaAlta(tipoAlta, municipioResidencia);
                                }
                            }
                        }
                    }
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setValue((int) percentual);
                    i++;
                }

            } catch (DBFException ex) {
                Master.mensagem("Erro:\n" + ex);
            }
        }
        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<HanseniaseCoorte> municipioBean = municipiosBeans.values();

        for (Iterator<HanseniaseCoorte> it = municipioBean.iterator(); it.hasNext();) {
            HanseniaseCoorte agravoDBF = it.next();
            this.getBeans().add(insereNoBean(agravoDBF));
        }
        Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
    }

    @Override
    public void calcula(DBFReader reader, Map parametros) {
        populaDatas(parametros);
        init("");
        String municipios = (String) parametros.get("municipios");
        String brasil = (String) parametros.get("parUf");
        if (municipios.equals("sim") && !brasil.equals("brasil")) {
            try {
                calculaMunicipios(reader, parametros);
            } catch (ParseException ex) {
                System.out.println(ex);
            }
        } else {
            try {
                if (brasil.equals("brasil")) {
                    calculaBrasil(reader, parametros);
                } else {
                    Object[] rowObjects;
                    DBFUtil utilDbf = new DBFUtil();
                    String modoEntrada;
                    Date dtDiagnostico;
                    String classificacaoOperacionalAtual;
                    String tipoAlta;
                    String ufResidencia = (String) parametros.get("parUf");
                    String municipioResidencia = (String) parametros.get("parMunicipio");
                    if (municipioResidencia == null) {
                        municipioResidencia = "";
                    }
                    String dataInicio1 = (String) parametros.get("parDataInicio1");
                    String dataFim1 = (String) parametros.get("parDataFim1");
                    String dataInicio2 = (String) parametros.get("parDataInicio2");
                    String dataFim2 = (String) parametros.get("parDataFim2");
                    //loop para ler os arquivos selecionados
                    String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
                    for (int k = 0; k < arquivos.length; k++) {
                        int i = 1;
                        reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                        utilDbf.mapearPosicoes(reader);
                        double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                        while ((rowObjects = reader.nextRecord()) != null) {
                            //cálculo da taxa estadual
                            //verifica a uf de residencia ATUAL
                            if (utilDbf.getString(rowObjects, "UFRESAT") != null) {
                                if (utilDbf.getString(rowObjects, "UFRESAT").equals(ufResidencia)) {
                                    //verifica se tem o parametro de municipio de residencia
                                    modoEntrada = utilDbf.getString(rowObjects, "MODOENTR", 1);
                                    classificacaoOperacionalAtual = utilDbf.getString(rowObjects, "CLASSATUAL", 1);
                                    dtDiagnostico = utilDbf.getDate(rowObjects, "DT_DIAG");
                                    if (verificaMunicipio(municipioResidencia, utilDbf.getString(rowObjects, "MUNIRESAT"))) {
                                        //calcula PB
                                        if (modoEntrada.equals("1")) {
                                            if (isBetweenDates(dtDiagnostico, dataInicio1, dataFim1) && classificacaoOperacionalAtual.equals("1")) {
                                                //busca o tipo de alta
                                                tipoAlta = utilDbf.getString(rowObjects, "TPALTA_N", 1);
                                                classificaAlta(tipoAlta);
                                            }
                                            if (isBetweenDates(dtDiagnostico, dataInicio2, dataFim2) && classificacaoOperacionalAtual.equals("2")) {
                                                //busca o tipo de alta
                                                tipoAlta = utilDbf.getString(rowObjects, "TPALTA_N", 1);
                                                classificaAlta(tipoAlta);
                                            }
                                        }
                                    }
                                }
                            }
                            float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                            getBarraStatus().setValue((int) percentual);
                            i++;
                        }
                    }

                    this.setBeans(new ArrayList());
                    String nomeElemento;
                    if (municipioResidencia.equals("")) {
                        nomeElemento = (String) parametros.get("parSgUf");
                    } else {
                        nomeElemento = (String) parametros.get("parNomeMunicipio");
                    }
                    this.getBeans().add(insereNoBean(ufResidencia, nomeElemento));
                }

            } catch (NumberFormatException ex) {
                System.out.println(ex);
            } catch (ParseException ex) {
                System.out.println(ex);
            } catch (DBFException ex) {
                System.out.println(ex);
            }
        }
    }

    @Override
    public List getBeansMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
        populaDatas(parametros);
        if (isDBF()) {
            return this.getBeans();
        } else {
            this.init("");
            ResultSet rs2;
            String municipio, noMunicipio = null;
            List beans = new ArrayList();
            Hanseniase d1 = null;
            DecimalFormat df = new DecimalFormat("0.0");

            municipio = parametros.get("parMunicipio").toString();
            noMunicipio = parametros.get("parNomeMunicipio").toString();
            String sql1 = "select distinct(tp_alta) as alta,count(*) as c " + " from dbsinan.tb_investiga_hanseniase t1 inner join dbsinan.tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao  and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A30.9'  and  t1.co_municipio_residencia_atual= ? and tp_modo_entrada=1 and dt_diagnostico_sintoma between ? and ? and tp_classific_operacao_atual=? and ( tp_duplicidade is null or tp_duplicidade=1  )" + " group by tp_alta";

            PreparedStatement stm2;
            try {
                stm2 = con.prepareStatement(sql1);
                stm2.setString(1, municipio);
                //calcula para PB
                stm2.setString(2, parametros.get("parDataInicio1").toString());
                stm2.setString(3, parametros.get("parDataFim1").toString());
                stm2.setString(4, "1");
                rs2 = stm2.executeQuery();
            } catch (Exception exception) {
                sql1 = "select distinct(tp_alta) as alta,count(*) as c " + " from tb_investiga_hanseniase t1 inner join tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A30.9'  and  t1.co_municipio_residencia_atual = " + municipio + " and " + "tp_modo_entrada=1 and " + "dt_diagnostico_sintoma between '" + parametros.get("parDataInicio1").toString() + "' and '" + parametros.get("parDataFim1").toString() + "' and " + "tp_classific_operacao_atual=1 and ( tp_duplicidade is null or tp_duplicidade=1  )" + " group by tp_alta";
                System.out.println(sql1);
                stm2 = con.prepareStatement(sql1);
                rs2 = stm2.executeQuery();
            }
            this.preencheValores(rs2);
            //calcula para MB
            try {
                stm2 = con.prepareStatement(sql1);
                stm2.setString(1, municipio);
                //calcula para PB
                stm2.setString(2, parametros.get("parDataInicio2").toString());
                stm2.setString(3, parametros.get("parDataFim2").toString());
                stm2.setString(4, "2");
                rs2 = stm2.executeQuery();
            } catch (Exception exception) {
                sql1 = "select distinct(tp_alta) as alta,count(*) as c " + " from tb_investiga_hanseniase t1 inner join tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao  and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A30.9'  and  t1.co_municipio_residencia_atual = " + municipio + " and " + "tp_modo_entrada=1 and " + "dt_diagnostico_sintoma between '" + parametros.get("parDataInicio2").toString() + "' and '" + parametros.get("parDataFim2").toString() + "' and " + "tp_classific_operacao_atual=2 and ( tp_duplicidade is null or tp_duplicidade=1  )" + " group by tp_alta";
                System.out.println(sql1);
                stm2 = con.prepareStatement(sql1);
                rs2 = stm2.executeQuery();
            }
            this.preencheValores(rs2);
            beans.add(insereNoBean(municipio, noMunicipio));
            System.out.println("terminou");
            return beans;
        }

    }

    public static String formataData(String data) {
        String[] d = data.split("-");
        return d[2] + "/" + d[1] + "/" + d[0];
    }

    public void populaDatas(Map parametros) {
        dtMbFinal = formataData(parametros.get("parDataFim2").toString());
        dtMbInicial = formataData(parametros.get("parDataInicio2").toString());
        dtPbFinal = formataData(parametros.get("parDataFim1").toString());
        dtPbInicial = formataData(parametros.get("parDataInicio1").toString());
    }

    @Override
    public List getBeansEstadoEspecifico(Connection con, Map parametros) throws SQLException {
        populaDatas(parametros);
        if (isDBF()) {
            return getBeans();
        } else {
            if (parametros.get("municipios").equals("nao")) {
                return this.getBeanEstadoEspecifico(con, parametros);
            } else {
                return this.getBeanMunicipios(con, parametros);
            }
        }

    }

    @Override
    public List getBeanMunicipios(Connection con, Map parametros) throws SQLException {
        populaDatas(parametros);
        if (isDBF()) {
            return getBeans();
        } else {
            String sql;
            java.sql.Statement stm = con.createStatement();
            ResultSet rs, rs2;
            String municipio, noMunicipio = null;
            List beans = new ArrayList();
            Hanseniase d1 = null;
            if (parametros.get("parNomeRegional") == null) {
                parametros.put("parNomeRegional", "-- Selecione --");
            }
            if (parametros.get("parNomeRegional").equals("Todas Regionais") || parametros.get("parNomeRegional").equals("-- Selecione --")) {
                sql = "select co_municipio_ibge,no_municipio from dbgeral.tb_municipio where sg_uf = '" + parametros.get("parSgUf") + "' order by no_municipio";
            } else {
                sql = "select t1.co_municipio_ibge,no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + parametros.get("parUf") + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + parametros.get("parCodRegional") + "' and no_municipio not like '%Ignorado%'  order by no_municipio";
            }
            try {
                rs = stm.executeQuery(sql);
            } catch (Exception exception) {
                if (parametros.get("parNomeRegional").equals("Todas Regionais") || parametros.get("parNomeRegional").equals("-- Selecione --")) {
                    sql = "select co_municipio_ibge,no_municipio from tb_municipio where sg_uf = '" + parametros.get("parSgUf") + "' order by no_municipio";
                } else {
                    sql = "select t1.co_municipio_ibge,no_municipio from tb_municipio as t1, rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + parametros.get("parUf") + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + parametros.get("parCodRegional") + "' and no_municipio not like '%Ignorado%'  order by no_municipio";
                }
                rs = stm.executeQuery(sql);
            }
            this.init("");
            while (rs.next()) {
                this.init("");
                municipio = rs.getString("co_municipio_ibge");
                noMunicipio = rs.getString("no_municipio");
                String sql1 = "select distinct(tp_alta) as alta,count(*) as c " + " from dbsinan.tb_investiga_hanseniase t1 inner join dbsinan.tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao  and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A30.9'  and  t1.co_municipio_residencia_atual= ? and tp_modo_entrada=1 and dt_diagnostico_sintoma between ? and ? and tp_classific_operacao_atual=? and ( tp_duplicidade is null or tp_duplicidade=1  )" + " group by tp_alta";
                PreparedStatement stm2;
                try {
                    stm2 = con.prepareStatement(sql1);
                    stm2.setString(1, municipio);
                    //calcula para PB
                    stm2.setString(2, parametros.get("parDataInicio1").toString());
                    stm2.setString(3, parametros.get("parDataFim1").toString());
                    stm2.setString(4, "1");
                    rs2 = stm2.executeQuery();
                } catch (Exception exception) {
                    sql1 = "select distinct(tp_alta) as alta,count(*) as c " + " from tb_investiga_hanseniase t1 inner join tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao  and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A30.9'  and  t1.co_municipio_residencia_atual = " + municipio + " and " + "tp_modo_entrada=1 and " + "dt_diagnostico_sintoma between '" + parametros.get("parDataInicio1").toString() + "' and '" + parametros.get("parDataFim1").toString() + "' and " + "tp_classific_operacao_atual=1 and ( tp_duplicidade is null or tp_duplicidade=1  )" + " group by tp_alta";
                    stm2 = con.prepareStatement(sql1);
                    rs2 = stm2.executeQuery();
                }
                this.preencheValores(rs2);
                //calcula para MB
                try {
                    stm2 = con.prepareStatement(sql1);
                    stm2.setString(1, municipio);
                    //calcula para PB
                    stm2.setString(2, parametros.get("parDataInicio2").toString());
                    stm2.setString(3, parametros.get("parDataFim2").toString());
                    stm2.setString(4, "2");
                    rs2 = stm2.executeQuery();
                } catch (Exception exception) {
                    sql1 = "select distinct(tp_alta) as alta,count(*) as c " + " from tb_investiga_hanseniase t1 inner join tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao  and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A30.9'  and  t1.co_municipio_residencia_atual = " + municipio + " and " + "tp_modo_entrada=1 and " + "dt_diagnostico_sintoma between '" + parametros.get("parDataInicio2").toString() + "' and '" + parametros.get("parDataFim2").toString() + "' and " + "tp_classific_operacao_atual=2 and ( tp_duplicidade is null or tp_duplicidade=1  )" + " group by tp_alta";
                    stm2 = con.prepareStatement(sql1);
                    rs2 = stm2.executeQuery();
                }
                this.preencheValores(rs2);
                beans.add(insereNoBean(municipio, noMunicipio));
            }
            System.out.println("terminou");

            return beans;
        }

    }

    public void preencheValores(ResultSet rs2) throws SQLException {
        int temp = 0;
        String tipoAlta;
        while (rs2.next()) {
            tipoAlta = rs2.getString("alta");
            temp = rs2.getInt("c");
            if (tipoAlta != null && !tipoAlta.equals("")) {
                this.setSubTotal(String.valueOf(temp + Integer.parseInt(getSubTotal())));
                if (tipoAlta.equals("1")) {
                    temp = temp + Integer.parseInt(getCura());
                    this.setCura(String.valueOf(temp));
                }
                if (tipoAlta.equals("2")) {
                    temp = temp + Integer.parseInt(getTransfMesmoMunicipio());
                    this.setTransfMesmoMunicipio(String.valueOf(temp));
                }
                if (tipoAlta.equals("3")) {
                    temp = temp + Integer.parseInt(getTransfOutroMunicipio());
                    this.setTransfOutroMunicipio(String.valueOf(temp));
                }
                if (tipoAlta.equals("4")) {
                    temp = temp + Integer.parseInt(getTransfOutroUf());
                    this.setTransfOutroUf(String.valueOf(temp));
                }
                //outro pais
                if (tipoAlta.equals("5")) {
                    temp = temp + Integer.parseInt(getTransfOutroPais());
                    this.setTransfOutroPais(String.valueOf(temp));
                }
                //obito
                if (tipoAlta.equals("6")) {
                    temp = temp + Integer.parseInt(getObito());
                    this.setObito(String.valueOf(temp));
                }
                if (tipoAlta.equals("7")) {
                    temp = temp + Integer.parseInt(getAbandono());
                    this.setAbandono(String.valueOf(temp));
                }
                if (tipoAlta.equals("8")) {
                    this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal()) - temp));
                    temp = temp + Integer.parseInt(getErroDiagnostico());
                    this.setErroDiagnostico(String.valueOf(temp));
                }
                if (tipoAlta.equals("9")) {
                    temp = temp + Integer.parseInt(getTransfNaoEspecificada());
                    this.setTransfNaoEspecificada(String.valueOf(temp));
                }
            } else {
                this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal()) + temp));
                temp = temp + Integer.parseInt(getNaoPreenchido());
                this.setNaoPreenchido(String.valueOf(temp));
            }
            this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal())));
        }
    }

    private void classificaAlta(String tipoAlta) {
        int temp = 1;
        if (tipoAlta != null && !tipoAlta.equals("")) {
            this.setSubTotal(String.valueOf(temp + Integer.parseInt(getSubTotal())));
            if (tipoAlta.equals("1")) {
                temp = temp + Integer.parseInt(getCura());
                this.setCura(String.valueOf(temp));
            }
            if (tipoAlta.equals("2")) {
                temp = temp + Integer.parseInt(getTransfMesmoMunicipio());
                this.setTransfMesmoMunicipio(String.valueOf(temp));
            }
            if (tipoAlta.equals("3")) {
                temp = temp + Integer.parseInt(getTransfOutroMunicipio());
                this.setTransfOutroMunicipio(String.valueOf(temp));
            }
            if (tipoAlta.equals("4")) {
                temp = temp + Integer.parseInt(getTransfOutroUf());
                this.setTransfOutroUf(String.valueOf(temp));
            }
            //outro pais
            if (tipoAlta.equals("5")) {
                temp = temp + Integer.parseInt(getTransfOutroPais());
                this.setTransfOutroPais(String.valueOf(temp));
            }
            //obito
            if (tipoAlta.equals("6")) {
                temp = temp + Integer.parseInt(getObito());
                this.setObito(String.valueOf(temp));
            }
            if (tipoAlta.equals("7")) {
                temp = temp + Integer.parseInt(getAbandono());
                this.setAbandono(String.valueOf(temp));
            }
            if (tipoAlta.equals("8")) {
                this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal()) - temp));
                temp = temp + Integer.parseInt(getErroDiagnostico());
                this.setErroDiagnostico(String.valueOf(temp));
            }
            if (tipoAlta.equals("9")) {
                temp = temp + Integer.parseInt(getTransfNaoEspecificada());
                this.setTransfNaoEspecificada(String.valueOf(temp));
            }
        } else {
            this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal()) + temp));
            temp = temp + Integer.parseInt(getNaoPreenchido());
            this.setNaoPreenchido(String.valueOf(temp));
        }
        this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal())));
    }

    private HanseniaseCoorte classificaAlta(String tipoAlta, HanseniaseCoorte beanMunicipioResidencia) {
        int temp = 1;
        if (tipoAlta != null && !tipoAlta.equals("")) {
            beanMunicipioResidencia.setSubTotal(String.valueOf(temp + Integer.parseInt(beanMunicipioResidencia.getSubTotal())));
            if (tipoAlta.equals("1")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getCura());
                beanMunicipioResidencia.setCura(String.valueOf(temp));
            }
            if (tipoAlta.equals("2")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getTransfMesmoMunicipio());
                beanMunicipioResidencia.setTransfMesmoMunicipio(String.valueOf(temp));
            }
            if (tipoAlta.equals("3")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getTransfOutroMunicipio());
                beanMunicipioResidencia.setTransfOutroMunicipio(String.valueOf(temp));
            }
            if (tipoAlta.equals("4")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getTransfOutroUf());
                beanMunicipioResidencia.setTransfOutroUf(String.valueOf(temp));
            }
            //outro pais
            if (tipoAlta.equals("5")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getTransfOutroPais());
                beanMunicipioResidencia.setTransfOutroPais(String.valueOf(temp));
            }
            //obito
            if (tipoAlta.equals("6")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getObito());
                beanMunicipioResidencia.setObito(String.valueOf(temp));
            }
            if (tipoAlta.equals("7")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getAbandono());
                beanMunicipioResidencia.setAbandono(String.valueOf(temp));
            }
            if (tipoAlta.equals("8")) {
                beanMunicipioResidencia.setSubTotal(String.valueOf(Integer.parseInt(beanMunicipioResidencia.getSubTotal()) - temp));
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getErroDiagnostico());
                beanMunicipioResidencia.setErroDiagnostico(String.valueOf(temp));
            }
            if (tipoAlta.equals("9")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getTransfNaoEspecificada());
                beanMunicipioResidencia.setTransfNaoEspecificada(String.valueOf(temp));
            }
        } else {
            beanMunicipioResidencia.setSubTotal(String.valueOf(Integer.parseInt(beanMunicipioResidencia.getSubTotal()) + temp));
            temp = temp + Integer.parseInt(beanMunicipioResidencia.getNaoPreenchido());
            beanMunicipioResidencia.setNaoPreenchido(String.valueOf(temp));
        }
        beanMunicipioResidencia.setSubTotal(String.valueOf(Integer.parseInt(beanMunicipioResidencia.getSubTotal())));
        return beanMunicipioResidencia;
    }

    public HanseniaseCoorte insereNoBean(HanseniaseCoorte beanHans) {

        DecimalFormat df = new DecimalFormat("0.0");
        if (beanHans.getSubTotal().equals("-1")) {
            beanHans.setSubTotal("0");
        }
        String sub = beanHans.getSubTotal();
        if (beanHans.getSubTotal().equals("0")) {
            sub = "1";
        }
        beanHans.setTotal(String.valueOf(Integer.valueOf(beanHans.getSubTotal()) + Integer.valueOf(beanHans.getErroDiagnostico())));
        beanHans.setPerAbandono(df.format(Float.valueOf(beanHans.getAbandono()) / Float.valueOf(sub) * 100));
        beanHans.setPerCura(df.format(Float.valueOf(beanHans.getCura()) / Float.valueOf(sub) * 100));
        beanHans.setPerNaoPreenchido(df.format(Float.valueOf(beanHans.getNaoPreenchido()) / Float.valueOf(sub) * 100));
        return beanHans;
    }

    public HanseniaseCoorte insereNoBean(String municipio, String noMunicipio) {

        DecimalFormat df = new DecimalFormat("0.0");
        HanseniaseCoorte d1 = new HanseniaseCoorte(true);
        d1.setCodMunicipio(municipio);
        d1.setNomeMunicipio(noMunicipio);
        getBarraStatus().setString("Calculando município: " + noMunicipio);
        this.setCura(String.valueOf(Integer.parseInt(this.getCura())));
//        if (this.getCura().equals("-1")){
//            this.setCura("1");
//        }

        d1.setCura(this.getCura());
        d1.setTransfMesmoMunicipio(this.getTransfMesmoMunicipio());
        d1.setTransfOutroMunicipio(this.getTransfOutroMunicipio());
        d1.setTransfOutroPais(this.getTransfOutroPais());
        d1.setTransfOutroUf(this.getTransfOutroUf());
        d1.setObito(this.getObito());
        d1.setAbandono(this.getAbandono());
        d1.setErroDiagnostico(this.getErroDiagnostico());
        d1.setNaoPreenchido(this.getNaoPreenchido());
        d1.setTransfNaoEspecificada(this.getTransfNaoEspecificada());
        if (this.getSubTotal().equals("-1")) {
            this.setSubTotal("0");
        }
        d1.setSubTotal(this.getSubTotal());
        String sub = this.getSubTotal();
        if (d1.getSubTotal().equals("0")) {
            sub = "1";
        }
        d1.setTotal(String.valueOf(Integer.valueOf(this.getSubTotal()) + Integer.valueOf(this.getErroDiagnostico())));
        d1.setPerAbandono(df.format(Float.valueOf(this.getAbandono()) / Float.valueOf(sub) * 100));
        d1.setPerCura(df.format(Float.valueOf(this.getCura()) / Float.valueOf(sub) * 100));
        d1.setPerNaoPreenchido(df.format(Float.valueOf(this.getNaoPreenchido()) / Float.valueOf(sub) * 100));
        return d1;
    }

    public HashMap<String, HanseniaseCoorte> populaUfsBeansHanseniase() {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> uf = new HashMap<String, String>();
        HashMap<String, HanseniaseCoorte> ufsBeans = new HashMap<String, HanseniaseCoorte>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela

        //busca municipios dessa regional
        DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("UF", "dbf\\");
        Object[] rowObjects1;
        try {
            utilDbf.mapearPosicoes(readerMunicipio);

            while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {

                HanseniaseCoorte agravoDbf = new HanseniaseCoorte();
                agravoDbf.init("");
                agravoDbf.setCodMunicipio(utilDbf.getString(rowObjects1, "ID_UF"));
                agravoDbf.setNomeMunicipio(utilDbf.getString(rowObjects1, "SG_UF"));
                uf.put(utilDbf.getString(rowObjects1, "ID_UF"), utilDbf.getString(rowObjects1, "SG_UF"));
                ufsBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);

            }
        } catch (DBFException e) {
            Master.mensagem("Erro ao carregar municipios:\n" + e);
        }

        uf = sortHashMapByValues(uf, false);
        Set<String> ufKeys = uf.keySet();
        HashMap<String, HanseniaseCoorte> ufsBeansRetorno = new HashMap<String, HanseniaseCoorte>();
        Iterator valueIt = ufKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            ufsBeansRetorno.put(key, ufsBeans.get(key));

        }
        return ufsBeansRetorno;
    }

    @Override
    public String getTaxaEstado(Connection con, Map parametros) throws SQLException {
        if (isDBF()) {
            //ler o arquivo dbf
            DBFReader reader = null;
//            Util.retornaObjetoDbf(Configuracao.getPropriedade("caminho"));
//            if (reader == null) {
//                abreJanelaEscolherCaminho();
//            }
            calcula(reader, parametros);
            return getTaxaEstadual();
        } else {
            getBeanEstadoEspecifico(con, parametros);
            return "";
        }
    }

    public List getBeanEstadoEspecifico(Connection con, Map parametros) throws SQLException {
        this.init("");
        ResultSet rs2;
        String uf, noUF = null;
        List beans = new ArrayList();
        Hanseniase d1 = null;
        DecimalFormat df = new DecimalFormat("0.0");

        uf = parametros.get("parUf").toString();
        noUF = parametros.get("parSgUf").toString();
        String sql1 = "select distinct(tp_alta) as alta,count(*) as c " + " from dbsinan.tb_investiga_hanseniase t1 inner join dbsinan.tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao  and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A30.9'  and  t1.co_uf_residencia_atual = ? and tp_modo_entrada=1 and dt_diagnostico_sintoma between ? and ? and tp_classific_operacao_atual=? and ( tp_duplicidade is null or tp_duplicidade=1  )" + " group by tp_alta";

        PreparedStatement stm2;
        try {
            stm2 = con.prepareStatement(sql1);
            stm2.setString(1, uf);
            System.out.println(sql1);
            System.out.println(uf);
            System.out.println(parametros.get("parDataInicio1").toString());
            System.out.println(parametros.get("parDataFim1").toString());
            //calcula para PB
            stm2.setString(2, parametros.get("parDataInicio1").toString());
            stm2.setString(3, parametros.get("parDataFim1").toString());
            stm2.setString(4, "1");
            rs2 = stm2.executeQuery();
        } catch (Exception exception) {
            sql1 = "select distinct(tp_alta) as alta,count(*) as c " + " from tb_investiga_hanseniase t1 inner join tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao  and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A30.9'  and  t1.co_uf_residencia_atual = " + uf + " and " + "tp_modo_entrada=1 and " + "dt_diagnostico_sintoma between '" + parametros.get("parDataInicio1").toString() + "' and '" + parametros.get("parDataFim1").toString() + "' and " + "tp_classific_operacao_atual=1 and ( tp_duplicidade is null or tp_duplicidade=1  )" + " group by tp_alta";
            stm2 = con.prepareStatement(sql1);
            rs2 = stm2.executeQuery();
        }
        this.preencheValores(rs2);
        //calcula para MB
        try {
            stm2.setString(4, "2");
            stm2.setString(2, parametros.get("parDataInicio2").toString());
            stm2.setString(3, parametros.get("parDataFim2").toString());
            rs2 = stm2.executeQuery();
        } catch (Exception exception) {
            sql1 = "select distinct(tp_alta) as alta,count(*) as c " + " from tb_investiga_hanseniase t1 inner join tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao  and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A30.9'  and  t1.co_uf_residencia_atual = " + uf + " and " + "tp_modo_entrada=1 and " + "dt_diagnostico_sintoma between '" + parametros.get("parDataInicio2").toString() + "' and '" + parametros.get("parDataFim2").toString() + "' and " + "tp_classific_operacao_atual=2 and ( tp_duplicidade is null or tp_duplicidade=1  )" + " group by tp_alta";
            stm2 = con.prepareStatement(sql1);
            rs2 = stm2.executeQuery();
        }


        this.preencheValores(rs2);
        beans.add(insereNoBean(uf, noUF));
        System.out.println("terminou");

        return beans;
    }

    public void reset() {
        this.setCura("0");
        this.setAbandono("0");
        this.setTransfMesmoMunicipio("0");
        this.setTransfOutroMunicipio("0");
        this.setTransfOutroPais("0");
        this.setTransfOutroUf("0");
        this.setErroDiagnostico("0");
        this.setObito("0");
        this.setNaoPreenchido("0");
        this.setSubTotal("0");
        this.setTransfNaoEspecificada("0");
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("ID_LOCRES", new ColunasDbf(7));
        hashColunas.put("DS_LOCRES", new ColunasDbf(30));
        hashColunas.put("ID_UFRES", new ColunasDbf(2));
        hashColunas.put("N_CURHANS", new ColunasDbf(10, 0));
        hashColunas.put("I_CURHANS", new ColunasDbf(4, 2));
        hashColunas.put("TS_ABAND", new ColunasDbf(10, 0));
        hashColunas.put("P_TSABAND", new ColunasDbf(4, 2));
        hashColunas.put("TS_MESMUN", new ColunasDbf(10, 0));
        hashColunas.put("TS_OUTMUN", new ColunasDbf(10, 0));
        hashColunas.put("TS_OUTUF", new ColunasDbf(10, 0));
        hashColunas.put("TS_OUTPA", new ColunasDbf(10, 0));
        hashColunas.put("TS_IGNOR", new ColunasDbf(10, 0));
        hashColunas.put("P_TSIGNOR", new ColunasDbf(4, 2));
        hashColunas.put("TS_TRANNE", new ColunasDbf(10, 0));
        hashColunas.put("TS_OBITO", new ColunasDbf(10, 0));
        hashColunas.put("D_SUBHANS", new ColunasDbf(4, 0));
        hashColunas.put("TS_ERRDIA", new ColunasDbf(10, 0));
        hashColunas.put("TOTAL_NOT", new ColunasDbf(4, 0));
        hashColunas.put("ORIGEM", new ColunasDbf(30));
        hashColunas.put("ANO_DIGPB", new ColunasDbf(4, 0));
        hashColunas.put("DT_DPBINI", new ColunasDbf(10));
        hashColunas.put("DT_DPBFIN", new ColunasDbf(10));
        hashColunas.put("ANO_DIGMB", new ColunasDbf(4, 0));
        hashColunas.put("DT_DMBINI", new ColunasDbf(10));
        hashColunas.put("DT_DMBFIN", new ColunasDbf(10));
        this.setColunas(hashColunas);
        return hashColunas;
    }

    @Override
    public String[] getOrdemColunas() {
        return new String[]{"ID_LOCRES", "DS_LOCRES", "ID_UFRES", "N_CURHANS", "I_CURHANS", "TS_ABAND", "P_TSABAND", "TS_MESMUN",
                    "TS_OUTMUN", "TS_OUTUF", "TS_OUTPA", "TS_IGNOR", "P_TSIGNOR", "TS_TRANNE", "TS_OBITO",
                    "D_SUBHANS", "TS_ERRDIA", "TOTAL_NOT", "ANO_DIGPB", "DT_DPBINI", "DT_DPBFIN", "ANO_DIGMB", "DT_DMBINI", "DT_DMBFIN", "ORIGEM"};
    }

    @Override
    public Map getParametros() {
        Util util = new Util();
        Map parametros = new HashMap();
        parametros.put("parRodape", this.getRodape());
        parametros.put("parConfig", "");
        parametros.put("parTitulo1", this.getTitulo1());
        return parametros;
    }

    @Override
    public DBFWriter getLinhas(HashMap<String, ColunasDbf> colunas, List bean, DBFWriter writer) throws DBFException, IOException {
        for (int i = 0; i < bean.size(); i++) {
            Object rowData[] = new Object[colunas.size()];
            HanseniaseCoorte agravo = (HanseniaseCoorte) bean.get(i);
            if (agravo.getNomeMunicipio().equals("BRASIL")) {
                rowData[0] = null;
                rowData[2] = null;
            } else {
                rowData[0] = agravo.getCodMunicipio();
                rowData[2] = agravo.getCodMunicipio().substring(0, 2);
            }
            rowData[1] = agravo.getNomeMunicipio();
            rowData[3] = Double.parseDouble(agravo.getCura());
            rowData[4] = Double.parseDouble(agravo.getPerCura().replace(",", "."));
            rowData[5] = Double.parseDouble(agravo.getAbandono());
            rowData[6] = Double.parseDouble(agravo.getPerAbandono().replace(",", "."));
            rowData[7] = Double.parseDouble(agravo.getTransfMesmoMunicipio());
            rowData[8] = Double.parseDouble(agravo.getTransfOutroMunicipio());
            rowData[9] = Double.parseDouble(agravo.getTransfOutroUf());
            rowData[10] = Double.parseDouble(agravo.getTransfOutroPais());
            rowData[11] = Double.parseDouble(agravo.getNaoPreenchido());
            rowData[12] = Double.parseDouble(agravo.getPerNaoPreenchido().replace(",", "."));
            rowData[13] = Double.parseDouble(agravo.getTransfNaoEspecificada());
            rowData[14] = Double.parseDouble(agravo.getObito());
            rowData[15] = Double.parseDouble(agravo.getSubTotal());
            rowData[16] = Double.parseDouble(agravo.getErroDiagnostico());
            rowData[17] = Double.parseDouble(agravo.getTotal());
            rowData[18] = preencheAno(dtPbInicial, dtPbFinal);
            rowData[19] = dtPbInicial;
            rowData[20] = dtPbFinal;
            rowData[21] = preencheAno(dtMbInicial, dtMbFinal);
            rowData[22] = dtMbInicial;
            rowData[23] = dtMbFinal;
            rowData[24] = "HANSENIASE-SINANNET";

            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/hanseniase.jasper";
    }

    /**
     * @return the cura
     */
    public String getCura() {
        return cura;
    }

    /**
     * @param cura the cura to set
     */
    public void setCura(String cura) {
        this.cura = cura;
    }

    /**
     * @return the abandono
     */
    public String getAbandono() {
        return abandono;
    }

    /**
     * @param abandono the abandono to set
     */
    public void setAbandono(String abandono) {
        this.abandono = abandono;
    }

    /**
     * @return the transfMesmoMunicipio
     */
    public String getTransfMesmoMunicipio() {
        return transfMesmoMunicipio;
    }

    /**
     * @param transfMesmoMunicipio the transfMesmoMunicipio to set
     */
    public void setTransfMesmoMunicipio(String transfMesmoMunicipio) {
        this.transfMesmoMunicipio = transfMesmoMunicipio;
    }

    /**
     * @return the transfOutroMunicipio
     */
    public String getTransfOutroMunicipio() {
        return transfOutroMunicipio;
    }

    /**
     * @param transfOutroMunicipio the transfOutroMunicipio to set
     */
    public void setTransfOutroMunicipio(String transfOutroMunicipio) {
        this.transfOutroMunicipio = transfOutroMunicipio;
    }

    /**
     * @return the transfOutroUf
     */
    public String getTransfOutroUf() {
        return transfOutroUf;
    }

    /**
     * @param transfOutroUf the transfOutroUf to set
     */
    public void setTransfOutroUf(String transfOutroUf) {
        this.transfOutroUf = transfOutroUf;
    }

    /**
     * @return the naoPreenchido
     */
    public String getNaoPreenchido() {
        return naoPreenchido;
    }

    /**
     * @param naoPreenchido the naoPreenchido to set
     */
    public void setNaoPreenchido(String naoPreenchido) {
        this.naoPreenchido = naoPreenchido;
    }

    /**
     * @return the erroDiagnostico
     */
    public String getErroDiagnostico() {
        return erroDiagnostico;
    }

    /**
     * @param erroDiagnostico the erroDiagnostico to set
     */
    public void setErroDiagnostico(String erroDiagnostico) {
        this.erroDiagnostico = erroDiagnostico;
    }

    /**
     * @return the perNaoPreenchido
     */
    public String getPerNaoPreenchido() {
        return perNaoPreenchido;
    }

    /**
     * @param perNaoPreenchido the perNaoPreenchido to set
     */
    public void setPerNaoPreenchido(String perNaoPreenchido) {
        this.perNaoPreenchido = perNaoPreenchido;
    }

    /**
     * @return the subTotal
     */
    public String getSubTotal() {
        return subTotal;
    }

    /**
     * @param subTotal the subTotal to set
     */
    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    /**
     * @return the total
     */
    public String getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(String total) {
        this.total = total;
    }

    /**
     * @return the perAbandono
     */
    public String getPerAbandono() {
        return perAbandono;
    }

    /**
     * @param perAbandono the perAbandono to set
     */
    public void setPerAbandono(String perAbandono) {
        this.perAbandono = perAbandono;
    }

    /**
     * @return the perCura
     */
    public String getPerCura() {
        return perCura;
    }

    /**
     * @param perCura the perCura to set
     */
    public void setPerCura(String perCura) {
        this.perCura = perCura;
    }

    /**
     * @return the transfOutroPais
     */
    public String getTransfOutroPais() {
        return transfOutroPais;
    }

    /**
     * @param transfOutroPais the transfOutroPais to set
     */
    public void setTransfOutroPais(String transfOutroPais) {
        this.transfOutroPais = transfOutroPais;
    }

    /**
     * @return the obito
     */
    public String getObito() {
        return obito;
    }

    /**
     * @param obito the obito to set
     */
    public void setObito(String obito) {
        this.obito = obito;
    }

    public String getTransfNaoEspecificada() {
        return transfNaoEspecificada;
    }

    /**
     * @param transfNaoEspecificada the transfNaoEspecificada to set
     */
    public void setTransfNaoEspecificada(String transfNaoEspecificada) {
        this.transfNaoEspecificada = transfNaoEspecificada;
    }

    /**
     * @return the dtPbInicial
     */
    public String getDtPbInicial() {
        return dtPbInicial;
    }

    /**
     * @param dtPbInicial the dtPbInicial to set
     */
    public void setDtPbInicial(String dtPbInicial) {
        this.dtPbInicial = dtPbInicial;
    }

    /**
     * @return the dtPbFinal
     */
    public String getDtPbFinal() {
        return dtPbFinal;
    }

    /**
     * @param dtPbFinal the dtPbFinal to set
     */
    public void setDtPbFinal(String dtPbFinal) {
        this.dtPbFinal = dtPbFinal;
    }

    /**
     * @return the dtMbInicial
     */
    public String getDtMbInicial() {
        return dtMbInicial;
    }

    /**
     * @param dtMbInicial the dtMbInicial to set
     */
    public void setDtMbInicial(String dtMbInicial) {
        this.dtMbInicial = dtMbInicial;
    }

    /**
     * @return the dtMbFinal
     */
    public String getDtMbFinal() {
        return dtMbFinal;
    }

    /**
     * @param dtMbFinal the dtMbFinal to set
     */
    public void setDtMbFinal(String dtMbFinal) {
        this.dtMbFinal = dtMbFinal;
    }
}
