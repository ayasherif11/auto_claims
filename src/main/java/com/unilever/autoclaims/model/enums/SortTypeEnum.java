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
public enum SortTypeEnum {
    ASC("asc"),
    DESC("desc");

    private final String sortType;

    private SortTypeEnum(String sortType) {
        this.sortType = sortType;
    }

    public String getSortType() {
        return sortType;
    }
}
