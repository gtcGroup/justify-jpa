/*
 * [Licensed per the Open Source "MIT License".]
 *
 * Copyright (c) 2006 - 2016 by
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
package com.gtcgroup.justify.jpa.assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gtcgroup.justify.core.base.JstBaseTestingPO;

/**
 * This Parameter Object class supports testing of Domain Entity cascade types.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author
 * @since v.6.0
 */
public class JstAssertJpaPO extends JstBaseTestingPO {

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstAssertJpaPO}
	 */
	public static JstAssertJpaPO withPopulatedEntity(final String persistenceUnitName, final Object populatedEntity) {

		return new JstAssertJpaPO(persistenceUnitName, populatedEntity);
	}

	private Object populatedEntity;

	private String persistenceUnitName;

	private final List<String> cascadePersistList = new ArrayList<String>();

	private final List<String> cascadeRemoveList = new ArrayList<String>();

	private final List<String> cascadeDoNotPersistList = new ArrayList<String>();

	private final List<String> cascadeDoNotRemoveList = new ArrayList<String>();

	private final List<String> doNotCleanupList = new ArrayList<String>();

	/**
	 * Constructor
	 */
	protected JstAssertJpaPO(final String persistenceUnitName, final Object populatedEntity) {

		super();

		this.persistenceUnitName = persistenceUnitName;
		this.populatedEntity = populatedEntity;

		return;
	}

	/**
	 * @return {@link List}
	 */
	List<String> getCascadeDoNoRemovedList() {

		return this.cascadeDoNotRemoveList;
	}

	/**
	 * @return {@link Map}
	 */
	List<String> getCascadeDoNotPersistList() {
		return this.cascadeDoNotPersistList;
	}

	/**
	 * @return {@link List}
	 */
	List<String> getCascadePersistList() {

		return this.cascadePersistList;
	}

	/**
	 * @return {@link List}
	 */
	List<String> getCascadeRemoveList() {

		return this.cascadeRemoveList;
	}

	/**
	 * @return {@link List}
	 */
	List<String> getDoNotCleanupList() {
		return this.doNotCleanupList;
	}

	/**
	 * @return String
	 */
	String getPersistenceUnitName() {

		return this.persistenceUnitName;
	}

	/**
	 * @return Object
	 */
	Object getPopulatedEntity() {

		return this.populatedEntity;
	}

	/**
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeAll(final String methodName) {

		this.cascadePersistList.add(methodName);
		this.cascadeRemoveList.add(methodName);

		return this;
	}

	/**
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeAllExceptRemove(final String methodName) {

		return withCascadeAllExceptRemove(methodName, true);
	}

	/**
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeAllExceptRemove(final String methodName, final boolean cleanup) {

		this.cascadePersistList.add(methodName);
		this.cascadeDoNotRemoveList.add(methodName);

		if (!cleanup) {
			this.doNotCleanupList.add(methodName);
		}

		return this;
	}

	/**
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeNone(final String methodName) {

		this.cascadeDoNotPersistList.add(methodName);
		this.cascadeDoNotRemoveList.add(methodName);

		return this;
	}

	/**
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadePersist(final String methodName, final boolean cleanup) {

		this.cascadePersistList.add(methodName);

		if (!cleanup) {
			this.doNotCleanupList.add(methodName);
		}
		return this;
	}

	/**
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeRemove(final String methodName, final boolean cleanup) {

		this.cascadeRemoveList.add(methodName);

		if (!cleanup) {
			this.doNotCleanupList.add(methodName);
		}
		return this;
	}
}
