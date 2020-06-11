/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.security.filters;

import com.unilever.autoclaims.model.ConstantStrings;
import com.unilever.autoclaims.security.utils.JWTUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 *
 * @author Mahmoud.Elsaka
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        String jwtToken = req.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwtToken == null) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(jwtToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            //ignore logout service from  token validation which will be check for remove
            if (!req.getRequestURI().contains(ConstantStrings.SUB_LOGOUT_URL)) {
                isValid(jwtToken);
            }
        } catch (AccessDeniedException e) {
            SecurityContextHolder.clearContext();
            Logger.getLogger("Stolen Token :" + jwtToken).log(Level.SEVERE, null, e);
        }
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String jwtToken) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;
        if (jwtToken != null) {
            String user = JWTUtil.getUsernameFromToken(jwtToken, ConstantStrings.SIGNING_KEY);
            if (user != null) {
                usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, null);
            }
        }
        return usernamePasswordAuthenticationToken;
    }

    private void isValid(String jwtToken) {
        boolean isValidUser = false;
        try {
            isValidUser = JWTUtil.isValidToken(jwtToken);
        } catch (Exception ex) {
            throw new AccessDeniedException("Access_Denied");
        }
        if (!isValidUser) {
            throw new AccessDeniedException("Access_Denied");
        }
    }
}
