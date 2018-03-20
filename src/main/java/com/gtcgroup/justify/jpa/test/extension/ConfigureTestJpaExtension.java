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
package com.gtcgroup.justify.jpa.test.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.gtcgroup.justify.core.helper.JstReflectionUtilHelper;
import com.gtcgroup.justify.core.test.extension.JstBaseExtension;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.po.JstEntityManagerFactoryPropertyPO;
import com.gtcgroup.justify.jpa.po.JstTransactionPO;
import com.gtcgroup.justify.jpa.rm.JstTransactionRM;
import com.gtcgroup.justify.jpa.test.populator.JstBaseDataPopulator;

/**
 * This {@link Extension} class initializes JPA.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v8.5
 */
class ConfigureTestJpaExtension extends JstBaseExtension implements BeforeAllCallback {

	protected static Map<String, Map<String, Object>> jpaStore = new ConcurrentHashMap<>();

	protected String persistenceUnitName;

	protected Class<? extends JstBaseDataPopulator>[] annotationDefinedDataPopulators;

	protected Class<? extends JstEntityManagerFactoryPropertyPO> entityManagerFactoryPropertyClass;

	protected final List<String> dataPopulatorNameList = new ArrayList<>();

	protected final List<Object> createList = new ArrayList<>();

	@Override
	public void beforeAll(final ExtensionContext extensionContext) throws Exception {

		try {

			initializePropertiesFromAnnotation(extensionContext);

			registerPopulatorsToBeProcessed();

			for (final String dataPopulatorName : this.dataPopulatorNameList) {

				final Optional<Class<?>> dataPopulatorClass = JstReflectionUtilHelper.retrieveClass(dataPopulatorName);

				if (dataPopulatorClass.isPresent()) {
					populateInsertionList(dataPopulatorClass.get());
				}
			}

			JstTransactionRM.commitListInOneTransaction(JstTransactionPO
					.withPersistenceUnitName(this.persistenceUnitName).withCreateAndUpdateList(this.createList));

		} catch (

		final RuntimeException e) {
			handleBeforeAllException(extensionContext, e); // Covered.
		}
	}

	@Override
	protected void initializePropertiesFromAnnotation(final ExtensionContext extensionContext) {

		final JstConfigureTestJPA configureJPA = (JstConfigureTestJPA) retrieveAnnotation(
				extensionContext.getRequiredTestClass(), JstConfigureTestJPA.class);

		this.persistenceUnitName = configureJPA.persistenceUnitName();

		this.annotationDefinedDataPopulators = configureJPA.dataPopulators();

		this.entityManagerFactoryPropertyClass = configureJPA.entityManagerFactoryPropertyClass();

		JstEntityManagerFactoryCacheHelper.startupJPA(this.persistenceUnitName, this.entityManagerFactoryPropertyClass);
	}

	protected void populateInsertionList(final Class<?> populatorClass) {

		@SuppressWarnings("unchecked")
		final Optional<JstBaseDataPopulator> dataPopulator = (Optional<JstBaseDataPopulator>) JstReflectionUtilHelper
				.instantiateInstanceUsingPublicConstructorWithNoArgument(populatorClass);

		if (dataPopulator.isPresent()) {

			this.createList.addAll(dataPopulator.get().populateCreateListTM(this.persistenceUnitName));
		}
	}

	protected void registerPopulatorsToBeProcessed() {

		final String key = JstEntityManagerFactoryCacheHelper.retrievePersistenceKey(this.persistenceUnitName,
				this.entityManagerFactoryPropertyClass);

		Map<String, Object> dataPopulatorClassMap = new HashMap<>();

		if (jpaStore.containsKey(key)) {
			dataPopulatorClassMap = jpaStore.get(key);
		}

		for (final Class<?> dataPopulatorClass : this.annotationDefinedDataPopulators) {

			// Has the populator been processed?
			final Object object = dataPopulatorClassMap.get(dataPopulatorClass.getName());

			if (null == object) {

				this.dataPopulatorNameList.add(dataPopulatorClass.getName());
				dataPopulatorClassMap.put(dataPopulatorClass.getName(), dataPopulatorClass.getSimpleName());
			}
		}

		jpaStore.replace(key, dataPopulatorClassMap);
	}

}