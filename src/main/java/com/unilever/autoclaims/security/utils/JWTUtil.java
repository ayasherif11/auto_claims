/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.security.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.unilever.autoclaims.model.ConstantStrings;
import com.unilever.autoclaims.security.SecurityConstants;
import com.unilever.autoclaims.utils.DateUtilities;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Mahmoud.Elsaka
 */
public class JWTUtil {

    static SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    public static final String DEFAULT_KEY = "YnZjeGJ2Y3hidmNJTG92ZVNlY3VyaXR5ZmFld29pamV3ZXdxcmV3";
    public static final String DATA = "data";
    private static final String USER_NAME_CLAIM = "code";

    public static String getUsernameFromToken(Claims claims) {
        String userName = claims.get(USER_NAME_CLAIM, String.class);
        return userName;
    }

    public static String getUsernameFromToken(String jwtString, String secretKeyString) {
        Claims claims = Jwts.parser().setSigningKey(secretKeyString).parseClaimsJws(jwtString).getBody();
        return JWTUtil.getUsernameFromToken(claims);

    }

    public static String generateToken(String userName) {
           String jwtToken = "";
        JwtBuilder jwtBuilder = Jwts.builder()
                .claim(USER_NAME_CLAIM, userName)
                .signWith(SignatureAlgorithm.HS256, ConstantStrings.SIGNING_KEY)
                .setExpiration(DateUtilities.addDays(new Date(), 30));
        jwtToken = jwtBuilder.compact();
        return jwtToken;
        /////////
        
//        String jwtToken = null;
//        try {
//            Algorithm algorithm = Algorithm.HMAC256(ConstantStrings.SIGNING_KEY);
//            jwtToken = JWT.create()
//                    .withClaim(USER_NAME_CLAIM, userName.trim().toLowerCase())
//                    .withExpiresAt(DateUtilities.addDays(new Date(), 30))
//                    .sign(algorithm);
//        } catch (JWTCreationException exception) {
//            //Invalid Signing configuration / Couldn't convert Claims.
//        }
//        return jwtToken;
    }

    public static String encodeJWT(Object data, byte[] apiKeySecretBytes) {
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        String jwtString = Jwts.builder()
                .claim(DATA, data)
                .signWith(signatureAlgorithm, signingKey)
                .setIssuedAt(new Date())
                .setExpiration(DateUtilities.addDays(new Date(), SecurityConstants.EXPIRATION_DAYS))
                .compact();
        return jwtString;
    }

    public static String decodeJWT(String jwtString, byte[] secretKeyByte) {
        return decodeJWT(jwtString, secretKeyByte, DATA);
    }

    public static String decodeJWT(String jwtString, byte[] secretKeyByte, String claimName) {
        Key signingKey = new SecretKeySpec(secretKeyByte, signatureAlgorithm.getJcaName());
        String string = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(jwtString)
                .getBody()
                .get(claimName, String.class);
        return string;
    }

    public static String getHeaderInfoByName(HttpServletRequest request, String headerKeyName) {
        return request.getHeader(headerKeyName);
    }

    public static byte[] getJwtKey(HttpServletRequest request) {
        return DEFAULT_KEY.getBytes();
    }

    public static boolean isValidToken(String token) {
        return !isTokenExpired(token);
    }

    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public static Date getExpirationDateFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getExpiresAt();
    }
}
