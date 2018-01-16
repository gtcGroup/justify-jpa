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
package com.gtcgroup.justify.jpa.helper.dependency;

/**
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2017 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
@SuppressWarnings("javadoc")
public enum ConstantsTestJPA {

    INSTANCE;

    public final static String NOTE_TEXT_ONE = "testTextOne";

    public final static String NOTE_TEXT_TWO = "testTextTwo";
    
    public final static String QUERY_NOTE_SINGLE_ONE = "queryNoteSingleOne";

    public final static String QUERY_NOTE_SINGLE_TWO = "queryNoteSingleTwo";

    public static final String NOTE_UUID_ONE = "noteIdentityOne";

    public static final String NOTE_UUID_TWO = "noteIdentityTwo";

    public static final String JUSTIFY_PU = "justify-pu";

    public static final String JUSTIFY_SECOND_PU = "justify-second-pu";

    public static final String CUSTOMER_ENTITY_IDENTITY = "customerIdentity";

    public static final String RANDOM_ENTITY_IDENTITY = "J!u@s#t$i%f^y";

    public static final String SQL_NATIVE_NOTE_LIST = "SELECT * FROM NOTE";

    public static final String SQL_NATIVE_NOTE_SINGLE = "SELECT * FROM NOTE WHERE NOTE_TEXT = ?";
}
