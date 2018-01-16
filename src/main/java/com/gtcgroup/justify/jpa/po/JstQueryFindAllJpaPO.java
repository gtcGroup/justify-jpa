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
package com.gtcgroup.justify.jpa.po;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.gtcgroup.justify.jpa.po.internal.BaseQueryJpaPO;

/**
 * This Parameter Object class supports finding all entities from a table or
 * view.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstQueryFindAllJpaPO extends BaseQueryJpaPO {

    /**
     * @return {@link TypedQuery}
     */
    protected static <ENTITY> TypedQuery<ENTITY> createCriteriaQuery(final EntityManager entityManager,
            final Class<ENTITY> resultClass) {

        final CriteriaQuery<ENTITY> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(resultClass);
        final Root<ENTITY> rootEntry = criteriaQuery.from(resultClass);
        final CriteriaQuery<ENTITY> criteria = criteriaQuery.select(rootEntry);

        return entityManager.createQuery(criteria);

    }

    /**
     * This method initializes the class.
     *
     * @return {@link JstQueryFindAllJpaPO}
     */
    public static JstQueryFindAllJpaPO withFindAll(final String persistenceUnitName) {

        return new JstQueryFindAllJpaPO(persistenceUnitName);
    }

    /**
     * Constructor
     */
    protected JstQueryFindAllJpaPO(final String persistenceUnitName) {

        super(persistenceUnitName);
        return;
    }

    /**
     * @return {@link Optional}
     */
    @Override
    public Optional<Query> createQuery() {

        final Optional<EntityManager> entityManager = getEntityManager();

        try {
            if (entityManager.isPresent()) {
                return Optional.of(createCriteriaQuery(entityManager.get(), getEntityClass()));
            }
        } catch (@SuppressWarnings("unused") final Exception e) {
            // Continue
        }
        return Optional.empty();
    }

    /**
     * @return {@link JstQueryFindAllJpaPO}
     */
    @SuppressWarnings("unchecked")
    public <ENTITY> JstQueryFindAllJpaPO withEntityClass(final Class<ENTITY> entityClass) {

        this.entityClass = (Class<Object>) entityClass;
        return this;
    }

    /**
     * @return {@link JstQueryFindAllJpaPO}
     */
    public JstQueryFindAllJpaPO withEntityManager(final EntityManager entityManager) {

        super.setEntityManager(entityManager);
        return this;
    }

    /**
     * @return {@link JstQueryFindAllJpaPO}
     */
    public JstQueryFindAllJpaPO withForceDatabaseTripWhenNoCacheCoordination(final boolean suppressForceDatabaseTrip) {

        super.setForceDatabaseTripWhenNoCacheCoordination(suppressForceDatabaseTrip);
        return this;
    }
}
