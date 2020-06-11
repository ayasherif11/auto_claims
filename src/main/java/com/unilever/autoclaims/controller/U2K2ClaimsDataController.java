/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.controller;

import com.unilever.autoclaims.business.dao.specifications.U2K2ClaimsDataSpecifications;
import com.unilever.autoclaims.business.servcies.ClaimsService;
import com.unilever.autoclaims.business.servcies.ExcelServices;
import com.unilever.autoclaims.exceptions.BusinessException;
import com.unilever.autoclaims.model.ConstantStrings;
import com.unilever.autoclaims.model.dto.ClaimsDiscountSumDto;
import com.unilever.autoclaims.model.entities.U2k2ClaimsData;
import com.unilever.autoclaims.model.entities.VU2k2ClaimsData;
import com.unilever.autoclaims.utils.excel.ExcelSheet;
import com.unilever.autoclaims.utils.excel.ExcelSheetQuery;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.jpa.domain.Specification;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mahmoud.Elsaka
 */
@RestController
@RequestMapping("/claims")
public class U2K2ClaimsDataController extends BaseRestController<U2k2ClaimsData> {

    private final String EXPORTED_FILE_EXTENSION = "CSV";
    private final int SHEET_INDEX = 0;

    private final Logger logger = LoggerFactory.getLogger(U2K2ClaimsDataController.class);

    @Autowired
    private ClaimsService claimsService;

    @Autowired
    private ExcelServices excelServices;

    private final U2K2ClaimsDataSpecifications u2K2ClaimsDataSpecifications;

    public U2K2ClaimsDataController() {
        this.genericEntitySpecfications = new U2K2ClaimsDataSpecifications();
        this.u2K2ClaimsDataSpecifications = new U2K2ClaimsDataSpecifications();
        //findOnlyService();
    }

    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    public ResponseEntity generateClaimsData(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @RequestParam(value = "branchno") List<String> branchno,
            @RequestParam(value = "claimType") String claimType,
            @RequestParam(value = "period") String period,
            @RequestParam(value = "userId") String userId) {
        try {
            if (period == null) {
                throw new BusinessException("period is missed");
            }
            claimsService.updateCummDist(branchno);
            Date perdiodDate = new SimpleDateFormat("dd/MM/yyyy").parse(period);
            boolean checkClaims = false;
            claimsService.generateU2k2ClaimsData(perdiodDate, userId, true, ConstantStrings.EN_LANG);
            Specification<U2k2ClaimsData> claimSpecifications = u2K2ClaimsDataSpecifications.buildU2K2ClaimsDataSpecifications(branchno, perdiodDate, claimType, checkClaims);
            List<U2k2ClaimsData> claimsList = findAllBySpecification(claimSpecifications);
            return buildResponseEntity(true, null, claimsList, HttpStatus.OK, headerAuthorization);
        } catch (Exception ex) {
            return buildExceptionResponseEntity(ex, headerAuthorization);
        }

    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ResponseEntity checkClaimsData(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @RequestParam(value = "branchno") List<String> branchno,
            @RequestParam(value = "claimType") String claimType,
            @RequestParam(value = "period") String period,
            @RequestParam(value = "userId") String userId) {
        try {
            if (period == null) {
                throw new BusinessException("period is missed");
            }
            Date perdiodDate = new SimpleDateFormat("dd/MM/yyyy").parse(period);
            boolean checkClaims = true;
            Specification claimSpecifications = u2K2ClaimsDataSpecifications.buildU2K2ClaimsDataSpecifications(branchno, perdiodDate, claimType, checkClaims);
            List<U2k2ClaimsData> claimsList = findAllBySpecification(claimSpecifications);

            return buildResponseEntity(true, null, claimsList, HttpStatus.OK, headerAuthorization);
        } catch (Exception ex) {
            return buildExceptionResponseEntity(ex, headerAuthorization);
        }

    }

    @RequestMapping(value = "/calctot", method = RequestMethod.GET)
    public ResponseEntity calcClaimsData(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @RequestParam(value = "branchno") String branchno,
            @RequestParam(value = "claimType") String claimType,
            @RequestParam(value = "period") String period) {
        try {
            if (period == null || claimType == null) {
                throw new BusinessException("period or claim type is missed");
            }
            //Date perdiodDate = new SimpleDateFormat("dd/MM/yyyy").parse(period);
            ClaimsDiscountSumDto claimsDiscountSumDto = claimsService.caculateClaimsDiscountSum(period, branchno, claimType);

            return buildResponseEntity(true, null, claimsDiscountSumDto, HttpStatus.OK, headerAuthorization);
        } catch (Exception ex) {
            return buildExceptionResponseEntity(ex, headerAuthorization);
        }
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public ResponseEntity confirmClaimsData(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @RequestParam(value = "claimType") String claimType,
            @RequestParam(value = "period") String period) {
        try {
            if (period == null || claimType == null) {
                throw new BusinessException("period or claim type is missed");
            }
            Date perdiodDate = new SimpleDateFormat("dd/MM/yyyy").parse(period);
            claimsService.confirmU2k2ClaimsData(perdiodDate, claimType, false, ConstantStrings.EN_LANG);
            String successMsg = "Les données ont été confirmées";

            return buildResponseEntity(true, null, successMsg, HttpStatus.OK, headerAuthorization);
        } catch (Exception ex) {
            return buildExceptionResponseEntity(ex, headerAuthorization);
        }
    }

    @RequestMapping(value = "/load", method = RequestMethod.GET)
    public ResponseEntity laodClaimsData(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @RequestParam(value = "period") String period) {
        try {
            if (period == null) {
                throw new BusinessException("period is missed");
            }
            Date perdiodDate = new SimpleDateFormat("dd/MM/yyyy").parse(period);
            String successMsg = claimsService.loadU2k2ClaimsData(perdiodDate, true, ConstantStrings.EN_LANG);
            return buildResponseEntity(true, null, successMsg, HttpStatus.OK, headerAuthorization);
        } catch (Exception ex) {
            return buildExceptionResponseEntity(ex, headerAuthorization);
        }
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public ResponseEntity runReport(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @RequestParam(value = "period") String period) {
        try {
            if (period == null) {
                throw new BusinessException("period is missed");
            }

            List<VU2k2ClaimsData> vU2k2ClaimsData = claimsService.findAllVK2U2ClaimsData(period);
            return buildResponseEntity(true, null, vU2k2ClaimsData, HttpStatus.OK, headerAuthorization);
        } catch (Exception ex) {
            return buildExceptionResponseEntity(ex, headerAuthorization);
        }
    }

    @RequestMapping(value = "/editClaims", method = RequestMethod.PUT)
    public ResponseEntity updateCummDist(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @RequestBody U2k2ClaimsData claimsData) {
        try {
            String successMsg = claimsService.updateCliamData(claimsData);
            return buildResponseEntity(true, null, successMsg, HttpStatus.OK, headerAuthorization);
        } catch (Exception ex) {
            return buildExceptionResponseEntity(ex, headerAuthorization);
        }
    }

    // ahmed report
    @RequestMapping(value = "/findReport", method = RequestMethod.GET)
    public ResponseEntity findReport(
            @RequestParam(value = "period") String period,
            HttpServletRequest request, HttpServletResponse response) {

        ByteArrayInputStream file = null;
        // TlReports report = null;
        String repName = null;
        
        try {
            ExcelSheet mainSheet = new ExcelSheet();
//            ReportSearchPojo searchPar = reportSearchPojo.isPresent() ? reportSearchPojo.get() : null;
//            Optional reportOptional = tlRepository.findById(repId);
//            if (reportOptional == null || !reportOptional.isPresent()) {
//                throw new BusinessException("report not exist");
//            }
            // report = (TlReports) reportOptional.get();
            String queryStr = "SELECT BRANCHNAMEE,CONDITIONTYPE,ITEMNO,DISCOUNT\n"
                    + ",DISCOUNTTYPE,PERIOD,INTERNAL_ORDER,DISTNO \n"
                    + "FROM V$U2K2_CLAIMS_DATA  WHERE period = '" + period + "'";

//            if (queryStr == null || queryStr.isEmpty()) {
//                throw new BusinessException("report query empty");
//
//            }
            repName = "Claims_report";
            repName = repName + new Date().getTime();

            mainSheet.setIndex(SHEET_INDEX);
            mainSheet.setName(repName);
            ExcelSheetQuery mainSheetQuery = new ExcelSheetQuery();

            mainSheetQuery.setQueryString(queryStr);

            mainSheet.getExcelSheetQueries().add(mainSheetQuery);
            List<ExcelSheet> excelSheets = Arrays.asList(new ExcelSheet[]{mainSheet});
            file = excelServices.export(repName, excelSheets, 100);
            response.setContentType("application/ms-excel");
            response.setHeader("Content-disposition", "attachment; filename=" + repName + "." + EXPORTED_FILE_EXTENSION);
            response.setHeader("Access-Control-Expose-Headers", "filename");
            response.setHeader("filename", repName + "." + EXPORTED_FILE_EXTENSION);

            return ResponseEntity
                    .ok()
                    .body(new InputStreamResource(file));
        } catch (BusinessException businessException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body(businessException.getMessage());
        } catch (Exception ex) {
            logger.error("error:", ex);
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Problem_with_export_report_contact_admin");
        }

    }
}
