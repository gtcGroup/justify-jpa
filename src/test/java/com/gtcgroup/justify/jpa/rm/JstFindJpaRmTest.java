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
package com.gtcgroup.justify.jpa.rm;

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.rulechain.JstRuleChain;
import com.gtcgroup.justify.core.si.JstRuleChainSI;
import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.jpa.de.dependency.NotAnEntityDE;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.extension.JstConfigureTestJpaExtension;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.JstFindSingleJpaPO;
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
@SuppressWarnings("all")
public class JstFindJpaRmTest {

    private static final String FAKE_IDENTITY = "fakeIdentity";

    @Rule
    public JstRuleChainSI ruleChain = JstRuleChain.outerRule(false).around(JstConfigureTestJpaExtension
            .withPersistenceUnit(ConstantsTestJPA.JUSTIFY_PU).withDataPopulators(NoteDataPopulator.class));

    private NoteDE createFindJpaPO(final boolean suppressExceptionForNull, final Class<?> clazz,
            final String entityIdentity) {

        final JstFindSingleJpaPO findJpaPO = JstFindSingleJpaPO.withFind(suppressExceptionForNull)
                .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withEntityClass(clazz)
                .withEntityIdentity(entityIdentity);

        final NoteDE note = JstQueryFindJpaRM.findSingle(findJpaPO);
        return note;
    }

    @Test
    public void testFindReadOnlySingleOrException() {

        final NoteDE note = createFindJpaPO(false, NoteDE.class, ConstantsTestJPA.NOTE_UUID_TWO);

        Assertions.assertThat(note.getUuid()).isEqualTo(ConstantsTestJPA.NOTE_UUID_TWO);
    }

    @Test(expected = JustifyException.class)
    public void testFindReadOnlySingleOrException_exception() {

        createFindJpaPO(false, NotAnEntityDE.class, JstFindJpaRmTest.FAKE_IDENTITY);
    }

    @Test(expected = JustifyException.class)
    public void testFindReadOnlySingleOrException_exception_suppress() {

        createFindJpaPO(true, NotAnEntityDE.class, JstFindJpaRmTest.FAKE_IDENTITY);
    }

    @Test
    public void testReadOnlySingleOrException_null() {

        final NoteDE note = createFindJpaPO(true, NoteDE.class, JstFindJpaRmTest.FAKE_IDENTITY);

        Assertions.assertThat(note).isNull();
    }

    @Test
    public void testWithEntityManager() {

        EntityManager entityManager = null;
        NoteDE note;

        try {
            entityManager = JstEntityManagerFactoryCacheHelper
                    .createEntityManagerToBeClosed(ConstantsTestJPA.JUSTIFY_PU);

            note = JstQueryFindJpaRM.findSingle(JstFindSingleJpaPO.withFind(true).withEntityClass(NoteDE.class)
                    .withEntityIdentity(ConstantsTestJPA.NOTE_UUID_TWO).withEntityManager(entityManager));
        } finally {
            JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
        }

        Assertions.assertThat(note).isNotNull();
    }

    @Test
    public void testWithModifiablePopulatedEntityContainingIdentity() {

        final NoteDE note = JstQueryFindJpaRM
                .findSingle(JstFindSingleJpaPO.withFind(true).withPopulatedEntityContainingIdentity(NoteDataPopulator.noteTwo)
                        .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU));

        Assertions.assertThat(note).isNotNull();
    }

    @Test(expected = JustifyException.class)
    public void testWithoutEntityManager() {

        JstQueryFindJpaRM.findSingle(JstFindSingleJpaPO.withFind(true).withEntityClass(NoteDE.class)
                .withEntityIdentity(ConstantsTestJPA.NOTE_UUID_TWO));
    }

    @Test(expected = JustifyException.class)
    public void testWithoutTargetEntity() {

        final NoteDE note = JstQueryFindJpaRM
                .findSingle(JstFindSingleJpaPO.withFind(true).withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
                        .withEntityIdentity(ConstantsTestJPA.NOTE_UUID_TWO));

        Assertions.assertThat(note).isNotNull();
    }
}
