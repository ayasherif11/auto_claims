/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.model.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author mahmoud.elsaka
 */
@Entity
@Table(name = "CUMM_DIST")
//@NamedNativeQuery(
//        name = "U2K2ClaimsDataDTO",
//        query
//        = "SELECT "
//        + "       d.CONDITIONTYPE AS conditionType , d.ITEMNO AS itemNo , "
//        + "       d.DISCOUNT AS discount , d.DISCOUNTTYPE AS discountType ,  "
//        + "       d.DISTNO AS distNo , d.PERIOD AS period ,  d.INTERNAL_ORDER As internalOrder "
//        + "FROM U2K2_CLAIMS_DATA d "
//        + "WHERE d.PERIOD = :period ",
//        resultSetMapping = "U2K2ClaimsDataDTO"
//)
//@SqlResultSetMapping(
//        name = "U2K2ClaimsDataDTO",
//        classes = @ConstructorResult(
//                targetClass = U2K2ClaimsDataDTO.class,
//                columns = {
//                    @ColumnResult(name = "conditionType")
//                    ,
//                    @ColumnResult(name = "itemNo")
//                    ,
//                    @ColumnResult(name = "discount")
//                    ,
//                    @ColumnResult(name = "discountType")
//                    ,
//                    @ColumnResult(name = "distNo")
//                    ,
//                    @ColumnResult(name = "period")
//                    ,
//                    @ColumnResult(name = "internalOrder")
//                }
//        )
//)

public class CummDist implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Size(max = 8)
    @NotNull
    @Column(name = "DISTNO")
    private String distNo;

    @Size(max = 10)
    @Column(name = "SCH")
    private String sch;

    @Column(name = "TOTAL_SALES")
    private BigDecimal totalSales;

    @Size(max = 1)
    @Column(name = "SEL")
    private String selected;

    public CummDist() {
    }

    public CummDist(String distNo, String sch) {
        this.distNo = distNo;
        this.sch = sch;
    }

    public String getDistNo() {
        return distNo;
    }

    public void setDistNo(String distNo) {
        this.distNo = distNo;
    }

    public String getSch() {
        return sch;
    }

    public void setSch(String sch) {
        this.sch = sch;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (distNo != null ? distNo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CummDist)) {
            return false;
        }
        CummDist other = (CummDist) object;
        if ((this.distNo == null && other.distNo != null) || (this.distNo != null && !this.distNo.equals(other.distNo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.unilever.autoclaims.model.entities.autoclaims.CummDist[ distNo=" + distNo + " ]";
    }

}
