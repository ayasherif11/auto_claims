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
public enum CrudOpTypesEnum {
    CREATE(1),
    EDIT(2),
    DELETE(3),
    FIND(4);

    private final int crudTypeId;

    private CrudOpTypesEnum(int crudTypeId) {
        this.crudTypeId = crudTypeId;
    }

    public int getCrudTypeId() {
        return this.crudTypeId;
    }
}
