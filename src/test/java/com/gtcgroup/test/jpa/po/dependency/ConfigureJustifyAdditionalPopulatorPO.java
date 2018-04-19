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
package com.gtcgroup.test.jpa.po.dependency;

import java.util.List;
import java.util.Map;

import com.gtcgroup.justify.jpa.testing.extension.JstConfigureTestingJpaPO;
import com.gtcgroup.justify.jpa.testing.populator.JstBaseDataPopulator;
import com.gtcgroup.test.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.test.jpa.testing.populator.dependency.NoteAdditionalDataPopulator;
import com.gtcgroup.test.jpa.testing.populator.dependency.NoteDataPopulator;

/**
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since 8.5
 */
public class ConfigureJustifyAdditionalPopulatorPO extends JstConfigureTestingJpaPO {

	@Override
	protected String definePersistenceUnitNameTM() {
		return ConstantsTestJPA.JUSTIFY_PU;
	}

	@Override
	protected void populateCreateListTM(final List<Class<? extends JstBaseDataPopulator>> dataPopulatorList) {
		dataPopulatorList.add(NoteDataPopulator.class);
		dataPopulatorList.add(NoteAdditionalDataPopulator.class);
	}

	@Override
	protected void populateEntityManagerFactoryPropertiesTM(final Map<String, Object> entityManagerFactoryPropertyMap) {
		// Empty Block
	}
}
