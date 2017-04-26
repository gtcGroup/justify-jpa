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
package com.gtcgroup.justify.jpa.helper;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;
import com.gtcgroup.justify.core.helper.internal.ReflectionUtilHelper;
import com.gtcgroup.justify.jpa.po.JstTransactionJpaPO;

/**
 * This Helper class provides persistence transaction support.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstTransactionUtilHelper {

	@SuppressWarnings("javadoc")
	INSTANCE;

	/**
	 * This convenience method guarantees deletion as long as the entity
	 * identity is specified.
	 */
	public static <ENTITY> void findAndDeleteEntity(final EntityManager entityManager,
			final ENTITY entityWithIdentity) {

		ENTITY entity = JstFindUtilHelper.findForceDatabaseTrip(entityManager, entityWithIdentity, false);

		if (null == entity) {
			return;
		}

		entityManager.getTransaction().begin();
		entity = entityManager.merge(entity);
		entityManager.remove(entity);
		entityManager.getTransaction().commit();
	}

	/**
	 * This convenience method guarantees deletion as long as the entity
	 * identity is specified.
	 */
	public static <ENTITY> void findAndDeleteEntity(final String persistenceUnitName, final ENTITY entityWithIdentity) {

		findAndDeleteEntity(JstEntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName),
				entityWithIdentity);
	}

	/**
	 * This convenience method guarantees deletion of child objects typically
	 * not marked for cascading remove that needs removal programmatically from
	 * a parent entity.
	 */
	public static <ENTITY> void findAndDeleteRelatedEntity(final EntityManager entityManager,
			final Object entityWithReleatedEnitityContainingIdentity, final String relatedEntityGetterMethodName) {

		@SuppressWarnings("unchecked")
		final ENTITY entity = (ENTITY) ReflectionUtilHelper.invokePublicMethod(relatedEntityGetterMethodName,
				entityWithReleatedEnitityContainingIdentity);

		if (null == entity) {
			throw new JustifyRuntimeException("The entity represented by the method [" + relatedEntityGetterMethodName
					+ "] could not be found for deletion (removal).");
		}

		findAndDeleteEntity(entityManager, entity);
	}

	/**
	 * @return {@link List}
	 */
	@SuppressWarnings("unchecked")
	private static <ENTITY, PO extends JstTransactionJpaPO> List<ENTITY> mergeEntities(final PO transactionPO) {

		final List<ENTITY> entityMergeList = new ArrayList<ENTITY>();

		if (transactionPO.isEntityMerge()) {

			for (Object entity : transactionPO.getEntityMergeList()) {

				entity = transactionPO.getEntityManager().merge(entity);
				entityMergeList.add((ENTITY) entity);
			}
		}
		return entityMergeList;
	}

	private static <PO extends JstTransactionJpaPO> void removeEntities(final PO transactionPO) {

		if (transactionPO.isEntityDelete()) {

			for (Object entity : transactionPO.getEntityDeleteList()) {

				entity = transactionPO.getEntityManager().merge(entity);
				transactionPO.getEntityManager().remove(entity);
			}
		}
		return;
	}

	/**
	 * This method is used for committing multiple entities. If any of the
	 * related child objects are not marked for an applicable
	 * {@link CascadeType} then they need to be explicitly in the
	 * {@link JstTransactionJpaPO}.
	 *
	 * @return {@link List}
	 */
	public static <ENTITY, PO extends JstTransactionJpaPO> List<ENTITY> transactEntities(final PO transactionPO) {

		List<ENTITY> entityMergeList;

		try {

			transactionPO.getEntityManager().getTransaction().begin();

			entityMergeList = mergeEntities(transactionPO);
			removeEntities(transactionPO);

			transactionPO.getEntityManager().getTransaction().commit();
			transactionPO.replaceEntityMergeList(entityMergeList);

		} catch (final RuntimeException e) {

			throw new JustifyRuntimeException(e);

		} finally {

			transactionPO.closeEntityManagerIfCreatedWithPersistenceUnitName();
		}
		return entityMergeList;
	}

	/**
	 * This method is used for committing a single entity. If any of the related
	 * child objects are not marked for an applicable {@link CascadeType} then
	 * they need to be explicitly in the {@link JstTransactionJpaPO}.
	 *
	 * @return {@link Object}
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY, PO extends JstTransactionJpaPO> ENTITY transactEntity(final PO transactionPO) {

		return (ENTITY) transactEntities(transactionPO).get(0);
	}
}