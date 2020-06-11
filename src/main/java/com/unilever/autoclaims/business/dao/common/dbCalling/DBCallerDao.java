/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.business.dao.common.dbCalling;

import com.unilever.autoclaims.business.dao.BaseDao;
import com.unilever.autoclaims.exceptions.BusinessException;
import com.unilever.autoclaims.model.dto.callDB.FunctionCallParDTO;
import com.unilever.autoclaims.model.dto.callDB.ProcdureCallParDTO;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author mahmoud.elsaka
 */
@Component
public class DBCallerDao extends BaseDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBCallerDao.class);

    @Autowired
    private ParsingDBReturnDao parsingDBReturnDao;

    //use this method when u have null default values in db procedure use name not index
    public ProcdureCallParDTO fillProcdureCallNameParObj(String parName, Class type, ParameterMode parameterMode, Object value) {
        ProcdureCallParDTO procdureCallPar = new ProcdureCallParDTO();
        procdureCallPar.setParName(parName);
        if (value != null && value instanceof Date) {
            procdureCallPar.setParType(convertUtilDateToSqlDate((Date) value).getClass());

            procdureCallPar.setParValue(convertUtilDateToSqlDate((Date) value));

        } else {
            procdureCallPar.setParType(type);
            procdureCallPar.setParValue(value);
        }
        procdureCallPar.setParameterMode(parameterMode);

        return procdureCallPar;

    }

    public FunctionCallParDTO fillFunctionCallNameParObj(int type, ParameterMode parameterMode, Object value) {
        FunctionCallParDTO functionCallPar = new FunctionCallParDTO();

        if (value != null && value instanceof Date) {
            functionCallPar.setParType(java.sql.Types.DATE);
            functionCallPar.setParValue(convertUtilDateToSqlDate((Date) value));

        } else {
            functionCallPar.setParType(type);
            functionCallPar.setParValue(value);
        }
        functionCallPar.setParameterMode(parameterMode);

        return functionCallPar;

    }

    public Object funcProcCaller(String procName, List<Object> procParList, String returnParName, Boolean parse, String localeLanguage, Boolean isFunction) throws Exception {

        try {
            Object objReturnValue = "";
            String returnMes = "";
            if (isFunction) {
                objReturnValue = callDBFunctionWithParIndex(procName, procParList, 1);
            } else {
                objReturnValue = callDBProcedureWithParName(procName, procParList, returnParName);
            }
            return parsingDBReturnDao.adjustReturnString(parse, objReturnValue, localeLanguage);
        } catch (Exception e) {
            LOGGER.error("error_general", e);//change message
            throw e;
        }

    }

    //out parameter must be first one in list
    private Object callDBFunctionWithParIndex(String functionName, List<Object> procParList, Integer returnParIndex) throws SQLException, Exception, BusinessException {
        //Connection connection = getEntityManager().unwrap(java.sql.Connection.class);  //eclipse link implementation
        Session hibernateSession = getEntityManager().unwrap(Session.class);
        Object returnMes = null;

        try {
            //That is important point,just pass object to ReturningWork<T> whatever you want to return from procedure, an also to execute method return type
            returnMes = hibernateSession.doReturningWork(new ReturningWork<Object>() {
                @Override
                public Object execute(Connection connection) throws SQLException {
                    CallableStatement callStmnt = null;
                    try {

                        Object innerReturn = null;
                        String inPar = adjustParameterString(procParList);
                        callStmnt = prepareCall(" { ? = call " + functionName + inPar + " } ", connection);
                        // Parameters can be numbered or named
                        if (procParList != null && procParList.size() > 0) {
                            for (int i = 0; i < procParList.size(); i++) {
                                FunctionCallParDTO funPar = (FunctionCallParDTO) procParList.get(i);
                                if (funPar.getParameterMode().equals(ParameterMode.IN)) {
                                    callStmnt = addInParameter(callStmnt, i + 1, funPar.getParValue());
                                } else {
                                    callStmnt = addOutParameter(callStmnt, i + 1, funPar.getParType());
                                }
                            }
                        }

                        excecute(callStmnt);

                        if (returnParIndex != null) {
                            innerReturn = getOutParameter(callStmnt, returnParIndex);
                        } else {
                            innerReturn = null;
                        }
                        return innerReturn;
                    } catch (Exception ex) {
                        throw new SQLException("");
                    } finally {
                        //DbUtils.closeQuietly(callStmnt);
                        //DbUtils.closeQuietly(connection);
                    }
                }

            });
        } catch (HibernateException e) {
            throw e;
        }
        return returnMes;

    }
//    private Object callDBFunctionWithParName(String functionName, List<Object> procParList, String returnParName) throws SQLException, Exception,BusinessException
//    {
//            //Connection connection = getEntityManager().unwrap(java.sql.Connection.class);  //eclipse link implementation
//
//        Session hibernateSession = getEntityManager().unwrap(Session.class);
//        Object returnMes=null;
//
//            try
//            {
//
//               //That is important point,just pass object to ReturningWork<T> whatever you want to return from procedure, an also to execute method return type
//                returnMes = hibernateSession.doReturningWork(new ReturningWork<Object>(){
//                @Override
//                public Object execute(Connection connection)throws SQLException {
//                        try
//                        {
//                                Object innerReturn =null;
//                                String inPar=adjustParameterString(procParList);
//                                CallableStatement callStmnt= prepareCall(" { ? = call " + functionName+inPar + " } ", connection);//proc name will contain call
//                                // Parameters can be numbered or named
//                                if (procParList != null && procParList.size() > 0) {
//                                    for (int i = 0; i < procParList.size(); i++) {
//                                              FunctionCallParDTO funPar = (FunctionCallParDTO)procParList.get(i);
//                                              if (funPar.getParameterMode().equals(ParameterMode.IN)){
//                                                   callStmnt=addInParameter(callStmnt,funPar.getParName(), funPar.getParValue());
//                                              }
//                                              else{
//                                                   callStmnt=addOutParameter(callStmnt,funPar.getParName(),funPar.getParType());
//                                              }
//                                    }
//                                }
//
//                                excecute(callStmnt);
//
//                                if (returnParName != null) {
//                                      innerReturn= getOutParameter(callStmnt,returnParName) ;
//                                } else {
//                                     innerReturn=null;
//                                }
//                                return innerReturn;
//                            }
//                            catch (Exception ex){
//                                throw new SQLException("");
//                            }
//                    }
//
//                });
//                }
//            catch(HibernateException e){
//                 throw e;
//            }finally{
//                hibernateSession.close();
//            }
//             return returnMes;
////            String inPar=adjustParameterString(procParList);
////            CallableStatement callStmnt= prepareCall("{? =call " + functionName+inPar + "}", connection);//proc name will contain call
////            // Parameters can be numbered or named
////            if (procParList != null && procParList.size() > 0) {
////                for (int i = 0; i < procParList.size(); i++) {
////                          FunctionCallParDTO funPar = (FunctionCallParDTO)procParList.get(i);
////                          if (funPar.getParameterMode().equals(ParameterMode.IN)){
////                              callStmnt=addInParameter(callStmnt,funPar.getParName(), funPar.getParValue());
////
////                          }
////                          else{
////                               callStmnt=addOutParameter(callStmnt,funPar.getParName(),funPar.getParType());
////                          }
////
////                }
////            }
////            excecute(callStmnt);
////
////            if (returnParName != null) {
////
////                      Object returnMes = getOutParameter(callStmnt,returnParName) ;
////                      return returnMes;
////            } else {
////                      return null;
////            }
//    }
    //use this method when u have null default values in db procedure use name not index

    private Object callDBProcedureWithParName(String procName, List<Object> procParList, String returnParName) {
        StoredProcedureQuery storedProcQuery = em.createStoredProcedureQuery(procName);

        // Parameters can be numbered or named
        if (procParList != null && procParList.size() > 0) {
            procParList.forEach((par) -> {

                ProcdureCallParDTO procPar = (ProcdureCallParDTO) par;
                storedProcQuery.registerStoredProcedureParameter(procPar.getParName(), procPar.getParType(), procPar.getParameterMode());

                if (procPar.getParameterMode().equals(ParameterMode.IN)) {

                    storedProcQuery.setParameter(procPar.getParName(), procPar.getParValue());

                }
            });
        }

        storedProcQuery.execute();
        //System.out.println("result = " + result);
        if (returnParName != null) {

            Object returnMes = storedProcQuery.getOutputParameterValue(returnParName);
            return returnMes;
        } else {
            return null;
        }

    }
    //https://stackoverflow.com/questions/19721238/callablestatement-with-parameter-names-on-postgresql
    //https://www.akadia.com/services/ora_jdbc_parameter.html
    // https://coderanch.com/t/300163/databases/CallableStatement-parameterName
    //https://jdbc.postgresql.org/documentation/80/callproc.html
    //https://github.com/golang/go/issues/12381

    //POSTGREE AND NAMED PARAMETERS SUPPORT
    //https://www.postgresql.org/message-id/15962BD4-D33C-412B-AC50-A1EB93AA5748%40fastcrypt.com
    //
    //https://www.postgresql.org/docs/9.1/static/sql-syntax-calling-funcs.html
    //NAMES VS INDEX
    //https://www.javaworld.com/article/2077706/core-java/named-parameters-for-preparedstatement.html
    private String adjustParameterString(List<Object> procParList) {
        String inPar = "";
        boolean addedPar = false;
        // Parameters can be numbered or named
        if (procParList != null && procParList.size() > 0) {
            inPar += "(";
            for (int i = 0; i < procParList.size(); i++) {
                FunctionCallParDTO funPar = (FunctionCallParDTO) procParList.get(i);
                if (funPar.getParameterMode().equals(ParameterMode.IN)) {
                    if (addedPar) {
                        inPar += " ,? ";
                    } else {
                        inPar += " ? ";
                        addedPar = true;
                    }

                }

            }
            inPar += ")";

        }
        return inPar;
    }

    private CallableStatement prepareCall(String procedureFunctionCall, Connection connection) throws SQLException {

        CallableStatement callStmnt = connection.prepareCall(procedureFunctionCall);
        return callStmnt;
    }

    private CallableStatement addInParameter(CallableStatement callStmnt, String parameterName, Object value) throws Exception {
        validateCallStmnt(callStmnt);
        if (value instanceof Date) {
            value = convertUtilDateToSqlDate((Date) value);
        }
        callStmnt.setObject(parameterName, value);
        return callStmnt;
    }

    public CallableStatement addInParameter(CallableStatement callStmnt, int parameterIndex, Object value) throws Exception {
        validateCallStmnt(callStmnt);
        if (value instanceof Date) {
            value = convertUtilDateToSqlDate((Date) value);
        }
        callStmnt.setObject(parameterIndex, value);
        return callStmnt;
    }

    private java.sql.Date convertUtilDateToSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    private CallableStatement addOutParameter(CallableStatement callStmnt, String parameterName, int type) throws Exception {
        validateCallStmnt(callStmnt);
        callStmnt.registerOutParameter(parameterName, type); // Note all results get from procedure or function will be converted to string
        return callStmnt;
    }

    public CallableStatement addOutParameter(CallableStatement callStmnt, int parameterIndex, int type) throws Exception {
        validateCallStmnt(callStmnt);
        callStmnt.registerOutParameter(parameterIndex, type); // Note all results get from procedure or function will be converted to string
        return callStmnt;
    }

    private Object getOutParameter(CallableStatement callStmnt, String parameterName) throws Exception {
        validateCallStmnt(callStmnt);
        return callStmnt.getObject(parameterName);
    }

    public Object getOutParameter(CallableStatement callStmnt, int parameterIndex) throws Exception {
        validateCallStmnt(callStmnt);
        return callStmnt.getObject(parameterIndex);
    }

    private void validateCallStmnt(CallableStatement callStmnt) throws Exception {
        if (callStmnt == null) {
            throw new BusinessException("Prepare Call First");
        }
    }

    /**
     * call function or procedure that prepared by calling prepareCall function
     *
     * @return null if no out or inout params
     * @throws Exception
     */
    private CallableStatement excecute(CallableStatement callStmnt) throws Exception {
        try {
            callStmnt.execute();
        } catch (Exception e) {
            LOGGER.error("", e);
            throw e;
        }//no need to close as we finally close hibernate  session which close all resources and we can not close it before get outreturn from it
        //https://stackoverflow.com/questions/25989132/closing-resultset-and-callablestatement
        //https://www.concretepage.com/hibernate-4/hibernate-4-returningwork-and-session-doreturningwork-example-for-jdbc
//        } finally {
//            try {
//                if (callStmnt != null) {
//                    callStmnt.close();
//                }
//            } catch (Exception e) {
//                LOGGER.error("error_general", e);
//                throw new BusinessException("error_general");
//            }
//        }
        return callStmnt;
    }

}
