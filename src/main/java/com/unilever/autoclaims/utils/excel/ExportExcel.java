/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.utils.excel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;

public class ExportExcel {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ExportExcel.class);

    public static Row createRowFromTemplate(SXSSFSheet sheet, int rowNum, XSSFSheet template) {
        Row row = sheet.createRow(rowNum);

        if (rowNum < 1 && template.getRow(row.getRowNum()) != null) {
            for (int j = 0; j < template.getRow(row.getRowNum()).getLastCellNum(); j++) {
                createCellFromTemplate(row, j, template);
            }
        } else if (template.getRow(1) != null) {
            for (int j = 0; j < template.getRow(1).getLastCellNum(); j++) {
                createCellFromTemplate(row, j, template);
            }
        }

        return row;
    }

    public static Cell setCellValue(Cell cell, int columnIndex, Object value) {
        if (value == null) {
            cell.setCellValue("");
            return cell;
        }
        try {
            if (Utilities.isNumeric(String.valueOf(value))) {
                cell.setCellValue(Double.parseDouble(String.valueOf(value)));
                if (cell.getCellType() != XSSFCell.CELL_TYPE_FORMULA) {
                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                }
            } else {
                cell.setCellValue(String.valueOf(value));
                if (cell.getCellType() != XSSFCell.CELL_TYPE_FORMULA) {
                    cell.setCellType(1);
                }
            }
        } catch (Exception e) {
            Date date = DateService.getDate(String.valueOf(value));
            if (date != null) {
                cell.setCellValue(date);
            } else {
                cell.setCellValue(String.valueOf(value));
                if (cell.getCellType() != XSSFCell.CELL_TYPE_FORMULA) {
                    cell.setCellType(1);
                }
            }
        }
        return cell;
    }

    public static XSSFWorkbook CreateXSSFWorkbook(byte[] templateFile) throws IOException {
        return new XSSFWorkbook(new ByteArrayInputStream(templateFile));
    }

    public static XSSFWorkbook CreateXSSFWorkbook() throws IOException {
        return new XSSFWorkbook();
    }

    public static SXSSFWorkbook CreateSXSSFWorkbook(byte[] templateFile, List<ExcelSheet> excelSheets) throws IOException {
        XSSFWorkbook workbookTemplate2 = new XSSFWorkbook(new ByteArrayInputStream(templateFile));
        SXSSFWorkbook workbook = new SXSSFWorkbook(workbookTemplate2, 1);
        for (ExcelSheet ex : excelSheets) {
            int sheetIndex = workbook.getSheetIndex(ex.getName());
            if (sheetIndex != -1) {
                workbook.removeSheetAt(sheetIndex);
            }
        }
        return workbook;
    }

    public static SXSSFWorkbook CreateSXSSFWorkbook(List<ExcelSheet> excelSheets) throws IOException {
        XSSFWorkbook workbookTemplate2 = new XSSFWorkbook();
        SXSSFWorkbook workbook = new SXSSFWorkbook(workbookTemplate2, 1);
        for (ExcelSheet ex : excelSheets) {
            int sheetIndex = workbook.getSheetIndex(ex.getName());
            if (sheetIndex != -1) {
                workbook.removeSheetAt(sheetIndex);
            }
        }
        return workbook;
    }

    public static void CreateSheetHeader(ExcelSheet sheet, SXSSFSheet newSheet, XSSFSheet templateSheet) {
        if (sheet.getHeader() != null) {
            Row row = ExportExcel.createRowFromTemplate(newSheet, sheet.getHeaderIndex(), templateSheet);
            for (int colNum = 0; colNum < sheet.getHeader().size(); colNum++) {
                Cell currentCell = row.getCell(colNum) != null
                        ? row.getCell(colNum)
                        : ExportExcel.createCellFromTemplate(row, colNum, templateSheet);
                ExportExcel.setCellValue(currentCell, colNum, sheet.getHeader().get(colNum));
            }
        }
    }

    public static void createHeaderFromResultSet(SXSSFSheet newSheet, ExcelSheetQuery query) throws SQLException {
        if (query != null && query.getResultSet() != null) {
            Row row = newSheet.createRow(0);
            Map<Integer, ExcelColumnsDefinitions> columnsDifinisionMap = getColumnsDifinitions(query.getResultSet().getMetaData());
            int columnIndx = 0;
            Iterator<Integer> iterator = columnsDifinisionMap.keySet().iterator();
            ExcelColumnsDefinitions columnsDefinitions = null;
            while (iterator.hasNext()) {
                Integer key = iterator.next();
                columnsDefinitions = columnsDifinisionMap.get(key);
                Cell cell = row.getCell(columnIndx) != null ? row.getCell(columnIndx) : row.createCell(columnIndx);
                setCellValue(cell, columnIndx, columnsDefinitions.getFieldText());
                columnIndx++;
            }
        }
    }

    public static void CreateSheetHeader(ExcelSheet sheet, SXSSFSheet newSheet) {
        if (sheet.getHeader() != null) {
            Row row = newSheet.createRow(sheet.getHeaderIndex());
            for (int colNum = 0; colNum < sheet.getHeader().size(); colNum++) {
                Cell currentCell = row.getCell(colNum) != null
                        ? row.getCell(colNum) : row.createCell(colNum);
                ExportExcel.setCellValue(currentCell, colNum, sheet.getHeader().get(colNum));
            }
        }
    }

    public static void CopySheetMetaData(SXSSFSheet newSheet, XSSFSheet templateSheet) {
        if (templateSheet.getPaneInformation() != null) {
            newSheet.createFreezePane(templateSheet.getPaneInformation().getVerticalSplitLeftColumn(), templateSheet.getPaneInformation().getHorizontalSplitTopRow());
        }
        if (templateSheet.getRow(0) != null) {
            for (int colNum = 1; colNum <= templateSheet.getRow(0).getLastCellNum(); colNum++) {
                newSheet.setColumnWidth(colNum, templateSheet.getColumnWidth(colNum));
            }
        }
    }

    public static void AddSheetQueriesHeader(ExcelSheet sheetData, Row row) throws SQLException {
        for (ExcelSheetQuery query : sheetData.getExcelSheetQueries()) {
            if (query.getQueryString() != null && query.getIncludeHeader()) {
                AddQueryHeader(query, row);
            }
        }
    }

    public static void AddQueryHeader(ExcelSheetQuery query, Row row) throws SQLException {
        // 1- Create Hash Map of Field Definitions
        Map<Integer, ExcelColumnsDefinitions> columnsDifinisionMap = getColumnsDifinitions(query.getResultSet().getMetaData());
        // 2- Generate column headings
        int columnIndx = 0;
        Iterator<Integer> iterator = columnsDifinisionMap.keySet().iterator();
        ExcelColumnsDefinitions columnsDefinitions = null;
        CellStyle headerStyle = row.getSheet().getWorkbook().createCellStyle();
        Font headerFont = row.getSheet().getWorkbook().createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        row.setRowStyle(headerStyle);
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            columnsDefinitions = columnsDifinisionMap.get(key);
            Cell cell = row.getCell(columnIndx) != null ? row.getCell(columnIndx) : row.createCell(columnIndx);
            cell.setCellStyle(headerStyle);
            setCellValue(cell, columnIndx, columnsDefinitions.getFieldText());
            columnIndx++;
        }
    }

    private static Map<Integer, ExcelColumnsDefinitions> getColumnsDifinitions(ResultSetMetaData metaData) throws SQLException {
        //Create Hash Map of Field Definitions
        Map<Integer, ExcelColumnsDefinitions> hashMap = new HashMap();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            ExcelColumnsDefinitions excelTableInfo = new ExcelColumnsDefinitions();
            excelTableInfo.setFieldName(metaData.getColumnName(i + 1).trim());
            excelTableInfo.setFieldText(metaData.getColumnLabel(i + 1));
            excelTableInfo.setFieldSize(metaData.getPrecision(i + 1));
            excelTableInfo.setFieldDecimal(metaData.getScale(i + 1));
            excelTableInfo.setFieldType(metaData.getColumnType(i + 1));
            hashMap.put(i, excelTableInfo);
        }
        return hashMap;
    }

    public static Cell createCellFromTemplate(Row row, int cell, XSSFSheet template) {
        Cell newCell = row.createCell(cell);
        Cell templateCell = null;

//	if (template.getRow(row.getRowNum()) != null
//		&& template.getRow(row.getRowNum()).getCell(cell) != null) {
//	    templateCell = template.getRow(row.getRowNum()).getCell(cell);
//	} else if (template.getRow(template.getLastRowNum()) != null
//		&& template.getRow(template.getLastRowNum()).getCell(cell) != null) {
//	    templateCell = template.getRow(template.getLastRowNum()).getCell(cell);
//	}
        if (row.getRowNum() < 1
                && template.getRow(row.getRowNum()) != null) {
            templateCell = template.getRow(row.getRowNum()).getCell(cell);
        } else if (template.getRow(1) != null) {
            templateCell = template.getRow(1).getCell(cell);
        }

        if (templateCell != null) {
            newCell.setCellType(templateCell.getCellType());
            newCell.setCellComment(templateCell.getCellComment());
            newCell.setCellStyle(templateCell.getCellStyle());
            newCell.setHyperlink(templateCell.getHyperlink());
            copyCellValue(templateCell, newCell);
        }
        return newCell;
    }

    public static void copyCellValue(Cell source, Cell destination) {
        if (source.getStringCellValue() != null && source.getStringCellValue().startsWith("DV=")) {
            // if cell contains data validation cell must contains DV= in the excel template
            createDataValidation((SXSSFSheet) destination.getSheet(), source);
        }
        switch (source.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                destination.setCellValue(source.getBooleanCellValue()); // boolean
                break;
            case Cell.CELL_TYPE_ERROR:
                destination.setCellValue(source.getErrorCellValue()); // byte
                break;
            case Cell.CELL_TYPE_NUMERIC:
                destination.setCellValue(source.getNumericCellValue()); // double
                break;
            case Cell.CELL_TYPE_FORMULA:
                if (destination.getRowIndex() < 1) {
                    destination.setCellFormula(source.getCellFormula());
                } else {
                    String newFormula = source.getCellFormula()
                            .replace("2",
                                    String.valueOf(destination.getRowIndex() + 1));
                    destination.setCellFormula(newFormula);
                }
                break;
            case Cell.CELL_TYPE_STRING:
            case Cell.CELL_TYPE_BLANK:
                destination.setCellValue(source.getStringCellValue()); // String
                break;
            default:
                throw new IllegalArgumentException();
        }

    }

    private static void createDataValidation(SXSSFSheet sheet, Cell cell) {
        CellRangeAddressList addressList = new CellRangeAddressList(
                1, 30000, cell.getColumnIndex(), cell.getColumnIndex());
        String cellRange = cell.getStringCellValue().substring(3);
        DataValidationConstraint dvConstraint = sheet.getDataValidationHelper().createFormulaListConstraint(cellRange);
        DataValidation validation = sheet.getDataValidationHelper().createValidation(
                dvConstraint, addressList);
        validation.setSuppressDropDownArrow(true);
        sheet.addValidationData(validation);
    }
}
