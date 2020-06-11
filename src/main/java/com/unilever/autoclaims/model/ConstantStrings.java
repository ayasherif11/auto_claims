/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.model;

/**
 *
 * @author mahmoud.elsaka
 */
public abstract class ConstantStrings {

    public static final String ACTIVE = "Y";
    public static final String CONFIRMED = "Y";
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 30;
    public static final String SIGNING_KEY = "secretkey";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final int MIN_PASSWOED_LENGTH = 6;
    public static final String LOGIN_WEB_URL = "/user/login";
    public static final String LOGOUT_WEB_URL = "/logoutservice/web/logout";
    public static final String DATE_FORMATE = "dd/MM/yyyy";
    public static final String SUB_LOGOUT_URL = "logout";
    public static final String CASH_DISCOUNT = "Cash Discount";
    public static final String WHOLESALE_DISCOUNT = "WholeSale Discount";
    public static final String PROMOTION_DISCOUNT = "Promotion Discount";
    public static final String OTHER_DISCOUNT = "Other Discount";
    public static final String AUTO_CLAIMS = "A";
    public static final String MANUAL_CLAIMS = "M";
    public static final String EN_LANG = "en";

}
