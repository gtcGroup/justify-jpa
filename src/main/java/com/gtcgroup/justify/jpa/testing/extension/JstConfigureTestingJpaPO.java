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
package com.gtcgroup.justify.jpa.testing.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.gtcgroup.justify.core.testing.extension.JstBaseExtension;
import com.gtcgroup.justify.jpa.testing.populator.JstBaseTestingPopulator;

/**
 * This Parameter Object class supports configuring an
 * {@link EntityManagerFactory}.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since 8.5
 */
public abstract class JstConfigureTestingJpaPO {

	private String persistenceUnitName;

	private final List<Class<? extends JstBaseTestingPopulator>> dataPopulatorList = new ArrayList<>();

	private final Map<String, Object> entityManagerFactoryPropertyMap = new ConcurrentHashMap<>();

	private boolean isConnectionString = false;

	private boolean isFirstInvocation = true;

	public List<Class<? extends JstBaseTestingPopulator>> getDataPopulatorList() {
		populateCreateListTM(this.dataPopulatorList);
		return this.dataPopulatorList;
	}

	public Map<String, Object> getEntityManagerFactoryPropertyMap() {
		populateEntityManagerFactoryPropertiesTM(this.entityManagerFactoryPropertyMap);
		// If the Property File contains an connection string.
		if (this.entityManagerFactoryPropertyMap.containsKey(PersistenceUnitProperties.JDBC_URL)) {
			this.isConnectionString = true;
		}

		return this.entityManagerFactoryPropertyMap;
	}

	public String getPersistenceUnitName() {

		this.persistenceUnitName = definePersistenceUnitNameTM();
		return this.persistenceUnitName;
	}

	boolean isConnectionString() {
		return this.isConnectionString;
	}

	/**
	 * This method changes the value upon the first invocation. It is intended for
	 * use by a subclass of {@link JstBaseExtension}.
	 *
	 * @return
	 */
	boolean isFirstInvocation() {

		boolean returnValue = false;

		if (this.isFirstInvocation) {

			returnValue = true;
			this.isFirstInvocation = false;
		}
		return returnValue;
	}

	protected abstract String definePersistenceUnitNameTM();

	protected abstract void populateCreateListTM(
			final List<Class<? extends JstBaseTestingPopulator>> dataPopulatorList);

	protected abstract void populateEntityManagerFactoryPropertiesTM(
			Map<String, Object> entityManagerFactoryPropertyMap);
}
