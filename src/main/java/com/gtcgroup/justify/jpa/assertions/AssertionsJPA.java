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

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.Assert;

import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerUtilHelper;

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

	private static JstAssertJpaPO assertionsJpaCascadePO;
	private static EntityManager entityManager;
	private static String persistenceUnitName = "";

	/**
	 * This method verifies cascade annotations.
	 *
	 * @param <ENTITY>
	 * @param <PO>
	 * @param assertionsJpaCascadePO
	 */
	public static <ENTITY, PO extends JstAssertJpaPO> void assertCascadeTypes(final PO assertionsJpaCascadePO) {

		AssertionsJPA.assertionsJpaCascadePO = assertionsJpaCascadePO;
		Object entityIdentity = null;

		AssertionsJPA.entityManager = getEntityManager(AssertionsJPA.assertionsJpaCascadePO.getPersistenceUnitName());

		AssertionsJPA.persistenceUnitName = assertionsJpaCascadePO.getPersistenceUnitName();

		try {

			entityIdentity = createEntity();

			verifyPersisted();

			verifyNotPersisted();

			deleteEntity(entityIdentity);

			verifyRemoved();

			verifyNotRemoved();

		} catch (final Exception e) {

			closeEntityManager();

			throw new TestingRuntimeException(e);

		} finally {

			try {
				JstEntityManagerUtilHelper.removeEntity(AssertionsJPA.entityManager,
						AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass(), entityIdentity);

				deleteRemainingEntities();

				closeEntityManager();

			} catch (final Exception e) {

				closeEntityManager();

				throw new TestingRuntimeException(e);
			}
		}
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entityIdentities
	 */
	public static <ENTITY> void assertExistsInDatabaseWithEntityIdentities(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		try {

			AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

			if (!JstEntityManagerUtilHelper.existsInDatabaseWithEntityIdentities(AssertionsJPA.entityManager,
					entityClass, entityIdentities)) {

				Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, entityClass, "database"));
			}

		} finally {
			closeEntityManager();
		}
		return;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityList
	 * @param entityIdentity
	 */
	public static <ENTITY> void assertExistsInDatabaseWithOneOfList(final String persistenceUnitName,
			final List<ENTITY> entityList, final Object entityIdentity) {
		try {

			AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

			for (final ENTITY entity : entityList) {

				if (JstEntityManagerUtilHelper.existsInDatabaseWithEntityIdentities(AssertionsJPA.entityManager,
						entity.getClass(), entityIdentity)) {

					// Found our target!
					return;
				}

			}
			Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null, "database"));

		} finally {
			closeEntityManager();
		}
		return;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param populatedEntities
	 */
	public static <ENTITY> void assertExistsInDatabaseWithPopulatedEntities(final String persistenceUnitName,
			final Object... populatedEntities) {
		{

			try {

				AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

				if (!JstEntityManagerUtilHelper.existsInDatabaseWithPopulatedEntities(AssertionsJPA.entityManager,
						populatedEntities)) {

					Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null, "database"));
				}

			} finally {
				closeEntityManager();
			}
		}
		return;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param persistedEntities
	 */
	public static <ENTITY> void assertExistsInPersistenceContextWithPersistedEntities(final String persistenceUnitName,
			final Object... persistedEntities) {

		try {

			AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

			if (!JstEntityManagerUtilHelper.existsInPersistenceContextWithPersistedEntities(AssertionsJPA.entityManager,
					persistedEntities)) {

				Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null, "persistence context"));
			}

		} finally {
			closeEntityManager();
		}
		return;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entityIdentities
	 */
	public static <ENTITY> void assertExistsInSharedCacheWithEntityIdentities(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		try {

			AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

			if (!JstEntityManagerUtilHelper.existsInSharedCacheWithEntityIdentities(AssertionsJPA.entityManager,
					entityClass, entityIdentities)) {

				Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null, "shared cache"));
			}

		} finally {
			AssertionsJPA.closeEntityManager();
		}
		return;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param persistedEntities
	 */
	public static <ENTITY> void assertExistsInSharedCacheWithPopulatedEntities(final String persistenceUnitName,
			final Object... persistedEntities) {

		try {

			AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

			if (!JstEntityManagerUtilHelper.existsInSharedCacheWithPopulatedEntities(AssertionsJPA.entityManager,
					persistedEntities)) {

				Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null, "shared cache"));
			}

		} finally {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(AssertionsJPA.entityManager);
		}
		return;
	}

	/**
	 * @param persistenceUnitName
	 */
	private static void closeEntityManager() {

		AssertionsJPA.persistenceUnitName = "";
		JstEntityManagerFactoryCacheHelper.closeEntityManager(AssertionsJPA.entityManager);
	}

	/**
	 * @param persistenceUnitName
	 * @param entities
	 * @return {@link Object} representing entityIdentity.
	 */
	private static Object createEntity() {

		Object entityIdentity = null;

		try {
			entityIdentity = JstEntityManagerUtilHelper.createOrUpdateEntity(AssertionsJPA.entityManager,
					AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity());

		} catch (@SuppressWarnings("unused") final Exception e) {

			Assert.fail("The domain entity ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] could not be created by the assert method.");

		}
		return entityIdentity;
	}

	private static <ENTITY> String createEntityShouldExistsMessage(final String persistenceUnitName,
			final Class<ENTITY> entityClassOrNull, final String fromWhat) {

		final StringBuilder assertionErrorMessage = new StringBuilder();

		assertionErrorMessage.append("An expected instance ");

		if (null != entityClassOrNull) {
			assertionErrorMessage.append("of [");
			assertionErrorMessage.append(entityClassOrNull.getSimpleName());
			assertionErrorMessage.append("] ");
		}

		assertionErrorMessage.append("is unavailable from the ");
		assertionErrorMessage.append(fromWhat + " [");
		assertionErrorMessage.append(persistenceUnitName);
		assertionErrorMessage.append("].");

		return assertionErrorMessage.toString();
	}

	private static void deleteEntity(final Object entityIdentity) {

		JstEntityManagerUtilHelper.findAndRemoveEntity(AssertionsJPA.entityManager,
				AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass(), entityIdentity);
		return;
	}

	private static void deleteRemainingEntities() throws ClassNotFoundException {

		for (final Map.Entry<String, Object> entry : AssertionsJPA.assertionsJpaCascadePO.getNotRemovedMap()
				.entrySet()) {

			JstEntityManagerUtilHelper.findAndRemoveEntity(AssertionsJPA.entityManager, Class.forName(entry.getKey()),
					entry.getValue());
		}

		return;
	}

	/**
	 * @param persistenceUnitName
	 * @return EntityManager
	 */
	public static EntityManager getEntityManager(final String persistenceUnitName) {

		if (AssertionsJPA.persistenceUnitName != persistenceUnitName) {

			AssertionsJPA.entityManager = JstEntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(persistenceUnitName);

			AssertionsJPA.persistenceUnitName = persistenceUnitName;
		}

		return AssertionsJPA.entityManager;
	}

	private static Boolean isExists(final Map<String, Object> existsMap) throws ClassNotFoundException {

		Boolean result = null;

		for (final Map.Entry<String, Object> entry : existsMap.entrySet()) {

			if (!JstEntityManagerUtilHelper.existsInDatabaseWithEntityIdentities(AssertionsJPA.entityManager,
					Class.forName(entry.getKey()), entry.getValue())) {

				return Boolean.FALSE;
			}
			result = Boolean.TRUE;
		}
		return result;
	}

	private static Boolean isNotExists(final Map<String, Object> existsMap) throws ClassNotFoundException {

		Boolean result = null;

		for (final Map.Entry<String, Object> entry : existsMap.entrySet()) {

			if (JstEntityManagerUtilHelper.existsInDatabaseWithEntityIdentities(AssertionsJPA.entityManager,
					Class.forName(entry.getKey()), entry.getValue())) {

				return Boolean.FALSE;
			}
			result = Boolean.TRUE;
		}
		return result;
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 * @throws ClassNotFoundException
	 */
	private static void verifyNotPersisted() throws ClassNotFoundException {

		final Boolean result = isExists(AssertionsJPA.assertionsJpaCascadePO.getNotPersistedMap());

		if (null != result && Boolean.TRUE == result) {

			Assert.fail("Non-cascading domain entities ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] were created.");
		}
		return;
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 * @throws ClassNotFoundException
	 */
	private static void verifyNotRemoved() throws ClassNotFoundException {

		final Boolean result = isExists(AssertionsJPA.assertionsJpaCascadePO.getNotRemovedMap());

		if (null != result && Boolean.FALSE == result) {

			Assert.fail("Non-cascading domain entities ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] were available for deletion.");
		}
		return;
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 * @throws ClassNotFoundException
	 */
	private static void verifyPersisted() throws ClassNotFoundException {

		final Boolean result = isExists(AssertionsJPA.assertionsJpaCascadePO.getPersistedMap());

		if (null != result && Boolean.FALSE == result) {

			Assert.fail("Cascade type persist domain entities ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] could not be created.");
		}
		return;
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 * @throws ClassNotFoundException
	 */
	private static void verifyRemoved() throws ClassNotFoundException {

		final Boolean result = isNotExists(AssertionsJPA.assertionsJpaCascadePO.getRemovedMap());

		if (null != result && Boolean.FALSE == result) {

			Assert.fail("Cascade type remove domain entities ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] were not available for deletion.");
		}

		return;
	}

}
