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
package com.gtcgroup.justify.jpa.de.dependency;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.gtcgroup.justify.jpa.de.base.dependency.BaseUuidDE;

@Entity
@Table(name = "BOOKING")
@AttributeOverrides({ @AttributeOverride(name = "uuid", column = @Column(name = "BOOKING_UUID")) })
@SuppressWarnings("javadoc")
public class BookingDE extends BaseUuidDE {

	private static final long serialVersionUID = 1L;

	@Column(name = "BOOKING_DESTINATION")
	private String destination;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "NOTE_UUID", referencedColumnName = "NOTE_UUID")
	private NoteDE note;

	@OneToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST })
	@JoinColumn(name = "CUSTOMER_UUID", referencedColumnName = "CUSTOMER_UUID")
	private CustomerDE customer;

	public CustomerDE getCustomer() {

		return this.customer;
	}

	public String getDestination() {
		return this.destination;
	}

	public NoteDE getNote() {
		return this.note;
	}

	public BookingDE setCustomer(final CustomerDE customerDE) {

		this.customer = customerDE;
		return this;
	}

	public BookingDE setDestination(final String destination) {

		this.destination = destination;
		return this;
	}

	public BookingDE setNote(final NoteDE noteDE) {

		this.note = noteDE;
		return this;
	}

	public BookingDE setNoteDE(final NoteDE noteDE) {
		return setNote(noteDE);
	}
}
