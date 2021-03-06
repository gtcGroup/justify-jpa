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

import com.gtcgroup.justify.jpa.po.internal.BaseFindPO;

/**
 * This Parameter Object class supports find operations.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstFindSinglePO extends BaseFindPO {

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstQueryNamedPO}
	 */
	public static JstFindSinglePO withPersistenceUnitName(final String persistenceUnitName) {

		return new JstFindSinglePO(persistenceUnitName);
	}

	private Object entityIdentity;

	/**
	 * Constructor
	 */
	protected JstFindSinglePO(final String persistenceUnitName) {
		super(persistenceUnitName);
		return;
	}

	/**
	 * @return {@link Object}
	 */
	public Object getEntityIdentity() {

		return this.entityIdentity;
	}

	/**
	 * @return {@link JstFindSinglePO}
	 */
	public <ENTITY> JstFindSinglePO withEntityClass(final Class<ENTITY> entityClass) {

		setEntityClass(entityClass);
		return this;
	}

	/**
	 * @return {@link JstFindSinglePO}
	 */
	public JstFindSinglePO withEntityContainingIdentity(final Object entityContainingIdentity) {

		setEntityClass(entityContainingIdentity.getClass());
		this.entityIdentity = retrieveEntityIdentity(entityContainingIdentity);
		return this;
	}

	/**
	 * @return {@link JstFindSinglePO}
	 */
	public JstFindSinglePO withEntityIdentity(final Object entityIdentity) {

		this.entityIdentity = entityIdentity;
		return this;
	}

	/**
	 * @return {@link JstFindSinglePO}
	 */
	public JstFindSinglePO withEntityManager(final EntityManager entityManager) {

		super.setEntityManager(entityManager);
		return this;
	}

	/**
	 * @return {@link JstFindSinglePO}
	 */
	public JstFindSinglePO withForceDatabaseTripWhenNoCacheCoordination() {

		super.setForceDatabaseTripWhenNoCacheCoordination();
		return this;
	}

	/**
	 * @return {@link JstFindSinglePO}
	 */
	public JstFindSinglePO withQueryHint(final String key, final Object value) {

		super.setQueryHint(key, value);
		return this;
	}

	/**
	 * @return {@link JstFindSinglePO}
	 */
	public JstFindSinglePO withReadOnly() {

		super.setReadOnly();
		return this;
	}
}
