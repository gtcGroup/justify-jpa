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

import javax.persistence.CascadeType;

import com.gtcgroup.justify.jpa.helper.JstTransactionUtilHelper;
import com.gtcgroup.justify.jpa.po.JstTransactionJpaPO;

/**
 * This Resource Manager class supports transactions.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstTransactionJpaRM {

	@SuppressWarnings("javadoc")
	INTERNAL;

	/**
	 * This method is used for committing a single transaction. If any of the
	 * related child objects are not marked for an applicable
	 * {@link CascadeType} then they need to be explicitly in the
	 * {@link JstTransactionJpaPO}.d.
	 *
	 * @return {@link List}
	 */
	public static <ENTITY, PO extends JstTransactionJpaPO> List<ENTITY> transactMultipleEntities(final PO transactionPO) {

		return JstTransactionUtilHelper.transactEntities(transactionPO);
	}

	/**
	 * This method is used for committing a single transaction. If any of the
	 * related child objects are not marked for an applicable
	 * {@link CascadeType} then they need to be explicitly in the
	 * {@link JstTransactionJpaPO}.
	 *
	 * @return {@link Object}
	 */
	public static <ENTITY, PO extends JstTransactionJpaPO> ENTITY transactEntity(final PO transactionPO) {

		return JstTransactionUtilHelper.transactEntity(transactionPO);
	}

}
