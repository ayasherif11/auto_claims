/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.model.dto.callDB;

import java.io.Serializable;
import javax.persistence.ParameterMode;

/**
 *
 * @author mahmoud.elsaka
 */
public class CallDBParDTO implements Serializable {

    private ParameterMode parameterMode;
    private Object ParValue;

    public CallDBParDTO() {
    }

    public Object getParValue() {
        return ParValue;
    }

    public void setParValue(Object ParValue) {
        this.ParValue = ParValue;
    }

    public ParameterMode getParameterMode() {
        return parameterMode;
    }

    public void setParameterMode(ParameterMode parameterMode) {
        this.parameterMode = parameterMode;
    }

}
