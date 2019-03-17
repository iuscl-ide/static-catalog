/*******************************************************************************
 * Copyright 2016 Univocity Software Pty Ltd
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
package com.univocity.parsers.annotations;

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.fixed.*;

import java.lang.annotation.*;

/**
 * The {@code @FixedWidth} annotation, along with the {@link Parsed} annotation, allows users to configure the length,
 * alignment and padding of fields parsed/written using the {@link FixedWidthParser} and {@link FixedWidthWriter}
 *
 * <p>Commonly used for java beans processed using {@link BeanProcessor} and/or {@link BeanWriterProcessor}
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 * @see FixedWidthFields
 * @see FixedWidthParser
 * @see FixedWidthWriter
 * @see FixedWidthParserSettings
 * @see FixedWidthWriterSettings
 * @see BeanProcessor
 * @see BeanWriterProcessor
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface FixedWidth {

	/**
	 * Sets the length of the fixed-width field
	 *
	 * @return length of the fixed-width field
	 */
	int value() default -1;

	/**
	 * Sets the alignment of the fixed-width field
	 *
	 * @return alignment of the fixed-width field
	 */
	FieldAlignment alignment() default FieldAlignment.LEFT;

	/**
	 * Sets the padding character of the fixed-width field
	 *
	 * @return padding of the fixed-width field
	 */
	char padding() default ' ';

	/**
	 * Configures whether to retain the padding character when parsing values for this field
	 *
	 * <i>(defaults to {@code false})</i>
	 *
	 * @return flag indicating the padding character should be kept in the parsed value
	 */
	boolean keepPadding() default false;

	/**
	 * Defines the starting position of the fixed-width field
	 *
	 * @return Defines the starting position of the fixed-width field
	 */
	int from() default -1;

	/**
	 * Defines the end position of the fixed-width field
	 *
	 * @return Defines the end position of the fixed-width field
	 */
	int to() default -1;

}
