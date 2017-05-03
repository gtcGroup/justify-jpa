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

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;
import com.gtcgroup.justify.jpa.po.internal.BaseQueryJpaPO;

/**
 * This Parameter Object class supports named queries.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstNamedQueryJpaPO extends BaseQueryJpaPO {

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstNamedQueryJpaPO}
	 */
	public static JstNamedQueryJpaPO withQuery() {

		return new JstNamedQueryJpaPO(false);
	}

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstNamedQueryJpaPO}
	 */
	public static JstNamedQueryJpaPO withQuery(final boolean suppressExceptionForNull) {

		return new JstNamedQueryJpaPO(suppressExceptionForNull);
	}

	protected String queryName;

	protected Map<String, Object> parameterMap;

	/**
	 * Constructor
	 */
	protected JstNamedQueryJpaPO(final boolean suppressExceptionForNull) {

		super(suppressExceptionForNull);
		return;
	}

	/**
	 * This method retains the {@link Query} as a class field.
	 */
	protected void createNamedQuery() {

		this.query = this.entityManager.createNamedQuery(getQueryName());
	}

	/**
	 * @return {@link Query}
	 */
	@Override
	public Query getQuery() {

		try {
			if (null == this.query) {
				createNamedQuery();
			}
		} catch (final Exception e) {
			throw new JustifyRuntimeException(e);
		}
		return this.query;
	}

	/**
	 * @return {@link String}
	 */
	public String getQueryName() {

		return this.queryName;
	}

	/**
	 * @return {@link Map}
	 */
	public Map<String, Object> getParameterMap() {

		return this.parameterMap;
	}

	/**
	 * @return boolean
	 */
	public boolean isParameterMap() {

		return null != this.parameterMap;
	}

	/**
	 * @return {@link JstNamedQueryJpaPO}
	 */
	public JstNamedQueryJpaPO withEntityManager(final EntityManager entityManager) {

		return (JstNamedQueryJpaPO) super.withEntityManager(entityManager);
	}

	/**
	 * @return {@link JstNamedQueryJpaPO}
	 */
	public JstNamedQueryJpaPO withFirstResult(final int firstResult) {

		this.firstResult = firstResult;
		return this;
	}

	/**
	 * @return {@link JstNamedQueryJpaPO}
	 */
	public JstNamedQueryJpaPO withMaxResults(final int maxResults) {

		this.maxResults = maxResults;
		return this;
	}

	/**
	 * @return {@link JstNamedQueryJpaPO}
	 */
	@Override
	public JstNamedQueryJpaPO withPersistenceUnitName(final String persistenceUnitName) {

		return (JstNamedQueryJpaPO) super.withPersistenceUnitName(persistenceUnitName);
	}

	/**
	 * @return {@link JstNamedQueryJpaPO}
	 */
	public JstNamedQueryJpaPO withQueryName(final String queryName) {

		this.queryName = queryName;
		return this;
	}

	/**
	 * @return {@link JstNamedQueryJpaPO}
	 */
	public JstNamedQueryJpaPO withSuppressReadOnly(final boolean suppress) {

		this.suppressReadOnly = suppress;
		return this;
	}

	/**
	 * @return {@link JstNamedQueryJpaPO}
	 */
	public JstNamedQueryJpaPO withSuppressForceDatabaseTrip(final boolean suppress) {

		this.suppressForceDatabaseTrip = suppress;
		return this;
	}

	/**
	 * @return {@link JstNamedQueryJpaPO}
	 */
	public JstNamedQueryJpaPO withParameterMap(final Map<String, Object> parameterMap) {

		this.parameterMap = parameterMap;
		return this;
	}
}
