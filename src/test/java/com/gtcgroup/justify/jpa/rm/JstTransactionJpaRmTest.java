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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.gtcgroup.justify.core.test.extension.JstConfigureTestLogToConsole;
import com.gtcgroup.justify.jpa.de.dependency.NoteDE;
import com.gtcgroup.justify.jpa.exception.JstOptimisiticLockException;
import com.gtcgroup.justify.jpa.helper.JstEntityManagerFactoryCacheHelper;
import com.gtcgroup.justify.jpa.helper.dependency.ConstantsTestJPA;
import com.gtcgroup.justify.jpa.po.JstTransactionPO;
import com.gtcgroup.justify.jpa.test.extension.JstConfigureTestJPA;

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
@JstConfigureTestLogToConsole
@JstConfigureTestJPA(persistenceUnitName = ConstantsTestJPA.JUSTIFY_PU)
@SuppressWarnings("static-method")
public class JstTransactionJpaRmTest {

	@Test
	public void testCommitListInOneTransaction_externalEntityManager() {

		Optional<EntityManager> entityManager = null;
		Optional<List<NoteDE>> optionalList = null;

		try {
			entityManager = JstEntityManagerFactoryCacheHelper
					.createEntityManagerToBeClosed(ConstantsTestJPA.JUSTIFY_PU);

			optionalList = JstTransactionRM.commitListInOneTransaction(JstTransactionPO
					.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
					.withCreateAndUpdateEntities(new NoteDE().setText("text2")).withEntityManager(entityManager.get()));

			if (optionalList.isPresent()) {
				final NoteDE noteDE = optionalList.get().get(0);

				optionalList = JstTransactionRM.commitListInOneTransaction(
						JstTransactionPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withDeleteEntities(noteDE)
								.withEntityManager(entityManager.get()));

				assertTrue(optionalList.isPresent());
			}
		} finally {
			JstEntityManagerFactoryCacheHelper.closeEntityManager(entityManager.get());
		}
	}

	@Test
	public void testCommitListInOneTransaction_happyPath() {

		Optional<List<NoteDE>> optionalNoteList = JstTransactionRM
				.commitListInOneTransaction(JstTransactionPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
						.withCreateAndUpdateEntities(new NoteDE().setText("text1")));

		if (optionalNoteList.isPresent()) {
			final NoteDE noteDE = optionalNoteList.get().get(0);

			optionalNoteList = JstTransactionRM.commitListInOneTransaction(
					JstTransactionPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withDeleteEntities(noteDE));

			assertTrue(optionalNoteList.isPresent());
		}
	}

	@Test
	public void testCommitSingleInOneTransaction_noEntities() {

		final Optional<NoteDE> optionalNoteDE = JstTransactionRM
				.commitSingleInOneTransaction(JstTransactionPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU));

		assertFalse(optionalNoteDE.isPresent());
	}

	@Test
	public void testCommitSingleInOneTransaction_optimisticLock() {

		final Optional<NoteDE> optionalNoteDE = JstTransactionRM
				.commitSingleInOneTransaction(JstTransactionPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
						.withCreateAndUpdateEntities(new NoteDE().setText("text1")));

		if (optionalNoteDE.isPresent()) {

			final NoteDE noteDE = optionalNoteDE.get();

			final Date date = new Date();
			noteDE.setUpdateTime(new Timestamp(date.getTime()));

			assertThrows(JstOptimisiticLockException.class, () -> {
				JstTransactionRM.commitSingleInOneTransaction(JstTransactionPO
						.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withCreateAndUpdateEntities(noteDE));
			});
		}
	}

	@Test
	public void testCommitSingleInOneTransaction_twoCommitted() {

		final Optional<NoteDE> optionalNoteDE = JstTransactionRM
				.commitSingleInOneTransaction(JstTransactionPO.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU)
						.withCreateAndUpdateEntities(new NoteDE().setText("text1"), new NoteDE().setText("text2")));

		// Both NoteDEs were committed... however, neither is returned.
		assertFalse(optionalNoteDE.isPresent());
	}

	@Test
	public void testUpdateFromOutsideJPA() {

		final NoteDE note1 = new NoteDE();
		note1.setText("One");

		final NoteDE note2 = new NoteDE();
		note2.setText("Two");

		Optional<List<Object>> noteList = JstTransactionRM.commitListInOneTransaction(JstTransactionPO
				.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withCreateAndUpdateEntities(note1, note2));

		if (noteList.isPresent()) {

			updateWithJDBC();

			noteList = JstTransactionRM.commitListInOneTransaction(JstTransactionPO
					.withPersistenceUnitName(ConstantsTestJPA.JUSTIFY_PU).withDeleteList(noteList.get()));
		}
		Assertions.assertTrue(noteList.isPresent());
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
