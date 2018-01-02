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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.eclipse.persistence.config.CascadePolicy;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.gtcgroup.justify.core.test.exception.internal.JustifyTestingException;
import com.gtcgroup.justify.jpa.po.JstCountAllJpaPO;
import com.gtcgroup.justify.jpa.po.internal.BaseJpaPO;
import com.gtcgroup.justify.jpa.po.internal.BaseQueryJpaPO;

/**
 * This Helper class provides persistence {@link Query} support.
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
    public static long count(final JstCountAllJpaPO queryPO) {

        final CriteriaBuilder criteriaBuilder = queryPO.getEntityManager().getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(queryPO.getResultClass())));

        final Query query = queryPO.getEntityManager().createQuery(criteriaQuery);

        final Long countLong = (Long) query.getSingleResult();
        final long count = countLong.longValue();

        return count;
    }

    static Query decorateQuery(final BaseQueryJpaPO queryPO, final Map<String, Object> stringParameterMap) {

        final Query query = queryPO.getQuery();

        if (null != stringParameterMap) {

            for (final Entry<String, Object> stringEntry : stringParameterMap.entrySet()) {

                query.setParameter(stringEntry.getKey(), stringEntry.getValue());
            }
        }

        if (!queryPO.isSuppressForceDatabaseTrip()) {
            query.setHint(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
            query.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);
            query.setHint(QueryHints.REFRESH_CASCADE, CascadePolicy.CascadeByMapping);
        }

        if (!queryPO.isSuppressReadOnly()) {

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

    private static <ENTITY> List<ENTITY> queryList(final Query query, final BaseQueryJpaPO queryPO) {

        List<ENTITY> entityList;
        try {
            entityList = query.getResultList();

        } catch (final Exception e) {

            throw new JustifyTestingException(e);
        }

        if (entityList.isEmpty()) {
            if (!queryPO.isSuppressExceptionForNull()) {

                throw new JustifyTestingException("The list is empty.");
            }
        }
        return entityList;
    }

    /**
     * This method executes a query with parameters.
     *
     * @return {@link List}
     */
    public static <ENTITY> List<ENTITY> queryResultList(final BaseQueryJpaPO queryPO) {

        List<ENTITY> entityList = null;

        try {

            final Query query = decorateQuery(queryPO, null);

            entityList = queryList(query, queryPO);

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
    public static <ENTITY> List<ENTITY> queryResultList(final BaseQueryJpaPO queryPO,
            final Map<String, Object> stringParameterMap) {

        List<ENTITY> entityList = null;

        try {

            final Query query = decorateQuery(queryPO, stringParameterMap);

            entityList = queryList(query, queryPO);

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
    public static <ENTITY> ENTITY querySingleResult(final BaseQueryJpaPO queryPO) {

        ENTITY entity = null;

        try {

            final Query query = decorateQuery(queryPO, null);

            entity = (ENTITY) query.getSingleResult();
            JstQueryUtilHelper.throwExceptionForNull(queryPO, entity);

        } catch (@SuppressWarnings("unused") final NoResultException e) {

            JstQueryUtilHelper.throwExceptionForNull(queryPO, entity);

        } catch (final Exception e) {

            throw new JustifyTestingException(e);

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
    public static <ENTITY> ENTITY querySingleResult(final BaseQueryJpaPO queryPO,
            final Map<String, Object> parameterMap) {

        ENTITY entity = null;

        try {

            final Query query = decorateQuery(queryPO, parameterMap);

            entity = (ENTITY) query.getSingleResult();
            JstQueryUtilHelper.throwExceptionForNull(queryPO, entity);

        } finally {
            queryPO.closeEntityManagerIfCreatedWithPersistenceUnitName();
        }
        return entity;
    }

    /**
     * This method handles exception suppression.
     */
    public static void throwExceptionForNull(final BaseJpaPO queryPO, final Object entity) {

        if (null == entity) {
            if (!queryPO.isSuppressExceptionForNull()) {
                throw new JustifyTestingException("Unable to retrieve a result instance.");

            }
        }
    }
}
