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
import com.gtcgroup.justify.jpa.helper.JstFindUtilHelper;
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
	 * This method verifies cascade annotations. If properly executed, it will
	 * remove any persisted instances used for verification.
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY, PO extends JstAssertCascadeJpaPO> ENTITY assertCascadeTypes(final PO assertionsCascadePO) {

		AssertionsJPA.assertionsJpaCascadePO = assertionsCascadePO;

		AssertionsJPA.entityManager = getEntityManager(AssertionsJPA.assertionsJpaCascadePO.getPersistenceUnitName());

		try {

			createParentEntity();

			verifyCascadeEntitiesPersisted(assertionsCascadePO.getCascadePersistList(), true);

			verifyCascadeEntitiesPersisted(assertionsCascadePO.getCascadeNoPersistList(), false);

			deleteParentEntity();

			verifyCascadeEntitiesRemoved(assertionsCascadePO.getCascadeRemoveList(), false);

			verifyCascadeEntitiesRemoved(assertionsCascadePO.getCascadeNoRemoveList(), true);

		} catch (final Exception e) {

			catchBlock(e);

		} finally {

			finallyBlock();
		}
		return (ENTITY) AssertionsJPA.parentEntity;
	}

	public static <ENTITY> void assertExistsInDatabase(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (!JstFindUtilHelper.existsInDatabase(AssertionsJPA.entityManager, entityClass, entityIdentities)) {

			assertFailWithMessage(persistenceUnitName, entityClass, "database", "instance unavailable");
		}
		return;
	}

	public static <ENTITY> void assertExistsInDatabase(final String persistenceUnitName,
			final List<ENTITY> entityListContainingIdentities) {

		assertExistsInDatabase(persistenceUnitName, entityListContainingIdentities.toArray());
	}

	public static void assertExistsInDatabase(final String persistenceUnitName,
			final Object... entititiesContainingIdentity) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (!JstFindUtilHelper.existsInDatabases(AssertionsJPA.entityManager, entititiesContainingIdentity)) {

			assertFailWithMessage(persistenceUnitName, null, "database", "instance unavailable");
		}
		return;
	}

	public static <ENTITY> void assertExistsInSharedCache(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (!JstFindUtilHelper.existsInSharedCache(AssertionsJPA.entityManager, entityClass, entityIdentities)) {

			assertFailWithMessage(persistenceUnitName, null, "shared cache", "class unavailable");
		}
	}

	public static void assertExistsInSharedCache(final String persistenceUnitName, final Object... managedEntities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (!JstFindUtilHelper.existsInSharedCache(AssertionsJPA.entityManager, managedEntities)) {

			assertFailWithMessage(persistenceUnitName, null, "shared cache", "class unavailable");
		}
	}

	private static <ENTITY> void assertFailWithMessage(final String persistenceUnitName,
			final Class<ENTITY> entityClassOrNull, final String fromWhere, final String availableOrUnavailable) {

		final StringBuilder assertionErrorMessage = new StringBuilder();

		assertionErrorMessage.append("An ");

		if (null != entityClassOrNull) {
			assertionErrorMessage.append("of [");
			assertionErrorMessage.append(entityClassOrNull.getSimpleName());
			assertionErrorMessage.append("] ");
		}

		assertionErrorMessage.append(availableOrUnavailable);
		assertionErrorMessage.append(" from the ");
		assertionErrorMessage.append(fromWhere + " [");
		assertionErrorMessage.append(persistenceUnitName);
		assertionErrorMessage.append("].");

		Assert.fail(assertionErrorMessage.toString());
	}

	public static <ENTITY> void assertNotExistsInDatabase(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (JstFindUtilHelper.existsInDatabase(AssertionsJPA.entityManager, entityClass, entityIdentities)) {

			assertFailWithMessage(persistenceUnitName, entityClass, "database", "instance unexpectedly available");
		}
		return;
	}

	public static <ENTITY> void assertNotExistsInDatabase(final String persistenceUnitName,
			final List<ENTITY> entityListContainingIdentities) {

		assertNotExistsInDatabase(persistenceUnitName, entityListContainingIdentities.toArray());

	}

	public static void assertNotExistsInDatabase(final String persistenceUnitName,
			final Object... entititiesContainingIdentity) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (JstFindUtilHelper.existsInDatabases(AssertionsJPA.entityManager, entititiesContainingIdentity)) {

			assertFailWithMessage(persistenceUnitName, null, "database", "instance unexpectedly available");
		}
	}

	public static <ENTITY> void assertNotExistsInSharedCache(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		AssertionsJPA.entityManager = getEntityManager(persistenceUnitName);

		if (JstFindUtilHelper.existsInSharedCache(AssertionsJPA.entityManager, entityClass, entityIdentities)) {

			assertFailWithMessage(persistenceUnitName, null, "shared cache", "class available");
		}
	}

	private static void cascadeError() {

		Assert.fail("A cascade error has occured for the domain entity ["
				+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName() + "].\n\t\t"
				+ "Check both the domain entity and the test Parameter Object to determine the source of the error.");
	}

	private static void catchBlock(final Exception e) {

		try {
			// Cleanup the parent if needed.
			JstTransactionUtilHelper.transactEntities(JstTransactionJpaPO.withException()
					.withEntityManager(AssertionsJPA.entityManager).withDeleteEntities(AssertionsJPA.parentEntity));
		} catch (final Exception ignore) {

			throw (JustifyRuntimeException) e;
		}
		throw (JustifyRuntimeException) e;
	}

	private static void closeEntityManager() {

		JstEntityManagerFactoryCacheHelper.closeEntityManager(AssertionsJPA.entityManager);
	}

	/**
	 * @return {@link Object} representing entityIdentity.
	 */
	private static void createParentEntity() {

		Object parentEntity = null;

		final JstTransactionJpaPO transactPO = JstTransactionJpaPO.withException()
				.withEntityManager(AssertionsJPA.entityManager)
				.withCreateAndUpdateEntities(AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity());

		parentEntity = JstTransactionJpaRM.transactEntity(transactPO);

		AssertionsJPA.parentEntity = parentEntity;
		AssertionsJPA.assertionsJpaCascadePO.replacePopulatedEntity(parentEntity);
	}

	private static void deleteParentEntity() {

		if (null != AssertionsJPA.parentEntity) {

			JstTransactionUtilHelper.findAndDeleteEntity(AssertionsJPA.entityManager, AssertionsJPA.parentEntity);
		}
	}

	private static void deleteRemainingEntities() {

		if (null != AssertionsJPA.parentEntity) {
			for (final String method : AssertionsJPA.assertionsJpaCascadePO.getAfterTheTestCleanupList()) {

				try {
					JstTransactionUtilHelper.findAndDeleteRelatedEntity(AssertionsJPA.entityManager,
							AssertionsJPA.parentEntity, method);
				} catch (final Exception e) {
					// Ignore
				}
			}
		}
	}

	private static void finallyBlock() {

		try {
			deleteParentEntity();
			deleteRemainingEntities();

		} finally {

			closeEntityManager();
			AssertionsJPA.entityManager = null;
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

	private static boolean isExists(final List<String> existsList, final boolean expected) {

		for (final String methodName : existsList) {

			final Object entityOrList = ReflectionUtilHelper.invokePublicMethod(methodName, AssertionsJPA.parentEntity);
			final boolean actual = JstFindUtilHelper.existsInDatabases(AssertionsJPA.entityManager, entityOrList);
			if (expected != actual) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @throws ClassNotFoundException
	 */
	private static void verifyCascadeEntitiesPersisted(final List<String> persistList, final boolean expected)
			throws ClassNotFoundException {

		final boolean actual = isExists(persistList, expected);

		if (!actual) {
			cascadeError();
		}
	}

	/**
	 * @throws ClassNotFoundException
	 */
	private static void verifyCascadeEntitiesRemoved(final List<String> persistList, final boolean expected)
			throws ClassNotFoundException {

		final boolean actual = isExists(persistList, expected);

		if (!actual) {
			cascadeError();
		}
	}
}
