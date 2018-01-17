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

package com.gtcgroup.justify.jpa.helper.internal;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gtcgroup.justify.core.helper.JstReflectionUtilHelper;
import com.gtcgroup.justify.core.po.JstStreamPO;

/**
 * This Cache Helper class lazily retrieves the
 * {@link PersistenceUnitProperties}.JDBC_URL.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum PersistenceDotXmlCacheHelper {

    INTERNAL;

    private static final String NON_JTA_DATA_SOURCE = "non-jta-data-source";

    private static final String JTA_DATA_SOURCE = "jta-data-source";

    private static final String JDBC_DATA_SOURCE = "jdbc-data-source";

    public static final String PERSISTENCE_DOT_XML = "META-INF/persistence.xml";

    private static Map<String, String> currentJdbcUrlOrDatasourceMap = new ConcurrentHashMap<>();

    private static Map<String, String> persistenceXmlJdbcUrlOrDatasourceMap = new ConcurrentHashMap<>();

    /**
     * @return {@link Optional}
     */
    private static Optional<String> retrieveFromElement(final Element element) {

        try {
            final NodeList nodeList = element.getElementsByTagName("property");

            for (int i = 0; i < nodeList.getLength(); i++) {

                final Node node = nodeList.item(i);

                if (PersistenceUnitProperties.JDBC_URL
                        .equals(node.getAttributes().getNamedItem("name").getNodeValue())) {

                    final String jdbcUrlOrNull = node.getAttributes().getNamedItem("value").getNodeValue();
                    return Optional.ofNullable(jdbcUrlOrNull);
                }
            }
        } catch (@SuppressWarnings("unused") final Exception e) {
            // Continue.
        }
        return Optional.empty();
    }

    /**
     * @return {@link Optional}
     */
    private static Optional<String> retrieveFromPersistenceDotXML(final String persistenceUnitName) {

        Optional<JstStreamPO> jstStreamPO = Optional.empty();

        try {

            jstStreamPO = JstReflectionUtilHelper
                    .getResourceAsStreamPoAndBeSureToClose(PersistenceDotXmlCacheHelper.PERSISTENCE_DOT_XML);

            if (jstStreamPO.isPresent()) {

                final Optional<NodeList> nodeList = retrieveNodeList(jstStreamPO.get());

                return searchElement(persistenceUnitName, nodeList);
            }
        } catch (@SuppressWarnings("unused") final Exception e) {
            // Continue.
        } finally

        {
            if (jstStreamPO.isPresent()) {
                jstStreamPO.get().closeInputStream();
            }
        }
        return Optional.empty();
    }

    /**
     * This method searches for the {@link PersistenceUnitProperties}.JDBC_URL.
     *
     * @return {@link Optional}
     */
    public static Optional<String> retrieveJdbcUrlOrDatasource(final String persistenceUnitName,
            final Map<String, Object> persistencePropertyMapOrNull) {

        Optional<String> jdbcUrlOrDatasource = retrieveValueFromPersistencePropertyMap(persistenceUnitName,
                persistencePropertyMapOrNull);

        if (jdbcUrlOrDatasource.isPresent()) {
            PersistenceDotXmlCacheHelper.currentJdbcUrlOrDatasourceMap.put(persistenceUnitName,
                    jdbcUrlOrDatasource.get());
            return jdbcUrlOrDatasource;
        }

        if (PersistenceDotXmlCacheHelper.currentJdbcUrlOrDatasourceMap.containsKey(persistenceUnitName)) {
            return Optional
                    .ofNullable(PersistenceDotXmlCacheHelper.currentJdbcUrlOrDatasourceMap.get(persistenceUnitName));
        }

        jdbcUrlOrDatasource = retrieveFromPersistenceDotXML(persistenceUnitName);

        if (jdbcUrlOrDatasource.isPresent()) {

            PersistenceDotXmlCacheHelper.currentJdbcUrlOrDatasourceMap.put(persistenceUnitName,
                    jdbcUrlOrDatasource.get());

            return jdbcUrlOrDatasource;
        }
        return Optional.empty();
    }

    /**
     * @return {@link Optional}
     */
    private static Optional<NodeList> retrieveNodeList(final JstStreamPO jstStreamPO) {

        try {

            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();

            final Document document = builder.parse(jstStreamPO.getInputStreamToBeClosed());
            document.getDocumentElement().normalize();

            return Optional.of(document.getElementsByTagName("persistence-unit"));

        } catch (@SuppressWarnings("unused") final Exception e) {
            // Continue.
        }

        return Optional.empty();
    }

    /**
     * @return {@link Optional}
     */
    private static Optional<Node> retrievePersistenceUnitNode(final String persistenceUnitName,
            final NodeList nodeList) {

        Node nNode = null;

        for (int i = 0; i < nodeList.getLength(); i++) {

            nNode = nodeList.item(i);

            final Element eElement = (Element) nNode;

            if (eElement.getAttribute("name").equals(persistenceUnitName)) {

                break;
            }
        }
        return Optional.ofNullable(nNode);
    }

    /**
     * @return {@link Optional}
     */
    private static Optional<String> retrieveValueFromPersistencePropertyMap(final String persistenceUnitName,
            final Map<String, Object> persistencePropertyMapOrNull) {

        if (null != persistencePropertyMapOrNull
                && persistencePropertyMapOrNull.containsKey(EntityManagerProperties.JDBC_URL)) {

            return Optional.of((String) persistencePropertyMapOrNull.get(EntityManagerProperties.JDBC_URL));
        }
        return Optional
                .ofNullable(PersistenceDotXmlCacheHelper.persistenceXmlJdbcUrlOrDatasourceMap.get(persistenceUnitName));
    }

    /**
     * @return {@link Optional}
     */
    private static void searchElement(final Element eElement, final NodeList dataSourceList) {

        if (0 != dataSourceList.getLength()) {

            eElement.getElementsByTagName(PersistenceDotXmlCacheHelper.JTA_DATA_SOURCE).item(0).getTextContent();
        } else {

            final NodeList dataSourceListTemp = eElement
                    .getElementsByTagName(PersistenceDotXmlCacheHelper.JDBC_DATA_SOURCE);

            if (0 != dataSourceListTemp.getLength()) {

                eElement.getElementsByTagName(PersistenceDotXmlCacheHelper.JDBC_DATA_SOURCE).item(0).getTextContent();
            }
        }
    }

    /**
     * @return {@link Optional}
     */
    private static Optional<String> searchElement(final String persistenceUnitName, final Optional<NodeList> nodeList) {

        if (nodeList.isPresent()) {

            Element eElement;

            final Optional<Node> nNode = retrievePersistenceUnitNode(persistenceUnitName, nodeList.get());

            if (nNode.isPresent()) {

                eElement = (Element) nNode.get();

                if (nNode.get().getNodeType() == Node.ELEMENT_NODE) {

                    NodeList dataSourceList = eElement
                            .getElementsByTagName(PersistenceDotXmlCacheHelper.NON_JTA_DATA_SOURCE);

                    if (0 != dataSourceList.getLength()) {

                        eElement.getElementsByTagName(PersistenceDotXmlCacheHelper.NON_JTA_DATA_SOURCE).item(0)
                                .getTextContent();
                    } else {

                        dataSourceList = eElement.getElementsByTagName(PersistenceDotXmlCacheHelper.JTA_DATA_SOURCE);

                        searchElement(eElement, dataSourceList);
                    }
                }
                return retrieveFromElement(eElement);
            }
        }
        return Optional.empty();
    }
}