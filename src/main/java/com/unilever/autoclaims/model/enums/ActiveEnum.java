/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.model.enums;

/**
 *
 * @author Mahmoud.Elsaka
 */
public enum ActiveEnum {
    ACTIVE(1),
    INACTIVE(0);

    private final Integer activeFlag;

    private ActiveEnum(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Integer getActiveFlag() {
        return activeFlag;
    }
}
