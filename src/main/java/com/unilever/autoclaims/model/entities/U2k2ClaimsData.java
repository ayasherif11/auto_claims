/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.model.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Mahmoud.Elsaka
 */
@Entity
@Table(name = "U2K2_CLAIMS_DATA")
@NamedQueries({
    @NamedQuery(name = "U2k2ClaimsData.findAll", query = "SELECT u FROM U2k2ClaimsData u")})
@IdClass(U2k2ClaimsDataPK.class)
public class U2k2ClaimsData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 4)
    @Column(name = "CONDITIONTYPE")
    private String conditiontype;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "ITEMNO")
    private String itemno;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "DISCOUNTTYPE")
    private String discounttype;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "DISTNO")
    private String distno;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "PERIOD")
    private String period;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "INTERNAL_ORDER")
    private String internalOrder;

//    @EmbeddedId
//    protected U2k2ClaimsDataPK u2k2ClaimsDataPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "DISCOUNT")
    private BigDecimal discount;
    
    @Transient
    private String checkVal ="true";

    public U2k2ClaimsData() {
    }

//    public U2k2ClaimsData(U2k2ClaimsDataPK u2k2ClaimsDataPK) {
//        this.u2k2ClaimsDataPK = u2k2ClaimsDataPK;
//    }
//
//    public U2k2ClaimsData(String conditiontype, String itemno, String discounttype, String distno, String period, String internalOrder) {
//        this.u2k2ClaimsDataPK = new U2k2ClaimsDataPK(conditiontype, itemno, discounttype, distno, period, internalOrder);
//    }

//    public U2k2ClaimsDataPK getU2k2ClaimsDataPK() {
//        return u2k2ClaimsDataPK;
//    }
//
//    public void setU2k2ClaimsDataPK(U2k2ClaimsDataPK u2k2ClaimsDataPK) {
//        this.u2k2ClaimsDataPK = u2k2ClaimsDataPK;
//    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getConditiontype() {
        return conditiontype;
    }

    public void setConditiontype(String conditiontype) {
        this.conditiontype = conditiontype;
    }

    public String getItemno() {
        return itemno;
    }

    public void setItemno(String itemno) {
        this.itemno = itemno;
    }

    public String getDiscounttype() {
        return discounttype;
    }

    public void setDiscounttype(String discounttype) {
        this.discounttype = discounttype;
    }

    public String getDistno() {
        return distno;
    }

    public void setDistno(String distno) {
        this.distno = distno;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getInternalOrder() {
        return internalOrder;
    }

    public void setInternalOrder(String internalOrder) {
        this.internalOrder = internalOrder;
    }

    public String getCheckVal() {
        return checkVal;
    }

    public void setCheckVal(String checkVal) {
        this.checkVal = checkVal;
    }
    
//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (u2k2ClaimsDataPK != null ? u2k2ClaimsDataPK.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof U2k2ClaimsData)) {
//            return false;
//        }
//        U2k2ClaimsData other = (U2k2ClaimsData) object;
//        if ((this.u2k2ClaimsDataPK == null && other.u2k2ClaimsDataPK != null) || (this.u2k2ClaimsDataPK != null && !this.u2k2ClaimsDataPK.equals(other.u2k2ClaimsDataPK))) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return "com.unilever.autoclaims.model.entities.U2k2ClaimsData[ u2k2ClaimsDataPK=" + u2k2ClaimsDataPK + " ]";
//    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.conditiontype);
        hash = 59 * hash + Objects.hashCode(this.itemno);
        hash = 59 * hash + Objects.hashCode(this.discounttype);
        hash = 59 * hash + Objects.hashCode(this.distno);
        hash = 59 * hash + Objects.hashCode(this.period);
        hash = 59 * hash + Objects.hashCode(this.internalOrder);
        hash = 59 * hash + Objects.hashCode(this.discount);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final U2k2ClaimsData other = (U2k2ClaimsData) obj;
        if (!Objects.equals(this.conditiontype, other.conditiontype)) {
            return false;
        }
        if (!Objects.equals(this.itemno, other.itemno)) {
            return false;
        }
        if (!Objects.equals(this.discounttype, other.discounttype)) {
            return false;
        }
        if (!Objects.equals(this.distno, other.distno)) {
            return false;
        }
        if (!Objects.equals(this.period, other.period)) {
            return false;
        }
        if (!Objects.equals(this.internalOrder, other.internalOrder)) {
            return false;
        }
        if (!Objects.equals(this.discount, other.discount)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "U2k2ClaimsData{" + "conditiontype=" + conditiontype + ", itemno=" + itemno + ", discounttype=" + discounttype + ", distno=" + distno + ", period=" + period + ", internalOrder=" + internalOrder + ", discount=" + discount + '}';
    }
    
    
}
