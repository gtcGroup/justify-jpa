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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.gtcgroup.justify.core.helper.JstReflectionUtilHelper;
import com.gtcgroup.justify.core.testing.extension.JstBaseExtension;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerCacheHelper;
import com.gtcgroup.justify.jpa.po.JstTransactionPO;
import com.gtcgroup.justify.jpa.rm.JstTransactionRM;
import com.gtcgroup.justify.jpa.testing.populator.JstBaseDataPopulator;

/**
 * This {@link Extension} class initializes JPA.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since 8.5
 */
class ConfigureTestJpaExtension extends JstBaseExtension implements BeforeAllCallback {

	protected static Map<String, List<String>> entityManagerFactoryMap = new ConcurrentHashMap<>();

	protected static List<String> loadDataPopulatorClassNameList(final JstConfigureTestJpaPO configureTestJpaPO) {

		List<String> currentDataPopulatorClassNameList;

		final List<String> newDataPopulatorList = new ArrayList<>();

		if (entityManagerFactoryMap.containsKey(configureTestJpaPO.getPersistenceUnitName())) {
			currentDataPopulatorClassNameList = entityManagerFactoryMap
					.get(configureTestJpaPO.getPersistenceUnitName());
		} else {
			currentDataPopulatorClassNameList = new ArrayList<>();
		}

		for (final Class<?> dataPopulatorClass : configureTestJpaPO.getDataPopulatorList()) {

			if (configureTestJpaPO.isConnectionString()) {

				newDataPopulatorList.add(dataPopulatorClass.getName());

			} else if (!currentDataPopulatorClassNameList.contains(dataPopulatorClass.getName())) {

				newDataPopulatorList.add(dataPopulatorClass.getName());
			}
		}
		return newDataPopulatorList;
	}

	@SuppressWarnings("unchecked")
	private static <ENTITY> List<ENTITY> loadEntityCreateList(final List<String> dataPopulatorList,
			final String persistenceUnitName) {

		final List<ENTITY> createEntityList = new ArrayList<>();

		for (final String dataPopulatorName : dataPopulatorList) {

			final Optional<Class<?>> dataPopulatorClass = JstReflectionUtilHelper.retrieveClass(dataPopulatorName);

			if (dataPopulatorClass.isPresent()) {

				final Optional<JstBaseDataPopulator> dataPopulator = (Optional<JstBaseDataPopulator>) JstReflectionUtilHelper
						.instantiateInstanceUsingPublicConstructorWithNoArgument(dataPopulatorClass.get());

				if (dataPopulator.isPresent()) {

					final List<ENTITY> entityList = (List<ENTITY>) dataPopulator.get()
							.populateCreateListTM(persistenceUnitName);

					createEntityList.addAll(entityList);
				}
			}
		}
		return createEntityList;
	}

	private static void updateDataPopulatorClassMap(final String persistenceUnitName,
			final List<String> dataPopulatorClassNameList) {

		if (entityManagerFactoryMap.containsKey(persistenceUnitName)) {

			final List<String> dataPopulatorList = entityManagerFactoryMap.get(persistenceUnitName);

			for (final String dataPopulatorClassName : dataPopulatorClassNameList) {

				dataPopulatorList.add(dataPopulatorClassName);

			}

			entityManagerFactoryMap.replace(persistenceUnitName, dataPopulatorList);

		} else {

			final List<String> dataPopulatorList = new ArrayList<>();

			for (final String dataPopulatorClassName : dataPopulatorClassNameList) {

				dataPopulatorList.add(dataPopulatorClassName);

			}

			entityManagerFactoryMap.put(persistenceUnitName, dataPopulatorList);
		}
	}

	@Override
	public void beforeAll(final ExtensionContext extensionContext) throws Exception {

		try {

			final Class<? extends JstConfigureTestJpaPO> configureTestJpaClassPO = initializePropertiesFromAnnotation(
					extensionContext);

			final Optional<JstConfigureTestJpaPO> optionalConfigureTestJpaPO = JstEntityManagerCacheHelper
					.initializeEntityManagerFactory(configureTestJpaClassPO);

			if (optionalConfigureTestJpaPO.isPresent()) {

				final JstConfigureTestJpaPO configureTestJpaPO = optionalConfigureTestJpaPO.get();

				if (configureTestJpaPO.isFirstInvocation()) {

					final List<String> newDataPopulatorList = loadDataPopulatorClassNameList(configureTestJpaPO);

					final List<String> createList = loadEntityCreateList(newDataPopulatorList,
							configureTestJpaPO.getPersistenceUnitName());

					JstTransactionRM.commitListInOneTransaction(
							JstTransactionPO.withPersistenceUnitName(configureTestJpaPO.getPersistenceUnitName())
									.withCreateAndUpdateList(createList));

					updateDataPopulatorClassMap(configureTestJpaPO.getPersistenceUnitName(), newDataPopulatorList);
				}
			}

		} catch (

		final RuntimeException e) {
			handleBeforeAllException(extensionContext, e); // Covered.
		}
	}

	@Override
	protected Class<? extends JstConfigureTestJpaPO> initializePropertiesFromAnnotation(
			final ExtensionContext extensionContext) {

		final JstConfigureTestJPA configureJPA = (JstConfigureTestJPA) retrieveAnnotation(
				extensionContext.getRequiredTestClass(), JstConfigureTestJPA.class);

		return configureJPA.configureTestJpaPO();
	}
}