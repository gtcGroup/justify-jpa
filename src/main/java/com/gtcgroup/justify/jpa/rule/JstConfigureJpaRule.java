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
package com.gtcgroup.justify.jpa.rule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Rule;
import org.junit.rules.TestRule;

import com.gtcgroup.justify.core.base.JstBaseRule;
import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;
import com.gtcgroup.justify.core.exception.internal.TestingConstructorRuleException;
import com.gtcgroup.justify.core.helper.internal.ReflectionUtilHelper;
import com.gtcgroup.justify.jpa.helper.JstBaseDataPopulator;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.po.JstTransactionJpaPO;
import com.gtcgroup.justify.jpa.rm.JstTransactionJpaRM;

/**
 * This {@link Rule} class initializes persistence.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public class JstConfigureJpaRule extends JstBaseRule {

	protected static List<Class<?>> DATA_POPULATOR_ALREADY_PROCESSED_LIST = new ArrayList<Class<?>>();

	private static Map<String, Class<?>> DATA_POPULATOR_TO_BE_PROCESSED_MAP = new LinkedHashMap<String, Class<?>>();

	private static Map<String, EntityManagerFactory> ENTITY_MANAGER_FACTORY_MAP = new LinkedHashMap<String, EntityManagerFactory>();

	/**
	 * This method is available for extension.
	 *
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	protected static <RULE extends TestRule> RULE createRule(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMapOrNull, final Class<?>... dataPopulators) {

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, persistencePropertyMapOrNull, dataPopulators);
	}

	/**
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends TestRule> RULE withDataPopulator(final String persistenceUnitName,
			final Class<?>... dataPopulators) {

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, null, dataPopulators);
	}

	/**
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends TestRule> RULE withDataPopulator(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMap, final Class<?>... dataPopulators) {

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, persistencePropertyMap, dataPopulators);
	}

	/**
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends TestRule> RULE withPersistencePropertyMap(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMap) {

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, persistencePropertyMap);
	}

	/**
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends TestRule> RULE withPersistenceUnitName(final String persistenceUnitName) {

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, null);
	}

	protected final String persistenceUnitName;

	protected EntityManagerFactory entityManagerFactory;

	/**
	 * Constructor - protected
	 */
	protected JstConfigureJpaRule(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMapOrNull, final Class<?>... dataPopulators) {

		super();

		this.persistenceUnitName = persistenceUnitName;

		if (0 != dataPopulators.length) {

			for (final Class<?> dataPopulator : dataPopulators) {

				final String entityManagerFactoryKey = JstEntityManagerFactoryCacheHelper
						.createEntityManagerFactory(persistenceUnitName, persistencePropertyMapOrNull);

				if (!JstConfigureJpaRule.DATA_POPULATOR_ALREADY_PROCESSED_LIST.contains(dataPopulator)) {

					if (JstBaseDataPopulator.class.isAssignableFrom(dataPopulator)) {

						JstConfigureJpaRule.ENTITY_MANAGER_FACTORY_MAP.put(dataPopulator.getName(),
								JstEntityManagerFactoryCacheHelper
										.retrieveEntityManagerFactory(entityManagerFactoryKey));
					} else {

						throw new TestingConstructorRuleException("\nThe class [" + dataPopulator.getSimpleName()
								+ "] does not appear to extend a base class for populating persistence test data.\n");
					}
					JstConfigureJpaRule.DATA_POPULATOR_TO_BE_PROCESSED_MAP.put(entityManagerFactoryKey, dataPopulator);
				}
			}
		}
	}

	/**
	 * @see JstBaseRule#afterTM()
	 */
	@Override
	public void afterTM() throws Throwable {

		return;
	}

	/**
	 * @see JstBaseRule#beforeTM()
	 */
	@Override
	public void beforeTM() {

		if (0 != JstConfigureJpaRule.DATA_POPULATOR_TO_BE_PROCESSED_MAP.size()) {

			for (final Entry<String, Class<?>> entry : JstConfigureJpaRule.DATA_POPULATOR_TO_BE_PROCESSED_MAP
					.entrySet()) {

				processDataPopulator(entry.getValue());

				JstConfigureJpaRule.DATA_POPULATOR_TO_BE_PROCESSED_MAP.remove(entry.getKey());

				JstConfigureJpaRule.DATA_POPULATOR_ALREADY_PROCESSED_LIST.add(entry.getValue());
			}
		}
	}

	protected void processDataPopulator(final Class<?> populatorClass) {

		EntityManager entityManager = null;

		try {

			final JstBaseDataPopulator dataPopulator = (JstBaseDataPopulator) ReflectionUtilHelper
					.instantiatePublicConstructorNoArgument(populatorClass);

			final List<Object> createList = dataPopulator.populateCreateListTM(this.persistenceUnitName);

			entityManager = JstConfigureJpaRule.ENTITY_MANAGER_FACTORY_MAP.get(populatorClass.getName())
					.createEntityManager();

			JstTransactionJpaRM.transactEntities(JstTransactionJpaPO.withException().withEntityManager(entityManager)
					.withCreateAndUpdateList(createList));

		} catch (final Exception e) {

			throw new JustifyRuntimeException(e);
		} finally {

			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
	}
}