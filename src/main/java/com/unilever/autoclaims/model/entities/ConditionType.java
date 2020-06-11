/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.model.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Mahmoud.Elsaka
 */
@Entity
@Table(name = "CONDITION_TYPE")
@NamedQueries({
    @NamedQuery(name = "ConditionType.findAll", query = "SELECT c FROM ConditionType c")})
public class ConditionType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "SC_TYPEID")
    private String scTypeid;
    @Size(max = 100)
    @Column(name = "SC_TYPEDESCR")
    private String scTypedescr;
    @Size(max = 5)
    @Column(name = "CONDITION_TYPE")
    private String conditionType;
    @Size(max = 1)
    @Column(name = "ON_INVOICE")
    private String onInvoice;

    public ConditionType() {
    }

    public ConditionType(String scTypeid) {
        this.scTypeid = scTypeid;
    }

    public String getScTypeid() {
        return scTypeid;
    }

    public void setScTypeid(String scTypeid) {
        this.scTypeid = scTypeid;
    }

    public String getScTypedescr() {
        return scTypedescr;
    }

    public void setScTypedescr(String scTypedescr) {
        this.scTypedescr = scTypedescr;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getOnInvoice() {
        return onInvoice;
    }

    public void setOnInvoice(String onInvoice) {
        this.onInvoice = onInvoice;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (scTypeid != null ? scTypeid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConditionType)) {
            return false;
        }
        ConditionType other = (ConditionType) object;
        if ((this.scTypeid == null && other.scTypeid != null) || (this.scTypeid != null && !this.scTypeid.equals(other.scTypeid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.unilever.autoclaims.model.entities.autoclaims.ConditionType[ scTypeid=" + scTypeid + " ]";
    }

}
