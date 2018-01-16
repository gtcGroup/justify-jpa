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
package com.gtcgroup.justify.jpa.rm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.rulechain.JstRuleChain;
import com.gtcgroup.justify.core.si.JstRuleChainSI;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.extension.JstConfigureTestJpaExtension;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.JstTransactionJpaPO;
import com.gtcgroup.justify.jpa.populator.dependency.NoteDataPopulator;

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
public class JstTransactionJpaRmTest {

    @Rule
    public JstRuleChainSI ruleChain = JstRuleChain.outerRule(false).around(JstConfigureTestJpaExtension
            .withPersistenceUnit(ConstantsTestJPA.JUSTIFY_PU).withDataPopulators(NoteDataPopulator.class));

    @Test
    public void testDeleteList() {

        final NoteDE note1 = new NoteDE();
        note1.setText("One");

        final NoteDE note2 = new NoteDE();
        note2.setText("Two");

        List<NoteDE> noteList = JstTransactionJpaRM.transactMultipleEntities(JstTransactionJpaPO.withException()
                .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withCreateAndUpdateEntities(note1, note2));

        Assertions.assertThat(noteList.size()).isEqualTo(2);

        updateWithJDBC();

        noteList = JstTransactionJpaRM.transactMultipleEntities(JstTransactionJpaPO.withException()
                .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withDeleteList(noteList));

        Assertions.assertThat(noteList.size()).isEqualTo(0);

    }

    private void updateWithJDBC() {
        Connection dbConnection = null;
        Statement statement = null;
        try {
            // Register JDBC driver
            Class.forName("org.h2.Driver");

            // Open a connection
            dbConnection = DriverManager.getConnection("jdbc:h2:mem:justify-persist;MODE=MSSQLServer", "persist",
                    "persist");

            // Execute a query
            statement = dbConnection.createStatement();
            final String sql = "UPDATE NOTE SET NOTE_TEXT = 'Updated Text' WHERE NOTE_TEXT = 'Two'";
            statement.execute(sql);

            statement.close();
            dbConnection.close();
        } catch (final Exception e) {

            e.printStackTrace();

        } finally {

            if (statement != null) {
                try {
                    statement.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }

            if (dbConnection != null) {
                try {
                    dbConnection.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
