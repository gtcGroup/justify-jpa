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
import java.util.Map.Entry;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.exceptions.DatabaseException;

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;
import com.gtcgroup.justify.jpa.po.JstCriteriaQueryJpaPO;
import com.gtcgroup.justify.jpa.po.internal.BaseQueryJpaPO;

/**
 * This Util Helper class provides persistence {@link Query} support.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstQueryUtilHelper {

	@SuppressWarnings("javadoc")
	INSTANCE;

	/**
	 * This method returns the number of records in the table or view.
	 *
	 * @return long
	 */
	public static long count(final JstCriteriaQueryJpaPO queryPO) {

		final CriteriaBuilder criteriaBuilder = queryPO.getEntityManager().getCriteriaBuilder();
		final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

		criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(queryPO.getResultClass())));

		final Query query = queryPO.getEntityManager().createQuery(criteriaQuery);

		final Long countLong = (Long) query.getSingleResult();
		final long count = countLong.longValue();

		return count;
	}

	@SuppressWarnings("boxing")
	static Query decorateQuery(final BaseQueryJpaPO queryPO, final Map<String, Object> stringParameterMap,
			final Object... orderedParameters) {

		final Query query = queryPO.getQuery();

		if (0 != orderedParameters.length) {

			for (final Entry<Integer, Object> integerEntry : instantiateIntegerParameterMap(orderedParameters)
					.entrySet()) {

				query.setParameter(integerEntry.getKey(), integerEntry.getValue());
			}
		}
		if (null != stringParameterMap) {

			for (final Entry<String, Object> stringEntry : stringParameterMap.entrySet()) {

				query.setParameter(stringEntry.getKey(), stringEntry.getValue());
			}
		}
		if (queryPO.isReadOnly()) {

			query.setHint(QueryHints.READ_ONLY, HintValues.TRUE);
		}
		if (queryPO.isFirstResult()) {

			query.setFirstResult(queryPO.getFirstResult());
		}
		if (queryPO.isMaxResults()) {

			query.setMaxResults(queryPO.getMaxResults());
		}
		return query;
	}

	/**
	 * @return {@link Map}
	 */
	static Map<Integer, Object> instantiateIntegerParameterMap(final Object... orderedParameters) {
		final Map<Integer, Object> integerParameterMap = new HashMap<Integer, Object>();

		for (int i = 0; i < orderedParameters.length; i++) {

			integerParameterMap.put(new Integer(i + 1), orderedParameters[i]);
		}
		return integerParameterMap;
	}

	/**
	 * This method executes a query with parameters.
	 *
	 * @return {@link List}
	 */
    @SuppressWarnings("unchecked")
    public static <ENTITY> List<ENTITY> queryResultList(final BaseQueryJpaPO queryPO,
			final Map<String, Object> stringParameterMap) {

		List<ENTITY> entityList = null;

		try {

			final Query query = decorateQuery(queryPO, stringParameterMap);

			entityList = query.getResultList();

		} finally {
			queryPO.closeEntityManagerIfCreatedWithPersistenceUnitName();
		}
		return entityList;
	}

	/**
	 * This method executes a query with parameters.
	 *
	 * @return {@link List}
	 */
    @SuppressWarnings("unchecked")
    public static <ENTITY> List<ENTITY> queryResultList(final BaseQueryJpaPO queryPO,
			final Object... orderedParameters) {

		List<ENTITY> entityList = null;

		try {

			final Query query = decorateQuery(queryPO, null, orderedParameters);

			entityList = query.getResultList();
		} catch (final DatabaseException sqlException) {

			throw new JustifyRuntimeException(sqlException);

		} finally {
			queryPO.closeEntityManagerIfCreatedWithPersistenceUnitName();
		}
		return entityList;
	}

	/**
	 * This method executes a query with parameters.
	 *
	 * @return ENTITY
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY> ENTITY querySingleResult(final BaseQueryJpaPO queryPO,
			final Map<String, Object> stringParameterMap) {

		ENTITY entity = null;

		try {

			final Query query = decorateQuery(queryPO, stringParameterMap);

			entity = (ENTITY) query.getSingleResult();

		} finally {
			queryPO.closeEntityManagerIfCreatedWithPersistenceUnitName();
		}
		return entity;
	}

	/**
	 * This method executes a query with parameters.
	 *
	 * @return ENTITY
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY> ENTITY querySingleResult(final BaseQueryJpaPO queryPO, final Object... orderedParameters) {

		ENTITY entity = null;

		try {

			final Query query = decorateQuery(queryPO, null, orderedParameters);

			entity = (ENTITY) query.getSingleResult();

		} finally {
			queryPO.closeEntityManagerIfCreatedWithPersistenceUnitName();
		}
		return entity;
	}
}
