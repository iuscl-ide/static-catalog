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
package com.univocity.parsers.common.routine;

import java.io.*;

/**
 * A simple class to hold information about the dimensions of a given input, which are calculated using
 * {@link AbstractRoutines#getInputDimension(File)}
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public final class InputDimension {

	long rows;
	int columns;

	InputDimension() {

	}

	/**
	 * Returns the total number of rows the input contains.
	 *
	 * @return the number of rows found in the input.
	 */
	public final long rowCount() {
		return rows;
	}

	/**
	 * Returns the maximum number of column the input contains.
	 *
	 * @return the number of columns found in the input.
	 */
	public final int columnCount() {
		return columns;
	}

}
