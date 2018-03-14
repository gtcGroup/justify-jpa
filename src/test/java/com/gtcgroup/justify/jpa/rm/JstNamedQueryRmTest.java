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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.core.test.extension.JstConfigureTestLogToConsole;
import com.gtcgroup.justify.jpa.assertions.AssertionsJPA;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.extension.JstConfigureTestJPA;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.JstQueryNamedJpaPO;
import com.gtcgroup.justify.jpa.populator.dependency.NoteDataPopulator;

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
@JstConfigureTestJPA(persistenceUnitName = ConstantsTestJPA.JUSTIFY_PU, dataPopulators = NoteDataPopulator.class)
@SuppressWarnings("all")
public class JstNamedQueryRmTest {

	private static final String QUERY_NAME_OOOOPPPSSS = "queryOooopppsss!";
	private static final String QUERY_NOTE_LIST = "queryNoteList";
	private static final String QUERY_NOTE_LIST_WITH_STRING_PARAMETER = "queryNoteListWithStringParameter";

	@Test
	public void testNamedQuerySingle_withParameter() {

		final Optional<NoteDE> optionalNoteDE = JstQueryNamedJpaRM
				.querySingle(JstQueryNamedJpaPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
						.withQueryName(JstNamedQueryRmTest.QUERY_NOTE_LIST_WITH_STRING_PARAMETER)
						.withParameter("text", ConstantsTestJPA.NOTE_TEXT_ONE).withReadOnly());

		Assertions.assertTrue(optionalNoteDE.isPresent());
	}

	@Test
	public void testNamedQueryList() {

		final List<NoteDE> noteList = JstQueryNamedJpaRM
				.queryReadOnlyList(JstQueryNamedJpaPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withQueryName(JstNamedQueryRmTest.QUERY_NOTE_LIST);

		AssertionsJPA.assertExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, noteList, ConstantsTestJPA.NOTE_UUID_ONE);
	}

	@Test
	public void testNamedQueryList_badName() {

		Assertions.assertFalse(createNamedQueryListPO(true, JstNamedQueryRmTest.QUERY_NAME_OOOOPPPSSS).isPresent());
	}

	@Test
	public void testNamedQueryList_badQueryName() {

		Assertions.assertFalse(((NoteDE) JstQueryNamedJpaRM
				.querySingle(JstQueryNamedJpaPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
						.withQueryName(JstNamedQueryRmTest.QUERY_NAME_OOOOPPPSSS)
						.withParameter("text", ConstantsTestJPA.NOTE_TEXT_ONE))
				.get()).isPresent());
	}

	@Test
	public void testNamedQueryList_empty() {

		final List<NoteDE> noteList = new ArrayList<>();

		AssertionsJPA.assertNotExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, noteList, ConstantsTestJPA.NOTE_UUID_ONE);
	}

	@Test
	public void testNamedQueryList_withParameter() {

		final List<NoteDE> noteList = createNamedQueryListMapPO(true,
				JstNamedQueryRmTest.QUERY_NOTE_LIST_WITH_STRING_PARAMETER);

		AssertionsJPA.assertExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, noteList, ConstantsTestJPA.NOTE_UUID_ONE);
	}

	@Test(expected = JustifyException.class)
	public void testNamedQueryList_withParameter_noSuppress() {

		final Map<String, Object> stringParameterMap = new HashMap<>();
		stringParameterMap.put("text", "*fake*");

		JstQueryNamedJpaRM.queryReadOnlyList(
				JstQueryNamedJpaPO.withPersistenceUnitName(false).withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
						.withQueryName(JstNamedQueryRmTest.QUERY_NOTE_LIST_WITH_STRING_PARAMETER)
						.withParameterMap(stringParameterMap));
	}

	@Test
	public void testNamedQueryList_withParameter_withSuppress() {

		final Map<String, Object> stringParameterMap = new HashMap<>();
		stringParameterMap.put("text", "*fake*");

		final List<NoteDE> noteList = JstQueryNamedJpaRM.queryReadOnlyList(
				JstQueryNamedJpaPO.withPersistenceUnitName(true).withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
						.withQueryName(JstNamedQueryRmTest.QUERY_NOTE_LIST_WITH_STRING_PARAMETER)
						.withParameterMap(stringParameterMap));

		Assertions.assertThat(noteList).isEmpty();
	}

	@Test
	public void testNamedQueryList_withParameterMap() {

		final List<NoteDE> noteList = createNamedQueryListMapPO(true,
				JstNamedQueryRmTest.QUERY_NOTE_LIST_WITH_STRING_PARAMETER);

		AssertionsJPA.assertExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, noteList, ConstantsTestJPA.NOTE_UUID_TWO);
	}

	@Test(expected = JustifyException.class)
	public void testNamedQueryList_withParameterMap_exception() {

		createNamedQueryListMapPO(true, JstNamedQueryRmTest.QUERY_NAME_OOOOPPPSSS);
	}

	@Test
	public void testNamedQuerySingle() {

		final NoteDE note = retrieveNamedQuerySingle(true, ConstantsTestJPA.QUERY_NOTE_SINGLE_ONE);

		Assertions.assertThat(note.getText()).isEqualTo(ConstantsTestJPA.NOTE_TEXT_ONE);
	}

	@Test(expected = JustifyException.class)
	public void testNamedQuerySingle_exception() {

		retrieveNamedQuerySingle(false, JstNamedQueryRmTest.QUERY_NAME_OOOOPPPSSS);
	}

	@Test
	public void testNamedQuerySingle_withParameterMap() {

		final NoteDE note = createNamedQueryMapPO(true, JstNamedQueryRmTest.QUERY_NOTE_LIST_WITH_STRING_PARAMETER);

		Assertions.assertThat(note.getText()).isEqualTo(ConstantsTestJPA.NOTE_TEXT_TWO);
	}

	@Test(expected = JustifyException.class)
	public void testNamedQuerySingle_withParameterMap_exception() {

		createNamedQueryMapPO(true, JstNamedQueryRmTest.QUERY_NAME_OOOOPPPSSS);
	}

	@Test(expected = JustifyException.class)
	public void testQueryNamedModifiableList_withParameterMap_exception() {

		createNamedQueryListMapPO(true, JstNamedQueryRmTest.QUERY_NAME_OOOOPPPSSS);
	}

	@Test
	public void testQueryNamedModifiableSingle_exception() {

		retrieveNamedQuerySingle(true, JstNamedQueryRmTest.QUERY_NAME_OOOOPPPSSS);
	}

	private Map<String, Object> createModifiableParameterMap() {

		final Map<String, Object> stringParameterMap = new HashMap<>();
		stringParameterMap.put("text", ConstantsTestJPA.NOTE_TEXT_ONE);
		return stringParameterMap;
	}

	private Optional<List<NoteDE>> createNamedQueryListMapPO(final boolean suppressExceptionForNull,
			final String queryName) {

		return JstQueryNamedJpaRM
				.queryReadOnlyList(JstQueryNamedJpaPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
						.withQueryName(queryName).withParameterMap(createModifiableParameterMap()));
	}

	private Optional<List<NoteDE>> createNamedQueryListPO(final boolean suppressExceptionForNull,
			final String queryName) {

		return JstQueryNamedJpaRM.queryReadOnlyList(
				JstQueryNamedJpaPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withQueryName(queryName));
	}

	private NoteDE createNamedQueryMapPO(final boolean suppressExceptionForNull, final String queryName) {

		final Optional<EntityManager> entityManager = JstEntityManagerFactoryCacheHelper
				.createEntityManagerToBeClosed(ConstantsTestJPA.JUSTIFY_PU);
		NoteDE note = null;
		try {
			note = JstQueryNamedJpaRM.querySingle(JstQueryNamedJpaPO.withPersistenceUnitName(suppressExceptionForNull)
					.withEntityManager(entityManager).withQueryName(queryName).withParameterMap(createParameterMap()));
		} catch (final Exception e) {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager);
			throw e;
		}
		return note;
	}

	private Map<String, Object> createParameterMap() {

		final Map<String, Object> stringParameterMap = new HashMap<>();
		stringParameterMap.put("text", ConstantsTestJPA.NOTE_TEXT_TWO);
		return stringParameterMap;
	}

	private Optional<NoteDE> retrieveNamedQuerySingle(final boolean suppressExceptionForNull, final String queryName) {

		final Optional<NoteDE> note = JstQueryNamedJpaRM.querySingle(
				JstQueryNamedJpaPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withQueryName(queryName));
		return note;
	}
}
