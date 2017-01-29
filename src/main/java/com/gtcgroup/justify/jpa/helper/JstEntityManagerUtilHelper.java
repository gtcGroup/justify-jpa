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
import java.util.Map;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;

/**
 * This Util Helper class provides persistence {@link EntityManager} support.
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

	/** Optimization for read-only. */
	public static final Map<String, Object> FIND_READ_ONLY = new HashMap<String, Object>();

	/**
	 * Optimization for reading from the database. Often used when there are
	 * multiple servers and no cache coordination.
	 */
	public static final Map<String, Object> FIND_FORCING_DATABASE_TRIP = new HashMap<String, Object>();

	/**
	 * Optimization for reading from the database only. That is, cache is NOT
	 * impacted.
	 */
	public static final Map<String, Object> FIND_FORCING_DATABASE_TRIP_AND_READ_ONLY = new HashMap<String, Object>();

	static {

		JstEntityManagerUtilHelper.FIND_READ_ONLY.put(QueryHints.READ_ONLY, HintValues.TRUE);

		JstEntityManagerUtilHelper.FIND_FORCING_DATABASE_TRIP.put(QueryHints.CACHE_RETRIEVE_MODE,
				CacheRetrieveMode.BYPASS);

		JstEntityManagerUtilHelper.FIND_FORCING_DATABASE_TRIP_AND_READ_ONLY.put(QueryHints.CACHE_RETRIEVE_MODE,
				CacheRetrieveMode.BYPASS);

		JstEntityManagerUtilHelper.FIND_FORCING_DATABASE_TRIP_AND_READ_ONLY.put(QueryHints.READ_ONLY, HintValues.TRUE);
	}

	/**
	 * This method clears the persistence context (final L1 cache), causing all
	 * managed entities to become detached. Changes made to entities that have
	 * not been flushed to the database will not be persisted.
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
	 */
	public static void detachEntityFromPersistenceContext(final EntityManager entityManager,
			final Object populatedEntity) {

		try {
			entityManager.detach(populatedEntity);

		} catch (final Exception e) {

			throw new JustifyRuntimeException(e);
		}
		return;
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
			throw new JustifyRuntimeException(e);
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
					JstEntityManagerUtilHelper.FIND_FORCING_DATABASE_TRIP_AND_READ_ONLY);

			if (null != result) {
				evictEntityInstanceFromSharedCache(entityManager, result);
			}
		}

	}

	/**
	 * This method forces a trip to the database without altering the state of
	 * cache.
	 *
	 * @return boolean
	 */
	public static <ENTITY> boolean existsInDatabaseWithEntityIdentities(final EntityManager entityManager,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		Object result;

		try {

			for (final Object entityIdentity : entityIdentities) {

				result = entityManager.find(entityClass, entityIdentity,
						JstEntityManagerUtilHelper.FIND_FORCING_DATABASE_TRIP_AND_READ_ONLY);

				if (null == result) {
					return false;
				}
			}

		} catch (@SuppressWarnings("unused") final Exception e) {

			return false;
		}
		return true;
	}

	/**
	 * This method forces a trip to the database without altering the state of
	 * cache.
	 *
	 * @return boolean
	 */
	public static boolean existsInDatabaseWithPopulatedEntities(final EntityManager entityManager,
			final Object... populatedEntities) {

		Object result;

		try {

			for (final Object populatedEntity : populatedEntities) {

				result = entityManager.find(populatedEntity.getClass(),
						retrieveIdentity(entityManager, populatedEntity),
						JstEntityManagerUtilHelper.FIND_FORCING_DATABASE_TRIP_AND_READ_ONLY);

				if (null == result) {
					return false;
				}
			}

		} catch (@SuppressWarnings("unused") final Exception e) {

			return false;
		}
		return true;
	}

	/**
	 * This method checks if the instance is a managed entity instance belonging
	 * to the current persistence context (L1 cache).
	 *
	 * @return boolean
	 */
	public static boolean existsInPersistenceContextWithManagedEntities(final EntityManager entityManager,
			final Object... managedEntities) {

		boolean result = true;

		try {

			for (final Object managedEntity : managedEntities) {

				result = entityManager.contains(managedEntity);

				if (false == result) {
					return result;
				}
			}
		} catch (final Exception e) {

			throw new JustifyRuntimeException(e);
		}
		return result;
	}

	/**
	 * This method checks if the instance is a managed entity instance belonging
	 * to the current persistence context (L1 cache).
	 *
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY> boolean existsInPersistenceContextWithPopulatedEntities(final EntityManager entityManager,
			final boolean readOnly, final Object... populatedEntities) {

		boolean result = true;

		try {

			for (final Object populatedEntity : populatedEntities) {

				ENTITY entity = null;

				if (readOnly) {
					entity = (ENTITY) findReadOnlySingleOrNull(entityManager, populatedEntity.getClass(),
							retrieveIdentity(entityManager, populatedEntity));
				} else {
					entity = (ENTITY) findModifiableSingleOrNull(entityManager, populatedEntity.getClass(),
							retrieveIdentity(entityManager, populatedEntity));
				}

				result = entityManager.contains(entity);

				if (false == result) {
					return result;
				}
			}
		} catch (final Exception e) {

			throw new JustifyRuntimeException(e);
		}
		return result;
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
	public static boolean existsInSharedCacheWithPopulatedEntities(final EntityManager entityManager,
			final Object... populatedEntities) {

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
	 * @return {@link Object}
	 */
	public static <ENTITY> ENTITY findModifiableSingleOrNull(final EntityManager entityManager,
			final Class<ENTITY> entityClass, final Object entityIdentity) {

		return entityManager.find(entityClass, entityIdentity);
	}

	/**
	 * @return {@link Object} or null
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY> ENTITY findModifiableSingleOrNull(final EntityManager entityManager,
			final Object populatedEntity) {

		return (ENTITY) entityManager.find(populatedEntity.getClass(),
				retrieveIdentity(entityManager, populatedEntity));
	}

	/**
	 * @return {@link Object} or null
	 */
	public static <ENTITY> ENTITY findReadOnlySingleOrNull(final EntityManager entityManager,
			final Class<ENTITY> entityClass, final Object entityIdentity) {

		try {
			return entityManager.find(entityClass, entityIdentity, JstEntityManagerUtilHelper.FIND_READ_ONLY);
		} catch (final Exception e) {
			throw new JustifyRuntimeException(e);
		}
	}

	/**
	 * @return {@link Object} or null
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY> ENTITY findReadOnlySingleOrNull(final EntityManager entityManager,
			final Object populatedEntity) {

		return (ENTITY) entityManager.find(populatedEntity.getClass(), retrieveIdentity(entityManager, populatedEntity),
				JstEntityManagerUtilHelper.FIND_READ_ONLY);
	}

	/**
	 * @return Object
	 */
	public static Object retrieveIdentity(final EntityManager entityManager, final Object populatedEntity) {

		return entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(populatedEntity);
	}
}
