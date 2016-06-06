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

package com.gtcgroup.justify.jpa.helper;

import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.Assert;

import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;
import com.gtcgroup.justify.jpa.rm.QueryRM;
import com.gtcgroup.justify.jpa.rm.TransactionRM;

/**
 * This Util Helper class provides support for assertion processing.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum AssertionsJpaUtilHelper {

	@SuppressWarnings("javadoc")
	INSTANCE;

	private static JstAssertJpaPO assertJpaPO;

	/**
	 * This method verifies cascade annotations.
	 *
	 * @param <ENTITY>
	 * @param assertJpaPO
	 */
	public static <ENTITY> void assertCascadeTypes(final JstAssertJpaPO assertJpaPO) {

		try {
			AssertionsJpaUtilHelper.assertJpaPO = assertJpaPO;

			@SuppressWarnings("unchecked")
			final ENTITY entity = (ENTITY) retrieveMergedEntityFromCreate();
			AssertionsJpaUtilHelper.assertJpaPO.setDomainEntity(entity);

			verifyCascadeTypePersist();

		} catch (final RuntimeException e) {

			deleteEntity();

			verifyCascadeTypeRemove();

			deleteRemainingEntities();

			throw e;
		}

		return;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entityIdentity
	 * @return boolean
	 */
	private static <ENTITY extends Object> boolean assertExists(final Class<ENTITY> entityClass,
			final Object entityIdentity, final boolean exists, final String createOrDelete) {

		EntityManager entityManager = null;

		try {
			entityManager = EntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(AssertionsJpaUtilHelper.assertJpaPO.getPersistenceUnitName());

			final QueryRM queryRM = new QueryRM().withEntityManager(entityManager);

			final boolean verdict = queryRM.existsEntity(entityClass, entityIdentity);

			if (exists) {

				if (false == verdict) {

					displayError(entityClass, "not", createOrDelete);
				}
			} else {
				if (true == verdict) {

					displayError(entityClass, "incorrectly", createOrDelete);
				}
			}

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return true;
	}

	private static void deleteEntity() {

		EntityManager entityManager = null;

		try {

			entityManager = EntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(AssertionsJpaUtilHelper.assertJpaPO.getPersistenceUnitName());

			TransactionRM.withEntityManager(entityManager)
					.transactDelete(AssertionsJpaUtilHelper.assertJpaPO.getDomainEntity());

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return;
	}

	private static void deleteRemainingEntities() {

		EntityManager entityManager = null;

		try {

			for (final Map.Entry<String, Object> entry : AssertionsJpaUtilHelper.assertJpaPO.getCascadeRemoveNotMap()
					.entrySet()) {

				entityManager = EntityManagerFactoryCacheHelper
						.createEntityManagerToBeClosed(AssertionsJpaUtilHelper.assertJpaPO.getPersistenceUnitName());

				final Object entity = entityManager.find(Class.forName(entry.getKey()), entry.getValue());

				TransactionRM.withEntityManager(entityManager).transactDelete(entity);

			}

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return;

	}

	private static <ENTITY> void displayError(final Class<ENTITY> entityClass, final String outcome,
			final String createOrDelete) {

		final StringBuilder message = new StringBuilder();

		message.append("The instance [");
		message.append(entityClass.getSimpleName());
		message.append("] for cascade type [");
		message.append(createOrDelete);
		message.append("] is ");
		message.append(outcome);
		message.append(" available from the database [");
		message.append(AssertionsJpaUtilHelper.assertJpaPO.getPersistenceUnitName());
		message.append("].");

		Assert.fail(message.toString());
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entities
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	private static <ENTITY> ENTITY retrieveMergedEntityFromCreate() {

		EntityManager entityManager = null;
		ENTITY mergedEntity = null;

		try {

			entityManager = EntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(AssertionsJpaUtilHelper.assertJpaPO.getPersistenceUnitName());

			mergedEntity = (ENTITY) TransactionRM.withEntityManager(entityManager)
					.transactCreateOrUpdate(AssertionsJpaUtilHelper.assertJpaPO.getDomainEntity());

		} catch (@SuppressWarnings("unused") final Exception e) {

			Assert.fail("The domain entities could not be created by the assert method.");

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return mergedEntity;
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 */
	private static void verifyCascadeTypePersist() {

		verifyUsingMaps(AssertionsJpaUtilHelper.assertJpaPO.getCascadePersistMap(),
				AssertionsJpaUtilHelper.assertJpaPO.getCascadePersistNotMap(), "Create");
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 */
	private static void verifyCascadeTypeRemove() {

		verifyUsingMaps(AssertionsJpaUtilHelper.assertJpaPO.getCascadeRemoveNotMap(),
				AssertionsJpaUtilHelper.assertJpaPO.getCascadeRemoveMap(), "delete");

	}

	private static void verifyUsingMaps(final Map<String, Object> existsMap, final Map<String, Object> notExistsMap,
			final String createOrDelete) {

		for (final Map.Entry<String, Object> entry : existsMap.entrySet()) {

			try {

				assertExists(Class.forName(entry.getKey()), entry.getValue(), true, createOrDelete);

			} catch (final ClassNotFoundException e) {

				throw new TestingRuntimeException(e);
			}
		}

		for (final Map.Entry<String, Object> entry : notExistsMap.entrySet()) {

			try {

				assertExists(Class.forName(entry.getKey()), entry.getValue(), false, createOrDelete);

			} catch (final ClassNotFoundException e) {

				throw new TestingRuntimeException(e);
			}
		}
	}
}
