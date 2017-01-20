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

import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;

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

	/** Key suffix for cached {@link EntityManagerFactory}. */
	public static final String NO_PERSISTENCE_PROPERTY_MAP = "_noPersistencePropertyMap";

	private static Map<String, EntityManagerFactory> ENTITY_MANAGER_FACTORY_MAP = new ConcurrentHashMap<String, EntityManagerFactory>();

	/**
	 * @return {@link String}
	 */
	public static String calculateKey(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMapOrNull) {

		String key = persistenceUnitName;

		if (null != persistencePropertyMapOrNull) {

			key += "_" + persistencePropertyMapOrNull.toString();
		} else {

			key += JstEntityManagerFactoryCacheHelper.NO_PERSISTENCE_PROPERTY_MAP;
		}
		return key;
	}

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
	 * @return {@link EntityManagerFactory}
	 */
	public static EntityManagerFactory createEntityManagerFactory(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMapOrNull) {

		String key = persistenceUnitName;

		key = calculateKey(persistenceUnitName, persistencePropertyMapOrNull);

		EntityManagerFactory entityManagerFactory = null;

		// RETURN
		if (JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.containsKey(key)) {

			entityManagerFactory = JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.get(key);
			// Ensure that both entries stay synchronized.
			JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.put(persistenceUnitName,
					entityManagerFactory);

			return entityManagerFactory;
		}

		// RETURN
		if (null == persistencePropertyMapOrNull
				&& JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.containsKey(persistenceUnitName)) {

			entityManagerFactory = JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP
					.get(persistenceUnitName);

			return entityManagerFactory;
		}

		try {
			entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName,
					persistencePropertyMapOrNull);
		} catch (final Exception e) {

			throw new TestingRuntimeException(e);
		}

		// Ensures retrieval with either key.
		JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.put(key, entityManagerFactory);
		JstEntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.put(persistenceUnitName, entityManagerFactory);

		return entityManagerFactory;
	}

	/**
	 * @return {@link EntityManager}
	 */
	public static EntityManager createEntityManagerToBeClosed(final String persistenceUnitName) {

		return createEntityManagerFactory(persistenceUnitName, null).createEntityManager();

	}

	/**
	 * @return {@link EntityManager}
	 */
	public static EntityManager createEntityManagerToBeClosed(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMap) {

		return createEntityManagerFactory(persistenceUnitName, persistencePropertyMap).createEntityManager();

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
}