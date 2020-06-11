/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.business.dao.repositories;

import com.unilever.autoclaims.business.dao.common.GenericJPARepository;
import com.unilever.autoclaims.model.entities.IntSettings;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Mahmoud.Elsaka
 */
public interface ParametersRepository extends GenericJPARepository<IntSettings> {

    @Query("SELECT p FROM IntSettings p WHERE p.code in (:codesList)")
    List<IntSettings> findByCodesList(List<String> codesList);
}
