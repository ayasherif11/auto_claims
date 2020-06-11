/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.business.dao.common.dbCalling;

import com.unilever.autoclaims.business.dao.BaseDao;
import com.unilever.autoclaims.model.dto.common.ErrorDto;
import com.unilever.exceptions.custom.DataBaseBusinessException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author mahmoud.elsaka
 */
@Component
public class ParsingDBReturnDao extends BaseDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingDBReturnDao.class);

    public Object adjustReturnString(Boolean parse, Object objReturnValue, String localeLanguage) throws Exception {
        try {

            String returnMes = "";

            if (parse && objReturnValue != null) {
                returnMes = (String) objReturnValue;
                //not start with sucess
                //there are functions sucess msg does not start with sucess but contain sucess inside it like ACC_DUE_CHECK,ACC_DUE_CHECK_IN_BANK
                //(!returnMes.startsWith("SUCCESS") && !returnMes.startsWith("success"))||
                if ((!returnMes.contains("SUCCESS") && !returnMes.contains("success"))) {
                    throw new DataBaseBusinessException(getDBMsg(returnMes, localeLanguage));
                } else {
                    return getDBMsg(returnMes, localeLanguage);
                }
            } else {
                return objReturnValue;
            }

        } catch (Exception e) {
            throw e;
        }

    }

    private List parseError(String returnErrorStr) {
        List errorMsgsList = new ArrayList();

        String[] items = returnErrorStr.split(ErrorDto.MESSAGE_SPLITTER);
        if (items.length == 1) {
            items = returnErrorStr.split(ErrorDto.MESSAGE_SPLITTER_CAPITAL);
        }

        for (int i = 0; i < items.length; i++) {
            ErrorDto errorMsgObj = new ErrorDto();
            errorMsgObj.setErrorMsgKey(items[i]);
            int numerOfArguments = 0;
            try {
                errorMsgObj.setMsgType(Integer.parseInt(items[i + 1]));
                numerOfArguments = Integer.parseInt(items[i + 2]);

            } catch (Exception ex) {
                numerOfArguments = 0;
            }

            errorMsgObj.setArgs(items, i + 3, numerOfArguments);
            errorMsgsList.add(errorMsgObj);
            i += (numerOfArguments + 2);

        }
        return errorMsgsList;
    }

    private String getDBMsg(String returnErrorStr, String localeLanguage) {
        List errorMessagesList = parseError(returnErrorStr);
        String msg = "";
        String allMsg = "";
        Iterator msgIter = errorMessagesList.iterator();

        while (msgIter.hasNext()) {

            ErrorDto errMsgObj = (ErrorDto) msgIter.next();
            msg = "";
            if (errMsgObj.getMsgType() == errMsgObj.USER_MESSAGE) {

                msg = getParameterizedMessage(errMsgObj.getErrorMsgKey(), localeLanguage, errMsgObj.getArgs());
                allMsg += msg;

            } else if (errMsgObj.getMsgType() == errMsgObj.ACTION_MESSAGE) {
            } else {
            }
            if (msgIter.hasNext()) {
                allMsg += " , ";
            }
        }

        return allMsg;
    }

    //https://stackoverflow.com/questions/37554150/is-it-a-bad-design-for-a-ejb-to-depend-on-facescontext
    //https://stackoverflow.com/questions/38773544/access-facescontext-in-interceptor-when-having-distributed-system
    private String getParameterizedMessage(String key, String localeLanguage, Object... params) {
        ResourceBundle myBundle = ResourceBundle.getBundle("application", Locale.forLanguageTag(localeLanguage));

        String value = myBundle.getString(key);
        return params == null ? value : MessageFormat.format(value, params);
    }

}
