/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.utils.excel;

public class Utilities {

    public static Double nvl(Double value, Double alternateValue) {
        if (value == null) {
            return alternateValue;
        }
        return value;
    }

    public static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

}
