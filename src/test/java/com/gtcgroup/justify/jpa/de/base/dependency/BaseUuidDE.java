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
package com.gtcgroup.justify.jpa.de.base.dependency;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.UuidGenerator;

import com.google.common.base.Objects;

@SuppressWarnings("javadoc")
@MappedSuperclass
public abstract class BaseUuidDE extends BaseAuditDE {

	private static final long serialVersionUID = 1L;

	@Id
	@UuidGenerator(name = "UUID")
	@GeneratedValue(generator = "UUID")
	@Column(length = 36, insertable = true, updatable = false, name = "KEY_UUID",
	nullable = false)
	private String uuid;

	@Override
	public boolean equals(final Object obj) {

		if (!super.equals(obj)) {
			return false;
		}

		final BaseUuidDE other = (BaseUuidDE)obj;
		return Objects.equal(this.uuid, other.uuid);
	}

	@SuppressWarnings("unchecked")
	public <CONCRETE extends BaseUuidDE> CONCRETE generateUuid() {

		this.uuid = UUID.randomUUID().toString();

		return (CONCRETE)this;
	}

	public String getUuid() {
		return this.uuid;
	}


	@SuppressWarnings("unchecked")
	public <CONCRETE extends BaseUuidDE> CONCRETE setUuid(
			final String uuid) {

		this.uuid = uuid;

		return (CONCRETE)this;
	}
}
