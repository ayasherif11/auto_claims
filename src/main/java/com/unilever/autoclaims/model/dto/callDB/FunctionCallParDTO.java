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
public class FunctionCallParDTO extends CallDBParDTO implements Serializable {

    private int parType;

    public FunctionCallParDTO() {
    }

    public int getParType() {
        return parType;
    }

    public void setParType(int parType) {
        this.parType = parType;
    }

}
