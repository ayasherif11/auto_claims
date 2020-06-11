/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.utils.excel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mohamed.abd-elwadod
 */
public class ExcelSheet {

    private String name;
    private int index;
    private List<Object> header;
    //private List<Integer> columnIndexs;
    private int headerIndex = 0;
    private List<ExcelSheetQuery> excelSheetQueries = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Object> getHeader() {
        return header;
    }

    public void setHeader(List<Object> header) {
        this.header = header;
    }

    public int getHeaderIndex() {
        return headerIndex;
    }

    public void setHeaderIndex(int headerIndex) {
        this.headerIndex = headerIndex;
    }

    public List<ExcelSheetQuery> getExcelSheetQueries() {
        return excelSheetQueries;
    }

    public void setExcelSheetQueries(List<ExcelSheetQuery> excelSheetQueries) {
        this.excelSheetQueries = excelSheetQueries;
    }
}
