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

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerUtilHelper;
import com.gtcgroup.justify.jpa.helper.JstQueryUtilHelper;
import com.gtcgroup.justify.jpa.po.JstFindJpaPO;

/**
 * This Resource Manager provides convenience methods for find operations.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstFindJpaRM {

	@SuppressWarnings("javadoc")
	INTERNAL;

	/**
	 * @return {@link Object}
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY> ENTITY find(final JstFindJpaPO findPO) {

		final Object entity = findWithEntityManager(findPO);
		JstQueryUtilHelper.throwExceptionForNull(findPO, entity);

		return (ENTITY) entity;
	}

	@SuppressWarnings("unchecked")
	protected static <ENTITY> ENTITY findModifiable(final JstFindJpaPO findPO) {

		ENTITY entity;
		if (findPO.isPopulatedEntityContainingIdentity()) {

			entity = (ENTITY) JstEntityManagerUtilHelper.findModifiableSingleOrNull(findPO.getEntityManager(),
					findPO.getPopulatedEntityContainingIdentity());

		} else {

			entity = (ENTITY) JstEntityManagerUtilHelper.findModifiableSingleOrNull(findPO.getEntityManager(),
					findPO.getEntityClass(), findPO.getEntityIdentity());
		}
		return entity;
	}

	@SuppressWarnings("unchecked")
	protected static <ENTITY> ENTITY findReadOnly(final JstFindJpaPO findPO) {

		ENTITY entity;
		if (findPO.isPopulatedEntityContainingIdentity()) {

			entity = (ENTITY) JstEntityManagerUtilHelper.findReadOnlySingleOrNull(findPO.getEntityManager(),
					findPO.getPopulatedEntityContainingIdentity());

		} else {

			entity = (ENTITY) JstEntityManagerUtilHelper.findReadOnlySingleOrNull(findPO.getEntityManager(),
					findPO.getEntityClass(), findPO.getEntityIdentity());
		}
		return entity;
	}

	protected static <ENTITY> ENTITY findWithEntityManager(final JstFindJpaPO findPO) {

		ENTITY entity = null;

		try {
			if (findPO.isReadOnly()) {

				entity = findReadOnly(findPO);

			} else {

				entity = findModifiable(findPO);
			}
		} catch (final Exception e) {
			throw new JustifyRuntimeException(e);

		} finally {

			findPO.closeEntityManagerIfCreatedWithPersistenceUnitName();
		}

		return entity;
	}
}