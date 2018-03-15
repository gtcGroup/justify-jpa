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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.gtcgroup.justify.core.helper.JstReflectionUtilHelper;
import com.gtcgroup.justify.core.po.JstExceptionPO;
import com.gtcgroup.justify.core.test.base.JstBaseExtension;
import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.core.test.helper.internal.AnnotationUtilHelper;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
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
public class JstConfigureTestJpaExtension extends JstBaseExtension implements BeforeAllCallback {

	private static Map<String, Object> jpaStore = new ConcurrentHashMap<>();

	protected Class<? extends JstBaseDataPopulator>[] annotationDefinedDataPopulators;

	protected String persistenceUnitName;

	protected Map<String, Object> persistencePropertyMapOrNull = null;

	private final List<String> dataPopulatorNameList = new ArrayList<>();

	private final List<Object> deInsertionList = new ArrayList<>();

	@Override
	public void beforeAll(final ExtensionContext extensionContext) throws Exception {

		initializeAnnotationValues(extensionContext);

		JstEntityManagerFactoryCacheHelper.startupJPA(this.persistenceUnitName, this.persistencePropertyMapOrNull);

		registerPopulatorsToBeProcessed();

		try {
			for (final String dataPopulatorName : this.dataPopulatorNameList) {

				final Optional<Class<?>> dataPopulatorClass = JstReflectionUtilHelper.retrieveClass(dataPopulatorName);

				if (dataPopulatorClass.isPresent()) {
					compileInsertionList(dataPopulatorClass.get());

				} else {
					throw new JustifyException(JstExceptionPO
							.withMessage("The data populator [" + dataPopulatorName + "] could not be processed.")
							.withExceptionClassName(JstConfigureTestJPA.class.getSimpleName()));
				}
			}
			this.dataPopulatorNameList.clear();
			JstTransactionRM.commitListInOneTransaction(JstTransactionPO
					.withPersistenceUnitName(this.persistenceUnitName).withCreateAndUpdateList(this.deInsertionList));
			this.deInsertionList.clear();
		} catch (final RuntimeException e) {
			handleBeforeAllException(extensionContext, e);
		}
	}

	protected void compileInsertionList(final Class<?> populatorClass) {

		@SuppressWarnings("unchecked")
		final Optional<JstBaseDataPopulator> dataPopulator = (Optional<JstBaseDataPopulator>) JstReflectionUtilHelper
				.instantiateInstanceUsingPublicConstructorWithNoArgument(populatorClass);

		if (dataPopulator.isPresent()) {

			this.deInsertionList.addAll(dataPopulator.get().populateCreateListTM(this.persistenceUnitName));
		}
	}

	protected void initializeAnnotationValues(final ExtensionContext extensionContext) {

		@SuppressWarnings("unchecked")
		final Optional<JstConfigureTestJPA> configureJPA = (Optional<JstConfigureTestJPA>) AnnotationUtilHelper
				.retrieveAnnotation(extensionContext.getTestClass(), JstConfigureTestJPA.class);

		if (configureJPA.isPresent()) {

			this.persistenceUnitName = configureJPA.get().persistenceUnitName();

			this.annotationDefinedDataPopulators = configureJPA.get().dataPopulators();
		} else {
			throw new JustifyException(JstExceptionPO.withMessage("The data populator(s) could not be processed.")
					.withExceptionClassName(JstConfigureTestJpaExtension.class.getSimpleName()));
		}
	}

	protected void registerPopulatorsToBeProcessed() {

		for (final Class<?> dataPopulatorClass : this.annotationDefinedDataPopulators) {

			final Object object = jpaStore.get(dataPopulatorClass.getName());

			if (null == object) {

				this.dataPopulatorNameList.add(dataPopulatorClass.getName());
				jpaStore.put(dataPopulatorClass.getName(), dataPopulatorClass.getSimpleName());
			}
		}
	}

}