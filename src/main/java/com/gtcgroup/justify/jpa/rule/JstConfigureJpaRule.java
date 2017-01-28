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
import com.gtcgroup.justify.core.rule.JstConfigureUserIdRule;
import com.gtcgroup.justify.jpa.helper.ConstantstJPA;
import com.gtcgroup.justify.jpa.helper.JstBaseDataPopulator;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.JstPersistenceDotXmlCacheHelper;
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
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	protected static <RULE extends TestRule, SUBCLASS extends JstConfigureUserIdRule> RULE decorateSubClassInstance(
			final SUBCLASS subClassInstance) {

		return (RULE) subClassInstance;
	}

	private static String formatCacheKey(final String entityManagerFactoryKey, final String dataPopulatorName) {

		return entityManagerFactoryKey + ConstantstJPA.KEY_DELIMITER + dataPopulatorName;

	}

	/**
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends JstConfigureJpaRule> RULE withPersistenceUnitName(final String persistenceUnitName) {

		return (RULE) new JstConfigureJpaRule(persistenceUnitName);
	}

	protected final String persistenceUnitName;

	protected Map<String, Object> persistencePropertyMapOrNull = null;

	protected EntityManagerFactory entityManagerFactory;

	protected boolean dataPopulatorSubmitted = false;

	/**
	 * Constructor - protected
	 */
	protected JstConfigureJpaRule(final String persistenceUnitName) {

		super();

		this.persistenceUnitName = persistenceUnitName;
		JstPersistenceDotXmlCacheHelper.loadPersistenceProperties();
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

				final Class<?> populatorClass = entry.getValue();

				processDataPopulator(populatorClass, entry.getKey());

				JstConfigureJpaRule.DATA_POPULATOR_ALREADY_PROCESSED_LIST.add(populatorClass);
			}
		}
		JstConfigureJpaRule.DATA_POPULATOR_TO_BE_PROCESSED_MAP.clear();
	}

	protected void processDataPopulator(final Class<?> populatorClass, final String cacheKey) {

		EntityManager entityManager = null;

		try {

			final JstBaseDataPopulator dataPopulator = (JstBaseDataPopulator) ReflectionUtilHelper
					.instantiatePublicConstructorNoArgument(populatorClass);

			final List<Object> createList = dataPopulator.populateCreateListTM(this.persistenceUnitName);

			entityManager = JstConfigureJpaRule.ENTITY_MANAGER_FACTORY_MAP.get(cacheKey).createEntityManager();

			JstTransactionJpaRM.transactEntities(JstTransactionJpaPO.withException().withEntityManager(entityManager)
					.withCreateAndUpdateList(createList));

		} catch (final Exception e) {

			throw new JustifyRuntimeException(e);
		} finally {

			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
	}

	/**
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public <RULE extends JstConfigureJpaRule> RULE withDataPopulators(final Class<?>... dataPopulators) {

		this.dataPopulatorSubmitted = true;

		if (0 != dataPopulators.length) {

			for (final Class<?> dataPopulator : dataPopulators) {

				final String entityManagerFactoryKey = JstEntityManagerFactoryCacheHelper
						.createEntityManagerFactory(this.persistenceUnitName, this.persistencePropertyMapOrNull);

				if (!JstConfigureJpaRule.DATA_POPULATOR_ALREADY_PROCESSED_LIST.contains(dataPopulator)) {

					if (JstBaseDataPopulator.class.isAssignableFrom(dataPopulator)) {

						JstConfigureJpaRule.ENTITY_MANAGER_FACTORY_MAP.put(
								formatCacheKey(entityManagerFactoryKey, dataPopulator.getName()),
								JstEntityManagerFactoryCacheHelper
										.retrieveEntityManagerFactory(entityManagerFactoryKey));
					} else {

						throw new TestingConstructorRuleException("\nThe class [" + dataPopulator.getSimpleName()
								+ "] does not appear to extend a base class for populating persistence test data.\n");
					}
					JstConfigureJpaRule.DATA_POPULATOR_TO_BE_PROCESSED_MAP.put(
							entityManagerFactoryKey + ConstantstJPA.KEY_DELIMITER + dataPopulator.getName(),
							dataPopulator);
				}
			}
		}
		return (RULE) this;
	}

	/**
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public <RULE extends JstConfigureJpaRule> RULE withPersistencePropertyMap(
			final Map<String, Object> persistencePropertyMap) {

		if (this.dataPopulatorSubmitted) {

			throw new JustifyRuntimeException(
					"A persistence property map, if used, must be defined PRIOR to populator processing.");
		}

		this.persistencePropertyMapOrNull = persistencePropertyMap;
		return (RULE) this;
	}
}