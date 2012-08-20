package com.javaboz.commons.test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.complex.ComplexFormat;

/**
 * Utilisataire sur les nombres.
 *
 * @author Julien Boz
 */
public final class NumberUtils extends org.apache.commons.lang.math.NumberUtils {

	/* imaginary symbol */
	public static final String IMAGINARY = "IMAGINARY";

	private NumberUtils() {
	}

	/**
	 * Thread safe number formatter.<br>
	 * Each thread gets it's own instance of the formatter, which is maintained for the lifetime of the thread.
	 */
	protected static ThreadLocal<NumberFormat> formatter = new ThreadLocal<NumberFormat>() {

		// use inner class to initialize instance per thread
		@Override
		protected NumberFormat initialValue() {
			return new DecimalFormat("#,###.##", new DecimalFormatSymbols());
		}
	};
	protected static ThreadLocal<ComplexFormat> complexFormat = new ThreadLocal<ComplexFormat>() {

		// use inner class to initialize instance per thread
		@Override
		protected ComplexFormat initialValue() {
			return new ComplexFormat(IMAGINARY, NumberUtils.getFormatter());
		}
	};

	/**
	 * @return thread safe date formatter
	 */
	public static final NumberFormat getFormatter() {
		return formatter.get();
	}

	/**
	 * @return thread safe date formatter
	 */
	public static final ComplexFormat getComplexFormat() {
		return complexFormat.get();
	}

	/**
	 * @return number formatted
	 */
	public static String format(final Number number) {
		if (number == null) {
			return null;
		}
		return getFormatter().format(number);
	}

	/**
	 * @return number formatted
	 */
	public static String toString(final Number number) {
		return format(number);
	}

	/**
	 * @return the smallest value of type double.
	 */
	public static String getDecimalMinValue() {
		return Double.toString(Double.MIN_VALUE);
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to an <code>Integer</code>, returning a default value if the conversion fails.
	 * </p>
	 * <p>
	 * If the string is <code>null</code>, the default value is returned.
	 * </p>
	 *
	 * <pre>
	 *   NumberUtils.toIntObject(null, 1) = 1
	 *   NumberUtils.toIntObject("", 1)   = 1
	 *   NumberUtils.toIntObject("1", 0)  = 1
	 * </pre>
	 *
	 * @param str : the string to convert, may be null
	 * @param defaultValue : the default value
	 * @return the Integer represented by the string, or the default if conversion fails
	 */
	public static Integer toIntObject(final String str, final Integer defaultValue) {
		try {
			return Integer.parseInt(String.valueOf(str));
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to an <code>Integer</code>, returning a null value if the conversion fails.
	 * </p>
	 *
	 * <pre>
	 *   NumberUtils.toIntObject(null) = null
	 *   NumberUtils.toIntObject("")   = null
	 *   NumberUtils.toIntObject("1")  = 1
	 * </pre>
	 *
	 * @param str the string to convert, may be null
	 * @return the Integer represented by the string, or null if conversion fails
	 */
	public static Integer toIntObject(final String str) {
		return toIntObject(str, null);
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to an <code>int</code>, returning <code>Integer.MIN_VALUE</code> if the conversion fails.
	 * </p>
	 * <p>
	 * If the string is <code>null</code>, <code>Integer.MIN_VALUE</code> is returned.
	 * </p>
	 *
	 * <pre>
	 *   NumberUtils.toInt(null) = Integer.MIN_VALUE
	 *   NumberUtils.toInt("")   = Integer.MIN_VALUE
	 *   NumberUtils.toInt("1")  = 1
	 * </pre>
	 *
	 * @param str the string to convert, may be null
	 * @return the int represented by the string, or <code>Integer.MIN_VALUE</code> if conversion fails
	 */
	public static int toInt(final String str) {
		return toInt(str, Integer.MIN_VALUE);
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to an <code>Long</code>, returning a default value if the conversion fails.
	 * </p>
	 * <p>
	 * If the string is <code>null</code>, the default value is returned.
	 * </p>
	 *
	 * <pre>
	 *   NumberUtils.toLongObject(null, 1) = 1
	 *   NumberUtils.toLongObject("", 1)   = 1
	 *   NumberUtils.toLongObject("1", 0)  = 1
	 * </pre>
	 *
	 * @param str : the string to convert, may be null
	 * @param defaultValue : the default value
	 * @return the Long represented by the string, or the default if conversion fails
	 */
	public static Long toLongObject(final String str, final Long defaultValue) {
		try {
			return Long.parseLong(String.valueOf(str));
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to an <code>Long</code>, returning a null value if the conversion fails.
	 * </p>
	 *
	 * <pre>
	 *   NumberUtils.toLongObject(null) = null
	 *   NumberUtils.toLongObject("")   = null
	 *   NumberUtils.toLongObject("1")  = 1
	 * </pre>
	 *
	 * @param str the string to convert, may be null
	 * @return the Long represented by the string, or null if conversion fails
	 */
	public static Long toLongObject(final String str) {
		return toLongObject(str, null);
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to a <code>long</code>, returning <code>Long.MIN_VALUE</code> if the conversion fails.
	 * </p>
	 * <p>
	 * If the string is <code>null</code>, <code>Long.MIN_VALUE</code> is returned.
	 * </p>
	 *
	 * <pre>
	 *   NumberUtils.toLong(null) = Long.MIN_VALUE
	 *   NumberUtils.toLong("")   = Long.MIN_VALUE
	 *   NumberUtils.toLong("1")  = 1L
	 * </pre>
	 *
	 * @param str the string to convert, may be null
	 * @return the long represented by the string, or <code>Long.MIN_VALUE</code> if conversion fails
	 */
	public static long toLong(final String str) {
		return toLong(str, Long.MIN_VALUE);
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to an <code>Double</code>, returning a default value if the conversion fails.
	 * </p>
	 * <p>
	 * If the string is <code>null</code>, the default value is returned.
	 * </p>
	 *
	 * <pre>
	 *   NumberUtils.toDoubleObject(null, 1) = 1
	 *   NumberUtils.toDoubleObject("", 1)   = 1
	 *   NumberUtils.toDoubleObject("1", 0)  = 1
	 * </pre>
	 *
	 * @param str : the string to convert, may be null
	 * @param defaultValue : the default value
	 * @return the Double represented by the string, or the default if conversion fails
	 */
	public static Double toDoubleObject(final String str, final Double defaultValue) {
		try {
			return Double.parseDouble(String.valueOf(str));
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to an <code>Double</code>, returning a null value if the conversion fails.
	 * </p>
	 *
	 * <pre>
	 *   NumberUtils.toDoubleObject(null) = null
	 *   NumberUtils.toDoubleObject("")   = null
	 *   NumberUtils.toDoubleObject("1")  = 1
	 * </pre>
	 *
	 * @param str the string to convert, may be null
	 * @return the Double represented by the string, or null if conversion fails
	 */
	public static Double toDoubleObject(final String str) {
		return toDoubleObject(str, null);
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to a <code>double</code>, returning <code>Long.MIN_VALUE</code> if the conversion fails.
	 * </p>
	 * <p>
	 * If the string is <code>null</code>, <code>Double.MIN_VALUE</code> is returned.
	 * </p>
	 *
	 * <pre>
	 *   NumberUtils.toDouble(null) = Double.MIN_VALUE
	 *   NumberUtils.toDouble("")   = Double.MIN_VALUE
	 *   NumberUtils.toDouble("1")  = 1L
	 * </pre>
	 *
	 * @param str the string to convert, may be null
	 * @return the double represented by the string, or <code>Double.MIN_VALUE</code> if conversion fails
	 */
	public static double toDouble(final String str) {
		return toDouble(str, Double.MIN_VALUE);
	}

	/**
	 * Manage formatted value.
	 *
	 * @see org.apache.commons.lang.math.NumberUtils#isNumber(String)
	 */
	public static boolean isNumber(final String str) {
		try {
			return StringUtils.isNotBlank(str)
					&& (org.apache.commons.lang.math.NumberUtils.isNumber(str) || getComplexFormat().parse(str) != null);
		} catch (final ParseException e) {
			return false;
		}
	}

	public static Complex parse(final String str) {
		try {
			return getComplexFormat().parse(str);
		} catch (final ParseException e) {
			return null;
		}
	}
}
