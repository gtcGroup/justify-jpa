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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.testing.extension.JstConfigureTestLogToConsole;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerCacheHelper;
import com.gtcgroup.justify.jpa.po.JstQueryStringPO;
import com.gtcgroup.justify.jpa.rm.JstQueryStringRM;
import com.gtcgroup.justify.jpa.testing.extension.JstConfigureTestingJPA;
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
@JstConfigureTestingJPA(configureTestJpaPO = ConfigureJustifyWithPopulatorPO.class)
@SuppressWarnings("static-method")
public class JstQueryStringRmTest {

	private static final String EXCEPTION_NOTHINGBEAN_SINGLE = "SELECT note FROM NothingBean note WHERE note.text='"
			+ ConstantsTestJPA.NOTE_TEXT_ONE + "'";

	private static final String SELECT_NOTE_SINGLE = "SELECT note FROM NoteDE note WHERE note.text='"
			+ ConstantsTestJPA.NOTE_TEXT_ONE + "'";

	private static final String SELECT_NOTE_EMPTY = "SELECT note FROM NoteDE note WHERE note.text='empty'";

	private static final String SELECT_NOTE_LIST = "SELECT note FROM NoteDE note";

	private static final String EXCEPTION_NOTHINGBEAN_LIST = "SELECT note FROM NothingBean note";

	private static final String SELECT_NOTE_SINGLE_FAKE = "SELECT note FROM NothingBean note WHERE note.text='"
			+ "fakeIdentity" + "'";

	@Test
	public void testQueryList_happyPath() {

		final Optional<List<NoteDE>> optionalNoteList = JstQueryStringRM
				.queryList(JstQueryStringPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
						.withQueryLanguageString(JstQueryStringRmTest.SELECT_NOTE_LIST).withReadOnly());

		final List<NoteDE> noteList = optionalNoteList.get();

		assertFalse(noteList.isEmpty());
	}

	@Test
	public void testQueryList_noEntity() {

		assertThrows(IllegalArgumentException.class, () -> {
			JstQueryStringRM.queryList(JstQueryStringPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
					.withQueryLanguageString(JstQueryStringRmTest.EXCEPTION_NOTHINGBEAN_LIST));
		});
	}

	@Test
	public void testQuerySingle_exceptionString() {

		assertThrows(IllegalArgumentException.class, () -> {
			JstQueryStringRM.querySingle(JstQueryStringPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
					.withQueryLanguageString(JstQueryStringRmTest.SELECT_NOTE_SINGLE_FAKE));
		});
	}

	@Test
	public void testQuerySingle_happyPath() {

		final Optional<NoteDE> optionalNoteDE = JstQueryStringRM.querySingle(JstQueryStringPO
				.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
				.withQueryLanguageString(JstQueryStringRmTest.SELECT_NOTE_SINGLE)
				.withQueryHint(QueryHints.READ_ONLY, HintValues.TRUE).withForceDatabaseTripWhenNoCacheCoordination());

		assertTrue(optionalNoteDE.isPresent());
	}

	@Test
	public void testQuerySingle_noEntity() {

		assertThrows(IllegalArgumentException.class, () -> {
			JstQueryStringRM.querySingle(JstQueryStringPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
					.withQueryLanguageString(JstQueryStringRmTest.EXCEPTION_NOTHINGBEAN_SINGLE));
		});
	}

	@Test
	public void testQuerySingle_noQueryString() {

		assertThrows(IllegalArgumentException.class, () -> {
			JstQueryStringRM.querySingle(JstQueryStringPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU));
		});

	}

	@Test
	public void testWithExternalEntityManager() {

		Optional<EntityManager> entityManager = null;
		Optional<NoteDE> optionalNoteDE = null;
		Optional<List<NoteDE>> optionalList = null;
		Optional<NoteDE> optionalEmpty = null;

		try {
			entityManager = JstEntityManagerCacheHelper
					.createEntityManagerToBeClosed(ConstantsTestJPA.JUSTIFY_PU);

			if (entityManager.isPresent()) {

				optionalNoteDE = JstQueryStringRM
						.querySingle(JstQueryStringPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
								.withQueryLanguageString(JstQueryStringRmTest.SELECT_NOTE_SINGLE)
								.withEntityManager(entityManager.get()));

				optionalList = JstQueryStringRM
						.queryList(JstQueryStringPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
								.withQueryLanguageString(JstQueryStringRmTest.SELECT_NOTE_LIST).withFirstResult(1)
								.withMaxResults(1).withEntityManager(entityManager.get()));

				optionalEmpty = JstQueryStringRM
						.querySingle(JstQueryStringPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
								.withQueryLanguageString(JstQueryStringRmTest.SELECT_NOTE_EMPTY)
								.withEntityManager(entityManager.get()));
			}
		} finally {
			JstEntityManagerCacheHelper.closeEntityManager(entityManager.get());
		}

		assertTrue(optionalNoteDE.isPresent());
		assertEquals(1, optionalList.get().size());
		assertFalse(optionalEmpty.isPresent());
	}
}
