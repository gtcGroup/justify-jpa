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

package com.gtcgroup.justify.jpa.rm;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.gtcgroup.justify.core.base.JstBaseTestingRM;
import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;

/**
 * This Resource Manager provides convenience methods for queries.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public class QueryRM extends JstBaseTestingRM {

	/**
	 * Optimization for reading from the database. Often used when there are
	 * multiple servers and no cache coordination.
	 */
	public static final HashMap<String, Object> FIND_FORCING_DATABASE_TRIP_AND_CACHE_REFRESH = new HashMap<String, Object>();

	/**
	 * Optimization for reading from the database only. That is, cache is NOT
	 * impacted.
	 */
	public static final HashMap<String, Object> FIND_FORCING_DATABASE_TRIP_WITH_NO_IMPACT_TO_CACHE = new HashMap<String, Object>();

	/** Optimization for read-only. */
	public static final ConcurrentMap<String, Object> FIND_READ_ONLY = new ConcurrentHashMap<String, Object>();

	static {
		QueryRM.FIND_READ_ONLY.put(QueryHints.READ_ONLY, HintValues.TRUE);
	}

	static {
		QueryRM.FIND_FORCING_DATABASE_TRIP_AND_CACHE_REFRESH.put(QueryHints.CACHE_RETRIEVE_MODE,
				CacheRetrieveMode.BYPASS);

		QueryRM.FIND_FORCING_DATABASE_TRIP_AND_CACHE_REFRESH.put(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH);

		QueryRM.FIND_FORCING_DATABASE_TRIP_WITH_NO_IMPACT_TO_CACHE.put(QueryHints.CACHE_RETRIEVE_MODE,
				CacheUsage.DoNotCheckCache);

		QueryRM.FIND_FORCING_DATABASE_TRIP_WITH_NO_IMPACT_TO_CACHE.put(QueryHints.CACHE_STORE_MODE,
				CacheStoreMode.BYPASS);
	}

	/**
	 * This method executes a SELECT query returning a result list.
	 *
	 * @param <ENTITY>
	 * @param query
	 * @param parameterValuesInOrder
	 * @return {@link List}
	 */
	@SuppressWarnings("unchecked")
	protected static <ENTITY> List<ENTITY> queryResultList(final Query query, final Object... parameterValuesInOrder) {

		List<ENTITY> entityList = null;

		try {

			for (int i = 0; i < parameterValuesInOrder.length; i++) {
				query.setParameter(i + 1, parameterValuesInOrder[i]);
			}

			entityList = query.getResultList();

		} catch (final Exception e) {

			throwException(e);
		}
		return entityList;
	}

	/**
	 * This method executes a SELECT query that returns a single untyped result.
	 *
	 * @param <ENTITY>
	 * @param query
	 * @param parameterValuesInOrder
	 * @return ENTITY
	 */
	@SuppressWarnings("unchecked")
	protected static <ENTITY> ENTITY querySingleResult(final Query query, final Object... parameterValuesInOrder) {

		ENTITY entity = null;

		try {

			for (int i = 0; i < parameterValuesInOrder.length; i++) {
				query.setParameter(i + 1, parameterValuesInOrder[i]);
			}

			entity = (ENTITY) query.getSingleResult();

		} catch (final Exception e) {

			throwException(e);
		}

		return entity;
	}

	/**
	 * @param message
	 */
	private static void throwException(final Exception e) {

		throw new TestingRuntimeException(e);
	}

	/**
	 * @param entity
	 * @param identity
	 * @param <ENTITY>
	 * @return
	 */
	private static <ENTITY> ENTITY throwExceptionForNull(final Class<ENTITY> entityClass, final ENTITY entity) {

		if (null == entity) {

			throw new TestingRuntimeException(
					"Unable to find an instance for class [" + entityClass.getSimpleName() + "].");
		}
		return entity;
	}

	private EntityManager entityManager;

	/**
	 * This method returns the number of records in the table or view. It may be
	 * used in support of query processing.
	 *
	 * @param <ENTITY>
	 * @param entityClass
	 * @return long
	 */
	public <ENTITY> long count(final Class<ENTITY> entityClass) {

		long count = 0;

		try {

			final CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
			final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

			criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(entityClass)));

			final Query query = getEntityManager().createQuery(criteriaQuery);

			final Long countLong = (Long) query.getSingleResult();
			count = countLong.longValue();

		} catch (final Exception e) {

			throwException(e);
		}

		return count;
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @return {@link Query}
	 */
	protected <ENTITY> Query createCriteriaQueryModifiable(final Class<ENTITY> entityClass) {

		final CriteriaQuery<ENTITY> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
		final Root<ENTITY> rootEntry = criteriaQuery.from(entityClass);
		final CriteriaQuery<ENTITY> criteria = criteriaQuery.select(rootEntry);

		return getEntityManager().createQuery(criteria);
	}

	/**
	 * @param queryLanguageString
	 * @return {@link Query}
	 */
	protected Query createCriteriaQueryModifiable(final String queryLanguageString) {

		return getEntityManager().createQuery(queryLanguageString);
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @return {@link Query}
	 */
	protected <ENTITY> Query createCriteriaQueryReadOnly(final Class<ENTITY> entityClass) {

		final Query query = createCriteriaQueryModifiable(entityClass);
		query.setHint(QueryHints.READ_ONLY, HintValues.TRUE);

		return query;
	}

	/**
	 * @param queryLanguageString
	 * @return {@link Query}
	 */
	protected Query createCriteriaQueryReadOnly(final String queryLanguageString) {

		final Query query = createCriteriaQueryModifiable(queryLanguageString);
		query.setHint(QueryHints.READ_ONLY, HintValues.TRUE);

		return query;
	}

	/**
	 * @param queryName
	 * @return {@link Query}
	 */
	protected Query createNamedQueryModifiable(final String queryName) {

		Query query;
		try {
			query = getEntityManager().createNamedQuery(queryName);
		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		}

		return query;
	}

	/**
	 * @param name
	 * @return {@link Query}
	 */
	protected Query createNamedQueryReadOnly(final String name) {

		final Query query = createNamedQueryModifiable(name);
		query.setHint(QueryHints.READ_ONLY, HintValues.TRUE);

		return query;
	}

	/**
	 * @param <ENTITY>
	 * @param sqlString
	 * @param clazz
	 * @return {@link Query}
	 */
	protected <ENTITY> Query createNativeQueryModifiable(final String sqlString, final Class<ENTITY> clazz) {

		return getEntityManager().createNativeQuery(sqlString, clazz);
	}

	/**
	 * @param <ENTITY>
	 * @param sqlString
	 * @param clazz
	 * @return {@link Query}
	 */
	protected <ENTITY> Query createNativeQueryReadOnly(final String sqlString, final Class<ENTITY> clazz) {

		final Query query = createNativeQueryModifiable(sqlString, clazz);
		query.setHint(QueryHints.READ_ONLY, HintValues.TRUE);

		return query;
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @param entityIdentities
	 * @return boolean
	 */
	public <ENTITY> boolean existsEntityIdentities(final Class<ENTITY> entityClass, final Object... entityIdentities) {

		for (final Object entityIdentity : entityIdentities) {

			if (false == existsEntityIdentity(entityClass, entityIdentity)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @param entityIdentity
	 * @return boolean
	 */
	protected <ENTITY> boolean existsEntityIdentity(final Class<ENTITY> entityClass, final Object entityIdentity) {

		Object entity;
		try {
			entity = getEntityManager().find(entityClass, entityIdentity, QueryRM.FIND_READ_ONLY);
		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		}

		if (null == entity) {
			return false;
		}
		return true;
	}

	/**
	 * @param <ENTITY>
	 * @param entitiesWithIdentity
	 * @return boolean
	 */
	public <ENTITY> boolean existsEntityInstances(final Object... entitiesWithIdentity) {

		boolean result = true;

		for (final Object entity : entitiesWithIdentity) {

			try {

				final Object entityIdentity = getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil()
						.getIdentifier(entity);

				if (!existsEntityIdentity(entity.getClass(), entityIdentity)) {
					result = false;
				}

			} catch (final Exception e) {

				throw new TestingRuntimeException(e);
			}
		}
		return result;
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @param entityIdentity
	 * @return {@link Object}
	 */
	public <ENTITY> ENTITY findModifiableSingleOrException(final Class<ENTITY> entityClass,
			final Object entityIdentity) {

		final ENTITY entity = findModifiableSingleOrNull(entityClass, entityIdentity);

		return throwExceptionForNull(entityClass, entity);
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @param entityIdentity
	 * @return {@link Object}
	 */
	public <ENTITY> ENTITY findModifiableSingleOrNull(final Class<ENTITY> entityClass, final Object entityIdentity) {

		return getEntityManager().find(entityClass, entityIdentity);
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @param entityIdentity
	 * @return {@link Object}
	 */
	public <ENTITY> ENTITY findReadOnlySingleOrException(final Class<ENTITY> entityClass, final Object entityIdentity) {

		final ENTITY entity = findReadOnlySingleOrNull(entityClass, entityIdentity);

		return throwExceptionForNull(entityClass, entity);
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @param entityIdentity
	 * @return {@link Object}
	 */
	public <ENTITY> ENTITY findReadOnlySingleOrNull(final Class<ENTITY> entityClass, final Object entityIdentity) {

		return getEntityManager().find(entityClass, entityIdentity, QueryRM.FIND_READ_ONLY);
	}

	/**
	 * @return EntityManager
	 */
	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @return {@link List}<ENTITY>
	 */
	public <ENTITY> List<ENTITY> queryModifiableListByClass(final Class<ENTITY> entityClass) {

		return queryResultList(createCriteriaQueryModifiable(entityClass));
	}

	/**
	 * @param <ENTITY>
	 * @param queryLanguageString
	 * @return {@link List}<ENTITY>
	 */
	public <ENTITY> List<ENTITY> queryModifiableListUsingJPQL(final String queryLanguageString) {

		return queryResultList(createCriteriaQueryModifiable(queryLanguageString));
	}

	/**
	 * @param <ENTITY>
	 * @param queryLanguageString
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryModifiableSingleUsingJPQL(final String queryLanguageString) {

		return querySingleResult(createCriteriaQueryModifiable(queryLanguageString));
	}

	/**
	 * @param <ENTITY>
	 * @param queryName
	 * @param parameterValuesInOrder
	 * @return {@link List}<ENTITY>
	 */
	public <ENTITY> List<ENTITY> queryNamedModifiableList(final String queryName,
			final Object... parameterValuesInOrder) {

		return queryResultList(createNamedQueryModifiable(queryName), parameterValuesInOrder);
	}

	/**
	 * @param <ENTITY>
	 * @param queryName
	 * @param parameterValuesInOrder
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNamedModifiableSingle(final String queryName, final Object... parameterValuesInOrder) {

		return querySingleResult(createNamedQueryModifiable(queryName), parameterValuesInOrder);
	}

	/**
	 * @param <ENTITY>
	 * @param queryName
	 * @param parameterValuesInOrder
	 * @return {@link List}<ENTITY>
	 */
	public <ENTITY> List<ENTITY> queryNamedNativeModifiableList(final String queryName,
			final Object... parameterValuesInOrder) {

		return queryResultList(createNamedQueryModifiable(queryName), parameterValuesInOrder);
	}

	/**
	 * @param <ENTITY>
	 * @param queryName
	 * @param parameterValuesInOrder
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNamedNativeModifiableSingle(final String queryName,
			final Object... parameterValuesInOrder) {

		return querySingleResult(createNamedQueryModifiable(queryName), parameterValuesInOrder);
	}

	/**
	 * @param <ENTITY>
	 * @param name
	 * @param parameterValuesInOrder
	 * @return ENTITY
	 */
	public <ENTITY> List<ENTITY> queryNamedNativeReadOnlyList(final String name,
			final Object... parameterValuesInOrder) {

		return queryResultList(createNamedQueryReadOnly(name), parameterValuesInOrder);
	}

	/**
	 * @param <ENTITY>
	 * @param name
	 * @param parameterValuesInOrder
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNamedNativeReadOnlySingle(final String name, final Object... parameterValuesInOrder) {

		return querySingleResult(createNamedQueryReadOnly(name), parameterValuesInOrder);
	}

	/**
	 * @param <ENTITY>
	 * @param name
	 * @param parameterValuesInOrder
	 * @return ENTITY
	 */
	public <ENTITY> List<ENTITY> queryNamedReadOnlyList(final String name, final Object... parameterValuesInOrder) {

		return queryResultList(createNamedQueryReadOnly(name), parameterValuesInOrder);
	}

	/**
	 * @param <ENTITY>
	 * @param name
	 * @param parameterValuesInOrder
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNamedReadOnlySingle(final String name, final Object... parameterValuesInOrder) {

		return querySingleResult(createNamedQueryReadOnly(name), parameterValuesInOrder);
	}

	/**
	 * @param <ENTITY>
	 * @param sqlString
	 * @param clazz
	 * @param parameterValuesInOrder
	 * @return {@link List}<ENTITY>
	 */
	public <ENTITY> List<ENTITY> queryNativeModifiableList(final String sqlString, final Class<ENTITY> clazz,
			final Object... parameterValuesInOrder) {

		return queryResultList(createNativeQueryModifiable(sqlString, clazz), parameterValuesInOrder);
	}

	/**
	 * @param <ENTITY>
	 * @param sqlString
	 * @param clazz
	 * @param parameterValuesInOrder
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNativeModifiableSingle(final String sqlString, final Class<ENTITY> clazz,
			final Object... parameterValuesInOrder) {

		return querySingleResult(createNativeQueryModifiable(sqlString, clazz), parameterValuesInOrder);
	}

	/**
	 * @param <ENTITY>
	 * @param sqlString
	 * @param clazz
	 * @param parameterValuesInOrder
	 * @return {@link List}<ENTITY>
	 */
	public <ENTITY> List<ENTITY> queryNativeReadOnlyList(final String sqlString, final Class<ENTITY> clazz,
			final Object... parameterValuesInOrder) {

		return queryResultList(createNativeQueryReadOnly(sqlString, clazz), parameterValuesInOrder);
	}

	/**
	 * @param <ENTITY>
	 * @param sqlString
	 * @param clazz
	 * @param parameterValuesInOrder
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNativeReadOnlySingle(final String sqlString, final Class<ENTITY> clazz,
			final Object... parameterValuesInOrder) {

		return querySingleResult(createNativeQueryReadOnly(sqlString, clazz), parameterValuesInOrder);
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @return {@link List}<ENTITY>
	 */
	public <ENTITY> List<ENTITY> queryReadOnlyListByClass(final Class<ENTITY> entityClass) {

		return queryResultList(createCriteriaQueryReadOnly(entityClass));
	}

	/**
	 * @param <ENTITY>
	 * @param queryLanguageString
	 * @return {@link List}<ENTITY>
	 */
	public <ENTITY> List<ENTITY> queryReadOnlyListUsingJPQL(final String queryLanguageString) {

		return queryResultList(createCriteriaQueryReadOnly(queryLanguageString));
	}

	/**
	 * @param <ENTITY>
	 * @param queryLanguageString
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryReadOnlySingleUsingJPQL(final String queryLanguageString) {

		return querySingleResult(createCriteriaQueryReadOnly(queryLanguageString));
	}

	/**
	 * @param <RM>
	 * @param entityManager
	 * @return {@link QueryRM}
	 */
	@SuppressWarnings("unchecked")
	public <RM extends QueryRM> RM withEntityManager(final EntityManager entityManager) {

		this.entityManager = entityManager;
		return (RM) this;
	}
}
