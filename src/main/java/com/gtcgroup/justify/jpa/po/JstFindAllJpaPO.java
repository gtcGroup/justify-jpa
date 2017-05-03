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
package com.gtcgroup.justify.jpa.po;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;
import com.gtcgroup.justify.jpa.po.internal.BaseQueryJpaPO;

/**
 * This Parameter Object class supports finding all entities.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstFindAllJpaPO extends BaseQueryJpaPO {

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstFindAllJpaPO}
	 */
	public static JstFindAllJpaPO withQuery(final boolean suppressExceptionForNull) {

		return new JstFindAllJpaPO(suppressExceptionForNull);
	}

	protected Class<?> resultClass;

	/**
	 * Constructor
	 */
	protected JstFindAllJpaPO(final boolean suppressExceptionForNull) {

		super(suppressExceptionForNull);
		return;
	}

	/**
	 * @return {@link TypedQuery}
	 */
	protected <ENTITY> TypedQuery<ENTITY> createCriteriaQuery(final Class<ENTITY> resultClass) {

		final CriteriaQuery<ENTITY> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(resultClass);
		final Root<ENTITY> rootEntry = criteriaQuery.from(resultClass);
		final CriteriaQuery<ENTITY> criteria = criteriaQuery.select(rootEntry);

		return getEntityManager().createQuery(criteria);
	}

	/**
	 * @return {@link Query}
	 */
	@Override
	public Query getQuery() {

		if (null == this.query) {

			if (isResultClass()) {
				try {
					this.query = createCriteriaQuery(getResultClass());
				} catch (final Exception e) {
					throw new JustifyRuntimeException(e);
				}
			} else {
				throw new JustifyRuntimeException(
						"Verify that a result class is defined in the PO [" + this.getClass().getName() + "].");
			}

		}
		return this.query;
	}

	/**
	 * @return {@link Class}
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY> Class<ENTITY> getResultClass() {

		return (Class<ENTITY>) this.resultClass;
	}

	/**
	 * @return boolean
	 */
	public boolean isResultClass() {

		return null != this.resultClass;
	}

	/**
	 * @return {@link JstFindAllJpaPO}
	 */
	public JstFindAllJpaPO withEntityManager(final EntityManager entityManager) {

		this.entityManager = entityManager;
		return this;
	}

	/**
	 * @return {@link JstFindAllJpaPO}
	 */
    @Override
    public JstFindAllJpaPO withPersistenceUnitName(final String persistenceUnitName) {

		return (JstFindAllJpaPO) super.withPersistenceUnitName(persistenceUnitName);
	}

	/**
	 * @return {@link JstFindAllJpaPO}
	 */
	public <ENTITY> JstFindAllJpaPO withResultClass(final Class<ENTITY> resultClass) {

		this.resultClass = resultClass;
		return this;
	}
}
