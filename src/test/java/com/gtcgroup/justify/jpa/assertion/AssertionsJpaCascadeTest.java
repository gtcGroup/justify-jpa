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
package com.gtcgroup.justify.jpa.assertion;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.test.exception.internal.JustifyException;
import com.gtcgroup.justify.core.test.extension.JstConfigureTestLogToConsole;
import com.gtcgroup.justify.core.test.extension.JstConfigureTestUserId;
import com.gtcgroup.justify.jpa.assertions.AssertionsJPA;
import com.gtcgroup.justify.jpa.de.dependency.BookingDE;
import com.gtcgroup.justify.jpa.de.dependency.CustomerDE;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.extension.JstConfigureTestJPA;
import com.gtcgroup.justify.jpa.helper.JstTransactionUtilHelper;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.JstAssertCascadeJpaPO;
import com.gtcgroup.justify.jpa.po.JstTransactionJpaPO;
import com.gtcgroup.justify.jpa.rm.JstTransactionJpaRM;

/**
 * Test Class
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v6.2
 */
@JstConfigureTestLogToConsole
@JstConfigureTestUserId
@JstConfigureTestJPA(persistenceUnitName = ConstantsTestJPA.JUSTIFY_PU)
public class AssertionsJpaCascadeTest {

    private static final String CUSTOMER_UUID = "customerUUID";

    private BookingDE populateBooking() {

        final CustomerDE customerDE = new CustomerDE().setUuid(AssertionsJpaCascadeTest.CUSTOMER_UUID);
        final NoteDE noteDE = new NoteDE();

        final BookingDE bookingDE = new BookingDE();
        bookingDE.setNote(noteDE);
        bookingDE.setCustomer(customerDE);

        return bookingDE;
    }

    @Test(expected = JustifyException.class)
    public void testCascadeTypesForBooking_badName() {

        final JstAssertCascadeJpaPO assertJpaPO = JstAssertCascadeJpaPO
                .withPopulatedEntity(ConstantsTestJPA.JUSTIFY_PU, populateBooking()).withCascadeAll("badName")
                .withCascadeAll("badName").withCleanupAfterTheTest("getCustomer");

        AssertionsJPA.assertCascadeTypes(assertJpaPO);
    }

    @Test(expected = JustifyException.class)
    public void testCascadeTypesForBooking_badPU() {

        final JstAssertCascadeJpaPO assertJpaPO = JstAssertCascadeJpaPO.withPopulatedEntity("badPU", populateBooking());

        AssertionsJPA.assertCascadeTypes(assertJpaPO);
    }

    @Test(expected = AssertionError.class)
    public void testCascadeTypesForBooking_cascadeNone() {

        final JstAssertCascadeJpaPO assertJpaPO = JstAssertCascadeJpaPO
                .withPopulatedEntity(ConstantsTestJPA.JUSTIFY_PU, populateBooking()).withCascadeAll("getNote")
                .withCascadeNone("getCustomer").withCleanupAfterTheTest("getCustomer");

        AssertionsJPA.assertCascadeTypes(assertJpaPO);
    }

    @Test(expected = AssertionError.class)
    public void testCascadeTypesForBooking_customerFailure() {

        final JstAssertCascadeJpaPO assertJpaPO = JstAssertCascadeJpaPO
                .withPopulatedEntity(ConstantsTestJPA.JUSTIFY_PU, populateBooking()).withCascadeAll("getNote")
                .withCascadeAll("getCustomer").withCleanupAfterTheTest("getCustomer");

        AssertionsJPA.assertCascadeTypes(assertJpaPO);
    }

    @Test
    public void testCascadeTypesForBooking_happyPath() {

        final JstAssertCascadeJpaPO assertJpaPO = JstAssertCascadeJpaPO
                .withPopulatedEntity(ConstantsTestJPA.JUSTIFY_PU, populateBooking()).withCascadeAll("getNote")
                .withCascadeAllExceptRemove("getCustomer").withCleanupAfterTheTest("getCustomer");

        AssertionsJPA.assertCascadeTypes(assertJpaPO);
    }

    @Test
    public void testCascadeTypesForBooking_happyPath_explicit() {

        final JstAssertCascadeJpaPO assertJpaPO = JstAssertCascadeJpaPO
                .withPopulatedEntity(ConstantsTestJPA.JUSTIFY_PU, populateBooking()).withCascadePersist("getNote")
                .withCascadeRemove("getNote").withCascadeAllExceptRemove("getCustomer")
                .withCleanupAfterTheTest("getCustomer");

        AssertionsJPA.assertCascadeTypes(assertJpaPO);
    }

    @Test
    public void testCascadeTypesForBooking_noCleanup1() {

        final JstAssertCascadeJpaPO assertJpaPO = JstAssertCascadeJpaPO
                .withPopulatedEntity(ConstantsTestJPA.JUSTIFY_PU, populateBooking()).withCascadeAll("getNote")
                .withCascadeAllExceptRemove("getCustomer");

        final BookingDE booking = AssertionsJPA.assertCascadeTypes(assertJpaPO);

        // These additional lines enable verification and cleanup.
        AssertionsJPA.assertExistsInDatabase(ConstantsTestJPA.JUSTIFY_PU, CustomerDE.class,
                AssertionsJpaCascadeTest.CUSTOMER_UUID);

        // Using a List here to verify code coverage.
        final List<CustomerDE> customerList = new ArrayList<>();
        customerList.add(booking.getCustomer());

        JstTransactionJpaRM.transactMultipleEntities(JstTransactionJpaPO.withException()
                .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withDeleteList(customerList));

    }

    @Test(expected = JustifyException.class)
    public void testCascadeTypesForBooking_noCleanup2() {

        final JstAssertCascadeJpaPO assertJpaPO = JstAssertCascadeJpaPO
                .withPopulatedEntity(ConstantsTestJPA.JUSTIFY_PU, populateBooking()).withCascadeAll("getNote")
                .withCascadeAllExceptRemove("getCustomer").withCascadeNone("fakeName");

        try {
            AssertionsJPA.assertCascadeTypes(assertJpaPO);
        } catch (final JustifyException e) {

            final BookingDE booking = (BookingDE) assertJpaPO.getPopulatedEntity();

            JstTransactionUtilHelper.findAndDeleteEntity(ConstantsTestJPA.JUSTIFY_PU, booking.getCustomer());
            throw e;
        }
    }

    @Test(expected = JustifyException.class)
    public void testCascadeTypesForBooking_NoteError() {

        final JstAssertCascadeJpaPO assertJpaPO = JstAssertCascadeJpaPO
                .withPopulatedEntity(ConstantsTestJPA.JUSTIFY_PU, populateBooking())
                .withCascadeAllExceptRemove("fakeName").withCascadeAllExceptRemove("fakeName");

        try {
            AssertionsJPA.assertCascadeTypes(assertJpaPO);
        } catch (final JustifyException e) {

            final BookingDE booking = (BookingDE) assertJpaPO.getPopulatedEntity();

            JstTransactionUtilHelper.findAndDeleteEntity(ConstantsTestJPA.JUSTIFY_PU, booking.getCustomer());
            throw e;
        }
    }

    @Test(expected = JustifyException.class)
    public void testCascadeTypesForBooking_null() {

        final JstAssertCascadeJpaPO assertJpaPO = JstAssertCascadeJpaPO.withPopulatedEntity(ConstantsTestJPA.JUSTIFY_PU,
                null);

        AssertionsJPA.assertCascadeTypes(assertJpaPO);
    }
}