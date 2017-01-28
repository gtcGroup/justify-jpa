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

import java.util.List;

import javax.persistence.NoResultException;

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;
import com.gtcgroup.justify.jpa.helper.JstQueryUtilHelper;
import com.gtcgroup.justify.jpa.po.JstCriteriaQueryJpaPO;

/**
 * This Resource Manager provides convenience methods for named queries.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstCriteriaQueryJpaRM {

	@SuppressWarnings("javadoc")
	INTERNAL;

	/**
	 * This method returns the number of records in the table or view. It may be
	 * used in support of query processing.
	 *
	 * @return long
	 */
	public static long count(final JstCriteriaQueryJpaPO queryPO) {

		long count = 0;

		try {
			count = JstQueryUtilHelper.count(queryPO);
		} catch (final Exception e) {

			throw new JustifyRuntimeException(e);
		}
		return count;
	}

	/**
	 * @return {@link Object} or null or {@link Exception}
	 */
	public static <ENTITY> List<ENTITY> queryList(final JstCriteriaQueryJpaPO queryPO) {

		return JstQueryUtilHelper.queryResultList(queryPO);
	}

	/**
	 * @return {@link Object} or null or {@link Exception}
	 */
	public static <ENTITY> ENTITY querySingle(final JstCriteriaQueryJpaPO queryPO) {

		ENTITY entity = null;

		try {

			entity = JstQueryUtilHelper.querySingleResult(queryPO);

		} catch (@SuppressWarnings("unused") final NoResultException e) {
			throwExceptionForNull(queryPO);
		}
		return entity;
	}

	/**
	 * This method handles exception suppression.
	 */
	protected static void throwExceptionForNull(final JstCriteriaQueryJpaPO queryPO) {

		if (!queryPO.isSuppressException()) {
			throw new JustifyRuntimeException(
					"Unable to retrieve results for the criteria query using the persistence key ["
							+ queryPO.getEntityManagerFactoryKey() + "].");

		}
	}

}
