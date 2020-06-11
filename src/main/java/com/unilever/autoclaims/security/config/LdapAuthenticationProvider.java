/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.security.config;

import com.unilever.autoclaims.business.dao.repositories.ParametersRepository;
import com.unilever.autoclaims.model.entities.IntSettings;
import com.unilever.autoclaims.security.utils.ldap.CustomSSLSocketFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * This Is LDAP Class for implement Authentication Provider
 *
 * @author Mahmoud.Elsaka
 */
//https://stackoverflow.com/questions/49622927/login-form-user-credentials-instead-of-hard-coded-userdn-and-password-in-ldap-sp
@Component
public class LdapAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ParametersRepository parametersRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapAuthenticationProvider.class);
    private static final String AT_DOMAIN_COM = "@unilever.com";
    private static Map<String, String> configurationMap;

    @PostConstruct
    public void inti() {
        List<IntSettings> parametersList = parametersRepository.findAll();
        configurationMap = new HashMap<>();

        if (parametersList != null & !parametersList.isEmpty()) {
            parametersList.stream().forEach((item) -> {
                configurationMap.put(item.getCode(), item.getValue());
            });
        }
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {

        String username = auth.getName();
        String password = auth.getCredentials().toString();

        if (isLdapRegisteredUser(username, password)) {
            // use the credentials and authenticate against a third-party system
            return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
        } else {
            return null;
        }

    }

    boolean isLdapRegisteredUser(String username, String password) {

        boolean result = false;
        Hashtable<String, String> env = new Hashtable<>();
        LdapContext ctx = null;

        try {

            if (!username.contains("@")) {
                username = username + AT_DOMAIN_COM;
            }

            CustomSSLSocketFactory.sslUrl = this.getLDAPHTTPSURL();
            env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(javax.naming.Context.SECURITY_AUTHENTICATION, "simple");
            env.put(javax.naming.Context.REFERRAL, "follow");
            env.put("java.naming.ldap.factory.socket", "com.unilever.autoclaims.security.utils.ldap.CustomSSLSocketFactory");
            env.put(Context.SECURITY_PRINCIPAL, username);
            env.put(Context.SECURITY_CREDENTIALS, password);
            env.put(Context.PROVIDER_URL, this.getLDAPURL());

            ctx = new InitialLdapContext(env, null);

            if (ctx != null) {
                result = true;
                // do further operations with "ctx" if needed
                System.out.println("Connection Successful!");
            }

        } catch (NamingException ex) {
            String detailmessage = ex.getExplanation();
            if (detailmessage.contains(" 49 ")) {

                if (detailmessage.contains("52e")) {
                    LOGGER.error("Invalid password : XXXXXX", ex);
                    throw new UsernameNotFoundException("INVALID_CREDENTIALS");
                } else if (detailmessage.contains("525")) {
                    LOGGER.error("Invalid username : " + username, ex);
                    throw new UsernameNotFoundException("USER_NOT_FOUND");
                } else if (detailmessage.contains("533")) {
                    LOGGER.error("ACCOUNT DISABLED : " + username, ex);
                    throw new UsernameNotFoundException("ACCOUNT_DISABLED");
                } else if (detailmessage.contains("701")) {
                    LOGGER.error("ACCOUNT EXPIRED : " + username, ex);
                    throw new UsernameNotFoundException("ACCOUNT_EXPIRED");
                } else if (detailmessage.contains("773")) {
                    LOGGER.error("USER MUST RESET PASSWORD : " + username, ex);
                    throw new UsernameNotFoundException("USER_MUST_RESET_PASSWORD");
                } else {
                    LOGGER.error("error_general", ex);
                    throw new UsernameNotFoundException("error_general");
                }
            } else {
                LOGGER.error("error_general", ex);
                throw new UsernameNotFoundException("error_general");
            }
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception ex) {
                }
            }
        }

        return result;

    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }

    public String getLDAPURL() {
        return configurationMap.get("LDAP_URL");
    }

    public String getLDAPHTTPSURL() {
        return configurationMap.get("LDAP_HTTPS_URL");
    }
}
