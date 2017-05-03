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

import com.gtcgroup.justify.jpa.po.internal.BaseJpaPO;

/**
 * This Parameter Object class supports counting all entities.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstCountAllJpaPO extends BaseJpaPO {

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstCountAllJpaPO}
	 */
	public static JstCountAllJpaPO withQuery(final boolean suppressExceptionForNull) {

		return new JstCountAllJpaPO(suppressExceptionForNull);
	}

	protected Class<?> resultClass;

	/**
	 * Constructor
	 */
	protected JstCountAllJpaPO(final boolean suppressExceptionForNull) {

		super(suppressExceptionForNull);
		return;
	}

	/**
	 * @return {@link Class}
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY> Class<ENTITY> getResultClass() {

		return (Class<ENTITY>) this.resultClass;
	}

	/**
	 * @return {@link JstCountAllJpaPO}
	 */
	public JstCountAllJpaPO withEntityManager(final EntityManager entityManager) {

		this.entityManager = entityManager;
		return this;
	}

	/**
	 * @return {@link JstCountAllJpaPO}
	 */
    @Override
    public JstCountAllJpaPO withPersistenceUnitName(final String persistenceUnitName) {

		return (JstCountAllJpaPO) super.withPersistenceUnitName(persistenceUnitName);
	}

	/**
	 * @return {@link JstCountAllJpaPO}
	 */
	public <ENTITY> JstCountAllJpaPO withResultClass(final Class<ENTITY> resultClass) {

		this.resultClass = resultClass;
		return this;
	}
}