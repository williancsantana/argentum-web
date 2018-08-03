/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.classes.agravos;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.DBFUtil;
import com.org.negocio.Configuracao;
import com.org.model.classes.Agravo;
import com.org.model.classes.ColunasDbf;
import com.org.model.classes.Municipio;
import com.org.model.classes.Semana;
import com.org.model.classes.UF;
import com.org.negocio.Util;
import com.org.service.RegiaoRegularidadeService;
import com.org.util.SinanUtil;
import com.org.view.Master;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanComparator;

/**
 *
 * @author geraldo
 */
public class Regularidade extends Agravo {

    static String ANO;
    private String semanaInicioDbf;
    private String semanaFimDbf;
    private List<String> agravosCompulsorios = new ArrayList<String>();
    boolean gtSinan = false;

    public Regularidade(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de Notificação");
        setTipoAgregacao("de Notificação");
        init("postgres");
        alimentaAgravosCompulsorios();
    }

    public void alimentaAgravosCompulsorios() {
        agravosCompulsorios.add("X29");
        agravosCompulsorios.add("W64");
        agravosCompulsorios.add("A080");
        agravosCompulsorios.add("A539");
        agravosCompulsorios.add("R36");
        agravosCompulsorios.add("O986");
        agravosCompulsorios.add("P371");

        agravosCompulsorios.add("J06");
        agravosCompulsorios.add("B24");
        agravosCompulsorios.add("A051");
        agravosCompulsorios.add("B571");
        agravosCompulsorios.add("A009");
        agravosCompulsorios.add("A90");
        agravosCompulsorios.add("A369");
        agravosCompulsorios.add("Z209");
        agravosCompulsorios.add("Y96");
        agravosCompulsorios.add("C80");
        agravosCompulsorios.add("L989");
        agravosCompulsorios.add("Z579");
        agravosCompulsorios.add("H833");
        agravosCompulsorios.add("J64");
        agravosCompulsorios.add("F99");
        agravosCompulsorios.add("A779");
        agravosCompulsorios.add("A010");
        agravosCompulsorios.add("Z21");
        agravosCompulsorios.add("A309");
        agravosCompulsorios.add("A988");
        agravosCompulsorios.add("T659");
        agravosCompulsorios.add("B551");
        agravosCompulsorios.add("A279");
        agravosCompulsorios.add("B54");
        agravosCompulsorios.add("G039");
        agravosCompulsorios.add("A809");
        agravosCompulsorios.add("A209");
        agravosCompulsorios.add("A829");
        agravosCompulsorios.add("O981");
        agravosCompulsorios.add("A35");
        agravosCompulsorios.add("A33");
        agravosCompulsorios.add("A169");
        agravosCompulsorios.add("A229");
        agravosCompulsorios.add("A810");
        agravosCompulsorios.add("Y59");
        agravosCompulsorios.add("A219");
        agravosCompulsorios.add("B03");
        agravosCompulsorios.add("P350");
        agravosCompulsorios.add("J07");
        agravosCompulsorios.add("R17");
        agravosCompulsorios.add("A379");
        agravosCompulsorios.add("A959");
        agravosCompulsorios.add("A509");
        agravosCompulsorios.add("B550");
        agravosCompulsorios.add("B19");
        agravosCompulsorios.add("J11");
        agravosCompulsorios.add("B09");
        agravosCompulsorios.add("EPI");
        agravosCompulsorios.add("Y09");
        agravosCompulsorios.add("A923");
        agravosCompulsorios.add("B659");
        agravosCompulsorios.add("Z206");
        agravosCompulsorios.add("B019");
        agravosCompulsorios.add("B749");

    }

    @Override
    public void init(String tipoBanco) {
    }

    @Override
    public String getTaxaEstado(Connection con, Map parametros) throws SQLException {
        return "";
    }

    @Override
    public String getCompletitude(Connection con, Map parametros) throws SQLException {
        return "";
    }

    private void calculaGtSinan(DBFReader reader, Map parametros) throws IOException {
        HashMap<String, Municipio> municipios = null;
        boolean calculaBrasil = false;
        if (parametros.get("parUf").toString().equals("brasil")) {
            municipios = getMunicipios(0, "", "", "");
            calculaBrasil = true;
        } else {
            if (parametros.get("parCodRegional") == null) {
                parametros.put("parCodRegional", "");
            }
            String municipioSelecionado = "";
            if(parametros.get("parMunicipio") != null)
                    municipioSelecionado = parametros.get("parMunicipio").toString();
            municipios = getMunicipios(1, parametros.get("parCodRegional").toString(), parametros.get("parSgUf").toString(), municipioSelecionado);
        }
        //looping com semanas selecionadas
        String parSemanas = parametros.get("parSemanas").toString();
        String[] arraySemanas = parSemanas.split("\n");
        Map parametrosRegularidade = new HashMap();
        for (int y = 0; y < arraySemanas.length; y++) {
            Set entries = municipios.entrySet();
            Iterator it = entries.iterator();
            while (it.hasNext()) {
                Map.Entry municipio = (Map.Entry) it.next();
                municipios.get(municipio.getKey().toString()).getSemanas().put(String.valueOf(y),
                        new Semana(arraySemanas[y].substring(0, 6), arraySemanas[y].substring(9, 15), arraySemanas[y]));
            }
            //loop para ler os arquivos selecionados
            String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
            for (int k = 0; k < arquivos.length; k++) {
                parametrosRegularidade.put("ARQUIVO"+k, arquivos[k].toString()+"; ");
                Vector<String> semanasAvaliacao = new Vector<String>();
                int semanaInicio = Integer.parseInt(arraySemanas[y].substring(4, 6));
                int semanaFim = Integer.parseInt(arraySemanas[y].substring(13, 15));
                setSemanaInicioDbf(arraySemanas[y].substring(0, 6));
                setSemanaFimDbf(arraySemanas[y].substring(9, 15));
                //adiciona semanas que estão entre os períodos
                // faz o tratamento das semanas de anos diferentes
                if (arraySemanas[y].substring(0, 4).equals(arraySemanas[y].substring(9, 13))) {
                    for (int i = semanaInicio; i <= semanaFim; i++) {
                        semanasAvaliacao.add(arraySemanas[y].substring(0, 4) + formataSemana(String.valueOf(i)));
                    }
                } else {
                    for (int i = semanaInicio; i <= 53; i++) {
                        semanasAvaliacao.add(arraySemanas[y].substring(0, 4) + formataSemana(String.valueOf(i)));
                    }
                    for (int i = 1; i <= semanaFim; i++) {
                        semanasAvaliacao.add(arraySemanas[y].substring(9, 13) + formataSemana(String.valueOf(i)));
                    }
                }

                //verificar nos arquivos se existe alguma notificacao no periodo selecionado
//            janela.jLabel20.setText("Lendo arquivo " + arquivos[k]);
                //lendo o arquivo do momento (arquivo na posição k)
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                Object[] rowObjects;
                DBFUtil utilDbf = new DBFUtil();
                String semanaNotificacao, municipioAvaliacao;
                DecimalFormat df = new DecimalFormat("0.##");
                try {
                    //mapea os cabeçalhos do dbf
                    utilDbf.mapearPosicoes(reader);
                    int TotalRegistrosInt = reader.getRecordCount();
                    int i = 1;
                    while ((rowObjects = reader.nextRecord()) != null) {
                        try {
                            semanaNotificacao = utilDbf.getString(rowObjects, "SEM_NOT");
                        } catch (Exception e) {
                            Master.mensagem("Campo SEM_NOT não encontrado na base especificada: " + arquivos[k]);
                            return;
                        }
                        try {
                            municipioAvaliacao = utilDbf.getString(rowObjects, "ID_MUNICIP");
                        } catch (Exception e) {
                            Master.mensagem("Campo ID_MUNICIP não encontrado na base especificada: " + arquivos[k]);
                            return;
                        }

                        //verifica se o registro está na semana selecionada
                        //se tiver, exclui do vetor dos municipios
                        if (semanasAvaliacao.contains(semanaNotificacao)) {
                            // se entrar nesse if é porque houve a notificação
                            //verifica se o agravo é compulsorio
                            //se o arquivo for NIND será comparado o campo ID_AGRAVO para verificar se ele é compulsório
                            if (municipios.get(municipioAvaliacao) != null && arquivos[k].substring(0, 2).equals("NI")) {
                                if (agravosCompulsorios.contains(utilDbf.getString(rowObjects, "ID_AGRAVO"))) {
                                    municipios.get(municipioAvaliacao).getSemanas().get(String.valueOf(y)).setSituacao(true);
                                }
                            } else if(municipios.containsKey(municipioAvaliacao)) {
                                municipios.get(municipioAvaliacao).getSemanas().get(String.valueOf(y)).setSituacao(true);
                            }
                        }
//                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistrosInt)) * 100;
//                    janela.prbStatus.setValue((int) percentual);
//                    janela.jLabel20.setText("Lendo arquivo " + arquivos[k] + ": " + i + " de " + TotalRegistrosInt);
                       //incrementar progressão
                        float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistrosInt)) * 100;
                        getBarraStatus().setValue((int) percentual);
                        getBarraStatus().setStringPainted(true);
                        getBarraStatus().setString("Processando: "+arquivos[k].toString() + ", período: "+ arraySemanas[y].toString()+"  ("+(int) percentual+"%)");
                        i++;
                    }
                } catch (DBFException e) {
                    Master.mensagem("Erro: " + e);
                }
                
            }
            
            float percentualGeral = Float.parseFloat(String.valueOf(y+1)) / Float.parseFloat(String.valueOf(arraySemanas.length)) * 100;
            getBarraStatusGeral().setValue((int) percentualGeral);
            getBarraStatusGeral().setStringPainted(true);
            getBarraStatusGeral().setString((int) percentualGeral + "%");
            
        }
        List<Municipio> beans = new ArrayList();
        List<Municipio> todosMunicipios = new ArrayList<Municipio>();
        Collection<Municipio> municipioBean = municipios.values();

        //lista com o resultado final dos processos
        int countFields = 4;
        Map mapaPeriodos = new HashMap();
        for (Iterator<Municipio> it = municipioBean.iterator(); it.hasNext();) {
            //fazer aqui o tratamento para verificar se existem semanas que está tudo OK
            Municipio agravoDBF = it.next();
         
            // retira municípios que estão OK em todos os períodos
            Semana semana = new Semana();
            int qtdePeriodos = 0, qtdeOK = 0, periodo = 0;
            countFields = 4;
            for(String key : agravoDBF.getSemanas().keySet()) {
                periodo = Integer.parseInt(key) + 1;
                qtdePeriodos++;
                countFields++;
                parametrosRegularidade.put("periodo"+periodo, agravoDBF.getSemanas().get(key).getPeriodo());
                semana = agravoDBF.getSemanas().get(key);
                if(semana.isSituacao()){
                    qtdeOK++;
                    mapaPeriodos.put("periodo"+periodo, "");
                }else{
                    mapaPeriodos.put("periodo"+periodo, "X");
                }
            }
            if(qtdePeriodos != qtdeOK){
                beans.add(agravoDBF);
                agravoDBF.setIrregular(true);
            }else{
                agravoDBF.setIrregular(false);
            }

            todosMunicipios.add(agravoDBF);
            if(mapaPeriodos.get("periodo1") != null){
                agravoDBF.setPeriodo1(mapaPeriodos.get("periodo1").toString());
            }
            if(mapaPeriodos.get("periodo2") != null){
                agravoDBF.setPeriodo2(mapaPeriodos.get("periodo2").toString());
            }
            if(mapaPeriodos.get("periodo3") != null){
                agravoDBF.setPeriodo3(mapaPeriodos.get("periodo3").toString());
            }
            if(mapaPeriodos.get("periodo4") != null){
                agravoDBF.setPeriodo4(mapaPeriodos.get("periodo4").toString());
            }
            if(mapaPeriodos.get("periodo5") != null){
                agravoDBF.setPeriodo5(mapaPeriodos.get("periodo5").toString());
            }
            if(mapaPeriodos.get("periodo6") != null){
                agravoDBF.setPeriodo6(mapaPeriodos.get("periodo6").toString());
            }
            if(mapaPeriodos.get("periodo7") != null){
                agravoDBF.setPeriodo7(mapaPeriodos.get("periodo7").toString());
            }
            if(mapaPeriodos.get("periodo8") != null){
                agravoDBF.setPeriodo8(mapaPeriodos.get("periodo8").toString());
            }
            if(mapaPeriodos.get("periodo9") != null){
                agravoDBF.setPeriodo9(mapaPeriodos.get("periodo9").toString());
            }
            if(mapaPeriodos.get("periodo10") != null){
                agravoDBF.setPeriodo10(mapaPeriodos.get("periodo10").toString());
            }
            if(mapaPeriodos.get("periodo11") != null){
                agravoDBF.setPeriodo11(mapaPeriodos.get("periodo11").toString());
            }
            if(mapaPeriodos.get("periodo12") != null){
                agravoDBF.setPeriodo12(mapaPeriodos.get("periodo12").toString());
            }
            if(mapaPeriodos.get("periodo13") != null){
                agravoDBF.setPeriodo13(mapaPeriodos.get("periodo13").toString());
            }
            if(mapaPeriodos.get("periodo14") != null){
                agravoDBF.setPeriodo14(mapaPeriodos.get("periodo14").toString());
            }
            if(mapaPeriodos.get("periodo15") != null){
               agravoDBF.setPeriodo15(mapaPeriodos.get("periodo15").toString()); 
            }
            if(mapaPeriodos.get("periodo16") != null){
               agravoDBF.setPeriodo16(mapaPeriodos.get("periodo16").toString()); 
            }
            if(mapaPeriodos.get("periodo17") != null){
                agravoDBF.setPeriodo17(mapaPeriodos.get("periodo17").toString());
            }
            if(mapaPeriodos.get("periodo18") != null){
               agravoDBF.setPeriodo18(mapaPeriodos.get("periodo18").toString()); 
            }
            
            
        }
//        janela.jLabel20.setText("Ordenando município");
        Collections.sort(beans, new BeanComparator("nmMunicipio"));
//        janela.jLabel20.setText("Ordenando UF");
        Collections.sort(beans, new BeanComparator("sgUF"));
//        janela.jLabel20.setText("Ordenando região");
        Collections.sort(beans, new BeanComparator("nmRegiao"));
        
        RegiaoRegularidadeService regiaoRegularidadeService = new RegiaoRegularidadeService();
        parametrosRegularidade.put("UF", parametros.get("parSgUf"));
        parametrosRegularidade.put("REGIONAL", parametros.get("parRegional"));
        parametrosRegularidade.put("MUNICIPIO", "TODOS");
        parametrosRegularidade.put("parIsRegiao",(Boolean) parametros.get("parIsRegiao"));
        parametrosRegularidade.put("parIsRegional",(Boolean) parametros.get("parIsRegional"));
        parametrosRegularidade.put("parDesagregacao", (String) parametros.get("parDesagregacao"));
        parametrosRegularidade.put("parIsRegional",(Boolean) parametros.get("parIsRegional"));

        if(parametros.get("tipoRelatorio").equals("analitico")){
            regiaoRegularidadeService.gerarRelatorioRegularidade(beans, parametrosRegularidade, countFields, parametros.get("isDBF").toString());
        }else if(parametros.get("tipoRelatorio").equals("sintetico")){
            regiaoRegularidadeService.gerarRelatorioPropMunicIrreg(todosMunicipios, parametrosRegularidade, countFields, parametros.get("isDBF").toString(), calculaBrasil);
        }else{
            regiaoRegularidadeService.gerarRelatorioRegularidade(beans, parametrosRegularidade, countFields, parametros.get("isDBF").toString());
            regiaoRegularidadeService.gerarRelatorioPropMunicIrreg(todosMunicipios, parametrosRegularidade, countFields, parametros.get("isDBF").toString(), calculaBrasil);
        }
        
        this.setBeans(beans);
    }

    @Override
    public void calcula(DBFReader reader, Map parametros) {
        //array para parametros de semana
        Vector<Semana> semanasParametro = new Vector<Semana>();
        //se for gtsinan, fazer loop com todos os periodos selecionados
        if (parametros.get("parGtSinan").toString().equals("true")) {
            gtSinan = true;
            try {
                calculaGtSinan(reader, parametros);
            } catch (IOException ex) {
                Logger.getLogger(Regularidade.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //montar array com valores possíveis
            Vector<String> semanasAvaliacao = new Vector<String>();
            int semanaInicio = Integer.parseInt(parametros.get("parSemanaInicial").toString());
            int semanaFim = Integer.parseInt(parametros.get("parSemanaFinal").toString());
            setSemanaInicioDbf(parametros.get("parAnoInicial").toString() + formataSemana(parametros.get("parSemanaInicial").toString()));
            setSemanaFimDbf(parametros.get("parAnoFinal").toString() + formataSemana(parametros.get("parSemanaFinal").toString()));
            if (parametros.get("parAnoInicial").toString().equals(parametros.get("parAnoFinal").toString())) {
                for (int i = semanaInicio; i <= semanaFim; i++) {
                    semanasAvaliacao.add(parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)));
                }
            } else {
                for (int i = semanaInicio; i <= 53; i++) {
                    semanasAvaliacao.add(parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)));
                }
                for (int i = 1; i <= semanaFim; i++) {
                    semanasAvaliacao.add(parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)));
                }
            }
            if (parametros.get("parAnoInicial").toString().equals(parametros.get("parAnoFinal").toString())) {
                ANO = parametros.get("parAnoFinal").toString();
            } else {
                ANO = "";
            }
            //TODO: fazer o filtro do nível de agregaçao

            //montar os municipios de acordo com o nivel de agregacao.
            //guardar todos os códigos dos municipios
            //para buscar o primeiro parametro = 0 == brasil
//        janela.jLabel20.setText("Carregando municipios");
            HashMap<String, Municipio> municipios = null;
            if (parametros.get("parUf").toString().equals("brasil")) {
                municipios = getMunicipios(0, "", "", "");
            } else {
                if (parametros.get("parCodRegional") == null) {
                    parametros.put("parCodRegional", "");
                }
                municipios = getMunicipios(1, parametros.get("parCodRegional").toString(), parametros.get("parSgUf").toString(), "");
            }

            //CALCULAR A QUANTIDADE DE MUNICIPIOS
            calcularQuantidadeMunicipios(municipios);

            //loop para ler os arquivos selecionados
            String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
            for (int k = 0; k < arquivos.length; k++) {

                //verificar nos arquivos se existe alguma notificacao no periodo selecionado
//            janela.jLabel20.setText("Lendo arquivo " + arquivos[k]);
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                Object[] rowObjects;
                DBFUtil utilDbf = new DBFUtil();
                String semanaNotificacao, municipioAvaliacao;
                DecimalFormat df = new DecimalFormat("0.##");
                try {
                    utilDbf.mapearPosicoes(reader);
                    int TotalRegistrosInt = reader.getRecordCount();
                    int i = 1;
                    while ((rowObjects = reader.nextRecord()) != null) {
                        try {
                            semanaNotificacao = utilDbf.getString(rowObjects, "SEM_NOT");
                        } catch (Exception e) {
                            Master.mensagem("Campo SEM_NOT não encontrado na base especificada: " + arquivos[k]);
                            return;
                        }
                        try {
                            municipioAvaliacao = utilDbf.getString(rowObjects, "ID_MUNICIP");
                        } catch (Exception e) {
                            Master.mensagem("Campo ID_MUNICIP não encontrado na base especificada: " + arquivos[k]);
                            return;
                        }

                        //verifica se o registro está na semana selecionada
                        //se tiver, exclui do vetor dos municipios
                        if (semanasAvaliacao.contains(semanaNotificacao)) {
                            //verifica se o agravo é compulsorio
                            if (arquivos[k].substring(0, 2).equals("NI")) {
                                if (agravosCompulsorios.contains(utilDbf.getString(rowObjects, "ID_AGRAVO"))) {
                                    municipios.remove(municipioAvaliacao);
                                }
                            } else {
                                municipios.remove(municipioAvaliacao);
                            }
                        }
//                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistrosInt)) * 100;
//                    janela.prbStatus.setValue((int) percentual);
//                    janela.jLabel20.setText("Lendo arquivo " + arquivos[k] + ": " + i + " de " + TotalRegistrosInt);
                        float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistrosInt)) * 100;
                        getBarraStatus().setValue((int) percentual);
                        i++;
                    }
                } catch (DBFException e) {
                    Master.mensagem("Erro: " + e);
                }
            }

            //calcular quantos municipios sobraram
            calcularQuantidadeMunicipiosRestantes(municipios);
            List<Municipio> beans = new ArrayList();
            Collection<Municipio> municipioBean = municipios.values();

            for (Iterator<Municipio> it = municipioBean.iterator(); it.hasNext();) {
                Municipio agravoDBF = it.next();
                beans.add(agravoDBF);
            }
//        janela.jLabel20.setText("Ordenando município");
            Collections.sort(beans, new BeanComparator("nmMunicipio"));
//        janela.jLabel20.setText("Ordenando UF");
            Collections.sort(beans, new BeanComparator("sgUF"));
//        janela.jLabel20.setText("Ordenando região");
            Collections.sort(beans, new BeanComparator("nmRegiao"));
            this.setBeans(beans);
        }
    }

    @Override
    public List getBeanMunicipios(Connection con, Map parametros) throws SQLException {
        calcula(null, parametros);
        return this.getBeans();
    }

    @Override
    public List getBeansMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
        calcula(null, parametros);
        return this.getBeans();
    }

    @Override
    public List getBeansEstadoEspecifico(Connection con, Map parametros) throws SQLException {
        calcula(null, parametros);
        return this.getBeans();
    }
/*
    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("ID_MUNIC", new ColunasDbf(7));
        hashColunas.put("NM_MUNIC", new ColunasDbf(30));
        hashColunas.put("SG_UF", new ColunasDbf(2));
        if (gtSinan) {
            hashColunas.put("PERIODO", new ColunasDbf(15));
            hashColunas.put("SITUACAO", new ColunasDbf(12));
            hashColunas.put("DTAVAL", new ColunasDbf(10));
        } else {
            hashColunas.put("ANO", new ColunasDbf(4, 0));
            hashColunas.put("SEMINICIO", new ColunasDbf(6, 0));
            hashColunas.put("SEMANAFIM", new ColunasDbf(6, 0));
            hashColunas.put("ORIGEM", new ColunasDbf(30));
        }
        this.setColunas(hashColunas);
        return hashColunas;
    }

    @Override
    public String[] getOrdemColunas() {
        if (gtSinan) {
            return new String[]{"ID_MUNIC", "NM_MUNIC", "SG_UF", "PERIODO", "SITUACAO", "DTAVAL"};
        } else {
            return new String[]{"ID_MUNIC", "NM_MUNIC", "SG_UF", "ANO", "SEMINICIO", "SEMANAFIM", "ORIGEM"};

        }
    }
*/
    @Override
    public Map getParametros() {
        Map parametros = new HashMap();
        return parametros;
    }
/*
    @Override
    public DBFWriter getLinhas(HashMap<String, ColunasDbf> colunas, List bean, DBFWriter writer) throws DBFException, IOException {
        String nomeArquivo = "c:\\outputregularidade_"+new SimpleDateFormat("ddMMyyyy_ss").format(new Date())+".csv";
        FileWriter x = new FileWriter(nomeArquivo, true);
        String conteudo = "";
        String codMunicipio = "";
        for (int i = 0; i < bean.size(); i++) {
            Municipio municipio = (Municipio) bean.get(i);

            if (gtSinan) {
                getBarraStatus().setString("Gerando arquivo csv, aguarde, pode levar alguns minutos.");
                HashMap<String, Semana> semanas = municipio.getSemanas();
                if (i == 0) {
                    conteudo += "UF;MUNICIPIO;ID_MUNICIPIO;";
                    for (int k = 0; k < semanas.size(); k++) {
                        Semana semana = semanas.get(String.valueOf(k));
                        conteudo += semana.getPeriodo() + ";";
                    }
                }
                for (int k = 0; k < semanas.size(); k++) {
                    //se k==0 montar o cabeçalho do arquivo csv

                    Object rowData[] = new Object[colunas.size()];
                    rowData[0] = municipio.getCodMunicipio();
                    rowData[1] = municipio.getNmMunicipio();
                    rowData[2] = municipio.getSgUF();
                    Semana semana = semanas.get(String.valueOf(k));
                    rowData[3] = semana.getPeriodo();
                    if (semana.isSituacao()) {
                        rowData[4] = "OK";
                    } else {
                        rowData[4] = "S/ NOTIFICACAO";
                    }

                    if (!codMunicipio.equals(municipio.getCodMunicipio())) {
                        codMunicipio = municipio.getCodMunicipio();
                        conteudo += "\n";
                        conteudo += municipio.getSgUF() + ";" + municipio.getNmMunicipio() + ";" + municipio.getCodMunicipio() + ";";
                    }
                    conteudo += rowData[4].toString() + ";";
                    rowData[5] = semana.getDtAvaliacao();
                    writer.addRecord(rowData);
                }
            } else {
                Object rowData[] = new Object[colunas.size()];
                rowData[0] = municipio.getCodMunicipio();
                rowData[1] = municipio.getNmMunicipio();
                rowData[2] = municipio.getSgUF();
                rowData[3] = preencheAnoSemana(getSemanaInicioDbf(), getSemanaFimDbf());
                rowData[4] = Double.parseDouble(getSemanaInicioDbf());
                rowData[5] = Double.parseDouble(getSemanaFimDbf());
                rowData[6] = "REGULARIDADE-SINANNET";
                writer.addRecord(rowData);
            }
        }
        x.write(conteudo); // armazena o texto no objeto x, que aponta para o arquivo
        x.close();
        if (gtSinan) {
            getBarraStatus().setString("Arquivo gerado com sucesso.");
            Master.mensagem("Arquivo csv gerado no seguinte local: "+nomeArquivo);
        }
        return writer;
    }
*/
    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/semNotificacao.jasper";
    }

    private String formataSemana(String semana) {
        if (Integer.parseInt(semana) < 10) {
            return "0" + semana;
        } else {
            return semana;
        }
    }

    private void calcularQuantidadeMunicipiosRestantes(HashMap<String, Municipio> municipios) {
        Collection<Municipio> municipioBean = municipios.values();
        HashMap<String, UF> ufs = new HashMap<String, UF>();
        for (Iterator<Municipio> it = municipioBean.iterator(); it.hasNext();) {
            Municipio itMunicipio = it.next();
            if (!ufs.containsKey(itMunicipio.getUf().getNmUF())) {
                UF uf = itMunicipio.getUf();
                uf.setQtdMunicipiosSemNotificacao(1);
                ufs.put(itMunicipio.getUf().getNmUF(), uf);
            } else {
                ufs.get(itMunicipio.getUf().getNmUF()).setQtdMunicipiosSemNotificacao(ufs.get(itMunicipio.getUf().getNmUF()).getQtdMunicipiosSemNotificacao() + 1);
            }
        }
        for (Iterator<Municipio> it = municipioBean.iterator(); it.hasNext();) {
            Municipio itMunicipio = it.next();
            itMunicipio.setUf(ufs.get(itMunicipio.getUf().getNmUF()));
        }
    }

    public HashMap<String, Municipio> getMunicipios(int UF, String regional, String sgUf, String codMunicipio) {
        DBFReader reader = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
        Object[] rowObjects;
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, Municipio> municipios = new HashMap<String, Municipio>();
        String nmMunicipio, idMunicipio, sgUfPar;
        String region, regiao;
        if (regional.length() > 0) {
            //busca municipios dessa regional
            DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");            
            try {
                utilDbf.mapearPosicoes(readerMunicipio);
                while ((rowObjects = readerMunicipio.nextRecord()) != null) {
                    nmMunicipio = utilDbf.getString(rowObjects, "NM_MUNICIP");
                    idMunicipio = utilDbf.getString(rowObjects, "ID_MUNICIP");
                    sgUfPar = utilDbf.getString(rowObjects, "SG_UF");
                    region = utilDbf.getString(rowObjects, "ID_REGIONA");
                    regiao = utilDbf.getString(rowObjects, "ID_REGIAO");
                    if (regional.equals(utilDbf.getString(rowObjects, "ID_REGIONA"))) {
                        if (!nmMunicipio.startsWith("IGNORADO") && !nmMunicipio.startsWith("MUNICIPIO") && nmMunicipio.lastIndexOf("TRANSF.") == -1 && nmMunicipio.lastIndexOf("ATUAL BENTO GONCALVES") == -1) {
                            if ((sgUfPar.equals("DF") && idMunicipio.equals("530010") && nmMunicipio.equals("BRASILIA")) || !sgUfPar.equals("DF")) {
                                if(!codMunicipio.equals("") && codMunicipio != null && idMunicipio.equals(codMunicipio)){
                                    municipios.put(idMunicipio, new Municipio(nmMunicipio, idMunicipio, sgUfPar, region, regiao));
                               }else if(codMunicipio.equals("") || codMunicipio == null){
                                   municipios.put(idMunicipio, new Municipio(nmMunicipio, idMunicipio, sgUfPar, region, regiao));
                               }
                            }
                        }
                    }
                }
            } catch (DBFException e) {
                Master.mensagem("Erro ao carregar municipios:\n" + e);
            }
        } else {
            try {
                utilDbf.mapearPosicoes(reader);
                while ((rowObjects = reader.nextRecord()) != null) {
                    nmMunicipio = utilDbf.getString(rowObjects, "NM_MUNICIP");
                    idMunicipio = utilDbf.getString(rowObjects, "ID_MUNICIP");
                    sgUfPar = utilDbf.getString(rowObjects, "SG_UF");
                    region = utilDbf.getString(rowObjects, "ID_REGIONA");
                    regiao = utilDbf.getString(rowObjects, "ID_REGIAO");
                    if (sgUf.equals(sgUfPar) || UF == 0) {
                        if (!nmMunicipio.startsWith("IGNORADO") && !nmMunicipio.startsWith("MUNICIPIO") && nmMunicipio.lastIndexOf("TRANSF.") == -1 && nmMunicipio.lastIndexOf("ATUAL BENTO GONCALVES") == -1) {
                            if ((sgUfPar.equals("DF") && idMunicipio.equals("530010") && nmMunicipio.equals("BRASILIA")) || !sgUfPar.equals("DF")) {
                               if(!codMunicipio.equals("") && codMunicipio != null && idMunicipio.equals(codMunicipio)){
                                    municipios.put(idMunicipio, new Municipio(nmMunicipio, idMunicipio, sgUfPar, region, regiao));
                               }else if(codMunicipio.equals("") || codMunicipio == null){
                                   municipios.put(idMunicipio, new Municipio(nmMunicipio, idMunicipio, sgUfPar, region, regiao));
                               }
                            }
                        }
                    }
                }
            } catch (DBFException e) {
                Master.mensagem("Erro ao carregar municipios:\n" + e);
                System.out.println("Erro ao carregar municipios: " + e);
            }
        }

        return municipios;
    }

    private void calcularQuantidadeMunicipios(HashMap<String, Municipio> municipios) {
        Collection<Municipio> municipioBean = municipios.values();
        HashMap<String, UF> ufs = new HashMap<String, UF>();
        for (Iterator<Municipio> it = municipioBean.iterator(); it.hasNext();) {
            Municipio itMunicipio = it.next();
            if (!ufs.containsKey(itMunicipio.getUf().getNmUF())) {
                UF uf = itMunicipio.getUf();
                uf.setQtdMunicipios(1);
                ufs.put(itMunicipio.getUf().getNmUF(), uf);
            } else {
                ufs.get(itMunicipio.getUf().getNmUF()).setQtdMunicipios(ufs.get(itMunicipio.getUf().getNmUF()).getQtdMunicipios() + 1);
            }
        }
        for (Iterator<Municipio> it = municipioBean.iterator(); it.hasNext();) {
            Municipio itMunicipio = it.next();
            itMunicipio.setUf(ufs.get(itMunicipio.getUf().getNmUF()));
        }
    }

    /**
     * @return the semanaInicioDbf
     */
    public String getSemanaInicioDbf() {
        return semanaInicioDbf;
    }

    /**
     * @param semanaInicioDbf the semanaInicioDbf to set
     */
    public void setSemanaInicioDbf(String semanaInicioDbf) {
        this.semanaInicioDbf = semanaInicioDbf;
    }

    /**
     * @return the semanaFimDbf
     */
    public String getSemanaFimDbf() {
        return semanaFimDbf;
    }

    /**
     * @param semanaFimDbf the semanaFimDbf to set
     */
    public void setSemanaFimDbf(String semanaFimDbf) {
        this.semanaFimDbf = semanaFimDbf;
    }
}
