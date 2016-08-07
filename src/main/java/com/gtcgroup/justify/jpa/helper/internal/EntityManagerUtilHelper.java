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
package com.gtcgroup.justify.jpa.helper.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;

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
public enum EntityManagerUtilHelper {

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

		EntityManagerUtilHelper.FIND_READ_ONLY.put(QueryHints.READ_ONLY, HintValues.TRUE);

		EntityManagerUtilHelper.FIND_FORCING_DATABASE_TRIP.put(QueryHints.CACHE_RETRIEVE_MODE,
				CacheRetrieveMode.BYPASS);

		EntityManagerUtilHelper.FIND_FORCING_DATABASE_TRIP_AND_READ_ONLY.put(QueryHints.CACHE_RETRIEVE_MODE,
				CacheRetrieveMode.BYPASS);

		EntityManagerUtilHelper.FIND_FORCING_DATABASE_TRIP_AND_READ_ONLY.put(QueryHints.READ_ONLY, HintValues.TRUE);
	}

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
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityManager
	 * @param populatedEntityList
	 */
	public static <ENTITY> void createOrUpdateEntities(final EntityManager entityManager,
			final List<ENTITY> populatedEntityList) {

		entityManager.getTransaction().begin();

		for (final Object entity : populatedEntityList) {

			entityManager.merge(entity);
		}
		entityManager.getTransaction().commit();
		return;
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityManager
	 * @param populatedEntity
	 * @return {@link Object} representing the identity.
	 */
	public static <ENTITY> Object createOrUpdateEntity(final EntityManager entityManager, final ENTITY populatedEntity) {

		entityManager.getTransaction().begin();
		final ENTITY entityWithIdentity = entityManager.merge(populatedEntity);
		entityManager.getTransaction().commit();

		return entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entityWithIdentity);
	}

	/**
	 * This method removes the given entity from the persistence context,
	 * causing a managed entity to become detached. Unflushed changes made to
	 * the entity if any (final including removal of the entity), will not be
	 * synchronized to the database. Entities which previously referenced the
	 * detached entity will continue to reference it.
	 *
	 * @param entityManager
	 * @param populatedEntity
	 */
	public static void detachEntityFromPersistenceContext(final EntityManager entityManager,
			final Object populatedEntity) {

		try {
			entityManager.detach(populatedEntity);

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
		evictEntityInstancesFromSharedCache(entityManager, entityClass);
	}

	/**
	 * This method clears the shared (L2) cache of all instances of a single
	 * object type.
	 *
	 * @param entityManager
	 * @param entityClass
	 *
	 * @param <ENTITY>
	 */
	public static <ENTITY extends Object> void evictEntityInstancesFromSharedCache(final EntityManager entityManager,
			final Class<ENTITY> entityClass) {

		entityManager.getEntityManagerFactory().getCache().evict(entityClass);
	}

	/**
	 * This method forces a trip to the database without altering the state of
	 * cache.
	 *
	 * @param entityManager
	 * @param populatedEntities
	 *
	 * @return boolean
	 */
	public static boolean existsInDatabaseWithPopulatedEntities(final EntityManager entityManager,
			final Object... populatedEntities) {

		Object result;

		try {

			for (final Object entity : populatedEntities) {

				result = entityManager.find(entity.getClass(), retrieveIdentity(entityManager, entity),
						EntityManagerUtilHelper.FIND_FORCING_DATABASE_TRIP_AND_READ_ONLY);

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
	 * @param <ENTITY>
	 * @param entityManager
	 * @param entityClass
	 * @param entityIdentities
	 *
	 * @return boolean
	 */
	public static <ENTITY> boolean existsInDatabaseWithEntityIdentities(final EntityManager entityManager,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		Object result;

		try {

			for (final Object identity : entityIdentities) {

				result = entityManager.find(entityClass, identity,
						EntityManagerUtilHelper.FIND_FORCING_DATABASE_TRIP_AND_READ_ONLY);

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
	 * @param entityManager
	 * @param persistedEntities
	 * @return boolean
	 */
	public static boolean existsInPersistenceContextWithPersistedEntities(final EntityManager entityManager,
			final Object... persistedEntities) {

		boolean result = true;

		try {

			for (final Object persistedEntity : persistedEntities) {

				result = entityManager.contains(persistedEntity);

				if (false == result) {
					return false;
				}
			}
		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		}
		return result;
	}

	/**
	 * This method determines whether the shared (L2) cache contains the given
	 * persisted entities.
	 *
	 * @param <ENTITY>
	 *
	 * @param entityManager
	 * @param persistedEntities
	 * @return boolean
	 */
	public static <ENTITY> boolean existsInSharedCacheWithPersistedEntities(final EntityManager entityManager,
			final Object... persistedEntities) {

		boolean result = true;

		try {

			for (final Object populatedEntity : persistedEntities) {

				@SuppressWarnings("unchecked")
				final ENTITY entity = (ENTITY) findReadOnlySingleOrNull(entityManager, populatedEntity.getClass(),
						retrieveIdentity(entityManager, populatedEntity));

				result = entityManager.getEntityManagerFactory().getCache().contains(entity.getClass(),
						retrieveIdentity(entityManager, entity));

				if (false == result) {
					return false;
				}
			}

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		}
		return result;
	}

	/**
	 * This method determines whether the shared (L2) cache contains the given
	 * entities.
	 *
	 * @param <ENTITY>
	 * @param entityManager
	 * @param entityClass
	 * @param entityIdentities
	 * @return boolean
	 */
	public static <ENTITY> boolean existsInSharedCacheWithEntityIdentities(final EntityManager entityManager,
			final Class<ENTITY> entityClass,
			final Object... entityIdentities) {

		boolean result = true;

		try {

			for (final Object entityIdentity : entityIdentities) {

				result = entityManager.getEntityManagerFactory().getCache().contains(entityClass,
						entityIdentity);

				if (false == result) {
					return false;
				}
			}

		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		}
		return result;
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityManager
	 * @param entityClass
	 * @param entityIdentity
	 */
	public static <ENTITY> void findAndRemoveEntity(final EntityManager entityManager, final Class<ENTITY> entityClass,
			final Object entityIdentity) {

		final ENTITY entity = entityManager.find(entityClass, entityIdentity);

		entityManager.getTransaction().begin();

		final ENTITY mergedEntity = entityManager.merge(entity);
		entityManager.remove(mergedEntity);

		entityManager.getTransaction().commit();
	}

	/**
	 * @param <ENTITY>
	 * @param entityManager
	 * @param entityClass
	 * @param entityIdentity
	 * @return {@link Object}
	 */
	public static <ENTITY> ENTITY findModifiableSingleOrNull(final EntityManager entityManager, final Class<ENTITY> entityClass, final Object entityIdentity) {

		return entityManager.find(entityClass, entityIdentity);
	}



	/**
	 * @param <ENTITY>
	 * @param entityManager
	 * @param entityClass
	 * @param entityIdentity
	 * @return {@link Object}
	 */
	public static <ENTITY> ENTITY findReadOnlySingleOrNull(final EntityManager entityManager, final Class<ENTITY> entityClass, final Object entityIdentity) {

		return entityManager.find(entityClass, entityIdentity, EntityManagerUtilHelper.FIND_READ_ONLY);
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityManager
	 * @param populatedEntityList
	 */
	public static <ENTITY> void removeEntities(final EntityManager entityManager, final List<ENTITY> populatedEntityList) {

		entityManager.getTransaction().begin();

		for (Object entity : populatedEntityList) {

			entity = entityManager.merge(entity);
			entityManager.remove(entity);
		}
		entityManager.getTransaction().commit();
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityManager
	 * @param entityClass
	 * @param entityIdentity
	 */
	public static <ENTITY> void removeEntity(final EntityManager entityManager,final Class<ENTITY> entityClass,
			final Object entityIdentity) {

		final ENTITY entity = entityManager.find(entityClass, entityIdentity);

		if (null != entity) {
			entityManager.getTransaction().begin();

			final ENTITY mergedEntity = entityManager.merge(entity);
			entityManager.remove(mergedEntity);

			entityManager.getTransaction().commit();
		}
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityManager
	 * @param populatedEntity
	 */
	public static <ENTITY> void removeEntity(final EntityManager entityManager, final ENTITY populatedEntity) {

		entityManager.getTransaction().begin();

		final ENTITY mergedEntity = entityManager.merge(populatedEntity);
		entityManager.remove(mergedEntity);

		entityManager.getTransaction().commit();
	}

	/**
	 * @param entityManager
	 * @param populatedEntity
	 * @return Object
	 */
	private static Object retrieveIdentity(final EntityManager entityManager, final Object populatedEntity) {

		return entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(populatedEntity);
	}
}
