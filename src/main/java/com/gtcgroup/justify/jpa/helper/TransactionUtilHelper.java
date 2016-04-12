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

import javax.persistence.EntityManager;

/**
 * This Util Helper class provides persistence transaction support.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public class TransactionUtilHelper {

	/**
	 * This method typically commits one or more parent entities. If any of the
	 * related children in the object graph are not marked for cascading then
	 * they need to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityManager
	 * @param entityList
	 * @return {@link List}
	 */
	public static <ENTITY> List<ENTITY> transactCreateOrUpdate(final EntityManager entityManager,
			final List<ENTITY> entityList) {

		entityManager.getTransaction().begin();

		final List<ENTITY> resultList = new ArrayList<ENTITY>();

		for (final Object entity : entityList) {

			@SuppressWarnings("unchecked")
			final ENTITY tempEntity = (ENTITY) entityManager.merge(entity);
			resultList.add(tempEntity);
		}

		entityManager.getTransaction().commit();

		return resultList;
	}

	/**
	 * This method typically commits one or more parent entities. If any of the
	 * related children in the object graph are not marked for cascading then
	 * they need to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityManager
	 * @param entities
	 * @return {@link List}
	 */
	public static <ENTITY> List<ENTITY> transactCreateOrUpdateArray(final EntityManager entityManager,
			final Object... entities) {

		@SuppressWarnings("unchecked")
		final List<ENTITY> entityList = (List<ENTITY>) Arrays.asList(entities);

		return transactCreateOrUpdate(entityManager, entityList);
	}

	/**
	 * This method typically deletes one or more parent entities. If any of the
	 * related children in the object graph are not marked for cascading then
	 * they need to be explicitly included.
	 *
	 * @param entityManager
	 * @param entityList
	 */
	public static void transactDelete(final EntityManager entityManager, final List<Object> entityList) {

		entityManager.getTransaction().begin();

		for (Object entity : entityList) {

			entity = entityManager.merge(entity);
			entityManager.remove(entity);
		}
		entityManager.getTransaction().commit();
	}

	/**
	 * This method merges the state of the given entity into the current
	 * persistence context.
	 *
	 * @param <ENTITY>
	 * @param entityManager
	 * @param entityList
	 * @return {@link List}
	 */
	public static <ENTITY> List<ENTITY> transactMerge(final EntityManager entityManager,
			final List<ENTITY> entityList) {

		return transactCreateOrUpdate(entityManager, entityList);
	}

	/**
	 * This method typically commits one or more parent entities. If any of the
	 * related children in the object graph are not marked for cascading then
	 * they need to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityManager
	 * @param entityList
	 * @return {@link List}
	 */
	public static <ENTITY> List<ENTITY> transactPersist(final EntityManager entityManager,
			final List<ENTITY> entityList) {

		entityManager.getTransaction().begin();

		final List<ENTITY> resultList = new ArrayList<ENTITY>();

		for (final Object entity : entityList) {

			entityManager.persist(entity);

		}

		entityManager.getTransaction().commit();

		return resultList;
	}

	/**
	 * This method typically commits one or more parent entities. If any of the
	 * related children in the object graph are not marked for cascading then
	 * they need to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityManager
	 * @param entities
	 * @return {@link List}
	 */
	public static <ENTITY> List<ENTITY> transactPersistArray(final EntityManager entityManager,
			final Object... entities) {

		@SuppressWarnings("unchecked")
		final List<ENTITY> entityList = (List<ENTITY>) Arrays.asList(entities);

		return transactPersist(entityManager, entityList);
	}

	/**
	 * Constructor
	 */
	private TransactionUtilHelper() {
		super();
		return;
	}
}
