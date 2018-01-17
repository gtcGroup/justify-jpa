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

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.rulechain.JstRuleChain;
import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.jpa.de.dependency.NotAnEntityDE;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.extension.JstConfigureTestJpaExtension;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.populator.dependency.NoteDataPopulator;

/**
 * Test Class
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public class JstEntityManagerUtilHelperTest {

    private EntityManager entityManager;

    @Rule
    public JstRuleChain ruleChain = JstRuleChain.outerRule(true).around(JstConfigureTestJpaExtension
            .withPersistenceUnit(ConstantsTestJPA.JUSTIFY_PU).withDataPopulators(NoteDataPopulator.class));

    @Before
    public void setup() {

        this.entityManager = JstEntityManagerFactoryCacheHelper
                .createEntityManagerToBeClosed(ConstantsTestJPA.JUSTIFY_PU);
    }

    @After
    public void teardown() {

        JstEntityManagerFactoryCacheHelper.closeEntityManager(this.entityManager);
    }

    @Test
    public void testClearAllInstancesFromPersistenceContext() {

        JstEntityManagerUtilHelper.clearAllInstancesFromPersistenceContext(this.entityManager);

    }

    @Test(expected = JustifyException.class)
    public void testEvictEntityInstanceFromSharedCache_fakeInstance() {

        JstEntityManagerUtilHelper.evictEntityInstanceFromSharedCache(this.entityManager, new NotAnEntityDE());
    }

    @Test(expected = JustifyException.class)
    public void testEvictEntityInstanceFromSharedCache_nonentity() {

        JstEntityManagerUtilHelper.evictEntityInstanceFromSharedCache(this.entityManager, new NotAnEntityDE());
    }

    @Test
    public void testEvictEntityInstanceFromSharedCache_populatedInstance() {

        JstEntityManagerUtilHelper.evictEntityInstanceFromSharedCache(this.entityManager, NoteDataPopulator.noteTwo);
    }

    @Test
    public void testEvictEntityInstancesFromSharedCache_class() {

        JstEntityManagerUtilHelper.evictEntityInstancesFromSharedCache(this.entityManager, NoteDE.class);
    }

    @Test
    public void testEvictEntityInstancesFromSharedCache_fakeIdentity() {

        JstEntityManagerUtilHelper.evictEntityInstancesFromSharedCache(this.entityManager, NoteDE.class, "fake");
    }

    @Test
    public void testEvictEntityInstancesFromSharedCache_identities() {

        JstEntityManagerUtilHelper.evictEntityInstancesFromSharedCache(this.entityManager, NoteDE.class,
                ConstantsTestJPA.NOTE_UUID_ONE);
    }

    @Test
    public void testEvictEntityInstancesFromSharedCache_nonentity() {

        JstEntityManagerUtilHelper.evictEntityInstancesFromSharedCache(this.entityManager, NotAnEntityDE.class);
    }
}