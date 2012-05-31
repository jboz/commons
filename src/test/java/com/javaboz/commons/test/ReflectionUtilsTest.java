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

import static org.fest.assertions.Assertions.assertThat;

import javax.annotation.Resource;
import javax.management.DescriptorKey;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.experimental.theories.DataPoint;
import org.junit.rules.ExpectedException;

import com.javaboz.commons.test.ReflectionUtils;


/**
 * Test de la classe {@link ReflectionUtils}.
 * 
 * @author jboz
 */
@SuppressWarnings("unused")
public class ReflectionUtilsTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Deprecated
  private static class AbstractAnnotedClass {

    @Rule
    public final String provider = "PROVIDED";

    @Ignore
    public void testAbstractAnnoted() {
    }
  }

  @DataPoint
  private static class AnnotedClass extends AbstractAnnotedClass {

    @Resource
    public IncompatibleClassChangeError service;

    @DescriptorKey("test")
    public void testAnnoted() {
    }
  }

  @Test
  public void testGetMethodOrClassLevelAnnotation() {
    assertThat(
        ReflectionUtils.getMethodOrClassLevelAnnotation(DescriptorKey.class, AnnotedClass.class.getMethods()[0],
            AnnotedClass.class)).isInstanceOf(DescriptorKey.class);

    assertThat(
        ReflectionUtils.getMethodOrClassLevelAnnotation(Deprecated.class, AnnotedClass.class.getMethods()[0], AnnotedClass.class))
        .isInstanceOf(Deprecated.class);

    assertThat(
        ReflectionUtils.getMethodOrClassLevelAnnotation(DataPoint.class, AnnotedClass.class.getMethods()[0], AnnotedClass.class))
        .isInstanceOf(DataPoint.class);

    assertThat(
        ReflectionUtils.getMethodOrClassLevelAnnotation(Ignore.class, AnnotedClass.class.getMethods()[0], AnnotedClass.class))
        .isNull();

    assertThat(
        ReflectionUtils.getMethodOrClassLevelAnnotation(Resource.class, AnnotedClass.class.getMethods()[0], AnnotedClass.class))
        .isNull();

    assertThat(
        ReflectionUtils.getMethodOrClassLevelAnnotation(IncludeCategory.class, AnnotedClass.class.getMethods()[0], AnnotedClass.class))
        .isNull();
  }

  @Test
  public void testGetClassLevelAnnotation() {
    assertThat(ReflectionUtils.getClassLevelAnnotation(DataPoint.class, AnnotedClass.class)).isInstanceOf(DataPoint.class);
    assertThat(ReflectionUtils.getClassLevelAnnotation(Deprecated.class, AnnotedClass.class)).isInstanceOf(Deprecated.class);
    assertThat(ReflectionUtils.getClassLevelAnnotation(IncludeCategory.class, AnnotedClass.class)).isNull();
  }

  @Test
  public void testGetFieldAnnotatedWith() {
    assertThat(ReflectionUtils.getFieldAnnotatedWith(Object.class, Resource.class)).isNull();
    assertThat(ReflectionUtils.getFieldAnnotatedWith(AnnotedClass.class, Resource.class)).isNotNull();
    assertThat(ReflectionUtils.getFieldAnnotatedWith(AnnotedClass.class, Resource.class).getName()).isEqualTo("service");
    assertThat(ReflectionUtils.getFieldAnnotatedWith(AnnotedClass.class, Rule.class).getName()).isEqualTo("provider");
  }

  @Test
  public void testGetFieldByType() {
    assertThat(ReflectionUtils.getFieldByType(Object.class, IncompatibleClassChangeError.class)).isNull();
    assertThat(ReflectionUtils.getFieldByType(AnnotedClass.class, IncompatibleClassChangeError.class)).isNotNull();
    assertThat(ReflectionUtils.getFieldByType(AnnotedClass.class, IncompatibleClassChangeError.class).getName()).isEqualTo("service");
    assertThat(ReflectionUtils.getFieldByType(AnnotedClass.class, String.class).getName()).isEqualTo("provider");
  }

  @Test
  public void testGetFieldByName() {
    assertThat(ReflectionUtils.getFieldByName(Object.class, "service")).isNull();
    assertThat(ReflectionUtils.getFieldByName(AnnotedClass.class, "service").getName()).isEqualTo("service");
    assertThat(ReflectionUtils.getFieldByName(AnnotedClass.class, "provider").getName()).isEqualTo("provider");
  }

  @Test
  public void testSetFieldValueObjectClassOfTObject() {
    final AnnotedClass annotedClass = new AnnotedClass();
    assertThat(annotedClass.service).isNull();
    ReflectionUtils.setFieldValue(annotedClass, IncompatibleClassChangeError.class, new IncompatibleClassChangeError());
    assertThat(annotedClass.service).isNotNull();
  }

  @Test
  public void testSetFieldValueObjectStringObject() {
    final AnnotedClass annotedClass = new AnnotedClass();
    assertThat(annotedClass.service).isNull();
    ReflectionUtils.setFieldValue(annotedClass, AnnotedClass.class.getFields()[0], new IncompatibleClassChangeError());
    assertThat(annotedClass.service).isNotNull();
  }

  @Test
  public void testSetFieldValueObjectFieldObject() {
    final AnnotedClass annotedClass = new AnnotedClass();
    assertThat(annotedClass.service).isNull();
    ReflectionUtils.setFieldValue(annotedClass, "service", new IncompatibleClassChangeError());
    assertThat(annotedClass.service).isNotNull();
    // pas d'erreur même si le champs est final
    ReflectionUtils.setFieldValue(new AnnotedClass(), "provider", "otherValue");
  }

  @Test
  public void testGetFieldValue() {
    final AnnotedClass annotedClass = new AnnotedClass();
    assertThat(ReflectionUtils.getFieldValue(annotedClass, AnnotedClass.class.getFields()[0])).isNull();
    annotedClass.service = new IncompatibleClassChangeError();
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
    thrown.expectMessage("Error while trying to access field " + Throwable.class.getDeclaredField("detailMessage"));

    ReflectionUtils.getFieldValue(new AnnotedClass(), Throwable.class.getDeclaredField("detailMessage"));
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
}
