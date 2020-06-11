/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.model.dto.callDB;

import java.io.Serializable;

/**
 *
 * @author mahmoud.elsaka
 */
public class ProcdureCallParDTO extends CallDBParDTO implements Serializable {

    private String parName;
    private Class parType;

    public ProcdureCallParDTO() {
    }

    public Class getParType() {
        return parType;
    }

    public void setParType(Class parType) {
        this.parType = parType;
    }

    public String getParName() {
        return parName;
    }

    public void setParName(String parName) {
        this.parName = parName;
    }

}
