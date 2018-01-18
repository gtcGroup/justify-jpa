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

import java.util.Map;

/**
 * This Parameter Object base class supports find operations using the Resource
 * Manager pattern.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.8.5
 */
public abstract class BaseFindJpaPO extends BaseJpaPO {

    protected Class<Object> entityClass;

    protected boolean readOnly = false;

    protected Map<String, Object> queryHints;

    protected boolean forceDatabaseTripWhenNoCacheCoordination = false;

    /**
     * Constructor
     */
    protected BaseFindJpaPO(final String persistenceUnitName) {
        super(persistenceUnitName);
    }

    /**
     * @return {@link Class}
     */
    @SuppressWarnings("unchecked")
    public <ENTITY> Class<ENTITY> getEntityClass() {
        return (Class<ENTITY>) this.entityClass;
    }

    /**
     * @return {@link Map}
     */
    public Map<String, Object> getQueryHints() {
        return this.queryHints;
    }

    /**
     * @return boolean
     */
    public boolean isEntityClass() {
        return null != this.entityClass;
    }

    /**
     * @return boolean
     */
    public boolean isForceDatabaseTripWhenNoCacheCoordination() {
        return this.forceDatabaseTripWhenNoCacheCoordination;
    }

    /**
     * @return boolean
     */
    public boolean isQueryParameterMap() {
        return null != this.queryHints;
    }

    /**
     * @return boolean
     */
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @SuppressWarnings("unchecked")
    public <ENTITY> void setEntityClass(final Class<ENTITY> entityClass) {
        this.entityClass = (Class<Object>) entityClass;
    }

    public void setForceDatabaseTripWhenNoCacheCoordination(final boolean forceDatabaseTripWhenNoCacheCoordination) {
        this.forceDatabaseTripWhenNoCacheCoordination = forceDatabaseTripWhenNoCacheCoordination;
    }

    public void setQueryHint(final String key, final Object value) {
        getQueryHints().put(key, value);
    }
}
