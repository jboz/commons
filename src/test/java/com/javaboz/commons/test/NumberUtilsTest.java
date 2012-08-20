package com.javaboz.commons.test;

import static org.fest.assertions.Assertions.assertThat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Test de la classe {@link NumberUtils}.
 *
 * @author Julien Boz
 */
public class NumberUtilsTest {

	@Before
	public void setUp() {
		Locale.setDefault(new Locale("fr", "CH"));
	}

	@Test
	public void testFormat() {
		assertThat(NumberUtils.format(1000526.159)).isEqualTo("1'000'526.16");
		assertThat(new DecimalFormat("#,###.##", new DecimalFormatSymbols(new Locale("fr", "CH"))).format(1000526.159)).isEqualTo(
				"1'000'526.16");
	}

	@Test
	public void testToString() {
		// System.out.println("sep = " + SEP);
		Assert.assertEquals("1'000'015'687", NumberUtils.toString(1000015687L));
		Assert.assertEquals("148'977'687.1", NumberUtils.toString(148977687.1));
		Assert.assertEquals("0.11", NumberUtils.toString(0.110));
		Assert.assertEquals("1.11", NumberUtils.toString(1.111111d));
		Assert.assertEquals("1.15", NumberUtils.toString(1.151111d));
		Assert.assertEquals("1.16", NumberUtils.toString(1.156111d));
		assertThat(NumberUtils.toString(null)).isNull();
	}

	@Test
	public void testGetDecimalMinValue() {
		assertThat(NumberUtils.getDecimalMinValue()).isEqualTo("4.9E-324");
	}

	@Test
	public void testToInt() {
		assertThat(NumberUtils.toInt(null)).isEqualTo(Integer.MIN_VALUE);
		assertThat(NumberUtils.toInt(" ")).isEqualTo(Integer.MIN_VALUE);
		assertThat(NumberUtils.toInt("")).isEqualTo(Integer.MIN_VALUE);
		assertThat(NumberUtils.toInt("123")).isEqualTo(123);
	}

	@Test
	public void testToIntObject() {
		assertThat(NumberUtils.toIntObject(null, null)).isNull();
		assertThat(NumberUtils.toIntObject("asd", null)).isNull();
		assertThat(NumberUtils.toIntObject(null)).isNull();
		assertThat(NumberUtils.toIntObject("asd")).isNull();
		assertThat(NumberUtils.toIntObject(null, 123)).isEqualTo(123);
		assertThat(NumberUtils.toIntObject("", 123)).isEqualTo(123);
		assertThat(NumberUtils.toIntObject("  ", 123)).isEqualTo(123);
		assertThat(NumberUtils.toIntObject("asd", 123)).isEqualTo(123);
		assertThat(NumberUtils.toIntObject("789", null)).isEqualTo(789);
		assertThat(NumberUtils.toIntObject("789", 123)).isEqualTo(789);
		assertThat(NumberUtils.toIntObject("789")).isEqualTo(789);
	}

	@Test
	public void testToLong() {
		assertThat(NumberUtils.toLong(null)).isEqualTo(Long.MIN_VALUE);
		assertThat(NumberUtils.toLong(" ")).isEqualTo(Long.MIN_VALUE);
		assertThat(NumberUtils.toLong("")).isEqualTo(Long.MIN_VALUE);
		assertThat(NumberUtils.toLong("123")).isEqualTo(123);
	}

	@Test
	public void testToLongObject() {
		assertThat(NumberUtils.toLongObject(null, null)).isNull();
		assertThat(NumberUtils.toLongObject("asd", null)).isNull();
		assertThat(NumberUtils.toLongObject(null)).isNull();
		assertThat(NumberUtils.toLongObject("asd")).isNull();
		assertThat(NumberUtils.toLongObject(null, 123L)).isEqualTo(123);
		assertThat(NumberUtils.toLongObject("", 123L)).isEqualTo(123);
		assertThat(NumberUtils.toLongObject("  ", 123L)).isEqualTo(123);
		assertThat(NumberUtils.toLongObject("asd", 123L)).isEqualTo(123);
		assertThat(NumberUtils.toLongObject("789", null)).isEqualTo(789);
		assertThat(NumberUtils.toLongObject("789", 123L)).isEqualTo(789);
		assertThat(NumberUtils.toLongObject("789")).isEqualTo(789);
	}

	@Test
	public void testToDouble() {
		assertThat(NumberUtils.toDouble(null)).isEqualTo(Double.MIN_VALUE);
		assertThat(NumberUtils.toDouble(" ")).isEqualTo(Double.MIN_VALUE);
		assertThat(NumberUtils.toDouble("")).isEqualTo(Double.MIN_VALUE);
		assertThat(NumberUtils.toDouble("123")).isEqualTo(123);
	}

	@Test
	public void testToDoubleObject() {
		assertThat(NumberUtils.toDoubleObject(null, null)).isNull();
		assertThat(NumberUtils.toDoubleObject("asd", null)).isNull();
		assertThat(NumberUtils.toDoubleObject(null)).isNull();
		assertThat(NumberUtils.toDoubleObject("asd")).isNull();
		assertThat(NumberUtils.toDoubleObject(null, 123.51)).isEqualTo(123.51);
		assertThat(NumberUtils.toDoubleObject("", 123.51)).isEqualTo(123.51);
		assertThat(NumberUtils.toDoubleObject("  ", 123.51)).isEqualTo(123.51);
		assertThat(NumberUtils.toDoubleObject("asd", 123.51)).isEqualTo(123.51);
		assertThat(NumberUtils.toDoubleObject("789", null)).isEqualTo(789);
		assertThat(NumberUtils.toDoubleObject("789.98", 123.51)).isEqualTo(789.98);
		assertThat(NumberUtils.toDoubleObject("789.98")).isEqualTo(789.98);
	}

	@Test
	public void testIsNumber() {
		assertThat(NumberUtils.isNumber("3125.15")).isTrue();
		assertThat(NumberUtils.isNumber("3'000'125.15")).isTrue();
		assertThat(NumberUtils.isNumber("3,000,125.15")).isFalse();
		assertThat(NumberUtils.isNumber("3125,15")).isFalse();
		assertThat(NumberUtils.isNumber("3 000 125,15")).isFalse();
		assertThat(NumberUtils.isNumber("asd5")).isFalse();
		assertThat(NumberUtils.isNumber("  ")).isFalse();
		assertThat(NumberUtils.isNumber(null)).isFalse();
	}

}
