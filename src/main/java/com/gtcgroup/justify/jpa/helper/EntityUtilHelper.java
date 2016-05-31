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
package com.gtcgroup.justify.jpa.helper;

import javax.persistence.EntityManager;

import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;
import com.gtcgroup.justify.jpa.rm.QueryRM;

/**
 * This Util Helper class provides persistence {@link EntityManager} support.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum EntityUtilHelper {

	@SuppressWarnings("javadoc")
	INSTANCE;

	/**
	 * This method clears the persistence context (final L1 cache), causing all
	 * managed entities to become detached. Changes made to entities that have
	 * not been flushed to the database will not be persisted.
	 *
	 * @param entityManager
	 */
	public static void clearAllInstancesFromPersistenceContext(final EntityManager entityManager) {

		entityManager.clear();
	}

	/**
	 * This method removes the given entity from the persistence context,
	 * causing a managed entity to become detached. Unflushed changes made to
	 * the entity if any (final including removal of the entity), will not be
	 * synchronized to the database. Entities which previously referenced the
	 * detached entity will continue to reference it.
	 *
	 * @param entityContainingIdentity
	 * @param entityManager
	 */
	public static void detachEntityFromPersistenceContext(final Object entityContainingIdentity,
			final EntityManager entityManager) {

		try {
			entityManager.detach(entityContainingIdentity);

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		}
		return;
	}

	/**
	 * This method clears the shared (L2) cache of all instances.
	 *
	 * @param entityManager
	 */
	public static void evictAllEntitiesFromSharedCache(final EntityManager entityManager) {

		entityManager.getEntityManagerFactory().getCache().evictAll();
	}

	/**
	 * This method clears the shared (L2) cache of a single instance.
	 *
	 * @param entity
	 * @param entityIdentity
	 * @param entityManager
	 */
	public static void evictEntityInstanceFromSharedCache(final Object entity, final Object entityIdentity,
			final EntityManager entityManager) {

		entityManager.getEntityManagerFactory().getCache().evict(entity.getClass(), entityIdentity);
	}

	/**
	 * This method clears the shared (L2) cache of all instances of a single
	 * object type.
	 *
	 * @param <ENTITY>
	 * @param entityClass
	 * @param entityManager
	 */
	public static <ENTITY extends Object> void evictEntityInstancesFromSharedCache(final Class<ENTITY> entityClass,
			final EntityManager entityManager) {

		entityManager.getEntityManagerFactory().getCache().evict(entityClass);
	}

	/**
	 * This method forces a trip to the database without altering the state of
	 * cache.
	 *
	 * @param entity
	 * @param entityIdentity
	 * @param entityManager
	 * @return boolean
	 */
	public static boolean existsInDatabase(final Object entity, final Object entityIdentity,
			final EntityManager entityManager) {

		Object result;

		try {

			result = entityManager.find(entity.getClass(), entityIdentity,
					QueryRM.FIND_FORCING_DATABASE_TRIP_WITH_NO_IMPACT_TO_CACHE);

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		}
		return null != result;
	}

	/**
	 * This method checks if the instance is a managed entity instance belonging
	 * to the current persistence context (L1 cache).
	 *
	 * @param entityContainingIdentity
	 * @param entityManager
	 * @return boolean
	 */
	public static boolean existsInPersistenceContext(final Object entityContainingIdentity,
			final EntityManager entityManager) {

		final boolean result;

		try {
			result = entityManager.contains(entityContainingIdentity);

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		}
		return result;
	}

	/**
	 * This method determines whether the shared (L2) cache contains the given
	 * entity.
	 *
	 * @param entity
	 * @param entityIdentity
	 * @param entityManager
	 * @return boolean
	 */
	public static boolean existsInSharedCache(final Object entity, final Object entityIdentity,
			final EntityManager entityManager) {

		final boolean result;

		try {
			result = entityManager.getEntityManagerFactory().getCache().contains(entity.getClass(), entityIdentity);

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		}
		return result;
	}
}
