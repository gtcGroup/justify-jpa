/*
 * [Licensed per the Open Source "MIT License".]
 *
 * Copyright (c) 2006 - 2016 by
 * Global Technology Consulting Group, Inc. at
 * http://gtcGroup.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gtcgroup.justify.jpa.rm;

import java.util.HashMap;
import java.util.Map;

/**
 * This Parameter Object class supports testing of Domain Entity cascade types.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author
 * @since v.6.0
 */
public class JstQueryJpaPO {

	/**
	 * This method initializes the class.
	 *
	 * @param queryName
	 * @return {@link JstQueryJpaPO}
	 */
	public static JstQueryJpaPO withQueryName(final String queryName) {

		return new JstQueryJpaPO(queryName);
	}

	private String queryName;

	private final Map<Integer, Object> integerParameterMap = new HashMap<Integer, Object>();

	private final Map<String, Object> keyParameterMap = new HashMap<String, Object>();

	/**
	 * Constructor
	 *
	 * @param queryName
	 */
	protected JstQueryJpaPO(final String queryName) {

		super();
		this.queryName = queryName;
		return;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<Integer, Object> getIntegerParameterMap() {

		return this.integerParameterMap;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getKeyParameterMap() {

		return this.keyParameterMap;
	}

	/**
	 * @return String
	 */
	public String getQueryName() {

		return this.queryName;
	}

	/**
	 * @param parameterNumber
	 * @param parameterValue
	 * @return {@link JstQueryJpaPO}
	 */
	public JstQueryJpaPO withIntegerParameter(final Integer parameterNumber, final Object parameterValue) {

		this.integerParameterMap.put(parameterNumber, parameterValue);
		return this;
	}

	/**
	 * @param parameterKey
	 * @param parameterValue
	 * @return {@link JstQueryJpaPO}
	 */
	public JstQueryJpaPO withKeyParameter(final String parameterKey, final Object parameterValue) {

		this.keyParameterMap.put(parameterKey, parameterValue);
		return this;
	}
}
