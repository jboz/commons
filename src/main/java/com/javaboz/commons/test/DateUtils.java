package com.javaboz.commons.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.ReadableInstant;

/**
 * Utilitaire sur les dates.
 *
 * @author Julien Boz
 */
public final class DateUtils {

	public static final String FR_DATE_FORMAT = "dd.MM.yyyy";
	public static final String ENDFILE_DATE_FORMAT = "yyyyMMddHHmmss";
	public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
	public static final String ISO_FORMAT_NO_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss";

	private DateUtils() {
	}

	/**
	 * Thread safe date/time formatter.<br>
	 * Each thread gets it's own instance of the formatter, which is maintained for the lifetime of the thread.
	 */
	private static ThreadLocal<SimpleDateFormat> frenchFormatter = new ThreadLocal<SimpleDateFormat>() {

		// use inner class to initialise instance per thread
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(FR_DATE_FORMAT);
		}
	};
	private static ThreadLocal<SimpleDateFormat> fileDateFormatter = new ThreadLocal<SimpleDateFormat>() {

		// use inner class to initialise instance per thread
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(ENDFILE_DATE_FORMAT);
		}
	};

	/**
	 * @return thread safe date formatter
	 */
	public static final SimpleDateFormat getFormatter(final String dateFormat) {
		if (FR_DATE_FORMAT.equals(dateFormat)) {
			return frenchFormatter.get();
		}
		if (ENDFILE_DATE_FORMAT.equals(dateFormat)) {
			return fileDateFormatter.get();
		}
		return new SimpleDateFormat(dateFormat);
	}

	// défini après le thread local !
	/** date min, take care of use, do not use instead of null value */
	public static final Date MIN_VALUE = parse("01.01.0001");
	/** date max, take care of use, do not use instead of null value */
	public static final Date MAX_VALUE = parse("31.12.9999");

	/**
	 * Parse une chaîne au format ISO ou {@value #FR_DATE_FORMAT}.
	 */
	public static Date parse(final String date) {
		if (StringUtils.isBlank(date)) {
			return null;
		}
		try {
			return DateTime.parse(date).toDate();
		} catch (final Exception e) {
			return parse(date, FR_DATE_FORMAT);
		}
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
		} catch (final Exception e) {
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
		if (date == null) {
			return "null";
		}
		return getFormatter(dateFormat).format(date);
	}

	/**
	 * Supprime les heures de la date.
	 */
	public static Date clearTime(final Date date) {
		if (date == null) {
			return null;
		}
		return LocalDate.fromDateFields(date).toDate();
	}

	/**
	 * @return la date du jour sans les heures.
	 */
	public static Date getToday() {
		return now();
	}

	/**
	 * @return la date du jour sans les heures.
	 */
	public static Date now() {
		return LocalDate.now().toDate();
	}

	/**
	 * @param date
	 * @return a {@link Calendar}
	 */
	public static Calendar toCalendar(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		return calendar;
	}

	/**
	 * Calculates the number of whole units between the two specified date.
	 *
	 * @see #between(ReadableInstant, ReadableInstant, DurationFieldType)
	 */
	public static int between(final Date start, final Date end, final DurationFieldType field) {
		return between(new DateTime(start), new DateTime(end), field);
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

	/**
	 * @return the maximum of two dates. A null date is treated as being less than any non-null date.
	 */
	public static Date max(final Date d1, final Date d2) {
		if (d1 == null) {
			return d2;
		}
		if (d2 == null) {
			return d1;
		}
		return d2.after(d1) ? d2 : d1;
	}

	/**
	 * Get time in milliseconds.
	 */
	public static long parseTime(final String time) {
		return LocalTime.parse(time).get(DateTimeFieldType.secondOfDay()) * 1000;
	}

	/**
	 * Compare two dates, ignore hours, check null values.
	 */
	public static int compareTo(final Date dateA, final Date dateB) {
		if (dateA == null && dateB != null) {
			return -1;
		}
		if (dateA != null && dateB == null) {
			return +1;
		}
		if (dateA == null && dateB == null || dateA == dateB) {
			// optimisation
			return 0;
		}
		return new LocalDate(dateA).compareTo(new LocalDate(dateB));
	}

	public static boolean isEquals(final Date dateA, final Date dateB) {
		return compareTo(dateA, dateB) == 0;
	}

	public static boolean isBefore(final Date dateA, final Date dateB) {
		return compareTo(dateA, dateB) == -1;
	}

	public static boolean isAfter(final Date dateA, final Date dateB) {
		return compareTo(dateA, dateB) == 1;
	}

	public static boolean isBeforeOrEquals(final Date dateA, final Date dateB) {
		return compareTo(dateA, dateB) <= 0;
	}

	public static boolean isAfterOrEquals(final Date dateA, final Date dateB) {
		return compareTo(dateA, dateB) >= 0;
	}
}
