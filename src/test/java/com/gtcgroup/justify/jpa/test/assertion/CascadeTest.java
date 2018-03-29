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
package com.gtcgroup.justify.jpa.test.assertion;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import com.gtcgroup.justify.core.test.extension.JstConfigureTestLogToConsole;
import com.gtcgroup.justify.core.test.extension.JstConfigureTestUserId;
import com.gtcgroup.justify.jpa.de.dependency.BookingDE;
import com.gtcgroup.justify.jpa.de.dependency.CustomerDE;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.dependency.ConfigureJustifyWithPopulatorPO;
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
 * @since v6.2
 */
@JstConfigureTestLogToConsole
@JstConfigureTestUserId
@JstConfigureTestJPA(configureTestJpaPO = ConfigureJustifyWithPopulatorPO.class)
@SuppressWarnings("static-method")
public class CascadeTest {

	private static final String GET_NOTE = "getNote";
	private static final String GET_CUSTOMER = "getCustomer";
	private static final String BAD_METHOD_NAME = "badMethodName";

	private static BookingDE populateBooking() {

		final CustomerDE customerDE = new CustomerDE();
		final NoteDE noteDE = new NoteDE();

		final BookingDE bookingDE = new BookingDE();
		bookingDE.setNote(noteDE);
		bookingDE.setCustomer(customerDE);

		return bookingDE;
	}

	@Test()
	public void testCascadeTypesForBooking_badMethodName() {

		assertThrows(AssertionFailedError.class, () -> {
			AssertionsJPA.assertCascadeTypes(JstAssertCascadePO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
					.withPopulatedEntity(populateBooking()).withCascadeAll(BAD_METHOD_NAME)
					.withCleanupAfterVerification(GET_CUSTOMER));
		});

	}

	@Test()
	public void testCascadeTypesForBooking_cleanupBadMethod() {

		assertThrows(AssertionFailedError.class, () -> {
			AssertionsJPA.assertCascadeTypes(JstAssertCascadePO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
					.withPopulatedEntity(populateBooking()).withCascadeAll(GET_NOTE)
					.withCascadeAllExceptRemove(GET_CUSTOMER).withCleanupAfterVerification(BAD_METHOD_NAME));
		});

	}

	@Test
	public void testCascadeTypesForBooking_customerAll() {

		assertThrows(AssertionFailedError.class, () -> {
			AssertionsJPA.assertCascadeTypes(JstAssertCascadePO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
					.withPopulatedEntity(populateBooking()).withCascadeAll(GET_NOTE, GET_CUSTOMER)
					.withCleanupAfterVerification(GET_CUSTOMER));
		});
	}

	@Test
	public void testCascadeTypesForBooking_customerNone() {

		assertThrows(AssertionFailedError.class, () -> {
			AssertionsJPA.assertCascadeTypes(JstAssertCascadePO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
					.withPopulatedEntity(populateBooking()).withCascadeAll(GET_NOTE).withCascadeNone(GET_CUSTOMER)
					.withCleanupAfterVerification(GET_CUSTOMER));
		});
	}

	@Test
	public void testCascadeTypesForBooking_happyPath_explicit() {

		final JstAssertCascadePO assertJpaPO = JstAssertCascadePO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
				.withPopulatedEntity(populateBooking()).withCascadePersist(GET_NOTE).withCascadeRemove(GET_NOTE)
				.withCascadeAllExceptRemove(GET_CUSTOMER).withCleanupAfterVerification(GET_CUSTOMER);

		AssertionsJPA.assertCascadeTypes(assertJpaPO);
	}

	@Test
	public void testCascadeTypesForBooking_happyPath1() {

		final JstAssertCascadePO assertJpaPO = JstAssertCascadePO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
				.withPopulatedEntity(populateBooking()).withCascadeAll(GET_NOTE)
				.withCascadeAllExceptRemove(GET_CUSTOMER).withCleanupAfterVerification(GET_CUSTOMER);

		AssertionsJPA.assertCascadeTypes(assertJpaPO);
	}

	@Test
	public void testCascadeTypesForBooking_happyPath2() {

		final JstAssertCascadePO assertJpaPO = JstAssertCascadePO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
				.withPopulatedEntity(new NoteDE());

		AssertionsJPA.assertCascadeTypes(assertJpaPO);
	}

	@Test
	public void testCascadeTypesForBooking_happyPath3() {

		final JstAssertCascadePO assertJpaPO = JstAssertCascadePO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
				.withPopulatedEntity(populateBooking()).withCascadeAll(GET_NOTE).withCascadeAll("getUnmappedList")
				.withCascadeAllExceptRemove(GET_CUSTOMER).withCleanupAfterVerification(GET_CUSTOMER, "getUnmappedList");

		AssertionsJPA.assertCascadeTypes(assertJpaPO);
	}

	@Test()
	public void testCascadeTypesForBooking_null() {

		assertThrows(AssertionFailedError.class, () -> {
			AssertionsJPA.assertCascadeTypes(
					JstAssertCascadePO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withPopulatedEntity(null));
		});
	}

	@Test()
	public void testCascadeTypesForBooking_pu() {

		assertThrows(AssertionFailedError.class, () -> {
			AssertionsJPA
					.assertCascadeTypes(JstAssertCascadePO.withPersistenceUnitName("bad_PU").withPopulatedEntity(null));
		});
	}
}