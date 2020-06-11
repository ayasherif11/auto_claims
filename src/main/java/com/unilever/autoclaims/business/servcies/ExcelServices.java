/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.business.servcies;

import com.unilever.autoclaims.exceptions.BusinessException;
import com.unilever.autoclaims.utils.excel.ExcelSheet;
import com.unilever.autoclaims.utils.excel.ExcelSheetQuery;
import com.unilever.autoclaims.utils.excel.ExportExcel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.dbutils.DbUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ExcelServices {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ExcelServices.class);

    @PersistenceContext
    private EntityManager em;

    public ExcelServices() {
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream export(String fileName, List<ExcelSheet> excelSheets, byte[] templateFile, Integer fetchSize) throws Exception {
        ByteArrayInputStream exportedFile = null;
        XSSFWorkbook workbookTemplate = null;
        SXSSFWorkbook workbook = null;
        try {
            if (templateFile != null) {
                workbookTemplate = ExportExcel.CreateXSSFWorkbook(templateFile);
            } else {
                workbookTemplate = ExportExcel.CreateXSSFWorkbook();

            }
            //workbook = ExportExcel.CreateSXSSFWorkbook(templateFile, excelSheets);
            workbook = ExportExcel.CreateSXSSFWorkbook(templateFile, excelSheets);
            for (ExcelSheet sheet : excelSheets) {
                XSSFSheet templateSheet = workbookTemplate.getSheetAt(sheet.getIndex());
                SXSSFSheet newSheet = workbook.createSheet(templateSheet.getSheetName());
                newSheet.setRandomAccessWindowSize(fetchSize);
                ExportExcel.CopySheetMetaData(newSheet, templateSheet);
                ExportExcel.CreateSheetHeader(sheet, newSheet, templateSheet);
                openSheetQuriesConnections(sheet, fetchSize, templateSheet, newSheet);

            }
            workbook.setForceFormulaRecalculation(true);
            exportedFile = pushExcelFile(workbook);
        } catch (Exception ex) {
            LOGGER.error("", ex);
            throw ex;
        } finally {
            if (workbook != null) {
                workbook.dispose();
            }
        }
        return exportedFile;
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream export(String fileName, List<ExcelSheet> excelSheets, Integer fetchSize) throws Exception {
        ByteArrayInputStream exportedFile = null;
        XSSFWorkbook workbookTemplate = null;
        SXSSFWorkbook workbook = null;
        try {
            workbookTemplate = ExportExcel.CreateXSSFWorkbook();
            workbook = ExportExcel.CreateSXSSFWorkbook(excelSheets);
            for (ExcelSheet sheet : excelSheets) {
                XSSFSheet templateSheet = workbookTemplate.createSheet("data");
                SXSSFSheet newSheet = workbook.createSheet(templateSheet.getSheetName());
                newSheet.setRandomAccessWindowSize(fetchSize);
                ExportExcel.CopySheetMetaData(newSheet, templateSheet);
                //ExportExcel.CreateSheetHeader(sheet, newSheet);
                openSheetQuriesConnections(sheet, fetchSize, templateSheet, newSheet);

            }
            workbook.setForceFormulaRecalculation(true);
            exportedFile = pushExcelFile(workbook);
        } catch (Exception ex) {
            LOGGER.error("", ex);
            throw ex;
        } finally {
            if (workbook != null) {
                workbook.dispose();
            }
            //closeQuriesConnections(excelSheets);
        }
        return exportedFile;
    }

    private void openSheetQuriesConnections(ExcelSheet sheet, Integer fetchSize, XSSFSheet templateSheet, SXSSFSheet newSheet) {
        try {
            Session session = em.unwrap(Session.class);
            ///open connection stream for runnng query
            session.doWork((Connection con) -> {
                Statement statement = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                statement.setFetchSize(fetchSize);

                try {

                    sheet.getExcelSheetQueries().stream().filter((query)
                            -> (query.getResultSet() == null && query.getQueryString() != null
                            && !query.getQueryString().isEmpty())).map((query) -> {
                        ResultSet rs = null;

                        try {
                            rs = statement.executeQuery(query.getQueryString());
                            prepareSheetQuery(query, rs);
                            query.setIncludeHeader(true);
                            ExportExcel.createHeaderFromResultSet(newSheet, query);

                        } catch (SQLException ex) {
                            Logger.getLogger(ExcelServices.class.getName()).log(Level.SEVERE, null, ex);
                            throw new BusinessException(ex.getMessage());
                        }
                        return query;
                    }).filter((query) -> (query.getStartCol() == null)).forEachOrdered((query) -> {
                        query.setStartCol(0);
                    });
                    fillExcelDataFromQueries(sheet, templateSheet, newSheet);
                } catch (SQLException ex) {
                    throw ex;
                } finally {
                    DbUtils.closeQuietly(statement);
                    DbUtils.closeQuietly(con);
                }
            });
        } catch (HibernateException ex) {
            throw ex;
        }
    }

    private void prepareSheetQuery(ExcelSheetQuery query, ResultSet rs) throws SQLException {
        query.setResultSet(rs);
        if (query.getEndCol() == null) {
            try {
                query.setEndCol(query.getResultSet().getMetaData().getColumnCount() - 1);
            } catch (SQLException ex) {
                throw ex;
            }

        }

    }

    private void fillExcelDataFromQueries(ExcelSheet sheet, XSSFSheet templateSheet, SXSSFSheet newSheet) throws SQLException {
        int rowNum = 0;
        boolean breakLoop = false;
        breakLoop = !moveQuriesToNextRow(sheet.getExcelSheetQueries());
        while (!breakLoop) {
            Row row = ExportExcel.createRowFromTemplate(newSheet, rowNum, templateSheet);
            if (sheet.getHeaderIndex() != rowNum) {
                breakLoop = fillRowFromQuery(sheet.getExcelSheetQueries(), row, templateSheet, rowNum);
                if (!breakLoop) {
                    breakLoop = !moveQuriesToNextRow(sheet.getExcelSheetQueries());
                }
            } else {
                ExportExcel.AddSheetQueriesHeader(sheet, row);
            }
            rowNum++;
        }
    }

    private Boolean fillRowFromQuery(List<ExcelSheetQuery> sheetQueries, Row row, XSSFSheet templateSheet, int rowNum) throws SQLException {
        Boolean notSuccess = false;
        for (ExcelSheetQuery query : sheetQueries) {
            if (query.getStartRow() != null && rowNum < query.getStartRow()) {
                continue;
            }
            if (query.getEndRow() != null && rowNum > query.getEndRow()) {
                continue;
            }
            int queryColNum = 0;
            for (int sheetColNum = query.getStartCol(); sheetColNum <= query.getEndCol(); sheetColNum++) {
                queryColNum++;
                Cell currentCell = row.getCell(sheetColNum) != null
                        ? row.getCell(sheetColNum)
                        : (templateSheet != null ? ExportExcel.createCellFromTemplate(row, sheetColNum, templateSheet) : row.createCell(sheetColNum));
                if (currentCell.getCellType() != Cell.CELL_TYPE_FORMULA) {
                    ExportExcel.setCellValue(currentCell, sheetColNum, getResultSetValue(query.getResultSet(), queryColNum, notSuccess));
                }
            }
        }
        return notSuccess;
    }

    private ByteArrayInputStream pushExcelFile(SXSSFWorkbook workbook) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        workbook.write(out);

        return new ByteArrayInputStream(out.toByteArray());
    }

    private Boolean moveQuriesToNextRow(List<ExcelSheetQuery> resultSets) {
        Boolean ret = false;
        for (ExcelSheetQuery query : resultSets) {
            try {
                if (query.getResultSet() != null && query.getResultSet().next()) {
                    ret = true;
                }
            } catch (SQLException ex) {
            }
        }
        return ret;
    }

    private Object getResultSetValue(ResultSet resultSet, int colNum, Boolean notSuccess) throws SQLException {
        Object ret = null;
        try {
            ret = resultSet.getObject(colNum);
        } catch (SQLException ex) {
            //row not found
            if (ex.getMessage().equals("Exhausted Resultset")) {
                notSuccess = true;
            } else {
                throw ex;
            }
        }
        return ret;
    }

}
