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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.jpa.po.internal.BaseQueryJpaPO;

/**
 * This Parameter Object class supports native and query language queries.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstQueryLanguageJpaPO extends BaseQueryJpaPO {

    /**
     * This method initializes the class.
     *
     * @return {@link JstQueryLanguageJpaPO}
     */
    public static JstQueryLanguageJpaPO withQuery(final boolean suppressExceptionForNull) {

        return new JstQueryLanguageJpaPO(suppressExceptionForNull);
    }

    protected String queryLanguageString;

    protected Class<?> entityClass;

    /**
     * Constructor
     */
    protected JstQueryLanguageJpaPO(final boolean suppressExceptionForNull) {

        super(suppressExceptionForNull);
        return;
    }

    /**
     * @return {@link Query}
     */
    protected Query createQueryLanguageQuery(final String queryLanguageString) {

        return getEntityManager().createQuery(queryLanguageString);
    }

    /**
     * @return {@link TypedQuery}
     */
    protected <ENTITY> TypedQuery<ENTITY> createQueryLanguageQuery(final String queryLanguageString,
            final Class<ENTITY> resultClass) {

        return getEntityManager().createQuery(queryLanguageString, resultClass);
    }

    /**
     * @return {@link Class}
     */
    @SuppressWarnings("unchecked")
    public <ENTITY> Class<ENTITY> getEntityClass() {

        return (Class<ENTITY>) this.entityClass;
    }

    /**
     * @return {@link Query}
     */
    @Override
    public Query getQuery() {

        if (null == this.query) {

            try {
                if (isResultClass() && isQueryLanaguageString()) {
                    this.query = createQueryLanguageQuery(getQueryLanguageString(), getEntityClass());
                } else if (isQueryLanaguageString()) {
                    this.query = createQueryLanguageQuery(getQueryLanguageString());
                } else {
                    throw new JustifyException(
                            "Verify that a coherent set of parameters were defined in the PO ["
                                    + this.getClass().getName() + "].");
                }
            } catch (final Exception e) {
                throw new JustifyException(e);
            }
        }
        return this.query;
    }

    /**
     * @return {@link String}
     */
    public String getQueryLanguageString() {

        return this.queryLanguageString;
    }

    /**
     * @return boolean
     */
    public boolean isQueryLanaguageString() {

        return null != this.queryLanguageString;
    }

    /**
     * @return boolean
     */
    public boolean isResultClass() {

        return null != this.entityClass;
    }

    /**
     * @return {@link JstQueryLanguageJpaPO}
     */
    public <ENTITY> JstQueryLanguageJpaPO withEntityClass(final Class<ENTITY> entityClass) {

        this.entityClass = entityClass;
        return this;
    }

    /**
     * @return {@link JstQueryLanguageJpaPO}
     */
    @Override
    public JstQueryLanguageJpaPO withEntityManager(final EntityManager entityManager) {

        return (JstQueryLanguageJpaPO) super.withEntityManager(entityManager);
    }

    /**
     * @return {@link JstQueryLanguageJpaPO}
     */
    @Override
    public JstQueryLanguageJpaPO withPersistenceUnitName(final String persistenceUnitName) {

        return (JstQueryLanguageJpaPO) super.withPersistenceUnitName(persistenceUnitName);
    }

    /**
     * @return {@link JstQueryLanguageJpaPO}
     */
    public JstQueryLanguageJpaPO withQueryLanguageString(final String queryLanguageString) {

        this.queryLanguageString = queryLanguageString;
        return this;
    }
}
