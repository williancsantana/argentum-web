/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor. gg
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
import com.org.util.SinanDateUtil;
import com.org.util.SinanUtil;
import com.org.view.Master;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
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
public class SemanaEpidemiologicaPactuacao extends Agravo {

    static String ANO;
    Agravo municipioResidencia;
    HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();

    HashMap<String, ArrayList<String>> semanas = new HashMap<String, ArrayList<String>>();
    DBFUtil utilDbf = new DBFUtil();

    public SemanaEpidemiologicaPactuacao(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de Diagnóstico");
        setTipoAgregacao("de Residência");
        init("postgres");
    }

    @Override

    public void init(String tipoBanco) {
        this.setArquivo("NINDINET");
        this.setTextoCompletitude("");
        this.setMultiplicador(100000);
        this.setTipo("");
        this.setTipo("populacao");
        this.setTitulo1("Número de semanas epidemiológicas com informação.");
        this.setTituloColuna("Indicador");
        this.setRodape("Indicador: Número de semanas epidemiológicas com informação.  \n");
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

    private void calculaIndicador(Object[] rowObjects, String anoAvaliacao, Integer semanaIni, Integer semanaFim) throws ParseException {

        // Date dtDiagnostico;
        String semEpidemilogica;
        String anoEdpid;
        Boolean semanaValida = false;
        int denominador = 0;

        if (utilDbf.getString(rowObjects, "SEM_NOT") != null) {
            semEpidemilogica = utilDbf.getString(rowObjects, "SEM_NOT");
        } else {
            semEpidemilogica = "";
        }
        if (semEpidemilogica != null && semEpidemilogica.length() >= 4) {
            anoEdpid = semEpidemilogica.substring(0, 4);
        } else {
            anoEdpid = "0";
        }

        if (semEpidemilogica != null && semEpidemilogica.length() >= 4) {
            semanaValida = (Integer.valueOf(semEpidemilogica) >= semanaIni) && (Integer.valueOf(semEpidemilogica) <= semanaFim);
        }

        ArrayList<String> WeekAtual = new ArrayList<String>();

        if (municipioResidencia != null && anoEdpid.equals(anoAvaliacao) && semanaValida) {

            if (semanas.get(municipioResidencia.getCodMunicipio()) != null) {
                WeekAtual = semanas.get(municipioResidencia.getCodMunicipio());
                if (!WeekAtual.contains(semEpidemilogica)) {
                    denominador = Integer.parseInt(municipioResidencia.getDenominador());
                    denominador++;
                    municipioResidencia.setDenominador(String.valueOf(denominador));
                    municipioResidencia.setDenominadorInt(denominador);
                    semanas.get(municipioResidencia.getCodMunicipio()).add(semEpidemilogica);
                }
            } else {
                WeekAtual.add(semEpidemilogica);
                semanas.put(municipioResidencia.getCodMunicipio(), WeekAtual);
                denominador = Integer.parseInt(municipioResidencia.getDenominador());
                denominador++;
                municipioResidencia.setDenominador(String.valueOf(denominador));
                municipioResidencia.setDenominadorInt(denominador);
                semanas.get(municipioResidencia.getCodMunicipio()).add(semEpidemilogica);
            }

        }

    }

    private void calculaRegiao(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado

        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        String codRegiao = (String) parametros.get("parCodRegiaoSaude");
        HashMap<String, Agravo> regiaoBeans = new HashMap<String, Agravo>();
        Agravo regiaoResidencia;
        parametros.put("numeradorTotal", 0);
        parametros.put("denominadorTotal", 0);
        if (codRegional == null) {
            codRegional = "";
        }
        if (codRegiao == null) {
            codRegiao = "";
        }

        String idMunicipio;
        if (parametros.get("parMunicipio") != null) {
            idMunicipio = (String) parametros.get("parMunicipio");
        } else {
            idMunicipio = "TODOS";
        }

        if ((Boolean) parametros.get("parIsRegiao")) {
            municipiosBeans = populaRegiaoBeans(sgUfResidencia, codRegiao);
            regiaoBeans = populaMunicipiosBeansMAL(sgUfResidencia, codRegiao, idMunicipio, parametros.get("parIsRegiao").toString());
        } else {
            municipiosBeans = populaRegionalBeans(sgUfResidencia, codRegional);
            regiaoBeans = populaMunicipiosBeansMAL(sgUfResidencia, codRegional, idMunicipio, parametros.get("parIsRegiao").toString());
        }
        //municipiosBeans = populaMunicipiosBeans(sgUfResidencia, codRegional);
        //inicia o calculo
        Object[] rowObjects;

        String total;
        DecimalFormat df = new DecimalFormat("0.00");
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");
        //loop para ler os arquivos selecionados
        String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
        int semanaIni = 0;
        int semanaFim = 0;
        String anoAvaliacao = (String) parametros.get("parAnoAvaliacao");
        setAnoAvaliado(anoAvaliacao);

        if (Integer.valueOf(parametros.get("parSemanaFinal").toString()) < 10) {
            if (parametros.get("parSemanaFinal").toString().length() < 2) {
                semanaFim = Integer.valueOf(anoAvaliacao + "0" + parametros.get("parSemanaFinal").toString());
            } else {
                semanaFim = Integer.valueOf(anoAvaliacao + parametros.get("parSemanaFinal").toString());
            }

        } else {
            semanaFim = Integer.valueOf(anoAvaliacao + parametros.get("parSemanaFinal").toString());
        }

        if (Integer.valueOf(parametros.get("parSemanaInicial").toString()) < 10) {
            if (parametros.get("parSemanaInicial").toString().length() < 2) {
                semanaIni = Integer.valueOf(anoAvaliacao + "0" + parametros.get("parSemanaInicial").toString());
            } else {
                semanaIni = Integer.valueOf(anoAvaliacao + parametros.get("parSemanaInicial").toString());
            }

        } else {
            semanaIni = Integer.valueOf(anoAvaliacao + parametros.get("parSemanaInicial").toString());
        }
        int totalSemanas = (Integer.valueOf(parametros.get("parSemanaFinal").toString()) - Integer.valueOf(parametros.get("parSemanaInicial").toString())) + 1;

        for (int k = 0; k < arquivos.length; k++) {
            int i = 1;
            try {
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                utilDbf.mapearPosicoes(reader);
                double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                while ((rowObjects = reader.nextRecord()) != null) {

                    if ((Boolean) parametros.get("parIsRegiao")) {
                        regiaoResidencia = regiaoBeans.get(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                        if (regiaoResidencia != null) {
                            municipioResidencia = municipiosBeans.get(regiaoResidencia.getCodRegiaoSaude());
                        }
                    } else {
                        regiaoResidencia = regiaoBeans.get(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                        if (regiaoResidencia != null) {
                            municipioResidencia = municipiosBeans.get(regiaoResidencia.getCodRegional());
                        }
                    }
                    calculaIndicador(rowObjects, anoAvaliacao, semanaIni, semanaFim);

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
            agravoDBF.setNumerador(String.valueOf(totalSemanas));
            agravoDBF.setNumeradorInt(totalSemanas);
            getBarraStatus().setString("Calculando indicador para: " + agravoDBF.getNomeMunicipio());
        }
        getBarraStatus().setString(null);

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

        // HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        String codRegiao = (String) parametros.get("parCodRegiaoSaude");
        String idMunicipio;
        if (parametros.get("parMunicipio") != null) {
            idMunicipio = (String) parametros.get("parMunicipio");
        } else {
            idMunicipio = "TODOS";
        }
        if (codRegional == null) {
            codRegional = "";
        }
        if (codRegiao == null) {
            codRegiao = "";
        }

        if ((Boolean) parametros.get("parIsRegiao")) {
            municipiosBeans = populaMunicipiosBeansMAL(sgUfResidencia, codRegiao, idMunicipio, parametros.get("parIsRegiao").toString());
        } else {
            municipiosBeans = populaMunicipiosBeansMAL(sgUfResidencia, codRegional, idMunicipio, parametros.get("parIsRegiao").toString());
        }
        //municipiosBeans = populaMunicipiosBeans(sgUfResidencia, codRegional);
        //inicia o calculo
        Object[] rowObjects;

        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");
        parametros.put("numeradorTotal", 0);
        parametros.put("denominadorTotal", 0);

        int semanaIni = 0;
        int semanaFim = 0;
        String anoAvaliacao = (String) parametros.get("parAnoAvaliacao");
        setAnoAvaliado(anoAvaliacao);

        if (Integer.valueOf(parametros.get("parSemanaFinal").toString()) < 10) {
            if (parametros.get("parSemanaFinal").toString().length() < 2) {
                semanaFim = Integer.valueOf(anoAvaliacao + "0" + parametros.get("parSemanaFinal").toString());
            } else {
                semanaFim = Integer.valueOf(anoAvaliacao + parametros.get("parSemanaFinal").toString());
            }

        } else {
            semanaFim = Integer.valueOf(anoAvaliacao + parametros.get("parSemanaFinal").toString());
        }

        if (Integer.valueOf(parametros.get("parSemanaInicial").toString()) < 10) {
            if (parametros.get("parSemanaInicial").toString().length() < 2) {
                semanaIni = Integer.valueOf(anoAvaliacao + "0" + parametros.get("parSemanaInicial").toString());
            } else {
                semanaIni = Integer.valueOf(anoAvaliacao + parametros.get("parSemanaInicial").toString());
            }
        } else {
            semanaIni = Integer.valueOf(anoAvaliacao + parametros.get("parSemanaInicial").toString());
        }
        int totalSemanas = (Integer.valueOf(parametros.get("parSemanaFinal").toString()) - Integer.valueOf(parametros.get("parSemanaInicial").toString())) + 1;

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
                    municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                    calculaIndicador(rowObjects, anoAvaliacao, semanaIni, semanaFim);
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setString("Preparando arquivos... ");
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
            agravoDBF.setNumerador(String.valueOf(totalSemanas));
            agravoDBF.setNumeradorInt(totalSemanas);
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
        Collections.sort(this.getBeans(), chain);

        //calcular o total
        if ((Boolean) parametros.get("parIsRegiao")) {
            this.getBeans().add(adicionaTotal(municipioBean, codRegiao));
        } else {
            this.getBeans().add(adicionaTotal(municipioBean, codRegional));
        }
    }

    private void calculaSomenteMunicipios(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado

        // HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String idMunicipio;
        if (parametros.get("parMunicipio") != null) {
            idMunicipio = (String) parametros.get("parMunicipio");
        } else {
            idMunicipio = "TODOS";
        }

        municipiosBeans = populaMunicipiosBeansMAL(sgUfResidencia, "", idMunicipio, "false");
        //municipiosBeans = populaMunicipiosBeans(sgUfResidencia, codRegional);
        //inicia o calculo
        Object[] rowObjects;

        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");
        parametros.put("numeradorTotal", 0);
        parametros.put("denominadorTotal", 0);

        //loop para ler os arquivos selecionados
        String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");

        int semanaIni = 0;
        int semanaFim = 0;
        String anoAvaliacao = (String) parametros.get("parAnoAvaliacao");
        setAnoAvaliado(anoAvaliacao);

        if (Integer.valueOf(parametros.get("parSemanaFinal").toString()) < 10) {
            
            if (parametros.get("parSemanaFinal").toString().length() < 2) {
                semanaFim = Integer.valueOf(anoAvaliacao + "0" + parametros.get("parSemanaFinal").toString());
            } else {
                semanaFim = Integer.valueOf(anoAvaliacao + parametros.get("parSemanaFinal").toString());
            }

        } else {
            semanaFim = Integer.valueOf(anoAvaliacao + parametros.get("parSemanaFinal").toString());
        }

        if (Integer.valueOf(parametros.get("parSemanaInicial").toString()) < 10) {
            if (parametros.get("parSemanaInicial").toString().length() < 2) {
                semanaIni = Integer.valueOf(anoAvaliacao + "0" + parametros.get("parSemanaInicial").toString());
            } else {
                semanaIni = Integer.valueOf(anoAvaliacao + parametros.get("parSemanaInicial").toString());
            }

        } else {
            semanaIni = Integer.valueOf(anoAvaliacao + parametros.get("parSemanaInicial").toString());
        }
        int totalSemanas = (Integer.valueOf(parametros.get("parSemanaFinal").toString()) - Integer.valueOf(parametros.get("parSemanaInicial").toString())) + 1;

        for (int k = 0; k < arquivos.length; k++) {
            int i = 1;
            try {
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                utilDbf.mapearPosicoes(reader);
                double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                while ((rowObjects = reader.nextRecord()) != null) {
                    //cálculo da taxa estadual
                    //verifica a uf de residencia
                    municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                    if (municipioResidencia != null) {
                        calculaIndicador(rowObjects, anoAvaliacao, semanaIni, semanaFim);
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
            agravoDBF.setNumerador(String.valueOf(totalSemanas));
            agravoDBF.setNumeradorInt(totalSemanas);

            this.getBeans().add(agravoDBF);
            getBarraStatus().setString("Calculando indicador para: " + agravoDBF.getNomeMunicipio());
        }
        getBarraStatus().setString(null);
        ComparatorChain chain;
        chain = new ComparatorChain(Arrays.asList(
                new BeanComparator("uf"),
                new BeanComparator("nomeMunicipio")));
        Collections.sort(this.getBeans(), chain);
        //calcular o total
        this.getBeans().add(adicionaTotal(municipioBean, ""));
    }

    @Override
    public void calcula(DBFReader reader, Map parametros) {

        Boolean municipios = (Boolean) parametros.get("parNenhum") ? false : true;
        Boolean somenteMunicipios = parametros.get("parDesagregacao").equals("Somente municípios");

        try {
            if (somenteMunicipios) {
                calculaSomenteMunicipios(reader, parametros);
            } else if (municipios) {
                calculaMunicipios(reader, parametros);
            } else {
                calculaRegiao(reader, parametros);
            }
        } catch (ParseException ex) {
            System.out.println(ex);

        }
    }

    @Override
    public Map getParametros() {
        Map parametros = new HashMap();
        parametros.put("parDataInicio", Util.formataData(this.getDtInicioAvaliacao()));
        parametros.put("parDataFim", Util.formataData(this.getDtFimAvaliacao()));

        parametros.put("parDataInicioCoortePB", Util.formataData(SinanDateUtil.subtrairAno(this.getDtInicioAvaliacao(), -1)));
        parametros.put("parDataFimCoortePB", Util.formataData(SinanDateUtil.subtrairAno(this.getDtFimAvaliacao(), -1)));
        parametros.put("parPeriodoCoortePB", SinanDateUtil.subtrairAno(this.getDtInicioAvaliacao(), -1) + " a " + SinanDateUtil.subtrairAno(this.getDtFimAvaliacao(), -1) + " (PB)");

        parametros.put("parDataInicioCoorteMB", Util.formataData(SinanDateUtil.subtrairAno(this.getDtInicioAvaliacao(), -2)));
        parametros.put("parDataFimCoorteMB", Util.formataData(SinanDateUtil.subtrairAno(this.getDtFimAvaliacao(), -2)));
        parametros.put("parPeriodoCoorteMB", SinanDateUtil.subtrairAno(this.getDtInicioAvaliacao(), -2) + " a " + SinanDateUtil.subtrairAno(this.getDtFimAvaliacao(), -2) + " (MB)");

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
        //return new String[]{"COUUFINF", "ID_LOCRES", "DS_LOCRES", "COD_CIR", "NOME_CIR", "D_HANSREG", "N_HANSEXAM", "P_HANSEXAM", "ANO_DIAG", "DT_DIAGIN", "DT_DIAGFI", "ORIGEM"};
        return new String[]{"COUFNOT", "ID_LOCNOT", "DS_LOCNOT", "COD_CIR", "NOME_CIR", "DENOMINAD", "NUMERADOR", "RESULTADO", "ANO_NOTI", "ORIGEM"};
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("COUFNOT", new ColunasDbf(30));
        hashColunas.put("ID_LOCNOT", new ColunasDbf(30));
        hashColunas.put("DS_LOCNOT", new ColunasDbf(30));
        hashColunas.put("COD_CIR", new ColunasDbf(30));
        hashColunas.put("NOME_CIR", new ColunasDbf(30));
        hashColunas.put("DENOMINAD", new ColunasDbf(30));
        hashColunas.put("NUMERADOR", new ColunasDbf(30));
        hashColunas.put("RESULTADO", new ColunasDbf(30));
        hashColunas.put("ANO_NOTI", new ColunasDbf(30));
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
                rowData[0] = null;
                rowData[1] = null;
                rowData[3] = null;
                rowData[4] = null;

            } else {
                rowData[0] = agravo.getCodMunicipio().substring(0, 2);
                rowData[1] = agravo.getCodMunicipio();

                if (agravo.getRegional() != null && agravo.getRegional() != null) {
                    if (!agravo.getRegional().isEmpty()) {
                        rowData[3] = agravo.getCodRegional();
                        rowData[4] = agravo.getRegional();
                    } else if (!agravo.getRegiaoSaude().isEmpty()) {
                        rowData[3] = agravo.getCodRegiaoSaude();
                        rowData[4] = agravo.getRegiaoSaude();
                    }
                }
            }
            rowData[2] = agravo.getNomeMunicipio();
            rowData[5] = agravo.getNumerador();
            rowData[6] = agravo.getDenominador();
            if (Integer.valueOf(agravo.getNumerador()) <= 0) {
                rowData[7] = "0.0";
            } else {
                Double percentual = (Double.valueOf(agravo.getDenominador()) / Double.valueOf(agravo.getNumerador())) * 100;
                rowData[7] = String.format("%.1f", percentual);
            }

            rowData[8] =  getAnoAvaliado() ; 
            rowData[9] = "SINANNET";
            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/SemanaEpidemiologicaPactuacao.jasper";
    }
}
