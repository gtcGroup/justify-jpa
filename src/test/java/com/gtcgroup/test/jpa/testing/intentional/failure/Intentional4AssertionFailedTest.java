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
package com.gtcgroup.test.jpa.testing.intentional.failure;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.testing.extension.JstConfigureTestingLogToConsole;
import com.gtcgroup.justify.jpa.testing.assertion.AssertionsJPA;
import com.gtcgroup.justify.jpa.testing.extension.JstConfigureTestingJPA;
import com.gtcgroup.test.jpa.de.dependency.NoteDE;
import com.gtcgroup.test.jpa.testing.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.test.jpa.testing.po.dependency.ConfigureJustifyWithPopulatorPO;
import com.gtcgroup.test.jpa.testing.populator.dependency.NoteTestingPopulator;

/**
 * Test Class
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since 8.5
 */
@Tag(value = "intentional")
@JstConfigureTestingLogToConsole
@JstConfigureTestingJPA(configureTestingJpaPO = ConfigureJustifyWithPopulatorPO.class)
@SuppressWarnings("static-method")
public class Intentional4AssertionFailedTest {

	@Test
	public void testIntentionalExistsInDatabase_fail() {

		AssertionsJPA.assertExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, NoteDE.class, "fake_IDENTITY");
	}

	@Test
	public void testIntentionalExistsInDatabase_instance() {

		final NoteDE note = new NoteDE();
		note.setUuid("uuid");
		AssertionsJPA.assertExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, note);
	}

	@Test
	public void testIntentionalNotExistsInDatabase_fail() {

		AssertionsJPA.assertNotExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, NoteDE.class,
				ConstantsTestJPA.NOTE_UUID_ONE);
	}

	@Test
	public void testIntentionalNotExistsInDatabase_instance() {

		AssertionsJPA.assertNotExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, NoteTestingPopulator.noteOne);
	}
}