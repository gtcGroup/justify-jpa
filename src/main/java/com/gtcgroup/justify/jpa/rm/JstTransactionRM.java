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

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import com.gtcgroup.justify.core.base.JstBaseTestingRM;
import com.gtcgroup.justify.jpa.helper.internal.EntityManagerUtilHelper;

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

	private final EntityManager entityManager;

	/**
	 * Constructor
	 * 
	 * @param entityManager
	 */
	public JstTransactionRM(final EntityManager entityManager) {
		super();
		this.entityManager = entityManager;
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly processed.
	 *
	 * @param <ENTITY>
	 * @param entity
	 */
	public <ENTITY> void transactCreateOrUpdateEntity(final ENTITY entity) {

		EntityManagerUtilHelper.createOrUpdateEntity(this.entityManager, entity);
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entities
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY> void transactCreateOrUpdateFromArray(final Object... entities) {


		EntityManagerUtilHelper.createOrUpdateEntities(this.entityManager,
				(List<ENTITY>) Arrays.asList(entities));
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityList
	 */
	public <ENTITY> void transactCreateOrUpdateFromList(final List<ENTITY> entityList) {

		EntityManagerUtilHelper.createOrUpdateEntities(this.entityManager, entityList);
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entityList
	 */
	public <ENTITY> void transactDeleteEntities(final List<ENTITY> entityList) {

		EntityManagerUtilHelper.removeEntities(this.entityManager, entityList);
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entities
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY> void transactDeleteEntities(final Object... entities) {

		EntityManagerUtilHelper.removeEntities(this.entityManager, (List<ENTITY>) Arrays.asList(entities));
	}

	/**
	 * This method is typically used for committing. If any of the related
	 * children in the object graph are not marked for cascading then they need
	 * to be explicitly included.
	 *
	 * @param <ENTITY>
	 * @param entity
	 */
	public <ENTITY> void transactDeleteEntity(final ENTITY entity) {

		EntityManagerUtilHelper.removeEntity(this.entityManager, entity);
	}
}
