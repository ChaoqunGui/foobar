package com.dr.license.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Time util
 * 
 * @author thomas.gui 2015-2-4
 */
public class DateUtil {

  /**
   * Convert the date(Date type) to the String type by the given format
   * 
   * @param date
   * @param formatType
   * @return
   */
  public static String dateToString(Date date, String formatType) {
    return new SimpleDateFormat(formatType).format(date);
  }

  /**
   * Covert the date(String type) to the Date type by the given format
   * 
   * @param strTime
   * @param formatType
   * @return
   * @throws ParseException
   */
  public static Date stringToDate(String strTime, String formatType)
      throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat(formatType);
    Date date = null;
    date = formatter.parse(strTime);
    return date;
  }

  /**
   * Convert the date(millisecond) to the Date type by the given format
   * 
   * @param currentTime
   * @param formatType
   * @return
   * @throws ParseException
   */
  public static Date longToDate(long currentTime, String formatType)
      throws ParseException {
    Date dateOld = new Date(currentTime);
    String sDateTime = dateToString(dateOld, formatType);
    Date date = stringToDate(sDateTime, formatType);
    return date;
  }

  /**
   * Convert the date(millisecond) to the String type by the given format
   * 
   * @param currentTime
   * @param formatType
   * @return
   * @throws ParseException
   */
  public static String longToString(long currentTime, String formatType)
      throws ParseException {
    Date date = longToDate(currentTime, formatType);
    String strTime = dateToString(date, formatType);
    return strTime;
  }

  /**
   * calculate how many days from now to the given time
   * 
   * @param now
   * @return
   * @throws ParseException
   */
  public static int daysBeforeNow(long now) throws ParseException {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date(System.currentTimeMillis()));
    long time2 = cal.getTimeInMillis();
    long between_days = (time2 - now) / (1000 * 3600 * 24);

    return Integer.parseInt(String.valueOf(between_days));
  }

  public static void main(String[] args) throws Exception {
  }
}
