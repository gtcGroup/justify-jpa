/*
 * [Licensed per the Open Source "MIT License".]
 *
 * Copyright (c) 2006 - 2017 by
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

import com.gtcgroup.justify.jpa.helper.JstQueryUtilHelper;
import com.gtcgroup.justify.jpa.po.JstNamedQueryJpaPO;

/**
 * This Resource Manager class supports named queries.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstNamedQueryJpaRM {

	@SuppressWarnings("javadoc")
	INTERNAL;

	/**
	 * @return {@link Object} or null or {@link Exception}
	 */
	public static <ENTITY> List<ENTITY> queryReadOnlyList(final JstNamedQueryJpaPO queryPO) {

		List<ENTITY> entityList = null;

		if (queryPO.isParameterMap()) {

			entityList = JstQueryUtilHelper.queryResultList(queryPO, queryPO.getParameterMap());
		} else {

			entityList = JstQueryUtilHelper.queryResultList(queryPO);
		}
		return entityList;
	}

	/**
	 * @return {@link Object} or null or {@link Exception}
	 */
	public static <ENTITY> ENTITY querySingle(final JstNamedQueryJpaPO queryPO) {

		ENTITY entity = null;

		if (queryPO.isParameterMap()) {

			entity = JstQueryUtilHelper.querySingleResult(queryPO, queryPO.getParameterMap());
		} else {

			entity = JstQueryUtilHelper.querySingleResult(queryPO);
		}

		return entity;
	}
}
