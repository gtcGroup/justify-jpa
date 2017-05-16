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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.CascadePolicy;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;

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
public enum JstFindUtilHelper {

	@SuppressWarnings("javadoc")
	INSTANCE;

	/** Optimization for read-only. */
	public static final Map<String, Object> CHECK_CACHE_ONLY = new HashMap<String, Object>();

	/**
	 * Optimization for reading from the database. Often used when there are
	 * multiple servers and no cache coordination.
	 */
	public static final Map<String, Object> FORCE_DATABASE_TRIP = new HashMap<String, Object>();

	static {

		JstFindUtilHelper.CHECK_CACHE_ONLY.put(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);

		JstFindUtilHelper.FORCE_DATABASE_TRIP.put(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);

		JstFindUtilHelper.FORCE_DATABASE_TRIP.put(QueryHints.CACHE_USAGE, CacheUsage.DoNotCheckCache);

		JstFindUtilHelper.FORCE_DATABASE_TRIP.put(QueryHints.REFRESH_CASCADE, CascadePolicy.CascadeByMapping);

		JstFindUtilHelper.FORCE_DATABASE_TRIP.put(QueryHints.REFRESH, HintValues.TRUE);
	}

	/**
	 * This method forces a trip to the database without altering the state of
	 * cache.
	 *
	 * @return boolean
	 */
	public static <ENTITY> boolean existsInDatabase(final EntityManager entityManager, final Class<ENTITY> entityClass,
			final Object... entityIdentities) {

		Object result;

		for (final Object entityIdentity : entityIdentities) {

			result = entityManager.find(entityClass, entityIdentity, JstFindUtilHelper.FORCE_DATABASE_TRIP);

			if (null == result) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method forces a trip to the database without altering the state of
	 * cache.
	 *
	 * @return boolean
	 */
	public static boolean existsInDatabases(final EntityManager entityManager,
			final Object... entititiesContainingIdentity) {

		Object result;

		try {

			for (final Object entityContainingIdentity : entititiesContainingIdentity) {

				if (entityContainingIdentity instanceof List<?>) {
					List<?> entityList = ((List<?>) entityContainingIdentity);
					if (entityList.isEmpty()) {
						return false;
					}
					return existsInDatabases(entityManager, entityList.toArray());
				}

				result = entityManager.find(entityContainingIdentity.getClass(),
						retrieveIdentity(entityManager, entityContainingIdentity),
						JstFindUtilHelper.FORCE_DATABASE_TRIP);

				if (null == result) {
					return false;
				}
			}

		} catch (final Exception e) {

			return false;
		}
		return true;
	}

	/**
	 * This method determines whether the shared (L2) cache contains the given
	 * entities.
	 *
	 * @return boolean
	 */
	public static <ENTITY> boolean existsInSharedCacheWithEntityIdentities(final EntityManager entityManager,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		boolean result = true;

		try {

			for (final Object entityIdentity : entityIdentities) {

				result = entityManager.getEntityManagerFactory().getCache().contains(entityClass, entityIdentity);

				if (false == result) {
					return false;
				}
			}

		} catch (final Exception e) {

			throw new JustifyRuntimeException(e);
		}
		return result;
	}

	/**
	 * This method determines whether the shared (L2) cache contains the given
	 * persisted entities.
	 *
	 * @return boolean
	 */
	public static boolean existsInSharedCache(final EntityManager entityManager, final Object... populatedEntities) {

		boolean result = true;

		try {

			for (final Object populatedEntity : populatedEntities) {

				result = entityManager.getEntityManagerFactory().getCache().contains(populatedEntity.getClass(),
						retrieveIdentity(entityManager, populatedEntity));

				if (false == result) {
					return false;
				}
			}

		} catch (final Exception e) {

			throw new JustifyRuntimeException(e);
		}
		return result;
	}

	/**
	 * @return {@link Object} or null
	 */
	public static <ENTITY> ENTITY findForceDatabaseTrip(final EntityManager entityManager,
			final Class<ENTITY> entityClass, final Object entityIdentity, final boolean suppressForceDatabaseTrip) {

		try {
			if (suppressForceDatabaseTrip) {
				return entityManager.find(entityClass, entityIdentity);
			}
			return entityManager.find(entityClass, entityIdentity, JstFindUtilHelper.FORCE_DATABASE_TRIP);
		} catch (final Exception e) {
			throw new JustifyRuntimeException(e);
		}
	}

	/**
	 * @return {@link Object} or null
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY> ENTITY findForceDatabaseTrip(final EntityManager entityManager, final Object populatedEntity,
			final boolean suppressForceTripToDatabase) {

		try {
			if (suppressForceTripToDatabase) {
				return (ENTITY) entityManager.find(populatedEntity.getClass(),
						retrieveIdentity(entityManager, populatedEntity));
			}

			return (ENTITY) entityManager.find(populatedEntity.getClass(),
					retrieveIdentity(entityManager, populatedEntity), JstFindUtilHelper.FORCE_DATABASE_TRIP);

		} catch (final Exception e) {
			throw new JustifyRuntimeException(e);
		}
	}

	/**
	 * @return Object
	 */
	public static Object retrieveIdentity(final EntityManager entityManager, final Object populatedEntity) {

		return entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(populatedEntity);
	}
}
