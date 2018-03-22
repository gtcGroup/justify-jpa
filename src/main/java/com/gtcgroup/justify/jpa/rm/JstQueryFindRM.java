/*
 * [Licensed per the Open Source "MIT License".]
 *
 * Copyright (c) 2006 - 2018 by
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
import java.util.Optional;

import com.gtcgroup.justify.jpa.helper.JstFindUtilHelper;
import com.gtcgroup.justify.jpa.helper.JstQueryUtilHelper;
import com.gtcgroup.justify.jpa.po.JstFindSinglePO;
import com.gtcgroup.justify.jpa.po.JstQueryAllJPO;

/**
 * This Resource Manager class supports "find" operations.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstQueryFindRM {

	INTERNAL;

	/**
	 * @return ENTITY
	 */
	public static <ENTITY> Optional<ENTITY> findSingle(final JstFindSinglePO findPO) {

		return JstFindUtilHelper.findSingle(findPO);
	}

	/**
	 * @return {@link Optional}
	 */
	public static <ENTITY> Optional<List<ENTITY>> queryAll(final JstQueryAllJPO queryAllPO) {

		return JstQueryUtilHelper.queryResultList(queryAllPO);
	}
}