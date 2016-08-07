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
package com.gtcgroup.justify.jpa.helper.internal;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;

/**
 * This Util Helper class provides persistence {@link EntityManager} support.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum QueryUtilHelper {

	@SuppressWarnings("javadoc")
	INSTANCE;

	/**
	 * his method executes a query that returns a result list. Parameters are
	 * passed with an {@link Integer} and/or a {@link String} Map of parameter
	 * values.
	 *
	 * @param <ENTITY>
	 * @param query
	 * @param integerParameterMap
	 *            or null
	 * @param stringParameterMap
	 *            or null
	 * @return {@link List}
	 */
	@SuppressWarnings({ "unchecked", "boxing" })
	public static <ENTITY> List<ENTITY> queryResultList(final Query query,
			final Map<Integer, Object> integerParameterMap, final Map<String, Object> stringParameterMap) {

		List<ENTITY> entityList = null;

		try {

			if (null != integerParameterMap) {

				for (final Entry<Integer, Object> entry1 : integerParameterMap.entrySet()) {

					query.setParameter(entry1.getKey(), entry1.getValue());
				}
			}
			if (null != stringParameterMap) {

				for (final Entry<String, Object> entry2 : stringParameterMap.entrySet()) {

					query.setParameter(entry2.getKey(), entry2.getValue());
				}
			}

			entityList = query.getResultList();

		} catch (final Exception e) {

			throwException(e);
		}
		return entityList;
	}

	/**
	 * This method executes a query that returns a single untyped result.
	 * Parameters are passed with an {@link Integer} and/or a {@link String} Map
	 * of parameter values.
	 *
	 * @param <ENTITY>
	 * @param query
	 * @param integerParameterMap
	 * @param stringParameterMap
	 * @return ENTITY
	 */
	@SuppressWarnings({ "boxing", "unchecked" })
	public static <ENTITY> ENTITY querySingleResult(final Query query, final Map<Integer, Object> integerParameterMap,
			final Map<String, Object> stringParameterMap) {

		ENTITY entity = null;

		try {

			for (final Entry<Integer, Object> entry1 : integerParameterMap.entrySet()) {

				query.setParameter(entry1.getKey(), entry1.getValue());
			}

			for (final Entry<String, Object> entry2 : stringParameterMap.entrySet()) {

				query.setParameter(entry2.getKey(), entry2.getValue());
			}

			entity = (ENTITY) query.getSingleResult();

		} catch (final Exception e) {

			throwException(e);
		}

		return entity;
	}

	/**
	 * @param message
	 */
	private static void throwException(final Exception e) {

		throw new TestingRuntimeException(e);
	}

}
