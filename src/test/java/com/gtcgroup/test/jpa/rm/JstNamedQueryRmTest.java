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
import com.gtcgroup.justify.jpa.po.JstQueryNamedPO;
import com.gtcgroup.justify.jpa.rm.JstQueryNamedRM;
import com.gtcgroup.justify.jpa.testing.extension.JstConfigureTestingJPA;
import com.gtcgroup.test.jpa.de.dependency.NoteDE;
import com.gtcgroup.test.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.test.jpa.po.dependency.ConfigureJustifyLoggingFinerPO;

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
@JstConfigureTestingJPA(configureTestJpaPO = ConfigureJustifyLoggingFinerPO.class)
@SuppressWarnings("static-method")
public class JstNamedQueryRmTest {

	public static final String QUERY_NAME_OOOOPPPSSS = "queryOooopppsss!";
	public static final String QUERY_NOTE_LIST = "queryNoteList";
	private static final String QUERY_NOTE_LIST_WITH_PARAMETER = "queryNoteListWithStringParameter";

	@Test
	public void testQueryList_badQueryName() {

		assertThrows(IllegalArgumentException.class, () -> {
			JstQueryNamedRM.queryList(JstQueryNamedPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
					.withQueryName(JstNamedQueryRmTest.QUERY_NAME_OOOOPPPSSS));
		});
	}

	@Test
	public void testQueryList_happyPath() {

		final Optional<List<NoteDE>> optionalNoteList = JstQueryNamedRM
				.queryList(JstQueryNamedPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
						.withQueryName(JstNamedQueryRmTest.QUERY_NOTE_LIST).withReadOnly());

		assertFalse(optionalNoteList.get().isEmpty());
	}

	@Test
	public void testQueryList_happyPath_withPaging() {

		final Optional<List<NoteDE>> optionalNoteList = JstQueryNamedRM.queryList(JstQueryNamedPO
				.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withQueryName(JstNamedQueryRmTest.QUERY_NOTE_LIST)
				.withQueryHint(QueryHints.READ_ONLY, HintValues.TRUE).withForceDatabaseTripWhenNoCacheCoordination()
				.withFirstResult(1).withMaxResults(1));

		assertEquals(1, optionalNoteList.get().size());
	}

	@Test
	public void testQueryList_withParameters_empty() {

		final Optional<List<NoteDE>> optionalNoteList = JstQueryNamedRM.queryList(JstQueryNamedPO
				.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
				.withQueryName(JstNamedQueryRmTest.QUERY_NOTE_LIST_WITH_PARAMETER).withParameter("text", "*fake*"));

		assertFalse(optionalNoteList.isPresent());
	}

	@Test
	public void testQuerySingle_badQueryName() {

		assertThrows(IllegalArgumentException.class, () -> {
			JstQueryNamedRM.querySingle(JstQueryNamedPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
					.withQueryName(JstNamedQueryRmTest.QUERY_NAME_OOOOPPPSSS)
					.withParameter("text", ConstantsTestJPA.NOTE_TEXT_ONE));
		});

	}

	@Test
	public void testQuerySingle_happyPath_withParameter() {

		final Optional<NoteDE> optionalNoteDE = JstQueryNamedRM
				.querySingle(JstQueryNamedPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
						.withQueryName(JstNamedQueryRmTest.QUERY_NOTE_LIST_WITH_PARAMETER)
						.withParameter("text", ConstantsTestJPA.NOTE_TEXT_ONE).withReadOnly());

		assertTrue(optionalNoteDE.isPresent());
	}

	@Test
	public void testWithExternalEntityManager() {

		Optional<EntityManager> entityManager = null;
		Optional<NoteDE> optionalNoteDE = null;
		Optional<List<NoteDE>> optionalList = null;
		Optional<List<NoteDE>> optionalEmpty = null;

		try {
			entityManager = JstEntityManagerCacheHelper
					.createEntityManagerToBeClosed(ConstantsTestJPA.JUSTIFY_PU);

			if (entityManager.isPresent()) {

				optionalNoteDE = JstQueryNamedRM.querySingle(JstQueryNamedPO
						.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withEntityManager(entityManager.get())
						.withQueryName(JstNamedQueryRmTest.QUERY_NOTE_LIST_WITH_PARAMETER)
						.withParameter("text", ConstantsTestJPA.NOTE_TEXT_ONE).withReadOnly());

				optionalList = JstQueryNamedRM.queryList(JstQueryNamedPO
						.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withEntityManager(entityManager.get())
						.withQueryName(JstNamedQueryRmTest.QUERY_NOTE_LIST).withReadOnly());

				optionalEmpty = JstQueryNamedRM.queryList(JstQueryNamedPO
						.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withEntityManager(entityManager.get())
						.withQueryName(JstNamedQueryRmTest.QUERY_NOTE_LIST_WITH_PARAMETER)
						.withParameter("text", "*fake*"));
			}
		} finally {
			JstEntityManagerCacheHelper.closeEntityManager(entityManager.get());
		}

		assertTrue(optionalNoteDE.isPresent());
		assertTrue(optionalList.isPresent());
		assertFalse(optionalEmpty.isPresent());
	}
}
