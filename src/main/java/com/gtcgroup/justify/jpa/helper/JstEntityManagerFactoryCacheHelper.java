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

import com.gtcgroup.justify.core.po.JstExceptionPO;
import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.jpa.test.extension.JstConfigureTestJpaPO;

/**
 * This Cache Helper class manages {@link EntityManagerFactory}(s).
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
	 * @return {@link Optional}
	 */
	public static Optional<JstConfigureTestJpaPO> initializeEntityManagerFactory(
			final Class<? extends JstConfigureTestJpaPO> configureTestClassPO) {

		JstConfigureTestJpaPO configureTestInstancePO = null;

		try {
			configureTestInstancePO = configureTestClassPO.newInstance();
		} catch (@SuppressWarnings("unused") final Exception e) {
			throw new JustifyException(JstExceptionPO.withMessage("The [" + JstConfigureTestJpaPO.class.getSimpleName()
					+ "] class should be extended with an instance containing property values."));
		}

		final boolean isFirstUseOfFactory = JstEntityManagerFactoryCacheHelper
				.createEntityManagerFactory(configureTestInstancePO);

		final Optional<EntityManager> optionalEntityManager = JstEntityManagerFactoryCacheHelper
				.createEntityManagerToBeClosed(configureTestInstancePO.getPersistenceUnitName());

		EntityManager entityManager = null;

		try {
			if (optionalEntityManager.isPresent()) {
				entityManager = optionalEntityManager.get();
			}
		} finally {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}

		if (isFirstUseOfFactory) {
			return Optional.of(configureTestInstancePO);
		}
		return Optional.empty();
	}

	/**
	 * @return {@link EntityManagerFactory}
	 */
	public static EntityManagerFactory retrieveCurrentEntityManagerFactory(final String persistenceUnitName) {
		return currentEntityManagerFactory.get(persistenceUnitName);
	}

	/**
	 * @return {@link String}
	 */
	public static String retrievePersistenceKey(final String persistenceUnitName,
			final Class<? extends JstConfigureTestJpaPO> entityManagerFactoryPropertyClassPO) {

		return persistenceUnitName + "/" + entityManagerFactoryPropertyClassPO.getName();
	}

	/**
	 * @return boolean
	 */
	private static boolean createEntityManagerFactory(final JstConfigureTestJpaPO entityManagerFactoryPropertyPO) {

		final String key = retrievePersistenceKey(entityManagerFactoryPropertyPO.getPersistenceUnitName(),
				entityManagerFactoryPropertyPO.getClass());

		if (entityManagerFactoryMap.containsKey(key)) {
			currentEntityManagerFactory.replace(entityManagerFactoryPropertyPO.getPersistenceUnitName(),
					entityManagerFactoryMap.get(key));

			// isFirstUseOfFactory
			return false;
		}

		final Map<String, Object> entityManagerFactoryPropertyMap = entityManagerFactoryPropertyPO
				.getEntityManagerFactoryPropertyMap();

		final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
				entityManagerFactoryPropertyPO.getPersistenceUnitName(), entityManagerFactoryPropertyMap);

		final EntityManager entityManager = entityManagerFactory.createEntityManager();

		JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);

		entityManagerFactoryMap.put(key, entityManagerFactory);
		currentEntityManagerFactory.put(entityManagerFactoryPropertyPO.getPersistenceUnitName(), entityManagerFactory);

		// isFirstUseOfFactory
		return true;
	}
}