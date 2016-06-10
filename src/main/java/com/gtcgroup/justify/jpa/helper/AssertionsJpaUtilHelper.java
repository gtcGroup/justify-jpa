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

	@SuppressWarnings("javadoc") INSTANCE;

	private static JstAssertJpaPO assertJpaPO;

	/**
	 * This method verifies cascade annotations.
	 *
	 * @param <ENTITY>
	 * @param <PO>
	 * @param assertJpaPO
	 */
	public static <ENTITY, PO extends JstAssertJpaPO> void assertCascadeTypes(final PO assertJpaPO) {

		String assertionErrorMessage = null;

		AssertionsJpaUtilHelper.assertJpaPO = assertJpaPO;

		@SuppressWarnings("unchecked")
		final ENTITY entity = (ENTITY) retrieveMergedEntityFromCreate();
		AssertionsJpaUtilHelper.assertJpaPO.setDomainEntity(entity);

		assertionErrorMessage = verifyCascadeTypePersist();

		if (!assertionErrorMessage.isEmpty()) {

			deleteEntity();
			deleteRemainingEntities();

			throwAssertionError(assertionErrorMessage);
		}

		assertionErrorMessage = deleteEntity();

		if (!assertionErrorMessage.isEmpty()) {

			deleteRemainingEntities();
			throwAssertionError(assertionErrorMessage);
		}

		assertionErrorMessage = verifyCascadeTypeRemove();

		if (!assertionErrorMessage.isEmpty()) {

			deleteRemainingEntities();
			throwAssertionError(assertionErrorMessage);
		}

		deleteRemainingEntities();
	}



	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entityIdentity
	 * @return boolean
	 */
	private static <ENTITY extends Object> String assertExists(final Class<ENTITY> entityClass,
			final Object entityIdentity, final boolean exists, final String createOrDelete) {

		EntityManager entityManager = null;
		String message = new String();

		try {
			entityManager = EntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(AssertionsJpaUtilHelper.assertJpaPO.getPersistenceUnitName());

			final QueryRM queryRM = new QueryRM().withEntityManager(entityManager);

			final boolean verdict = queryRM.existsEntityIdentities(entityClass, entityIdentity);

			if (exists) {

				if (false == verdict) {

					message = createAssertionErrorMessage(entityClass, "not", createOrDelete);
				}
			} else {
				if (true == verdict) {

					message = createAssertionErrorMessage(entityClass, "incorrectly", createOrDelete);
				}
			}

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return message;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entities
	 */
	public static <ENTITY> void assertExistsEntity(final String persistenceUnitName,
			final Object... entities) {

		QueryRM queryRM = null;

		try {
			queryRM = EntityManagerFactoryCacheHelper.createQueryRmToBeClosed(persistenceUnitName);
			final boolean result = queryRM.existsEntityInstances(entities);
			if (!result) {
				final String assertionErrorMessage = createAssertionExistsMessage(entities[0].getClass(),
						persistenceUnitName);
				throwAssertionError(assertionErrorMessage);
			}

		} finally {
			EntityManagerFactoryCacheHelper.closeQueryRM(queryRM);
		}
		return;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param identities
	 */
	public static <ENTITY> void assertExistsById(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... identities) {

		QueryRM queryRM = null;

		try {
			queryRM = EntityManagerFactoryCacheHelper.createQueryRmToBeClosed(persistenceUnitName);
			final boolean result = queryRM.existsEntityIdentities(entityClass, identities);
			if (!result) {
				final String assertionErrorMessage = createAssertionExistsMessage(entityClass, persistenceUnitName);
				throwAssertionError(assertionErrorMessage);
			}

		} finally {
			EntityManagerFactoryCacheHelper.closeQueryRM(queryRM);
		}

		return;
	}

	private static <ENTITY> String createAssertionErrorMessage(final Class<ENTITY> entityClass, final String outcome,
			final String createOrDelete) {

		final StringBuilder assertionErrorMessage = new StringBuilder();

		assertionErrorMessage.append("The instance [");
		assertionErrorMessage.append(entityClass.getSimpleName());
		assertionErrorMessage.append("] for cascade type [");
		assertionErrorMessage.append(createOrDelete);
		assertionErrorMessage.append("] is ");
		assertionErrorMessage.append(outcome);
		assertionErrorMessage.append(" available from the database [");
		assertionErrorMessage.append(AssertionsJpaUtilHelper.assertJpaPO.getPersistenceUnitName());
		assertionErrorMessage.append("].");

		return assertionErrorMessage.toString();

	}

	private static <ENTITY> String createAssertionExistsMessage(final Class<ENTITY> entityClass, final String persistenceUnitName) {

		final StringBuilder assertionErrorMessage = new StringBuilder();

		assertionErrorMessage.append("An expected instance of [");
		assertionErrorMessage.append(entityClass.getSimpleName());
		assertionErrorMessage.append("] is unavailable from the database [");
		assertionErrorMessage.append(persistenceUnitName);
		assertionErrorMessage.append("].");

		return assertionErrorMessage.toString();

	}

	private static String deleteEntity() {

		EntityManager entityManager = null;
		final String assertionErrorMessage = new String();

		try {

			entityManager = EntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(AssertionsJpaUtilHelper.assertJpaPO.getPersistenceUnitName());

			final boolean exists = new QueryRM().withEntityManager(entityManager)
					.existsEntityInstances(AssertionsJpaUtilHelper.assertJpaPO.getDomainEntity());

			if (!exists) {
				createAssertionErrorMessage(AssertionsJpaUtilHelper.assertJpaPO.getDomainEntity().getClass(),
						"not available",
						"delete");
			}

			new TransactionRM().withEntityManager(entityManager)
			.transactDelete(AssertionsJpaUtilHelper.assertJpaPO.getDomainEntity());

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return assertionErrorMessage;
	}

	private static void deleteRemainingEntities() {

		EntityManager entityManager = null;

		try {

			for (final Map.Entry<String, Object> entry : AssertionsJpaUtilHelper.assertJpaPO.getCascadeRemoveNotMap()
					.entrySet()) {

				entityManager = EntityManagerFactoryCacheHelper
						.createEntityManagerToBeClosed(AssertionsJpaUtilHelper.assertJpaPO.getPersistenceUnitName());

				final Object entity = entityManager.find(Class.forName(entry.getKey()), entry.getValue());

				new TransactionRM().withEntityManager(entityManager).transactDelete(entity);

			}

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return;

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

			mergedEntity = (ENTITY) new TransactionRM().withEntityManager(entityManager)
					.transactCreateOrUpdate(AssertionsJpaUtilHelper.assertJpaPO.getDomainEntity());

		} catch (@SuppressWarnings("unused") final Exception e) {

			Assert.fail("The domain entities could not be created by the assert method.");

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return mergedEntity;
	}

	private static void throwAssertionError(final String assertionErrorMessage) throws AssertionError {

		throw new AssertionError(assertionErrorMessage);
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 */
	private static String verifyCascadeTypePersist() {

		return verifyUsingMaps(AssertionsJpaUtilHelper.assertJpaPO.getCascadePersistMap(),
				AssertionsJpaUtilHelper.assertJpaPO.getCascadePersistNotMap(), "Create");
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 * @return
	 */
	private static String verifyCascadeTypeRemove() {

		return verifyUsingMaps(AssertionsJpaUtilHelper.assertJpaPO.getCascadeRemoveNotMap(),
				AssertionsJpaUtilHelper.assertJpaPO.getCascadeRemoveMap(), "delete");

	}

	private static String verifyUsingMaps(final Map<String, Object> existsMap, final Map<String, Object> notExistsMap,
			final String createOrDelete) {

		String assertionErrorMessage = new String();

		for (final Map.Entry<String, Object> entry : existsMap.entrySet()) {

			try {

				final String tempMessage = assertExists(Class.forName(entry.getKey()), entry.getValue(), true,
						createOrDelete);
				if (!tempMessage.isEmpty()) {
					assertionErrorMessage += "[" + tempMessage + "] ";
				}
			} catch (final ClassNotFoundException e) {

				throw new TestingRuntimeException(e);
			}
		}

		for (final Map.Entry<String, Object> entry : notExistsMap.entrySet()) {

			try {

				final String tempMessage = assertExists(Class.forName(entry.getKey()), entry.getValue(), false,
						createOrDelete);
				if (!tempMessage.isEmpty()) {
					assertionErrorMessage += "[" + tempMessage + "] ";
				}

			} catch (final ClassNotFoundException e) {

				throw new TestingRuntimeException(e);
			}
		}
		return assertionErrorMessage;
	}
}
