/*
 * [Licensed per the Open Source "MIT License".]
 *
 * Copyright (c) 2006 - 2017 by
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

import javax.persistence.EntityManager;

import org.junit.Assert;

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;
import com.gtcgroup.justify.core.helper.internal.ReflectionUtilHelper;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerUtilHelper;
import com.gtcgroup.justify.jpa.helper.JstTransactionUtilHelper;
import com.gtcgroup.justify.jpa.po.JstAssertCascadeJpaPO;
import com.gtcgroup.justify.jpa.po.JstTransactionJpaPO;
import com.gtcgroup.justify.jpa.rm.JstTransactionJpaRM;

/**
 * This Assertions class provides convenience methods for assertion processing.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
@SuppressWarnings("javadoc")
public enum AssertionsJPA {

	INSTANCE;

	private static JstAssertCascadeJpaPO assertionsJpaCascadePO;
	private static EntityManager entityManager;
	private static Object parentEntity;

	/**
	 * This method verifies cascade annotations.
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY, PO extends JstAssertCascadeJpaPO> ENTITY assertCascadeTypes(final PO assertionsCascadePO) {

		AssertionsJPA.assertionsJpaCascadePO = assertionsCascadePO;

		AssertionsJPA.entityManager = getEntityManager(AssertionsJPA.assertionsJpaCascadePO.getPersistenceUnitName());

		try {

			createParentEntity();

			verifyParentEntityPersisted();

			verifyCascadeEntitiesPersisted();

			verifyCascadeEntitiesNotPersisted();

			deleteParentEntity();

			verifyParentEntityRemoved();

			verifyCascadeEntitiesRemoved();

			verifyCascadeEntitiesNotRemoved();

		} catch (final Exception e) {

			// Just in case we failed.
			JstTransactionUtilHelper.transactEntities(JstTransactionJpaPO.withException()
					.withEntityManager(AssertionsJPA.entityManager).withDeleteEntities(AssertionsJPA.parentEntity));

			if (e instanceof RuntimeException) {

				throw (RuntimeException) e;
			}
			throw new JustifyRuntimeException(e);

		} finally {

			try {

				cleanupRemainingEntities();

			} catch (final Exception e) {

				throw new JustifyRuntimeException(e);

			} finally {

				closeEntityManager();
				AssertionsJPA.entityManager = null;
			}
		}
		return (ENTITY) AssertionsJPA.parentEntity;
	}

	public static void assertExistsInDatabaseWithEntities(final String persistenceUnitName,
			final Object... populatedEntities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (!JstEntityManagerUtilHelper.existsInDatabaseWithPopulatedEntities(AssertionsJPA.entityManager,
				populatedEntities)) {

			Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null, "database"));
		}
		return;
	}

	public static <ENTITY> void assertExistsInDatabaseWithEntityIdentities(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (!JstEntityManagerUtilHelper.existsInDatabaseWithEntityIdentities(AssertionsJPA.entityManager, entityClass,
				entityIdentities)) {

			Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, entityClass, "database"));
		}
		return;
	}

	public static void assertExistsInDatabaseWithEntityList(final String persistenceUnitName,
			final List<Object> entityListContainingIdentities) {

		assertExistsInDatabaseWithEntities(persistenceUnitName, entityListContainingIdentities.toArray());
		return;
	}

	public static <ENTITY> void assertExistsInDatabaseWithListElement(final String persistenceUnitName,
			final List<ENTITY> entityList, final Object entityIdentity) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		for (final ENTITY entity : entityList) {

			if (JstEntityManagerUtilHelper.existsInDatabaseWithEntityIdentities(AssertionsJPA.entityManager,
					entity.getClass(), entityIdentity)) {

				// Found our target!
				return;
			}
		}
		Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null, "database"));
		return;
	}

	public static void assertExistsInPersistenceContextWithManagedEntities(final String persistenceUnitName,
			final Object... managedEntities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (!JstEntityManagerUtilHelper.existsInPersistenceContextWithManagedEntities(AssertionsJPA.entityManager,
				managedEntities)) {

			Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null, "persistence context"));
		}
		return;
	}

	public static void assertExistsInSharedCacheWithEntities(final String persistenceUnitName,
			final Object... managedEntities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (!JstEntityManagerUtilHelper.existsInSharedCacheWithPopulatedEntities(AssertionsJPA.entityManager,
				managedEntities)) {

			Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null, "shared cache"));
		}
		return;
	}

	public static <ENTITY> void assertExistsInSharedCacheWithEntityIdentities(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (!JstEntityManagerUtilHelper.existsInSharedCacheWithEntityIdentities(AssertionsJPA.entityManager,
				entityClass, entityIdentities)) {

			Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null, "shared cache"));
		}
		return;
	}

	public static void assertNotExistsInDatabaseWithEntities(final String persistenceUnitName,
			final Object... populatedEntities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (JstEntityManagerUtilHelper.existsInDatabaseWithPopulatedEntities(AssertionsJPA.entityManager,
				populatedEntities)) {

			Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null, "database"));
		}
		return;
	}

	public static <ENTITY> void assertNotExistsInDatabaseWithEntityIdentities(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (JstEntityManagerUtilHelper.existsInDatabaseWithEntityIdentities(AssertionsJPA.entityManager, entityClass,
				entityIdentities)) {

			Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, entityClass, "database"));
		}
		return;
	}

	public static void assertNotExistsInPersistenceContextWithManagedEntities(final String persistenceUnitName,
			final Object... managedEntities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (JstEntityManagerUtilHelper.existsInPersistenceContextWithManagedEntities(AssertionsJPA.entityManager,
				managedEntities)) {

			Assert.fail(createEntityShouldExistsMessage(persistenceUnitName, null, "persistence context"));
		}
		return;
	}

	private static void cleanupRemainingEntities() {

		for (final String methodNameDoNotRemove : AssertionsJPA.assertionsJpaCascadePO.getCascadeDoNoRemovedList()) {

			deleteRemainingEntities(methodNameDoNotRemove);
		}
		return;
	}

	private static void closeEntityManager() {

		JstEntityManagerFactoryCacheHelper.closeEntityManager(AssertionsJPA.entityManager);
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

	/**
	 * @return {@link Object} representing entityIdentity.
	 */
	private static void createParentEntity() {

		Object parentEntity = null;

		final JstTransactionJpaPO transactPO = JstTransactionJpaPO.withException()
				.withEntityManager(AssertionsJPA.entityManager)
				.withCreateAndUpdateEntities(AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity());

		parentEntity = JstTransactionJpaRM.transactEntities(transactPO).get(0);

		AssertionsJPA.parentEntity = parentEntity;
		AssertionsJPA.assertionsJpaCascadePO.replacePopulatedEntity(parentEntity);
	}

	private static void deleteParentEntity() {

		JstTransactionUtilHelper.findAndDeleteEntity(AssertionsJPA.entityManager, AssertionsJPA.parentEntity);
		return;
	}

	private static void deleteRemainingEntities(final String methodNameDoNotRemove) {

		for (final String methodNameDoNotCleanup : AssertionsJPA.assertionsJpaCascadePO.getDoNotCleanupList()) {

			if (methodNameDoNotRemove.equals(methodNameDoNotCleanup)) {

				return;
			}
		}

		try {
			JstTransactionUtilHelper.findAndDeleteRelatedEntity(AssertionsJPA.entityManager, AssertionsJPA.parentEntity,
					methodNameDoNotRemove);
		} catch (@SuppressWarnings("unused") final Exception e) {
			// Ignore
		}

	}

	/**
	 * @return EntityManager
	 */
	public static EntityManager getEntityManager(final String persistenceUnitName) {

		if (null == AssertionsJPA.entityManager) {

			AssertionsJPA.entityManager = JstEntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(persistenceUnitName);
		}

		return AssertionsJPA.entityManager;
	}

	private static Boolean isExists(final List<String> existsList) {

		Boolean result = null;

		for (final String methodName : existsList) {

			final Object entity = ReflectionUtilHelper.invokePublicMethod(methodName, AssertionsJPA.parentEntity);

			if (!JstEntityManagerUtilHelper.existsInDatabaseWithPopulatedEntities(AssertionsJPA.entityManager,
					entity)) {

				return Boolean.FALSE;
			}
			result = Boolean.TRUE;
		}
		return result;
	}

	private static Boolean isNotExists(final List<String> existsList) {

		Boolean result = null;

		for (final String methodName : existsList) {

			final Object entity = ReflectionUtilHelper.invokePublicMethod(methodName, AssertionsJPA.parentEntity);

			if (JstEntityManagerUtilHelper.existsInDatabaseWithPopulatedEntities(AssertionsJPA.entityManager, entity)) {

				return Boolean.FALSE;
			}
			result = Boolean.TRUE;
		}
		return result;
	}

	/**
	 * @throws ClassNotFoundException
	 */
	private static void verifyCascadeEntitiesNotPersisted() throws ClassNotFoundException {

		final Boolean result = isExists(AssertionsJPA.assertionsJpaCascadePO.getCascadeDoNotPersistList());

		if (null != result && Boolean.TRUE == result) {

			Assert.fail("Non-cascading domain entities ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] were created.");
		}
		return;
	}

	/**
	 * @throws ClassNotFoundException
	 */
	private static void verifyCascadeEntitiesNotRemoved() throws ClassNotFoundException {

		final Boolean result = isExists(AssertionsJPA.assertionsJpaCascadePO.getCascadeDoNoRemovedList());

		if (null != result && Boolean.FALSE == result) {

			Assert.fail("One or more domain entities was not deleted as expected.");
		}
		return;
	}

	/**
	 * @throws ClassNotFoundException
	 */
	private static void verifyCascadeEntitiesPersisted() throws ClassNotFoundException {

		final Boolean result = isExists(AssertionsJPA.assertionsJpaCascadePO.getCascadePersistList());

		if (null != result && Boolean.FALSE == result) {

			Assert.fail("Cascade type for domain entity ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] could not be created.");
		}
		return;
	}

	/**
	 * @throws ClassNotFoundException
	 */
	private static void verifyCascadeEntitiesRemoved() throws ClassNotFoundException {

		final Boolean result = isNotExists(AssertionsJPA.assertionsJpaCascadePO.getCascadeRemoveList());

		if (null != result && Boolean.FALSE == result) {

			Assert.fail("Cascade type remove domain entities ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] were not available for deletion.");
		}
		return;
	}

	private static void verifyParentEntityPersisted() {

		if (!JstEntityManagerUtilHelper.existsInDatabaseWithPopulatedEntities(AssertionsJPA.entityManager,
				AssertionsJPA.parentEntity)) {

			Assert.fail("Cascade type persist domain entities ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] could not be created.");
		}
		return;
	}

	/**
	 * @throws ClassNotFoundException
	 */
	private static void verifyParentEntityRemoved() throws ClassNotFoundException {

		if (null != JstEntityManagerUtilHelper.findReadOnlySingleOrNull(AssertionsJPA.entityManager,
				AssertionsJPA.parentEntity)) {

			Assert.fail("Parent domain entity ["
					+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName()
					+ "] was not deleted.");
		}
		return;
	}
}
