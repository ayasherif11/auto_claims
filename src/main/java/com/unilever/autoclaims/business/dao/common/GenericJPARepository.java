/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.business.dao.common;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 *
 * @author Mahmoud.Elsaka
 * @param <T>
 */
@NoRepositoryBean
public interface GenericJPARepository<T extends Serializable> extends JpaRepository<T, Serializable>, JpaSpecificationExecutor {

}
