package org.jboss.tools.example.springmvc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.jboss.tools.example.springmvc.entity.ForexDataNew;

public class ForexUtils {
   
   public static final String DATE_FORMAT_STRING_DAY = "yyyy-MM-dd";
   public static final String DATE_FORMAT_STRING_MINUTE = "yyyy-MM-dd HH:mm";
   public static final String DATE_TIME_FORMAT_FRONT_END = "MM/dd/yyyy HH:mm";
   
   public static final int CONSTANT_INTERVAL = 10;
   
   public static final int FUTURE_SAMPLES = 25;

   public static Short getShortPeriod(short value) {
      return value;
   }
   
   public static Date getDateByDay(String dateStr) {
      try {
         SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING_DAY);
         format.setTimeZone(TimeZone.getTimeZone("GMT"));
         Date date = format.parse(dateStr);
         return date;
      } catch (ParseException e) {
         return null;
      }
   }
   
   public static Date getDateByMinute(String dateStr) {
      try {
         SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING_MINUTE);
         format.setTimeZone(TimeZone.getTimeZone("GMT"));
         Date date = format.parse(dateStr);
         return date;
      } catch (ParseException e) {
         return null;
      }
   }
   
   public static long getDateValueFromDate(Date dateObj) {
      return dateObj.getTime();
   }
   
   public static Date getDateFromDateValue(long value) {
      return new Date(value);
   }
   
   public static String getDateStringFromDate(Date dateObj) {
      try {
         SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING_MINUTE);
         format.setTimeZone(TimeZone.getTimeZone("GMT"));
         return format.format(dateObj);
      } catch (Exception e) {
         return null;
      }
   }
   
   public static String convertDateFromFrontEnd2BackEnd(String frontEnd) {
      try {
         SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT_FRONT_END);
         format.setTimeZone(TimeZone.getTimeZone("GMT"));
         Date date = format.parse(frontEnd);
         format = new SimpleDateFormat(DATE_FORMAT_STRING_MINUTE);
         format.setTimeZone(TimeZone.getTimeZone("GMT"));
         return format.format(date);
      } catch (ParseException e) {
         return frontEnd;
      }
   }
   
   public static String getStartDate(int code) {
      switch (code) {
      case 1:
         return "2016-04-14 00:00";
      case 2:
         return "2002-01-01 00:00";
      }
      return null;
   }
   
   public static short getPeriod(int code) {
      switch (code) {
      case 1:
         return 5;
      case 2:
         return 1440;
      }
      return 0;
   }
   
   public static String getNextMinute(String dateTimeStr, int minute) {
      Date date = getDateByMinute(dateTimeStr);
      if (date == null) {
         return null;
      }
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.add(Calendar.MINUTE, minute);
      date = cal.getTime();
      return getDateStringFromDate(date);
   }
   
   public static boolean isMinimum(List<ForexDataNew> dataList, int pivot, boolean isLeft) {
      boolean status = true;
      float minPivot = dataList.get(pivot).getLow();
      if (isLeft) {
         for (int i = 1; i <= CONSTANT_INTERVAL; i++) {
            int index = pivot - i;
            if (index < 0) {
               status = false; break;
            }
            ForexDataNew dataItem = dataList.get(index);
            if (dataItem.getLow() < minPivot) {
               status = false; break;
            }
         }
      } else {
         for (int i = 1; i <= CONSTANT_INTERVAL; i++) {
            int index = pivot + i;
            if (index >= dataList.size()) {
               status = false; break;
            }
            ForexDataNew dataItem = dataList.get(index);
            if (dataItem.getLow() < minPivot) {
               status = false; break;
            }
         }
      }
      return status;
   }
   
   public static boolean isMaximum(List<ForexDataNew> dataList, int pivot, boolean isLeft) {
      boolean status = true;
      float maxPivot = dataList.get(pivot).getHigh();
      if (isLeft) {
         for (int i = 1; i <= CONSTANT_INTERVAL; i++) {
            int index = pivot - i;
            if (index < 0) {
               status = false; break;
            }
            ForexDataNew dataItem = dataList.get(index);
            if (dataItem.getHigh() > maxPivot) {
               status = false; break;
            }
         }
      } else {
         for (int i = 1; i <= CONSTANT_INTERVAL; i++) {
            int index = pivot + i;
            if (index >= dataList.size()) {
               status = false; break;
            }
            ForexDataNew dataItem = dataList.get(index);
            if (dataItem.getHigh() > maxPivot) {
               status = false; break;
            }
         }
      }
      return status;
   }
   
   public static int getMaxIndex(List<Double> data) {
      int index = 0;
      double maxValue = data.get(0);
      for (int i = 1; i < data.size(); i++) {
         if (data.get(i) > maxValue) {
            index = i;
            maxValue = data.get(i);
         }
      }
      return index;
   }
   
   public static double getMaxValueInList(List<Double> data) {
      double maxValue = data.get(0);
      for (int i = 1; i < data.size(); i++) {
         if (data.get(i) > maxValue) {
            maxValue = data.get(i);
         }
      }
      return maxValue;
   }
   
   public static List<Double> createList(double... dataArray) {
      List<Double> result = new ArrayList<Double>();
      for (double data : dataArray) {
         result.add(data);
      }
      return result;
   }
   
   public static int getCode(String forexName) {
      if ("EURUSD".equals(forexName)) {
         return 1;
      } else if ("TAIEX".equals(forexName)) {
         return 2;
      }
      return 0;
   }
}
