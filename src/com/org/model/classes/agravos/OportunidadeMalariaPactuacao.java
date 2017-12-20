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
public class OportunidadeMalariaPactuacao extends Agravo {

    static String ANO;
    Agravo municipioResidencia;
    HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
    DBFUtil utilDbf = new DBFUtil();

    public OportunidadeMalariaPactuacao(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de Diagnóstico");
        setTipoAgregacao("de Residência");
        init("postgres");
    }

    @Override
    public void init(String tipoBanco) {
        this.setArquivo("MALANET");
        this.setTextoCompletitude("");
        this.setMultiplicador(100000);
        this.setTipo("");
        this.setTipo("populacao");
        this.setTitulo1("Proporçao de Casos de Malária que iniciaram tratamento em tempo oportuno");
        this.setTituloColuna("Indicador");
        this.setRodape("Indicador: Proporçao de Casos de Malária que iniciaram tratamento em tempo oportuno  \n");
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

    private void calculaIndicador(Object[] rowObjects, Map parametros) throws ParseException {

        Date dtDiagnostico;
        Boolean CID_B54 = false;
        Boolean AT_LAMINA = false;
        Boolean AT_SINTOMA = false;
        Boolean RESULT = false;
        Boolean Data = false;
        Boolean MUNIC_NOTIFICACAO = false;

        String total;
        DecimalFormat df = new DecimalFormat("0.00");
        int numerador = 0;
        int denominador = 0;
        int numeradorEstadual = 0;
        int denominadorEstadual = 0;

        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");

        java.sql.Date dtPrimeirosSintomas = null;
        java.sql.Date dtTratamento = null;

        //verifica se tem o parametro de municipio de residencia
        //Critérios
        CID_B54 = utilDbf.getString(rowObjects, "ID_AGRAVO").equals("B54");
        if (utilDbf.getString(rowObjects, "AT_LAMINA") != null) {
            AT_LAMINA = utilDbf.getString(rowObjects, "AT_LAMINA").equals("1") || utilDbf.getString(rowObjects, "AT_LAMINA").equals("2");
        }
        if (utilDbf.getString(rowObjects, "AT_SINTOMA") != null) {
            AT_SINTOMA = utilDbf.getString(rowObjects, "AT_SINTOMA").equals("1");
        }

        if (utilDbf.getString(rowObjects, "RESULT") != null) {
            RESULT = utilDbf.getString(rowObjects, "RESULT").equals("2") || utilDbf.getString(rowObjects, "RESULT").equals("3")
                    || utilDbf.getString(rowObjects, "RESULT").equals("4") || utilDbf.getString(rowObjects, "RESULT").equals("5")
                    || utilDbf.getString(rowObjects, "RESULT").equals("6") || utilDbf.getString(rowObjects, "RESULT").equals("7")
                    || utilDbf.getString(rowObjects, "RESULT").equals("8") || utilDbf.getString(rowObjects, "RESULT").equals("9")
                    || utilDbf.getString(rowObjects, "RESULT").equals("10");
        }

        dtDiagnostico = utilDbf.getDate(rowObjects, "DT_NOTIFIC");

        if (municipioResidencia != null && CID_B54 && AT_LAMINA && RESULT && AT_SINTOMA) {
            MUNIC_NOTIFICACAO = utilDbf.getString(rowObjects, "ID_MUNICIP").equals(utilDbf.getString(rowObjects, "COMUNINF"));

            if (isBetweenDates(dtDiagnostico, dataInicio, dataFim)) {
                numerador = Integer.parseInt(municipioResidencia.getNumerador());
                numerador++;
                municipioResidencia.setNumerador(String.valueOf(numerador));
                municipioResidencia.setNumeradorInt(numerador);
                numeradorEstadual = (Integer) parametros.get("numeradorTotal");
                numeradorEstadual++;
                parametros.put("numeradorTotal", numeradorEstadual);

                if (utilDbf.getDate(rowObjects, "DT_SIN_PRI") != null && utilDbf.getDate(rowObjects, "DTRATA") != null) {
                    dtPrimeirosSintomas = utilDbf.getDate(rowObjects, "DT_SIN_PRI");
                    dtTratamento = utilDbf.getDate(rowObjects, "DTRATA");

                    if (MUNIC_NOTIFICACAO) {
                        if (dataDiff(dtPrimeirosSintomas, dtTratamento) <= 2) {
                            denominador = Integer.parseInt(municipioResidencia.getDenominador());
                            denominador++;
                            municipioResidencia.setDenominador(String.valueOf(denominador));
                            municipioResidencia.setDenominadorInt(denominador);
                            denominadorEstadual = (Integer) parametros.get("denominadorTotal");
                            denominadorEstadual++;
                            parametros.put("denominadorTotal", denominadorEstadual);

                        }
                    } else {
                        if (dataDiff(dtPrimeirosSintomas, dtTratamento) <= 4) {
                            denominador = Integer.parseInt(municipioResidencia.getDenominador());
                            denominador++;
                            municipioResidencia.setDenominador(String.valueOf(denominador));
                            municipioResidencia.setDenominadorInt(denominador);
                            denominadorEstadual = (Integer) parametros.get("denominadorTotal");
                            denominadorEstadual++;
                            parametros.put("denominadorTotal", denominadorEstadual);
                        }
                    }
                }

            }
        }

    }

    private void calculaRegiao(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        String codRegiao = (String) parametros.get("parCodRegiaoSaude");
        parametros.put("numeradorTotal", 0);
        parametros.put("denominadorTotal", 0);

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
                    if (utilDbf.getString(rowObjects, "SG_UF_NOT") != null) {
                        try {
                            //verifica se existe a referencia do municipio no bean
                            if ((Boolean) parametros.get("parIsRegiao")) {
                                municipioResidencia = municipiosBeans.get(buscaIdRegiaoSaude(utilDbf.getString(rowObjects, "ID_MUNICIP")));
                            } else {
                                municipioResidencia = municipiosBeans.get(buscaIdRegionalSaude(utilDbf.getString(rowObjects, "ID_MUNICIP")));
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(AutoctoneMalariaPactuacao.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        calculaIndicador(rowObjects, parametros);
                        //verifica se tem o parametro de municipio de residencia
                        //Critérios
                    }
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setString("Calculando Indicador... " + (int) percentual + "% " + (k + 1) + " de " + arquivos.length + " (" + (arquivos[k] + ")"));
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
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        String codRegiao = (String) parametros.get("parCodRegiaoSaude");
        parametros.put("numeradorTotal", 0);
        parametros.put("denominadorTotal", 0);
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");
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
                    if (utilDbf.getString(rowObjects, "SG_UF_NOT") != null) {
                        //verifica se existe a referencia do municipio no bean
                        municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                        //verifica se tem o parametro de municipio de residencia
                        //Critérios
                        calculaIndicador(rowObjects, parametros);
                    }

                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setString("Calculando Indicador... " + (int) percentual + "% " + (k + 1) + " de " + arquivos.length + " (" + (arquivos[k] + ")"));
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
                    calculaIndicador(rowObjects, parametros);

                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setString("Calculando Indicador... " + (int) percentual + "% " + (k + 1) + " de " + arquivos.length + " (" + (arquivos[k] + ")"));
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
        chain = new ComparatorChain(Arrays.asList(
                new BeanComparator("uf"),
                new BeanComparator("nomeMunicipio")));
        Collections.sort(this.getBeans(), chain);
        //calcular o total
        this.getBeans().add(adicionaTotal(municipioBean, ""));
    }

    public static int dataDiff(Date dataLow, Date dataHigh) {

        GregorianCalendar startTime = new GregorianCalendar();
        GregorianCalendar endTime = new GregorianCalendar();

        GregorianCalendar curTime = new GregorianCalendar();
        GregorianCalendar baseTime = new GregorianCalendar();
        try {
            startTime.setTime(dataLow);
            endTime.setTime(dataHigh);
            int dif_multiplier = 1;

            // Verifica a ordem de inicio das datas
            if (dataLow.compareTo(dataHigh) < 0) {
                baseTime.setTime(dataHigh);
                curTime.setTime(dataLow);
                dif_multiplier = 1;
            } else {
                baseTime.setTime(dataLow);
                curTime.setTime(dataHigh);
                dif_multiplier = -1;
            }

            int result_years = 0;
            int result_months = 0;
            int result_days = 0;

            // Para cada mes e ano, vai de mes em mes pegar o ultimo dia para import acumulando
            // no total de dias. Ja leva em consideracao ano bissesto
            while (curTime.get(GregorianCalendar.YEAR) < baseTime.get(GregorianCalendar.YEAR) || curTime.get(GregorianCalendar.MONTH) < baseTime.get(GregorianCalendar.MONTH)) {

                int max_day = curTime.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
                result_months += max_day;
                curTime.add(GregorianCalendar.MONTH, 1);

            }

            // Marca que é um saldo negativo ou positivo
            result_months = result_months * dif_multiplier;

            // Retirna a diferenca de dias do total dos meses
            result_days += (endTime.get(GregorianCalendar.DAY_OF_MONTH) - startTime.get(GregorianCalendar.DAY_OF_MONTH));

            return result_years + result_months + result_days;
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
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
        parametros.put("parPeriodo", "de " + this.getDtInicioAvaliacao() + " a " + this.getDtFimAvaliacao());
        parametros.put("parTituloColuna", this.getTituloColuna());
        parametros.put("parFator", String.valueOf(this.getMultiplicador()));
        parametros.put("parAno", Util.getAno(this.getDtFimAvaliacao()));
        parametros.put("parRodape", this.getRodape());
        parametros.put("parConfig", "");
        parametros.put("parTitulo1", "Proporçao de Casos de Malária que iniciaram tratamento em tempo oportuno.");
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
        //return new String[]{"COUUFINF", "ID_LOCRES", "DS_LOCRES", "COD_CIR", "NOME_CIR", "D_TBREG", "N_TBEXAM", "P_TBEXAM", "ANO_DIAG", "DT_DIAGIN", "DT_DIAGFI", "ORIGEM"};
        return new String[]{"COUFNOT", "ID_LOCNOT", "DS_LOCNOT", "COD_CIR", "NOME_CIR", "D_MALSIN", "N_MALOP", "P_MALOP", "ANO_NOTI", "DT_NOTIN", "DT_NOTIFI", "ORIGEM"};
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("COUFNOT", new ColunasDbf(30));
        hashColunas.put("ID_LOCNOT", new ColunasDbf(30));
        hashColunas.put("DS_LOCNOT", new ColunasDbf(30));
        hashColunas.put("COD_CIR", new ColunasDbf(30));
        hashColunas.put("NOME_CIR", new ColunasDbf(30));
//        hashColunas.put("D_TBREG", new ColunasDbf(30));
//        hashColunas.put("N_TBEXAM", new ColunasDbf(30));
//        hashColunas.put("P_TBEXAM", new ColunasDbf(30));
//        hashColunas.put("ANO_DIAG", new ColunasDbf(30));
//        hashColunas.put("DT_DIAGIN", new ColunasDbf(30));
//        hashColunas.put("DT_DIAGFI", new ColunasDbf(30));
        hashColunas.put("D_MALSIN", new ColunasDbf(30));
        hashColunas.put("N_MALOP", new ColunasDbf(30));
        hashColunas.put("P_MALOP", new ColunasDbf(30));
        hashColunas.put("ANO_NOTI", new ColunasDbf(30));
        hashColunas.put("DT_NOTIN", new ColunasDbf(30));
        hashColunas.put("DT_NOTIFI", new ColunasDbf(30));
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
            String ano = String.valueOf(preencheAno(getDataInicio(), getDataFim())).replace(".0", "");
            if (ano.contains(".0")) {
                ano = ano.replace(".0", "");
            }
            rowData[8] = ano;
            rowData[9] = getDataInicio();
            rowData[10] = getDataFim();
            rowData[11] = "MALAR-SINANNET";
            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/OportunidadeMalariaPactuacao.jasper";
    }
}
