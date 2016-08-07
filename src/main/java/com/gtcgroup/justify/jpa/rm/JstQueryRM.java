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

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.gtcgroup.justify.core.base.JstBaseTestingRM;
import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;
import com.gtcgroup.justify.jpa.helper.internal.EntityManagerUtilHelper;
import com.gtcgroup.justify.jpa.helper.internal.QueryUtilHelper;

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
public class JstQueryRM extends JstBaseTestingRM {

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

	private final EntityManager entityManager;

	/**
	 * Constructor
	 *
	 * @param entityManager
	 */
	public JstQueryRM(final EntityManager entityManager) {
		super();
		this.entityManager = entityManager;
	}

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
	 * @param entityManager
	 * @param entityClass
	 * @param entityIdentity
	 * @return {@link Object}
	 */
	public <ENTITY> ENTITY findModifiableSingleOrException(final EntityManager entityManager,
			final Class<ENTITY> entityClass, final Object entityIdentity) {

		final ENTITY entity = EntityManagerUtilHelper.findModifiableSingleOrNull(this.entityManager, entityClass,
				entityIdentity);

		return throwExceptionForNull(entityClass, entity);
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @param entityIdentity
	 * @return {@link Object}
	 */
	public <ENTITY> ENTITY findModifiableSingleOrNull(final Class<ENTITY> entityClass, final Object entityIdentity) {

		return EntityManagerUtilHelper.findModifiableSingleOrNull(this.entityManager, entityClass, entityIdentity);
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @param entityIdentity
	 * @return {@link Object}
	 */
	public <ENTITY> ENTITY findReadOnlySingleOrException(final Class<ENTITY> entityClass, final Object entityIdentity) {

		final ENTITY entity = EntityManagerUtilHelper.findReadOnlySingleOrNull(this.entityManager, entityClass,
				entityIdentity);

		return throwExceptionForNull(entityClass, entity);
	}

	/**
	 * @param <ENTITY>
	 * @param entityClass
	 * @param entityIdentity
	 * @return {@link Object}
	 */
	public <ENTITY> ENTITY findReadOnlySingleOrNull(final Class<ENTITY> entityClass, final Object entityIdentity) {

		return EntityManagerUtilHelper.findModifiableSingleOrNull(this.entityManager, entityClass, entityIdentity);
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
	public <ENTITY> List<ENTITY> queryClassModifiableList(final Class<ENTITY> entityClass) {

		return QueryUtilHelper.queryResultList(createCriteriaQueryModifiable(entityClass), null, null);
	}

	/**
	 * @param <ENTITY>
	 * @param queryLanguageString
	 * @return {@link List}<ENTITY>
	 */
	public <ENTITY> List<ENTITY> queryJpqlModifiableList(final String queryLanguageString) {

		return QueryUtilHelper.queryResultList(createCriteriaQueryModifiable(queryLanguageString), null, null);
	}

	/**
	 * @param <ENTITY>
	 * @param queryLanguageString
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryJpqlModifiableSingle(final String queryLanguageString) {

		return QueryUtilHelper.querySingleResult(createCriteriaQueryModifiable(queryLanguageString), null, null);
	}

	/**
	 * @param <ENTITY>
	 * @param queryName
	 * @param integerParameterMap
	 * @param stringParameterMap
	 * @return {@link List}<ENTITY>
	 */
	public <ENTITY> List<ENTITY> queryNamedModifiableList(final String queryName,
			final Map<Integer, Object> integerParameterMap, final Map<String, Object> stringParameterMap) {

		return QueryUtilHelper.queryResultList(createNamedQueryModifiable(queryName), integerParameterMap,
				stringParameterMap);
	}

	/**
	 * @param <ENTITY>
	 * @param queryName
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNamedModifiableSingle(final String queryName) {

		return QueryUtilHelper.querySingleResult(createNamedQueryModifiable(queryName), null, null);
	}

	/**
	 * @param <ENTITY>
	 * @param queryName
	 * @param integerParameterMap
	 * @param stringParameterMap
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNamedModifiableSingle(final String queryName,
			final Map<Integer, Object> integerParameterMap, final Map<String, Object> stringParameterMap) {

		return QueryUtilHelper.querySingleResult(createNamedQueryModifiable(queryName), integerParameterMap,
				stringParameterMap);
	}

	/**
	 * @param <ENTITY>
	 * @param queryName
	 * @param integerParameterMap
	 * @param stringParameterMap
	 * @return {@link List}<ENTITY>
	 */
	public <ENTITY> List<ENTITY> queryNamedNativeModifiableList(final String queryName,
			final Map<Integer, Object> integerParameterMap, final Map<String, Object> stringParameterMap) {

		return QueryUtilHelper.queryResultList(createNamedQueryModifiable(queryName), integerParameterMap,
				stringParameterMap);
	}

	/**
	 * @param <ENTITY>
	 * @param queryName
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNamedNativeModifiableSingle(final String queryName) {

		return QueryUtilHelper.querySingleResult(createNamedQueryModifiable(queryName), null, null);
	}
	/**
	 * @param <ENTITY>
	 * @param queryName
	 * @param integerParameterMap
	 * @param stringParameterMap
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNamedNativeModifiableSingle(final String queryName,
			final Map<Integer, Object> integerParameterMap, final Map<String, Object> stringParameterMap) {

		return QueryUtilHelper.querySingleResult(createNamedQueryModifiable(queryName), integerParameterMap,
				stringParameterMap);
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

		return QueryUtilHelper.querySingleResult(createNamedQueryReadOnly(name), parameterValuesInOrder);
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
	 * @param queryPO
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNamedReadOnlySingle(final JstJpaQueryPO queryPO) {

		return QueryUtilHelper.querySingleResult(createNamedQueryReadOnly(queryPO.getQueryName()),
				queryPO.getIntegerParameterMap(), queryPO.getKeyParameterMap());
	}

	/**
	 * @param <ENTITY>
	 * @param queryName
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNamedReadOnlySingle(final String queryName) {

		return QueryUtilHelper.querySingleResult(createNamedQueryReadOnly(queryName));
	}

	/**
	 * @param <ENTITY>
	 * @param sqlString
	 * @param integerParameterMap
	 * @param stringParameterMap
	 * @param clazz
	 * @return {@link List}<ENTITY>
	 */
	public <ENTITY> List<ENTITY> queryNativeModifiableList(final String sqlString, final Class<ENTITY> clazz,
			final Map<Integer, Object> integerParameterMap, final Map<String, Object> stringParameterMap) {

		return QueryUtilHelper.queryResultList(createNativeQueryModifiable(sqlString, clazz), integerParameterMap,
				stringParameterMap);
	}

	/**
	 * @param <ENTITY>
	 * @param sqlString
	 * @param clazz
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNativeModifiableSingle(final String sqlString, final Class<ENTITY> clazz) {

		return QueryUtilHelper.querySingleResult(createNativeQueryModifiable(sqlString, clazz), null, null);
	}

	/**
	 * @param <ENTITY>
	 * @param sqlString
	 * @param clazz
	 * @param integerParameterMap
	 * @param stringParameterMap
	 * @return ENTITY
	 */
	public <ENTITY> ENTITY queryNativeModifiableSingle(final String sqlString, final Class<ENTITY> clazz,
			final Map<Integer, Object> integerParameterMap, final Map<String, Object> stringParameterMap) {

		return QueryUtilHelper.querySingleResult(createNativeQueryModifiable(sqlString, clazz), integerParameterMap,
				stringParameterMap);
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

		return QueryUtilHelper.querySingleResult(createNativeQueryReadOnly(sqlString, clazz), parameterValuesInOrder);
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

		return QueryUtilHelper.querySingleResult(createCriteriaQueryReadOnly(queryLanguageString));
	}
}
