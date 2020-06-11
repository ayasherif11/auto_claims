/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.business.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mahmoud.elsaka
 */
public class BaseDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDao.class);

    @PersistenceContext
    protected EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }
}
