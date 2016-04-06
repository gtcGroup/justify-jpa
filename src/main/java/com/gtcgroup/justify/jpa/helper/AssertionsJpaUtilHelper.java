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

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Assert;

import com.gtcgroup.justify.jpa.rm.QueryRM;

/**
 * This Util Helper class provides support for assertion processing.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */

public class AssertionsJpaUtilHelper {

	/**
	 * This method persists, and deletes an entity.
	 *
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entities
	 * @return boolean
	 */
	public static <ENTITY extends Object> List<Object> assertCreateAndDeleteEntities(final String persistenceUnitName,
			final ENTITY... entities) {

		List<Object> createdList = null;

		EntityManager entityManager = null;

		try {

			entityManager = EntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

			createdList = TransactionUtilHelper.transactCreatesOrUpdatesArray(entityManager, entities);

			TransactionUtilHelper.transactDelete(entityManager, createdList);

		} catch (@SuppressWarnings("unused") final Exception e) {

			Assert.fail("The domain entities could not be handled.");

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return createdList;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entityIdentity
	 * @return boolean
	 */
	public static <ENTITY extends Object> boolean assertExists(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object entityIdentity) {

		if (false == assertExistsPrivate(persistenceUnitName, entityClass, entityIdentity)) {

			Assert.fail("The class [" + entityClass.getSimpleName() + "] instance is not available from the database ["
					+ persistenceUnitName + "].");
		}
		return true;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entityIdentities
	 * @return boolean
	 */

	public static <ENTITY extends Object> boolean assertExistsArray(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		final List<Object> entityIdentityList = Arrays.asList(entityIdentities);

		return assertExistsList(persistenceUnitName, entityClass, entityIdentityList);
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entityIdentityList
	 * @return boolean
	 */
	public static <ENTITY extends Object> boolean assertExistsList(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final List<Object> entityIdentityList) {

		if (false == assertExistsListPrivate(persistenceUnitName, entityClass, entityIdentityList)) {

			Assert.fail(
					"The class [" + entityClass.getSimpleName() + "] instances are not available from the database.");

		}

		return true;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entityIdentityList
	 * @return boolean
	 */
	private static <ENTITY extends Object> boolean assertExistsListPrivate(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final List<Object> entityIdentityList) {

		EntityManager entityManager = null;

		try {

			entityManager = EntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

			final QueryRM queryRM = new QueryRM().setEntityManager(entityManager);

			for (final Object entityIdentity : entityIdentityList) {

				if (!queryRM.exists(entityClass, entityIdentity)) {

					return false;
				}

			}

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return true;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entityIdentity
	 * @return boolean
	 */
	private static <ENTITY extends Object> boolean assertExistsPrivate(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object entityIdentity) {

		EntityManager entityManager = null;

		try {
			entityManager = EntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

			final QueryRM queryRM = new QueryRM().setEntityManager(entityManager);

			if (false == queryRM.exists(entityClass, entityIdentity)) {

				return false;
			}

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return true;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entityIdentity
	 * @return boolean
	 */
	public static <ENTITY extends Object> boolean assertNotExists(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object entityIdentity) {

		if (true == assertExistsPrivate(persistenceUnitName, entityClass, entityIdentity)) {
			Assert.fail("The class [" + entityClass.getSimpleName()
					+ "] instance is incorrectly available from the database [" + persistenceUnitName + "].");
		}
		return true;

	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entityIdentities
	 * @return boolean
	 */
	public static <ENTITY extends Object> boolean assertNotExistsArray(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object... entityIdentities) {

		final List<Object> entityIdentityList = Arrays.asList(entityIdentities);

		return assertNotExistsList(persistenceUnitName, entityClass, entityIdentityList);
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityClass
	 * @param entityIdentityList
	 * @return boolean
	 */
	public static <ENTITY extends Object> boolean assertNotExistsList(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final List<Object> entityIdentityList) {

		if (true == assertExistsListPrivate(persistenceUnitName, entityClass, entityIdentityList)) {

			Assert.fail("The class [" + entityClass.getSimpleName() + "] instance is not available from the database ["
					+ persistenceUnitName + "].");
		}
		return true;
	}

	/**
	 * Constructor
	 */
	private AssertionsJpaUtilHelper() {

		super();
		return;
	}
}
