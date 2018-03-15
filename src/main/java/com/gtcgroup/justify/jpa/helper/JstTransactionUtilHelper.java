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
package com.gtcgroup.justify.jpa.helper;

import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;

import com.gtcgroup.justify.core.helper.JstReflectionUtilHelper;
import com.gtcgroup.justify.core.po.JstExceptionPO;
import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.jpa.exception.JstOptimisiticLockException;
import com.gtcgroup.justify.jpa.po.JstFindSinglePO;
import com.gtcgroup.justify.jpa.po.JstTransactionPO;

/**
 * This Helper class provides persistence transaction support.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstTransactionUtilHelper {

	INSTANCE;

	/**
	 * This method is used for committing multiple entities. If any of the related
	 * child objects are not marked for an applicable {@link CascadeType} then they
	 * need to be explicitly in the {@link JstTransactionPO}.
	 *
	 * @return {@link Optional}
	 * @throws JstOptimisiticLockException
	 */
	public static <ENTITY> Optional<List<ENTITY>> commitEntitiesInSingleTransaction(
			final JstTransactionPO transactionPO) {

		try {

			final EntityManager entityManager = transactionPO.getEntityManager();

			entityManager.getTransaction().begin();

			mergeCreateAndUpdates(entityManager, transactionPO);
			deleteEntities(entityManager, transactionPO);

			entityManager.getTransaction().commit();

			if (transactionPO.getEntityCommittedList().isEmpty()) {
				return Optional.empty();
			}
			return Optional.of(transactionPO.getEntityCommittedList());

		} catch (final javax.persistence.OptimisticLockException
				| org.eclipse.persistence.exceptions.OptimisticLockException e) {

			throw new JstOptimisiticLockException(JstExceptionPO.withMessage(e.getMessage())
					.withExceptionClassName(JstTransactionUtilHelper.class.getSimpleName())
					.withExceptionMethodName("transactEntities"));

		} finally {

			transactionPO.closeEntityManager();
		}
	}

	/**
	 * return boolean
	 */
	public static <ENTITY> boolean findAndDeleteEntity(final String persistenceUnitName,
			final EntityManager entityManager, final ENTITY entityContainingIdentity) {

		final Optional<ENTITY> optionalEntity = JstFindUtilHelper
				.findSingle(JstFindSinglePO.withPersistenceUnitName(persistenceUnitName)
						.withEntityManager(entityManager).withEntityContainingIdentity(entityContainingIdentity));

		if (optionalEntity.isPresent()) {

			entityManager.getTransaction().begin();
			final ENTITY entity = entityManager.merge(optionalEntity.get());
			entityManager.remove(entity);
			entityManager.getTransaction().commit();
		}
		return true;
	}

	/**
	 * This convenience method deletes of child objects (typically not marked for
	 * cascading remove) that require programmatic delete from a parent entity.
	 *
	 * return boolean
	 */
	public static <ENTITY> boolean findAndDeleteRelatedEntity(final String persistenceUnitName,
			final Object entityWithReleatedEnitity, final String relatedEntityGetterMethodName,
			final EntityManager entityManager) {

		@SuppressWarnings("unchecked")
		final Optional<ENTITY> entity = (Optional<ENTITY>) JstReflectionUtilHelper
				.invokePublicMethod(relatedEntityGetterMethodName, entityWithReleatedEnitity);

		if (!entity.isPresent()) {
			throw new JustifyException(JstExceptionPO
					.withMessage("The entity represented by the method [" + relatedEntityGetterMethodName
							+ "] could not be found for deletion (removal).")
					.withExceptionClassName(JstTransactionUtilHelper.class.getSimpleName())
					.withExceptionMethodName("findAndDeleteRelatedEntity"));
		}

		return findAndDeleteEntity(persistenceUnitName, entityManager, entity);
	}

	private static void deleteEntities(final EntityManager entityManager, final JstTransactionPO transactionPO) {

		for (Object entity : transactionPO.getEntityDeleteList()) {

			entity = entityManager.merge(entity);
			entityManager.remove(entity);
			transactionPO.addEntityCommitted(entity);
		}
	}

	private static void mergeCreateAndUpdates(final EntityManager entityManager,
			final JstTransactionPO transactionPO) {

		for (Object entity : transactionPO.getEntityCreateAndUpdateList()) {

			entity = entityManager.merge(entity);
			transactionPO.addEntityCommitted(entity);
		}
	}
}