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

import java.util.HashMap;
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
	 * @param persistenceUnitName
	 * @param populatedEntity
	 * @return {@link JstAssertJpaPO}
	 */
	public static JstAssertJpaPO withPopulatedEntity(final String persistenceUnitName, final Object populatedEntity) {

		return new JstAssertJpaPO(persistenceUnitName, populatedEntity);
	}

	private Object populatedEntity;

	private String persistenceUnitName;

	private final Map<String, String> cascadePersistMap = new HashMap<String, String>();

	private final Map<String, String> cascadeRemoveMap = new HashMap<String, String>();

	private final Map<String, String> cascadeDoNotPersistMap = new HashMap<String, String>();

	private final Map<String, String> cascadeDoNotRemoveMap = new HashMap<String, String>();

	private final Map<String, String> doNotCleanupMap = new HashMap<String, String>();

	/**
	 * Constructor
	 *
	 * @param persistenceUnitName
	 * @param populatedEntity
	 */
	protected JstAssertJpaPO(final String persistenceUnitName, final Object populatedEntity) {

		super();

		this.persistenceUnitName = persistenceUnitName;
		this.populatedEntity = populatedEntity;

		return;
	}

	/**
	 * @return {@link Map}
	 */
	Map<String, String> getCascadeDoNoRemovedMap() {

		return this.cascadeDoNotRemoveMap;
	}



	/**
	 * @return {@link Map}
	 */
	Map<String, String> getCascadeDoNotPersistMap() {
		return this.cascadeDoNotPersistMap;
	}


	/**
	 * @return {@link Map}
	 */
	Map<String, String> getCascadePersistMap() {

		return this.cascadePersistMap;
	}




	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, String> getCascadeRemoveMap() {

		return this.cascadeRemoveMap;
	}

	/**
	 * @return {@link Map}
	 */
	Map<String, String> getDoNotCleanupMap() {
		return this.doNotCleanupMap;
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
	 * @param clazz
	 * @param fieldName
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeAll(final Class<?> clazz, final String fieldName) {

		final String className = clazz.getName();

		this.cascadePersistMap.put(className, fieldName);
		this.cascadeRemoveMap.put(className, fieldName);

		return this;
	}

	/**
	 * @param clazz
	 * @param fieldName
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeAllExceptRemove(final Class<?> clazz, final String fieldName) {

		return withCascadeAllExceptRemove(clazz, fieldName, true);
	}

	/**
	 * @param clazz
	 * @param fieldName
	 * @param cleanup
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeAllExceptRemove(final Class<?> clazz, final String fieldName,
			final boolean cleanup) {

		final String className = clazz.getName();

		this.cascadePersistMap.put(className, fieldName);
		this.cascadeDoNotRemoveMap.put(className, fieldName);

		if (!cleanup) {
			this.doNotCleanupMap.put(className, fieldName);
		}

		return this;
	}

	/**
	 * @param clazz
	 * @param fieldName
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeNone(final Class<?> clazz, final String fieldName) {

		final String className = clazz.getName();

		this.cascadeDoNotPersistMap.put(className, fieldName);
		this.cascadeDoNotRemoveMap.put(className, fieldName);

		return this;
	}

	/**
	 * @param clazz
	 * @param fieldName
	 * @param cleanup
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadePersist(final Class<?> clazz, final String fieldName, final boolean cleanup) {

		final String className = clazz.getName();

		this.cascadePersistMap.put(className, fieldName);

		if (!cleanup) {
			this.doNotCleanupMap.put(className, fieldName);
		}
		return this;
	}

	/**
	 * @param clazz
	 * @param fieldName
	 * @param cleanup
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeRemove(final Class<?> clazz, final String fieldName, final boolean cleanup) {

		final String className = clazz.getName();

		this.cascadeRemoveMap.put(className, fieldName);

		if (!cleanup) {
			this.doNotCleanupMap.put(className, fieldName);
		}
		return this;
	}
}
