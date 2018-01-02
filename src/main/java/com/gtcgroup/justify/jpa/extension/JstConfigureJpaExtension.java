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
package com.gtcgroup.justify.jpa.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.gtcgroup.justify.core.helper.JstReflectionUtilHelper;
import com.gtcgroup.justify.core.test.base.JstBaseExtension;
import com.gtcgroup.justify.core.test.exception.internal.JustifyTestingException;
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
public class JstConfigureJpaExtension extends JstBaseExtension implements BeforeTestExecutionCallback {

    public final static String KEY_DELIMITER = "_~_";

    private static List<String> DATA_POPULATOR_ALREADY_PROCESSED_LIST = new ArrayList<>();

    private static List<String> DATA_POPULATOR_TO_BE_PROCESSED_LIST = new ArrayList<>();

    protected static Class<? extends JstBaseDataPopulator>[] dataPopulators;

    protected static String persistenceUnitName;

    protected static void initializePersistenceUnitName(final ExtensionContext context) {

        final JstConfigureJPA configureJPA = retrieveAnnotation(context, JstConfigureJPA.class);

        if (null != configureJPA) {

            JstConfigureJpaExtension.persistenceUnitName = configureJPA.persistenceUnitName();
            JstConfigureJpaExtension.dataPopulators = configureJPA.dataPopulators();
        }
    }

    protected static Class<?> retrieveClassname(final String listKey) {

        Class<?> dataPopulator;

        final String[] stringArray = listKey.split(JstConfigureJpaExtension.KEY_DELIMITER);

        try {
            dataPopulator = Class.forName(stringArray[1]);
        } catch (final Exception e) {
            throw new JustifyTestingException(e);
        }

        return dataPopulator;
    }

    protected static String retrievePersistenceKey(final String listKey) {

        final String[] stringArray = listKey.split(JstConfigureJpaExtension.KEY_DELIMITER);

        return stringArray[0] + JstConfigureJpaExtension.KEY_DELIMITER + stringArray[1];
    }

    protected static String retrievePersistenceUnitName(final String listKey) {

        final String[] stringArray = listKey.split(JstConfigureJpaExtension.KEY_DELIMITER);

        return stringArray[0];
    }

    protected Map<String, Object> persistencePropertyMapOrNull = null;

    protected EntityManagerFactory entityManagerFactory;

    protected boolean dataPopulatorSubmitted = false;

    protected String persistenceKey;

    @Override
    public void beforeTestExecution(final ExtensionContext context) throws Exception {

        initializePersistenceUnitName(context);
        determineToBeProcessed();

        if (0 != JstConfigureJpaExtension.DATA_POPULATOR_TO_BE_PROCESSED_LIST.size()) {

            for (final String listKey : JstConfigureJpaExtension.DATA_POPULATOR_TO_BE_PROCESSED_LIST) {

                final Class<?> dataPopulator = retrieveClassname(listKey);

                processDataPopulator(dataPopulator, listKey);

                JstConfigureJpaExtension.DATA_POPULATOR_ALREADY_PROCESSED_LIST.add(listKey);
            }
        }
        JstConfigureJpaExtension.DATA_POPULATOR_TO_BE_PROCESSED_LIST.clear();

    }

    protected void determineToBeProcessed() {

        this.dataPopulatorSubmitted = true;

        for (final Class<?> dataPopulator : JstConfigureJpaExtension.dataPopulators) {

            final String listKey = formatListKey(dataPopulator);

            if (!JstConfigureJpaExtension.DATA_POPULATOR_ALREADY_PROCESSED_LIST.contains(listKey)
                    && !JstConfigureJpaExtension.DATA_POPULATOR_TO_BE_PROCESSED_LIST.contains(listKey)) {

                if (!JstBaseDataPopulator.class.isAssignableFrom(dataPopulator)) {

                    throw new JustifyTestingException("\nThe class [" + dataPopulator.getSimpleName()
                            + "] does not appear to extend a base class for populating persistence test data.\n");
                }
                JstConfigureJpaExtension.DATA_POPULATOR_TO_BE_PROCESSED_LIST.add(listKey);
            }
        }
    }

    protected final String formatListKey(final Class<?> dataPopulator) {

        return this.persistenceKey + JstConfigureJpaExtension.KEY_DELIMITER + dataPopulator.getName();
    }

    protected void processDataPopulator(final Class<?> populatorClass, final String listKey) {

        EntityManager entityManager = null;

        JstBaseDataPopulator dataPopulator;

        try {
            dataPopulator = (JstBaseDataPopulator) JstReflectionUtilHelper
                    .instantiateInstanceWithPublicConstructorNoArgument(populatorClass);
        } catch (final Exception e) {
            JstConfigureJpaExtension.DATA_POPULATOR_TO_BE_PROCESSED_LIST.remove(formatListKey(populatorClass));
            throw (JustifyTestingException) e;
        }

        final List<Object> createList = dataPopulator.populateCreateListTM(retrievePersistenceUnitName(listKey));

        entityManager = JstEntityManagerFactoryCacheHelper
                .createEntityManagerToBeClosed(JstConfigureJpaExtension.persistenceUnitName);
        // .createEntityManagerToBeClosedWithKey(retrievePersistenceKey(listKey));

        JstTransactionJpaRM.transactMultipleEntities(JstTransactionJpaPO.withException()
                .withEntityManager(entityManager).withCreateAndUpdateList(createList));

        JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
    }
}