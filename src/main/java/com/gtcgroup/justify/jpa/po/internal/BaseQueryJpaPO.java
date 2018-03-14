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
package com.gtcgroup.justify.jpa.po.internal;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Query;

/**
 * This Parameter Object base class supports queries using the Resource Manager
 * pattern.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public abstract class BaseQueryJpaPO extends BaseJpaPO {

	protected Map<String, Object> parameterMap = new ConcurrentHashMap<>();

	protected Class<Object> entityClass;

	protected int firstResult;

	protected int maxResults;

	protected boolean forceDatabaseTripWhenNoCacheCoordination = false;

	/**
	 * Constructor
	 */
	protected BaseQueryJpaPO(final String persistenceUnitName) {
		super(persistenceUnitName);
	}

	/**
	 * @return {@link Optional}
	 */
	public abstract Optional<Query> createQuery();

	/**
	 * @return {@link Class}
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY> Class<ENTITY> getEntityClass() {
		return (Class<ENTITY>) this.entityClass;
	}

	/**
	 * @return int
	 */
	public int getFirstResult() {
		return this.firstResult;
	}

	/**
	 * @return int}
	 */
	public int getMaxResults() {
		return this.maxResults;
	}

	public Map<String, Object> getParameterMap() {
		return this.parameterMap;
	}

	/**
	 * @return boolean
	 */
	public boolean isEntityClass() {
		return null != this.entityClass;
	}

	/**
	 * @return boolean
	 */
	public boolean isFirstResult() {
		return 0 != this.firstResult;
	}

	/**
	 * @return boolean
	 */
	public boolean isForceDatabaseTripWhenNoCacheCoordination() {
		return this.forceDatabaseTripWhenNoCacheCoordination;
	}

	/**
	 * @return boolean
	 */
	public boolean isMaxResults() {
		return 0 != this.maxResults;
	}

	@SuppressWarnings("unchecked")
	public <ENTITY> void setEntityClass(final Class<ENTITY> entityClass) {
		this.entityClass = (Class<Object>) entityClass;
	}

	@Override
	public void setQueryHint(final String key, final Object value) {
		getQueryHints().put(key, value);
	}

	protected void setParameter(final String key, final Object value) {

		getParameterMap().put(key, value);
	}
}
