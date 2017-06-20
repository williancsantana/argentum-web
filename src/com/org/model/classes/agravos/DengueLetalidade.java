/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import com.org.view.Master;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanComparator;

/**
 *
 * @author geraldo
 */
public class DengueLetalidade extends Agravo {

    static String ANO;

    public DengueLetalidade(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de Primeiros Sintomas");
        setTipoAgregacao("de Residência");
        init("postgres");
    }

    public DengueLetalidade() {
    }

    @Override
    public void init(String tipoBanco) {
        this.setArquivo("DENGNET");
        this.setMultiplicador(100);
        this.setTitulo1("Taxa de letalidade por febre hemorrágica dengue por local de residência ");
        this.setTextoCompletitude("% de não preenchimento do campo Evolução do caso: ");
        this.setTituloColuna("Taxa Letalidade");
        this.setRodape("Numerador: Número de óbitos confirmados por febre hemorrágica da dengue (FHD) e síndrome do choque da dengue (SCD), por local de residência e ano de dos primeiros sintomas \n" + "Denominador: Número de casos confirmados por febre hemorrágica da dengue (FHD) e síndrome do choque da dengue (SCD), por local de residência e ano de dos primeiros sintomas ");
        this.setTipo("");
        this.setSqlNumeradorCompletitude("tem completitude");
        if (!isDBF()) {
            if (tipoBanco.equals("INTERBASE")) {
                this.setSqlNumeradorMunicipioEspecifico("SELECT  count(*)  numerador FROM  tb_notificacao " + 
                        "where co_cid = 'A90' and (dt_diagnostico_sintoma BETWEEN ?  AND ?) "
                        + "and co_uf_residencia= ? and co_municipio_residencia = ? and tp_evolucao_caso = '2' "
                        + "and tp_classificacao_final in (3,4) and tp_criterio_confirmacao = 1");
                
                this.setSqlDenominadorMunicipioEspecifico("SELECT count(*) denominador FROM  tb_notificacao " + 
                        "where co_cid = 'A90' and (dt_diagnostico_sintoma BETWEEN  ? AND ?) and co_uf_residencia= ? "
                        + "and co_municipio_residencia = ? and tp_classificacao_final in (3,4) "
                        + "and tp_criterio_confirmacao = 1");

                this.setSqlNumeradorEstado("SELECT count(*) numerador FROM tb_notificacao where co_cid = 'A90' "
                        + "and (dt_diagnostico_sintoma BETWEEN ? AND ?) and co_uf_residencia= ? "
                        + "and tp_evolucao_caso = '2' and tp_classificacao_final in (3,4) "
                        + "and tp_criterio_confirmacao = 1");
                
                this.setSqlDenominandorEstado("SELECT count(*) denominador FROM tb_notificacao where co_cid = 'A90' "
                        + "and (dt_diagnostico_sintoma BETWEEN ? AND ?) and co_uf_residencia= ? "
                        + "and tp_classificacao_final in (3,4) and tp_criterio_confirmacao = 1");
                
                this.setSqlNumeradorCompletitude("SELECT  count(*) numerador FROM tb_notificacao where co_cid = 'A90' "
                        + " and (dt_diagnostico_sintoma BETWEEN ?  AND ?) and co_uf_residencia= ? "
                        + "and tp_evolucao_caso is null and tp_classificacao_final in (3,4) "
                        + "and tp_criterio_confirmacao = 1");
                
            } else {
                this.setSqlNumeradorMunicipioEspecifico("SELECT  count(*) as numerador "
                        + "FROM dbsinan.tb_notificacao as t1 where co_cid = 'A90' "
                        + "and (dt_diagnostico_sintoma BETWEEN ? AND ?) and co_uf_residencia= ? "
                        + "and co_municipio_residencia = ? and tp_evolucao_caso = '2' "
                        + "and tp_classificacao_final in (3,4) and tp_criterio_confirmacao = 1");
                
                this.setSqlDenominadorMunicipioEspecifico("SELECT count(*) as denominador "
                        + "FROM dbsinan.tb_notificacao as t1 where co_cid = 'A90' "
                        + "and (dt_diagnostico_sintoma BETWEEN ? AND ?) and co_uf_residencia= ? "
                        + "and co_municipio_residencia = ? and tp_classificacao_final in (3,4) "
                        + "and tp_criterio_confirmacao = 1");

                this.setSqlNumeradorEstado("SELECT  count(*) as numerador FROM dbsinan.tb_notificacao as t1 " + 
                        "where co_cid = 'A90' and (dt_diagnostico_sintoma BETWEEN ? AND ?) and co_uf_residencia= ? "
                        + "and tp_evolucao_caso = '2' and tp_classificacao_final in (3,4) "
                        + "and tp_criterio_confirmacao = 1");
                
                this.setSqlDenominandorEstado("SELECT count(*) as denominador FROM  dbsinan.tb_notificacao as t1 " + 
                        "where co_cid = 'A90' and (dt_diagnostico_sintoma BETWEEN ? AND ?) and co_uf_residencia= ? "
                        + "and tp_classificacao_final in (3,4) and tp_criterio_confirmacao = 1");
                
                this.setSqlNumeradorCompletitude("SELECT count(*) as numerador FROM dbsinan.tb_notificacao as t1 " + 
                        "where co_cid = 'A90' and (dt_diagnostico_sintoma BETWEEN ? AND ?) and co_uf_residencia= ? "
                        + "and tp_evolucao_caso is null and tp_classificacao_final in (3,4) "
                        + "and tp_criterio_confirmacao = 1");

            }
            this.setSqlNumeradorBeanMunicipios(this.getSqlNumeradorMunicipioEspecifico());
            this.setSqlDenominadorBeanMunicipios(this.getSqlDenominadorMunicipioEspecifico());
        }
    }

    public boolean verificaClassificacaoFinal(int classificacaoFinal) {
        if (classificacaoFinal == 3 || classificacaoFinal == 4) {
            return true;
        }
        return false;
    }
    
    public boolean verificaCriterio(int criterio) {
        if (criterio == 1) {
            return true;
        }
        return false;
    }

    public boolean verificaPeriodo(Map parametros, DBFUtil utilDbf, Object[] rowObjects) throws ParseException {
        return isBetweenDates(utilDbf.getDate(rowObjects, "DT_SIN_PRI"), (String) parametros.get("parDataInicio"), (String) parametros.get("parDataFim"));
    }

    private void calculaBrasil(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        DBFUtil utilDbf = new DBFUtil();
        String coluna;
        if (parametros.get("municipios").toString().equals("sim")) {
            municipiosBeans = populaMunicipiosBeans("BR", "");
            coluna = "ID_MN_RESI";
        } else {
            municipiosBeans = populaUfsBeans();
            coluna = "SG_UF";
        }

        //inicia o calculo

        Object[] rowObjects;
        String evolucao;
        int classificacaoFinal, completitude = 0, denominadorCompletitude = 0, criterio = 0;
        String total;
        DecimalFormat df = new DecimalFormat("0.00");
        int denominador = 0;
        int numerador = 0;
        int numeradorEstadual = 0;
        int denominadorEstadual = 0;

        boolean isBetween = false;

        Agravo municipioResidencia;
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
                        evolucao = utilDbf.getString(rowObjects, "EVOLUCAO");
                        classificacaoFinal = utilDbf.getInt(rowObjects, "CLASSI_FIN");
                        criterio = utilDbf.getInt(rowObjects, "CRITERIO");
                        isBetween = verificaPeriodo(parametros, utilDbf, rowObjects);
                        if (municipioResidencia != null) {
                            //se estiver no hashmap, inicia o calulo
                            if (evolucao != null) {
                                if (verificaClassificacaoFinal(classificacaoFinal) && verificaCriterio(criterio)) {
                                    if (isBetween) {
                                        //incrementa o denominador
                                        denominador = Integer.parseInt(municipioResidencia.getDenominador());
                                        denominador++;
                                        municipioResidencia.setDenominador(String.valueOf(denominador));
                                        if (evolucao.equals("2")) {
                                            //incrementa o numerador
                                            numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                            numerador++;
                                            municipioResidencia.setNumerador(String.valueOf(numerador));
                                        }
                                        denominadorCompletitude++;
                                    }
                                }
                            } else {
                                //entra no incremento da completitude
                                if (isBetween && verificaClassificacaoFinal(classificacaoFinal) && verificaCriterio(criterio)) {
                                    completitude++;
                                }
                            }
                        }
//                    else
//                    {
//                        //CALCULA A TAXA ESTADUAL
//                        if (evolucao != null) {
//                            if (verificaClassificacaoFinal(classificacaoFinal)) {
//                                if (isBetween) {
//                                    //denominadorEstadual
//                                    denominadorEstadual++;
//                                    if (evolucao.equals("2")) {
//                                        //numeradorEstadual
//                                        numeradorEstadual++;
//                                    }
//                                }
//
//                            }
//                        }
//                            else {
//                            //entra no incremento da completitude
//                            if (isBetween && verificaClassificacaoFinal(classificacaoFinal)) {
//                                completitude++;
//                            }
//                        }
//                    }
                    }
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setValue((int) percentual);
                    i++;
                }

            } catch (DBFException ex) {
                Master.mensagem("Erro:\n" + ex);
            }
        }
        //denominadorEstadual--;
        total = df.format(Double.parseDouble(String.valueOf(numeradorEstadual + numerador)) / Double.parseDouble(String.valueOf(denominadorEstadual + denominador)) * 100);
        setTaxaEstadual(total + " (Numerador:" + String.valueOf(numeradorEstadual + numerador) + " / Denominador: " + String.valueOf(denominadorEstadual + denominador) + ")");
        //calcula o percentual da completitude
        setPercentualCompletitude(df.format(Double.parseDouble(String.valueOf(completitude)) / Double.parseDouble(String.valueOf(denominadorCompletitude)) * 100));

        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<Agravo> municipioBean = municipiosBeans.values();
        if (parametros.get("parSgUf").toString().equals("TODAS") || parametros.get("municipios").toString().equals("sim")) {
            for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
                Agravo agravoDBF = it.next();
                double num = Double.parseDouble(agravoDBF.getNumerador());
                double den = Double.parseDouble(agravoDBF.getDenominador());
                if (den == 0) {
                    agravoDBF.setTaxa("0.00");
                } else {
                    agravoDBF.setTaxa(df.format(num / den * 100));
                }
                this.getBeans().add(agravoDBF);
            }
            Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
        }
        //calcular o total
        this.getBeans().add(adicionaBrasil(municipioBean));
    }

    private void calculaMunicipios(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        DBFUtil utilDbf = new DBFUtil();
        if (codRegional == null) {
            codRegional = "";
        }

        municipiosBeans = populaMunicipiosBeans(sgUfResidencia, codRegional);

        //inicia o calculo

        Object[] rowObjects;
        String evolucao;
        int classificacaoFinal, completitude = 0, denominadorCompletitude = 0, criterio = 0;
        
        String total;
        DecimalFormat df = new DecimalFormat("0.00");
        int denominador = 0;
        int numerador = 0;
        int numeradorEstadual = 0;
        int denominadorEstadual = 0;
        int numeradorRegional = 0;
        int denominadorRegional = 0;
        boolean isBetween = false;

        Agravo municipioResidencia;
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
                        evolucao = utilDbf.getString(rowObjects, "EVOLUCAO");
                        classificacaoFinal = utilDbf.getInt(rowObjects, "CLASSI_FIN");
                        criterio = utilDbf.getInt(rowObjects, "CRITERIO");
                        isBetween = verificaPeriodo(parametros, utilDbf, rowObjects);
                        if (municipioResidencia != null) {
                            //se estiver no hashmap, inicia o calulo
                            if (evolucao != null) {
                                if (verificaClassificacaoFinal(classificacaoFinal) && verificaCriterio(criterio)) {
                                    if (isBetween) {
                                        //incrementa o denominador
                                        denominador = Integer.parseInt(municipioResidencia.getDenominador());
                                        denominador++;
                                        municipioResidencia.setDenominador(String.valueOf(denominador));
                                        denominadorEstadual++;
                                        denominadorRegional++;
                                        if (evolucao.equals("2")) {
                                            //incrementa o numerador
                                            numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                            numerador++;
                                            municipioResidencia.setNumerador(String.valueOf(numerador));
                                            numeradorEstadual++;
                                            numeradorRegional++;
                                        }
                                        denominadorCompletitude++;
                                    }
                                }
                            } else {
                                //entra no incremento da completitude
                                if (isBetween && verificaClassificacaoFinal(classificacaoFinal) && verificaCriterio(criterio)) {
                                    completitude++;
                                }
                            }
                        }
//                    else {
//                        //CALCULA A TAXA ESTADUAL
//                        //verifica se é do estado
//                        if (ufResidencia.equals(utilDbf.getString(rowObjects, "SG_UF"))) {
//                            if (evolucao != null) {
//                                if (verificaClassificacaoFinal(classificacaoFinal)) {
//                                    if (isBetween) {
//                                        //denominadorEstadual
//                                        denominadorEstadual++;
//                                        if (evolucao.equals("2")) {
//                                            //numeradorEstadual
//                                            numeradorEstadual++;
//                                        }
//                                    }
//
//                                }
//                            } else {
//                                //entra no incremento da completitude
//                                if (isBetween && verificaClassificacaoFinal(classificacaoFinal)) {
//                                    completitude++;
//                                }
//                            }
//
//                        }
//                    }
                    }
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setValue((int) percentual);
                    i++;
                }

            } catch (DBFException ex) {
                Master.mensagem("Erro:\n" + ex);
            }
        }
        //denominadorEstadual--;
        total = df.format(Double.parseDouble(String.valueOf(numeradorEstadual)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100);
        setTaxaEstadual(total + " (Numerador:" + String.valueOf(numeradorEstadual) + " / Denominador: " + String.valueOf(denominadorEstadual) + ")");
        //verifica se tem que mostrar o relatorio por regional
        if (!codRegional.equals("")) {
            total = df.format(Double.parseDouble(String.valueOf(numeradorRegional)) / Double.parseDouble(String.valueOf(denominadorRegional)) * 100);
            setTaxaEstadual(getTaxaEstadual() + " Taxa Regional: " + total + " (Numerador:" + String.valueOf(numeradorRegional) + " / Denominador: " + String.valueOf(denominadorRegional) + ")");
        }
        //calcula o percentual da completitude
        setPercentualCompletitude(df.format(Double.parseDouble(String.valueOf(completitude)) / Double.parseDouble(String.valueOf(denominadorCompletitude)) * 100));

        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<Agravo> municipioBean = municipiosBeans.values();

        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoDBF = it.next();
            double num = Double.parseDouble(agravoDBF.getNumerador());
            double den = Double.parseDouble(agravoDBF.getDenominador());
            if (den == 0) {
                agravoDBF.setTaxa("0.00");
            } else {
                agravoDBF.setTaxa(df.format(num / den * 100));
            }
            this.getBeans().add(agravoDBF);
        }
        Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
    }

    @Override
    public void calcula(DBFReader reader, Map parametros) {
        String municipios = (String) parametros.get("municipios");
        String brasil = (String) parametros.get("parUf");
        if (municipios.equals("sim") && !brasil.equals("brasil")) {
            try {
                calculaMunicipios(reader, parametros);
            } catch (ParseException ex) {
                System.out.println(ex);
            }
        } else {
            if (brasil.equals("brasil")) {
                try {
                    calculaBrasil(reader, parametros);
                } catch (ParseException ex) {
                    System.out.println(ex);
                }
            } else {
                Object[] rowObjects;
                DBFUtil utilDbf = new DBFUtil();
                String evolucao;
                int classificacaoFinal, completitude = 0, denominadorCompletitude = 0, criterio = 0;
                String total;
                DecimalFormat df = new DecimalFormat("0.00");
                int denominadorEstadual = 0;
                int numeradorEstadual = 0;
                int denominadorEspecifico = 0;
                int numeradorEspecifico = 0;
                boolean isBetween = false;
                String ufResidencia = (String) parametros.get("parUf");
                String municipioResidencia = (String) parametros.get("parMunicipio");
                String parMunicipio = municipioResidencia;
                if (municipioResidencia == null) {
                    municipioResidencia = "";
                    parMunicipio = ufResidencia;
                }
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
                                if (utilDbf.getString(rowObjects, "SG_UF").equals(ufResidencia)) {
                                    //verifica se tem o parametro de municipio de residencia
                                    evolucao = utilDbf.getString(rowObjects, "EVOLUCAO");
                                    classificacaoFinal = utilDbf.getInt(rowObjects, "CLASSI_FIN");
                                    criterio = utilDbf.getInt(rowObjects, "CRITERIO");
                                    try {
                                        isBetween = verificaPeriodo(parametros, utilDbf, rowObjects);
                                    } catch (ParseException ex) {
                                        System.out.println(ex);
                                    }
                                    if (verificaMunicipio(municipioResidencia, utilDbf.getString(rowObjects, "ID_MN_RESI"))) {
                                        if (evolucao != null) {
                                            if (verificaClassificacaoFinal(classificacaoFinal) && verificaCriterio(criterio)) {
                                                try {
                                                    if (isBetween) {
                                                        System.out.println(utilDbf.getString(rowObjects, "DT_SIN_PRI") + ";" + utilDbf.getString(rowObjects, "NU_NOTIFIC"));
                                                        //incrementa o denominador
                                                        denominadorEspecifico++;
                                                        denominadorEstadual++;
                                                        if (evolucao.equals("2")) {
                                                            numeradorEspecifico++;
                                                            numeradorEstadual++;
                                                        }
                                                        denominadorCompletitude++;
                                                    }
                                                } catch (NumberFormatException ex) {
                                                    Master.mensagem("Erro:\n" + ex);
                                                    System.out.println(ex);
                                                }

                                            }
                                        } else {
                                            //entra no incremento da completitude
                                            if (isBetween && verificaClassificacaoFinal(classificacaoFinal) && verificaCriterio(criterio)) {
                                                completitude++;
                                                //incrementa o denominador
                                                denominadorEspecifico++;
                                                denominadorEstadual++;
                                                denominadorCompletitude++;
                                            }
                                        }
                                    }
//                                else
//                                {
//                                    //CALCULA A TAXA ESTADUAL
//                                    if (evolucao != null) {
//                                        if (verificaClassificacaoFinal(classificacaoFinal)) {
//                                            if (isBetween) {
//                                                //denominadorEstadual
//                                                denominadorEstadual++;
//                                                if (evolucao.equals("2")) {
//                                                    //numeradorEstadual
//                                                    numeradorEstadual++;
//                                                }
//                                            }
//                                        }
//                                    }
////                                    else {
////                                        //entra no incremento da completitude
////                                        if (isBetween && verificaClassificacaoFinal(classificacaoFinal)) {
////                                            completitude++;
////                                        }
////                                    }
//                                }

                                }
                            }
                            float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                            getBarraStatus().setValue((int) percentual);
                            i++;
                        }

                    } catch (DBFException ex) {
                        Master.mensagem("Erro:\n" + ex);
                        System.out.println(ex);
                    }
                }
                total = df.format(Double.parseDouble(String.valueOf(numeradorEstadual)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100);
                setTaxaEstadual(total + " (Numerador:" + String.valueOf(numeradorEstadual) + " / Denominador: " + String.valueOf(denominadorEstadual) + ")");

                //começa o preencher o bean para estado ou 1 municipio
                Agravo d1 = new Agravo();
                d1.setCodMunicipio(parMunicipio);//falta aqui
                if (municipioResidencia.equals("")) {
                    d1.setNomeMunicipio((String) parametros.get("parSgUf"));
                } else {
                    d1.setNomeMunicipio((String) parametros.get("parNomeMunicipio"));
                }


                if (!String.valueOf(denominadorEspecifico).equals("0.0")) {
                    d1.setNumerador(String.valueOf(NumberFormat.getNumberInstance().format(Double.parseDouble(String.valueOf(numeradorEspecifico)))));
                    d1.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(Double.parseDouble(String.valueOf(denominadorEspecifico)))));
                    total = df.format(Double.parseDouble(String.valueOf(numeradorEspecifico)) / Double.parseDouble(String.valueOf(denominadorEspecifico)) * 100);
                    //calcula o percentual da completitude
                    setPercentualCompletitude(df.format(Double.parseDouble(String.valueOf(completitude)) / Double.parseDouble(String.valueOf(denominadorCompletitude)) * 100));
                    d1.setTaxa(total);
                } else {
                    d1.setNumerador("0");
                    d1.setDenominador("0");
                    d1.setTaxa("0.00");
                    setPercentualCompletitude("0.00");
                }
                this.setBeans(new ArrayList());
                this.getBeans().add(d1);
            }
        }
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("ID_LOCRES", new ColunasDbf(7));
        hashColunas.put("DS_LOCRES", new ColunasDbf(30));
        hashColunas.put("ID_UFRES", new ColunasDbf(2));
        hashColunas.put("N_LETDENG", new ColunasDbf(10, 0));
        hashColunas.put("D_LETDENG", new ColunasDbf(10, 0));
        hashColunas.put("I_LETDENG", new ColunasDbf(6, 2));
        hashColunas.put("ANO_PRSIN", new ColunasDbf(4, 0));
        hashColunas.put("DT_PRINIC", new ColunasDbf(10));
        hashColunas.put("DT_PRIFIN", new ColunasDbf(10));
        hashColunas.put("ORIGEM", new ColunasDbf(30));
        this.setColunas(hashColunas);
        return hashColunas;
    }
     @Override
    public String[] getOrdemColunas() {
        return new String[]{"ID_LOCRES", "DS_LOCRES", "ID_UFRES", "N_LETDENG", "D_LETDENG",
                    "I_LETDENG", "ANO_PRSIN", "DT_PRINIC", "DT_PRIFIN", "ORIGEM"};
    }

    @Override
    public Map getParametros() {
        Map parametros = new HashMap();
        parametros.put("parDataInicio", Util.formataData(this.getDataInicio()));
        parametros.put("parDataFim", Util.formataData(this.getDataFim()));
        parametros.put("parPeriodo", "de " + this.getDataInicio() + " a " + this.getDataFim());
        parametros.put("parTituloColuna", this.getTituloColuna());
        parametros.put("parFator", String.valueOf(this.getMultiplicador()));
        parametros.put("parAno", Util.getAno(this.getDataFim()));
        parametros.put("parRodape", this.getRodape());
        parametros.put("parConfig", "");
        parametros.put("parTitulo1", "Taxa de letalidade por febre hemorrágica dengue por local de residência");
        //pegar o ano para exportar para dbf
        ANO = "";
        if (Util.getAno(this.getDataFim()).equals(Util.getAno(this.getDataInicio()))) {
            ANO = Util.getAno(this.getDataFim());
        }
        //informa datas selecionadas
        setDataInicio(this.getDataInicio());
        setDataFim(this.getDataFim());
        return parametros;
    }

    @Override
    public DBFWriter getLinhas(HashMap<String, ColunasDbf> colunas, List bean, DBFWriter writer) throws DBFException, IOException {
        for (int i = 0; i < bean.size(); i++) {
            Object rowData[] = new Object[colunas.size()];
            Agravo agravo = (Agravo) bean.get(i);
            if (agravo.getNomeMunicipio().equals("BRASIL")) {
                rowData[0] = null;
                rowData[2] = null;
            } else {
                rowData[0] = agravo.getCodMunicipio();
                rowData[2] = agravo.getCodMunicipio().substring(0, 2);
            }
            rowData[1] = agravo.getNomeMunicipio();
            rowData[3] = Double.parseDouble(agravo.getNumerador());
            rowData[4] = Double.parseDouble(agravo.getDenominador());
            rowData[5] = Double.parseDouble(agravo.getTaxa().replace(",", "."));
            rowData[6] = preencheAno(getDataInicio(), getDataFim());
            rowData[7] = getDataInicio();
            rowData[8] = getDataFim();
            rowData[9] = "DENGUELETALIDADE-SINANNET";


            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/agravo1.jasper";
    }

   
}
