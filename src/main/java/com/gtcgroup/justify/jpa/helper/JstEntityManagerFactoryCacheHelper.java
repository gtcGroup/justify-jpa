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

package com.gtcgroup.justify.jpa.helper;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.gtcgroup.justify.jpa.helper.internal.PersistenceDotXmlCacheHelper;

/**
 * This Helper class caches {@link EntityManagerFactory}(s).
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public enum JstEntityManagerFactoryCacheHelper {

	/** Instance */
	INSTANCE;

	private static Map<String, EntityManagerFactory> entityManagerFactoryMap = new ConcurrentHashMap<>();

	public static void closeEntityManager(final EntityManager entityManager) {

		if (null != entityManager && entityManager.isOpen()) {

			entityManager.clear();
			entityManager.close();
		}
	}

	/**
	 * @return boolean
	 */
	public static boolean createEntityManagerFactory(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMapOrNull, final boolean forceReplacement) {

		if (JstEntityManagerFactoryCacheHelper.entityManagerFactoryMap.containsKey(persistenceUnitName)
				&& !forceReplacement) {
			return true;
		}

		final Optional<String> jdbcUrlOrDatasource = PersistenceDotXmlCacheHelper
				.retrieveJdbcUrlOrDatasource(persistenceUnitName, persistencePropertyMapOrNull);

		if (jdbcUrlOrDatasource.isPresent()) {

			try {
				final EntityManagerFactory entityManagerFactory = Persistence
						.createEntityManagerFactory(persistenceUnitName, persistencePropertyMapOrNull);

				final EntityManager entityManager = entityManagerFactory.createEntityManager();

				JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);

				JstEntityManagerFactoryCacheHelper.entityManagerFactoryMap.put(persistenceUnitName,
						entityManagerFactory);

				return true;

			} catch (@SuppressWarnings("unused") final Exception e) {

				// Continue.
			}
			return false;
		}
		return false;
	}

	/**
	 * @return {@link Optional}
	 */
	public static Optional<EntityManager> createEntityManagerToBeClosed(final String persistenceUnitName) {

		final boolean isEntityManagerFactory = createEntityManagerFactory(persistenceUnitName, null, false);

		if (isEntityManagerFactory) {

			return Optional.of(JstEntityManagerFactoryCacheHelper.entityManagerFactoryMap.get(persistenceUnitName)
					.createEntityManager());
		}
		return Optional.empty();
	}

	/**
	 * @return {@link Optional}
	 */
	public static Optional<EntityManager> createEntityManagerToBeClosed(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMapOrNull,
			final boolean forceReplacementEntityManagerFactory) {

		final boolean isEntityManagerFactory = createEntityManagerFactory(persistenceUnitName,
				persistencePropertyMapOrNull, forceReplacementEntityManagerFactory);

		if (isEntityManagerFactory) {

			return Optional.of(JstEntityManagerFactoryCacheHelper.entityManagerFactoryMap.get(persistenceUnitName)
					.createEntityManager());
		}
		return Optional.empty();
	}

	/**
	 * This method
	 *
	 * @param persistenceUnitName
	 * @param persistencePropertyMapOrNull
	 */
	public static void startupJPA(final String persistenceUnitName,
			final Map<String, Object> persistencePropertyMapOrNull) {

		final Optional<EntityManager> entityManagerOptional = JstEntityManagerFactoryCacheHelper
				.createEntityManagerToBeClosed(persistenceUnitName, persistencePropertyMapOrNull, true);

		if (entityManagerOptional.isPresent()) {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManagerOptional.get());
		} else {
			// TODO: fix this
			throw new RuntimeException(" PPPPPPPPPPPrrrrrrrrooooooooobbbbbbbbllllllllleeeeeeeeemmmmmmmm");
		}
	}
}