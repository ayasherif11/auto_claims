/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.model.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Mahmoud.Elsaka
 */
@Embeddable
public class U2k2ClaimsDataPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 4)
    @Column(name = "CONDITIONTYPE")
    private String conditiontype;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "ITEMNO")
    private String itemno;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "DISCOUNTTYPE")
    private String discounttype;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "DISTNO")
    private String distno;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "PERIOD")
    private String period;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "INTERNAL_ORDER")
    private String internalOrder;

    public U2k2ClaimsDataPK() {
    }

    public U2k2ClaimsDataPK(String conditiontype, String itemno, String discounttype, String distno, String period, String internalOrder) {
        this.conditiontype = conditiontype;
        this.itemno = itemno;
        this.discounttype = discounttype;
        this.distno = distno;
        this.period = period;
        this.internalOrder = internalOrder;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conditiontype != null ? conditiontype.hashCode() : 0);
        hash += (itemno != null ? itemno.hashCode() : 0);
        hash += (discounttype != null ? discounttype.hashCode() : 0);
        hash += (distno != null ? distno.hashCode() : 0);
        hash += (period != null ? period.hashCode() : 0);
        hash += (internalOrder != null ? internalOrder.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof U2k2ClaimsDataPK)) {
            return false;
        }
        U2k2ClaimsDataPK other = (U2k2ClaimsDataPK) object;
        if ((this.conditiontype == null && other.conditiontype != null) || (this.conditiontype != null && !this.conditiontype.equals(other.conditiontype))) {
            return false;
        }
        if ((this.itemno == null && other.itemno != null) || (this.itemno != null && !this.itemno.equals(other.itemno))) {
            return false;
        }
        if ((this.discounttype == null && other.discounttype != null) || (this.discounttype != null && !this.discounttype.equals(other.discounttype))) {
            return false;
        }
        if ((this.distno == null && other.distno != null) || (this.distno != null && !this.distno.equals(other.distno))) {
            return false;
        }
        if ((this.period == null && other.period != null) || (this.period != null && !this.period.equals(other.period))) {
            return false;
        }
        if ((this.internalOrder == null && other.internalOrder != null) || (this.internalOrder != null && !this.internalOrder.equals(other.internalOrder))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.unilever.autoclaims.model.entities.U2k2ClaimsDataPK[ conditiontype=" + conditiontype + ", itemno=" + itemno + ", discounttype=" + discounttype + ", distno=" + distno + ", period=" + period + ", internalOrder=" + internalOrder + " ]";
    }
}
