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
package com.gtcgroup.justify.jpa.rm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import com.gtcgroup.justify.core.rulechain.JstRuleChain;
import com.gtcgroup.justify.core.si.JstRuleChainSI;
import com.gtcgroup.justify.jpa.assertions.AssertionsJPA;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.extension.JstConfigureTestJpaExtension;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.JstQueryAllJpaPO;
import com.gtcgroup.justify.jpa.po.JstFindSingleJpaPO;
import com.gtcgroup.justify.jpa.po.JstQueryNamedJpaPO;
import com.gtcgroup.justify.jpa.po.JstTransactionJpaPO;
import com.gtcgroup.justify.jpa.populator.dependency.NoteDataPopulator;

/**
 * Test Class
 *
 * <p style="font-family:Verdana; font-size:10px; font-style:italic">
 * Copyright (c) 2006 - 2018 by Global Technology Consulting Group, Inc. at
 * <a href="http://gtcGroup.com">gtcGroup.com </a>.
 * </p>
 *
 * @author Marvin Toll
 * @since v3.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("all")
public class JstLifeCycleRmTest {

    String UPDATED_TEXT = "Updated Text";

    @Rule
    public JstRuleChainSI ruleChain = JstRuleChain.outerRule(false).around(JstConfigureTestJpaExtension
            .withPersistenceUnit(ConstantsTestJPA.JUSTIFY_PU).withDataPopulators(NoteDataPopulator.class));

    @Test
    public void test1LifeCycle_querySingle() {

        NoteDE note = JstQueryNamedJpaRM
                .querySingle(JstQueryNamedJpaPO.withPersistenceUnitName().withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
                        .withQueryName(ConstantsTestJPA.QUERY_NOTE_SINGLE_ONE));

        note = JstQueryFindJpaRM.findSingle(JstFindSingleJpaPO.withFind().withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
                .withEntityClass(NoteDE.class).withEntityIdentity(note.getUuid()));

        AssertionsJPA.assertExistsInSharedCache(ConstantsTestJPA.JUSTIFY_PU, note);

        note.setText(this.UPDATED_TEXT);

        note = JstTransactionJpaRM.transactEntity(JstTransactionJpaPO.withException()
                .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withCreateAndUpdateEntities(note));

        Assertions.assertThat(note.getText()).isEqualTo(this.UPDATED_TEXT);

        note.setText(ConstantsTestJPA.NOTE_TEXT_ONE);

        note = JstTransactionJpaRM.transactEntity(JstTransactionJpaPO.withException()
                .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withCreateAndUpdateEntities(note));

        Assertions.assertThat(note.getText()).isEqualTo(ConstantsTestJPA.NOTE_TEXT_ONE);
    }

    @Test
    public void test2LifeCycle_findAll_suppressDatabaseTrip() {

        final List<NoteDE> noteList = JstQueryFindAllJpaRM
                .queryAll(JstQueryAllJpaPO.withFindAll().withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
                        .withEntityClass(NoteDE.class).withForceDatabaseTripWhenNoCacheCoordination(true)); // Note:
                                                                                             // TRUE

        AssertionsJPA.assertExistsInSharedCache(ConstantsTestJPA.JUSTIFY_PU, noteList.toArray());
    }

    @Test
    public void test3LifeCycleQuery_externalUpdate_suppressForceDatabaseTrip() {

        NoteDE note = new NoteDE();
        note.setText("initialText");

        note = JstTransactionJpaRM.transactEntity(JstTransactionJpaPO.withException()
                .withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withCreateAndUpdateEntities(note));

        // External update.
        updateWithJDBC();

        note = JstQueryFindJpaRM.findSingle(JstFindSingleJpaPO.withFind().withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
                .withEntityClass(NoteDE.class).withEntityIdentity(ConstantsTestJPA.NOTE_UUID_ONE)
                .withForceDatabaseTripWhenNoCacheCoordination(true)); // Note: TRUE

        Assertions.assertThat(note.getText()).isEqualTo(ConstantsTestJPA.NOTE_TEXT_ONE);

        note = JstQueryFindJpaRM.findSingle(JstFindSingleJpaPO.withFind().withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
                .withEntityClass(NoteDE.class).withEntityIdentity(ConstantsTestJPA.NOTE_UUID_ONE)
                .withForceDatabaseTripWhenNoCacheCoordination(false)); // Note: FALSE

        Assertions.assertThat(note.getText()).isEqualTo(this.UPDATED_TEXT);

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
            final String sql = "UPDATE NOTE SET NOTE_TEXT = 'Updated Text' WHERE NOTE_TEXT = 'testTextOne'";
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
