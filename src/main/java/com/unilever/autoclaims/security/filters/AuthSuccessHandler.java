/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.security.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unilever.autoclaims.model.pojo.ResponsePojo;
import com.unilever.autoclaims.security.pojo.UserLoginResponse;
import com.unilever.autoclaims.security.utils.JWTUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mahmoud.Elsaka
 */
@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest hsr, HttpServletResponse hsr1, Authentication authResult) throws IOException, ServletException {
        PrintWriter out = hsr1.getWriter();
        out.print(prepareUserLoginResponse(hsr, authResult));
    }

    private String prepareUserLoginResponse(HttpServletRequest hsr, Authentication authResult)
            throws JsonProcessingException {
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        ResponsePojo responsePojo = new ResponsePojo();
        String username = (String) authResult.getPrincipal();

        String jwtToken = JWTUtil.generateToken(username);

        userLoginResponse.setToken(jwtToken);
        userLoginResponse.setUsername(username);

        responsePojo.setSuccess(true);
        responsePojo.setData(userLoginResponse);

        String encodedResponse = JWTUtil.encodeJWT(responsePojo, JWTUtil.getJwtKey(hsr));
        return encodedResponse;
    }
}
