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
public enum FilterTypeEnum {
    CONTAINS("1"),
    EQUALS("2"),
    NOT_EQUALS("3"),
    STARTS_WITH("4"),
    ENDS_WITH("5"),
    GREATERTHAN("6"),
    LESSTHAN("7"),
    GREATERTHANOREQUAL("8"),
    LESSTHANOOREQUAL("9");

    private final String filterType;

    private FilterTypeEnum(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterType() {
        return filterType;
    }
}
