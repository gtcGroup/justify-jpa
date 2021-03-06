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
package com.gtcgroup.justify.jpa.testing.assertion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Entity;

import com.gtcgroup.justify.core.base.JstBasePO;

/**
 * This Parameter Object class supports testing of Domain {@link Entity} cascade
 * types.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v6.2
 */
public class JstAssertCascadePO extends JstBasePO {

	/**
	 * This method initializes the class.
	 *
	 * @return {@link JstAssertCascadePO}
	 */
	public static JstAssertCascadePO withPersistenceUnitName(final String persistenceUnitName) {

		return new JstAssertCascadePO(persistenceUnitName);
	}

	private Object populatedEntity;

	private String persistenceUnitName;

	private final List<String> cascadePersistList = new ArrayList<>();

	private final List<String> cascadeRemoveList = new ArrayList<>();

	private final List<String> cascadeNoPersistList = new ArrayList<>();

	private final List<String> afterVerificationCleanupList = new ArrayList<>();

	/**
	 * Constructor
	 */
	protected JstAssertCascadePO(final String persistenceUnitName) {

		super();

		this.persistenceUnitName = persistenceUnitName;

		return;
	}

	/**
	 * This method is used to replace merged version of the populated entity.
	 */
	public void replacePopulatedEntity(final Optional<Object> populatedEntity) {

		if (populatedEntity.isPresent()) {
			this.populatedEntity = populatedEntity.get();
		} else {
			this.populatedEntity = null;
		}

	}

	/**
	 * @return {@link JstAssertCascadePO}
	 */
	public JstAssertCascadePO withCascadeAll(final String... methodNames) {

		for (final String methodName : methodNames) {
			this.cascadePersistList.add(methodName);
			this.cascadeRemoveList.add(methodName);
		}

		return this;
	}

	/**
	 * @return {@link JstAssertCascadePO}
	 */
	public JstAssertCascadePO withCascadeAllExceptRemove(final String... methodNames) {

		for (final String methodName : methodNames) {
			this.cascadePersistList.add(methodName);
		}

		return this;
	}

	/**
	 * @return {@link JstAssertCascadePO}
	 */
	public JstAssertCascadePO withCascadeNone(final String... methodNames) {

		for (final String methodName : methodNames) {
			this.cascadeNoPersistList.add(methodName);
		}

		return this;
	}

	/**
	 * @return {@link JstAssertCascadePO}
	 */
	public JstAssertCascadePO withCascadePersist(final String... methodNames) {

		for (final String methodName : methodNames) {
			this.cascadePersistList.add(methodName);
		}
		return this;
	}

	/**
	 * @return {@link JstAssertCascadePO}
	 */
	public JstAssertCascadePO withCascadeRemove(final String... methodNames) {

		for (final String methodName : methodNames) {
			this.cascadeRemoveList.add(methodName);
		}

		return this;
	}

	/**
	 * @return {@link JstAssertCascadePO}
	 */
	public JstAssertCascadePO withCleanupAfterVerification(final String... methodNames) {

		for (final String methodName : methodNames) {
			this.afterVerificationCleanupList.add(methodName);
		}
		return this;
	}

	/**
	 * This method signature handles an {@link Entity} that is populated, but not
	 * committed.
	 *
	 * @return {@link JstAssertCascadePO}
	 */
	public JstAssertCascadePO withPopulatedEntity(final Object populatedEntity) {

		this.populatedEntity = populatedEntity;
		return this;
	}

	/**
	 * @return {@link List}
	 */
	protected List<String> getAfterTheTestCleanupList() {
		return this.afterVerificationCleanupList;
	}

	/**
	 * @return {@link Map}
	 */
	protected List<String> getCascadeNoPersistList() {
		return this.cascadeNoPersistList;
	}

	/**
	 * @return {@link List}
	 */
	protected List<String> getCascadePersistList() {

		return this.cascadePersistList;
	}

	/**
	 * @return {@link List}
	 */
	protected List<String> getCascadeRemoveList() {

		return this.cascadeRemoveList;
	}

	/**
	 * @return String
	 */
	protected String getPersistenceUnitName() {

		return this.persistenceUnitName;
	}

	/**
	 * @return Object
	 */
	protected Object getPopulatedEntity() {

		return this.populatedEntity;
	}
}
