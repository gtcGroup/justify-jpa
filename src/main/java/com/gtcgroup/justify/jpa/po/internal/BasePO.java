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

import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.config.CascadePolicy;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.gtcgroup.justify.core.base.JstBasePO;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;

/**
 * This Parameter Object base class supports {@link Query}s and transactions
 * using the Resource Manager pattern.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public abstract class BasePO extends JstBasePO {

	private final Map<String, Object> queryHints = new ConcurrentHashMap<>();

	private final Map<String, Object> parameterMap = new ConcurrentHashMap<>();

	private EntityManager entityManager;

	private boolean entityManagerEncapsulated = false;

	private String persistenceUnitName;

	/**
	 * Constructor
	 */
	protected BasePO(final String persistenceUnitName) {
		super();

		this.persistenceUnitName = persistenceUnitName;

		return;
	}

	/**
	 * This method closes the {@link EntityManager} if the creation was encapsulated
	 * within this PO.
	 */
	public void closeEntityManager() {

		if (this.entityManagerEncapsulated) {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(this.entityManager);
			this.entityManager = null;
		}
	}

	/**
	 * @return {@link Optional}
	 */
	public EntityManager getEntityManager() {

		if (null == this.entityManager) {

			final Optional<EntityManager> entityManagerTemp = JstEntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(this.persistenceUnitName);

			if (entityManagerTemp.isPresent()) {
				this.entityManager = entityManagerTemp.get();
				this.entityManagerEncapsulated = true;
			}
		}
		return this.entityManager;
	}

	public Map<String, Object> getParameterMap() {
		return this.parameterMap;
	}

	/**
	 * @return {@link Map}
	 */
	public Map<String, Object> getQueryHints() {
		return this.queryHints;
	}

	protected void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	protected void setForceDatabaseTripWhenNoCacheCoordination() {

		getQueryHints().put(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
		getQueryHints().put(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH);
		getQueryHints().put(QueryHints.REFRESH_CASCADE, CascadePolicy.CascadeByMapping);
		getQueryHints().put(QueryHints.REFRESH, HintValues.TRUE);
	}

	protected void setParameter(final String key, final Object value) {

		getParameterMap().put(key, value);
	}

	protected void setQueryHint(final String key, final Object value) {
		getQueryHints().put(key, value);
	}

	protected void setReadOnly() {
		getQueryHints().put(QueryHints.READ_ONLY, HintValues.TRUE);
	}
}
