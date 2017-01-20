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
package com.gtcgroup.justify.jpa.po;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.po.internal.BaseQueryJpaPO;

/**
 * This Parameter Object class supports named query operations.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstCriteriaQueryJpaPO extends BaseQueryJpaPO {

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstCriteriaQueryJpaPO}
	 */
	public static JstCriteriaQueryJpaPO withQuery(final boolean readOnly, final boolean suppressExceptionForNull) {

		return new JstCriteriaQueryJpaPO(readOnly, suppressExceptionForNull);
	}

	protected String queryLanguageString;

	protected Class<?> resultClass;

	/**
	 * Constructor
	 */
	protected JstCriteriaQueryJpaPO(final boolean readOnly, final boolean suppressExceptionForNull) {

		super(readOnly, suppressExceptionForNull);
		this.readOnly = readOnly;
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
	protected Query createCriteriaQuery(final String queryLanguageString) {

		return getEntityManager().createQuery(queryLanguageString);
	}

	/**
	 * @return {@link TypedQuery}
	 */
	protected <ENTITY> TypedQuery<ENTITY> createCriteriaQuery(final String queryLanguageString,
			final Class<ENTITY> resultClass) {

		return getEntityManager().createQuery(queryLanguageString, resultClass);
	}

	/**
	 * @return {@link Query}
	 */
	@Override
	public Query getQuery() {

		if (null == this.query) {

			try {
				if (isResultClass() && isQueryLanaguageString()) {
					this.query = createCriteriaQuery(getQueryLanguageString(), getResultClass());
				} else if (isQueryLanaguageString()) {
					this.query = createCriteriaQuery(getQueryLanguageString());
				} else if (isResultClass()) {
					this.query = createCriteriaQuery(getResultClass());
				} else {
					throw new TestingRuntimeException(
							"Verify that a coherent set of parameters were defined in the PO ["
									+ this.getClass().getName() + "].");
				}
			} catch (final Exception e) {
				throw new TestingRuntimeException(e);
			}
		}
		return this.query;
	}

	/**
	 * @return {@link String}
	 */
	public String getQueryLanguageString() {

		return this.queryLanguageString;
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
	public boolean isQueryLanaguageString() {

		return null != this.queryLanguageString;
	}

	/**
	 * @return boolean
	 */
	public boolean isResultClass() {

		return null != this.resultClass;
	}

	/**
	 * @return {@link JstCriteriaQueryJpaPO}
	 */
	public JstCriteriaQueryJpaPO withEntityManager(final EntityManager entityManager) {

		this.entityManager = entityManager;
		return this;
	}

	/**
	 * @return {@link JstCriteriaQueryJpaPO}
	 */
	public JstCriteriaQueryJpaPO withPersistenceUnitName(final String persistenceUnitName) {

		this.persistenceUnitName = persistenceUnitName;
		this.entityManager = JstEntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

		return this;
	}

	/**
	 * @return {@link JstCriteriaQueryJpaPO}
	 */
	public JstCriteriaQueryJpaPO withQueryLanguageString(final String queryLanguageString) {

		this.queryLanguageString = queryLanguageString;
		return this;
	}

	/**
	 * @return {@link JstCriteriaQueryJpaPO}
	 */
	public <ENTITY> JstCriteriaQueryJpaPO withResultClass(final Class<ENTITY> resultClass) {

		this.resultClass = resultClass;
		return this;
	}
}
