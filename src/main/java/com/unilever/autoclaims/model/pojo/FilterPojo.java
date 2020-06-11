/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.model.pojo;

/**
 *
 * @author Mahmoud.Elsaka
 */
public class FilterPojo {

    private String fieldName;
    private String filter;
    private String type;

    public FilterPojo() {
    }

    public FilterPojo(String fieldName, String filter, String type) {
        this.fieldName = fieldName;
        this.filter = filter;
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
