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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Rule;
import org.junit.rules.TestRule;

import com.gtcgroup.justify.core.base.JstBaseTestingRule;
import com.gtcgroup.justify.core.exception.internal.TestingConstructorRuleException;
import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;
import com.gtcgroup.justify.core.helper.internal.ReflectionUtilHelper;
import com.gtcgroup.justify.jpa.helper.JstBaseDataPopulator;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.rm.JstQueryJpaRM;
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
public class JstConfigureJpaRule extends JstBaseTestingRule {

	protected static Map<String, EntityManagerFactory> ENTITY_MANAGER_FACTORY_MAP;

	protected static List<String> dataPopulatorProcessedList = new ArrayList<String>();

	private static void initializeJPA(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMap) {

		final EntityManager entityManager = JstEntityManagerFactoryCacheHelper
				.createEntityManagerToBeClosed(persistenceUnitName, persistencePropertyMap);
		entityManager.setProperty(null, "toForceCompletingConfiguration");
		JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);

		return;
	}

	/**
	 * @param <RULE>
	 * @param persistenceUnitName
	 * @param persistencePropertyMap
	 * @param dataPopulators
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	protected static <RULE extends TestRule> RULE processRule(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMap,
			final Map<String, EntityManagerFactory> entityManagerFactoryMap, final Class<?>... dataPopulators) {

		JstConfigureJpaRule.ENTITY_MANAGER_FACTORY_MAP = entityManagerFactoryMap;

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, persistencePropertyMap, dataPopulators);
	}

	/**
	 * @param <RULE>
	 * @param persistenceUnitName
	 * @param dataPopulators
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends TestRule> RULE withDataPopulator(final String persistenceUnitName,
			final Class<?>... dataPopulators) {

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, null, dataPopulators);
	}

	/**
	 * @param <RULE>
	 * @param persistenceUnitName
	 * @param persistencePropertyMap
	 * @param dataPopulators
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends TestRule> RULE withDataPopulator(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMap, final Class<?>... dataPopulators) {

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, persistencePropertyMap, dataPopulators);
	}

	/**
	 * @param <RULE>
	 * @param persistenceUnitName
	 * @param persistencePropertyMap
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends TestRule> RULE withPersistencePropertyMap(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMap) {

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, persistencePropertyMap);
	}

	/**
	 * @param <RULE>
	 * @param persistenceUnitName
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends TestRule> RULE withPersistenceUnitName(final String persistenceUnitName) {

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, null);
	}

	private final Map<String, JstBaseDataPopulator> dataPopulatorToBeProcessedMap = new LinkedHashMap<String, JstBaseDataPopulator>();

	protected final String persistenceUnitName;

	protected final Map<String, Object> persistencePropertyMap;

	/**
	 * Constructor - protected
	 *
	 * @param persistenceUnitName
	 * @param persistencePropertyMap
	 * @param dataPopulators
	 */
	protected JstConfigureJpaRule(final String persistenceUnitName, final Map<String, Object> persistencePropertyMap,
			final Class<?>... dataPopulators) {

		super();

		this.persistenceUnitName = persistenceUnitName;

		this.persistencePropertyMap = persistencePropertyMap;

		if (0 != dataPopulators.length) {

			for (final Class<?> clazz : dataPopulators) {

				final String key = JstEntityManagerFactoryCacheHelper.calculateKey(persistenceUnitName,
						persistencePropertyMap) + " @ " + clazz.getName();

				if (!JstConfigureJpaRule.dataPopulatorProcessedList.contains(key)) {

					if (JstBaseDataPopulator.class.isAssignableFrom(clazz)) {

						this.dataPopulatorToBeProcessedMap.put(key, (JstBaseDataPopulator) ReflectionUtilHelper
								.instantiatePublicConstructorNoArgument(clazz));

						JstConfigureJpaRule.dataPopulatorProcessedList.add(key);

					} else {

						throw new TestingConstructorRuleException("\nThe class [" + clazz.getSimpleName()
						+ "] does not appear to extend a base class for creating persistence test data.\n");
					}
				}
			}
		}
		initializeJPA(persistenceUnitName, persistencePropertyMap);
	}

	/**
	 * @see JstBaseTestingRule#afterTM()
	 */
	@Override
	public void afterTM() throws Throwable {

		return;
	}

	/**
	 * @see JstBaseTestingRule#beforeTM()
	 */
	@Override
	public void beforeTM() {

		final EntityManagerFactory entityManagerFactory = JstEntityManagerFactoryCacheHelper
				.createEntityManagerFactory(this.persistenceUnitName, this.persistencePropertyMap);

		if (null != JstConfigureJpaRule.ENTITY_MANAGER_FACTORY_MAP) {

			initializeJPA(this.persistenceUnitName, this.persistencePropertyMap);
			JstConfigureJpaRule.ENTITY_MANAGER_FACTORY_MAP.put(this.persistenceUnitName, entityManagerFactory);
		}

		if (0 != this.dataPopulatorToBeProcessedMap.size()) {

			for (final Map.Entry<String, JstBaseDataPopulator> entry : this.dataPopulatorToBeProcessedMap
					.entrySet()) {

				processdataPopulator(entry.getValue());

				JstConfigureJpaRule.dataPopulatorProcessedList.add(entry.getKey());
			}
		}
	}

	/**
	 * @param dataPopulator
	 */
	protected void processdataPopulator(final JstBaseDataPopulator dataPopulator) {

		EntityManager entityManager = null;

		try {
			entityManager = JstEntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(this.persistenceUnitName,
					this.persistencePropertyMap);

			final List<Object> createList = dataPopulator
					.populateCreateListTM(new JstQueryJpaRM(entityManager));

			new JstTransactionJpaRM(entityManager).transactCreateOrUpdateFromList(createList);

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		} finally {

			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
	}
}