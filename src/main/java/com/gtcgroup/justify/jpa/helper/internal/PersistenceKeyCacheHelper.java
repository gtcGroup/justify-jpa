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

package com.gtcgroup.justify.jpa.helper.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;

/**
 * This Helper class caches the latest persistence key.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum PersistenceKeyCacheHelper {

    @SuppressWarnings("javadoc")
    INTERNAL;

    // Contains persistence unit name and jdbc URL.
    private static Map<String, String> persistenceKeyMap = new ConcurrentHashMap<String, String>();

    public static String formatPersistenceKey(final String persistenceUnitName, final String jdbcURL) {
        return persistenceUnitName + "_~_" + jdbcURL;
    }

    /**
     * @return {@link String}
     */
    public static String retrievePersistenceKey(final String persistenceUnitName) {

        if (!persistenceKeyMap.containsKey(persistenceUnitName)) {

            throw new JustifyRuntimeException("The persistence unit name [" + persistenceUnitName + "] is not valid.");
        }


        return persistenceKeyMap.get(persistenceUnitName);
    }

    /**
     * @return boolean
     */
    public static boolean containsJdbcURL(final String persistenceUnitName) {

        return persistenceKeyMap.containsKey(persistenceUnitName);
    }
}