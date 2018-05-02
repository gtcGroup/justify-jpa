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
import com.gtcgroup.justify.core.testing.extension.JstBaseTestingExtension;

/**
 * This Domain Entity base class supports audit columns.
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

	public void setCreateTime(final Timestamp createTime) {

		this.createTime = createTime;
	}

	public void setCreateUser(final String createUser) {

		this.createUser = createUser;
	}

	public void setUpdateTime(final Timestamp updateTime) {

		this.updateTime = updateTime;
	}

	public void setUpdateUser(final String updateUser) {

		this.updateUser = updateUser;
	}

	@PrePersist
	protected void prePersistAuditFields() {

		final Date date = new Date();
		setCreateTime(new Timestamp(date.getTime()));
		final String user = JstBaseTestingExtension.getUserId();

		setCreateUser(user);
		setUpdateUser(user);
	}

	@PreUpdate
	protected void preUpdateAuditFields() {

		setUpdateUser(JstBaseTestingExtension.getUserId());
	}
}