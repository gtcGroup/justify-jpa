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
package com.gtcgroup.justify.jpa.rule;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.rulechain.JstRuleChain;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.extension.JstConfigureJpaExtension;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.JstQueryFindSingleJpaPO;
import com.gtcgroup.justify.jpa.populator.dependency.NoteAdditionalDataPopulator;
import com.gtcgroup.justify.jpa.populator.dependency.NoteDataPopulator;
import com.gtcgroup.justify.jpa.rm.JstQueryFindSingleJpaRM;

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
public class JstConfigureJpaRuleTest {

    private static final Map<String, Object> PERSISTENCE_PROPERTY_ADDITIONAL_MAP = new HashMap<>();

    static {
        JstConfigureJpaRuleTest.PERSISTENCE_PROPERTY_ADDITIONAL_MAP.put(PersistenceUnitProperties.JDBC_URL,
            "jdbc:h2:mem:justify-additional;MODE=MSSQLServer");
        JstConfigureJpaRuleTest.PERSISTENCE_PROPERTY_ADDITIONAL_MAP.put(PersistenceUnitProperties.JDBC_USER, "additional");
        JstConfigureJpaRuleTest.PERSISTENCE_PROPERTY_ADDITIONAL_MAP.put(PersistenceUnitProperties.JDBC_PASSWORD,
            "additional");
    }

    private static final Map<String, Object> PERSISTENCE_PROPERTY_MAP2 = new HashMap<>();

    static {

        JstConfigureJpaRuleTest.PERSISTENCE_PROPERTY_MAP2.put("key", "value");
    }

    @Rule
    public JstRuleChain ruleChain = JstRuleChain.outerRule(false)
        .around(JstConfigureJpaExtension
            .withPersistenceUnit(ConstantsTestJPA.JUSTIFY_PU, JstConfigureJpaRuleTest.PERSISTENCE_PROPERTY_ADDITIONAL_MAP)
            .withDataPopulators(
                NoteDataPopulator.class, NoteDataPopulator.class, NoteAdditionalDataPopulator.class))
        .around(JstConfigureJpaExtension
            .withPersistenceUnit(ConstantsTestJPA.JUSTIFY_PU, JstConfigureJpaRuleTest.PERSISTENCE_PROPERTY_MAP2)
            .withDataPopulators(
                NoteDataPopulator.class, NoteDataPopulator.class, NoteAdditionalDataPopulator.class))
        .around(JstConfigureJpaExtension.withPersistenceUnit(ConstantsTestJPA.JUSTIFY_SECOND_PU)
            .withDataPopulators(NoteDataPopulator.class, NoteDataPopulator.class, NoteAdditionalDataPopulator.class))
        .around(JstConfigureJpaExtension
            .withPersistenceUnit(ConstantsTestJPA.JUSTIFY_PU)
            .withDataPopulators(
                NoteDataPopulator.class, NoteDataPopulator.class, NoteAdditionalDataPopulator.class));

    @Test
    public void testForPopulator_additional() {

        JstQueryFindSingleJpaRM.findSingle(JstQueryFindSingleJpaPO.withFind(false)
            .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
            .withEntityClass(NoteDE.class)
            .withEntityIdentity(NoteAdditionalDataPopulator.ADDITIONAL_UUID)
            .withPersistencePropertyMap(PERSISTENCE_PROPERTY_ADDITIONAL_MAP));
    }

    @Test
    public void testForPopulator_note_readonly() {

        JstQueryFindSingleJpaRM.findSingle(JstQueryFindSingleJpaPO.withFind(false)
            .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
            .withEntityClass(NoteDE.class)
            .withEntityIdentity(ConstantsTestJPA.NOTE_UUID_TWO));
    }
}
