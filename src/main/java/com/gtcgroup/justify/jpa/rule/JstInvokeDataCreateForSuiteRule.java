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
import com.gtcgroup.justify.core.si.UniqueForSuiteRuleSI;
import com.gtcgroup.justify.jpa.helper.EntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.JstBaseCreateForSuiteBeanHelper;
import com.gtcgroup.justify.jpa.helper.TransactionUtilHelper;
import com.gtcgroup.justify.jpa.rm.QueryRM;

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
public class JstInvokeDataCreateForSuiteRule extends JstBaseForSuiteRule {

	protected final List<UniqueForSuiteRuleSI> createList;

	private final String persistenceUnitName;

	private final Map<String, Object> propertyOverrideMap;

	/**
	 * Constructor
	 *
	 * @param persistenceUnitName
	 * @param createForSuiteBeanHelperClasses
	 */
	public JstInvokeDataCreateForSuiteRule(final String persistenceUnitName,
			final Class<?>... createForSuiteBeanHelperClasses) {

		this(persistenceUnitName, null, createForSuiteBeanHelperClasses);
	}

	/**
	 * Constructor
	 *
	 * @param persistenceUnitName
	 * @param propertyOverrideMap
	 * @param createForSuiteBeanHelperClasses
	 */
	public JstInvokeDataCreateForSuiteRule(final String persistenceUnitName,
			final Map<String, Object> propertyOverrideMap, final Class<?>... createForSuiteBeanHelperClasses) {

		super();

		this.persistenceUnitName = persistenceUnitName;

		this.propertyOverrideMap = propertyOverrideMap;

		final List<UniqueForSuiteRuleSI> createListTemp = new ArrayList<UniqueForSuiteRuleSI>();

		for (final Class<?> clazz : createForSuiteBeanHelperClasses) {

			if (JstBaseCreateForSuiteBeanHelper.class.isAssignableFrom(clazz)) {

				createListTemp.add((JstBaseCreateForSuiteBeanHelper) ReflectionUtilHelper
						.instantiatePublicConstructorNoArgument(clazz));
			} else {

				throw new TestingConstructorRuleException("\nThe class [" + clazz.getSimpleName()
						+ "] does not appear to extend a base class for creating persistence test data.\n");
			}
		}

		this.createList = createListTemp;
	}

	/**
	 * @see BaseRule#beforeTM()
	 */
	@Override
	public void beforeTM() {

		for (final UniqueForSuiteRuleSI createBeanHelper : this.createList) {
			processCreateBeanHelperAsTransaction(createBeanHelper);
		}
	}

	/**
	 * @see UniqueForSuiteRuleSI#uniqueSuiteIdentityTM()
	 */
	@Override
	public String uniqueSuiteIdentityTM() {

		final StringBuilder uniqueIdentity = new StringBuilder();

		for (final UniqueForSuiteRuleSI createBeanHelper : this.createList) {
			uniqueIdentity.append(createBeanHelper.uniqueSuiteIdentityTM());
		}
		return uniqueIdentity.toString();
	}

	/**
	 * @param createBeanHelper
	 */
	protected void processCreateBeanHelperAsTransaction(final UniqueForSuiteRuleSI createBeanHelper) {

		EntityManager entityManager = null;

		try {
			entityManager = EntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(this.persistenceUnitName,
					this.propertyOverrideMap);

			final List<Object> createList = ((JstBaseCreateForSuiteBeanHelper) createBeanHelper)
					.populateDomainEntityCreateListTM(new QueryRM().setEntityManager(entityManager));

			TransactionUtilHelper.transactCreateOrUpdate(entityManager, createList);

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
	}
}