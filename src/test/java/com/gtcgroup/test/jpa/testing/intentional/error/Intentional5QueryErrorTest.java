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
package com.gtcgroup.test.jpa.testing.intentional.error;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.testing.extension.JstConfigureTestLogToConsole;
import com.gtcgroup.justify.jpa.po.JstQueryCountPO;
import com.gtcgroup.justify.jpa.po.JstQueryNamedPO;
import com.gtcgroup.justify.jpa.po.JstQueryStringPO;
import com.gtcgroup.justify.jpa.rm.JstQueryCountRM;
import com.gtcgroup.justify.jpa.rm.JstQueryNamedRM;
import com.gtcgroup.justify.jpa.rm.JstQueryStringRM;
import com.gtcgroup.justify.jpa.testing.extension.JstConfigureTestJPA;
import com.gtcgroup.test.jpa.de.dependency.NotAnEntityDE;
import com.gtcgroup.test.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.test.jpa.po.dependency.ConfigureJustifyWithPopulatorPO;
import com.gtcgroup.test.jpa.rm.JstFindJpaRmTest;
import com.gtcgroup.test.jpa.rm.JstNamedQueryRmTest;

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
@JstConfigureTestLogToConsole
@JstConfigureTestJPA(configureTestJpaPO = ConfigureJustifyWithPopulatorPO.class)
@SuppressWarnings("static-method")
public class Intentional5QueryErrorTest {

	@Test
	public void testIntentionalCount_missingResultClass() {

		JstQueryCountRM.count(JstQueryCountPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)).isPresent();
	}

	@Test
	public void testIntentionalCount_notAnEntity() {

		JstQueryCountRM.count(JstQueryCountPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
				.withResultClass(NotAnEntityDE.class)).isPresent();
	}

	@Test
	public void testIntentionalFind_notAnEntity() {

		JstFindJpaRmTest.findReadOnlyNoteDE(NotAnEntityDE.class, ConstantsTestJPA.NOTE_UUID_TWO);

	}

	@Test
	public void testIntentionalNamedQueryList_badName() {

		JstQueryNamedRM.queryList(JstQueryNamedPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
				.withQueryName(JstNamedQueryRmTest.QUERY_NAME_OOOOPPPSSS));
	}

	@Test
	public void testIntentionalQuerySingle_noQueryString() {

		JstQueryStringRM.querySingle(JstQueryStringPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU));

	}
}