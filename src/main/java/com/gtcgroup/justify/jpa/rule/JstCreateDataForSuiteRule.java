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
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.gtcgroup.justify.core.base.JstBaseForSuiteRule;
import com.gtcgroup.justify.core.exception.internal.TestingConstructorRuleException;
import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;
import com.gtcgroup.justify.core.helper.internal.ReflectionUtilHelper;
import com.gtcgroup.justify.core.pattern.palette.internal.BaseRule;
import com.gtcgroup.justify.core.si.JstUniqueForSuiteRuleSI;
import com.gtcgroup.justify.jpa.helper.EntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.JstBaseCreateForSuiteBeanHelper;
import com.gtcgroup.justify.jpa.rm.QueryRM;
import com.gtcgroup.justify.jpa.rm.TransactionRM;

/**
 * This Rule class initializes persistence.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public class JstCreateDataForSuiteRule extends JstBaseForSuiteRule {

	/**
	 *
	 * 
	 * @param <RULE>
	 * @param persistenceUnitName
	 * @param createBeanHelperList
	 * @return {@link JstConfigureJpaForSuiteRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends JstCreateDataForSuiteRule> RULE withCreateBeanHelper(
			final String persistenceUnitName, final Class<?>... createBeanHelperList) {

		return (RULE) new JstCreateDataForSuiteRule(persistenceUnitName, createBeanHelperList);
	}

	/**
	 * @param <RULE>
	 * @param persistenceUnitName
	 * @param propertyOverrideMap
	 * @param createBeanHelperList
	 * @return {@link JstConfigureJpaForSuiteRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends JstCreateDataForSuiteRule> RULE withCreateBeanHelper(
			final String persistenceUnitName, final Map<String, Object> propertyOverrideMap,
			final Class<?>... createBeanHelperList) {

		return (RULE) new JstCreateDataForSuiteRule(persistenceUnitName, propertyOverrideMap,
				createBeanHelperList);
	}

	protected final List<JstUniqueForSuiteRuleSI> createBeanHelperList;

	private final String persistenceUnitName;

	private final Map<String, Object> propertyOverrideMap;

	/**
	 * Constructor - protected
	 *
	 * @param persistenceUnitName
	 * @param createBeanHelperList
	 */
	protected JstCreateDataForSuiteRule(final String persistenceUnitName, final Class<?>... createBeanHelperList) {

		this(persistenceUnitName, null, createBeanHelperList);
	}

	/**
	 * Constructor - protected
	 *
	 * @param persistenceUnitName
	 * @param propertyOverrideMap
	 * @param createBeanHelperList
	 */
	protected JstCreateDataForSuiteRule(final String persistenceUnitName, final Map<String, Object> propertyOverrideMap,
			final Class<?>... createBeanHelperList) {

		super();

		if (0 == createBeanHelperList.length) {
			throw new TestingRuntimeException("This rule requires specifying at least one class that loads data.");
		}

		this.persistenceUnitName = persistenceUnitName;

		this.propertyOverrideMap = propertyOverrideMap;

		final List<JstUniqueForSuiteRuleSI> createListTemp = new ArrayList<JstUniqueForSuiteRuleSI>();

		for (final Class<?> clazz : createBeanHelperList) {

			if (JstBaseCreateForSuiteBeanHelper.class.isAssignableFrom(clazz)) {

				createListTemp.add((JstBaseCreateForSuiteBeanHelper) ReflectionUtilHelper
						.instantiatePublicConstructorNoArgument(clazz));
			} else {

				throw new TestingConstructorRuleException("\nThe class [" + clazz.getSimpleName()
				+ "] does not appear to extend a base class for creating persistence test data.\n");
			}
		}

		this.createBeanHelperList = createListTemp;
	}

	/**
	 * @see BaseRule#beforeTM()
	 */
	@Override
	public void beforeTM() {

		for (final JstUniqueForSuiteRuleSI createBeanHelper : this.createBeanHelperList) {
			processCreateBeanHelperAsTransaction(createBeanHelper);
		}
	}

	/**
	 * @param createBeanHelper
	 */
	protected void processCreateBeanHelperAsTransaction(final JstUniqueForSuiteRuleSI createBeanHelper) {

		EntityManager entityManager = null;

		try {
			entityManager = EntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(this.persistenceUnitName,
					this.propertyOverrideMap);

			final List<Object> createList = ((JstBaseCreateForSuiteBeanHelper) createBeanHelper)
					.populateCreateListTM(new QueryRM().withEntityManager(entityManager));

			TransactionRM.withEntityManager(entityManager).transactCreateOrUpdateFromList(createList);

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
	}

	/**
	 * @see JstUniqueForSuiteRuleSI#uniqueSuiteIdentityTM()
	 */
	@Override
	public String uniqueSuiteIdentityTM() {

		final StringBuilder uniqueIdentity = new StringBuilder();

		for (final JstUniqueForSuiteRuleSI createBeanHelper : this.createBeanHelperList) {
			uniqueIdentity.append(createBeanHelper.uniqueSuiteIdentityTM());
		}
		return uniqueIdentity.toString();
	}
}