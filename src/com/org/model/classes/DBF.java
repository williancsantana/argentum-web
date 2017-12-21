/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.classes;

import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import com.org.negocio.Configuracao;
import com.org.util.ArquivoUtils;
import com.org.view.Master;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author geraldo
 */
public class DBF {

    public void beanToDbf(HashMap<String, ColunasDbf> colunas, List bean, Agravo agravo) throws IOException {
        try {
            DBFField fields[] = getCabecalho(colunas, agravo.getOrdemColunas());
            DBFWriter writer = new DBFWriter();
            writer.setFields(fields);

            // now populate DBFWriter
            //verificar se precisa retornar o writer novamente
            agravo.getLinhas(colunas, bean, writer);

            FileOutputStream fos = new FileOutputStream(Configuracao.getPropriedade("arquivo"));
            writer.write(fos);
            fos.close();
            System.out.println("ok");
        } catch (RuntimeException DBFException) {
            DBFException.printStackTrace();
            ArquivoUtils.gerarLogErro(DBFException);
            Master.mensagem("Ocorreu um erro ao gerar o DBF. Consulte o arquivo de log e informe ao suporte do Sinan Relatórios.");
        }

    }

    public DBFField[] getCabecalho(HashMap<String, ColunasDbf> colunas, String[] refColunas) {
        DBFField fields[] = new DBFField[colunas.size()];
//        int k = 0;
        String str;

         for (int k = 0; k < refColunas.length; k++) {

             fields[k] = new DBFField();
             str = refColunas[k];
             fields[k].setName(str);
            if (colunas.get(str).getTipo().equals("N")) {
                if (colunas.get(str).getPrecisao() > 0) {
                    fields[k].setDataType(DBFField.FIELD_TYPE_F);
                } else {
                    fields[k].setDataType(DBFField.FIELD_TYPE_N);
                }
                fields[k].setFieldLength(colunas.get(str).getTamanho());
                fields[k].setDecimalCount(colunas.get(str).getPrecisao());
            } else {
                fields[k].setDataType(DBFField.FIELD_TYPE_C);
                fields[k].setFieldLength(colunas.get(str).getTamanho());
            }

             System.out.println(str + ": " + str);
        }
        return fields;
    }
}
