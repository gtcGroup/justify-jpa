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
import com.gtcgroup.justify.jpa.helper.EntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.JstBasePopulateDataBeanHelper;
import com.gtcgroup.justify.jpa.rm.QueryRM;
import com.gtcgroup.justify.jpa.rm.TransactionRM;

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

	protected static List<String> createBeanHelperProcessedList = new ArrayList<String>();

	private static void initializeJPA(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMap) {

		final EntityManager entityManager = EntityManagerFactoryCacheHelper
				.createEntityManagerToBeClosed(persistenceUnitName, persistencePropertyMap);
		entityManager.setProperty(null, "toForceCompletingConfiguration");
		EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);

		return;
	}

	/**
	 * @param <RULE>
	 * @param persistenceUnitName
	 * @param persistencePropertyMap
	 * @param createBeanHelpers
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	protected static <RULE extends TestRule> RULE processRule(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMap,
			final Map<String, EntityManagerFactory> entityManagerFactoryMap, final Class<?>... createBeanHelpers) {

		JstConfigureJpaRule.ENTITY_MANAGER_FACTORY_MAP = entityManagerFactoryMap;

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, persistencePropertyMap, createBeanHelpers);
	}

	/**
	 * @param <RULE>
	 * @param persistenceUnitName
	 * @param createBeanHelpers
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends TestRule> RULE withDataLoad(final String persistenceUnitName,
			final Class<?>... createBeanHelpers) {

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, null, createBeanHelpers);
	}

	/**
	 * @param <RULE>
	 * @param persistenceUnitName
	 * @param persistencePropertyMap
	 * @param createBeanHelpers
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends TestRule> RULE withDataLoad(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMap, final Class<?>... createBeanHelpers) {

		return (RULE) new JstConfigureJpaRule(persistenceUnitName, persistencePropertyMap, createBeanHelpers);
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

	private final Map<String, JstBasePopulateDataBeanHelper> createBeanHelperToBeProcessedMap = new LinkedHashMap<String, JstBasePopulateDataBeanHelper>();

	protected final String persistenceUnitName;

	protected final Map<String, Object> persistencePropertyMap;

	/**
	 * Constructor - protected
	 *
	 * @param persistenceUnitName
	 * @param persistencePropertyMap
	 * @param createBeanHelpers
	 */
	protected JstConfigureJpaRule(final String persistenceUnitName, final Map<String, Object> persistencePropertyMap,
			final Class<?>... createBeanHelpers) {

		super();

		this.persistenceUnitName = persistenceUnitName;

		this.persistencePropertyMap = persistencePropertyMap;

		if (0 != createBeanHelpers.length) {

			for (final Class<?> clazz : createBeanHelpers) {

				final String key = EntityManagerFactoryCacheHelper.calculateKey(persistenceUnitName,
						persistencePropertyMap) + " @ " + clazz.getName();

				if (JstConfigureJpaRule.createBeanHelperProcessedList.contains(key)) {
					break;
				}

				if (JstBasePopulateDataBeanHelper.class.isAssignableFrom(clazz)) {

					this.createBeanHelperToBeProcessedMap.put(key, (JstBasePopulateDataBeanHelper) ReflectionUtilHelper
							.instantiatePublicConstructorNoArgument(clazz));
				} else {

					throw new TestingConstructorRuleException("\nThe class [" + clazz.getSimpleName()
					+ "] does not appear to extend a base class for creating persistence test data.\n");
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

		final EntityManagerFactory entityManagerFactory = EntityManagerFactoryCacheHelper
				.createEntityManagerFactory(this.persistenceUnitName, this.persistencePropertyMap);

		if (null != JstConfigureJpaRule.ENTITY_MANAGER_FACTORY_MAP) {

			initializeJPA(this.persistenceUnitName, this.persistencePropertyMap);
			JstConfigureJpaRule.ENTITY_MANAGER_FACTORY_MAP.put(this.persistenceUnitName, entityManagerFactory);
		}

		if (0 != this.createBeanHelperToBeProcessedMap.size()) {

			for (final Map.Entry<String, JstBasePopulateDataBeanHelper> entry : this.createBeanHelperToBeProcessedMap
					.entrySet()) {

				processCreateBeanHelper(entry.getValue());

				JstConfigureJpaRule.createBeanHelperProcessedList.add(entry.getKey());
			}
		}
	}

	/**
	 * @param createBeanHelper
	 */
	protected void processCreateBeanHelper(final JstBasePopulateDataBeanHelper createBeanHelper) {

		EntityManager entityManager = null;

		try {
			entityManager = EntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(this.persistenceUnitName,
					this.persistencePropertyMap);

			final List<Object> createList = createBeanHelper
					.populateCreateListTM(new QueryRM().withEntityManager(entityManager));

			new TransactionRM().withEntityManager(entityManager).transactCreateOrUpdateFromList(createList);

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
	}
}