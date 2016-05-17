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

import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.rules.TestRule;

import com.gtcgroup.justify.core.base.JstBaseForSuiteRule;
import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;
import com.gtcgroup.justify.core.pattern.palette.internal.BaseRule;
import com.gtcgroup.justify.core.si.JstUniqueForSuiteRuleSI;
import com.gtcgroup.justify.jpa.helper.EntityManagerFactoryCacheHelper;

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
public class JstConfigureJpaForSuiteRule extends JstBaseForSuiteRule {

	/**
	 * @param <RULE>
	 * @param persistenceUnitName
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends TestRule> RULE withPersistenceUnitName(final String persistenceUnitName) {

		return (RULE) new JstConfigureJpaForSuiteRule(persistenceUnitName);
	}

	/**
	 * @param <RULE>
	 * @param persistenceUnitName
	 * @param propertyOverrideMap
	 * @return {@link TestRule}
	 */
	@SuppressWarnings("unchecked")
	public static <RULE extends TestRule> RULE withPersistenceUnitName(final String persistenceUnitName,
			final Map<String, Object> propertyOverrideMap) {

		return (RULE) new JstConfigureJpaForSuiteRule(persistenceUnitName, propertyOverrideMap);
	}

	private final String persistenceUnitName;

	private final Map<String, Object> propertyOverrideMap;

	/**
	 * Constructor - protected
	 *
	 * @param persistenceUnitName
	 */
	protected JstConfigureJpaForSuiteRule(final String persistenceUnitName) {

		this(persistenceUnitName, null);
	}

	/**
	 * Constructor - protected
	 *
	 * @param persistenceUnitName
	 * @param propertyOverrideMap
	 */
	protected JstConfigureJpaForSuiteRule(final String persistenceUnitName,
			final Map<String, Object> propertyOverrideMap) {

		super();

		this.persistenceUnitName = persistenceUnitName;

		this.propertyOverrideMap = propertyOverrideMap;

	}

	/**
	 * @see BaseRule#beforeTM()
	 */
	@Override
	public void beforeTM() {

		EntityManager entityManager = null;

		try {
			entityManager = EntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(this.persistenceUnitName,
					this.propertyOverrideMap);

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

		return this.persistenceUnitName + "." + this.persistenceUnitName;
	}
}