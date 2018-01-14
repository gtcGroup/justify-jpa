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

import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.rulechain.JstRuleChain;
import com.gtcgroup.justify.core.si.JstRuleChainSI;
import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.core.test.extension.JstConfigureTestUserIdExtension;
import com.gtcgroup.justify.jpa.assertions.AssertionsJPA;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.extension.JstConfigureJpaExtension;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.JstQueryStringJpaPO;
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
public class JstQueryLanguageRmTest {

    private static final String SELECT_NOTE_SINGLE_MODIFIABLE = "SELECT note FROM NothingBean note WHERE note.text='"
            + ConstantsTestJPA.NOTE_TEXT_ONE + "'";

    private static final String SELECT_NOTE_LIST = "SELECT note FROM NothingBean note";

    private static final String SELECT_NOTE_SINGLE_FAKE = "SELECT note FROM NothingBean note WHERE note.text='"
            + "fakeIdentity" + "'";

    @Rule
    public JstRuleChainSI ruleChain = JstRuleChain.outerRule(false).around(JstConfigureJpaExtension
            .withPersistenceUnit(ConstantsTestJPA.JUSTIFY_PU).withDataPopulators(NoteDataPopulator.class))
            .around(JstConfigureTestUserIdExtension.withUserId());

    @Test(expected = JustifyException.class)
    public void testQueryLanguageSingle_exception() {

        JstQueryStringJpaRM.querySingle(
                JstQueryStringJpaPO.withQuery(false).withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU));
    }

    @Test(expected = JustifyException.class)
    public void testQueryLanguageSingle_exceptionString() {
        JstQueryStringJpaRM
                .querySingle(JstQueryStringJpaPO.withQuery(false).withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
                        .withQueryLanguageString(JstQueryLanguageRmTest.SELECT_NOTE_SINGLE_FAKE));
    }

    @Test
    public void testQueryLanguageSingle_exceptionString_supress() {

        final NoteDE note = JstQueryStringJpaRM
                .querySingle(JstQueryStringJpaPO.withQuery(true).withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
                        .withQueryLanguageString(JstQueryLanguageRmTest.SELECT_NOTE_SINGLE_FAKE));

        Assertions.assertThat(note).isNull();
    }

    @Test
    public void testQueryLanguageSingle_queryLanguageString_resultClass() {

        final NoteDE note = JstQueryStringJpaRM.querySingle(JstQueryStringJpaPO.withQuery(false)
                .withEntityClass(NoteDE.class).withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
                .withQueryLanguageString(JstQueryLanguageRmTest.SELECT_NOTE_SINGLE_MODIFIABLE));

        Assertions.assertThat(note.getText()).isEqualTo(ConstantsTestJPA.NOTE_TEXT_ONE);
    }

    @Test
    public void testQueryList_queryLanguageString() {

        final List<Object> noteList = JstQueryStringJpaRM.queryReadOnlyList(JstQueryStringJpaPO
                .withQuery(false).withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
                .withQueryLanguageString(JstQueryLanguageRmTest.SELECT_NOTE_LIST).withEntityClass(NoteDE.class));

        AssertionsJPA.assertExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, noteList, ConstantsTestJPA.NOTE_UUID_ONE);
    }

    @Test
    public void testQueryList_queryLanguageString_incomplete() {

        final List<Object> noteList = JstQueryStringJpaRM.queryReadOnlyList(
                JstQueryStringJpaPO.withQuery(false).withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
                        .withQueryLanguageString(JstQueryLanguageRmTest.SELECT_NOTE_LIST));

        AssertionsJPA.assertExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, noteList, ConstantsTestJPA.NOTE_UUID_ONE);
    }
}
