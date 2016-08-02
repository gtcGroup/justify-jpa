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
package com.gtcgroup.justify.jpa.rm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import com.gtcgroup.justify.core.base.JstBaseTestingRM;

/**
 * This Resource Manager provides convenience methods for transactions.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2016 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public class JstTransactionRM extends JstBaseTestingRM {

	private EntityManager entityManager;

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entity
	 * @return {@link List}
	 */
	public <ENTITY> ENTITY transactCreateOrUpdate(final ENTITY entity) {

		@SuppressWarnings("unchecked")
		final List<ENTITY> entityList = Arrays.asList(entity);

		return transactCreateOrUpdateFromList(entityList).get(0);
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entities
	 * @return {@link List}
	 */
	public <ENTITY> List<ENTITY> transactCreateOrUpdateFromArray(final Object... entities) {

		@SuppressWarnings("unchecked")
		final List<ENTITY> entityList = (List<ENTITY>) Arrays.asList(entities);

		return transactCreateOrUpdateFromList(entityList);
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityList
	 * @return {@link List}
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY> List<ENTITY> transactCreateOrUpdateFromList(final List<ENTITY> entityList) {

		final List<ENTITY> mergedList = new ArrayList<ENTITY>();
		this.entityManager.getTransaction().begin();

		for (final Object entity : entityList) {

			mergedList.add((ENTITY) this.entityManager.merge(entity));
		}
		this.entityManager.getTransaction().commit();
		return mergedList;
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param domainEntity
	 */
	public <ENTITY> void transactDelete(final ENTITY domainEntity) {

		this.entityManager.getTransaction().begin();

		final ENTITY mergedEntity = this.entityManager.merge(domainEntity);
		this.entityManager.remove(mergedEntity);

		this.entityManager.getTransaction().commit();
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityList
	 */
	public <ENTITY> void transactDeleteFromList(final List<ENTITY> entityList) {

		this.entityManager.getTransaction().begin();

		for (Object entity : entityList) {

			entity = this.entityManager.merge(entity);
			this.entityManager.remove(entity);
		}
		this.entityManager.getTransaction().commit();
	}

	/**
	 * @param <RM>
	 * @param entityManager
	 * @return {@link JstQueryRM}
	 */
	@SuppressWarnings("unchecked")
	public <RM extends JstTransactionRM> RM withEntityManager(final EntityManager entityManager) {

		this.entityManager = entityManager;
		return (RM) this;
	}
}
