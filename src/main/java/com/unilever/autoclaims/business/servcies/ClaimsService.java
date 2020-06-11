/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.business.servcies;

import com.unilever.autoclaims.business.dao.common.dbCalling.DBCallerDao;
import com.unilever.autoclaims.business.dao.repositories.CummDistRepository;
import com.unilever.autoclaims.business.dao.repositories.U2K2ClaimsDataRepository;
import com.unilever.autoclaims.business.dao.repositories.VU2k2ClaimsDataRepository;
import com.unilever.autoclaims.exceptions.BusinessException;
import com.unilever.autoclaims.model.ConstantStrings;
import com.unilever.autoclaims.model.dto.ClaimsDiscountSumDto;
import com.unilever.autoclaims.model.dto.callDB.FunctionCallParDTO;
import com.unilever.autoclaims.model.entities.CummDist;
import com.unilever.autoclaims.model.entities.U2k2ClaimsData;
import com.unilever.autoclaims.model.entities.VU2k2ClaimsData;
import com.unilever.autoclaims.utils.DateUtilities;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.ParameterMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Mahmoud.Elsaka
 */
@Service
@Transactional
public class ClaimsService {

    public static String GET_SAS2_DISTRIBUTORS_PROC_NAME = "GET_SAS2_DISTRIBUTORS";
    public static String GENERATE_U2K2_CLAIMS_DATA_FUNC_NAME = "GENERATE_U2K2_CLAIMS_DATA";
    public static String CONFIRM_U2K2_CLAIMS_DATA_FUNC_NAME = "CONFIRM_U2K2_CLAIMS_DATA";
    public static String LOAD_U2K2_CLAIMS_DATA_FUNC_NAME = "LOAD_U2K2_CLAIMS_DATA";
    public static String SUCCESS = "success";

    @Autowired
    private DBCallerDao dBCallerDao;
    @Autowired
    private U2K2ClaimsDataRepository u2K2ClaimsDataRepository;
    @Autowired
    private VU2k2ClaimsDataRepository vU2k2ClaimsDataRepository;

    @Autowired
    private CummDistRepository cummDistRepository;

    public void getSAS2Distributors() throws Exception {
        try {
            dBCallerDao.funcProcCaller(GET_SAS2_DISTRIBUTORS_PROC_NAME, null, null, false, null, false);
        } catch (Exception e) {
            throw e;
        }
    }

    public void generateU2k2ClaimsData(Date period, String userId, Boolean parse, String localeLanguage) throws Exception {
        try {

            FunctionCallParDTO functionCallPar = null;
            List<Object> funcCallParList = new ArrayList();

            functionCallPar = dBCallerDao.fillFunctionCallNameParObj(java.sql.Types.VARCHAR, ParameterMode.OUT, null);
            funcCallParList.add(functionCallPar);

            functionCallPar = dBCallerDao.fillFunctionCallNameParObj(java.sql.Types.DATE, ParameterMode.IN, period);
            funcCallParList.add(functionCallPar);

            functionCallPar = dBCallerDao.fillFunctionCallNameParObj(java.sql.Types.VARCHAR, ParameterMode.IN, userId);
            funcCallParList.add(functionCallPar);

            String retMessage = (String) dBCallerDao.funcProcCaller(GENERATE_U2K2_CLAIMS_DATA_FUNC_NAME, funcCallParList, "P_RETURN", parse, localeLanguage, true);

            if (!checkSuccessDbCallingByMessage(retMessage)) {
                throw new BusinessException(retMessage);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void confirmU2k2ClaimsData(Date period, String claimType, Boolean parse, String localeLanguage) throws Exception {
        try {

            FunctionCallParDTO functionCallPar = null;
            List<Object> funcCallParList = new ArrayList();

            functionCallPar = dBCallerDao.fillFunctionCallNameParObj(java.sql.Types.VARCHAR, ParameterMode.OUT, null);
            funcCallParList.add(functionCallPar);

            functionCallPar = dBCallerDao.fillFunctionCallNameParObj(java.sql.Types.VARCHAR, ParameterMode.IN, claimType);
            funcCallParList.add(functionCallPar);

            functionCallPar = dBCallerDao.fillFunctionCallNameParObj(java.sql.Types.DATE, ParameterMode.IN, period);
            funcCallParList.add(functionCallPar);

            String retMessage = (String) dBCallerDao.funcProcCaller(CONFIRM_U2K2_CLAIMS_DATA_FUNC_NAME, funcCallParList, "P_RETURN", parse, localeLanguage, true);

            if (!checkSuccessDbCallingByMessage(retMessage)) {
                throw new BusinessException(retMessage);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public String loadU2k2ClaimsData(Date period, Boolean parse, String localeLanguage) throws Exception {
        try {

            FunctionCallParDTO functionCallPar = null;
            List<Object> funcCallParList = new ArrayList();

            functionCallPar = dBCallerDao.fillFunctionCallNameParObj(java.sql.Types.VARCHAR, ParameterMode.OUT, null);
            funcCallParList.add(functionCallPar);

            functionCallPar = dBCallerDao.fillFunctionCallNameParObj(java.sql.Types.DATE, ParameterMode.IN, period);
            funcCallParList.add(functionCallPar);

            String retMessage = (String) dBCallerDao.funcProcCaller(LOAD_U2K2_CLAIMS_DATA_FUNC_NAME, funcCallParList, "P_RETURN", parse, localeLanguage, true);

            if (!checkSuccessDbCallingByMessage(retMessage)) {
                throw new BusinessException(retMessage);
            } else {
                return retMessage;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public ClaimsDiscountSumDto caculateClaimsDiscountSum(String period, String distNo, String claimType) {
        ClaimsDiscountSumDto claimsDiscountSumDto = new ClaimsDiscountSumDto();
        if (claimType.equals(ConstantStrings.AUTO_CLAIMS)) {
            claimsDiscountSumDto.setDiscountCash(cashDiscountSum(period, distNo));
            claimsDiscountSumDto.setDiscountWH(wholesaleDiscountSum(period, distNo));
            claimsDiscountSumDto.setDiscountCoupons(promotionDiscountSum(period, distNo));
            claimsDiscountSumDto.setDiscountOther(BigDecimal.ZERO);
        } else {
            claimsDiscountSumDto.setDiscountCash(BigDecimal.ZERO);
            claimsDiscountSumDto.setDiscountWH(BigDecimal.ZERO);
            claimsDiscountSumDto.setDiscountCoupons(BigDecimal.ZERO);
            claimsDiscountSumDto.setDiscountOther(otherDiscountSum(period, distNo));
        }

        return claimsDiscountSumDto;
    }

    public List<VU2k2ClaimsData> findAllVK2U2ClaimsData(Date period) {
        String periodStr = DateUtilities.convertDateToString(ConstantStrings.DATE_FORMATE, period);
        return vU2k2ClaimsDataRepository.findByPeriod(periodStr);
    }

    public List<VU2k2ClaimsData> findAllVK2U2ClaimsData(String period) {
        //String periodStr = DateUtilities.convertDateToString(ConstantStrings.DATE_FORMATE, period);
        return vU2k2ClaimsDataRepository.findByPeriod(period);
    }

    private BigDecimal cashDiscountSum(String period, String distNo) {
        try {
            List<String> discountTypes = new ArrayList();
            discountTypes.add(ConstantStrings.CASH_DISCOUNT);
            return u2K2ClaimsDataRepository.calculateClaimsDiscoutSumByDiscountType(period, distNo, discountTypes);
        } catch (Exception ex) {
            return new BigDecimal(0);
        }
    }

    private BigDecimal wholesaleDiscountSum(String period, String distNo) {
        try {
            List<String> discountTypes = new ArrayList();
            discountTypes.add(ConstantStrings.WHOLESALE_DISCOUNT);
            return u2K2ClaimsDataRepository.calculateClaimsDiscoutSumByDiscountType(period, distNo, discountTypes);
        } catch (Exception ex) {
            return new BigDecimal(0);
        }
    }

    private BigDecimal promotionDiscountSum(String period, String distNo) {
        try {
            List<String> discountTypes = new ArrayList();
            discountTypes.add(ConstantStrings.PROMOTION_DISCOUNT);
            return u2K2ClaimsDataRepository.calculateClaimsDiscoutSumByDiscountType(period, distNo, discountTypes);
        } catch (Exception ex) {
            return new BigDecimal(0);
        }
    }

    private BigDecimal otherDiscountSum(String period, String distNo) {
        try {
            List<String> discountTypes = new ArrayList();
            discountTypes.add(ConstantStrings.OTHER_DISCOUNT);
            return u2K2ClaimsDataRepository.calculateClaimsDiscoutSumByDiscountType(period, distNo, discountTypes);
        } catch (Exception ex) {
            return new BigDecimal(0);
        }
    }

    private boolean checkSuccessDbCallingByMessage(String message) throws Exception {
        return checkDbExpectedMessage(message, SUCCESS);
    }

    private boolean checkDbExpectedMessage(String message, String returnMessage) throws Exception {
        boolean is_success_msg = true;
        if (message != null && !message.equals("")) {
            if (!message.toLowerCase().startsWith(returnMessage)) {
                is_success_msg = false;
            }
        }
        return is_success_msg;
    }

    public String updateCummDist(List<String> distnoList) {
        try {
          //  CummDist obj = new CummDist();
            List<CummDist> cummDistList = cummDistRepository.findAll();
            if (cummDistList!=null && !cummDistList.isEmpty()) {
                for (CummDist cummDist : cummDistList) {
                    if(distnoList.contains(cummDist.getDistNo())){
                        cummDist.setSelected("Y");
                    }
//else{
//                        cummDist.setSelected("N");
//                    }
                    cummDistRepository.save(cummDist);
                }
            }
            return "Update done";
        } catch (Exception e) {
            throw new BusinessException("Can not update ");
        }

    }
   
    public String updateCliamData(U2k2ClaimsData claimsData) {
        try {
            u2K2ClaimsDataRepository.save(claimsData);
              return "Update done";
        } catch (Exception e) {
            throw new BusinessException("Can not update ");
        }
    }
}
