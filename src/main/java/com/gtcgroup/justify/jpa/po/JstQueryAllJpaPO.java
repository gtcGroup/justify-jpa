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
package com.gtcgroup.justify.jpa.po;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.gtcgroup.justify.jpa.po.internal.BaseQueryJpaPO;

/**
 * This Parameter Object class supports finding all entities from a table or
 * view.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstQueryAllJpaPO extends BaseQueryJpaPO {

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstQueryAllJpaPO}
	 */
	public static JstQueryAllJpaPO withPersistenceUnitName(final String persistenceUnitName) {

		return new JstQueryAllJpaPO(persistenceUnitName);
	}

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstQueryAllJpaPO}
	 */
	public static JstQueryAllJpaPO withQueryAll(final String persistenceUnitName) {

		return new JstQueryAllJpaPO(persistenceUnitName);
	}

	/**
	 * @return {@link TypedQuery}
	 */
	protected static <ENTITY> TypedQuery<ENTITY> createCriteriaQuery(final EntityManager entityManager,
			final Class<ENTITY> resultClass) {

		final CriteriaQuery<ENTITY> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(resultClass);
		final Root<ENTITY> rootEntry = criteriaQuery.from(resultClass);
		final CriteriaQuery<ENTITY> criteria = criteriaQuery.select(rootEntry);

		return entityManager.createQuery(criteria);
	}

	/**
	 * Constructor
	 */
	protected JstQueryAllJpaPO(final String persistenceUnitName) {

		super(persistenceUnitName);
		return;
	}

	/**
	 * @return {@link Query}
	 */
	@Override
	public Query createQuery() {

		return createCriteriaQuery(getEntityManager(), getEntityClass());
	}

	/**
	 * @return {@link JstQueryAllJpaPO}
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY> JstQueryAllJpaPO withEntityClass(final Class<ENTITY> entityClass) {

		this.entityClass = (Class<Object>) entityClass;
		return this;
	}

	/**
	 * @return {@link JstQueryAllJpaPO}
	 */
	public JstQueryAllJpaPO withEntityManager(final EntityManager entityManager) {

		super.setEntityManager(entityManager);
		return this;
	}

	/**
	 * @return {@link JstQueryAllJpaPO}
	 */
	public JstQueryAllJpaPO withForceDatabaseTripWhenNoCacheCoordination() {

		super.setForceDatabaseTripWhenNoCacheCoordination();
		return this;
	}

	/**
	 * @return {@link JstQueryAllJpaPO}
	 */
	public JstQueryAllJpaPO withQueryHint(final String key, final Object value) {

		super.setQueryHint(key, value);
		return this;
	}

	/**
	 * @return {@link JstQueryAllJpaPO}
	 */
	public JstQueryAllJpaPO withReadOnly() {

		super.setReadOnly();
		return this;
	}
}
