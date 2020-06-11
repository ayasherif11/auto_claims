/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.business.dao.repositories;

import com.unilever.autoclaims.business.dao.common.GenericJPARepository;
import com.unilever.autoclaims.model.entities.VU2k2ClaimsData;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Mahmoud.Elsaka
 */
public interface VU2k2ClaimsDataRepository extends GenericJPARepository<VU2k2ClaimsData> {

    @Query("SELECT v FROM VU2k2ClaimsData v WHERE v.period = :period")
    public List<VU2k2ClaimsData> findByPeriod(@Param("period") String period);
}
