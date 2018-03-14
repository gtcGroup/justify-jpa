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
package com.gtcgroup.justify.jpa.intentional.failure;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.test.extension.JstConfigureTestLogToConsole;
import com.gtcgroup.justify.jpa.assertions.AssertionsJPA;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.extension.JstConfigureTestJPA;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
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
@Tag(value = "intentional")
@JstConfigureTestLogToConsole
@JstConfigureTestJPA(persistenceUnitName = ConstantsTestJPA.JUSTIFY_PU, dataPopulators = NoteDataPopulator.class)
@SuppressWarnings("static-method")
public class AssertionsIntentionalFailedJpaTest {

	@Test
	public void testIntentionalExistsInDatabase() {

		AssertionsJPA.assertNotExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, NoteDE.class,
				ConstantsTestJPA.NOTE_UUID_ONE);
	}

	@Test
	public void testIntentionalExistsInDatabase_instance() {

		AssertionsJPA.assertNotExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, NoteDataPopulator.noteOne);
	}

	@Test
	public void testIntentionalNotExistsInDatabase() {

		AssertionsJPA.assertExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, NoteDE.class, "fake_IDENTITY");
	}

	@Test
	public void testIntentionalNotExistsInDatabase_instance() {

		AssertionsJPA.assertExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, new NoteDE());
	}
}