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
package com.gtcgroup.justify.jpa.po;

import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.gtcgroup.justify.jpa.po.internal.BaseQueryJpaPO;

/**
 * This Parameter Object class supports @ queries.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstQueryNamedJpaPO extends BaseQueryJpaPO {

    /**
     * This method initializes the class.
     *
     * @return {@link JstQueryNamedJpaPO}
     */
    public static JstQueryNamedJpaPO withPersistenceUnitName(final String persistenceUnitName) {

        return new JstQueryNamedJpaPO(persistenceUnitName);
    }

    protected String queryName;

    /**
     * Constructor
     */
    protected JstQueryNamedJpaPO(final String persistenceUnitName) {

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
                return Optional.of(entityManager.get().createNamedQuery(getQueryName()));
            }
        } catch (@SuppressWarnings("unused") final Exception e) {
            // Continue.
        }
        return Optional.empty();
    }

    /**
     * @return {@link String}
     */
    public String getQueryName() {

        return this.queryName;
    }

    /**
     * @return {@link JstQueryNamedJpaPO}
     */
    public JstQueryNamedJpaPO withEntityManager(final EntityManager entityManager) {

        super.setEntityManager(entityManager);
        return this;
    }

    /**
     * @return {@link JstQueryNamedJpaPO}
     */
    public JstQueryNamedJpaPO withFirstResult(final int firstResult) {

        this.firstResult = firstResult;
        return this;
    }

    /**
     * @return {@link JstQueryNamedJpaPO}
     */
    public JstQueryNamedJpaPO withForceDatabaseTripWhenNoCacheCoordination(final boolean suppress) {

        this.forceDatabaseTripWhenNoCacheCoordination = suppress;
        return this;
    }

    /**
     * @return {@link JstQueryNamedJpaPO}
     */
    public JstQueryNamedJpaPO withMaxResults(final int maxResults) {

        this.maxResults = maxResults;
        return this;
    }

    /**
     * @return {@link JstQueryNamedJpaPO}
     */
    public JstQueryNamedJpaPO withParameterMap(final Map<String, Object> parameterMap) {

        this.queryHints = parameterMap;
        return this;
    }

    /**
     * @return {@link JstQueryNamedJpaPO}
     */
    public JstQueryNamedJpaPO withQueryName(final String queryName) {

        this.queryName = queryName;
        return this;
    }

    /**
     * @return {@link JstQueryNamedJpaPO}
     */
    public JstQueryNamedJpaPO withSuppressReadOnly(final boolean suppress) {

        this.readOnly = suppress;
        return this;
    }
}