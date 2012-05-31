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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author jboz
 */
public final class ReflectionUtils {
  private ReflectionUtils() {
  }

  public static <T extends Annotation> T getMethodOrClassLevelAnnotation(final Class<T> annotationClass, final Method method,
      final Class<?> clazz) {
    final T annotation = method.getAnnotation(annotationClass);
    if (annotation != null) {
      return annotation;
    }
    return getClassLevelAnnotation(annotationClass, clazz);
  }

  public static <T extends Annotation> T getClassLevelAnnotation(final Class<T> annotationClass, final Class<?> clazz) {
    if (Object.class.equals(clazz)) {
      return null;
    }

    final T annotation = clazz.getAnnotation(annotationClass);
    if (annotation != null) {
      return annotation;
    }
    return getClassLevelAnnotation(annotationClass, clazz.getSuperclass());
  }

  /**
   * Returns the given class's declared field that are marked with the given annotation
   * 
   * @param clazz The class, not null
   * @param annotation The annotation, not null
   * @return The first field annotated with the given annotation, empty list if none found
   */
  public static <T extends Annotation> Field getFieldAnnotatedWith(final Class<? extends Object> clazz, final Class<T> annotation) {
    if (Object.class.equals(clazz)) {
      return null;
    }
    final Field[] fields = clazz.getDeclaredFields();
    for (final Field field : fields) {
      if (field.getAnnotation(annotation) != null) {
        return field;
      }
    }
    return getFieldAnnotatedWith(clazz.getSuperclass(), annotation);
  }

  /**
   * Search field by type in class and super class.
   */
  public static <T> Field getFieldByType(final Class<? extends Object> clazz, final Class<T> type) {
    if (Object.class.equals(clazz)) {
      return null;
    }
    final Field[] fields = clazz.getDeclaredFields();
    for (final Field field : fields) {
      if (type.equals(field.getType())) {
        return field;
      }
    }
    return getFieldByType(clazz.getSuperclass(), type);
  }

  /**
   * Search field by name in class and super class.
   */
  public static <T> Field getFieldByName(final Class<? extends Object> clazz, final String name) {
    if (Object.class.equals(clazz)) {
      return null;
    }
    final Field[] fields = clazz.getDeclaredFields();
    for (final Field field : fields) {
      if (field.getName().equals(name)) {
        return field;
      }
    }
    return getFieldByName(clazz.getSuperclass(), name);
  }

  /**
   * Sets the given value to the given field on the given object
   * 
   * @param object The object containing the field, not null
   * @param fieldType The field type, not null
   * @param value The value for the given field in the given object
   * @throws UnitilsException if the field could not be accessed
   */
  public static <T> void setFieldValue(final Object object, final Class<T> fieldType, final Object value) {
    setFieldValue(object, getFieldByType(object.getClass(), fieldType), value);
  }

  /**
   * Sets the given value to the given field on the given object
   * 
   * @param object The object containing the field, not null
   * @param fieldName The field name, not null
   * @param value The value for the given field in the given object
   * @throws UnitilsException if the field could not be accessed
   */
  public static void setFieldValue(final Object object, final String fieldName, final Object value) {
    setFieldValue(object, getFieldByName(object.getClass(), fieldName), value);
  }

  /**
   * Sets the given value to the given field on the given object
   * 
   * @param object The object containing the field, not null
   * @param field The field, not null
   * @param value The value for the given field in the given object
   * @throws UnitilsException if the field could not be accessed
   */
  public static void setFieldValue(final Object object, final Field field, final Object value) {
    try {
      field.setAccessible(true);
      field.set(object, value);

    } catch (final IllegalArgumentException e) {
      throw new IllegalArgumentException("Unable to assign the value to field: " + field.getName()
          + ". Ensure that this field is of the correct type. Value: " + value, e);

    } catch (final IllegalAccessException e) {
      // Cannot occur, since field.accessible has been set to true
      throw new IllegalArgumentException("Error while trying to access field " + field, e);
    }
  }

  /**
   * Returns the value of the given field (may be private) in the given object
   * 
   * @param object The object containing the field, null for static fields
   * @param field The field, not null
   * @return The value of the given field in the given object
   * @throws UnitilsException if the field could not be accessed
   */
  @SuppressWarnings("unchecked")
  public static <T> T getFieldValue(final Object object, final Field field) {
    try {
      field.setAccessible(true);
      return (T) field.get(object);

    } catch (final IllegalArgumentException e) {
      throw new IllegalArgumentException("Error while trying to access field " + field, e);

    } catch (final IllegalAccessException e) {
      throw new IllegalArgumentException("Error while trying to access field " + field, e);
    }
  }
}
