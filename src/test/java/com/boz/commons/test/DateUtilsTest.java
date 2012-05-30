package com.boz.commons.test;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.boz.commons.test.DateUtils;

/**
 * Test de la classe {@link DateUtils}.
 * 
 * @author jboz
 */
public class DateUtilsTest {

  private static Locale defaultLocale;

  @BeforeClass
  public static void setup() {
    // on passe en locale FR pour avoir la même local sut tous les environnement
    defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.FRANCE);
  }

  @AfterClass
  public static void tearDown() {
    // on restaure la locale à la fin du test
    Locale.setDefault(defaultLocale);
  }

  private static Date createDate(int year, int month, int day) {
    return createDate(year, month, day, 0, 0, 0, 0);
  }

  private static Date createDate(int year, int month, int day, int hour, int minute, int second, int millsecond) {
    final Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minute);
    cal.set(Calendar.SECOND, second);
    cal.set(Calendar.MILLISECOND, millsecond);

    return cal.getTime();
  }

  @Test
  public void testParse() {
    assertThat(DateUtils.parse(null)).isNull();
    assertThat(DateUtils.parse("")).isNull();
    assertThat(DateUtils.parse("   ")).isNull();
    assertThat(DateUtils.parse("as4df5687")).isNull();

    assertThat(DateUtils.parse("2012-01-01", DateUtils.SQL_DATE_FORMAT)).isEqualTo(createDate(2012, 01, 01));

    assertThat(DateUtils.parse("31.12.2015")).isEqualTo(createDate(2015, 12, 31));

    assertThat(DateUtils.parse("25 Décembre 2015 à 10h20", "dd MMMM yyyy 'à' hh'h'mm")).isEqualTo(
        createDate(2015, 12, 25, 10, 20, 0, 0));
  }

  @Test
  public void testParseDateTime() {
    assertThat(DateUtils.parseDateTime(null)).isNull();
    assertThat(DateUtils.parseDateTime("")).isNull();
    assertThat(DateUtils.parseDateTime("   ")).isNull();
    assertThat(DateUtils.parseDateTime("as4df5687")).isNull();

    assertThat(DateUtils.parseDateTime("2012-01-01", DateUtils.SQL_DATE_FORMAT)).isInstanceOf(DateTime.class);
    assertThat(DateUtils.parseDateTime("31.12.2015", DateUtils.FR_DATE_FORMAT)).isInstanceOf(DateTime.class);

    assertThat(DateUtils.parseDateTime("2012-01-01", DateUtils.SQL_DATE_FORMAT).toDate()).isEqualTo(createDate(2012, 01, 01));

    assertThat(DateUtils.parseDateTime("31.12.2015", DateUtils.FR_DATE_FORMAT).toDate()).isEqualTo(createDate(2015, 12, 31));

    assertThat(DateUtils.parseDateTime("25 Décembre 2015 à 10h20", "dd MMMM yyyy 'à' hh'h'mm").toDate()).isEqualTo(
        createDate(2015, 12, 25, 10, 20, 0, 0));
  }

  @Test
  public void testFormat() {
    assertThat(DateUtils.format(createDate(2012, 01, 01), DateUtils.SQL_DATE_FORMAT)).isEqualTo("2012-01-01");
    assertThat(DateUtils.format(createDate(2012, 01, 01, 12, 15, 25, 150), DateUtils.SQL_DATE_FORMAT)).isEqualTo("2012-01-01");

    assertThat(DateUtils.format(createDate(2012, 12, 31, 12, 15, 25, 150))).isEqualTo("31.12.2012");

    if (Locale.GERMAN.equals(Locale.getDefault())) {
      assertThat(DateUtils.format(createDate(2015, 10, 12, 10, 20, 0, 0), "dd MMMM yyyy 'à' hh'h'mm")).isEqualTo(
          "12 October 2015 à 10h20");
    } else {
      assertThat(DateUtils.format(createDate(2015, 10, 12, 10, 20, 0, 0), "dd MMMM yyyy 'à' hh'h'mm")).isEqualTo(
          "12 octobre 2015 à 10h20");
    }
  }

  @Test
  public void testClearDate() {
    assertThat(DateUtils.clearTime(null)).isNull();
    assertThat(DateUtils.clearTime(createDate(2012, 03, 01, 12, 15, 45, 85))).isEqualTo(createDate(2012, 03, 01, 0, 0, 0, 0));
  }

  @Test
  public void testGetToday() {
    final Calendar today = Calendar.getInstance();
    assertThat(DateUtils.getToday()).isEqualTo(
        createDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), 0, 0, 0, 0));
  }

  @Test
  public void testToCalendar() {
    final Calendar today = Calendar.getInstance();
    assertThat(DateUtils.toCalendar(DateUtils.getToday()).getTime()).isEqualTo(
        createDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), 0, 0, 0, 0));
  }

  @Test
  public void testBetween() {
    assertThat(
        DateUtils.between(DateUtils.parseDateTime("01.01.2000"), DateUtils.parseDateTime("31.12.2000"), DurationFieldType.years()))
        .isEqualTo(0);
    assertThat(
        DateUtils.between(DateUtils.parseDateTime("01.01.2000"), DateUtils.parseDateTime("31.12.2000"),
            DurationFieldType.months())).isEqualTo(11);
    assertThat(
        DateUtils.between(DateUtils.parseDateTime("01.01.2000"), DateUtils.parseDateTime("31.12.2000"), DurationFieldType.days()))
        .isEqualTo(365);
    assertThat(
        DateUtils.between(DateUtils.parseDateTime("01.01.2000"), DateUtils.parseDateTime("31.12.2000"), DurationFieldType.weeks()))
        .isEqualTo(52);
  }
}
