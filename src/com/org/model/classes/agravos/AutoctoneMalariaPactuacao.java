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
public class AutoctoneMalariaPactuacao extends Agravo {

    static String ANO;
    Agravo municipioResidencia;
    HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
    DBFUtil utilDbf = new DBFUtil();

    public AutoctoneMalariaPactuacao(boolean isDbf) {
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
        this.setTitulo1("Número de Casos de Malária");
        this.setTituloColuna("Indicador");
        this.setRodape("Indicador: Nº de casos autóctones de Malária  \n");
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
        Boolean RESULT = false;
        Boolean AUTOCTONE = false;

        int numerador = 0;
        int numeradorEstadual = 0;

        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");
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
            AUTOCTONE = utilDbf.getString(rowObjects, "ID_MUNICIP").equals(utilDbf.getString(rowObjects, "COMUNINF"));
            if (isBetweenDates(dtDiagnostico, dataInicio, dataFim) && AUTOCTONE) {
                numerador = Integer.parseInt(municipioResidencia.getNumerador());
                numerador++;
                municipioResidencia.setNumerador(String.valueOf(numerador));
                municipioResidencia.setNumeradorInt(numerador);
                numeradorEstadual = (Integer) parametros.get("numeradorTotal");
                numeradorEstadual++;
                parametros.put("numeradorTotal", numeradorEstadual);
            }
        }

    }

    private void calculaRegiao(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        String codRegiao = (String) parametros.get("parCodRegiaoSaude");
        HashMap<String, Agravo> regiaoBeans = new HashMap<String, Agravo>();
        parametros.put("numeradorTotal", 0);
        Agravo regiaoResidencia;

        String idMunicipio;
        if (parametros.get("parMunicipio") != null) {
            idMunicipio = (String) parametros.get("parMunicipio");
        } else {
            idMunicipio = "TODOS";
        }

        if (codRegional == null) {
            codRegional = "";
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
                        regiaoResidencia = regiaoBeans.get(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                        if(regiaoResidencia != null){
                            if((Boolean) parametros.get("parIsRegiao")){
                                municipioResidencia = municipiosBeans.get(regiaoResidencia.getCodRegiaoSaude());
                            }
                            else{
                                municipioResidencia = municipiosBeans.get(regiaoResidencia.getCodRegional());
                            }
                            calculaIndicador(rowObjects, parametros);
                        }
//                        if ((Boolean) parametros.get("parIsRegiao")) {
//                            //regiaoResidencia = regiaoBeans.get(utilDbf.getString(rowObjects, "ID_MUNICIP"));
//                            if (regiaoResidencia != null) {
//                                municipioResidencia = municipiosBeans.get(regiaoResidencia.getCodRegiaoSaude());
//                            }
//                        } else {
//                            //regiaoResidencia = regiaoBeans.get(utilDbf.getString(rowObjects, "ID_MUNICIP"));
//                            if (regiaoResidencia != null) {
//                                municipioResidencia = municipiosBeans.get(regiaoResidencia.getCodRegional());
//                            }
//                        }
                        //calculaIndicador(rowObjects, parametros);
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
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        String codRegiao = (String) parametros.get("parCodRegiaoSaude");
        parametros.put("numeradorTotal", 0);
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
                        //verifica se existe a referencia do municipio no bean
                        municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, "COMUNINF"));
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

                    municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, "COMUNINF"));
                    calculaIndicador(rowObjects, parametros);

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
        return new String[]{"COUFINF", "ID_LOCRES", "DS_LOCRES", "COD_CIR", "NOME_CIR", "NUM_MALA", "ANO_DIAG", "DT_DIAGIN", "DT_DIAGFI", "ORIGEM"};
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("COUFINF", new ColunasDbf(30));
        hashColunas.put("ID_LOCRES", new ColunasDbf(30));
        hashColunas.put("DS_LOCRES", new ColunasDbf(30));
        hashColunas.put("COD_CIR", new ColunasDbf(30));
        hashColunas.put("NOME_CIR", new ColunasDbf(30));
        hashColunas.put("NUM_MALA", new ColunasDbf(30));
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
                rowData[0] = null;
                rowData[1] = null;
                rowData[3] = null;
                rowData[4] = null;

            } else {
                rowData[0] = agravo.getCodIbgeUF(agravo.getUf());
                rowData[1] = agravo.getCodMunicipio();

                if (agravo.getRegional() != null && agravo.getRegional() != null) {
                    if (!agravo.getRegional().isEmpty()) {
                        rowData[0] = SinanUtil.siglaUFToIDUF(agravo.getUf());
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

            rowData[6] = String.valueOf(preencheAno(getDataInicio(), getDataFim()).intValue());
            rowData[7] = getDataInicio();
            rowData[8] = getDataFim();
            rowData[9] = "MALAR-SINANNET";
            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/MalariaPactuacao.jasper";
    }
}
