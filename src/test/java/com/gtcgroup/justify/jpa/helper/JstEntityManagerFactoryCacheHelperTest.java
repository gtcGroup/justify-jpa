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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.core.test.extension.JstConfigureTestLogToConsole;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.dependency.ConfigureJustifyWithPopulatorPO;
import com.gtcgroup.justify.jpa.test.extension.JstConfigureTestJPA;

/**
 * Test Class
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
@JstConfigureTestLogToConsole
@JstConfigureTestJPA(configureTestJpaPO = ConfigureJustifyWithPopulatorPO.class)
@SuppressWarnings("static-method")
public class JstEntityManagerFactoryCacheHelperTest {

	@Test
	public void testCreateEntityManagerToBeClosed() {

		JstEntityManagerFactoryCacheHelper.createEntityManagerToBeClosed("fakePU");

	}

	@Test
	public void testCreateEntityManagerToBeClosed_happyPath() {

		final Optional<EntityManager> entityManager = JstEntityManagerFactoryCacheHelper
				.createEntityManagerToBeClosed(ConstantsTestJPA.JUSTIFY_PU);

		if (entityManager.isPresent()) {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager.get());
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager.get());
		}
	}

	@Test
	public void testCreateEntityManagerToBeClosed_null() {

		assertThrows(JustifyException.class, () -> {
			JstEntityManagerFactoryCacheHelper.createEntityManagerToBeClosed(null);
		});
	}
}