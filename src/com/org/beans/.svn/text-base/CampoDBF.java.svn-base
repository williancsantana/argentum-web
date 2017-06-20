/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.beans;

import com.linuxense.javadbf.DBFField;

/**
 *
 * @author Taidson
 */
public class CampoDBF extends DBFField{

    
    public CampoDBF(String name, String type, int length, int decimalCount) {
        super.setName(name);
        if(type.equals("N")){
            if (decimalCount > 0) {
                super.setDataType(FIELD_TYPE_F);
            }else{
                super.setDataType(FIELD_TYPE_N);
            }
        }else{
            super.setDataType(FIELD_TYPE_C);
        }
        super.setFieldLength(length);
    }
    
}
