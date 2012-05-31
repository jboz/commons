/*
 * Copyright 2012 J.Boz and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.javaboz.commons.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DurationFieldType;
import org.joda.time.ReadableInstant;

/**
 * Utilitaire sur les dates.
 * 
 * @author jboz
 */
public final class DateUtils {

  public static final String SQL_DATE_FORMAT = "yyyy-MM-dd";
  public static final String FR_DATE_FORMAT = "dd.MM.yyyy";

  private DateUtils() {
  }

  /**
   * Thread safe date/time formatter.<br>
   * Each thread gets it's own instance of the formatter, which is maintained for the lifetime of the thread.
   */
  private static ThreadLocal<SimpleDateFormat> sqlFormatter = new ThreadLocal<SimpleDateFormat>() {

    // use inner class to initialise instance per thread
    @Override
    protected SimpleDateFormat initialValue() {
      return new SimpleDateFormat(SQL_DATE_FORMAT);
    }
  };
  private static ThreadLocal<SimpleDateFormat> frenchFormatter = new ThreadLocal<SimpleDateFormat>() {

    // use inner class to initialise instance per thread
    @Override
    protected SimpleDateFormat initialValue() {
      return new SimpleDateFormat(FR_DATE_FORMAT);
    }
  };

  /**
   * @return thread safe date formatter
   */
  public static final SimpleDateFormat getFormatter(final String dateFormat) {
    if (SQL_DATE_FORMAT.equals(dateFormat) || StringUtils.isBlank(dateFormat)) {
      return sqlFormatter.get();
    }
    if (FR_DATE_FORMAT.equals(dateFormat)) {
      return frenchFormatter.get();
    }
    return new SimpleDateFormat(dateFormat);
  }

  // défini après le thread local !
  /** date min */
  public static Date MIN_VALUE = parse("01.01.0001");
  /** date max */
  public static Date MAX_VALUE = parse("31.12.9999");

  /**
   * Parse une chaîne de type : {@value #FR_DATE_FORMAT}.
   */
  public static Date parse(final String date) {
    return parse(date, FR_DATE_FORMAT);
  }

  /**
   * Parse une chaîne de type : {@value #SQL_DATE_FORMAT}.
   */
  public static Date parseSQL(final String date) {
    return parse(date, SQL_DATE_FORMAT);
  }

  /**
   * Parse une chaîne en date.
   */
  public static Date parse(final String date, final String dateFormat) {
    if (StringUtils.isBlank(date)) {
      return null;
    }
    try {
      return getFormatter(dateFormat).parse(date);
    } catch (final ParseException e) {
      return null;
    }
  }

  /**
   * Parse une chaîne en date.
   */
  public static DateTime parseDateTime(final String date) {
    return parseDateTime(date, FR_DATE_FORMAT);
  }

  /**
   * Parse une chaîne en date.
   */
  public static DateTime parseDateTime(final String date, final String dateFormat) {
    final Date parsed = parse(date, dateFormat);
    if (parsed == null) {
      return null;
    }
    return new DateTime(parsed);
  }

  /**
   * @return true si <code>date</code> match le pattern {@value #FR_DATE_FORMAT}.
   */
  public static boolean isValidDate(final String date) {
    return isValidDate(date, FR_DATE_FORMAT);
  }

  /**
   * @return true si <code>date</code> match le pattern <code>dateFormat</code>.
   */
  public static boolean isValidDate(final String date, final String dateFormat) {
    try {
      getFormatter(dateFormat).parse(date);
    } catch (final ParseException e) {
      return false;
    }
    return true;
  }

  /**
   * @return la date au format {@value #FR_DATE_FORMAT}.
   */
  public static String format(final Date date) {
    return format(date, FR_DATE_FORMAT);
  }

  /**
   * @return la date au format <code>dateFormat</code>
   */
  public static String format(final Date date, final String dateFormat) {
    return getFormatter(dateFormat).format(date);
  }

  /**
   * Supprime les heures de la date.
   */
  public static Date clearTime(final Date date) {
    if (date == null) {
      return null;
    }
    final Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal.getTime();
  }

  /**
   * @return la date du jour sans les heures.
   */
  public static Date getToday() {
    return clearTime(new Date());
  }

  /**
   * @param date
   * @return a {@link Calendar}
   */
  public static Calendar toCalendar(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    return calendar;
  }

  /**
   * Calculates the number of whole units between the two specified datetimes.
   * 
   * @param start the start instant, validated to not be null
   * @param end the end instant, validated to not be null
   * @param field the field type to use, must not be null
   * @return the period
   * @throws IllegalArgumentException if the instants are null or invalid
   */
  public static int between(final ReadableInstant start, final ReadableInstant end, final DurationFieldType field) {
    if (start == null || end == null) {
      throw new IllegalArgumentException("ReadableInstant objects must not be null");
    }
    final Chronology chrono = DateTimeUtils.getInstantChronology(start);

    return field.getField(chrono).getDifference(end.getMillis(), start.getMillis());
  }
}
