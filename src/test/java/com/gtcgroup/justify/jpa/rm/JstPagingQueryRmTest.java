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
import com.gtcgroup.justify.core.test.extension.JstConfigureTestUserIdExtension;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.extension.JstConfigureJpaExtension;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.JstQueryNamedJpaPO;
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
public class JstPagingQueryRmTest {

    private static final String QUERY_NOTE_LIST = "queryNoteList";

    @Rule
    public JstRuleChainSI ruleChain = JstRuleChain.outerRule(false).around(JstConfigureJpaExtension
            .withPersistenceUnit(ConstantsTestJPA.JUSTIFY_PU).withDataPopulators(NoteDataPopulator.class))
            .around(JstConfigureTestUserIdExtension.withUserId());

    private List<NoteDE> createNamedQueryList(final boolean suppressExceptionForNull, final String queryName) {

        final List<NoteDE> noteList = JstQueryNamedJpaRM.queryReadOnlyList(JstQueryNamedJpaPO
                .withQuery(suppressExceptionForNull).withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
                .withQueryName(queryName).withFirstResult(1).withMaxResults(1));
        return noteList;
    }

    @Test
    public void testNamedQueryList() {

        final List<NoteDE> noteList = createNamedQueryList(true, JstPagingQueryRmTest.QUERY_NOTE_LIST);

        Assertions.assertThat(noteList.size()).isEqualTo(1);
    }
}
