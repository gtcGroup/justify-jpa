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
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Cache;

import com.gtcgroup.justify.jpa.de.base.dependency.BaseUuidDE;

@Entity
@Table(name = "NOTE")
@AttributeOverrides({ @AttributeOverride(name = "uuid", column = @Column(name = "NOTE_UUID")) })
@Cacheable
@Cache (size=100)
@NamedQueries({ @NamedQuery(name = "queryNoteList", query = "SELECT note FROM NothingBean note"),
		@NamedQuery(name = "queryNoteSingleOne", query = "SELECT note FROM NothingBean note WHERE note.text = 'testTextOne' "),
		@NamedQuery(name = "queryNoteSingleTwo", query = "SELECT note FROM NothingBean note WHERE note.text = 'testTextTwo' "),
		@NamedQuery(name = "queryNoteListWithStringParameter", query = "SELECT note FROM NothingBean note WHERE note.text = :text"), })
@SuppressWarnings("javadoc")
public class NoteDE extends BaseUuidDE {

	private static final long serialVersionUID = 1L;

	public static String STRING = "string";

	@Column(name = "NOTE_TEXT")
	private String text;

	@Transient
	private String string = NoteDE.STRING;

	public String getString() {

		return this.string;
	}

	/**
	 * @return String
	 */
	@SuppressWarnings("static-method")
	public String getSTRING() {
		return NoteDE.STRING;
	}

	public String getText() {

		return this.text;
	}

	@SuppressWarnings("static-method")
	public String retrieveException() {

		throw new RuntimeException();
	}

	/**
	 * @param string
	 * @return NothingBean.java
	 */
	public NoteDE setString(final String string) {
		this.string = string;
		return this;
	}

	/**
	 * @param sTRING
	 * @return NothingBean.java
	 */
	public NoteDE setSTRING(final String sTRING) {
		NoteDE.STRING = sTRING;
		return this;
	}

	public NoteDE setText(final String text) {

		this.text = text;
		return this;
	}
}