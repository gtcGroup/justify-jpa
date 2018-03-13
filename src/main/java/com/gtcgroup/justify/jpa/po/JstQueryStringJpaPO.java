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

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.gtcgroup.justify.jpa.po.internal.BaseQueryJpaPO;

/**
 * This Parameter Object class supports native and query language queries.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstQueryStringJpaPO extends BaseQueryJpaPO {

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstQueryStringJpaPO}
	 */
	public static JstQueryStringJpaPO withPersistenceUnitName(final String persistenceUnitName) {

		return new JstQueryStringJpaPO(persistenceUnitName);
	}

	protected String queryLanguageString;

	/**
	 * Constructor
	 */
	protected JstQueryStringJpaPO(final String persistenceUnitName) {

		super(persistenceUnitName);
		return;
	}

	/**
	 * @return {@link Optional}
	 */
	@Override
	public Optional<Query> createQuery() {

		try {
			if (isResultClass() && isQueryLanaguageString()) {
				return Optional.of(createQueryLanguageQuery(getQueryLanguageString(), getEntityClass()));

			} else if (isQueryLanaguageString()) {
				return Optional.of(createQueryLanguageQuery(getQueryLanguageString()));
			}
		} catch (@SuppressWarnings("unused") final Exception e) {
			// Continue.
		}
		return Optional.empty();
	}

	/**
	 * @return {@link Class}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <ENTITY> Class<ENTITY> getEntityClass() {

		return (Class<ENTITY>) this.entityClass;
	}

	public String getQueryLanguageString() {
		return this.queryLanguageString;
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

		return null != this.entityClass;
	}

	/**
	 * @return {@link JstQueryStringJpaPO}
	 */
	public <ENTITY> JstQueryStringJpaPO withEntityClass(final Class<ENTITY> entityClass) {

		setEntityClass(entityClass);
		return this;
	}

	/**
	 * @return {@link JstQueryStringJpaPO}
	 */
	public JstQueryStringJpaPO withEntityManager(final EntityManager entityManager) {

		super.setEntityManager(entityManager);
		return this;
	}

	/**
	 * @return {@link JstQueryStringJpaPO}
	 */
	public JstQueryStringJpaPO withQueryLanguageString(final String queryLanguageString) {

		this.queryLanguageString = queryLanguageString;
		return this;
	}

	/**
	 * @return {@link Query}
	 */
	protected Query createQueryLanguageQuery(final String queryLanguageString) {

		return getEntityManager().createQuery(queryLanguageString);
	}

	/**
	 * @return {@link TypedQuery}
	 */
	protected <ENTITY> TypedQuery<ENTITY> createQueryLanguageQuery(final String queryLanguageString,
			final Class<ENTITY> resultClass) {

		return getEntityManager().createQuery(queryLanguageString, resultClass);
	}
}
