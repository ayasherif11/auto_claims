/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.model.entities;

import java.io.Serializable;
import java.util.Objects;
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
@Table(name = "V$CUMM_DIST_GRID")
@NamedQueries({
    @NamedQuery(name = "VCummDistGrid.findAll", query = "SELECT v FROM VCummDistGrid v")})
public class VCummDistGrid implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "BRANCHNO")
    @Id
    private String branchno;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "BRANCHNAMEE")
    private String branchnamee;
    @Size(max = 10)
    @Column(name = "SAP_CODE")
    private String sapCode;
    @Size(max = 15)
    @Column(name = "REGION")
    private String region;
    @Size(max = 15)
    @Column(name = "TYPE")
    private String type;
    @Size(max = 1)
    @Column(name = "SEL")
    private String selected;

    public VCummDistGrid() {
    }

    public String getBranchno() {
        return branchno;
    }

    public void setBranchno(String branchno) {
        this.branchno = branchno;
    }

    public String getBranchnamee() {
        return branchnamee;
    }

    public void setBranchnamee(String branchnamee) {
        this.branchnamee = branchnamee;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.branchno);
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
        final VCummDistGrid other = (VCummDistGrid) obj;
        if (!Objects.equals(this.branchno, other.branchno)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "VCummDistGrid{" + "branchno=" + branchno + '}';
    }

}
