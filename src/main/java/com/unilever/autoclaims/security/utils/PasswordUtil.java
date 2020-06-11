/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.security.utils;

import java.util.Base64;

/**
 *
 * @author Mahmoud.Elsaka
 */
public class PasswordUtil {

    public static String decrypt(final String hash, String key) {
        try {
            return new String(xor(Base64.getDecoder().decode(hash.getBytes()), key), "UTF-8");
        } catch (java.io.UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static byte[] xor(final byte[] input, String key) {
        final byte[] output = new byte[input.length];
        final byte[] secret = key.getBytes();
        int spos = 0;
        for (int pos = 0; pos < input.length; ++pos) {
            output[pos] = (byte) (input[pos] ^ secret[spos]);
            spos += 1;
            if (spos >= secret.length) {
                spos = 0;
            }
        }
        return output;
    }

}
