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

import javax.persistence.EntityManager;

import com.gtcgroup.justify.core.test.exception.internal.JustifyException;

/**
 * This Helper class provides persistence {@link EntityManager} support.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstEntityManagerUtilHelper {

	@SuppressWarnings("javadoc")
	INSTANCE;

	/**
	 * This method clears the persistence context (final L1 cache), causing all
	 * managed entities to become detached. Changes made to entities that have
	 * not been flushed to the database will not be persisted.
	 */
	public static void clearAllInstancesFromPersistenceContext(final EntityManager entityManager) {

		entityManager.clear();
	}

	/**
	 * This method clears the shared (L2) cache of all instances.
	 */
	public static void evictAllEntitiesFromSharedCache(final EntityManager entityManager) {

		entityManager.getEntityManagerFactory().getCache().evictAll();
	}

	/**
	 * This method clears the shared (L2) cache of a single instance.
	 */
	public static void evictEntityInstanceFromSharedCache(final EntityManager entityManager,
			final Object populatedEntity) {

		try {
			entityManager.getEntityManagerFactory().getCache().evict(populatedEntity.getClass(),
					retrieveIdentity(entityManager, populatedEntity));
		} catch (final Exception e) {
			throw new JustifyException(e);
		}
	}

	/**
	 * This method clears the shared (L2) cache of all instances of a single
	 * object type.
	 */
	public static <ENTITY extends Object> void evictEntityInstancesFromSharedCache(final EntityManager entityManager,
			final Class<ENTITY> entityClass) {

		entityManager.getEntityManagerFactory().getCache().evict(entityClass);
	}

	/**
	 * This method clears the shared (L2) cache of all instances of a single
	 * object type.
	 */
	public static <ENTITY extends Object> void evictEntityInstancesFromSharedCache(final EntityManager entityManager,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		Object result;

		for (final Object entityIdentity : entityIdentities) {

			result = entityManager.find(entityClass, entityIdentity,
					JstFindUtilHelper.FORCE_DATABASE_TRIP);

			if (null != result) {
				evictEntityInstanceFromSharedCache(entityManager, result);
			}
		}

	}

	/**
	 * @return Object
	 */
	public static Object retrieveIdentity(final EntityManager entityManager, final Object populatedEntity) {

		return entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(populatedEntity);
	}
}
