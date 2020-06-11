/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.security.controller.common;

import com.unilever.autoclaims.exceptions.BusinessException;
import com.unilever.autoclaims.model.ConstantStrings;
import com.unilever.autoclaims.model.enums.ResponseMessageEnum;
import com.unilever.autoclaims.model.pojo.ResponsePojo;
import com.unilever.autoclaims.security.utils.JWTUtil;
import java.util.Arrays;
import java.util.Base64;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mahmoud.Elsaka
 */
@RestController
@RequestMapping("/logoutservice")
public class LogOutController {

    // <editor-fold defaultstate="collapsed" desc="WEB_LOGOUT">
    @RequestMapping(value = "/web/logout", method = RequestMethod.DELETE)
    public ResponseEntity webLogout(HttpServletRequest request, HttpServletResponse response, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization) {
        try {
            prepareLogout(request);
            return buildResponseEntity(true, null, ResponseMessageEnum.SUCCESS.getMessage(), HttpStatus.OK, headerAuthorization);
        } catch (Exception exception) {
            return buildExceptionResponseEntity(exception, headerAuthorization);
        }
    }
    //</editor-fold>

    private void prepareLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        SecurityContextHolder.clearContext();
        session = request.getSession(false);
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String userName = JWTUtil.getUsernameFromToken(jwtToken, ConstantStrings.SIGNING_KEY);
        if (session != null) {
            session.invalidate();
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                cookie.setMaxAge(0);
            }
        }
    }

    private ResponseEntity buildResponseEntity(boolean iSuccess, String Message, Object data, HttpStatus httpStatus, String headerAuthorization) {
        ResponsePojo responsePojo = buildResponsePojo(iSuccess, Message, data);
        String responsePojoString = "";
        String signKey = Base64.getEncoder().encodeToString(headerAuthorization.getBytes());
        responsePojoString = JWTUtil.encodeJWT(responsePojo, signKey.getBytes());
        return new ResponseEntity<>(responsePojoString, httpStatus);
    }

    private ResponsePojo buildResponsePojo(boolean iSuccess, String Message, Object data) {
        ResponsePojo responsePojo = new ResponsePojo(iSuccess, Message, data);
        return responsePojo;
    }

    private ResponseEntity buildExceptionResponseEntity(Exception exception, String headerAuthorization) {

        String exceptionMessage = exception.getMessage() != null ? exception.getMessage() : "";

        if (exception instanceof BusinessException) {
            BusinessException businessException = (BusinessException) exception;
            if (businessException.getParams() != null && businessException.getParams().length > 0) {
                exceptionMessage = exceptionMessage + "-" + Arrays.toString(businessException.getParams());
            }
        }
        return buildResponseEntity(false, exceptionMessage, null, HttpStatus.OK, headerAuthorization);

    }

}
