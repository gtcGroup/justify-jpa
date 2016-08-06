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
import com.gtcgroup.justify.jpa.helper.internal.EntityManagerUtilHelper;

/**
 * This Assertions class provides convenience methods for assertion processing.
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

	@SuppressWarnings("javadoc")
	INSTANCE;

	private static JstAssertionsJpaCascadePO assertionsJpaCascadePO;
	private static EntityManager entityManager;

	/**
	 * This method verifies cascade annotations.
	 *
	 * @param <ENTITY>
	 * @param <PO>
	 * @param assertionsJpaCascadePO
	 */
	public static <ENTITY, PO extends JstAssertionsJpaCascadePO> void assertCascadeTypes(
			final PO assertionsJpaCascadePO) {

		AssertionsJPA.assertionsJpaCascadePO = assertionsJpaCascadePO;
		Object entityIdentity = null;

		AssertionsJPA.entityManager = JstEntityManagerFactoryCacheHelper
				.createEntityManagerToBeClosed(AssertionsJPA.assertionsJpaCascadePO.getPersistenceUnitName());

		try {

			entityIdentity = createEntity();

			verifyCascadeTypePersist();

			verifyNoCascadeTypePersist();

			deleteEntity(entityIdentity);

			verifyCascadeTypeRemove();

			verifyNoCascadeTypeRemove();

		} finally {

			try {
				EntityManagerUtilHelper.removeEntity(AssertionsJPA.entityManager,
						AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass(), entityIdentity);

				deleteRemainingEntities();

			} catch (final Exception e) {

				JstEntityManagerFactoryCacheHelper.closeEntityManager(AssertionsJPA.entityManager);

				throw new TestingRuntimeException(e);
			}
		}
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entities
	 */
	public static <ENTITY> void assertExistsInDatabaseWithEntity(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... entities) {
		{

			EntityManager entityManager = null;

			try {

				entityManager = JstEntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

				if (!EntityManagerUtilHelper.existsInDatabaseWithEntity(entityManager, entities)) {

					Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, entityClass));
				}

			} finally {
				JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
			}
		}
		return;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param identities
	 */
	public static <ENTITY> void assertExistsInDatabaseWithIdentity(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... identities) {

		EntityManager entityManager = null;

		try {

			entityManager = JstEntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

			if (!EntityManagerUtilHelper.existsInDatabaseWithIdentity(entityManager, entityClass, identities)) {

				Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, entityClass));
			}

		} finally {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entities
	 */
	public static <ENTITY> void assertExistsInPersistenceContextWithEntity(final String persistenceUnitName,
			final Object... entities) {

		EntityManager entityManager = null;

		try {

			entityManager = JstEntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

			if (!EntityManagerUtilHelper.existsInPersistenceContextWithEntity(entityManager, entities)) {

				Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null));
			}

		} finally {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entities
	 */
	public static <ENTITY> void assertExistsInSharedCacheWithEntity(final String persistenceUnitName,
			final Object... entities) {

		EntityManager entityManager = null;

		try {

			entityManager = JstEntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

			if (!EntityManagerUtilHelper.existsInSharedCacheWithEntity(entityManager, entities)) {

				Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null));
			}

		} finally {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param identities
	 */
	public static <ENTITY> void assertExistsInSharedCacheWithIdentity(final String persistenceUnitName,
			final Object... identities) {

		EntityManager entityManager = null;

		try {

			entityManager = JstEntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

			if (!EntityManagerUtilHelper.existsInSharedCacheWithIdentity(entityManager, identities)) {

				Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null));
			}

		} finally {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return;
	}

	/**
	 * @param persistenceUnitName
	 * @param entities
	 * @return {@link Object} representing entityIdentity.
	 */
	private static Object createEntity() {

		Object entityIdentity = null;

		try {
			entityIdentity = EntityManagerUtilHelper.createOrUpdateEntity(AssertionsJPA.entityManager,
					AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity());

		} catch (@SuppressWarnings("unused") final Exception e) {

			Assert.fail("The domain entity ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] could not be created by the assert method.");

		}
		return entityIdentity;
	}

	private static <ENTITY> String createEntityShouldExistsMessage(final String persistenceUnitName,
			final Class<ENTITY> entityClassOrNull) {

		final StringBuilder assertionErrorMessage = new StringBuilder();

		assertionErrorMessage.append("An expected instance ");

		if (null != entityClassOrNull) {
			assertionErrorMessage.append("of [");
			assertionErrorMessage.append(entityClassOrNull.getSimpleName());
			assertionErrorMessage.append("] ");
		}

		assertionErrorMessage.append("is unavailable from the database [");
		assertionErrorMessage.append(persistenceUnitName);
		assertionErrorMessage.append("].");

		return assertionErrorMessage.toString();
	}

	private static void deleteEntity(final Object entityIdentity) {

		EntityManagerUtilHelper.findAndRemoveEntity(AssertionsJPA.entityManager,
				AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass(), entityIdentity);
		return;
	}

	private static void deleteRemainingEntities() throws ClassNotFoundException {

		for (final Map.Entry<String, Object> entry : AssertionsJPA.assertionsJpaCascadePO.getCascadeRemoveNotMap()
				.entrySet()) {

			EntityManagerUtilHelper.findAndRemoveEntity(AssertionsJPA.entityManager,
					Class.forName(entry.getKey()),
					entry.getValue());
		}

		return;
	}

	private static boolean isExists(final Map<String, Object> existsMap) throws ClassNotFoundException {

		for (final Map.Entry<String, Object> entry : existsMap.entrySet()) {

			if (!EntityManagerUtilHelper.existsInDatabaseWithIdentity(AssertionsJPA.entityManager,
					Class.forName(entry.getKey()), entry.getValue())) {

				return false;
			}
		}
		return true;
	}

	private static boolean isNotExists(final Map<String, Object> notExistsMap) throws ClassNotFoundException {

		for (final Map.Entry<String, Object> entry : notExistsMap.entrySet()) {

			if (EntityManagerUtilHelper.existsInDatabaseWithIdentity(AssertionsJPA.entityManager,
					Class.forName(entry.getKey()), entry.getValue())) {

				return false;
			}

		}
		return true;
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 */
	private static void verifyCascadeTypePersist() {

		try {
			isExists(AssertionsJPA.assertionsJpaCascadePO.getCascadePersistMap());

		} catch (@SuppressWarnings("unused") final Exception e) {

			Assert.fail("Cascade type persist domain entities ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] could not be created.");
		}
		return;
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 */
	private static void verifyCascadeTypeRemove() {

		try {
			isExists(AssertionsJPA.assertionsJpaCascadePO.getCascadeRemoveNotMap());

		} catch (@SuppressWarnings("unused") final Exception e) {

			Assert.fail("Cascade type remove domain entities ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] were not available for deletion.");
		}
		return;
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 */
	private static void verifyNoCascadeTypePersist() {

		try {
			isNotExists(AssertionsJPA.assertionsJpaCascadePO.getCascadePersistNotMap());

		} catch (@SuppressWarnings("unused") final Exception e) {

			Assert.fail("Non-cascading domain entities ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] were created.");
		}
		return;
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 */
	private static void verifyNoCascadeTypeRemove() {

		try {
			isNotExists(AssertionsJPA.assertionsJpaCascadePO.getCascadeRemoveMap());

		} catch (@SuppressWarnings("unused") final Exception e) {

			Assert.fail("Non-cascading domain entities ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] were available for deletion.");
		}
		return;
	}
}
