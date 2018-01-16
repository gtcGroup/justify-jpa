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

import java.util.Optional;

import javax.persistence.EntityManager;

import com.gtcgroup.justify.jpa.po.JstQueryFindSingleJpaPO;

/**
 * This Helper class provides persistence {@link EntityManager} support.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstFindUtilHelper {

    INSTANCE;

    /**
     * @return {@link Object} or null
     */
    @SuppressWarnings("unchecked")
    public static <ENTITY> Optional<ENTITY> findSingle(final JstQueryFindSingleJpaPO findPO) {

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
    public static <ENTITY> Optional<ENTITY> retrieveIdentity(final String persistenceUnitName,
            final Object entityWithPrimaryKey) {

        final Optional<EntityManager> entityManager = JstEntityManagerFactoryCacheHelper
                .createEntityManagerToBeClosed(persistenceUnitName);

        try {
            if (entityManager.isPresent()) {
                return (Optional<ENTITY>) Optional.ofNullable(entityManager.get().getEntityManagerFactory()
                        .getPersistenceUnitUtil().getIdentifier(entityWithPrimaryKey));
            }
        } catch (@SuppressWarnings("unused") final Exception e) {
            // Continue.
        } finally {
            if (entityManager.isPresent()) {
                JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager.get());
            }
        }
        return Optional.empty();
    }
}
