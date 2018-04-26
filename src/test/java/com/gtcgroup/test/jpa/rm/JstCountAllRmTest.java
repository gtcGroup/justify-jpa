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
package com.gtcgroup.test.jpa.rm;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.testing.extension.JstConfigureTestLogToConsole;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerCacheHelper;
import com.gtcgroup.justify.jpa.po.JstQueryCountPO;
import com.gtcgroup.justify.jpa.rm.JstQueryCountRM;
import com.gtcgroup.justify.jpa.testing.extension.JstConfigureTestingJPA;
import com.gtcgroup.test.jpa.de.dependency.EntityNotPopulatedDE;
import com.gtcgroup.test.jpa.de.dependency.NotAnEntityDE;
import com.gtcgroup.test.jpa.de.dependency.NoteDE;
import com.gtcgroup.test.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.test.jpa.po.dependency.ConfigureJustifyWithPopulatorPO;

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
@JstConfigureTestingJPA(configureTestingJpaPO = ConfigureJustifyWithPopulatorPO.class)
@SuppressWarnings("static-method")
public class JstCountAllRmTest {

	@Test
	public void testCount_happyPath() {

		assertAll(
				() -> assertTrue(
						JstQueryCountRM
								.count(JstQueryCountPO
										.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withResultClass(
												NoteDE.class))
								.isPresent()),
				() -> assertEquals(0,
						JstQueryCountRM.count(JstQueryCountPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
								.withResultClass(EntityNotPopulatedDE.class)).get().longValue()));
	}

	@Test
	public void testCount_missingResultClass() {

		assertThrows(IllegalArgumentException.class, () -> {
			JstQueryCountRM.count(JstQueryCountPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)).isPresent();
		});
	}

	@Test
	public void testCount_notAnEntity() {

		assertThrows(IllegalArgumentException.class, () -> {
			JstQueryCountRM.count(JstQueryCountPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
					.withResultClass(NotAnEntityDE.class)).isPresent();
		});
	}

	@Test
	public void testWithExternalEntityManager() {

		final Optional<EntityManager> optionalEntityManager = JstEntityManagerCacheHelper
				.createEntityManagerToBeClosed(ConstantsTestJPA.JUSTIFY_PU);
		if (optionalEntityManager.isPresent()) {
			try {

				assertAll(
						() -> assertTrue(JstQueryCountRM.count(
								JstQueryCountPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withResultClass(
										NoteDE.class).withEntityManager(
												optionalEntityManager.get()))
								.isPresent()),
						() -> assertEquals(0,
								JstQueryCountRM
										.count(JstQueryCountPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
												.withResultClass(EntityNotPopulatedDE.class)
												.withEntityManager(optionalEntityManager.get()))
										.get().longValue()));
			} finally {
				JstEntityManagerCacheHelper.closeEntityManager(optionalEntityManager.get());
			}
		}
	}
}
