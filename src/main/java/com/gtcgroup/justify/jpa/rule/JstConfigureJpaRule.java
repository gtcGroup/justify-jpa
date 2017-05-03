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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Rule;
import org.junit.rules.TestRule;

import com.gtcgroup.justify.core.base.JstBaseRule;
import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;
import com.gtcgroup.justify.core.exception.internal.RuleException;
import com.gtcgroup.justify.core.helper.internal.ReflectionUtilHelper;
import com.gtcgroup.justify.jpa.helper.JstBaseDataPopulator;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.po.JstTransactionJpaPO;
import com.gtcgroup.justify.jpa.rm.JstTransactionJpaRM;

/**
 * This {@link Rule} class initializes persistence.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public class JstConfigureJpaRule extends JstBaseRule {

    public final static String KEY_DELIMITER = "_~_";

    private static List<String> DATA_POPULATOR_ALREADY_PROCESSED_LIST = new ArrayList<String>();

    private static List<String> DATA_POPULATOR_TO_BE_PROCESSED_LIST = new ArrayList<String>();

    /**
     * @return {@link TestRule}
     */
    @SuppressWarnings("unchecked")
    public static <RULE extends JstConfigureJpaRule> RULE withPersistenceUnit(final String persistenceUnitName) {

        return (RULE)new JstConfigureJpaRule(persistenceUnitName);
    }

    /**
     * @return {@link TestRule}
     */
    @SuppressWarnings("unchecked")
    public static <RULE extends JstConfigureJpaRule> RULE withPersistenceUnit(final String persistenceUnitName,
        final Map<String, Object> persistencePropertyMap) {

        return (RULE)new JstConfigureJpaRule(persistenceUnitName, persistencePropertyMap);
    }

    protected String persistenceUnitName;

    protected Map<String, Object> persistencePropertyMapOrNull = null;

    protected EntityManagerFactory entityManagerFactory;

    protected boolean dataPopulatorSubmitted = false;

    protected String persistenceKey;

    /**
     * Constructor - protected
     */
    protected JstConfigureJpaRule(final String persistenceUnitName) {

        this(persistenceUnitName, null);
    }

    /**
     * Constructor - protected
     */
    protected JstConfigureJpaRule(final String persistenceUnitName, final Map<String, Object> persistencePropertyMap) {

        super();

        this.persistenceKey =
            JstEntityManagerFactoryCacheHelper.createEntityManagerFactory(persistenceUnitName, persistencePropertyMap);

        this.persistenceUnitName = persistenceUnitName;
    }

    /**
     * @see JstBaseRule#afterTM()
     */
    @Override
    public void afterTM() throws Throwable {

        return;
    }

    /**
     * @see JstBaseRule#beforeTM()
     */
    @Override
    public void beforeTM() {

        if (0 != JstConfigureJpaRule.DATA_POPULATOR_TO_BE_PROCESSED_LIST.size()) {

            for (final String listKey : JstConfigureJpaRule.DATA_POPULATOR_TO_BE_PROCESSED_LIST) {

                final Class<?> dataPopulator = retrieveClassname(listKey);

                processDataPopulator(dataPopulator, listKey);

                JstConfigureJpaRule.DATA_POPULATOR_ALREADY_PROCESSED_LIST.add(listKey);
            }
        }
        JstConfigureJpaRule.DATA_POPULATOR_TO_BE_PROCESSED_LIST.clear();
    }

    protected void processDataPopulator(final Class<?> populatorClass, final String listKey) {

        EntityManager entityManager = null;

        JstBaseDataPopulator dataPopulator;

        try {
            dataPopulator = (JstBaseDataPopulator)ReflectionUtilHelper
                .instantiatePublicConstructorNoArgument(populatorClass);
        } catch (final Exception e) {
            JstConfigureJpaRule.DATA_POPULATOR_TO_BE_PROCESSED_LIST.remove(formatListKey(populatorClass));
            throw (JustifyRuntimeException)e;
        }

        final List<Object> createList = dataPopulator.populateCreateListTM(retrievePersistenceUnitName(listKey));

        entityManager = JstEntityManagerFactoryCacheHelper
            .createEntityManagerToBeClosedWithKey(retrievePersistenceKey(listKey));

        JstTransactionJpaRM.transactMultipleEntities(JstTransactionJpaPO.withException()
            .withEntityManager(entityManager)
            .withCreateAndUpdateList(createList));

        JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
    }

    /**
     * @return {@link TestRule}
     */
    @SuppressWarnings("unchecked")
    public <RULE extends JstConfigureJpaRule> RULE withDataPopulators(final Class<?>... dataPopulators) {

        this.dataPopulatorSubmitted = true;

        for (final Class<?> dataPopulator : dataPopulators) {

            final String listKey = formatListKey(dataPopulator);

            if ((!JstConfigureJpaRule.DATA_POPULATOR_ALREADY_PROCESSED_LIST.contains(listKey))
                && (!DATA_POPULATOR_TO_BE_PROCESSED_LIST.contains(listKey))) {

                if (!JstBaseDataPopulator.class.isAssignableFrom(dataPopulator)) {

                    throw new RuleException("\nThe class [" + dataPopulator.getSimpleName()
                        + "] does not appear to extend a base class for populating persistence test data.\n");
                }
                JstConfigureJpaRule.DATA_POPULATOR_TO_BE_PROCESSED_LIST.add(listKey);
            }
        }
        return (RULE)this;
    }

    protected String formatListKey(final Class<?> dataPopulator) {

        return this.persistenceKey + KEY_DELIMITER + dataPopulator.getName();
    }

    protected static Class<?> retrieveClassname(final String listKey) {

        Class<?> dataPopulator;

        final String[] stringArray = listKey.split(KEY_DELIMITER);

        try {
            dataPopulator = Class.forName(stringArray[2]);
        } catch (final Exception e) {
            throw new RuleException("Unable to retrieve classname from list key [" + listKey + "].");
        }

        return dataPopulator;
    }

    protected static String retrievePersistenceUnitName(final String listKey) {

        final String[] stringArray = listKey.split(KEY_DELIMITER);

        return stringArray[0];
    }

    protected static String retrievePersistenceKey(final String listKey) {

        final String[] stringArray = listKey.split(KEY_DELIMITER);

        return stringArray[0] + KEY_DELIMITER + stringArray[1];
    }
}