package com.javaboz.commons.test;

import static org.fest.assertions.Assertions.assertThat;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.theories.DataPoint;
import org.junit.rules.ExpectedException;

/**
 * Test de la classe {@link ReflectionUtils}.
 *
 * @author Julien Boz
 */
public class ReflectionUtilsTest {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Resource
	private static class AbstractAnnotedClass {

		@DataPoint
		public final String provider = "PROVIDED";

		@Category(Integer.class)
		public void testAbstractAnnoted() {
		}
	}

	@Deprecated
	private static class AnnotedClass extends AbstractAnnotedClass {

		@ClassRule
		public BigDecimal service;

		@Before
		public void testAnnoted() {
		}
	}

	@Test
	public void testGetMethodOrClassLevelAnnotation() {
		assertThat(
				ReflectionUtils.getMethodOrClassLevelAnnotation(Before.class, AnnotedClass.class.getMethods()[0], AnnotedClass.class))
				.isInstanceOf(Before.class);

		assertThat(
				ReflectionUtils.getMethodOrClassLevelAnnotation(Resource.class, AnnotedClass.class.getMethods()[0], AnnotedClass.class))
				.isInstanceOf(Resource.class);

		assertThat(
				ReflectionUtils.getMethodOrClassLevelAnnotation(Deprecated.class, AnnotedClass.class.getMethods()[0], AnnotedClass.class))
				.isInstanceOf(Deprecated.class);

		assertThat(
				ReflectionUtils.getMethodOrClassLevelAnnotation(Category.class, AnnotedClass.class.getMethods()[0], AnnotedClass.class))
				.isNull();

		assertThat(
				ReflectionUtils.getMethodOrClassLevelAnnotation(ClassRule.class, AnnotedClass.class.getMethods()[0], AnnotedClass.class))
				.isNull();

		assertThat(
				ReflectionUtils.getMethodOrClassLevelAnnotation(Test.class, AnnotedClass.class.getMethods()[0], AnnotedClass.class))
				.isNull();
	}

	@Test
	public void testGetClassLevelAnnotation() {
		assertThat(ReflectionUtils.getClassLevelAnnotation(Deprecated.class, AnnotedClass.class)).isInstanceOf(Deprecated.class);
		assertThat(ReflectionUtils.getClassLevelAnnotation(Resource.class, AnnotedClass.class)).isInstanceOf(Resource.class);
		assertThat(ReflectionUtils.getClassLevelAnnotation(Test.class, AnnotedClass.class)).isNull();
	}

	@Test
	public void testGetFieldAnnotatedWith() {
		assertThat(ReflectionUtils.getFieldAnnotatedWith(Object.class, ClassRule.class)).isNull();
		assertThat(ReflectionUtils.getFieldAnnotatedWith(AnnotedClass.class, ClassRule.class)).isNotNull();
		assertThat(ReflectionUtils.getFieldAnnotatedWith(AnnotedClass.class, ClassRule.class).getName()).isEqualTo("service");
		assertThat(ReflectionUtils.getFieldAnnotatedWith(AnnotedClass.class, DataPoint.class).getName()).isEqualTo("provider");
	}

	@Test
	public void testGetFieldByType() {
		assertThat(ReflectionUtils.getFieldByType(Object.class, BigDecimal.class)).isNull();
		assertThat(ReflectionUtils.getFieldByType(AnnotedClass.class, BigDecimal.class)).isNotNull();
		assertThat(ReflectionUtils.getFieldByType(AnnotedClass.class, BigDecimal.class).getName()).isEqualTo("service");
		assertThat(ReflectionUtils.getFieldByType(AnnotedClass.class, String.class).getName()).isEqualTo("provider");
	}

	@Test
	public void testGetFieldByName() {
		assertThat(ReflectionUtils.getFieldByName(Object.class, "service")).isNull();
		assertThat(ReflectionUtils.getFieldByName(AnnotedClass.class, "service").getName()).isEqualTo("service");
		assertThat(ReflectionUtils.getFieldByName(AnnotedClass.class, "provider").getName()).isEqualTo("provider");
	}

	@Test
	public void testSetFieldValue_class_ok() {
		final AnnotedClass annotedClass = new AnnotedClass();
		assertThat(annotedClass.service).isNull();
		ReflectionUtils.setFieldValue(annotedClass, BigDecimal.class, new BigDecimal("1"));
		assertThat(annotedClass.service).isNotNull();
	}

	@Test
	public void testSetFieldValue_field_ok() {
		final AnnotedClass annotedClass = new AnnotedClass();
		assertThat(annotedClass.service).isNull();
		ReflectionUtils.setFieldValue(annotedClass, AnnotedClass.class.getFields()[0], new BigDecimal("1"));
		assertThat(annotedClass.service).isNotNull();
	}

	@Test
	public void testSetFieldValue_string_ok() {
		final AnnotedClass annotedClass = new AnnotedClass();
		assertThat(annotedClass.service).isNull();
		ReflectionUtils.setFieldValue(annotedClass, "service", new BigDecimal("1"));
		assertThat(annotedClass.service).isNotNull();
		// pas d'erreur même si le champs est final
		ReflectionUtils.setFieldValue(new AnnotedClass(), "provider", "otherValue");
	}

	@Test
	public void testSetFieldValue_string_objNull() {
		ReflectionUtils.setFieldValue(null, "service", new BigDecimal("1"));
		// pas d'erreur même si l'objet est null
	}

	@Test
	public void testGetFieldValue() {
		final AnnotedClass annotedClass = new AnnotedClass();
		assertThat(ReflectionUtils.getFieldValue(annotedClass, AnnotedClass.class.getFields()[0])).isNull();
		annotedClass.service = new BigDecimal("1");
		assertThat(ReflectionUtils.getFieldValue(annotedClass, AnnotedClass.class.getFields()[0])).isNotNull();
	}

	@Test
	public void testGetFieldValue_NullPointerException() {
		thrown.expect(NullPointerException.class);

		ReflectionUtils.getFieldValue(null, AbstractAnnotedClass.class.getFields()[0]);
	}

	@Test
	public void testGetFieldValue_IllegalArgumentException() throws SecurityException, NoSuchFieldException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Error while trying to access field " + String.class.getDeclaredField("hash"));

		ReflectionUtils.getFieldValue(new AnnotedClass(), String.class.getDeclaredField("hash"));
	}

	@Test
	public void testSetFieldValue_NullPointerException() {
		thrown.expect(NullPointerException.class);

		ReflectionUtils.setFieldValue(null, AbstractAnnotedClass.class.getFields()[0], "otherValue");
	}

	@Test
	public void testSetFieldValue_IllegalArgumentException() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Unable to assign the value to field: provider. "
				+ "Ensure that this field is of the correct type. Value: 1");

		ReflectionUtils.setFieldValue(new AnnotedClass(), AbstractAnnotedClass.class.getFields()[0], Long.valueOf(1));
	}

	@Test
	public void testSetFieldValue_nullObject() {
		ReflectionUtils.setFieldValue(null, (Field) null, "otherValue");
		// pas d'erreur même si le field est null
	}
}
