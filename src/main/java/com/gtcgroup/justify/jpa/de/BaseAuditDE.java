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

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import com.gtcgroup.justify.core.base.JstBaseDE;
import com.gtcgroup.justify.core.test.extension.JstBaseExtension;

@MappedSuperclass
public abstract class BaseAuditDE extends JstBaseDE {

	private static final long serialVersionUID = 1L;

	@Column(insertable = true, updatable = false, unique = false, name = "CREATE_TIME", nullable = false, columnDefinition = "timestamp")
	private Timestamp createTime;

	@Version
	@Column(insertable = true, updatable = true, unique = false, name = "UPDATE_TIME", nullable = false, columnDefinition = "timestamp")
	private Timestamp updateTime;

	@Column(insertable = true, updatable = false, unique = false, name = "CREATE_USER", nullable = false)
	private String createUser;

	@Column(insertable = true, updatable = true, unique = false, name = "UPDATE_USER", nullable = false)
	private String updateUser;

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public Timestamp getUpdateTime() {
		return this.updateTime;
	}

	public String getUpdateUser() {
		return this.updateUser;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		return super.hashCode();
	}

	@SuppressWarnings("unchecked")
	public <CONCRETE extends BaseAuditDE> CONCRETE setCreateTime(final Timestamp createTime) {

		this.createTime = createTime;
		return (CONCRETE) this;
	}

	@SuppressWarnings("unchecked")
	public <CONCRETE extends BaseAuditDE> CONCRETE setCreateUser(final String createUser) {

		this.createUser = createUser;
		return (CONCRETE) this;
	}

	@SuppressWarnings("unchecked")
	public <CONCRETE extends BaseAuditDE> CONCRETE setUpdateTime(final Timestamp updateTime) {

		this.updateTime = updateTime;
		return (CONCRETE) this;
	}

	@SuppressWarnings("unchecked")
	public <CONCRETE extends BaseAuditDE> CONCRETE setUpdateUser(final String updateUser) {

		this.updateUser = updateUser;
		return (CONCRETE) this;
	}

	@PrePersist
	protected void prePersistAuditFields() {

		final Date date = new Date();
		this.setCreateTime(new Timestamp(date.getTime()));
		final String user = JstBaseExtension.getUserId();

		this.setCreateUser(user);
		this.setUpdateUser(user);
	}

	@PreUpdate
	protected void preUpdateAuditFields() {

		this.setUpdateUser(JstBaseExtension.getUserId());
	}
}