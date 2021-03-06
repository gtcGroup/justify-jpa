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
import java.util.Map.Entry;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import com.gtcgroup.justify.jpa.po.JstQueryCountPO;
import com.gtcgroup.justify.jpa.po.internal.BaseQueryPO;

/**
 * This Util Helper class provides persistence {@link Query} support.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstQueryUtilHelper {

	INSTANCE;

	/**
	 * This method returns the number of records in the table or view.
	 *
	 * @return {@link Optional}
	 */
	public static Optional<Long> count(final JstQueryCountPO queryPO) {

		final EntityManager entityManager = queryPO.getEntityManager();

		try {
			final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

			criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(queryPO.getResultClass())));

			final Query query = entityManager.createQuery(criteriaQuery);
			final Long count = (Long) query.getSingleResult();
			return Optional.of(count);

		} finally {
			queryPO.closeEntityManager();
		}
	}

	/**
	 * @return {@link Optional}
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY> Optional<List<ENTITY>> queryResultList(final BaseQueryPO queryPO) {

		try {
			final Query query = decorateQuery(queryPO);

			final List<ENTITY> entityList = query.getResultList();

			if (entityList.isEmpty()) {
				return Optional.empty();
			}

			return Optional.of(entityList);

		} finally {
			queryPO.closeEntityManager();
		}
	}

	/**
	 * @return {@link Optional}
	 */
	@SuppressWarnings("unchecked")
	public static <ENTITY> Optional<ENTITY> querySingleResult(final BaseQueryPO queryPO) {

		try {

			final Query query = decorateQuery(queryPO);

			return (Optional<ENTITY>) Optional.of(query.getSingleResult());

		} catch (@SuppressWarnings("unused") final NoResultException e) {

			return Optional.empty();

		} finally {
			queryPO.closeEntityManager();
		}
	}

	private static Query decorateQuery(final BaseQueryPO queryPO) {

		final Query query = queryPO.createQueryTM();

		// Query Hints
		for (final Entry<String, Object> stringEntry : queryPO.getQueryHints().entrySet()) {

			query.setHint(stringEntry.getKey(), stringEntry.getValue());
		}

		// Query Parameters
		for (final Entry<String, Object> stringEntry : queryPO.getParameterMap().entrySet()) {

			query.setParameter(stringEntry.getKey(), stringEntry.getValue());
		}

		if (queryPO.isFirstResult()) {

			query.setFirstResult(queryPO.getFirstResult());
		}

		if (queryPO.isMaxResults()) {

			query.setMaxResults(queryPO.getMaxResults());
		}

		return query;
	}
}
