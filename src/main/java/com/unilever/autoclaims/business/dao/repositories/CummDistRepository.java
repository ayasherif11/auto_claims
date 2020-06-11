/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.business.dao.repositories;

import com.unilever.autoclaims.business.dao.common.GenericJPARepository;
import com.unilever.autoclaims.model.entities.CummDist;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Mahmoud.Elsaka
 */
public interface CummDistRepository extends GenericJPARepository<CummDist> {
    
        
    
    @Query("update CummDist set selected= :selected where  distNo = :distno")
    public String updateSelected(@Param("selected") String selected, @Param("distno") String distno);
}
