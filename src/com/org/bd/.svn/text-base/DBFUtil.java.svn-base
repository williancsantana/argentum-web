/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.bd;

import com.linuxense.javadbf.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;

/**
 *
 * @author Geraldo
 */
public class DBFUtil extends DBFBase {

    private HashMap headers = new HashMap();
    Object linha = new Object();

    public String getString(Object[] rowObjects, String campo) {
        if (rowObjects[Integer.parseInt(getHeaders().get(campo).toString())] == null) {
            return null;
        } else {
            String retorno = rowObjects[Integer.parseInt(getHeaders().get(campo).toString())].toString().trim();
            if (retorno.isEmpty()) {
                retorno = null;
            }
            //se for ID_MUNICIP ou ID_MN_RES, E SE 539 COLOCAR COMO CODIGO = 530010
            if (retorno != null) {
                if ((campo.equals("ID_MUNICIP") || campo.equals("ID_MUNIC_2") || campo.equals("ID_MUNI_AT") || campo.equals("ID_MN_RESI")) && retorno.substring(0, 3).equals("539")) {
                    retorno = "530010";
                }
            }
            return retorno;
        }
    }

    public String getString(Object[] rowObjects, String campo, int size) {
        if (rowObjects[Integer.parseInt(getHeaders().get(campo).toString())] == null) {
            return null;
        } else {

            String retorno = rowObjects[Integer.parseInt(getHeaders().get(campo).toString())].toString().trim();
            if (size > retorno.length()) {
                size = retorno.length();
            }
            retorno = retorno.substring(0, size);

            if (retorno.isEmpty()) {
                retorno = null;
            }
            return retorno;
        }
    }

    public int getInt(Object[] rowObjects, String campo) {
        if (rowObjects[Integer.parseInt(getHeaders().get(campo).toString())] == null) {
            return -1;
        } else {
            String retorno = rowObjects[Integer.parseInt(getHeaders().get(campo).toString())].toString().trim();
            if (retorno.equals("")) {
                return -1;
            } else {
                if (retorno.contains(".")) {
                    retorno = retorno.substring(0, retorno.length() - 2);
                }
                return Integer.parseInt(retorno);
            }
        }

    }

    public java.sql.Date getDate(Object[] rowObjects, String campo) throws NumberFormatException, ParseException {
        if (rowObjects[Integer.parseInt(getHeaders().get(campo).toString())] == null) {
            return null;
        }
        String data = rowObjects[Integer.parseInt(getHeaders().get(campo).toString())].toString();

        String mes = data.substring(4, 7);
        String dia = data.substring(8, 10);
        String ano = data.substring(data.length() - 5, data.length());

        String str = mes + " " + dia + ", " + ano;
        DateFormat dt = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        try {
            java.util.Date date = dt.parse(str);
            return new java.sql.Date(date.getTime());
        } catch (ParseException e) {
            System.out.println(e);
            return null;
        }
    }

    public void mapearPosicoes(DBFReader reader) throws DBFException {
        int numberOfFields = reader.getFieldCount();
        for (int i = 0; i < numberOfFields; i++) {
            DBFField field = reader.getField(i);
            getHeaders().put(field.getName(), i);
        }
    }

    /**
     * @return the headers
     */
    public HashMap getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(HashMap headers) {
        this.headers = headers;
    }
}
