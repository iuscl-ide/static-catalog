/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.annotations.helpers;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.beans.*;
import com.univocity.parsers.common.input.*;
import com.univocity.parsers.conversions.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.*;
import java.text.*;
import java.util.*;

/**
 * Helper class to process fields annotated with {@link Parsed}
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class AnnotationHelper {

	private static AnnotatedElement lastProcessedElement;
	private static Class<? extends Annotation> lastProcessedAnnotationType;
	private static Annotation lastAnnotationFound;

	private AnnotationHelper() {

	}

	/**
	 * Converts the special "null" strings that might be provided by {@link Parsed#defaultNullRead() and  Parsed#defaultNullWrite()}
	 *
	 * @param defaultValue The string returned by {@link Parsed#defaultNullRead() and  Parsed#defaultNullWrite()}
	 *
	 * @return the default value if it is not the String literal "null" or "'null'".
	 * <p> If "null" was provided, then null will be returned.
	 * <p> If "'null'" was provided, then "null" will be returned.
	 */
	private static String getNullValue(String defaultValue) {
		if ("null".equals(defaultValue)) {
			return null;
		}
		if ("'null'".equals(defaultValue)) {
			return "null";
		}

		return defaultValue;
	}

	private static String getNullWriteValue(AnnotatedElement target, Parsed parsed) {
		if (parsed == null) {
			return null;
		}
		return getNullValue(AnnotationRegistry.getValue(target, parsed, "defaultNullWrite", parsed.defaultNullWrite()));
	}

	private static String getNullReadValue(AnnotatedElement target, Parsed parsed) {
		if (parsed == null) {
			return null;
		}
		return getNullValue(AnnotationRegistry.getValue(target, parsed, "defaultNullRead", parsed.defaultNullRead()));
	}

	/**
	 * Identifies the proper conversion for a given Field and an annotation from the package {@link com.univocity.parsers.annotations}
	 *
	 * @param target     The field or method to have conversions applied to
	 * @param annotation the annotation from {@link com.univocity.parsers.annotations} that identifies a {@link Conversion} instance.
	 *
	 * @return The {@link Conversion} that should be applied to the field
	 */
	@SuppressWarnings("rawtypes")
	public static Conversion getConversion(AnnotatedElement target, Annotation annotation) {
		return getConversion(getType(target), target, annotation);
	}

	/**
	 * Identifies the proper conversion for a given type and an annotation from the package {@link com.univocity.parsers.annotations}
	 *
	 * @param classType  the type to have conversions applied to
	 * @param annotation the annotation from {@link com.univocity.parsers.annotations} that identifies a {@link Conversion} instance.
	 *
	 * @return The {@link Conversion} that should be applied to the type
	 */
	@SuppressWarnings("rawtypes")
	public static Conversion getConversion(Class classType, Annotation annotation) {
		return getConversion(classType, null, annotation);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Conversion getConversion(Class fieldType, AnnotatedElement target, Annotation annotation) {
		try {
			Parsed parsed = target == null ? null : findAnnotation(target, Parsed.class);
			Class annType = annotation.annotationType();

			String nullRead = getNullReadValue(target, parsed);
			String nullWrite = getNullWriteValue(target, parsed);

			if (annType == NullString.class) {
				NullString nullString = (NullString) annotation;
				String[] nulls = AnnotationRegistry.getValue(target, nullString, "nulls", nullString.nulls());
				return Conversions.toNull(nulls);
			} else if (annType == Validate.class) {
				Validate validate = (Validate) annotation;

				boolean nullable = AnnotationRegistry.getValue(target, validate, "nullable", validate.nullable());
				boolean allowBlanks = AnnotationRegistry.getValue(target, validate, "allowBlanks", validate.allowBlanks());
				String[] oneOf = AnnotationRegistry.getValue(target, validate, "oneOf", validate.oneOf());
				String[] noneOf = AnnotationRegistry.getValue(target, validate, "noneOf", validate.noneOf());
				String matches = AnnotationRegistry.getValue(target, validate, "matches", validate.matches());

				Class[] validators = AnnotationRegistry.getValue(target, validate, "validators", validate.validators());
				return new ValidatedConversion(nullable, allowBlanks, oneOf, noneOf, matches, validators);
			} else if (annType == EnumOptions.class) {
				if (!fieldType.isEnum()) {
					if (target == null) {
						throw new IllegalStateException("Invalid " + EnumOptions.class.getName() + " instance for converting class " + fieldType.getName() + ". Not an enum type.");
					} else {
						throw new IllegalStateException("Invalid " + EnumOptions.class.getName() + " annotation on " + describeElement(target) + ". Attribute must be an enum type.");
					}
				}
				EnumOptions enumOptions = ((EnumOptions) annotation);
				String customElement = AnnotationRegistry.getValue(target, enumOptions, "customElement", enumOptions.customElement());
				String element = customElement.trim();
				if (element.isEmpty()) {
					element = null;
				}

				Enum nullReadValue = nullRead == null ? null : Enum.valueOf(fieldType, nullRead);
				EnumSelector[] selectors = AnnotationRegistry.getValue(target, enumOptions, "selectors", enumOptions.selectors());
				return new EnumConversion(fieldType, nullReadValue, nullWrite, element, selectors);
			} else if (annType == Trim.class) {
				Trim trim = ((Trim) annotation);
				int length = AnnotationRegistry.getValue(target, trim, "length", trim.length());
				if (length == -1) {
					return Conversions.trim();
				} else {
					return Conversions.trim(length);
				}
			} else if (annType == LowerCase.class) {
				return Conversions.toLowerCase();
			} else if (annType == UpperCase.class) {
				return Conversions.toUpperCase();
			} else if (annType == Replace.class) {
				Replace replace = ((Replace) annotation);
				String expression = AnnotationRegistry.getValue(target, replace, "expression", replace.expression());
				String replacement = AnnotationRegistry.getValue(target, replace, "replacement", replace.replacement());
				return Conversions.replace(expression, replacement);
			} else if (annType == BooleanString.class) {
				if (fieldType != boolean.class && fieldType != Boolean.class) {
					if (target == null) {
						throw new DataProcessingException("Invalid  usage of " + BooleanString.class.getName() + ". Got type " + fieldType.getName() + " instead of boolean.");
					} else {
						throw new DataProcessingException("Invalid annotation: " + describeElement(target) + " has type " + fieldType.getName() + " instead of boolean.");
					}
				}
				BooleanString boolString = ((BooleanString) annotation);
				String[] falseStrings = AnnotationRegistry.getValue(target, boolString, "falseStrings", boolString.falseStrings());
				String[] trueStrings = AnnotationRegistry.getValue(target, boolString, "trueStrings", boolString.trueStrings());
				Boolean valueForNull = nullRead == null ? null : BooleanConversion.getBoolean(nullRead, trueStrings, falseStrings);

				if (valueForNull == null && fieldType == boolean.class) {
					valueForNull = Boolean.FALSE;
				}

				return Conversions.toBoolean(valueForNull, nullWrite, trueStrings, falseStrings);
			} else if (annType == Format.class) {
				Format format = ((Format) annotation);
				String[] formats = AnnotationRegistry.getValue(target, format, "formats", format.formats());
				String[] options = AnnotationRegistry.getValue(target, format, "options", format.options());

				Locale locale = extractLocale(options);
				TimeZone timezone = extractTimeZone(options);

				Conversion conversion = null;

				if (fieldType == BigDecimal.class) {
					BigDecimal defaultForNull = nullRead == null ? null : new BigDecimal(nullRead);
					conversion = Conversions.formatToBigDecimal(defaultForNull, nullWrite, formats);
				} else if (Number.class.isAssignableFrom(fieldType) || (fieldType.isPrimitive()) && fieldType != boolean.class && fieldType != char.class) {
					conversion = Conversions.formatToNumber(formats);
					((NumericConversion) conversion).setNumberType(fieldType);
				} else {
					Date dateIfNull = null;
					if (nullRead != null) {
						if ("now".equalsIgnoreCase(nullRead)) {
							dateIfNull = new Date();
						} else {
							if (formats.length == 0) {
								throw new DataProcessingException("No format defined");
							}
							SimpleDateFormat sdf = new SimpleDateFormat(formats[0], locale);
							sdf.setTimeZone(timezone);
							dateIfNull = sdf.parse(nullRead);
						}
					}

					if (Date.class == fieldType) {
						conversion = Conversions.toDate(timezone, locale, dateIfNull, nullWrite, formats);
					} else if (Calendar.class == fieldType) {
						Calendar calendarIfNull = null;
						if (dateIfNull != null) {
							calendarIfNull = Calendar.getInstance();
							calendarIfNull.setTime(dateIfNull);
							calendarIfNull.setTimeZone(timezone);
						}
						conversion = Conversions.toCalendar(timezone, locale, calendarIfNull, nullWrite, formats);
					}
				}

				if (conversion != null) {
					if (options.length > 0) {
						//noinspection ConstantConditions
						if (conversion instanceof FormattedConversion) {
							Object[] formatters = ((FormattedConversion) conversion).getFormatterObjects();
							for (Object formatter : formatters) {
								applyFormatSettings(formatter, options);
							}
						} else {
							throw new DataProcessingException("Options '" + Arrays.toString(options) + "' not supported by conversion of type '" + conversion.getClass() + "'. It must implement " + FormattedConversion.class);
						}
					}
					return conversion;
				}

			} else if (annType == Convert.class) {
				Convert convert = ((Convert) annotation);
				String[] args = AnnotationRegistry.getValue(target, convert, "args", convert.args());
				Class conversionClass = AnnotationRegistry.getValue(target, convert, "conversionClass", convert.conversionClass());
				return (Conversion) newInstance(Conversion.class, conversionClass, args);
			}

			if (fieldType == String.class && (nullRead != null || nullWrite != null)) {
				return new ToStringConversion(nullRead, nullWrite);
			}

			return null;
		} catch (DataProcessingException ex) {
			throw ex;
		} catch (Throwable ex) {
			if (target == null) {
				throw new DataProcessingException("Unexpected error identifying conversions to apply over type " + fieldType, ex);
			} else {
				throw new DataProcessingException("Unexpected error identifying conversions to apply over " + describeElement(target), ex);
			}
		}
	}

	private static String extractOption(String[] options, String key) {
		for (int i = 0; i < options.length; i++) {
			if (options[i] != null && options[i].trim().toLowerCase().startsWith(key)) {
				String out = options[i].split("=")[1].trim();
				options[i] = null;
				return out;
			}
		}
		return null;
	}

	private static TimeZone extractTimeZone(String[] options) {
		String code = extractOption(options, "timezone=");
		if (code != null) {
			return TimeZone.getTimeZone(code);
		}
		return TimeZone.getDefault();
	}

	private static Locale extractLocale(String[] options) {
		String locale = extractOption(options, "locale=");
		if (locale != null) {
			String languageCode;
			String countryCode;
			String variant;

			CharAppender appender = new DefaultCharAppender(100, "", 0);
			int j = 0;
			char ch;
			for (; j < locale.length() && Character.isLetterOrDigit((ch = locale.charAt(j))); j++, appender.append(ch))
				;
			languageCode = appender.getAndReset();
			for (++j; j < locale.length() && Character.isLetterOrDigit((ch = locale.charAt(j))); j++, appender.append(ch))
				;
			countryCode = appender.getAndReset();
			for (++j; j < locale.length() && Character.isLetterOrDigit((ch = locale.charAt(j))); j++, appender.append(ch))
				;
			variant = appender.getAndReset();

			return new Locale(languageCode, countryCode, variant);
		}
		return Locale.getDefault();
	}

	public static <T> T newInstance(Class parent, Class<T> type, String[] args) {
		if (!parent.isAssignableFrom(type)) {
			throw new DataProcessingException("Not a valid " + parent.getSimpleName() + " class: '" + type.getSimpleName() + "' (" + type.getName() + ')');
		}
		try {
			Constructor constructor = type.getConstructor(String[].class);
			return (T) constructor.newInstance((Object) args);
		} catch (NoSuchMethodException e) {
			if (args.length == 0) {
				try {
					return type.newInstance();
				} catch (Exception ex) {
					throw new DataProcessingException("Unexpected error instantiating custom " + parent.getSimpleName() + " class '" + type.getSimpleName() + "' (" + type.getName() + ')', e);
				}
			}
			throw new DataProcessingException("Could not find a public constructor with a String[] parameter in custom " + parent.getSimpleName() + " class '" + type.getSimpleName() + "' (" + type.getName() + ')', e);
		} catch (Exception e) {
			throw new DataProcessingException("Unexpected error instantiating custom " + parent.getSimpleName() + " class '" + type.getSimpleName() + "' (" + type.getName() + ')', e);
		}
	}

	/**
	 * Identifies the proper conversion for a given type
	 *
	 * @param fieldType The type of field to have conversions applied to.
	 * @param target    the annotated attribute of method that has the annotation
	 * @param parsed    the {@link Parsed} annotation from {@link com.univocity.parsers.annotations}.
	 *
	 * @return The {@link Conversion} that should be applied to the field type
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Conversion getDefaultConversion(Class fieldType, AnnotatedElement target, Parsed parsed) {
		String nullRead = getNullReadValue(target, parsed);
		Object valueIfStringIsNull = null;

		ObjectConversion conversion = null;
		if (fieldType == Boolean.class || fieldType == boolean.class) {
			conversion = Conversions.toBoolean();
			valueIfStringIsNull = nullRead == null ? null : Boolean.valueOf(nullRead);
		} else if (fieldType == Character.class || fieldType == char.class) {
			conversion = Conversions.toChar();
			if (nullRead != null && nullRead.length() > 1) {
				throw new DataProcessingException("Invalid default value for character '" + nullRead + "'. It should contain one character only.");
			}
			valueIfStringIsNull = nullRead == null ? null : nullRead.charAt(0);
		} else if (fieldType == Byte.class || fieldType == byte.class) {
			conversion = Conversions.toByte();
			valueIfStringIsNull = nullRead == null ? null : Byte.valueOf(nullRead);
		} else if (fieldType == Short.class || fieldType == short.class) {
			conversion = Conversions.toShort();
			valueIfStringIsNull = nullRead == null ? null : Short.valueOf(nullRead);
		} else if (fieldType == Integer.class || fieldType == int.class) {
			conversion = Conversions.toInteger();
			valueIfStringIsNull = nullRead == null ? null : Integer.valueOf(nullRead);
		} else if (fieldType == Long.class || fieldType == long.class) {
			conversion = Conversions.toLong();
			valueIfStringIsNull = nullRead == null ? null : Long.valueOf(nullRead);
		} else if (fieldType == Float.class || fieldType == float.class) {
			conversion = Conversions.toFloat();
			valueIfStringIsNull = nullRead == null ? null : Float.valueOf(nullRead);
		} else if (fieldType == Double.class || fieldType == double.class) {
			conversion = Conversions.toDouble();
			valueIfStringIsNull = nullRead == null ? null : Double.valueOf(nullRead);
		} else if (fieldType == BigInteger.class) {
			conversion = Conversions.toBigInteger();
			valueIfStringIsNull = nullRead == null ? null : new BigInteger(nullRead);
		} else if (fieldType == BigDecimal.class) {
			conversion = Conversions.toBigDecimal();
			valueIfStringIsNull = nullRead == null ? null : new BigDecimal(nullRead);
		} else if (Enum.class.isAssignableFrom(fieldType)) {
			conversion = Conversions.toEnum(fieldType);
		}

		if (conversion != null) {
			conversion.setValueIfStringIsNull(valueIfStringIsNull);
			conversion.setValueIfObjectIsNull(getNullWriteValue(target, parsed));
		}

		return conversion;
	}

	/**
	 * Returns the default {@link Conversion} that should be applied to the field based on its type.
	 *
	 * @param target The field or method whose values must be converted from a given parsed String.
	 *
	 * @return The default {@link Conversion} applied to the given field.
	 */
	@SuppressWarnings("rawtypes")
	public static Conversion getDefaultConversion(AnnotatedElement target) {
		Parsed parsed = findAnnotation(target, Parsed.class);
		return getDefaultConversion(getType(target), target, parsed);
	}

	/**
	 * Applied the configuration of a formatter object ({@link SimpleDateFormat}, {@link NumberFormat} and others).
	 *
	 * @param formatter           the formatter instance
	 * @param propertiesAndValues a sequence of key-value pairs, where the key is a property of the formatter
	 *                            object to be set to the following value via reflection
	 */
	public static void applyFormatSettings(Object formatter, String[] propertiesAndValues) {
		if (propertiesAndValues.length == 0) {
			return;
		}

		Map<String, String> values = new HashMap<String, String>();
		for (String setting : propertiesAndValues) {
			if (setting == null) {
				continue;
			}
			String[] pair = setting.split("=");
			if (pair.length != 2) {
				throw new DataProcessingException("Illegal format setting '" + setting + "' among: " + Arrays.toString(propertiesAndValues));
			}

			values.put(pair[0], pair[1]);
		}

		try {
			for (PropertyWrapper property : BeanHelper.getPropertyDescriptors(formatter.getClass())) {
				String name = property.getName();
				String value = values.remove(name);
				if (value != null) {
					invokeSetter(formatter, property, value);
				}

				if ("decimalFormatSymbols".equals(property.getName())) {
					DecimalFormatSymbols modifiedDecimalSymbols = new DecimalFormatSymbols();
					boolean modified = false;
					try {
						for (PropertyWrapper prop : BeanHelper.getPropertyDescriptors(modifiedDecimalSymbols.getClass())) {
							value = values.remove(prop.getName());
							if (value != null) {
								invokeSetter(modifiedDecimalSymbols, prop, value);
								modified = true;
							}
						}

						if (modified) {
							Method writeMethod = property.getWriteMethod();
							if (writeMethod != null) {
								writeMethod.invoke(formatter, modifiedDecimalSymbols);
							} else {
								throw new IllegalStateException("No write method defined for property " + property.getName());
							}
						}
					} catch (Throwable ex) {
						throw new DataProcessingException("Error trying to configure decimal symbols of formatter '" + formatter.getClass() + '.', ex);
					}
				}
			}
		} catch (Exception e) {
			//ignore and proceed
		}

		if (!values.isEmpty()) {
			throw new DataProcessingException("Cannot find properties in formatter of type '" + formatter.getClass() + "': " + values);
		}
	}

	private static void invokeSetter(Object formatter, PropertyWrapper property, final String value) {
		Method writeMethod = property.getWriteMethod();
		if (writeMethod == null) {
			DataProcessingException exception = new DataProcessingException("Cannot set property '" + property.getName() + "' of formatter '" + formatter.getClass() + "' to '{value}'. No setter defined");
			exception.setValue(value);
			throw exception;
		}
		Class<?> parameterType = writeMethod.getParameterTypes()[0];
		Object parameterValue = null;
		if (parameterType == String.class) {
			parameterValue = value;
		} else if (parameterType == Integer.class || parameterType == int.class) {
			parameterValue = Integer.parseInt(value);
		} else if (parameterType == Character.class || parameterType == char.class) {
			parameterValue = value.charAt(0);
		} else if (parameterType == Currency.class) {
			parameterValue = Currency.getInstance(value);
		} else if (parameterType == Boolean.class || parameterType == boolean.class) {
			parameterValue = Boolean.valueOf(value);
		} else if (parameterType == TimeZone.class) {
			parameterValue = TimeZone.getTimeZone(value);
		} else if (parameterType == DateFormatSymbols.class) {
			parameterValue = DateFormatSymbols.getInstance(new Locale(value));
		}
		if (parameterValue == null) {
			DataProcessingException exception = new DataProcessingException("Cannot set property '" + property.getName() + "' of formatter '" + formatter.getClass() + ". Cannot convert '{value}' to instance of " + parameterType);
			exception.setValue(value);
			throw exception;
		}

		try {
			writeMethod.invoke(formatter, parameterValue);
		} catch (Throwable e) {
			DataProcessingException exception = new DataProcessingException("Error setting property '" + property.getName() + "' of formatter '" + formatter.getClass() + ", with '{parameterValue}' (converted from '{value}')", e);
			exception.setValue("parameterValue", parameterValue);
			exception.setValue(value);
			throw exception;
		}
	}

	private static boolean allFieldsIndexOrNameBased(boolean searchName, Class<?> beanClass, MethodFilter filter) {
		boolean hasAnnotation = false;

		for (TransformedHeader header : getFieldSequence(beanClass, true, null, filter)) {
			if (header == null || header.getTarget() == null) {
				continue;
			}
			AnnotatedElement element = header.getTarget();
			if (element instanceof Method && filter.reject((Method) element)) {
				continue;
			}

			Parsed annotation = findAnnotation(element, Parsed.class);
			if (annotation != null) {
				hasAnnotation = true;

				int index = AnnotationRegistry.getValue(element, annotation, "index", annotation.index());

				if ((index != -1 && searchName) || (index == -1 && !searchName)) {
					return false;
				}
			}
		}
		return hasAnnotation;
	}

	/**
	 * Runs through all annotations of a given class to identify whether all annotated fields and methods
	 * (with the {@link Parsed} annotation) are mapped to a column by index.
	 *
	 * @param beanClass a class whose {@link Parsed} annotations will be processed.
	 *
	 * @return {@code true} if every field and method annotated with {@link Parsed} in the given class maps to an index, otherwise {@code false}.
	 */
	public static boolean allFieldsIndexBasedForParsing(Class<?> beanClass) {
		return allFieldsIndexOrNameBased(false, beanClass, MethodFilter.ONLY_SETTERS);
	}

	/**
	 * Runs through all annotations of a given class to identify whether all annotated fields and methods
	 * (with the {@link Parsed} annotation) are mapped to a column by name.
	 *
	 * @param beanClass a class whose {@link Parsed} annotations will be processed.
	 *
	 * @return {@code true} if every field and method annotated with {@link Parsed} in the given class maps to a header name, otherwise {@code false}.
	 */
	public static boolean allFieldsNameBasedForParsing(Class<?> beanClass) {
		return allFieldsIndexOrNameBased(true, beanClass, MethodFilter.ONLY_SETTERS);
	}


	/**
	 * Runs through all annotations of a given class to identify whether all annotated fields and methods
	 * (with the {@link Parsed} annotation) are mapped to a column by index.
	 *
	 * @param beanClass a class whose {@link Parsed} annotations will be processed.
	 *
	 * @return {@code true} if every field and method annotated with {@link Parsed} in the given class maps to an index, otherwise {@code false}.
	 */
	public static boolean allFieldsIndexBasedForWriting(Class<?> beanClass) {
		return allFieldsIndexOrNameBased(false, beanClass, MethodFilter.ONLY_GETTERS);
	}

	/**
	 * Runs through all annotations of a given class to identify whether all annotated fields and methods
	 * (with the {@link Parsed} annotation) are mapped to a column by name.
	 *
	 * @param beanClass a class whose {@link Parsed} annotations will be processed.
	 *
	 * @return {@code true} if every field and method annotated with {@link Parsed} in the given class maps to a header name, otherwise {@code false}.
	 */
	public static boolean allFieldsNameBasedForWriting(Class<?> beanClass) {
		return allFieldsIndexOrNameBased(true, beanClass, MethodFilter.ONLY_GETTERS);
	}

	/**
	 * Runs through all {@link Parsed} annotations of a given class to identify all indexes associated with its fields
	 *
	 * @param beanClass a class whose {@link Parsed} annotations will be processed.
	 * @param filter    filter to apply over annotated methods when the class is being used for reading data from beans (to write values to an output)
	 *                  or when writing values into beans (while parsing). It is used to choose either a "get" or a "set"
	 *                  method annotated with {@link Parsed}, when both methods target the same field.
	 *
	 * @return an array of column indexes used by the given class
	 */
	public static Integer[] getSelectedIndexes(Class<?> beanClass, MethodFilter filter) {
		List<Integer> indexes = new ArrayList<Integer>();
		for (TransformedHeader header : getFieldSequence(beanClass, true, null, filter)) {
			if (header == null) {
				continue;
			}
			int index = header.getHeaderIndex();

			if (index != -1) {
				if (filter == MethodFilter.ONLY_GETTERS && indexes.contains(index)) { //allows the same column to be mapped to multiple fields when parsing, but not when writing.
					throw new IllegalArgumentException("Duplicate field index '" + index + "' found in attribute '" + header.getTargetName() + "' of class " + beanClass.getName());
				}
				indexes.add(index);
			}
		}
		return indexes.toArray(new Integer[indexes.size()]);
	}

	/**
	 * Runs through all {@link Parsed} annotations of a given class to identify all header names associated with its fields
	 *
	 * @param beanClass a class whose {@link Parsed} annotations will be processed.
	 * @param filter    a filter to exclude annotated methods that won't be used for parsing or writing
	 *
	 * @return an array of column names used by the given class
	 */
	public static String[] deriveHeaderNamesFromFields(Class<?> beanClass, MethodFilter filter) {
		List<TransformedHeader> sequence = getFieldSequence(beanClass, true, null, filter);
		List<String> out = new ArrayList<String>(sequence.size());

		for (TransformedHeader field : sequence) {
			if (field == null) {
				return ArgumentUtils.EMPTY_STRING_ARRAY;  // some field has an index that goes beyond list of header names, can't derive.
			}
			out.add(field.getHeaderName());
		}
		return out.toArray(new String[out.size()]);
	}

	/**
	 * Searches for a given annotation in the hierarchy of a class
	 *
	 * @param beanClass the class whose hierarchy will be searched
	 * @param annotation the annotation to locate
	 * @param <T> the type of the annotation to return
	 * @return the annotation of the given class or its most immediate parent, or {@code null} if not found.
	 */
	public static <T extends Annotation> T findAnnotationInClass(Class<?> beanClass, Class<T> annotation) {
		T out;

		Class<?> parent = beanClass;
		do {
			out = parent.getAnnotation(annotation);

			if (out != null) {
				return out;
			} else {
				for (Class<?> iface : parent.getInterfaces()) {
					out = findAnnotationInClass(iface, annotation);
					if (out != null) {
						return out;
					}
				}
			}

			parent = parent.getSuperclass();
		} while (parent != null);

		return null;
	}

	/**
	 * Searches for the {@link Headers} annotation in the hierarchy of a class
	 *
	 * @param beanClass the class whose hierarchy will be searched
	 *
	 * @return the {@link Headers} annotation of the given class or its most immediate parent, or {@code null} if not found.
	 */
	public static Headers findHeadersAnnotation(Class<?> beanClass) {
		return findAnnotationInClass(beanClass, Headers.class);
	}

	public static Class<?> getType(AnnotatedElement element) {
		if (element instanceof Field) {
			return ((Field) element).getType();
		}
		Method method = (Method) element;
		Class<?>[] params = method.getParameterTypes();
		if (params.length == 1) {
			return params[0];
		} else if (params.length > 1) {
			throw new IllegalArgumentException("Method " + describeElement(element) + " cannot have multiple parameters");
		}

		Class<?> returnType = method.getReturnType();
		if (returnType != void.class) {
			return returnType;
		}
		throw new IllegalArgumentException("Method " + describeElement(element) + " must return a value if it has no input parameter");
	}

	public static Class<?> getDeclaringClass(AnnotatedElement element) {
		if (element instanceof Field) {
			return ((Field) element).getDeclaringClass();
		} else {
			return ((Method) element).getDeclaringClass();
		}
	}

	public static String getName(AnnotatedElement element) {
		if (element instanceof Field) {
			return ((Field) element).getName();
		} else {
			return ((Method) element).getName();
		}
	}

	static String describeElement(AnnotatedElement element) {
		String description;
		if (element instanceof Field) {
			description = "attribute '" + ((Field) element).getName() + "'";
		} else {
			description = "method '" + ((Method) element).getName() + "'";
		}
		return description + " of class " + getDeclaringClass(element).getName();
	}

	private static void processAnnotations(AnnotatedElement element, boolean processNested, List<Integer> indexes, List<TransformedHeader> tmp, Map<AnnotatedElement, List<TransformedHeader>> nestedReplacements, HeaderTransformer transformer, MethodFilter filter) {
		Parsed annotation = findAnnotation(element, Parsed.class);
		if (annotation != null) {
			TransformedHeader header = new TransformedHeader(element, transformer);

			if (filter == MethodFilter.ONLY_GETTERS && header.getHeaderIndex() >= 0 && indexes.contains(header.getHeaderIndex())) {
				//allows the same column to be mapped to multiple fields when parsing, but not when writing.
				throw new IllegalArgumentException("Duplicate field index '" + header.getHeaderIndex() + "' found in " + describeElement(element));
			}
			tmp.add(header);
			indexes.add(header.getHeaderIndex());
		}

		if (processNested) {
			Nested nested = findAnnotation(element, Nested.class);
			if (nested != null) {
				tmp.add(new TransformedHeader(element, null));
				Class nestedBeanType = AnnotationRegistry.getValue(element, nested, "type", nested.type());
				if (nestedBeanType == Object.class) {
					nestedBeanType = getType(element);
				}

				Class<? extends HeaderTransformer> transformerType = AnnotationRegistry.getValue(element, nested, "headerTransformer", nested.headerTransformer());
				if (transformerType != HeaderTransformer.class) {
					String[] args = AnnotationRegistry.getValue(element, nested, "args", nested.args());
					HeaderTransformer innerTransformer = AnnotationHelper.newInstance(HeaderTransformer.class, transformerType, args);
					nestedReplacements.put(element, getFieldSequence(nestedBeanType, true, indexes, innerTransformer, filter));
				} else {
					nestedReplacements.put(element, getFieldSequence(nestedBeanType, true, indexes, transformer, filter));
				}
			}
		}
	}

	/**
	 * Returns a list of fields with {@link Parsed} annotations in the sequence they should be processed for parsing
	 * or writing. The sequence is ordered taking into account their original order in the annotated class, unless
	 * {@link Parsed#index()} is set to a non-negative number.
	 *
	 * @param beanClass     the class whose field sequence will be returned.
	 * @param processNested flag indicating whether {@link Nested} annotations should be processed
	 * @param transformer   a {@link HeaderTransformer} instance to be used for transforming headers of a given {@link Nested} attribute.
	 * @param filter        filter to apply over annotated methods when the class is being used for reading data from beans (to write values to an output)
	 *                      or when writing values into beans (while parsing). It is used to choose either a "get" or a "set"
	 *                      method annotated with {@link Parsed}, when both methods target the same field.
	 *
	 * @return a list of fields ordered by their processing sequence
	 */
	public static List<TransformedHeader> getFieldSequence(Class beanClass, boolean processNested, HeaderTransformer transformer, MethodFilter filter) {
		List<Integer> indexes = new ArrayList<Integer>();
		List<TransformedHeader> tmp = getFieldSequence(beanClass, processNested, indexes, transformer, filter);

		Collections.sort(tmp, new Comparator<TransformedHeader>() {
			@Override
			public int compare(TransformedHeader t1, TransformedHeader t2) {
				int i1 = t1.getHeaderIndex();
				int i2 = t2.getHeaderIndex();
				return i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
			}
		});
		Collections.sort(indexes);

		int col = -1;
		for (int i : indexes) {
			col++;
			if (i < 0) {
				continue;
			}
			if (i != col) {
				while (i >= tmp.size()) {
					tmp.add(null);
				}
				Collections.swap(tmp, i, col);
			}
		}

		return tmp;
	}

	private static List<TransformedHeader> getFieldSequence(Class beanClass, boolean processNested, List<Integer> indexes, HeaderTransformer transformer, MethodFilter filter) {
		List<TransformedHeader> tmp = new ArrayList<TransformedHeader>();

		Map<AnnotatedElement, List<TransformedHeader>> nestedReplacements = new LinkedHashMap<AnnotatedElement, List<TransformedHeader>>();

		for (Field field : getAllFields(beanClass).keySet()) {
			processAnnotations(field, processNested, indexes, tmp, nestedReplacements, transformer, filter);
		}

		for (Method method : getAnnotatedMethods(beanClass, filter)) {
			processAnnotations(method, processNested, indexes, tmp, nestedReplacements, transformer, filter);
		}

		if (!nestedReplacements.isEmpty()) {
			int size = tmp.size();
			for (int i = size - 1; i >= 0; i--) {
				TransformedHeader field = tmp.get(i);
				List<TransformedHeader> nestedFields = nestedReplacements.remove(field.getTarget());
				if (nestedFields != null) {
					tmp.remove(i);
					tmp.addAll(i, nestedFields);

					if (nestedReplacements.isEmpty()) {
						break;
					}
				}
			}
		}
		return tmp;
	}

	/**
	 * Returns all fields available from a given class.
	 *
	 * @param beanClass a class whose fields will be returned.
	 *
	 * @return a map of {@link Field} and the corresponding {@link PropertyWrapper}
	 */
	public static Map<Field, PropertyWrapper> getAllFields(Class<?> beanClass) {

		Map<String, PropertyWrapper> properties = new LinkedHashMap<String, PropertyWrapper>();
		try {
			for (PropertyWrapper property : BeanHelper.getPropertyDescriptors(beanClass)) {
				String name = property.getName();
				if (name != null) {
					properties.put(name, property);
				}
			}
		} catch (Exception e) {
			//ignore and proceed to get fields directly
		}

		Set<String> used = new HashSet<String>();
		Class<?> clazz = beanClass;

		Map<Field, PropertyWrapper> out = new LinkedHashMap<Field, PropertyWrapper>();

		do {
			Field[] declared = clazz.getDeclaredFields();
			for (Field field : declared) {
				if (used.contains(field.getName())) {
					continue;
				}
				used.add(field.getName());
				out.put(field, properties.get(field.getName()));
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null && clazz != Object.class);
		return out;
	}

	/**
	 * Returns all methods available from a given class that have a specific annotation.
	 *
	 * @param beanClass a class whose methods will be returned.
	 * @param filter    filter to apply over annotated methods when the class is being used for reading data from beans (to write values to an output)
	 *                  or when writing values into beans (while parsing). It is used to choose either a "get" or a "set"
	 *                  method annotated with {@link Parsed}, when both methods target the same field.
	 * @param annotationType the annotation type to consider
	 *
	 * @param <A> the expected annotation type
	 *
	 * @return a list of {@link Method} with at least one annotation
	 */
	public static <A extends Annotation> List<Method> getAnnotatedMethods(Class<?> beanClass, MethodFilter filter, Class<A> annotationType) {
		List<Method> out = new ArrayList<Method>();

		Class clazz = beanClass;

		do {
			Method[] declared = clazz.getDeclaredMethods();
			outer:
			for (Method method : declared) {
				if (!method.isSynthetic() && annotationType == NO_ANNOTATIONS.class) {
					if (!filter.reject(method)) {
						out.add(method);
					}
				} else {
					Annotation[] annotations = method.getDeclaredAnnotations();
					for (Annotation annotation : annotations) {
						if ((annotationType == null && isCustomAnnotation(annotation)) || annotationType == annotation.annotationType()) {
							if (filter.reject(method)) {
								continue outer;
							}
							out.add(method);
							continue outer;
						}
					}
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null && clazz != Object.class);
		return out;
	}

	private static final class NO_ANNOTATIONS implements Annotation {
		@Override
		public Class<? extends Annotation> annotationType() {
			return NO_ANNOTATIONS.class;
		}
	}

	/**
	 * Returns all methods available from a given class
	 *
	 * @param beanClass a class whose methods will be returned.
	 * @param filter    filter to apply over annotated methods when the class is being used for reading data from beans (to write values to an output)
	 *                  or when writing values into beans (while parsing). It is used to choose either a "get" or a "set"
	 *                  method annotated with {@link Parsed}, when both methods target the same field.
	 *
	 * @return a list of {@link Method}s that conform to the given filter.
	 */
	public static List<Method> getAllMethods(Class<?> beanClass, MethodFilter filter) {
		return getAnnotatedMethods(beanClass, filter, NO_ANNOTATIONS.class);
	}

	/**
	 * Returns all methods available from a given class that have an annotation.
	 *
	 * @param beanClass a class whose methods will be returned.
	 * @param filter    filter to apply over annotated methods when the class is being used for reading data from beans (to write values to an output)
	 *                  or when writing values into beans (while parsing). It is used to choose either a "get" or a "set"
	 *                  method annotated with {@link Parsed}, when both methods target the same field.
	 *
	 * @return a list of {@link Method} with at least one annotation
	 */
	public static List<Method> getAnnotatedMethods(Class<?> beanClass, MethodFilter filter) {
		return getAnnotatedMethods(beanClass, filter, null);
	}

	/**
	 * Returns all attributes available from a given class that have an annotation.
	 *
	 * @param beanClass a class whose methods will be returned.
	 *
	 * @return a list of {@link Field} with at least one annotation
	 */
	public static List<Field> getAnnotatedFields(Class<?> beanClass) {
		return getAnnotatedFields(beanClass, null);
	}

	/**
	 * Returns all attributes available from a given class that have an annotation.
	 *
	 * @param beanClass a class whose methods will be returned.
	 * @param annotationType the type of annotation to consider
	 * @param <A> the expected annotation type
	 *
	 * @return a list of {@link Field} with at least one annotation
	 */
	public static <A extends Annotation> List<Field> getAnnotatedFields(Class<?> beanClass, Class<A> annotationType) {
		List<Field> out = new ArrayList<Field>();

		Class clazz = beanClass;

		do {
			Field[] declared = clazz.getDeclaredFields();
			outer:
			for (Field field : declared) {
				Annotation[] annotations = field.getDeclaredAnnotations();
				for (Annotation annotation : annotations) {
					if ((annotationType == null && isCustomAnnotation(annotation)) || annotationType == annotation.annotationType()) {
						out.add(field);
						continue outer;
					}
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null && clazz != Object.class);
		return out;
	}

	/**
	 * Searches for an annotation of a given type that's been applied to an element either directly (as a regular annotation)
	 * or indirectly (as a meta-annotations, i.e. an annotation that has annotations).
	 *
	 * @param annotatedElement the element whose annotations will be searched
	 * @param annotationType   the type of annotation to search for
	 * @param <A>              the type of the annotation being searched for
	 *
	 * @return the annotation associated with the given element, or {@code null} if not found.
	 */
	public synchronized static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
		if (annotatedElement == null || annotationType == null) {
			return null;
		}

		if (annotatedElement.equals(lastProcessedElement) && annotationType == lastProcessedAnnotationType) {
			return (A) lastAnnotationFound;
		}

		lastProcessedElement = annotatedElement;
		lastProcessedAnnotationType = annotationType;

		Stack<Annotation> path = new Stack<Annotation>();

		A annotation = findAnnotation(annotatedElement, annotationType, new HashSet<Annotation>(), path);
		if (annotation == null || path.isEmpty()) {
			lastAnnotationFound = annotation;
			return annotation;
		}

		while (!path.isEmpty()) {
			Annotation parent = path.pop();
			Annotation target = path.isEmpty() ? annotation : path.peek();

			for (Method method : parent.annotationType().getDeclaredMethods()) {
				Copy copy = method.getAnnotation(Copy.class);
				if (copy != null) {
					Class targetClass = copy.to();

					String targetProperty = copy.property();
					if (targetProperty.trim().isEmpty()) {
						targetProperty = method.getName();
					}

					Object value;

					Object existingValue = AnnotationRegistry.getValue(annotatedElement, target, method.getName());
					if (existingValue != null) {
						value = existingValue;
					} else {
						value = invoke(parent, method);
					}

					Class sourceValueType = method.getReturnType();
					Class<?> targetPropertyType = findAnnotationMethodType(targetClass, targetProperty);
					if (targetPropertyType != null && targetPropertyType.isArray() && !value.getClass().isArray()) {
						Object array = Array.newInstance(sourceValueType, 1);
						Array.set(array, 0, value);
						value = array;
					}

					if (targetClass == target.annotationType()) {
						AnnotationRegistry.setValue(annotatedElement, annotation, targetProperty, value);
					} else {
						A ann = (A) findAnnotation(annotatedElement, targetClass, new HashSet<Annotation>(), new Stack<Annotation>());
						if (ann != null) {
							AnnotationRegistry.setValue(annotatedElement, ann, targetProperty, value);
						} else {
							throw new IllegalStateException("Can't process @Copy annotation on '" + method + "'. " +
									"Annotation '" + targetClass.getName() + "' not used in " + parent.annotationType().getName() + ". Unable to process field " + annotatedElement + "'");
						}
					}

				}
			}
		}
		lastAnnotationFound = annotation;
		return annotation;
	}

	private static Class<?> findAnnotationMethodType(Class<? extends Annotation> type, String methodName) {
		for (Method method : type.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				return method.getReturnType();
			}
		}
		return null;
	}

	private static Object invoke(Annotation annotation, Method method) {
		try {
			return method.invoke(annotation, (Object[]) null);
		} catch (Exception e) {
			throw new IllegalStateException("Can't read value from annotation " + annotation, e);
		}
	}

	private static <A> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType, Set<Annotation> visited, Stack<Annotation> path) {
		Annotation[] declaredAnnotations = annotatedElement.getDeclaredAnnotations();
		for (int i = 0; i < declaredAnnotations.length; i++) {
			Annotation ann = declaredAnnotations[i];
			if (ann.annotationType() == annotationType) {
				return (A) ann;
			}
		}
		for (int i = 0; i < declaredAnnotations.length; i++) {
			Annotation ann = declaredAnnotations[i];
			if (isCustomAnnotation(ann) && visited.add(ann)) {
				A annotation = findAnnotation(ann.annotationType(), annotationType, visited, path);
				path.push(ann);
				if (annotation != null) {
					return annotation;
				}
			}
		}
		return null;
	}


	private static final Set<Class> javaLangAnnotationTypes = new HashSet<Class>();
	private static final Set<Class> customAnnotationTypes = new HashSet<Class>();

	private static boolean isCustomAnnotation(Annotation annotation) {
		Class annotationType = annotation.annotationType();
		if (customAnnotationTypes.contains(annotationType)) {
			return true;
		}
		if (javaLangAnnotationTypes.contains(annotationType)) {
			return false;
		}
		if (annotationType.getName().startsWith("java.lang.annotation")) {
			javaLangAnnotationTypes.add(annotationType);
			return false;
		} else {
			customAnnotationTypes.add(annotationType);
			return true;
		}
	}

	/**
	 * Returns all annotations applied to an element, excluding the ones not in a given package.
	 *
	 * @param annotatedElement the element (method, field, etc) whose annotations will be extracted
	 * @param aPackage         the package of the annotations that should be returned
	 *
	 * @return the list of annotation elements applied to the given element, that are also members of the given package.
	 */
	public static List<Annotation> findAllAnnotationsInPackage(AnnotatedElement annotatedElement, Package aPackage) {
		final ArrayList<Annotation> found = new ArrayList<Annotation>();
		findAllAnnotationsInPackage(annotatedElement, aPackage, found, new HashSet<Annotation>());
		return found;
	}

	private static void findAllAnnotationsInPackage(AnnotatedElement annotatedElement, Package aPackage, ArrayList<? super Annotation> found, Set<Annotation> visited) {
		Annotation[] declaredAnnotations = annotatedElement.getDeclaredAnnotations();

		for (int i = 0; i < declaredAnnotations.length; i++) {
			Annotation ann = declaredAnnotations[i];
			if (aPackage.equals(ann.annotationType().getPackage())) {
				found.add(ann);
			}
			if (isCustomAnnotation(ann) && visited.add(ann)) {
				findAllAnnotationsInPackage(ann.annotationType(), aPackage, found, visited);
			}
		}
	}

	/**
	 * Returns Java's default value for a given type, in a primitive type wrapper.
	 *
	 * @param type the primitive type whose default value will be returned.
	 *
	 * @return the default value for the given primitive type, or {@code null} if the type is not primitive.
	 */
	public static final Object getDefaultPrimitiveValue(Class type) {
		if (type == int.class) {
			return Integer.valueOf(0);
		} else if (type == double.class) {
			return 0.0D;
		} else if (type == boolean.class) {
			return Boolean.FALSE;
		} else if (type == long.class) {
			return Long.valueOf(0L);
		} else if (type == float.class) {
			return 0.0F;
		} else if (type == byte.class) {
			return Byte.valueOf((byte) 0);
		} else if (type == char.class) {
			return Character.valueOf('\0');
		} else if (type == short.class) {
			return Short.valueOf((short) 0);
		}
		return null;
	}
}
