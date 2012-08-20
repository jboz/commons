package com.javaboz.commons.test;

import static com.javaboz.commons.test.DateUtils.ENDFILE_DATE_FORMAT;
import static com.javaboz.commons.test.DateUtils.FR_DATE_FORMAT;
import static com.javaboz.commons.test.DateUtils.between;
import static com.javaboz.commons.test.DateUtils.clearTime;
import static com.javaboz.commons.test.DateUtils.compareTo;
import static com.javaboz.commons.test.DateUtils.format;
import static com.javaboz.commons.test.DateUtils.getToday;
import static com.javaboz.commons.test.DateUtils.isAfter;
import static com.javaboz.commons.test.DateUtils.isAfterOrEquals;
import static com.javaboz.commons.test.DateUtils.isBefore;
import static com.javaboz.commons.test.DateUtils.isBeforeOrEquals;
import static com.javaboz.commons.test.DateUtils.isEquals;
import static com.javaboz.commons.test.DateUtils.max;
import static com.javaboz.commons.test.DateUtils.parse;
import static com.javaboz.commons.test.DateUtils.parseDateTime;
import static com.javaboz.commons.test.DateUtils.parseTime;
import static com.javaboz.commons.test.DateUtils.toCalendar;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.ReadableInstant;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test de la classe {@link DateUtils}.
 *
 * @author Julien Boz
 */
public class DateUtilsTest {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

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

	private static Date createDate(final int year, final int month, final int day) {
		return createDate(year, month, day, 0, 0, 0, 0);
	}

	private static Date createDate(final int year, final int month, final int day, final int hour, final int minute,
			final int second, final int millsecond) {
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
		assertThat(parse(null)).isNull();
		assertThat(parse("")).isNull();
		assertThat(parse("   ")).isNull();
		assertThat(parse("as4df5687")).isNull();

		assertThat(parse("1974-12-12T00:00:00")).isEqualTo(createDate(1974, 12, 12, 0, 0, 0, 0));
		assertThat(parse("2000-12-25T23:59:15")).isEqualTo(createDate(2000, 12, 25, 23, 59, 15, 0));
		assertThat(parse("2000-12-25T23:59")).isEqualTo(createDate(2000, 12, 25, 23, 59, 0, 0));
		assertThat(parse("2000-12-25T23")).isEqualTo(createDate(2000, 12, 25, 23, 0, 0, 0));
		assertThat(parse("2000-12-25")).isEqualTo(createDate(2000, 12, 25, 0, 0, 0, 0));
		assertThat(parse("31.12.2015")).isEqualTo(createDate(2015, 12, 31));

		assertThat(parse("2012-01-01", null)).isNull();
		assertThat(parse("2012-01-01", "")).isNull();

		assertThat(parse("20120225155619", ENDFILE_DATE_FORMAT)).isEqualTo(createDate(2012, 02, 25, 15, 56, 19, 00));

		assertThat(parse("25 Décembre 2015 à 10h20", "dd MMMM yyyy 'à' hh'h'mm")).isEqualTo(createDate(2015, 12, 25, 10, 20, 0, 0));
	}

	@Test
	public void testParseDateTime() {
		assertThat(parseDateTime(null)).isNull();
		assertThat(parseDateTime("")).isNull();
		assertThat(parseDateTime("   ")).isNull();
		assertThat(parseDateTime("as4df5687")).isNull();

		assertThat(parseDateTime("31.12.2015", FR_DATE_FORMAT)).isInstanceOf(DateTime.class);
		assertThat(parseDateTime("31.12.2015", FR_DATE_FORMAT).toDate()).isEqualTo(createDate(2015, 12, 31));

		assertThat(parseDateTime("25 Décembre 2015 à 10h20", "dd MMMM yyyy 'à' hh'h'mm").toDate()).isEqualTo(
				createDate(2015, 12, 25, 10, 20, 0, 0));
	}

	@Test
	public void testFormat() {
		assertThat(format(null)).isEqualTo("null");

		assertThat(format(createDate(2012, 12, 31, 12, 15, 25, 150))).isEqualTo("31.12.2012");

		if (Locale.GERMAN.equals(Locale.getDefault())) {
			assertThat(format(createDate(2015, 10, 12, 10, 20, 0, 0), "dd MMMM yyyy 'à' hh'h'mm")).isEqualTo("12 October 2015 à 10h20");
		} else {
			assertThat(format(createDate(2015, 10, 12, 10, 20, 0, 0), "dd MMMM yyyy 'à' hh'h'mm")).isEqualTo("12 octobre 2015 à 10h20");
		}
	}

	@Test
	public void testClearDate() {
		assertThat(clearTime(null)).isNull();
		assertThat(clearTime(createDate(2012, 03, 01, 12, 15, 45, 85))).isEqualTo(createDate(2012, 03, 01, 0, 0, 0, 0));
	}

	@Test
	public void testGetToday() {
		final Calendar today = Calendar.getInstance();
		assertThat(getToday()).isEqualTo(
				createDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), 0, 0, 0, 0));
	}

	@Test
	public void testToCalendar() {
		final Calendar today = Calendar.getInstance();
		assertThat(toCalendar(getToday()).getTime()).isEqualTo(
				createDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), 0, 0, 0, 0));
	}

	@Test
	public void testBetween_error() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("ReadableInstant objects must not be null");

		between((ReadableInstant) null, (ReadableInstant) null, DurationFieldType.years());
	}

	@Test
	public void testBetween_error1() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("ReadableInstant objects must not be null");

		between(parseDateTime("01.01.2000"), (ReadableInstant) null, DurationFieldType.years());
	}

	@Test
	public void testBetween_error2() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("ReadableInstant objects must not be null");

		between((ReadableInstant) null, parseDateTime("01.01.2000"), DurationFieldType.years());
	}

	@Test
	public void testBetween() {
		assertThat(between(parseDateTime("01.01.2000"), parseDateTime("31.12.2000"), DurationFieldType.years())).isEqualTo(0);
		assertThat(between(parseDateTime("01.01.2000"), parseDateTime("31.12.2000"), DurationFieldType.months())).isEqualTo(11);
		assertThat(between(parseDateTime("01.01.2000"), parseDateTime("31.12.2000"), DurationFieldType.days())).isEqualTo(365);
		assertThat(between(parseDateTime("01.01.2000"), parseDateTime("31.12.2000"), DurationFieldType.weeks())).isEqualTo(52);

		assertThat(between(parse("01.01.2000"), parse("31.12.2000"), DurationFieldType.years())).isEqualTo(0);
		assertThat(between(parse("01.01.2000"), parse("31.12.2000"), DurationFieldType.weeks())).isEqualTo(52);
	}

	@Test
	public void testMax() {
		assertThat(max(null, null)).isNull();
		assertThat(max(null, parse("01.01.2000"))).isEqualTo(parse("01.01.2000"));
		assertThat(max(parse("01.01.2000"), null)).isEqualTo(parse("01.01.2000"));
		assertThat(max(parse("01.01.2000"), parse("01.01.2012"))).isEqualTo(parse("01.01.2012"));
		assertThat(max(parse("01.01.2230"), parse("01.01.2012"))).isEqualTo(parse("01.01.2230"));
	}

	@Test
	public void testParseTime() {
		assertThat(parseTime("00:00:00")).isEqualTo(0);
		assertThat(parseTime("00:03:00")).isEqualTo(180000);
		assertThat(parseTime("00:03")).isEqualTo(180000);
		assertThat(parseTime("03:10")).isEqualTo(11400000);
		assertThat(parseTime("00:00:10")).isEqualTo(10000);
		assertThat(parseTime("00:03:10")).isEqualTo(190000);
		assertThat(parseTime("01:00:00")).isEqualTo(3600000);
		assertThat(parseTime("01")).isEqualTo(3600000);
		assertThat(parseTime("01:00:10")).isEqualTo(3610000);
		assertThat(parseTime("01:03:10")).isEqualTo(3790000);
	}

	@Test
	public void testCompareTO_andCo() {
		assertThat(compareTo(parse("01.01.2012"), parse("01.01.2012"))).isEqualTo(0);
		assertThat(compareTo(parse("01.01.2010"), parse("01.01.2012"))).isEqualTo(-1);
		assertThat(compareTo(parse("01.01.2012"), parse("01.01.2010"))).isEqualTo(+1);

		assertThat(compareTo(null, null)).isEqualTo(0);
		assertThat(compareTo(parse("01.01.2010"), null)).isEqualTo(+1);
		assertThat(compareTo(null, parse("01.01.2010"))).isEqualTo(-1);
		final Date date = parse("01.01.2010");
		assertThat(compareTo(date, date)).isEqualTo(0);

		assertThat(isEquals(parse("01.01.2012"), parse("01.01.2012"))).isTrue();
		assertThat(isEquals(parse("01.01.2010"), parse("01.01.2012"))).isFalse();
		assertThat(isEquals(parse("01.01.2012"), parse("01.01.2010"))).isFalse();

		assertThat(isBefore(parse("01.01.2012"), parse("01.01.2012"))).isFalse();
		assertThat(isBefore(parse("01.01.2010"), parse("01.01.2012"))).isTrue();
		assertThat(isBefore(parse("01.01.2012"), parse("01.01.2010"))).isFalse();

		assertThat(isAfter(parse("01.01.2012"), parse("01.01.2012"))).isFalse();
		assertThat(isAfter(parse("01.01.2010"), parse("01.01.2012"))).isFalse();
		assertThat(isAfter(parse("01.01.2012"), parse("01.01.2010"))).isTrue();

		assertThat(isBeforeOrEquals(parse("01.01.2012"), parse("01.01.2012"))).isTrue();
		assertThat(isBeforeOrEquals(parse("01.01.2010"), parse("01.01.2012"))).isTrue();
		assertThat(isBeforeOrEquals(parse("01.01.2012"), parse("01.01.2010"))).isFalse();

		assertThat(isAfterOrEquals(parse("01.01.2012"), parse("01.01.2012"))).isTrue();
		assertThat(isAfterOrEquals(parse("01.01.2010"), parse("01.01.2012"))).isFalse();
		assertThat(isAfterOrEquals(parse("01.01.2012"), parse("01.01.2010"))).isTrue();
	}
}
