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
package com.gtcgroup.justify.jpa.de;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.UuidGenerator;

/**
 * This Domain Entity base class supports identity generation.
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since 8.5
 */
@MappedSuperclass
public abstract class BaseUuidDE extends BaseAuditDE {

	private static final long serialVersionUID = 1L;

	@Id
	@UuidGenerator(name = "UUID")
	@GeneratedValue(generator = "UUID")
	@Column(length = 36, insertable = true, updatable = false, name = "KEY_UUID", nullable = false)
	private String uuid;

	@Override
	public boolean equals(final Object obj) {

		if (obj == null) {
			return false;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final BaseUuidDE other = (BaseUuidDE) obj;
		return Objects.equals(this.uuid, other.uuid);
	}

	@SuppressWarnings("unchecked")
	public <CONCRETE extends BaseUuidDE> CONCRETE generateUuid() {

		this.uuid = UUID.randomUUID().toString();

		return (CONCRETE) this;
	}

	public String getUuid() {
		return this.uuid;
	}

	@Override
	public int hashCode() {

		return super.hashCode();
	}

	public void setUuid(final String uuid) {

		this.uuid = uuid;
	}
}
