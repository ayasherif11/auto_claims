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
public class ResponsePojo {

    private boolean success;
    private String exceptionMessage;
    private Object data;

    public ResponsePojo(boolean success, String exceptionMessage, Object data) {
        this.success = success;
        this.exceptionMessage = exceptionMessage;
        this.data = data;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public ResponsePojo() {
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
