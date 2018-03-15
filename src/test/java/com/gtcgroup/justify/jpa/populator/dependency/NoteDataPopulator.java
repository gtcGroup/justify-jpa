/*
 * [Licensed per the Open Source "MIT License".]
 *
 * Copyright (c) 2006 - 2017 by
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
package com.gtcgroup.justify.jpa.populator.dependency;

import java.util.ArrayList;
import java.util.List;

import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.test.populator.JstBaseDataPopulator;

/**
 * Test Class
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
public class NoteDataPopulator extends JstBaseDataPopulator {

	public static NoteDE noteOne = new NoteDE().setText(ConstantsTestJPA.NOTE_TEXT_ONE)
			.setUuid(ConstantsTestJPA.NOTE_UUID_ONE);

	public static NoteDE noteTwo = new NoteDE().setText(ConstantsTestJPA.NOTE_TEXT_TWO)
			.setUuid(ConstantsTestJPA.NOTE_UUID_TWO);

	public static List<Object> populatedList = new ArrayList<>();

	/**
	 * @see JstBaseDataPopulator#populateCreateListTM(JstQueryJpaRM)
	 */
	@Override
	public List<Object> populateCreateListTM(final String persistenceUnitName) {

		populatedList.add(NoteDataPopulator.noteOne);
		populatedList.add(NoteDataPopulator.noteTwo);

		return NoteDataPopulator.populatedList;
	}
}
