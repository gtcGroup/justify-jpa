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
import javax.persistence.NamedQueries;
import javax.persistence.Query;

import com.gtcgroup.justify.jpa.po.internal.BaseQueryPO;

/**
 * This Parameter Object class supports {@link NamedQueries}s.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstQueryNamedPO extends BaseQueryPO {

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstQueryNamedPO}
	 */
	public static JstQueryNamedPO withPersistenceUnitName(final String persistenceUnitName) {

		return new JstQueryNamedPO(persistenceUnitName);
	}

	/**
	 * Constructor
	 */
	protected JstQueryNamedPO(final String persistenceUnitName) {

		super(persistenceUnitName);
		return;
	}

	/**
	 * @return {@link Query}
	 */
	@Override
	public Query createQueryTM() {

		return getEntityManager().createNamedQuery(getQueryName());
	}

	/**
	 * @return {@link JstQueryNamedPO}
	 */
	public JstQueryNamedPO withEntityManager(final EntityManager entityManager) {

		super.setEntityManager(entityManager);
		return this;
	}

	/**
	 * @return {@link JstQueryNamedPO}
	 */
	public JstQueryNamedPO withFirstResult(final int firstResult) {

		setFirstResult(firstResult);
		return this;
	}

	/**
	 * @return {@link JstQueryNamedPO}
	 */
	public JstQueryNamedPO withForceDatabaseTripWhenNoCacheCoordination() {

		setForceDatabaseTripWhenNoCacheCoordination();
		return this;
	}

	/**
	 * @return {@link JstQueryNamedPO}
	 */
	public JstQueryNamedPO withMaxResults(final int maxResults) {

		setMaxResults(maxResults);
		return this;
	}

	/**
	 * @return {@link JstQueryNamedPO}
	 */
	public JstQueryNamedPO withParameter(final String key, final Object value) {

		setParameter(key, value);
		return this;
	}

	/**
	 * @return {@link JstQueryNamedPO}
	 */
	public JstQueryNamedPO withQueryHint(final String key, final Object value) {

		super.setQueryHint(key, value);
		return this;
	}

	/**
	 * @return {@link JstQueryNamedPO}
	 */
	public JstQueryNamedPO withQueryName(final String queryName) {

		setQueryName(queryName);
		return this;
	}

	/**
	 * @return {@link JstQueryNamedPO}
	 */
	public JstQueryNamedPO withReadOnly() {

		setReadOnly();
		return this;
	}
}
