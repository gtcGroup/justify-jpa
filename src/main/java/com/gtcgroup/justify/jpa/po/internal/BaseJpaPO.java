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
package com.gtcgroup.justify.jpa.po.internal;

import java.util.Map;

import javax.persistence.EntityManager;

import com.gtcgroup.justify.core.base.JstBasePO;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;

/**
 * This Parameter Object base class supports query execution.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public abstract class BaseJpaPO extends JstBasePO {

	protected boolean readOnly = true;

	protected boolean suppressExceptionForNull = false;

	protected EntityManager entityManager;

	protected boolean entityManagerNeedsToBeClosed = false;

	protected String persistenceUnitName;

	protected Map<String, Object> persistencePropertyMapOrNull;

	/**
	 * Constructor
	 */
	protected BaseJpaPO(final boolean suppressExceptionForNull) {

		super();
		this.readOnly = true;
		this.suppressExceptionForNull = suppressExceptionForNull;
		return;
	}

	/**
	 * Constructor
	 */
	protected BaseJpaPO(final boolean readOnly, final boolean suppressExceptionForNull) {

		super();
		this.readOnly = readOnly;
		this.suppressExceptionForNull = suppressExceptionForNull;
		return;
	}

	/**
	 * This method closes the {@link EntityManager} if the creation is
	 * encapsulated within this PO.
	 */
	public void closeEntityManagerIfCreatedWithPersistenceUnitName() {

		if (this.entityManagerNeedsToBeClosed) {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(this.entityManager);
		}
	}

	/**
	 * @return {@link EntityManager}
	 */
	public EntityManager getEntityManager() {

		if (!isEntityManager()) {

			this.entityManager = JstEntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(this.persistenceUnitName);
			this.entityManagerNeedsToBeClosed = true;
		}

		return this.entityManager;
	}

	/**
	 * @return boolean
	 */
	protected boolean isEntityManager() {
		return null != this.entityManager;
	}

	/**
	 * @return boolean
	 */
	public boolean isReadOnly() {
		return this.readOnly;
	}

	/**
	 * @return boolean
	 */
	public boolean isSuppressException() {
		return this.suppressExceptionForNull;
	}
}
