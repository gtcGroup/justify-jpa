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

package com.gtcgroup.justify.jpa.assertions;

import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.Assert;

import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.rm.JstQueryRM;
import com.gtcgroup.justify.jpa.rm.JstTransactionRM;

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
public enum AssertionsJPA {

	@SuppressWarnings("javadoc") INSTANCE;

	private static JstAssertionsJpaCascadePO assertionsJpaCascadePO;

	/**
	 * This method verifies cascade annotations.
	 *
	 * @param <ENTITY>
	 * @param <PO>
	 * @param assertionsJpaCascadePO
	 */
	public static <ENTITY, PO extends JstAssertionsJpaCascadePO> void assertCascadeTypes(
			final PO assertionsJpaCascadePO) {

		String assertionErrorMessage = null;

		AssertionsJPA.assertionsJpaCascadePO = assertionsJpaCascadePO;

		@SuppressWarnings("unchecked")
		final ENTITY entity = (ENTITY) retrieveMergedEntityFromCreate();
		AssertionsJPA.assertionsJpaCascadePO.setDomainEntity(entity);

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
			entityManager = JstEntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(AssertionsJPA.assertionsJpaCascadePO.getPersistenceUnitName());

			final JstQueryRM jstQueryRM = new JstQueryRM().withEntityManager(entityManager);

			final boolean verdict = jstQueryRM.existsEntityIdentities(entityClass, entityIdentity);

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

			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return message;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param identities
	 */
	public static <ENTITY> void assertExistsById(final String persistenceUnitName, final Class<ENTITY> entityClass,
			final Object... identities) {

		JstQueryRM jstQueryRM = null;

		try {
			jstQueryRM = JstEntityManagerFactoryCacheHelper.createQueryRmToBeClosed(persistenceUnitName);
			final boolean result = jstQueryRM.existsEntityIdentities(entityClass, identities);
			if (!result) {
				final String assertionErrorMessage = createAssertionExistsMessage(entityClass, persistenceUnitName);
				throwAssertionError(assertionErrorMessage);
			}

		} finally {
			JstEntityManagerFactoryCacheHelper.closeQueryRM(jstQueryRM);
		}

		return;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entities
	 */
	public static <ENTITY> void assertExistsEntity(final String persistenceUnitName, final Object... entities) {

		JstQueryRM jstQueryRM = null;

		try {
			jstQueryRM = JstEntityManagerFactoryCacheHelper.createQueryRmToBeClosed(persistenceUnitName);
			final boolean result = jstQueryRM.existsEntityInstances(entities);
			if (!result) {
				final String assertionErrorMessage = createAssertionExistsMessage(entities[0].getClass(),
						persistenceUnitName);
				throwAssertionError(assertionErrorMessage);
			}

		} finally {
			JstEntityManagerFactoryCacheHelper.closeQueryRM(jstQueryRM);
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
		assertionErrorMessage.append(AssertionsJPA.assertionsJpaCascadePO.getPersistenceUnitName());
		assertionErrorMessage.append("].");

		return assertionErrorMessage.toString();

	}

	private static <ENTITY> String createAssertionExistsMessage(final Class<ENTITY> entityClass,
			final String persistenceUnitName) {

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

			entityManager = JstEntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(AssertionsJPA.assertionsJpaCascadePO.getPersistenceUnitName());

			final boolean exists = new JstQueryRM().withEntityManager(entityManager)
					.existsEntityInstances(AssertionsJPA.assertionsJpaCascadePO.getDomainEntity());

			if (!exists) {
				createAssertionErrorMessage(AssertionsJPA.assertionsJpaCascadePO.getDomainEntity().getClass(),
						"not available", "delete");
			}

			new JstTransactionRM().withEntityManager(entityManager)
			.transactDelete(AssertionsJPA.assertionsJpaCascadePO.getDomainEntity());

		} finally {

			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return assertionErrorMessage;
	}

	private static void deleteRemainingEntities() {

		EntityManager entityManager = null;

		try {

			for (final Map.Entry<String, Object> entry : AssertionsJPA.assertionsJpaCascadePO.getCascadeRemoveNotMap()
					.entrySet()) {

				entityManager = JstEntityManagerFactoryCacheHelper
						.createEntityManagerToBeClosed(AssertionsJPA.assertionsJpaCascadePO.getPersistenceUnitName());

				final Object entity = entityManager.find(Class.forName(entry.getKey()), entry.getValue());

				new JstTransactionRM().withEntityManager(entityManager).transactDelete(entity);

			}

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);

		} finally {

			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
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

			entityManager = JstEntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(AssertionsJPA.assertionsJpaCascadePO.getPersistenceUnitName());

			mergedEntity = (ENTITY) new JstTransactionRM().withEntityManager(entityManager)
					.transactCreateOrUpdate(AssertionsJPA.assertionsJpaCascadePO.getDomainEntity());

		} catch (@SuppressWarnings("unused") final Exception e) {

			Assert.fail("The domain entities could not be created by the assert method.");

		} finally {

			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
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

		return verifyUsingMaps(AssertionsJPA.assertionsJpaCascadePO.getCascadePersistMap(),
				AssertionsJPA.assertionsJpaCascadePO.getCascadePersistNotMap(), "Create");
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 * @return
	 */
	private static String verifyCascadeTypeRemove() {

		return verifyUsingMaps(AssertionsJPA.assertionsJpaCascadePO.getCascadeRemoveNotMap(),
				AssertionsJPA.assertionsJpaCascadePO.getCascadeRemoveMap(), "delete");

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
