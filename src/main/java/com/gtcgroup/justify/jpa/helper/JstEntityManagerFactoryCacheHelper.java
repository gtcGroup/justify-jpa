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

import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.gtcgroup.justify.core.po.JstExceptionPO;
import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.jpa.po.JstEntityManagerFactoryPropertyPO;
import com.gtcgroup.justify.jpa.po.internal.DefaultManagerFactoryPropertyPO;

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

	private static Map<String, EntityManagerFactory> currentEntityManagerFactory = new ConcurrentHashMap<>();

	public static void closeEntityManager(final EntityManager entityManager) {

		if (null != entityManager && entityManager.isOpen()) {

			entityManager.clear();
			entityManager.close();
		}
	}

	/**
	 * @return {@link Optional}
	 */
	public static Optional<EntityManager> createEntityManagerToBeClosed(final String persistenceUnitName) {

		if (null == persistenceUnitName) {
			throw new JustifyException(JstExceptionPO.withMessage("The persistence unit name is null."));
		}

		// Early Return
		if (currentEntityManagerFactory.containsKey(persistenceUnitName)) {
			return Optional.of(retrieveCurrentEntityManagerFactory(persistenceUnitName).createEntityManager());
		}

		return Optional.empty();
	}

	/**
	 * This method
	 *
	 * @param persistenceUnitName
	 * @param optionalEntityManagerFactoryPropertyMap
	 */
	public static boolean initializeEntityManagerFactory(final String persistenceUnitName,
			final Class<? extends JstEntityManagerFactoryPropertyPO> entityManagerFactoryPropertyPO) {

		JstEntityManagerFactoryPropertyPO entityManagerFactoryPropertyInstancePO = null;

		try {
			entityManagerFactoryPropertyInstancePO = entityManagerFactoryPropertyPO.newInstance();
		} catch (@SuppressWarnings("unused") final Exception e) {
			throw new JustifyException(
					JstExceptionPO.withMessage("The [" + JstEntityManagerFactoryPropertyPO.class.getSimpleName()
							+ "] class should be extended with an instance containing property values."));
		}

		entityManagerFactoryPropertyInstancePO.setPersistenceUnitName(persistenceUnitName);

		final boolean isNewJdbcURL = JstEntityManagerFactoryCacheHelper
				.createEntityManagerFactory(entityManagerFactoryPropertyInstancePO);

		final Optional<EntityManager> optionalEntityManager = JstEntityManagerFactoryCacheHelper
				.createEntityManagerToBeClosed(persistenceUnitName);

		EntityManager entityManager = null;

		try {
			if (optionalEntityManager.isPresent()) {
				entityManager = optionalEntityManager.get();
				JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
			}
		} finally {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return isNewJdbcURL;
	}

	public static EntityManagerFactory retrieveCurrentEntityManagerFactory(final String persistenceUnitName) {
		return currentEntityManagerFactory.get(persistenceUnitName);
	}

	public static String retrievePersistenceKey(final String persistenceUnitName,
			final Class<? extends JstEntityManagerFactoryPropertyPO> entityManagerFactoryPropertyClassPO) {

		return persistenceUnitName + "/" + entityManagerFactoryPropertyClassPO.getName();
	}

	/**
	 * @return boolean
	 */
	private static boolean createEntityManagerFactory(
			final JstEntityManagerFactoryPropertyPO entityManagerFactoryPropertyPO) {

		final String key = retrievePersistenceKey(entityManagerFactoryPropertyPO.getPersistenceUnitName(),
				entityManagerFactoryPropertyPO.getClass());

		if (entityManagerFactoryMap.containsKey(key)) {
			currentEntityManagerFactory.replace(entityManagerFactoryPropertyPO.getPersistenceUnitName(),
					entityManagerFactoryMap.get(key));
			return true;
		}

		final Map<String, Object> entityManagerFactoryPropertyMap = entityManagerFactoryPropertyPO
				.getEntityManagerFactoryPropertyMap();

		final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
				entityManagerFactoryPropertyPO.getPersistenceUnitName(), entityManagerFactoryPropertyMap);

		final EntityManager entityManager = entityManagerFactory.createEntityManager();

		JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);

		entityManagerFactoryMap.put(key, entityManagerFactory);
		currentEntityManagerFactory.put(entityManagerFactoryPropertyPO.getPersistenceUnitName(), entityManagerFactory);

		// Determine if a new database is in play.
		return entityManagerFactoryPropertyMap.containsKey(PersistenceUnitProperties.JDBC_URL)
				|| entityManagerFactoryPropertyPO.getClass().isAssignableFrom(DefaultManagerFactoryPropertyPO.class);
	}
}