/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.business.dao.repositories;

import com.unilever.autoclaims.business.dao.common.GenericJPARepository;
import com.unilever.autoclaims.model.entities.U2k2ClaimsData;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Mahmoud.Elsaka
 */
public interface U2K2ClaimsDataRepository extends GenericJPARepository<U2k2ClaimsData> {

    @Query("SELECT sum(c.discount) FROM U2k2ClaimsData c WHERE c.period = :period  and c.distno = :distno "
            + "and c.discounttype in (:discounttype)")
    public BigDecimal calculateClaimsDiscoutSumByDiscountType(@Param("period") String period, @Param("distno") String distno,
            @Param("discounttype") List<String> discounttype);
}
