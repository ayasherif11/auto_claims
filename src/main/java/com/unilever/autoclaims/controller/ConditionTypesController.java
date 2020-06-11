/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.controller;

import com.unilever.autoclaims.business.dao.specifications.ConditionTypesSpecifications;
import com.unilever.autoclaims.model.entities.ConditionType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mahmoud.Elsaka
 */
@RestController
@RequestMapping("/conditionTypes")
public class ConditionTypesController extends BaseRestController<ConditionType> {

    public ConditionTypesController() {
        this.genericEntitySpecfications = new ConditionTypesSpecifications();
        //findOnlyService();
    }
}
