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
package com.gtcgroup.justify.jpa.po.internal;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.CascadePolicy;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

/**
 * This Parameter Object base class supports {@link EntityManager} processing.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.8.5
 */
public abstract class BaseEntityManagerPropertiesPO extends BaseJpaPO {

    /**
     * These {@link QueryHints} are typically used when there are multiple servers
     * without cache coordination.
     */
    protected static final Map<String, Object> FORCE_DATABASE_TRIP = new HashMap<>();

    static {

        BaseEntityManagerPropertiesPO.FORCE_DATABASE_TRIP.put(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);

        BaseEntityManagerPropertiesPO.FORCE_DATABASE_TRIP.put(QueryHints.CACHE_USAGE, CacheUsage.DoNotCheckCache);

        BaseEntityManagerPropertiesPO.FORCE_DATABASE_TRIP.put(QueryHints.REFRESH_CASCADE,
                CascadePolicy.CascadeByMapping);

        BaseEntityManagerPropertiesPO.FORCE_DATABASE_TRIP.put(QueryHints.REFRESH, HintValues.TRUE);
    }

    protected Map<String, Object> queryHints = new HashMap<>();

    private boolean forceDatabaseTripWhenNoCacheCoordinationAvailable;

    private boolean readOnly = false;

    /**
     * Constructor
     */
    protected BaseEntityManagerPropertiesPO(final String persistenceUnitName) {
        super(persistenceUnitName);

        this.persistenceUnitName = persistenceUnitName;

        return;
    }

    /**
     * This method returns standard and vendor-specific properties and hints.
     *
     * @return {@link Map}
     */
    public Map<String, Object> getQueryHints() {

        if (isForceDatabaseTripWhenNoCacheCoordinationAvailable()) {
            this.queryHints.putAll(BaseEntityManagerPropertiesPO.FORCE_DATABASE_TRIP);
        }

        if (isReadOnly()) {
            this.queryHints.put(QueryHints.READ_ONLY, HintValues.TRUE);
        }

        return this.queryHints;
    }

    /**
     * {@link QueryHints} are typically used when there are multiple servers without
     * cache coordination.
     *
     * return boolean
     */
    public boolean isForceDatabaseTripWhenNoCacheCoordinationAvailable() {
        return this.forceDatabaseTripWhenNoCacheCoordinationAvailable;
    }

    /**
     * @return boolean
     */
    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setForceDatabaseTripWhenNoCacheCoordinationAvailable(
            final boolean forceDatabaseTripWhenNoCacheCoordination) {
        this.forceDatabaseTripWhenNoCacheCoordinationAvailable = forceDatabaseTripWhenNoCacheCoordination;
    }

    /**
     * This method returns standard and vendor-specific properties and hints.
     */
    public void setQueryHint(final String key, final Object value) {
        this.queryHints.put(key, value);
    }

    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

}
