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

package com.gtcgroup.justify.jpa.assertions;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.opentest4j.AssertionFailedError;

import com.gtcgroup.justify.core.helper.JstReflectionUtilHelper;
import com.gtcgroup.justify.core.po.JstExceptionPO;
import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.JstFindUtilHelper;
import com.gtcgroup.justify.jpa.helper.JstTransactionUtilHelper;
import com.gtcgroup.justify.jpa.po.JstAssertCascadeJpaPO;
import com.gtcgroup.justify.jpa.po.JstFindListJpaPO;
import com.gtcgroup.justify.jpa.po.JstFindSingleJpaPO;
import com.gtcgroup.justify.jpa.po.JstTransactionJpaPO;
import com.gtcgroup.justify.jpa.rm.JstTransactionJpaRM;

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

	private static final String DATABASE = "database";
	private static JstAssertCascadeJpaPO assertionsJpaCascadePO;
	private static EntityManager entityManagerForCascadeTypes;
	private static String persistenceUnitNameForCascadeTypes;
	private static Object parentEntityForCascadeTypes;

	/**
	 * This method verifies cascade annotations. If properly executed, it will
	 * remove any persisted instances used for verification.
	 *
	 * return {@link Optional}
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY> Optional<ENTITY> assertCascadeTypes(final JstAssertCascadeJpaPO assertionsCascadePO) {

		AssertionsJPA.assertionsJpaCascadePO = assertionsCascadePO;

		final Optional<EntityManager> entityManager = JstEntityManagerFactoryCacheHelper
				.createEntityManagerToBeClosed(assertionsCascadePO.getPersistenceUnitName());

		if (entityManager.isPresent()) {
			AssertionsJPA.entityManagerForCascadeTypes = entityManager.get();
			AssertionsJPA.persistenceUnitNameForCascadeTypes = assertionsCascadePO.getPersistenceUnitName();

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
			return (Optional<ENTITY>) Optional.of(AssertionsJPA.parentEntityForCascadeTypes);
		}
		return Optional.empty();
	}

	public static <ENTITY> void assertExistsInDatabase(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object entityIdentity) {

		final Optional<ENTITY> entity = JstFindUtilHelper
				.findSingle(JstFindSingleJpaPO.withPersistenceUnitName(persistenceUnitName).withEntityClass(entityClass)
						.withEntityIdentity(entityIdentity));

		if (!entity.isPresent()) {
			assertFailedWithMessage(persistenceUnitName, entityClass, AssertionsJPA.DATABASE, "instance unavailable");
		}
		return;
	}

	public static void assertExistsInDatabase(final String persistenceUnitName,
			final Object entitityContainingIdentity) {

		if (!existsInDatabase(persistenceUnitName, entitityContainingIdentity)) {
			assertFailedWithMessage(persistenceUnitName, null, AssertionsJPA.DATABASE, "instance unavailable");
		}
		return;
	}

	public static <ENTITY> void assertNotExistsInDatabase(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object entityIdentity) {

		final Optional<ENTITY> entity = JstFindUtilHelper
				.findSingle(JstFindSingleJpaPO.withPersistenceUnitName(persistenceUnitName).withEntityClass(entityClass)
						.withEntityIdentity(entityIdentity));

		if (entity.isPresent()) {
			assertFailedWithMessage(persistenceUnitName, entityClass, AssertionsJPA.DATABASE, "instance available");
		}
		return;
	}

	public static void assertNotExistsInDatabase(final String persistenceUnitName,
			final Object entityContainingIdentity) {

		final boolean exists = existsInDatabase(persistenceUnitName, entityContainingIdentity);

		if (exists) {
			assertFailedWithMessage(persistenceUnitName, null, AssertionsJPA.DATABASE,
					"instance unexpectedly available");
		}
		return;
	}

	private static <ENTITY> void assertFailedWithMessage(final String persistenceUnitName,
			final Class<ENTITY> entityClassOrNull, final String fromWhere, final String availableOrUnavailable) {

		final StringBuilder assertionFailedMessage = new StringBuilder();

		assertionFailedMessage.append("An ");

		if (null != entityClassOrNull) {
			assertionFailedMessage.append("of [");
			assertionFailedMessage.append(entityClassOrNull.getSimpleName());
			assertionFailedMessage.append("] ");
		}

		assertionFailedMessage.append(availableOrUnavailable);
		assertionFailedMessage.append(" from the ");
		assertionFailedMessage.append(fromWhere + " [");
		assertionFailedMessage.append(persistenceUnitName);
		assertionFailedMessage.append("].");

		throw new AssertionFailedError(assertionFailedMessage.toString());
	}

	private static void cascadeError() {

		Assert.fail("A cascade error has occured for the domain entity ["
				+ AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity().getClass().getSimpleName() + "].\n\t\t"
				+ "Check both the domain entity and the test Parameter Object to determine the source of the error.");
	}

	private static <ENTITY> void catchBlock(final Exception e) {

		final Optional<List<ENTITY>> entityList = JstTransactionUtilHelper.transactEntities(
				JstTransactionJpaPO.withPersistenceUnitName(AssertionsJPA.persistenceUnitNameForCascadeTypes)
						.withEntityManager(AssertionsJPA.entityManagerForCascadeTypes)
						.withDeleteEntities(AssertionsJPA.parentEntityForCascadeTypes));

		if (entityList.isPresent()) {
			return;
		}

		throw new JustifyException(
				JstExceptionPO.withMessage(e.getMessage()).withExceptionClassName(AssertionsJPA.class.getSimpleName())
						.withExceptionMethodName("assertCascadeTypes"));
	}

	private static void createParentEntity() {

		Object parentEntity = null;

		final JstTransactionJpaPO transactPO = JstTransactionJpaPO
				.withPersistenceUnitName(AssertionsJPA.persistenceUnitNameForCascadeTypes)
				.withEntityManager(AssertionsJPA.entityManagerForCascadeTypes)
				.withCreateAndUpdateEntities(AssertionsJPA.assertionsJpaCascadePO.getPopulatedEntity());

		parentEntity = JstTransactionJpaRM.transactEntity(transactPO);

		AssertionsJPA.parentEntityForCascadeTypes = parentEntity;
		AssertionsJPA.assertionsJpaCascadePO.replacePopulatedEntity(parentEntity);
	}

	private static void deleteParentEntity() {

		if (null != AssertionsJPA.parentEntityForCascadeTypes) {

			JstTransactionUtilHelper.findAndDeleteEntity(AssertionsJPA.persistenceUnitNameForCascadeTypes,
					AssertionsJPA.parentEntityForCascadeTypes);
		}
	}

	private static void deleteRemainingEntities() {

		if (null != AssertionsJPA.parentEntityForCascadeTypes) {
			for (final String method : AssertionsJPA.assertionsJpaCascadePO.getAfterTheTestCleanupList()) {

				try {
					JstTransactionUtilHelper.findAndDeleteRelatedEntity(
							AssertionsJPA.persistenceUnitNameForCascadeTypes, AssertionsJPA.parentEntityForCascadeTypes,
							method);
				} catch (@SuppressWarnings("unused") final Exception e) {
					// Ignore
				}
			}
		}
	}

	/**
	 * This method forces a trip to the database.
	 *
	 * @return boolean
	 */
	private static boolean existsInDatabase(final String persistenceUnitName, final Object entityContainingIdentity) {

		final JstFindSingleJpaPO findSingleJpaPO = JstFindSingleJpaPO.withPersistenceUnitName(persistenceUnitName)
				.withEntityContainingIdentity(entityContainingIdentity).withReadOnly()
				.withForceDatabaseTripWhenNoCacheCoordination();

		return JstFindUtilHelper.findSingle(findSingleJpaPO).isPresent();

	}

	private static void finallyBlock() {

		JstEntityManagerFactoryCacheHelper.closeEntityManager(AssertionsJPA.entityManagerForCascadeTypes);
		AssertionsJPA.entityManagerForCascadeTypes = null;

		deleteParentEntity();
		deleteRemainingEntities();

	}

	private static boolean isExists(final List<String> existsList, final boolean expected) {

		for (final String methodName : existsList) {

			final Object entityOrList = JstReflectionUtilHelper.invokePublicMethod(methodName,
					AssertionsJPA.parentEntityForCascadeTypes);

			final boolean actual = JstFindUtilHelper.existsInDatabase(
					JstFindListJpaPO.withPersistenceUnitName(AssertionsJPA.persistenceUnitNameForCascadeTypes)
							.withEntityManager(AssertionsJPA.entityManagerForCascadeTypes)
							.withEntitiesContainingIdentity(entityOrList));
			if (expected != actual) {
				return false;
			}
		}
		return true;
	}

	private static void verifyCascadeEntitiesPersisted(final List<String> persistList, final boolean expected) {

		final boolean actual = isExists(persistList, expected);

		if (!actual) {
			cascadeError();
		}
	}

	/**
	 * @throws ClassNotFoundException
	 */
	private static void verifyCascadeEntitiesRemoved(final List<String> persistList, final boolean expected) {

		final boolean actual = isExists(persistList, expected);

		if (!actual) {
			cascadeError();
		}
	}
}
