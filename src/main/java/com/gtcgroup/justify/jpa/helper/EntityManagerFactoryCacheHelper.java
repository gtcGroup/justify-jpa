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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;
import com.gtcgroup.justify.core.helper.JstJndiUtilHelper;
import com.gtcgroup.justify.jpa.rm.QueryRM;

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
public enum EntityManagerFactoryCacheHelper {

	/** Instance */
	INSTANCE;

	private static Map<String, EntityManagerFactory> ENTITY_MANAGER_FACTORY_MAP = new ConcurrentHashMap<String, EntityManagerFactory>();

	private static final String JNDI_ENTITY_MANAGER_FACTORY = "java:justify/EntityManagerFactory";

	/**
	 * This method closes the {@link EntityManager}.
	 *
	 * @param entityManager
	 */
	public static void closeEntityManager(final EntityManager entityManager) {

		if (null == entityManager) {
			return;
		}

		try {

			if (entityManager.isOpen()) {

				entityManager.clear();
				entityManager.close();
			}

		} catch (final Exception e) {
			throw new TestingRuntimeException(e);
		}
		return;
	}

	/**
	 * This method closes the {@link EntityManager}.
	 *
	 * @param queryRM
	 */
	public static void closeQueryRM(final QueryRM queryRM) {

		closeEntityManager(queryRM.getEntityManager());
	}

	/**
	 * @param persistenceUnitName
	 * @return {@link EntityManager}
	 */
	public static EntityManager createEntityManagerToBeClosed(final String persistenceUnitName) {

		return retrieveEntityManagerFactoryStatic(persistenceUnitName, null).createEntityManager();

	}

	/**
	 * @param persistenceUnitName
	 * @param propertyOverrideMap
	 * @return {@link EntityManager}
	 */
	public static EntityManager createEntityManagerToBeClosed(final String persistenceUnitName,
			final Map<String, Object> propertyOverrideMap) {

		return retrieveEntityManagerFactoryStatic(persistenceUnitName, propertyOverrideMap).createEntityManager();

	}

	/**
	 * @param persistenceUnitName
	 * @return {@link EntityManager}
	 */
	public static QueryRM createQueryRmToBeClosed(final String persistenceUnitName) {

		return new QueryRM()
				.setEntityManager(retrieveEntityManagerFactoryStatic(persistenceUnitName, null).createEntityManager());

	}

	/**
	 * @param persistenceUnitName
	 * @param propertyOverrideMap
	 *            or null
	 * @return {@link EntityManagerFactory}
	 */
	public static EntityManagerFactory retrieveEntityManagerFactoryStatic(final String persistenceUnitName,
			final Map<String, Object> propertyOverrideMap) {

		final StringBuilder mapKey = new StringBuilder();

		mapKey.append(persistenceUnitName);

		if (null != propertyOverrideMap) {

			for (final Entry<String, Object> entry : propertyOverrideMap.entrySet()) {

				mapKey.append(":");
				mapKey.append(entry.getKey());
				mapKey.append(",");
				mapKey.append(entry.getValue());
			}
		}

		final String mapKeys = mapKey.toString();

		if (!EntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.containsKey(mapKeys)) {

			final EntityManagerFactory entityManagerFactory = Persistence
					.createEntityManagerFactory(persistenceUnitName);

			EntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.put(mapKey.toString(), entityManagerFactory);

			JstJndiUtilHelper.bind(EntityManagerFactoryCacheHelper.JNDI_ENTITY_MANAGER_FACTORY,
					EntityManagerFactoryCacheHelper.INSTANCE);

		}
		return EntityManagerFactoryCacheHelper.ENTITY_MANAGER_FACTORY_MAP.get(mapKeys);
	}

	/**
	 * @param persistenceUnitName
	 * @param propertyOverrideMap
	 * @return {@link EntityManagerFactory}
	 */
	@SuppressWarnings("static-method")
	public EntityManagerFactory retrieveEntityManagerFactoryInstance(final String persistenceUnitName,
			final Map<String, Object> propertyOverrideMap) {

		return EntityManagerFactoryCacheHelper.retrieveEntityManagerFactoryStatic(persistenceUnitName,
				propertyOverrideMap);
	}
}