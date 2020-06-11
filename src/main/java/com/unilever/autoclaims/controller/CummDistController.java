/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.controller;

import com.unilever.autoclaims.business.dao.repositories.VCummDistGridRepository;
import com.unilever.autoclaims.business.dao.specifications.VCummDistGridSpecifications;
import com.unilever.autoclaims.business.servcies.ClaimsService;
import com.unilever.autoclaims.exceptions.BusinessException;
import com.unilever.autoclaims.model.ConstantStrings;
import com.unilever.autoclaims.model.entities.VCummDistGrid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mahmoud.Elsaka
 */
@RestController
@RequestMapping("/cummdist")
public class CummDistController extends BaseRestController<VCummDistGrid> {

    @Autowired
    private ClaimsService claimsService;
    @Autowired
    private VCummDistGridRepository vCummDistGridRepository;

    public CummDistController() {
        this.genericEntitySpecfications = new VCummDistGridSpecifications();
        //findOnlyService();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity findCummDist(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization) {
        try {
            claimsService.getSAS2Distributors();
            List<VCummDistGrid> conditiontypesList = vCummDistGridRepository.findAll();
            return buildResponseEntity(true, null, conditiontypesList, HttpStatus.OK, headerAuthorization);
        } catch (Exception ex) {
            return buildExceptionResponseEntity(ex, headerAuthorization);
        }
    }
    
    @RequestMapping(value = "/edit/cummDist/{distNo}", method = RequestMethod.PUT)
    public ResponseEntity updateCummDist(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @PathVariable List<String> distNo) {
        try {
            String successMsg = claimsService.updateCummDist(distNo);
            return buildResponseEntity(true, null, successMsg, HttpStatus.OK, headerAuthorization);
        } catch (Exception ex) {
            return buildExceptionResponseEntity(ex, headerAuthorization);
        }
    }
}
