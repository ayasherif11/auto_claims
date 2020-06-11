/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.security.config;

import com.unilever.autoclaims.model.ConstantStrings;
import com.unilever.autoclaims.security.filters.AuthSuccessHandler;
import com.unilever.autoclaims.security.filters.AuthUnsuccessHandler;
import com.unilever.autoclaims.security.filters.JWTAuthenticationFilter;
import com.unilever.autoclaims.security.filters.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author Mahmoud.Elsaka
 */
@EnableWebSecurity
public class webSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthSuccessHandler authSuccessHandler;

    @Autowired
    private AuthUnsuccessHandler authUnsuccessHandler;
    @Autowired
    private LdapAuthenticationProvider ldapAuthenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ldapAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        JWTAuthenticationFilter authenticationFilter = new JWTAuthenticationFilter(authenticationManager());
        authenticationFilter.setAuthenticationSuccessHandler(authSuccessHandler);
        authenticationFilter.setAuthenticationFailureHandler(authUnsuccessHandler);
        JWTAuthorizationFilter authorizationFilter = new JWTAuthorizationFilter(authenticationManager());
        http.cors().and()
                .csrf().disable().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers(HttpMethod.POST, ConstantStrings.LOGIN_WEB_URL).permitAll()
                .antMatchers(HttpMethod.DELETE, ConstantStrings.LOGOUT_WEB_URL).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(authenticationFilter)
                .addFilter(authorizationFilter);
    }

    //https://spring.io/blog/2015/06/08/cors-support-in-spring-framework
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods(HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name());
            }

        };
    }

    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }

}
