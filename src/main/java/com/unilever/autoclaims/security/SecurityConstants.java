/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.security;

public class SecurityConstants {

    public static final String SECRET = "secretkey";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_AUTH_STRING = "Authorization";
    public static final String LOGIN_HTTP_METHOD = "POST";
    public final static Integer EXPIRATION_DAYS = 30;
    public final static String ERROR_URL = "/error";
}
