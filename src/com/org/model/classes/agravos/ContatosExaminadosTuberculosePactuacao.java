/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor. jj
 */
package com.org.model.classes.agravos;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.DBFUtil;
import com.org.model.classes.Agravo;
import com.org.model.classes.ColunasDbf;
import com.org.negocio.Configuracao;
import com.org.negocio.Util;
import com.org.util.SinanUtil;
import com.org.view.Master;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;

/**
 *
 * @author geraldo
 */
public class ContatosExaminadosTuberculosePactuacao extends Agravo {

    static String ANO;

    public ContatosExaminadosTuberculosePactuacao(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de Diagnóstico");
        setTipoAgregacao("de Residência");
        init("postgres");
    }

    @Override
    public void init(String tipoBanco) {
        this.setArquivo("TUBENET");
        this.setTextoCompletitude("");
        this.setMultiplicador(100000);
        this.setTipo("");
        this.setTipo("populacao");
        this.setTitulo1("Proporção de contatos examinados de casos novos de tuberculose pulmonar com confirmação laboratorial");
        this.setTituloColuna("Indicador");
        this.setRodape("Indicador: Proporção de contatos examinados de casos novos de tuberculose pulmonar com confirmação laboratorial  \n");
        this.setSqlNumeradorCompletitude("");
        if (!isDBF()) {
            this.setSqlNumeradorMunicipioEspecifico("select count(*) as numerador from dbsinan.tb_notificacao as t1, " + "dbsinan.tb_investiga_aids_crianca as t2 " + "where  t1.nu_notificacao=t2.nu_notificacao and " + "t1.dt_notificacao=t2.dt_notificacao and " + "t1.co_municipio_notificacao=t2.co_municipio_notificacao" + " and nu_idade < 4005 and tp_criterio_definicao not in (900,901) and (t1.dt_diagnostico_sintoma BETWEEN ?  " + "AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.co_municipio_residencia = ?");
            this.setSqlDenominadorMunicipioEspecifico("select nu_pop1a4anos+nu_pop1ano as denominador from dblocalidade.tb_estatistica_ibge where co_uf_municipio_ibge = ? and nu_ano = ?");
            this.setSqlNumeradorEstado("select count(*) as numerador from dbsinan.tb_notificacao as t1, " + "dbsinan.tb_investiga_aids_crianca as t2 " + "where  t1.nu_notificacao=t2.nu_notificacao and " + "t1.dt_notificacao=t2.dt_notificacao and " + "t1.co_municipio_notificacao=t2.co_municipio_notificacao" + " and nu_idade < 4005 and tp_criterio_definicao not in (900,901) and (t1.dt_diagnostico_sintoma BETWEEN ?  " + "AND ?) and " + "t1.co_uf_residencia= ? ");

            this.setSqlDenominandorEstado(this.getSqlDenominadorMunicipioEspecifico());
            this.setSqlNumeradorBeanMunicipios(this.getSqlNumeradorMunicipioEspecifico());
            this.setSqlDenominadorBeanMunicipios(this.getSqlDenominadorMunicipioEspecifico());
        }
    }

    /*
    private void calculaBrasil(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        DBFUtil utilDbf = new DBFUtil();
        String coluna;
        String municipios = (!parametros.get("parNenhum").equals("true")) ? "sim" : "não";
        if (municipios.equals("sim")) {
            municipiosBeans = populaMunicipiosBeans("BR", "");
            coluna = "ID_MN_RESI";
        } else {
            municipiosBeans = populaUfsBeans();
            coluna = "SG_UF";
        }
        //inicia o calculo
        Object[] rowObjects;
        Date dtDiagnostico;
        int completitude = 0;
        DecimalFormat df = new DecimalFormat("0.00");
        int numerador = 0;
        int numeradorEstadual = 0;
        int denominadorEstadual = 0;
        Agravo municipioResidencia;
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");
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
                        municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, coluna));
                        //verifica se tem o parametro de municipio de residencia
                        dtDiagnostico = utilDbf.getDate(rowObjects, "DT_NOTIFIC");
                        if (municipioResidencia != null) {
                            if (isBetweenDates(dtDiagnostico, dataInicio, dataFim)) {
                                numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                numerador++;
                                municipioResidencia.setNumerador(String.valueOf(numerador));
                                numeradorEstadual++;
                            }
                        }
                    }
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setValue((int) percentual);
                    i++;
                    System.out.println(i);
                }

            } catch (DBFException ex) {
                Master.mensagem("Erro:\n" + ex);
            }
        }
        String ano = dataInicio.substring(0, 4);
        setTaxaEstadual("");
        //calcula o percentual da completitude
        setPercentualCompletitude(df.format(Double.parseDouble(String.valueOf(completitude)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100));
        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<Agravo> municipioBean = municipiosBeans.values();
        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoDBF = it.next();
            this.getBeans().add(agravoDBF);
            getBarraStatus().setString("Calculando indicador para: " + agravoDBF.getNomeMunicipio());
        }
        getBarraStatus().setString(null);
        Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
        this.getBeans().add(adicionaBrasil(municipioBean));

        if (!parametros.get("parSgUf").toString().equals("TODAS") && !parametros.get("municipios").toString().equals("sim")) {
            Agravo agravoBrasil = (Agravo) this.getBeans().get(27);
            List arrayT = new ArrayList();
            arrayT.add(agravoBrasil);
            this.setBeans(arrayT);
        }
    }
     */
    private void calculaRegiao(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        String codRegiao = (String) parametros.get("parCodRegiaoSaude");

        DBFUtil utilDbf = new DBFUtil();
        if (codRegional == null) {
            codRegional = "";
        }
        if ((Boolean) parametros.get("parIsRegiao")) {
            municipiosBeans = populaRegiaoBeans(sgUfResidencia, codRegiao);
        } else {
            municipiosBeans = populaRegionalBeans(sgUfResidencia, codRegional);
        }
        //municipiosBeans = populaMunicipiosBeans(sgUfResidencia, codRegional);
        //inicia o calculo
        Object[] rowObjects;
        Date dtDiagnostico;
        Boolean CID_B54 = false;
        Boolean AT_LAMINA = false;
        Boolean RESULT = false;

        String total;
        DecimalFormat df = new DecimalFormat("0.00");
        int numerador = 0;
        int numeradorEstadual = 0;
        int denominadorEstadual = 0;
        Agravo municipioResidencia = null;
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");
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
                    if (utilDbf.getString(rowObjects, "COUFINF") != null) {
                        try {
                            //verifica se existe a referencia do municipio no bean
                            if ((Boolean) parametros.get("parIsRegiao")) {
                                municipioResidencia = municipiosBeans.get(buscaIdRegiaoSaude(utilDbf.getString(rowObjects, "COMUNINF")));
                            } else {
                                municipioResidencia = municipiosBeans.get(buscaIdRegionalSaude(utilDbf.getString(rowObjects, "COMUNINF")));
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(ContatosExaminadosTuberculosePactuacao.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //verifica se tem o parametro de municipio de residencia
                        //Critérios
                        CID_B54 = utilDbf.getString(rowObjects, "ID_AGRAVO").equals("B54");
                        if (utilDbf.getString(rowObjects, "AT_LAMINA") != null) {
                            AT_LAMINA = utilDbf.getString(rowObjects, "AT_LAMINA").equals("1") || utilDbf.getString(rowObjects, "AT_LAMINA").equals("2");
                        }
                        if (utilDbf.getString(rowObjects, "RESULT") != null) {
                            RESULT = utilDbf.getString(rowObjects, "RESULT").equals("2") || utilDbf.getString(rowObjects, "RESULT").equals("3")
                                    || utilDbf.getString(rowObjects, "RESULT").equals("4") || utilDbf.getString(rowObjects, "RESULT").equals("5")
                                    || utilDbf.getString(rowObjects, "RESULT").equals("6") || utilDbf.getString(rowObjects, "RESULT").equals("7")
                                    || utilDbf.getString(rowObjects, "RESULT").equals("8") || utilDbf.getString(rowObjects, "RESULT").equals("9")
                                    || utilDbf.getString(rowObjects, "RESULT").equals("10");
                        }

                        dtDiagnostico = utilDbf.getDate(rowObjects, "DT_NOTIFIC");

                        if (municipioResidencia != null && CID_B54 && AT_LAMINA && RESULT) {
                            // AUTOCTONE = utilDbf.getString(rowObjects, "ID_MN_RESI").equals(utilDbf.getString(rowObjects, "COMUNINF"));
                            if (isBetweenDates(dtDiagnostico, dataInicio, dataFim)) {
                                numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                numerador++;
                                municipioResidencia.setNumerador(String.valueOf(numerador));
                                numeradorEstadual++;
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
        String ano = dataInicio.substring(0, 4);

        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<Agravo> municipioBean = municipiosBeans.values();

        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoDBF = it.next();
            this.getBeans().add(agravoDBF);
            getBarraStatus().setString("Calculando indicador para: " + agravoDBF.getNomeMunicipio());
        }
        getBarraStatus().setString(null);

        parametros.put("numeradorTotal", String.valueOf(numeradorEstadual));
        ComparatorChain chain;
        chain = new ComparatorChain(Arrays.asList(
                new BeanComparator("uf"),
                new BeanComparator("nomeMunicipio")));
        Collections.sort(this.getBeans(), chain);
        //calcular o total
        if ((Boolean) parametros.get("parIsRegiao")) {
            this.getBeans().add(adicionaTotal(municipioBean, codRegiao));
        } else {
            this.getBeans().add(adicionaTotal(municipioBean, codRegional));
        }
    }

    private void calculaMunicipios(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        String codRegiao = (String) parametros.get("parCodRegiaoSaude");

        DBFUtil utilDbf = new DBFUtil();
        if (codRegional == null) {
            codRegional = "";
        }
        if (codRegiao == null) {
            codRegiao = "";
        }

        if ((Boolean) parametros.get("parIsRegiao")) {
            municipiosBeans = populaMunicipiosBeansMAL(sgUfResidencia, codRegiao, parametros.get("parIsRegiao").toString());
        } else {
            municipiosBeans = populaMunicipiosBeansMAL(sgUfResidencia, codRegional, parametros.get("parIsRegiao").toString());
        }
        //municipiosBeans = populaMunicipiosBeans(sgUfResidencia, codRegional);
        //inicia o calculo
        Object[] rowObjects;
        Date dtDiagnostico;
        Boolean CID_A169 = false;
        Boolean TRATAMENTO = false;
        Boolean SITUACAO_ENCERRAMENTO = false;
        Boolean FORMA = false;
        Boolean BACILOSC_E = false;
        Boolean BACILOSC_E2 = false;
        Boolean CULTURA_ES = false;
        Boolean TESTE_MOLEC = false;

        String total;
        DecimalFormat df = new DecimalFormat("0.00");
        int numerador = 0;
        int denominador;
        int numeradorEstadual = 0;
        int denominadorEstadual = 0;
        Agravo municipioResidencia;
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");
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
                    if (utilDbf.getString(rowObjects, "SG_UF") != null) {
                        //verifica se existe a referencia do municipio no bean
                        municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, "ID_MN_RESI"));
                        //verifica se tem o parametro de municipio de residencia
                        //Critérios
                        dtDiagnostico = utilDbf.getDate(rowObjects, "DT_DIAG");

                        if (utilDbf.getString(rowObjects, "ID_AGRAVO") != null) {
                            CID_A169 = utilDbf.getString(rowObjects, "ID_AGRAVO").equals("A169");
                        }
                        if (utilDbf.getString(rowObjects, "TRATAMENTO") != null) {
                            TRATAMENTO = utilDbf.getString(rowObjects, "TRATAMENTO").equals("1")
                                    || utilDbf.getString(rowObjects, "TRATAMENTO").equals("4")
                                    || utilDbf.getString(rowObjects, "TRATAMENTO").equals("6");
                        }
                        if (utilDbf.getString(rowObjects, "SITUA_ENCE") != null) {
                            SITUACAO_ENCERRAMENTO = !utilDbf.getString(rowObjects, "SITUA_ENCE").equals("6");
                        }else{
                           SITUACAO_ENCERRAMENTO = true; 
                        }
                        if (utilDbf.getString(rowObjects, "FORMA") != null) {
                            FORMA = utilDbf.getString(rowObjects, "FORMA").equals("1")
                                    || utilDbf.getString(rowObjects, "FORMA").equals("3");
                        }
                        if (utilDbf.getString(rowObjects, "BACILOSC_E") != null) {
                            BACILOSC_E = utilDbf.getString(rowObjects, "BACILOSC_E").equals("1");
                        }
                        if (utilDbf.getString(rowObjects, "BACILOS_E2") != null) {
                            BACILOSC_E2 = utilDbf.getString(rowObjects, "BACILOS_E2").equals("1");
                        }
                        if (utilDbf.getString(rowObjects, "CULTURA_ES") != null) {
                            CULTURA_ES = utilDbf.getString(rowObjects, "CULTURA_ES").equals("1");
                        }
                        if (utilDbf.getString(rowObjects, "TEST_MOLEC") != null) {
                            TESTE_MOLEC = utilDbf.getString(rowObjects, "TEST_MOLEC").equals("1")
                                    || utilDbf.getString(rowObjects, "TEST_MOLEC").equals("2");
                        }

                        if (municipioResidencia != null){
                           if  ( municipioResidencia.getCodMunicipio().equals("310160")){
                               System.out.println("e ai.."); 
                           }
                        }
                        
                        if (municipioResidencia != null && CID_A169 && TRATAMENTO && SITUACAO_ENCERRAMENTO && FORMA && (BACILOSC_E || BACILOSC_E2 || CULTURA_ES || TESTE_MOLEC)) {
                            //AUTOCTONE = utilDbf.getString(rowObjects, "ID_MN_RESI").equals(utilDbf.getString(rowObjects, "COMUNINF"));
                                
                            if (isBetweenDates(dtDiagnostico, dataInicio, dataFim)) {
                                if (utilDbf.getString(rowObjects, "NU_CONTATO") != null) {
                                    numerador = Integer.parseInt(municipioResidencia.getNumerador() ); 
                                    numerador += (utilDbf.getInt(rowObjects, "NU_CONTATO"));
                                    municipioResidencia.setNumerador(String.valueOf(numerador));
                                    municipioResidencia.setNumeradorInt(numerador);
                                    numeradorEstadual += numerador;
                                }
                                if (utilDbf.getString(rowObjects, "NU_COMU_EX") != null) {
                                    denominador = Integer.parseInt(municipioResidencia.getDenominador()); 
                                    denominador += utilDbf.getInt(rowObjects, "NU_COMU_EX");
                                    municipioResidencia.setDenominador(String.valueOf(denominador));
                                    municipioResidencia.setDenominadorInt(denominador);
                                    denominadorEstadual += denominador;
                                }
                                
                            }
                        }
                    }
                    CID_A169 = false;
                    TRATAMENTO = false;
                    SITUACAO_ENCERRAMENTO = false;
                    FORMA = false;
                    BACILOSC_E = false;
                    BACILOSC_E2 = false;
                    CULTURA_ES = false;
                    TESTE_MOLEC = false;

                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setValue((int) percentual);
                    i++;
                }

            } catch (DBFException ex) {
                Master.mensagem("Erro:\n" + ex);
            }
        }
        String ano = dataInicio.substring(0, 4);

        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<Agravo> municipioBean = municipiosBeans.values();

        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoDBF = it.next();
            this.getBeans().add(agravoDBF);
            getBarraStatus().setString("Calculando indicador para: " + agravoDBF.getNomeMunicipio());
        }
        getBarraStatus().setString(null);
        ComparatorChain chain;
        if ((Boolean) parametros.get("parIsRegiao")) {
            chain = new ComparatorChain(Arrays.asList(
                    new BeanComparator("uf"),
                    new BeanComparator("regiaoSaude"),
                    new BeanComparator("nomeMunicipio")));
        } else {
            chain = new ComparatorChain(Arrays.asList(
                    new BeanComparator("uf"),
                    new BeanComparator("regional"),
                    new BeanComparator("nomeMunicipio")));
        }
        parametros.put("numeradorTotal", numeradorEstadual);
        parametros.put("denominadorTotal", denominadorEstadual);

        Collections.sort(this.getBeans(), chain);

        //calcular o total
        if ((Boolean) parametros.get("parIsRegiao")) {
            this.getBeans().add(adicionaTotal(municipioBean, codRegiao));
        } else {
            this.getBeans().add(adicionaTotal(municipioBean, codRegional));
        }
    }

    /*
    public void calculaUF(DBFReader reader, Map parametros) {

        // String municipios = (String) parametros.get("municipios");
        Boolean CID_B54 = false;
        Boolean AT_LAMINA = false;
        Boolean RESULT = false;
        Boolean AUTOCTONE = false;

        String municipios = (!parametros.get("parNenhum").equals("true")) ? "sim" : "não";
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
                    //calculaBrasil(reader, parametros);
                } else if (!parametros.get("parUf").equals("Todos")) {
                    calculaRegiao(reader, parametros);
                } else {

                    Object[] rowObjects;
                    DBFUtil utilDbf = new DBFUtil();
                    Date dtDiagnostico;
                    String criterio;
                    int idade;
                    int completitude = 0;
                    String total;
                    DecimalFormat df = new DecimalFormat("0.00");
                    int denominadorEstadual = 0;
                    int numeradorEstadual = 0;
                    int denominadorEspecifico = 0;
                    int numeradorEspecifico = 0;
                    String ufResidencia = (String) parametros.get("parUf");
                    String municipioResidencia = (String) parametros.get("parMunicipio");
                    if (municipioResidencia == null) {
                        municipioResidencia = "";
                    }
                    String dataInicio = (String) parametros.get("parDataInicio");
                    String dataFim = (String) parametros.get("parDataFim");
                    //loop para ler os arquivos selecionados
                    String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
                    for (int k = 0; k < arquivos.length; k++) {
                        int i = 1;
                        reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                        utilDbf.mapearPosicoes(reader);
                        double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                        while ((rowObjects = reader.nextRecord()) != null) {
                            //cálculo da taxa estadual
                            //verifica a uf de residencia
                            if (utilDbf.getString(rowObjects, "SG_UF") != null) {
                                if (utilDbf.getString(rowObjects, "SG_UF").equals(ufResidencia)) {
                                    //verifica se tem o parametro de municipio de residencia
                                    //Critérios
                                    CID_B54 = utilDbf.getString(rowObjects, "ID_AGRAVO").equals("B54");
                                    if (utilDbf.getString(rowObjects, "AT_LAMINA") != null) {
                                        AT_LAMINA = utilDbf.getString(rowObjects, "AT_LAMINA").equals("1") || utilDbf.getString(rowObjects, "AT_LAMINA").equals("2");
                                    }
                                    if (utilDbf.getString(rowObjects, "RESULT") != null) {
                                        RESULT = utilDbf.getString(rowObjects, "RESULT").equals("2") || utilDbf.getString(rowObjects, "RESULT").equals("3")
                                                || utilDbf.getString(rowObjects, "RESULT").equals("4") || utilDbf.getString(rowObjects, "RESULT").equals("5")
                                                || utilDbf.getString(rowObjects, "RESULT").equals("6") || utilDbf.getString(rowObjects, "RESULT").equals("7");
                                    }

                                    dtDiagnostico = utilDbf.getDate(rowObjects, "DT_NOTIFIC");

                                    if (municipioResidencia != null && CID_B54 && AT_LAMINA && RESULT) {
                                        AUTOCTONE = utilDbf.getString(rowObjects, "ID_MN_RESI").equals(utilDbf.getString(rowObjects, "COMUNINF"));
                                        if (isBetweenDates(dtDiagnostico, dataInicio, dataFim) && AUTOCTONE) {
                                            numeradorEstadual++;
                                        }
                                    }

                                }
                            }
                            float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                            getBarraStatus().setValue((int) percentual);
                            i++;
                        }
                    }
                    //busca o denonimador que é a pop por estado

                    String ano = dataInicio.substring(0, 4);
                    // denominadorEstadual = getPopulacao(ufResidencia, 2, ano);
                    if (municipioResidencia.length() == 0) {
                        denominadorEspecifico = denominadorEstadual;
                    } else {
                        // denominadorEspecifico = getPopulacao(municipioResidencia, 2, ano);
                    }

                    total = df.format(Double.parseDouble(String.valueOf(numeradorEstadual)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100000);

                    setTaxaEstadual(total + " (Numerador:" + String.valueOf(numeradorEstadual) + " / Denominador: " + String.valueOf(denominadorEstadual) + ")");
                    //calcula o percentual da completitude
                    setPercentualCompletitude(df.format(Double.parseDouble(String.valueOf(completitude)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100000));
                    //começa o preencher o bean para estado ou 1 municipio
                    Agravo d1 = new Agravo();
                    d1.setCodMunicipio((String) parametros.get("parMunicipio"));//falta aqui
                    if (municipioResidencia.equals("")) {
                        d1.setNomeMunicipio((String) parametros.get("parSgUf"));
                        d1.setCodMunicipio(ufResidencia);
                    } else {
                        d1.setNomeMunicipio((String) parametros.get("parNomeMunicipio"));
                        d1.setCodMunicipio(municipioResidencia);
                    }
                    if (!String.valueOf(denominadorEspecifico).equals("0.0")) {
                        d1.setNumerador(String.valueOf(NumberFormat.getNumberInstance().format(Double.parseDouble(String.valueOf(numeradorEspecifico)))));
                        d1.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(Double.parseDouble(String.valueOf(denominadorEspecifico)))));
                        total = df.format(Double.parseDouble(String.valueOf(numeradorEspecifico)) / Double.parseDouble(String.valueOf(denominadorEspecifico)) * 100000);
                        d1.setTaxa(total);
                    } else {
                        d1.setNumerador("0");
                        d1.setDenominador("0");
                        d1.setTaxa("0.00");
                    }
                    this.setBeans(new ArrayList());
                    this.getBeans().add(d1);
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
     */
    @Override
    public void calcula(DBFReader reader, Map parametros) {

        Boolean municipios = ((Boolean) parametros.get("parNenhum")) ? false : true;
        String brasil = (String) parametros.get("parUf");
        if (municipios) {
            try {
                calculaMunicipios(reader, parametros);
            } catch (ParseException ex) {
                System.out.println(ex);
            }
        } else {
            try {
                if (brasil.equals("brasil")) {
                    //calculaBrasil(reader, parametros);
                } else if (!parametros.get("parUf").equals("Todos")) {
                    calculaRegiao(reader, parametros);
                }
            } catch (NumberFormatException ex) {
                System.out.println(ex);
            } catch (ParseException ex) {
                System.out.println(ex);
            }
        }
    }

    @Override
    public Map getParametros() {
        Map parametros = new HashMap();
        parametros.put("parDataInicio", Util.formataData(this.getDtInicioAvaliacao()));
        parametros.put("parDataFim", Util.formataData(this.getDtFimAvaliacao()));
        parametros.put("parPeriodo", "de " + this.getDtInicioAvaliacao() + " a " + this.getDtFimAvaliacao());
        parametros.put("parTituloColuna", this.getTituloColuna());
        parametros.put("parFator", String.valueOf(this.getMultiplicador()));
        parametros.put("parAno", Util.getAno(this.getDtFimAvaliacao()));
        parametros.put("parRodape", this.getRodape());
        parametros.put("parConfig", "");
        parametros.put("parTitulo1", "Número de casos autóctones de malária.");
        //pegar o ano para exportar para dbf
        ANO = "";
        if (Util.getAno(this.getDtFimAvaliacao()).equals(Util.getAno(this.getDtInicioAvaliacao()))) {
            ANO = Util.getAno(this.getDtFimAvaliacao());
        }
        //informa datas selecionadas
        setDataInicio(this.getDtInicioAvaliacao());
        setDataFim(this.getDtFimAvaliacao());
        return parametros;
    }

    @Override
    public String[] getOrdemColunas() {
        return new String[]{"COUUFINF", "ID_RG_INF", "COMUNINF", "COD_CIR", "NOME_CIR", "ID_LOCRES", "NUM_MALA", "DS_LOCRES", "ANO_DIAG", "DT_DIAGIN", "DT_DIAGFI", "ORIGEM"};
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("COUUFINF", new ColunasDbf(30));
        hashColunas.put("ID_RG_INF", new ColunasDbf(30));
        hashColunas.put("COMUNINF", new ColunasDbf(30));
        hashColunas.put("COD_CIR", new ColunasDbf(30));
        hashColunas.put("NOME_CIR", new ColunasDbf(30));
        hashColunas.put("ID_LOCRES", new ColunasDbf(30));
        hashColunas.put("NUM_MALA", new ColunasDbf(30));
        hashColunas.put("DS_LOCRES", new ColunasDbf(30));
        hashColunas.put("ANO_DIAG", new ColunasDbf(30));
        hashColunas.put("DT_DIAGIN", new ColunasDbf(30));
        hashColunas.put("DT_DIAGFI", new ColunasDbf(30));
        hashColunas.put("ORIGEM", new ColunasDbf(30));

        this.setColunas(hashColunas);
        return hashColunas;
    }

    @Override
    public DBFWriter getLinhas(HashMap<String, ColunasDbf> colunas, List bean, DBFWriter writer) throws DBFException, IOException {
        for (int i = 0; i < bean.size(); i++) {
            Object rowData[] = new Object[colunas.size()];
            Agravo agravo = (Agravo) bean.get(i);
            if (agravo.getNomeMunicipio().equals("BRASIL") || agravo.getNomeMunicipio().equals("TOTAL")) {
                rowData[5] = null;
                rowData[7] = null;
            } else {
                rowData[5] = null;
                rowData[7] = null;
                rowData[0] = agravo.getCodMunicipio().substring(0, 2);
                rowData[2] = agravo.getCodMunicipio();
                if (!agravo.getCodRegional().isEmpty()) {
                    rowData[3] = agravo.getCodRegional();
                    rowData[4] = agravo.getRegional();
                } else {
                    rowData[3] = agravo.getCodRegiaoSaude();
                    rowData[4] = agravo.getRegiaoSaude();
                }
                rowData[8] = String.valueOf(preencheAno(getDataInicio(), getDataFim()));
                rowData[9] = getDataInicio();
                rowData[10] = getDataFim();

            }
            rowData[1] = null; //agravo.getRegional();
            rowData[6] = agravo.getNumerador();
            rowData[11] = "MALAR-SINANNET";
            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/ContatosExaminadosTuberculosePacuacao.jasper";
    }
}
