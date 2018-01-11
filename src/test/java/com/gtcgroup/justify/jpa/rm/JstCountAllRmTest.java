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
package com.gtcgroup.justify.jpa.rm;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.rulechain.JstRuleChain;
import com.gtcgroup.justify.core.si.JstRuleChainSI;
import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.core.test.extension.JstConfigureTestUserIdExtension;
import com.gtcgroup.justify.jpa.de.dependency.NotAnEntityDE;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.extension.JstConfigureJpaExtension;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.JstCountAllJpaPO;
import com.gtcgroup.justify.jpa.po.JstFindAllJpaPO;
import com.gtcgroup.justify.jpa.populator.dependency.NoteDataPopulator;

/**
 * Test Class
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
@SuppressWarnings("all")
public class JstCountAllRmTest {

    @Rule
    public JstRuleChainSI ruleChain = JstRuleChain.outerRule(false).around(JstConfigureJpaExtension
            .withPersistenceUnit(ConstantsTestJPA.JUSTIFY_PU).withDataPopulators(NoteDataPopulator.class))
            .around(JstConfigureTestUserIdExtension.withUserId());

    @Test
    public void testCount_happyPath1() {

        final long count = JstCountAllJpaRM.count(JstCountAllJpaPO.withQuery(false).withResultClass(NoteDE.class)
                .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU));

        final List<NoteDE> noteList = JstFindAllJpaRM.findReadOnlyList(JstFindAllJpaPO.withFindAll(false)
                .withEntityClass(NoteDE.class).withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU));

        Assertions.assertThat(count).isEqualTo(noteList.size());
    }

    @Test
    public void testCount_happyPath2() {

        long count;
        List<NoteDE> noteList;

        final EntityManager entityManager = JstEntityManagerFactoryCacheHelper
                .createEntityManagerToBeClosed(ConstantsTestJPA.JUSTIFY_PU);

        try {

            count = JstCountAllJpaRM.count(
                    JstCountAllJpaPO.withQuery(false).withResultClass(NoteDE.class).withEntityManager(entityManager));

            noteList = JstFindAllJpaRM.findReadOnlyList(JstFindAllJpaPO.withFindAll(false).withEntityClass(NoteDE.class)
                    .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU));

        } finally {
            JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
        }

        Assertions.assertThat(count).isEqualTo(noteList.size());
    }

    @Test(expected = JustifyException.class)
    public void testCount_missingResultClass() {

        final long count = JstCountAllJpaRM
                .count(JstCountAllJpaPO.withQuery(false).withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU));

        Assertions.assertThat(count).isGreaterThan(0);
    }

    @Test(expected = JustifyException.class)
    public void testCount_notAnEntity_exception() {

        JstCountAllJpaRM.count(JstCountAllJpaPO.withQuery(false).withResultClass(NotAnEntityDE.class)
                .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU));
    }
}