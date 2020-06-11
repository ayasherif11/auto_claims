/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.model.dto;

import java.math.BigDecimal;

/**
 *
 * @author mahmoud.elsaka
 */
public class ClaimsDiscountSumDto {

    private BigDecimal discountWH;
    private BigDecimal discountCash;
    private BigDecimal discountCoupons;
    private BigDecimal discountOther;

    public ClaimsDiscountSumDto() {
    }

    public ClaimsDiscountSumDto(BigDecimal discountWH, BigDecimal discountCash, BigDecimal discountCoupons, BigDecimal discountOther) {
        this.discountWH = discountWH;
        this.discountCash = discountCash;
        this.discountCoupons = discountCoupons;
        this.discountOther = discountOther;
    }

    public BigDecimal getDiscountWH() {
        return discountWH;
    }

    public void setDiscountWH(BigDecimal discountWH) {
        this.discountWH = discountWH;
    }

    public BigDecimal getDiscountCash() {
        return discountCash;
    }

    public void setDiscountCash(BigDecimal discountCash) {
        this.discountCash = discountCash;
    }

    public BigDecimal getDiscountCoupons() {
        return discountCoupons;
    }

    public void setDiscountCoupons(BigDecimal discountCoupons) {
        this.discountCoupons = discountCoupons;
    }

    public BigDecimal getDiscountOther() {
        return discountOther;
    }

    public void setDiscountOther(BigDecimal discountOther) {
        this.discountOther = discountOther;
    }
}
