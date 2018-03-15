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
package com.gtcgroup.justify.jpa.rm;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.test.extension.JstConfigureTestLogToConsole;
import com.gtcgroup.justify.jpa.de.dependency.EntityNotPopulatedDE;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.JstFindSinglePO;
import com.gtcgroup.justify.jpa.po.JstQueryAllJPO;
import com.gtcgroup.justify.jpa.populator.dependency.NoteAdditionalDataPopulator;
import com.gtcgroup.justify.jpa.populator.dependency.NoteDataPopulator;
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
@JstConfigureTestJPA(persistenceUnitName = ConstantsTestJPA.JUSTIFY_PU, dataPopulators = { NoteDataPopulator.class,
		NoteAdditionalDataPopulator.class })
@SuppressWarnings("static-method")
public class JstFindJpaRmTest {

	private static final String FAKE_IDENTITY = "fakeIdentity";

	public static Optional<NoteDE> findReadOnlyNoteDE(final Class<?> clazz, final String entityIdentity) {

		final JstFindSinglePO findJpaPO = JstFindSinglePO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
				.withEntityClass(clazz).withEntityIdentity(entityIdentity).withReadOnly();

		return JstQueryFindRM.findSingle(findJpaPO);
	}

	private static Optional<NoteDE> findNoteDE(final Class<?> clazz, final String entityIdentity) {

		final JstFindSinglePO findJpaPO = JstFindSinglePO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
				.withEntityClass(clazz).withEntityIdentity(entityIdentity);

		return JstQueryFindRM.findSingle(findJpaPO);
	}

	@Test
	public void testFind_fakeIdentity() {

		assertFalse(findNoteDE(NoteDE.class, JstFindJpaRmTest.FAKE_IDENTITY).isPresent());
	}

	@Test
	public void testFind_happyPath() {

		assertAll(() -> {
			assertTrue(findNoteDE(NoteDE.class, ConstantsTestJPA.NOTE_UUID_TWO).isPresent());
			assertTrue(findReadOnlyNoteDE(NoteDE.class, ConstantsTestJPA.NOTE_UUID_TWO).isPresent());
		});
	}

	@Test
	public void testWithExternalEntityManager() {

		Optional<EntityManager> entityManager = null;
		Optional<NoteDE> optionalNoteDE = null;
		Optional<List<NoteDE>> optionalList = null;
		Optional<List<NoteDE>> optionalEmpty = null;

		try {
			entityManager = JstEntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(ConstantsTestJPA.JUSTIFY_PU, null, false);

			if (entityManager.isPresent()) {

				optionalNoteDE = JstQueryFindRM.findSingle(JstFindSinglePO
						.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withEntityClass(NoteDE.class)
						.withEntityIdentity(ConstantsTestJPA.NOTE_UUID_TWO).withEntityManager(entityManager.get()));

				optionalList = JstQueryFindRM
						.queryAll(JstQueryAllJPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
								.withEntityClass(NoteDE.class).withEntityManager(entityManager.get()).withReadOnly());

				optionalEmpty = JstQueryFindRM
						.queryAll(JstQueryAllJPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
								.withEntityClass(EntityNotPopulatedDE.class).withEntityManager(entityManager.get())
								.withReadOnly());
			}
		} finally {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager.get());
		}

		assertTrue(optionalNoteDE.isPresent());
		assertTrue(optionalList.isPresent());
		assertFalse(optionalEmpty.isPresent());
	}
}
