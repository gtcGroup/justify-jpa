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

	private final Map<String, Object> persistedMap = new HashMap<String, Object>();

	private final Map<String, Object> removedMap = new HashMap<String, Object>();

	private final Map<String, Object> noPersistedMap = new HashMap<String, Object>();

	private final Map<String, Object> noRemovedMap = new HashMap<String, Object>();

	private final Map<String, Object> noCleanupMap = new HashMap<String, Object>();

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
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getCascadePersistMap() {
		return getPersistedMap();
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getCascadePersistMapFalse() {
		return getNoPersistedMap();
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getCascadePersistNotMap() {
		return getNoPersistedMap();
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getCascadeRemoveMap() {
		return getRemovedMap();
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getCascadeRemoveMapFalse() {
		return getNoRemovedMap();
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getCascadeRemoveNotMap() {
		return getNoRemovedMap();
	}

	/**
	 * @return Map<String,Object>
	 */
	Map<String, Object> getNoCleanupMap() {
		return this.noCleanupMap;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getNoPersistedMap() {
		return this.noPersistedMap;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getNoRemovedMap() {

		return this.noRemovedMap;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getPersistedMap() {

		return this.persistedMap;
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
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getRemovedMap() {

		return this.removedMap;
	}

	/**
	 * @param clazz
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeAll(final Class<?> clazz) {

		final String className = clazz.getName();

		this.persistedMap.put(className, null);
		this.removedMap.put(className, null);

		return this;
	}

	/**
	 * @param clazz
	 * @param entityIdentity
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeAll(final Class<?> clazz, final Object entityIdentity) {

		final String className = clazz.getName();

		this.persistedMap.put(className, entityIdentity);
		this.removedMap.put(className, entityIdentity);

		return this;
	}

	/**
	 * @param clazz
	 * @param entityIdentity
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeAllExceptRemove(final Class<?> clazz, final Object entityIdentity) {

		return withCascadeAllExceptRemove(clazz, entityIdentity, true);
	}

	/**
	 * @param clazz
	 * @param entityIdentity
	 * @param cleanup
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeAllExceptRemove(final Class<?> clazz, final Object entityIdentity,
			final boolean cleanup) {

		final String className = clazz.getName();

		this.persistedMap.put(className, entityIdentity);
		this.noRemovedMap.put(className, entityIdentity);

		if (!cleanup) {
			this.noCleanupMap.put(className, entityIdentity);
		}

		return this;
	}

	/**
	 * @param clazz
	 * @param entityIdentity
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeNone(final Class<?> clazz, final Object entityIdentity) {

		final String className = clazz.getName();

		this.noPersistedMap.put(className, entityIdentity);
		this.noRemovedMap.put(className, entityIdentity);

		return this;
	}

	/**
	 * @param clazz
	 * @param entityIdentity
	 * @param isTrue
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadePersist(final Class<?> clazz, final Object entityIdentity, final boolean isTrue) {

		final String className = clazz.getName();

		if (isTrue) {
			this.persistedMap.put(className, entityIdentity);
		} else {
			this.noPersistedMap.put(className, entityIdentity);
		}

		return this;
	}

	/**
	 * @param clazz
	 * @param entityIdentity
	 * @param isTrue
	 * @return {@link JstAssertJpaPO}
	 */
	public JstAssertJpaPO withCascadeRemove(final Class<?> clazz, final Object entityIdentity, final boolean isTrue) {

		final String className = clazz.getName();

		if (isTrue) {
			this.removedMap.put(className, entityIdentity);
		} else {
			this.noRemovedMap.put(className, entityIdentity);
		}

		return this;
	}
}
