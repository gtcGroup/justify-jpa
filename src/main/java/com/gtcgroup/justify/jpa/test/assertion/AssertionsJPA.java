/*
 * [Licensed per the Open Source "MIT License".]
 *
 * Copyright (c) 2006 - 2018 by
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

package com.gtcgroup.justify.jpa.test.assertion;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.opentest4j.AssertionFailedError;

import com.gtcgroup.justify.core.helper.JstReflectionUtilHelper;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.JstFindUtilHelper;
import com.gtcgroup.justify.jpa.helper.JstTransactionUtilHelper;
import com.gtcgroup.justify.jpa.po.JstFindSinglePO;
import com.gtcgroup.justify.jpa.po.JstTransactionPO;
import com.gtcgroup.justify.jpa.rm.JstTransactionRM;
import com.gtcgroup.justify.jpa.test.po.JstAssertCascadePO;

/**
 * This Assertions class provides convenience methods for assertion processing.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum AssertionsJPA {

	INSTANCE;

	private static final String UNEXPECTEDLY_AVAILABLE = " was unexpectedly available";
	private static final String UNAVAILABLE = " was unavailable";

	/**
	 * This method verifies cascade annotations. If properly executed, it will
	 * remove (clean-up) any persisted instances used for verification.
	 *
	 * return {@link Optional}
	 */
	public static boolean assertCascadeTypes(final JstAssertCascadePO assertCascadePO) {

		boolean createSucceededPopulatedEntity = false;
		boolean deleteAttemptedPopulatedEntity = false;
		boolean deleteAttemptedRemainingEntities = false;
		EntityManager cascadeEntityManager = null;

		try {
			final Optional<EntityManager> optionalEntityManager = JstEntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(assertCascadePO.getPersistenceUnitName());

			if (optionalEntityManager.isPresent()) {

				cascadeEntityManager = optionalEntityManager.get();

				commitPopulatedEntity(assertCascadePO, cascadeEntityManager);

				createSucceededPopulatedEntity = true;

				verifyCascadeEntitiesPersisted(assertCascadePO);

				verifyEntitiesNotPersisted(assertCascadePO);
				deleteAttemptedPopulatedEntity = true;

				deleteParentEntity(assertCascadePO, cascadeEntityManager);

				verifyCascadeEntitiesDeleted(assertCascadePO);

				verifyEntitiesNotDeleted(assertCascadePO);

				deleteAttemptedRemainingEntities = true;

				deleteRemainingEntities(assertCascadePO, cascadeEntityManager);
			} else {
				throw new AssertionFailedError(
						"The persistence unit [" + assertCascadePO.getPersistenceUnitName() + "] may not be correct.");
			}

		} finally {

			cascadeFinallyBlock(assertCascadePO, cascadeEntityManager, createSucceededPopulatedEntity,
					deleteAttemptedPopulatedEntity, deleteAttemptedRemainingEntities);
		}
		return true;
	}

	public static <ENTITY> void assertExistsInDatabase(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object entityIdentity) {

		if (!existsInDatabase(persistenceUnitName, entityClass, entityIdentity)) {
			throwAssertFailedWithMessage(persistenceUnitName, entityClass.getSimpleName() + UNAVAILABLE); // Covered.
		}
		return;
	}

	public static void assertExistsInDatabase(final String persistenceUnitName,
			final Object entitityContainingIdentity) {

		if (!existsInDatabase(persistenceUnitName, entitityContainingIdentity)) {
			throwAssertFailedWithMessage(persistenceUnitName,
					entitityContainingIdentity.getClass().getSimpleName() + UNAVAILABLE);
		}
		return;
	}

	public static <ENTITY> void assertNotExistsInDatabase(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object entityIdentity) {

		if (existsInDatabase(persistenceUnitName, entityClass, entityIdentity)) {
			throwAssertFailedWithMessage(persistenceUnitName, entityClass.getSimpleName() + UNEXPECTEDLY_AVAILABLE); // Covered.
		}
		return;
	}

	public static void assertNotExistsInDatabase(final String persistenceUnitName,
			final Object entityContainingIdentity) {

		if (existsInDatabase(persistenceUnitName, entityContainingIdentity)) {
			throwAssertFailedWithMessage(persistenceUnitName,
					entityContainingIdentity.getClass().getSimpleName() + UNEXPECTEDLY_AVAILABLE);
		}
		return;
	}

	private static void cascadeFinallyBlock(final JstAssertCascadePO assertCascadePO, final EntityManager entityManager,
			final boolean createSucceededPopulatedEntity, final boolean populatedEntityDeleteAttempted,
			final boolean remainingEntitiesDeleted) {

		try {
			if (createSucceededPopulatedEntity) {
				if (!populatedEntityDeleteAttempted) {
					deleteParentEntity(assertCascadePO, entityManager);
				}
				if (!remainingEntitiesDeleted) {
					deleteRemainingEntities(assertCascadePO, entityManager);
				}
			}
		} finally {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}

	}

	private static void commitPopulatedEntity(final JstAssertCascadePO assertCascadePO,
			final EntityManager cascadeEntityManager) {

		try {

			final JstTransactionPO transactPO = JstTransactionPO
					.withPersistenceUnitName(assertCascadePO.getPersistenceUnitName())
					.withEntityManager(cascadeEntityManager)
					.withCreateAndUpdateEntities(assertCascadePO.getPopulatedEntity());

			assertCascadePO.replacePopulatedEntity(JstTransactionRM.commitSingleInOneTransaction(transactPO));

		} catch (final Exception e) {

			throw new AssertionFailedError("The populated entity could not be processed due to an exception ["
					+ e.getClass().getSimpleName() + "].");
		}
	}

	private static void deleteParentEntity(final JstAssertCascadePO assertCascadePO,
			final EntityManager entityManager) {

		JstTransactionUtilHelper.findAndDeleteEntity(assertCascadePO.getPersistenceUnitName(), entityManager,
				assertCascadePO.getPopulatedEntity());
	}

	private static void deleteRemainingEntities(final JstAssertCascadePO assertCascadePO,
			final EntityManager entityManager) {

		for (final String methodName : assertCascadePO.getAfterTheTestCleanupList()) {

			try {

				final Optional<Object> optionalEntityOrList = JstReflectionUtilHelper.invokePublicMethod(methodName,
						assertCascadePO.getPopulatedEntity());

				if (optionalEntityOrList.isPresent()) {

					final Object entityOrList = optionalEntityOrList.get();

					if (entityOrList instanceof List) {

						@SuppressWarnings("unchecked")
						final List<Object> entityList = (List<Object>) entityOrList;

						for (final Object entity : entityList) {

							JstTransactionUtilHelper.findAndDeleteEntity(assertCascadePO.getPersistenceUnitName(),
									entityManager, entity);
						}
					} else {
						JstTransactionUtilHelper.findAndDeleteEntity(assertCascadePO.getPersistenceUnitName(),
								entityManager, entityOrList);
					}
				} else {
					throw new AssertionFailedError("The attempt to clean up remaining entities with a ["
							+ assertCascadePO.getPopulatedEntity().getClass().getSimpleName() + "] method ["
							+ methodName + "] was unsuccessful.");
				}
			} catch (@SuppressWarnings("unused") final Exception e) {
				throwAssertCascadeFailed(assertCascadePO);
			}
		}
	}

	/**
	 * This method forces a trip to the database.
	 *
	 * @return boolean
	 */
	private static <ENTITY> boolean existsInDatabase(final String persistenceUnitName, final Class<ENTITY> entityClass,
			final Object entityIdentity) {

		final JstFindSinglePO findSingleJpaPO = JstFindSinglePO.withPersistenceUnitName(persistenceUnitName)
				.withEntityClass(entityClass).withEntityIdentity(entityIdentity).withReadOnly()
				.withForceDatabaseTripWhenNoCacheCoordination();

		return JstFindUtilHelper.findSingle(findSingleJpaPO).isPresent();

	}

	/**
	 * This method forces a trip to the database.
	 *
	 * @return boolean
	 */
	private static boolean existsInDatabase(final String persistenceUnitName, final Object entityContainingIdentity) {

		final JstFindSinglePO findSingleJpaPO = JstFindSinglePO.withPersistenceUnitName(persistenceUnitName)
				.withEntityContainingIdentity(entityContainingIdentity).withReadOnly()
				.withForceDatabaseTripWhenNoCacheCoordination();

		return JstFindUtilHelper.findSingle(findSingleJpaPO).isPresent();

	}

	private static boolean isExists(final List<String> existsList, final JstAssertCascadePO assertCascadePO,
			final boolean expected) {

		for (final String methodName : existsList) {

			final Optional<Object> optionalEntityOrList = JstReflectionUtilHelper.invokePublicMethod(methodName,
					assertCascadePO.getPopulatedEntity());

			if (optionalEntityOrList.isPresent()) {

				final Object entityOrList = optionalEntityOrList.get();

				if (entityOrList instanceof List) {

					@SuppressWarnings("unchecked")
					final List<Object> entityList = (List<Object>) entityOrList;

					for (final Object entityContainingIdentity : entityList) {

						final boolean actual = existsInDatabase(assertCascadePO.getPersistenceUnitName(),
								entityContainingIdentity);

						if (expected != actual) {
							return false;
						}
					}
				} else {
					final boolean actual = existsInDatabase(assertCascadePO.getPersistenceUnitName(), entityOrList);
					if (expected != actual) {
						return false;
					}
				}

			} else {
				throwAssertCascadeFailed(assertCascadePO); // Covered.
			}
		}
		return true;
	}

	private static void throwAssertCascadeFailed(final JstAssertCascadePO assertCascadePO) {

		throw new AssertionFailedError("A cascade type error has occured for the domain entity ["
				+ assertCascadePO.getPopulatedEntity().getClass().getSimpleName() + "].\n\t\t"
				+ "Compare the cascades with the test method to determine which are incorrect.");
	}

	private static void throwAssertFailedWithMessage(final String persistenceUnitName, final String message) {

		final StringBuilder assertionFailedMessage = new StringBuilder();

		assertionFailedMessage.append("An instance of ");
		assertionFailedMessage.append(message);
		assertionFailedMessage.append(" in the database [");
		assertionFailedMessage.append(persistenceUnitName);
		assertionFailedMessage.append("].");

		throw new AssertionFailedError(assertionFailedMessage.toString());
	}

	private static void verifyCascadeEntitiesDeleted(final JstAssertCascadePO assertCascadePO) {

		final boolean actual = isExists(assertCascadePO.getCascadeRemoveList(), assertCascadePO, false);

		if (!actual) {
			throwAssertCascadeFailed(assertCascadePO); // Covered.
		}
	}

	private static void verifyCascadeEntitiesPersisted(final JstAssertCascadePO assertCascadePO) {

		final boolean actual = isExists(assertCascadePO.getCascadePersistList(), assertCascadePO, true);

		if (!actual) {
			throwAssertCascadeFailed(assertCascadePO);
		}
	}

	private static void verifyEntitiesNotDeleted(final JstAssertCascadePO assertCascadePO) {

		final boolean actual = isExists(assertCascadePO.getCascadeRemoveList(), assertCascadePO, false);

		if (!actual) {
			throwAssertCascadeFailed(assertCascadePO);
		}
	}

	private static void verifyEntitiesNotPersisted(final JstAssertCascadePO assertCascadePO) {

		final boolean actual = isExists(assertCascadePO.getCascadeNoPersistList(), assertCascadePO, false);

		if (!actual) {
			throwAssertCascadeFailed(assertCascadePO); // Covered.
		}
	}
}
