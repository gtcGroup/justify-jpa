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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.po.internal.BaseJpaPO;

/**
 * This Parameter Object class supports find operations.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstTransactionJpaPO extends BaseJpaPO {

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstTransactionJpaPO}
	 */
	public static JstTransactionJpaPO withException() {

		return withException(false);
	}

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstTransactionJpaPO}
	 */
	public static JstTransactionJpaPO withException(final boolean suppressExceptionLogging) {

		return new JstTransactionJpaPO(suppressExceptionLogging);
	}

	private List<Object> entityMergeList = new ArrayList<Object>();

	private final List<Object> entityDeleteList = new ArrayList<Object>();

	/**
	 * Constructor
	 */
	protected JstTransactionJpaPO(final boolean suppressExceptionForNull) {

		super(suppressExceptionForNull);
		return;
	}

	/**
	 * @return {@link Object}
	 */
	public List<Object> getEntityDeleteList() {

		return this.entityDeleteList;
	}

	/**
	 * @return {@link Object}
	 */
	public List<Object> getEntityMergeList() {

		return this.entityMergeList;
	}

	/**
	 * @return boolean
	 */
	public boolean isEntityDelete() {
		return 0 != this.entityDeleteList.size();
	}

	/**
	 * @return boolean
	 */
	public boolean isEntityMerge() {
		return 0 != this.entityMergeList.size();
	}

	/**
	 * This method substitutes merged entities after the transaction is
	 * completed.
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY> void replaceEntityMergeList(final List<ENTITY> entityMergeList) {

		this.entityMergeList = (List<Object>) entityMergeList;
		return;
	}

	/**
	 * @return {@link JstTransactionJpaPO}
	 */
	public <ENTITY> JstTransactionJpaPO withCreateAndUpdateEntities(final ENTITY... entity) {

		this.entityMergeList.addAll(Arrays.asList(entity));
		return this;
	}

	/**
	 * @return {@link JstTransactionJpaPO}
	 */
	public <ENTITY> JstTransactionJpaPO withCreateAndUpdateList(final List<ENTITY> entityList) {

		this.entityMergeList.addAll(entityList);
		return this;
	}

	/**
	 * @return {@link JstTransactionJpaPO}
	 */
	public <ENTITY> JstTransactionJpaPO withDeleteEntities(final ENTITY... entity) {

		this.entityDeleteList.addAll(Arrays.asList(entity));
		return this;
	}

	/**
	 * @return {@link JstTransactionJpaPO}
	 */
	public <ENTITY> JstTransactionJpaPO withDeleteList(final List<ENTITY> deleteList) {

		this.entityDeleteList.addAll(deleteList);
		return this;
	}

	/**
	 * @return {@link JstTransactionJpaPO}
	 */
	public JstTransactionJpaPO withEntityManager(final EntityManager entityManager) {

		this.entityManager = entityManager;
		return this;
	}

	/**
	 * @return {@link JstTransactionJpaPO}
	 */
	public JstTransactionJpaPO withPersistenceUnitName(final String persistenceUnitName) {

		this.persistenceUnitName = persistenceUnitName;
		this.entityManager = JstEntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

		return this;
	}
}
