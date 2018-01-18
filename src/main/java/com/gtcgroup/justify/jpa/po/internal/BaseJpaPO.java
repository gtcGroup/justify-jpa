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
package com.gtcgroup.justify.jpa.po.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.CascadePolicy;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.gtcgroup.justify.core.base.JstBasePO;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;

/**
 * This Parameter Object base class supports queries and transactions using a
 * Resource Manager.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public abstract class BaseJpaPO extends JstBasePO {

    /**
     * These {@link QueryHints} are typically used when there are multiple servers
     * without cache coordination.
     */
    protected static final Map<String, Object> FORCE_DATABASE_TRIP = new HashMap<>();

    static {

        BaseJpaPO.FORCE_DATABASE_TRIP.put(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);

        BaseJpaPO.FORCE_DATABASE_TRIP.put(QueryHints.CACHE_USAGE, CacheUsage.DoNotCheckCache);

        BaseJpaPO.FORCE_DATABASE_TRIP.put(QueryHints.REFRESH_CASCADE, CascadePolicy.CascadeByMapping);

        BaseJpaPO.FORCE_DATABASE_TRIP.put(QueryHints.REFRESH, HintValues.TRUE);
    }

    public static Map<String, Object> getForceDatabaseTrip() {
        return BaseJpaPO.FORCE_DATABASE_TRIP;
    }

    protected EntityManager entityManager;

    protected boolean entityManagerEncapsulated = false;

    protected String persistenceUnitName;

    /**
     * Constructor
     */
    protected BaseJpaPO(final String persistenceUnitName) {
        super();

        this.persistenceUnitName = persistenceUnitName;

        return;
    }

    /**
     * This method closes the {@link EntityManager} if the creation was encapsulated
     * within this PO.
     */
    public void closeEncapsulatedEntityManager() {

        if (this.entityManagerEncapsulated) {
            JstEntityManagerFactoryCacheHelper.closeEntityManager(this.entityManager);
            this.entityManager = null;
        }
    }

    /**
     * @return {@link Optional}
     */
    public Optional<EntityManager> getEntityManager() {

        if (null == this.entityManager) {

            final Optional<EntityManager> entityManager = JstEntityManagerFactoryCacheHelper
                    .createEntityManagerToBeClosed(this.persistenceUnitName);

            if (entityManager.isPresent()) {
                this.entityManager = entityManager.get();
            }
        }
        return Optional.ofNullable(this.entityManager);
    }

    protected void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
