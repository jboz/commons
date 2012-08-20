package com.javaboz.commons.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Julien Boz
 */
public class ReflectionUtils {

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
			if (field.getName().equalsIgnoreCase(name)) {
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
		if (object != null) {
			setFieldValue(object, getFieldByName(object.getClass(), fieldName), value);
		}
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
		if (field == null) {
			return;
		}
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
			// Cannot occur, since field.accessible has been set to true
			throw new IllegalArgumentException("Error while trying to access field " + field, e);
		} catch (final IllegalAccessException e) {
			// Cannot occur, since field.accessible has been set to true
			throw new IllegalArgumentException("Error while trying to access field " + field, e);
		}
	}

	/**
	 * Returns the value of the given field (may be private and/or static) for the given object instance (may be null for static
	 * field).
	 */
	public static <T, O> O getFieldValue(final Class<T> clazz, final Object object, final String fieldName) {
		return getFieldValue(object, getFieldByName(clazz, fieldName));
	}

	/**
	 * Returns the value of the given field (may be private) for the given object instance.
	 */
	public static <T, O> O getFieldValue(final Object object, final String fieldName) {
		return getFieldValue(object.getClass(), object, fieldName);
	}

	/**
	 * Returns the value of the given static field (may be private).
	 */
	public static <T, O> O getFieldValue(final Class<T> clazz, final String fieldName) {
		return getFieldValue(clazz, null, fieldName);
	}

	/**
	 * Invoke method (may be private).
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(final Object object, final String methodName, final Object... params) {
		if (object == null) {
			return null;
		}

		// Go and find the private method...
		final Method methods[] = object.getClass().getDeclaredMethods();
		for (final Method method : methods) {
			if (method.getName().equals(methodName)) {
				try {
					method.setAccessible(true);
					return (T) method.invoke(object, params);
				} catch (final IllegalAccessException e) {
					// Cannot occur, since methods.accessible has been set to true
					throw new IllegalArgumentException("Error while trying to access method " + methodName, e);
				} catch (final InvocationTargetException e) {
					// Cannot occur, since methods.accessible has been set to true
					throw new IllegalArgumentException("Error while trying to access method " + methodName, e);
				}
			}
		}
		return null;
	}
}
