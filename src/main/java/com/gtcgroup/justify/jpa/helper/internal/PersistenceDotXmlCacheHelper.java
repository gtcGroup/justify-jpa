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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.gtcgroup.justify.core.helper.JstReflectionUtilHelper;
import com.gtcgroup.justify.core.po.JstStreamPO;
import com.gtcgroup.justify.core.test.exception.internal.JustifyTestingException;

/**
 * This Cache Helper class lazily retrieves the
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
public enum PersistenceDotXmlCacheHelper {

    INTERNAL;

    public static final String PERSISTENCE_DOT_XML = "META-INF/persistence.xml";

    private static Map<String, String> currentJdbcUrlOrDatasourceMap = new ConcurrentHashMap<>();

    private static Map<String, String> persistenceXmlJdbcUrlOrDatasourceMap = new ConcurrentHashMap<>();

    private static String retrieveFromElement(final Element element) {

        String jdbcUrlOrNull = null;

        final NodeList nodeList = element.getElementsByTagName("property");

        if (0 == nodeList.getLength()) {
            return jdbcUrlOrNull;
        }

        for (int i = 0; i < nodeList.getLength(); i++) {

            final Node node = nodeList.item(i);

            if (PersistenceUnitProperties.JDBC_URL.equals(node.getAttributes().getNamedItem("name").getNodeValue())) {

                jdbcUrlOrNull = node.getAttributes().getNamedItem("value").getNodeValue();
                break;
            }
        }
        return jdbcUrlOrNull;
    }

    private static String retrieveFromPersistenceDotXML(final String persistenceUnitName) {

        JstStreamPO jstStreamPO = null;
        String jdbcUrlOrDatasource = null;

        try {

            jstStreamPO = JstReflectionUtilHelper.getResourceAsStream(PersistenceDotXmlCacheHelper.PERSISTENCE_DOT_XML,
                    false);

            final NodeList nodeList = retrieveNodeList(jstStreamPO);

            final Node nNode = retrievePersistenceUnitNode(persistenceUnitName, nodeList);

            if (null == nNode) {

                throw new JustifyTestingException(
                        "The persistence unit name [" + persistenceUnitName + "] could not be found.");
            }

            final Element eElement = (Element) nNode;

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                NodeList dataSourceList = eElement.getElementsByTagName("non-jta-data-source");

                if (0 != dataSourceList.getLength()) {

                    jdbcUrlOrDatasource = eElement.getElementsByTagName("non-jta-data-source").item(0).getTextContent();
                } else {

                    dataSourceList = eElement.getElementsByTagName("jta-data-source");

                    if (0 != dataSourceList.getLength()) {

                        jdbcUrlOrDatasource = eElement.getElementsByTagName("jta-data-source").item(0).getTextContent();
                    } else {

                        dataSourceList = eElement.getElementsByTagName("jdbc-data-source");

                        if (0 != dataSourceList.getLength()) {

                            jdbcUrlOrDatasource = eElement.getElementsByTagName("jdbc-data-source").item(0)
                                    .getTextContent();
                        } else {

                            jdbcUrlOrDatasource = retrieveFromElement(eElement);

                        }
                    }
                }
            }
        } catch (

        final Exception e) {

            throw new JustifyTestingException(e);
        } finally {
            if (null != jstStreamPO) {
                jstStreamPO.closeInputStream();
            }
        }

        if (null == jdbcUrlOrDatasource) {

            throw new JustifyTestingException("The JPA property [" + PersistenceUnitProperties.JDBC_URL
                    + "] using the persistence unit name [" + persistenceUnitName + "] could not be found.");
        }
        PersistenceDotXmlCacheHelper.persistenceXmlJdbcUrlOrDatasourceMap.put(persistenceUnitName, jdbcUrlOrDatasource);
        return jdbcUrlOrDatasource;
    }

    /**
     * @return {@link String}
     */
    private static String retrieveFromPersistencePropertyMap(final String persistenceUnitName,
            final Map<String, Object> persistencePropertyMapOrNull) {

        if (null != persistencePropertyMapOrNull) {

            if (persistencePropertyMapOrNull.containsKey(EntityManagerProperties.JDBC_URL)) {

                return (String) persistencePropertyMapOrNull.get(EntityManagerProperties.JDBC_URL);
            }
        }

        if (PersistenceDotXmlCacheHelper.persistenceXmlJdbcUrlOrDatasourceMap.containsKey(persistenceUnitName)) {
            return PersistenceDotXmlCacheHelper.persistenceXmlJdbcUrlOrDatasourceMap.get(persistenceUnitName);
        }
        return null;
    }

    /**
     * This method searches the persistence.xml file for the
     * {@link PersistenceUnitProperties}.JDBC_URL.
     *
     * @return {@link String}
     */
    public static String retrieveJdbcUrlOrDatasource(final String persistenceUnitName,
            final Map<String, Object> persistencePropertyMapOrNull) {

        String jdbcUrlOrDatasource = retrieveFromPersistencePropertyMap(persistenceUnitName,
                persistencePropertyMapOrNull);

        if (null != jdbcUrlOrDatasource) {
            PersistenceDotXmlCacheHelper.currentJdbcUrlOrDatasourceMap.put(persistenceUnitName, jdbcUrlOrDatasource);
            return jdbcUrlOrDatasource;
        }

        if (PersistenceDotXmlCacheHelper.currentJdbcUrlOrDatasourceMap.containsKey(persistenceUnitName)) {
            return PersistenceDotXmlCacheHelper.currentJdbcUrlOrDatasourceMap.get(persistenceUnitName);
        }

        jdbcUrlOrDatasource = retrieveFromPersistenceDotXML(persistenceUnitName);

        PersistenceDotXmlCacheHelper.currentJdbcUrlOrDatasourceMap.put(persistenceUnitName, jdbcUrlOrDatasource);

        return jdbcUrlOrDatasource;
    }

    private static NodeList retrieveNodeList(final JstStreamPO jstStreamPO)
            throws ParserConfigurationException, SAXException, IOException {

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();

        final Document document = builder.parse(jstStreamPO.getInputStreamToBeClosed());

        document.getDocumentElement().normalize();

        return document.getElementsByTagName("persistence-unit");
    }

    private static Node retrievePersistenceUnitNode(final String persistenceUnitName, final NodeList nodeList) {

        Node nNode = null;

        for (int i = 0; i < nodeList.getLength(); i++) {

            nNode = nodeList.item(i);

            final Element eElement = (Element) nNode;

            if (eElement.getAttribute("name").equals(persistenceUnitName)) {

                break;
            }
        }
        return nNode;
    }
}