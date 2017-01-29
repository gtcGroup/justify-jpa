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

package com.gtcgroup.justify.jpa.helper;

import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;
import com.gtcgroup.justify.core.helper.internal.ReflectionUtilHelper;
import com.gtcgroup.justify.core.po.internal.StreamPO;

/**
 * This Helper class caches the persistence.xml file.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstPersistenceDotXmlCacheHelper {

	@SuppressWarnings("javadoc")
	INTERNAL;

	private static String JDBC_URL = null;

	/**
	 * This method loads the properties into cache.
	 */
	public static void loadPersistenceProperties(final String resourceName) {

		JstPersistenceDotXmlCacheHelper.JDBC_URL = null;
		StreamPO streamPO = null;

		try {

			streamPO = ReflectionUtilHelper.getResourceAsStream(resourceName, false);

			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();

			final Document document = builder.parse(streamPO.getInputStreamToBeClosed());

			final NodeList nodeList = document.getElementsByTagName("property");

			processNodeList(nodeList);

		} catch (final Exception e) {

			throw new JustifyRuntimeException(e);
		} finally {
			if (null != streamPO) {
				streamPO.closeInputStream();
			}
		}
	}

	private static void processNodeList(final NodeList nodeList) throws ParserConfigurationException {

		if (0 == nodeList.getLength()) {
			throw new ParserConfigurationException("There are no persistence properties found in this file.");
		}

		for (int i = 0; i < nodeList.getLength(); i++) {

			final Node node = nodeList.item(i);

			if (PersistenceUnitProperties.JDBC_URL.equals(node.getAttributes().getNamedItem("name").getNodeValue())) {

				JstPersistenceDotXmlCacheHelper.JDBC_URL = node.getAttributes().getNamedItem("value").getNodeValue();
				break;
			}
		}
	}

	/**
	 * @return {@link Properties}
	 */
	public static String retrievePersistenceDotXmlJdbcUrl() {

		if (null == JstPersistenceDotXmlCacheHelper.JDBC_URL) {

			throw new JustifyRuntimeException("The JDBC URL from the persistence.xml file was not loaded into cache.");

		}
		return JstPersistenceDotXmlCacheHelper.JDBC_URL;
	}
}