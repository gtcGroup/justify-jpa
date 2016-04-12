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
package com.gtcgroup.justify.jpa.po;

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
public class JstCascadeTypesPO {

	private final Object entity;

	private final Map<String, Object> cascadeDetachMap = new HashMap<String, Object>();

	private final Map<String, Object> cascadeMergeMap = new HashMap<String, Object>();

	private final Map<String, Object> cascadeRefreshMap = new HashMap<String, Object>();

	private final Map<String, Object> cascadeRemoveMap = new HashMap<String, Object>();

	/**
	 * Constructor
	 *
	 * @param entity
	 */
	public JstCascadeTypesPO(final Object entity) {

		super();
		this.entity = entity;
		return;
	}

	/**
	 * @param className
	 * @param entityIdentity
	 * @return {@link JstCascadeTypesPO}
	 */
	public JstCascadeTypesPO addCascadeAll(final String className, final Object entityIdentity) {

		this.cascadeDetachMap.put(className, entityIdentity);
		this.cascadeMergeMap.put(className, entityIdentity);
		this.cascadeRefreshMap.put(className, entityIdentity);
		this.cascadeRemoveMap.put(className, entityIdentity);
		return this;
	}

	/**
	 * @param className
	 * @param entityIdentity
	 * @return {@link JstCascadeTypesPO}
	 */
	public JstCascadeTypesPO addCascadeDetach(final String className, final Object entityIdentity) {

		this.cascadeDetachMap.put(className, entityIdentity);
		return this;
	}

	/**
	 * @param className
	 * @param entityIdentity
	 * @return {@link JstCascadeTypesPO}
	 */
	public JstCascadeTypesPO addCascadeMerge(final String className, final Object entityIdentity) {

		this.cascadeMergeMap.put(className, entityIdentity);
		return this;
	}

	/**
	 * @param className
	 * @param entityIdentity
	 * @return {@link JstCascadeTypesPO}
	 */
	public JstCascadeTypesPO addCascadeRefresh(final String className, final Object entityIdentity) {

		this.cascadeRefreshMap.put(className, entityIdentity);
		return this;
	}

	/**
	 * @param className
	 * @param entityIdentity
	 * @return {@link JstCascadeTypesPO}
	 */
	public JstCascadeTypesPO addCascadeRemove(final String className, final Object entityIdentity) {

		this.cascadeRemoveMap.put(className, entityIdentity);
		return this;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	public Map<String, Object> getCascadeDetachMap() {
		return this.cascadeDetachMap;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	public Map<String, Object> getCascadeMergeMap() {
		return this.cascadeMergeMap;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	public Map<String, Object> getCascadeRefreshMap() {
		return this.cascadeRefreshMap;
	}

	/**
	 * @return Map<Object,Class<Object>>
	 */
	public Map<String, Object> getCascadeRemoveMap() {
		return this.cascadeRemoveMap;
	}

	/**
	 * @return Object
	 */
	public Object getEntity() {
		return this.entity;
	}
}
