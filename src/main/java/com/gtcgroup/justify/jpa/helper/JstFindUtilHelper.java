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

import java.util.Optional;

import javax.persistence.EntityManager;

import com.gtcgroup.justify.jpa.po.JstFindInstancesJpaPO;
import com.gtcgroup.justify.jpa.po.JstFindSingleJpaPO;
import com.gtcgroup.justify.jpa.po.internal.BaseJpaPO;

/**
 * This Helper class provides persistence {@link EntityManager} support.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstFindUtilHelper {

    INSTANCE;

    /**
     * This method forces a trip to the database without altering the state of
     * cache.
     *
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public static <ENTITY> boolean existsInDatabase(final JstFindInstancesJpaPO findPO) {

        final Optional<EntityManager> entityManager = findPO.getEntityManager();
        ENTITY entity = null;

        try {
            if (entityManager.isPresent()) {

                for (final Object entityContainingIdentity : findPO.getEntitiesContainingIdentity()) {

                    final Optional<ENTITY> entityIdentity = retrieveEntityIdentity(entityManager.get(),
                            entityContainingIdentity);

                    if (entityIdentity.isPresent()) {

                        if (findPO.isForceDatabaseTripWhenNoCacheCoordination()) {
                            entity = (ENTITY) entityManager.get().find(entityContainingIdentity.getClass(),
                                    entityIdentity.get(), BaseJpaPO.getForceDatabaseTrip());
                        } else {
                            entity = (ENTITY) entityManager.get().find(entityContainingIdentity.getClass(),
                                    entityIdentity.get());
                        }

                    }
                }
            }
        } catch (@SuppressWarnings("unused") final Exception e) {
            return false;
        }
        return null != entity;
    }

    /**
     * @return {@link Optional}
     */
    @SuppressWarnings("unchecked")
    public static <ENTITY> Optional<ENTITY> findSingle(final JstFindSingleJpaPO findPO) {

        final Optional<EntityManager> entityManager = findPO.getEntityManager();

        try {
            if (entityManager.isPresent()) {

                return (Optional<ENTITY>) Optional.ofNullable(entityManager.get().find(findPO.getEntityClass(),
                        findPO.getEntityIdentity(), findPO.getQueryHints()));
            }
        } catch (@SuppressWarnings("unused") final Exception e) {
            // Continue.
        } finally {
            findPO.closeEncapsulatedEntityManager();
        }
        return Optional.empty();
    }

    /**
     * @return {@link Optional}
     */
    @SuppressWarnings("unchecked")
    public static <IDENTITY> Optional<IDENTITY> retrieveEntityIdentity(final EntityManager entityManager,
            final Object entityWithIdentity) {

        try {
            return (Optional<IDENTITY>) Optional.ofNullable(
                    entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entityWithIdentity));

        } catch (@SuppressWarnings("unused") final Exception e) {
            // Continue.
        }
        return Optional.empty();
    }
}
