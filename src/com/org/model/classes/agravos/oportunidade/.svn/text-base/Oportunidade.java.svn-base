package com.org.model.classes.agravos.oportunidade;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.org.bd.DBFUtil;
import com.org.model.classes.Agravo;
import com.org.negocio.Municipio;
import com.org.negocio.Util;
import com.org.view.Master;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
public class Oportunidade extends Agravo {

    private String sqlCalculoAgravos180dias;
    private String sqlCalculoAgravos60dias;
    private String sqlCalculoAgravosMalaria60dias;
    private String sqlCalculoAgravoEspecifico;
    private String naoEncerrado;
    private String inoportuno;
    private String oportuno;
    private String dataInvalida;
    private List<String> agravosValidos = new ArrayList<String>();
    private List<CasoOportunidade> listagemCasos = new ArrayList<CasoOportunidade>();
    private boolean temListagem = false;
    private List<Municipio> municipios = new ArrayList<Municipio>();

    @Override
    public void init(String tipoBanco) {
        if (tipoBanco.equals("INTERBASE")) {
        } else {
            this.setSqlCalculoAgravoEspecifico(
                    "select (dt_encerramento-dt_notificacao) as  diff,dt_encerramento,tp_classificacao_final,dt_notificacao,tp_classificacao_final," +
                    "no_agravo as co_cid,nu_notificacao,co_cnes as co_unidade_notificacao,co_municipio_notificacao,co_municipio_notificacao,no_municipio " +
                    "from dbsinan.tb_notificacao as t1 inner join dbsinan.tb_agravo t2 on t1.co_cid=t2.co_cid inner join dbgeral.tb_municipio t3 on t1.co_municipio_notificacao=t3.co_municipio_ibge " +
                    " inner join dblocalidade.tb_estabelecimento_saude us on t1.co_unidade_notificacao = us.co_estabelecimento  where " +
                    "(dt_notificacao BETWEEN ?  AND ?) and co_uf_residencia= ? and t1.co_cid = ? and" +
                    "(tp_duplicidade <> 2 or tp_duplicidade is null) ");
            this.setSqlCalculoAgravos180dias(
                    "select (dt_encerramento-dt_notificacao) as  diff,dt_encerramento,tp_classificacao_final,dt_notificacao,tp_classificacao_final," +
                    "no_agravo as co_cid,nu_notificacao,co_cnes as co_unidade_notificacao,co_municipio_notificacao,co_municipio_notificacao,no_municipio " +
                    "from dbsinan.tb_notificacao as t1 inner join dbsinan.tb_agravo t2 on t1.co_cid=t2.co_cid inner join dbgeral.tb_municipio t3 on t1.co_municipio_notificacao=t3.co_municipio_ibge " +
                    " inner join dblocalidade.tb_estabelecimento_saude us on t1.co_unidade_notificacao = us.co_estabelecimento  where  " +
                    "t1.co_cid in( 'B19','B55.1','P35.0' ) and  " +
                    "(dt_notificacao BETWEEN ?  AND ?) and co_uf_residencia= ? and " +
                    "(tp_duplicidade <> 2 or tp_duplicidade is null) ");
            this.setSqlCalculoAgravos60dias(
                    "select (dt_encerramento-dt_notificacao) as  diff,dt_encerramento,tp_classificacao_final,tp_suspeita,dt_notificacao,tp_classificacao_final," +
                    "no_agravo as co_cid,nu_notificacao,co_cnes as co_unidade_notificacao,co_municipio_notificacao,co_municipio_notificacao,no_municipio " +
                    "from dbsinan.tb_notificacao as t1 inner join dbsinan.tb_agravo t2 on t1.co_cid=t2.co_cid inner join dbgeral.tb_municipio t3 on t1.co_municipio_notificacao=t3.co_municipio_ibge  " +
                    " inner join dblocalidade.tb_estabelecimento_saude us on t1.co_unidade_notificacao = us.co_estabelecimento  where  " +
                    "(t1.co_cid in('A35','B09','B57.1','A00.9','A37.9','A36.9','A95.9','A01.0','A27.9','A80.9','A20.9','A82.9','A33','B55.0'," +
                    "'A98.8','A77.9','G03.9','A05.1','A77.9','A92.3') or (t1.co_cid='A90' and tp_classificacao_final in(2,3,4))) and  " +
                    "(dt_notificacao BETWEEN ?  AND ?) and co_uf_residencia= ? and (tp_duplicidade <> 2 or tp_duplicidade is null)");
            this.setSqlCalculoAgravosMalaria60dias(
                    "select (dt_encerramento-dt_notificacao) as  diff,dt_encerramento,tp_classificacao_final,tp_suspeita,dt_notificacao,tp_classificacao_final," +
                    "no_agravo as co_cid,nu_notificacao,co_cnes as co_unidade_notificacao,co_municipio_notificacao,co_municipio_notificacao,no_municipio " +
                    "from dbsinan.tb_notificacao as t1 inner join dbsinan.tb_agravo t2 on t1.co_cid=t2.co_cid inner join dbgeral.tb_municipio t3 on t1.co_municipio_notificacao=t3.co_municipio_ibge  " +
                    " inner join dblocalidade.tb_estabelecimento_saude us on t1.co_unidade_notificacao = us.co_estabelecimento  where  " +
                    "(t1.co_cid in('A35','B09','B57.1','A00.9','A37.9','A36.9','A95.9','A01.0','A27.9','A80.9','A20.9','A82.9','A33','B55.0'," +
                    "'A98.8','A77.9','G03.9','A05.1','A77.9','A92.3','B54') or (t1.co_cid='A90' and tp_classificacao_final in(2,3,4))) and  " +
                    "(dt_notificacao BETWEEN ?  AND ?) and co_uf_residencia= ? and (tp_duplicidade <> 2 or tp_duplicidade is null)");
        }
        this.setSqlNumeradorCompletitude("");
        this.setSqlNumeradorBeanMunicipios(this.getSqlNumeradorMunicipioEspecifico());
        this.setSqlDenominadorBeanMunicipios(this.getSqlDenominadorMunicipioEspecifico());
        this.setTextoCompletitude("");
        this.setMultiplicador(100);
        this.setTipo("Oportunidade");
        this.setTitulo1("Proporção de doenças exantemáticas investigados oportunamente");
        this.setTituloColuna("Proporção");
        this.setRodape("Numerador: Total de casos suspeitos de sarampo e rubéola investigados em até 48 horas após a notificação, residentes em determinado local e notificados em determinado período \n" +
                "Denominador: Total de casos suspeitos de sarampo e rubéola,  residentes em determinado local e notificados em determinado período ");
    }

    /**
     *
     * @param reader
     * @param parametros
     * @return uma lista dos resultados com beans
     * @throws ParseException
     * @throws DBFException
     * CALCULO A PARTIR DA BASE DBF - POR ESTADO OU MUNICIPIO
     */
    private List calculaEstadoMunicipio(DBFReader reader, Map parametros) throws ParseException, DBFException {
        //buscar os municipios que vao para o resultado
        HashMap<String, OportunidadeAgravo> municipiosBeans = new HashMap<String, OportunidadeAgravo>();
        DBFUtil utilDbf = new DBFUtil();
        CasoOportunidade casoListado = null;
        //busca o parametro agravo
        String agravoSelecionado = parametros.get("parAgravo").toString();
        Agravo agravodbf = new Agravo();

        //inicia variavel com agravos válidos
        iniciaAgravosValidos();
        //verificar se a calculo é para ser listado por agravo ou por municipio
        if (parametros.get("parNomeMunicipio").toString().equals("Todos Municípios")) {
            municipiosBeans = populaMunicipiosBeans(parametros.get("parSgUf").toString());
        } else {
            municipiosBeans = populaAgravosBeans(agravoSelecionado);
        }


        //inicia o calculo

        Object[] rowObjects;
        java.sql.Date dtNotificacao, dtEncerramento;
        String agravo;
        OportunidadeAgravo municipioResidencia;
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim60 = (String) parametros.get("parDataFim60");
        String dataFim180 = (String) parametros.get("parDataFim180");
        String ufResidencia = (String) parametros.get("parUf");
        String ufResidenciaDbf = "";
        String parMunicipioResidencia = (String) parametros.get("parMunicipio");
        String parMunicipioResidenciaDbf = "";
        int i = 1;

        //verifica se o agravo é malaria e se o ano é 2008. se for, parar por aqui
        int anoAvaliadoMalaria = Integer.parseInt(parametros.get("parDataInicio").toString().split("-")[0]);
        if (anoAvaliadoMalaria == 2008 && agravoSelecionado.equals("B54")) {
            Master.mensagem("Malária está disponível a partir do ano de 2009");
            return null;
        }
        try {
            utilDbf.mapearPosicoes(reader);
            double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
            while ((rowObjects = reader.nextRecord()) != null) {
                //cálculo da taxa estadual
                //verifica a uf de residencia
                if (utilDbf.getString(rowObjects, "SG_UF") != null) {

                    dtNotificacao = utilDbf.getDate(rowObjects, "DT_NOTIFIC");
                    dtEncerramento = utilDbf.getDate(rowObjects, "DT_ENCERRA");
                    agravo = utilDbf.getString(rowObjects, "ID_AGRAVO");
                    ufResidenciaDbf = utilDbf.getString(rowObjects, "SG_UF");

                    //verifica se tem o parametro de municipio de residencia
                    parMunicipioResidenciaDbf = utilDbf.getString(rowObjects, "ID_MN_RESI");
                    if (agravo.equals("B09")) {
                        int suspeita = utilDbf.getInt(rowObjects, "CS_SUSPEIT");
                        if (suspeita == 1) {
                            agravo = "B091";
                        } else {
                            agravo = "B092";
                        }
                    }
                    //verifica se existe a referencia do municipio no bean
                    if (parametros.get("parNomeMunicipio").toString().equals("Todos Municípios")) {
                        municipioResidencia = municipiosBeans.get(parMunicipioResidenciaDbf);
                    }else{
                        municipioResidencia = municipiosBeans.get(agravo);
                    }
                    
                    boolean continuaCalculo = true;

                    //verifica se foi selecionado agravo = todos e se o ano de avaliacao = 2008 e se o agravo é malaria
                    if (agravoSelecionado.equals("TODOS") && anoAvaliadoMalaria == 2008 && agravo.equals("B54")) {
                        continuaCalculo = false;
                    }
                    //verifica se a uf de residencia é igual a selecionada
                    if (ufResidenciaDbf == null) {
                        continuaCalculo = false;
                    } else {
                        if (!ufResidencia.equals(ufResidenciaDbf)) {
                            continuaCalculo = false;
                        }
                    }

                    //verifica se o municipio de residencia é igual a selecionada, se foi selecionada
                    if (parMunicipioResidencia != null) {
                        if (parMunicipioResidenciaDbf == null) {
                            continuaCalculo = false;
                        } else {
                            if (!parMunicipioResidencia.equals(parMunicipioResidenciaDbf)) {
                                continuaCalculo = false;
                            }
                        }

                    }

                    //se o agravo for dengue, verifica classificacao final
                    if (agravo.equals("A90")) {
                        String classificacaoFinal = utilDbf.getString(rowObjects, "CLASSI_FIN", 1);
                        if (classificacaoFinal == null) {
                            continuaCalculo = false;
                        } else {
                            int cf = Integer.parseInt(classificacaoFinal);
                            if (cf < 2 || cf > 4) {
                                continuaCalculo = false;
                            }
                        }
                    }
                    if (municipioResidencia != null && verificaAgravoOportuno(agravo) && continuaCalculo) {

                        //verifica qual agravo para designar a dataFinal de avaliacao
                        String dataFim = dataFim60;
                        if (agravo.equals("B19") || agravo.equals("P350") || agravo.equals("B551")) {
                            dataFim = dataFim180;
                        }
                        if (agravodbf.isBetweenDates(dtNotificacao, dataInicio, dataFim)) {
                            casoListado = new CasoOportunidade();
                            //verifica se a data de encerramento é nula
                            if (dtEncerramento == null) {
                                //se o usuario marcou que quer a listagem, armazenar o caso no array listagemCasos
                                if (temListagem) {
                                    casoListado.setAgravo(buscaNomeAgravo(agravo));
                                    casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                                    casoListado.setIdUnidade(utilDbf.getString(rowObjects, "ID_UNIDADE"));
                                    casoListado.setNumNotificacao(utilDbf.getString(rowObjects, "NU_NOTIFIC"));
                                    casoListado.setIdMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                    String nomeMunicipio = retornaNome(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                    if (nomeMunicipio.equals("")) {
                                        Municipio municipioNovo = new Municipio();
                                        municipioNovo.setCodMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                        municipioNovo.setNmMunicipio(agravodbf.getNomeMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP")));
                                        casoListado.setNmMunicipio(municipioNovo.getNmMunicipio());
                                        getMunicipios().add(municipioNovo);
                                    } else {
                                        casoListado.setNmMunicipio(nomeMunicipio);
                                    }
                                    casoListado.setSituacao("Não Encerrado");
                                    this.getListagemCasos().add(casoListado);
                                }
                                municipioResidencia.setQtdNaoEncerrado(Integer.valueOf(municipioResidencia.getQtdNaoEncerrado().intValue() + 1));
                                municipioResidencia.setTotal(Integer.valueOf(municipioResidencia.getTotal().intValue() + 1));
                            } else {
                                int diferenca = this.dataDiff(dtNotificacao, dtEncerramento);
                                int diferencaOportuna = retornaDiferenca(agravo);
                                String situacao = classificaNotificacao(diferenca, diferencaOportuna, municipioResidencia, utilDbf.getInt(rowObjects, "CLASSI_FIN"));
                                if (temListagem && !situacao.equals("Oportuno") && !situacao.equals("Inoportuno com outras categorias")) {
                                    casoListado.setAgravo(buscaNomeAgravo(agravo));
                                    casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                                    casoListado.setIdUnidade(utilDbf.getString(rowObjects, "ID_UNIDADE"));
                                    casoListado.setNumNotificacao(utilDbf.getString(rowObjects, "NU_NOTIFIC"));
                                    casoListado.setIdMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                    String nomeMunicipio = retornaNome(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                    if (nomeMunicipio.equals("")) {
                                        Municipio municipioNovo = new Municipio();
                                        municipioNovo.setCodMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                        municipioNovo.setNmMunicipio(agravodbf.getNomeMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP")));
                                        casoListado.setNmMunicipio(municipioNovo.getNmMunicipio());
                                        getMunicipios().add(municipioNovo);
                                    } else {
                                        casoListado.setNmMunicipio(nomeMunicipio);
                                    }
                                    casoListado.setSituacao(situacao);
                                    this.getListagemCasos().add(casoListado);
                                }
                            }
                        }
                    }
                }
                getLabel().setText("Registros: " + i + " de " + TotalRegistros);
                i++;
            }

        } catch (DBFException ex) {
            Master.mensagem("Erro:\n" + ex);
        }
        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        List<OportunidadeAgravo> beans = new ArrayList();
        Collection<OportunidadeAgravo> municipioBean = municipiosBeans.values();

        for (Iterator<OportunidadeAgravo> it = municipioBean.iterator(); it.hasNext();) {
            OportunidadeAgravo agravoDBF = it.next();
            if(agravoDBF.getTotal().intValue() > 0)
                beans.add(agravoDBF);
        }
        Collections.sort(beans, new BeanComparator("nmAgravo"));

        Collections.sort(this.getListagemCasos(), new BeanComparator("situacao"));
        Collections.sort(this.getListagemCasos(), new BeanComparator("nmMunicipio"));

        //calcular o total
        OportunidadeAgravo agravoBean = new OportunidadeAgravo();
        agravoBean.setNmAgravo("TOTAL");
        for (i = 0; i < beans.size(); i++) {
            agravoBean.setQtdOportuno(Integer.valueOf(beans.get(i).getQtdOportuno().intValue() + agravoBean.getQtdOportuno().intValue()));
            agravoBean.setQtdInoportuno(Integer.valueOf(beans.get(i).getQtdInoportuno().intValue() + agravoBean.getQtdInoportuno().intValue()));
            agravoBean.setQtdInoportunoOutras(Integer.valueOf(beans.get(i).getQtdInoportunoOutras().intValue() + agravoBean.getQtdInoportunoOutras().intValue()));
            agravoBean.setQtdNaoEncerrado(Integer.valueOf(beans.get(i).getQtdNaoEncerrado().intValue() + agravoBean.getQtdNaoEncerrado().intValue()));
            agravoBean.setQtdDataInvalida(Integer.valueOf(beans.get(i).getQtdDataInvalida().intValue() + agravoBean.getQtdDataInvalida().intValue()));
            agravoBean.setTotal(Integer.valueOf(beans.get(i).getTotal().intValue() + agravoBean.getTotal().intValue()));
        }
        beans.add(agravoBean);
        return beans;
    }

    /**
     *
     * @param reader
     * @param parametros
     * @return uma lista dos resultados com beans
     * @throws ParseException
     * @throws DBFException
     * CALCULO A PARTIR DA BASE DBF - BRASIL
     */
    public List getCalculaResultado(DBFReader reader, Map parametros) throws ParseException, DBFException {

        //verifica se o calculo é o brasil
        if (!parametros.get("parUf").equals("brasil")) {
            return calculaEstadoMunicipio(reader, parametros);
        }
        CasoOportunidade casoListado = null;
        HashMap<String, OportunidadeAgravo> municipiosBeans = new HashMap<String, OportunidadeAgravo>();
        DBFUtil utilDbf = new DBFUtil();
        Agravo agravodbf = new Agravo();
        Object[] rowObjects;
        java.sql.Date dtNotificacao, dtEncerramento;
        String agravo;
        OportunidadeAgravo municipioResidencia;
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim60 = (String) parametros.get("parDataFim60");
        String dataFim180 = (String) parametros.get("parDataFim180");
        int i = 1;

        //busca o parametro agravo
        String agravoSelecionado = parametros.get("parAgravo").toString();

        //buscar os municipios que vao para o resultado
        municipiosBeans = populaUfsBeansOportunidade();

        //inicia variavel com agravos válidos
        iniciaAgravosValidos();

        //inicia o calculo

        try {
            utilDbf.mapearPosicoes(reader);
            int TotalRegistrosInt = reader.getRecordCount();
            while ((rowObjects = reader.nextRecord()) != null) {

                //verifica a uf de residencia é diferente de null
                if (utilDbf.getString(rowObjects, "SG_UF") != null) {

                    //verifica se existe a referencia do municipio no bean
                    municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, "SG_UF"));

                    //busca data de notificacao e encerramento
                    dtNotificacao = utilDbf.getDate(rowObjects, "DT_NOTIFIC");
                    dtEncerramento = utilDbf.getDate(rowObjects, "DT_ENCERRA");
                    boolean continuaCalculo = true;
                    agravo = utilDbf.getString(rowObjects, "ID_AGRAVO");

                    if (agravo.equals("B09")) {
                        int suspeita = utilDbf.getInt(rowObjects, "CS_SUSPEIT");
                        if (suspeita == 1) {
                            agravo = "B091";
                        } else {
                            agravo = "B092";
                        }
                    }


                    //se o agravo for dengue, verifica classificacao final
                    if (agravo.equals("A90")) {
                        String classificacaoFinal = utilDbf.getString(rowObjects, "CLASSI_FIN", 1);
                        if (classificacaoFinal == null) {
                            continuaCalculo = false;
                        } else {
                            int cf = Integer.parseInt(classificacaoFinal);
                            if (cf < 2 || cf > 4) {
                                continuaCalculo = false;
                            }
                        }
                    }

                    //verifica se foi selecionado um agravo especifico
                    if (!agravoSelecionado.equals("TODOS") && !agravoSelecionado.equals(agravo)) {
                        continuaCalculo = false;
                    }

                    int anoAvaliadoMalaria = Integer.parseInt(parametros.get("parDataInicio").toString().split("-")[0]);
                    if (agravoSelecionado.equals("TODOS") && anoAvaliadoMalaria == 2008 && agravo.equals("B54")) {
                        continuaCalculo = false;
                    }
                    if (anoAvaliadoMalaria == 2008 && agravoSelecionado.equals("B54")) {
                        Master.mensagem("Malária está disponível a partir do ano de 2009");
                        return null;
                    }
                    if (municipioResidencia != null && verificaAgravoOportuno(agravo) && continuaCalculo) {
                        //verifica qual agravo para designar a dataFinal de avaliacao
                        String dataFim = dataFim60;
                        if (agravo.equals("B19") || agravo.equals("P350") || agravo.equals("B551")) {
                            dataFim = dataFim180;
                        }
                        if (agravodbf.isBetweenDates(dtNotificacao, dataInicio, dataFim)) {
                            casoListado = new CasoOportunidade();
                            //verifica se a data de encerramento é nula
                            if (dtEncerramento == null) {
                                //se o usuario marcou que quer a listagem, armazenar o caso no array listagemCasos
                                if (temListagem) {
                                    casoListado.setAgravo(buscaNomeAgravo(agravo));
                                    casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                                    casoListado.setIdUnidade(utilDbf.getString(rowObjects, "ID_UNIDADE"));
                                    casoListado.setNumNotificacao(utilDbf.getString(rowObjects, "NU_NOTIFIC"));
                                    casoListado.setIdMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                    String nomeMunicipio = retornaNome(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                    if (nomeMunicipio.equals("")) {
                                        Municipio municipioNovo = new Municipio();
                                        municipioNovo.setCodMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                        municipioNovo.setNmMunicipio(agravodbf.getNomeMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP")));
                                        casoListado.setNmMunicipio(municipioNovo.getNmMunicipio());
                                        getMunicipios().add(municipioNovo);
                                    } else {
                                        casoListado.setNmMunicipio(nomeMunicipio);
                                    }
                                    casoListado.setSituacao("Não Encerrado");
                                    this.getListagemCasos().add(casoListado);
                                }
                                municipioResidencia.setQtdNaoEncerrado(Integer.valueOf(municipioResidencia.getQtdNaoEncerrado().intValue() + 1));
                                municipioResidencia.setTotal(Integer.valueOf(municipioResidencia.getTotal().intValue() + 1));
                            } else {
                                int diferenca = this.dataDiff(dtNotificacao, dtEncerramento);
                                int diferencaOportuna = retornaDiferenca(agravo);
                                String situacao = classificaNotificacao(diferenca, diferencaOportuna, municipioResidencia, utilDbf.getInt(rowObjects, "CLASSI_FIN"));
                                if (situacao.equals("Data Inválida")) {
                                    System.out.println("dt_notificacao:" + formataData(dtNotificacao.toString()) + "\nnum notificacao:" + utilDbf.getString(rowObjects, "NU_NOTIFIC") + "\nmunicipio:" + utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                }
                                if (temListagem && !situacao.equals("Oportuno") && !situacao.equals("Inoportuno com outras categorias")) {
                                    casoListado.setAgravo(buscaNomeAgravo(agravo));
                                    casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                                    casoListado.setIdUnidade(utilDbf.getString(rowObjects, "ID_UNIDADE"));
                                    casoListado.setNumNotificacao(utilDbf.getString(rowObjects, "NU_NOTIFIC"));
                                    casoListado.setIdMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                    String nomeMunicipio = retornaNome(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                    if (nomeMunicipio.equals("")) {
                                        Municipio municipioNovo = new Municipio();
                                        municipioNovo.setCodMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                        municipioNovo.setNmMunicipio(agravodbf.getNomeMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP")));
                                        casoListado.setNmMunicipio(municipioNovo.getNmMunicipio());
                                        getMunicipios().add(municipioNovo);
                                    } else {
                                        casoListado.setNmMunicipio(nomeMunicipio);
                                    }
                                    casoListado.setSituacao(situacao);
                                    this.getListagemCasos().add(casoListado);
                                }
                            }
                        }
                    }
                }
                getLabel().setText("Registros: " + i + " de " + TotalRegistrosInt);
                i++;
            }

        } catch (DBFException ex) {
            Master.mensagem("Erro:\n" + ex);
        }
        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        List<OportunidadeAgravo> beans = new ArrayList();
        Collection<OportunidadeAgravo> municipioBean = municipiosBeans.values();

        for (Iterator<OportunidadeAgravo> it = municipioBean.iterator(); it.hasNext();) {
            OportunidadeAgravo agravoDBF = it.next();
            beans.add(agravoDBF);
        }
        Collections.sort(beans, new BeanComparator("nmAgravo"));
        Collections.sort(beans, new BeanComparator("regiao"));

        Collections.sort(this.getListagemCasos(), new BeanComparator("situacao"));
        Collections.sort(this.getListagemCasos(), new BeanComparator("nmMunicipio"));
        //calcular o total
        OportunidadeAgravo agravoBean = new OportunidadeAgravo();
        agravoBean.setNmAgravo("TOTAL");
        for (i = 0; i < beans.size(); i++) {
            agravoBean.setQtdOportuno(Integer.valueOf(beans.get(i).getQtdOportuno().intValue() + agravoBean.getQtdOportuno().intValue()));
            agravoBean.setQtdInoportuno(Integer.valueOf(beans.get(i).getQtdInoportuno().intValue() + agravoBean.getQtdInoportuno().intValue()));
            agravoBean.setQtdInoportunoOutras(Integer.valueOf(beans.get(i).getQtdInoportunoOutras().intValue() + agravoBean.getQtdInoportunoOutras().intValue()));
            agravoBean.setQtdNaoEncerrado(Integer.valueOf(beans.get(i).getQtdNaoEncerrado().intValue() + agravoBean.getQtdNaoEncerrado().intValue()));
            agravoBean.setQtdDataInvalida(Integer.valueOf(beans.get(i).getQtdDataInvalida().intValue() + agravoBean.getQtdDataInvalida().intValue()));
            agravoBean.setTotal(Integer.valueOf(beans.get(i).getTotal().intValue() + agravoBean.getTotal().intValue()));
        }
        beans.add(agravoBean);
        return beans;
    }

    /**
     *
     * @param con
     * @param parametros
     * @return uma lista dos resultados com beans
     * @throws SQLException
     * CALCULO A PARTIR DA BASE POSTGRES
     */
    public List getCalculaResultado(Connection con, Map parametros) throws SQLException, ParseException {
        ResultSet rs2;
        PreparedStatement stm2;
        String agravo = "";
        String sql;
        OportunidadeAgravo agravoBean = null;
        List<OportunidadeAgravo> beans = new ArrayList();
        CasoOportunidade casoListado = null;
        boolean prosseguir = true;
        //verifica se vai calcular por municipio
        if (parametros.get("parNomeMunicipio").toString().equals("Todos Municípios")) {
            return getCalculaResultadoPorMunicipio(con, parametros);
        }
        //calcular os agravos de 180 dias
        //veririca se tem o municipio como parametro
        String municipio = "";
        if (parametros.get("parMunicipio") != null) {
            municipio = " and co_municipio_residencia = ? ";
        }

        //busca o parametro agravo
        String agravoSelecionado = parametros.get("parAgravo").toString();
        if (agravoSelecionado.equals("TODOS") || agravoSelecionado.equals("B19") || agravoSelecionado.equals("B551") ||
                agravoSelecionado.equals("P350")) {
            if (agravoSelecionado.equals("TODOS")) {
                sql = this.getSqlCalculoAgravos180dias() + municipio + " order by co_cid,tp_suspeita";
            } else {
                sql = this.getSqlCalculoAgravoEspecifico() + municipio + " order by co_cid,tp_suspeita";
                agravoSelecionado = converteCodigoParaCompleto(agravoSelecionado);
                prosseguir = false;
            }
            stm2 = con.prepareStatement(sql);
            //passa data inicial
            stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
            //passa data final
            stm2.setDate(2, this.transformaDate(parametros.get("parDataFim180").toString()));
            //parametro uf
            stm2.setString(3, parametros.get("parUf").toString());
            //paremetro municipio
            if (!municipio.equals("")) {
                if (!agravoSelecionado.equals("TODOS")) {
                    stm2.setString(4, agravoSelecionado);
                    stm2.setString(5, parametros.get("parMunicipio").toString());
                } else {
                    stm2.setString(4, parametros.get("parMunicipio").toString());
                }

            } else if (!agravoSelecionado.equals("TODOS")) {
                stm2.setString(4, agravoSelecionado);
            }
            rs2 = stm2.executeQuery();

            //looping para somar a quantidade de cada agravo
            while (rs2.next()) {
                String codCid = rs2.getString("co_cid");
                //se o agravo for diferente, comecar a calcular
                if (!agravo.equals(codCid)) {
                    //se agravo for diferente do inicio, inserir na lista para retornar no final
                    agravoBean = new OportunidadeAgravo();
                    agravoBean.setNmAgravo(codCid);
                    beans.add(agravoBean);
                    agravo = codCid;
                }
                //verifica se tem data de encerramento
                casoListado = new CasoOportunidade();
                if (rs2.getDate("dt_encerramento") == null) {
                    //se o usuario marcou que quer a listagem, armazenar o caso no array listagemCasos
                    if (temListagem) {
                        casoListado.setAgravo(codCid);
                        casoListado.setDtNotificacao(formataData(rs2.getDate("dt_notificacao").toString()));
                        casoListado.setIdUnidade(rs2.getString("co_unidade_notificacao"));
                        casoListado.setNumNotificacao(rs2.getString("nu_notificacao"));
                        casoListado.setIdMunicipio(rs2.getString("co_municipio_notificacao"));
                        casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                        casoListado.setSituacao("Não Encerrado");
                        this.getListagemCasos().add(casoListado);
                    }
                    agravoBean.setQtdNaoEncerrado(Integer.valueOf(agravoBean.getQtdNaoEncerrado().intValue() + 1));
                    agravoBean.setTotal(Integer.valueOf(agravoBean.getTotal().intValue() + 1));

                } else {
                    String situacao = this.classificaNotificacao(rs2.getInt("diff"), 180, agravoBean, rs2.getInt("tp_classificacao_final"));
                    if (temListagem && !situacao.equals("Oportuno")&& !situacao.equals("Inoportuno com outras categorias")) {
                        casoListado.setAgravo(codCid);
                        casoListado.setDtNotificacao(formataData(rs2.getDate("dt_notificacao").toString()));
                        casoListado.setIdUnidade(rs2.getString("co_unidade_notificacao"));
                        casoListado.setNumNotificacao(rs2.getString("nu_notificacao"));
                        casoListado.setIdMunicipio(rs2.getString("co_municipio_notificacao"));
                        casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                        casoListado.setSituacao(situacao);
                        this.getListagemCasos().add(casoListado);
                    }
                }
            }
        }

        if (prosseguir) {
            //pega ano avaliado
            int anoAvaliadoMalaria = Integer.parseInt(parametros.get("parDataInicio").toString().split("-")[0]);
            if (agravoSelecionado.equals("TODOS")) {
                //se o ano selecionado for maior que 2008, pegar sql com malaria
                if (anoAvaliadoMalaria > 2008) {
                    stm2 = con.prepareStatement(this.getSqlCalculoAgravosMalaria60dias() + municipio + " order by co_cid,tp_suspeita");
                } else //calcular os agravos de 60 dias
                {
                    stm2 = con.prepareStatement(this.getSqlCalculoAgravos60dias() + municipio + " order by co_cid,tp_suspeita");

                }
            } else {
                if (anoAvaliadoMalaria == 2008 && agravoSelecionado.equals("B54")) {
                    Master.mensagem("Malária está disponível a partir do ano de 2009");
                    return null;
                }
                agravoSelecionado = converteCodigoParaCompleto(agravoSelecionado);
                stm2 = con.prepareStatement(this.getSqlCalculoAgravoEspecifico() + municipio + " order by co_cid,tp_suspeita");
            }

            //passa data inicial
            stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
            //passa data final
            stm2.setDate(2, this.transformaDate(parametros.get("parDataFim60").toString()));
            //parametro uf
            stm2.setString(3, parametros.get("parUf").toString());
            //paremetro municipio
            if (!municipio.equals("")) {
                if (!agravoSelecionado.equals("TODOS")) {
                    stm2.setString(4, agravoSelecionado);
                    stm2.setString(5, parametros.get("parMunicipio").toString());
                } else {
                    stm2.setString(4, parametros.get("parMunicipio").toString());
                }
            } else if (!agravoSelecionado.equals("TODOS")) {
                stm2.setString(4, agravoSelecionado);
            }
            rs2 = stm2.executeQuery();
            agravo = "";
            agravoBean = null;
            //looping para somar a quantidade de cada agravo
            while (rs2.next()) {
                String codCid = rs2.getString("co_cid");
                //se o agravo for diferente, comecar a calcular
                //se for exantemtica, verificar tp_suspeita. Se for 1 = sarampo || 2 = rubéola
                if (codCid.equals("DOENCAS EXANTEMATICAS")) {
                    if (rs2.getInt("tp_suspeita") == 1) {
                        codCid = "SARAMPO";
                    } else {
                        codCid = "RUBEOLA";
                    }
                }
                if (!agravo.equals(codCid)) {
                    //se agravo for diferente do inicio, inserir na lista para retornar no final
                    agravoBean = new OportunidadeAgravo();
                    agravoBean.setNmAgravo(codCid);
                    beans.add(agravoBean);
                    agravo = codCid;
                }
                //verifica se tem data de encerramento
                casoListado = new CasoOportunidade();
                //verifica se tem data de encerramento
                if (rs2.getDate("dt_encerramento") == null) {
                    //se o usuario marcou que quer a listagem, armazenar o caso no array listagemCasos
                    if (temListagem) {
                        casoListado.setAgravo(codCid);
                        casoListado.setDtNotificacao(formataData(rs2.getDate("dt_notificacao").toString()));
                        casoListado.setIdUnidade(rs2.getString("co_unidade_notificacao"));
                        casoListado.setNumNotificacao(rs2.getString("nu_notificacao"));
                        casoListado.setIdMunicipio(rs2.getString("co_municipio_notificacao"));
                        casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                        casoListado.setSituacao("Não Encerrado");
                        this.getListagemCasos().add(casoListado);
                    }
                    agravoBean.setQtdNaoEncerrado(Integer.valueOf(agravoBean.getQtdNaoEncerrado().intValue() + 1));
                    agravoBean.setTotal(Integer.valueOf(agravoBean.getTotal().intValue() + 1));
                } else {
                    String situacao = this.classificaNotificacao(rs2.getInt("diff"), 60, agravoBean, rs2.getInt("tp_classificacao_final"));
                    if (temListagem && !situacao.equals("Oportuno") && !situacao.equals("Inoportuno com outras categorias")) {
                        casoListado.setAgravo(codCid);
                        casoListado.setDtNotificacao(formataData(rs2.getDate("dt_notificacao").toString()));
                        casoListado.setIdUnidade(rs2.getString("co_unidade_notificacao"));
                        casoListado.setNumNotificacao(rs2.getString("nu_notificacao"));
                        casoListado.setIdMunicipio(rs2.getString("co_municipio_notificacao"));
                        casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                        casoListado.setSituacao(situacao);
                        this.getListagemCasos().add(casoListado);
                    }
                }
            }
        }

        //calcular o total
        agravoBean = new OportunidadeAgravo();
        agravoBean.setNmAgravo("TOTAL");
        for (int i = 0; i < beans.size(); i++) {
            agravoBean.setQtdOportuno(Integer.valueOf(beans.get(i).getQtdOportuno().intValue() + agravoBean.getQtdOportuno().intValue()));
            agravoBean.setQtdInoportuno(Integer.valueOf(beans.get(i).getQtdInoportuno().intValue() + agravoBean.getQtdInoportuno().intValue()));
            agravoBean.setQtdInoportunoOutras(Integer.valueOf(beans.get(i).getQtdInoportunoOutras().intValue() + agravoBean.getQtdInoportunoOutras().intValue()));
            agravoBean.setQtdNaoEncerrado(Integer.valueOf(beans.get(i).getQtdNaoEncerrado().intValue() + agravoBean.getQtdNaoEncerrado().intValue()));
            agravoBean.setQtdDataInvalida(Integer.valueOf(beans.get(i).getQtdDataInvalida().intValue() + agravoBean.getQtdDataInvalida().intValue()));
            agravoBean.setTotal(Integer.valueOf(beans.get(i).getTotal().intValue() + agravoBean.getTotal().intValue()));
        }
        beans.add(agravoBean);
        Collections.sort(beans, new BeanComparator("nmAgravo"));
        Collections.sort(this.getListagemCasos(), new BeanComparator("situacao"));
        Collections.sort(this.getListagemCasos(), new BeanComparator("nmMunicipio"));
        return beans;
    }

    /**
     *
     * @param con
     * @param parametros
     * @return uma lista dos resultados com beans
     * @throws SQLException
     * CALCULO A PARTIR DA BASE POSTGRES - USADO PARA CALCULAR TODOS OS MUNICIPIOS
     */
    private List getCalculaResultadoPorMunicipio(Connection con, Map parametros) throws SQLException {
        String sql;
        String municipio, noMunicipio = null;
        java.sql.Statement stm = con.createStatement();
        PreparedStatement stm2;
        ResultSet rs, rs2;
        OportunidadeAgravo agravoBean = null;
        List<OportunidadeAgravo> beans = new ArrayList();
        CasoOportunidade casoListado = null;
        boolean prosseguir = true;
        if (parametros.get("parNomeRegional").equals("Todas Regionais") || parametros.get("parNomeRegional").equals("")) {
            sql = "select co_municipio_ibge,no_municipio from dbgeral.tb_municipio where sg_uf = '" + parametros.get("parSgUf") + "' order by no_municipio";
        } else {
            sql = "select t1.co_municipio_ibge,no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + parametros.get("parUf") + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + parametros.get("parCodRegional") + "' and no_municipio not like '%Ignorado%'  order by no_municipio";
        }
        try {
            rs = stm.executeQuery(sql);
        } catch (Exception exception) {
            if (parametros.get("parNomeRegional").equals("Todas Regionais") || parametros.get("parNomeRegional").equals("")) {
                sql = "select co_municipio_ibge,no_municipio from tb_municipio where sg_uf = '" + parametros.get("parSgUf") + "' order by no_municipio";
            } else {
                sql = "select t1.co_municipio_ibge,no_municipio from tb_municipio as t1, rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + parametros.get("parUf") + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + parametros.get("parCodRegional") + "' and no_municipio not like '%Ignorado%'  order by no_municipio";
            }
            rs = stm.executeQuery(sql);
        }
        //calcula para cada municipio
        while (rs.next()) {

            municipio = rs.getString("co_municipio_ibge");
            noMunicipio = rs.getString("no_municipio");
            getLabel().setText("Aguarde, calculado do município de " + noMunicipio);
            //adicona no list o bean do municipio
            agravoBean = new OportunidadeAgravo();
            agravoBean.setNmAgravo(noMunicipio);
            beans.add(agravoBean);
            //busca o total de encerrados, nao encerrados, data inválida e inoportunos


            //primeira busca é dos agravos de 180 dias
            //busca o parametro agravo
            String agravoSelecionado = parametros.get("parAgravo").toString();
            if (agravoSelecionado.equals("TODOS") || agravoSelecionado.equals("B19") || agravoSelecionado.equals("B551") ||
                    agravoSelecionado.equals("P350")) {
                if (agravoSelecionado.equals("TODOS")) {
                    sql = this.getSqlCalculoAgravos180dias() + " and co_municipio_residencia = ? " + " order by co_cid,tp_suspeita";
                } else {
                    sql = this.getSqlCalculoAgravoEspecifico() + " and co_municipio_residencia = ? " + " order by co_cid,tp_suspeita";
                    agravoSelecionado = converteCodigoParaCompleto(agravoSelecionado);
                    prosseguir = false;
                }
                stm2 = con.prepareStatement(sql);
                //passa data inicial
                stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                //passa data final
                stm2.setDate(2, this.transformaDate(parametros.get("parDataFim180").toString()));
                //parametro uf
                stm2.setString(3, parametros.get("parUf").toString());
                //paremetro municipio

                if (!agravoSelecionado.equals("TODOS")) {
                    stm2.setString(4, agravoSelecionado);
                    stm2.setString(5, municipio);
                } else {
                    stm2.setString(4, municipio);
                }
                rs2 = stm2.executeQuery();

                //looping para somar a quantidade de cada agravo e adicionar no municipio
                while (rs2.next()) {
                    //verifica se tem data de encerramento
                    casoListado = new CasoOportunidade();
                    if (rs2.getDate("dt_encerramento") == null) {
                        //se o usuario marcou que quer a listagem, armazenar o caso no array listagemCasos
                        if (temListagem) {
                            String codCid = rs2.getString("co_cid");
                            casoListado.setAgravo(codCid);
                            casoListado.setDtNotificacao(formataData(rs2.getDate("dt_notificacao").toString()));
                            casoListado.setIdUnidade(rs2.getString("co_unidade_notificacao"));
                            casoListado.setNumNotificacao(rs2.getString("nu_notificacao"));
                            casoListado.setIdMunicipio(rs2.getString("co_municipio_notificacao"));
                            casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                            casoListado.setSituacao("Não Encerrado");
                            this.getListagemCasos().add(casoListado);
                        }
                        agravoBean.setQtdNaoEncerrado(Integer.valueOf(agravoBean.getQtdNaoEncerrado().intValue() + 1));
                        agravoBean.setTotal(Integer.valueOf(agravoBean.getTotal().intValue() + 1));

                    } else {
                        String situacao = this.classificaNotificacao(rs2.getInt("diff"), 180, agravoBean, rs2.getInt("tp_classificacao_final"));
                        if (temListagem && !situacao.equals("Oportuno")&& !situacao.equals("Inoportuno com outras categorias")) {
                            String codCid = rs2.getString("co_cid");
                            casoListado.setAgravo(codCid);
                            casoListado.setDtNotificacao(formataData(rs2.getDate("dt_notificacao").toString()));
                            casoListado.setIdUnidade(rs2.getString("co_unidade_notificacao"));
                            casoListado.setNumNotificacao(rs2.getString("nu_notificacao"));
                            casoListado.setIdMunicipio(rs2.getString("co_municipio_notificacao"));
                            casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                            casoListado.setSituacao(situacao);
                            this.getListagemCasos().add(casoListado);
                        }
                    }
                }
            }
            //-----------------------------------------------------------------------------------------
            //calcula para os agravos de 60 dias
            //-----------------------------------------------------------------------------------------
            if (prosseguir) {
                //pega ano avaliado
                int anoAvaliadoMalaria = Integer.parseInt(parametros.get("parDataInicio").toString().split("-")[0]);
                if (agravoSelecionado.equals("TODOS")) {
                    //se o ano selecionado for maior que 2008, pegar sql com malaria
                    if (anoAvaliadoMalaria > 2008) {
                        stm2 = con.prepareStatement(this.getSqlCalculoAgravosMalaria60dias() + " and co_municipio_residencia = ? " + " order by co_cid,tp_suspeita");
                    } else //calcular os agravos de 60 dias
                    {
                        stm2 = con.prepareStatement(this.getSqlCalculoAgravos60dias() + " and co_municipio_residencia = ? " + " order by co_cid,tp_suspeita");

                    }
                } else {
                    if (anoAvaliadoMalaria == 2008 && agravoSelecionado.equals("B54")) {
                        Master.mensagem("Malária está disponível a partir do ano de 2009");
                        return null;
                    }
                    agravoSelecionado = converteCodigoParaCompleto(agravoSelecionado);
                    stm2 = con.prepareStatement(this.getSqlCalculoAgravoEspecifico() + " and co_municipio_residencia = ? " + " order by co_cid,tp_suspeita");
                }

                //passa data inicial
                stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                //passa data final
                stm2.setDate(2, this.transformaDate(parametros.get("parDataFim60").toString()));
                //parametro uf
                stm2.setString(3, parametros.get("parUf").toString());
                //paremetro municipio
                if (!agravoSelecionado.equals("TODOS")) {
                    stm2.setString(4, agravoSelecionado);
                    stm2.setString(5, municipio);
                } else {
                    stm2.setString(4, municipio);
                }

                rs2 = stm2.executeQuery();
                //looping para somar a quantidade de cada agravo
                while (rs2.next()) {
                    String codCid = rs2.getString("co_cid");
                    //se o agravo for diferente, comecar a calcular
                    //se for exantemtica, verificar tp_suspeita. Se for 1 = sarampo || 2 = rubéola
                    if (codCid.equals("DOENCAS EXANTEMATICAS")) {
                        if (rs2.getInt("tp_suspeita") == 1) {
                            codCid = "SARAMPO";
                        } else {
                            codCid = "RUBEOLA";
                        }
                    }
                    casoListado = new CasoOportunidade();
                    //verifica se tem data de encerramento
                    if (rs2.getDate("dt_encerramento") == null) {
                        //se o usuario marcou que quer a listagem, armazenar o caso no array listagemCasos
                        if (temListagem) {
                            casoListado.setAgravo(codCid);
                            casoListado.setDtNotificacao(formataData(rs2.getDate("dt_notificacao").toString()));
                            casoListado.setIdUnidade(rs2.getString("co_unidade_notificacao"));
                            casoListado.setNumNotificacao(rs2.getString("nu_notificacao"));
                            casoListado.setIdMunicipio(rs2.getString("co_municipio_notificacao"));
                            casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                            casoListado.setSituacao("Não Encerrado");
                            this.getListagemCasos().add(casoListado);
                        }
                        agravoBean.setQtdNaoEncerrado(Integer.valueOf(agravoBean.getQtdNaoEncerrado().intValue() + 1));
                        agravoBean.setTotal(Integer.valueOf(agravoBean.getTotal().intValue() + 1));
                    } else {
                        String situacao = this.classificaNotificacao(rs2.getInt("diff"), 60, agravoBean, rs2.getInt("tp_classificacao_final"));
                        if (temListagem && !situacao.equals("Oportuno") && !situacao.equals("Inoportuno com outras categorias")) {
                            casoListado.setAgravo(codCid);
                            casoListado.setDtNotificacao(formataData(rs2.getDate("dt_notificacao").toString()));
                            casoListado.setIdUnidade(rs2.getString("co_unidade_notificacao"));
                            casoListado.setNumNotificacao(rs2.getString("nu_notificacao"));
                            casoListado.setIdMunicipio(rs2.getString("co_municipio_notificacao"));
                            casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                            casoListado.setSituacao(situacao);
                            this.getListagemCasos().add(casoListado);
                        }
                    }
                }
            }
        }
        Collections.sort(beans, new BeanComparator("nmAgravo"));
        Collections.sort(this.getListagemCasos(), new BeanComparator("situacao"));
        Collections.sort(this.getListagemCasos(), new BeanComparator("nmMunicipio"));
        //calcular o total
        agravoBean = new OportunidadeAgravo();
        agravoBean.setNmAgravo("TOTAL");
        for (int i = 0; i < beans.size(); i++) {
            agravoBean.setQtdOportuno(Integer.valueOf(beans.get(i).getQtdOportuno().intValue() + agravoBean.getQtdOportuno().intValue()));
            agravoBean.setQtdInoportuno(Integer.valueOf(beans.get(i).getQtdInoportuno().intValue() + agravoBean.getQtdInoportuno().intValue()));
            agravoBean.setQtdInoportunoOutras(Integer.valueOf(beans.get(i).getQtdInoportunoOutras().intValue() + agravoBean.getQtdInoportunoOutras().intValue()));
            agravoBean.setQtdNaoEncerrado(Integer.valueOf(beans.get(i).getQtdNaoEncerrado().intValue() + agravoBean.getQtdNaoEncerrado().intValue()));
            agravoBean.setQtdDataInvalida(Integer.valueOf(beans.get(i).getQtdDataInvalida().intValue() + agravoBean.getQtdDataInvalida().intValue()));
            agravoBean.setTotal(Integer.valueOf(beans.get(i).getTotal().intValue() + agravoBean.getTotal().intValue()));
        }
        beans.add(agravoBean);
        return beans;
    }

    public String formataData(String data) {
        return data.split("-")[2] + "/" + data.split("-")[1] + "/" + data.split("-")[0];
    }

    /**
     * @return the sqlCalculoAgravos180dias
     */
    public String getSqlCalculoAgravos180dias() {
        return sqlCalculoAgravos180dias;
    }

    /**
     * @param sqlCalculoAgravos180dias the sqlCalculoAgravos180dias to set
     */
    public void setSqlCalculoAgravos180dias(String sqlCalculoAgravos180dias) {
        this.sqlCalculoAgravos180dias = sqlCalculoAgravos180dias;
    }

    /**
     * @return the sqlCalculoAgravos60dias
     */
    public String getSqlCalculoAgravos60dias() {
        return sqlCalculoAgravos60dias;
    }

    /**
     * @param sqlCalculoAgravos60dias the sqlCalculoAgravos60dias to set
     */
    public void setSqlCalculoAgravos60dias(String sqlCalculoAgravos60dias) {
        this.sqlCalculoAgravos60dias = sqlCalculoAgravos60dias;
    }

    /**
     * @return the naoEncerrado
     */
    public String getNaoEncerrado() {
        return naoEncerrado;
    }

    /**
     * @return the inoportuno
     */
    public String getInoportuno() {
        return inoportuno;
    }

    /**
     * @return the oportuno
     */
    public String getOportuno() {
        return oportuno;
    }

    /**
     * @return the dataInvalida
     */
    public String getDataInvalida() {
        return dataInvalida;
    }

    /**classifica a oportunidade da notificacao
     *
     * @param diferencaDatas
     * @param diferencaValida indica se eh um agravo de 180 ou 60 dias
     * @param agravoBean
     * retorna qual situacao foi classificada
     */
    private String classificaNotificacao(int diferencaDatas, int diferencaValida, OportunidadeAgravo agravoBean, int classificaoFinal) {
        String retorno = "";
        if (diferencaDatas < 0) {
            agravoBean.setQtdDataInvalida(Integer.valueOf(agravoBean.getQtdDataInvalida().intValue() + 1));
            retorno = "Data Inválida";
        } else {
            if (diferencaDatas > diferencaValida) {
                if (classificaoFinal == 8) {
                    agravoBean.setQtdInoportuno(Integer.valueOf(agravoBean.getQtdInoportuno().intValue() + 1));
                    retorno = "Inconclusivo";
                } else {
                    agravoBean.setQtdInoportunoOutras(Integer.valueOf(agravoBean.getQtdInoportunoOutras().intValue() + 1));
                    retorno = "Inoportuno com outras categorias";
                }
            } else {
                if (diferencaDatas <= diferencaValida) {
                    agravoBean.setQtdOportuno(Integer.valueOf(agravoBean.getQtdOportuno().intValue() + 1));
                    retorno = "Oportuno";
                }
            }
        }
        agravoBean.setTotal(Integer.valueOf(agravoBean.getTotal().intValue() + 1));
        return retorno;
    }


    public HashMap<String, OportunidadeAgravo> populaAgravosBeans(String agravoSelecionado) {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> hashAgravos = new HashMap<String, String>();
        HashMap<String, OportunidadeAgravo> agravosBeans = new HashMap<String, OportunidadeAgravo>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela

        //variavel para controlar exantematica
        int exantematica = 1;
        //busca agravos
        List<String> agravos = null;
        if (agravoSelecionado.equals("TODOS")) {
            agravos = getAgravosValidos();
        } else {
            agravos = new ArrayList<String>();
            agravos.add(agravoSelecionado);
        }
        for (int i = 0; i < agravos.size(); i++) {

            OportunidadeAgravo agravoDbf = new OportunidadeAgravo();
            if (agravos.get(i).equals("B09")) {
                agravoDbf.setCodAgravo("B09" + exantematica);
                if (exantematica == 1) {
                    agravoDbf.setNmAgravo("SARAMPO");
                    exantematica++;
                    i--;
                } else {
                    agravoDbf.setNmAgravo("RUBEOLA");
                }
            } else {
                agravoDbf.setCodAgravo(agravos.get(i));
                agravoDbf.setNmAgravo(buscaNomeAgravo(agravos.get(i)));
            }
            agravoDbf.setQtdDataInvalida(0);
            agravoDbf.setQtdInoportuno(0);
            agravoDbf.setQtdNaoEncerrado(0);
            agravoDbf.setQtdOportuno(0);
            agravoDbf.setTotal(0);
            hashAgravos.put(agravoDbf.getCodAgravo(), agravoDbf.getNmAgravo());
            agravosBeans.put(agravoDbf.getCodAgravo(), agravoDbf);
        }

        hashAgravos = this.sortHashMapByValues(hashAgravos, false);
        Set<String> agravoKeys = hashAgravos.keySet();
        HashMap<String, OportunidadeAgravo> agravosBeansRetorno = new HashMap<String, OportunidadeAgravo>();
        Iterator valueIt = agravoKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            agravosBeansRetorno.put(key, agravosBeans.get(key));
        }
        return agravosBeansRetorno;
    }

    public String buscaNomeAgravo(String codAgravo) {
        String retorno = "";
        if (codAgravo.equals("A90")) {
            retorno = "DENGUE";
        }
        if (codAgravo.equals("G039")) {
            retorno = "MENINGITE";
        }
        if (codAgravo.equals("A779")) {
            retorno = "FEBRE MACULOSA";
        }
        if (codAgravo.equals("A988")) {
            retorno = "HANTAVIROSE";
        }
        if (codAgravo.equals("B550")) {
            retorno = "LEISHMANIOSE VICERAL";
        }
        if (codAgravo.equals("A33")) {
            retorno = "TÉTANO NEONATAL";
        }
        if (codAgravo.equals("A829")) {
            retorno = "RAIVA";
        }
        if (codAgravo.equals("A209")) {
            retorno = "PESTE";
        }
        if (codAgravo.equals("A809")) {
            retorno = "PARALISIA FLÁCIDA AGUDA";
        }
        if (codAgravo.equals("A279")) {
            retorno = "LEPTOSPIROSE";
        }
        if (codAgravo.equals("A010")) {
            retorno = "FEBRE TIFÓIDE";
        }
        if (codAgravo.equals("A959")) {
            retorno = "FEBRE AMARELA";
        }
        if (codAgravo.equals("A369")) {
            retorno = "DIFTERIA";
        }
        if (codAgravo.equals("A379")) {
            retorno = "COQUELUCHE";
        }
        if (codAgravo.equals("A009")) {
            retorno = "CÓLERA";
        }
        if (codAgravo.equals("B571")) {
            retorno = "DOENÇA DE CHAGAS";
        }
        if (codAgravo.equals("A35")) {
            retorno = "TÉTANO ACIDENTAL";
        }
        if (codAgravo.equals("P350")) {
            retorno = "SRC";
        }
        if (codAgravo.equals("B551")) {
            retorno = "LTA";
        }
        if (codAgravo.equals("B19")) {
            retorno = "HEPATITE VIRAL";
        }
        if (codAgravo.equals("B091")) {
            retorno = "SARAMPO";
        }
        if (codAgravo.equals("B092")) {
            retorno = "RUBÉOLA";
        }
        if (codAgravo.equals("A051")) {
            retorno = "BOTULISMO";
        }
        if (codAgravo.equals("A779")) {
            retorno = "FEBRE MACULOSA";
        }
        if (codAgravo.equals("A923")) {
            retorno = "FEBRE DO NILO";
        }
        if (codAgravo.equals("B54")) {
            retorno = "MALÁRIA";
        }
        
        
        if (codAgravo.equals("X29")) {
            retorno = "ACIDENTE POR ANIMAIS PEÇONHENTOS";
        }
        if (codAgravo.equals("Y59")) {
            retorno = "EVENTOS ADVERSOS PÓS-VACINACAO";
        }
        return retorno;
    }

    
    public HashMap<String, OportunidadeAgravo> populaUfsBeansOportunidade() {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> uf = new HashMap<String, String>();
        HashMap<String, OportunidadeAgravo> ufsBeans = new HashMap<String, OportunidadeAgravo>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela

        //busca municipios dessa regional
        DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("UF", "dbf\\");
        Object[] rowObjects1;
        try {
            utilDbf.mapearPosicoes(readerMunicipio);

            while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {

                OportunidadeAgravo agravoDbf = new OportunidadeAgravo();
                agravoDbf.setNmAgravo(utilDbf.getString(rowObjects1, "SG_UF"));
                agravoDbf.setCodAgravo(utilDbf.getString(rowObjects1, "ID_UF"));
                agravoDbf.setRegiao(buscaRegiao(agravoDbf.getNmAgravo()));
                agravoDbf.setQtdDataInvalida(0);
                agravoDbf.setQtdInoportuno(0);
                agravoDbf.setQtdNaoEncerrado(0);
                agravoDbf.setQtdOportuno(0);
                agravoDbf.setTotal(0);
                uf.put(utilDbf.getString(rowObjects1, "ID_UF"), utilDbf.getString(rowObjects1, "SG_UF"));
                ufsBeans.put(agravoDbf.getCodAgravo(), agravoDbf);
            }
        } catch (DBFException e) {
            Master.mensagem("Erro ao carregar municipios:\n" + e);
        }
        uf = this.sortHashMapByValues(uf, false);
        Set<String> ufKeys = uf.keySet();
        HashMap<String, OportunidadeAgravo> ufsBeansRetorno = new HashMap<String, OportunidadeAgravo>();
        Iterator valueIt = ufKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            ufsBeansRetorno.put(key, ufsBeans.get(key));
        }
        return ufsBeansRetorno;
    }

    private String buscaRegiao(String uf) {
        String regiao = "";
        if (uf.equals("TO")) {
            return "NORTE";
        }
        if (uf.equals("AC")) {
            return "NORTE";
        }
        if (uf.equals("AL")) {
            return "NORDESTE";
        }
        if (uf.equals("AM")) {
            return "NORTE";
        }
        if (uf.equals("AP")) {
            return "NORTE";
        }
        if (uf.equals("BA")) {
            return "NORDESTE";
        }
        if (uf.equals("CE")) {
            return "NORDESTE";
        }
        if (uf.equals("DF")) {
            return "CENTRO-OESTE";
        }
        if (uf.equals("ES")) {
            return "SUDESTE";
        }
        if (uf.equals("GO")) {
            return "CENTRO-OESTE";
        }
        if (uf.equals("MA")) {
            return "NORDESTE";
        }
        if (uf.equals("MG")) {
            return "SUDESTE";
        }
        if (uf.equals("MS")) {
            return "CENTRO-OESTE";
        }
        if (uf.equals("MT")) {
            return "CENTRO-OESTE";
        }
        if (uf.equals("PA")) {
            return "NORTE";
        }
        if (uf.equals("PB")) {
            return "NORDESTE";
        }
        if (uf.equals("PE")) {
            return "NORDESTE";
        }
        if (uf.equals("PI")) {
            return "NORDESTE";
        }
        if (uf.equals("PR")) {
            return "SUL";
        }
        if (uf.equals("RJ")) {
            return "SUDESTE";
        }
        if (uf.equals("RN")) {
            return "NORDESTE";
        }
        if (uf.equals("RO")) {
            return "NORTE";
        }
        if (uf.equals("RR")) {
            return "NORTE";
        }
        if (uf.equals("RS")) {
            return "SUL";
        }
        if (uf.equals("RO")) {
            return "NORTE";
        }
        if (uf.equals("SC")) {
            return "SUL";
        }
        if (uf.equals("SE")) {
            return "NORDESTE";
        }
        if (uf.equals("SP")) {
            return "SUDESTE";
        }

        return null;
    }

    private int retornaDiferenca(String agravo) {
        if (agravo.equals("B19") || agravo.equals("B551") || agravo.equals("P350")) {
            return 180;
        }
        return 60;
    }

    private boolean verificaAgravoOportuno(String agravo) {
        if (this.getAgravosValidos().contains(agravo)) {
            return true;
        }
        return false;
    }

    /**
     * @return the agravosValidos
     */
    public List<String> getAgravosValidos() {
        return agravosValidos;
    }

    /**
     * @param agravosValidos the agravosValidos to set
     */
    public void setAgravosValidos(List<String> agravosValidos) {
        this.agravosValidos = agravosValidos;
    }

    private void iniciaAgravosValidos() {
      getAgravosValidos().add("B19");     //HEPATITES VIRAIS
        getAgravosValidos().add("B551");    //LEISHMANIOSE TEGUMENTAR AMERICANA
        getAgravosValidos().add("P350");    //SINDROME DA RUBEOLA CONGENITA

 //       getAgravosValidos().add("Y59");     // EVENTOS ADVERSOS POS-VACINACAO
 //       getAgravosValidos().add("X29");     // ACIDENTE POR ANIMAIS PECONHENTOS

        
        getAgravosValidos().add("A35");     //TETANO ACIDENTAL
        getAgravosValidos().add("B09");     //DOENCAS EXANTEMATICAS
        getAgravosValidos().add("B091");    //SARAMPO
        getAgravosValidos().add("B092");    //RUBEOLA
        getAgravosValidos().add("B571");    //DOENCA DE CHAGAS AGUDA
        getAgravosValidos().add("A009");    //COLERA
        getAgravosValidos().add("A379");    //COQUELUCHE
        getAgravosValidos().add("A369");    //DIFTERIA
        getAgravosValidos().add("A959");    //FEBRE AMARELA
        getAgravosValidos().add("A010");    //FEBRE TIFOIDE
        getAgravosValidos().add("A279");    //LEPTOSPIROSE
        getAgravosValidos().add("A809");    //PARALISIA FLACIDA AGUDA   POLIOMIELITE
        getAgravosValidos().add("A209");    //PESTE
        getAgravosValidos().add("A829");    //RAIVA HUMANA
        getAgravosValidos().add("A33");     //TETANO NEONATAL
        getAgravosValidos().add("B550");    //LEISHMANIOSE VISCERAL
        getAgravosValidos().add("A988");    //HANTAVIROSE
        getAgravosValidos().add("A779");    //FEBRE MACULOSA / RICKETTSIOSES
        getAgravosValidos().add("G039");    //MENINGITE
        getAgravosValidos().add("A90");     //DENGUE
        getAgravosValidos().add("A051");    //BOTULISMO
        getAgravosValidos().add("A923");    //FEBRE DO NILO
        getAgravosValidos().add("B54");     //MALARIA
        
        

    }

    /**
     * @return the sqlCalculoAgravosMalaria60dias
     */
    public String getSqlCalculoAgravosMalaria60dias() {
        return sqlCalculoAgravosMalaria60dias;
    }

    /**
     * @param sqlCalculoAgravosMalaria60dias the sqlCalculoAgravosMalaria60dias to set
     */
    public void setSqlCalculoAgravosMalaria60dias(String sqlCalculoAgravosMalaria60dias) {
        this.sqlCalculoAgravosMalaria60dias = sqlCalculoAgravosMalaria60dias;
    }

    /**
     * @return the sqlCalculoAgravoEspecifico
     */
    public String getSqlCalculoAgravoEspecifico() {
        return sqlCalculoAgravoEspecifico;
    }

    /**
     * @param sqlCalculoAgravoEspecifico the sqlCalculoAgravoEspecifico to set
     */
    public void setSqlCalculoAgravoEspecifico(String sqlCalculoAgravoEspecifico) {
        this.sqlCalculoAgravoEspecifico = sqlCalculoAgravoEspecifico;
    }

    private String converteCodigoParaCompleto(String agravoSelecionado) {
        if (agravoSelecionado.length() > 4) {
            return agravoSelecionado.substring(0, 2) + "." + agravoSelecionado.substring(3);
        }
        return agravoSelecionado;
    }

    /**
     * @return the listagemCasos
     */
    public List<CasoOportunidade> getListagemCasos() {
        return listagemCasos;
    }

    /**
     * @param listagemCasos the listagemCasos to set
     */
    public void setListagemCasos(List<CasoOportunidade> listagemCasos) {
        this.listagemCasos = listagemCasos;
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
     * @return the municipios
     */
    public List<Municipio> getMunicipios() {
        return municipios;
    }

    /**
     * @param municipios the municipios to set
     */
    public void setMunicipios(List<Municipio> municipios) {
        this.municipios = municipios;
    }

    private String retornaNome(String codMunicipio) {
        String nomeMunicipio = "";
        for (int i = 0; i < getMunicipios().size(); i++) {
            if (getMunicipios().get(i).getCodMunicipio().equals(codMunicipio)) {
                nomeMunicipio = getMunicipios().get(i).getNmMunicipio();
            }
        }
        return nomeMunicipio;
    }

    private HashMap<String, OportunidadeAgravo> populaMunicipiosBeans(String uf) {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> municipios = new HashMap<String, String>();
        HashMap<String, OportunidadeAgravo> municipiosBeans = new HashMap<String, OportunidadeAgravo>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela

        //busca municipios dessa regional
        DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
        Object[] rowObjects1;
        try {
            utilDbf.mapearPosicoes(readerMunicipio);

            while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                if (uf.equals(utilDbf.getString(rowObjects1, "SG_UF"))) {
                    OportunidadeAgravo agravoDbf = new OportunidadeAgravo();
                    agravoDbf.setNmAgravo(utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                    agravoDbf.setCodAgravo(utilDbf.getString(rowObjects1, "ID_MUNICIP"));
                    agravoDbf.setRegiao(buscaRegiao(agravoDbf.getNmAgravo()));
                    agravoDbf.setQtdDataInvalida(0);
                    agravoDbf.setQtdInoportuno(0);
                    agravoDbf.setQtdNaoEncerrado(0);
                    agravoDbf.setQtdOportuno(0);
                    agravoDbf.setTotal(0);
                    municipios.put(utilDbf.getString(rowObjects1, "ID_MUNICIP"), utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                    municipiosBeans.put(agravoDbf.getCodAgravo(), agravoDbf);
                }
            }
        } catch (DBFException e) {
            Master.mensagem("Erro ao carregar municipios:\n" + e);
        }
        municipios = this.sortHashMapByValues(municipios, false);
        Set<String> municipiosKeys = municipios.keySet();
        HashMap<String, OportunidadeAgravo> municipiosBeansRetorno = new HashMap<String, OportunidadeAgravo>();
        Iterator valueIt = municipiosKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            municipiosBeansRetorno.put(key, municipiosBeans.get(key));
        }
        return municipiosBeans;
    }
}
