/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.model.dto.common;

/**
 *
 * @author mahmoud.elsaka
 */
/**
 * this class to get the return from the PLSQl function a and split the Error
 * Msg and the arg to it
 */
public class ErrorDto {

    /**
     * we use it as separtor in the String to parse the error message and get
     * the the element of the Error
     *
     * The Error Msg will be format as follow
     *
     * Msg_KEY--x--NUmberOfArgument--x--arg0--x--arg1.....--x--argn--x--MSG_KEY(for
     * the second Error) --- e.g ORDER_DELETE_ERROR_2_10_90
     *
     */
    public static final String MESSAGE_SPLITTER = "--x--";
    public static final String MESSAGE_SPLITTER_CAPITAL = "--X--";
    public static final int USER_MESSAGE = 1;
    public static final int LOG_MESSAGE = 2;
    public static final int ACTION_MESSAGE = 3;

    public ErrorDto() {
    }
    /**
     * the message Key
     */
    private String errorMsgKey;
    /**
     * the array of arguments
     */
    private String[] args;

    /**
     * the type of the Message (Error or Information Message only )
     */
    private int MsgType;//false to Error and true to Information

    public void setErrorMsgKey(String errorMsgKey) {
        this.errorMsgKey = errorMsgKey;
    }

    public String getErrorMsgKey() {
        return errorMsgKey;
    }

    public void setArgs(String[] items, int startIndex, int numberOfargument) {
        String[] errorArgs = new String[100];
        for (int i = 0; i < numberOfargument; i++) {
            errorArgs[i] = items[startIndex + i];
        }
        this.args = errorArgs;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setMsgType(int MsgType) {
        this.MsgType = MsgType;
    }

    public int getMsgType() {
        return MsgType;
    }

}
