/*
 * [Licensed per the Open Source "MIT License".]
 *
 * Copyright (c) 2006 - 2016 by
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.gtcgroup.justify.core.exception.internal.JustifyRuntimeException;

/**
 * This Helper class caches {@link EntityManagerFactory}s.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstEntityManagerFactoryCacheHelper {

	/** Instance */
	INSTANCE;

	/** Indicates that the URL is coming from the persistence.xml document. */
	public static final String DEFAULT_JDBC_URL = "_defaultJdbcURL";

	/** Key suffix for cached {@link EntityManagerFactory}. */
	public static final String NO_PERSISTENCE_PROPERTY_MAP = "_noPersistencePropertyMap";

	private static Map<String, EntityManagerFactory> ENTITY_MANAGER_FACTORY_MAP = new ConcurrentHashMap<String, EntityManagerFactory>();

	/**
	 * This method closes the {@link EntityManager}.
	 */
	public static void closeEntityManager(final EntityManager entityManager) {

		if (null == entityManager) {
			return;
		}

		if (entityManager.isOpen()) {

			entityManager.clear();
			entityManager.close();
		}

		return;
	}

	/**
	 * This method returns the entity manager factory key for use in subsequent
	 * retrieval invocations.
	 *
	 * @return {@link String}
	 */
	public static String createEntityManagerFactory(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMapOrNull) {

		final String entityManagerFactoryKey = formatKey(persistenceUnitName, persistencePropertyMapOrNull);

		if (!JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.containsKey(entityManagerFactoryKey)) {

			try {
				final EntityManagerFactory entityManagerFactory = Persistence
						.createEntityManagerFactory(persistenceUnitName, persistencePropertyMapOrNull);

				final EntityManager entityManager = entityManagerFactory.createEntityManager();
				entityManager.setProperty(null, "toForceCompletingConfiguration");
				JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);

				JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.put(entityManagerFactoryKey,
						entityManagerFactory);

			} catch (final Exception e) {

				throw new JustifyRuntimeException(e);
			}
		}
		return entityManagerFactoryKey;
	}

	/**
	 * @return {@link EntityManager}
	 */
	public static EntityManager createEntityManagerToBeClosed(final String persistenceUnitName) {

		return retrieveEntityManagerFactory(persistenceUnitName + JstEntityManagerFactoryCacheHelper.DEFAULT_JDBC_URL)
				.createEntityManager();

	}

	/**
	 * @return {@link EntityManager}
	 */
	public static EntityManager createEntityManagerToBeClosed(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMap) {

		return createEntityManagerToBeClosed(formatKey(persistenceUnitName, persistencePropertyMap));

	}

	/**
	 * @return {@link EntityManager}
	 */
	public static EntityManager createEntityManagerToBeClosedWithEntityManagerFactoryKey(
			final String entityManagerFactoryKey) {

		return retrieveEntityManagerFactory(entityManagerFactoryKey).createEntityManager();

	}

	/**
	 * @return {@link String}
	 */
	private static String formatKey(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMapOrNull) {

		String key = persistenceUnitName;

		if (null == persistencePropertyMapOrNull
				|| !persistencePropertyMapOrNull.containsKey(PersistenceUnitProperties.JDBC_URL)) {

			key += JstEntityManagerFactoryCacheHelper.DEFAULT_JDBC_URL;

		} else {

			key += "_" + persistencePropertyMapOrNull.get(PersistenceUnitProperties.JDBC_URL);
		}

		return key;
	}

	/**
	 * @return {@link EntityManagerFactory}
	 */
	public static EntityManagerFactory getCurrentEntityManagerFactory(final String persistenceUnitName) {

		return JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.get(persistenceUnitName);
	}

	/**
	 * @return {@link Map}<String,EntityManagerFactory>
	 */
	public static Map<String, EntityManagerFactory> getEntityManagerFactoryMap() {
		return JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP;
	}

	/**
	 * @return {@link EntityManagerFactory}
	 */
	public static EntityManagerFactory retrieveEntityManagerFactory(final String entityManagerFactoryKey) {

		EntityManagerFactory entityManagerFactory = null;

		if (JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.containsKey(entityManagerFactoryKey)) {

			entityManagerFactory = JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP
					.get(entityManagerFactoryKey);
		} else {

			throw new JustifyRuntimeException(
					"The entity manager factory key [" + entityManagerFactoryKey + "] could not be resolved.");
		}
		return entityManagerFactory;
	}
}