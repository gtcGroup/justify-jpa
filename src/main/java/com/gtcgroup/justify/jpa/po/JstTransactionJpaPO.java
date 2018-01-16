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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import com.gtcgroup.justify.jpa.po.internal.BaseJpaPO;

/**
 * This Parameter Object class supports transactions.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v.6.2
 */
public class JstTransactionJpaPO extends BaseJpaPO {

    /**
     * This method initializes the class.
     *
     * @return {@link JstTransactionJpaPO}
     */
    public static JstTransactionJpaPO withException(final String persistenceUnitName) {

        return new JstTransactionJpaPO(persistenceUnitName);
    }

    private List<Object> entityCreateAndUpdateList = new ArrayList<>();

    private final List<Object> entityDeleteList = new ArrayList<>();

    /**
     * Constructor
     */
    protected JstTransactionJpaPO(final String persistenceUnitName) {

        super(persistenceUnitName);
        return;
    }

    /**
     * @return {@link Object}
     */
    @SuppressWarnings("unchecked")
    public <ENTITY> List<ENTITY> getEntityCreateAndUpdateList() {

        return (List<ENTITY>) this.entityCreateAndUpdateList;
    }

    /**
     * @return {@link Object}
     */
    public List<Object> getEntityDeleteList() {

        return this.entityDeleteList;
    }

    /**
     * This method substitutes merged entities while completing the transaction.
     */
    @SuppressWarnings("unchecked")
    public <ENTITY> void replaceEntityCreateAndUpdateList(final List<ENTITY> entityCreateAndUpdateList) {

        this.entityCreateAndUpdateList = (List<Object>) entityCreateAndUpdateList;
        return;
    }

    /**
     * @return {@link JstTransactionJpaPO}
     */
    public <ENTITY> JstTransactionJpaPO withCreateAndUpdateEntities(final ENTITY... entities) {

        this.entityCreateAndUpdateList.addAll(Arrays.asList(entities));
        return this;
    }

    /**
     * @return {@link JstTransactionJpaPO}
     */
    public <ENTITY> JstTransactionJpaPO withCreateAndUpdateList(final List<ENTITY> entityList) {

        this.entityCreateAndUpdateList.addAll(entityList);
        return this;
    }

    /**
     * @return {@link JstTransactionJpaPO}
     */
    public <ENTITY> JstTransactionJpaPO withDeleteEntities(final ENTITY... entity) {

        this.entityDeleteList.addAll(Arrays.asList(entity));
        return this;
    }

    /**
     * @return {@link JstTransactionJpaPO}
     */
    public <ENTITY> JstTransactionJpaPO withDeleteList(final List<ENTITY> deleteList) {

        this.entityDeleteList.addAll(deleteList);
        return this;
    }

    /**
     * @return {@link JstTransactionJpaPO}
     */
    public JstTransactionJpaPO withEntityManager(final EntityManager entityManager) {

        super.setEntityManager(entityManager);
        return this;
    }
}
