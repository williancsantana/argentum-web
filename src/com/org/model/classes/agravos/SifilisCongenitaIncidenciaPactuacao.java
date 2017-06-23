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
import com.org.model.classes.agravos.oportunidade.OportunidadeAgravoPQAVS;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanComparator;

/**
 *
 * @author geraldo
 */
public class SifilisCongenitaIncidenciaPactuacao extends Agravo {

    static String ANO;

    public SifilisCongenitaIncidenciaPactuacao(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de Diagnóstico");
        setTipoAgregacao("de Residência");
        init("postgres");
    }

    public SifilisCongenitaIncidenciaPactuacao() {
    }

    @Override
    public void init(String tipoBanco) {
        this.setMultiplicador(1);
        this.setTitulo1("SifilisCongenitaIncidencia");
        this.setTextoCompletitude("");
        this.setTituloColuna("Todas Idades");
        this.setRodape("Número absoluto de casos de Sífilis Congênita residentes em determinado local e período de diagnóstico selecionado");
        this.setTipo("sifilis");
        this.setSqlNumeradorCompletitude("");
        if (!isDBF()) {
            this.setSqlNumeradorMunicipioEspecifico("SELECT  count(*) as numerador FROM  dbsinan.tb_notificacao " + "where co_cid = 'A50.9' and (dt_diagnostico_sintoma BETWEEN ?  " + "AND ?) and " + "co_uf_residencia= ? and " + "co_municipio_residencia = ? and nu_idade < 4001");
            this.setSqlDenominadorMunicipioEspecifico("SELECT count(*) as denominador " + "FROM  dbsinan.tb_notificacao " + "where co_cid = 'A50.9' and (dt_diagnostico_sintoma BETWEEN  ? " + " AND ?) and" + " co_uf_residencia= ? and " + "co_municipio_residencia = ?  and (nu_idade is null or nu_idade > 4125 or nu_idade = 0000)");
            this.setSqlNumeradorEstado("SELECT  count(*) as numerador FROM  dbsinan.tb_notificacao " + "where co_cid = 'A50.9' and (dt_diagnostico_sintoma BETWEEN ?  " + "AND ?) and " + "co_uf_residencia= ?  and nu_idade < 4001");
            this.setSqlDenominandorEstado("SELECT  count(*) as denominador FROM  dbsinan.tb_notificacao " + "where co_cid = 'A50.9' and (dt_diagnostico_sintoma BETWEEN ?  " + "AND ?) and " + "co_uf_residencia= ?  and (nu_idade is null or nu_idade > 4125 or nu_idade = 0000)");
            this.setSqlNumeradorCompletitude("");
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
        int idade;
        String total;
        DecimalFormat df = new DecimalFormat("0.00");
        int denominador = 0;
        int numerador = 0;
        int totalNotificacoes = 0;
        int numeradorEstadual = 0;
        int denominadorEstadual = 0;
        int diagnosticoFinal = 0;
        boolean isBetween = false;

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
                        idade = utilDbf.getInt(rowObjects, "NU_IDADE_N");
                        diagnosticoFinal = utilDbf.getInt(rowObjects, "EVO_DIAG_N");
                        isBetween = isBetweenDates(utilDbf.getDate(rowObjects, "DT_DIAG"), dataInicio, dataFim);
                        if(diagnosticoFinal == 1 || diagnosticoFinal == 2 || diagnosticoFinal ==4){
                        if (municipioResidencia != null) {
                            //se estiver no hashmap, inicia o calulo
                            if (idade == -1 || idade > 4125) {
                                if (isBetween) {
                                    //incrementa o denominador que na verdade eh os casos sem informacao de idade
                                    denominador = Integer.parseInt(municipioResidencia.getDenominador());
                                    denominador++;
                                    municipioResidencia.setDenominador(String.valueOf(denominador));
                                }
                            } else {
                                //incrementa o numerador que na verdade eh os casos < que 1 ano de idade
                                if (isBetween && idade < 4001) {
                                    numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                    numerador++;
                                    municipioResidencia.setNumerador(String.valueOf(numerador));
                                }
                            }
                            //incrementa a taxa que tras todas as notificacoes
                            if (isBetween) {
                                if (municipioResidencia.getTaxa() == null) {
                                    municipioResidencia.setTaxa("0");
                                }
                                numerador = Integer.parseInt(municipioResidencia.getTaxa());
                                numerador++;
                                municipioResidencia.setTaxa(String.valueOf(numerador));
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
        //denominadorEstadual--;
        total = df.format(Double.parseDouble(String.valueOf(numeradorEstadual + numerador)) + Double.parseDouble(String.valueOf(denominadorEstadual + denominador)));
        setTaxaEstadual(total + " (Menor que 1 ano:" + String.valueOf(numeradorEstadual + numerador) + " / Sem informação de idade: " + String.valueOf(denominadorEstadual + denominador) + ")");

        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<Agravo> municipioBean = municipiosBeans.values();
        if (parametros.get("parSgUf").toString().equals("TODAS") || parametros.get("municipios").toString().equals("sim")) {
            for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
                Agravo agravoDBF = it.next();
//                double num = Double.parseDouble(agravoDBF.getNumerador());
//                double den = Double.parseDouble(agravoDBF.getDenominador());
//                if (den == 0) {
//                    agravoDBF.setTaxa("0");
//                } else {
//                    agravoDBF.setTaxa(df.format(num + den));
//                }
                if (agravoDBF.getTaxa() == null) {
                    agravoDBF.setTaxa("0");
                }
                if (!coluna.equals("ID_MN_RESI")) {
                    agravoDBF.setCodMunicipio(agravoDBF.getNomeMunicipio());
                }
                this.getBeans().add(agravoDBF);
            }
            Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
        }
        //calcular o total
        this.getBeans().add(adicionaBrasil(municipioBean));
    }

    @Override
    public Agravo adicionaBrasil(Collection<Agravo> municipioBean) {
        Agravo agravoBean = new Agravo();
        agravoBean.setNomeMunicipio("BRASIL");
        agravoBean.setCodMunicipio("51");
        agravoBean.setNumerador("0");
        agravoBean.setDenominador("0");
        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoUF = it.next();
            agravoBean.setNumerador(String.valueOf(Integer.parseInt(agravoUF.getNumerador()) + Integer.parseInt(agravoBean.getNumerador())));
            agravoBean.setDenominador(String.valueOf(Integer.parseInt(agravoUF.getDenominador()) + Integer.parseInt(agravoBean.getDenominador())));
            if (agravoUF.getTaxa() == null) {
                agravoUF.setTaxa("0");

            }
            if (agravoBean.getTaxa() == null) {
                agravoBean.setTaxa("0");

            }
            agravoBean.setTaxa(String.valueOf(Integer.parseInt(agravoUF.getTaxa()) + Integer.parseInt(agravoBean.getTaxa())));
        }
        return agravoBean;
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
        if ((Boolean)parametros.get("parIsRegiao")) {
            municipiosBeans = populaMunicipiosBeansPactuacao(sgUfResidencia,codRegiao);
        }else{
             municipiosBeans = populaMunicipiosBeans(sgUfResidencia, codRegional);
        }

        //inicia o calculo
        Object[] rowObjects;
        int idade;
        String total;
        DecimalFormat df = new DecimalFormat("0.00");
        int denominador = 0;
        int numerador = 0;
        int numeradorEstadual = 0;
        int denominadorEstadual = 0;
        int numeradorRegional = 0;
        int diagnosticoFinal = 0;
        int denominadorRegional = 0;
        boolean isBetween = false;

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
                        idade = utilDbf.getInt(rowObjects, "NU_IDADE_N");
                        diagnosticoFinal = utilDbf.getInt(rowObjects, "EVO_DIAG_N");
                        isBetween = isBetweenDates(utilDbf.getDate(rowObjects, "DT_DIAG"), dataInicio, dataFim);
                        if(diagnosticoFinal == 1 || diagnosticoFinal == 2 || diagnosticoFinal ==4){
                            /*
                        if (municipioResidencia != null) {
                            //se estiver no hashmap, inicia o calulo
                            if (idade == -1 || idade > 4125) {
                                if (isBetween) {
                                    //incrementa o denominador
                                    denominador = Integer.parseInt(municipioResidencia.getDenominador());
                                    denominador++;
                                    municipioResidencia.setDenominador(String.valueOf(denominador));
                                    denominadorEstadual++;
                                    denominadorRegional++;
                                   
                                }

                            } else {
                                if (idade < 4001 && isBetween) {
                                    //incrementa o numerador
                                    numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                    numerador++;
                                    municipioResidencia.setNumerador(String.valueOf(numerador));
                                    numeradorEstadual++;
                                    numeradorRegional++;
                                    
                                }
                            }
                            //incrementa a taxa que tras todas as notificacoes
                            if (isBetween) {
                                if (municipioResidencia.getTaxa() == null) {
                                    municipioResidencia.setTaxa("0");
                                }
                                numerador = Integer.parseInt(municipioResidencia.getTaxa());
                                numerador++;
                                //municipioResidencia.setTaxa(String.valueOf(numerador));
                                municipioResidencia.setTaxa(String.valueOf(todasIdades));
                            }
                        } 
                        */
                             if (municipioResidencia != null) {
                            //se estiver no hashmap, inicia o calulo
                            if (idade == -1 || idade > 4125) {
                                if (isBetween) {
                                    //incrementa o denominador que na verdade eh os casos sem informacao de idade
                                    denominador = Integer.parseInt(municipioResidencia.getDenominador());
                                    denominador++;
                                    municipioResidencia.setDenominador(String.valueOf(denominador));
                                }
                            } else {
                                //incrementa o numerador que na verdade eh os casos < que 1 ano de idade
                                if (isBetween && idade < 4001) {
                                    numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                    numerador++;
                                    municipioResidencia.setNumerador(String.valueOf(numerador));
                                }
                            }
                            //incrementa a taxa que tras todas as notificacoes
                            if (isBetween) {
                                if (municipioResidencia.getTaxa() == null) {
                                    municipioResidencia.setTaxa("0");
                                }
                                numerador = Integer.parseInt(municipioResidencia.getTaxa());
                                numerador++;
                                municipioResidencia.setTaxa(String.valueOf(numerador));
                            }
                        }else {
                            //CALCULA A TAXA ESTADUAL
                            //verifica se é do estado
                            if (ufResidencia.equals(utilDbf.getString(rowObjects, "SG_UF"))) {
                                if (idade == -1) {
                                    if (isBetween) {
                                        //denominadorEstadual
                                        denominadorEstadual++;
                                    }
                                } else {
                                    if (idade < 4001 && isBetween) {
                                        //numeradorEstadual
                                        numeradorEstadual++;
                                    }
                                }

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
        //denominadorEstadual--;
        total = df.format(Double.parseDouble(String.valueOf(numeradorEstadual)) + Double.parseDouble(String.valueOf(denominadorEstadual)));
        setTaxaEstadual(total + " (Menor que 1 ano:" + String.valueOf(numeradorEstadual) + " / Sem informação de idade: " + String.valueOf(denominadorEstadual) + ")");
        //verifica se tem que mostrar o relatorio por regional
        if (!codRegional.equals("")) {
            total = df.format(Double.parseDouble(String.valueOf(numeradorRegional)) + Double.parseDouble(String.valueOf(denominadorRegional)));
            setTaxaEstadual(getTaxaEstadual() + " Regional: " + total + " (Menor que 1 ano:" + String.valueOf(numeradorRegional) + " / Sem informação de idade: " + String.valueOf(denominadorRegional) + ")");
        }

        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<Agravo> municipioBean = municipiosBeans.values();

        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoDBF = it.next();
//            double num = Double.parseDouble(agravoDBF.getNumerador());
//            double den = Double.parseDouble(agravoDBF.getDenominador());
//            if (den == 0) {
//                agravoDBF.setTaxa("0");
//            } else {
//                agravoDBF.setTaxa(df.format(num + den));
//            }
            if (agravoDBF.getTaxa() == null) {
                agravoDBF.setTaxa("0");
            }
            this.getBeans().add(agravoDBF);
        }
        Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
         //calcular o total
        if ((Boolean)parametros.get("parIsRegiao")) {
            this.getBeans().add(adicionaTotal(municipioBean,codRegiao));
        }else{
             this.getBeans().add(adicionaTotal(municipioBean,codRegional));
        }
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
                int idade;
                String total;
                DecimalFormat df = new DecimalFormat("0.00");
                int denominadorEstadual = 0;
                int numeradorEstadual = 0;
                int denominadorEspecifico = 0;
                int diagnosticoFinal = 0;
                int numeradorEspecifico = 0;
                int todasIdades = 0;
                boolean isBetween = false;
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
                                    idade = utilDbf.getInt(rowObjects, "NU_IDADE_N");
                                    diagnosticoFinal = utilDbf.getInt(rowObjects, "EVO_DIAG_N");
                                    if(diagnosticoFinal == 1 || diagnosticoFinal == 2 || diagnosticoFinal ==4){
                                    try {
                                        isBetween = isBetweenDates(utilDbf.getDate(rowObjects, "DT_DIAG"), dataInicio, dataFim);
                                    } catch (ParseException ex) {
                                        System.out.println(ex);
                                    }
                                    if (verificaMunicipio(municipioResidencia, utilDbf.getString(rowObjects, "ID_MN_RESI"))) {
                                        if (idade == -1) {
                                            try {
                                                if (isBetween) {
                                                    //incrementa o denominador
                                                    denominadorEspecifico++;
                                                    denominadorEstadual++;
                                                }
                                            } catch (NumberFormatException ex) {
                                                Master.mensagem("Erro:\n" + ex);
                                            }
                                        } else {
                                            if (isBetween && idade < 4001) {
                                                numeradorEspecifico++;
                                                numeradorEstadual++;
                                            }
                                        }
                                        if (isBetween) {
                                            todasIdades++;
                                        }
                                    } else {
                                        //CALCULA A TAXA ESTADUAL
                                        if (idade == -1) {
                                            if (isBetween) {
                                                //denominadorEstadual
                                                denominadorEstadual++;

                                            }
                                        } else {
                                            if (isBetween && idade < 4001) {
                                                //numeradorEstadual
                                                numeradorEstadual++;
                                            }
                                        }
                                    }
                                    }

                                }
                            }
                            float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                            getBarraStatus().setValue((int) percentual);
//                        getLabel().setText("Registros: " + i + " de " + TotalRegistros);
                            i++;
                        }

                    } catch (DBFException ex) {
                        Master.mensagem("Erro:\n" + ex);
                        System.out.println(ex);
                    }
                }
                total = df.format(Double.parseDouble(String.valueOf(numeradorEstadual)) + Double.parseDouble(String.valueOf(denominadorEstadual)));
                setTaxaEstadual(total + " (Menor que 1 ano:" + String.valueOf(numeradorEstadual) + " / Sem informação de idade: " + String.valueOf(denominadorEstadual) + ")");

                //começa o preencher o bean para estado ou 1 municipio
                Agravo d1 = new Agravo();
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
                    total = df.format(Double.parseDouble(String.valueOf(numeradorEspecifico)) + Double.parseDouble(String.valueOf(denominadorEspecifico)));
                    d1.setTaxa(String.valueOf(todasIdades));
                } else {
                    d1.setNumerador("0");
                    d1.setDenominador("0");
                    d1.setTaxa(String.valueOf(todasIdades));
                }
                this.setBeans(new ArrayList());
                this.getBeans().add(d1);
            }
        }
    }

    @Override
    public Map getParametros() {
        Util util = new Util();
        Map parametros = new HashMap();
        parametros.put("parDataInicio", util.formataData(this.getDataInicio()));
        parametros.put("parDataFim", util.formataData(this.getDataFim()));
        parametros.put("parPeriodo", "de " + this.getDataInicio() + " a " + this.getDataFim());
        parametros.put("parTituloColuna", this.getTituloColuna());
        parametros.put("parFator", String.valueOf(this.getMultiplicador()));
        parametros.put("parAno", util.getAno(this.getDataFim()));
        parametros.put("parRodape", this.getRodape());
        parametros.put("parConfig", "");
        parametros.put("parTituloDenominador", "Idade ign ");
        parametros.put("parTituloNumerador", "Menor 1 ano");
        parametros.put("parTitulo1", "Número de casos novos de sífilis congênita em menores de 1 ano de idade");
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
    public String[] getOrdemColunas() {
        return new String[]{"ID_LOCRES", "DS_LOCRES", "ID_UFRES", "NOT_M1ANO", "NOT_IDIGN",
            "I_INCSIF", "ANO_DIAG", "DT_DIAGIN", "DT_DIAGFI", "ORIGEM"};
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("ID_LOCRES", new ColunasDbf(7));
        hashColunas.put("DS_LOCRES", new ColunasDbf(30));
        hashColunas.put("ID_UFRES", new ColunasDbf(2));
        hashColunas.put("NOT_M1ANO", new ColunasDbf(10, 0));
        hashColunas.put("NOT_IDIGN", new ColunasDbf(10, 0));
        hashColunas.put("I_INCSIF", new ColunasDbf(6, 0));
        hashColunas.put("ANO_DIAG", new ColunasDbf(4, 0));
        hashColunas.put("DT_DIAGIN", new ColunasDbf(10));
        hashColunas.put("DT_DIAGFI", new ColunasDbf(10));
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
            rowData[9] = "SIFILIS-SINANNET";
            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/sifilisCongenita.jasper";
    }
}
