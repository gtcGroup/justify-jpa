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

import java.util.Map;

import javax.persistence.EntityManager;

import com.gtcgroup.justify.jpa.po.internal.BaseJpaPO;

/**
 * This Parameter Object class supports find operations.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstQueryFindSingleJpaPO extends BaseJpaPO {

    /**
     * This method initializes the class.
     *
     * @return {@link JstQueryNamedJpaPO}
     */
    public static JstQueryFindSingleJpaPO withPersistenceUnitName(final String persistenceUnitName) {

        return new JstQueryFindSingleJpaPO(persistenceUnitName);
    }

    private Class<Object> entityClass;

    private Object entityIdentity;

    /**
     * Constructor
     */
    protected JstQueryFindSingleJpaPO(final String persistenceUnitName) {

        super(persistenceUnitName);
        return;
    }

    /**
     * @return {@link Class}
     */
    public Class<Object> getEntityClass() {

        return this.entityClass;
    }

    /**
     * @return {@link Object}
     */
    public Object getEntityIdentity() {

        return this.entityIdentity;
    }

    /**
     * @return {@link JstQueryFindSingleJpaPO}
     */
    @SuppressWarnings("unchecked")
    public <ENTITY> JstQueryFindSingleJpaPO withEntityClass(final Class<ENTITY> entityClass) {

        this.entityClass = (Class<Object>) entityClass;
        return this;
    }

    /**
     * @return {@link JstQueryFindSingleJpaPO}
     */
    public JstQueryFindSingleJpaPO withEntityIdentity(final Object entityIdentity) {

        this.entityIdentity = entityIdentity;
        return this;
    }

    /**
     * @return {@link JstQueryFindSingleJpaPO}
     */
    public JstQueryFindSingleJpaPO withEntityManager(final EntityManager entityManager) {

        super.setEntityManager(entityManager);
        return this;
    }

    /**
     * @return {@link JstQueryFindSingleJpaPO}
     */
    @Override
    public JstQueryFindSingleJpaPO withForceDatabaseTripWhenNoCacheCoordination(final boolean suppress) {

        return (JstQueryFindSingleJpaPO) super.withForceDatabaseTripWhenNoCacheCoordination(suppress);
    }

    /**
     * @return {@link JstQueryFindSingleJpaPO}
     */
    @Override
    public JstQueryFindSingleJpaPO withPersistencePropertyMap(final Map<String, Object> persistencePropertyMap) {

        return (JstQueryFindSingleJpaPO) super.withPersistencePropertyMap(persistencePropertyMap);
    }

    /**
     * @return {@link JstQueryFindSingleJpaPO}
     */
    public JstQueryFindSingleJpaPO withPopulatedEntityContainingIdentity(
            final Object populatedEntityContainingIdentity) {

        this.populatedEntityContainingIdentity = populatedEntityContainingIdentity;
        return this;
    }
}
