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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.Assert;

import com.gtcgroup.justify.core.exception.internal.TestingRuntimeException;
import com.gtcgroup.justify.jpa.po.JstAssertJpaPO;
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
	 * TODO: This method is incomplete.
	 *
	 * @param persistenceUnitName
	 * @param cascadeTypesPO
	 */
	public static void assertCascadeTypes(final String persistenceUnitName, final JstAssertJpaPO cascadeTypesPO) {

		assertPersistedEntities(persistenceUnitName, cascadeTypesPO.getDomainEntity());

		verifyPersist(persistenceUnitName, cascadeTypesPO);

		final List<Object> deleteList = new ArrayList<Object>();
		deleteList.add(cascadeTypesPO.getDomainEntity());

		assertDeleteEntities(persistenceUnitName, deleteList);

		final List<Object> createdList = assertUpdateEntities(persistenceUnitName, cascadeTypesPO.getDomainEntity());

		verifyMerge(persistenceUnitName, cascadeTypesPO);

		verifyRefresh(persistenceUnitName, cascadeTypesPO);

		assertDeleteEntities(persistenceUnitName, createdList);

		verifyRemove(persistenceUnitName, cascadeTypesPO);

		return;

	}

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

			createdList = assertUpdateEntities(persistenceUnitName, entities);

			assertDeleteEntities(persistenceUnitName, createdList);

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return createdList;
	}

	/**
	 * @param persistenceUnitName
	 * @param entities
	 */
	private static void assertDeleteEntities(final String persistenceUnitName, final List<Object> createList) {

		EntityManager entityManager = null;

		try {

			entityManager = EntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

			TransactionUtilHelper.transactDelete(entityManager, createList);

		} catch (@SuppressWarnings("unused") final Exception e) {

			Assert.fail("The domain entities could not be deleted by the assert method.");

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return;
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

		if (false == assertExistsInternal(persistenceUnitName, entityClass, entityIdentity)) {

			Assert.fail("The class [" + entityClass.getSimpleName() + "] instance is not available from the database ["
					+ persistenceUnitName + "].");
		}
		return true;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entityInstance
	 * @param entityIdentity
	 * @return boolean
	 */
	public static <ENTITY extends Object> boolean assertExists(final String persistenceUnitName,
			final Object entityInstance, final Object entityIdentity) {

		return assertExists(persistenceUnitName, entityInstance.getClass(), entityIdentity);
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
	 * @param entityIdentity
	 * @return boolean
	 */
	private static <ENTITY extends Object> boolean assertExistsInternal(final String persistenceUnitName,
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
	 * @param entityIdentityList
	 * @return boolean
	 */
	public static <ENTITY extends Object> boolean assertExistsList(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final List<Object> entityIdentityList) {

		if (false == assertExistsListInternal(persistenceUnitName, entityClass, entityIdentityList)) {

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
	private static <ENTITY extends Object> boolean assertExistsListInternal(final String persistenceUnitName,
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
	public static <ENTITY extends Object> boolean assertNotExists(final String persistenceUnitName,
			final Class<ENTITY> entityClass, final Object entityIdentity) {

		if (true == assertExistsInternal(persistenceUnitName, entityClass, entityIdentity)) {
			Assert.fail("The class [" + entityClass.getSimpleName()
					+ "] instance is incorrectly available from database [" + persistenceUnitName + "].");
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

		if (true == assertExistsListInternal(persistenceUnitName, entityClass, entityIdentityList)) {

			Assert.fail("The class [" + entityClass.getSimpleName() + "] instance is not available from database ["
					+ persistenceUnitName + "].");
		}
		return true;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entities
	 * @return boolean
	 */
	private static <ENTITY extends Object> boolean assertPersistedEntities(final String persistenceUnitName,
			final ENTITY... entities) {

		EntityManager entityManager = null;

		try {

			entityManager = EntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

			TransactionUtilHelper.transactPersistArray(entityManager, entities);

		} catch (@SuppressWarnings("unused") final Exception e) {

			Assert.fail("The domain entities could not be created by the assert method.");

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return true;
	}

	/**
	 * @param <ENTITY>
	 * @param persistenceUnitName
	 * @param entities
	 * @return boolean
	 */
	public static <ENTITY extends Object> List<Object> assertUpdateEntities(final String persistenceUnitName,
			final ENTITY... entities) {

		List<Object> createdList = null;

		EntityManager entityManager = null;

		try {

			entityManager = EntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

			createdList = TransactionUtilHelper.transactCreateOrUpdateArray(entityManager, entities);

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}
		return createdList;
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 */
	private static void verifyMerge(final String persistenceUnitName, final JstAssertJpaPO cascadeTypesPO) {

		for (final Map.Entry<String, Object> entry : cascadeTypesPO.getCascadeMergeMap().entrySet()) {

			try {

				assertExists(persistenceUnitName, Class.forName(entry.getKey()), entry.getValue());

			} catch (final ClassNotFoundException e) {

				throw new TestingRuntimeException(e);
			}
		}

		for (final Map.Entry<String, Object> entry : cascadeTypesPO.getCascadeMergeMapFalse().entrySet()) {

			try {

				assertNotExists(persistenceUnitName, Class.forName(entry.getKey()), entry.getValue());

			} catch (final ClassNotFoundException e) {

				throw new TestingRuntimeException(e);
			}
		}
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 */
	private static void verifyPersist(final String persistenceUnitName, final JstAssertJpaPO cascadeTypesPO) {

		for (final Map.Entry<String, Object> entry : cascadeTypesPO.getCascadePersistMap().entrySet()) {

			try {

				assertExists(persistenceUnitName, Class.forName(entry.getKey()), entry.getValue());

			} catch (final ClassNotFoundException e) {

				throw new TestingRuntimeException(e);
			}
		}

		for (final Map.Entry<String, Object> entry : cascadeTypesPO.getCascadePersistMapFalse().entrySet()) {

			try {

				assertNotExists(persistenceUnitName, Class.forName(entry.getKey()), entry.getValue());

			} catch (final ClassNotFoundException e) {

				throw new TestingRuntimeException(e);
			}
		}
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 */
	private static void verifyRefresh(final String persistenceUnitName, final JstAssertJpaPO cascadeTypesPO) {

		EntityManager entityManager = null;

		try {
			entityManager = EntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(persistenceUnitName);

			EntityManagerMethodsUtilHelper.evictAllEntitiesFromSharedCache(entityManager);

		} finally {

			EntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
		}

		for (final Map.Entry<String, Object> entry : cascadeTypesPO.getCascadeRefreshMap().entrySet()) {

			try {

				assertExists(persistenceUnitName, Class.forName(entry.getKey()), entry.getValue());

			} catch (final ClassNotFoundException e) {

				throw new TestingRuntimeException(e);
			}
		}

		for (final Map.Entry<String, Object> entry : cascadeTypesPO.getCascadeRefreshMapFalse().entrySet()) {

			try {

				assertNotExists(persistenceUnitName, Class.forName(entry.getKey()), entry.getValue());

			} catch (final ClassNotFoundException e) {

				throw new TestingRuntimeException(e);
			}
		}
	}

	/**
	 * @param persistenceUnitName
	 * @param cascadeRemoveMap
	 */
	private static void verifyRemove(final String persistenceUnitName, final JstAssertJpaPO cascadeTypesPO) {

		for (final Map.Entry<String, Object> entry : cascadeTypesPO.getCascadeRemoveMap().entrySet()) {

			final String className = entry.getKey();
			final Object entityIdentity = entry.getValue();

			try {

				assertNotExists(persistenceUnitName, Class.forName(className), entityIdentity);

			} catch (final ClassNotFoundException e) {

				throw new TestingRuntimeException(e);
			}
		}

		for (final Map.Entry<String, Object> entry : cascadeTypesPO.getCascadeRemoveMapFalse().entrySet()) {

			final String className = entry.getKey();
			final Object entityIdentity = entry.getValue();

			try {

				assertExists(persistenceUnitName, Class.forName(className), entityIdentity);

			} catch (final ClassNotFoundException e) {

				throw new TestingRuntimeException(e);
			}
		}
	}

	/**
	 * Constructor
	 */
	private AssertionsJpaUtilHelper() {

		super();
		return;
	}
}
