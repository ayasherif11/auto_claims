/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.utils.excel;

import com.unilever.autoclaims.model.ConstantStrings;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateService {

    public static final String dateFormatString = ConstantStrings.DATE_FORMATE;

    private DateService() {
    }

    public static String getDateString(Date periodDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);
        return ((periodDate == null) ? null : sdf.format(periodDate));
    }

    public static String getDateString(Date periodDate, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return ((periodDate == null) ? null : sdf.format(periodDate));
    }

    public static Date getDate(String periodDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);
        Date date;
        try {
            date = ((periodDate == null) ? null : sdf.parse(periodDate));
        } catch (ParseException ex) {
            date = null;
        }
        return date;
    }

    public static Date getDate(String periodDate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date;
        try {
            date = ((periodDate == null) ? null : sdf.parse(periodDate));
        } catch (ParseException ex) {
            date = null;
        }
        return date;
    }

    public static Date getNextMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

    public static Date formatDate(Date date) {
        String formattedDate = getDateString(date);
        return getDate(formattedDate);
    }

    public static Date getPrevoiusMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    public static List<Date> getPreviousMonthsOfCurrentYear() {
        List<Date> monthsList = new ArrayList<Date>();
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(new Date());
        c.set(java.util.Calendar.DAY_OF_YEAR, 1);
        Date month = c.getTime();
        while (!getDateString(new Date()).equals(getDateString(month))) {
            if (month != null) {
                monthsList.add(month);
                month = getNextMonth(month);
            }
        }
        return monthsList;
    }

    public static List getNextMonthsFromSelectedMonth(Date startDate, int numberOfMonths) {
        List monthsList = new ArrayList();
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(startDate);
        monthsList.add(getDateString(c.getTime()));
        for (int i = 1; i < numberOfMonths; i++) {
            c.add(java.util.Calendar.MONTH, 1);
            monthsList.add(getDateString(c.getTime()));
        }
        return monthsList;
    }

    public static List<Date> getNextMonthsDateFromSelectedMonth(Date startDate, int numberOfMonths) {
        List<Date> dateList = new ArrayList<Date>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        for (int i = 0; i < numberOfMonths; i++) {
            dateList.add(cal.getTime());
            cal.add(Calendar.MONTH, 1);
        }
        return dateList;
    }

    public static List<Date> getIntersectionPeriod(Date firstPeriodStart, Date firstPeriodEnd, Date secondPeriodStart, Date secondPeriodEnd) {
        List<Date> intersectionList = new ArrayList<Date>();
        // secondPeriodStart <= firstPeriodStart && firstPeriodStart <= secondPeriodEnd <= firstPeriodEnd
        if (secondPeriodStart.compareTo(firstPeriodStart) <= 0 && secondPeriodEnd.compareTo(firstPeriodStart) >= 0 && secondPeriodEnd.compareTo(firstPeriodEnd) <= 0) {
            intersectionList.add(firstPeriodStart);
            intersectionList.add(secondPeriodEnd);
        }// secondPeriodStart <= firstPeriodStart && secondPeriodEnd >= firstPeriodEnd
        else if (secondPeriodStart.compareTo(firstPeriodStart) <= 0 && secondPeriodEnd.compareTo(firstPeriodEnd) >= 0) {
            intersectionList.add(firstPeriodStart);
            intersectionList.add(firstPeriodEnd);
        }// firstPeriodStart <= secondPeriodStart <= firstPeriodEnd && secondPeriodEnd <= secondPeriodEnd
        else if (firstPeriodStart.compareTo(secondPeriodStart) <= 0 && secondPeriodStart.compareTo(firstPeriodEnd) <= 0 && firstPeriodEnd.compareTo(secondPeriodEnd) <= 0) {
            intersectionList.add(secondPeriodStart);
            intersectionList.add(firstPeriodEnd);
        }// firstPeriodStart <= secondPeriodStart <= firstPeriodEnd && secondPeriodEnd >= secondPeriodEnd
        else if (firstPeriodStart.compareTo(secondPeriodStart) <= 0 && secondPeriodStart.compareTo(firstPeriodEnd) <= 0 && firstPeriodEnd.compareTo(secondPeriodEnd) >= 0) {
            intersectionList.add(secondPeriodStart);
            intersectionList.add(secondPeriodEnd);
        }
        return intersectionList;
    }

    /**
     * adding number of days, months, ..etc to date
     *
     * @param date
     * @param number
     * @param fieldType : the calendar field ex : Calendar.MONTH
     * @return
     */
    public static Date add(Date date, int number, int fieldType) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(fieldType, number);
        return cal.getTime();
    }

    public static Date subtract(Date date, int number, int fieldType) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(fieldType, -number);
        return cal.getTime();
    }

    public static List<Date> getNextMonthsDateAndHistoricalMonthsFromSelectedMonth(Date startDate, int numberOfMonths) {
        List<Date> dateList = new ArrayList<Date>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        int historicalMonthsNo = cal.get(Calendar.MONTH) - Calendar.JANUARY;
        cal.set(Calendar.MONTH, 0);
        for (int i = 0; i < historicalMonthsNo + numberOfMonths; i++) {
            dateList.add(cal.getTime());
            cal.add(Calendar.MONTH, 1);
        }
        return dateList;
    }

    public static List<Date> getMonthsBetween(Date startDate, Date endDate) {
        endDate = truncDate(endDate);
        startDate = truncDate(startDate);
        List<Date> dateList = new ArrayList<Date>();
        Date month = startDate;
        dateList.add(month);
        while (!month.equals(endDate)) {
            month = DateService.getNextMonth(month);
            month = truncDate(month);
            dateList.add(month);
        }
        return dateList;
    }

    public static List<String> getMonthsStringBetween(Date startDate, Date endDate) {
        String endDateStr = getDateString(endDate);
        startDate = truncDate(startDate);
        List<String> dateList = new ArrayList<String>();
        String month = getDateString(startDate);
        dateList.add(month);
        while (!month.equals(endDateStr)) {
            startDate = DateService.getNextMonth(startDate);
            month = getDateString(startDate);
            dateList.add(month);
        }
        return dateList;
    }

    public static Date truncCurrentDate() {
        return truncDate(new Date());
    }

    public static Date truncDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(java.util.Calendar.DAY_OF_MONTH, 1);
        c.set(java.util.Calendar.HOUR_OF_DAY, 0);
        c.clear(java.util.Calendar.MINUTE);
        c.clear(java.util.Calendar.SECOND);
        c.clear(java.util.Calendar.MILLISECOND);
        return c.getTime();
    }
}
