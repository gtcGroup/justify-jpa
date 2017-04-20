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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;
import com.gtcgroup.justify.core.helper.internal.ReflectionUtilHelper;
import com.gtcgroup.justify.core.po.internal.StreamPO;

/**
 * This Helper class searches the persistence.xml file for the
 * {@link PersistenceUnitProperties}.JDBC_URL.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JdbcUrlCacheHelper {

    @SuppressWarnings("javadoc")
    INTERNAL;
    
	public static final String PERSISTENCE_DOT_XML = "META-INF/persistence.xml";

    private static final String NOT_AVAILABLE = "n@tAvailable";

    private static String jdbcURL = NOT_AVAILABLE;

    private static String persistenceUnitName = NOT_AVAILABLE;

    /**
     * @return {@link String}
     */
    private static String retrieveJdbcUrlOrNullFromPersistencePropertyMapOrNull(
        final Map<String, Object> persistencePropertyMapOrNull) {

        if (null != persistencePropertyMapOrNull) {

            if (persistencePropertyMapOrNull.containsKey(EntityManagerProperties.JDBC_URL)) {

                return (String)persistencePropertyMapOrNull.get(EntityManagerProperties.JDBC_URL);
            }
        }
        return null;
    }

    /**
     * @return {@link String}
     */
    public static String retrieveJdbcUrlOrNull(final String persistenceUnitName,
        final Map<String, Object> persistencePropertyMapOrNull) {

        if ((NOT_AVAILABLE != jdbcURL) && (JdbcUrlCacheHelper.persistenceUnitName == persistenceUnitName)) {
            return jdbcURL;
        }

        String jdbcURL = retrieveJdbcUrlOrNullFromPersistencePropertyMapOrNull(persistencePropertyMapOrNull);

        if (null != jdbcURL) {
            JdbcUrlCacheHelper.persistenceUnitName = persistenceUnitName;
            JdbcUrlCacheHelper.jdbcURL = jdbcURL;
            return jdbcURL;
        }

        StreamPO streamPO = null;

        try {

            streamPO = ReflectionUtilHelper.getResourceAsStream(PERSISTENCE_DOT_XML, false);

            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();

            final Document document = builder.parse(streamPO.getInputStreamToBeClosed());

            final NodeList nodeList = document.getElementsByTagName("persistence-unit");

            Node nNode = null;

            for (int i = 0; i < nodeList.getLength(); i++) {

                nNode = nodeList.item(i);
                final Element eElement = (Element)nNode;

                if (eElement.getAttribute("name").equals(persistenceUnitName)) {

                    JdbcUrlCacheHelper.persistenceUnitName = eElement.toString();
                    break;
                }

                if (i == nodeList.getLength() - 1) {

                    throw new JustifyRuntimeException(
                        "The persistence unit name [" + persistenceUnitName + "] could not be found.");
                }
            }

            final Element eElement = (Element)nNode;

            jdbcURL = processNodeList(eElement);

        } catch (final Exception e) {

            throw new JustifyRuntimeException(e);
        } finally {
            if (null != streamPO) {
                streamPO.closeInputStream();
            }
        }
        return jdbcURL;
    }

    private static String processNodeList(final Element element) {

        String jdbcUrlOrNull = null;

        final NodeList nodeList = element.getElementsByTagName("property");

        if (0 == nodeList.getLength()) {
            return jdbcUrlOrNull;
        }

        for (int i = 0; i < nodeList.getLength(); i++) {

            final Node node = nodeList.item(i);

            if (PersistenceUnitProperties.JDBC_URL.equals(node.getAttributes().getNamedItem("name").getNodeValue())) {

                jdbcUrlOrNull = node.getAttributes().getNamedItem("value").getNodeValue();
                JdbcUrlCacheHelper.jdbcURL = jdbcUrlOrNull;
                break;
            }
        }
        return jdbcUrlOrNull;
    }
}