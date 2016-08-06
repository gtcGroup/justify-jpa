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
public class JstAssertionsJpaCascadePO {

	/**
	 * This method initializes the class.
	 *
	 * @param persistenceUnitName
	 * @param populatedEntity
	 * @return {@link JstAssertionsJpaCascadePO}
	 */
	public static JstAssertionsJpaCascadePO withPopulatedEntityIncludingIdentities(final String persistenceUnitName,
			final Object populatedEntity) {

		return new JstAssertionsJpaCascadePO(persistenceUnitName, populatedEntity);
	}

	private Object populatedEntity;

	private String persistenceUnitName;

	private final Map<String, Object> cascadePersistMap = new HashMap<String, Object>();

	private final Map<String, Object> cascadeRemoveMap = new HashMap<String, Object>();

	private final Map<String, Object> cascadePersistNotMap = new HashMap<String, Object>();

	private final Map<String, Object> cascadeRemoveNotMap = new HashMap<String, Object>();

	/**
	 * Constructor
	 *
	 * @param persistenceUnitName
	 * @param populatedEntity
	 */
	protected JstAssertionsJpaCascadePO(final String persistenceUnitName, final Object populatedEntity) {

		super();

		this.persistenceUnitName = persistenceUnitName;
		this.populatedEntity = populatedEntity;

		return;
	}

	/**
	 * @param className
	 * @param entityIdentity
	 * @param isTrue
	 * @return {@link JstAssertionsJpaCascadePO}
	 */
	public JstAssertionsJpaCascadePO withCascadePersist(final String className, final Object entityIdentity,
			final boolean isTrue) {

		if (isTrue) {
			this.cascadePersistMap.put(className, entityIdentity);
		} else {
			this.cascadePersistNotMap.put(className, entityIdentity);
		}

		return this;
	}

	/**
	 * @param className
	 * @param entityIdentity
	 * @param isTrue
	 * @return {@link JstAssertionsJpaCascadePO}
	 */
	public JstAssertionsJpaCascadePO withCascadeRemove(final String className, final Object entityIdentity,
			final boolean isTrue) {

		if (isTrue) {
			this.cascadeRemoveMap.put(className, entityIdentity);
		} else {
			this.cascadeRemoveNotMap.put(className, entityIdentity);
		}

		return this;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getCascadePersistMap() {

		return this.cascadePersistMap;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getCascadePersistMapFalse() {
		return getCascadePersistNotMap();
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getCascadePersistNotMap() {

		return this.cascadePersistNotMap;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getCascadeRemoveMap() {

		return this.cascadeRemoveMap;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getCascadeRemoveMapFalse() {
		return getCascadeRemoveNotMap();
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	Map<String, Object> getCascadeRemoveNotMap() {

		return this.cascadeRemoveNotMap;
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
	 * @param entityIdentity
	 * @return {@link JstAssertionsJpaCascadePO}
	 */
	public JstAssertionsJpaCascadePO withCascadeAll(final Class<?> clazz, final Object entityIdentity) {

		final String className = clazz.getName();

		this.cascadePersistMap.put(className, entityIdentity);
		this.cascadeRemoveMap.put(className, entityIdentity);

		return this;
	}

	/**
	 * @param clazz
	 * @param entityIdentity
	 * @return {@link JstAssertionsJpaCascadePO}
	 */
	public JstAssertionsJpaCascadePO withCascadeAllExceptRemove(final Class<?> clazz, final Object entityIdentity) {

		final String className = clazz.getName();

		this.cascadePersistMap.put(className, entityIdentity);
		this.cascadeRemoveNotMap.put(className, entityIdentity);

		return this;
	}

	/**
	 * @param className
	 * @param entityIdentity
	 * @return {@link JstAssertionsJpaCascadePO}
	 */
	public JstAssertionsJpaCascadePO withCascadeNone(final String className, final Object entityIdentity) {

		this.cascadePersistNotMap.put(className, entityIdentity);
		this.cascadeRemoveNotMap.put(className, entityIdentity);

		return this;
	}
}
